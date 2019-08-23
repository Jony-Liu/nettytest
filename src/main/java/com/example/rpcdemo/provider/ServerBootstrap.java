package com.example.rpcdemo.provider;

/**
 * @author Jony-Liu
 */
public class ServerBootstrap {

    public static void main(String[] args) {
        NettyServer.startServer("localhost", 8088);
    }

}