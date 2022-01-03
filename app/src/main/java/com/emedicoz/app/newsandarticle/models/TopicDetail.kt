package com.emedicoz.app.newsandarticle.models


import com.google.gson.annotations.SerializedName


data class TopicDetail(

        @SerializedName("status")
        val status: Boolean,
        @SerializedName("message")
        val message: String,

        @SerializedName("data")
        val data: Topic
) {
    data class Topic(

            /*  @SerializedName("id")
              val id: String,
              @SerializedName("name")
              val name: String*/

            @SerializedName("id")
            val id: String,
            @SerializedName("count")
            val count: String,
            @SerializedName("name")
            val name: String,

            @SerializedName("topic")
            val topic: String,

            @SerializedName("text")
            val text: String,


            @SerializedName("subject")
            val subject: String,


            )


}