package com.asn.structuredstreaming.kafkaSource

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.streaming.StreamingQuery

/**
 * http://spark.apache.org/docs/3.0.0/structured-streaming-kafka-integration.html
 *
 * RDD的分区和topic的分区是一一对应的
 */
object KafkaSourceDemo {
  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName("structured-kafkasource")
      .getOrCreate()
    import spark.implicits._

    val source: DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribe", "topicA")//可以订阅多个topic，还可以使用通配符
      .option("startingOffsets","earliest")//earliest、assign、latest
      .load()
    // Subscribe to 1 topic

    //打印读取的kafka的数据结构
    source.printSchema()

    val result: StreamingQuery = source.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
      .writeStream
      .format("console")
      .outputMode("append")
      .start

    result.awaitTermination()
    spark.stop()
    /*// Subscribe to 1 topic, with headers
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribe", "topic1")
      .option("includeHeaders", "true")
      .load()
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)", "headers")
      .as[(String, String, Map)]

    // Subscribe to multiple topics
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribe", "topic1,topic2")
      .load()
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]

    // Subscribe to a pattern
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribePattern", "topic.*")
      .load()
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]*/
  }

}
