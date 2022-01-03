package com.emedicoz.app.retrofit.apiinterfaces;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface FlashCardApiInterface {

    @POST
    Call<JsonObject> get(@Url String url);

    @FormUrlEncoded
    @POST
    Call<JsonObject> postData(@Url String url, @FieldMap Map<String, Object> fieldMap);
}
