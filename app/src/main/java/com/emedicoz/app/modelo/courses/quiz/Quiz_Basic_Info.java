package com.emedicoz.app.modelo.courses.quiz;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 11/01/17.
 */

public class Quiz_Basic_Info implements Serializable {
    private String pass_percentage;

    private String test_series_name;

    private String test_end_date;

    private String test_start_date;

    private String subject;

    private String negative_marking;

    private String test_type;

    private String id;

    private String end_date;

    private String allow_duplicate_rank;

    private String difficulty_level;

    private String marks_per_question;

    private String description;

    private String skip_rank;

    private String show_question_time;

    private String time_in_mins;

    private String test_price;

    private String mandatory_check;

    private String seg_id;

    private String general_message;

    private String fail_message;

    private String answer_shuffle;

    private String time_boundation;

    private String consider_time;

    private String end_time;

    private String session;

    private String pass_message;

    private String shuffle;

    private String start_time;

    private String allow_user_move;

    private String start_date;

    private String total_questions;

    private String total_marks;

    private String set_type;

    private String display_qid;

    private String is_locked;

    private String is_paused;

    private String display_bubble;

    public String getPass_percentage() {
        return pass_percentage;
    }

    public void setPass_percentage(String pass_percentage) {
        this.pass_percentage = pass_percentage;
    }

    public String getTest_end_date() {
        return test_end_date;
    }

    public void setTest_end_date(String test_end_date) {
        this.test_end_date = test_end_date;
    }

    public String getTest_start_date() {
        return test_start_date;
    }

    public void setTest_start_date(String test_start_date) {
        this.test_start_date = test_start_date;
    }

    public String getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(String is_locked) {
        this.is_locked = is_locked;
    }

    public String getIs_paused() {
        return is_paused;
    }

    public void setIs_paused(String is_paused) {
        this.is_paused = is_paused;
    }

    public String getTest_series_name() {
        return test_series_name;
    }

    public void setTest_series_name(String test_series_name) {
        this.test_series_name = test_series_name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNegative_marking() {
        return negative_marking;
    }

    public void setNegative_marking(String negative_marking) {
        this.negative_marking = negative_marking;
    }

    public String getSeg_id() {
        return seg_id;
    }

    public void setSeg_id(String seg_id) {
        this.seg_id = seg_id;
    }

    public String getTest_type() {
        return test_type;
    }

    public void setTest_type(String test_type) {
        this.test_type = test_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getAllow_duplicate_rank() {
        return allow_duplicate_rank;
    }

    public void setAllow_duplicate_rank(String allow_duplicate_rank) {
        this.allow_duplicate_rank = allow_duplicate_rank;
    }

    public String getDifficulty_level() {
        return difficulty_level;
    }

    public void setDifficulty_level(String difficulty_level) {
        this.difficulty_level = difficulty_level;
    }

    public String getMarks_per_question() {
        return marks_per_question;
    }

    public void setMarks_per_question(String marks_per_question) {
        this.marks_per_question = marks_per_question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkip_rank() {
        return skip_rank;
    }

    public void setSkip_rank(String skip_rank) {
        this.skip_rank = skip_rank;
    }

    public String getShow_question_time() {
        return show_question_time;
    }

    public void setShow_question_time(String show_question_time) {
        this.show_question_time = show_question_time;
    }

    public String getTime_in_mins() {
        return time_in_mins;
    }

    public void setTime_in_mins(String time_in_mins) {
        this.time_in_mins = time_in_mins;
    }

    public String getTest_price() {
        return test_price;
    }

    public void setTest_price(String test_price) {
        this.test_price = test_price;
    }

    public String getMandatory_check() {
        return mandatory_check;
    }

    public void setMandatory_check(String mandatory_check) {
        this.mandatory_check = mandatory_check;
    }

    public String getGeneral_message() {
        return general_message;
    }

    public void setGeneral_message(String general_message) {
        this.general_message = general_message;
    }

    public String getFail_message() {
        return fail_message;
    }

    public void setFail_message(String fail_message) {
        this.fail_message = fail_message;
    }

    public String getAnswer_shuffle() {
        return answer_shuffle;
    }

    public void setAnswer_shuffle(String answer_shuffle) {
        this.answer_shuffle = answer_shuffle;
    }

    public String getTime_boundation() {
        return time_boundation;
    }

    public void setTime_boundation(String time_boundation) {
        this.time_boundation = time_boundation;
    }

    public String getConsider_time() {
        return consider_time;
    }

    public void setConsider_time(String consider_time) {
        this.consider_time = consider_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPass_message() {
        return pass_message;
    }

    public void setPass_message(String pass_message) {
        this.pass_message = pass_message;
    }

    public String getShuffle() {
        return shuffle;
    }

    public void setShuffle(String shuffle) {
        this.shuffle = shuffle;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getAllow_user_move() {
        return allow_user_move;
    }

    public void setAllow_user_move(String allow_user_move) {
        this.allow_user_move = allow_user_move;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(String total_questions) {
        this.total_questions = total_questions;
    }

    public String getTotal_marks() {
        return total_marks;
    }

    public void setTotal_marks(String total_marks) {
        this.total_marks = total_marks;
    }


    public String getSet_type() {
        return set_type;
    }

    public void setSet_type(String set_type) {
        this.set_type = set_type;
    }

    public String getDisplay_qid() {
        return display_qid;
    }

    public void setDisplay_qid(String display_qid) {
        this.display_qid = display_qid;
    }

    public String getDisplay_bubble() {
        return display_bubble;
    }

    public void setDisplay_bubble(String display_bubble) {
        this.display_bubble = display_bubble;
    }
}