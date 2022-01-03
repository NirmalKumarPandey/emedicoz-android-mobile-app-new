package com.emedicoz.app.courses.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.courses.adapter.OrderAdapter
import com.emedicoz.app.databinding.FragmentOrderBinding
import com.emedicoz.app.modelo.testseries.OrderHistoryData
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class OrderFragment : Fragment(), MyNetworkCallBack {
    private var previousTotalItemCount = 0
    private lateinit var binding: FragmentOrderBinding
    private lateinit var activity: Activity
    private var lastId = ""
    private lateinit var orderArrayList: ArrayList<OrderHistoryData>
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var loading = true
    private val visibleThreshold = 5
    private lateinit var myNetworkCall: MyNetworkCall
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderArrayList = ArrayList()
        myNetworkCall = MyNetworkCall(this, activity)
        linearLayoutManager = LinearLayoutManager(activity)
        orderAdapter = OrderAdapter(activity, orderArrayList)
        binding.apply {
            orderRV.layoutManager = linearLayoutManager
            orderRV.adapter = orderAdapter
            orderRV.addItemDecoration(
                EqualSpacingItemDecoration(
                    20,
                    EqualSpacingItemDecoration.VERTICAL
                )
            )
        }
        refreshDataList(1)
        binding.orderRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    refreshDataList(2) // for pagination
                    loading = true
                }
            }
        })
    }

    private fun refreshDataList(type: Int) {
        if (type == 1 && orderArrayList.isNotEmpty()) {
            orderArrayList.clear()
        }
        myNetworkCall.NetworkAPICall(API.API_GET_ORDER_HISTORY, true)
    }

    private fun initOrdersAdapter() {
        orderAdapter.notifyDataSetChanged()
    }

    override fun getAPI(apiType: String, service: FlashCardApiInterface): Call<JsonObject> {
        val params: MutableMap<String, Any> = HashMap()
        params[Const.USER_ID] = SharedPreference.getInstance().loggedInUser.id
        params[Const.LAST_ID] = lastId
        return service.postData(apiType, params)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonObject: JSONObject, apiType: String) {
        val gson = Gson()
        val data = GenericUtils.getJsonArray(jsonObject)
        if (data.length() != 0) {
            for (i in 0 until data.length()) {
                val dataObj = data.optJSONObject(i)
                val orderHistoryData =
                    gson.fromJson(dataObj.toString(), OrderHistoryData::class.java)
                orderArrayList.add(orderHistoryData)
            }
            lastId = orderArrayList[orderArrayList.size - 1].id
            binding.apply {
                orderRV.visibility = View.VISIBLE
                textnocontent.visibility = View.GONE
                imgnobookmarks.visibility = View.GONE
            }
            initOrdersAdapter()
        } else {
            if (!GenericUtils.isEmpty(lastId)) return
            binding.apply {
                orderRV.visibility = View.GONE
                imgnobookmarks.visibility = View.VISIBLE
                textnocontent.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.no_orders_found)
                }
            }
        }
    }

    override fun errorCallBack(jsonString: String, apiType: String) {
        Log.e("errorCallBack: ", jsonString)
        if (!GenericUtils.isEmpty(lastId)) return

        binding.apply {
            orderRV.visibility = View.GONE
            imgnobookmarks.visibility = View.VISIBLE
            textnocontent.apply {
                visibility = View.VISIBLE
                text = getString(R.string.no_orders_found)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): OrderFragment {
            return OrderFragment()
        }
    }
}