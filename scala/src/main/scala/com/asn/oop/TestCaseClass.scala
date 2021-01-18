package com.asn.oop

/**
 * @Author: wangsen
 * @Date: 2021/1/18 10:07
 * @Description:
 **/
object TestCaseClass {
  def main(args: Array[String]): Unit = {

    //1、样例类的初始化可以不用new
    val lisi = CaseClassDemo("lisi",21,96)
    val zhangsan = CaseClassDemo.apply("zhangsan",20,95)
    val wangwu1 = new CaseClassDemo("wangwu",20,99)
    val wangwu2 = new CaseClassDemo("wangwu",20,99)
    println(lisi)
    println(zhangsan)
    println(wangwu1)
    println(lisi.hashCode())
    println(zhangsan.hashCode())
    println(wangwu1.hashCode())

    //2、样例类的参数是public的，可以直接访问
    println(lisi.name)

    //3、==按值比较
    println(wangwu1==wangwu2)
    println(wangwu1==zhangsan)

    //4、copy浅拷贝
    val wangwu3: CaseClassDemo = wangwu1.copy(name = "wangwu3",score = 96)
    println(wangwu3)

  }

}
