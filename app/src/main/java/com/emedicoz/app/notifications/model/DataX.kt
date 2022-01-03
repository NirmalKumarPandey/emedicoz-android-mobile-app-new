package com.emedicoz.app.notifications.model

data class DataX(
    val created_on: String,
    val device_type: String,
    val extra: String,
    val id: String,
    val notification_type: String,
    val sent_by: String,
    val text: String,
    val user_type: String
)