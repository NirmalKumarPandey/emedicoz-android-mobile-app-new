package com.emedicoz.app.response;

import java.io.Serializable;

public class NotificationSettingsResponse implements Serializable {
    private String id;

    private String follow_notification = "";

    private String comment_on_post_notification = "";

    private String user_id;

    private String tag_notification = "";

    private String other_notification = "";

    private String post_like_notification = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollow_notification() {
        return follow_notification;
    }

    public void setFollow_notification(String follow_notification) {
        this.follow_notification = follow_notification;
    }

    public String getComment_on_post_notification() {
        return comment_on_post_notification;
    }

    public void setComment_on_post_notification(String comment_on_post_notification) {
        this.comment_on_post_notification = comment_on_post_notification;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTag_notification() {
        return tag_notification;
    }

    public void setTag_notification(String tag_notification) {
        this.tag_notification = tag_notification;
    }

    public String getOther_notification() {
        return other_notification;
    }

    public void setOther_notification(String other_notification) {
        this.other_notification = other_notification;
    }

    public String getPost_like_notification() {
        return post_like_notification;
    }

    public void setPost_like_notification(String post_like_notification) {
        this.post_like_notification = post_like_notification;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", follow_notification = " + follow_notification + ", comment_on_post_notification = " + comment_on_post_notification + ", user_id = " + user_id + ", tag_notification = " + tag_notification + ", other_notification = " + other_notification + ", post_like_notification = " + post_like_notification + "]";
    }
}