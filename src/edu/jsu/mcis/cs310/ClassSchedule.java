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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

 



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
        
        
        LinkedHashMap<String, Object> jsonData = new LinkedHashMap<>();
        LinkedHashMap<String, Object> scheduletype = new LinkedHashMap<>();
        LinkedHashMap<String, Object> subject = new LinkedHashMap<>();
        JsonObject course = new JsonObject();
        JsonArray section = new JsonArray();
        
        while (iterator.hasNext()) {
            
            String[] csvRow = iterator.next();
             //-----------------subjectid-----------------
            String subjectid = (csvRow[CSV_NUM_COLUMN].split(" "))[0];
            
            subject.put(subjectid, csvRow[CSV_SUBJECT_COLUMN]);
            //-----------------schedule-----------------
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
            section_objects.put("crn",Integer.parseInt(CRN));
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
            
            
            JsonArray instructor = new JsonArray();
            
            String[] instructorArray = csvRow[CSV_INSTRUCTOR_COLUMN].split(",");
            
            //for loop for instructors
            for (String element : instructorArray) {
                instructor.add(element.trim()); // container to store instructors
            }
            
            section_objects.put(INSTRUCTOR_COL_HEADER,instructor);// seperate instructors with comma
          
            section.add(section_objects);
      
        }
        
        jsonData.put("scheduletype", scheduletype);
        jsonData.put("subject", subject);
        jsonData.put("course", course);
        jsonData.put("section", section);
        
        return ( Jsoner.serialize(jsonData));

    }
    
    public String convertJsonToCsvString(JsonObject json) {
       
        JsonObject jsondata = new JsonObject(json);

        JsonObject schedule = (JsonObject) jsondata.get("scheduletype");
        JsonObject subjects = (JsonObject) jsondata.get("subject");
        JsonObject courses = (JsonObject) jsondata.get("course");
        JsonArray section = (JsonArray) jsondata.get("section");

        List<String[]> rows = new ArrayList<>();

        String[] headers = {
            CRN_COL_HEADER, 
            SUBJECT_COL_HEADER, 
            NUM_COL_HEADER, 
            DESCRIPTION_COL_HEADER,
            SECTION_COL_HEADER, 
            TYPE_COL_HEADER, 
            CREDITS_COL_HEADER, 
            START_COL_HEADER,
            END_COL_HEADER, 
            DAYS_COL_HEADER, 
            WHERE_COL_HEADER, 
            SCHEDULE_COL_HEADER, 
            INSTRUCTOR_COL_HEADER
        };
        
        rows.add(headers);

        // FOR LOOP FOR SECTIONS
        for (int i = 0; i < section.size(); i++) {
              //instructors
            JsonObject currentSection = (JsonObject) section.get(i);
            JsonArray instructorArray = (JsonArray) currentSection.get(INSTRUCTOR_COL_HEADER);
            String[] instructorNames = instructorArray.toArray(new String[0]);
            String instructors = String.join(", ", instructorNames);

            //course
            HashMap courseDetails = (HashMap) courses.get(
            currentSection.get(SUBJECTID_COL_HEADER) + " " + currentSection.get(NUM_COL_HEADER)
            );

            // Populate rows
            String[] lineArray  = {
                currentSection.get(CRN_COL_HEADER).toString(),
                subjects.get(currentSection.get(SUBJECTID_COL_HEADER)).toString(),
                (currentSection.get(SUBJECTID_COL_HEADER) + " " + currentSection.get(NUM_COL_HEADER)),
                courseDetails.get(DESCRIPTION_COL_HEADER).toString(),
                currentSection.get(SECTION_COL_HEADER).toString(),
                currentSection.get(TYPE_COL_HEADER).toString(),
                courseDetails.get(CREDITS_COL_HEADER).toString(),
                currentSection.get(START_COL_HEADER).toString(),
                currentSection.get(END_COL_HEADER).toString(),
                currentSection.get(DAYS_COL_HEADER).toString(),
                currentSection.get(WHERE_COL_HEADER).toString(),
                schedule.get(currentSection.get(TYPE_COL_HEADER).toString()).toString(),
                instructors
            };

            
            rows.add(lineArray );
        }

       
        StringWriter stringWriter = new StringWriter();

        CSVWriter csvWriter = new CSVWriter(stringWriter, '\t', '"', '\\', "\n");

        csvWriter.writeAll(rows);

        return stringWriter.toString();

       
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