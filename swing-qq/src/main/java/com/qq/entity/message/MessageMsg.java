package com.qq.entity.message;

import java.awt.*;
import java.util.Date;

public class MessageMsg extends Message{

    private String sendName="";
    //发送人id
    private String sendId="";
    //消息时间
    private Date date;
    //消息字体
    private Font font = new Font("微软雅黑", Font.PLAIN, 12);
    //消息颜色
    private Color color = Color.BLUE;

    private String style ="";
    //消息主体
    private String body = "";

    private String shield ="";

    public MessageMsg(){
        super(MessageType.msg);
    }

    public MessageMsg(MessageType type) {
        super(type);
        this.date = new Date();
    }


    public String getShield() {
        return shield;
    }

    public void setShield(String shield) {
        this.shield = shield;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MessageMsg{" +
                "sendName='" + sendName + '\'' +
                ", sendId='" + sendId + '\'' +
                ", date=" + date +
                ", font=" + font +
                ", color=" + color +
                ", style='" + style + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
