package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ProfileApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/user_follow/follow_user")
    Call<JsonObject> follow(
            @Field(Const.USER_ID) String userId,
            @Field(Const.FOLLOWER_ID) String FOLLOWER_ID
    );

    @FormUrlEncoded
    @POST("data_model/user/user_follow/unfollow_user")
    Call<JsonObject> unFollow(
            @Field(Const.USER_ID) String userId,
            @Field(Const.FOLLOWER_ID) String FOLLOWER_ID
    );

    @FormUrlEncoded
    @POST("data_model/user/user_expert_control/make_user_expert")
    Call<JsonObject> makeAnExpert(
            @Field(Const.USER_ID) String userId,
            @Field(Const.USER_FOR) String USER_FOR
    );

    @FormUrlEncoded
    @POST("data_model/user/user_expert_control/remove_user_expert")
    Call<JsonObject> removeAnExpert(
            @Field(Const.USER_ID) String userId,
            @Field(Const.USER_FOR) String USER_FOR
    );

    @FormUrlEncoded
    @POST("data_model/fanwall/fan_wall/get_fan_wall_for_user")
    Call<JsonObject> feedsForUser(
            @Query("is_watcher") String watcher,
            @Field(Const.USER_ID) String userId,
            @Field(Const.LAST_POST_ID) String LAST_POST_ID
    );

    @FormUrlEncoded
    @POST("data_model/user/user_follow/following_list")
    Call<JsonObject> getFollowingList(
            @Query("is_watcher") String watcher,
            @Field(Const.USER_ID) String userId,
            @Field(Const.LAST_FOLLOW_ID) String LAST_FOLLOW_ID
    );

    @FormUrlEncoded
    @POST("data_model/user/user_follow/followers_list")
    Call<JsonObject> getFollowerList(
            @Query("is_watcher") String watcher,
            @Field(Const.USER_ID) String userId,
            @Field(Const.LAST_FOLLOW_ID) String LAST_FOLLOW_ID
    );

    @POST
    Call<JsonObject> getUser(
            @Url String url
    );
}
