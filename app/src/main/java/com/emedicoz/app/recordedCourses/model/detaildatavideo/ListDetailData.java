package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ListDetailData implements Serializable {

    @SerializedName("chat_platform")
    private Object chatPlatform;
    @Expose
    private String id;
    @SerializedName("image_icon")
    private String imageIcon;
    @SerializedName("is_live")
    private String isLive;
    @SerializedName("live_status")
    private String liveStatus;
    @SerializedName("node_chat")
    private String nodeChat;
    @Expose
    private String title;
    @Expose
    private ArrayList<Total> total;
    @SerializedName("vedio_list")
    private ArrayList<VideoItemData> videoList;
    @SerializedName("video_type")
    private String videoType;

    public Object getChatPlatform() {
        return chatPlatform;
    }

    public void setChatPlatform(Object chatPlatform) {
        this.chatPlatform = chatPlatform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getNodeChat() {
        return nodeChat;
    }

    public void setNodeChat(String nodeChat) {
        this.nodeChat = nodeChat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Total> getTotal() {
        return total;
    }

    public void setTotal(ArrayList<Total> total) {
        this.total = total;
    }

    public ArrayList<VideoItemData> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<VideoItemData> videoList) {
        this.videoList = videoList;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

}
