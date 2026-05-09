package com.watercalculator.dao;

import com.watercalculator.model.Consumption;
import com.watercalculator.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Consommation — CRUD + statistiques
 */
public class ConsumptionDAO {

    // ── Ajouter ──────────────────────────────────────────────────────────────

    public boolean save(Consumption c) throws SQLException {
        String sql = """
                INSERT INTO consumptions
                (user_id, consumption_date, shower_liters, dishwashing_liters,
                 watering_liters, agriculture_liters, other_liters, who_norm, notes)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, c.getUserId());
            ps.setDate  (2, Date.valueOf(c.getConsumptionDate()));
            ps.setDouble(3, c.getShowerLiters());
            ps.setDouble(4, c.getDishwashingLiters());
            ps.setDouble(5, c.getWateringLiters());
            ps.setDouble(6, c.getAgricultureLiters());
            ps.setDouble(7, c.getOtherLiters());
            ps.setDouble(8, c.getWhoNorm());
            ps.setString(9, c.getNotes());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) c.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    // ── Historique d'un utilisateur ──────────────────────────────────────────

    public List<Consumption> getByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM consumptions WHERE user_id = ? ORDER BY consumption_date DESC";
        List<Consumption> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── 7 derniers jours ────────────────────────────────────────────────────

    public List<Consumption> getLast7Days(int userId) throws SQLException {
        String sql = """
                SELECT * FROM consumptions
                WHERE user_id = ?
                  AND consumption_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                ORDER BY consumption_date
                """;
        List<Consumption> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── Total du mois courant ────────────────────────────────────────────────

    public double getMonthlyTotal(int userId) throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(total_liters), 0)
                FROM consumptions
                WHERE user_id = ?
                  AND MONTH(consumption_date) = MONTH(CURDATE())
                  AND YEAR(consumption_date)  = YEAR(CURDATE())
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    // ── Moyenne journalière du mois ──────────────────────────────────────────

    public double getMonthlyAverage(int userId) throws SQLException {
        String sql = """
                SELECT COALESCE(AVG(total_liters), 0)
                FROM consumptions
                WHERE user_id = ?
                  AND MONTH(consumption_date) = MONTH(CURDATE())
                  AND YEAR(consumption_date)  = YEAR(CURDATE())
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    // ── Supprimer ────────────────────────────────────────────────────────────

    public boolean delete(int consumptionId) throws SQLException {
        String sql = "DELETE FROM consumptions WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, consumptionId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mapper ResultSet → Consumption ──────────────────────────────────────

    private Consumption mapRow(ResultSet rs) throws SQLException {
        Consumption c = new Consumption();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setConsumptionDate(rs.getDate("consumption_date").toLocalDate());
        c.setShowerLiters(rs.getDouble("shower_liters"));
        c.setDishwashingLiters(rs.getDouble("dishwashing_liters"));
        c.setWateringLiters(rs.getDouble("watering_liters"));
        c.setAgricultureLiters(rs.getDouble("agriculture_liters"));
        c.setOtherLiters(rs.getDouble("other_liters"));
        c.setWhoNorm(rs.getDouble("who_norm"));
        c.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        return c;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
