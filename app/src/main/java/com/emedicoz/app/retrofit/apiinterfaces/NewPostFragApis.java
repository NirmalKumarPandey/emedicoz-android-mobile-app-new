package com.emedicoz.app.retrofit.apiinterfaces;

import com.emedicoz.app.utilso.Const;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NewPostFragApis {
    @FormUrlEncoded
    @POST("data_model/user/post_mcq/add_mcq")
// &post_id=10 /post_id=10 ?
    Call<JsonObject> getPostMcq(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Const.QUESTION) String QUESTION,
            @Field(Const.ANSWER_ONE) String ANSWER_ONE,
            @Field(Const.ANSWER_TWO) String ANSWER_TWO,
            @Field(Const.ANSWER_THREE) String ANSWER_THREE,
            @Field(Const.ANSWER_FOUR) String ANSWER_FOUR,
            @Field(Const.ANSWER_FIVE) String ANSWER_FIVE,
            @Field(Const.RIGHT_ANSWER) String RIGHT_ANSWER,
            @Field(Const.POST_TAG) String POST_TAG,
            @Field(Const.TAG_PEOPLE) String TAG_PEOPLE,
            @Field(Const.FILE) String FILE
    );

    @FormUrlEncoded
    @POST("data_model/user/post_mcq/edit_mcq")
    Call<JsonObject> getEditPostMcq(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Const.POST_ID) String POST_ID,
            @Field(Const.QUESTION) String QUESTION,
            @Field(Const.ANSWER_ONE) String ANSWER_ONE,
            @Field(Const.ANSWER_TWO) String ANSWER_TWO,
            @Field(Const.ANSWER_THREE) String ANSWER_THREE,
            @Field(Const.ANSWER_FOUR) String ANSWER_FOUR,
            @Field(Const.ANSWER_FIVE) String ANSWER_FIVE,
            @Field(Const.RIGHT_ANSWER) String RIGHT_ANSWER,
            @Field(Const.POST_TAG) String POST_TAG,
            @Field(Const.TAG_PEOPLE) String TAG_PEOPLE,
            @Field(Const.REMOVE_TAG_PEOPLE) String REMOVE_TAG_PEOPLE,
            @Field(Const.FILE) String FILE,
            @Field(Const.DELETE_META) String DELETE_META
    );

    @FormUrlEncoded
    @POST("data_model/user/post_text/add_post")
    Call<JsonObject> getPostNormalVideo(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Const.TEXT) String QUESTION,
            @Field(Const.POST_TYPE) String POST_TYPE,
            @Field(Const.POST_TAG) String POST_TAG,
            @Field(Const.TAG_PEOPLE) String TAG_PEOPLE,
            @Field(Const.FILE) String FILE
    );

    @FormUrlEncoded
    @POST("data_model/user/post_text/edit_post")
    Call<JsonObject> getEditNormalPost(
            @Field(Const.USER_ID) String USER_ID,
            @Field(Const.POST_ID) String POST_ID,
            @Field(Const.TEXT) String TEXT,
            @Field(Const.POST_TYPE) String POST_TYPE,
            @Field(Const.POST_TAG) String POST_TAG,
            @Field(Const.TAG_PEOPLE) String TAG_PEOPLE,
            @Field(Const.REMOVE_TAG_PEOPLE) String REMOVE_TAG_PEOPLE,
            @Field(Const.FILE) String FILE,
            @Field(Const.DELETE_META) String DELETE_META
    );
}
