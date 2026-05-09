package com.watercalculator.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle Consommation d'eau
 * Encapsule tous les usages : douche, vaisselle, arrosage, agriculture, autre
 */
public class Consumption {

    private int        id;
    private int        userId;
    private LocalDate  consumptionDate;

    private double showerLiters;
    private double dishwashingLiters;
    private double wateringLiters;
    private double agricultureLiters;
    private double otherLiters;

    private double whoNorm = 50.0;   // Norme OMS (litres/personne/jour)
    private String notes;
    private LocalDateTime createdAt;

    public Consumption() {
        this.consumptionDate = LocalDate.now();
    }

    public Consumption(int userId, double shower, double dishwashing,
                       double watering, double agriculture, double other) {
        this.userId            = userId;
        this.consumptionDate   = LocalDate.now();
        this.showerLiters      = shower;
        this.dishwashingLiters = dishwashing;
        this.wateringLiters    = watering;
        this.agricultureLiters = agriculture;
        this.otherLiters       = other;
    }

    /** Total calculé côté Java (cohérent avec la colonne GENERATED en SQL) */
    public double getTotalLiters() {
        return showerLiters + dishwashingLiters + wateringLiters
                + agricultureLiters + otherLiters;
    }

    /** Ratio par rapport à la norme OMS (1.0 = dans la norme) */
    public double getWhoRatio() {
        return whoNorm > 0 ? getTotalLiters() / whoNorm : 0;
    }

    /** Dépassement en litres (négatif = économie) */
    public double getWhoExcess() {
        return getTotalLiters() - whoNorm;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getUserId()                      { return userId; }
    public void setUserId(int v)                { this.userId = v; }

    public LocalDate getConsumptionDate()       { return consumptionDate; }
    public void setConsumptionDate(LocalDate v) { this.consumptionDate = v; }

    public double getShowerLiters()             { return showerLiters; }
    public void setShowerLiters(double v)       { this.showerLiters = v; }

    public double getDishwashingLiters()        { return dishwashingLiters; }
    public void setDishwashingLiters(double v)  { this.dishwashingLiters = v; }

    public double getWateringLiters()           { return wateringLiters; }
    public void setWateringLiters(double v)     { this.wateringLiters = v; }

    public double getAgricultureLiters()        { return agricultureLiters; }
    public void setAgricultureLiters(double v)  { this.agricultureLiters = v; }

    public double getOtherLiters()              { return otherLiters; }
    public void setOtherLiters(double v)        { this.otherLiters = v; }

    public double getWhoNorm()                  { return whoNorm; }
    public void setWhoNorm(double v)            { this.whoNorm = v; }

    public String getNotes()                    { return notes; }
    public void setNotes(String v)              { this.notes = v; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime v)   { this.createdAt = v; }

    @Override
    public String toString() {
        return String.format("Consumption{date=%s, total=%.1fL, whoRatio=%.1f%%}",
                consumptionDate, getTotalLiters(), getWhoRatio() * 100);
    }
}
