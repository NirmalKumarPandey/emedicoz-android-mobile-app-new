package com.emedicoz.app.modelo.courses.Crs;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EBookFile implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("segment_fk")
    @Expose
    private String segmentFk;
    @SerializedName(Constants.Extras.TYPE)
    @Expose
    private String type;
    @SerializedName("element_fk")
    @Expose
    private String elementFk;
    @SerializedName("key_data")
    @Expose
    private String keyData;
    @SerializedName("topic_name_qbank")
    @Expose
    private String topicNameQbank;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;
    @SerializedName("is_live")
    @Expose
    private String isLive;
    @SerializedName("live_on")
    @Expose
    private String liveOn;
    @SerializedName("topic_id")
    @Expose
    private String topicId;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("topic_name")
    @Expose
    private String topicName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSegmentFk() {
        return segmentFk;
    }

    public void setSegmentFk(String segmentFk) {
        this.segmentFk = segmentFk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElementFk() {
        return elementFk;
    }

    public void setElementFk(String elementFk) {
        this.elementFk = elementFk;
    }

    public String getKeyData() {
        return keyData;
    }

    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }

    public String getTopicNameQbank() {
        return topicNameQbank;
    }

    public void setTopicNameQbank(String topicNameQbank) {
        this.topicNameQbank = topicNameQbank;
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

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
