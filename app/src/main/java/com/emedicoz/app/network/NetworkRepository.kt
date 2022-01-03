package com.emedicoz.app.network

import android.content.Context
import android.util.Log
import com.emedicoz.app.cart.AddressListViewModel
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.network.model.AddressResponse
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.models.LatestNewsListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.newsandarticle.models.TopicListResponse
import com.emedicoz.app.newsandarticle.viewmodel.NewsAndArticleViewModel
import com.emedicoz.app.newsandarticle.viewmodel.TopicTagHomeViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.SharedPreference

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository {

    val TAG = NetworkRepository::class.java.simpleName

    companion object {

        @JvmStatic
        fun newInstance() {
            NetworkRepository()
        }
    }


    public fun callAddressList(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner?,
        mAddressListViewModel: AddressListViewModel,
        userID: String
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getAddress(userID)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response : " + response.body())

                var mAddressResponse: AddressResponse = Gson().fromJson(response.body(), AddressResponse::class.java)

                mAddressListViewModel.addressList.value = mAddressResponse.data

                if (mProgressListner != null)
                    mProgressListner.update(true)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                if (mProgressListner != null)
                    mProgressListner.update(true)
            }


        })

    }


    public fun insertAddressList(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner,
        mAddressListViewModel: AddressListViewModel,
        mAddressBook: AddressBook?
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)


        val mCall = mApiInterface?.insertAddress(

            mAddressBook?.user_id,
            mAddressBook?.name,
            mAddressBook?.code,
            mAddressBook?.phone,
            mAddressBook?.pincode,
            mAddressBook?.address,
            mAddressBook?.address_2,
            mAddressBook?.city,
            mAddressBook?.state,
            mAddressBook?.country,
            mAddressBook?.latitude,
            mAddressBook?.longitude,
            mAddressBook?.is_default
        )
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response  Address Insert: " + response.body())

                var mAddressResponse: AddressResponse =
                    Gson().fromJson(response.body(), AddressResponse::class.java)

                mProgressListner.update(true)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }


        })

    }

    public fun editAddressList(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner,
        mAddressListViewModel: AddressListViewModel,
        mAddressBook: AddressBook?
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)


        val mCall = mApiInterface?.editAddress(

            mAddressBook?.user_id,
            mAddressBook?.id,
            mAddressBook?.name,
            mAddressBook?.code,
            mAddressBook?.phone,
            mAddressBook?.pincode,
            mAddressBook?.address,
            mAddressBook?.address_2,
            mAddressBook?.city,
            mAddressBook?.state,
            mAddressBook?.country,
            mAddressBook?.latitude,
            mAddressBook?.longitude,
            mAddressBook?.is_default
        )
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response  Address Insert: " + response.body())

                var mAddressResponse: AddressResponse =
                    Gson().fromJson(response.body(), AddressResponse::class.java)

                mProgressListner.update(true)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }


        })

    }

    public fun call_latest_News_List(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner,
        mNewsAndArticleViewModel: NewsAndArticleViewModel,
        userID: String,
        type: String
    ) {

        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getNewsList(userID,type)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response News : " + response.body())

               var    newsResponse : NewsListResponse = Gson().fromJson(response.body(), NewsListResponse::class.java)

                mNewsAndArticleViewModel.newsList.value = newsResponse
                mProgressListner.update(true)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }


        })

    }




    public fun call_topic_tag_List(
            mContext: Context?,
            mProgressListner: ProgressUpdateListner,
            mTopicTagHomeViewModel: TopicTagHomeViewModel,
            userID: String,
            type: String
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getTopicTagList(userID,type)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response News : " + response.body())

                var    topicListResponse: TopicListResponse = Gson().fromJson(response.body(), TopicListResponse::class.java)

                mTopicTagHomeViewModel.topTagList.value = topicListResponse
                mProgressListner.update(true)

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }


        })

    }


    // book mark list

   /* public fun getApiCall() {

        val mApiInterface = ApiClientNew().getClient()?.create(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getBookmarkList(SharedPreference.getInstance().getLoggedInUser().getId())

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Article Detail", "Response News : " + response.body())
                var    newsResponse : NewsListResponse = Gson().fromJson(response.body(), NewsListResponse::class.java)

                // mNewsAndArticleViewModel.newsList.value = newsResponse
                //  mProgressListner.update(true)
                val mAdapter = this?.let { LatestNewsListRecycleViewAdapter(this@BookmarkActivity,newsResponse.data ) }
                // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                binding.RecyclerView.setItemAnimator(null)
                binding.RecyclerView.setAdapter(mAdapter)

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }


        })

    }*/



    public fun getBookmarkList(
            mContext: Context?,
            mProgressListner: ProgressUpdateListner,
            mNewsAndArticleViewModel: NewsAndArticleViewModel,
            userID: String,
    ) {

        val mApiInterface =ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getBookmarkList(userID)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response News : " + response.body())

                var    newsResponse : NewsListResponse = Gson().fromJson(response.body(), NewsListResponse::class.java)

                mNewsAndArticleViewModel.newsList.value = newsResponse
                mProgressListner.update(true)

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }


        })

    }



    //for topic detail list

    public fun getTopicDetailList(mContext: Context?, mProgressListner: ProgressUpdateListner, mNewsAndArticleViewModel: NewsAndArticleViewModel,
            userID: String,type:String,type_id:String
    ) {

        val mApiInterface =ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getTopicDetailList(userID,type,type_id)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response News : " + response.body())

                var    newsResponse : NewsListResponse = Gson().fromJson(response.body(), NewsListResponse::class.java)

                mNewsAndArticleViewModel.newsList.value = newsResponse
                mProgressListner.update(true)

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }


        })

    }

    public fun call_search_topic_tag_List(
            mContext: Context?,
            mProgressListner: ProgressUpdateListner,
            mTopicTagHomeViewModel: TopicTagHomeViewModel,
            userID: String,
            type: String,
            searchText:String
    ) {

        val mApiInterface =
            ApiClient.createService(ApiInterfacesNew::class.java)

        val mCall = mApiInterface?.getSearchTopicTagList(userID,type,searchText)

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d(TAG, "Response News : " + response.body())

                var    topicListResponse: TopicListResponse = Gson().fromJson(response.body(), TopicListResponse::class.java)

                mTopicTagHomeViewModel.topTagList.value = topicListResponse
                mProgressListner.update(true)

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgressListner.update(false)
            }
        })

    }


}

