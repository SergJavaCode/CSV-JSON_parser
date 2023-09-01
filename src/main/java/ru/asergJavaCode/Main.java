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
    static Employee objEmployee;
    static List<Employee> tempListXML = new ArrayList<>();

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        List<Employee> listXML = parseXML("data.xml");
        String json = listToJson(listCSV);
        String xml = listToJson(listXML);
        writeString(json, "dataCSV.json");
        writeString(xml, "dataXML.json");
    }

    private static List<Employee> read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            String ss = node_.getNodeName();
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                element.getNodeValue();
                String name = element.getTagName();
                String value = element.getTextContent();
                if (name.equals("id")) {
                    objEmployee = new Employee();
                    objEmployee.id = Long.parseLong(value);
                }
                if (name.equals("firstName")) {
                    objEmployee.firstName = value;
                }
                if (name.equals("lastName")) {
                    objEmployee.lastName = value;
                }
                if (name.equals("country")) {
                    objEmployee.country = value;
                }
                if (name.equals("age")) {
                    objEmployee.age = Integer.parseInt(value);
                    tempListXML.add(objEmployee);
                }
                read(node_);
            }
        }
        return tempListXML;
    }

    private static List<Employee> parseXML(String path) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(path));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        Node root = doc.getDocumentElement();
        return read(root);

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