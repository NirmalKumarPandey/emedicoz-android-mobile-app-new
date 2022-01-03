package com.emedicoz.app.dailychallenge.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DCBookmarkData {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subject")
    @Expose
    private List<SubjectBookmark> subject = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SubjectBookmark> getSubject() {
        return subject;
    }

    public void setSubject(List<SubjectBookmark> subject) {
        this.subject = subject;
    }

}
