package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 10/14/17.
 */

public class FAQ implements Serializable {

    private String id;

    private String description;

    private String question;

    private String course_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", description = " + description + ", question = " + question + ", course_id = " + course_id + "]";
    }
}

