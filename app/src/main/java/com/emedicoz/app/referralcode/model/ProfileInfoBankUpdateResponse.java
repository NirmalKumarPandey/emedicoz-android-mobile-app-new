package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileInfoBankUpdateResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ProfileInfoBankUpdateData data;
    @SerializedName("is_ios_price")
    @Expose
    private Integer isIosPrice;
    @SerializedName("error")
    @Expose
    private List<Object> error = null;

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

    public ProfileInfoBankUpdateData getData() {
        return data;
    }

    public void setData(ProfileInfoBankUpdateData data) {
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
