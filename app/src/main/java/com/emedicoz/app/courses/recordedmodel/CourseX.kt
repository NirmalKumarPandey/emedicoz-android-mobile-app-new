package com.emedicoz.app.courses.recordedmodel

data class CourseX(
    val course_category_fk: String,
    val course_main_fk: String,
    val course_tag: Any,
    val course_type: String,
    val cover_image: String,
    val creation_time: String,
    val desc_header_image: String,
    val for_dams: String,
    val free_message: String,
    val free_trial_duration: String,
    val gst_include: String,
    val id: String,
    val instructor_id: String,
    val instructor_share: String,
    val is_combo: String,
    val is_free_trial: Boolean,
    val is_live: String,
    val is_new: String,
    val is_purchased: Boolean,
    val is_renew: String,
    val is_subscription: String,
    val is_wishlist: Boolean,
    val last_updated: String,
    val learner: String,
    val mrp: String,
    val non_dams: String,
    val publish: String,
    val rating: String,
    val renew_discount_percent: Any,
    val review_count: String,
    val state: String,
    val subject_id: String,
    val tags: String,
    val title: String
)