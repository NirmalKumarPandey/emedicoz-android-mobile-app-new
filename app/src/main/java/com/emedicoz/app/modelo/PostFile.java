package com.emedicoz.app.modelo;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/19/17.
 */

public class PostFile implements Serializable {

    private String id;
    private String post_id;
    private String file_type;
    private String link;
    private String file_info;
    private String test_segment_id;
    private String test_result_date;
    private String noOfQuestion;
    private String time;
    private String subject;
    private String rank;

    public String getTest_result_date() {
        return test_result_date;
    }

    public void setTest_result_date(String test_result_date) {
        this.test_result_date = test_result_date;
    }

    public String getNoOfQuestion() {
        return noOfQuestion;
    }

    public void setNoOfQuestion(String noOfQuestion) {
        this.noOfQuestion = noOfQuestion;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getFile_info() {
        return file_info;
    }

    public void setFile_info(String file_info) {
        this.file_info = file_info;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTest_segment_id() {
        return test_segment_id;
    }

    public void setTest_segment_id(String test_segment_id) {
        this.test_segment_id = test_segment_id;
    }
}
