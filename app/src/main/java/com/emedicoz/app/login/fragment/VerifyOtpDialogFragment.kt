package com.emedicoz.app.login.fragment

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LayoutOtpVerificationBinding
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.login.receiver.SmsBroadcastReceiver
import com.emedicoz.app.modelo.User
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface
import com.emedicoz.app.utilso.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.transparent_auth_toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class VerifyOtpDialogFragment : DialogFragment() {
    private lateinit var binding: LayoutOtpVerificationBinding
    private lateinit var mProgress: Progress
    private lateinit var user: User
    private var type: String = ""
    private var countDownTimer: CountDownTimer? = null
    var count = 0L
    var leftMillisUntilFinished: Long = 0
    var message: String = ""
    private val REQ_USER_CONSENT = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_NoTitleBar_Fullscreen
        )*/
        user = arguments?.getSerializable("user") as User
        type = arguments?.getString(Const.FRAG_TYPE) as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window: Window = dialog?.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        startSmsUserConsent()
        binding.verifyOtpBtn.setOnClickListener {
            if (binding.pinview.value.isNotEmpty()) {
                networkCallForUserLoginAuth(requireActivity(), user, binding.pinview.value)
            } else {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.authToolbar.authBack.visibility = View.GONE
        binding.authToolbar.tvHelp.visibility = View.GONE

        setSpannable()
        setTimer(60000)
    }

    private fun setSpannable() {
        val string: String = requireActivity().resources.getString(R.string.dont_receive_otp)
        val ss = SpannableString(string)
        val span: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                networkCallForUserLoginAuth(requireActivity(), user, "")
            }
        }
        ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.sky_blue)),
            18,
            string.length,
            0
        )
        ss.setSpan(span, 18, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.dontReceiveOtp.text = ss
        binding.dontReceiveOtp.movementMethod = LinkMovementMethod.getInstance();
    }

    private fun networkCallForUserLoginAuth(activity: Activity, userRes: User, otp: String) {
        mProgress = Progress(activity)
        mProgress.setCancelable(false)
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        mProgress.show()
        val apiInterface = ApiClient.createService(
            LoginApiInterface::class.java
        )
        var response: Call<JsonObject> = apiInterface.userLoginAuthenticationV4(
            userRes.email,
            userRes.mobile,
            userRes.c_code,
            otp,
            userRes.is_social,
            userRes.social_type,
            userRes.social_tokken,
            userRes.device_type,
            userRes.device_tokken
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("String Login", jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            if (!GenericUtils.isEmpty(otp)) {
                                val jsonObject1: JSONObject = jsonResponse.optJSONObject("data")!!
                                user = Gson().fromJson(jsonObject1.toString(), User::class.java)
                                userRes.id = user.id
                                Log.e("TAG", "onResponse: ${user.c_code}")
                                SharedPreference.getInstance().loggedInUser = userRes
                                SharedPreference.getInstance().putString(
                                    Const.LOGGED_IN_USER_TOKEN,
                                    user.loggedInUserToken
                                )
                                (((requireActivity() as AuthActivity)
                                    .getCurrentFragment() as ProfileSubmissionFragment)
                                    .getCurrentFragment() as ContactDetailFragment).networkCallForContactRegistration()
                                dismiss()
                            } else {
                                setTimer(60000)
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
                            RetrofitResponse.handleAuthCode(
                                activity, jsonResponse.optString(Const.AUTH_CODE),
                                jsonResponse.getString(Const.AUTH_MESSAGE)
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

    private fun setTimer(time: Long) {
        binding.dontReceiveOtp.visibility = View.GONE
        countDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                count = millisUntilFinished / 1000
                binding.otpSentTo.text = "OTP Sent To"
                binding.otpSentToPhone.text = " ${user.mobile}"
                binding.otpSentToCount.text = " $count seconds"
                leftMillisUntilFinished = millisUntilFinished
                Log.e("onTick: ", leftMillisUntilFinished.toString())
            }

            override fun onFinish() {
                binding.dontReceiveOtp.visibility = View.VISIBLE
                binding.otpSentTo.text = "OTP Sent To"
                binding.otpSentToPhone.text = " ${user.mobile}"
                binding.otpSentToCount.text = ""
            }
        }
        countDownTimer?.start()
    }


    companion object {
        const val TAG = "VerifyOtpDialogFragment"
        fun newInstance(user: User, type: String): VerifyOtpDialogFragment {
            val fragment = VerifyOtpDialogFragment()
            val args = Bundle()
            args.putSerializable("user", user)
            args.putString(Const.FRAG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: ")
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
        context?.unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (leftMillisUntilFinished > 0)
            setTimer(leftMillisUntilFinished)
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireContext())
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener { }.addOnFailureListener { }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent!!, REQ_USER_CONSENT)
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        context?.registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        // This will match any 6 digit number in the message
        val pattern: Pattern = Pattern.compile("(\\d{4})")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            val str = matcher.group(0)
            binding.pinview.value = str
            Log.e(TAG, "getOtpFromMessage: $str")
            Handler().postDelayed({
                if (binding.pinview.value.isNotEmpty()) {
                    networkCallForUserLoginAuth(requireActivity(), user, binding.pinview.value)
                } else {
                    Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
                }
            }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }
}