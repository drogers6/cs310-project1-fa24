package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
 



public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";

    private final int CSV_CRN_COLUMN = 0;
    private final int CSV_SUBJECT_COLUMN = 1;
    private final int CSV_NUM_COLUMN = 2;
    private final int CSV_DESCRIPTION_COLUMN = 3;
     private final int CSV_SECTION_COLUMN = 4;
     private final int CSV_TYPE_COLUMN = 5;
    private final int CSV_CREDITS_COLUMN = 6;
    private final int CSV_START_COLUMN = 7;
    private final int CSV_END_COLUMN = 8;
    private final int CSV_DAYS_COLUMN = 9;
    private final int CSV_WHERE_COLUMN = 10;
    private final int CSV_SCHEDULE_COLUMN = 11;
    private final int CSV_INSTRUCTOR_COLUMN = 12;
    
    public String convertCsvToJsonString(List<String[]> csv) {

        
        Iterator<String[]> iterator = csv.iterator();
        String[] headerRow = iterator.next();
        
        JsonObject jsonData = new JsonObject();
        
        JsonObject scheduletype = new JsonObject();
        JsonObject subject = new JsonObject();
        JsonObject course = new JsonObject();
        //JsonObject section_objects = new JsonObject();
        JsonObject sections = new JsonObject();
        JsonArray section = new JsonArray();
        
        while (iterator.hasNext()) {
            
            String[] csvRow = iterator.next();
            
            String subjectid = (csvRow[CSV_NUM_COLUMN].split(" "))[0];
            
            subject.put(subjectid, csvRow[CSV_SUBJECT_COLUMN]);
            
            String schedule = (csvRow[CSV_TYPE_COLUMN].split(" "))[0];
           
            scheduletype.put(schedule, csvRow[CSV_SCHEDULE_COLUMN]);
            //-----------------courses-----------------
            String courses = (csvRow[CSV_NUM_COLUMN]);
            
            String course_words = (csvRow[CSV_NUM_COLUMN].split(" "))[0];
            String course_num = (csvRow[CSV_NUM_COLUMN].split(" "))[1];
            String course_des = (csvRow[CSV_DESCRIPTION_COLUMN]);
            String course_credits = (csvRow[CSV_CREDITS_COLUMN]);
            
            LinkedHashMap<String, Object> course_container = new LinkedHashMap<>();
            course_container.put("subjectid",course_words);
            course_container.put("num",course_num);
            course_container.put("description",course_des);
            course_container.put("credits",Integer.parseInt(course_credits));
            course.put(courses,course_container);
            
            //-----------------section-----------------
            String CRN = (csvRow[CSV_CRN_COLUMN]);
            LinkedHashMap<String, Object> section_objects = new LinkedHashMap<>();
            section_objects.put("crn",Integer.parseInt(CRN));//Integer.parseInt
            String section_words = (csvRow[CSV_NUM_COLUMN].split(" "))[0];
            String section_num = (csvRow[CSV_NUM_COLUMN].split(" "))[1];
            section_objects.put("subjectid",section_words);
            section_objects.put("num",section_num);
            section_objects.put("section",csvRow[CSV_SECTION_COLUMN]);
            section_objects.put("type",schedule);
            section_objects.put("start",csvRow[CSV_START_COLUMN]);
            section_objects.put("end",csvRow[CSV_END_COLUMN]);
            section_objects.put("days",csvRow[CSV_DAYS_COLUMN]);
            section_objects.put("where",csvRow[CSV_WHERE_COLUMN]);
            
            //for loop for instructors
            JsonArray instructor = new JsonArray();
            instructor.add(csvRow[CSV_INSTRUCTOR_COLUMN]);
            String instructors_conatiner[] = new String[instructor.size()];
            
            for (int i = 0; i < instructor.size(); i++) {
            instructors_conatiner[i] = instructor.getString(i);
            }
            
            for (String value : instructors_conatiner) {
            JsonArray instructors_conatiner2 = new JsonArray(); //container 2 holds value and puts it into an array
            instructors_conatiner2.add(value);
            section_objects.put("instuctor",instructors_conatiner2);
            }
            
            section.add(section_objects);
            
          
            
            
           

        }
        
        jsonData.put("scheduletype", scheduletype);
        jsonData.put("subject", subject);
        jsonData.put("courses", course);
        jsonData.put("sections", section);
        
        return ( Jsoner.serialize(jsonData));

    }
    
    public String convertJsonToCsvString(JsonObject json) {
        
        return ""; // remove this!
        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}