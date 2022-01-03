package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SubmitQueryApiInterface {
    @FormUrlEncoded
    @POST("data_model/user/user_queries/submit_query")
    Call<JsonObject> getresponseofsubmitquery(
            @Field(Const.USER_ID) String userId,
            @Field(Const.TITLE) String title,
            @Field(Const.DESCRIPTION) String desc,
            @Field(Const.CATEGORY) String cat,
            @Field(Const.FILE) String file
    );
}
