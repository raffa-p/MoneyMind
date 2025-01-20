package com.prota.moneymindapp;

import com.prota.moneymindapp.exceptions.BadCredentialsException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SignUpPage {
    
    @FXML private TextField usernameField; 
    @FXML private PasswordField passwordField; 
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Button loginButton;
    @FXML private HBox errorContainer;
    @FXML private ProgressIndicator loadingSpinner;
    @FXML private Pane containerSignup;

    
    /**
     * Redirect to sign up page
     * 
     * @throws IOException 
     */
    @FXML
    private void switchToLogin() throws IOException { App.setRoot("logInPage"); }

    
    
    /**
     * Validates user inputs
     * Then sends request to server to confirm user inputs
     * In positive case redirect to home page
     * else display a message error for wrong inputs
     * 
     * @throws IOException 
     */
    @FXML
    private void trySignUp() throws IOException {
        signUpButton.setDisable(true);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        confirmPasswordField.setDisable(true);
        if(loadingSpinner == null){
            loadingSpinner = new ProgressIndicator();
            loadingSpinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            loadingSpinner.setLayoutX(126);
            loadingSpinner.setLayoutY(235);
            loadingSpinner.getStyleClass().add("progress-bar");
            Platform.runLater(() -> {containerSignup.getChildren().add(loadingSpinner);});
        }
        loadingSpinner.setVisible(true);
        
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try{
                    if(usernameField.getText().equals("")){ throw new BadCredentialsException(ErrorType.BAD_USERNAME); } //username mancante
                    else if(passwordField.getText().equals("") || confirmPasswordField.getText().equals("")){ throw new BadCredentialsException(ErrorType.BAD_PASSWORD); } //password mancante
                    else if(!confirmPasswordField.getText().equals(passwordField.getText())){ throw new BadCredentialsException(ErrorType.BAD_CONFIRM_PASSWORD); } //password diverse
                    else{    
                        HashMap<String, String> body = new HashMap<>();
                        body.put("username", usernameField.getText());
                        body.put("password", passwordField.getText());
                        body.put("query type", "signup");
                
                        HashMap<String, Object> response = HttpManager.postRequestHandler(body);
                        JsonObject jsonResponse = JsonParser.parseString((String) response.get("body")).getAsJsonObject();

                        if (!jsonResponse.get("result").getAsBoolean()) {
                            if((Integer) response.get("statusCode") == 400){ throw new BadCredentialsException(ErrorType.BAD_USERNAME); }
                            else{ throw new Exception(ErrorType.NOT_RECOGNIZED.toString()); }
                        }
                        
                        System.out.println("Response from server: " + response);

                        Platform.runLater(() -> {
                            try {
                                App.setRoot("logInPage");
                            } catch (IOException e) {
                                System.out.println("ERRORE REINDIRIZZAMENTO");
                            }
                        });
                    }
                }
                catch(BadCredentialsException e){
                    System.out.println("Error: " + e.getMessage());
                    if(errorContainer != null){ 
                        containerSignup.getChildren().remove(errorContainer);
                    }
                    errorContainer = new HBox();
                    errorContainer.setId("errorPopupBox");
                    errorContainer.setLayoutX(0);
                    errorContainer.setLayoutY(0);
                    errorContainer.setPrefHeight(35);
                    errorContainer.setPrefWidth(350);
                    Label error = new Label(e.getMessage());
                    error.setId("errorPopup");
                    
                    Platform.runLater(() -> {
                        errorContainer.getChildren().add(error);
                        containerSignup.getChildren().add(errorContainer);
                    });
                    loginButton.setDisable(false);
                    signUpButton.setDisable(false);
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);
                    confirmPasswordField.setDisable(true);
                    loadingSpinner.setVisible(false);

                    Timer timer = new Timer();
                    timer.schedule(new java.util.TimerTask(){
                        @Override
                        public void run(){
                            Platform.runLater(() -> {
                                if (errorContainer != null){ containerSignup.getChildren().remove(errorContainer); }
                            });
                        }
                    }, 5000);
                }
                catch(IOException i){
                    System.out.println("Fail connection to server");
                    loginButton.setDisable(false);
                    signUpButton.setDisable(false);
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);
                    confirmPasswordField.setDisable(true);
                    loadingSpinner.setVisible(false);
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
}