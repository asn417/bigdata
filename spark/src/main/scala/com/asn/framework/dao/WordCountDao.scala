package com.asn.framework.dao

import com.asn.framework.common.TDao
import com.asn.framework.util.EnvUtil
import org.apache.spark.rdd.RDD

class WordCountDao extends TDao{
  def readFile(path:String):RDD[String]={
    EnvUtil.take().textFile(path)
  }
}
