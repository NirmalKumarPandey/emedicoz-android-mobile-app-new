package com.emedicoz.app.api;

import com.emedicoz.app.modelo.Logout;
import com.emedicoz.app.utilso.Const;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyLogoutApi
{
    @FormUrlEncoded
    @POST("data_model/user/registration/logout_old_deveices")
    Call<Logout> logoutProfile(
            @Field(Const.USER_ID) String user_id,
            @Field(Const.DEVICE_TOKEN) String device_tokken);
}
