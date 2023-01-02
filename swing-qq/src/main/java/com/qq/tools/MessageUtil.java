package com.qq.tools;

import com.qq.entity.*;
import com.qq.entity.message.Message;
import com.qq.entity.message.MessageMsg;
import com.qq.entity.message.MessageNotice;
import com.qq.entity.message.MessageType;
import sun.security.util.Debug;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageUtil {


    public static MessageMsg parseMessageMsg(String xml){

        String type = XMLUtil.getContent(xml, "type");
        if(!"msg".equals(type)){
            System.out.println("消息字符串解析失败，类型不为msg");
        }
        String sendName = XMLUtil.getContent(xml, "sendName");
        String sendId = XMLUtil.getContent(xml, "sendId");
        String style = XMLUtil.getContent(xml, "style");
        String body = XMLUtil.getContent(xml, "body");
        String shield = XMLUtil.getContent(xml, "shield");
        String dateStr =  XMLUtil.getContent(xml, "date");
        String fontSizeStr =  XMLUtil.getContent(xml, "fontSize");
        String colorStr =  XMLUtil.getContent(xml, "color");

        MessageMsg msg = new MessageMsg();
        msg.setSendId(sendId);
        msg.setSendName(sendName);
        msg.setStyle(style);
        msg.setBody(body);
        msg.setShield(shield);

        Date date = new Date(Long.parseLong(dateStr));
        msg.setDate(date);
        msg.setFont(new Font("微软雅黑",Font.PLAIN,Integer.parseInt(fontSizeStr)));
        msg.setColor(new Color(Integer.parseInt(colorStr)));
        return msg;
    }
    public static Message createMessage(MessageType messageType){

        Message message =null ;
        switch (messageType){
            case msg:
                  message= new MessageMsg(messageType);
                break;
            case notice:
                 message =new MessageNotice(messageType);
                break;
        }
        return message;

    }
    public static String messageMsgToXml(MessageMsg messageMsg){

        Date date = messageMsg.getDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        String template =
                "<message>"+
                    "<type>"+messageMsg.getType()+"</type>"+
                    "<sendName>"+messageMsg.getSendName()+"</sendName>"+
                    "<sendId>"+messageMsg.getSendId()+"</sendId>"+
                    "<date>"+date.getTime()+"</date>"+
                    "<fontSize>"+messageMsg.getFont().getSize()+"</fontSize>"+
                    "<color>"+messageMsg.getColor().getRGB()+"</color>"+
                    "<style>"+messageMsg.getStyle()+"</style>"+
                    "<shield>"+messageMsg.getShield()+"</shield>"+
                    "<body>"+messageMsg.getBody()+"</body>"+
                "</message>";

        return template;
    }


    public static String messageNoticeToXml(MessageNotice messageNotice){
        String template =
                "<message>" +
                    "<type>%s</type>" +
                    "<clientId>%s</clientId>" +
                    "<clientName>%s</clientName>" +
                    "<online>" +
//                     "id-name,id-name" +
                     "%s" +
                    "</online>" +
                "</message>";
        StringBuilder stringBuilder = new StringBuilder();
        for (User user : messageNotice.getOnline()) {
            stringBuilder.append(user.getId()+"-"+user.getName()+",");
        }
        if(stringBuilder.length()>0){
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }


        return String.format(template,
                MessageType.notice,
                messageNotice.getClientId(),
                messageNotice.getClientName(),
                stringBuilder
        );
    }
    public static void main(String[] args) {
        MessageMsg message = (MessageMsg) MessageUtil.createMessage(MessageType.msg);
//        MessageNotice messageNotice = (MessageNotice) MessageUtil.createMessage(MessageType.notice);






    }

}
