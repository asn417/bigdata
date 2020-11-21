package com.asn.mysql

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/21 16:17
 * @Description: 只能读取一整个表，返回的是一个DataFrame
 **/
object MysqlConnect1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("test").master("local[2]").getOrCreate()
    val df: DataFrame = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost/big_data?serverTimezone=Asia/Shanghai")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("dbtable", "student")
      .option("user", "wangsen")
      .option("password", "root")
      .load()

    df.show()

    spark.stop()
  }
}
