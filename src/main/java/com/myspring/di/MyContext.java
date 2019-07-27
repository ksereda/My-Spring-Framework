package com.myspring.di;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContext {

    public static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";

    private Map<String, Object> objectsById = new HashMap<>();
    private List<Bean> beans = new ArrayList<>();

    public MyContext(String xmlPath) {
        // parsing XML - fill ArrayList beans
        try {
            parseXml(xmlPath);
        } catch (ParserConfigurationException | IOException | SAXException | InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }

        // create class instance based on a bean: beans -> objectsById
        instantiateBeans();
    }

    public Object getBean(String beanId) {
        return objectsById.get(beanId);
    }

    private void parseXml(String xmlPath) throws ParserConfigurationException, IOException, SAXException, InvalidConfigurationException {
        // DOM parser or SAX parser
        Document document;
        // Document <- DocumentBuilder <- DocumentBuilderFactory (singleton)
        document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(xmlPath));

        Element root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node bean = nodes.item(i);
            if (TAG_BEAN.equals(bean.getNodeName())) {
                parseBean(bean);
            }
        }
    }

    private void parseBean(Node bean) throws InvalidConfigurationException {
        NamedNodeMap attributes = bean.getAttributes();
        Node id = attributes.getNamedItem("id");
        String idVal = id.getNodeValue();
        String classVal = attributes.getNamedItem("class").getNodeValue();

        Map<String, Property> properties = new HashMap<>();

        NodeList nodes = bean.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (TAG_PROPERTY.equals(node.getNodeName())) {
                Property property = parseProperty(node);
                properties.put(property.getName(), property);
            }

        }

        beans.add(new Bean(idVal, classVal, properties));
    }

    private Property parseProperty(Node node) throws InvalidConfigurationException {
        NamedNodeMap attributes = node.getAttributes();
        String name = attributes.getNamedItem("name").getNodeValue();
        Node val = attributes.getNamedItem("val");
        if (val != null) {
            return new Property(name, val.getNodeValue(), ValueType.VALUE);
        } else {
            Node ref = attributes.getNamedItem("ref");
            if (ref == null) {
                throw new InvalidConfigurationException("Failed to find attributes ref or val");
            } else {
                return new Property(name, ref.getNodeValue(), ValueType.REF);
            }
        }
    }

    private void instantiateBeans() {
    }

}
