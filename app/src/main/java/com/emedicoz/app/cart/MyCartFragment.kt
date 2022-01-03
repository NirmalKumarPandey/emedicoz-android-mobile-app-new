package com.emedicoz.app.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.cart.adapter.MyCartAddressAdapter
import com.emedicoz.app.cart.callback.OnCartAddressClick
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.FragmentMyCartBinding
import com.emedicoz.app.feeds.activity.FeedsActivity
import com.emedicoz.app.installment.model.Installment
import com.emedicoz.app.modelo.MyRewardPoints
import com.emedicoz.app.modelo.courses.Coupon
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.modelo.courses.CoursePayment
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.Helper.getCurrencySymbol
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MyCartFragment : Fragment(), View.OnClickListener, OnCartAddressClick {
    private var finalPriceValue: String = ""
    private lateinit var progress: Progress
    val courseListOfCart: ArrayList<Course> = ArrayList()
    private lateinit var courseListString: String
    private var gstAmount: String = ""
    private var totalPrice: String = ""
    private var courseCount: String = ""
    private var coursePrice: String = ""
    private var selectedCoupon: String = ""
    private var pointsConversionRate: String = ""
    private var gst: String = ""
    private lateinit var jsonCourseList: JSONArray
    private lateinit var rewardPoints: MyRewardPoints
    var str = ""
    var redeemPoints = "0"
    private var isRedeem: Boolean = false
    private var gson = Gson()
    private val df2 = DecimalFormat("#.##")
    lateinit var mAddressListViewModel: AddressListViewModel
    var selectedAddressId: String = ""

    companion object {
        @JvmStatic
        fun newInstance(fragType: String): MyCartFragment {
            val fragment = MyCartFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var cBinding: FragmentMyCartBinding
    val binding get() = cBinding
    private var couponList = ArrayList<Coupon>()
    private lateinit var couponListDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cBinding = FragmentMyCartBinding.inflate(inflater, container, false)
        progress = Progress(activity)
        progress.setCancelable(false)

        val view = binding.root

        binding.addressRV.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        mAddressListViewModel = ViewModelProvider(this).get(AddressListViewModel::class.java)
//        mAddressListViewModel.loadData(
//                requireContext(),
//                null,
//                SharedPreference.getInstance().loggedInUser.id
//        )
        binding.apply {

            couponLayout.setOnClickListener(this@MyCartFragment)
            crossCouponButton.setOnClickListener(this@MyCartFragment)
            btnProceed.setOnClickListener(this@MyCartFragment)
            reddemCoinsBtn.setOnClickListener(this@MyCartFragment)
            txtReferralStatusApply.setOnClickListener(this@MyCartFragment)
            selectCouponTextview.setOnClickListener(this@MyCartFragment)
            llRemoveRedeemedCoin.setOnClickListener(this@MyCartFragment)

            txtReferralStatusApply.setOnClickListener(this@MyCartFragment)
            selectCouponTextview.setOnClickListener(this@MyCartFragment)
            getUserCartList()
            // networkCallForRewardPoint()
        }

        binding.selectAddressTV.setOnClickListener {
            val intent = Intent(requireContext(), AddressList::class.java)
            SharedPreference.getInstance().saveCardConst(Const.CARD, "card")
            startActivity(intent)
            //startActivity(Intent(requireContext(), AddressList::class.java))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("tag", "onResume")
        if (activity is HomeActivity) {
            (activity as HomeActivity).toolbarHeader.visibility = View.GONE
            val actionBar = (activity as HomeActivity).supportActionBar
            actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun getUserCartList() {
        // asynchronous operation to get user cart list.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getUserCartData(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag", "SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()

                networkCallForRewardPoint()

                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            if (GenericUtils.getJsonObject(jsonResponse).getString("course_count")
                                    .equals("0")
                            ) {
                                binding.layoutSinglePayment.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            } else {
                                binding.layoutSinglePayment.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            val jsonResp = GenericUtils.getJsonObject(jsonResponse)

                            courseCount = jsonResp.optString("course_count")
                            coursePrice = jsonResp.optString("course_price")
                            gstAmount = jsonResp.optString("gst_amount")
                            totalPrice = jsonResp.optString("total_amount")
                            finalPriceValue = jsonResp.optString("total_amount")
                            pointsConversionRate = jsonResp.optString("points_conversion_rate")
                            gst = jsonResp.optString("gst")

                            binding.txtTotalCourse.text = courseCount

                            binding.txtPriceValue.text =
                                String.format("%s %s", Helper.getCurrencySymbol(), coursePrice)
                            binding.txtServiceTaxValue.text =
                                String.format("%s %s", Helper.getCurrencySymbol(), gstAmount)
                            binding.txtTotalValue.text =
                                String.format("%s %s", Helper.getCurrencySymbol(), totalPrice)
                            binding.txtGrandTotalValue.text =
                                String.format("%s %s", Helper.getCurrencySymbol(), finalPriceValue)
                            binding.txtServiceTax.text = "GST (" + gst + "%)"
                            prepareCartCourseList(jsonResponse)

                            binding.CouponAppliedLayout.visibility = View.GONE

                            if (courseListOfCart != null) {
                                SharedPreference.getInstance()
                                    .putInt(Const.CART_COUNT, courseListOfCart.size)
                            }

                            if (binding.cartRecyclerView.adapter == null) {
                                val myCartListAdapter = MyCartListAdapter(
                                    courseListOfCart,
                                    context!!,
                                    gst,
                                    object : MyCartInterface {
                                        override fun onClickDelete() {
                                            selectedCoupon = ""
                                            binding.reddemCoinsBtn.text =
                                                activity!!.getString(R.string.redeem)
                                            binding.coinsRedeemTB.visibility = View.GONE
                                            binding.selectCouponTextview.text.clear()
                                            getUserCartList()
                                        }
                                    })
                                binding.cartRecyclerView.apply {
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = myCartListAdapter
                                }
                            } else {
                                ((binding.cartRecyclerView.adapter) as MyCartListAdapter).onNotifyDataSetChange(
                                    courseListOfCart,
                                    context!!
                                )
                            }

                            couponList = loadCouponList(jsonResponse)

                        } else {
                            Helper.showErrorLayoutForNav("getUserCartList", activity, 1, 0)
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                            binding.layoutSinglePayment.visibility = View.GONE
                            binding.noDataFound.visibility = View.VISIBLE
                            SharedPreference.getInstance().putInt(Const.CART_COUNT, 0)
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("getUserCartList", activity, 1, 0)
                    }
                } else {
                    binding.layoutSinglePayment.visibility = View.GONE
                    binding.noDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("getUserCartList", activity, 1, 1)
            }
        })
    }

    private fun prepareCartCourseList(jsonResponse: JSONObject) {

        jsonCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("list")
        courseListString = gson.toJson(jsonCourseList)

        courseListOfCart.clear()

        for (i in 0 until jsonCourseList.length()) {

            val courseObject = jsonCourseList.getJSONObject(i)
            val course =
                gson.fromJson(Objects.requireNonNull(courseObject).toString(), Course::class.java)
            courseListOfCart.add(course)
        }

        if (courseListOfCart.size == 1) {
            binding.selectCouponTextview.hint = getString(R.string.enter_coupon_referral_code)
            binding.selectCouponTextview.isFocusable = true
            binding.selectCouponTextview.isFocusableInTouchMode = true
            binding.appliedCouponLayout.visibility = View.VISIBLE
            binding.txtReferralStatusApply.text = "Apply"
            binding.crossCouponButton.visibility = View.GONE
        } else if (courseListOfCart.size > 1) {
            binding.selectCouponTextview.hint = getString(R.string.select_coupon_code)
            binding.selectCouponTextview.isFocusable = false
            binding.appliedCouponLayout.visibility = View.GONE
            binding.txtReferralStatusApply.text = "Applied"
        }
    }

    private fun processCourseListForPayment(courseList: ArrayList<Course>): String {

        val processedCourseList = ArrayList<CoursePayment>()
        for (i in 0 until courseList.size) {

            val coursePaymantObject = CoursePayment()
            coursePaymantObject.courseId = courseList[i].id
            coursePaymantObject.paymentMeta = courseList[i].subscription_data
            coursePaymantObject.paymentMode = courseList[i].is_subscription

            if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                coursePaymantObject.coursePrice = courseList[i].for_dams
            } else {
                coursePaymantObject.coursePrice = courseList[i].non_dams
            }

            val installmentData =
                gson.fromJson(gson.toJson(courseList[i].subscription_data), Installment::class.java)

            if (courseList[i].is_subscription.equals("1")) {
                coursePaymantObject.expiry = installmentData.amount_description.expiry
                coursePaymantObject.coursePrice = installmentData.amount_description.payment.get(0)
            }

            processedCourseList.add(coursePaymantObject)
        }

        return gson.toJson(processedCourseList).toString()
    }

    private fun retrieveCourseIds(courseList: ArrayList<Course>): String {
        var courseIds: String = ""
        for (i in 0 until courseList.size) {
            courseIds = courseIds + courseList[i].id + ","
        }
        if (courseIds.endsWith(",")) {
            courseIds = courseIds.substring(0, courseIds.length - 1)
        }
        return courseIds
    }

    private fun processCourseListForPaymentStatus(courseList: ArrayList<Course>): String {

        val processedCourseList = ArrayList<CoursePayment>()
        for (i in 0 until courseList.size) {

            val coursePaymantObject = CoursePayment()
            coursePaymantObject.courseId = courseList[i].id
            coursePaymantObject.discount = courseList[i].discount

            val installmentData =
                gson.fromJson(gson.toJson(courseList[i].subscription_data), Installment::class.java)
            if (installmentData.amount_description != null) {
                coursePaymantObject.subscriptionCode =
                    installmentData.amount_description.subscription_code
            } else {
                coursePaymantObject.subscriptionCode = ""
            }

            processedCourseList.add(coursePaymantObject)
        }

        return gson.toJson(processedCourseList).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        cBinding = null
    }

    override fun onClick(view: View?) {

        when (view!!.id) {

            R.id.coupon_layout -> {
                if (courseListOfCart.size > 1) {
                    showCouponListDialog()
                }
            }

            R.id.btn_continue -> {
                if (!GenericUtils.isEmpty(selectedAddressId)) {
                    val paymentDialog = view.getTag(R.id.questions) as Dialog
                    val viewTag = view.getTag(R.id.optionsAns) as View
                    val radioGroup = viewTag.findViewById<RadioGroup>(R.id.radioGroupPay)

                    if (radioGroup != null && radioGroup.checkedRadioButtonId != -1) {
                        /*val rb = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                    if (rb.text == activity!!.getString(R.string.paytm)) {
                        paymentModeCheck = Const.PAYTM
                    } else if (rb.text == activity!!.getString(R.string.in_app_purchase)) {
                        paymentModeCheck = Const.ANIN
                    }*/
                        paymentDialog.dismiss()
                        initializeTransactionCart()
                    } else {
                        Toast.makeText(
                            activity,
                            getString(R.string.paymentModeSelcetion),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    requireActivity().show("Please Select Address", Toast.LENGTH_SHORT)
                }
            }

            R.id.btn_proceed -> {
                //Toast.makeText(context, "pay now", Toast.LENGTH_SHORT).show()
                if (finalPriceValue.toDouble() < 1) {
                    /* Toast.makeText(
                             activity,
                             "Not a valid amount we cannot proceed",
                             Toast.LENGTH_SHORT
                     ).show()*/
                    initializeTransactionCart()
                    //networkCallForFreeCourseTransaction()
                } else {
                    if (Helper.getCurrencySymbol() == eMedicozApp.getAppContext().resources.getString(
                            R.string.rs
                        )
                    ) {
                        showPaymentModePopup()
                    } else showConversion()
                }
            }

            R.id.cross_coupon_button -> {
                selectedCoupon = ""
                gstAmount = ""
                totalPrice = ""
                binding.appliedCouponLayout.visibility = View.GONE
                binding.selectCouponTextview.text.clear()
                binding.reddemCoinsBtn.isEnabled = true
                getUserCartList()
            }

            R.id.reddemCoinsBtn -> {
                coinRedemption()
            }

            R.id.txtReferralStatusApply -> {

                if (binding.selectCouponTextview.text.isEmpty()) {
                    Toast.makeText(
                        activity,
                        getString(R.string.enter_coupon_code_error),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    selectedCoupon = binding.selectCouponTextview.text.toString()
                    callCouponSelectedApi(selectedCoupon)
                }
            }

            R.id.selectCouponTextview -> {

                if (courseListOfCart.size > 1) {
                    showCouponListDialog()
                }
            }

            R.id.llRemoveRedeemedCoin -> {

                binding.coinsRedeemTB.visibility = View.GONE
                binding.reddemCoinsBtn.text = activity!!.getString(R.string.redeem)
                setRedeemText(rewardPoints.reward_points)
                binding.reddemCoinsBtn.setOnClickListener(this)
                binding.couponLayout.isEnabled = true
                binding.selectCouponTextview.isEnabled = true
                binding.txtReferralStatusApply.isEnabled = true

                binding.txtTotalCourse.text = courseCount
                binding.txtPriceValue.text =
                    String.format("%s %s", Helper.getCurrencySymbol(), coursePrice)
                binding.txtServiceTaxValue.text =
                    String.format("%s %s", Helper.getCurrencySymbol(), gstAmount)
                binding.txtTotalValue.text =
                    String.format("%s %s", Helper.getCurrencySymbol(), totalPrice)
                binding.txtGrandTotalValue.text =
                    String.format("%s %s", Helper.getCurrencySymbol(), totalPrice)

                finalPriceValue = totalPrice
            }
        }
    }

    //region Transaction API's handling

    private fun showConversion() {
        val view = Helper.newCustomDialog(
            activity,
            true,
            "Alert",
            activity!!.getString(R.string.amount_wil_be_show_deducted_in_inr)
        )
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val btnSubmit = view.findViewById<Button>(R.id.btn_submit)
        btnSubmit.text = activity!!.getString(R.string.continueText)
        val params = LinearLayout.LayoutParams(
            activity!!.resources.getDimension(R.dimen.dp120).toInt(),
            activity!!.resources.getDimension(R.dimen.dp35).toInt()
        )
        params.setMargins(
            0,
            activity!!.resources.getDimension(R.dimen.dp20).toInt(),
            0,
            activity!!.resources.getDimension(R.dimen.dp20).toInt()
        )
        btnCancel.layoutParams = params
        btnSubmit.layoutParams = params
        btnCancel.setOnClickListener { Helper.dismissDialog() }
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
            showPaymentModePopup()
        }
    }

    private fun showPaymentModePopup() {
        val li = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val promptsView = li.inflate(R.layout.dialog_payment, null, false)
        val paymentDialog = Dialog(activity!!)
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        paymentDialog.setCanceledOnTouchOutside(true)
        paymentDialog.setContentView(promptsView)
        paymentDialog.show()
        val radioBtnInapp: AppCompatRadioButton
        radioBtnInapp = promptsView.findViewById(R.id.radio_btn_inApp)
        val btnContinue = promptsView.findViewById<Button>(R.id.btn_continue)
        val titleDialog = promptsView.findViewById<TextView>(R.id.titleDialog)

        titleDialog.text = context!!.getString(R.string.choose_payment_, finalPriceValue) //

        if (!TextUtils.isEmpty(SharedPreference.getInstance().masterHitResponse.android_inapp)) {
            if (SharedPreference.getInstance().masterHitResponse.android_inapp == "1") {
                radioBtnInapp.visibility = View.VISIBLE
            } else {
                radioBtnInapp.visibility = View.GONE
            }
        } else {
            radioBtnInapp.visibility = View.GONE
        }
        btnContinue.setTag(R.id.questions, paymentDialog)
        btnContinue.setTag(R.id.optionsAns, promptsView)
        btnContinue.tag = paymentDialog
        btnContinue.setOnClickListener(this)
    }

    private fun initializeTransactionCart() {

        // asynchronous operation to initialize payment for cart.
        progress.show()

        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        var response: Call<JsonObject>? = null
        if (totalPrice != "0") {
            response = apiInterface.initializeTransactionCart(
                processCourseListForPayment(courseListOfCart),
                SharedPreference.getInstance().loggedInUser.id,
                "PAY_TM",
                "",
                "",
                "",
                setTax(),
                totalPrice,
                "",
                selectedCoupon,
                selectedAddressId
            )
        } else {
            response = apiInterface.initializeTransactionCart(
                processCourseListForPayment(courseListOfCart),
                SharedPreference.getInstance().loggedInUser.id,
                "",
                "",
                "",
                "",
                setTax(),
                totalPrice,
                "",
                selectedCoupon,
                selectedAddressId
            )
        }
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag", "SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                val jsonObject = response.body()
                val jsonResponse: JSONObject
                try {
                    jsonResponse = JSONObject(jsonObject.toString())
                    if (jsonResponse.optBoolean(Const.STATUS)) {
                        val data = jsonResponse.optJSONObject("data")
                        SharedPreference.getInstance().putString(
                            Const.COURSE_INIT_PAYMENT_TOKEN,
                            Objects.requireNonNull(data).optString("pre_transaction_id")
                        )
                        if (totalPrice != "0") {
                            networkCallForCheckSumForPaytm()
                        } else {
                            completeCoursePayment()
                        }
                    } else {
                        Helper.showErrorLayoutForNav("initializeTransactionCart", activity, 1, 0)
                        RetrofitResponse.getApiData(
                            activity,
                            jsonResponse.optString(Const.AUTH_CODE)
                        )

                    }
                } catch (e: JSONException) {
                    Helper.showErrorLayoutForNav("initializeTransactionCart", activity, 1, 0)
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("initializeTransactionCart", activity, 1, 1)
            }
        })
    }

    private fun networkCallForCheckSumForPaytm() {
        progress.show()
        finalPriceValue = df2.format(finalPriceValue.toDouble())
        var response: Call<JsonObject>? = null
        val apiInterface = ApiClient.createService2(ApiInterface::class.java)
        response = if (Const.SERVER_TYPE == "LIVE") {
            apiInterface.getCheckSumForPaytmLive(
                String.format(
                    API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE
                ),
                Const.PAYTM_MID,
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                Const.INDUSTRYTYPE_ID,
                SharedPreference.getInstance().loggedInUser.user_registration_info.id,
                Const.CHANNELID,
                finalPriceValue,
                Const.PAYTM_WEBSITE,
                Const.CALLBACKURL + SharedPreference.getInstance()
                    .getString(Const.COURSE_INIT_PAYMENT_TOKEN)
            )
        } else {
            apiInterface.getCheckSumForPaytm(
                String.format(
                    API.API_GET_CHECKSUM_FOR_PAYTM,
                    Const.SERVER_TYPE
                ),
                Const.PAYTM_MID,
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                Const.INDUSTRYTYPE_ID,
                SharedPreference.getInstance().loggedInUser.user_registration_info.id,
                Const.CHANNELID,
                finalPriceValue,
                Const.PAYTM_WEBSITE,
                Const.CALLBACKURL + SharedPreference.getInstance()
                    .getString(Const.COURSE_INIT_PAYMENT_TOKEN)
            )
        }
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    lateinit var jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    progress.dismiss()
                    if (Objects.requireNonNull(jsonResponse).has("CHECKSUMHASH")) {
                        SharedPreference.getInstance()
                            .putString(Const.CHECK_SUM, jsonResponse.optString("CHECKSUMHASH"))
                        makePaytmTransaction()
                        //region Dummy transaction testing
//                        SharedPreference.getInstance().putString(Const.COURSE_FINAL_PAYMENT_TOKEN,
//                        "DUMMY_FINAL_TXN_ID_" + SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN))
//                        networkCallForFinalTransForPaytm()
//                        completeCoursePayment()
                        //endregion
                    } else {
                        RetrofitResponse.getApiData(
                            activity,
                            jsonResponse.optString(Const.AUTH_CODE)
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_GET_CHECKSUM_FOR_PAYTM, activity, 0, 0)
            }
        })
    }

    private fun makePaytmTransaction() {
        val service: PaytmPGService = if (Const.SERVER_TYPE == "LIVE") {
            PaytmPGService.getProductionService()
        } else {
            PaytmPGService.getStagingService("")
        }

        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.
        val paramMap: HashMap<String, String> = HashMap()
        paramMap[Const.MID] = Const.PAYTM_MID
        paramMap[Const.ORDER_ID] =
            SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN)
        paramMap[Const.CUST_ID] =
            SharedPreference.getInstance().loggedInUser.user_registration_info.id
        paramMap[Const.INDUSTRY_TYPE_ID] = Const.INDUSTRYTYPE_ID
        paramMap[Const.CHANNEL_ID] = Const.CHANNELID
        paramMap[Const.TXN_AMOUNT] = finalPriceValue
        paramMap[Const.WEBSITE] = Const.PAYTM_WEBSITE
        paramMap[Const.CALLBACK_URL] = Const.CALLBACKURL + SharedPreference.getInstance()
            .getString(Const.COURSE_INIT_PAYMENT_TOKEN)
//        paramMap.put(Const.MOBILE_NO, SharedPreference.getInstance().getLoggedInUser().getMobile());
//        paramMap.put(Const.EMAIL_PAYTM, SharedPreference.getInstance().getLoggedInUser().getEmail());
        paramMap[Const.CHECKSUMHASH] = SharedPreference.getInstance().getString(Const.CHECK_SUM)
        val Order = PaytmOrder(paramMap)
        service.initialize(Order, null)
        service.startPaymentTransaction(activity, true, true,
            object : PaytmPaymentTransactionCallback {
                override fun someUIErrorOccurred(inErrorMessage: String) {
                    // Some UI Error Occurred in Payment Gateway Activity.
                    // // This may be due to initialization of views in
                    // Payment Gateway Activity or may be due to //
                    // initialization of webview. // Error Message details
                    // the error occurred.
                    Log.e("SOMEUI ERROR", inErrorMessage)
                }

                override fun onTransactionResponse(inResponse: Bundle?) {
                    if (inResponse == null) {
                        return
                    }
                    Log.d("LOG", "Payment Transaction : $inResponse")
                    val s = inResponse.getString("STATUS")
                    when (s) {
                        "TXN_FAILURE" -> Toast.makeText(
                            activity,
                            inResponse.getString("RESPMSG"),
                            Toast.LENGTH_SHORT
                        ).show()
                        "TXN_SUCCESS" -> {
                            SharedPreference.getInstance().putString(
                                Const.COURSE_FINAL_PAYMENT_TOKEN,
                                inResponse.getString("TXNID")
                            )
                            networkCallForFinalTransForPaytm()
                            completeCoursePayment()
                        }
                    }
                }

                override fun networkNotAvailable() {
                    // If network is not
                    // available, then this
                    // method gets called.
                }

                override fun onErrorProceed(p0: String?) {
                    TODO("Not yet implemented")
                }

                override fun clientAuthenticationFailed(inErrorMessage: String) {
                    // This method gets called if client authentication
                    // failed. // Failure may be due to following reasons //
                    // 1. Server error or downtime. // 2. Server unable to
                    // generate checksum or checksum response is not in
                    // proper format. // 3. Server failed to authenticate
                    // that client. That is value of payt_STATUS is 2. //
                    // Error Message describes the reason for failure.
                    Log.e("clientAuthentication", inErrorMessage)
                }

                override fun onErrorLoadingWebPage(
                    iniErrorCode: Int,
                    inErrorMessage: String, inFailingUrl: String
                ) {
                    Log.e("onErrorLoadingWebPage", inErrorMessage + "" + iniErrorCode)
                }

                // had to be added: NOTE
                override fun onBackPressedCancelTransaction() {
                }

                override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
                    Log.d("LOG", "Payment Transaction Failed $inErrorMessage")
                    Toast.makeText(activity, "Payment Transaction Failed ", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    private fun networkCallForFinalTransForPaytm() {
        /* progress.show()
         val apiInterface = ApiClient.createService2(CourseInvoiceApiInterface::class.java)
         val response = apiInterface.finalTransactionForPaytm(String.format("Paytm/TxnStatus.php?MID=%s&ORDERID=%s&SERVER=%s", Const.PAYTM_MID,
                 SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                 Const.SERVER_TYPE))
         response.enqueue(object : Callback<JsonObject?> {
             override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                 progress.dismiss()
                 if (response.body() != null) {
                     val jsonObject = response.body()
                 }
             }

             override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                 progress.dismiss()
             }
         })*/
    }

    private fun completeCoursePayment() {
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        var response: Call<JsonObject>? = null
        if (totalPrice != "0") {
            response = apiInterface.completeCoursePaymentCart(
                SharedPreference.getInstance().loggedInUser.id,
                processCourseListForPaymentStatus(courseListOfCart),
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN),
                selectedCoupon
            )
        } else {
            val currentTimeStamp: Long = System.currentTimeMillis()
            response = apiInterface.completeCoursePaymentCart(
                SharedPreference.getInstance().loggedInUser.id,
                processCourseListForPaymentStatus(courseListOfCart),
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                currentTimeStamp.toString(),
                selectedCoupon
            )
        }

        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                val jsonObject = response.body()
                val jsonResponse: JSONObject?
                try {
                    progress.dismiss()
                    jsonResponse = JSONObject(jsonObject.toString())

                    Toast.makeText(
                        activity,
                        jsonResponse.optString(Constants.Extras.MESSAGE),
                        Toast.LENGTH_SHORT
                    ).show()
                    if (jsonResponse.optString("status") == Const.TRUE) {


                        apiUpdateTransactionInfo()

                    } else {
                        RetrofitResponse.getApiData(
                            activity,
                            jsonResponse.optString(Const.AUTH_CODE)
                        )
                    }
                } catch (e: JSONException) {
                    progress.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_COMPLETE_COURSE_PAYMENT, activity, 1, 1)
            }
        })
    }

    private fun apiUpdateTransactionInfo() {
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        var response: Call<JsonObject>? = null
        if (finalPriceValue != "0") {
            response = apiInterface.updateTransactionInfo(
                SharedPreference.getInstance().loggedInUser.id,
                ApiClient.getStreamId(SharedPreference.getInstance().loggedInUser),
                processCourseListForPaymentStatus(courseListOfCart),
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                SharedPreference.getInstance().getString(Const.COURSE_FINAL_PAYMENT_TOKEN),
                finalPriceValue
            )
        } else {
            val currentTimeStamp = System.currentTimeMillis()
            response = apiInterface.updateTransactionInfo(
                SharedPreference.getInstance().loggedInUser.id,
                ApiClient.getStreamId(SharedPreference.getInstance().loggedInUser),
                processCourseListForPaymentStatus(courseListOfCart),
                SharedPreference.getInstance().getString(Const.COURSE_INIT_PAYMENT_TOKEN),
                currentTimeStamp.toString(),
                finalPriceValue
            )
        }

        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                val jsonObject = response.body()
                val jsonResponse: JSONObject?
                try {
                    progress.dismiss()
                    jsonResponse = JSONObject(jsonObject.toString())

                    if (jsonResponse.optString("status") == Const.TRUE) {

                        SharedPreference.getInstance().putInt(Const.CART_COUNT, 0)
//                        val intent = Intent(activity, CourseActivity::class.java)
//                        intent.putExtra(Const.FRAG_TYPE, Const.MYCOURSES)
//                        SharedPreference.getInstance().putBoolean(Const.IS_PAYMENT_DONE, true)
                        val intent = Intent(activity, HomeActivity::class.java)
                        intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                        activity!!.startActivity(intent)
                        activity!!.finish()

                    } else {
                        RetrofitResponse.getApiData(
                            activity,
                            jsonResponse.optString(Const.AUTH_CODE)
                        )
                    }


                } catch (e: JSONException) {
                    progress.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
            }
        })
    }
    //endregion

    private fun showCouponListDialog() {

        if (couponList == null || couponList.size == 0) {
            Toast.makeText(context, R.string.no_coupons_available, Toast.LENGTH_LONG).show()
        } else {

            couponListDialog = context?.let { Dialog(it) }!!
            couponListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            couponListDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            couponListDialog.setContentView(R.layout.dialog_coupon_list_layout)

            val couponListDialog =
                couponListDialog.findViewById<RecyclerView>(R.id.recyclerview_dialog_coupon_list)
            val couponListAdapter =
                CouponListAdapter(couponList, context!!, object : OnCouponClickListener {
                    override fun onCouponClick(position: Int) {

                        binding.selectCouponTextview.setText(couponList.get(position).coupon_tilte)
                        selectedCoupon = couponList.get(position).coupon_tilte
                        callCouponSelectedApi(selectedCoupon)
                    }

                })
            couponListDialog.apply {
                adapter = couponListAdapter
                layoutManager = LinearLayoutManager(context)
            }

            this.couponListDialog.show()
        }

    }

    private fun loadCouponList(jsonResponse: JSONObject): ArrayList<Coupon> {

        val jsonCouponList: JSONArray?

        val couponList: ArrayList<Coupon> = ArrayList()
        jsonCouponList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("coupons_list")
        for (i in 0 until jsonCouponList.length()) {

            val couponObject = jsonCouponList.getJSONObject(i)
            val coupon =
                gson.fromJson(Objects.requireNonNull(couponObject).toString(), Coupon::class.java)
            couponList.add(coupon)
        }
        return couponList
    }

    private fun callCouponSelectedApi(couponId: String) {

        // asynchronous operation to apply coupon on cart.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response =
            apiInterface.applyCartCoupon(SharedPreference.getInstance().loggedInUser.id, couponId)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag", "SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())

                        Toast.makeText(
                            context,
                            jsonResponse.optString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            if (jsonResponse.optString("message")
                                    .equals("Invalid Coupon Code", true)
                            ) {
                                binding.selectCouponTextview.text.clear()
                                selectedCoupon = ""
//                                Toast.makeText(context, R.string.invalid_coupon_code, Toast.LENGTH_LONG).show()
                                getUserCartList()
                                if (::couponListDialog.isInitialized) {
                                    couponListDialog.dismiss()
                                }
                                return
                            }

                            if (::couponListDialog.isInitialized) {
                                couponListDialog.dismiss()
                            }
                            val jsonResp = GenericUtils.getJsonObject(jsonResponse)
                            binding.txtTotalCourse.text = jsonResp.optString("course_count")
                            binding.txtPriceValue.text = String.format(
                                "%s %s",
                                Helper.getCurrencySymbol(),
                                jsonResp.optString("course_price")
                            )
                            binding.txtServiceTaxValue.text = String.format(
                                "%s %s",
                                Helper.getCurrencySymbol(),
                                jsonResp.optString("gst_amount")
                            )
                            binding.txtTotalValue.text = String.format(
                                "%s %s",
                                Helper.getCurrencySymbol(),
                                jsonResp.optString("total_amount")
                            )
                            binding.txtGrandTotalValue.text = String.format(
                                "%s %s",
                                Helper.getCurrencySymbol(),
                                jsonResp.optString("total_amount")
                            )
                            binding.couponDiscountValue.text = "- " + String.format(
                                "%s %s",
                                Helper.getCurrencySymbol(),
                                jsonResp.optString("discount_amount")
                            )
                            binding.appliedCouponLayout.visibility = View.VISIBLE
                            binding.CouponAppliedLayout.visibility = View.VISIBLE
                            binding.crossCouponButton.visibility = View.VISIBLE
                            binding.txtReferralStatusApply.text = "Applied"

                            gstAmount = jsonResp.optString("gst_amount")
                            totalPrice = jsonResp.optString("total_amount")
                            finalPriceValue = totalPrice
                            if (finalPriceValue == "0") {
                                binding.btnProceed.text = "Add to my course"
                            } else {
                                binding.btnProceed.text = "Pay now"
                            }

                            binding.reddemCoinsBtn.isEnabled = false

                        } else {
                            selectedCoupon = ""
                            binding.CouponAppliedLayout.visibility = View.GONE
                            Helper.showErrorLayoutForNav("callCouponSelectedApi", activity, 1, 0)
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )

                        }
                    } catch (e: JSONException) {
                        binding.CouponAppliedLayout.visibility = View.GONE
                        Helper.showErrorLayoutForNav("callCouponSelectedApi", activity, 1, 0)
                        selectedCoupon = ""
                    }
                } else {
                    couponListDialog.dismiss()
                    binding.selectCouponTextview.text.clear()
                    selectedCoupon = ""
                    binding.CouponAppliedLayout.visibility = View.GONE
                    Toast.makeText(context, R.string.invalid_coupon_code, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("callCouponSelectedApi", activity, 1, 1)
                selectedCoupon = ""
                binding.CouponAppliedLayout.visibility = View.GONE
            }
        })
    }

    private fun setTax(): String {

        return if (!GenericUtils.isEmpty(gst)) {
            gst
        } else {
            "18"
        }
    }

    private fun networkCallForRewardPoint() {
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getRewardPoints(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    lateinit var jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    progress.dismiss()
                    if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                        Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 0, 0)
                        rewardPoints = gson.fromJson(
                            GenericUtils.getJsonObject(jsonResponse).toString(),
                            MyRewardPoints::class.java
                        )
                        setRedeemText(rewardPoints.reward_points)

                        if (rewardPoints.reward_points == "0") {
                            binding.redeemCoinsLayout.visibility = View.GONE
                        } else {
                            binding.redeemCoinsLayout.visibility = View.VISIBLE
                        }

                    } else if (!jsonResponse.optBoolean(Const.STATUS)) {
                        setRedeemText("0")
                        RetrofitResponse.getApiData(
                            activity,
                            jsonResponse.optString(Const.AUTH_CODE)
                        )
                    } else {
                        // errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_REWARD_POINTS)
                        RetrofitResponse.getApiData(
                            activity,
                            jsonResponse.optString(Const.AUTH_CODE)
                        )
                    }
                } else {
                    progress.dismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 1, 1)
            }
        })
    }

    private fun setRedeemText(rewardPoint: String) {
        binding.coinsTextTV.text = String.format(
            "You have %s points to redeem. \n ( %s points = %s 1).",
            rewardPoint,
            getConversionRate(),
            getCurrencySymbol()
        )

    }

    private fun coinRedemption() {
        if (finalPriceValue != "0") {
            str = calculateRedeemCoinsValue()
            if (str.split(",").toTypedArray().get(1) != "0") {
                if (str.contains(",")) {
                    isRedeem = true
                    setRedeemText(
                        (rewardPoints.reward_points.toInt() - str.split(",").toTypedArray().get(0)
                            .toInt()).toString()
                    )
                    binding.coinsRedeemTB.visibility = View.VISIBLE
                    binding.coinRedeemValue.text = String.format(
                        "%s %s %s",
                        "-",
                        getCurrencySymbol(),
                        Helper.calculatePriceBasedOnCurrency(str.split(",").toTypedArray().get(1))
                    )
                    finalPriceValue = (df2.format(
                        finalPriceValue.toDouble() - str.split(",").toTypedArray().get(1).toInt()
                    ).toString())
                    binding.txtGrandTotalValue.text = String.format(
                        "%s %s",
                        getCurrencySymbol(),
                        if (finalPriceValue.toDouble() < 0) "0" else Helper.calculatePriceBasedOnCurrency(
                            finalPriceValue
                        )
                    )
                    redeemPoints = str.split(",").toTypedArray().get(0)
                    binding.reddemCoinsBtn.text = activity!!.getString(R.string.redeemed)
                    binding.reddemCoinsBtn.setOnClickListener(null)
                    binding.couponLayout.isEnabled = false
                    binding.selectCouponTextview.isEnabled = false
                    binding.txtReferralStatusApply.isEnabled = false
                }
            } else {
                binding.coinsRedeemTB.visibility = View.GONE
                Helper.newCustomDialog(
                    activity,
                    "Alert",
                    activity!!.getString(R.string.you_do_not_have_enough_point_to_redeem),
                    true,
                    activity!!.getString(R.string.ok),
                    ContextCompat.getDrawable(activity!!, R.drawable.bg_round_corner_fill_green)
                )
            }
        } else {
            Toast.makeText(activity, "Course have already at the lower Price", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun calculateRedeemCoinsValue(): String {
        var coinsValue = 0
        var convertionRate = 100
        var redeempoints: Int
        var finalprice: Int

        if (GenericUtils.isListEmpty(courseListOfCart)) return ""

        if (!GenericUtils.isEmpty(courseListOfCart[0].points_conversion_rate))
            convertionRate = courseListOfCart[0].points_conversion_rate.toInt()
        if (rewardPoints != null && !GenericUtils.isEmpty(rewardPoints.reward_points))
            coinsValue = rewardPoints.reward_points.toInt()
        try {
            if (coinsValue / convertionRate > finalPriceValue.toInt()) {
                finalprice = finalPriceValue.toInt()
                redeempoints = finalPriceValue.toInt() * convertionRate
            } else {
                finalprice = coinsValue / convertionRate
                redeempoints = coinsValue - coinsValue % convertionRate
            }
        } catch (e: NumberFormatException) {
            finalprice = coinsValue / convertionRate
            redeempoints = coinsValue - coinsValue % convertionRate
        }
        return "$redeempoints,$finalprice"
    }

    private fun getConversionRate(): String {

        if (pointsConversionRate != null && pointsConversionRate != "") {
            return pointsConversionRate
        } else {
            return "100"
        }
    }

    override fun onCartAddressClicked(address: AddressBook, position: Int) {
        selectedAddressId = address.id
    }

    override fun onStart() {
        super.onStart()
        Log.e("Tad", "onStart")
        getAddress()

    }

    fun getAddress() {
        mAddressListViewModel.loadData(
            requireContext(),
            null,
            SharedPreference.getInstance().loggedInUser.id
        )
        mAddressListViewModel.addressList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.e("Tad", it.size.toString())
            if (!GenericUtils.isListEmpty(it)) {
                Log.e("Tad", "onStart1")
                binding.addressRV.visibility = View.VISIBLE
                binding.noAddressTV.visibility = View.GONE
                for (address in it) {
                    if (address.is_default == "1") {
                        address.selectedAddress = "1"
                        selectedAddressId = address.id
                        break
                    } else {
                        address.selectedAddress = "0"
                        selectedAddressId = ""
                    }
                }
                val myCartAddressAdapter = MyCartAddressAdapter(requireActivity(), it, this)
                binding.addressRV.adapter = myCartAddressAdapter
            } else {
                Log.e("Tad", "onStart2")
                binding.addressRV.visibility = View.GONE
                binding.noAddressTV.visibility = View.VISIBLE
            }
        })

    }

    private fun networkCallForFreeCourseTransaction() {

        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.makeFreeCourseTransaction(
            SharedPreference.getInstance().loggedInUser.id,
            getConversionRate(), "0", "", "", retrieveCourseIds(courseListOfCart), "0"
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Toast.makeText(
                                context,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                "networkCallForFreeCourseTransaction",
                                context as Activity?,
                                1,
                                0
                            )
                            RetrofitResponse.getApiData(
                                context,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav(
                            "networkCallForFreeCourseTransaction",
                            activity,
                            1,
                            0
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav(
                    "networkCallForFreeCourseTransaction",
                    activity,
                    1,
                    1
                )
            }
        })
    }
}