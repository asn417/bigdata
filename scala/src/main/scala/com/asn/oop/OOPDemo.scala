package com.asn.oop

object OOPDemo {
  def main(args: Array[String]): Unit = {
    val user: User = new User()
    //这就是setter
    user.userName="zhangsan"
    //这就是getter
    println(user.userName)

  }
}

class User{
  //属性的默认修饰符是private，且Scala会自动提供公共的getter，setter方法
  //如果显示的使用private修饰属性，则编译后生成的getter和setter方法也是private的
  var userName:String=_
  var age:Int=_
  def login():Boolean={
    true
  }
  //如果属性用val修饰，那么编译后就是private final修饰的属性，且只提供getter方法
}


