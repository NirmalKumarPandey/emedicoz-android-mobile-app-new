package com.emedicoz.app.Books.Model

import com.google.gson.annotations.SerializedName

data class BookListModelResponse(
        @SerializedName("status") val status : Boolean,
        @SerializedName("message") val message : String,
        @SerializedName("data") val data : List<Data>,
        @SerializedName("is_ios_price") val is_ios_price : Int,
        @SerializedName("error") val error : List<String>,
        @SerializedName("auth_code") val auth_code : String,
        @SerializedName("auth_message") val auth_message : String


)
