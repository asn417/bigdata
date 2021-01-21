package com.asn.application.mapper;

import com.asn.application.model.AppInfo;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: wangsen
 * @Date: 2020/11/21 11:09
 * @Description:
 **/
@Component
public interface AppInfoMapper {
    @Select("SELECT * FROM appinfo")
    @Results({
            @Result(property = "mainClass",  column = "mainclass"),
            @Result(property = "jarPath", column = "jarpath"),
            @Result(property = "note", column = "note")
    })
    List<AppInfo> getAllAppInfo();
}

