package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankUpdateData {

    @SerializedName("account_holder_name")
    @Expose
    public String accountHolderName;
    @SerializedName("bank_name")
    @Expose
    public String bankName;
    @SerializedName("bank_branch_name")
    @Expose
    public String bankBranchName;
    @SerializedName("account_number")
    @Expose
    public String accountNumber;
    @SerializedName("ifsc_code")
    @Expose
    public String ifscCode;
    @SerializedName("affiliate_user_id")
    @Expose
    public String affiliateUserId;
    @SerializedName("bank_info_id")
    @Expose
    public String bankInfoId;

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getAffiliateUserId() {
        return affiliateUserId;
    }

    public void setAffiliateUserId(String affiliateUserId) {
        this.affiliateUserId = affiliateUserId;
    }

    public String getBankInfoId() {
        return bankInfoId;
    }

    public void setBankInfoId(String bankInfoId) {
        this.bankInfoId = bankInfoId;
    }
}
