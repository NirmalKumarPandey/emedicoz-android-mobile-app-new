package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RankList implements Serializable {
    @SerializedName(Const.USER_ID)
    @Expose
    private String userId;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("time_spent")
    @Expose
    private String timeSpent;
    @SerializedName(Constants.ResultExtras.CORRECT_COUNT)
    @Expose
    private String correct_count;
    @SerializedName(Constants.ResultExtras.INCORRECT_COUNT)
    @Expose
    private String incorrect_count;
    @SerializedName(Constants.ResultExtras.NON_ATTEMPT)
    @Expose
    private String non_attempt;
    @SerializedName("marks")
    @Expose
    private String marks;
    @SerializedName("guess_count")
    @Expose
    private String guessCount;
    @SerializedName("correct_guess")
    @Expose
    private String correctGuess;
    @SerializedName("incorrect_guess")
    @Expose
    private String incorrectGuess;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getCorrect_count() {
        return correct_count;
    }

    public void setCorrect_count(String correct_count) {
        this.correct_count = correct_count;
    }

    public String getIncorrect_count() {
        return incorrect_count;
    }

    public void setIncorrect_count(String incorrect_count) {
        this.incorrect_count = incorrect_count;
    }

    public String getNon_attempt() {
        return non_attempt;
    }

    public void setNon_attempt(String non_attempt) {
        this.non_attempt = non_attempt;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getGuessCount() {
        return guessCount;
    }

    public void setGuessCount(String guessCount) {
        this.guessCount = guessCount;
    }

    public String getCorrectGuess() {
        return correctGuess;
    }

    public void setCorrectGuess(String correctGuess) {
        this.correctGuess = correctGuess;
    }

    public String getIncorrectGuess() {
        return incorrectGuess;
    }

    public void setIncorrectGuess(String incorrectGuess) {
        this.incorrectGuess = incorrectGuess;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

}
