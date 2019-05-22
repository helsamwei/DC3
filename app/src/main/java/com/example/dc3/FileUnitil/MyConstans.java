package com.example.dc3.FileUnitil;

import android.os.Environment;

/**
 * 数据采集存储路径
 */

public class MyConstans {
    public static String DATA_PATH1 = Environment.getExternalStorageDirectory().getPath()
            + "/"+"DC3.0"+ "/";//获取SD卡的根目录1,定时采集根目录
    public static String DATA_PATH2 = Environment.getExternalStorageDirectory().getPath()
            + "/"+"DC3.0_Dyn"+ "/";//获取SD卡的根目录2，动态采集根目录
}
