package com.emedicoz.app.newsandarticle.models

import com.google.gson.annotations.SerializedName

data class ResponseData(

        @SerializedName("status")
        val status: Boolean,
        @SerializedName("message")
        val message: String,

        )
