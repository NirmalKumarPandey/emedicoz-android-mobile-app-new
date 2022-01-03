package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RewardsPointApiInterface {

    @FormUrlEncoded
    @POST("data_model/user/user_reward/get_user_rewards")
    Call<JsonObject> getrewarPointsUser(
            @Field(Const.USER_ID) String userId
    );
}
