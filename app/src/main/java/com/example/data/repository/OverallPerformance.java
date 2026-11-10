package com.example.data.repository;

public class OverallPerformance {
    private final int totalSolved;
    private final int correctCount;
    private final int accuracyPercent;
    private final double averageTimeSeconds;

    public OverallPerformance(int totalSolved, int correctCount, int accuracyPercent, double averageTimeSeconds) {
        this.totalSolved = totalSolved;
        this.correctCount = correctCount;
        this.accuracyPercent = accuracyPercent;
        this.averageTimeSeconds = averageTimeSeconds;
    }

    public int getTotalSolved() { return totalSolved; }
    public int getCorrectCount() { return correctCount; }
    public int getAccuracyPercent() { return accuracyPercent; }
    public double getAverageTimeSeconds() { return averageTimeSeconds; }
}
