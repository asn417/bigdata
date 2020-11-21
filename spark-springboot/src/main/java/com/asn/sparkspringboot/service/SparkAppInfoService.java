package com.asn.sparkspringboot.service;

import com.alibaba.fastjson.JSONObject;
import com.asn.sparkspringboot.mapper.AppInfoMapper;
import com.asn.sparkspringboot.model.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wangsen
 * @Date: 2020/11/21 11:06
 * @Description:
 **/
@Service
public class SparkAppInfoService {

    @Autowired
    private AppInfoMapper appInfo;

    public String getAllAppInfo(){
        List<AppInfo> list = appInfo.getAllAppInfo();
        return JSONObject.toJSONString(list);
    }

}
