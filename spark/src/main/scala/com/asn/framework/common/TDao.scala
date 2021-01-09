package com.asn.framework.common

import com.asn.framework.util.EnvUtil
import org.apache.spark.rdd.RDD

trait TDao {
  def readFile(path:String):RDD[String]
}
