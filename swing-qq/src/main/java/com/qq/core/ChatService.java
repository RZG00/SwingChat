package com.qq.core;

import com.qq.entity.User;
import com.qq.tools.StringUtil;
import com.qq.tools.UUIDUtil;
import com.qq.tools.XMLUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 服务端服务，只负责转发客户端的消息，和获取客户端的状态
 * 在建立新连接时和接收到消息时在当前类中都有对应的处理函数
 * 在继承类中，可以重写对应的方法来
 */
public class ChatService extends Thread{

    private  int PORT = 8888;
    private static ServerSocket serverSocket;
    private List<User> userList = new ArrayList<>();
    //禁言的用户列表
    private List<User> prohibitUserList = new ArrayList<>();
    protected void setProhibitUserName(){

        for (User user : userList) {
            User prohibitUser = getProhibitUser(user.getId());
            String name = user.getName();
            if(prohibitUser==null){
                //没有被禁言的用户，名字要去掉（禁言）帽子
                user.setName(name.replace("(禁言)",""));
            }else{
                //被禁言的用户，名字要加上(禁言)帽子
                if(!name.contains("(禁言)")){
                    user.setName(name+"(禁言)");
                }

            }
        }

    }


    public List<User> getProhibitUserList() {
        return prohibitUserList;
    }

    public User getProhibitUser(String id){
        if(!StringUtil.isNotEmpty(id))return null;
        for (User user : prohibitUserList) {
            if(user.getId().equals(id))
                return user;
        }
        return null;
    }

    public User delProhibitUser(String id){
        if(!StringUtil.isNotEmpty(id))return null;
        for (User user : prohibitUserList) {
            if(user.getId().equals(id))
            {
                prohibitUserList.remove(user);
                User userById = getUserById(id);
                userById.setName(userById.getName().replace("(禁言)",""));
                return user;
            }
        }
        return null;
    }

    /**
     * 添加要禁言的用户
     * @param id
     * @return
     */
    public List<User> addProhibitUser(String id){
        User userById = getUserById(id);
        if(userById!=null){
            prohibitUserList.add(userById);
            setProhibitUserName();
            System.out.println("添加禁言："+userById.getId());
        }
        return prohibitUserList;
    }

    private void Lisenter(){
        try {
            serverSocket = new ServerSocket(PORT);
            while (true){
                System.out.println("等待连接..");
                Socket socket = serverSocket.accept();

                receiveMsg(socket);//进入读消息状态 异步
                handleConnect(socket);//处理连接
                System.out.println("客户端["+socket.getInetAddress()+"]连接成功");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void CloseService(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 处理新连接,有新的连接时，保存User用户
     * @param socket
     * @return  新连接的User
     */
    protected User handleConnect(Socket socket) {
        User user = new User();
        try {
            user.setSocket(socket);
            user.setInputStream(socket.getInputStream());
            user.setOutputStream(socket.getOutputStream());
            user.setId(UUIDUtil.newUUID());

            userList.add(user);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user;
    }
    /**
     * 单独给一个用户发消息
     */
    public void sendMsgSingle(User user,String msg){
        try {
            byte[] bytes = msg.getBytes();
            OutputStream outputStream = user.getOutputStream();
            outputStream.write(bytes,0,bytes.length);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    //发送给所有人员消息
    public void sendMsg(String msg){

        for (User user : userList) {
            sendMsgSingle(user,msg);
        }
    }
    //接收消息
    private void receiveMsg(Socket socket){

        new Thread(()->{
                try {
                    byte[] bytes = new  byte[1024];
                    String msg = "";
                    int count = 0;
                    while (((count=socket.getInputStream().read(bytes))>0)){

                        msg += new String(bytes,0,count);
                        if(msg.contains("</message>")){
                            //读取一条消息结束。
                            handleMsg(msg);
                            msg = "";
                        }
                        //todo 需要分清消息是否完整，消息太长需要多次读取。
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);

                } finally {
                    //todo 去掉userLsit中用户信息
                    for(User user : userList) {
                        if(user.getSocket()==socket){
                            userList.remove(user);
                        }
                    }
                }

        }).start();

    }

    //处理收到的通知
    protected void handleMsg(String string) {
        String type = XMLUtil.getContent(string, "type");
        switch (type){
            case "msg":

                break;

        }
    }

    public User getUserById(String id){
        if(StringUtil.isNotEmpty(id))
        {
            for (User user : userList) {
                if(id.equals(user.getId())){
                    return user;
                }
            }
        }
        return null;

    }

    @Override
    public void run() {
        super.run();
        Lisenter();
    }

    public void setPort(int PORT) {
        this.PORT = PORT;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public static void main(String[] args) {
        new ChatService().Lisenter();
    }
}
