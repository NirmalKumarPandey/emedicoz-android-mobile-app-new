package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReferralMoneyEarned {
    @SerializedName("referral_money_earned")
    @Expose
    public String referralMoneyEarned;

    public String getReferralMoneyEarned() {
        return referralMoneyEarned;
    }

    public void setReferralMoneyEarned(String referralMoneyEarned) {
        this.referralMoneyEarned = referralMoneyEarned;
    }
}
