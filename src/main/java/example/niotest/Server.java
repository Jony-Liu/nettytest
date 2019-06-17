package example.niotest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    /**
     * 服务器端口
     */
    private static final int PORT = 30000;
    /**
     * 设置服务器address对象
     */
    private static final InetSocketAddress ADDRESS = new InetSocketAddress(PORT);
    /**
     * 设置读/取缓冲区的字节大小
     */
    private static final int SIZE = 1024;
    /**
     * 定义一个发送数据的缓冲区对象
     */
    private static ByteBuffer sendBuffer = ByteBuffer.allocate(SIZE);
    /**
     * 定义一个接收数据的缓冲区对象
     */
    private static ByteBuffer receiveBuffer = ByteBuffer.allocate(SIZE);
    /**
     * 定义一个事件选择器对象
     */
    private static Selector selector;

    public Server() throws IOException {
        /**
         * 定义一个socket服务端管道对象
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置成非阻塞
        serverSocketChannel.configureBlocking(false);
        //绑定端口
        serverSocketChannel.bind(ADDRESS);
        //创建一个事件选择器对象
        selector = Selector.open();
        //注册请求事件（个人理解是将请求事件与管道绑定关系的意思）)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器已经开启...");
    }

    public void listen() throws IOException {
        /**
         * 因为服务器一般都是接收所有的客户端请求的所以这里使用一个死循环
         */
        while (true) {
            //查询是否有事件，如果没有将停顿一秒后在执行后续代码
            selector.select(1000);
            //获取所有事件键值
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            //开始循环事件
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //删除已处理的事件，防止重复处理
                iterator.remove();
                handler(key);
            }
            //清空注册事件
            selectionKeys.clear();
        }
    }

    /**
     * 业务处理
     * @param key
     */
    private void handler(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            //获取请求事件
            accept(key);
        } else if (key.isReadable()) {
            //获取读取事件
            read(key);
        } else if (key.isWritable()) {
            write(key);
        }
    }

    /**
     * 写入事件
     * @param key
     */
    private void write(SelectionKey key) throws IOException {
        //获取客户端
        SocketChannel client = (SocketChannel) key.channel();
        //清空读的缓冲区
        sendBuffer.clear();
        //将数据写入缓冲区
        sendBuffer.put("我是服务器".getBytes("UTF-8"));
        //将缓冲区复位
        sendBuffer.flip();
        //发送数据到客户端
        client.write(sendBuffer);
        //注册读事件
        client.register(selector,SelectionKey.OP_READ);
    }

    /**
     * 读取事件
     * @param key
     * @throws IOException
     */
    private void read(SelectionKey key) throws IOException {
        //获取客户端
        SocketChannel client = (SocketChannel) key.channel();
        //清空读的缓冲区
        receiveBuffer.clear();
        //读取客户端传过来的书籍
        int len = client.read(receiveBuffer);
        //复位缓冲区
        receiveBuffer.flip();
        //解析数据
        System.out.println("服务端接收客户端传过来的数据:"+new String(receiveBuffer.array(),0,len));
        //注册写入事件
        client.register(selector,SelectionKey.OP_WRITE);
    }

    private void accept(SelectionKey key) throws IOException {
        //获取服务器管道
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        //获取客户端请求
        SocketChannel client = server.accept();
        //将客户端对象设置为非阻塞模式
        client.configureBlocking(false);
        //注册读的事件
        client.register(selector,SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.listen();
    }
}
