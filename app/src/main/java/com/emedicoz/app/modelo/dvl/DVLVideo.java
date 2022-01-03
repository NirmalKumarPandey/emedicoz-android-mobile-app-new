package com.emedicoz.app.modelo.dvl;

import com.emedicoz.app.modelo.courses.Enc;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DVLVideo implements Serializable {
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
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("view_limit")
    @Expose
    private String viewLimit;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("duration_limit")
    @Expose
    private String durationLimit;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("enc_url")
    @Expose
    private Enc enc_url;
    @SerializedName("new_link")
    @Expose
    private String new_link;

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getViewLimit() {
        return viewLimit;
    }

    public void setViewLimit(String viewLimit) {
        this.viewLimit = viewLimit;
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

    public Enc getEnc_url() {
        return enc_url;
    }

    public void setEnc_url(Enc enc_url) {
        this.enc_url = enc_url;
    }

    public String getNew_link() {
        return new_link;
    }

    public void setNew_link(String new_link) {
        this.new_link = new_link;
    }
}
