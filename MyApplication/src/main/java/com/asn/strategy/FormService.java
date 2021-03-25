package com.asn.strategy;

import com.asn.json.model.BaseJSONVo;
import org.springframework.lang.NonNull;

/**
 * @Author: wangsen
 * @Date: 2021/1/28 16:15
 * @Description:
 **/
public interface FormService {
    BaseJSONVo submitForm(@NonNull FormSubmitRequest request);
}
