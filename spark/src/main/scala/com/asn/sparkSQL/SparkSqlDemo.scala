package com.asn.sparkSQL

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
 * sparkSQL是作用在视图上的，视图可以从dataframe或dataset直接创建，也可以直接读取hive元数据操作hive表
 */
object SparkSqlDemo {
  def main(args: Array[String]): Unit = {
    //读取json文件生成DF
    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()
    val dfSG: DataFrame = sparkSession.read.json("D:\\ideaProject\\bigdata\\spark\\src\\main\\resources\\data\\grade.json")
    //转换成dataset
    import sparkSession.implicits._
    val dsSG: Dataset[StudentGrade] = dfSG.map(a => StudentGrade(a.getAs[String]("name"),
      a.getAs[String]("subject"),
      a.getAs[Long]("grade")))

    //创建临时视图
    dfSG.createOrReplaceTempView("sg_df")
    dsSG.createOrReplaceTempView("sg_ds")

    //计算每科平均分
    sparkSession.sql("select subject,avg(grade) from sg_df group by subject").show()
    sparkSession.sql("select subject,avg(grade) from sg_ds group by subject").show()

    sparkSession.close()
  }
}


