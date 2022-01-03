package com.emedicoz.app.modelo;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CourseBannerData implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("image_link")
    @Expose
    private String imageLink;
    @SerializedName("web_image_link")
    @Expose
    private String webImageLink;
    @SerializedName("web_link")
    @Expose
    private String webLink;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("banner_title")
    @Expose
    private String bannerTitle;
    @SerializedName("hit_count")
    @Expose
    private String hitCount;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("course_id")
    @Expose
    private String courseId;
    @SerializedName("add_for")
    @Expose
    private String addFor;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("course_type")
    @Expose
    private String courseType;

    @SerializedName("is_combo")
    @Expose
    private String isCombo;

    @SerializedName("is_live")
    @Expose
    private String is_live;

    @SerializedName("mrp")
    @Expose
    private String mrp;

    @SerializedName("for_dams")
    @Expose
    private String for_dams;

    @SerializedName("non_dams")
    @Expose
    private String non_dams;

    @SerializedName("media_type")
    @Expose
    private String media_type;

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getWebImageLink() {
        return webImageLink;
    }

    public void setWebImageLink(String webImageLink) {
        this.webImageLink = webImageLink;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public String getHitCount() {
        return hitCount;
    }

    public void setHitCount(String hitCount) {
        this.hitCount = hitCount;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getAddFor() {
        return addFor;
    }

    public void setAddFor(String addFor) {
        this.addFor = addFor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getIsCombo() {
        return isCombo;
    }

    public void setIsCombo(String isCombo) {
        this.isCombo = isCombo;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getFor_dams() {
        return for_dams;
    }

    public void setFor_dams(String for_dams) {
        this.for_dams = for_dams;
    }

    public String getNon_dams() {
        return non_dams;
    }

    public void setNon_dams(String non_dams) {
        this.non_dams = non_dams;
    }
}
