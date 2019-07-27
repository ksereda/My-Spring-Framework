package com.myspring.di;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContext {

    private Map<String, Object> objectsById = new HashMap<>();
    private List<Bean> beans = new ArrayList<>();

    public MyContext(String xmlPath) {
        // parsing XML - fill ArrayList beans
        parseXml(xmlPath);

        // create class instance based on a bean: beans -> objectsById
        instantiateBeans();
    }

    public Object getBean(String beanId) {
        return objectsById.get(beanId);
    }

    private void parseXml(String xmlPath) {
    }

    private void instantiateBeans() {
    }

}
