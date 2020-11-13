/**
 * @Author: wangsen
 * @Date: 2020/11/13 18:22
 * @Description:
 **/
//函数式编程
object FunctionProgram {

  //Scala是完全面向函数的编程语言
  //在Scala中函数可以做任何事：函数可以赋值给变量、函数可以作为参数传给一个函数、函数可以作为函数的返回值


  def main(args: Array[String]): Unit = {
    //调用函数返回的函数
    f1()()

    //函数作为参数传给函数
    println(f3(f4))

    //将一个匿名函数传给f3
    println(f3(()=>{6}))
  }

  //将函数作为参数，f表示形参，()表示参数类型是函数类型，Int表示传入的函数的返回值类型为Int
  def f3(f:()=>Int): Int ={
    f()+2
  }
  def f4(): Int ={
    5
  }

  //返回函数f2
  def f1()={
    f2 _
  }
  def f2(): Unit ={
    println("xxxxxx")
  }
}
