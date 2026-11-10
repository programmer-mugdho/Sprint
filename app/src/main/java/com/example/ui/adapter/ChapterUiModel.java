package com.example.ui.adapter;

public class ChapterUiModel {
    private final String chapterName;
    private final String subjectName;
    private final int questionCount;
    private final int attemptsCount;
    private final int accuracyPercent;
    private final int avgSpeedSeconds;

    public ChapterUiModel(String chapterName, String subjectName, int questionCount,
                          int attemptsCount, int accuracyPercent, int avgSpeedSeconds) {
        this.chapterName = chapterName;
        this.subjectName = subjectName;
        this.questionCount = questionCount;
        this.attemptsCount = attemptsCount;
        this.accuracyPercent = accuracyPercent;
        this.avgSpeedSeconds = avgSpeedSeconds;
    }

    public String getChapterName() { return chapterName; }
    public String getSubjectName() { return subjectName; }
    public int getQuestionCount() { return questionCount; }
    public int getAttemptsCount() { return attemptsCount; }
    public int getAccuracyPercent() { return accuracyPercent; }
    public int getAvgSpeedSeconds() { return avgSpeedSeconds; }
}
