package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefProfileData {
    @SerializedName("profile_info")
    @Expose
    public RefProfileInfo profileInfo;
    @SerializedName("referral_money_earned")
    @Expose
    public ReferralMoneyEarned referralMoneyEarned;
    @SerializedName("friends_joined")
    @Expose
    public Integer friendsJoined;

    public RefProfileInfo getProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(RefProfileInfo profileInfo) {
        this.profileInfo = profileInfo;
    }

    public ReferralMoneyEarned getReferralMoneyEarned() {
        return referralMoneyEarned;
    }

    public void setReferralMoneyEarned(ReferralMoneyEarned referralMoneyEarned) {
        this.referralMoneyEarned = referralMoneyEarned;
    }

    public Integer getFriendsJoined() {
        return friendsJoined;
    }

    public void setFriendsJoined(Integer friendsJoined) {
        this.friendsJoined = friendsJoined;
    }
}
