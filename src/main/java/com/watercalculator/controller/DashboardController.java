package com.watercalculator.controller;

import com.watercalculator.model.Consumption;
import com.watercalculator.model.User;
import com.watercalculator.service.WaterService;
import com.watercalculator.util.SceneManager;
import com.watercalculator.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.util.List;

/**
 * Contrôleur — Tableau de bord principal
 */
public class DashboardController {

    // ── Statistiques cards ────────────────────────────────────────────────────
    @FXML private Label welcomeLabel;
    @FXML private Label todayLabel;
    @FXML private Label monthTotalLabel;
    @FXML private Label monthAvgLabel;
    @FXML private Label whoStatusLabel;

    // ── Graphique 7 jours ─────────────────────────────────────────────────────
    @FXML private BarChart<String, Number>       weekChart;
    @FXML private CategoryAxis                   weekChartX;
    @FXML private NumberAxis                     weekChartY;

    // ── Graphique répartition ─────────────────────────────────────────────────
    @FXML private PieChart pieChart;

    private final WaterService waterService = new WaterService();

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        welcomeLabel.setText("Bonjour, " + user.getFullName() + " 👋");

        try {
            loadStats(user.getId());
            loadWeekChart(user.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── Chargement stats ──────────────────────────────────────────────────────

    private void loadStats(int userId) throws Exception {
        List<Consumption> history = waterService.getHistory(userId);

        // Consommation aujourd'hui
        double todayTotal = history.stream()
                .filter(c -> c.getConsumptionDate().equals(java.time.LocalDate.now()))
                .mapToDouble(Consumption::getTotalLiters)
                .sum();
        todayLabel.setText(String.format("%.1f L", todayTotal));

        // Mois
        double monthTotal = waterService.getMonthlyTotal(userId);
        double monthAvg   = waterService.getMonthlyAverage(userId);
        monthTotalLabel.setText(String.format("%.0f L", monthTotal));
        monthAvgLabel.setText(String.format("%.1f L/jour", monthAvg));

        // Statut OMS
        if (monthAvg == 0) {
            whoStatusLabel.setText("—");
            whoStatusLabel.getStyleClass().add("who-ok");
        } else if (monthAvg <= WaterService.WHO_NORM_DOMESTIC) {
            whoStatusLabel.setText("✔ Dans la norme OMS");
            whoStatusLabel.getStyleClass().add("who-ok");
        } else {
            whoStatusLabel.setText(String.format("⚠ +%.0f L/j vs OMS", monthAvg - WaterService.WHO_NORM_DOMESTIC));
            whoStatusLabel.getStyleClass().add("who-warn");
        }

        // Pie chart — dernière saisie
        if (!history.isEmpty()) {
            Consumption last = history.get(0);
            pieChart.getData().clear();
            pieChart.getData().addAll(
                    new PieChart.Data("Douche "      + f(last.getShowerLiters())      + "L", last.getShowerLiters()),
                    new PieChart.Data("Vaisselle "   + f(last.getDishwashingLiters()) + "L", last.getDishwashingLiters()),
                    new PieChart.Data("Arrosage "    + f(last.getWateringLiters())    + "L", last.getWateringLiters()),
                    new PieChart.Data("Agriculture " + f(last.getAgricultureLiters()) + "L", last.getAgricultureLiters()),
                    new PieChart.Data("Autre "       + f(last.getOtherLiters())       + "L", last.getOtherLiters())
            );
            pieChart.getData().removeIf(d -> d.getPieValue() == 0);
        }
    }

    private void loadWeekChart(int userId) throws Exception {
        List<Consumption> week = waterService.getLast7Days(userId);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Consommation (L)");

        for (Consumption c : week) {
            String day = c.getConsumptionDate().getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.SHORT,
                            java.util.Locale.FRENCH);
            series.getData().add(new XYChart.Data<>(day, c.getTotalLiters()));
        }

        weekChart.getData().clear();
        weekChart.getData().add(series);
        weekChartY.setLabel("Litres");

        // Ligne de référence OMS
        weekChart.setTitle("Consommation 7 derniers jours (norme OMS : 50 L/j)");
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    @FXML private void goToCalculator() {
        SceneManager.switchTo("/fxml/calculator.fxml", "Water Calculator — Saisie");
    }

    @FXML private void goToHistory() {
        SceneManager.switchTo("/fxml/history.fxml", "Water Calculator — Historique");
    }

    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/fxml/login.fxml", "Water Calculator — Connexion");
    }

    private String f(double v) { return String.format("%.0f", v); }
}
