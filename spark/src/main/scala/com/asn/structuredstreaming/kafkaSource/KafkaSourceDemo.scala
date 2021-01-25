package com.asn.structuredstreaming.kafkaSource

import net.minidev.json.JSONObject
import net.minidev.json.parser.JSONParser
import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}

import scala.collection.mutable

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
      .option("kafka.bootstrap.servers", "master:9092")
      .option("subscribe", "test-topic")//可以订阅多个topic，还可以使用通配符
      .option("startingOffsets","earliest")//earliest、assign、latest
      .load()
    // Subscribe to 1 topic

    //打印读取的kafka的数据结构
    source.printSchema()

    val schema = StructType(mutable.Seq(
      StructField("userID", DataTypes.StringType),
      StructField("province", DataTypes.StringType),
      StructField("productID", DataTypes.StringType),
      StructField("productTypeID", DataTypes.StringType),
      StructField("price", DataTypes.IntegerType)
    ))
    val query: StreamingQuery = source.selectExpr( "CAST(value AS STRING)","cast(timestamp as STRING)")
      .writeStream.format("console").outputMode("append").start()
/*
    val result = source.selectExpr( "CAST(value AS STRING)","cast(timestamp as STRING)")
      .map(a => {
        val value: String = a.getAs("value")
        val timestamp: String = a.getAs("timestamp")

        val jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE)
        val obj: JSONObject = jsonParser.parse(value).asInstanceOf[JSONObject]
        val userID: AnyRef = obj.get("userID")
        val province: AnyRef = obj.get("province")
        val productID: AnyRef = obj.get("productID")
        val productTypeID: AnyRef = obj.get("productTypeID")
        val price: AnyRef = obj.get("price")

        OrderData(userID.toString,province.toString,productID.toString,productTypeID.toString,price.toString,timestamp.toString)

      }).createOrReplaceTempView("view")

    val start: StreamingQuery = spark.sql("select userID,count(*) from view group by userID")
      .writeStream
      .format("console")
      .outputMode("complete")
      .start*/

    //start.awaitTermination()
query.awaitTermination()
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
case class KafkaSource(json:String,timestamp: String)
case class OrderData(userID:String,province:String,productID:String,productTypeID:String,price:String,timestamp: String)