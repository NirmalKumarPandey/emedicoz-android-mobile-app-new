package com.emedicoz.app.retrofit.apiinterfaces

import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

public interface BookApis {
    @FormUrlEncoded
    @POST("data_model/courses/course/get_book_list")
    public fun getBookList(
            @Field(Const.USER_ID) userId: String?,
    ): Call<JsonObject?>?


    @FormUrlEncoded
    @POST("data_model/courses/course/get_book_detail")
    public fun bookDetails(
            @Field(Const.USER_ID) userId: String?,
            @Field(Const.BOOK_ID) bookId: String?,
    ): Call<JsonObject?>?

    @FormUrlEncoded
    @POST("data_model/courses/course/get_all_book_detail")
    public fun bookDetailsList(
            @Field(Const.USER_ID) userId: String?,
            @Field(Const.BOOK_TYPE) type: String?,
    ): Call<JsonObject?>?







}