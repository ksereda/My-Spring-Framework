package com.myspring;

import com.myspring.di.MyContext;

public class Main {

    public static void main(String[] args) {
        MyContext myContext = new MyContext("config.xml");
        Car car = myContext.getBean("car");
    }

}
