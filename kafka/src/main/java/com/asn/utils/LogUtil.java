package com.asn.utils;

import java.util.Random;

/**
 * @Author: wangsen
 * @Date: 2021/1/21 19:34
 * @Description:
 **/
public class LogUtil {
    private static String[] province = {"山东","河南","河北","北京","天津","辽宁","吉林","黑龙江","上海","内蒙古","福建","江苏","浙江","广东","广西","甘肃","江西","湖北","湖南","贵州","重庆","四川","云南"};
    private static Random r=new Random();
    public static String getProvince(){
        return province[r.nextInt(23)];//[0,23)
    }

    public static String getID(){
        return String.valueOf(r.nextInt(20));
    }

    public static String getPrice(){
        return String.valueOf(r.nextInt(100));
    }
}
