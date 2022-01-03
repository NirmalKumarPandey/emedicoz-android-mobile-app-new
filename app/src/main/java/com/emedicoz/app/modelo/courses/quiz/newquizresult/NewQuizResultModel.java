
package com.emedicoz.app.modelo.courses.quiz.newquizresult;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewQuizResultModel {

    @SerializedName("data")
    private Data mData;
    @SerializedName("error")
    private List<Object> mError;
    @SerializedName("is_ios_price")
    private Long mIsIosPrice;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public List<Object> getError() {
        return mError;
    }

    public void setError(List<Object> error) {
        mError = error;
    }

    public Long getIsIosPrice() {
        return mIsIosPrice;
    }

    public void setIsIosPrice(Long isIosPrice) {
        mIsIosPrice = isIosPrice;
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
