package com.emedicoz.app.courses.fragment

import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.video.adapter.DVLParentAdapter
import com.emedicoz.app.modelo.dvl.DvlData
import com.emedicoz.app.modelo.dvl.DVLTopic
import com.emedicoz.app.utilso.MyNetworkCall
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emedicoz.app.utilso.network.API
import com.google.gson.JsonObject
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.databinding.FragmentEBookBinding
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.Const
import org.json.JSONException
import org.json.JSONObject
import com.emedicoz.app.utilso.GenericUtils
import com.google.gson.Gson
import retrofit2.Call
import java.util.ArrayList
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [CRSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CRSFragment : Fragment(), MyNetworkCallBack {
    private lateinit var binding: FragmentEBookBinding
    var activity: Activity? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    var arrayList: ArrayList<String>? = null
    private lateinit var adapter: DVLParentAdapter
    var crsData: DvlData? = null
    var hashMap: HashMap<Int, ArrayList<DVLTopic>>? = null
    var courseId: String? = null
    private lateinit var myNetworkCall: MyNetworkCall
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
        if (arguments != null) {
            courseId = arguments!!.getString(Const.COURSE_ID)
        }
    }

    override fun onResume() {
        super.onResume()
//        if (activity is BaseABNavActivity) {
//            //(activity as BaseABNavActivity).toolbar.setBackgroundResource(R.color.dark_ebook)
//            //(activity as BaseABNavActivity).searchView.visibility = View.VISIBLE
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        myNetworkCall = MyNetworkCall(this, activity)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.eBookRV.layoutManager = linearLayoutManager
        arrayList = ArrayList()
        hashMap = HashMap()
        networkCallForCRS()
    }

    private fun networkCallForCRS() {
        myNetworkCall.NetworkAPICall(API.API_GET_VIDEO_COURSE, true)
    }

    override fun getAPI(apiType: String, service: FlashCardApiInterface): Call<JsonObject> {
        val param: MutableMap<String, Any?> = HashMap()
        param[Const.USER_ID] = SharedPreference.getInstance().loggedInUser.id
        param[Const.COURSE_ID] = courseId
        Log.e(TAG, "getAPI: $param")
        return service.postData(apiType, param)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonObject: JSONObject, apiType: String) {
        val data = GenericUtils.getJsonObject(jsonObject)
        crsData = Gson().fromJson(data.toString(), DvlData::class.java)
        initCRSAdapter(crsData)
    }

    override fun errorCallBack(jsonString: String, apiType: String) {
        GenericUtils.showToast(activity, jsonString)
    }

    private fun initCRSAdapter(crsData: DvlData?) {
        if (crsData != null) {
            if (hashMap!!.size > 0) hashMap!!.clear()
            val decks = ArrayList<String>()
            if (decks.isNotEmpty()) decks.clear()
            for (i in crsData.curriculam.topics.indices) {
                if (crsData.curriculam.topics[i].deck != null &&
                    !decks.contains(crsData.curriculam.topics[i].deck)
                ) {
                    decks.add(crsData.curriculam.topics[i].deck)
                }
            }
            Log.e("onResponse: ", "Deck_SIZE" + decks.size)
            for (i in decks.indices) {
                val topics = ArrayList<DVLTopic>()
                if (topics.size > 0) topics.clear()
                for (j in crsData.curriculam.topics.indices) {
                    if (crsData.curriculam.topics[j].deck != null &&
                        decks[i].equals(crsData.curriculam.topics[j].deck, ignoreCase = true)
                    ) {
                        topics.add(crsData.curriculam.topics[j])
                    }
                }
                hashMap!![i] = topics
            }
            if (decks.isNotEmpty()) {
                binding.txvNodata.visibility = View.GONE
                adapter = DVLParentAdapter(activity, decks, hashMap, crsData, Const.CRS_SECTION)
                binding.eBookRV.adapter = adapter
            } else binding.txvNodata.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val TAG = "CRSFragment"

        @JvmStatic
        fun newInstance(courseId: String?): CRSFragment {
            val fragment = CRSFragment()
            val args = Bundle()
            args.putString(Const.COURSE_ID, courseId)
            fragment.arguments = args
            return fragment
        }
    }
}