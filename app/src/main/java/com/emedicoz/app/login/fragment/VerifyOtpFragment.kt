package com.emedicoz.app.login.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.renderscript.ScriptGroup
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.ViewModel.LogoutViewModel
import com.emedicoz.app.ViewModel.VerifyOtpFragmentModel
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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_new_profile.*
import kotlinx.android.synthetic.main.transparent_auth_toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class VerifyOtpFragment : Fragment() {
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
    lateinit var verifyOtpFragmentModel:VerifyOtpFragmentModel
    var otp:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        startSmsUserConsent()
        mProgress = Progress(activity)
        mProgress.setCancelable(false)
         verifyOtpFragmentModel=ViewModelProvider(this).get(VerifyOtpFragmentModel::class.java)
        verifyOtpFragmentModel.onFailureLiveData.observe(viewLifecycleOwner, Observer {
            mProgress.dismiss()
            Helper.showExceptionMsg(activity, it)
        })
        verifyOtpFragmentModel.mutableLiveData.observe(viewLifecycleOwner, Observer {

            mProgress.dismiss()
            if (it != null) {
                val jsonObject = it
                val jsonResponse: JSONObject
                try {
                    jsonResponse = JSONObject(jsonObject.toString())
                    Log.e("String Login", jsonObject.toString())
                    if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                        if (!GenericUtils.isEmpty(otp)) {
                            val jsonObject1: JSONObject = jsonResponse.optJSONObject("data")!!
                            user = Gson().fromJson(jsonObject1.toString(), User::class.java)

                            Log.e("TAG", "onResponse: ${user.c_code}")
                            SharedPreference.getInstance().loggedInUser = user
                            SharedPreference.getInstance().putString(
                                Const.LOGGED_IN_USER_TOKEN,
                                user.getLoggedInUserToken()
                            )
                            if (user.is_course_register == "1") {
                                if (user.expert_following >= 5) {
                                    val intent = Intent(activity, HomeActivity::class.java)
                                    intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    requireActivity().startActivity(intent)
                                    SharedPreference.getInstance()
                                        .putBoolean(Const.IS_USER_REGISTRATION_DONE, true)
                                    SharedPreference.getInstance()
                                        .putBoolean(Const.IS_USER_LOGGED_IN, true)
                                }else{
                                    if (requireActivity() is AuthActivity) {
                                        (requireActivity() as AuthActivity).replaceFragment(
                                            ProfileSubmissionFragment.newInstance(user,Const.FOLLOW)
                                        )
                                    }
                                }
                            } else {
                                if (requireActivity() is AuthActivity) {
                                    (requireActivity() as AuthActivity).replaceFragment(
                                        ProfileSubmissionFragment.newInstance(user,Const.PROFILE)
                                    )
                                }
                            }
                        } else {
                            setTimer(60000)
                        }
                    } else {
                        RetrofitResponse.handleAuthCode(
                            activity, jsonResponse.optString(Const.AUTH_CODE),
                            jsonResponse.getString(Const.AUTH_MESSAGE)
                        )
                    }
                    Toast.makeText(
                        activity,
                        jsonResponse.optString(Constants.Extras.MESSAGE),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })




        binding.verifyOtpBtn.setOnClickListener {
            if (binding.pinview.value.isNotEmpty()) {
              //  networkCallForUserLoginAuth(requireActivity(), user, binding.pinview.value)
                  otp=binding.pinview.value
                mProgress.show()
                verifyOtpFragmentModel.getOtpVerify(user,binding.pinview.value, type)
            } else {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        authBack.setOnClickListener {
            (requireContext() as AuthActivity).onBackPressed()
        }

        tvHelp.setOnClickListener {
            (activity as AuthActivity).initHelpAndSupport()
        }
        setSpannable()
        setTimer(60000)
    }

    private fun setSpannable() {
        val string: String = requireActivity().resources.getString(R.string.dont_receive_otp)
        val ss = SpannableString(string)
        val span: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
              //  networkCallForUserLoginAuth(requireActivity(), user, "")
                otp=""
                mProgress.show()
                verifyOtpFragmentModel.getOtpVerify(user,"",type)

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
        var response: Call<JsonObject>
        if (type == "FROM_MOBILE") {
            if (user.is_social == "1") {
                response = apiInterface.userLoginAuthenticationV3(
                    userRes.email,
                    otp,
                    userRes.is_social,
                    userRes.social_type,
                    userRes.social_tokken,
                    userRes.device_type,
                    userRes.device_tokken
                )
            } else {
                response = apiInterface.userLoginAuthenticationV2(
                    userRes.mobile,
                    userRes.c_code,
                    otp,
                    userRes.is_social,
                    userRes.social_type,
                    userRes.social_tokken,
                    userRes.device_type,
                    userRes.device_tokken
                )
            }
        } else {
            response = apiInterface.userLoginAuthenticationV3(
                userRes.email,
                otp,
                userRes.is_social,
                userRes.social_type,
                userRes.social_tokken,
                userRes.device_type,
                userRes.device_tokken
            )
        }
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

                                Log.e("TAG", "onResponse: ${user.c_code}")
                                SharedPreference.getInstance().loggedInUser = user
                                SharedPreference.getInstance().putString(
                                    Const.LOGGED_IN_USER_TOKEN,
                                    user.getLoggedInUserToken()
                                )
                                if (user.is_course_register == "1") {
                                    if (user.expert_following >= 5) {
                                        val intent = Intent(activity, HomeActivity::class.java)
                                        intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        requireActivity().startActivity(intent)
                                        SharedPreference.getInstance()
                                            .putBoolean(Const.IS_USER_REGISTRATION_DONE, true)
                                        SharedPreference.getInstance()
                                            .putBoolean(Const.IS_USER_LOGGED_IN, true)
                                    }else{
                                        if (requireActivity() is AuthActivity) {
                                            (requireActivity() as AuthActivity).replaceFragment(
                                                ProfileSubmissionFragment.newInstance(user,Const.FOLLOW)
                                            )
                                        }
                                    }
                                } else {
                                    if (requireActivity() is AuthActivity) {
                                        (requireActivity() as AuthActivity).replaceFragment(
                                            ProfileSubmissionFragment.newInstance(user,Const.PROFILE)
                                        )
                                    }
                                }
                            } else {
                                setTimer(60000)
                            }
                        } else {
                            RetrofitResponse.handleAuthCode(
                                activity, jsonResponse.optString(Const.AUTH_CODE),
                                jsonResponse.getString(Const.AUTH_MESSAGE)
                            )
                        }
                        Toast.makeText(
                            activity,
                            jsonResponse.optString(Constants.Extras.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
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
                if (type == "FROM_MOBILE") {
                    if (user.is_social == "1") {
                        binding.otpSentTo.text = "OTP Sent To"
                        binding.otpSentToPhone.text = " ${user.email}"
                        binding.otpSentToCount.text = " $count seconds"
                    } else {
                        binding.otpSentTo.text = "OTP Sent To"
                        binding.otpSentToPhone.text = " ${user.mobile}"
                        binding.otpSentToCount.text = " $count seconds"
                    }
                } else {
                    binding.otpSentTo.text = "OTP Sent To"
                    binding.otpSentToPhone.text = " ${user.email}"
                    binding.otpSentToCount.text = " $count seconds"
                }
/*                if (type == "FROM_MOBILE")
                    setCountSpannable(
                        "OTP Sent To ${user.mobile} $count seconds",
                        "OTP Sent To ${user.mobile}".length,
                        "OTP Sent To ${user.mobile} $count seconds".length
                    )
                else
                    setCountSpannable(
                        "OTP Sent To ${user.email} $count seconds",
                        "OTP Sent To ${user.email}".length,
                        "OTP Sent To ${user.email} $count seconds".length
                    )*/
                //  binding.otpSentTo.text = "Otp Sent To ${user.email} $count seconds"
                leftMillisUntilFinished = millisUntilFinished
                Log.e("onTick: ", leftMillisUntilFinished.toString())
            }

            override fun onFinish() {
                binding.dontReceiveOtp.visibility = View.VISIBLE
                if (type == "FROM_MOBILE") {
                    if (user.is_social == "1") {
                        binding.otpSentTo.text = "OTP Sent To"
                        binding.otpSentToPhone.text = " ${user.email}"
                        binding.otpSentToCount.text = ""
                    } else {
                        binding.otpSentTo.text = "OTP Sent To"
                        binding.otpSentToPhone.text = " ${user.mobile}"
                        binding.otpSentToCount.text = ""
                    }
                } else {
                    binding.otpSentTo.text = "OTP Sent To"
                    binding.otpSentToPhone.text = " ${user.email}"
                    binding.otpSentToCount.text = ""
                }
            }
        }
        countDownTimer?.start()
    }


    companion object {
        private const val TAG = "VerifyOtpFragment"
        fun newInstance(user: User, type: String): VerifyOtpFragment {
            val fragment = VerifyOtpFragment()
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
                 //   networkCallForUserLoginAuth(requireActivity(), user, binding.pinview.value)
                     otp=binding.pinview.value
                    mProgress.show()
                    verifyOtpFragmentModel.getOtpVerify(user,binding.pinview.value,type)
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