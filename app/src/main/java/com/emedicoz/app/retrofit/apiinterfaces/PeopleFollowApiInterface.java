package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PeopleFollowApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/user_follow/follow_user")
    Call<JsonObject> follow(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Const.FOLLOWER_ID) String FOLLOWER_ID
    );

    @FormUrlEncoded
    @POST("data_model/user/user_follow/unfollow_user")
    Call<JsonObject> unfollow(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Const.FOLLOWER_ID) String FOLLOWER_ID
    );
}
