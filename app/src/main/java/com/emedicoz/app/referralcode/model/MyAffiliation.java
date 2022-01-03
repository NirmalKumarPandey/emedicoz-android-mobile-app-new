package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyAffiliation {

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("course_purchased")
    @Expose
    private String coursePurchased;
    @SerializedName("transaction_date")
    @Expose
    private String transactionDate;
    @SerializedName("used_referral_code")
    @Expose
    private String usedReferralCode;
    @SerializedName("commission")
    @Expose
    private String commission;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public MyAffiliation withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public MyAffiliation withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getCoursePurchased() {
        return coursePurchased;
    }

    public void setCoursePurchased(String coursePurchased) {
        this.coursePurchased = coursePurchased;
    }

    public MyAffiliation withCoursePurchased(String coursePurchased) {
        this.coursePurchased = coursePurchased;
        return this;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public MyAffiliation withTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public String getUsedReferralCode() {
        return usedReferralCode;
    }

    public void setUsedReferralCode(String usedReferralCode) {
        this.usedReferralCode = usedReferralCode;
    }

    public MyAffiliation withUsedReferralCode(String usedReferralCode) {
        this.usedReferralCode = usedReferralCode;
        return this;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public MyAffiliation withCommission(String commission) {
        this.commission = commission;
        return this;
    }
}
