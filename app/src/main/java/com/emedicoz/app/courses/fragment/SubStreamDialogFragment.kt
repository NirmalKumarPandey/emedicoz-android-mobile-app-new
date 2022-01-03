package com.emedicoz.app.courses.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.FragmentSubStreamBinding
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.login.fragment.AcademicFragment
import com.emedicoz.app.login.fragment.ProfileSubmissionFragment
import com.emedicoz.app.modelo.Registration
import com.emedicoz.app.modelo.User
import com.emedicoz.app.response.MasterRegistrationResponse
import com.emedicoz.app.response.registration.SubStreamResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SubStreamDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentSubStreamBinding
    var subStreamViewList: ArrayList<View>? = null
    var subStreamResponseListFrag: ArrayList<SubStreamResponse>? = null
    var subStreamList: ArrayList<String>? = null
    var registration: Registration? = null
    var user: User? = null
    var mProgress: Progress? = null
    private var onOptionClick = View.OnClickListener { view1: View ->
        registration!!.master_id_level_two = ""
        registration!!.master_id_level_two_name = ""
        var i = 0
        var j = 0
        var substr = ""
        while (i < subStreamViewList!!.size) {
            var v = subStreamViewList!![i]
            if (view1.tag === v.tag) {
                v.findViewById<View>(R.id.optioniconIBtn).visibility = View.VISIBLE
                v = changeBackgroundColor(v, 1)
                substr = v.tag.toString()
            } else {
                v.findViewById<View>(R.id.optioniconIBtn).visibility = View.GONE
                v = changeBackgroundColor(v, 2)
            }
            i++
        }
        while (j < subStreamResponseListFrag!!.size) {
            val sub = subStreamResponseListFrag!![j]
            if (sub.text_name == substr) {
                registration!!.master_id_level_one = sub.id
                registration!!.master_id_level_one_name = sub.text_name
            }
            j++
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgress = Progress(getActivity())
        mProgress!!.setCancelable(false)
        user = arguments?.getSerializable(Const.USER) as User
        registration = if (user?.user_registration_info != null)
            user?.user_registration_info
        else
            Registration()
        subStreamResponseListFrag = ArrayList()
/*        if (registration != null)
            subStreamResponseListFrag =
                getsubStreamList(SharedPreference.getInstance().registrationResponse.main_sub_category)*/
    }

    fun getsubStreamList(list: ArrayList<SubStreamResponse>): ArrayList<SubStreamResponse> {
        val subStreamResponses = ArrayList<SubStreamResponse>()
        for (streamResponse in list) {
            if (streamResponse.parent_id.equals(registration?.master_id, ignoreCase = true)) {
                subStreamResponses.add(streamResponse)
            }
        }
        return subStreamResponses
    }

    override fun onDestroy() {
        super.onDestroy()
        subStreamResponseListFrag = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (registration != null) {
            binding.mainstreamTV.visibility = View.VISIBLE
            binding.mainstreamTV.text = registration?.master_id_name + " > "
        } else
            binding.mainstreamTV.visibility = View.GONE

        if (subStreamResponseListFrag!!.isNotEmpty()) {
            initSubStreamOptions()
        } else {
            networkCallForMasterReg()
        }
        binding.nextBtn.setOnClickListener {
            if (GenericUtils.isEmpty(
                    registration!!.master_id_level_one_name
                )
            ) Toast.makeText(
                activity,
                "Kindly select any Option first",
                Toast.LENGTH_SHORT
            ).show() else {
                user!!.user_registration_info = registration
                dismiss()
                (((requireActivity() as AuthActivity).getCurrentFragment() as ProfileSubmissionFragment).getCurrentFragment() as AcademicFragment).registration =
                    registration!!
                (((requireActivity() as AuthActivity).getCurrentFragment() as ProfileSubmissionFragment).getCurrentFragment() as AcademicFragment).setSubStreamTV(
                    registration!!.master_id_level_one_name
                )
                Log.e(
                    TAG,
                    "onViewCreated: " + ((requireActivity() as AuthActivity).getCurrentFragment() as ProfileSubmissionFragment).getCurrentFragment()
                )
            }
        }
    }

    fun initSubStreamOptions() {
        subStreamList = ArrayList()
        for (subStreamResponse in subStreamResponseListFrag!!) {
            subStreamList!!.add(subStreamResponse.text_name)
        }
        addViewtoSubStreamOpt()
    }

    private fun addViewtoSubStreamOpt() {
        binding.substreamRL.visibility = View.VISIBLE
        subStreamViewList = ArrayList()
        var i = 0
        while (i < subStreamList!!.size) {
            var v = View.inflate(activity, R.layout.single_row_reg_option, null)
            val tv = v.findViewById<TextView>(R.id.optionTextTV)
            v = changeBackgroundColor(v, 2)
            v.findViewById<View>(R.id.optioniconIBtn).visibility = View.GONE
            tv.text = subStreamList!![i]
            v.tag = tv.text
            v.setOnClickListener(onOptionClick)
            if (subStreamList!![i] == registration!!.master_id_level_one_name) {
                v = changeBackgroundColor(v, 1)
                v.findViewById<View>(R.id.optioniconIBtn).visibility = View.VISIBLE
                registration!!.master_id_level_one = subStreamResponseListFrag!![i].id
                registration!!.master_id_level_one_name = subStreamResponseListFrag!![i].text_name
            }
            binding.substreamoptionLL.addView(v)
            subStreamViewList!!.add(v)
            i++
        }
    }

    fun changeBackgroundColor(v: View, type: Int): View {
        v.setBackgroundResource(R.drawable.bg_refcode_et)
        val drawable = v.background as GradientDrawable
        drawable.setColor(ContextCompat.getColor(requireActivity(), R.color.white))
        if (type == 1) drawable.setStroke(
            3,
            ContextCompat.getColor(requireActivity(), R.color.blue)
        ) else drawable.setStroke(
            3, ContextCompat.getColor(
                requireActivity(), R.color.transparent
            )
        )
        return v
    }

    fun networkCallForMasterReg() {
        mProgress!!.show()
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getMasterRegistrationResponse()
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    var jsonResponse: JSONObject? = null
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val masterRegistrationResponse = Gson().fromJson(
                                GenericUtils.getJsonObject(jsonResponse).toString(),
                                MasterRegistrationResponse::class.java
                            )
                            subStreamResponseListFrag =
                                getsubStreamList(masterRegistrationResponse.main_sub_category)
                            if (subStreamResponseListFrag!!.isNotEmpty()) {
                                SharedPreference.getInstance()
                                    .setMasterRegistrationData(masterRegistrationResponse)
                                initSubStreamOptions()
                                Log.e("substream", "post count " + subStreamResponseListFrag!!.size)
                            } else {
                                Toast.makeText(
                                    activity,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
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
                mProgress!!.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 2)
            }
        })
    }

    companion object {
        const val TAG = "SubStreamDialogFragment"
        fun newInstance(user: User): SubStreamDialogFragment {
            val fragment = SubStreamDialogFragment()
            val args = Bundle()
            args.putSerializable(Const.USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}