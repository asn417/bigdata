package com.asn.application.service;

import com.alibaba.fastjson.JSONObject;
import com.asn.application.mapper.AppInfoMapper;
import com.asn.application.model.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier(value = "appInfoMapper")
    private AppInfoMapper appInfo;

    public String getAllAppInfo(){
        List<AppInfo> list = appInfo.getAllAppInfo();
        return JSONObject.toJSONString(list);
    }

}
