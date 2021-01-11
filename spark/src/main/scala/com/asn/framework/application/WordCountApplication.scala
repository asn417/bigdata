package com.asn.framework.application

import com.asn.framework.common.TApplication
import com.asn.framework.controller.WordCountController
/*
用这个类的全类名启动spark应用即可
bin/spark-submit --class com.asn.framework.application.WordCountApplication --master yarn --deploy-mode cluster ./spark-1.0-SNAPSHOT.jar
 */
object WordCountApplication extends App with TApplication{
  start("yarn","WordCountApp"){
    val controller = new WordCountController
    controller.dispatch()
  }
}
