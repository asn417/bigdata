package com.asn.framework.common

import com.asn.framework.controller.WordCountController
import com.asn.framework.util.EnvUtil
import org.apache.spark.{SparkConf, SparkContext}

trait TApplication {

  def start(master:String="local[*]",app:String="MyApplication")(op : => Unit)={
    val sparkConf: SparkConf = new SparkConf().setMaster(master).setAppName(app)
    val sc = new SparkContext(sparkConf)
    EnvUtil.put(sc)
    try {
      op
    }catch {
      case ex => println(ex.getMessage)
    }
    sc.stop()
    EnvUtil.clear()
  }

}
