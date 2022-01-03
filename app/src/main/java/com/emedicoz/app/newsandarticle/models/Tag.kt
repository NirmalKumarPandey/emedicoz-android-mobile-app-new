package com.emedicoz.app.newsandarticle.models

import com.google.gson.annotations.SerializedName

data class Tag(@SerializedName("id") val id : Int,
               @SerializedName("text") val text : String
               )
