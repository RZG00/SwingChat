软件整体思路：
 需要有一台电脑点击创建，来启动服务程序。所有的窗口程序都是客户端。
客户端只能给服务端发送消息，然后由服务端转发给所有的客户端。

为了告诉所有的客户端当前在线人数，需要设置一个心跳消息。

消息格式
```xml
 <message>
<!--   消息类型：msg-->
    <type>msg</type>
    <sendName>消息发送者名字</sendName>
    <sendId>消息发送者id</sendId>
    <date>2022-8-9 20:12:20</date>
    <fontSize>25</fontSize>
    <color>int的rgb</color>
    <style>b i u</style>
<!-- 被屏蔽掉的id-->
    <shield>id</shield>
<!--    包含b就加粗，i，u同理分别是倾斜，下划线-->
    <content></content>
</message>
```
```xml
<message>
<!--   消息类型：msg，notice-->
    <type>notice</type>
    <clientId>被分配的ID</clientId>
    <clientName>上传自己的Name</clientName>
    <online>
        id-name,id-name
    </online>
</message>
```

- 客户端仅具备发送消息，接收消息的功能，成功建立连接需要用消息通知客户端
- 客户端不具有获取连接状态的功能。
```aidl
连接成功之后，服务端首先向客户端询问昵称，
```

