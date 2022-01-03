package com.emedicoz.app.feeds.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.emedicoz.app.R
import com.emedicoz.app.modelo.MyRewardPoints
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.RewardsPointApiInterface
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RewardsPointsActivity : AppCompatActivity() {
    private var earnedValueTV: TextView? = null
    private  var referralCodeTV:TextView? = null
    private lateinit var inviteBtn: Button
    private lateinit var showTransactionBtn: Button
    private var rewardPoints: MyRewardPoints? = null
    private lateinit var tvHeaderName: TextView
    private lateinit var ibBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards_points)

        initView()
    }

    private fun initView(){
        referralCodeTV = findViewById(R.id.referralcodeTV)
        earnedValueTV = findViewById(R.id.earnedValueTV)
        inviteBtn = findViewById(R.id.inviteBtn)
        showTransactionBtn = findViewById(R.id.showTransactionBtn)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        tvHeaderName.text = "Invite and Earn"
        ibBack = findViewById(R.id.ibBack)

        ibBack.setOnClickListener(View.OnClickListener { finish() })

        inviteBtn.setOnClickListener(View.OnClickListener {
            if (inviteBtn.text == "Retry") networkCallForRewardPoints() //NetworkAPICall(API.API_GET_REWARD_POINTS, true);
            else {
                if (rewardPoints != null) sendEmailForReferral() else networkCallForRewardPoints() //NetworkAPICall(API.API_GET_REWARD_POINTS, true);
            }
        })
        showTransactionBtn.setOnClickListener(View.OnClickListener { v: View? -> Helper.GoToPostActivity(this, null, Const.REWARD_TRANSACTION_FRAGMENT) })
        networkCallForRewardPoints() //NetworkAPICall(API.API_GET_REWARD_POINTS, true);
    }

    fun initData() {
        if (rewardPoints != null) {
            earnedValueTV!!.text = "Total Points " + rewardPoints!!.reward_points
            referralCodeTV!!.text = rewardPoints!!.refer_code
        }
    }

    fun sendEmailForReferral() {
        val shortLinkTask: Task<ShortDynamicLink> = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://emedicoz.com/?invitedBy=" + rewardPoints!!.refer_code))
                .setDynamicLinkDomain("wn2d8.app.goo.gl")
                .setAndroidParameters(AndroidParameters.Builder().build()) // Open links with com.example.ios on iOS
                //                .setIosParameters(new DynamicLink.IosParameters.Builder("com.eMedicoz.app").build())
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, OnCompleteListener<ShortDynamicLink> { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result.shortLink
                        val msgHtml = String.format("<p>Hey There!!! Checkout new eMedicoz app on your Smart Phone. Download it now with my"
                                + "<a href=\"%s\">referrer link</a>!</p>", shortLink.toString())
                        Helper.sendLink(this, "eMedicoz Referral Link", "Hey There!!! Checkout new eMedicoz app on your Smart Phone. Download it now with my referrer link: " + shortLink.toString(),
                                msgHtml
                        )
                    } else {
                        Toast.makeText(this, "Link could not be generated please try again!", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    fun networkCallForRewardPoints() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apiInterface = ApiClient.createService(RewardsPointApiInterface::class.java)
        val response = apiInterface.getrewarPointsUser(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (this@RewardsPointsActivity.isFinishing()) return
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            rewardPoints = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MyRewardPoints::class.java)
                            initData()
                            showTransactionBtn.visibility = View.VISIBLE
                            inviteBtn.setText(this@RewardsPointsActivity.getResources().getString(R.string.invite))
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(this@RewardsPointsActivity.getResources().getString(R.string.internet_error_message), ignoreCase = true)) Helper.showErrorLayoutForNav(API.API_GET_REWARD_POINTS, this@RewardsPointsActivity, 1, 1)
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(getString(R.string.something_went_wrong_string))) Helper.showErrorLayoutForNav(API.API_GET_REWARD_POINTS, this@RewardsPointsActivity, 1, 2)
                            RetrofitResponse.getApiData(this@RewardsPointsActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showExceptionMsg(this@RewardsPointsActivity, t)
                Helper.showErrorLayoutForNav(API.API_GET_REWARD_POINTS, this@RewardsPointsActivity, 1, 2)
            }
        })
    }
}