package com.myspring.di;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.naming.ConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
            try {
                instantiateBeans();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchFieldException | InvalidConfigurationException | ConfigurationException e) {
                e.printStackTrace();
            }
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

    private void instantiateBeans() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, InvalidConfigurationException, ConfigurationException {
        for (Bean bean : beans) {

            // get class instance
            Class<?> aClass = Class.forName(bean.getClassName());
            Object ob = aClass.newInstance();

            // set up ob
            for(String id : bean.getProperties().keySet()) {
                Field field = getField(aClass, id);

                if (field == null) {
                    throw new InvalidConfigurationException("Failed to set field " + id + " for class " + aClass.getName());
                }

                field.setAccessible(true);

                Property property = bean.getProperties().get(id);

                switch (property.getType()) {
                    case VALUE:
//                        field.set(ob, property.getValue());   // property.getValue() - already return string
                        field.set(ob, convert(field.getType().getName(), property.getValue()));  // (field type, value)
                        break;
                    case REF:
                        break;
                    default:
                        throw new InvalidConfigurationException("Type error");
                }
            }

            // put into map
            objectsById.put(bean.getId(), ob);
        }
    }

    private Object convert(String typeName, String value) throws ConfigurationException {
        switch (typeName) {
            case "int":
            case "Integer":
                return Integer.valueOf(value);
            case "double":
            case "Double":
                return Double.valueOf(value);
            case "float":
            case "Float":
                return Float.valueOf(value);
            case "boolean":
            case "Boolean":
                return Boolean.valueOf(value);
            default:
                throw new ConfigurationException();
        }
    }

    private Field getField(Class<?> aClass, String fieldName) throws NoSuchFieldException {
        return aClass.getDeclaredField(fieldName);
    }

}
