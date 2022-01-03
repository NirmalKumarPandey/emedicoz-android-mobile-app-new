package com.emedicoz.app.pastpaperexplanation.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Curriculam implements Serializable {

    @SerializedName("topics")
    @Expose
    private List<Topic> topics = null;
    @SerializedName("subjects")
    @Expose
    private List<Subject> subjects = null;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

}