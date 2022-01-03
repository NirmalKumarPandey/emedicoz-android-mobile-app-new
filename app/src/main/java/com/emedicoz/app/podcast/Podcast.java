
package com.emedicoz.app.podcast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Podcast implements Serializable {

    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("created_by")
    private String mCreatedBy;
    @SerializedName("delete_status")
    private String mDeleteStatus;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("id")
    private String mId;
    @SerializedName("is_public")
    private String mIsPublic;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("stream")
    private String mStream;
    @SerializedName("subject")
    private String mSubject;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("duration")
    private String duration;
    private String downloadedUrl;
    private long createdAtTimestamp;

    @SerializedName("thumbnail_image")
    private String thumbnail_image;

    private String is_bookmarked;
    private boolean isPrepared;
    private String views;

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public boolean getIsPrepared() {
        return isPrepared;
    }

    public void setIsPrepared(boolean isPrepared) {
        this.isPrepared = isPrepared;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getCreatedBy() {
        return mCreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        mCreatedBy = createdBy;
    }

    public String getDeleteStatus() {
        return mDeleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        mDeleteStatus = deleteStatus;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIsPublic() {
        return mIsPublic;
    }

    public void setIsPublic(String isPublic) {
        mIsPublic = isPublic;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStream() {
        return mStream;
    }

    public void setStream(String stream) {
        mStream = stream;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getDownloadedUrl() {
        return downloadedUrl;
    }

    public void setDownloadedUrl(String url) {
        downloadedUrl = url;
    }

    public String getIs_bookmarked() {
        return is_bookmarked;
    }

    public void setIs_bookmarked(String is_bookmarked) {
        this.is_bookmarked = is_bookmarked;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public long getCreatedAtTimestamp() {
        return createdAtTimestamp;
    }

    public void setCreatedAtTimestamp(long createdAtTimestamp) {
        this.createdAtTimestamp = createdAtTimestamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
