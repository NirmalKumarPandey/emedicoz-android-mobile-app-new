package com.emedicoz.app.modelo.liveclass;

import com.emedicoz.app.modelo.liveclass.courses.UpComingLiveVideo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveCLassVideoData {
    @SerializedName("layer")
    @Expose
    private Integer layer;
    @SerializedName("list")
    @Expose
    private List<LiveClassList> list = null;
    @SerializedName("upcoming_videos")
    @Expose
    private List<UpComingLiveVideo> upcomingVideos = null;

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public java.util.List<LiveClassList> getList() {
        return list;
    }

    public void setList(java.util.List<LiveClassList> list) {
        this.list = list;
    }

    public List<UpComingLiveVideo> getUpcomingVideos() {
        return upcomingVideos;
    }

    public void setUpcomingVideos(List<UpComingLiveVideo> upcomingVideos) {
        this.upcomingVideos = upcomingVideos;
    }

}
