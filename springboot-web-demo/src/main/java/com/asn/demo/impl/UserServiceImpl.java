package com.asn.demo.impl;

import com.asn.demo.domin.User;
import com.asn.demo.mapper.UserMapper;
import com.asn.demo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

}