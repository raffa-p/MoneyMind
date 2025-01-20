package com.prota.moneymindapp;


import com.prota.moneymindapp.common.DataSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.moneymindapp.common.Categoria;
import com.prota.moneymindapp.exceptions.BadCredentialsException;
import com.prota.moneymindapp.exceptions.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author Prota Raffaele
 */
public class AddCostPage {
       
    @FXML private Button confirmAddCost; 
    @FXML private ChoiceBox categoryInput;
    @FXML private ChoiceBox choiceNumber;
    @FXML private ChoiceBox choicePeriod;
    @FXML private TextField costoLabel;
    @FXML private CheckBox flagRecurrent;
    @FXML private ProgressIndicator loadingSpinner;
    @FXML private Pane containerAdd;
    @FXML private HBox errorContainer;   
    @FXML private TableView<Categoria> tableCategory;
    @FXML private TableColumn<Categoria, Double> nomeColumn;
    @FXML private TableColumn<Categoria, String> descrizioneColumn;
    
    
    
    /**
     * Create a new window for data insert
     * Then send POST request to save a new cost
     * 
     */
    @FXML
    private void initialize(){
        costoLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                costoLabel.setText(oldValue); 
            }
        });
        
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        descrizioneColumn.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        descrizioneColumn.setCellFactory(tc -> {
            return new TableCell<Categoria, String>() {
                private final Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(descrizioneColumn.widthProperty());
                        setGraphic(text);
                    }
                }
            };
        });
        
        Task<HashMap<String, List>> task = new Task<>() {
            @Override
            protected HashMap<String, List> call() throws Exception {
                try{
                    HashMap<String, String> body = new HashMap<>();
                    body.put("query type", "retrieveCategory");

                    HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                    JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

                    if (!jsonResponse.get("result").getAsBoolean()) {
                        if((Integer) response.get("statusCode") == 404){ throw new NotFoundException(); }
                        else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                    }

                    System.out.println("Response from server: " + response);

                    String listJsonString = jsonResponse.get("list").getAsString();
                    JsonArray categoryArray = JsonParser.parseString(listJsonString).getAsJsonArray();
                    List<String> options = new ArrayList<>();
                    List<Categoria> optionsTable = new ArrayList<>();
                    for (int i = 0; i < categoryArray.size(); i++) {
                        JsonObject category = categoryArray.get(i).getAsJsonObject();
                        String categoryName = category.get("nome").getAsString();
                        String categoryDesc = category.get("descrizione") == null? "" : category.get("descrizione").getAsString();
                        options.add(categoryName);
                        optionsTable.add(new Categoria(categoryName, categoryDesc));
                    }
                    Platform.runLater(() -> {
                        categoryInput.getItems().clear();
                        categoryInput.getItems().addAll(FXCollections.observableArrayList(options));
                        tableCategory.setItems(FXCollections.observableArrayList(optionsTable));
                            });
                }
                catch(Exception e){
                    categoryInput.getItems().clear();
                    categoryInput.getItems().add("Errore di caricamento");
                    System.out.println("Response from server: " + e.getMessage());
                    tableCategory.setItems(FXCollections.observableArrayList());
                }
                return null;
            }
        };
        new Thread(task).start(); 
        
        for(int i = 1; i < 31; i++){
            choiceNumber.getItems().add(i);
        }
        choicePeriod.getItems().add("Giorni");
        choicePeriod.getItems().add("Mesi");
        choicePeriod.getItems().add("Anni");
        
        flagRecurrent.selectedProperty().addListener((observable, oldValue, newValue) -> {
            choiceNumber.setDisable(!newValue);
            choicePeriod.setDisable(!newValue);
        });
    }
    
    
    
    @FXML
    private void tryConfirmAddCost(){
            if(loadingSpinner == null){
                loadingSpinner = new ProgressIndicator();
                loadingSpinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                loadingSpinner.getStyleClass().add("progress-bar-cost");
                loadingSpinner.setLayoutX(208);
                loadingSpinner.setLayoutY(156);
                loadingSpinner.setPrefWidth(31);
                loadingSpinner.setPrefHeight(30);
                Platform.runLater(() -> {containerAdd.getChildren().add(loadingSpinner);});
            }
            loadingSpinner.setVisible(true);
            confirmAddCost.setDisable(true);
            
            Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try{
                    if(flagRecurrent.isIndeterminate() || (flagRecurrent.isSelected() && (choiceNumber.getValue() == null || choicePeriod.getValue() == null)) || costoLabel.getText().equals("") || Double.parseDouble(costoLabel.getText()) < 0 || categoryInput.getValue() == null){ 
                        throw new BadCredentialsException(ErrorType.VOID_FIELDS); 
                    } 
                    else{    
                        HashMap<String, String> body = new HashMap<>();
                        body.put("category", categoryInput.getValue().toString());
                        body.put("costo", costoLabel.getText());
                        body.put("flagRecurrent", flagRecurrent.isSelected()? "yes" : "no");
                        body.put("username", DataSession.getInstance().getUsername());
                        body.put("query type", "addCost");
                        body.put("ricorrenzaNumero", (flagRecurrent.isSelected() && choiceNumber.getValue() != null)? choiceNumber.getValue().toString() : "");
                        body.put("ricorrenzaPeriodo", (flagRecurrent.isSelected() && choicePeriod.getValue() != null)? choicePeriod.getValue().toString() : "");
                
                        HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                        JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

                        if (!jsonResponse.get("result").getAsBoolean()) {
                            if((Integer) response.get("statusCode") == 500){ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                        }
                    
                        System.out.println("Response from server: " + jsonResponse.get("message").getAsString());

                        Platform.runLater(() -> {
                            loadingSpinner.setVisible(false);
                            confirmAddCost.setText("Inserimento completato");
                            confirmAddCost.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: green; -fx-pref-width: 150px; -fx-layout-x: 94px;");
                        });
                    }
                }
                catch(IOException | InterruptedException i){
                    System.out.println("Response from server: " + i.getMessage());
                    confirmAddCost.setDisable(false);
                    loadingSpinner.setVisible(false);
                }
                catch(Exception e){
                    System.out.println("Response from server: " + e.getMessage());
                    if(errorContainer != null){
                        containerAdd.getChildren().remove(errorContainer);
                    }
                    errorContainer = new HBox();
                    errorContainer.setId("errorPopupBox");
                    errorContainer.setLayoutX(0);
                    errorContainer.setLayoutY(331);
                    errorContainer.setPrefHeight(55);
                    errorContainer.setPrefWidth(350);
                    Label error = new Label(e.getMessage());
                    error.setId("errorPopup");
                    
                    Platform.runLater(() -> {
                        errorContainer.getChildren().add(error);
                        containerAdd.getChildren().add(errorContainer);
                        confirmAddCost.setDisable(false);
                        loadingSpinner.setVisible(false);
                    });

                    Timer timer = new Timer();
                    timer.schedule(new java.util.TimerTask(){
                        @Override
                        public void run(){
                            Platform.runLater(() -> {
                                if (errorContainer != null){ containerAdd.getChildren().remove(errorContainer); }
                            });
                        }
                    }, 5000);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
