package com.emedicoz.app.Books.Model

import Books
import com.google.gson.annotations.SerializedName

data class Subject(
        @SerializedName("id") val id : Int,
        @SerializedName("subject") val subject : String,
        @SerializedName("books") val books : List<Books>
)
