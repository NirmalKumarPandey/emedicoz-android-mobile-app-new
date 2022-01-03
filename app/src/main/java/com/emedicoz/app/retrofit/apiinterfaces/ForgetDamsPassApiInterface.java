package com.emedicoz.app.retrofit.apiinterfaces;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ForgetDamsPassApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/registration/forget_password")
    Call<JsonObject> forgetpass(
            @Field("username") String username
    );
}
