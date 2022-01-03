package com.emedicoz.app.Books.Model

import Books
import com.google.gson.annotations.SerializedName

data class Trending(
        @SerializedName("id") val id : Int,
        @SerializedName("trending") val trending : String,
        @SerializedName("books") val books : List<Books>
)
