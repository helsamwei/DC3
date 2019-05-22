package com.example.dc3.IMUSensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MySensorListener implements SensorEventListener {

    //加速度计
    private float acc_x,acc_y,acc_z;
    //陀螺仪
    private float gyro_x,gyro_y,gyro_z;
    //地磁
    private float mag_x,mag_y,mag_z;
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            //加速度
            case Sensor.TYPE_ACCELEROMETER:
                acc_x = event.values[0];
                acc_y = event.values[1];
                acc_z = event.values[2];
                break;
            //角速度
            case Sensor.TYPE_GYROSCOPE:
                gyro_x = event.values[0];
                gyro_y = event.values[1];
                gyro_z = event.values[2];
                break;
            //地磁
            case Sensor.TYPE_MAGNETIC_FIELD:
                mag_x = event.values[0];
                mag_y = event.values[0];
                mag_z = event.values[0];
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getAcc_x() {
        return acc_x;
    }

    public float getAcc_y() {
        return acc_y;
    }

    public float getAcc_z() {
        return acc_z;
    }

    public float getGyro_x() {
        return gyro_x;
    }

    public float getGyro_y() {
        return gyro_y;
    }

    public float getGyro_z() {
        return gyro_z;
    }

    public float getMag_x() {
        return mag_x;
    }

    public float getMag_y() {
        return mag_y;
    }

    public float getMag_z() {
        return mag_z;
    }
}
