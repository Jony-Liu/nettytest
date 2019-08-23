package com.example.rpcdemo.customer;


import com.example.rpcdemo.publicinterface.HelloService;

/**
 * @author Jony-Liu
 */
public class ClientBootstrap {

    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {
        //创建一个代理对象
        RpcConsumer consumer = new RpcConsumer();
        HelloService service = (HelloService) consumer
                .createProxy(HelloService.class, providerName);
        for (; ; ) {
            Thread.sleep(1000);
            System.out.println(service.hello("are you ok ?"));
        }
    }
}