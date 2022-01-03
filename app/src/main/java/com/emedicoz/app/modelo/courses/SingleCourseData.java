package com.emedicoz.app.modelo.courses;

import com.emedicoz.app.installment.model.Installment;
import com.emedicoz.app.utilso.GenericUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by appsquadz on 28/9/17.
 */

public class SingleCourseData implements Serializable {
    private String tags;

    private Reviews[] reviews;

    private List<Installment> installment;

    private String cover_image;

    private String cover_video;

    private String course_type;

    private String review_count;

    private String learner;

    private String mrp;

    private String state;

    private String id;

    private String title;

    private String non_dams;

    private String description;

    private String for_dams;

    private String rating;

    private String is_instalment;
    private String is_subscription;
    private String is_purchased;
    private String is_renew;
    private String validity;
    private String course_category_fk;
    private String publish;
    private Instructor instructor_data;
    private Curriculam curriculam;
    private Review review;
    private String desc_header_image;
    private ArrayList<Course> related_course;
    private String gst;
    private String gst_include;  // to check that the course price is included GST or not.
    private String is_locked;  // to check that the course content should be locked or not.
    private String points_conversion_rate;
    private String total_bookmarked;
    private String is_combo;
    private String is_live;
    private String child_courses;
    private String course_tag;
    private String category_tag;


    public String getCategory_tag() {
        return category_tag;
    }

    public void setCategory_tag(String category_tag) {
        this.category_tag = category_tag;
    }

    public String getCourse_tag() {
        return course_tag;
    }

    public void setCourse_tag(String course_tag) {
        this.course_tag = course_tag;
    }

    public String getIs_subscription() {
        return is_subscription;
    }

    public void setIs_subscription(String is_subscription) {
        this.is_subscription = is_subscription;
    }

    public String getChild_courses() {
        return child_courses;
    }

    public void setChild_courses(String child_courses) {
        this.child_courses = child_courses;
    }

    public String getIs_combo() {
        return is_combo;
    }

    public void setIs_combo(String is_combo) {
        this.is_combo = is_combo;
    }

    public String getIs_live() {
        return GenericUtils.isEmpty(is_live) ? "" : is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }

    public String getIs_instalment() {
        return is_instalment;
    }

    public void setIs_instalment(String is_instalment) {
        this.is_instalment = is_instalment;
    }

    public List<Installment> getInstallment() {
        return installment;
    }

    public void setInstallment(List<Installment> installment) {
        this.installment = installment;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getGst_include() {
        return gst_include;
    }

    public void setGst_include(String gst_include) {
        this.gst_include = gst_include;
    }

    public String getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(String is_locked) {
        this.is_locked = is_locked;
    }

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

    public String getDesc_header_image() {
        return desc_header_image;
    }

    public void setDesc_header_image(String desc_header_image) {
        this.desc_header_image = desc_header_image;
    }

    public String getCover_video() {
        return cover_video;
    }

    public void setCover_video(String cover_video) {
        this.cover_video = cover_video;
    }

    public ArrayList<Course> getRelated_course() {
        return related_course;
    }

    public void setRelated_course(ArrayList<Course> related_course) {
        this.related_course = related_course;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getIs_purchased() {
        return is_purchased;
    }

    public void setIs_purchased(String is_purchased) {
        this.is_purchased = is_purchased;
    }

    public Curriculam getCurriculam() {
        return curriculam;
    }

    public void setCurriculam(Curriculam curriculam) {
        this.curriculam = curriculam;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Reviews[] getReviews() {
        return reviews;
    }

    public void setReviews(Reviews[] reviews) {
        this.reviews = reviews;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getNon_dams() {
        return non_dams;
    }

    public void setNon_dams(String non_dams) {
        this.non_dams = non_dams;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFor_dams() {
        return for_dams;
    }

    public void setFor_dams(String for_dams) {
        this.for_dams = for_dams;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCourse_category_fk() {
        return course_category_fk;
    }

    public void setCourse_category_fk(String course_category_fk) {
        this.course_category_fk = course_category_fk;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public Instructor getInstructor_data() {
        return instructor_data;
    }

    public void setInstructor_data(Instructor instructor_data) {
        this.instructor_data = instructor_data;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getTotal_bookmarked() {
        return total_bookmarked;
    }

    public void setTotal_bookmarked(String total_bookmarked) {
        this.total_bookmarked = total_bookmarked;
    }

    public boolean isIs_renew() {
        return is_renew != null && is_renew.equals("1");
    }

    public void setIs_renew(String is_renew) {
        this.is_renew = is_renew;
    }
}
