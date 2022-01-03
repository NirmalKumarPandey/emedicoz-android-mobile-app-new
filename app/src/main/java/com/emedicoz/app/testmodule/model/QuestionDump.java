package com.emedicoz.app.testmodule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionDump implements Serializable {
    @SerializedName("question_id")
    @Expose
    private String questionId;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("guess")
    @Expose
    private String guess;
    @SerializedName("onscreen")
    @Expose
    private String onscreen;
    @SerializedName("part")
    @Expose
    private String part;
    @SerializedName("section_id")
    @Expose
    private String sectionId;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("unanswered")
    @Expose
    private String unanswered;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getOnscreen() {
        return onscreen;
    }

    public void setOnscreen(String onscreen) {
        this.onscreen = onscreen;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUnanswered() {
        return unanswered;
    }

    public void setUnanswered(String unanswered) {
        this.unanswered = unanswered;
    }
}
