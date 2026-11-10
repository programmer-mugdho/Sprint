package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class QuestionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String subject; // Physics, Chemistry, Higher Math, English
    private String chapter;
    private String questionText;
    private String formula;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctOptionIndex; // 0 for A, 1 for B, 2 for C, 3 for D
    private String explanation;
    private String difficulty; // Easy, Medium, Hard

    public QuestionEntity() {
    }

    @Ignore
    public QuestionEntity(String subject, String chapter, String questionText,
                          String formula, String optionA, String optionB,
                          String optionC, String optionD, int correctOptionIndex,
                          String explanation, String difficulty) {
        this.subject = subject;
        this.chapter = chapter;
        this.questionText = questionText;
        this.formula = formula;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.difficulty = difficulty;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getChapter() { return chapter; }
    public void setChapter(String chapter) { this.chapter = chapter; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
