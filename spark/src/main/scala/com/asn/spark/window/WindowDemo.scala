package com.asn.spark.window

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @Author: wangsen
 * @Date: 2020/11/15 15:26
 * @Description:
 **/
object WindowDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("window-wordcount")
    val streamingContext: StreamingContext = new StreamingContext(sparkConf,Seconds(3))

    val socketStreaming: ReceiverInputDStream[String] = streamingContext.socketTextStream("flink1",9999)
    //窗口大小和滑动步长需要设置成批次的整数倍
    socketStreaming.window(Seconds(6),Seconds(3))
    val dstream: DStream[(String, Int)] = socketStreaming.flatMap(line=>line.split(" ")).map(word=>(word,1))
    dstream.reduceByKey(_+_).print()

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
