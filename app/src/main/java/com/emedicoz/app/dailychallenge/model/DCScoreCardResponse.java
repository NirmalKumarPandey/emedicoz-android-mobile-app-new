package com.emedicoz.app.dailychallenge.model;

import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DCScoreCardResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<ResultTestSeries> data = null;
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

    public List<ResultTestSeries> getData() {
        return data;
    }

    public void setData(List<ResultTestSeries> data) {
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
