package com.asn.mysql

import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author: wangsen
 * @Date: 2020/11/14 14:40
 * @Description: 可以通过SQL语句获取指定表的指定字段，返回的是一个jdbcRDD
 *              lowerBound：结果集的上界
 *              upperBound：结果集的下界（这两个参数就相当于是limit的作用）
 **/
object MysqlConnect2 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordcount")

    val session: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc: SparkContext = session.sparkContext
    //建议使用sparksession的方式创建sparkcontext，方便将rdd转df
    //val sc = new SparkContext(sparkConf)

    val driver="com.mysql.cj.jdbc.Driver"
    val url="jdbc:mysql://localhost/big_data?serverTimezone=Asia/Shanghai"
    val username="wangsen"
    val password="root"

    val sql="select * from student where studentID >= ? and studentID <= ?"
    val jdbcRDD = new JdbcRDD[Student](sc, () => {
      Class.forName(driver)
      java.sql.DriverManager.getConnection(url, username, password)
    }, sql, 2, 5, 2,
      rs => {
        val id: Int = rs.getInt(1)
        val name: String = rs.getString(2)
        val age: Int = rs.getInt(3)
        Student(id,name,age)
      })
    jdbcRDD
    val result: Array[Student] = jdbcRDD.collect()
    println(result.toBuffer)

    //将RDD转成有结构的DF
    import session.implicits._
    val frame: DataFrame = jdbcRDD.toDF()
    frame.show()

    sc.stop()
  }
}
case class Student(id:Int,name:String,age:Int)
