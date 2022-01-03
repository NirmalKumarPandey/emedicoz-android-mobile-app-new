package com.emedicoz.app.modelo.testseries;

import com.emedicoz.app.installment.model.PaymentMeta;

import java.io.Serializable;

public class OrderHistoryData implements Serializable {

    private String id;
    private String user_id;
    private String is_dams;
    private String course_id;
    private String pre_transaction_id;
    private String post_transaction_id;
    private String refund_id;
    private String payIvia;
    private String coupon_applied;
    private String course_price;
    private String points_used;
    private String points_rate;
    private String transaction_status;
    private String instructor_id;
    private String instructor_share;
    private String creation_time;
    private String is_complete;
    private String is_validity;
    private String validity;
    private String title;
    private String cover_image;
    private String course_learner;
    private String net_amt;
    private String gst;
    private String payment_mode;
    private String upcoming_emi_amount;
    private String upcoming_emi_date;
    private String invoice_no;
    private String dues;
    private String note;
    private String subscription_code;
    private String upcoming_attemt;
    private String course_type;
    private String is_combo;
    private String is_live;
    private String child_courses;
    private String is_subscription;
    private String is_instalment;
    private PaymentMeta payment_meta;

    public String getIs_subscription() {
        return is_subscription;
    }

    public void setIs_subscription(String is_subscription) {
        this.is_subscription = is_subscription;
    }

    public String getIs_instalment() {
        return is_instalment;
    }

    public void setIs_instalment(String is_instalment) {
        this.is_instalment = is_instalment;
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
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getUpcoming_attemt() {
        return upcoming_attemt;
    }

    public void setUpcoming_attemt(String upcoming_attemt) {
        this.upcoming_attemt = upcoming_attemt;
    }

    public String getSubscription_code() {
        return subscription_code;
    }

    public void setSubscription_code(String subscription_code) {
        this.subscription_code = subscription_code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDues() {
        return dues;
    }

    public void setDues(String dues) {
        this.dues = dues;
    }

    public PaymentMeta getPaymentMeta() {
        return payment_meta;
    }

    public void setPaymentMeta(PaymentMeta payment_meta) {
        this.payment_meta = payment_meta;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getUpcoming_emi_date() {
        return upcoming_emi_date;
    }

    public void setUpcoming_emi_date(String upcoming_emi_date) {
        this.upcoming_emi_date = upcoming_emi_date;
    }

    public String getUpcoming_emi_amount() {
        return upcoming_emi_amount;
    }

    public void setUpcoming_emi_amount(String upcoming_emi_amount) {
        this.upcoming_emi_amount = upcoming_emi_amount;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_dams() {
        return is_dams;
    }

    public void setIs_dams(String is_dams) {
        this.is_dams = is_dams;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getPre_transaction_id() {
        return pre_transaction_id;
    }

    public void setPre_transaction_id(String pre_transaction_id) {
        this.pre_transaction_id = pre_transaction_id;
    }

    public String getPost_transaction_id() {
        return post_transaction_id;
    }

    public void setPost_transaction_id(String post_transaction_id) {
        this.post_transaction_id = post_transaction_id;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getPayIvia() {
        return payIvia;
    }

    public void setPayIvia(String payIvia) {
        this.payIvia = payIvia;
    }

    public String getCoupon_applied() {
        return coupon_applied;
    }

    public void setCoupon_applied(String coupon_applied) {
        this.coupon_applied = coupon_applied;
    }

    public String getCourse_price() {
        return course_price;
    }

    public void setCourse_price(String course_price) {
        this.course_price = course_price;
    }

    public String getPoints_used() {
        return points_used;
    }

    public void setPoints_used(String points_used) {
        this.points_used = points_used;
    }

    public String getPoints_rate() {
        return points_rate;
    }

    public void setPoints_rate(String points_rate) {
        this.points_rate = points_rate;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }

    public String getInstructor_id() {
        return instructor_id;
    }

    public void setInstructor_id(String instructor_id) {
        this.instructor_id = instructor_id;
    }

    public String getInstructor_share() {
        return instructor_share;
    }

    public void setInstructor_share(String instructor_share) {
        this.instructor_share = instructor_share;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(String is_complete) {
        this.is_complete = is_complete;
    }

    public String getIs_validity() {
        return is_validity;
    }

    public void setIs_validity(String is_validity) {
        this.is_validity = is_validity;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getCourse_learner() {
        return course_learner;
    }

    public void setCourse_learner(String course_learner) {
        this.course_learner = course_learner;
    }

    public String getNet_amt() {
        return net_amt;
    }

    public void setNet_amt(String net_amt) {
        this.net_amt = net_amt;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }
}
