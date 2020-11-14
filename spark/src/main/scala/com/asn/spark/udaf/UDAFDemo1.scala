package com.asn.spark.udaf

import org.apache.spark.Aggregator
import org.apache.spark.sql.SparkSession

/**
 * @Author: wangsen
 * @Date: 2020/11/14 17:41
 * @Description:
 **/
object UDAFDemo1 {
  def main(args: Array[String]): Unit = {
    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()



    sparkSession.stop()
  }
}
case class UserBean(name:String,age:BigInt)
case class AvgBuffer(var sum:BigInt,var count:Int)

//声明自定义聚合函数（强类型）
/*class MyAvgClassFunction extends Aggregator[ UserBean,AvgBuffer,Double ]{


}*/

