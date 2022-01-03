package com.emedicoz.app.courses.recordedmodel

data class Data(
        val banners: List<Banner>,
        val cart_count: Int,
        val course: List<Course>,
        val gst: String,
        val points_conversion_rate: String,
        val position_order: PositionOrder,
        val qlinks: List<Qlink>
)