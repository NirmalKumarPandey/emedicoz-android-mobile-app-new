package com.emedicoz.app.podcastnew

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.emedicoz.app.R
import com.emedicoz.app.modelo.DailyQuizData
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.podcast.Author
import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.Constants
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface
import com.emedicoz.app.ui.podcast.*
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PodcastRepository {

    private var mProjectRepository: PodcastRepository? = null

    private fun PodcastRepository() {}


    companion object {
        @JvmStatic
        fun getInstance() = PodcastRepository()

    }


    fun loadDataOnViewHolder(
        mContext: Context?,
        mTestViewHolder: PodcastViewModel,
        mProgressListner: ProgressUpdateListner
    ) {
        val mPodCastService = ApiClientTest.getClient().create(
            PodCastService::class.java
        )
        val call = mPodCastService.doGetStoriesList()
        call.enqueue(object : Callback<StoriesList?> {
            override fun onResponse(call: Call<StoriesList?>, response: Response<StoriesList?>) {
                val mList = response.body()
                if (mList != null) {
                    val list = mList.list
                    val mStringb = StringBuilder()
                    for (i in list.indices) {
                        if (list[i].type == "items") {
                            mStringb.append(list[i].salesItemList[0].itemName + ",")
                        }
                    }
                    mProgressListner.update(true)
                    mTestViewHolder.storyList.value = list
                    Log.d("TAGRAM", "Number of items received: $mStringb")
                    //                    Toast.makeText(mContext, mList.getResult(), Toast.LENGTH_SHORT).show();
                } else {
                    mProgressListner.update(true)
                    mTestViewHolder.messageContainerA.setValue(response.toString())
                }
            }

            override fun onFailure(call: Call<StoriesList?>, t: Throwable) {
                Log.d("TAGRAMFAIL", "Number of movies received: $t")
                Toast.makeText(mContext, "FAIL", Toast.LENGTH_SHORT).show()
                mTestViewHolder.messageContainerA.value = "Fail to Load Data"
            }
        })
    }


    fun getPodcastAuthorList(
        mContext: Context?,
        mViewHolder: PodcastViewModel,
        userID: String?,
        selectedAuthorId: String?,
        mProgressListner: ProgressUpdateListner,
        filterType: String
    ) {
        val authorList = ArrayList<Author>()
        val apiInterface = ApiClient.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apiInterface.getPodcastAuthorChannel(
            SharedPreference.getInstance().loggedInUser.id,
            ApiClient.getStreamId(SharedPreference.getInstance().loggedInUser),
            filterType
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val jsonObject1 = GenericUtils.getJsonArray(jsonResponse)
                            val gson = Gson()
                            val type = object : TypeToken<List<Author?>?>() {}.type
                            Log.d("AuthorList", jsonObject1.toString())
                            val authorListServer =
                                gson.fromJson<List<Author>>(jsonObject1.toString(), type)
                            authorList.addAll(authorListServer)
                            mViewHolder.authorList.value = authorList
                            mProgressListner.update(true)
                        } else {
                            mProgressListner.update(true)
                            Toast.makeText(
                                mContext,
                                jsonResponse.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        //refreshPodcastList(false);
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Toast.makeText(mContext, "Failed to load Author List", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun loadPoadCastDataOnViewHolder(
        mContext: Context?,
        mViewHolder: PodcastViewModel,
        mProgressListner: ProgressUpdateListner,
        userID: String?,
        selectedAuthorId: String?,
        filterType: String,
        page:Int
    ) {
        val podcastList = ArrayList<Podcast>()
        val apiInterface = ApiClient.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apiInterface.getPodcastData(userID, selectedAuthorId, filterType,
            page.toString()
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.e("OnResponse", "  onResponse: " + response.body())
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(
                            "PodcastRepository",
                            " loadPoadCastDataOnViewHolder onResponse: $jsonResponse"
                        )
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val jsonObject1 = GenericUtils.getJsonObject(jsonResponse)
                            val podcastListData = jsonObject1.getString("my_podcast")
                            val gson = Gson()
                            val type = object : TypeToken<List<Podcast?>?>() {}.type
                            val list = gson.fromJson<List<Podcast>>(podcastListData, type)
                            podcastList.addAll(list)
                            mProgressListner.update(true)
                            mViewHolder.getpodcastList().value = list
                        } else {
                            mProgressListner.update(true)
                            Toast.makeText(
                                mContext,
                                jsonResponse.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "PodcastException",
                            " loadPoadCastDataOnViewHolder onResponse: $e"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // Helper.showExceptionMsg(activity, t);
                Log.d("PODCASTFAIL", " exception: $t")
                Toast.makeText(mContext, "FAIL TO LOAD POAD CAST", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun loadPodcastBookmarkedData(
        mContext: Context?,
        mViewHolder: PodcastViewModel,
        mProgressListner: ProgressUpdateListner,
        userID: String?,
        selectedAuthorId: String?,
        filterType: String,
        pageNumber:Int
    ) {
        val podcastList = ArrayList<Podcast>()
        val apiInterface = ApiClient.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apiInterface.getBookmarkedPodcastDataV2(userID,filterType,
            pageNumber.toString()
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.e("OnResponse", "  onResponse: " + response.body())
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(
                            "PodcastRepository",
                            " loadPoadCastDataOnViewHolder onResponse: $jsonResponse"
                        )
                        if (jsonResponse.optBoolean(Const.STATUS)) {
/*                            val jsonObject1 = GenericUtils.getJsonObject(jsonResponse)
                            val podcastListData = jsonObject1.optJSONObject("my_podcast")
                            val gson = Gson()
                            val type = object : TypeToken<List<Podcast?>?>() {}.type
                            val list = gson.fromJson<List<Podcast>>(podcastListData, type)
                            podcastList.addAll(list)*/
                            val dataArray = GenericUtils.getJsonArray(jsonResponse)
                            for (i in 0 until dataArray.length()) {
                                val dataObj: JSONObject = dataArray.optJSONObject(i)
                                val gson = Gson()
                                val podcastListData = gson.fromJson(
                                    dataObj.toString(),
                                    Podcast::class.java
                                )
                                podcastList.add(podcastListData)
                            }
                            mProgressListner.update(true)
                            mViewHolder.getBookmarkedPodcastList().value = podcastList
                        } else {
                            mProgressListner.update(true)
                            Toast.makeText(
                                mContext,
                                jsonResponse.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "PodcastException",
                            " loadPoadCastDataOnViewHolder onResponse: $e"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // Helper.showExceptionMsg(activity, t);
                Log.d("PODCASTFAIL", " exception: $t")
                Toast.makeText(mContext, "FAIL TO LOAD POAD CAST", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun loadPoadCastDataTest(userID: String?, selectedAuthorId: String?) {
        val podcastList = ArrayList<Podcast>()
        val apiInterface = ApiClientTest.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apiInterface.getPodcastData(userID, selectedAuthorId, "","1")
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(
                            "PodcastRepository",
                            " loadPoadCastDataOnViewHolder onResponse: $jsonResponse"
                        )
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val jsonObject1 = GenericUtils.getJsonObject(jsonResponse)
                            val podcastListData = jsonObject1.getString("my_podcast")
                            val gson = Gson()
                            val type = object : TypeToken<List<Podcast?>?>() {}.type
                            val list = gson.fromJson<List<Podcast>>(podcastListData, type)
                            podcastList.addAll(list)
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "PodcastException",
                            " loadPoadCastDataOnViewHolder onResponse: $e"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // Helper.showExceptionMsg(activity, t);
                Log.d("PODCASTFAIL", " exception: $t")
            }
        })
    }

    fun networkCallForBookmark(activity: Activity, podcastId: String) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apiInterface = ApiClient.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apiInterface.managePodcastBookmark(
            SharedPreference.getInstance().loggedInUser.id,
            podcastId, "1"
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        Constants.UPDATE_LIST = "true"
                        jsonResponse = JSONObject(jsonObject.toString())
                        Toast.makeText(
                            activity,
                            jsonResponse.optString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else Toast.makeText(
                    activity,
                    R.string.exception_api_error_message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showExceptionMsg(activity, t)
            }
        })
    }

    fun loadAuthorPodcastById(
        mContext: Context?,
        mViewHolder: PodcastViewModel,
        mProgressListner: ProgressUpdateListner,
        authorId: String?
    ) {
        val podcastList = ArrayList<Podcast>()
        val apiInterface = ApiClient.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apiInterface.getPodcastAuthorPodcastById(
            SharedPreference.getInstance().loggedInUser.id,
            authorId,
            ApiClient.getStreamId(SharedPreference.getInstance().loggedInUser)
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.e("OnResponse", "  onResponse: " + response.body())
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(
                            "PodcastRepository",
                            " loadPoadCastDataOnViewHolder onResponse: $jsonResponse"
                        )
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val dataArray = GenericUtils.getJsonArray(jsonResponse)
                            for (i in 0 until dataArray.length()) {
                                val dataObj: JSONObject = dataArray.optJSONObject(i)
                                val gson = Gson()
                                val podcastListData = gson.fromJson(
                                    dataObj.toString(),
                                    Podcast::class.java
                                )
                                podcastList.add(podcastListData)
                            }
                            mProgressListner.update(true)
                            mViewHolder.getPodcastByUserId().value = podcastList
                        }else{
                            mProgressListner.update(true)
                            mContext?.show(
                                jsonResponse.optString("message"),
                                Toast.LENGTH_SHORT
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "PodcastException",
                            " loadPoadCastDataOnViewHolder onResponse: $e"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // Helper.showExceptionMsg(activity, t);
                Log.d("PODCASTFAIL", " exception: $t")
                Toast.makeText(mContext, "FAIL TO LOAD POAD CAST", Toast.LENGTH_SHORT).show()
            }
        })
    }

}