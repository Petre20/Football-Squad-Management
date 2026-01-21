package models;

public class TrainingAttendance {
    private int id;
    private int trainingId;
    private int playerId;
    private String status; // "Prezent", "Absent", etc.
    private int rating;    // Nota 1-10

    public TrainingAttendance(int id, int trainingId, int playerId, String status, int rating) {
        this.id = id;
        this.trainingId = trainingId;
        this.playerId = playerId;
        this.status = status;
        this.rating = rating;
    }

    // Getters
    public int getId() { return id; }
    public int getTrainingId() { return trainingId; }
    public int getPlayerId() { return playerId; }
    public String getStatus() { return status; }
    public int getRating() { return rating; }
}