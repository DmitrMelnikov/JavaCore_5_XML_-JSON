package org.XML_JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String nameXmlFile = "data.xml";
        String nameJsonFile = "data2.json";
        makeXmlFile(nameXmlFile);
        String strJson = listToJson(parseXML(nameXmlFile));
        writeString(strJson, nameJsonFile);

    }

    public static void makeXmlFile(String fileName) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            // создаем пустой объект Document, в котором будем
            // создавать наш xml-файл
            Document document = builder.newDocument();
            // создаем корневой элемент
            Element rootElement = document.createElement("staff");
            // добавляем корневой элемент в объект Document
            document.appendChild(rootElement);
            // добавляем первый дочерний элемент к корневому
            rootElement.appendChild(getEmployee(document, "1", "John", "Smith", "USA", "25"));
            //добавляем второй дочерний элемент к корневому
            rootElement.appendChild(getEmployee(document, "2", "Inav", "Petrov", "RU", "23"));
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(fileName));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, streamResult);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Node getEmployeeAttribute(Document doc, String id, String firstName, String lastName, String country, String age) {

        Element employee = doc.createElement("employee");
        employee.setAttribute("id", id);
        employee.setAttribute("firstName", firstName);
        employee.setAttribute("lastName", lastName);
        employee.setAttribute("country", country);
        employee.setAttribute("age", age);

        return employee;
    }

    private static Node getEmployee(Document doc, String id, String firstName, String lastName, String country, String age) {

        Element employee = doc.createElement("employee");

        employee.appendChild((getElements(doc, employee, "id", id)));
        employee.appendChild(getElements(doc, employee, "firstName", firstName));
        employee.appendChild(getElements(doc, employee, "lastName", lastName));
        employee.appendChild(getElements(doc, employee, "country", country));
        employee.appendChild(getElements(doc, employee, "age", age));
        return employee;
    }

    private static Node getElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    public static List<Employee> parseXML(String fileName) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        try {
            doc = builder.parse(new File(fileName));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Node root = doc.getDocumentElement();
        return read(root);


    }

    private static List<Employee> read(Node node) {
        List list = new ArrayList<Employee>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (node_.getNodeName() == "employee") {
                NodeList listEmployee = node_.getChildNodes();
                Employee employee = new Employee(Long.parseLong(listEmployee.item(0).getTextContent().toString()),
                        listEmployee.item(1).getTextContent().toString(),
                        listEmployee.item(2).getTextContent().toString(),
                        listEmployee.item(3).getTextContent().toString(),
                        Integer.parseInt(listEmployee.item(4).getTextContent().toString()));
                list.add(employee);
            }
        }
        return list;
    }


    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String strJson, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(strJson);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}