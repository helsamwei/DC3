package com.example.dc21.FileUnitil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    public boolean saveZip(String fileName, String content) {
        try {
            File file = new File(fileName);
            //input为内存里的数据
            InputStream input = getStringInputStream(content);
            ZipOutputStream zipOut;
            zipOut = new ZipOutputStream(new FileOutputStream(file));
            zipOut.putNextEntry(new ZipEntry(file.getName().replace("zip", "txt")));
            int temp = 0;
            while (( temp=input.read()) != -1) {
                zipOut.write(temp);
            }
            input.close();
            zipOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static InputStream getStringInputStream(String content) {
        if (content != null && !content.equals("")) {
            try {
                ByteArrayInputStream stringInputStream = new ByteArrayInputStream(content.getBytes());
                return stringInputStream;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public boolean saveData(String fileName, String content) {
        boolean res=false;
        File file = new File(fileName);
        FileOutputStream os;
        try {
            os = new FileOutputStream(file,true);
            os.write(content.getBytes());
            os.close();
            res=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
