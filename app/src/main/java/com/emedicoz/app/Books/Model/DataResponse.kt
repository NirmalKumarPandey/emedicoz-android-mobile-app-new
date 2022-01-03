package com.emedicoz.app.Books.Model

import com.emedicoz.app.Books.Model.Banner
import com.google.gson.annotations.SerializedName

data class DataResponse(
        @SerializedName("status") val status : Boolean,
        @SerializedName("message") val message : String,
        @SerializedName("banner") val banner : List<Banner>,
        @SerializedName("data") val data : List<Data>

)
