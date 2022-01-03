package com.emedicoz.app.retrofit.models.masterfeed;

import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MasterHitFeedModel {
    @SerializedName("data")
    private MasterFeedsHitResponse mData;
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

    public MasterFeedsHitResponse getData() {
        return mData;
    }

    public void setData(MasterFeedsHitResponse data) {
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
