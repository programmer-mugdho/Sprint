package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "mock_test_results")
public class MockTestResultEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String testTitle;
    private int totalQuestions;
    private int correctCount;
    private int wrongCount;
    private int skippedCount;
    private double scoreAchieved;
    private int totalTimeSeconds;
    private long timestamp;
    private String questionIdsCsv;
    private String selectedOptionsCsv;
    private String timesPerQuestionCsv;

    public MockTestResultEntity() {
        this.timestamp = System.currentTimeMillis();
    }

    @Ignore
    public MockTestResultEntity(String testTitle, int totalQuestions, int correctCount,
                                int wrongCount, int skippedCount, double scoreAchieved,
                                int totalTimeSeconds, String questionIdsCsv,
                                String selectedOptionsCsv, String timesPerQuestionCsv) {
        this.testTitle = testTitle;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.skippedCount = skippedCount;
        this.scoreAchieved = scoreAchieved;
        this.totalTimeSeconds = totalTimeSeconds;
        this.timestamp = System.currentTimeMillis();
        this.questionIdsCsv = questionIdsCsv;
        this.selectedOptionsCsv = selectedOptionsCsv;
        this.timesPerQuestionCsv = timesPerQuestionCsv;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTestTitle() { return testTitle; }
    public void setTestTitle(String testTitle) { this.testTitle = testTitle; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public double getScoreAchieved() { return scoreAchieved; }
    public void setScoreAchieved(double scoreAchieved) { this.scoreAchieved = scoreAchieved; }

    public int getTotalTimeSeconds() { return totalTimeSeconds; }
    public void setTotalTimeSeconds(int totalTimeSeconds) { this.totalTimeSeconds = totalTimeSeconds; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getQuestionIdsCsv() { return questionIdsCsv; }
    public void setQuestionIdsCsv(String questionIdsCsv) { this.questionIdsCsv = questionIdsCsv; }

    public String getSelectedOptionsCsv() { return selectedOptionsCsv; }
    public void setSelectedOptionsCsv(String selectedOptionsCsv) { this.selectedOptionsCsv = selectedOptionsCsv; }

    public String getTimesPerQuestionCsv() { return timesPerQuestionCsv; }
    public void setTimesPerQuestionCsv(String timesPerQuestionCsv) { this.timesPerQuestionCsv = timesPerQuestionCsv; }
}
