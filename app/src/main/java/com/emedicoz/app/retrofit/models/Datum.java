
package com.emedicoz.app.retrofit.models;

import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("category_info")
    private CourseCategory mCategoryInfo;
    @SerializedName("course_list")
    private List<Course> mCourseList;

    public CourseCategory getCategoryInfo() {
        return mCategoryInfo;
    }

    public void setCategoryInfo(CourseCategory categoryInfo) {
        mCategoryInfo = categoryInfo;
    }

    public List<Course> getCourseList() {
        return mCourseList;
    }

    public void setCourseList(List<Course> courseList) {
        mCourseList = courseList;
    }

}
