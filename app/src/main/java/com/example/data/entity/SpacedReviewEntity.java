package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "spaced_reviews")
public class SpacedReviewEntity {

    @PrimaryKey
    private long questionId;

    private int intervalDays;
    private long nextReviewTimestamp;
    private int timesWrong;
    private int timesCorrect;
    private boolean isMastered;
    private long lastReviewedTimestamp;

    public SpacedReviewEntity() {
        this.intervalDays = 1;
        this.nextReviewTimestamp = System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000L);
        this.timesWrong = 1;
        this.timesCorrect = 0;
        this.isMastered = false;
        this.lastReviewedTimestamp = System.currentTimeMillis();
    }

    @Ignore
    public SpacedReviewEntity(long questionId, int intervalDays, long nextReviewTimestamp,
                              int timesWrong, int timesCorrect, boolean isMastered,
                              long lastReviewedTimestamp) {
        this.questionId = questionId;
        this.intervalDays = intervalDays;
        this.nextReviewTimestamp = nextReviewTimestamp;
        this.timesWrong = timesWrong;
        this.timesCorrect = timesCorrect;
        this.isMastered = isMastered;
        this.lastReviewedTimestamp = lastReviewedTimestamp;
    }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }

    public long getNextReviewTimestamp() { return nextReviewTimestamp; }
    public void setNextReviewTimestamp(long nextReviewTimestamp) { this.nextReviewTimestamp = nextReviewTimestamp; }

    public int getTimesWrong() { return timesWrong; }
    public void setTimesWrong(int timesWrong) { this.timesWrong = timesWrong; }

    public int getTimesCorrect() { return timesCorrect; }
    public void setTimesCorrect(int timesCorrect) { this.timesCorrect = timesCorrect; }

    public boolean isMastered() { return isMastered; }
    public void setMastered(boolean mastered) { isMastered = mastered; }

    public long getLastReviewedTimestamp() { return lastReviewedTimestamp; }
    public void setLastReviewedTimestamp(long lastReviewedTimestamp) { this.lastReviewedTimestamp = lastReviewedTimestamp; }
}
