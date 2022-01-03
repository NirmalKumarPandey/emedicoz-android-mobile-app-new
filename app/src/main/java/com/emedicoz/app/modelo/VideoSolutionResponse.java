package com.emedicoz.app.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoSolutionResponse implements Serializable {

    private Boolean status;
    private String message;
    private ArrayList<VideoSolutionData> data = null;
    private Integer isIosPrice;
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

    public ArrayList<VideoSolutionData> getData() {
        return data;
    }

    public void setData(ArrayList<VideoSolutionData> data) {
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
