package com.qq.core;

import com.qq.entity.User;
import com.qq.entity.message.Message;
import com.qq.entity.message.MessageNotice;
import com.qq.entity.message.MessageType;
import com.qq.tools.MessageUtil;
import com.qq.tools.StringUtil;
import com.qq.tools.XMLUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceExtention extends ChatService{

    private boolean isStart = false;//心跳函数是否启动；
    /**
     * 接收到新消息
     * @param string
     */
    @Override
    protected void handleMsg(String string) {
        super.handleMsg(string);
        String type = XMLUtil.getContent(string, "type");
        System.out.println("服务端收到："+string);
        switch (type){
            case "notice":
                String clientName = XMLUtil.getContent(string, "clientName");
                String clientId = XMLUtil.getContent(string, "clientId");

                //服务端发过来id name 就给更新user列表中的昵称
                if(StringUtil.isNotEmpty(clientName)&&StringUtil.isNotEmpty(clientId)){
                    User user = getUserById(clientId);
                    user.setName(clientName);
                   //如果当前用户被禁言了，就要扣上（禁言帽子）
                    setProhibitUserName();
                    //更新了用户的信息，就通知他当前的在线成员状态
                    MessageNotice messageNotice = new MessageNotice();
                    messageNotice.setOnline(getUserList());
                    String messageNoticeXml = MessageUtil.messageNoticeToXml(messageNotice);
                    sendMsgSingle(user,messageNoticeXml);
                }

                if(StringUtil.isNotEmpty(clientId)){

                }else{

                }

                break;
            case "msg":
                String sendId = XMLUtil.getContent(string, "sendId");
                if(getProhibitUser(sendId)!=null){
                    //消息发送者id在禁言名单中，不给转发


                }else{
                    sendMsg(string);//收到消息就将消息转发给其他客户端
                }

                break;
        }
    }

    /**
     * 有新的连接
     * @param socket
     * @return
     */
    @Override
    protected User handleConnect(Socket socket) {
        User user = super.handleConnect(socket);
        //给新用户分配ID  通知其上传自己昵称
        MessageNotice messageNotice = new MessageNotice();
        messageNotice.setClientId(user.getId());

        String messageNoticeXml = MessageUtil.messageNoticeToXml(messageNotice);
        sendMsgSingle(user,messageNoticeXml);

        sendHeart();//启动心跳函数
        return user;
    }

    /**
     *发送心跳消息，包含在线人数
     */
    private void sendHeart(){
        if(isStart)return;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (getUserList().size()>0){
                    MessageNotice messageNotice = new MessageNotice();
                    messageNotice.setOnline(getUserList());
                    String messageNoticeXml = MessageUtil.messageNoticeToXml(messageNotice);
                    sendMsg(messageNoticeXml);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }).start();
        isStart = true;

    }



}
