package com.prota.moneymindapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.moneymindapp.common.DataSession;
import com.prota.moneymindapp.common.SpesaDiary;
import com.prota.moneymindapp.common.SpesaRicorrente;
import com.prota.moneymindapp.exceptions.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Prota Raffaele
 */
public class RecurrentsPage {
    
    @FXML private TableView<SpesaRicorrente> table;
    @FXML private TableColumn<SpesaRicorrente, Integer> idColumn;
    @FXML private TableColumn<SpesaRicorrente, Double> costoColumn;
    @FXML private TableColumn<SpesaRicorrente, String> categoriaColumn;
    @FXML private TableColumn<SpesaRicorrente, String> prossimoPagamentoColumn;
    
    
    
    @FXML
    private void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        costoColumn.setCellValueFactory(new PropertyValueFactory<>("costo"));
        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        prossimoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("prossimoPagamento"));
        
        idColumn.setComparator((id1, id2) -> id2.compareTo(id1));
        table.getItems().addListener((ListChangeListener<SpesaRicorrente>) change -> {
            table.getSortOrder().clear();
            table.getSortOrder().add(idColumn);
            idColumn.setSortType(TableColumn.SortType.DESCENDING);
            table.sort();
        });
        
        updateTable();
    }
    
    @FXML
    private void redirectDiary() throws IOException { App.setRoot("diaryPage"); }
    
    @FXML
    private void redirectHome() throws IOException { App.setRoot("homePage"); }
    
    @FXML
    private void logOut() throws IOException { DataSession.getInstance().resetSession(); App.setRoot("logInPage"); }
    
    
    
    @FXML
    private void updateTable(){
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try{
                    HashMap<String, String> body = new HashMap<>();
                    body.put("query type", "retrieveSpeseRicorrenti");
                    body.put("username", DataSession.getInstance().getUsername());

                    HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                    JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

                    if (!jsonResponse.get("result").getAsBoolean()) {
                        if((Integer) response.get("statusCode") == 404){ throw new NotFoundException(); }
                        else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                    }

                    System.out.println("Response from server: " + response);
                    String listJsonString = jsonResponse.get("list").getAsString();
                    JsonArray array = JsonParser.parseString(listJsonString).getAsJsonArray();
                    List<SpesaRicorrente> options = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject lista = array.get(i).getAsJsonObject();
                        Long id = lista.get("ID").getAsLong();
                        String categoria = lista.get("categoria").getAsString();
                        double costo = lista.get("costo").getAsDouble();
                        String prossimoPagamento = lista.get("prossimoPagamento").getAsString();
                        options.add(new SpesaRicorrente(id, costo, categoria, prossimoPagamento));
                    }
                    
                    options.sort((spesa1, spesa2) -> Long.compare(spesa2.getID(), spesa1.getID()));

                    Platform.runLater(() -> table.setItems(FXCollections.observableArrayList(options)) );
                }
                catch(Exception e){
                    System.out.println("Response from server: " + e.getMessage());
                    table.setItems(FXCollections.observableArrayList());
                }
                return null;
            }
        };
        new Thread(task).start(); 
    }
    
    
    
    @FXML
    private void removeCost(){
        HashMap<String, String> body = new HashMap<>();
        body.put("query type", "removeCost");
        body.put("id", table.getSelectionModel().getSelectedItem().getID() + "");
        
        try{
            HashMap<String, Object> response = HttpManager.postRequestHandler(body);
            JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

            if (!jsonResponse.get("result").getAsBoolean()) {
                if((Integer) response.get("statusCode") == 400){ throw new NotFoundException(); }
                else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
            }
        }
        catch(Exception e){
            System.out.println("Response from server: " + e.getMessage());
        }
        finally{ updateTable(); }
    }
}
