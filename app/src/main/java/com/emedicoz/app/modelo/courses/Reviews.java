package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

/**
 * Created by appsquadz on 28/9/17.
 */

public class Reviews implements Serializable {
    private String id;

    private String text;

    private String profile_picture;

    private String name;

    private String creation_time;

    private String rating;

    private String user_id;

    private String course_fk_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCourse_fk_id() {
        return course_fk_id;
    }

    public void setCourse_fk_id(String course_fk_id) {
        this.course_fk_id = course_fk_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
