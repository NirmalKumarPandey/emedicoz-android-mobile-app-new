package com.emedicoz.app.modelo;

public class VideoViewCount {
    String user_id;
    String video_id;
    int viewCount;

    public VideoViewCount(String user_id, String video_id, int viewCount) {
        this.user_id = user_id;
        this.video_id = video_id;
        this.viewCount = viewCount;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
