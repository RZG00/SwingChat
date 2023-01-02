package com.qq.view;

import java.util.ArrayList;
import java.util.List;

public class MyTest {
    public static void main(String[] args) {
        B();
    }

    private static void A() {
        String s = "123";
        String b = "";
        System.out.println("s:"+s);
        System.out.println("b:"+b);
        b = s;
        s = "456";
        System.out.println("s:"+s);
        System.out.println("b:"+b);
    }

    private static void B(){
        List<User> userList = new ArrayList<>();
        userList.add(new User("123","小华"));
        User user = userList.get(0);
        System.out.println(user);
        userList.remove(user);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(user);

    }
}

class User{

    String id;
    String name;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
