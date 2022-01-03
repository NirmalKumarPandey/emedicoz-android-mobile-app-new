package com.emedicoz.app.dailychallenge.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArchiveAverageMarks {
    @SerializedName("avg_time")
    @Expose
    private String avgTime;
    @SerializedName("avg_correct")
    @Expose
    private String avgCorrect;
    @SerializedName("avg_incorrect")
    @Expose
    private String avgIncorrect;
    @SerializedName("avg_non_attempt")
    @Expose
    private String avgNonAttempt;
    @SerializedName("avg_marks")
    @Expose
    private String avgMarks;

    public String getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }

    public String getAvgCorrect() {
        return avgCorrect;
    }

    public void setAvgCorrect(String avgCorrect) {
        this.avgCorrect = avgCorrect;
    }

    public String getAvgIncorrect() {
        return avgIncorrect;
    }

    public void setAvgIncorrect(String avgIncorrect) {
        this.avgIncorrect = avgIncorrect;
    }

    public String getAvgNonAttempt() {
        return avgNonAttempt;
    }

    public void setAvgNonAttempt(String avgNonAttempt) {
        this.avgNonAttempt = avgNonAttempt;
    }

    public String getAvgMarks() {
        return avgMarks;
    }

    public void setAvgMarks(String avgMarks) {
        this.avgMarks = avgMarks;
    }

}
