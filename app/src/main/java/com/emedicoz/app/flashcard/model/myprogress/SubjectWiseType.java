package com.emedicoz.app.flashcard.model.myprogress;

public class SubjectWiseType {

    private int type;
    private String title;
    private Subjectwise subjectwise;

    public SubjectWiseType(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public SubjectWiseType(int type, Subjectwise subjectwise) {
        this.type = type;
        this.subjectwise = subjectwise;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Subjectwise getSubjectwise() {
        return subjectwise;
    }

    public void setSubjectwise(Subjectwise subjectwise) {
        this.subjectwise = subjectwise;
    }
}
