package com.emedicoz.app.retrofit.models.feedmodel;

import com.emedicoz.app.response.PostResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainFeedDataModel {
    @SerializedName("data")
    private List<PostResponse> mData;
    @SerializedName("error")
    private List<Object> mError;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;
    @SerializedName("auth_code")
    private String auth_code;

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public List<PostResponse> getData() {
        return mData;
    }

    public void setData(List<PostResponse> data) {
        mData = data;
    }

    public List<Object> getError() {
        return mError;
    }

    public void setError(List<Object> error) {
        mError = error;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }
}
