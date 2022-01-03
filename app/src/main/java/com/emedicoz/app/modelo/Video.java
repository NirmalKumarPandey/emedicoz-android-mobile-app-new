package com.emedicoz.app.modelo;

import com.emedicoz.app.modelo.courses.Enc;

import java.io.Serializable;

/**
 * Created by admin1 on 28/10/17.
 */

public class Video implements Serializable {
    private String initial_view;

    private String tags;

    private String for_non_dams;

    private String video_type;

    private String allow_comments;

    private String is_like;

    private String video_title;

    private String featured;

    private Enc enc_url;

    private String video_desc;

    private String is_viewed;

    private String id;

    private String is_new;

    private String thumbnail_url;

    private String end_date;

    private String sub_cat;

    private String views;

    private String likes;

    private String for_dams;

    private String author_name;

    private String creation_time;

    private String main_cat;

    private String URL;

    private String start_date;

    private String comments;
    private String screen_tag;
    private String is_bookmarked;
    private String live_url;

    public String getLive_url() {
        return live_url;
    }

    public void setLive_url(String live_url) {
        this.live_url = live_url;
    }

    public String getIs_bookmarked() {
        return is_bookmarked;
    }

    public void setIs_bookmarked(String is_bookmarked) {
        this.is_bookmarked = is_bookmarked;
    }

    public String getInitial_view() {
        return initial_view;
    }

    public void setInitial_view(String initial_view) {
        this.initial_view = initial_view;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getFor_non_dams() {
        return for_non_dams;
    }

    public void setFor_non_dams(String for_non_dams) {
        this.for_non_dams = for_non_dams;
    }

    public String getScreen_tag() {
        return screen_tag;
    }

    public void setScreen_tag(String screen_tag) {
        this.screen_tag = screen_tag;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }

    public String getAllow_comments() {
        return allow_comments;
    }

    public void setAllow_comments(String allow_comments) {
        this.allow_comments = allow_comments;
    }

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getVideo_desc() {
        return video_desc;
    }

    public void setVideo_desc(String video_desc) {
        this.video_desc = video_desc;
    }

    public String getIs_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(String is_viewed) {
        this.is_viewed = is_viewed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getSub_cat() {
        return sub_cat;
    }

    public void setSub_cat(String sub_cat) {
        this.sub_cat = sub_cat;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getFor_dams() {
        return for_dams;
    }

    public void setFor_dams(String for_dams) {
        this.for_dams = for_dams;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getMain_cat() {
        return main_cat;
    }

    public void setMain_cat(String main_cat) {
        this.main_cat = main_cat;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Enc getEnc_url() {
        return enc_url;
    }

    public void setEnc_url(Enc enc_url) {
        this.enc_url = enc_url;
    }

    @Override
    public String toString() {
        return "ClassPojo [initial_view = " + initial_view + ", tags = " + tags + ", for_non_dams = " + for_non_dams + ", screen_tag = " + screen_tag + ", video_type = " + video_type + ", allow_comments = " + allow_comments + ", is_like = " + is_like + ", video_title = " + video_title + ", featured = " + featured + ", video_desc = " + video_desc + ", is_viewed = " + is_viewed + ", id = " + id + ", is_new = " + is_new + ", thumbnail_url = " + thumbnail_url + ", end_date = " + end_date + ", sub_cat = " + sub_cat + ", views = " + views + ", likes = " + likes + ", for_dams = " + for_dams + ", author_name = " + author_name + ", creation_time = " + creation_time + ", main_cat = " + main_cat + ", URL = " + URL + ", start_date = " + start_date + ", comments = " + comments + "]";
    }
}