package com.emedicoz.app.Books.Model

import com.emedicoz.app.modelo.Detail
import com.google.gson.annotations.SerializedName

data class DataDetail(
        @SerializedName("banner") val banner : List<BookDetailBanner>,
        @SerializedName("detail") val detail :BookDetail
)
