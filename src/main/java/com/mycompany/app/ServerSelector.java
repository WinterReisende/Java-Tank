package com.mycompany.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.Iterator;

//import javax.swing.text.html.HTMLDocument.Iterator;

public class ServerSelector implements Runnable {

    private ServerView view;
    private ServerModel model;

    private SelectionKey[] keys;
    private int[] players;

    private Selector selector;// 创建一个Selector，用于处理所有的网络事件
    private InetSocketAddress listenAddress;
    private String address;
    private final static int PORT = 9093;// 服务器侦听的端口号

    public ServerSelector(ServerView thisview) {
        view = thisview;
        address = "localhost";
        keys = new SelectionKey[5];
        players = new int[5];
        for (int i = 0; i < 5; i++) {
            players[i] = -1;
        }

        listenAddress = new InetSocketAddress(address, PORT);
    }

    public void setModel(ServerModel thismodel) {
        model = thismodel;
    }

    public synchronized int setClient(SelectionKey key) {
        int id = model.addClient();
        if (id == -1) {
            return 0;
        }
        players[id] = id;
        keys[id] = key;
        return 1;
    }

    public synchronized void deleteClient(SelectionKey key) {
        int id = -1;
        for (int i = 0; i < 5; i++) {
            if (keys[i] == key) {
                id = i;
                break;
            }
        }
        if (players[id] != -1) {
            players[id] = -1;
            keys[id] = null;
            model.deleteClient(id);
        }
    }

    public synchronized void setClientInstruction(SelectionKey key, String s) {
        int id = -1;
        for (int i = 0; i < 5; i++) {
            if (keys[i] == key) {
                id = i;
                break;
            }
        }
        if (players[id] != -1) {
            model.setClientInstruction(id, s);
            model.setClientInstructionPrepared(id, true);
        }
    }

    // startServer 方法，启动服务器的核心方法
    public void run() {
        try {
            this.selector = Selector.open();// 打开选择器
            ServerSocketChannel serverChannel = ServerSocketChannel.open();// 打开ServerSocketChannel以侦听客户端连接
            serverChannel.configureBlocking(false);// 配置为非阻塞模式

            // 绑定服务器通道到指定端口
            serverChannel.socket().bind(listenAddress);
            serverChannel.register(this.selector, SelectionKey.OP_ACCEPT); // 向选择器注册ServerSocketChannel，并监听接受连接事件
            System.out.println("Server started on port >> " + PORT);
            while (!Thread.currentThread().isInterrupted()) { // 循环处理就绪的事件
                // 向全部client发送信息
                if (model.getInstructionPrepared()) {
                    String s = model.getInstruction();
                    for (int i = 0; i < 5; i++) {
                        if (players[i] != -1 && keys[i] != null && keys[i].isValid()) {
                            write(keys[i], s);
                        }
                    }
                    model.setInstructionPrepared(false);
                }

                // wait for events
                int readyCount = selector.select(12);
                if (readyCount == 0) {
                    continue;// 如果没有事件发生，继续循环
                }
                // 获取已经就绪的键集合
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    // 从集合中移除当前的键，防止重复处理
                    iterator.remove();

                    if (!key.isValid()) {// 检查键是否有效
                        continue;
                    }

                    if (key.isAcceptable()) { // Accept client connections // 检查是否有新的连接
                        this.accept(key);
                    } else if (key.isReadable()) { // Read from client // 检查通道是否有数据可读
                        this.read(key);
                    } else if (key.isWritable()) {
                        ;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            view.mainPanel.setgameStart(false);
            model.setGameStarted(false);
        }
    }

    // accept 方法，处理接受新连接的事件
    private synchronized void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();// 接受连接并创建SocketChannel
        channel.configureBlocking(false); // 设置非阻塞模式
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);

        // 将新的SocketChannel注册到选择器，准备读取数据
        SelectionKey clientKey = channel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        setClient(clientKey);// 将新的SocketChannel注册到selector和model中

    }

    //
    // 从SocketChannel读取数据
    private synchronized void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);// 分配缓冲区
        int numRead = -1;
        numRead = channel.read(buffer);// 将数据读入缓冲区

        if (numRead == -1) {// 检查是否到达流的末尾（客户端关闭连接）
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by client: " + remoteAddr);
            deleteClient(key);
            channel.close();// 关闭通道
            key.cancel();// 取消选择键
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);// 将数据从缓冲区复制到字节数组
        System.out.println("Got: " + new String(data));// 打印接收到的数据
        String clientInstruction = new String(data);
        setClientInstruction(key, clientInstruction);// 将接收到的数据存到model的clientInstruction中
    }

    private void write(SelectionKey key, String message) throws IOException {
        System.out.println("Sending: " + new String(message));// 打印接收到的数据
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes()); //
        while (buffer.hasRemaining()) { //
            channel.write(buffer); //
        }
    }
}
