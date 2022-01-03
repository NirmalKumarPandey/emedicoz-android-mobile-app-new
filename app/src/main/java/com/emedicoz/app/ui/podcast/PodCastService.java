package com.emedicoz.app.ui.podcast;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PodCastService {

    public static  final String GET_BOOKING_TIPS = "http://exampractice.membrainsoft.com/services_live/getbookingtips.php";


    @GET("services_live/getbookingtips.php")
    Call<StoriesList> doGetStoriesList();

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/get_videos_for_tag_list")
    Call<JsonObject> getVideoListData(
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.SUB_CAT) String sub_cat,
            @Field(Constants.LAST_VIDEO_ID) String LAST_VIDEO_ID,
            @Field(Constants.SORT_BY) String SORT_BY,
            @Field(Constants.PAGE_SEGMENT) String PAGE_SEGMENT,
            @Field(Constants.SEARCH_CONTENT) String SEARCH_CONTENT
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_podcast_data")
    Call<JsonObject> getPodcastData(
            @Field(Constants.USER_ID) String userId,
            @Field("author_id") String authorId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_stream_subject_list ")
    Call<JsonObject> getStreamSubjectList(
            @Field(Constants.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_bookmarked_podcast")
    Call<JsonObject> getBookmarkedPodcastData(
            @Field(Constants.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/get_podcast_author_list")
    Call<JsonObject> getPodcastAuthorData(
            @Field(Constants.USER_ID) String userId,
            @Field("stream_id") String streamId
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/add_podcast")
    Call<JsonObject> addPodcastData(
            @Field(Constants.USER_ID) String userId,
            @Field("main_id") String mainId,
            @Field(Constants.TITLE) String title,
            @Field(Constants.DESCRIPTION) String desc,
            @Field("subject") String subject,
            @Field("audio_url") String audioUrl,
            @Field("thumbnail_image") String thumbnailImage);

    @FormUrlEncoded
    @POST("data_model/fanwall/master_hit/content")
    Call<JsonObject> getMasterFeedForUser(
            @Field(Constants.USER_ID) String userId
    );

    @FormUrlEncoded
    @POST("data_model/video/Video_channel/create_bookmark")
    Call<JsonObject> createBookmark(
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.VIDEO_ID) String videoid
    );

    @FormUrlEncoded
    @POST("data_model/fanwall/fan_wall/remove_video_bookmark")
    Call<JsonObject> removeVideoBookmark(
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.VIDEO_ID) String videoid
    );

    @FormUrlEncoded
    @POST("data_model/podcasts/podcast/bookmarked_podcast")
    Call<JsonObject> managePodcastBookmark(
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.PODCAST_ID) String podcastId,
            @Field("is_remove_bookmark") String removeBookmark
    );
}
