package models;

public class Threat {
    private int id;
    private String title;
    private String description;
    private String severity;

    public Threat() {}

    public Threat(int id, String title, String description, String severity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
    }

    public Threat(String title, String description, String severity) {
        this.title = title;
        this.description = description;
        this.severity = severity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
