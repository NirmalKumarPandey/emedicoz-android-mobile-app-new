package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotesTestDataLayer3 {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName(Constants.ResultExtras.TEST_SERIES_NAME)
    @Expose
    private String testSeriesName;
    @SerializedName("time_in_mins")
    @Expose
    private String timeInMins;
    @SerializedName("total_questions")
    @Expose
    private String totalQuestions;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;
    @SerializedName("is_paused")
    @Expose
    private String isPaused;
    @SerializedName("seg_id")
    @Expose
    private Object segId;
    @SerializedName("test_start_date")
    @Expose
    private String testStartDate;
    @SerializedName("test_end_date")
    @Expose
    private String testEndDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestSeriesName() {
        return testSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        this.testSeriesName = testSeriesName;
    }

    public String getTimeInMins() {
        return timeInMins;
    }

    public void setTimeInMins(String timeInMins) {
        this.timeInMins = timeInMins;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(String isPaused) {
        this.isPaused = isPaused;
    }

    public Object getSegId() {
        return segId;
    }

    public void setSegId(Object segId) {
        this.segId = segId;
    }

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestEndDate() {
        return testEndDate;
    }

    public void setTestEndDate(String testEndDate) {
        this.testEndDate = testEndDate;
    }

}
