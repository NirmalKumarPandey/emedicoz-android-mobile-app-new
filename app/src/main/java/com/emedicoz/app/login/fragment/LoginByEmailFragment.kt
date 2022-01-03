package com.emedicoz.app.login.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
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
import kotlinx.android.synthetic.main.transparent_auth_toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginByEmailFragment : Fragment() {

    private lateinit var binding: LayoutMobileOtpRegisterationBinding
    private lateinit var mProgress: Progress
    private var email: String = ""
    private lateinit var user:User
    private var socialType:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.apply {
            registerUsingEmail.visibility = View.GONE
            registerUsingMobile.visibility = View.VISIBLE

            loginByEmail.visibility = View.VISIBLE
            loginByMobile.visibility = View.GONE
            btnGetOtp.setOnClickListener {
                validateFields()
            }
            registerUsingMobile.setOnClickListener {
                if (requireActivity() is AuthActivity) {
                    (requireActivity() as AuthActivity).replaceFragment(LoginByMobileFragment.newInstance())
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
        authBack.setOnClickListener{
            (requireContext() as AuthActivity).onBackPressed()
        }

        tvHelp.setOnClickListener{
            (activity as AuthActivity).initHelpAndSupport()
        }
    }

    private fun validateFields() {
        email = binding.emailET.text.trim().toString()
        var deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)
        if (deviceId == null || deviceId == "") {
            deviceId = FirebaseInstanceId.getInstance().token
        }
        if (!Helper.isValidEmail(email)) {
            Helper.DataNotValid(binding.emailET, 1)
        } else {
            user.email = email
            user.is_social = "0"
            user.social_tokken = ""
            user.social_type = ""
            user.device_type = Const.DEVICE_TYPE_ANDROID
            user.device_tokken = deviceId
            networkCallForUserLoginAuth(requireActivity(), user)
        }
    }


    private fun networkCallForUserLoginAuth(activity: Activity, user: User) {
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
        val response = apiInterface.userLoginAuthenticationV3(
            user.email,
            "",
            user.is_social,
            user.social_type,
            user.social_tokken,
            user.device_type,
            user.device_tokken
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
                            if (requireActivity() is AuthActivity) {
                                (requireActivity() as AuthActivity).replaceFragment(
                                    VerifyOtpFragment.newInstance(user, "FROM_EMAIL")
                                )
                            }
                        } else {
                            RetrofitResponse.handleAuthCode(
                                activity, jsonResponse.optString(Const.AUTH_CODE),
                                jsonResponse.getString(Const.AUTH_MESSAGE)
                            )
                            SharedPreference.getInstance()
                                .putBoolean(Const.IS_NOTIFICATION_BLOCKED, false)
                            Toast.makeText(
                                activity,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
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
            if (`object`.has(Const.EMAIL)) user.email = `object`.getString(Const.EMAIL) else user.email =
                ""
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
                    requireActivity().show("You can not login with facebook as we have not found your email from facebook.Please try different login options",Toast.LENGTH_SHORT)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(): LoginByEmailFragment {
            return LoginByEmailFragment()
        }
    }
}