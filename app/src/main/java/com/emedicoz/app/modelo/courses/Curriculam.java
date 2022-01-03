package com.emedicoz.app.modelo.courses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by appsquadz on 12/10/17.
 */

public class Curriculam implements Serializable {
    private String title;

    private String topic_id;

    private Topics[] topics;
    private ArrayList<File_meta> file_meta;

    public Topics[] getTopics() {
        return topics;
    }

    public void setTopics(Topics[] topics) {
        this.topics = topics;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<File_meta> getFile_meta() {
        return file_meta;
    }

    public void setFile_meta(ArrayList<File_meta> file_meta) {
        this.file_meta = file_meta;
    }


    // TODO FILE DATA
    public class File_meta implements Serializable {
        private String count;

        private String title;

        private String description;

        private String file;

        private String link;

        private String new_link;

        private String duration;

        private String page_count;

        private String id;

        private String total_questions;

        private String time_in_mins;

        private String key_data;

        private String display_reattempt;

        private String display_v_solution;

        private String test_start_date;

        private String test_end_date;

        private Enc enc_url;

        private String test_result_date;

        private String view_duration = "120";
        private String is_user_attemp;
        private String is_paused;

        public String getTime_in_mins() {
            return time_in_mins;
        }

        public void setTime_in_mins(String time_in_mins) {
            this.time_in_mins = time_in_mins;
        }

        public String getTotal_questions() {
            return total_questions;
        }

        public void setTotal_questions(String total_questions) {
            this.total_questions = total_questions;
        }

        public String getIs_paused() {
            return is_paused;
        }

        public void setIs_paused(String is_paused) {
            this.is_paused = is_paused;
        }

        public String isIs_user_attemp() {
            return is_user_attemp;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPage_count() {
            return page_count;
        }

        public void setPage_count(String page_count) {
            this.page_count = page_count;
        }

        public String getNew_link() {
            return new_link;
        }

        public void setNew_link(String new_link) {
            this.new_link = new_link;
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

        public String getTest_start_date() {
            return test_start_date;
        }

        public void setTest_start_date(String test_start_date) {
            this.test_start_date = test_start_date;
        }

        public String getTest_end_date() {
            return test_end_date;
        }

        public void setTest_end_date(String test_end_date) {
            this.test_end_date = test_end_date;
        }

        public String getTest_result_date() {
            return test_result_date;
        }

        public void setTest_result_date(String test_result_date) {
            this.test_result_date = test_result_date;
        }

        public String getView_duration() {
            return view_duration;
        }

        public void setView_duration(String view_duration) {
            this.view_duration = view_duration;
        }

        public Enc getEnc_url() {
            return enc_url;
        }

        public void setEnc_url(Enc enc_url) {
            this.enc_url = enc_url;
        }
    }
}
