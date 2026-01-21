package models;

public class MatchStats {
    private int id;
    private int matchId;
    private int playerId;

    // Statisticile efective
    private int goals;
    private int shotsOnTarget;
    private int totalShots;
    private int passesCompleted;
    private int passesTotal;
    private int dribblesCompleted;
    private double distanceKm;
    private int saves;
    private int tackles;

    public MatchStats(int id, int matchId, int playerId, int goals, int shotsOnTarget, int totalShots,
                      int passesCompleted, int passesTotal, int dribblesCompleted, double distanceKm,
                      int saves, int tackles) {
        this.id = id;
        this.matchId = matchId;
        this.playerId = playerId;
        this.goals = goals;
        this.shotsOnTarget = shotsOnTarget;
        this.totalShots = totalShots;
        this.passesCompleted = passesCompleted;
        this.passesTotal = passesTotal;
        this.dribblesCompleted = dribblesCompleted;
        this.distanceKm = distanceKm;
        this.saves = saves;
        this.tackles = tackles;
    }

    // Getters
    public int getId() { return id; }
    public int getMatchId() { return matchId; }
    public int getPlayerId() { return playerId; }
    public int getGoals() { return goals; }
    public int getShotsOnTarget() { return shotsOnTarget; }
    public int getTotalShots() { return totalShots; }
    public int getPassesCompleted() { return passesCompleted; }
    public int getPassesTotal() { return passesTotal; }
    public int getDribblesCompleted() { return dribblesCompleted; }
    public double getDistanceKm() { return distanceKm; }
    public int getSaves() { return saves; }
    public int getTackles() { return tackles; }
}