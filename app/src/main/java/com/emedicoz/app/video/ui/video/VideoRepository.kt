package com.emedicoz.app.video.ui.video

import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.video.ui.services.VideoApi

class VideoRepository {
    val apiInterface = ApiClient.createService(VideoApi::class.java)!!
    suspend fun getTag(userId: String, masterId: String) = apiInterface.getTags(userId, masterId)
    suspend fun getAllVideo(userId: String, subCat: String, lastVideoId: String, sortBy: String, pageSegment: String, searchContent: String) =
        apiInterface.getAllVideo(userId, subCat, lastVideoId, sortBy, pageSegment, searchContent)
}