package com.asn.sparkCore.project1

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
/*
  电商网站用户行为：搜索、点击、下单、支付。
  1、如果搜索关键字为空，则表示不是搜索行为
  2、如果点击的品类id和商品id为-1，则表示不是点击行为
  3、对于下单行为，一次可以下单多个商品，所以品类id和产品id可以是多个，id之间用逗号分隔，如果不是下单行为，则数据采用null表示
  4、支付行为与下单行为类似
  数据格式：
  日期       用户id       sessionid                     页面id     动作时间       搜索关键字  品类id  商品id  品类ids    商品ids   支付品类ids  支付商品ids  城市id
  2019-07-17	95	26070e87-1ad7-49a3-8fb3-cc741facaddf	37	2019-07-17 00:00:02	手机	    -1	    -1	     \N	      \N	        \N	   \N	         3

  需求：以商品品类为key，根据品类对应的点击次数、下单次数和支付次数依次排序，取top10的品类
 */
object ClickCount {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)

    val sc: SparkContext = SparkContext.getOrCreate(sparkConf)
    //val sc = new SparkContext(sparkConf)

    val actionRDD: RDD[String] = sc.textFile("spark\\src\\main\\resources\\data\\user_visit_action.txt")

    //点击行为
    val clickActionRDD: RDD[String] = actionRDD.filter(
      line => {
        val arr: Array[String] = line.split("\t")
        arr(6) != "-1"
      }
    )
    //每个品类的点击次数
    val clickCountRDD: RDD[(String, Int)] = clickActionRDD.map(
      line => {
        val arr: Array[String] = line.split("\t")
        (arr(6), 1)
      }
    ).reduceByKey(_ + _)

    //下单行为
    val orderActionRDD: RDD[String] = actionRDD.filter(
      line => {
        val arr: Array[String] = line.split("\t")
        arr(8) != "null"
      }
    )
    //每个品类的下单次数
    val orderCountRDD: RDD[(String, Int)] = orderActionRDD.flatMap(
      line => {
        val arr: Array[String] = line.split("\t")
        val cid = arr(8)
        val cids = cid.split(",")
        cids.map(id => (id, 1))//这不是map算子，而是Scala里的map方法
      }
    ).reduceByKey(_ + _)


    //支付行为
    val payActionRDD: RDD[String] = actionRDD.filter(
      line => {
        val arr: Array[String] = line.split("\t")
        arr(10) != "null"
      }
    )
    //每个品类的支付次数
    val payCountRDD: RDD[(String, Int)] = orderActionRDD.flatMap(
      line => {
        val arr: Array[String] = line.split("\t")
        val cid = arr(10)
        val cids = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)

    //cogroup的作用
    val cogroupRDD: RDD[(String, (Iterable[Int], Iterable[Int], Iterable[Int]))] = clickCountRDD.cogroup(orderCountRDD, payCountRDD)
    //mapValues的作用
    val analysisRDD: RDD[(String, (Int, Int, Int))] = cogroupRDD.mapValues {
      case (clickIter, orderIter, payIter) => {
        var clickCnt = 0
        val it1: Iterator[Int] = clickIter.iterator
        if (it1.hasNext) {
          clickCnt = it1.next()
        }
        var orderCnt = 0
        val it2: Iterator[Int] = orderIter.iterator
        if (it2.hasNext) {
          orderCnt = it2.next()
        }
        var payCnt = 0
        val it3: Iterator[Int] = payIter.iterator
        if (it3.hasNext) {
          payCnt = it3.next()
        }

        (clickCnt, orderCnt, payCnt)
      }
    }

    val result: Array[(String, (Int, Int, Int))] = analysisRDD.sortBy(_._2, false).take(10)

    result.foreach(println)

    sc.stop()
  }
}
