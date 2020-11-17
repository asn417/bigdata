package com.asn.dataframe

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 成绩统计：统计每个学生各个科目三年的平均成绩
 * 分组聚合统计方式有多种：
 *  1)直接使用groupBy+avg
 *  2)使用透视函数pivot：会对透视的列进行行转列
 *  3)使用rollup函数：会对分组的列进行各种情况的组合进行统计，得到的统计结果更为全面
 *  4)使用cube函数：也是组合各种统计维度，比rollup更全面
 */
object GradeStatisticDemo {
  def main(args: Array[String]): Unit = {
    //设置日志级别
    //Logger.getLogger("org").setLevel(Level.ERROR)

    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    val frame: DataFrame = sparkSession.read.json("D:\\ideaProject\\bigdata\\spark\\src\\main\\resources\\data\\grade.json")

    val avgGrade: DataFrame = frame.groupBy("name", "subject").avg("grade")

    avgGrade.show()

    //透视pivot，作用就是行转列
    frame.groupBy("name").pivot("subject").avg("grade").show()

    //rollup
    frame.rollup("name","subject").avg("grade").show()

    //cube
    frame.cube("name","subject").avg("grade").show()
    sparkSession.close()
  }
}
