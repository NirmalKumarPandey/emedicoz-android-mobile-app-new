package com.emedicoz.app.notifications.model

data class NotificationCategoryData(
    val auth_code: String,
    val auth_message: String,
    val `data`: List<Data>,
    val error: List<Any>,
    val is_ios_price: Int,
    val message: String,
    val status: Boolean
)