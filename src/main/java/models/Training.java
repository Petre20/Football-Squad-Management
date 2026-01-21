package models;

public class Training {
    private int id;
    private String date;
    private String type;
    private int duration;
    private String description;

    public Training(int id, String date, String type, int duration, String description) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return type + " (" + date + ")";
    }
}