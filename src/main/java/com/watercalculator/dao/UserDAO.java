package com.watercalculator.dao;

import com.watercalculator.model.User;
import com.watercalculator.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DAO Utilisateur — opérations CRUD + authentification
 */
public class UserDAO {

    // ── Créer un compte ──────────────────────────────────────────────────────

    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, full_name, city) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashPassword(user.getPassword()));
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getCity());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) user.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    // ── Connexion ────────────────────────────────────────────────────────────

    public Optional<User> login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    // ── Vérifier unicité ─────────────────────────────────────────────────────

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setCity(rs.getString("city"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }

    /**
     * Hash simple SHA-256.
     * En production, utiliser BCrypt (ex: org.mindrot.jbcrypt).
     */
    private String hashPassword(String plain) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return plain; // fallback
        }
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
