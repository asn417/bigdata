package com.asn.sparkspringboot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @Author: wangsen
 * @Date: 2020/11/21 11:08
 * @Description:
 **/
public class RestUtil {

    /**
     * 实现对REST服务的请求
     * @param urlStr
     * @param urlParam
     * @return
     * @throws IOException
     */
    public static String httpGet(String urlStr, List<String> urlParam) throws IOException, InterruptedException {
        URL url = new URL(urlStr);
        HttpURLConnection connet = null;
        int i = 0;
        while(connet==null || connet.getResponseCode() != 200 ){
            connet = (HttpURLConnection) url.openConnection();
            connet.setRequestMethod("GET");
            connet.setRequestProperty("Charset", "UTF-8");
            connet.setRequestProperty("Content-Type", "application/json");
            connet.setConnectTimeout(15000);// 连接超时 单位毫秒
            connet.setReadTimeout(15000);// 读取超时 单位毫秒
            i++;
            if (i==50)break;
            Thread.sleep(500);
        }
        //将返回的值存入到String中
        BufferedReader brd = new BufferedReader(new InputStreamReader(connet.getInputStream(),"UTF-8"));
        StringBuilder  sb  = new StringBuilder();
        String line;

        while((line = brd.readLine()) != null){
            sb.append(line);
        }
        brd.close();
        connet.disconnect();
        return sb.toString();
    }
}
