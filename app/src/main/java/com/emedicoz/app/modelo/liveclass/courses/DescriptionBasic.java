package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.installment.model.Installment;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DescriptionBasic implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("course_type")
    @Expose
    private String courseType;
    @SerializedName("is_combo")
    @Expose
    private String isCombo;
    @SerializedName("validity")
    @Expose
    private String validity;
    @SerializedName("is_validity")
    @Expose
    private String isValidity;
    @SerializedName("course_main_fk")
    @Expose
    private String courseMainFk;
    @SerializedName("course_category_fk")
    @Expose
    private String courseCategoryFk;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
    @SerializedName("desc_header_image")
    @Expose
    private String descHeaderImage;
    @SerializedName("cover_video")
    @Expose
    private String coverVideo;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("publish")
    @Expose
    private String publish;
    @SerializedName("is_new")
    @Expose
    private String isNew;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("for_dams")
    @Expose
    private String forDams;
    @SerializedName("non_dams")
    @Expose
    private String nonDams;
    @SerializedName("instructor_id")
    @Expose
    private String instructorId;
    @SerializedName("instructor_share")
    @Expose
    private String instructorShare;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("review_count")
    @Expose
    private String reviewCount;
    @SerializedName("learner")
    @Expose
    private String learner;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;
    @SerializedName("free_ids")
    @Expose
    private String freeIds;
    @SerializedName("gst_include")
    @Expose
    private String gstInclude;
    @SerializedName("skip_topic")
    @Expose
    private String skipTopic;
    @SerializedName("dams_passing")
    @Expose
    private String damsPassing;
    @SerializedName("is_renew")
    @Expose
    private String is_renew;
    @SerializedName("points_conversion_rate")
    @Expose
    private String pointsConversionRate;
    @SerializedName("gst")
    @Expose
    private String gst;
    @SerializedName("is_subscription")
    @Expose
    private String is_subscription;
    @SerializedName("is_instalment")
    @Expose
    private String is_instalment;
    @SerializedName("child_courses")
    @Expose
    private String child_courses;
    @SerializedName("course_schedule")
    @Expose
    private List<DescCourseSchedule> courseSchedule = null;
    @SerializedName("installment")
    @Expose
    private List<Installment> installment = null;
    @SerializedName("share_url")
    @Expose
    private String share_url;
    @SerializedName("learn_description")
    @Expose
    private String learn_description;
    @SerializedName("is_live")
    @Expose
    private String is_live;

    public String getLearn_description() {
        return learn_description;
    }

    public void setLearn_description(String learn_description) {
        this.learn_description = learn_description;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getIs_instalment() {
        return is_instalment;
    }

    public void setIs_instalment(String is_instalment) {
        this.is_instalment = is_instalment;
    }

    public String getIs_subscription() {
        return is_subscription;
    }

    public void setIs_subscription(String is_subscription) {
        this.is_subscription = is_subscription;
    }

    public List<Installment> getInstallment() {
        return installment;
    }

    public void setInstallment(List<Installment> installment) {
        this.installment = installment;
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

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getIsValidity() {
        return isValidity;
    }

    public void setIsValidity(String isValidity) {
        this.isValidity = isValidity;
    }

    public String getCourseMainFk() {
        return courseMainFk;
    }

    public void setCourseMainFk(String courseMainFk) {
        this.courseMainFk = courseMainFk;
    }

    public String getCourseCategoryFk() {
        return courseCategoryFk;
    }

    public void setCourseCategoryFk(String courseCategoryFk) {
        this.courseCategoryFk = courseCategoryFk;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescHeaderImage() {
        return descHeaderImage;
    }

    public void setDescHeaderImage(String descHeaderImage) {
        this.descHeaderImage = descHeaderImage;
    }

    public String getCoverVideo() {
        return coverVideo;
    }

    public void setCoverVideo(String coverVideo) {
        this.coverVideo = coverVideo;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorShare() {
        return instructorShare;
    }

    public void setInstructorShare(String instructorShare) {
        this.instructorShare = instructorShare;
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

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
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

    public String getSkipTopic() {
        return skipTopic;
    }

    public void setSkipTopic(String skipTopic) {
        this.skipTopic = skipTopic;
    }

    public String getDamsPassing() {
        return damsPassing;
    }

    public void setDamsPassing(String damsPassing) {
        this.damsPassing = damsPassing;
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

    public List<DescCourseSchedule> getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(List<DescCourseSchedule> courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public String getChild_courses() {
        return child_courses;
    }

    public void setChild_courses(String child_courses) {
        this.child_courses = child_courses;
    }

    public String getIs_renew() {
        return is_renew == null ? "" : is_renew;
    }

    public void setIs_renew(String is_renew) {
        this.is_renew = is_renew;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }



}
