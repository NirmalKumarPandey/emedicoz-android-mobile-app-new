package com.emedicoz.app.Books.Model

import com.google.gson.annotations.SerializedName

data class Banner(

        @SerializedName("id") val id : Int,
        @SerializedName("image_link") val image_link : String,
        @SerializedName("web_link") val web_link : String,
        @SerializedName("banner_title") val banner_title : String
)
