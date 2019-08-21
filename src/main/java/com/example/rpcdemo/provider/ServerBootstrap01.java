package com.example.rpcdemo.provider;

public class ServerBootstrap01 {
    public static void main(String[] args) {
        NettyServer.startServer("localhost", 8088);
    }
}