package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SingleFeedviewApis {
    @FormUrlEncoded
    @POST("data_model/user/post_like/like_post")
    Call<JsonObject> getLikePost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    @FormUrlEncoded
    @POST("data_model/user/post_like/dislike_post")
    Call<JsonObject> getDisLikePost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    @FormUrlEncoded
    @POST("data_model/user/post_bookmarks/add_to_bookmarks")
    Call<JsonObject> getBookmarkePost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    @FormUrlEncoded
    @POST("data_model/user/post_bookmarks/remove_from_bookmarks")
    Call<JsonObject> getRemoveBookmark(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    @FormUrlEncoded
    @POST("data_model/user/Post_report_abuse/report")
    Call<JsonObject> getReportPost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId,
            @Field(Const.REASON_ID) String REASON_ID,
            @Field(Const.COMMENT) String COMMENT
    );

    @FormUrlEncoded
    @POST("data_model/user/Post_delete/delete_post")
    Call<JsonObject> getDeletePost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    //@FormUrlEncoded
    @POST("data_model/user/Post_report_abuse/get_all_report_reasons")
    Call<JsonObject> getAbuseList();

    @FormUrlEncoded
    @POST("data_model/user/post_pinning/pin_a_post")
    Call<JsonObject> getPinnedPost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    @FormUrlEncoded
    @POST("data_model/user/Post_pinning/pin_a_post_remove")
    Call<JsonObject> removePinnedPost(
            @Field(Const.USER_ID) String userId,
            @Field(Const.POST_ID) String postId
    );

    @FormUrlEncoded
    @POST("data_model/user/post_mcq/answer_post_mcq")
    Call<JsonObject> submitAnswerPostmcq(
            @Field(Const.USER_ID) String userId,
            @Field(Const.MCQ_ID) String MCQ_ID,
            @Field(Const.ANSWER) String ANSWER
    );
}
