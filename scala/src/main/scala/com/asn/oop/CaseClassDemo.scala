package com.asn.oop

/**
 * @Author: wangsen
 * @Date: 2021/1/18 9:59
 * @Description: case class（样例类）
 *  1、样例类初始化时可以不适用new，因为样例类默认会使用apply方法创建对象
 *  2、默认实现了equals和hashCode方法
 *  3、默认实现了Serializable接口
 *  4、构造函数的参数默认是public val的，可以直接访问，且不可修改
 *  5、支持模式匹配，这也是定义case class的主要原因
 *  6、样例类的==是按值比较的，而不是按引用比较
 *  7、可以通过copy实现浅拷贝，且可以修改构造参数的值来做些改变
 **/
case class CaseClassDemo(name:String,age:Int,score:Float)


