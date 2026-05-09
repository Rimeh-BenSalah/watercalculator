package com.watercalculator.service;

import com.watercalculator.dao.ConsumptionDAO;
import com.watercalculator.dao.RecommendationDAO;
import com.watercalculator.model.Consumption;
import com.watercalculator.model.Recommendation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service métier Water Calculator
 * Calcule les consommations et génère les recommandations personnalisées.
 */
public class WaterService {

    private final ConsumptionDAO    consumptionDAO    = new ConsumptionDAO();
    private final RecommendationDAO recommendationDAO = new RecommendationDAO();

    // ── Norme OMS (litres / personne / jour, usage domestique) ───────────────
    public static final double WHO_NORM_DOMESTIC    = 50.0;
    public static final double WHO_NORM_SHOWER      = 40.0;   // ~5 min
    public static final double WHO_NORM_DISHWASHING = 15.0;   // bac fermé
    public static final double WHO_NORM_WATERING    = 30.0;   // jardin moyen

    // ── Enregistrer une consommation ─────────────────────────────────────────

    public boolean saveConsumption(Consumption c) throws SQLException {
        c.setWhoNorm(WHO_NORM_DOMESTIC);
        return consumptionDAO.save(c);
    }

    // ── Générer les recommandations pour une consommation donnée ─────────────

    public List<Recommendation> generateRecommendations(Consumption c) throws SQLException {
        List<Recommendation> result = new ArrayList<>();

        addReco(result, "shower",      c.getShowerLiters());
        addReco(result, "dishwashing", c.getDishwashingLiters());
        addReco(result, "watering",    c.getWateringLiters());
        addReco(result, "agriculture", c.getAgricultureLiters());
        addReco(result, "general",     c.getTotalLiters());

        return result;
    }

    private void addReco(List<Recommendation> list, String category, double value)
            throws SQLException {
        List<Recommendation> found = recommendationDAO.getApplicable(category, value);
        list.addAll(found);
    }

    // ── Historique ───────────────────────────────────────────────────────────

    public List<Consumption> getHistory(int userId) throws SQLException {
        return consumptionDAO.getByUser(userId);
    }

    public List<Consumption> getLast7Days(int userId) throws SQLException {
        return consumptionDAO.getLast7Days(userId);
    }

    // ── Statistiques mensuelles ───────────────────────────────────────────────

    public double getMonthlyTotal(int userId) throws SQLException {
        return consumptionDAO.getMonthlyTotal(userId);
    }

    public double getMonthlyAverage(int userId) throws SQLException {
        return consumptionDAO.getMonthlyAverage(userId);
    }

    // ── Supprimer ────────────────────────────────────────────────────────────

    public boolean deleteConsumption(int id) throws SQLException {
        return consumptionDAO.delete(id);
    }

    // ── Calcul rapide (sans BD) ───────────────────────────────────────────────

    /**
     * Durée de douche → litres (débit moyen 8 L/min)
     */
    public static double showerMinutesToLiters(double minutes) {
        return minutes * 8.0;
    }

    /**
     * Arrosage : surface (m²) × 5 L/m²
     */
    public static double wateringAreaToLiters(double surfaceM2) {
        return surfaceM2 * 5.0;
    }

    /**
     * Agriculture : surface (ha) × 50 000 L/ha (irrigation standard)
     */
    public static double agricultureHaToLiters(double hectares) {
        return hectares * 50_000.0;
    }
}
