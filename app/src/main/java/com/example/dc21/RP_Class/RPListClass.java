package com.example.dc21.RP_Class;

import java.util.ArrayList;

public class RPListClass {
    private String RP_Each;
    private ArrayList<String> RP_List = new ArrayList<String>();



    /**
     * 添加扫描的RP坐标
     *
     */
    public void addString(String x,String y,String z){
        RP_Each = "RP:(" + x + "," + y + "," + z + ")";
        RP_List.add(RP_Each);
    }

    /**
     * 返回RP位置坐标字符
     * @return
     */
    public String getItem(int position){
        return RP_List.get(position);
    }

    /**
     *
     */
    public ArrayList<String> getList(){
        return RP_List;
    }
    /**
     *
     * @return
     */
    public int getSize(){
        return RP_List.size();
    }
}
