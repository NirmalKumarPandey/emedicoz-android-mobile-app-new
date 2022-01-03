package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentHistoryData {

    @SerializedName("per_page_total")
    @Expose
    private String perPageTotal;
    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("total_records")
    @Expose
    private int totalRecords;
    @SerializedName("payment_history")
    @Expose
    private List<PaymentHistory> paymentHistory = null;

    public String getPerPageTotal() {
        return perPageTotal;
    }

    public void setPerPageTotal(String perPageTotal) {
        this.perPageTotal = perPageTotal;
    }

    public PaymentHistoryData withPerPageTotal(String perPageTotal) {
        this.perPageTotal = perPageTotal;
        return this;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public PaymentHistoryData withPage(String page) {
        this.page = page;
        return this;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public PaymentHistoryData withTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        return this;
    }

    public List<PaymentHistory> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(List<PaymentHistory> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    public PaymentHistoryData withPaymentHistory(List<PaymentHistory> paymentHistory) {
        this.paymentHistory = paymentHistory;
        return this;
    }
}
