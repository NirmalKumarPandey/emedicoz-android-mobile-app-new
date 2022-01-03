package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpubLayer3Data {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("main_id")
    @Expose
    private String mainId;
    @SerializedName("sub_id")
    @Expose
    private String subId;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("enc_url")
    @Expose
    private String encUrl;
    @SerializedName("enc_file_url")
    @Expose
    private String encFileUrl;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("topic_id")
    @Expose
    private String topicId;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("page_count")
    @Expose
    private String pageCount;
    @SerializedName("backend_user_id")
    @Expose
    private String backendUserId;
    @SerializedName("view_limit")
    @Expose
    private String viewLimit;
    @SerializedName("is_vod")
    @Expose
    private String isVod;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("duration_limit")
    @Expose
    private String durationLimit;
    @SerializedName("ext_duration")
    @Expose
    private String extDuration;
    @SerializedName("users_to_extend")
    @Expose
    private String usersToExtend;
    @SerializedName("channel_url_type")
    @Expose
    private String channelUrlType;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("live_status")
    @Expose
    private String liveStatus;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("creation")
    @Expose
    private String creation;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(String encUrl) {
        this.encUrl = encUrl;
    }

    public String getEncFileUrl() {
        return encFileUrl;
    }

    public void setEncFileUrl(String encFileUrl) {
        this.encFileUrl = encFileUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getBackendUserId() {
        return backendUserId;
    }

    public void setBackendUserId(String backendUserId) {
        this.backendUserId = backendUserId;
    }

    public String getViewLimit() {
        return viewLimit;
    }

    public void setViewLimit(String viewLimit) {
        this.viewLimit = viewLimit;
    }

    public String getIsVod() {
        return isVod;
    }

    public void setIsVod(String isVod) {
        this.isVod = isVod;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationLimit() {
        return durationLimit;
    }

    public void setDurationLimit(String durationLimit) {
        this.durationLimit = durationLimit;
    }

    public String getExtDuration() {
        return extDuration;
    }

    public void setExtDuration(String extDuration) {
        this.extDuration = extDuration;
    }

    public String getUsersToExtend() {
        return usersToExtend;
    }

    public void setUsersToExtend(String usersToExtend) {
        this.usersToExtend = usersToExtend;
    }

    public String getChannelUrlType() {
        return channelUrlType;
    }

    public void setChannelUrlType(String channelUrlType) {
        this.channelUrlType = channelUrlType;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

}
