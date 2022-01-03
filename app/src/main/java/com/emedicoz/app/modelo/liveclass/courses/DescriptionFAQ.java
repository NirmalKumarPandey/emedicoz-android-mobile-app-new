package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DescriptionFAQ implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("course_id")
    @Expose
    private String courseId;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("expand")
    @Expose
    private boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
