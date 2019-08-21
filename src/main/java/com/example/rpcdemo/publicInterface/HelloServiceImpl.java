package com.example.rpcdemo.publicInterface;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return msg != null ? msg + " -----> I am fine." : "I am fine.";
    }
}