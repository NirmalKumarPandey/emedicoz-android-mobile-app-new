package com.emedicoz.app.Books.Model

import Books
import com.google.gson.annotations.SerializedName

data class BookListResponse(
        @SerializedName("status") val status : Boolean,
        @SerializedName("message") val message : String,
        @SerializedName("data") val data : List<DataList>,
        @SerializedName("is_ios_price") val is_ios_price : Int,
        @SerializedName("error") val error : List<String>,
        @SerializedName("auth_code") val auth_code : String,
        @SerializedName("auth_message") val auth_message : String)
