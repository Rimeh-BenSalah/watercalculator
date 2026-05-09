package com.watercalculator.controller;

import com.watercalculator.dao.UserDAO;
import com.watercalculator.model.User;
import com.watercalculator.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Contrôleur — Écran d'inscription
 */
public class RegisterController {

    @FXML private TextField     fullNameField;
    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;
    @FXML private Label         successLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        cityCombo.getItems().addAll(
                "Tunis","Sfax","Sousse","Bizerte","Nabeul",
                "Kairouan","Monastir","Béja","Jendouba","Kef",
                "Sidi Bouzid","Kasserine","Gafsa","Gabès","Médenine",
                "Tataouine","Tozeur","Kebili","Mahdia","Siliana","Zaghouan","Ariana","Ben Arous","Manouba"
        );
        cityCombo.setValue("Tunis");
    }

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String city     = cityCombo.getValue();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        // Validations
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }
        if (password.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            showError("Adresse email invalide.");
            return;
        }

        try {
            if (userDAO.usernameExists(username)) {
                showError("Ce nom d'utilisateur est déjà pris.");
                return;
            }
            if (userDAO.emailExists(email)) {
                showError("Cet email est déjà utilisé.");
                return;
            }

            User newUser = new User(username, email, password, fullName, city);
            if (userDAO.register(newUser)) {
                successLabel.setText("Compte créé avec succès ! Redirection...");
                successLabel.setVisible(true);
                // Délai 1.5s puis retour au login
                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(e ->
                        SceneManager.switchTo("/fxml/login.fxml", "Water Calculator — Connexion"));
                pause.play();
            }
        } catch (Exception ex) {
            showError("Erreur lors de la création du compte.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        SceneManager.switchTo("/fxml/login.fxml", "Water Calculator — Connexion");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
