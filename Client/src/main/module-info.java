module com.prota.moneymindapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires spring.web;
    requires java.sql;

    opens com.prota.moneymindapp.common to javafx.base;
    opens com.prota.moneymindapp to javafx.fxml;
    exports com.prota.moneymindapp;
}
