package com.emedicoz.app.dailychallenge.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubjectBookmark {
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("subject_name")
    @Expose
    private String subjectName;
    @SerializedName("questions")
    @Expose
    private List<BookmarkQuestion> questions = null;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<BookmarkQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<BookmarkQuestion> questions) {
        this.questions = questions;
    }
}
