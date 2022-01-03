
package com.emedicoz.app.retrofit.models.mycoursedata;

import com.emedicoz.app.modelo.courses.Course;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("course_list")
    private List<Course> mCourseList;

    public List<Course> getCourseList() {
        return mCourseList;
    }

    public void setCourseList(List<Course> courseList) {
        mCourseList = courseList;
    }

}
