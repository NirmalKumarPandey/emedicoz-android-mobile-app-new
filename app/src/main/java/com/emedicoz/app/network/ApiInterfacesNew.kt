package com.emedicoz.app.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterfacesNew {
    @FormUrlEncoded
    @POST("data_model/user/User_cart/get_address")
    public fun getAddress(
        @Field(ConstantsNew.USER_ID) userid: String?
        ): Call<JsonObject?>?

    @FormUrlEncoded
    @POST("data_model/user/User_cart/add_new_address")
    public fun insertAddress(

        @Field("user_id") user_id: String?,
        @Field("name") name: String?,
        @Field("code") code: String?,
        @Field("phone") phone: String?,
        @Field("pincode") pincode: String?,
        @Field("address") address: String?,
        @Field("address_2") address_2: String?,
        @Field("city") city: String?,
        @Field("state") state: String?,
        @Field("country") country: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("default") is_default: String?


    ): Call<JsonObject?>?

    @FormUrlEncoded
    @POST("data_model/user/User_cart/edit_address")
    public fun editAddress(
        @Field("user_id") user_id: String?,
        @Field("address_id") addressId: String?,
        @Field("name") name: String?,
        @Field("code") code: String?,
        @Field("phone") phone: String?,
        @Field("pincode") pincode: String?,
        @Field("address") address: String?,
        @Field("address_2") address_2: String?,
        @Field("city") city: String?,
        @Field("state") state: String?,
        @Field("country") country: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("is_default") is_default: String?


    ): Call<JsonObject?>?

    @FormUrlEncoded
    @POST("data_model/user/User_cart/set_default_address")
    public fun setDefaultAddress(
        @Field(ConstantsNew.USER_ID) userid: String?,
        @Field("address_id") addressId: String?
    ): Call<JsonObject?>?


    @FormUrlEncoded
    @POST("data_model/user/User_cart/delete_address")
    public fun deleteAddress(
        @Field(ConstantsNew.USER_ID) userid: String?,
        @Field("address_id") addressId: String?
    ): Call<JsonObject?>?


    @FormUrlEncoded
    //@POST("data_model/courses/News_articles/articles_list")
    @POST("data_model/courses/News_articles/articles_news_list")
    public fun getNewsList(
        @Field("user_id") user_id: String?,
        @Field("type") type: String?,
    ): Call<JsonObject?>?


    @FormUrlEncoded
    //@POST("data_model/courses/News_articles/articles_list")
   // @POST("data_model/courses/News_articles/subject")
    @POST("data_model/courses/News_articles/topic_tag_list")
    public fun getTopicTagList(
            @Field("user_id") user_id: String?,
            @Field("type") type: String?,
    ): Call<JsonObject?>?



    //article detail

    @FormUrlEncoded
    @POST("data_model/courses/News_articles/articles_detail")
    public fun getArticleDetail(
            @Field("user_id") user_id: String?,
            @Field("article_id") article_id: String?,
    ): Call<JsonObject?>?

    //like

    @FormUrlEncoded
    @POST("data_model/courses/News_articles/articles_like")
    public fun article_like(
            @Field("user_id") user_id: String?,
            @Field("article_id") article_id: String?,
    ): Call<JsonObject?>?


    //unlike
    @FormUrlEncoded
    @POST("data_model/courses/News_articles/articles_unlike")
    public fun removeLike(
            @Field("user_id") user_id: String?,
            @Field("article_id") article_id: String?,
    ): Call<JsonObject?>?

  //add bookmark
    @FormUrlEncoded
    @POST("data_model/courses/News_articles/add_bookmark")
    public fun addBookmark(
            @Field("user_id") user_id: String?,
            @Field("article_id") article_id: String?,
    ): Call<JsonObject?>?

    //remove bookmark
    @FormUrlEncoded
    @POST("data_model/courses/News_articles/remove_bookmark")
    public fun removeBookmark(
            @Field("user_id") user_id: String?,
            @Field("article_id") article_id: String?,
    ): Call<JsonObject?>?

// article_share_API

    @FormUrlEncoded
    @POST("data_model/courses/News_articles/articles_share")
    public fun article_share_API(

            @Field("user_id") user_id: String?,
            @Field("article_id") article_id: String?,
    ): Call<JsonObject?>?


    // bookmark List
    @FormUrlEncoded
    @POST("data_model/courses/News_articles/bookmark_list")
    public fun  getBookmarkList(
            @Field("user_id") user_id: String?
    ): Call<JsonObject?>?


    // topic_tag_detail List
    @FormUrlEncoded
    @POST("data_model/courses/News_articles/topic_tag_detail")
    public fun  getTopicDetailList(
            @Field("user_id") user_id: String?,
            @Field("type") type: String?,
            @Field("type_id") type_id: String?

    ): Call<JsonObject?>?


    // search_topic_tag_List
    @FormUrlEncoded
    @POST("data_model/courses/News_articles/search_list")
    public fun   getSearchTopicTagList(
            @Field("user_id") user_id: String?,
            @Field("search") search: String?,
            @Field("type") type: String?

    ): Call<JsonObject?>?


    @FormUrlEncoded
    @POST("data_model/courses/News_articles/follow_unfollow_tag")
    public fun   follow_TagAPi(
            @Field("user_id") user_id: String?,
            @Field("tag_id") tag_id: String?,
            @Field("type") type: String?

    ): Call<JsonObject?>?

}