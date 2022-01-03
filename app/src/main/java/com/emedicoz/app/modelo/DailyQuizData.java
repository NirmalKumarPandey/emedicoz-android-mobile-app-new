package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DailyQuizData implements Serializable {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;

    @SerializedName("dq_title")
    @Expose
    private String dqTitle;
    @SerializedName(Constants.ResultExtras.TEST_SERIES_NAME)
    @Expose
    private String testSeriesName;
    @SerializedName("test_start_date")
    @Expose
    private String testStartDate;
    @SerializedName("segment_id")
    @Expose
    private String segmentId;
    @SerializedName("is_paused")
    @Expose
    private String isPaused;

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

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(String isPaused) {
        this.isPaused = isPaused;
    }

    public String getDqTitle() {
        return dqTitle;
    }

    public void setDqTitle(String dqTitle) {
        this.dqTitle = dqTitle;
    }
}
