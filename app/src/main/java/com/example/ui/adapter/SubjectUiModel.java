package com.example.ui.adapter;

public class SubjectUiModel {
    private final String name;
    private final String details;
    private final String accuracy;
    private final int colorRes;

    public SubjectUiModel(String name, String details, String accuracy, int colorRes) {
        this.name = name;
        this.details = details;
        this.accuracy = accuracy;
        this.colorRes = colorRes;
    }

    public String getName() { return name; }
    public String getDetails() { return details; }
    public String getAccuracy() { return accuracy; }
    public int getColorRes() { return colorRes; }
}
