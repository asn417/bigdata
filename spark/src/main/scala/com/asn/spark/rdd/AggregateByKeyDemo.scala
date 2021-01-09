package com.asn.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/*

 */
object AggregateByKeyDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[(String,Int)] = context.makeRDD(List(("a",1),("a",2),("a",3),("a",4),("b",1),("b",2)), 2)

    value.aggregateByKey(0)( //初始值
      (x,y)=>math.max(x,y), //分区内逻辑
      (x,y)=>x+y            //分区间逻辑
    ).collect().foreach(println)
  }

}
