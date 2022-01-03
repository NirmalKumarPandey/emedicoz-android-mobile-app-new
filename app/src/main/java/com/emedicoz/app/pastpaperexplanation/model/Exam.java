package com.emedicoz.app.pastpaperexplanation.model;

import java.io.Serializable;

public class Exam implements Serializable {

    private String title;
    private String questionCount;
    private boolean isSelected = false;

    public Exam(String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }

    public Exam(String title, String questionCount) {
        this.title = title;
        this.questionCount = questionCount;
    }

    public String getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(String questionCount) {
        this.questionCount = questionCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
