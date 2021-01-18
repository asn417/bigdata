package com.asn.utils;

import com.asn.json.model.BaseJSONVo;

/**
 * @Author: wangsen
 * @Date: 2020/12/18 17:03
 * @Description:
 **/
public class REJSONUtils {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String WARN = "warn";
    public static final String ERROR_CODE = "code";
    public static final String ERROR_DETAIL = "msg";

    public static BaseJSONVo success(Object data, String msg) {
        return getAppBaseJSONVo(SUCCESS,data,true,1,msg);
    }

    public static BaseJSONVo getAppBaseJSONVo(String type, Object data, boolean isSuccess, int code, Object msg) {
        BaseJSONVo reData = new BaseJSONVo();
        reData.setData(data);
        reData.setResult(type);
        reData.setSuccess(isSuccess);
        reData.setCode(code);
        reData.setMsg(msg == null ? "" : msg.toString());
        return reData;
    }
    public static BaseJSONVo failure(String msg) {
        return getAppBaseJSONVo(ERROR,null,false,0,msg);
    }
}
