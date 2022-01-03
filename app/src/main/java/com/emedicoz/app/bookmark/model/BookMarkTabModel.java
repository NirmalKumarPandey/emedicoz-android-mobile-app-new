
package com.emedicoz.app.bookmark.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BookMarkTabModel {

    @SerializedName("data")
    private List<Datum> mData;
    @SerializedName("error")
    private List<Object> mError;
    @SerializedName("is_ios_price")
    private Long mIsIosPrice;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
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
