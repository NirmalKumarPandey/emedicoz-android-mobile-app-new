package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DetailDataResponse implements Serializable {

    @Expose
    private Data data;
    @Expose
    private List<Object> error;
    @SerializedName("is_ios_price")
    private Long isIosPrice;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

    public Long getIsIosPrice() {
        return isIosPrice;
    }

    public void setIsIosPrice(Long isIosPrice) {
        this.isIosPrice = isIosPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
