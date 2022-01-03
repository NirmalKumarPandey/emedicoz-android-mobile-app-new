package com.emedicoz.app.login.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.ViewModel.LoginByMobileFragmentViewModel
import com.emedicoz.app.ViewModel.VerifyOtpFragmentModel
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LayoutMobileOtpRegisterationBinding
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.modelo.User
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface
import com.emedicoz.app.utilso.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rilixtech.Country
import com.rilixtech.CountryCodePicker.OnCountryChangeListener
import kotlinx.android.synthetic.main.single_row_feeds.*
import kotlinx.android.synthetic.main.transparent_auth_toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginByMobileFragment : Fragment() {

    private lateinit var binding: LayoutMobileOtpRegisterationBinding
    private lateinit var mProgress: Progress
    private var mobile: String = ""
    private var countryCode: String = ""
    private var activity: Activity? = null
    private lateinit var user: User
    private var socialType:String = ""
    lateinit var loginByMobileFragmentViewModel: LoginByMobileFragmentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = LayoutMobileOtpRegisterationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = User()
        // Some changes.............
        mProgress = Progress(activity)
        mProgress.setCancelable(false)
        loginByMobileFragmentViewModel= ViewModelProvider(this).get(LoginByMobileFragmentViewModel::class.java)
        loginByMobileFragmentViewModel.onFailureLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mProgress.dismiss()
            Helper.showExceptionMsg(activity, it)
        })
        loginByMobileFragmentViewModel.mutableLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mProgress.dismiss()
            if (it != null) {
                val jsonObject = it
                val jsonResponse: JSONObject
                try {
                    jsonResponse = JSONObject(jsonObject.toString())
                    Log.e("String Login", jsonObject.toString())
                    System.out.print("hello"+it.size())
                    Log.d("nir", "Mobile............" + jsonResponse.toString())
                    if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                        if (requireActivity() is AuthActivity) {
                            (requireActivity() as AuthActivity).replaceFragment(
                                VerifyOtpFragment.newInstance(user, "FROM_MOBILE")
                            )
                        }
                    } else {
                        RetrofitResponse.handleAuthCode(
                            activity, jsonResponse.optString(Const.AUTH_CODE),
                            jsonResponse.getString(Const.AUTH_MESSAGE)
                        )
                        /* SharedPreference.getInstance()
                             .putBoolean(Const.IS_NOTIFICATION_BLOCKED, false)*/
                    }
                    activity?.show(
                        jsonResponse.optString(Constants.Extras.MESSAGE),
                        Toast.LENGTH_SHORT
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })





// changes....................................................................................









        binding.apply {
            registerUsingEmail.visibility = View.VISIBLE
            registerUsingMobile.visibility = View.GONE

            loginByEmail.visibility = View.GONE
            loginByMobile.visibility = View.VISIBLE
            btnGetOtp.setOnClickListener {
                var isDataValid = true
                var deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)
                if (deviceId == null || deviceId == "") {
                    deviceId = FirebaseInstanceId.getInstance().token
                }
                mobile = binding.mobileET.text.trim().toString()
                countryCode = ccp.selectedCountryCodeWithPlus
                ccp.isValid
                user.mobile = mobile
                user.c_code = countryCode
                user.is_social = "0"
                user.social_tokken = ""
                user.social_type = ""
                user.device_type = Const.DEVICE_TYPE_ANDROID
                user.device_tokken = deviceId
                if (TextUtils.isEmpty(mobile))
                    isDataValid = Helper.DataNotValid(mobileET)
                else if (ccp.selectedCountryCode == "91") {
                    if (mobile.length < 10 || mobile.length > 10) {
                        isDataValid = false
                        activity?.show(
                            "Mobile Number should be of 10 digits",
                            Toast.LENGTH_SHORT
                        )
                    }
                }
                if (isDataValid)
                  //  networkCallForUserLoginAuth(requireActivity(), user)
                    mProgress.show()
                loginByMobileFragmentViewModel.getLoginByMobile(user)

            }
            registerUsingEmail.setOnClickListener {
                if (requireActivity() is AuthActivity) {
                    (requireActivity() as AuthActivity).replaceFragment(LoginByEmailFragment.newInstance())
                }
            }

            ccp.enableHint(false)
            ccp.registerPhoneNumberTextView(mobileET)

            ccp.setOnCountryChangeListener {
                if (mobileET.error != null) {
                    mobileET.error = null
                }
            }

            gpIV.setOnClickListener{
                socialType = Const.SOCIAL_TYPE_GMAIL
                (activity as AuthActivity).LoginMaster(socialType)
            }

            fbIV.setOnClickListener {
                socialType = Const.SOCIAL_TYPE_FACEBOOK;
                (activity as AuthActivity).LoginMaster(socialType);
            }

        }

        authBack.setOnClickListener {
            (requireContext() as AuthActivity).onBackPressed()
        }

        tvHelp.setOnClickListener {
            (activity as AuthActivity).initHelpAndSupport()
        }
    }

    private fun networkCallForUserLoginAuth(activity: Activity, user: User) {
        mProgress = Progress(activity)
        mProgress.setCancelable(false)
        if (!Helper.isConnected(activity)) {
            activity.show(
                activity.resources.getString(R.string.internet_error_message),
                Toast.LENGTH_SHORT
            )
            return
        }
        mProgress.show()
        val apiInterface = ApiClient.createService(
            LoginApiInterface::class.java
        )
        var response:Call<JsonObject>? = null
        if (user.is_social == "1") {
            response = apiInterface.userLoginAuthenticationV3(
                user.email,
                "",
                user.is_social,
                user.social_type,
                user.social_tokken,
                user.device_type,
                user.device_tokken
            )
        } else {
            response = apiInterface.userLoginAuthenticationV2(
                user.mobile,
                countryCode,
                "",
                user.is_social,
                user.social_type,
                user.social_tokken,
                user.device_type,
                user.device_tokken
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
                            if (requireActivity() is AuthActivity) {
                                (requireActivity() as AuthActivity).replaceFragment(
                                    VerifyOtpFragment.newInstance(user, "FROM_MOBILE")
                                )
                            }
                        } else {
                            RetrofitResponse.handleAuthCode(
                                activity, jsonResponse.optString(Const.AUTH_CODE),
                                jsonResponse.getString(Const.AUTH_MESSAGE)
                            )
                            /* SharedPreference.getInstance()
                                 .putBoolean(Const.IS_NOTIFICATION_BLOCKED, false)*/
                        }
                        activity.show(
                            jsonResponse.optString(Constants.Extras.MESSAGE),
                            Toast.LENGTH_SHORT
                        )
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

    private fun networkCallForCheckSocial(activity: Activity, userRes: User) {
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
        val response = apiInterface.checkSocial(
            userRes.email,
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
                    val gson = Gson()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("String Login", jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
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
                            SharedPreference.getInstance().loggedInUser = user
                            RetrofitResponse.handleAuthCode(
                                activity, jsonResponse.optString(Const.AUTH_CODE),
                                jsonResponse.getString(Const.AUTH_MESSAGE)
                            )
                            if (requireActivity() is AuthActivity) {
                                (requireActivity() as AuthActivity).replaceFragment(
                                    ProfileSubmissionFragment.newInstance(userRes, Const.PROFILE)
                                )
                            }
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


    fun logInTask(`object`: JSONObject, type: String?) {
        try {
            when (type) {
                Const.SOCIAL_TYPE_FACEBOOK -> user.profile_picture = Objects.requireNonNull(
                    Objects.requireNonNull(
                        `object`.optJSONObject(
                            Const.PICTURE
                        )
                    ).optJSONObject(Const.DATA)
                ).optString(Const.URL)
                Const.SOCIAL_TYPE_GMAIL -> user.profile_picture = `object`.optString(Const.IMGURL)
            }
            var deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)
            if (GenericUtils.isEmpty(deviceId)) {
                deviceId = FirebaseInstanceId.getInstance().token
            }
            user.is_social = Const.SOCIAL_TYPE_TRUE
            user.social_tokken = `object`.getString(Constants.Extras.ID)
            if (`object`.has(Const.EMAIL)) user.setEmail(`object`.getString(Const.EMAIL)) else user.setEmail(
                ""
            )
            user.social_type = type
            if (`object`.has(Constants.Extras.NAME)) user.name =
                `object`.getString(Constants.Extras.NAME) else user.name =
                ""
            user.device_type = Const.DEVICE_TYPE_ANDROID
            user.device_tokken = deviceId
            activity?.let {
                if (!GenericUtils.isEmpty(user.email)) {
                    networkCallForCheckSocial(it, user)
                }else{
                    requireActivity().show("You can not login with facebook as we have not found your email from facebook.Please try different login option.",Toast.LENGTH_SHORT)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(): LoginByMobileFragment {
            return LoginByMobileFragment()
        }
    }
}