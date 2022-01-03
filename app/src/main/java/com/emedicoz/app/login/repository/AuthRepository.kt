package com.emedicoz.app.login.repository

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.modelo.User
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AuthRepository {

    private lateinit var mProgress:Progress
    init {

    }
    fun networkCallForUserLoginAuth(activity: Activity, user: User) {
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
        val response = apiInterface.userLoginAuthentication(
            user.email,
            user.password,
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
}