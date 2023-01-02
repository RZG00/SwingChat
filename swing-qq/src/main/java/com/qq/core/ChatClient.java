package com.qq.core;

import com.qq.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 客户端的核心功能，尽可能简单，启动线程后，只提供接收消息的接口和发送消息的功能。
 */
public class ChatClient extends Thread{

    private  int PORT = 8888;
    private String address = "127.0.0.1";
    private  Socket socket;

    private OutputStream outputStream;

    private InputStream inputStream;

    protected SocketCallback socketCallback;

    public void setSocketCallback(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
    }

    public void sendMsg(String msg){
        byte[] bytes = msg.getBytes();
        try {
            outputStream.write(bytes,0,bytes.length);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void receiveMsg(){
        try {
            byte[] bytes = new  byte[1024];
            String string = "";
            int count = 0;
            while (((count=inputStream.read(bytes))>0)){
                string += new String(bytes,0,count);
                if(string.contains("</message>")){
                    //读取一条消息结束。
                    socketCallback.receiveMsg(string);
                    System.out.println("客户端收到："+string);
                    string = "";
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void CloseClient(){
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void Connect(){
        try {
            socket = new Socket("127.0.0.1",8888);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            receiveMsg();//接收消息，进行阻塞
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public void run() {
        super.run();
        Connect();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) {
        new ChatClient().Connect();
    }
}
