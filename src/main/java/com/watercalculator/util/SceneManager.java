package com.watercalculator.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestion centralisée des scènes JavaFX
 */
public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Charge un fichier FXML et l'affiche dans la scène principale.
     *
     * @param fxmlPath chemin relatif depuis /resources, ex: "/fxml/login.fxml"
     * @param title    titre de la fenêtre
     */
    public static void switchTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                scene.getStylesheets().add(
                        SceneManager.class.getResource("/css/style.css").toExternalForm());
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            primaryStage.setTitle(title);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne le contrôleur d'un FXML chargé dynamiquement (utile pour passer des données).
     */
    public static <T> T loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }
}
