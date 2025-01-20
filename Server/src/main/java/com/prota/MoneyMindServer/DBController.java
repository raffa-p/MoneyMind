package com.prota.MoneyMindServer;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Prota Raffaele
 */
@Controller
@RequestMapping(path="/DB")
public class DBController {
    
    @PostMapping(path = "/init")
    public @ResponseBody ResponseEntity<String> connectDB(@RequestBody String parameters){
        try ( 
                Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/635650","root", "root");
            ){
            
            PreparedStatement stmt = co.prepareStatement("ALTER TABLE spesa auto_increment = 22;");
            stmt.executeUpdate();
            
            String sql = readFile("DBInsert.sql");

            if (sql != null && !sql.isEmpty()) {
                String[] queries = sql.split(";");
                for (String query : queries) {
                    if (!query.trim().isEmpty()) {
                        PreparedStatement pstmt = co.prepareStatement(query.trim());
                        pstmt.executeUpdate();
                        System.out.println("Query eseguita con successo: " + query.trim());
                        
                    }
                }
            }
        } 
        catch(IOException | SQLException i){
            JsonObject response = new JsonObject();
            response.addProperty("result", false);
            response.addProperty("message", "Error during query execution: " + i.getMessage());
            return ResponseEntity.status(500).body(response.toString());
        }
        JsonObject response = new JsonObject();
        response.addProperty("result", true);
        response.addProperty("message", "Query execution complete");
        return ResponseEntity.status(200).body(response.toString());
    }
    
    
    
    private static String readFile(String filePath) throws IOException {
        StringBuilder sqlContent = new StringBuilder();
        InputStream inputStream = DBController.class.getClassLoader().getResourceAsStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        
        if (inputStream == null) {
            throw new FileNotFoundException("File non trovato utilizzando il path: " + filePath);
        }
        
        String line;
        while ((line = br.readLine()) != null) {
            sqlContent.append(line).append("\n");
        }
        
        return sqlContent.toString();
    } 
}
