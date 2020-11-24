package com.asn.structuredstreaming

import org.apache.spark.sql.types.{DataTypes, IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

object FileSourceDemo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local[*]")
      .appName("FileSource")
      .getOrCreate()

    import spark.implicits._
    val schema: StructType = StructType(Array(StructField("name", DataTypes.StringType), StructField("age", DataTypes.IntegerType)))

    val df: DataFrame = spark.readStream
      .format("csv")
      .schema(schema)
      .load("xxx")//只能是目录，不能是文件

    df.writeStream
      .format("console")
      .outputMode("update")
      .start
      .awaitTermination()

    spark.stop()
  }

}
