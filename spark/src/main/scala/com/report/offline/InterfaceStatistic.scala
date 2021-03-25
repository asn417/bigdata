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

    import org.apache.spark.sql.functions._

    //添加一列spendTime生成新的df
    //val newDF: DataFrame = df.withColumn("spendTime",col("endTime")-col("startTime"))

    //这种方式比withColumn性能好
    val newDF: DataFrame = df.select(col("*"),col("endTime")-col("startTime") as "spendTime")
    //新的df创建视图
    newDF.createOrReplaceTempView("interfaceInfo")

    //sparkSQL操作视图计算调用次数top10
    val countTop10: DataFrame = spark.sql("select count(1) as num,userID from interfaceInfo group by userID order by num desc limit 10")
    countTop10.show()

    //sparkSQL操作视图计算平均耗时top10
    val timeTop10: DataFrame = spark.sql("select avg(spendTime) as avgTime,userID from interfaceInfo group by userID order by avgTime desc limit 10")
    timeTop10.show()


  }
}
