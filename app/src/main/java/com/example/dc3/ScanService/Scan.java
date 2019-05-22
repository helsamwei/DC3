package com.example.dc3.ScanService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.widget.Chronometer;

import com.example.dc3.BLE_Senor.BleList;
import com.example.dc3.BLE_Senor.iBeaconClass;
import com.example.dc3.FileUnitil.savaData;
import com.example.dc3.IMUSensor.MySensorListener;
import com.example.dc3.MainActivity;
import com.example.dc3.SecondActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scan {

    private Context context;
    //Timer
    private Timer wsnStaScanTimer,sensorStaScanTimer,wsnDynScanTimer,sensorDynScanTimer;
    //TimerTask
    private wsnStaScanTimerTask wsnStaTimerTask;
    private wsnDynScanTimertask wsnDynTimerTask;
    private sensorStaScanTimerTask sensorStaTimerTask;
    private sensorDynScanTimerTask sensorDynTimerTask;

    private Chronometer chronometer;
    private boolean isScanSta;
    /**
     * Wifi参数列表
     */
    private StringBuilder wifiSBSTA = new StringBuilder();
    private StringBuilder wifiSBDYN = new StringBuilder();
    private WifiManager wifiManager;
    private long currentWSNstaTime,currrntWSNDyn;
    private android.net.wifi.ScanResult mScanResult;
    private List<android.net.wifi.ScanResult> lists;
    private ScanSettings scanSettings;
    /**
     * BLE参数列表
     */
    private StringBuilder BleSBSTA = new StringBuilder();
    private StringBuilder BleSBDYN = new StringBuilder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private iBeaconClass.iBeacon ibeacon;
    private BleList StaBLeDevice = new BleList();
    private BleList DynBLeDevice = new BleList();
    /**
     * IMU参数列表
     */
    private StringBuilder IMUSBSTA = new StringBuilder();
    private StringBuilder IMUSBDYN = new StringBuilder();
    private SensorManager sensorManager;
    private MySensorListener mySensorListener;
    private long currentIMUSta,currentIMUDyn;
    /**
     * 位置及及其相关信息
     */
    private float x,y,z;
    private float duration;
    private int device;
    private char head;
    /**
     * Scan构造方法
     * @param context:上下文
     * @param wifiManager
     * @param bluetoothAdapter
     * @param chronometer
     */
    public Scan(Context context, SensorManager sensorManager,WifiManager wifiManager, BluetoothAdapter bluetoothAdapter, Chronometer chronometer) {
            this.context = context;
            this.sensorManager = sensorManager;
            this.wifiManager = wifiManager;
            this.bluetoothAdapter = bluetoothAdapter;
            this.chronometer = chronometer;
        }


    /**\
     * 开启Wifi/Ble定时扫描
     * @param duration:扫描时间
     * @param device：设备名称
     * @param head：手机朝向
     * @param x：手机x坐标
     * @param y：手机y坐标
     * @param z：手机z坐标
     */
    public void startBleAndWifiStaScan(float duration,int device,char head,float x,float y,float z){
        int frq = 2;
        long timeIndex = 1000/frq;
        this.x = x;
        this.y = y;
        this.z = z;
        this.device = device;
        this.head = head;
        this.duration=duration;
        isScanSta = true;

        wsnStaScanTimer = new Timer();
        BleSBSTA.setLength(0);
        wifiSBSTA.setLength(0);
        wsnStaTimerTask = new wsnStaScanTimerTask();
        wsnStaScanTimer.scheduleAtFixedRate(wsnStaTimerTask,0,timeIndex);
    }

    /**
     * 开启IMU定时扫描
     * @param duration：时间
     * @param device：设备号
     * @param head：手机朝向
     * @param x
     * @param y
     * @param z
     */
    public void startSensorStaScan(float duration,int device,char head,float x,float y,float z){
        int frq = 50;
        long timeIndex = 1000/frq;
        IMUSBSTA.setLength(0);

        this.x = x;
        this.y = y;
        this.z = z;
        this.device = device;
        this.head = head;
        this.duration= duration;
        isScanSta = true;

        mySensorListener = new MySensorListener();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor:sensors){
            sensorManager.registerListener(mySensorListener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
        sensorStaScanTimer = new Timer();
        sensorStaTimerTask = new sensorStaScanTimerTask();
        sensorStaScanTimer.scheduleAtFixedRate(sensorStaTimerTask,0,timeIndex);

    }

    /**
     * 开启的wifi/ble动态扫描
     */
    public void startDynWifiBleScan(int device,char head,float x,float y,float z){
        int frq = 2;
        long timeIndex = 1000/frq;

        this.x = x;
        this.y = y;
        this.z = z;
        this.device = device;
        this.head = head;
        isScanSta = false;

        BleSBDYN.setLength(0);
        wifiSBDYN.setLength(0);

        wsnDynScanTimer = new Timer();
        wsnDynTimerTask = new wsnDynScanTimertask();
        wsnDynScanTimer.scheduleAtFixedRate(wsnDynTimerTask,0,timeIndex);
    }

    /**
     * 开启IMU动态扫描
     */
    public void startDynSensorScan(int device,char head,float x,float y,float z){
        int frq = 50;
        long timeIndex = 1000/frq;
        IMUSBDYN.setLength(0);

        this.x = x;
        this.y = y;
        this.z = z;
        this.device = device;
        this.head = head;

        isScanSta = false;
        mySensorListener = new MySensorListener();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor:sensors){
            sensorManager.registerListener(mySensorListener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
        }

        sensorDynScanTimer = new Timer();
        sensorDynTimerTask = new sensorDynScanTimerTask();
        sensorDynScanTimer.scheduleAtFixedRate(sensorDynTimerTask,0,timeIndex);

    }
    /**
     * WiFi/BLE定时扫描
     */
    private class wsnStaScanTimerTask extends TimerTask{
        /**
         * 参数列表
         */
        private int countNum = 0;

        @Override
        public void run() {
            countNum++;
            currentWSNstaTime = System.currentTimeMillis();

            wifiManager.startScan();
            //Wifi扫描结果
            lists = wifiManager.getScanResults();
            for (int i = 0; i < lists.size(); i++) {
                mScanResult = lists.get(i);
                wifiSBSTA.append((currentWSNstaTime) + "\t").append(device+"\t").append(head + "\t")
                        .append(x + "\t").append(y + "\t").append(z + "\t")
                        .append(mScanResult.BSSID + "\t").append(mScanResult.SSID + "\t")
                        .append(mScanResult.level + "\t").append("\n");
            }
            lists.clear();

            //BLE扫描,设置成低延迟扫描
            scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            bleScanner.startScan(null,scanSettings,scanCallback);
            //蓝牙扫描结果
            for(int i = 0;i < StaBLeDevice.getCount();i++){
                ibeacon = StaBLeDevice.getDevice(i);
                if(ibeacon!=null){
                    BleSBSTA.append(currentWSNstaTime + "\t").append(device+"\t").append(head+"\t")
                            .append(x + "\t").append(y + "\t").append(z + "\t")
                            .append(ibeacon.minor + "\t").append(ibeacon.major + "\t")
                            .append(ibeacon.rssi + "\t").append("\r\n");
                }

            }

            if(countNum > duration * 60 * 2){
                bleScanner.stopScan(scanCallback);
                StaBLeDevice.clear();
                wsnStaTimerTask.cancel();
                wsnStaScanTimer.cancel();
                chronometer.stop();
                MainActivity.mainActivity.waitingDialog.dismiss();
                new savaData(context,device,head,x,y,z,wifiSBSTA,BleSBSTA,IMUSBSTA,1).execute();
            }

        }
    }

    /**
     * IMU定时扫描
     */
    private class sensorStaScanTimerTask extends TimerTask{

        private int countNum = 0;

        @Override
        public void run() {
            countNum++;
            currentIMUSta = System.currentTimeMillis();

            IMUSBSTA.append(currentIMUSta+"\t").append(device+"\t").append(head+"\t")
                    .append(x+"\t").append(y+"\t").append(z+"\t")
                    .append(mySensorListener.getAcc_x()+"\t").append(mySensorListener.getAcc_y()+"\t")
                    .append(mySensorListener.getAcc_z()+"\t").append(mySensorListener.getGyro_x()+"\t")
                    .append(mySensorListener.getGyro_y()+"\t").append(mySensorListener.getGyro_z()+"\t")
                    .append(mySensorListener.getMag_x()+"\t").append(mySensorListener.getMag_y()+"\t")
                    .append(mySensorListener.getMag_z()+"\t").append("\n");

            if(countNum > duration * 60 * 50){
                sensorStaTimerTask.cancel();
                sensorStaScanTimer.cancel();
            }
        }
    }

    /**
     * Wifi/BlE动态扫描
     */
    private class wsnDynScanTimertask extends TimerTask{

        @Override
        public void run() {

            currrntWSNDyn = System.currentTimeMillis();

            wifiManager.startScan();
            //Wifi扫描结果
            lists = wifiManager.getScanResults();
            for (int i = 0; i < lists.size(); i++) {
                mScanResult = lists.get(i);
                wifiSBDYN.append((currrntWSNDyn) + "\t").append(device+"\t").append(head + "\t")
                        .append(x + "\t").append(y + "\t").append(z + "\t")
                        .append(mScanResult.BSSID + "\t").append(mScanResult.SSID + "\t")
                        .append(mScanResult.level + "\t").append("\n");
            }
            //BLE扫描,设置成低延迟扫描
            scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            bleScanner.startScan(null,scanSettings,scanCallback);
            //蓝牙扫描结果
            for(int i = 0;i < DynBLeDevice.getCount();i++){
                ibeacon = DynBLeDevice.getDevice(i);
                BleSBDYN.append(currrntWSNDyn + "\t").append(device+"\t").append(head+"\t")
                        .append(x + "\t").append(y + "\t").append(z + "\t")
                        .append(ibeacon.minor + "\t").append(ibeacon.major + "\t")
                        .append(ibeacon.rssi + "\t").append("\r\n");
            }


            if(SecondActivity.secondActivity.isFinish){
                bleScanner.stopScan(scanCallback);
                DynBLeDevice.clear();
                wsnDynTimerTask.cancel();
                wsnDynScanTimer.cancel();
                chronometer.stop();
                new savaData(context,device,head,x,y,z,wifiSBDYN,BleSBDYN,IMUSBDYN,2).execute();
            }
        }
    }

    /**
     * IMU动态扫描
     */
    private class sensorDynScanTimerTask extends TimerTask{

        @Override
        public void run() {
            currentIMUDyn = System.currentTimeMillis();

            IMUSBDYN.append(currentIMUDyn+"\t").append(device+"\t").append(head+"\t")
                    .append(x+"\t").append(y+"\t").append(z+"\t")
                    .append(mySensorListener.getAcc_x()+"\t").append(mySensorListener.getAcc_y()+"\t")
                    .append(mySensorListener.getAcc_z()+"\t").append(mySensorListener.getGyro_x()+"\t")
                    .append(mySensorListener.getGyro_y()+"\t").append(mySensorListener.getGyro_z()+"\t")
                    .append(mySensorListener.getMag_x()+"\t").append(mySensorListener.getMag_y()+"\t")
                    .append(mySensorListener.getMag_z()+"\t").append("\n");

            if(SecondActivity.secondActivity.isFinish){
                sensorDynTimerTask.cancel();
                sensorDynScanTimer.cancel();
            }
        }
    }
    /**
     * 蓝牙扫描回调方法
     */
    private ScanCallback scanCallback = new ScanCallback(){
        private int rssi;
        private BluetoothDevice BleDevice;
        private byte[] scanData;
        private iBeaconClass.iBeacon ibeacon;
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BleDevice = result.getDevice();
            rssi = result.getRssi();
            scanData =  result.getScanRecord().getBytes();
            ibeacon = iBeaconClass.fromScanData(BleDevice,rssi,scanData);
            if(isScanSta){
                StaBLeDevice.addDevice(ibeacon);
            }else{
                DynBLeDevice.addDevice(ibeacon);
            }

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

}
