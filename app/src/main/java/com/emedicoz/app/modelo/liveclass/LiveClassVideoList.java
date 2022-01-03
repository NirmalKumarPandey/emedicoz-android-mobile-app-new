package com.emedicoz.app.modelo.liveclass;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LiveClassVideoList implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("video_title")
    @Expose
    private String videoTitle;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("is_vod")
    @Expose
    private String isVod;
    @SerializedName("enc_url")
    @Expose
    private Object encUrl = null;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;
    @SerializedName("is_live")
    @Expose
    private String isLive;
    @SerializedName("live_on")
    @Expose
    private String liveOn;
    @SerializedName("chat_node")
    @Expose
    private String chatNode;
    @SerializedName("live_status")
    @Expose
    private String liveStatus;
    @SerializedName("chat_platform")
    @Expose
    private String chatPlatform;
    @SerializedName("live_url")
    @Expose
    private String liveUrl;
    @SerializedName("index")
    @Expose
    private List<LiveClassIndex> index = null;
    @SerializedName("bookmark")
    @Expose
    private List<Object> bookmark = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsVod() {
        return isVod;
    }

    public void setIsVod(String isVod) {
        this.isVod = isVod;
    }

    public Object getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(Object encUrl) {
        this.encUrl = encUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public String getLiveOn() {
        return liveOn;
    }

    public void setLiveOn(String liveOn) {
        this.liveOn = liveOn;
    }

    public String getChatNode() {
        return chatNode;
    }

    public void setChatNode(String chatNode) {
        this.chatNode = chatNode;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getChatPlatform() {
        return chatPlatform;
    }

    public void setChatPlatform(String chatPlatform) {
        this.chatPlatform = chatPlatform;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public List<LiveClassIndex> getIndex() {
        return index;
    }

    public void setIndex(List<LiveClassIndex> index) {
        this.index = index;
    }

    public List<Object> getBookmark() {
        return bookmark;
    }

    public void setBookmark(List<Object> bookmark) {
        this.bookmark = bookmark;
    }
}
