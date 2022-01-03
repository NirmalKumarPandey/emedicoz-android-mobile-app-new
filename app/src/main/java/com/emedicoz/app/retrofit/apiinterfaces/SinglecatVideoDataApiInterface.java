package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SinglecatVideoDataApiInterface {
    @FormUrlEncoded
    @POST("data_model/video/Video_channel/get_videos_for_tag_list")
    Call<JsonObject> getVideoListData(
            @Field(Const.USER_ID) String userId,
            @Field(Const.SUB_CAT) String sub_cat,
            @Field(Const.LAST_VIDEO_ID) String LAST_VIDEO_ID,
            @Field(Const.SORT_BY) String SORT_BY,
            @Field(Const.PAGE_SEGMENT) String PAGE_SEGMENT,
            @Field(Const.SEARCH_CONTENT) String SEARCH_CONTENT
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_podcast_data")
    Call<JsonObject> getPodcastData(
            @Field(Const.USER_ID) String userId,
            @Field("author_id") String authorId,
            @Field("sort_by") String sortBy,
            @Field("page") String page
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_stream_subject_list ")
    Call<JsonObject> getStreamSubjectList(
            @Field(Const.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_bookmarked_podcast")
    Call<JsonObject> getBookmarkedPodcastData(
            @Field(Const.USER_ID) String userId,
            @Field("author_id") String authorId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_bookmarked_podcast")
    Call<JsonObject> getBookmarkedPodcastDataV2(
            @Field(Const.USER_ID) String userId,
            @Field("sort_by") String sortBy,
            @Field("page") String page
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_podcast_author_list")
    Call<JsonObject> getPodcastAuthorData(
            @Field(Const.USER_ID) String userId,
            @Field("stream_id") String streamId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_podcast_author_channel_list")
    Call<JsonObject> getPodcastAuthorChannel(
            @Field(Const.USER_ID) String userId,
            @Field("stream_id") String streamId,
            @Field("sort_by") String sortBy
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_podcast_author_channels")
    Call<JsonObject> getPodcastAuthorPodcastById(
            @Field(Const.USER_ID) String userId,
            @Field("author_id") String authorId,
            @Field("stream_id") String streamId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/add_podcast")
    Call<JsonObject> addPodcastData(
            @Field(Const.USER_ID) String userId,
            @Field("main_id") String mainId,
            @Field(Const.TITLE) String title,
            @Field(Const.DESCRIPTION) String desc,
            @Field("subject") String subject,
            @Field("audio_url") String audioUrl,
            @Field("thumbnail_image") String thumbnailImage,
            @Field("duration") String duration);

    @FormUrlEncoded
    @POST("data_model/fanwall/master_hit/content")
    Call<JsonObject> getMasterFeedForUser(
            @Field(Const.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/create_bookmark")
    Call<JsonObject> createBookmark(
            @Field(Const.USER_ID) String userId,
            @Field(Const.VIDEO_ID) String videoid
    );

    @FormUrlEncoded
    @POST("data_model/fanwall/fan_wall/remove_video_bookmark")
    Call<JsonObject> removeVideoBookmark(
            @Field(Const.USER_ID) String userId,
            @Field(Const.VIDEO_ID) String videoid
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/bookmarked_podcast")
    Call<JsonObject> managePodcastBookmark(
            @Field(Const.USER_ID) String userId,
            @Field(Const.PODCAST_ID) String podcastId,
            @Field("is_remove_bookmark") String removeBookmark
    );
}
