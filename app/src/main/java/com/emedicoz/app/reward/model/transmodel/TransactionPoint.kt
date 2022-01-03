package com.emedicoz.app.reward.model.transmodel

data class TransactionPoint(
    val auth_code: String,
    val auth_message: String,
    val `data`: List<Data>,
    val error: List<Any>,
    val is_ios_price: Int,
    val message: String,
    val status: Boolean
)