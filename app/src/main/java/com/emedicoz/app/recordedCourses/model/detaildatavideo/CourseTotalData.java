package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CourseTotalData implements Serializable {

    @SerializedName("total_module")
    private Long totalModule;
    @SerializedName("total_topic")
    private Long totalTopic;
    @SerializedName("total_video")
    private Long totalVideo;

    public Long getTotalModule() {
        return totalModule;
    }

    public void setTotalModule(Long totalModule) {
        this.totalModule = totalModule;
    }

    public Long getTotalTopic() {
        return totalTopic;
    }

    public void setTotalTopic(Long totalTopic) {
        this.totalTopic = totalTopic;
    }

    public Long getTotalVideo() {
        return totalVideo;
    }

    public void setTotalVideo(Long totalVideo) {
        this.totalVideo = totalVideo;
    }

}
