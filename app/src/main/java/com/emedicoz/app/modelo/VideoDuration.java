package com.emedicoz.app.modelo;

import java.io.Serializable;

public class VideoDuration implements Serializable {

    private String video_id;
    private String duration_limit;
    private String watched;
    private String type;

    public String getDuration_limit() {
        return duration_limit;
    }

    public void setDuration_limit(String duration_limit) {
        this.duration_limit = duration_limit;
    }

    public String getWatched() {
        return watched;
    }

    public void setWatched(String watched) {
        this.watched = watched;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
