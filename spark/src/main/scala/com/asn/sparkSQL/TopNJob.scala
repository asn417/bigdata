package com.asn.sparkSQL

import org.apache.spark.sql.SparkSession

/**
 * @Author: wangsen
 * @Date: 2021/1/27 15:13
 * @Description: 按项目分组统计房间数前5的项目
 **/
object TopNJob {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local[*]")//提交yarn运行时需要注释掉这一行
      .appName("Hive on Spark")
      .config("spark.sql.warehouse.dir","hdfs://master:9000/user/hive")
      .config("hive.metastore.uris","thrift://master:9083")
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("show databases;").show()
    spark.sql("use ods;")
    println(System.currentTimeMillis())
    spark.sql(
      """
        |select count(0) as num,FProjectID from room group by FProjectID order by num desc limit 5;
        |""".stripMargin)
      .collect().foreach(println)
    println(System.currentTimeMillis())
    spark.close()
  }
}
