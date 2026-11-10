package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "question_attempts")
public class QuestionAttemptEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long questionId;
    private int selectedOption; // 0..3 or -1 for skipped
    private boolean isCorrect;
    private int timeSpentSeconds;
    private long timestamp;
    private String sessionType; // PRACTICE or MOCK_TEST

    public QuestionAttemptEntity() {
        this.timestamp = System.currentTimeMillis();
        this.sessionType = "PRACTICE";
    }

    @Ignore
    public QuestionAttemptEntity(long questionId, int selectedOption, boolean isCorrect,
                                 int timeSpentSeconds, String sessionType) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.timeSpentSeconds = timeSpentSeconds;
        this.timestamp = System.currentTimeMillis();
        this.sessionType = sessionType;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public int getSelectedOption() { return selectedOption; }
    public void setSelectedOption(int selectedOption) { this.selectedOption = selectedOption; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
}
