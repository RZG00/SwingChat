package com.qq;

import javax.swing.*;
public class Main {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {

//        for (UIManager.LookAndFeelInfo installedLookAndFeel
//                : UIManager.getInstalledLookAndFeels()) {
////            System.out.println(installedLookAndFeel.getName());
//            if("Nimbus".equals(installedLookAndFeel.getName())){
//                    UIManager.setLookAndFeel(installedLookAndFeel.getClassName());
//            }
//        }
        A a = new B();
        a.aa();


    }
}

class A{
    public void aa(){
        a();
    }
    public void a(){
        System.out.println("A");
    }
}

class B extends A{
    @Override
    public void a() {
        System.out.println("B");
    }
}
