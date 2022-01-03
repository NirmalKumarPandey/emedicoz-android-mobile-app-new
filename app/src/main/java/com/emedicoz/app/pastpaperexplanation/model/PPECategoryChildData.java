package com.emedicoz.app.pastpaperexplanation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PPECategoryChildData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("test_series_name")
    @Expose
    private String testSeriesName;
    @SerializedName("test_type_title")
    @Expose
    private String testTypeTitle;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("file_id")
    @Expose
    private String fileId;
    @SerializedName("view_limit")
    @Expose
    private String viewLimit;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("is_vod")
    @Expose
    private String isVod;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("page_count")
    @Expose
    private String pageCount;
    @SerializedName("enc_url")
    @Expose
    private String encUrl;
    @SerializedName("is_paused")
    @Expose
    private String isPaused;
    @SerializedName("result_id")
    @Expose
    private String resultId;
    @SerializedName("vod_link")
    @Expose
    private String vodLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestSeriesName() {
        return testSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        this.testSeriesName = testSeriesName;
    }

    public String getTestTypeTitle() {
        return testTypeTitle;
    }

    public void setTestTypeTitle(String testTypeTitle) {
        this.testTypeTitle = testTypeTitle;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getViewLimit() {
        return viewLimit;
    }

    public void setViewLimit(String viewLimit) {
        this.viewLimit = viewLimit;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(String encUrl) {
        this.encUrl = encUrl;
    }

    public String getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(String isPaused) {
        this.isPaused = isPaused;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getVodLink() {
        return vodLink;
    }

    public void setVodLink(String vodLink) {
        this.vodLink = vodLink;
    }


}
