package com.qq.entity.message;

public class Message {

    //消息类型
    private MessageType type;
    //发送人昵称


    public Message() {
    }

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                '}';
    }
}
