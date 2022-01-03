package com.emedicoz.app.modelo.courses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by appsquadz on 28/9/17.
 */

public class Instructor implements Serializable {
    private String id;
    private ArrayList<Reviews> reviews;

    private ArrayList<Course> course_list;

    private String students;

    private String courses;

    private String email;

    private String name;

    private String about;

    private String rating;

    private String review;

    private String profile_pic;

    private Review user_given_review;

    public Review getUser_given_review() {
        return user_given_review;
    }

    public void setUser_given_review(Review user_given_review) {
        this.user_given_review = user_given_review;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudents() {
        return students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Course> getCourse_list() {
        return course_list;
    }

    public void setCourse_list(ArrayList<Course> course_list) {
        this.course_list = course_list;
    }

}