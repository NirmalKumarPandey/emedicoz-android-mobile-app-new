package com.emedicoz.app.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RelatedCourseCommentData {
    @SerializedName("related_course")
    @Expose
    private ArrayList<RelatedCourses> relatedCourse = null;
    @SerializedName("points_conversion_rate")
    @Expose
    private String pointsConversionRate;

    public ArrayList<RelatedCourses> getRelatedCourse() {
        return relatedCourse;
    }

    public void setRelatedCourse(ArrayList<RelatedCourses> relatedCourse) {
        this.relatedCourse = relatedCourse;
    }

    public String getPointsConversionRate() {
        return pointsConversionRate;
    }

    public void setPointsConversionRate(String pointsConversionRate) {
        this.pointsConversionRate = pointsConversionRate;
    }
}
