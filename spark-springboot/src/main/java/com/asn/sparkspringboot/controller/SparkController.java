package com.asn.sparkspringboot.controller;

/**
 * @Author: wangsen
 * @Date: 2020/11/21 11:03
 * @Description:
 **/
import com.asn.aop.LogToKafka;
import com.asn.sparkspringboot.model.SparkAppPara;
import com.asn.sparkspringboot.service.SparkAppInfoService;
import com.asn.sparkspringboot.service.SparkSubmitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class SparkController {
    private static Logger logger = LoggerFactory.getLogger(SparkController.class);
    @Autowired
    SparkSubmitService submitService;

    @Autowired
    SparkAppInfoService sparkAppInfoService;

    @RequestMapping("/appInfo")
    public String appInfo(){
        return "appInfo";
    }

    /***
     * @Author: wangsen
     * @Description: 提交应用
     * @Date: 2020/11/21
     * @Param: [sparkAppPara]
     * @Return: java.lang.String
     **/
    @RequestMapping(value = "/submit")
    @ResponseBody
    public String Submit(@RequestBody SparkAppPara sparkAppPara) throws IOException, InterruptedException {
        String resultJson = submitService.submitApp(sparkAppPara);
        System.out.println("======resultJson======"+resultJson);
        return resultJson;
    }

    @RequestMapping("/result")
    public ModelAndView toResult(String resultJson){
        System.out.println("======toResult======"+resultJson);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("result");
        mav.addObject("resultJson",resultJson);

        return mav;
    }

    @RequestMapping("/submitApp")
    public ModelAndView submitApp(String mainClass,String jarPath){

        ModelAndView mav = new ModelAndView();
        mav.setViewName("submitApp");
        mav.addObject("mainClass",mainClass);
        mav.addObject("jarPath",jarPath);

        return mav;
    }

    @RequestMapping("/getAllAppInfo")
    @ResponseBody
    public String getAllAppInfo(){
        logger.info("==================info");
        logger.warn("==================warn");
        logger.error("=================error");
        logger.debug("=================debug");
        return "xxxxxxxxxxxxxxx";
        //return sparkAppInfoService.getAllAppInfo();
    }

    @RequestMapping("/testLogToKafka")
    @ResponseBody
    @LogToKafka(topic = "topic1")
    public String testLogToKafka(String topic){
        System.out.println("args: "+topic);
        return topic;
    }
}
