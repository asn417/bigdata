package com.asn.sparkSQL.hive

import org.apache.spark.sql.SparkSession

/**
 * @Author: wangsen
 * @Date: 2021/1/19 15:06
 * @Description:
 **/
object ReadHiveTable {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      //.master("local[*]")//提交yarn运行时需要注释掉这一行
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
        |select count(0) from room r join project p on r.FProjectID = p.FID;
        |""".stripMargin)
        .collect().foreach(println)
    println(System.currentTimeMillis())
    spark.close()

  }

}
