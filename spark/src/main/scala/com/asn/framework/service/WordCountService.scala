package com.asn.framework.service

import com.asn.framework.common.TService
import com.asn.framework.dao.WordCountDao
import org.apache.spark.rdd.RDD

class WordCountService extends TService{
  private val wordcountDao = new WordCountDao

  def dataAnalysis():Array[(String, Int)]={
    val lines: RDD[String] = wordcountDao.readFile("spark\\src\\main\\resources\\data\\word.txt")

    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String,Int)] = words.map((_,1))

    val wordToSum: RDD[(String,Int)] = wordToOne.reduceByKey(_+_)

    val result: Array[(String, Int)] = wordToSum.collect()

    result
  }
}
