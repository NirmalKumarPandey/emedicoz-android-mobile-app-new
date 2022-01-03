package com.emedicoz.app.Books.Model

import Books
import com.google.gson.annotations.SerializedName

data class Tag(
        @SerializedName("id") val id : Int,
        @SerializedName("tag") val tag : String,
        @SerializedName("books") val books : List<Books>
)
