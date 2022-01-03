package com.emedicoz.app.video.ui.models

import com.emedicoz.app.modelo.Video
import com.emedicoz.app.modelo.courses.Enc
import java.io.Serializable

/**
 * Video response data
 */
data class VideoResponse(val status: Boolean, val message: String, val pagecount: String, val data: List<Video>)

/**
 * Tags response data
 */
data class TagResponse(val status: Boolean, val message: String, val data: Data) {
    data class Data(val all_tags: List<AllTags>, val banner_list: List<VideoBanner>) {
        data class AllTags(val id: String, val master_id: String, val text: String, val position: String, val status: String, val logo_image: String)
        data class VideoBanner(var id: String, var image_link: String)
    }
}