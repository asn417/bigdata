package com.asn.functional

/**
 * @Author: wangsen
 * @Date: 2020/11/13 18:22
 * @Description: 函数式编程
 *              1、函数内部还可以定义函数
 *              2、函数没有重载的概念，因此在同一个作用域中，函数不能同名(这好像是错的，可以定义同名的)
 *              3、Scala没有throw关键字，函数声明时不需要抛异常
 *              4、函数可以赋值给变量
 *              5、函数可以作为函数的参数：(参数列表)=>返回值类型
 *              6、函数可以作为函数的返回值
 **/
object FunctionProgram {

  //Scala是完全面向函数的编程语言
  //在Scala中函数可以做任何事：函数可以赋值给变量、函数可以作为参数传给一个函数、函数可以作为函数的返回值


  def main(args: Array[String]): Unit = {
    //调用函数返回的函数，直接调用即可，不需要像Java那样必须通过对象或类调用
    f1()()

    //函数作为参数传给函数
    println(f3(f4))

    //将一个匿名函数传给f3
    println(f3(()=>{6}))

    //
    println(f4(5)(6))
  }

  //将函数作为参数，f表示形参，()表示参数类型是函数类型，Int表示传入的函数的返回值类型为Int
  def f3(f:()=>Int): Int ={
    def f5(){}//函数内部还可以定义函数
    f()+2
  }
  //如果函数体中只有一行代码，那么大括号可以省略
  def f4(): Int ={
    5
  }

  //返回函数f2
  def f1()={
    f2 _ //要返回函数，需要加上空格_，否则就是调用函数了
  }
  //如果没有形参，则小括号可以省略：def f2= "xxxx"
  def f2(): Unit ={
    println("xxxxxx")
    return "xxxxxxxxxx"//函数返回值定义的是Unit，那么函数体中即使有return也不起作用。此外，如果函数体的最后一行是要返回的内容，则可以省略return
  }


  def f4(i:Int)={
    def f6(j:Int):Int={
      i*j
    }

    f6 _
  }

  /*
    函数科里化
    闭包：函数科里化比存在闭包，闭包就是内部函数用到了外层函数的变量，因此改变了外层变量的生命周期，这就叫闭包
   */
  def f5(i:Int)(j:Int):Int={
    i*j
  }

}
