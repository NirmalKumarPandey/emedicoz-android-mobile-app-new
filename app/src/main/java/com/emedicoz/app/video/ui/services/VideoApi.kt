package com.emedicoz.app.video.ui.services

import com.emedicoz.app.video.ui.models.TagResponse
import com.emedicoz.app.video.ui.models.VideoResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VideoApi {

    @FormUrlEncoded
    @POST("data_model/fanwall/master_hit/content")
    suspend fun getTags(
        @Field("user_id") userId: String,
        @Field("master_id") masterId: String
    ): Response<TagResponse>

    @FormUrlEncoded
    @POST("data_model/video/video_channel/update_bookmark_status")
    fun updateBookMarkStatus(
        @Field("user_id") userId: String,
        @Field("video_id") videoId: String,
        @Field("is_bookmarked") isBookmarked: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/get_videos_for_tag_list")
    suspend fun getAllVideo(
        @Field("user_id") userId: String,
        @Field("sub_cat") subCat: String,
        @Field("last_video_id") lastVideoId: String,
        @Field("sort_by") sortBy: String,
        @Field("page_segment") pageSegment: String,
        @Field("search_content") searchContent: String
    ): Response<VideoResponse>

//    @FormUrlEncoded
//    @POST("data_model/video/Video_channel/get_videos_for_tag_list")
//    suspend fun getAllVideo(
//        @Field("user_id") userId: String,
//        @Field("sub_cat") subCat: String,
//        @Field("sort_by") sortBy: String,
//        @Field("page_segment") pageSegment: String,
//        @Field("search_content") searchContent: String
//    ): Response<VideoResponse>
}


