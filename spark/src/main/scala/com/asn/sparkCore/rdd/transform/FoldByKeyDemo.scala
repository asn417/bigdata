package com.asn.sparkCore.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
  如果分区间和分区内的计算逻辑相同，则可以使用foldbykey。
  下面的例子是使用foldbykey实现类似wordcount
 */
object FoldByKeyDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[(String,Int)] = context.makeRDD(List(("a",1),("a",2),("a",3),("a",4),("b",1),("b",2)), 2)

    value.foldByKey(0)( //初始值
      (x,y)=>x+y            //逻辑
    ).collect().foreach(println)
  }

}
