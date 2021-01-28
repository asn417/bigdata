package com.asn.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asn.json.model.BaseJSONVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
public class SimpleController {
    @Autowired
    private FormService formService;
    @RequestMapping("/form/submit")
    public BaseJSONVo<String, Serializable> submitForm(@RequestParam String submitType, @RequestParam String formInputJson) {
        JSONObject formInput = JSON.parseObject(formInputJson);
        FormSubmitRequest request = new FormSubmitRequest();
        request.setUserId(123456L);
        request.setSubmitType(submitType);
        request.setFormInput(formInput);
        return formService.submitForm(request);
    }
}