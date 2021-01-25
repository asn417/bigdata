package com.asn.utils;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * @Author: wangsen
 * @Date: 2021/1/21 19:34
 * @Description:
 **/
public class LogUtil {
    private static String[] province = {"山东","河南","河北","北京","天津","辽宁","吉林","黑龙江","上海","内蒙古","福建","江苏","浙江","广东","广西","甘肃","江西","湖北","湖南","贵州","重庆","四川","云南"};

    public static String getProvince(){
        return province[RandomUtils.nextInt(0,23)];//[0,22]
    }

    public static long getUserID(){
        return RandomUtils.nextLong(10,100);//[10,99]
    }

    public static long getProductID(){
        return getUserID();
    }

    public static long getProductTypeID(){
        return getUserID();
    }

    public static float getPrice(){
        return RandomUtils.nextFloat(10f,50f);
    }

    public static long getTimeStamp(){
        return System.currentTimeMillis();
    }
}
