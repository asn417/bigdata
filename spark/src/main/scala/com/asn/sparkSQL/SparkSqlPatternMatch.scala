package com.asn.sparkSQL

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/23 12:44
 * @Description: 使用模式匹配将RDD转换成dataset
 **/
object SparkSqlPatternMatch {
  def main(args: Array[String]): Unit = {
    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    import sparkSession.implicits._

    val rdd: RDD[(String, String, Int)] = sparkSession.sparkContext.makeRDD(List(("zhangsan","math",90),("lisi","math",70),("wangwu","math",80)))

    val gradeRDD: RDD[StudentGrade] = rdd.map {
      case (name, subject, grade) => {
        StudentGrade(name, subject, grade)
      }
    }
    gradeRDD

    val gradeDS: Dataset[StudentGrade] = gradeRDD.toDS()

    gradeDS.show()

    sparkSession.stop()
  }
}
