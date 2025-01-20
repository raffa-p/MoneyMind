package com.prota.moneymindapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import com.google.gson.Gson;
import java.util.HashMap;

public class HttpManager {
    private static final String connectionUrl = "http://127.0.0.1:8080/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static HashMap<String, Object> postRequestHandler(Map<String, String> parameters) throws IOException, InterruptedException {
        String queryType = parameters.get("query type");
        String json = gson.toJson(parameters);

        String path = getPath(queryType);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(connectionUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<String, Object> result = new HashMap<>();
        result.put("statusCode", response.statusCode());
        result.put("body", response.body());
        return result;
    }
    
    private static String getPath(String queryType){
        switch(queryType){
            case "login": return "user/login";                                      
            case "signup": return "user/signup";
            case "addCost": return "spese/add";                                  
            case "retrieveCategory": return "categoria/retrieveAll";                
            case "retrieveSpese": return "spese/retrieveAll";                       
            case "retrieveSpeseRicorrenti": return "spese/retrieveAllRecurrents";   
            case "removeCost": return "spese/remove";                               
            case "getStatus": return "spese/status";
            case "getDataChart": return "spese/retrieveSumCategory";                
            case "initDB": return "DB/init";                
        }
        return "";
    }
}

