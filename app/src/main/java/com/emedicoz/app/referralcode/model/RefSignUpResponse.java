package com.emedicoz.app.referralcode.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
    @SuppressWarnings("unused")
    public class RefSignUpResponse {

        @Expose
        private RefData data;
        @Expose
        private List<Object> error;
        @SerializedName("is_ios_price")
        private Long isIosPrice;
        @Expose
        private String message;
        @Expose
        private Boolean status;

        public RefData getData() {
            return data;
        }

        public void setData(RefData data) {
            this.data = data;
        }

        public List<Object> getError() {
            return error;
        }

        public void setError(List<Object> error) {
            this.error = error;
        }

        public Long getIsIosPrice() {
            return isIosPrice;
        }

        public void setIsIosPrice(Long isIosPrice) {
            this.isIosPrice = isIosPrice;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }

