package com.watercalculator.controller;

import com.watercalculator.model.Consumption;
import com.watercalculator.model.Recommendation;
import com.watercalculator.service.WaterService;
import com.watercalculator.util.SceneManager;
import com.watercalculator.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Contrôleur — Écran de saisie et calcul
 */
public class CalculatorController {

    // ── Champs de saisie ──────────────────────────────────────────────────────
    @FXML private Spinner<Double> showerMinutesSpinner;
    @FXML private Spinner<Double> dishwashingLitersSpinner;
    @FXML private Spinner<Double> wateringAreaSpinner;
    @FXML private Spinner<Double> agricultureHaSpinner;
    @FXML private Spinner<Double> otherLitersSpinner;
    @FXML private TextArea        notesArea;

    // ── Résultats ─────────────────────────────────────────────────────────────
    @FXML private Label showerResultLabel;
    @FXML private Label dishwashingResultLabel;
    @FXML private Label wateringResultLabel;
    @FXML private Label agricultureResultLabel;
    @FXML private Label totalResultLabel;
    @FXML private Label whoCompareLabel;
    @FXML private VBox  recommendationsBox;

    // ── Boutons ───────────────────────────────────────────────────────────────
    @FXML private Button calculateButton;
    @FXML private Button saveButton;
    @FXML private Label  saveStatusLabel;

    private final WaterService waterService = new WaterService();
    private Consumption lastCalculated = null;

    @FXML
    public void initialize() {
        saveButton.setDisable(true);
        saveStatusLabel.setVisible(false);

        // Spinners avec valeurs par défaut
        showerMinutesSpinner    .setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 120, 5,  0.5));
        dishwashingLitersSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 200, 10, 1));
        wateringAreaSpinner     .setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, 20, 5));
        agricultureHaSpinner    .setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000,  0, 0.1));
        otherLitersSpinner      .setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 500,   5, 1));

        // Édition manuelle dans les spinners
        for (Spinner<?> s : List.of(showerMinutesSpinner, dishwashingLitersSpinner,
                wateringAreaSpinner, agricultureHaSpinner, otherLitersSpinner)) {
            s.setEditable(true);
        }
    }

    // ── Calcul ────────────────────────────────────────────────────────────────

    @FXML
    private void handleCalculate() {
        double showerMin    = showerMinutesSpinner.getValue();
        double dishwashing  = dishwashingLitersSpinner.getValue();
        double wateringArea = wateringAreaSpinner.getValue();
        double agriHa       = agricultureHaSpinner.getValue();
        double other        = otherLitersSpinner.getValue();

        double showerL      = WaterService.showerMinutesToLiters(showerMin);
        double wateringL    = WaterService.wateringAreaToLiters(wateringArea);
        double agriL        = WaterService.agricultureHaToLiters(agriHa);

        double total = showerL + dishwashing + wateringL + agriL + other;
        double excess = total - WaterService.WHO_NORM_DOMESTIC;

        // Affichage résultats
        showerResultLabel.setText(String.format("%.1f L (%.0f min × 8 L/min)", showerL, showerMin));
        dishwashingResultLabel.setText(String.format("%.1f L", dishwashing));
        wateringResultLabel.setText(String.format("%.1f L (%.0f m² × 5 L/m²)", wateringL, wateringArea));
        agricultureResultLabel.setText(agriHa > 0
                ? String.format("%.0f L (%.2f ha)", agriL, agriHa)
                : "0 L");
        totalResultLabel.setText(String.format("%.1f L", total));

        if (excess <= 0) {
            whoCompareLabel.setText(String.format("✔ Dans la norme OMS (%.0fL/j) — économie : %.1f L", WaterService.WHO_NORM_DOMESTIC, -excess));
            whoCompareLabel.getStyleClass().removeAll("who-warn","who-danger");
            whoCompareLabel.getStyleClass().add("who-ok");
        } else if (excess <= 50) {
            whoCompareLabel.setText(String.format("⚠ Dépassement OMS : +%.1f L/j", excess));
            whoCompareLabel.getStyleClass().removeAll("who-ok","who-danger");
            whoCompareLabel.getStyleClass().add("who-warn");
        } else {
            whoCompareLabel.setText(String.format("✘ Dépassement critique : +%.1f L/j !", excess));
            whoCompareLabel.getStyleClass().removeAll("who-ok","who-warn");
            whoCompareLabel.getStyleClass().add("who-danger");
        }

        // Préparer l'objet Consumption
        lastCalculated = new Consumption(
                SessionManager.getInstance().getCurrentUser().getId(),
                showerL, dishwashing, wateringL, agriL, other
        );
        lastCalculated.setNotes(notesArea.getText());

        // Générer recommandations
        try {
            List<Recommendation> recos = waterService.generateRecommendations(lastCalculated);
            showRecommendations(recos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        saveButton.setDisable(false);
    }

    // ── Sauvegarde ────────────────────────────────────────────────────────────

    @FXML
    private void handleSave() {
        if (lastCalculated == null) return;
        try {
            if (waterService.saveConsumption(lastCalculated)) {
                saveStatusLabel.setText("✔ Consommation enregistrée avec succès !");
                saveStatusLabel.getStyleClass().removeAll("error-label");
                saveStatusLabel.getStyleClass().add("success-label");
                saveStatusLabel.setVisible(true);
                saveButton.setDisable(true);

                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(e -> saveStatusLabel.setVisible(false));
                pause.play();
            }
        } catch (Exception ex) {
            saveStatusLabel.setText("✘ Erreur lors de l'enregistrement.");
            saveStatusLabel.getStyleClass().add("error-label");
            saveStatusLabel.setVisible(true);
            ex.printStackTrace();
        }
    }

    // ── Affichage des recommandations ─────────────────────────────────────────

    private void showRecommendations(List<Recommendation> recos) {
        recommendationsBox.getChildren().clear();

        if (recos.isEmpty()) {
            Label noReco = new Label("✔ Aucune alerte — consommation optimale !");
            noReco.getStyleClass().add("reco-ok");
            recommendationsBox.getChildren().add(noReco);
            return;
        }

        for (Recommendation r : recos) {
            VBox card = new VBox(4);
            card.setPadding(new Insets(10));
            String styleClass = switch (r.getSeverity()) {
                case INFO    -> "reco-info";
                case WARNING -> "reco-warning";
                case DANGER  -> "reco-danger";
            };
            card.getStyleClass().add(styleClass);

            Label msg = new Label("💧 " + r.getMessage());
            msg.getStyleClass().add("reco-message");
            msg.setWrapText(true);

            Label tip = new Label("💡 " + r.getTip());
            tip.getStyleClass().add("reco-tip");
            tip.setWrapText(true);

            card.getChildren().addAll(msg, tip);
            recommendationsBox.getChildren().add(card);
        }
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    @FXML private void goToDashboard() {
        SceneManager.switchTo("/fxml/dashboard.fxml", "Water Calculator — Tableau de bord");
    }

    @FXML private void goToHistory() {
        SceneManager.switchTo("/fxml/history.fxml", "Water Calculator — Historique");
    }

    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/fxml/login.fxml", "Water Calculator — Connexion");
    }
}
