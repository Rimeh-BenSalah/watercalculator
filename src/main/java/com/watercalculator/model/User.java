package com.watercalculator.model;

import java.time.LocalDateTime;

/**
 * Modèle Utilisateur
 */
public class User {

    private int           id;
    private String        username;
    private String        email;
    private String        password;
    private String        fullName;
    private String        city;
    private LocalDateTime createdAt;

    public User() {}

    public User(String username, String email, String password, String fullName, String city) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.fullName  = fullName;
        this.city      = city;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getUsername()           { return username; }
    public void setUsername(String v)     { this.username = v; }

    public String getEmail()              { return email; }
    public void setEmail(String v)        { this.email = v; }

    public String getPassword()           { return password; }
    public void setPassword(String v)     { this.password = v; }

    public String getFullName()           { return fullName; }
    public void setFullName(String v)     { this.fullName = v; }

    public String getCity()               { return city; }
    public void setCity(String v)         { this.city = v; }

    public LocalDateTime getCreatedAt()   { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', city='" + city + "'}";
    }
}
