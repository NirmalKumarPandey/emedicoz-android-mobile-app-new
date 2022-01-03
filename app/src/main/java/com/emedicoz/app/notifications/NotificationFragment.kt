package com.emedicoz.app.notifications

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.Model.offlineData
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.feeds.adapter.NotificationRVAdapter
import com.emedicoz.app.response.NotificationResponse
import com.emedicoz.app.response.PostResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class NotificationFragment : Fragment() {

    lateinit var mProgress: Progress
    var lastActivityId = ""
    lateinit var notificationArrayList: ArrayList<NotificationResponse>
    var totalItemCount = 0
    lateinit var errorMessage: String
    var sharedPreference = SharedPreference.getInstance()
    lateinit var notificationRVAdapter: NotificationRVAdapter
    lateinit var outerCommonRV: RecyclerView
    lateinit var outerrorTV: TextView
    lateinit var no_notificationLayout: LinearLayout
    var post: PostResponse? = null
    private var offlinedata: offlineData? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mProgress = Progress(context)
        mProgress.setCancelable(false)
        outerCommonRV = view.findViewById(R.id.outercommonRV)
        outerrorTV = view.findViewById(R.id.outerrorTV)
        no_notificationLayout = view.findViewById(R.id.no_notificationLayout)
        notificationArrayList = ArrayList()
        //lastActivityId = notificationArrayList.get(totalItemCount - 1).getId()
        if (post != null)
            offlinedata = eMedicozDownloadManager.getOfflineDataIds(
                post!!.id,
                Const.FEEDS,
                activity,
                post!!.id
            )
        networkCallForGetUserNoti(true)
    }

    override fun onResume() {
        super.onResume()
        if (activity is HomeActivity) {
            (activity as HomeActivity).toolbarHeader.visibility = View.GONE
            val actionBar = (activity as HomeActivity).supportActionBar
            actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
        networkCallForReadNoti()
    }

    fun networkCallForGetUserNoti(show: Boolean) {
        if (show) mProgress.show()
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response =
            apis.getUserNotification(SharedPreference.getInstance().loggedInUser.id, lastActivityId)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (show) mProgress.dismiss()
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val jsonArray = GenericUtils.getJsonArray(jsonResponse)
                            if (TextUtils.isEmpty(lastActivityId)) {
                                notificationArrayList = ArrayList<NotificationResponse>()
                            }
                            if (jsonArray.length() > 0) {
                                var i = 0
                                while (i < jsonArray.length()) {
                                    val singledatarow = jsonArray.optJSONObject(i)
                                    val response1 = gson.fromJson(
                                        singledatarow.toString(),
                                        NotificationResponse::class.java
                                    )
                                    notificationArrayList.add(response1)
                                    i++
                                }
                            }
                        } else {
                            //errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_USER_NOTIFICATIONS)
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                        //checkFeedActivity(API.API_GET_USER_NOTIFICATIONS)
                        initNotificationAdapter()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                if (show) mProgress.dismiss()
                //if (offlinedata == null) Helper.showErrorLayoutForNoNav(API.API_GET_USER_NOTIFICATIONS, activity, 1, 1)
            }
        })
    }

    protected fun initNotificationAdapter() {
        if (TextUtils.isEmpty(lastActivityId)) {
            if (!notificationArrayList.isEmpty()) {
                notificationRVAdapter = NotificationRVAdapter(notificationArrayList, activity)
                outerCommonRV.setAdapter(notificationRVAdapter)
                outerCommonRV.setVisibility(View.VISIBLE)
            } else {
                no_notificationLayout.setVisibility(View.VISIBLE)
                outerrorTV.setVisibility(View.GONE)
                outerCommonRV.setVisibility(View.GONE)
            }
        } else notificationRVAdapter.notifyDataSetChanged()
    }

    fun networkCallForReadNoti() {
        mProgress.show()
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getReadNotification(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            lastActivityId = ""
                            if (!notificationArrayList.isEmpty()) {
                                for (NR in notificationArrayList) {
                                    NR.view_state = 1
                                }
                            }
                            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, 0)
                            ShortcutBadger.removeCount(activity!!.applicationContext)
                        } else {
                            //errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_ALL_NOTIFICATION_READ)
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                        initNotificationAdapter()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
            }
        })
    }

}