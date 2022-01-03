package com.emedicoz.app.modelo.testseries;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.SerializedName;

public class TopTenList {

    @SerializedName("creation_time")
    private String mCreationTime;
    @SerializedName(Constants.Extras.NAME)
    private String mName;
    @SerializedName("profile_picture")
    private String mProfilePicture;
    @SerializedName(Const.USER_ID)
    private String mUserId;
    @SerializedName("marks")
    private String marks;
    @SerializedName(Constants.ResultExtras.CORRECT_COUNT)
    private String correct_count;
    @SerializedName(Constants.ResultExtras.INCORRECT_COUNT)
    private String incorrect_count;
    @SerializedName(Constants.ResultExtras.NON_ATTEMPT)
    private String non_attempt;

    public String getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(String creationTime) {
        mCreationTime = creationTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProfilePicture() {
        return mProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        mProfilePicture = profilePicture;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
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
}
