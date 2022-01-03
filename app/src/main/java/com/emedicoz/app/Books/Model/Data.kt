package com.emedicoz.app.Books.Model

import Best_seller
import com.google.gson.annotations.SerializedName


data class Data(
        @SerializedName("title") val title: String,
        @SerializedName("category") val category : List<Category>,
        @SerializedName("tag") val tag : List<Tag>,
        @SerializedName("trending") val  trending: List<Trending>,
        @SerializedName("best_seller") val best_seller : List<Best_seller>,
        @SerializedName("subject") val subject : List<Subject>
)