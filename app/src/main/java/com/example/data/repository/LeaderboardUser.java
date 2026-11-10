package com.example.data.repository;

public class LeaderboardUser {
    private final int rank;
    private final String name;
    private final String college;
    private final double totalScore;
    private final int accuracy;
    private final boolean isCurrentUser;

    public LeaderboardUser(int rank, String name, String college, double totalScore, int accuracy, boolean isCurrentUser) {
        this.rank = rank;
        this.name = name;
        this.college = college;
        this.totalScore = totalScore;
        this.accuracy = accuracy;
        this.isCurrentUser = isCurrentUser;
    }

    public int getRank() { return rank; }
    public String getName() { return name; }
    public String getCollege() { return college; }
    public double getTotalScore() { return totalScore; }
    public int getAccuracy() { return accuracy; }
    public boolean isCurrentUser() { return isCurrentUser; }
}
