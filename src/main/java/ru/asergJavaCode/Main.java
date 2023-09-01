package ru.asergJavaCode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        List<Employee> listXML = parseXML("data.xml");
        String csv = listToJson(listCSV);
        String xml = listToJson(listXML);
        writeString(csv, "dataCSV.json");
        writeString(xml, "dataXML.json");
    }

    private static List<Employee> parseXML(String path) {
        List<Employee> tempListXML = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(path));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        int lNode = nodeList.getLength();
        for (int i = 0; i < lNode; i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element employee = (Element) node_;
                long id = Integer.parseInt(employee.getElementsByTagName("id").item(0).getTextContent());
                String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                String country = employee.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
                Employee objEmployee = new Employee(id, firstName, lastName, country, age);
                tempListXML.add(objEmployee);
            }

        }
        return tempListXML;
    }

    private static void writeString(String json, String file) {
        try (FileWriter writer = new FileWriter(file);) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }
}