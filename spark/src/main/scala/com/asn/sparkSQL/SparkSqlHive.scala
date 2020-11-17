package com.asn.sparkSQL

import org.apache.spark.sql.SparkSession

/**
 * sparkSQL可以通过hive-site.xml文件的配置直接读取hive元数据.
 * 通过enableHiveSupport()方法开启hive支持，前提是需要将hive-site.xml拷贝到spark的配置目录下
 */
object SparkSqlHive {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[*]").appName("Hive on Spark")
      .enableHiveSupport().getOrCreate()

    spark.sql("").show()

    spark.stop()
  }
}
