package com.example.niotest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Clinet {
    private static final int PORT = 30000;
    /**
     * 设置服务器address对象
     */
    private static final InetSocketAddress ADDRESS = new InetSocketAddress("127.0.0.1",PORT);
    /**
     * 设置读/取缓冲区的字节大小
     */
    private static final int SIZE = 1024;
    /**
     * 获取数据的缓冲区对象
     */
    private static ByteBuffer receiveByteBuffer = ByteBuffer.allocate(SIZE);
    /**
     * 写入数据的缓冲区对象
     */
    private static ByteBuffer sendByteBuffer = ByteBuffer.allocate(SIZE);
    /**
     * 事件选择器
     */
    private Selector selector;

    /**
     * 键盘输入缓存区对象
     */
    private BufferedReader reader;

    public Clinet(BufferedReader reader) throws IOException {
        //创建客户端管道对象
        SocketChannel client = SocketChannel.open();
        //使用非阻塞
        client.configureBlocking(false);
        //连接
        client.connect(ADDRESS);
        //创建事件选择器
        selector = Selector.open();
        //注册连接事件
        client.register(selector, SelectionKey.OP_CONNECT);
        this.reader = reader;
    }

    public void serice() throws IOException {

        while (true) {
            selector.select(1000);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //删除已处理事件，防止重复处理
                iterator.remove();
                handler(key);
            }
            //清空事件
            selectionKeys.clear();
        }
    }

    private void handler(SelectionKey key) throws IOException {
        //连接事件判断
        if (key.isConnectable()) {
            connect(key);
        }
        //读取事件判断
        else if (key.isReadable()) {
            read(key);
        }
        //写入事件判断
        else if (key.isWritable()) {
            write(key);
        }
    }

    private void write(SelectionKey key) throws IOException {
        //客户端管道
        SocketChannel client = (SocketChannel) key.channel();
        //清空缓冲区
        sendByteBuffer.clear();
        //将数据填充到缓冲区
        sendByteBuffer.put(("客户端输入数据为："+this.reader.readLine()).getBytes("UTF-8"));
        //这里一定要flip一下，这个是复位缓冲区的意思
        sendByteBuffer.flip();
        //发送数据
        client.write(sendByteBuffer);
        //注册读取事件
        client.register(selector,SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        //客户端管道
        SocketChannel client = (SocketChannel) key.channel();
        //清空缓冲区
        receiveByteBuffer.clear();
        //读取服务端传数据写进缓冲区
        int len = client.read(receiveByteBuffer);
        //每次都要调用一个flip方法
        receiveByteBuffer.flip();
        //打印获取服务端的数据
        System.out.println(new String(receiveByteBuffer.array(),0,len));
        //注册写入事件
        client.register(selector,SelectionKey.OP_WRITE);
    }

    private void connect(SelectionKey key) throws IOException {
        //客户端管道
        SocketChannel client = (SocketChannel) key.channel();
        if (client.isConnectionPending()) {
            //完成与服务端连接
            client.finishConnect();
            //清空缓冲区
            sendByteBuffer.clear();
            //将数据填充到缓冲区
            sendByteBuffer.put("我是客户端来了".getBytes("UTF-8"));
            //这里一定要flip一下，这个是复位缓冲区的意思
            sendByteBuffer.flip();
            //发送数据
            client.write(sendByteBuffer);
        }
        //注册读取事件
        client.register(selector,SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        //实例化一个键盘输入流缓冲区
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Clinet clinet = new Clinet(reader);
        clinet.serice();
    }
}
