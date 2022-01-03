
package com.emedicoz.app.testmodule.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Section implements Serializable {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("section_title")
    @Expose
    private String sectionTitle;
    @SerializedName("test_series_id")
    @Expose
    private String testSeriesId;
    @SerializedName("section_id")
    @Expose
    private String sectionId;
    @SerializedName("no_of_questions")
    @Expose
    private String noOfQuestions;
    @SerializedName("section_timing")
    @Expose
    private String sectionTiming;
    @SerializedName("marks_per_question")
    @Expose
    private String marksPerQuestion;
    @SerializedName("attempt_limit")
    @Expose
    private int attemptLimit;

    public int getAttemptLimit() {
        return attemptLimit;
    }

    public void setAttemptLimit(int attemptLimit) {
        this.attemptLimit = attemptLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getTestSeriesId() {
        return testSeriesId;
    }

    public void setTestSeriesId(String testSeriesId) {
        this.testSeriesId = testSeriesId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getNoOfQuestions() {
        return noOfQuestions;
    }

    public void setNoOfQuestions(String noOfQuestions) {
        this.noOfQuestions = noOfQuestions;
    }

    public String getSectionTiming() {
        return sectionTiming;
    }

    public void setSectionTiming(String sectionTiming) {
        this.sectionTiming = sectionTiming;
    }

    public String getMarksPerQuestion() {
        return marksPerQuestion;
    }

    public void setMarksPerQuestion(String marksPerQuestion) {
        this.marksPerQuestion = marksPerQuestion;
    }

}
