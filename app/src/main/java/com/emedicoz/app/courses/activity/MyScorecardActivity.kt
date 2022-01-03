package com.emedicoz.app.courses.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.courses.adapter.CommonListAdapter
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.header_layout.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class MyScorecardActivity : AppCompatActivity() {

    var previousTotalItemCount = 0
    var commonListRV: RecyclerView? = null
    var start = 0
    var resultTestSeriesArrayList: ArrayList<ResultTestSeries>? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var commonListAdapter: CommonListAdapter? = null
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private var loading = true
    private val visibleThreshold = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_scorecard)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "My Scorecard"

        resultTestSeriesArrayList = ArrayList()
        commonListRV = findViewById(R.id.scorecardRV)
        linearLayoutManager = LinearLayoutManager(this)
        commonListRV!!.layoutManager = linearLayoutManager
        networkCallForTestSeries(true)
        setPagination()
    }

    private fun setPagination() {
        commonListRV!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = linearLayoutManager!!.childCount
                totalItemCount = linearLayoutManager!!.itemCount
                firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotalItemCount) {
                        loading = false
                        previousTotalItemCount = totalItemCount
                    }
                }
                if (!loading && totalItemCount - visibleItemCount
                        <= firstVisibleItem + visibleThreshold) {
                    // End has been reached
                    Log.i("Yaeye!", "end called")
                    // Do something
                    start = start + 1
                    networkCallForTestSeries(false)
                    loading = true
                }
            }
        })
    }

    fun networkCallForTestSeries(status: Boolean) {
        val progress = Progress(this)
        progress.setCancelable(false)
        if (status) progress.show()
        Log.e("TestSeries: ", "start: $start")
        val apiInterface = ApiClient.createService(LandingPageApiInterface::class.java)
        val response = apiInterface.getUserGivenTestSeries(SharedPreference.getInstance().loggedInUser.id, start.toString())
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                val gson = Gson()
                var dataArray: JSONArray? = null
                if (response.body() != null) {
                    val jsonObject = response.body()
                    var jsonResponse: JSONObject? = null
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())

                        /* if (resultTestSeriesArrayList != null)
                            resultTestSeriesArrayList = new ArrayList<>();*/if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse)
                            if (status) {
                                resultTestSeriesArrayList!!.clear()
                            }
                            for (i in 0 until dataArray.length()) {
                                val dataObj = dataArray.optJSONObject(i)
                                val resultTestSeries = gson.fromJson(dataObj.toString(), ResultTestSeries::class.java)
                                resultTestSeriesArrayList!!.add(resultTestSeries)
                            }
                        } else {
                            Log.e("onResponse: ", jsonResponse.optString(Constants.Extras.MESSAGE))
                            if (start == 0) {
                                Toast.makeText(this@MyScorecardActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                                //                            Helper.showErrorLayoutForNoNav(API.API_GET_USER_GIVEN_TESTSERIES, activity, 1, 1);
                                RetrofitResponse.getApiData(this@MyScorecardActivity, jsonResponse.optString(Const.AUTH_CODE))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    initMyTestSeriesAdapter()
                    if (progress.isShowing) progress.dismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //Log.e("onFailure: ", Objects.requireNonNull(t.message))
                if (progress.isShowing) progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_GET_USER_GIVEN_TESTSERIES, this@MyScorecardActivity, 1, 1)
            }
        })
    }

    private fun initMyTestSeriesAdapter() {
        if (!GenericUtils.isListEmpty(resultTestSeriesArrayList)) {
            if (commonListAdapter == null) {
                commonListAdapter = CommonListAdapter(this, resultTestSeriesArrayList)
                commonListRV!!.adapter = commonListAdapter
            } else {
                commonListAdapter!!.notifyDataSetChanged()
            }
            commonListRV!!.visibility = View.VISIBLE
        } else {
            commonListRV!!.visibility = View.GONE
        }
    }
}