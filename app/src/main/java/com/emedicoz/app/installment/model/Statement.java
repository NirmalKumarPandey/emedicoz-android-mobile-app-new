package com.emedicoz.app.installment.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Statement implements Serializable {

    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("attempt")
    @Expose
    private String attempt;
    @SerializedName("paid_on")
    @Expose
    private String paidOn;
    @SerializedName("amount_paid")
    @Expose
    private String amountPaid;
    @SerializedName("next_date")
    @Expose
    private String nextDate;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(String paidOn) {
        this.paidOn = paidOn;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

}