import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Task 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        // Task 2
        List<Employee> list2 = parseXML();
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
        //Task3
        String json3 = readString();
        List<Employee> list3 = jsonToList(json3);
        printList(list3);

    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            Employee employee = gson.fromJson(String.valueOf(jsonObject), Employee.class);
            list.add(employee);
        }
        return list;
    }

    private static String readString() {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try {
            Object obj = parser.parse(new BufferedReader(new FileReader("data2.json")));
            jsonArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            list = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> String listToJson(List<T> list) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String string, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            assert builder != null;
            doc = builder.parse(new File("data.xml"));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Node root = doc.getDocumentElement();
        return read(root);

    }

    private static List<Employee> read(Node node) {
        List<Employee> list = new ArrayList<>();
        long id;
        String firstName;
        String lastName;
        String country;
        int age;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                country = element.getElementsByTagName("country").item(0).getTextContent();
                age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                list.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return list;
    }

    public static void printList(List<Employee> list) {
        System.out.println("> Task :Main.main()");
        for (Employee employee : list) {
            System.out.println(employee);
        }
    }
}