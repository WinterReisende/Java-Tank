package com.mycompany.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientSocket implements Runnable {
    private ClientView view;
    private ClientModel model;
    private boolean connecting;
    private boolean connected;
    private SocketChannel client;
    private String readString;

    public ClientSocket(ClientView thisview) {
        view = thisview;
        connecting = false;
        connected = false;
    }

    public void setModel(ClientModel thismodel) {
        model = thismodel;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println("Thread was interrupted: " + e.getMessage());
                view.mainPanel.setgameStart(false);
                Thread.currentThread().interrupt();
                break;
            }
            if (connecting && !connected) {
                try {
                    InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9093);
                    this.client = SocketChannel.open();
                    this.client.connect(hostAddress);

                    // 等待连接完成
                    while (!this.client.finishConnect()) {
                        Thread.sleep(3);
                    }
                    System.out.println("Connected successfully.");
                    this.setConnected(true);
                    this.setConnecting(false);
                    model.setServerConnected(true);
                    Thread.sleep(5);
                } catch (IOException e) {
                    System.out.println("Failed to connect to server: " + e.getMessage());
                    e.printStackTrace();
                    if (this.client != null) {
                        try {
                            this.client.close();
                        } catch (IOException e2) {
                            System.out.println("Error closing socket channel: " + e2.getMessage());
                        }
                    }
                } catch (InterruptedException e) {
                    // 恢复中断状态
                    Thread.currentThread().interrupt();
                    break;
                }
            } else if (connected) {
                if (model.getInstructionPrepared()) {// 已经生成客户端指令，要发送给服务器
                    String clientInstruction = model.getInstruction();
                    System.out.println("Send to server: " + clientInstruction);
                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(clientInstruction);
                    try {
                        while (buffer.hasRemaining()) {
                            client.write(buffer);
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to connect to server: " + e.getMessage());
                        e.printStackTrace();
                        if (this.client != null) {
                            try {
                                this.client.close(); // 关闭 SocketChannel
                            } catch (IOException e2) {
                                System.out.println("Error closing socket channel: " + e2.getMessage());
                            }
                        }
                    }
                    // 将指令标记为已处理，避免重复发送
                    model.setInstructionPrepared(false);
                }

                ByteBuffer buffer = ByteBuffer.allocate(4096);
                int bytesRead = 0;
                try {
                    bytesRead = client.read(buffer);
                } catch (IOException e) {
                    System.out.println("Failed to connect to server: " + e.getMessage());
                    e.printStackTrace();
                    if (this.client != null) {
                        try {
                            this.client.close(); // 关闭 SocketChannel
                        } catch (IOException e2) {
                            System.out.println("Error closing socket channel: " + e2.getMessage());
                        }
                    }
                } // 从channel中读取数据
                if (bytesRead > 0) {
                    buffer.flip(); // 切换buffer从写模式到读模式
                    readString = StandardCharsets.UTF_8.decode(buffer).toString(); // 将数据转换为字符串
                    System.out.println("Got from server: " + readString);
                    buffer.clear(); // 清空buffer，为下一次读取做准备
                    if (model.getServerInstructionPrepared()) {// 如果服务器指令已经准备好，就不要再读取了
                        model.setServerInstruction(model.getServerInstruction() + readString);
                    } else {
                        model.setServerInstruction(readString);
                        model.setServerInstructionPrepared(true);
                    }
                }
            }
        }
    }

    public synchronized void setConnected(boolean c) {
        this.connected = c;
    }

    public synchronized void setConnecting(boolean c) {
        this.connecting = c;
    }

    public void closeConnection() {
        try {
            if (client != null && client.isOpen()) {
                client.close(); // 关闭 SocketChannel
            }
        } catch (IOException e) {
            System.out.println("Error closing the connection: " + e.getMessage());
        }
    }
}
