package com.watercalculator.controller;

import com.watercalculator.model.Consumption;
import com.watercalculator.service.WaterService;
import com.watercalculator.util.SceneManager;
import com.watercalculator.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Contrôleur — Historique des consommations
 */
public class HistoryController {

    @FXML private TableView<Consumption>             historyTable;
    @FXML private TableColumn<Consumption, String>   colDate;
    @FXML private TableColumn<Consumption, String>   colShower;
    @FXML private TableColumn<Consumption, String>   colDishwashing;
    @FXML private TableColumn<Consumption, String>   colWatering;
    @FXML private TableColumn<Consumption, String>   colAgriculture;
    @FXML private TableColumn<Consumption, String>   colTotal;
    @FXML private TableColumn<Consumption, String>   colWho;

    @FXML private Label totalEntriesLabel;
    @FXML private Label avgConsumptionLabel;
    @FXML private Label deleteStatusLabel;

    private final WaterService waterService = new WaterService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        deleteStatusLabel.setVisible(false);
        setupColumns();
        loadData();
    }

    // ── Configuration colonnes ────────────────────────────────────────────────

    private void setupColumns() {
        colDate       .setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getConsumptionDate().format(DATE_FMT)));
        colShower     .setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f L", c.getValue().getShowerLiters())));
        colDishwashing.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f L", c.getValue().getDishwashingLiters())));
        colWatering   .setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f L", c.getValue().getWateringLiters())));
        colAgriculture.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f L", c.getValue().getAgricultureLiters())));
        colTotal      .setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f L", c.getValue().getTotalLiters())));
        colWho        .setCellValueFactory(c -> {
            double ratio = c.getValue().getWhoRatio();
            String text  = ratio <= 1.0
                    ? String.format("✔ %.0f%%", ratio * 100)
                    : String.format("⚠ %.0f%%", ratio * 100);
            return new SimpleStringProperty(text);
        });

        // Couleur de ligne selon dépassement OMS
        historyTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Consumption item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-ok", "row-warn", "row-danger");
                if (item != null) {
                    double ratio = item.getWhoRatio();
                    if      (ratio <= 1.0) getStyleClass().add("row-ok");
                    else if (ratio <= 2.0) getStyleClass().add("row-warn");
                    else                   getStyleClass().add("row-danger");
                }
            }
        });
    }

    // ── Chargement données ────────────────────────────────────────────────────

    private void loadData() {
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        try {
            List<Consumption> list = waterService.getHistory(userId);
            historyTable.setItems(FXCollections.observableArrayList(list));

            totalEntriesLabel.setText("Total entrées : " + list.size());

            double avg = list.stream().mapToDouble(Consumption::getTotalLiters).average().orElse(0);
            avgConsumptionLabel.setText(String.format("Moyenne : %.1f L/jour", avg));

        } catch (Exception ex) {
            totalEntriesLabel.setText("Erreur de chargement.");
            ex.printStackTrace();
        }
    }

    // ── Supprimer entrée sélectionnée ────────────────────────────────────────

    @FXML
    private void handleDelete() {
        Consumption selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Sélectionnez une entrée à supprimer.", false);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la consommation du " + selected.getConsumptionDate().format(DATE_FMT) + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    if (waterService.deleteConsumption(selected.getId())) {
                        showStatus("✔ Entrée supprimée.", true);
                        loadData();
                    }
                } catch (Exception ex) {
                    showStatus("✘ Erreur lors de la suppression.", false);
                    ex.printStackTrace();
                }
            }
        });
    }

    @FXML private void handleRefresh() { loadData(); }

    // ── Navigation ─────────────────────────────────────────────────────────────

    @FXML private void goToDashboard() {
        SceneManager.switchTo("/fxml/dashboard.fxml", "Water Calculator — Tableau de bord");
    }

    @FXML private void goToCalculator() {
        SceneManager.switchTo("/fxml/calculator.fxml", "Water Calculator — Saisie");
    }

    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/fxml/login.fxml", "Water Calculator — Connexion");
    }

    private void showStatus(String msg, boolean ok) {
        deleteStatusLabel.setText(msg);
        deleteStatusLabel.getStyleClass().removeAll("success-label","error-label");
        deleteStatusLabel.getStyleClass().add(ok ? "success-label" : "error-label");
        deleteStatusLabel.setVisible(true);
        javafx.animation.PauseTransition p =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        p.setOnFinished(e -> deleteStatusLabel.setVisible(false));
        p.play();
    }
}
