package com.example.ui.adapter;

import com.example.data.entity.QuestionEntity;

public class QuestionReviewUiModel {
    private final int index;
    private final QuestionEntity question;
    private final int userSelectedOption; // 0..3 or -1 for skipped
    private final int timeSpentSeconds;

    public QuestionReviewUiModel(int index, QuestionEntity question, int userSelectedOption, int timeSpentSeconds) {
        this.index = index;
        this.question = question;
        this.userSelectedOption = userSelectedOption;
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public int getIndex() { return index; }
    public QuestionEntity getQuestion() { return question; }
    public int getUserSelectedOption() { return userSelectedOption; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
}
