package com.emedicoz.app.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OngoingCourseData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
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
    private String encUrl;
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
    private Object previewVideoDuration;
    @SerializedName("chat_node")
    @Expose
    private String chatNode;
    @SerializedName("live_status")
    @Expose
    private String liveStatus;
    @SerializedName("chat_platform")
    @Expose
    private String chatPlatform;
    @SerializedName("course_id")
    @Expose
    private String courseId;
    @SerializedName("course_name")
    @Expose
    private String courseName;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("for_dams")
    @Expose
    private String forDams;
    @SerializedName("non_dams")
    @Expose
    private String nonDams;
/*    @SerializedName("index")
    @Expose
    private ArrayList<Object> index = null;
    @SerializedName("bookmark")
    @Expose
    private ArrayList<Object> bookmark = null;*/
    @SerializedName("is_viewed")
    @Expose
    private String isViewed;
    @SerializedName("is_purchased")
    @Expose
    private Boolean isPurchased;

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

    public String getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(String encUrl) {
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

    public String getIsPreviewVideo() {
        return isPreviewVideo;
    }

    public void setIsPreviewVideo(String isPreviewVideo) {
        this.isPreviewVideo = isPreviewVideo;
    }

    public Object getPreviewVideoDuration() {
        return previewVideoDuration;
    }

    public void setPreviewVideoDuration(Object previewVideoDuration) {
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getForDams() {
        return forDams;
    }

    public void setForDams(String forDams) {
        this.forDams = forDams;
    }

    public String getNonDams() {
        return nonDams;
    }

    public void setNonDams(String nonDams) {
        this.nonDams = nonDams;
    }

/*    public List<Object> getIndex() {
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
    }*/

    public String getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(String isViewed) {
        this.isViewed = isViewed;
    }

    public Boolean getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

}
