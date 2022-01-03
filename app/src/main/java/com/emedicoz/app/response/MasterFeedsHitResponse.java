package com.emedicoz.app.response;

import com.emedicoz.app.modelo.Banner;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.courses.Course;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 05-01-2018.
 */

public class MasterFeedsHitResponse {

    private String validate_dams_user;
    private int cpr_view;
    private String custom_display;
    private String qbank_timer;
    private String flashcard;
    private String fanwall_chat;
    private String code;
    private String qrcode;
    private String show_feeds;
    private String show_dvl;
    private String show_podcast;
    private String show_premium_videos;
    private String premium_videos_title;
    private String show_bookmark;
    private String show_dqb;
    private String show_daily_quiz;
    private String show_feed_live_video;

    private String feeds_view;
    private String dvl_view;
    private String bookmark_view;
    private String course_view;
    private String qbank_view;
    private String show_affiliate_program;
    private String show_my_downloads;

    private String show_live_course;
    private String display_combo;
    private String display_test_bookmark;
    private String display_dquiz_bookmark;
    private String display_quiz_bookmark;
    private String show_ppe_course;

    private String pop_msg;
    private String paytm;
    private String android_inapp;
    private String cloud_front_url_prefix;
    private String s3_url_prefix;

    private User user_detail;
    private int cpr_display;
    private ArrayList<People> people_you_may_know_list;
    private ArrayList<Banner> banner_list;
    private ArrayList<People> expert_list;
    private ArrayList<Tags> all_tags;
    private ArrayList<Video> suggested_videos;
    private ArrayList<Course> suggested_course;
    private List<StudyInfo> study_info = null;

    @SerializedName("minimum_followers_stream_wise")
    private int minimumFollowersPerStream;

    public int getMinimumFollowersPerStream() {
        return minimumFollowersPerStream != 0 ? minimumFollowersPerStream : 5;
    }

    public void setMinimumFollowersPerStream(int minimumFollowersPerStream) {
        this.minimumFollowersPerStream = minimumFollowersPerStream;
    }

    public String getShow_ppe_course() {
        return show_ppe_course;
    }

    public void setShow_ppe_course(String show_ppe_course) {
        this.show_ppe_course = show_ppe_course;
    }

    public String getDisplay_test_bookmark() {
        return display_test_bookmark;
    }

    public void setDisplay_test_bookmark(String display_test_bookmark) {
        this.display_test_bookmark = display_test_bookmark;
    }

    public String getDisplay_dquiz_bookmark() {
        return display_dquiz_bookmark;
    }

    public void setDisplay_dquiz_bookmark(String display_dquiz_bookmark) {
        this.display_dquiz_bookmark = display_dquiz_bookmark;
    }

    public String getDisplay_quiz_bookmark() {
        return display_quiz_bookmark;
    }

    public void setDisplay_quiz_bookmark(String display_quiz_bookmark) {
        this.display_quiz_bookmark = display_quiz_bookmark;
    }

    public String getPaytm() {
        return paytm;
    }

    public void setPaytm(String paytm) {
        this.paytm = paytm;
    }

    public String getAndroid_inapp() {
        return android_inapp;
    }

    public void setAndroid_inapp(String android_inapp) {
        this.android_inapp = android_inapp;
    }

    public int getCpr_display() {
        return cpr_display;
    }

    public void setCpr_display(int cpr_display) {
        this.cpr_display = cpr_display;
    }

    public ArrayList<Course> getSuggested_course() {
        return suggested_course;
    }

    public void setSuggested_course(ArrayList<Course> suggested_course) {
        this.suggested_course = suggested_course;
    }

    public ArrayList<Video> getSuggested_videos() {
        return suggested_videos;
    }

    public void setSuggested_videos(ArrayList<Video> suggested_videos) {
        this.suggested_videos = suggested_videos;
    }

    public ArrayList<Tags> getAll_tags() {
        return all_tags;
    }

    public void setAll_tags(ArrayList<Tags> all_tags) {
        this.all_tags = all_tags;
    }

    public ArrayList<People> getPeople_you_may_know_list() {
        return people_you_may_know_list;
    }

    public void setPeople_you_may_know_list(ArrayList<People> people_you_may_know_list) {
        this.people_you_may_know_list = people_you_may_know_list;
    }

    public ArrayList<Banner> getBanner_list() {
        return banner_list;
    }

    public void setBanner_list(ArrayList<Banner> banner_list) {
        this.banner_list = banner_list;
    }

    public ArrayList<People> getExpert_list() {
        return expert_list;
    }

    public void setExpert_list(ArrayList<People> expert_list) {
        this.expert_list = expert_list;
    }


    public String getValidate_dams_user() {
        return validate_dams_user;
    }

    public void setValidate_dams_user(String validate_dams_user) {
        this.validate_dams_user = validate_dams_user;
    }

    public int getCpr_view() {
        return cpr_view;
    }

    public void setCpr_view(int cpr_view) {
        this.cpr_view = cpr_view;
    }

    public String getCustom_display() {
        return custom_display;
    }

    public void setCustom_display(String custom_display) {
        this.custom_display = custom_display;
    }

    public String getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(String flashcard) {
        this.flashcard = flashcard;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQbank_timer() {
        return qbank_timer;
    }

    public void setQbank_timer(String qbank_timer) {
        this.qbank_timer = qbank_timer;
    }

    public String getFanwall_chat() {
        return fanwall_chat;
    }

    public void setFanwall_chat(String fanwall_chat) {
        this.fanwall_chat = fanwall_chat;
    }

    public String getShow_live_course() {
        return show_live_course;
    }

    public void setShow_live_course(String show_live_course) {
        this.show_live_course = show_live_course;
    }

    public String getDisplay_combo() {
        return display_combo;
    }

    public void setDisplay_combo(String display_combo) {
        this.display_combo = display_combo;
    }

    public String getShow_feeds() {
        return feeds_view;
    }

    public void setShow_feeds(String feeds_view) {
        this.feeds_view = feeds_view;
    }

    public String getShow_dvl() {
        return dvl_view;
    }

    public void setShow_dvl(String dvl_view) {
        this.dvl_view = dvl_view;
    }

    public String getShow_dqb() {
        return qbank_view;
    }

    public void setShow_dqb(String qbank_view) {
        this.qbank_view = qbank_view;
    }

    public String getShow_bookmark() {
        return bookmark_view;
    }

    public void setShow_bookmark(String bookmark_view) {
        this.bookmark_view = bookmark_view;
    }

    public String getCourse_view() {
        return course_view;
    }

    public void setCourse_view(String course_view) {
        this.course_view = course_view;
    }

    public String getShow_daily_quiz() {
        return show_daily_quiz;
    }

    public void setShow_daily_quiz(String show_daily_quiz) {
        this.show_daily_quiz = show_daily_quiz;
    }

    public String getShow_feed_live_video() {
        return show_feed_live_video;
    }

    public void setShow_feed_live_video(String show_feed_live_video) {
        this.show_feed_live_video = show_feed_live_video;
    }

    public User getUser_detail() {
        return user_detail;
    }

    public void setUser_detail(User user_detail) {
        this.user_detail = user_detail;
    }

    public String getPop_msg() {
        return pop_msg;
    }

    public void setPop_msg(String pop_msg) {
        this.pop_msg = pop_msg;
    }

    @Override
    public String toString() {
        return "ClassPojo [people_you_may_know_list = " + people_you_may_know_list + ", banner_list = " + banner_list + ", expert_list = " + expert_list + "]";
    }


    public String getShow_affiliate_program() {
        return show_affiliate_program;
    }

    public void setShow_affiliate_program(String show_affiliate_program) {
        this.show_affiliate_program = show_affiliate_program;
    }

    public List<StudyInfo> getStudy_info() {
        return study_info;
    }

    public void setStudy_info(List<StudyInfo> study_info) {
        this.study_info = study_info;
    }

    public String getShow_my_downloads() {
        return show_my_downloads;
    }

    public void setShow_my_downloads(String show_my_downloads) {
        this.show_my_downloads = show_my_downloads;
    }

    public String getS3_url_prefix() {
        return s3_url_prefix;
    }

    public void setS3_url_prefix(String s3_url_prefix) {
        this.s3_url_prefix = s3_url_prefix;
    }

    public String getCloud_front_url_prefix() {
        return cloud_front_url_prefix;
    }

    public void setCloud_front_url_prefix(String cloud_front_url_prefix) {
        this.cloud_front_url_prefix = cloud_front_url_prefix;
    }

    public String getShow_podcast() {
        return show_podcast;
    }

    public void setShow_podcast(String show_podcast) {
        this.show_podcast = show_podcast;
    }

    public String getPremium_videos_title() {
        return premium_videos_title;
    }

    public void setPremium_videos_title(String premium_videos_title) {
        this.premium_videos_title = premium_videos_title;
    }

    public String getShow_premium_videos() {
        return show_premium_videos;
    }

    public void setShow_premium_videos(String show_premium_videos) {
        this.show_premium_videos = show_premium_videos;



    }


    private String news_article;


    public String getNews_article() {
        return news_article;
    }

    public void setNews_article(String news_article) {
        this.news_article = news_article;
    }
}
