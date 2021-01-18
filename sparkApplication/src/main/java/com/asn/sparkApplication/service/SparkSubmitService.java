package com.asn.sparkApplication.service;

import com.asn.sparkApplication.model.SparkAppPara;
import com.asn.sparkApplication.util.RestUtil;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wangsen
 * @Date: 2020/11/21 11:07
 * @Description:
 **/
@Service
public class SparkSubmitService {

    public SparkSubmitService(){

    }
    public String submitApp(SparkAppPara sparkAppPara) throws IOException, InterruptedException {
        if (null==sparkAppPara || "".equals(sparkAppPara)){
            return "param is empty";
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SparkLauncher launcher = new SparkLauncher()
                .setSparkHome("/opt/bigdata/spark-3.0.0-bin-hadoop3.2")
                .setAppResource(sparkAppPara.getJarPath())
                .setMainClass(sparkAppPara.getMainClass())
                .setMaster(sparkAppPara.getMaster())
                .setDeployMode(sparkAppPara.getDeployMode())
                .setConf("spark.driver.memory", sparkAppPara.getDriverMemory()+"g")
                .setConf("spark.executor.memory", sparkAppPara.getExecutorMemory()+"g")
                .setConf("spark.executor.instances", sparkAppPara.getExecutorInstances())
                .setConf("spark.executor.cores", sparkAppPara.getExecutorCores())
                .setConf("spark.default.parallelism", sparkAppPara.getDefaultParallelism())
                .setConf("spark.driver.extraJavaOptions", sparkAppPara.getDriverExtraJavaOptions());

        String otherConf = sparkAppPara.getOtherConf();
        if (!otherConf.isEmpty()){
            String[] confs = otherConf.split(";");
            if (confs.length>0){
                for (String conf:confs) {
                    launcher.setConf(conf.substring(0,conf.indexOf("=")).trim(),conf.substring(conf.indexOf("=")+1,conf.length()));
                }
            }
        }

        SparkAppHandle handle =launcher.setVerbose(true).startApplication(new SparkAppHandle.Listener() {
            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
            @Override
            public void stateChanged(SparkAppHandle sparkAppHandle) {
                if (sparkAppHandle.getState().isFinal()) {
                    countDownLatch.countDown();
                }
                System.out.println("State changed:" + sparkAppHandle.getState().toString());
            }
            @Override
            public void infoChanged(SparkAppHandle sparkAppHandle) {
                System.out.println("Info changed:" + sparkAppHandle.getState().toString());
            }
        });
        System.out.println("The task is executing, please wait ....");
        //线程等待任务结束
        countDownLatch.await();
        System.out.println("The task is finished!");

        String restUrl = "http://flink1:8088/cluster/app/"+handle.getAppId();
        String resultJson = RestUtil.httpGet(restUrl,null);
        System.out.println("appId:"+handle.getAppId());
        return resultJson;
    }
}
