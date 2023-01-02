package com.qq.tools;

import java.util.UUID;

public class UUIDUtil {

    public static void main(String[] args) {
        String s = UUIDUtil.newUUID();
        System.out.println(s);
        System.out.println(s.length());

    }

    public static String newUUID(){
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        s= s.replaceAll("-","");
        return s;
    }

}
