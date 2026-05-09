package com.watercalculator.controller;

import com.watercalculator.dao.UserDAO;
import com.watercalculator.model.User;
import com.watercalculator.util.SceneManager;
import com.watercalculator.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * Contrôleur — Écran de connexion
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginButton;
    @FXML private Hyperlink     registerLink;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        // Appui Entrée → connexion
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Optional<User> user = userDAO.login(username, password);
            if (user.isPresent()) {
                SessionManager.getInstance().setCurrentUser(user.get());
                SceneManager.switchTo("/fxml/dashboard.fxml", "Water Calculator — Tableau de bord");
            } else {
                showError("Identifiant ou mot de passe incorrect.");
                passwordField.clear();
            }
        } catch (Exception ex) {
            showError("Erreur de connexion à la base de données.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink() {
        SceneManager.switchTo("/fxml/register.fxml", "Water Calculator — Créer un compte");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
