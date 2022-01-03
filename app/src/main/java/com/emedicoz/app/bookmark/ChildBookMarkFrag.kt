package com.emedicoz.app.bookmark

import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import com.emedicoz.app.bookmark.adapter.ChildBookmarkAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.bookmark.model.Datum
import com.emedicoz.app.databinding.FragmentChildBookMarkBinding
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.network.API
import com.emedicoz.app.utilso.*
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import com.google.gson.Gson
import retrofit2.Call
import java.util.ArrayList
import java.util.HashMap

class ChildBookMarkFrag : Fragment(), MyNetworkCallBack {
    private lateinit var binding: FragmentChildBookMarkBinding
    var name: String? = null
    private lateinit var datum: Datum
    private lateinit var itemList: ArrayList<Datum>
    private lateinit var adapter: ChildBookmarkAdapter
    var tagName: String = ""
    var streamId: String = ""
    var qTypeDqb: String = ""
    private lateinit var myNetworkCall: MyNetworkCall
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChildBookMarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            name = arguments!!.getString(Constants.Extras.BOOKMARK_NAME) as String
            tagName = arguments!!.getString(Constants.Extras.TAG_NAME) as String
            qTypeDqb = arguments!!.getString(Constants.Extras.Q_TYPE_DQB) as String
            Log.e(TAG, "onViewCreated: name = $name, tagname = $tagName, qTypeDqb = $qTypeDqb")
        }
        itemList = ArrayList()
        initViews(view)
    }

    private fun initViews(view: View) {
        myNetworkCall = MyNetworkCall(this, activity)
        binding.recyclerbookmark.layoutManager = LinearLayoutManager(activity)
        bindControl()
    }

    private fun bindControl() {
        binding.progressbar.visibility = View.GONE
        when (name) {
            Helper.toUpperCase(Constants.TestType.TEST) -> {
                qTypeDqb = Constants.Type.TEST_SERIES
            }
            Helper.toUpperCase(Constants.TestType.QUIZ) -> {
                qTypeDqb = Constants.Type.DQB
            }
            Helper.toUpperCase(Constants.TestType.DAILY_CHALLENGE) -> {
                qTypeDqb = Constants.Type.DAILY_CHALLENGE
            }
        }
        adapter = ChildBookmarkAdapter(itemList, requireContext(), tagName, qTypeDqb)
        binding.recyclerbookmark.adapter = adapter
    }

    private fun networkCallForBookmarks() {
        streamId = SharedPreference.getInstance().getString(Constants.Extras.STREAM_ID)
        Log.e(
            TAG,
            "networkcallforbookmarks: streamId = $streamId, qTypeDqb = $qTypeDqb, name = $name"
        )
        if (GenericUtils.isEmpty(streamId)) streamId = Constants.Type.DEFAULT_STREAM_ID
       // myNetworkCall.NetworkAPICall(API.API_BOOKMARK_CATEGORY_LIST, true)
        myNetworkCall.NetworkAPICall(API.API_BOOKMARK_CATEGORY_LIST,true)
    }

    private fun setAdapter() {
        if (!GenericUtils.isListEmpty(itemList)) {
            val list: ArrayList<Datum> = ArrayList()
            for (i in itemList.indices) {
                if (itemList[i].total != "0") {
                    list.add(itemList[i])
                }
            }
            if (!GenericUtils.isListEmpty(list)) {
                adapter = ChildBookmarkAdapter(list, requireContext(), tagName, qTypeDqb)
                binding.apply {
                    recyclerbookmark.adapter = adapter
                    recyclerbookmark.visibility = View.VISIBLE
                    textnocontent.visibility = View.GONE
                    imgnobookmarks.visibility = View.GONE
                }
            } else {
                binding.textnocontent.text = String.format(
                    "No Bookmarks Found ! %n%nAll your Bookmarked %s will be available here !",
                    name!!.toLowerCase()
                )
                binding.apply {
                    textnocontent.visibility = View.VISIBLE
                    imgnobookmarks.visibility = View.VISIBLE
                    recyclerbookmark.visibility = View.GONE
                }
            }
        } else {
            binding.textnocontent.text = String.format(
                "No Bookmarks Found ! %n%nAll your Bookmarked %s will be available here !",
                name!!.toLowerCase()
            )
            binding.apply {
                textnocontent.visibility = View.VISIBLE
                imgnobookmarks.visibility = View.VISIBLE
                recyclerbookmark.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume: ")
        networkCallForBookmarks()
    }

    override fun getAPI(apiType: String?, service: FlashCardApiInterface?): Call<JsonObject>? {
        val params: MutableMap<String, Any?> = HashMap()
        params[Const.USER_ID] = SharedPreference.getInstance().loggedInUser.id
        params[Constants.Extras.TYPE] = name
        params[Constants.Extras.STREAM] = streamId
        params[Constants.Extras.Q_TYPE_DQB] = qTypeDqb
        Log.e(TAG, "getAPI: $params")
        return service!!.postData(apiType, params)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonObject: JSONObject, apiType: String) {
        val jsonArray = jsonObject.optJSONArray(Const.DATA)
        if (jsonArray != null && jsonArray.length() > 0) {
            itemList.clear()
            for (i in 0 until jsonArray.length()) {
                if (jsonArray.optJSONObject(i).optString(Const.TOTAL) != "0") {
                    datum =
                        Gson().fromJson(jsonArray.optJSONObject(i).toString(), Datum::class.java)
                    itemList.add(datum)
                }
            }
            if (itemList.isNotEmpty()) {
                adapter.notifyDataSetChanged()
                binding.apply {
                    recyclerbookmark.visibility = View.VISIBLE
                    textnocontent.visibility = View.GONE
                    imgnobookmarks.visibility = View.GONE
                }
            } else {
                binding.textnocontent.text = String.format(
                    "No Bookmarks Found ! \n\nAll your Bookmarked %s will be available here !",
                    name!!.toLowerCase()
                )
                binding.apply {
                    textnocontent.visibility = View.VISIBLE
                    imgnobookmarks.visibility = View.VISIBLE
                    recyclerbookmark.visibility = View.GONE
                }
            }
        } else {
            binding.textnocontent.text = String.format(
                "No Bookmarks Found ! \n\nAll your Bookmarked %s will be available here !",
                name!!.toLowerCase()
            )
            binding.apply {
                textnocontent.visibility = View.VISIBLE
                imgnobookmarks.visibility = View.VISIBLE
                recyclerbookmark.visibility = View.GONE
            }
        }
    }

    override fun errorCallBack(jsonString: String, apiType: String) {
        binding.textnocontent.text = String.format(
            "No Bookmarks Found ! \n\nAll your Bookmarked %s will be available here !",
            name!!.toLowerCase()
        )
        binding.apply {
            textnocontent.visibility = View.VISIBLE
            imgnobookmarks.visibility = View.VISIBLE
            recyclerbookmark.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "ChildBookMarkFrag"
    }
}