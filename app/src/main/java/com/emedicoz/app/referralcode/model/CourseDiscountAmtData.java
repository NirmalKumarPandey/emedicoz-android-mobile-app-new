package com.emedicoz.app.referralcode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseDiscountAmtData {

    @SerializedName("discount_amount")
    @Expose
    private Integer discountAmount;

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }
}
