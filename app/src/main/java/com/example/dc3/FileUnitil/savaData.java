package com.example.dc3.FileUnitil;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dc3.MainActivity;
import com.example.dc3.SecondActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class savaData extends AsyncTask<Void,Void,Void> {
    /**
     * 参数列表
     */
    private Context context;
    private int device;
    private float x,y,z;
    private char head;
    private StringBuilder WifiSB;
    private StringBuilder BleSB;
    private StringBuilder IMUSB;

    private boolean saveWifi = false;
    private boolean saveBle = false;
    private boolean saveIMU = false;
    private int contentNum;
    /**
     *
     * @param context:上下文
     * @param device：设备号
     * @param head：手机朝向
     * @param x：手机x坐标
     * @param y：手机y坐标
     * @param z：手机z坐标
     * @param WifiSB：需保存的Wifi字符串
     * @param BleSB：需保存的Ble字符串
     */
    public savaData(Context context, int device, char head, float x, float y, float z, StringBuilder WifiSB,
                    StringBuilder BleSB,StringBuilder IMUSB,int contentNum){
        this.context = context;
        this.device = device;
        this.head = head;
        this.x = x;
        this.y = y;
        this.z = z;
        this.WifiSB = WifiSB;
        this.BleSB = BleSB;
        this.IMUSB = IMUSB;
        this.contentNum = contentNum;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        FileUtil fileUtil1 = new FileUtil();
        FileUtil fileUtil2 = new FileUtil();
        FileUtil fileUtil3 = new FileUtil();
        String content1 = WifiSB.toString();
        String content2 = BleSB.toString();
        String content3 = IMUSB.toString();
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date(currentTime);

        switch (contentNum){
            case 1:
                saveWifi = fileUtil1.saveData(MyConstans.DATA_PATH1 + "WIFI-device"+ device +"-"+ "RP("+x+","+y+","+z+")-head"+ "-"+head +"-"+formatter.format(date)+".txt", content1) && (WifiSB.length() > 0);
                saveBle = fileUtil2.saveData(MyConstans.DATA_PATH1 + "BLE-device"+ device + "-"+"RP("+x+","+y+","+z+")-head"+"-"+ head +"-"+formatter.format(date)+".txt" ,content2) && (BleSB.length() > 0);
                saveIMU = fileUtil3.saveData(MyConstans.DATA_PATH1 + "IMU-device"+ device + "-"+"RP("+x+","+y+","+z+")-head"+"-"+ head +"-"+formatter.format(date)+".txt" ,content3) && (IMUSB.length() > 0);
                break;
            case 2:
                saveWifi = fileUtil1.saveData(MyConstans.DATA_PATH2 + "WIFI-device"+ device +"-"+ "RP("+x+","+y+","+z+")-head"+ "-"+head +"-"+formatter.format(date)+".txt", content1) && (WifiSB.length() > 0);
                saveBle = fileUtil2.saveData(MyConstans.DATA_PATH2 + "BLE-device"+ device + "-"+"RP("+x+","+y+","+z+")-head"+"-"+ head +"-"+formatter.format(date)+".txt" ,content2) && (BleSB.length() > 0);
                saveIMU = fileUtil3.saveData(MyConstans.DATA_PATH2 + "IMU-device"+ device + "-"+"RP("+x+","+y+","+z+")-head"+"-"+ head +"-"+formatter.format(date)+".txt" ,content3) && (IMUSB.length() > 0);
                break;
        }
        WifiSB=null;
        BleSB=null;
        IMUSB=null;

        System.out.println("saveWifi=" +saveWifi);
        System.out.println("saveBle="+ saveBle);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (saveWifi && saveBle && saveIMU) {
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "保存错误", Toast.LENGTH_SHORT).show();
        }
    }
}
