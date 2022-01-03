package com.emedicoz.app.installment.model;

import java.io.Serializable;
import java.util.List;

public class AmountDescription implements Serializable {
    private String subscription_code;

    private String panelty;

    private String grace;

    private String emi_paid_count;

    private List<String> payment;

    private String expiry;

    private String panelty_type;

    private String loan_amt;

    private List<String> cycle;

    public String getLoan_amt() {
        return loan_amt;
    }

    public void setLoan_amt(String loan_amt) {
        this.loan_amt = loan_amt;
    }

    public String getSubscription_code() {
        return subscription_code;
    }

    public void setSubscription_code(String subscription_code) {
        this.subscription_code = subscription_code;
    }

    public String getPanelty() {
        return panelty;
    }

    public void setPanelty(String panelty) {
        this.panelty = panelty;
    }

    public String getGrace() {
        return grace;
    }

    public void setGrace(String grace) {
        this.grace = grace;
    }

    public String getEmi_paid_count() {
        return emi_paid_count;
    }

    public void setEmi_paid_count(String emi_paid_count) {
        this.emi_paid_count = emi_paid_count;
    }

    public List<String> getPayment() {
        return payment;
    }

    public void setPayment(List<String> payment) {
        this.payment = payment;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getPanelty_type() {
        return panelty_type;
    }

    public void setPanelty_type(String panelty_type) {
        this.panelty_type = panelty_type;
    }

    public List<String> getCycle() {
        return cycle;
    }

    public void setCycle(List<String> cycle) {
        this.cycle = cycle;
    }

    @Override
    public String toString() {
        return "ClassPojo [subscription_code = " + subscription_code + ", panelty = " + panelty + ", grace = " + grace + ", emi_paid_count = " + emi_paid_count + ", payment = " + payment + ", expiry = " + expiry + ", panelty_type = " + panelty_type + ", cycle = " + cycle + "]";
    }
}