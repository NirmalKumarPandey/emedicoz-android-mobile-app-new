package com.emedicoz.app.modelo.liveclass;

import com.emedicoz.app.modelo.Total;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveClassList {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_icon")
    @Expose
    private String imageIcon;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("is_live")
    @Expose
    private String isLive;
    @SerializedName("total")
    @Expose
    private java.util.List<Total> total = null;
    @SerializedName("node_chat")
    @Expose
    private String nodeChat;
    @SerializedName("live_status")
    @Expose
    private String liveStatus;
    @SerializedName("chat_platform")
    @Expose
    private Object chatPlatform;
    @SerializedName("vedio_list")
    @Expose
    private java.util.List<LiveClassVideoList> vedioList = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public java.util.List<Total> getTotal() {
        return total;
    }

    public void setTotal(java.util.List<Total> total) {
        this.total = total;
    }

    public String getNodeChat() {
        return nodeChat;
    }

    public void setNodeChat(String nodeChat) {
        this.nodeChat = nodeChat;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public Object getChatPlatform() {
        return chatPlatform;
    }

    public void setChatPlatform(Object chatPlatform) {
        this.chatPlatform = chatPlatform;
    }

    public java.util.List<LiveClassVideoList> getVedioList() {
        return vedioList;
    }

    public void setVedioList(java.util.List<LiveClassVideoList> vedioList) {
        this.vedioList = vedioList;
    }

}
