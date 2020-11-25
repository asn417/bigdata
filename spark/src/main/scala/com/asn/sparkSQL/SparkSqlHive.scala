package com.asn.sparkSQL

import java.io.File

import org.apache.spark.sql.SparkSession

/**
 * 官网：http://spark.apache.org/docs/3.0.0/sql-data-sources-hive-tables.html
 *
 * sparkSQL可以通过hive-site.xml文件的配置直接读取hive元数据.
 * 通过enableHiveSupport()方法开启hive支持，前提是需要将hive-site.xml拷贝到spark的配置目录下
 */
object SparkSqlHive {
  def main(args: Array[String]): Unit = {

    // warehouseLocation points to the default location for managed databases and tables
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath

    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._
    import spark.sql

    sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING) USING hive")
    sql("LOAD DATA LOCAL INPATH 'data/kv1.txt' INTO TABLE src")

    // Queries are expressed in HiveQL
    sql("SELECT * FROM src").show()
  }
}
case class Record(key: Int, value: String)
