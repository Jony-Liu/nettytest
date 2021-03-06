package com.example.rpcdemo.provider;

import com.example.rpcdemo.customer.ClientBootstrap;
import com.example.rpcdemo.publicinterface.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Jony-Liu
 */
public class HelloServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 如何符合约定，则调用本地方法，返回数据
     * @param ctx ChannelHandlerContext
     * @param msg Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg.toString().startsWith(ClientBootstrap.providerName)) {
            String result = new HelloServiceImpl()
                    .hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }
}