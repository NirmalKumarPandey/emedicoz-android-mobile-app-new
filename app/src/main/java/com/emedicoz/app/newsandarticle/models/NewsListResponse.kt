package com.emedicoz.app.newsandarticle.models

import com.google.gson.annotations.SerializedName

data class NewsListResponse(
        @SerializedName("status")
        val status: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("is_follow")
        val is_follow : String,
        @SerializedName("data")
        val data: List<NewsDetail.Article>


)
