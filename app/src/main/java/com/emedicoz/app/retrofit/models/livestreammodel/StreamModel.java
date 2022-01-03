package com.emedicoz.app.retrofit.models.livestreammodel;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.SerializedName;

public class StreamModel {
    @SerializedName("hls")
    private String hls;
    @SerializedName(Constants.Extras.ID)
    private String id;
    @SerializedName(Constants.Extras.NAME)
    private String name;
    @SerializedName("profile_picture")
    private String profile_picture;

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
