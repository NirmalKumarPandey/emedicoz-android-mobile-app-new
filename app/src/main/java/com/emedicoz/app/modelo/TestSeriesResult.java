package com.emedicoz.app.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestSeriesResult implements Serializable {

    private Boolean status;
    private String message;
    private ArrayList<TestSeriesResultData> data = null;
    private Integer is_ios_price;
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

    public ArrayList<TestSeriesResultData> getData() {
        return data;
    }

    public void setData(ArrayList<TestSeriesResultData> data) {
        this.data = data;
    }

    public Integer getIs_ios_price() {
        return is_ios_price;
    }

    public void setIs_ios_price(Integer is_ios_price) {
        this.is_ios_price = is_ios_price;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }
}
