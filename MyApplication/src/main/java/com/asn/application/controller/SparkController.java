package com.asn.application.controller;

/**
 * @Author: wangsen
 * @Date: 2020/11/21 11:03
 * @Description:
 **/
import com.asn.aop.LogToKafka;
import com.asn.json.model.BaseJSONVo;
import com.asn.utils.CommonPairResponse;
import com.asn.config.ConfigTest;
import com.asn.config.Dog;
import com.asn.hbase.config.HBaseConfig;
import com.asn.hbase.utils.HBaseUtil;
import com.asn.producer.ProducerUtilConf;
import com.asn.application.model.SparkAppPara;
import com.asn.application.service.SparkAppInfoService;
import com.asn.application.service.SparkSubmitService;
import com.asn.utils.SpringContextUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
public class SparkController {
    private static Logger logger = LoggerFactory.getLogger(SparkController.class);
    @Autowired
    SparkSubmitService submitService;

    @Autowired
    SparkAppInfoService sparkAppInfoService;

    @Autowired
    private ConfigTest configTest;

    @Autowired
    private Dog dog;

    @Resource(name = "dog2")
    private Dog dog2;
    @Autowired
    private ApplicationContext ioc;
    @Resource
    private ProducerUtilConf producerUtilConf;
    @Autowired
    private HBaseConfig hBaseConfig;

    @RequestMapping("/hbase")
    public BaseJSONVo hbase(){
        System.out.println(hBaseConfig);
        HBaseUtil.getInstance(hBaseConfig);
        RegionInfo regionInfo = HBaseUtil.getRegionInfo("mydb:test");
        String appName = SpringContextUtil.getAppName();
        System.out.println("appName: "+appName);
        String contextName = SpringContextUtil.getContextName();
        System.out.println("contextName: "+appName);
        Dog dog = (Dog)SpringContextUtil.getBean("dog");
        dog.setDogName("asdasdasdasdasd");
        System.out.println(dog);
        return CommonPairResponse.success(regionInfo.getRegionNameAsString(),"");
    }

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
        logger.error("======="+dog.toString());
        logger.error("======="+dog2.toString());
        logger.error("========"+ioc.getBean("dog"));
        logger.error("========"+ioc.getBean("dog2"));
        logger.error("producerUtilConf=>"+producerUtilConf);
        logger.error("producerUtilConf.getServers=>"+producerUtilConf.getServers());
        logger.error("producerUtilConf.getInstance=>"+producerUtilConf.getInstance());
        return configTest.toString();
        //return sparkAppInfoService.getAllAppInfo();
    }

    @RequestMapping("/generateLog")
    @ResponseBody
    @LogToKafka(topic = "test-topic")
    public String testLogToKafka(String topic) throws InterruptedException {
        System.out.println("线程："+Thread.currentThread().getId()+"，请求开始："+System.currentTimeMillis());
        TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(10,1000));
        System.out.println("线程："+Thread.currentThread().getId()+"，请求结束："+System.currentTimeMillis());
        return topic;
    }
}
