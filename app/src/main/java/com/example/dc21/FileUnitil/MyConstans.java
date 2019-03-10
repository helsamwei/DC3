package com.example.dc21.FileUnitil;

import android.os.Environment;

/**
 * 数据采集存储路径
 */

public class MyConstans {
    public static String DATA_PATH1 = Environment.getExternalStorageDirectory().getPath()
            + "/"+"DC2.1"+ "/";//获取SD卡的根目录
}
