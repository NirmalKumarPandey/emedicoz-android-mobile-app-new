package com.emedicoz.app.courses.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LayoutSelectCourseBinding
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.login.fragment.ProfileSubmissionFragment
import com.emedicoz.app.modelo.Registration
import com.emedicoz.app.modelo.User
import com.emedicoz.app.response.MasterRegistrationResponse
import com.emedicoz.app.response.registration.CoursesInterestedResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SelectCourseFragment : Fragment(), MyNetworkCall.MyNetworkCallBack {
    private lateinit var binding: LayoutSelectCourseBinding
    var user: User? = null
    var coursesViewList: ArrayList<View>? = null
    var selectedcoursesList: ArrayList<String>? = null
    var coursesCheckBox: ArrayList<CheckBox>? = null
    var coursesResponseList: ArrayList<CoursesInterestedResponse>? = null
    var registration: Registration? = null
    var regType: String? = null
    private lateinit var userMain: User
    private lateinit var mProgress: Progress
    private lateinit var myNetworkCall: MyNetworkCall
    var onCheckboxClick =
        CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            val resp = buttonView.tag as CoursesInterestedResponse
            if (isChecked && !selectedcoursesList!!.contains(resp.id)) {
                selectedcoursesList!!.add(resp.id)
            } else if (!isChecked) {
                selectedcoursesList!!.remove(resp.id)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getSerializable(Const.USER) as User
        registration = if (user?.user_registration_info == null) {
            Registration()
        } else {
            user?.user_registration_info
        }
        selectedcoursesList = ArrayList()
        coursesResponseList = ArrayList()
        coursesResponseList =
            getCoursesInterestedList(SharedPreference.getInstance().registrationResponse.intersted_course)
    }

    fun getCoursesInterestedList(list: ArrayList<CoursesInterestedResponse>): ArrayList<CoursesInterestedResponse> {
        val coursesInterestedResponses = ArrayList<CoursesInterestedResponse>()
        for (coursesInterestedResponse in list) {
            if (coursesInterestedResponse.parent_id.equals("1", ignoreCase = true)) {
                coursesInterestedResponses.add(coursesInterestedResponse)
            }
        }
        return coursesInterestedResponses
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSelectCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myNetworkCall = MyNetworkCall(this, requireContext())
        mProgress = Progress(requireContext())
        mProgress.setCancelable(false)
        if (!coursesResponseList!!.isEmpty()) {
            initCoursesOptions()
        } else {
            networkCallForMasterRegHit() //// NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
        }

        binding.nextBtn.setOnClickListener {
            var substr = ""
            var substrtext = ""
            for (str in selectedcoursesList!!) {
                substr = if (substr == "") str else "$substr,$str"
            }
            for (str1 in selectedcoursesList!!) {
                for (ci in coursesResponseList!!) {
                    if (ci.id == str1) {
                        substrtext =
                            if (substrtext == "") ci.text_name else substrtext + ", " + ci.text_name
                    }
                }
            }
            registration!!.interested_course = substr
            registration!!.interested_course_text = substrtext
            user!!.user_registration_info = registration
            Log.e("onViewCreated: ", user.toString())
            var deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)
            if (GenericUtils.isEmpty(deviceId)) {
                deviceId = FirebaseInstanceId.getInstance().token
            }
            user?.device_tokken = deviceId
            if (!GenericUtils.isEmpty(user!!.user_registration_info.interested_course)) {
                networkCallForCouserRegistration()
            } else {
                requireActivity().show("Please Select atleast one course", Toast.LENGTH_SHORT)
            }
            //networkCallForStreamRegistration()
        }
    }

    fun initCoursesOptions() {
        addViewtoCoursesOpt()
    }

    private fun addViewtoCoursesOpt() {
        var courses: Array<String>? = null
        coursesCheckBox = ArrayList()
        binding.coursesLL.visibility = View.VISIBLE
        coursesViewList = ArrayList()
        val v = View.inflate(activity, R.layout.single_row_catcourse, null)
        val coursesoptionLL = v.findViewById<LinearLayout>(R.id.coursesoptionLL)
        var j = 0
        while (j < coursesResponseList!!.size) {
            val layout = LinearLayout(requireContext())
            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,

                LinearLayout.LayoutParams.MATCH_PARENT
            )
            layout.orientation = LinearLayout.HORIZONTAL

            val tv = TextView(requireContext())
            tv.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
            tv.text = coursesResponseList!![j].text_name
            tv.gravity = Gravity.CENTER
            tv.setBackgroundColor(requireActivity().resources.getColor(R.color.gray_8e8e8e))
            val cb = CheckBox(activity)
            cb.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                .2f
            )
            //cb.text = coursesResponseList!![j].text_name
            // cb.setPadding(8, 8, 8, 8)
            cb.tag = coursesResponseList!![j]
            if (registration != null && registration!!.interested_course != null && registration!!.interested_course != "") {
                courses = registration!!.interested_course.split(",").toTypedArray()
            }
            if (courses != null) {
                for (str in courses) {
                    if (coursesResponseList!![j].id == str) {
                        cb.isChecked = true
                        selectedcoursesList!!.add(str)
                    }
                }
            }
            coursesCheckBox!!.add(cb)
            layout.addView(tv)
            layout.addView(cb)
            coursesoptionLL.addView(layout)
            cb.setOnCheckedChangeListener(onCheckboxClick)
            j++
        }
        binding.coursesLL.addView(v)
    }

    fun networkCallForMasterRegHit() {
        mProgress.show()
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getMasterRegistrationResponse()
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val masterRegistrationResponse = Gson().fromJson(
                                GenericUtils.getJsonObject(jsonResponse).toString(),
                                MasterRegistrationResponse::class.java
                            )
                            coursesResponseList =
                                getCoursesInterestedList(masterRegistrationResponse.intersted_course)
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(
                                    activity!!.resources.getString(R.string.internet_error_message),
                                    ignoreCase = true
                                )
                            ) Helper.showErrorLayoutForNav(
                                API.API_GET_MASTER_REGISTRATION_HIT,
                                activity,
                                1,
                                1
                            )
                            if (jsonResponse.optString(Constants.Extras.MESSAGE)
                                    .contains(getString(R.string.something_went_wrong_string))
                            ) Helper.showErrorLayoutForNav(
                                API.API_GET_MASTER_REGISTRATION_HIT,
                                activity,
                                1,
                                2
                            )
                            var data: JSONObject? = null
                            var popupMessage: String? = ""
                            data = GenericUtils.getJsonObject(jsonResponse)
                            popupMessage = data.getString("popup_msg")
                            RetrofitResponse.handleAuthCode(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE),
                                popupMessage
                            )
                        }
                        initCoursesOptions()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
                Helper.showErrorLayoutForNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 1)
            }
        })
    }


    private fun networkCallForCouserRegistration() {
        mProgress.show()
        var response: Call<JsonObject?>? = null
        val apis = ApiClient.createService(RegFragApis::class.java)
        response = apis?.courseRegistration(
            SharedPreference.getInstance().loggedInUser.id,
            user!!.user_registration_info.interested_course
        )
        SharedPreference.getInstance().putBoolean(Const.PICTURE, true)
        response?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Log.e("TAG", "onResponse: $jsonResponse")
                            val jsonObject1: JSONObject = jsonResponse.optJSONObject("data")!!
                            user = Gson().fromJson(jsonObject1.toString(), User::class.java)
                            //SharedPreference.getInstance().ClearLoggedInUser()
                            SharedPreference.getInstance().loggedInUser = user
                            (activity as AuthActivity).followExpertCounter = user!!.expert_following
                            SharedPreference.getInstance().putBoolean(Const.SYNC_COMPLETE, false)
                            SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, true)
                            SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true)
                            SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true)
                            DbAdapter.getInstance(activity)
                                .deleteAll(DbAdapter.TABLE_NAME_COLORCODE)
/*                            SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true)
                            SharedPreference.getInstance().putBoolean(
                                Const.IS_USER_REGISTRATION_DONE, true
                            )*/
                            val masterFeedsHitResponse =
                                SharedPreference.getInstance().masterHitResponse
                            if (masterFeedsHitResponse != null) {
                                masterFeedsHitResponse.user_detail = user
                                SharedPreference.getInstance()
                                    .setMasterHitData(masterFeedsHitResponse)
                            }
                            val parentFragment =
                                (requireActivity() as AuthActivity).getCurrentFragment()
                            if (parentFragment is ProfileSubmissionFragment) {
                                parentFragment.replaceFragment(FollowExpertFragment.newInstance())
                                parentFragment.changeBackground(
                                    Const.FOLLOW,
                                    isContactFilled = true,
                                    isAcademicFilled = true,
                                    isCourseFilled = true
                                )
                            }
                        } else {
                            errorCallBack(
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                API.API_STREAM_REGISTRATION
                            )
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 1)
            }
        })
    }

    companion object {
        fun newInstance(user: User): SelectCourseFragment {
            val fragment = SelectCourseFragment()
            val bundle = Bundle()
            bundle.putSerializable(Const.USER, user)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getAPI(apiType: String?, service: FlashCardApiInterface): Call<JsonObject?>? {
        val params: MutableMap<String, Any> = HashMap()
        when (apiType) {
            API.API_GET_UPDATE_COLLEGE_INFO -> {
                params[Const.USER_ID] =
                    if (SharedPreference.getInstance().loggedInUser != null) SharedPreference.getInstance().loggedInUser.id else ""
                params[Const.COUNTRY_ID] = user?.country_id!!
                params[Const.COUNTRY_NAME] = user?.country!!
                params[Const.STATE_ID] = if (user?.state_id == "001") "" else user?.state_id!!
                params[Const.CITY_ID] = user?.city_id!!
                params[Const.COLLEGE_ID] = user?.college_id!!
                params[Const.COLLEGE_NAME] = user?.college_name!!
            }
        }
        return service.postData(apiType, params)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonString: JSONObject, apiType: String?) {
        val gson = Gson()
        Log.e("TAG", jsonString.toString())
        val data: JSONObject
        val user1: User
        if (jsonString.optBoolean(Const.STATUS)) {
            Helper.showErrorLayoutForNoNav(apiType, activity, 0, 0)
            when (apiType) {
                API.API_GET_UPDATE_COLLEGE_INFO -> {
                    val userNew = SharedPreference.getInstance().loggedInUser
                    userNew.country_id = user!!.country_id
                    userNew.state_id = user!!.state_id
                    userNew.city_id = user!!.city_id
                    userNew.college_id = user!!.college_id
                    userNew!!.college =
                        GenericUtils.getJsonObject(jsonString).optString("college_name")
                    userNew.city = GenericUtils.getJsonObject(jsonString).optString("city_name")
                    userNew.state = GenericUtils.getJsonObject(jsonString).optString("state_name")
                    userNew.country =
                        GenericUtils.getJsonObject(jsonString).optString("country_name")
                    Log.e("successCallBack: ", userNew.toString())
                    SharedPreference.getInstance().loggedInUser = userNew
                    val masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
                    if (masterFeedsHitResponse != null) {
                        masterFeedsHitResponse.user_detail = userNew
                        SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse)
                    }
                    val parentFragment =
                        (requireActivity() as AuthActivity).getCurrentFragment()
                    if (parentFragment is ProfileSubmissionFragment) {
                        parentFragment.replaceFragment(FollowExpertFragment.newInstance())
                        parentFragment.changeBackground(
                            Const.FOLLOW,
                            isContactFilled = true,
                            isAcademicFilled = true,
                            isCourseFilled = true
                        )
                    }
/*                    NewProfileActivity.IS_PROFILE_UPDATED = true
                    //     isDataChanged = false
                    if (regType == Const.REGISTRATION) {
                        if (userMain.expert_following < Helper.getMinimumFollowerCount()) {
                            SharedPreference.getInstance()
                                .putBoolean(Const.IS_USER_REGISTRATION_DONE, false)
                            Helper.GoToFollowExpertActivity(activity, Const.FOLLOW_THE_EXPERT_FIRST)
                        } else {
                            SharedPreference.getInstance()
                                .putBoolean(Const.IS_USER_REGISTRATION_DONE, true)
                            Helper.GoToNextActivity(activity)
                        }
                    } else {
                        requireActivity().finish()
                    }*/
                }
            }
        } else {
            errorCallBack(jsonString.getString(Constants.Extras.MESSAGE), apiType)
        }
    }

    override fun errorCallBack(jsonString: String?, apiType: String?) {
        requireActivity().show(jsonString!!, Toast.LENGTH_SHORT)
    }

}