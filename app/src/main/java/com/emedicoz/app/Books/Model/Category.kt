package com.emedicoz.app.Books.Model

import Books
import com.google.gson.annotations.SerializedName

data class Category(
        @SerializedName("id") val id : Int,
        @SerializedName("category") val category : String,
        @SerializedName("books") val books : List<Books>
)
