package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoItemData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @Expose
    private String module_id;
    @Expose
    private String topic_id;
    @Expose
    private String course_id;
    @SerializedName("video_title")
    @Expose
    private String videoTitle;
    @SerializedName("duration")
    @Expose
    private String duration;
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
    private List<Object> encUrl = null;
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
    @SerializedName("is_preview_video")
    @Expose
    private String isPreviewVideo;
    @SerializedName("preview_video_duration")
    @Expose
    private String previewVideoDuration;
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
    private List<Object> index = null;
    @SerializedName("bookmark")
    @Expose
    private List<Object> bookmark = null;

    @SerializedName("is_viewed")
    @Expose
    private String is_viewed = "";

    @SerializedName("is_drm")
    @Expose
    private String isDrm = "";


    @SerializedName("resource_id")
    @Expose
    private String resourceId = "";

    private String drmUrl;
    private String drmToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public List<Object> getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(List<Object> encUrl) {
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

    public boolean getIsPreviewVideo() {
        return isPreviewVideo != null && isPreviewVideo.equals("1");
    }

    public void setIsPreviewVideo(String isPreviewVideo) {
        this.isPreviewVideo = isPreviewVideo;
    }

    public String getPreviewVideoDuration() {
        return previewVideoDuration;
    }

    public void setPreviewVideoDuration(String previewVideoDuration) {
        this.previewVideoDuration = previewVideoDuration;
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

    public List<Object> getIndex() {
        return index;
    }

    public void setIndex(List<Object> index) {
        this.index = index;
    }

    public List<Object> getBookmark() {
        return bookmark;
    }

    public void setBookmark(List<Object> bookmark) {
        this.bookmark = bookmark;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getIs_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(String is_viewed) {
        this.is_viewed = is_viewed;
    }

    public String getIsDrm() {
        return isDrm;
    }

    public void setIsDrm(String isDrm) {
        this.isDrm = isDrm;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDrmUrl() {
        return drmUrl;
    }

    public void setDrmUrl(String drmUrl) {
        this.drmUrl = drmUrl;
    }

    public String getDrmToken() {
        return drmToken;
    }

    public void setDrmToken(String drmToken) {
        this.drmToken = drmToken;
    }
}
