package com.report.offline

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2021/2/24 14:09
 * @Description: 接口调用信息统计
 * 平均耗时top10
 * 调用次数top10
 **/
object InterfaceStatistic {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("InterfaceStatistic").master("local[*]").getOrCreate()
    val df: DataFrame = spark.read.json("E:\\bigdata\\logs\\test.json")
    df.show()


    df.createOrReplaceTempView("interfaceInfo")

    val countTop10: DataFrame = spark.sql("select count(1) as num,userID from interfaceInfo group by userID order by num desc limit 10")
    countTop10.show()


  }
}
