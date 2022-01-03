package com.emedicoz.app.modelo.liveclass.courses;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DescInstructorData implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName(Constants.Extras.NAME)
    @Expose
    private String name;
    @SerializedName(Const.EMAIL)
    @Expose
    private String email;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    @SerializedName("instructor_rating")
    @Expose
    private String instructorRating;

    @SerializedName("instructor_reviews")
    @Expose
    private String instructorReviews;

    @SerializedName("instructor_students")
    @Expose
    private String instructorStudents;

    @SerializedName("instructor_courses")
    @Expose
    private String instructorCourses;

    @SerializedName("instructor_designation")
    @Expose
    private String instructorDesignation;

    @SerializedName("instructor_other_course_ids")
    @Expose
    private String instructorOtherCourseIds;

    @SerializedName("instructor_description")
    @Expose
    private String instructorDescription;

    public String getInstructorDescription() {
        return instructorDescription;
    }

    public void setInstructorDescription(String instructorDescription) {
        this.instructorDescription = instructorDescription;
    }

    public String getInstructorOtherCourseIds() {
        return instructorOtherCourseIds;
    }

    public void setInstructorOtherCourseIds(String instructorOtherCourseIds) {
        this.instructorOtherCourseIds = instructorOtherCourseIds;
    }

    public String getInstructorRating() {
        return instructorRating;
    }

    public void setInstructorRating(String instructorRating) {
        this.instructorRating = instructorRating;
    }

    public String getInstructorReviews() {
        return instructorReviews;
    }

    public void setInstructorReviews(String instructorReviews) {
        this.instructorReviews = instructorReviews;
    }

    public String getInstructorStudents() {
        return instructorStudents;
    }

    public void setInstructorStudents(String instructorStudents) {
        this.instructorStudents = instructorStudents;
    }

    public String getInstructorCourses() {
        return instructorCourses;
    }

    public void setInstructorCourses(String instructorCourses) {
        this.instructorCourses = instructorCourses;
    }

    public String getInstructorDesignation() {
        return instructorDesignation;
    }

    public void setInstructorDesignation(String instructorDesignation) {
        this.instructorDesignation = instructorDesignation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}
