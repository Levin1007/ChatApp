package com.chatapp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX client.
 * Run this AFTER ServerMain is already running.
 *
 * Use Maven to launch:  mvn javafx:run
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/chatapp/client/views/login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Java Chat App");
        primaryStage.setScene(new Scene(root, 420, 380));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
