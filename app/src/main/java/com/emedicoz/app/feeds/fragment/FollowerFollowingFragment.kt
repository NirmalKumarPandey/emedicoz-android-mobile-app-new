package com.emedicoz.app.feeds.fragment

import com.emedicoz.app.feeds.adapter.FeedRVAdapter
import com.emedicoz.app.feeds.adapter.PeopleFollowRVAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.Activity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.response.PostResponse
import com.emedicoz.app.response.FollowResponse
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentFollowerFollowingBinding
import com.emedicoz.app.retrofit.apiinterfaces.ProfileApiInterface
import com.emedicoz.app.retrofit.ApiClient
import com.google.gson.JsonObject
import org.json.JSONObject
import com.google.gson.Gson
import org.json.JSONArray
import com.emedicoz.app.retrofit.RetrofitResponse
import org.json.JSONException
import com.emedicoz.app.utilso.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FollowerFollowingFragment : Fragment() {
    private lateinit var binding: FragmentFollowerFollowingBinding
    private lateinit var feedRVAdapter: FeedRVAdapter
    private lateinit var peopleFollowRVAdapter: PeopleFollowRVAdapter
    private var previousTotalItemCount = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var activity: Activity
    private var adapterType: String? = null
    private var id: String? = null
    private var lastPostId: String = ""
    private var lastFollowingId: String = ""
    private var lastFollowersId: String = ""
    private lateinit var mProgress: Progress
    private lateinit var feedArrayList: ArrayList<PostResponse>
    private lateinit var followingArrayList: ArrayList<FollowResponse>
    private lateinit var followersArrayList: ArrayList<FollowResponse>
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var loading = true
    private val visibleThreshold = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()!!
        feedArrayList = ArrayList()
        followingArrayList = ArrayList()
        followersArrayList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowerFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgress = Progress(activity)
        mProgress.setCancelable(false)
        if (arguments != null) {
            adapterType = arguments!!.getString(Constants.Extras.ADAPTER_TYPE)
            id = arguments!!.getString(Constants.Extras.ID)
        }

        linearLayoutManager = LinearLayoutManager(activity)
        binding.followerFollowingRV.layoutManager = linearLayoutManager
        binding.followerFollowingRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = linearLayoutManager.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                if (loading && totalItemCount > previousTotalItemCount) {
                    loading = false
                    previousTotalItemCount = totalItemCount
                }
                if (!loading && totalItemCount - visibleItemCount
                    <= firstVisibleItem + visibleThreshold
                ) {
                    // End has been reached
                    Log.i("Yaeye!", "end called")
                    // Do something
                    if (adapterType!!.equals(Constants.Extras.POST, ignoreCase = true)) lastPostId =
                        feedArrayList[totalItemCount - 1].id else if (adapterType!!.equals(
                            Constants.Extras.FOLLOWING, ignoreCase = true
                        )
                    ) lastFollowingId =
                        followingArrayList[totalItemCount - 1].id else if (adapterType!!.equals(
                            Constants.Extras.FOLLOWERS, ignoreCase = true
                        )
                    ) lastFollowersId = followersArrayList[totalItemCount - 1].id
                    refreshDataList(1) // for pagination
                    loading = true
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshDataList(0)
    }

    fun refreshDataList(type: Int) {
        if (adapterType!!.equals(Constants.Extras.FOLLOWERS, ignoreCase = true)) {
            if (type == 1 || followersArrayList.isEmpty()) networkCallForFollowerList() else initFollowersAdapter()
        } else if (adapterType!!.equals(Constants.Extras.FOLLOWING, ignoreCase = true)) {
            if (type == 1 || followingArrayList.isEmpty()) networkCallForFollowingList() else initFollowingAdapter()
        } else if (adapterType!!.equals(Constants.Extras.POST, ignoreCase = true)) {
            if (type == 0) {
                lastPostId = ""
                if (feedArrayList.size > 0)
                    feedArrayList.clear()
            }
            /*if (type == 1 || feedArrayList.isEmpty()) networkCallForFeeds() else initFeedAdapter()*/
            networkCallForFeeds()
        }
    }

    private fun initFeedAdapter() {
        if (TextUtils.isEmpty(lastPostId)) {
            binding.followerFollowingRV.invalidate()
            if (feedArrayList.isNotEmpty()) {
                binding.followerFollowingRV.visibility = View.VISIBLE
                feedRVAdapter = FeedRVAdapter(activity, feedArrayList, activity)
                binding.followerFollowingRV.adapter = feedRVAdapter
            } else {
                binding.followerFollowingRV.visibility = View.GONE
            }
        } else {
            binding.followerFollowingRV.visibility = View.VISIBLE
            feedRVAdapter.notifyDataSetChanged()
        }
    }

    protected fun initFollowingAdapter() {
        if (TextUtils.isEmpty(lastFollowingId)) {
            binding.followerFollowingRV.invalidate()
            if (followingArrayList.isNotEmpty()) {
                binding.followerFollowingRV.visibility = View.VISIBLE
                //this is to refine the dams momup from non-dams user
                if (TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                    var i = 0
                    while (i < followingArrayList.size) {
                        if (followingArrayList[i].user_id == Const.DAMS_MOPUP_USER_ID) {
                            followingArrayList.removeAt(i)
                            i = followingArrayList.size
                        }
                        i++
                    }
                }
                peopleFollowRVAdapter = PeopleFollowRVAdapter(followingArrayList, activity)
                binding.followerFollowingRV.recycledViewPool.setMaxRecycledViews(0, 0)
                binding.followerFollowingRV.adapter = peopleFollowRVAdapter
            } else {
                binding.followerFollowingRV.visibility = View.GONE
            }
        } else {
            binding.followerFollowingRV.visibility = View.VISIBLE
            peopleFollowRVAdapter.notifyDataSetChanged()
        }
    }

    private fun initFollowersAdapter() {
        if (TextUtils.isEmpty(lastFollowersId)) {
            binding.followerFollowingRV.invalidate()
            if (followersArrayList.isNotEmpty()) {
                binding.followerFollowingRV.visibility = View.VISIBLE

                //this is to refine the dams momup from non-dams user
                if (TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                    var i = 0
                    while (i < followersArrayList.size) {
                        if (followersArrayList[i].user_id == Const.DAMS_MOPUP_USER_ID) {
                            followersArrayList.removeAt(i)
                            i = followersArrayList.size
                        }
                        i++
                    }
                }
                peopleFollowRVAdapter = PeopleFollowRVAdapter(followersArrayList, activity)
                binding.followerFollowingRV.adapter = peopleFollowRVAdapter
            } else {
                binding.followerFollowingRV.visibility = View.GONE
            }
        } else {
            binding.followerFollowingRV.visibility = View.VISIBLE
            peopleFollowRVAdapter.notifyDataSetChanged()
        }
    }

    private fun networkCallForFeeds() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        mProgress.show()
        val apiInterface = ApiClient.createService(
            ProfileApiInterface::class.java
        )
        val response = apiInterface.feedsForUser(
            SharedPreference.getInstance().loggedInUser.id,
            id, lastPostId
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val gson = Gson()
                    val dataArray: JSONArray
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse)
                            if (TextUtils.isEmpty(lastPostId)) {
                                feedArrayList = ArrayList()
                            }
                            if (dataArray.length() > 0) {
                                var i = 0
                                while (i < dataArray.length()) {
                                    val singleDataRow = dataArray.optJSONObject(i)
                                    val response1 = gson.fromJson(
                                        singleDataRow.toString(),
                                        PostResponse::class.java
                                    )
                                    feedArrayList.add(response1)
                                    i++
                                }
                            }
                            binding.noPostLL.visibility = View.GONE
                            initFeedAdapter()
                        } else {
                            if (TextUtils.isEmpty(lastPostId)) {
                                binding.followerFollowingRV.visibility = View.GONE
                                if (binding.noPostLL != null) {
                                    binding.noPostLL.visibility = View.VISIBLE
                                    if (id == SharedPreference.getInstance().loggedInUser.id) {
                                        binding.noPostLL.findViewById<View>(R.id.post_now).visibility =
                                            View.VISIBLE
                                        binding.noPostLL.findViewById<View>(R.id.post_now)
                                            .setOnClickListener {
                                                Helper.GoToFollowExpertActivity(
                                                    activity,
                                                    Const.POST_FRAG
                                                )
                                            }
                                    } else binding.noPostLL.findViewById<View>(R.id.post_now).visibility =
                                        View.GONE
                                }
                            }
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
                Helper.showExceptionMsg(activity, t)
            }
        })
    }

    private fun networkCallForFollowingList() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        mProgress.show()
        val apiInterface = ApiClient.createService(
            ProfileApiInterface::class.java
        )
        val response = apiInterface.getFollowingList(
            SharedPreference.getInstance().loggedInUser.id,
            id, lastFollowingId
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val gson = Gson()
                    val dataArray: JSONArray
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse)
                            if (TextUtils.isEmpty(lastFollowingId)) {
                                followingArrayList = ArrayList()
                            }
                            if (dataArray.length() > 0) {
                                var i = 0
                                while (i < dataArray.length()) {
                                    val singleDataRow = dataArray.optJSONObject(i)
                                    val response1 = gson.fromJson(
                                        singleDataRow.toString(),
                                        FollowResponse::class.java
                                    )
                                    followingArrayList.add(response1)
                                    i++
                                }
                            }
                            initFollowingAdapter()
                        } else {
                            binding.followerFollowingRV.visibility = View.GONE
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
                Helper.showExceptionMsg(activity, t)
            }
        })
    }

    private fun networkCallForFollowerList() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        mProgress.show()
        val apiInterface = ApiClient.createService(
            ProfileApiInterface::class.java
        )
        val response = apiInterface.getFollowerList(
            SharedPreference.getInstance().loggedInUser.id, id,
            lastFollowersId
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val gson = Gson()
                    val dataArray: JSONArray
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse)
                            if (TextUtils.isEmpty(lastFollowersId)) {
                                followersArrayList = ArrayList()
                            }
                            if (dataArray.length() > 0) {
                                var i = 0
                                while (i < dataArray.length()) {
                                    val singleDataRow = dataArray.optJSONObject(i)
                                    val response1 = gson.fromJson(
                                        singleDataRow.toString(),
                                        FollowResponse::class.java
                                    )
                                    followersArrayList.add(response1)
                                    i++
                                }
                            }
                            initFollowersAdapter()
                        } else {
                            if (TextUtils.isEmpty(lastFollowersId)) {
                                binding.followerFollowingRV.visibility = View.GONE
                            }
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
                Helper.showExceptionMsg(activity, t)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(adapterType: String?, id: String?): FollowerFollowingFragment {
            val followerFollowingFragment = FollowerFollowingFragment()
            val args = Bundle()
            args.putString(Constants.Extras.ADAPTER_TYPE, adapterType)
            args.putString(Constants.Extras.ID, id)
            followerFollowingFragment.arguments = args
            return followerFollowingFragment
        }
    }
}