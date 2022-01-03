package com.emedicoz.app.modelo.courses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CourseDetail implements Serializable {

    @SerializedName("course_type")
    @Expose
    private String courseType;
    @SerializedName("is_live")
    @Expose
    private String isLive;

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }
}
