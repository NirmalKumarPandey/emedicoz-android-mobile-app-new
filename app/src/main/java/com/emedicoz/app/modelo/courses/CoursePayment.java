package com.emedicoz.app.modelo.courses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoursePayment {

    @SerializedName("course_id")
    @Expose
    private String courseId;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("payment_meta")
    @Expose
    private Object paymentMeta;
    @SerializedName("child_courses")
    @Expose
    private String childCourses;
    @SerializedName("course_price")
    @Expose
    private String coursePrice;
    @SerializedName("subscription_code")
    @Expose
    private String subscriptionCode;
    @SerializedName("expiry")
    @Expose
    private String expiry;

    @SerializedName("course_discount")
    @Expose
    private String discount;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Object getPaymentMeta() {
        return paymentMeta;
    }

    public void setPaymentMeta(Object paymentMeta) {
        this.paymentMeta = paymentMeta;
    }

    public String getChildCourses() {
        return childCourses;
    }

    public void setChildCourses(String childCourses) {
        this.childCourses = childCourses;
    }

    public String getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(String coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
