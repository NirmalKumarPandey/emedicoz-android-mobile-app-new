package com.emedicoz.app.modelo.courses.quiz;

import java.io.Serializable;

/**
 * Created by app on 2/2/18.
 */
public class LeaderBoardUserModel implements Serializable {
    private String profile_picture;

    private String name;

    private String marks;
    private String creation_time;

    private String user_id;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "ClassPojo [profile_picture = " + profile_picture + ", name = " + name + ", creation_time = " + creation_time + ", user_id = " + user_id + "]";
    }
}
