package com.prota.moneymindapp;

import com.prota.moneymindapp.exceptions.BadCredentialsException;
import com.prota.moneymindapp.common.DataSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import javafx.application.Platform;


import java.util.HashMap;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class LogInPage {
    
    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    @FXML private Pane containerLogin;
    @FXML private HBox errorContainer;
    @FXML private ProgressIndicator loadingSpinner;

    
    
    @FXML
    private void switchToRegister() throws IOException { App.setRoot("signUpPage"); }

    
    
    /**
     * Validates user inputs
     * Then sends request to server to confirm user inputs
     * In positive case redirect to home page
     * else display a message error for wrong inputs
     * 
     * @throws IOException 
     */
    @FXML
    private void tryLogIn() throws IOException {
        Platform.runLater(() -> {
            loginButton.setDisable(true);
            signUpButton.setDisable(true);
            usernameField.setDisable(true);
            passwordField.setDisable(true);
        });

        if (loadingSpinner == null) {
            loadingSpinner = new ProgressIndicator();
            loadingSpinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            loadingSpinner.getStyleClass().add("progress-bar");
            Platform.runLater(() -> {
                containerLogin.getChildren().add(loadingSpinner);
                loadingSpinner.setVisible(true);
            });
        } 
        else { Platform.runLater(() -> loadingSpinner.setVisible(true)); }
      
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try{
                    if(usernameField.getText().equals("")){ throw new BadCredentialsException(ErrorType.BAD_USERNAME); } //username mancante
                    else if(passwordField.getText().equals("")){ throw new BadCredentialsException(ErrorType.BAD_PASSWORD); } //password mancante
                    else{    
                        HashMap<String, String> body = new HashMap<>();
                        body.put("username", usernameField.getText());
                        body.put("password", passwordField.getText());
                        body.put("query type", "login");
                
                        HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                        JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

                        if (!jsonResponse.get("result").getAsBoolean()) {
                            if((Integer) response.get("statusCode") == 404){ throw new BadCredentialsException(ErrorType.BAD_USERNAME); }
                            else if((Integer) response.get("statusCode") == 400){ throw new BadCredentialsException(ErrorType.BAD_PASSWORD); }
                            else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                        }
                        
                        System.out.println("Response from server: " + response);

                        DataSession.getInstance().setUsername(usernameField.getText());
                        Platform.runLater(() -> {
                            try {
                                App.setRoot("homePage");
                            } catch (IOException e) {
                                System.out.println("ERRORE REINDIRIZZAMENTO");
                            }
                        });
                        
                    }
                }
                catch(BadCredentialsException e){
                    System.out.println("Error: " + e.getMessage());
                    Platform.runLater(() -> {
                        if (errorContainer != null && containerLogin.getChildren().contains(errorContainer)) {
                            containerLogin.getChildren().remove(errorContainer);
                        }
                        errorContainer = new HBox();
                        errorContainer.setId("errorPopupBox");
                        errorContainer.setPrefHeight(35);
                        errorContainer.setPrefWidth(350);
                        Label error = new Label(e.getMessage());
                        error.setId("errorPopup");

                        errorContainer.getChildren().add(error);
                        containerLogin.getChildren().add(errorContainer);
                    });
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        signUpButton.setDisable(false);
                        usernameField.setDisable(false);
                        passwordField.setDisable(false);
                        loadingSpinner.setVisible(false);
                    });
                    PauseTransition pause = new PauseTransition(Duration.seconds(5));
                    pause.setOnFinished(event -> Platform.runLater(() -> {
                        if (errorContainer != null) {
                            containerLogin.getChildren().remove(errorContainer);
                        }
                    }));
                    pause.play();
                }
                catch(IOException i){
                    System.out.println("Error: " + i.getMessage());
                    Platform.runLater(() -> {
                        if (errorContainer != null && containerLogin.getChildren().contains(errorContainer)) {
                            containerLogin.getChildren().remove(errorContainer);
                        }
                        errorContainer = new HBox();
                        errorContainer.setId("errorPopupBox");
                        errorContainer.setPrefHeight(35);
                        errorContainer.setPrefWidth(350);
                        Label error = new Label("Errore di connessione");
                        error.setId("errorPopup");

                        errorContainer.getChildren().add(error);
                        containerLogin.getChildren().add(errorContainer);
                    });
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        signUpButton.setDisable(false);
                        usernameField.setDisable(false);
                        passwordField.setDisable(false);
                        loadingSpinner.setVisible(false);
                    });
                    PauseTransition pause = new PauseTransition(Duration.seconds(5));
                    pause.setOnFinished(event -> Platform.runLater(() -> {
                        if (errorContainer != null) {
                            containerLogin.getChildren().remove(errorContainer);
                        }
                    }));
                    pause.play();
                }
                catch(InterruptedException ie){
                    System.out.println("Error: " + ie.getMessage());
                }
                catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    
    
    
    
    
    
    /**
     * Send request to server to initialize database
     */
    @FXML
    private void DBInit(){
        try{
            HashMap<String, String> body = new HashMap<>();
            body.put("query type", "initDB");

            HashMap<String, Object> response = HttpManager.postRequestHandler(body);
            JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

            if (!jsonResponse.get("result").getAsBoolean()){ 
                throw new Exception("Fail Database initialization." + jsonResponse.get("message").getAsString()); 
            }
            System.out.println(jsonResponse.get("message").getAsString());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
