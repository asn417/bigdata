package com.asn.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestThymeleafController {

    @RequestMapping("/hello1")
    public String hello(Model model){
        model.addAttribute("title", "欢迎来到用户界面");
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
        return "hello2";
    }

}
