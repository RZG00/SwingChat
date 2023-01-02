package com.qq.tools;

public class XMLUtil {


    /**
     * 在xml中获取元素名为elementName的标签中的内容字符串
     * @param xml
     * @param elementName
     * @return
     */
    public static String getContent(String xml,String elementName){
        /**
         <message>
         <send></send>
         <time></time>
         <fontSize></fontSize>
         <color></color>
         <style>b,i,u</style>
         <content>
         </content>
         </message>
         */
        int startIndex = xml.indexOf("<"+elementName+">")+elementName.length()+2;
        int endIndex = xml.indexOf("</"+elementName+">");
        String substring = null;
        if(startIndex>=0&&endIndex>=0){
            substring = xml.substring(startIndex, endIndex);
        }
        return substring;

    }

    public static void main(String[] args) {
        String msg = " <message>\n" +
                "         <send></send>\n" +
                "         <time></time>\n" +
                "         <fontSize></fontSize>\n" +
                "         <color></color>\n" +
                "         <style>b,i,u</style>\n" +
                "         <content>\n" +
                "         </content>\n" +
                "         </message>";
        System.out.println(XMLUtil.getContent(msg,"message"));
    }
}
