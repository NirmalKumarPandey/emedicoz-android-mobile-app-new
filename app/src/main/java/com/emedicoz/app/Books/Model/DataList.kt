package com.emedicoz.app.Books.Model

import com.google.gson.annotations.SerializedName

data class DataList(
        @SerializedName("id") val id : Int,
        @SerializedName("title") val title : String,
        @SerializedName("description") val description : String,
        @SerializedName("mrp") val mrp : Int,
        @SerializedName("for_dams") val for_dams : Int,
        @SerializedName("non_dams") val non_dams : Int,
        @SerializedName("rating") val rating : Double,
        @SerializedName("course_review_count") val course_review_count : Int,
        @SerializedName("book_type") val book_type : Int,
        @SerializedName("brand_name") val brand_name : String,
        @SerializedName("model_name") val model_name : String,
        @SerializedName("category") val category : Int,
        @SerializedName("book_tag") val book_tag : Int,
        @SerializedName("top_trending") val top_trending : Int,
        @SerializedName("best_seller") val best_seller : Int,
        @SerializedName("featured_image") val featured_image : String
)
