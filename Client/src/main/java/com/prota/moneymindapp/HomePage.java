package com.prota.moneymindapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.moneymindapp.common.DataSession;
import com.prota.moneymindapp.common.SpesaCategoria;
import com.prota.moneymindapp.exceptions.NotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Prota Raffaele
 */
public class HomePage {
    
    private double status;
    @FXML private Label statusMonth;
    @FXML private Pane mainContainer;
    
    
    
    @FXML
    private void initialize(){ getStatus(); retrieveDataPieChart(); }
    
    @FXML
    private void redirectDiary() throws IOException { App.setRoot("diaryPage"); }
    
    @FXML
    private void redirectRecurrents() throws IOException { App.setRoot("recurrentsPage"); }
    
    @FXML
    private void logOut() throws IOException { DataSession.getInstance().resetSession(); App.setRoot("logInPage"); }
    
    
    
    private void retrieveDataPieChart(){
        if(status == 0.0){ 
            StackPane containerChart = new StackPane();
            containerChart.setLayoutX(50);
            containerChart.setLayoutY(75);
            Label label = new Label();
            label.setPrefWidth(200);
            label.setPrefHeight(150);
            label.setWrapText(true);
            label.setStyle("-fx-text-fill: #FFFFFF; -fx-alignment: center; -fx-wrap-text: true; -fx-text-alignment: center;");
            label.setText("Inserisci almeno una spesa per visualizzare il grafico.");
            Platform.runLater(() -> { containerChart.getChildren().add(label); mainContainer.getChildren().add(containerChart); });
        }
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                try{
                    HashMap<String, String> body = new HashMap<>();
                    body.put("query type", "getDataChart");
                    body.put("username", DataSession.getInstance().getUsername());
                    
                    HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                    JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

                    if (!jsonResponse.get("result").getAsBoolean()) {
                        if((Integer) response.get("statusCode") == 404){ throw new NotFoundException(); }
                        else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                    }

                    System.out.println("Response from server: " + response);
                    
                    List<SpesaCategoria> spesePerCategoria = parseJsonToSpese(jsonResponse.get("list").getAsString());
                    PieChart pieChart = new PieChart();
                    for (SpesaCategoria spesaCategoria : spesePerCategoria) {
                        PieChart.Data slice = new PieChart.Data(spesaCategoria.getCategoria(), spesaCategoria.getSpesa());
                        pieChart.getData().add(slice);
                    }
                    pieChart.setLegendVisible(true);
                    pieChart.setLabelsVisible(false);

                    StackPane containerChart = new StackPane(pieChart);
                    Platform.runLater(() -> mainContainer.getChildren().add(containerChart));
                    pieChart.setLayoutX(301);
                    pieChart.setLayoutY(0);
                    pieChart.setPrefWidth(300);
                    pieChart.setPrefHeight(300);
                }
                catch(Exception e){
                    System.out.println("Response from server: " + e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start(); 
    }
    
    
    
    private void getStatus(){
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                try{
                    HashMap<String, String> body = new HashMap<>();
                    body.put("query type", "getStatus");
                    body.put("username", DataSession.getInstance().getUsername());
                    body.put("anno", LocalDate.now().getYear() + "");
                    body.put("mese", LocalDate.now().getMonthValue() + "");

                    HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                    JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();
                    if (!jsonResponse.get("result").getAsBoolean()) {
                        if((Integer) response.get("statusCode") == 404){ throw new NotFoundException(); }
                        else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                    }

                    System.out.println("Response from server: " + jsonResponse.get("message").getAsString());
                    status = Double.parseDouble(jsonResponse.get("status").getAsString());
                    if(status != 0.0){
                        Platform.runLater(() -> {
                            statusMonth.setText("-" + jsonResponse.get("status").getAsString() + "\u20AC");
                            if(statusMonth.getWidth() > 190){ statusMonth.setStyle("-fx-font-size: 2.7em"); }
                        });
                    }
                    else{
                        Platform.runLater(() -> statusMonth.setText("-" + "\u20AC"));
                    }
                }
                catch(Exception e){
                    System.out.println("Response from server: " + e.getMessage());
                    status = 0.0;
                    Platform.runLater(() -> statusMonth.setText("-" + "\u20AC"));
                }
                return null;
            }
        };
        new Thread(task).start(); 
    }
    
    
    
    private List<SpesaCategoria> parseJsonToSpese(String json) {
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        List<SpesaCategoria> speseList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String categoria = jsonObject.get("categoria").getAsString();
            double spesa = jsonObject.get("spesa").getAsDouble();
            speseList.add(new SpesaCategoria(categoria, spesa));
        }

        return speseList;
    }
}
