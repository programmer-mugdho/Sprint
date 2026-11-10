package com.example.data.dao;

public class ChapterStats {
    public String chapter;
    public String subject;
    public int totalAttempts;
    public int correctAttempts;
    public double avgTimeSeconds;

    public ChapterStats() {}

    public ChapterStats(String chapter, String subject, int totalAttempts, int correctAttempts, double avgTimeSeconds) {
        this.chapter = chapter;
        this.subject = subject;
        this.totalAttempts = totalAttempts;
        this.correctAttempts = correctAttempts;
        this.avgTimeSeconds = avgTimeSeconds;
    }
}
