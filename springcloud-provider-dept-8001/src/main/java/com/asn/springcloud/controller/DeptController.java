package com.asn.springcloud.controller;

import com.asn.pojo.Dept;
import com.asn.springcloud.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    @PostMapping(value = "/dept/add")
    public boolean addDept(Dept dept){
        return deptService.addDept(dept);
    }

    @GetMapping(value = "/dept/get/{id}")
    public Dept addDept(@PathVariable("id") Long id){
        return deptService.queryById(id);
    }

    @GetMapping(value = "/dept/queryAll")
    public List<Dept> queryAll(){
        return deptService.queryAll();
    }
}
