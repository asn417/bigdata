package com.asn.sparkSQL.mysql

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/22 13:32
 * @Description: sparkSQL读取mysql表做join操作
 *              对于DataFrame，可以直接使用API执行SQL操作，也可以转换成表，使用SQL语句执行SQL操作(建议使用这种)
 **/
object JoinDemo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("join-demo").master("local[2]").getOrCreate()
    val studentDF: DataFrame = getDFByTableName("student",spark)
    val leaderDF: DataFrame = getDFByTableName("leader",spark)
/*
    //与SQL的select * from student s join leader l on s.leaderID=l.leaderID; 类似
    //val joinDF: DataFrame = studentDF.join(leaderDF,"leaderID")
    var seq = Seq[String]()
    seq:+"leaderID"
    seq:+"departmentID"
    val joinDF: DataFrame = studentDF.join(leaderDF,seq)
    val filterDS: Dataset[Row] = joinDF.filter("sex is not null")
    joinDF.show()
    filterDS.show()*/

    //将dataframe转换成表
    studentDF.createOrReplaceTempView("student")
    leaderDF.createOrReplaceTempView("leader")
    //用SQL的方式获取数据
    spark.sql("select * from student s join leader l on s.leaderID=l.leaderID and s.departmentID = l.departmentID ; ").show()

    spark.stop()
  }
  def getDFByTableName(table:String,spark:SparkSession):DataFrame={
    val df: DataFrame = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost/big_data?serverTimezone=Asia/Shanghai")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("dbtable", table)
      .option("user", "wangsen")
      .option("password", "root")
      .load()
    df
  }
}
