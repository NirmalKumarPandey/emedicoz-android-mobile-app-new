package com.emedicoz.app.modelo.custommodule;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModuleData implements Serializable {


    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName(Const.USER_ID)
    @Expose
    private String userId;
    @SerializedName("difficulty")
    @Expose
    private String difficulty;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("subjects")
    @Expose
    private Integer subjects;
    @SerializedName("topics")
    @Expose
    private Integer topics;
    @SerializedName("creation")
    @Expose
    private String creation;
    @SerializedName("no_of_question")
    @Expose
    private String noOfQuestion;
    @SerializedName("tags")
    @Expose
    private Integer tags;
    @SerializedName(Constants.ResultExtras.ACCURACY)
    @Expose
    private String accuracy;
    @SerializedName("test_result")
    @Expose
    private TestResult test_result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getSubjects() {
        return subjects;
    }

    public void setSubjects(Integer subjects) {
        this.subjects = subjects;
    }

    public Integer getTopics() {
        return topics;
    }

    public void setTopics(Integer topics) {
        this.topics = topics;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getNoOfQuestion() {
        return noOfQuestion;
    }

    public void setNoOfQuestion(String noOfQuestion) {
        this.noOfQuestion = noOfQuestion;
    }

    public Integer getTags() {
        return tags;
    }

    public void setTags(Integer tags) {
        this.tags = tags;
    }

    public TestResult getTest_result() {
        return test_result;
    }

    public void setTest_result(TestResult test_result) {
        this.test_result = test_result;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
