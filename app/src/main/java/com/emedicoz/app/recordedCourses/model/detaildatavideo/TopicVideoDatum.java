package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TopicVideoDatum implements Serializable {

    @Expose
    private String id;
    @Expose
    private ArrayList<ListDetailData> list;
    @SerializedName("module_name")
    private String moduleName;
    @SerializedName("total_duration")
    private String totalDuration;
    @SerializedName("total_videos")
    private Long totalVideos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(Long totalVideos) {
        this.totalVideos = totalVideos;
    }

    public ArrayList<ListDetailData> getList() {
        return list;
    }

    public void setList(ArrayList<ListDetailData> list) {
        this.list = list;
    }
}
