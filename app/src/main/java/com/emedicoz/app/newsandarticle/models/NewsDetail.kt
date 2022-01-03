package com.emedicoz.app.newsandarticle.models

import com.google.gson.annotations.SerializedName

data class NewsDetail(

        @SerializedName("status")
        val status: Boolean,

        @SerializedName("message")
        val message: String,

        @SerializedName("data")
        val data: Article

) {
    data class Article(
            @SerializedName("article_id")
            val articleId: String,
            @SerializedName("title")
            val title: String,
            @SerializedName("creation_date")
            val creationDateval: String,
            @SerializedName("created_by")
            val createdBy: String,
            @SerializedName("title_uniqid")
            val titleUniqid: String,
            @SerializedName("content")
            val content: String,
            @SerializedName("subject_tags")
            val subjectTags: String,
            @SerializedName("topic_tags")
            val topicTags: String,
            @SerializedName("other_tags")
            val otherTags: String,
            @SerializedName("likes")
            val likes: String,
            @SerializedName("image")
            val image: String,

            @SerializedName("views")
            val views: String
    )
}