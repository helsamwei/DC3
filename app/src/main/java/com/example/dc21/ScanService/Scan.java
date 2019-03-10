package com.example.dc21.ScanService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.widget.Chronometer;

import com.example.dc21.BLE_Senor.BleList;
import com.example.dc21.BLE_Senor.iBeaconClass;
import com.example.dc21.FileUnitil.savaData;
import com.example.dc21.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scan {

    private Context context;
    private Timer timer = new Timer();
    private long currentTime;
    private scanTimerTask timerTask;
    private Chronometer chronometer;
    /**
     * Wifi参数列表
     */
    StringBuilder wifiSB = new StringBuilder();
    private WifiManager wifiManager;
    /**
     * BLE参数列表
     */
    StringBuilder BleSB = new StringBuilder();
    private BluetoothAdapter bluetoothAdapter;
    private BleList BLeDevice = new BleList();
    /**
     * Scan构造方法
     * @param context:上下文
     * @param wifiManager
     * @param bluetoothAdapter
     * @param chronometer
     */
    public Scan(Context context, WifiManager wifiManager, BluetoothAdapter bluetoothAdapter, Chronometer chronometer) {
            this.context = context;
            this.wifiManager = wifiManager;
            this.bluetoothAdapter = bluetoothAdapter;
            this.chronometer = chronometer;
        }

    /**\
     *
     * @param duration:扫描时间
     * @param device：设备名称
     * @param head：手机朝向
     * @param x：手机x坐标
     * @param y：手机y坐标
     * @param z：手机z坐标
     */
    public void startBleAndWifiScan(float duration,int device,char head,float x,float y,float z){
        int frq = 10;
        long timeIndex = 1000/frq;
        BleSB.setLength(0);
        wifiSB.setLength(0);
        timerTask = new scanTimerTask(duration,device,head,x,y,z);
        timer.scheduleAtFixedRate(timerTask,0,timeIndex);
    }

    private class scanTimerTask extends TimerTask{
        /**
         * 参数列表
         */
        private int device;
        private int countNum = 0;
        private float x,y,z,duration;
        private char head;
        /**
         * scanTimerTask构造方法
         * @param duration:扫描时间
         * @param device：设备号
         * @param head：手机朝向
         * @param x：手机x坐标
         * @param y：手机y坐标
         * @param z：手机z坐标
         */
        public scanTimerTask(float duration,int device,char head,float x,float y,float z){
            this.duration = duration;
            this.device = device;
            this.head = head;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void run() {
            countNum++;
            currentTime = System.currentTimeMillis();

            //Wifi rssi 用广播接收，注册广播接收器
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            context.registerReceiver(WifiReceiver,intentFilter);
            wifiManager.startScan();

            //BLE扫描,设置成低延迟扫描
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            BluetoothLeScanner bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            bleScanner.startScan(null,scanSettings,scanCallback);

            for(int i = 0;i < BLeDevice.getCount();i++){
                iBeaconClass.iBeacon ibeacon = BLeDevice.getDevice(i);
                BleSB.append(currentTime + "\t").append(x + "\t").append(y + "\t").append(z + "\t").
                        append(ibeacon.major + "\t").append(ibeacon.minor + "\t").append(ibeacon.rssi + "\t").
                        append("\r\n");
            }

            if(countNum >= duration * 60 * 10){
                context.unregisterReceiver(WifiReceiver);
                bleScanner.stopScan(scanCallback);
                timer.cancel();
                chronometer.stop();
                MainActivity.mainActivity.waitingDialog.dismiss();
                new savaData(context,device,head,x,y,z,wifiSB,BleSB).execute();
            }

        }

        /**
         * 蓝牙扫描回调方法
         */
        ScanCallback scanCallback = new ScanCallback(){
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                int rssi = result.getRssi();
                byte[] scanData =  result.getScanRecord().getBytes();
                final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanData);
                BLeDevice.addDevice(ibeacon);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
        /**
         * wifi广播接收器
         */
        public BroadcastReceiver WifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)||
                        action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)||
                        action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                    List<android.net.wifi.ScanResult> lists = wifiManager.getScanResults();
                    System.out.println("周围AP数目：" + lists.size());
                    for (int i = 0; i < lists.size(); i++) {
                        android.net.wifi.ScanResult mScanResult = lists.get(i);
                        wifiSB.append((currentTime) + "\t").append(device + "\t").append(head + "\t").
                                append(x + "\t").append(y + "\t").append(z + "\t").append(mScanResult.BSSID + "\t").append(mScanResult.SSID + "\t")
                                .append(mScanResult.level + "\t").append("\n");
                    }
                }
            }
        };
    }
}
