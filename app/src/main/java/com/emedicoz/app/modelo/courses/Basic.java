package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

public class Basic implements Serializable {
    private String image_icon;

    private String id;

    private String title;

    private String course_sp;

    private String course_type;

    private String desc_header_image;

    private String cover_image;

    private String description;

    private String validity;

    private String color_code;

    private String mrp;

    private String course_attribute;

    private String l_text;

    private String r_text;

    private String rating;

    private String for_dams;

    private String non_dams;

    private String gst;

    private String skip_topic;

    private String points_conversion_rate;

    private String gst_include;

    private String learner;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getPoints_conversion_rate() {
        return points_conversion_rate;
    }

    public void setPoints_conversion_rate(String points_conversion_rate) {
        this.points_conversion_rate = points_conversion_rate;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_icon() {
        return image_icon;
    }

    public void setImage_icon(String image_icon) {
        this.image_icon = image_icon;
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

    public String getCourse_sp() {
        return course_sp;
    }

    public void setCourse_sp(String course_sp) {
        this.course_sp = course_sp;
    }

    public String getFor_dams() {
        return for_dams;
    }

    public void setFor_dams(String for_dams) {
        this.for_dams = for_dams;
    }

    public String getNon_dams() {
        return non_dams;
    }

    public void setNon_dams(String non_dams) {
        this.non_dams = non_dams;
    }

    public String getDesc_header_image() {
        return desc_header_image;
    }

    public void setDesc_header_image(String desc_header_image) {
        this.desc_header_image = desc_header_image;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getCourse_attribute() {
        return course_attribute;
    }

    public void setCourse_attribute(String course_attribute) {
        this.course_attribute = course_attribute;
    }

    public String getL_text() {
        return l_text;
    }

    public void setL_text(String l_text) {
        this.l_text = l_text;
    }

    public String getR_text() {
        return r_text;
    }

    public void setR_text(String r_text) {
        this.r_text = r_text;
    }

    public String getSkip_topic() {
        return skip_topic;
    }

    public void setSkip_topic(String skip_topic) {
        this.skip_topic = skip_topic;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
}

