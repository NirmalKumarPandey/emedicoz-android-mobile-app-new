package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveClassCourseList {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("course_type")
    @Expose
    private String courseType;
    @SerializedName("course_group")
    @Expose
    private String courseGroup;
    @SerializedName("course_main_fk")
    @Expose
    private String courseMainFk;
    @SerializedName("course_category_fk")
    @Expose
    private String courseCategoryFk;
    @SerializedName("course_web_category_fk")
    @Expose
    private String courseWebCategoryFk;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("course_for")
    @Expose
    private String courseFor;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
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
    @SerializedName("gst_include")
    @Expose
    private String gstInclude;
    @SerializedName("instructor_share")
    @Expose
    private String instructorShare;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;
    @SerializedName("course_rating_count")
    @Expose
    private String courseRatingCount;
    @SerializedName("course_review_count")
    @Expose
    private String courseReviewCount;
    @SerializedName("course_learner")
    @Expose
    private String courseLearner;
    @SerializedName("in_suggested_list")
    @Expose
    private String inSuggestedList;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("fake_learner")
    @Expose
    private String fakeLearner;
    @SerializedName("free_ids")
    @Expose
    private String freeIds;
    @SerializedName("creation_time")
    @Expose
    private String creationTime;
    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;
    @SerializedName("validity")
    @Expose
    private String validity;
    @SerializedName("is_validity")
    @Expose
    private String isValidity;
    @SerializedName("test_type_master")
    @Expose
    private String testTypeMaster;
    @SerializedName("is_testing")
    @Expose
    private String isTesting;
    @SerializedName("is_combo")
    @Expose
    private String isCombo;
    @SerializedName("test_type_subcategory")
    @Expose
    private String testTypeSubcategory;
    @SerializedName("is_live")
    @Expose
    private String isLive;
    @SerializedName("skip_topic")
    @Expose
    private String skipTopic;
    @SerializedName("is_instalment")
    @Expose
    private String isInstalment;
    @SerializedName("installment_id")
    @Expose
    private String installmentId;
    @SerializedName("installment_meta")
    @Expose
    private String installmentMeta;
    @SerializedName("registration_fee")
    @Expose
    private String registrationFee;
    @SerializedName("is_subscription")
    @Expose
    private String isSubscription;

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

    public String getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(String courseGroup) {
        this.courseGroup = courseGroup;
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

    public String getCourseWebCategoryFk() {
        return courseWebCategoryFk;
    }

    public void setCourseWebCategoryFk(String courseWebCategoryFk) {
        this.courseWebCategoryFk = courseWebCategoryFk;
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

    public String getCourseFor() {
        return courseFor;
    }

    public void setCourseFor(String courseFor) {
        this.courseFor = courseFor;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getGstInclude() {
        return gstInclude;
    }

    public void setGstInclude(String gstInclude) {
        this.gstInclude = gstInclude;
    }

    public String getInstructorShare() {
        return instructorShare;
    }

    public void setInstructorShare(String instructorShare) {
        this.instructorShare = instructorShare;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getCourseRatingCount() {
        return courseRatingCount;
    }

    public void setCourseRatingCount(String courseRatingCount) {
        this.courseRatingCount = courseRatingCount;
    }

    public String getCourseReviewCount() {
        return courseReviewCount;
    }

    public void setCourseReviewCount(String courseReviewCount) {
        this.courseReviewCount = courseReviewCount;
    }

    public String getCourseLearner() {
        return courseLearner;
    }

    public void setCourseLearner(String courseLearner) {
        this.courseLearner = courseLearner;
    }

    public String getInSuggestedList() {
        return inSuggestedList;
    }

    public void setInSuggestedList(String inSuggestedList) {
        this.inSuggestedList = inSuggestedList;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getFakeLearner() {
        return fakeLearner;
    }

    public void setFakeLearner(String fakeLearner) {
        this.fakeLearner = fakeLearner;
    }

    public String getFreeIds() {
        return freeIds;
    }

    public void setFreeIds(String freeIds) {
        this.freeIds = freeIds;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
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

    public String getTestTypeMaster() {
        return testTypeMaster;
    }

    public void setTestTypeMaster(String testTypeMaster) {
        this.testTypeMaster = testTypeMaster;
    }

    public String getIsTesting() {
        return isTesting;
    }

    public void setIsTesting(String isTesting) {
        this.isTesting = isTesting;
    }

    public String getIsCombo() {
        return isCombo;
    }

    public void setIsCombo(String isCombo) {
        this.isCombo = isCombo;
    }

    public String getTestTypeSubcategory() {
        return testTypeSubcategory;
    }

    public void setTestTypeSubcategory(String testTypeSubcategory) {
        this.testTypeSubcategory = testTypeSubcategory;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public String getSkipTopic() {
        return skipTopic;
    }

    public void setSkipTopic(String skipTopic) {
        this.skipTopic = skipTopic;
    }

    public String getIsInstalment() {
        return isInstalment;
    }

    public void setIsInstalment(String isInstalment) {
        this.isInstalment = isInstalment;
    }

    public String getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(String installmentId) {
        this.installmentId = installmentId;
    }

    public String getInstallmentMeta() {
        return installmentMeta;
    }

    public void setInstallmentMeta(String installmentMeta) {
        this.installmentMeta = installmentMeta;
    }

    public String getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(String registrationFee) {
        this.registrationFee = registrationFee;
    }

    public String getIsSubscription() {
        return isSubscription;
    }

    public void setIsSubscription(String isSubscription) {
        this.isSubscription = isSubscription;
    }

}
