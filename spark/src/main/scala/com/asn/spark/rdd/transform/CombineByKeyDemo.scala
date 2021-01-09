package com.asn.spark.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
  combineByKey跟aggregateByKey类似，只是不需要使用初始值，直接将第一个数当做初始值。
 */
object CombineByKeyDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[(String,Int)] = context.makeRDD(List(("a",1),("a",2),("a",3),("a",4),("b",1),("b",2)), 2)

    value.combineByKey(
      v=>(v,1),
      (t:(Int,Int),v)=>{
        (t._1+v,t._2+v)
      },
      (t1:(Int,Int),t2:(Int,Int))=>{
        (t1._1+t2._1,t2._2+t2._2)
      }
    ).collect().foreach(println)
  }

}
