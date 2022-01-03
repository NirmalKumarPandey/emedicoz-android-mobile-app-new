package com.emedicoz.app.modelo.liveclass.courses;

import android.widget.SeekBar;

import com.emedicoz.app.modelo.CourseBannerData;
import com.emedicoz.app.modelo.StudentFeedback;
import com.emedicoz.app.modelo.courses.Cards;
import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DescriptionData implements Serializable {
    @SerializedName("basic")
    @Expose
    private DescriptionBasic basic;
    @SerializedName("instructor_data")
    @Expose
    private DescInstructorData instructorData;
    @SerializedName("tiles")
    @Expose
    private ArrayList<Cards> tiles;
    @SerializedName("is_purchased")
    @Expose
    private String isPurchased;
    @SerializedName("is_wishlist")
    @Expose
    private String isWishList;
    @SerializedName("faq")
    @Expose
    private List<DescriptionFAQ> faq = null;
    @SerializedName("course_review")
    @Expose
    private List<DescCourseReview> courseReview = null;
    @SerializedName("total_enrolled")
    @Expose
    private String totalEnrolled;
    @SerializedName("banners")
    @Expose
    private List<CourseBannerData> courseBanner = null;
    @SerializedName("is_cart_added")
    @Expose
    private String is_cart_added;

    @SerializedName("student_feedback")
    @Expose
    private StudentFeedback student_feedback;


    @SerializedName("student_viewing_cat_id")
    @Expose
    private String student_viewing_cat_id;

    public String getStudent_viewing_cat_id() {
        return student_viewing_cat_id;
    }

    public void setStudent_viewing_cat_id(String student_viewing_cat_id) {
        this.student_viewing_cat_id = student_viewing_cat_id;
    }

    public StudentFeedback getStudent_feedback() {
        return student_feedback;
    }

    public void setStudent_feedback(StudentFeedback student_feedback) {
        this.student_feedback = student_feedback;
    }

    public List<CourseBannerData> getCourseBanner() {
        return courseBanner;
    }

    public void setCourseBanner(List<CourseBannerData> courseBanner) {
        this.courseBanner = courseBanner;
    }

    public DescriptionBasic getBasic() {
        return basic;
    }

    public void setBasic(DescriptionBasic basic) {
        this.basic = basic;
    }

    public DescInstructorData getInstructorData() {
        return instructorData;
    }

    public String getWishList() {
        return isWishList;
    }

    public void setWishList(String wishList) {
        isWishList = wishList;
    }

    public void setInstructorData(DescInstructorData instructorData) {
        this.instructorData = instructorData;
    }

    public ArrayList<Cards> getTiles() {
        return GenericUtils.isListEmpty(tiles) ? new ArrayList<>() : tiles;
    }

    public void setTiles(ArrayList<Cards> tiles) {
        this.tiles = tiles;
    }

    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
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

    public String getTotalEnrolled() {
        return totalEnrolled;
    }

    public void setTotalEnrolled(String totalEnrolled) {
        this.totalEnrolled = totalEnrolled;
    }

    public String getIs_cart_added() {
        return is_cart_added;
    }

    public void setIs_cart_added(String is_cart_added) {
        this.is_cart_added = is_cart_added;
    }
}
