package com.emedicoz.app.newsandarticle.models

import com.google.gson.annotations.SerializedName

data class TopicListResponse(

    @SerializedName("status")
    val status:Boolean,
    @SerializedName("message")
    val message:String,

    @SerializedName("data")
     val data: List<TopicDetail.Topic>

)


