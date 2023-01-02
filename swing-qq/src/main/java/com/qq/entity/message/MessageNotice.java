package com.qq.entity.message;

import com.qq.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MessageNotice extends Message{


    private String clientId = "";
    private String clientName = "";
    private List<User> online = new ArrayList<>();


    public MessageNotice() {
        super(MessageType.notice);

    }

    public MessageNotice(MessageType type) {
        super(type);

    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<User> getOnline() {
        return online;
    }

    public void setOnline(List<User> online) {
        this.online = online;
    }
}
