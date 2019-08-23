package com.example.rpcdemo.publicinterface;

/**
 * @author Jony-Liu
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return msg != null ? msg + " -----> I am fine." : "I am fine.";
    }
}