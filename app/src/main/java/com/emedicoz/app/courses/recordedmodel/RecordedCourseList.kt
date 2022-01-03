package com.emedicoz.app.courses.recordedmodel


data class RecordedCourseList(
        val auth_code: String,
        val auth_message: String,
        val `data`: Data,
        val error: List<Any>,
        val is_ios_price: Int,
        val message: String,
        val status: Boolean
)