package com.emedicoz.app.modelo.courses;

import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestSeries implements Serializable {
    private String test_start_date;

    private String test_description;

    private String test_series_name;

    private String test_type_master;

    private String segment_fk;

    private String type;

    private String test_end_date;

    private String test_type_title;

    private String is_paused;

    private String test_result_date;

    private String topic_name;

    private String video_based;

    private String test_series_id;

    private String id;

    private String topic_id;

    private String element_fk;

    private String key_data;

    private String is_user_attemp;

    private String topic;

    private String total_questions;

    private String is_locked;

    private String time_in_mins;

    private String subject_id;

    private String display_reattempt;

    private String display_v_solution;

    private String avg_rating;

    private String marks;

    private String percentage;

    private String is_new;

    @SerializedName("display_review_answer")
    @Expose
    private String display_review_answer;

    public String getDisplay_review_answer() {
        return display_review_answer;
    }

    public void setDisplay_review_answer(String display_review_answer) {
        this.display_review_answer = display_review_answer;
    }

    public String getTest_start_date() {
        return !GenericUtils.isEmpty(test_start_date) ? test_start_date : "0";
    }

    public void setTest_start_date(String test_start_date) {
        this.test_start_date = test_start_date;
    }

    public String getTest_description() {
        return test_description;
    }

    public void setTest_description(String test_description) {
        this.test_description = test_description;
    }

    public String getVideo_based() {
        return video_based;
    }

    public void setVideo_based(String video_based) {
        this.video_based = video_based;
    }

    public String getTest_series_name() {
        return test_series_name;
    }

    public void setTest_series_name(String test_series_name) {
        this.test_series_name = test_series_name;
    }

    public String getTest_type_master() {
        return test_type_master;
    }

    public void setTest_type_master(String test_type_master) {
        this.test_type_master = test_type_master;
    }

    public String getSegment_fk() {
        return segment_fk;
    }

    public void setSegment_fk(String segment_fk) {
        this.segment_fk = segment_fk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTest_end_date() {
        return !GenericUtils.isEmpty(test_end_date) ? test_end_date : "0";
    }

    public void setTest_end_date(String test_end_date) {
        this.test_end_date = test_end_date;
    }

    public String getTest_type_title() {
        return test_type_title;
    }

    public void setTest_type_title(String test_type_title) {
        this.test_type_title = test_type_title;
    }

    public String getIs_paused() {
        return is_paused;
    }

    public void setIs_paused(String is_paused) {
        this.is_paused = is_paused;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public String getTest_series_id() {
        return test_series_id;
    }

    public void setTest_series_id(String test_series_id) {
        this.test_series_id = test_series_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getElement_fk() {
        return element_fk;
    }

    public void setElement_fk(String element_fk) {
        this.element_fk = element_fk;
    }

    public String getKey_data() {
        return key_data;
    }

    public void setKey_data(String key_data) {
        this.key_data = key_data;
    }

    public String getIs_user_attemp() {
        return is_user_attemp;
    }

    public void setIs_user_attemp(String is_user_attemp) {
        this.is_user_attemp = is_user_attemp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(String total_questions) {
        this.total_questions = total_questions;
    }

    public String getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(String is_locked) {
        this.is_locked = is_locked;
    }

    public String getTime_in_mins() {
        return time_in_mins;
    }

    public void setTime_in_mins(String time_in_mins) {
        this.time_in_mins = time_in_mins;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getTest_result_date() {
        return test_result_date;
    }

    public void setTest_result_date(String test_result_date) {
        this.test_result_date = test_result_date;
    }

    public String getDisplay_reattempt() {
        return display_reattempt;
    }

    public void setDisplay_reattempt(String display_reattempt) {
        this.display_reattempt = display_reattempt;
    }

    public String getDisplay_v_solution() {
        return display_v_solution;
    }

    public void setDisplay_v_solution(String display_v_solution) {
        this.display_v_solution = display_v_solution;
    }

    public String getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(String avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }

    @Override
    public String toString() {
        return "ClassPojo [test_start_date = " + test_start_date + ", test_description = " + test_description + ", test_series_name = " + test_series_name + ", test_type_master = " + test_type_master + ", segment_fk = " + segment_fk + ", type = " + type + ", test_end_date = " + test_end_date + ", test_type_title = " + test_type_title + ", is_paused = " + is_paused + ", topic_name = " + topic_name + ", test_series_id = " + test_series_id + ", id = " + id + ", topic_id = " + topic_id + ", element_fk = " + element_fk + ", key_data = " + key_data + ", is_user_attemp = " + is_user_attemp + "]";
    }
}
			
			