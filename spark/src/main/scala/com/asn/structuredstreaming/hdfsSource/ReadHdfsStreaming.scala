package com.asn.structuredstreaming.hdfsSource

import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 使用structured streaming流式的读取hdfs上的小文件
 * 读取hdfs的source叫FileStreamSource，不仅可以读取hdfs，还可以读取本地文件file://、亚马逊云s3://、阿里云oss://等文件系统。
 */
object ReadHdfsStreaming {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[6]").appName(this.getClass.getName).getOrCreate()

    val schema: StructType = new StructType()
      .add("name", "string")
      .add("age", "integer")
    //读取数据，只能是目录，不能是文件
    val source: DataFrame = spark.readStream
      .schema(schema)
      .json("hdfs://xxx")

    //直接输出
    source.writeStream
      .outputMode(OutputMode.Append())//append不会更新状态
      .format("console")
      .start()
      .awaitTermination()

  }
}
