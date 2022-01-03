
package com.emedicoz.app.retrofit.models.testseriesmodel;

import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TestSeriesModel {

    @SerializedName("data")
    private List<ResultTestSeries> mData;
    @SerializedName("error")
    private List<Object> mError;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;

    public List<ResultTestSeries> getData() {
        return mData;
    }

    public void setData(List<ResultTestSeries> data) {
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
