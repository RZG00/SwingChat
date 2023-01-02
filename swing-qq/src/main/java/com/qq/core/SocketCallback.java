package com.qq.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface SocketCallback {
   // public void success(Socket socket);

    public void receiveMsg(String msg);
}
