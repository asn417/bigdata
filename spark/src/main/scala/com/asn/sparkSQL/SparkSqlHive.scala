package com.asn.sparkSQL

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

/**
 * 提交到yarn运行
 * spark-submit --master yarn --class com.asn.sparkSQL.SparkSqlHive spark-1.0-SNAPSHOT.jar
 */
object SparkSqlHive {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("Hive on Spark")
      .config("spark.sql.warehouse.dir","hdfs://ns1/user/hive")
      .config("hive.metastore.uris","thrift://flink3:9083")
      .enableHiveSupport()
      .getOrCreate()

    val schema = StructType(
      List(
        StructField("name",StringType),
        StructField("age",IntegerType),
      )
    )

    //读取数据到DF
    val df: DataFrame = spark.read.option("delimiter", ",")
      .schema(schema)
      .csv("hdfs:///student.csv")

    import spark.implicits._

    //过滤数据
    val resultDF = df.where('age>30)

    //将数据存到spark数据库的student表
    resultDF.write.mode(SaveMode.Overwrite).saveAsTable("spark.student")
  }
}
