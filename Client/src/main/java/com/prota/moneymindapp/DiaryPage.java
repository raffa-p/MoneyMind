package com.prota.moneymindapp;


import com.prota.moneymindapp.common.DataSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.moneymindapp.common.SpesaDiary;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Prota Raffaele
 */
public class DiaryPage {
    
    @FXML private TableView<SpesaDiary> table;
    @FXML private TableColumn<SpesaDiary, Integer> idColumn;
    @FXML private TableColumn<SpesaDiary, Double> costoColumn;
    @FXML private TableColumn<SpesaDiary, String> categoriaColumn;
    @FXML private TableColumn<SpesaDiary, Boolean> ricorrenteColumn;
    
    
    
    @FXML
    private void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        costoColumn.setCellValueFactory(new PropertyValueFactory<>("costo"));
        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        ricorrenteColumn.setCellValueFactory(new PropertyValueFactory<>("ricorrente"));
        
        ricorrenteColumn.setCellFactory(column -> new TableCell<SpesaDiary, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } 
                else {
                    String imagePath = item ? "/images/accept.png" : "/images/remove.png";
                    try {
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(20);
                        imageView.setFitHeight(20);

                        StackPane stackPane = new StackPane(imageView);
                        setGraphic(stackPane);
                    } catch (NullPointerException e) {
                        System.out.println("Image not found");
                        setGraphic(null);
                    }
                }
            }
        });
        
        updateTable();
    }
    
    
    
    @FXML
    private void redirectHome() throws IOException { App.setRoot("homePage"); }
    
    @FXML
    private void redirectRecurrents() throws IOException { App.setRoot("recurrentsPage"); }
    
    @FXML
    private void logOut() throws IOException { DataSession.getInstance().resetSession(); App.setRoot("logInPage"); }
    
    
    
    /**
     * Create new window for data insert
     * Then send POST request to save a new cost
     * 
     */
    @FXML
    private void tryAddCost(){
        try{
            openNewWindow("addCostPage");
            updateTable();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("ERRORE CREAZIONE NUOVA FINESTRA");
        }
    }
    
    
    
    @FXML
    private void updateTable(){
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try{
                    HashMap<String, String> body = new HashMap<>();
                    body.put("query type", "retrieveSpese");
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
                    List<SpesaDiary> options = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject lista = array.get(i).getAsJsonObject();
                        Long id = lista.get("ID").getAsLong();
                        String categoria = lista.get("categoria").getAsString();
                        double costo = lista.get("costo").getAsDouble();
                        boolean ricorrente = lista.get("ricorrente").getAsBoolean();
                        options.add(new SpesaDiary(id, costo, categoria, ricorrente));
                    }
                    
                    options.sort((spesa1, spesa2) -> Long.compare(spesa2.getID(), spesa1.getID()));

                    Platform.runLater(() -> table.setItems(FXCollections.observableArrayList(options)));
                }
                catch(Exception e){
                    System.out.println("Response from server: " + e.getMessage());
                    Platform.runLater(() ->table.setItems(FXCollections.observableArrayList()));
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
    
    
    
    private void openNewWindow(String layout) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(layout + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
        Stage newPage = new Stage();
        newPage.setScene(scene);
        newPage.setResizable(false);

        newPage.setTitle("Nuova spesa");
        newPage.initModality(Modality.APPLICATION_MODAL);
        newPage.show();
    }
}
