package com.emedicoz.app.pastpaperexplanation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Topic implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("topic_name")
    @Expose
    private String topicName;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("course_fk")
    @Expose
    private String courseFk;
    @SerializedName("is_preview")
    @Expose
    private String isPreview;
    @SerializedName("only_for_dams")
    @Expose
    private String onlyForDams;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("topic_type")
    @Expose
    private String topicType;
    @SerializedName("image")
    @Expose
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCourseFk() {
        return courseFk;
    }

    public void setCourseFk(String courseFk) {
        this.courseFk = courseFk;
    }

    public String getIsPreview() {
        return isPreview;
    }

    public void setIsPreview(String isPreview) {
        this.isPreview = isPreview;
    }

    public String getOnlyForDams() {
        return onlyForDams;
    }

    public void setOnlyForDams(String onlyForDams) {
        this.onlyForDams = onlyForDams;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}