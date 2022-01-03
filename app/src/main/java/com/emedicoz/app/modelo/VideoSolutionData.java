package com.emedicoz.app.modelo;

import java.io.Serializable;

public class VideoSolutionData implements Serializable {

    private String id;
    private String test_series_id;
    private String title;
    private String video_url;
    private String view_count;
    private String extend_view_limit_users;
    private String is_vod;
    private String creation_time;
    private String ext_view_count;
    private String video_url_live;
    private String allow_to_watch;
    private String duration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest_series_id() {
        return test_series_id;
    }

    public void setTest_series_id(String test_series_id) {
        this.test_series_id = test_series_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getExtend_view_limit_users() {
        return extend_view_limit_users;
    }

    public void setExtend_view_limit_users(String extend_view_limit_users) {
        this.extend_view_limit_users = extend_view_limit_users;
    }

    public String getIs_vod() {
        return is_vod;
    }

    public void setIs_vod(String is_vod) {
        this.is_vod = is_vod;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getExt_view_count() {
        return ext_view_count;
    }

    public void setExt_view_count(String ext_view_count) {
        this.ext_view_count = ext_view_count;
    }

    public String getVideo_url_live() {
        return video_url_live;
    }

    public void setVideo_url_live(String video_url_live) {
        this.video_url_live = video_url_live;
    }

    public String getAllow_to_watch() {
        return allow_to_watch;
    }

    public void setAllow_to_watch(String allow_to_watch) {
        this.allow_to_watch = allow_to_watch;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
