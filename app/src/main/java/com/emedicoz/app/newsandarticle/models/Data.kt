package com.emedicoz.app.newsandarticle.models

import com.google.gson.annotations.SerializedName

data class Data(
        @SerializedName("article") val article : NewsDetail.Article,
        @SerializedName("tag") val tag : List<Tag>,
        @SerializedName("user_like") val user_like : Int,
        @SerializedName("user_bookmark") val user_bookmark : Boolean
)
