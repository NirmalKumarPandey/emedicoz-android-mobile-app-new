package com.emedicoz.app.pastpaperexplanation.model;

import com.emedicoz.app.installment.model.Installment;
import com.emedicoz.app.modelo.courses.Instructor;
import com.emedicoz.app.modelo.liveclass.courses.DescCourseReview;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PPEData implements Serializable {

    @SerializedName("id")
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
    @SerializedName("test_type_master")
    @Expose
    private String testTypeMaster;
    @SerializedName("desc_header_image")
    @Expose
    private String descHeaderImage;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
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
    @SerializedName("is_instalment")
    @Expose
    private String isInstalment;
    @SerializedName("is_subscription")
    @Expose
    private String isSubscription;
    @SerializedName("installment")
    @Expose
    private List<Installment> installment = null;
    @SerializedName("user_installment_data")
    @Expose
    private List<Object> userInstallmentData = null;
    @SerializedName("points_conversion_rate")
    @Expose
    private String pointsConversionRate;
    @SerializedName("gst")
    @Expose
    private String gst;
    @SerializedName("child_courses")
    @Expose
    private String childCourses;
    @SerializedName("curriculam")
    @Expose
    private Curriculam curriculam;
    @SerializedName("is_purchased")
    @Expose
    private String isPurchased;
    @SerializedName("instructor_data")
    @Expose
    private Instructor instructorData;
    @SerializedName("faq")
    @Expose
    private List<DescriptionFAQ> faq = null;
    @SerializedName("course_review")
    @Expose
    private List<DescCourseReview> courseReview = null;

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

    public String getTestTypeMaster() {
        return testTypeMaster;
    }

    public void setTestTypeMaster(String testTypeMaster) {
        this.testTypeMaster = testTypeMaster;
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

    public String getIsInstalment() {
        return isInstalment;
    }

    public void setIsInstalment(String isInstalment) {
        this.isInstalment = isInstalment;
    }

    public String getIsSubscription() {
        return isSubscription;
    }

    public void setIsSubscription(String isSubscription) {
        this.isSubscription = isSubscription;
    }

    public List<Installment> getInstallment() {
        return installment;
    }

    public void setInstallment(List<Installment> installment) {
        this.installment = installment;
    }

    public List<Object> getUserInstallmentData() {
        return userInstallmentData;
    }

    public void setUserInstallmentData(List<Object> userInstallmentData) {
        this.userInstallmentData = userInstallmentData;
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

    public Curriculam getCurriculam() {
        return curriculam;
    }

    public void setCurriculam(Curriculam curriculam) {
        this.curriculam = curriculam;
    }

    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
    }

    public String getChildCourses() {
        return childCourses;
    }

    public void setChildCourses(String childCourses) {
        this.childCourses = childCourses;
    }

    public Instructor getInstructorData() {
        return instructorData;
    }

    public void setInstructorData(Instructor instructorData) {
        this.instructorData = instructorData;
    }

    public List<DescriptionFAQ> getFaq() {
        return faq;
    }

    public void setFaq(List<DescriptionFAQ> faq) {
        this.faq = faq;
    }

    public List<DescCourseReview> getCourseReview() {
        return courseReview;
    }

    public void setCourseReview(List<DescCourseReview> courseReview) {
        this.courseReview = courseReview;
    }
}
