package com.asn.strategy;

import com.asn.json.model.BaseJSONVo;
import com.asn.utils.CommonPairResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FormModelSubmitHandler implements FormSubmitHandler<Long> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getSubmitType() { return "model"; }

    @Override
    public BaseJSONVo handleSubmit(FormSubmitRequest request) {
        logger.info("模型提交：userId={}, formInput={}", request.getUserId(), request.getFormInput());
        // 模型创建成功后获得模型的 id
        Long modelId = createModel(request);
        return CommonPairResponse.success(null,"模型提交成功！");
    }
    private Long createModel(FormSubmitRequest request) {
        // 创建模型的逻辑
        return 123L;
    }
}