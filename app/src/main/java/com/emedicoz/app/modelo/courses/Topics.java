package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

public class Topics implements Serializable {
    private String total;

    private String topic_id;

    private String title;

    private String desc_header_image;

    private String cover_image;

    private String validity;

    private String is_combo;

    private String is_new;

    private String course_type;

    private String completed;

    private String sub_img_url;

    private float progress;

    public String getIs_combo() {
        return is_combo;
    }

    public void setIs_combo(String is_combo) {
        this.is_combo = is_combo;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getDesc_header_image() {
        return desc_header_image;
    }

    public void setDesc_header_image(String desc_header_image) {
        this.desc_header_image = desc_header_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getSub_img_url() {
        return sub_img_url;
    }

    public void setSub_img_url(String sub_img_url) {
        this.sub_img_url = sub_img_url;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }

    @Override
    public String toString() {
        return "ClassPojo [total = " + total + ", topic_id = " + topic_id + ", title = " + title + "]";
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
			
			