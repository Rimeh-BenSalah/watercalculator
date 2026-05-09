package com.watercalculator;

import com.watercalculator.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée — Water Calculator Application
 * ThinkGreen Project
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(640);
        primaryStage.setResizable(true);

        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchTo("/fxml/login.fxml", "Water Calculator — Connexion");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
