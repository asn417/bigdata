package com.asn.oop

object OOPDemo {
  def main(args: Array[String]): Unit = {
    val user: User = new User()
    user.userName="zhangsan"
    println(user.userName)

    val user1 = new User("lisi")
    println(user1.userName)
  }
}

//Scala构件对象可以通过辅助构造方法创建，但最终必须调用主构造方法
//Scala是完全面向函数的语言，因此类也是函数（代表的是这个类的构造方法），因此可以使用小括号作为参数列表。
//默认情况下类的后面没有小括号，表示使用Scala给类提供的无参构造方法
//在类后面声明的构造方法就是主构造方法，在主构造方法中声明的构造方法就是辅助构造方法
//辅助构造方法间调用时，需要保证被调用的先声明
class User extends Person {//默认的主构造方法
  var userName:String=_
  var age:Int=_

  //初始化父类的抽象属性
  override var gender: String = "男"

  def login():Boolean={
    true
  }

  //辅助构造方法
  def this(userName:String){
  this()//辅助构造方法中必须调用主构造方法
  this.userName = userName;
}

  override def test(): Unit = {
    println("实现父类的抽象方法")
  }

  override def test1(): Unit = {
    println("子类重新父类的test1方法")
  }

}

//抽象类
abstract class Person{

  //抽象属性：Scala中属性可以是抽象的（只声明而不初始化则为抽象属性），因此可以被重写。抽象属性在编译为class文件时，不会产生属性，只会生成抽象的getter方法
  //如果不是抽象属性，则会被子类继承，子类也可以override重写继承的非抽象属性，但必须是val修饰的
  var gender:String

  //抽象方法（没有具体实现就表示抽象方法）
  def test()

  def test1(): Unit ={
    println("父类的test1方法")
  }
}

