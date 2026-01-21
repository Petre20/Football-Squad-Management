package models;

public class Match {
    private int id;
    private String opponent;
    private String date;
    private String location;
    private int scored;
    private int received;

    public Match(int id, String opponent, String date, String location, int scored, int received) {
        this.id = id;
        this.opponent = opponent;
        this.date = date;
        this.location = location;
        this.scored = scored;
        this.received = received;
    }

    public int getId() { return id; }
    public String getOpponent() { return opponent; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getScored() { return scored; }
    public int getReceived() { return received; }

    public String getScoreString() {
        return scored + " - " + received;
    }
}