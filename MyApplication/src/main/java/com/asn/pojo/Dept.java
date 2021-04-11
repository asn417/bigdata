package com.asn.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分布式应用的实体类，必须是可序列化的
 */
@Data //为所有非final属性生成getter、setter方法
@NoArgsConstructor //无参构造器
@Accessors(chain = true) //链式写法
public class Dept implements Serializable {
    //部门id
    private Long deptno;
    //部门名称
    private String dname;
    //表示这条数据存在哪个数据库
    private String db_source;

    public Dept(String dname) {
        this.dname = dname;
    }
}
