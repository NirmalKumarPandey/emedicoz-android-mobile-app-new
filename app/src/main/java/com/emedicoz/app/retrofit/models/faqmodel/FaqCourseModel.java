package com.emedicoz.app.retrofit.models.faqmodel;

import com.emedicoz.app.modelo.courses.FAQ;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqCourseModel {
    @SerializedName("data")
    private List<FAQ> mData;
    @SerializedName("error")
    private List<Object> mError;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;

    public List<FAQ> getData() {
        return mData;
    }

    public void setData(List<FAQ> data) {
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
