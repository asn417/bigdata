package com.asn.dataframe

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/14 16:53
 * @Description:
 **/
object DataFrame {
  def main(args: Array[String]): Unit = {

    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    val frame: DataFrame = sparkSession.read.json("E:\\bigdata\\spark\\src\\main\\resources\\person.json")

    //展示数据
    frame.show()

    //将dataframe转换成表
    frame.createOrReplaceTempView("person")
    //用SQL的方式获取数据
    sparkSession.sql("select * from person").show()

    sparkSession.stop()

  }
}
