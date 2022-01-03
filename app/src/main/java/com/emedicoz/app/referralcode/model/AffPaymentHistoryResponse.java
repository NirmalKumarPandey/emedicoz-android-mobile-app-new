package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AffPaymentHistoryResponse {
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private PaymentHistoryData data;
    @SerializedName("is_ios_price")
    @Expose
    private int isIosPrice;
    @SerializedName("error")
    @Expose
    private List<Object> error = null;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public AffPaymentHistoryResponse withStatus(boolean status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AffPaymentHistoryResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public PaymentHistoryData getData() {
        return data;
    }

    public void setData(PaymentHistoryData data) {
        this.data = data;
    }

    public AffPaymentHistoryResponse withData(PaymentHistoryData data) {
        this.data = data;
        return this;
    }

    public int getIsIosPrice() {
        return isIosPrice;
    }

    public void setIsIosPrice(int isIosPrice) {
        this.isIosPrice = isIosPrice;
    }

    public AffPaymentHistoryResponse withIsIosPrice(int isIosPrice) {
        this.isIosPrice = isIosPrice;
        return this;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

    public AffPaymentHistoryResponse withError(List<Object> error) {
        this.error = error;
        return this;
    }

}
