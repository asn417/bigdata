package com.asn.sparkSQL

import org.apache.spark.sql.SparkSession

/**
 * @Author: wangsen
 * @Date: 2020/11/25 17:51
 * @Description: 读写parquet文件
 **/
object ReadWriteParquet {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .getOrCreate()

    spark.read
      .option("header",true)
      .csv()
  }

}
