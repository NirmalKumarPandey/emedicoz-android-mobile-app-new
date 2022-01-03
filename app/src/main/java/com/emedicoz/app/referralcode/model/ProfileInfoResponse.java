package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileInfoResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public RefProfileData data;
    @SerializedName("is_ios_price")
    @Expose
    public Integer isIosPrice;
    @SerializedName("error")
    @Expose
    public List<Object> error = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RefProfileData getData() {
        return data;
    }

    public void setData(RefProfileData data) {
        this.data = data;
    }

    public Integer getIsIosPrice() {
        return isIosPrice;
    }

    public void setIsIosPrice(Integer isIosPrice) {
        this.isIosPrice = isIosPrice;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }
}
