package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoSolutionCounterData {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName(Const.USER_ID)
    @Expose
    private String userId;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("counter")
    @Expose
    private String counter;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;

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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}
