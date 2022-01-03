package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DescCourseReview implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("course_fk_id")
    @Expose
    private String courseFkId;
    @SerializedName(Const.USER_ID)
    @Expose
    private String userId;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseFkId() {
        return courseFkId;
    }

    public void setCourseFkId(String courseFkId) {
        this.courseFkId = courseFkId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
