package com.example.dc3.BLE_Senor;

import java.util.ArrayList;
import com.example.dc3.BLE_Senor.iBeaconClass.iBeacon;

/**
 * 用于存放和读取iBeacon的ArrayList
 */
public class BleList {
    private ArrayList<iBeacon> mDevice = new ArrayList<iBeacon>();

    /**
     * 在iBeacon列表中添加扫描到的iBeacon
     * @param ibeacon
     */
    public void addDevice(iBeacon ibeacon){
        if(ibeacon == null){
            return;
        }

        for(int i=0;i<mDevice.size();i++){
            String btAddress = mDevice.get(i).bluetoothAddress;
            if(btAddress.equals(ibeacon.bluetoothAddress)){
                mDevice.add(i+1,ibeacon);
                mDevice.remove(i);
                return;
            }
        }
        mDevice.add(ibeacon);
    }

    /**
     * 获取iBeacon
     * @param position
     * @return
     */
    public iBeacon getDevice(int position){
        return mDevice.get(position);
    }

    /**
     * 获取扫描列表的大小
     * @return
     */
    public int getCount(){
        return mDevice.size();
    }

    /**
     * 清空列表
     */
    public void clear(){
        mDevice.clear();
    }

}
