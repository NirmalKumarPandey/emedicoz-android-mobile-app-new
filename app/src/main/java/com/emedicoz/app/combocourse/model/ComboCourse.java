package com.emedicoz.app.combocourse.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComboCourse {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("desc_header_image")
    @Expose
    private String descHeaderImage;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("publish")
    @Expose
    private String publish;
    @SerializedName("for_dams")
    @Expose
    private String forDams;
    @SerializedName("non_dams")
    @Expose
    private String nonDams;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("gst_include")
    @Expose
    private String gstInclude;
    @SerializedName("review_count")
    @Expose
    private String reviewCount;
    @SerializedName("learner")
    @Expose
    private String learner;
    @SerializedName("is_purchased")
    @Expose
    private String isPurchased;
    @SerializedName("validity")
    @Expose
    private String validity;
    @SerializedName("dams_passing")
    @Expose
    private String damsPassing;
    @SerializedName("points_conversion_rate")
    @Expose
    private String points_conversion_rate;
    @SerializedName("gst")
    @Expose
    private String gst;

    public String getPoints_conversion_rate() {
        return points_conversion_rate;
    }

    public void setPoints_conversion_rate(String points_conversion_rate) {
        this.points_conversion_rate = points_conversion_rate;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescHeaderImage() {
        return descHeaderImage;
    }

    public void setDescHeaderImage(String descHeaderImage) {
        this.descHeaderImage = descHeaderImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getFor_dams() {
        return forDams;
    }

    public void setForDams(String forDams) {
        this.forDams = forDams;
    }

    public String getNon_dams() {
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

    public String getGstInclude() {
        return gstInclude;
    }

    public void setGstInclude(String gstInclude) {
        this.gstInclude = gstInclude;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getDamsPassing() {
        return damsPassing;
    }

    public void setDamsPassing(String damsPassing) {
        this.damsPassing = damsPassing;
    }

}

