package com.emedicoz.app.modelo;

import com.emedicoz.app.modelo.testseries.OrderHistoryData;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderHistoryResponse implements Serializable {

    private Boolean status;
    private String message;
    private ArrayList<OrderHistoryData> data = null;
    private Integer isIosPrice;
    private ArrayList<Object> error = null;

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

    public ArrayList<OrderHistoryData> getData() {
        return data;
    }

    public void setData(ArrayList<OrderHistoryData> data) {
        this.data = data;
    }

    public Integer getIsIosPrice() {
        return isIosPrice;
    }

    public void setIsIosPrice(Integer isIosPrice) {
        this.isIosPrice = isIosPrice;
    }

    public ArrayList<Object> getError() {
        return error;
    }

    public void setError(ArrayList<Object> error) {
        this.error = error;
    }
}
