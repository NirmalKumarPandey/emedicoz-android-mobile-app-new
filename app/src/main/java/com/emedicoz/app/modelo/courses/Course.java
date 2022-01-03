package com.emedicoz.app.modelo.courses;

import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 09/04/17.
 */
public class Course implements Serializable {
    private String tags;

    private String cover_image;

    @SerializedName(value = "learner", alternate = {"total_enrolled", "course_learner"})
    private String learner;

    private String mrp;

    private String state;

    private String currency;

    private String id;

    private String non_dams;

    private String title;

    private String description;

    private String for_dams;

    private String course_category_fk;

    private String publish;

    private String desc_header_image;

    @SerializedName(value = "rating", alternate = {"course_rating_count"})
    private String rating;

    private Review review;

    private String is_locked;

    private String course_type;

    @Expose
    @SerializedName("is_live")
    private String is_live;

    private String is_combo;
    private String is_subscription;

    @Expose
    @SerializedName("is_free_trial")
    private boolean isFreeTrial;

    private String subject_title;

    private String gst;

    private String gst_include;
    @SerializedName("points_conversion_rate")
    private String points_conversion_rate;

    private String share_url;

    private boolean is_purchased;
    private boolean is_wishlist;
    private String is_renew;
    private String validity;
    private String calMrp;
    private boolean discounted = false;
    private boolean is_dams = false;
    private Object subscription_data;

    private boolean is_streaming = false;
    private String live_on;
    private String video_title;
    private String course_tag;

    private String category_tag;

    private String end_date;
    private int course_completion_percentage;
    private String last_updated;
    private String duration;
    @SerializedName("free_message")
    private String free_message;

    @SerializedName("course_discount")
    @Expose
    private String discount = "";


    private String video_id;

    private String current_timestamp;

    private Boolean is_remind_opted;
    private String typeBanner;


    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getCurrent_timestamp() {
        return current_timestamp;
    }

    public void setCurrent_timestamp(String current_timestamp) {
        this.current_timestamp = current_timestamp;
    }

    public Boolean getIs_remind_opted() {
        return is_remind_opted;
    }

    public void setIs_remind_opted(Boolean is_remind_opted) {
        this.is_remind_opted = is_remind_opted;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getCourse_completion_percentage() {
        return course_completion_percentage;
    }

    public void setCourse_completion_percentage(int course_completion_percentage) {
        this.course_completion_percentage = course_completion_percentage;
    }

    public String getShareLink() {
        return share_url;
    }

    public void setShareLink(String share_url) {
        this.share_url = share_url;
    }

    public String getLive_on() {
        return live_on;
    }

    public void setLive_on(String live_on) {
        this.live_on = live_on;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public boolean isIs_streaming() {
        return is_streaming;
    }

    public void setIs_streaming(boolean is_streaming) {
        this.is_streaming = is_streaming;
    }

    public String getSubject_title() {
        return subject_title;
    }

    public void setSubject_title(String subject_title) {
        this.subject_title = subject_title;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getIs_combo() {
        return is_combo;
    }

    public void setIs_combo(String is_combo) {
        this.is_combo = is_combo;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public boolean isIs_dams() {
        return is_dams;
    }

    public void setIs_dams(boolean is_dams) {
        this.is_dams = is_dams;
    }

    public String getCalMrp() {
        return calMrp;
    }

    public void setCalMrp(String calMrp) {
        this.calMrp = calMrp;
    }

    public String getIs_locked() {    // to check that the course content should be locked or not.
        return is_locked;
    }

    public void setIs_locked(String is_locked) {
        this.is_locked = is_locked;
    }

    public String getDesc_header_image() {
        return desc_header_image;
    }

    public void setDesc_header_image(String desc_header_image) {
        this.desc_header_image = desc_header_image;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getMrp() {
        return GenericUtils.isEmpty(mrp) ? "0" : mrp;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNon_dams() {
        return non_dams;
    }

    public void setNon_dams(String non_dams) {
        this.non_dams = non_dams;
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

    public String getFor_dams() {
        return for_dams;
    }

    public void setFor_dams(String for_dams) {
        this.for_dams = for_dams;
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

    public String getRating() {
        return GenericUtils.isEmpty(rating) ? "0" : rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getGst_include() {
        return gst_include;
    }

    public void setGst_include(String gst_include) {
        this.gst_include = gst_include;
    }

    public String getPoints_conversion_rate() {
        return points_conversion_rate;
    }

    public void setPoints_conversion_rate(String points_conversion_rate) {
        this.points_conversion_rate = points_conversion_rate;
    }

    public boolean isIs_purchased() {
        return is_purchased;
    }

    public void setIs_purchased(boolean is_purchased) {
        this.is_purchased = is_purchased;
    }

    public String isIs_renew() {
        return is_renew;
    }

    public void setIs_renew(String is_renew) {
        this.is_renew = is_renew;
    }

    public boolean isIs_wishlist() {
        return is_wishlist;
    }

    public void setIs_wishlist(boolean is_wishlist) {
        this.is_wishlist = is_wishlist;
    }

    public String getIs_subscription() {
        return is_subscription;
    }

    public void setIs_subscription(String is_subscription) {
        this.is_subscription = is_subscription;
    }

    public String getCourse_tag() {
        return course_tag;
    }

    public void setCourse_tag(String course_tag) {
        this.course_tag = course_tag;
    }

    public String getCategory_tag() {
        return category_tag;
    }

    public void setCategory_tag(String category_tag) {
        this.category_tag = category_tag;
    }

    public Object getSubscription_data() {
        return subscription_data;
    }

    public void setSubscription_data(Object subscription_data) {
        this.subscription_data = subscription_data;
    }

    public boolean getIsFreeTrial() {
        return isFreeTrial;
    }

    public void setIsFreeTrial(boolean isFreeTrial) {
        this.isFreeTrial = isFreeTrial;
    }

    public String getFree_message() {
        return free_message;
    }

    public void setFree_message(String free_message) {
        this.free_message = free_message;
    }

    public String getTypeBanner() {
        return typeBanner;
    }

    public void setTypeBanner(String typeBanner) {
        this.typeBanner = typeBanner;
    }

    private boolean is_cart;

    public boolean isIs_cart() {
        return is_cart;
    }

    public void setIs_cart(boolean is_cart) {
        this.is_cart = is_cart;
    }
}

