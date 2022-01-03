package com.emedicoz.app.Books.Model

import com.google.gson.annotations.SerializedName

data class BookDetailBanner(

        @SerializedName("id") val id : Int,
        @SerializedName("book_id") val book_id : Int,
        @SerializedName("course_master_book_id") val course_master_book_id : Int,
        @SerializedName("images") val images : String,
        @SerializedName("created_date") val created_date : String
)
