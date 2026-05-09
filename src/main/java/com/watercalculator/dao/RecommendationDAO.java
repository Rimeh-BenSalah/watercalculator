package com.watercalculator.dao;

import com.watercalculator.model.Recommendation;
import com.watercalculator.model.Recommendation.Severity;
import com.watercalculator.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Recommandations
 */
public class RecommendationDAO {

    /** Recommandations applicables selon catégorie et valeur dépassée */
    public List<Recommendation> getApplicable(String category, double value) throws SQLException {
        String sql = """
                SELECT * FROM recommendations
                WHERE category = ? AND threshold <= ?
                ORDER BY threshold DESC
                LIMIT 1
                """;
        List<Recommendation> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setDouble(2, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Toutes les recommandations générales */
    public List<Recommendation> getAllGeneral() throws SQLException {
        String sql = "SELECT * FROM recommendations WHERE category = 'general' ORDER BY threshold";
        List<Recommendation> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Recommendation mapRow(ResultSet rs) throws SQLException {
        Recommendation r = new Recommendation();
        r.setId(rs.getInt("id"));
        r.setCategory(rs.getString("category"));
        r.setThreshold(rs.getDouble("threshold"));
        r.setMessage(rs.getString("message"));
        r.setTip(rs.getString("tip"));
        r.setSeverity(Severity.valueOf(rs.getString("severity").toUpperCase()));
        return r;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
