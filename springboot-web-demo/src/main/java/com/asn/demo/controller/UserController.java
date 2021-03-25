package com.asn.demo.controller;
 
import com.asn.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user")
    public String user(Model model) {
        model.addAttribute("title", "欢迎来到用户界面");
        model.addAttribute("userList", userService.list());
        return "user";
    }
}