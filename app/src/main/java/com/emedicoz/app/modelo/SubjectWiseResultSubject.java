package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubjectWiseResultSubject implements Serializable {


    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("guess")
    @Expose
    private String guess;
    @SerializedName("skipped")
    @Expose
    private String skipped;
    @SerializedName("wrong")
    @Expose
    private String wrong;
    @SerializedName("right")
    @Expose
    private String right;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("percentge")
    @Expose
    private String percentge;
    @SerializedName("topper_time")
    @Expose
    private String topperTime;
    @SerializedName("topper_percentge")
    @Expose
    private String topperPercentge;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getSkipped() {
        return skipped;
    }

    public void setSkipped(String skipped) {
        this.skipped = skipped;
    }

    public String getWrong() {
        return wrong;
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPercentge() {
        return percentge;
    }

    public void setPercentge(String percentge) {
        this.percentge = percentge;
    }

    public String getTopperTime() {
        return topperTime;
    }

    public void setTopperTime(String topperTime) {
        this.topperTime = topperTime;
    }

    public String getTopperPercentge() {
        return topperPercentge;
    }

    public void setTopperPercentge(String topperPercentge) {
        this.topperPercentge = topperPercentge;
    }


}
