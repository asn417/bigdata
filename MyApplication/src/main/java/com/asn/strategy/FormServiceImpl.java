package com.asn.strategy;

import com.asn.json.model.BaseJSONVo;
import com.asn.utils.CommonPairResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class FormServiceImpl implements FormService {
    @Autowired
    private FormSubmitHandlerFactory submitHandlerFactory;

    public BaseJSONVo<String, Serializable> submitForm(@NonNull FormSubmitRequest request) {
        String submitType = request.getSubmitType();
        // 根据 submitType 找到对应的提交处理器
        FormSubmitHandler<Serializable> submitHandler = submitHandlerFactory.getHandler(submitType);
        // 判断 submitType 对应的 handler 是否存在
        if (submitHandler == null) {
            return CommonPairResponse.failure("非法的提交类型");
        }
        // 处理提交
        return submitHandler.handleSubmit(request);
    }
}