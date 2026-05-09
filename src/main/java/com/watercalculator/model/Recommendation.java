package com.watercalculator.model;

/**
 * Modèle Recommandation
 */
public class Recommendation {

    public enum Severity { INFO, WARNING, DANGER }

    private int      id;
    private String   category;
    private double   threshold;
    private String   message;
    private String   tip;
    private Severity severity;

    public Recommendation() {}

    public Recommendation(String category, double threshold,
                          String message, String tip, Severity severity) {
        this.category  = category;
        this.threshold = threshold;
        this.message   = message;
        this.tip       = tip;
        this.severity  = severity;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }

    public String getCategory()          { return category; }
    public void setCategory(String v)    { this.category = v; }

    public double getThreshold()         { return threshold; }
    public void setThreshold(double v)   { this.threshold = v; }

    public String getMessage()           { return message; }
    public void setMessage(String v)     { this.message = v; }

    public String getTip()               { return tip; }
    public void setTip(String v)         { this.tip = v; }

    public Severity getSeverity()        { return severity; }
    public void setSeverity(Severity v)  { this.severity = v; }

    @Override
    public String toString() {
        return "[" + severity + "] " + message;
    }
}
