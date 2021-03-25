package com.asn.strategy;

import com.asn.json.model.BaseJSONVo;
import com.asn.utils.CommonPairResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class FormPreviewSubmitHandler implements FormSubmitHandler<Serializable> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getSubmitType() { return "preview"; }

    @Override
    public BaseJSONVo handleSubmit(FormSubmitRequest request) {
        logger.info("预览模式提交：userId={}, formInput={}", request.getUserId(), request.getFormInput());
        return CommonPairResponse.success("预览模式提交数据成功！", null);
    }
}