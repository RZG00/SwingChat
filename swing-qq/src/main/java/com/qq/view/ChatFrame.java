/*
 * Created by JFormDesigner on Fri Dec 30 21:48:46 CST 2022
 */

package com.qq.view;

import java.beans.*;
import javax.swing.event.*;
import com.qq.core.ChatClient;
import com.qq.core.ChatService;
import com.qq.core.ChatServiceExtention;
import com.qq.core.SocketCallback;
import com.qq.entity.User;
import com.qq.entity.message.MessageMsg;
import com.qq.entity.message.MessageNotice;
import com.qq.tools.MessageUtil;
import com.qq.tools.StringUtil;
import com.qq.tools.XMLUtil;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author Brainrain
 */
public class ChatFrame extends JFrame {

    private ChatClient chatClient = new ChatClient();//客户端服务
    private ChatService chatService = new ChatServiceExtention();//只有创建房间的程序用的上

    private List<User> shieldUserLsit = new ArrayList<>();//被屏蔽的用户列表

    private User user = new User(); //记录自己的id 和 昵称
    private List<User> userList = new ArrayList<>();//在线用户列表

    public static void main(String[] args) {
        new ChatFrame().setVisible(true);
    }
    public ChatFrame() {
        initComponents();
        init();
    }

    private void init() {
        user.setName(textField4.getText());
    }

    //连接服务器
    private void connect(String address,int port){
        chatClient.setPORT(port);
        chatClient.setAddress(address);
        //接收到消息的回调函数
        chatClient.setSocketCallback(new SocketCallback() {
            @Override
            public void receiveMsg(String msg) {
                ChatFrame.this.receiveMsg(msg);
            }
        });
        chatClient.start();
    }

    private User getShieldUser(String id){
        if(StringUtil.isNotEmpty(id)){

            for (int i = 0; i < shieldUserLsit.size(); i++) {
                User user1 = shieldUserLsit.get(i);
                if(user1.getId().equals(id)){
                    return user1;
                }
            }

        }
        return null;

    }

    private void showMsg(String msg){
        MessageMsg messageMsg = MessageUtil.parseMessageMsg(msg);

        //检查当前用户的消息是否被屏蔽
        User user1 = getShieldUser(messageMsg.getSendId());
        if(user1!=null){
            return;
        }

        Document document = textPane1.getDocument();
        SimpleAttributeSet sas = new SimpleAttributeSet();
        try {
            //得到发送人
            String send = messageMsg.getSendName();
            send=send+" ";
            StyleConstants.setFontSize(sas,15);
            document.insertString(document.getLength(),send,sas);
            //时间
            Date date = messageMsg.getDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            String time =simpleDateFormat.format(date)+"\n";
            StyleConstants.setFontSize(sas,10);
            document.insertString(document.getLength(),time,sas);

            int fontSize = messageMsg.getFont().getSize();
            StyleConstants.setFontSize(sas,fontSize);

            Color color = messageMsg.getColor();
            StyleConstants.setForeground(sas,color);

            String style = messageMsg.getStyle();
            if(style==null||style.equals("")){
                //todo 默认风格
            }else {
                style = style.toUpperCase();
                StyleConstants.setBold(sas,style.contains("B"));
                StyleConstants.setItalic(sas,style.contains("I"));
                StyleConstants.setUnderline(sas,style.contains("U"));
            }

            String content = messageMsg.getBody();
            content+="\n";

            document.insertString(document.getLength(),content,sas);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 接收到消息 将消息显示在屏幕上
     * @param msg
     */
    private void receiveMsg(String msg){
        String type = XMLUtil.getContent(msg, "type");

        switch (type){
            case "notice":
                //通知类型，需要设置Id  并返回自己的昵称，设置好在线人数
                String clientId = XMLUtil.getContent(msg, "clientId");
                if(StringUtil.isNotEmpty(clientId)){
                    user.setId(clientId);
                }


                //保存用户到userLsit
                setOnlineUser(msg);
                //刷新用户列表的(屏蔽) 帽子
                setShieidNameOnUserList();
                //刷新表格
                flushTable();

                //返回昵称
                MessageNotice messageNotice = new MessageNotice();
                messageNotice.setClientName(user.getName());
                messageNotice.setClientId(clientId);
                String s = MessageUtil.messageNoticeToXml(messageNotice);
                chatClient.sendMsg(s);

                break;
            case "msg":


                //服务端发来消息，显示在屏幕
                showMsg(msg);
                movenLastLine(textPane1);//移动到最后一行
                break;
        }


    }

    //解析消息，保存用户到userList
    private void setOnlineUser(String msg){

        userList = new ArrayList<>();
        //检查当前是否有选中的行，更新数据后会取消选中，
//        int selectedRow = table1.getSelectedRow();
//        DefaultTableModel dft = (DefaultTableModel) table1.getModel();
//        dft.setRowCount(0);

        String online = XMLUtil.getContent(msg, "online");
        if(StringUtil.isNotEmpty(online)){
            String[] split = online.split(",");
            for(String s:split){
                String[] idAndName = s.split("-");
                String id = idAndName[0];
                String name = idAndName[1];

//                Vector<String > vector = new Vector<>();
//                vector.add(name);
//                dft.addRow(vector);

                User user = new User();
                user.setId(id);
                user.setName(name);
                userList.add(user);
            }

        }

//        //恢复选中行
//        if(selectedRow>=0){
//            table1.setRowSelectionInterval(selectedRow,selectedRow);
//        }

    }


    /**
     * 移动JtextPane到最后一行
     * @param textPane1
     */
    private void movenLastLine(JTextPane textPane1){
        textPane1.selectAll();
        textPane1 .setCaretPosition(textPane1.getSelectedText().length());
        textPane1 .requestFocus();
    }

    /**
     * 发送消息
     */
    private void sendMsg(){
        String text = textPane2.getText();
        if(!StringUtil.isNotEmpty(text)){
            JOptionPane.showMessageDialog(this,"您没有输入任何你消息");
            return;
        }

        String fontSize = textField5.getText();
        int size = 12;
        try {
           size  = Integer.parseInt(fontSize);
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,"字体尺寸输入有误");

        }


        String style = "";
        if(checkBox1.isSelected()){
            style=style+"b";
        }
        if(checkBox2.isSelected()){
            style=style+"i";
        }
        if(checkBox3.isSelected()){
            style=style+"u";
        }

        MessageMsg messageMsg = new MessageMsg();
        messageMsg.setSendId(user.getId());
        messageMsg.setSendName(user.getName());
        messageMsg.setColor(button8.getBackground());
        messageMsg.setDate(new Date());
        messageMsg.setShield("");//屏蔽的人员
        messageMsg.setBody(text);
        messageMsg.setStyle(style);
        messageMsg.setFont(new Font("微软雅黑", Font.PLAIN, size));
        String xml = MessageUtil.messageMsgToXml(messageMsg);

        chatClient.sendMsg(xml);
        textPane2.setText("");

    }

    private void startService(){

        String portStr = textField1.getText();
        //开启服务端
        int port = Integer.parseInt(portStr);
        chatService.setPort(port);
        chatService.start();
    }

    /**
     * 创建 按钮的点击事件
     * @param e
     */
    private void button1(ActionEvent e) {
        String portStr = textField1.getText();
        if (!StringUtil.isNotEmpty(portStr)){
            JOptionPane.showMessageDialog(this, "请输入端口号！");
            return;
        }else{
            startService();
            //创建完服务端，自己也要连接
            connect("127.0.0.1",Integer.parseInt(portStr));
            JOptionPane.showMessageDialog(this, "成功创建房间！");
            button1.setEnabled(false);
            tabbedPane1.setEnabledAt(1,false);
            button6.setEnabled(true);
        }

    }

    /*
    销毁按钮点击事件
     */
    private void button2(ActionEvent e) {
        chatClient.CloseClient();
        chatService.CloseService();
        button1.setEnabled(true);
        tabbedPane1.setEnabledAt(1,true);
        button6.setEnabled(false);//禁言按钮失效

    }

    /*
    加入按钮点击事件
     */
    private void button3(ActionEvent e) {
        String address = textField2.getText();
        String port = textField3.getText();
        if(!StringUtil.isNotEmpty(address)){
            JOptionPane.showMessageDialog(this,"IP地址不能为空");
            return;
        }
        if(!StringUtil.isNotEmpty(port)){
            JOptionPane.showMessageDialog(this,"端口号不能为空");
            return;
        }
        chatClient.setAddress(address);
        chatClient.setPORT(Integer.parseInt(port));
        chatClient.setSocketCallback(new SocketCallback() {
            @Override
            public void receiveMsg(String msg) {
                ChatFrame.this.receiveMsg(msg);
            }
        });
        chatClient.start();
        button3.setEnabled(false);
        JOptionPane.showMessageDialog(this,"加入成功");

    }

    /*
    发送按钮点击事件
     */
    private void button4(ActionEvent e) {
        sendMsg();
    }

    /*
    颜色选择面板，将选择的颜色保存在button8.backGround中
     */
    private void button8(ActionEvent e) {
        // TODO add your code here
        Color color =
            JColorChooser.showDialog(this,"消息颜色",Color.black);
        button8.setBackground(color);

    }

    //修改昵称输入框，失去焦点
    private void textField4FocusLost(FocusEvent e) {
        // TODO add your code here
        JTextField jTextField = (JTextField) e.getComponent();
        user.setName( jTextField.getText());

        //返回昵称
        MessageNotice messageNotice = new MessageNotice();
        messageNotice.setClientName(user.getName());
        messageNotice.setClientId(user.getId());
        String s = MessageUtil.messageNoticeToXml(messageNotice);
        chatClient.sendMsg(s);

    }

    private void textPane2KeyPressed(KeyEvent e) {
        // TODO add your code here
        if(e.getKeyCode()==10){
            sendMsg();
        }
    }

    /**
     * 禁言/解除 按钮点击事件
     * @param e
     */
    private void JinYanBtnClick(ActionEvent e) {
        // TODO add your code here
        int selectedRow = table1.getSelectedRow();
        System.out.println(selectedRow);



        if(selectedRow>=0){
            //得到要禁言的用户id
            User user1 = userList.get(selectedRow);


            User prohibitUser = chatService.getProhibitUser(user1.getId());

            if(prohibitUser==null){
                chatService.addProhibitUser(user1.getId());//通知服务端禁言
                //用户列表显示禁言标志
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                String  value = (String) model.getValueAt(selectedRow, 0);
                model.setValueAt(value+"(禁言)",selectedRow,0);
            }else {
                // 已经禁言
                chatService.delProhibitUser(prohibitUser.getId());//解除禁言
                //去掉禁言标志
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                String  value = (String) model.getValueAt(selectedRow, 0);
                value = value.replace("(禁言)","");
                model.setValueAt(value,selectedRow,0);
            }


        }

    }

    //屏蔽按钮
    private void PingBiBtnClick(ActionEvent e) {


        //得到选选中的index
        int selectedRow = table1.getSelectedRow();

        if(selectedRow>=0){
            User user = userList.get(selectedRow);
            //查看是否已经屏蔽
            if(null==getShieldUser(user.getId())){
                //没有屏蔽
                shieldUserLsit.add(user);//添加进屏蔽列表
            }else {
                //已经被屏蔽
                delSheildUser(user.getId());//解除屏蔽
            }
            setShieidNameOnUserList();//设置屏蔽帽子
            flushTable();//刷新Table表格

        }else{
            JOptionPane.showMessageDialog(this,"请选择被屏蔽的用户");
        }
    }

    private void delSheildUser(String id){
        if(StringUtil.isNotEmpty(id)){

            for (int i = 0; i < shieldUserLsit.size(); i++) {
                User user = shieldUserLsit.get(i);
                if(id.equals(user.getId())){
                    shieldUserLsit.remove(user);
                }
            }

        }

    }

    /**
     * 刷新在线table表格，根据userList信息
     */
    private void flushTable() {
        int selectedRow = table1.getSelectedRow();

        DefaultTableModel dft = (DefaultTableModel) table1.getModel();
        dft.setRowCount(0);
        for (User user : userList) {
            Vector vector = new Vector();
            vector.add(user.getName());
            dft.addRow(vector);
        }

        if(selectedRow>=0){
            table1.setRowSelectionInterval(selectedRow,selectedRow);
        }
    }

    /**
     * 给屏蔽的用户设置（屏蔽）帽子
     */
    protected void setShieidNameOnUserList(){
        for (User user : userList) {
            User prohibitUser = getShieldUser(user.getId());
            String name = user.getName();
            if(prohibitUser==null){
                //没有被屏蔽的用户，名字要去掉（屏蔽）帽子
                user.setName(name.replace("(屏蔽)",""));
            }else{
                //被禁言的用户，名字要加上(禁言)帽子
                if(!name.contains("(屏蔽)")){
                    user.setName(name+"(屏蔽)");
                }

            }
        }

    }

    //退出按钮
    private void TuiChuBtn(ActionEvent e) {
            chatClient.CloseClient();
            button3.setEnabled(true);
            userList = new ArrayList<>();
            flushTable();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panel1 = new JPanel();
        tabbedPane1 = new JTabbedPane();
        panel3 = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        button1 = new JButton();
        button2 = new JButton();
        panel4 = new JPanel();
        label2 = new JLabel();
        textField2 = new JTextField();
        label3 = new JLabel();
        textField3 = new JTextField();
        button3 = new JButton();
        button5 = new JButton();
        panel8 = new JPanel();
        scrollPane4 = new JScrollPane();
        table1 = new JTable();
        button6 = new JButton();
        button7 = new JButton();
        panel9 = new JPanel();
        label4 = new JLabel();
        textField4 = new JTextField();
        label5 = new JLabel();
        checkBox1 = new JCheckBox();
        checkBox2 = new JCheckBox();
        checkBox3 = new JCheckBox();
        hSpacer2 = new JPanel(null);
        label6 = new JLabel();
        button8 = new JButton();
        hSpacer3 = new JPanel(null);
        label7 = new JLabel();
        textField5 = new JTextField();
        panel6 = new JPanel();
        scrollPane1 = new JScrollPane();
        textPane1 = new JTextPane();
        panel7 = new JPanel();
        scrollPane2 = new JScrollPane();
        textPane2 = new JTextPane();
        button4 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setMaximumSize(new Dimension(210, 30000));
            panel1.setMinimumSize(new Dimension(200, 110));
            panel1.setPreferredSize(new Dimension(210, 500));
            panel1.setLayout(new FlowLayout());

            //======== tabbedPane1 ========
            {
                tabbedPane1.setBorder(new TitledBorder("\u8fde\u63a5"));
                tabbedPane1.setPreferredSize(new Dimension(200, 150));
                tabbedPane1.setMaximumSize(new Dimension(200, 32767));

                //======== panel3 ========
                {
                    panel3.setPreferredSize(new Dimension(200, 75));
                    panel3.setMaximumSize(new Dimension(200, 32767));
                    panel3.setMinimumSize(new Dimension(100, 43));
                    panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));

                    //---- label1 ----
                    label1.setText("\u7aef\u53e3\u53f7\uff1a");
                    panel3.add(label1);

                    //---- textField1 ----
                    textField1.setColumns(10);
                    textField1.setText("8888");
                    panel3.add(textField1);

                    //---- button1 ----
                    button1.setText("\u521b\u5efa");
                    button1.addActionListener(e -> button1(e));
                    panel3.add(button1);

                    //---- button2 ----
                    button2.setText("\u9500\u6bc1");
                    button2.addActionListener(e -> button2(e));
                    panel3.add(button2);
                }
                tabbedPane1.addTab("\u521b\u5efa", panel3);

                //======== panel4 ========
                {
                    panel4.setLayout(new FlowLayout());

                    //---- label2 ----
                    label2.setText("IP\u5730\u5740\uff1a");
                    panel4.add(label2);

                    //---- textField2 ----
                    textField2.setColumns(10);
                    textField2.setText("127.0.0.1");
                    panel4.add(textField2);

                    //---- label3 ----
                    label3.setText("\u7aef\u53e3\u53f7\uff1a");
                    panel4.add(label3);

                    //---- textField3 ----
                    textField3.setColumns(10);
                    textField3.setText("8888");
                    panel4.add(textField3);

                    //---- button3 ----
                    button3.setText("\u52a0\u5165");
                    button3.addActionListener(e -> button3(e));
                    panel4.add(button3);

                    //---- button5 ----
                    button5.setText("\u9000\u51fa");
                    button5.addActionListener(e -> TuiChuBtn(e));
                    panel4.add(button5);
                }
                tabbedPane1.addTab("\u52a0\u5165", panel4);
            }
            panel1.add(tabbedPane1);

            //======== panel8 ========
            {
                panel8.setBorder(new TitledBorder(""));
                panel8.setMaximumSize(new Dimension(210, 32767));
                panel8.setPreferredSize(new Dimension(210, 170));
                panel8.setLayout(new FlowLayout());

                //======== scrollPane4 ========
                {
                    scrollPane4.setPreferredSize(new Dimension(200, 127));

                    //---- table1 ----
                    table1.setModel(new DefaultTableModel(
                        new Object[][] {
                        },
                        new String[] {
                            "\u5728\u7ebf\u4eba\u5458"
                        }
                    ));
                    table1.setPreferredScrollableViewportSize(new Dimension(200, 100));
                    table1.setRowHeight(25);
                    scrollPane4.setViewportView(table1);
                }
                panel8.add(scrollPane4);

                //---- button6 ----
                button6.setText("\u7981\u8a00/\u89e3\u9664");
                button6.setEnabled(false);
                button6.addActionListener(e -> JinYanBtnClick(e));
                panel8.add(button6);

                //---- button7 ----
                button7.setText("\u5c4f\u853d/\u89e3\u9664");
                button7.addActionListener(e -> PingBiBtnClick(e));
                panel8.add(button7);
            }
            panel1.add(panel8);

            //======== panel9 ========
            {
                panel9.setBorder(new TitledBorder("\u8bbe\u7f6e"));
                panel9.setMaximumSize(new Dimension(210, 32767));
                panel9.setPreferredSize(new Dimension(210, 160));
                panel9.setLayout(new FlowLayout(FlowLayout.LEFT));

                //---- label4 ----
                label4.setText("\u6635\u79f0\uff1a");
                panel9.add(label4);

                //---- textField4 ----
                textField4.setColumns(10);
                textField4.setText("\u5c0f\u534e");
                textField4.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        textField4FocusLost(e);
                    }
                });
                panel9.add(textField4);

                //---- label5 ----
                label5.setText("\u683c\u5f0f\uff1a");
                panel9.add(label5);

                //---- checkBox1 ----
                checkBox1.setText("B");
                checkBox1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
                panel9.add(checkBox1);

                //---- checkBox2 ----
                checkBox2.setText("I ");
                checkBox2.setFont(new Font("Microsoft YaHei UI", Font.ITALIC, 12));
                panel9.add(checkBox2);

                //---- checkBox3 ----
                checkBox3.setText("U");
                panel9.add(checkBox3);

                //---- hSpacer2 ----
                hSpacer2.setPreferredSize(new Dimension(20, 10));
                panel9.add(hSpacer2);

                //---- label6 ----
                label6.setText("\u989c\u8272\uff1a");
                panel9.add(label6);

                //---- button8 ----
                button8.setText(" ");
                button8.setBackground(Color.black);
                button8.addActionListener(e -> button8(e));
                panel9.add(button8);

                //---- hSpacer3 ----
                hSpacer3.setPreferredSize(new Dimension(100, 10));
                panel9.add(hSpacer3);

                //---- label7 ----
                label7.setText("\u5b57\u4f53\u5927\u5c0f\uff1a");
                panel9.add(label7);

                //---- textField5 ----
                textField5.setColumns(4);
                textField5.setText("12");
                panel9.add(textField5);
            }
            panel1.add(panel9);
        }
        contentPane.add(panel1, BorderLayout.WEST);

        //======== panel6 ========
        {
            panel6.setBorder(new TitledBorder("\u804a\u5929"));
            panel6.setPreferredSize(new Dimension(600, 104));
            panel6.setLayout(new BorderLayout(0, 5));

            //======== scrollPane1 ========
            {
                scrollPane1.setAutoscrolls(true);

                //---- textPane1 ----
                textPane1.setEditable(false);
                scrollPane1.setViewportView(textPane1);
            }
            panel6.add(scrollPane1, BorderLayout.CENTER);

            //======== panel7 ========
            {
                panel7.setLayout(new BorderLayout(5, 5));

                //======== scrollPane2 ========
                {

                    //---- textPane2 ----
                    textPane2.setPreferredSize(new Dimension(200, 50));
                    textPane2.setMargin(new Insets(5, 5, 5, 5));
                    textPane2.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            textPane2KeyPressed(e);
                        }
                    });
                    scrollPane2.setViewportView(textPane2);
                }
                panel7.add(scrollPane2, BorderLayout.CENTER);

                //---- button4 ----
                button4.setText("\u53d1\u9001");
                button4.addActionListener(e -> button4(e));
                panel7.add(button4, BorderLayout.EAST);
            }
            panel6.add(panel7, BorderLayout.SOUTH);
        }
        contentPane.add(panel6, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JPanel panel3;
    private JLabel label1;
    private JTextField textField1;
    private JButton button1;
    private JButton button2;
    private JPanel panel4;
    private JLabel label2;
    private JTextField textField2;
    private JLabel label3;
    private JTextField textField3;
    private JButton button3;
    private JButton button5;
    private JPanel panel8;
    private JScrollPane scrollPane4;
    private JTable table1;
    private JButton button6;
    private JButton button7;
    private JPanel panel9;
    private JLabel label4;
    private JTextField textField4;
    private JLabel label5;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JPanel hSpacer2;
    private JLabel label6;
    private JButton button8;
    private JPanel hSpacer3;
    private JLabel label7;
    private JTextField textField5;
    private JPanel panel6;
    private JScrollPane scrollPane1;
    private JTextPane textPane1;
    private JPanel panel7;
    private JScrollPane scrollPane2;
    private JTextPane textPane2;
    private JButton button4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
