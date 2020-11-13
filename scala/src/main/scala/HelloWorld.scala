/**
 * @Author: wangsen
 * @Date: 2020/11/13 14:10
 * @Description:
 **/
//object 声明的对象为伴生对象，模拟java中的静态对象，可以直接通过伴生对象的名字调用其内部的方法
//主方法必须定义在伴生对象中才能运行
object HelloWorld {
  //scala中定义变量必须显式初始化值，_代表对应类型的默认值
  //一个语句结尾可以不加;，但如果多条语句写在一行的时候，中间必须用;隔开
  var name:String=_
  def main(args: Array[String]): Unit = {
    //变量
    var myVar:String ="asn"
    //常量
    val myVal:String ="aaa"

    //使用object声明的伴生对象类似于java中的静态对象，可以直接调用其内部的方法
    HelloWorld.setName("hello")
    println(HelloWorld.getName())

    //插值字符串
    var s=s"${myVar}---${myVal}"
    println(s)
    println(s"myVar=${myVar},myVal=${myVal}")
    printf("myVar=%s,myVal=%s",myVar,myVal)

  }
  //def用来定义方法，方法形参的格式和java的不同，要先写参数名，然后写参数类型，方法的返回值类型也是在方法名后面指定，然后在大括号内定义方法体，并用等号赋给方法名
  def getName():String={
    this.name
  }

  //没有返回值的方法
  def setName(name:String)={
    this.name=name
  }

}
