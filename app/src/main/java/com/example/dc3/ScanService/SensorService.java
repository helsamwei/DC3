package com.example.dc3.ScanService;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.dc3.MainActivity;
import com.example.dc3.SecondActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SensorService extends IntentService {

    private float dur,x,y,z;
    private int device;
    private char head;

    public SensorService() {
        super("SensorService");

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int scanMode = intent.getIntExtra("scanMode",2);
        //定时扫描
        if (scanMode==0) {
            dur = intent.getFloatExtra("dur",0);
            device = intent.getIntExtra("device",0);
            head = intent.getCharExtra("head",'e');
            x = intent.getFloatExtra("x",0);
            y = intent.getFloatExtra("y",0);
            z = intent.getFloatExtra("z",0);
            MainActivity.mainActivity.scan.startSensorStaScan(dur,device,head,x,y,z);
        }
        //动态扫描
        if(scanMode == 1){
            device = intent.getIntExtra("device",0);
            head = intent.getCharExtra("head",'e');
            x = intent.getFloatExtra("x",0);
            y = intent.getFloatExtra("y",0);
            z = intent.getFloatExtra("z",0);
            SecondActivity.secondActivity.scan.startDynSensorScan(device,head,x,y,z);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
