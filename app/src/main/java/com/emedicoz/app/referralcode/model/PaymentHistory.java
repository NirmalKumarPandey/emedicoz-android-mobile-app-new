package com.emedicoz.app.referralcode.model;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentHistory {
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
    @SerializedName("course_name")
    @Expose
    private String courseName;
    @SerializedName("course_fee")
    @Expose
    private String courseFee;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("used_referral_code")
    @Expose
    private String usedReferralCode;
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("no_of_transaction")
    @Expose
    private String noOfTransaction;

    public String getNoOfTransaction() {
        return noOfTransaction;
    }

    public void setNoOfTransaction(String noOfTransaction) {
        this.noOfTransaction = noOfTransaction;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public PaymentHistory withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PaymentHistory withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getCoursePurchased() {
        return coursePurchased;
    }

    public void setCoursePurchased(String coursePurchased) {
        this.coursePurchased = coursePurchased;
    }

    public PaymentHistory withCoursePurchased(String coursePurchased) {
        this.coursePurchased = coursePurchased;
        return this;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public PaymentHistory withTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public String  getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public PaymentHistory withCourseName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public String getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(String courseFee) {
        this.courseFee = courseFee;
    }

    public PaymentHistory withCourseFee(String courseFee) {
        this.courseFee = courseFee;
        return this;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public PaymentHistory withCommission(String commission) {
        this.commission = commission;
        return this;
    }

    public String getUsedReferralCode() {
        return usedReferralCode;
    }

    public void setUsedReferralCode(String usedReferralCode) {
        this.usedReferralCode = usedReferralCode;
    }

    public PaymentHistory withUsedReferralCode(String usedReferralCode) {
        this.usedReferralCode = usedReferralCode;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentHistory withId(String id) {
        this.id = id;
        return this;
    }

}
