package com.emedicoz.app.recordedCourses.model

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CourseListingViewModel : ViewModel() {

    var courseId: String = ""
    var publishResult: MutableLiveData<ArrayList<Course>> = MutableLiveData()

    fun getUserWishList() {
        // asynchronous operation to get user wishlist.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getUserWishlist(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()

                    val arrCourseList: JSONArray?
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    val courseArrayList = ArrayList<Course>()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list")

                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val course = gson.fromJson(Objects.requireNonNull(courseObject).toString(), Course::class.java)

                                if (Helper.isMrpZero(courseObject)) {
                                    course.calMrp = "Free"
                                } else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                                    course.isIs_dams = true
                                    if (courseObject.optString("for_dams") == "0") {
                                        course.calMrp = "Free"
                                    } else {
                                        if (courseObject.optString("mrp") == courseObject.optString("for_dams")) {
                                            course.calMrp = String.format("%s %s", Helper.getCurrencySymbol(),
                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")))
                                        } else {
                                            course.isDiscounted = true
                                            course.calMrp = Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("non_dams"))
                                        }
                                    }
                                } else {
                                    course.isIs_dams = false
                                    if (courseObject.optString("non_dams") == "0") {
                                        course.calMrp = "Free"
                                    } else {
                                        if (courseObject.optString("mrp") == courseObject.optString("non_dams")) {
                                            course.calMrp = String.format("%s %s", Helper.getCurrencySymbol(),
                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")))
                                        } else {
                                            course.isDiscounted = true
                                            course.calMrp = Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                    Helper.calculatePriceBasedOnCurrency(courseObject.optString("non_dams"))
                                        }
                                    }
                                }
                                if (course.calMrp.equals("free", true) || course.isIs_purchased)
                                    course.isFreeTrial = false

                                if (!course.isIs_purchased) {
                                    courseArrayList.add(course)
                                }
                            }
                            publishResult.postValue(courseArrayList)
                        } else
                            publishResult.postValue(null)


                    } catch (e: JSONException) {
                        publishResult.postValue(null)
                        e.printStackTrace()
                    }
                } else {
//                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                    publishResult.postValue(null)
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                publishResult.postValue(null)

            }
        })

    }

    fun getChildCourseList() {
        // asynchronous operation to get child course list.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getChildCourseList(SharedPreference.getInstance().loggedInUser.id, courseId)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()

                    val arrCourseList: JSONArray?
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    val courseArrayList = ArrayList<Course>()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("list")
                            if (arrCourseList != null) {
                                for (j in 0 until arrCourseList.length()) {
                                    val courseObject = arrCourseList.getJSONObject(j)
                                    val course = gson.fromJson(Objects.requireNonNull(courseObject).toString(), Course::class.java)

                                    if (Helper.isMrpZero(courseObject)) {
                                        course.calMrp = "Free"
                                    } else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                                        course.isIs_dams = true
                                        if (courseObject.optString("for_dams") == "0") {
                                            course.calMrp = "Free"
                                        } else {
                                            if (courseObject.optString("mrp") == courseObject.optString("for_dams")) {
                                                course.calMrp = String.format("%s %s", Helper.getCurrencySymbol(),
                                                        Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")))
                                            } else {
                                                course.isDiscounted = true
                                                course.calMrp = Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                        Helper.calculatePriceBasedOnCurrency(courseObject.optString("non_dams"))
                                            }
                                        }
                                    } else {
                                        course.isIs_dams = false
                                        if (courseObject.optString("non_dams") == "0") {
                                            course.calMrp = "Free"
                                        } else {
                                            if (courseObject.optString("mrp") == courseObject.optString("non_dams")) {
                                                course.calMrp = String.format("%s %s", Helper.getCurrencySymbol(),
                                                        Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")))
                                            } else {
                                                course.isDiscounted = true
                                                course.calMrp = Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp")) + " " +
                                                        Helper.calculatePriceBasedOnCurrency(courseObject.optString("non_dams"))
                                            }
                                        }
                                    }
                                    if (course.calMrp.equals("free", true) || course.isIs_purchased)
                                        course.isFreeTrial = false
                                    courseArrayList.add(course)
                                }
                            }
                            publishResult.postValue(courseArrayList)
                        } else
                            publishResult.postValue(null)

                    } catch (e: JSONException) {
                        publishResult.postValue(null)
                        e.printStackTrace()
                    }
                } else {
//                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                    publishResult.postValue(null)
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                publishResult.postValue(null)

            }
        })

    }
}