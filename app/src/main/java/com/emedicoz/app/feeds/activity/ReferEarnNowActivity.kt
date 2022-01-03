package com.emedicoz.app.feeds.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.referralcode.adapter.MyAffiliationAdapter
import com.emedicoz.app.referralcode.adapter.paymentHistoryAdapter
import com.emedicoz.app.referralcode.model.*
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.ReferralApiInterface
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.header_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ReferEarnNowActivity : AppCompatActivity(), View.OnClickListener {

    var mProgress: Progress? = null
    var affiliationAdapter: MyAffiliationAdapter? = null
    private lateinit var profileNameTV: TextView
    lateinit var referralLinkTV: TextView
    lateinit var earnedMoneyTV: TextView
    lateinit var joinedFriendTV: TextView
    var profileName: String? = null
    var referralCode: String? = null
    var affiliateUserId: String? = null
    var earnedMoney: String? = null
    var bankInfoId: String? = null
    var pageNumber = 1
    var perPageTotal = 20
    lateinit var transferMoneyLL: LinearLayout
    lateinit var profileInfoLayout: LinearLayout
    lateinit var myAffiliateLayout: LinearLayout
    lateinit var paymentHistoryLayout: LinearLayout
    var joinedFriends: Int? = null
    lateinit var affiliateRecyclerView: RecyclerView
    lateinit var paymentHistoryRecycler: RecyclerView
    var refProfileData: RefProfileData? = null
    lateinit var myProfileTV: TextView
    lateinit var myAffiliationsTV: TextView
    lateinit var paymentHistoryTV: TextView
    var paymentHistoryList: ArrayList<PaymentHistory>? = null
    var affiliationList: ArrayList<MyAffiliation>? = null
    private var isLoading = false
    private var isLastPage = false
    var previousTotalItemCount = 0
    lateinit var shareLinkLL: LinearLayout
    var referralLink: String? = null
    lateinit var profileImageAffiliate: ImageView
    var paymentHistoryAdapter: paymentHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_earn_now)
        mProgress = Progress(this)
        mProgress!!.setCancelable(false)
        tvHeaderName.text = "Refer and Earn Now"
        ibBack.setOnClickListener(View.OnClickListener { finish() })

        affiliateUserId = intent.getStringExtra(Constants.Extras.AFFILIATE_USER_ID)
        if (affiliateUserId == null){
            affiliateUserId = SharedPreference.getInstance().loggedInUser.affiliate_user_id
        }

        initViews()
        networkCallForProfileInfo()
    }

    fun initViews() {
        mProgress = Progress(this)
        mProgress?.setCancelable(false)
        profileNameTV = findViewById(R.id.profile_name_TV)
        referralLinkTV = findViewById(R.id.referral_link_TV)
        transferMoneyLL = findViewById(R.id.transfer_money_LL)
        transferMoneyLL.setOnClickListener(this)
        earnedMoneyTV = findViewById(R.id.earned_money_TV)
        joinedFriendTV = findViewById(R.id.joined_friend_TV)
        shareLinkLL = findViewById(R.id.shareLinkLL)
        shareLinkLL.setOnClickListener(this)
        affiliateRecyclerView = findViewById(R.id.my_affiliation_recyclerview)
        paymentHistoryRecycler = findViewById(R.id.payment_history_recycler)
        affiliationList = ArrayList()
        setAffiliationAdapter()
        setPaymentHistoryAdapter()
        myAffiliationsTV = findViewById(R.id.my_affiliations_TV)
        paymentHistoryTV = findViewById(R.id.payment_history_TV)
        myProfileTV = findViewById(R.id.my_profile_TV)
        myProfileTV.setOnClickListener(this)
        myAffiliationsTV.setOnClickListener(this)
        paymentHistoryTV.setOnClickListener(this)
        profileInfoLayout = findViewById(R.id.profile_info_layout)
        myAffiliateLayout = findViewById(R.id.my_affiliate_layout)
        paymentHistoryLayout = findViewById(R.id.payment_history_layout)
        profileImageAffiliate = findViewById(R.id.profileImageAffiliate)
    }

    fun setAffiliationAdapter() {
        if (affiliationAdapter == null) {
            affiliationAdapter = MyAffiliationAdapter(affiliationList, this)
            val myAffLayoutManager = LinearLayoutManager(this)
            affiliateRecyclerView.layoutManager = myAffLayoutManager
            affiliateRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = myAffLayoutManager.childCount
                    val totalItemCount = myAffLayoutManager.itemCount
                    val firstVisibleItemPosition = myAffLayoutManager.findFirstVisibleItemPosition()
                    if (!isLoading && !isLastPage) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= perPageTotal) {
                            pageNumber++
                            networkCallForMyAffiliation()
                        }
                    }
                }
            })
            affiliateRecyclerView.adapter = affiliationAdapter
        } else {
            affiliationAdapter!!.setList(affiliationList)
            affiliationAdapter!!.notifyDataSetChanged()
        }
    }

    fun setPaymentHistoryAdapter() {
        if (paymentHistoryAdapter == null) {
            paymentHistoryAdapter = paymentHistoryAdapter(paymentHistoryList, this)
            paymentHistoryRecycler.setHasFixedSize(true)
            val payLinearLayoutManager = LinearLayoutManager(this)
            paymentHistoryRecycler.layoutManager = payLinearLayoutManager
            paymentHistoryRecycler.adapter = paymentHistoryAdapter
            paymentHistoryRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = payLinearLayoutManager.childCount
                    val totalItemCount = payLinearLayoutManager.itemCount
                    val firstVisibleItemPosition = payLinearLayoutManager.findFirstVisibleItemPosition()
                    if (!isLoading && !isLastPage) {
                        isLoading = true
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= perPageTotal) {
                            pageNumber++
                            networkCallForPaymentHistory()
                        }
                    }
                }
            })
        } else {
            paymentHistoryAdapter!!.setList(paymentHistoryList)
            paymentHistoryAdapter!!.notifyDataSetChanged()
        }
    }

    private fun networkCallForProfileInfo() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.getProfileInfo(affiliateUserId)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val profileInfoResponse = Gson().fromJson(jsonObject, ProfileInfoResponse::class.java)
                            refProfileData = profileInfoResponse.getData()
                            if (refProfileData != null) {
                                earnedMoney = refProfileData!!.getReferralMoneyEarned().getReferralMoneyEarned()
                                joinedFriends = refProfileData!!.getFriendsJoined()
                                profileName = refProfileData!!.getProfileInfo().firstName
                                referralCode = refProfileData!!.getProfileInfo().referralCode
                                referralLink =  /*"https://emedicoz.com/" +*/
                                        referralCode
                                referralLinkTV.text = referralLink
                                if (profileName != null) {
                                    profileNameTV.text = profileName
                                }
                                if (earnedMoney != null) earnedMoneyTV.text = String.format("₹%s", earnedMoney) else earnedMoneyTV.text = "₹0"
                                if (joinedFriends != null) joinedFriendTV.text = String.format("%d", joinedFriends) else joinedFriendTV.text = "0"
                                if (!TextUtils.isEmpty(refProfileData!!.getProfileInfo().profileImage)) {
                                    Glide.with(Objects.requireNonNull(this@ReferEarnNowActivity)).load(refProfileData!!.getProfileInfo().profileImage).into(profileImageAffiliate)
                                }
                                SharedPreference.getInstance().affiliateProfileInfo = profileInfoResponse
                            } else {
                                Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_LONG).show()
                                RetrofitResponse.getApiData(this@ReferEarnNowActivity, jsonResponse.optString(Const.AUTH_CODE))
                            }
                        } else {
                            profileInfoLayout.visibility = View.GONE
                            Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@ReferEarnNowActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun networkCallForBankInfo() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.getAffiliateUserBankInfo(affiliateUserId)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val bankInfoResponse = Gson().fromJson(jsonObject, BankInfoResponse::class.java)
                            val bankInfo = bankInfoResponse.getData()
                            bankInfoId = bankInfoResponse.getData()[0].getId()
                            showAlertBankConfirm(bankInfo)
                        } else {
                            Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@ReferEarnNowActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@ReferEarnNowActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showAlertBankConfirm(bankInfo: List<BankInfo>?) {
        // create an alert builder
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.referral_bank_detail_confirm_dialog, null)
        builder.setView(view)
        // add a button
        val accHolderNameTv: TextView
        val bankNameTv: TextView
        val bankBranchTv: TextView
        val accNumberTv: TextView
        val ifscCodeTv: TextView
        accHolderNameTv = view.findViewById(R.id.acc_holder_name_tv)
        bankNameTv = view.findViewById(R.id.bank_name_tv)
        bankBranchTv = view.findViewById(R.id.bank_branch_tv)
        accNumberTv = view.findViewById(R.id.acc_number_tv)
        ifscCodeTv = view.findViewById(R.id.ifsc_code_tv)
        if (bankInfo != null && !bankInfo.isEmpty()) {
            accHolderNameTv.text = bankInfo[0].getAccountHolderName()
            bankNameTv.text = bankInfo[0].getBankName()
            bankBranchTv.text = bankInfo[0].getBankBranchName()
            accNumberTv.text = bankInfo[0].getAccountNumber()
            ifscCodeTv.text = bankInfo[0].getIfscCode()
        }
        builder.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
            // send data from the AlertDialog to the Activity
            networkCallForEnCashRequest()
        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface?, i: Int -> }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun networkCallForPaymentHistory() {
        mProgress!!.show()
        isLoading = true
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.getAffiliationsPaymentHistory(affiliateUserId, pageNumber, perPageTotal)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                isLoading = false
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val affPaymentHistoryResponse = Gson().fromJson(jsonObject, AffPaymentHistoryResponse::class.java)
                            val paymentHistoryData = affPaymentHistoryResponse.data
                            paymentHistoryList!!.addAll(paymentHistoryData.paymentHistory)
                            setPaymentHistoryAdapter()
                            isLastPage = GenericUtils.isListEmpty(paymentHistoryData.paymentHistory)
                        } else {
                            Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@ReferEarnNowActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@ReferEarnNowActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun networkCallForEnCashRequest() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.getAffiliateEncashRequest(affiliateUserId, bankInfoId)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@ReferEarnNowActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@ReferEarnNowActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun networkCallForMyAffiliation() {
        mProgress!!.show()
        isLoading = true
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.myAffiliations(affiliateUserId, pageNumber, perPageTotal)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                isLoading = false
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val myAffiliationResponse = Gson().fromJson(jsonObject, MyAffiliationResponse::class.java)
                            val myAffiliationData = myAffiliationResponse.data
                            affiliationList!!.addAll(myAffiliationResponse.data.myAffiliations)
                            setAffiliationAdapter()
                            isLastPage = GenericUtils.isListEmpty(myAffiliationData.myAffiliations)
                        } else {
                            Toast.makeText(this@ReferEarnNowActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@ReferEarnNowActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@ReferEarnNowActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onClick(p0: View?) {
        when (p0!!.getId()) {
            R.id.transfer_money_LL -> networkCallForBankInfo()
            R.id.my_profile_TV -> {
                profileInfoLayout.visibility = View.VISIBLE
                myAffiliateLayout.visibility = View.GONE
                paymentHistoryLayout.visibility = View.GONE
                myProfileTV!!.setBackgroundResource(R.color.blue)
                myProfileTV!!.setTextColor(ContextCompat.getColor(this, R.color.white))
                myAffiliationsTV.setBackgroundResource(R.color.white)
                paymentHistoryTV.setBackgroundResource(R.color.white)
                networkCallForProfileInfo()
                myAffiliationsTV.setTextColor(ContextCompat.getColor(this, R.color.black))
                paymentHistoryTV.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            R.id.my_affiliations_TV -> {
                myAffiliateLayout.visibility = View.VISIBLE
                profileInfoLayout.visibility = View.GONE
                paymentHistoryLayout.visibility = View.GONE
                pageNumber = 1
                affiliationList = ArrayList()
                networkCallForMyAffiliation()
                myAffiliationsTV.setBackgroundResource(R.color.blue)
                myAffiliationsTV.setTextColor(ContextCompat.getColor(this, R.color.white))
                myProfileTV!!.setBackgroundResource(R.color.white)
                paymentHistoryTV.setBackgroundResource(R.color.white)
                myProfileTV!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                paymentHistoryTV.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            R.id.payment_history_TV -> {
                paymentHistoryLayout.visibility = View.VISIBLE
                profileInfoLayout.visibility = View.GONE
                myAffiliateLayout.visibility = View.GONE
                pageNumber = 1
                paymentHistoryList = ArrayList()
                networkCallForPaymentHistory()
                paymentHistoryTV.setBackgroundResource(R.color.blue)
                paymentHistoryTV.setTextColor(ContextCompat.getColor(this, R.color.white))
                myProfileTV!!.setBackgroundResource(R.color.white)
                myAffiliationsTV.setBackgroundResource(R.color.white)
                myAffiliationsTV.setTextColor(ContextCompat.getColor(this, R.color.black))
                myProfileTV!!.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            R.id.shareLinkLL -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = referralLink!!
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }
            else -> {
            }
        }
    }
}