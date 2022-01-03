package com.emedicoz.app.Books.Model

import com.google.gson.annotations.SerializedName

data class BookDetail(
        @SerializedName("book_detail_id") val book_detail_id : Int,
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
        @SerializedName("is_cod") val is_cod : Int,
        @SerializedName("is_ebook") val is_ebook : Int,
        @SerializedName("is_paper_book") val is_paper_book : Int,
        @SerializedName("is_audiable") val is_audiable : Int,
        @SerializedName("ebook_price") val ebook_price : String,
        @SerializedName("paper_book_price") val paper_book_price : Int,
        @SerializedName("audiable_price") val audiable_price : String,
        @SerializedName("ebook_page") val ebook_page : String,
        @SerializedName("paper_book_page") val paper_book_page : String,
        @SerializedName("language") val language : String,
        @SerializedName("publisher") val publisher : String,
        @SerializedName("publication_date") val publication_date : String
)
