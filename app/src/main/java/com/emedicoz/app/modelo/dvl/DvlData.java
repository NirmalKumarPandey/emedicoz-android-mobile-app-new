package com.emedicoz.app.modelo.dvl;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DvlData implements Serializable {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("validity")
    @Expose
    private String validity;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("desc_header_image")
    @Expose
    private String desc_header_image;
    @SerializedName("for_dams")
    @Expose
    private String forDams;
    @SerializedName("non_dams")
    @Expose
    private String nonDams;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("review_count")
    @Expose
    private String reviewCount;
    @SerializedName("free_ids")
    @Expose
    private String freeIds;
    @SerializedName("gst_include")
    @Expose
    private String gstInclude;
    @SerializedName("is_validity")
    @Expose
    private String isValidity;
    @SerializedName("points_conversion_rate")
    @Expose
    private String pointsConversionRate;
    @SerializedName("gst")
    @Expose
    private String gst;

    @SerializedName("course_type")
    @Expose
    private String course_type;

    @SerializedName("learner")
    @Expose
    private String learner;

    @SerializedName("cover_image")
    @Expose
    private String cover_image;

    @SerializedName("curriculam")
    @Expose
    private DVLCurriculum curriculam;
    @SerializedName("is_purchased")
    @Expose
    private String isPurchased;

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

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDesc_header_image() {
        return desc_header_image;
    }

    public void setDesc_header_image(String desc_header_image) {
        this.desc_header_image = desc_header_image;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getFreeIds() {
        return freeIds;
    }

    public void setFreeIds(String freeIds) {
        this.freeIds = freeIds;
    }

    public String getGstInclude() {
        return gstInclude;
    }

    public void setGstInclude(String gstInclude) {
        this.gstInclude = gstInclude;
    }

    public String getIsValidity() {
        return isValidity;
    }

    public void setIsValidity(String isValidity) {
        this.isValidity = isValidity;
    }

    public String getPointsConversionRate() {
        return pointsConversionRate;
    }

    public void setPointsConversionRate(String pointsConversionRate) {
        this.pointsConversionRate = pointsConversionRate;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public DVLCurriculum getCurriculam() {
        return curriculam;
    }

    public void setCurriculam(DVLCurriculum curriculam) {
        this.curriculam = curriculam;
    }

    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
}
