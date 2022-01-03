package com.emedicoz.app.login.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courses.fragment.SelectCourseFragment
import com.emedicoz.app.courses.fragment.SubStreamDialogFragment
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LayoutAcademicBinding
import com.emedicoz.app.feeds.fragment.CountryStateCity
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.modelo.Registration
import com.emedicoz.app.modelo.State
import com.emedicoz.app.modelo.User
import com.emedicoz.app.response.MasterRegistrationResponse
import com.emedicoz.app.response.registration.StreamResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis
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

class AcademicFragment : Fragment(), MyNetworkCall.MyNetworkCallBack {
    var masterRegistrationResponse: MasterRegistrationResponse? = null
    private lateinit var binding: LayoutAcademicBinding
    private lateinit var mProgress: Progress
    private lateinit var streamList: ArrayList<String>
    var streamAdapter: ArrayAdapter<String>? = null
    private var stream: StreamResponse? = null
    private lateinit var user: User
    private lateinit var myNetworkCall: MyNetworkCall

    @JvmField
    var searchDialog: Dialog? = null
    private var etSearch: EditText? = null
    var searchRecyclerview: RecyclerView? = null
    var countryStateCity: CountryStateCity? = null
    var ivClearSearch: ImageView? = null
    var tvCancel: TextView? = null
    private var state: State? = null
    private var countryId = ""
    var stateId = ""
    var cityId = ""
    var collegeId = ""
    private var countryName = ""
    var stateName = ""
    var cityName = ""
    var collegeName = ""
    lateinit var registration: Registration
    private var type: String = ""
    private val stateList = ArrayList<State?>()
    private val newStateList = ArrayList<State>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getSerializable(Const.USER) as User
        registration = if (user.user_registration_info == null) {
            Registration()
        } else {
            user.user_registration_info
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAcademicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myNetworkCall = MyNetworkCall(this, requireContext())
        mProgress = Progress(requireContext())
        mProgress.setCancelable(false)
        // binding.streamSpinner
        binding.selectCollegeTV.setOnClickListener {
            onCollegeClick()
        }

        binding.countryTV.setOnClickListener {
            type = Const.COUNTRY
            myNetworkCall.NetworkAPICall(API.API_GET_COUNTRIES, true)
        }

        binding.stateTV.setOnClickListener {
            onStateClick()
        }

        binding.selectCollegeCityTV.setOnClickListener {
            onCityClick()
        }

        binding.streamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (i != 0) {
                    stream = masterRegistrationResponse!!.main_category[i - 1]
                    if (registration.master_id != stream?.id) {
                        registration.master_id = stream?.id
                        registration.master_id_name = stream?.text_name
                        registration.master_id_level_one = ""
                        registration.master_id_level_one_name = ""
                        registration.master_id_level_two = ""
                        registration.master_id_level_two_name = ""
                    }
                } else {
                    stream = null
                    registration.master_id = ""
                    registration.master_id_name = ""
                    registration.master_id_level_one = ""
                    registration.master_id_level_one_name = ""
                    registration.master_id_level_two = ""
                    registration.master_id_level_two_name = ""
                }
                user.user_registration_info = registration
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        binding.nextBtn.setOnClickListener {
            user.state_id = stateId
            if (stateId == "001") {
                user.city_id = "-"
            } else {
                user.city_id = cityId
            }
            user.city = binding.selectCollegeCityTV.text.toString()
            user.state = binding.stateTV.text.toString()
            user.college_id = collegeId
            user.college_name = collegeName
            user.user_registration_info = registration
            var isDataValid = true
/*            user.user_registration_info.master_id_level_one = binding*/
            if (TextUtils.isEmpty(stateId)) {
                isDataValid = false
                Toast.makeText(requireContext(), "Please Select State", Toast.LENGTH_SHORT).show()
            } else if (countryId == "101") {
                if (TextUtils.isEmpty(cityId)) {
                    isDataValid = false
                    Toast.makeText(requireContext(), "Please Select City", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (TextUtils.isEmpty(collegeId) && TextUtils.isEmpty(collegeName)) {
                isDataValid = false
                Toast.makeText(requireContext(), "Please Select College", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(user.user_registration_info.master_id)) {
                isDataValid = false
                Toast.makeText(requireContext(), "Please Select Stream", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(user.user_registration_info.master_id_level_one)) {
                isDataValid = false
                Toast.makeText(requireContext(), "Please Select Sub Stream", Toast.LENGTH_SHORT)
                    .show()
            }
            if (isDataValid) {
                networkCallForContactRegistration()
            }
        }

        binding.selectSubStreamTV.setOnClickListener {
            if (!GenericUtils.isEmpty(user.user_registration_info.master_id)) {
                user.state_id = stateId
                user.city_id = cityId
                user.city = binding.selectCollegeCityTV.text.toString()
                user.state = binding.stateTV.text.toString()
                user.college_id = collegeId
                user.college_name = collegeName
                user.user_registration_info = registration
                SubStreamDialogFragment.newInstance(user)
                    .show(childFragmentManager, SubStreamDialogFragment.TAG)
            } else {
                Toast.makeText(requireContext(), "Please select stream", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setData() {
        if (user.user_registration_info != null) {
/*            if (!GenericUtils.isEmpty(user.name)) {
                binding.streamSpinner
            }*/

            if (!GenericUtils.isEmpty(user.user_registration_info.master_id_level_one_name)) {
                binding.selectSubStreamTV.text =
                    user.user_registration_info.master_id_level_one_name
            }

            if (!GenericUtils.isEmpty(user.country)) {
                binding.countryTV.text = user.country
                countryId = user.country_id
            }

            if (!GenericUtils.isEmpty(user.country_id) && user.country_id != "0") {
                if (user.country_id == "101") {
                    binding.selectCollegeCityLL.visibility = View.VISIBLE
                    if (!GenericUtils.isEmpty(user.state_id)) {
                        binding.stateTV.text = user.state
                        stateId = user.state_id
                    }
                    if (!GenericUtils.isEmpty(user.city)) {
                        binding.selectCollegeCityTV.text = user.city
                        cityId = user.city_id
                    }
                } else {
                    stateId = "001"
                    stateName = "Foreign Medical Graduates"
                    binding.stateTV.text = "Foreign Medical Graduates"
                    binding.selectCollegeCityLL.visibility = View.GONE
                }
            }


            if (!GenericUtils.isEmpty(user.college)) {
                binding.selectCollegeTV.text = user.college
                collegeId = user.college_id
                collegeName = user.college
            }
        }
    }


    private fun networkCallForContactRegistration() {
        mProgress.show()
        var response: Call<JsonObject?>? = null
        val apis = ApiClient.createService(RegFragApis::class.java)
        //  if (!TextUtils.isEmpty(user.profile_picture)) {
        response = apis?.academicRegistration(
            SharedPreference.getInstance().loggedInUser.id,
            user.user_registration_info.master_id,
            user.user_registration_info.master_id_level_one,
            user.user_registration_info.master_id_level_two,
            countryId,
            user.state_id,
            user.city_id,
            user.college_id,
            user.college_name
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
                            SharedPreference.getInstance().loggedInUser = user
                            val parentFragment =
                                (requireActivity() as AuthActivity).getCurrentFragment()
                            if (parentFragment is ProfileSubmissionFragment) {
                                parentFragment.replaceFragment(SelectCourseFragment.newInstance(user))
                                SharedPreference.getInstance().loggedInUser = user
                                parentFragment.changeBackground(
                                    Const.COURSE,
                                    isContactFilled = true,
                                    isAcademicFilled = true,
                                    isCourseFilled = false
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

    private fun onCollegeClick() {
        if (stateId != null) {
            if (stateId == "001") {
                val alertBuild = AlertDialog.Builder(
                    requireActivity()
                )
                val inflater = requireActivity().layoutInflater
                val v = inflater.inflate(R.layout.enter_college_name_dialog, null)
                alertBuild.setView(v)
                val dialog = alertBuild.create()
                val etCollegeName: EditText = v.findViewById(R.id.et_college_name)
                val btnSubmit: Button = v.findViewById(R.id.btn_submit)
                btnSubmit.setOnClickListener {
                    dialog.dismiss()
                    collegeId = ""
                    collegeName = Helper.GetText(etCollegeName)
                    binding.selectCollegeTV.setText(Helper.GetText(etCollegeName))
                }
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                val displayMetrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                dialog.window!!.setLayout(
                    displayMetrics.widthPixels * 90 / 100,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            } else {
                when {
                    TextUtils.isEmpty(stateId) -> {
                        Toast.makeText(activity, "Please select state first.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    TextUtils.isEmpty(cityId) -> {
                        Toast.makeText(activity, "Please select city first.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        type = Const.COLLEGE
                        myNetworkCall.NetworkAPICall(API.API_GET_COLLEGES, true)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume: ", "called")
        if (masterRegistrationResponse != null && masterRegistrationResponse?.main_category?.isNotEmpty()!!)
            initStreamList()
        else
            networkCallForMasterRegHit()

        setData()
    }

    fun initStreamList() {
        streamList = ArrayList<String>()
        streamList.add(getString(R.string.select_stream))
        var i = 0
        var pos = 0
        while (i < masterRegistrationResponse!!.main_category.size) {
            streamList.add(masterRegistrationResponse!!.main_category[i].text_name)
            stream = masterRegistrationResponse!!.main_category[i]
            pos = i + 1
            // }
            i++
        }
        streamAdapter = ArrayAdapter<String>(
            requireContext(), R.layout.single_row_spinner_item, streamList
        )
        binding.streamSpinner.adapter = streamAdapter
        if (!GenericUtils.isListEmpty(streamList)) {
            binding.streamSpinner.setSelection(0)
        }

        if (user.user_registration_info != null) {
            for (stream in streamList) {
                if (user.user_registration_info.master_id_name.equals(stream, true)) {
                    binding.streamSpinner.setSelection(streamList.indexOf(stream))
                }
            }
        }
    }

    private fun networkCallForMasterRegHit() {
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
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            masterRegistrationResponse = Gson().fromJson(
                                GenericUtils.getJsonObject(jsonResponse).toString(),
                                MasterRegistrationResponse::class.java
                            )
                            if (masterRegistrationResponse != null) {
                                SharedPreference.getInstance()
                                    .setMasterRegistrationData(masterRegistrationResponse)
                                initStreamList()
                            } else {
                                errorCallBack(
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    API.API_GET_MASTER_REGISTRATION_HIT
                                )
                            }
                        } else {
                            errorCallBack(
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                API.API_GET_MASTER_REGISTRATION_HIT
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
                Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 1)
            }
        })
    }


    companion object {
        private const val TAG = "AcademicFragment"
        fun newInstance(user: User): AcademicFragment {
            val fragment = AcademicFragment()
            val bundle = Bundle()
            bundle.putSerializable(Const.USER, user)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getAPI(apiType: String?, service: FlashCardApiInterface): Call<JsonObject?>? {
        val params: MutableMap<String, Any> = HashMap()
        when (apiType) {
            API.API_GET_COLLEGES -> {
                params[Const.STATE] = stateId
                params[Const.CITY] = cityId
            }
            API.API_GET_STATES, API.API_GET_COUNTRIES -> return service[apiType]
            API.API_GET_CITIES -> params[Const.STATE] = stateId
        }
        Log.e("RegistrationFragment", "getAPI: $params")
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
                API.API_GET_COLLEGES, API.API_GET_COUNTRIES, API.API_GET_STATES, API.API_GET_CITIES -> {
                    val jsonArray = GenericUtils.getJsonArray(jsonString)
                    if (jsonArray.length() > 0) {
                        stateList.clear()
                        if (type == Const.STATE) {
//                            stateList.add(new State("001", "Foreign Medical Graduates"));
                        }
                        var i = 0
                        while (i < jsonArray.length()) {
                            state = gson.fromJson(
                                jsonArray.optJSONObject(i).toString(),
                                State::class.java
                            )
                            stateList.add(state)
                            i++
                        }
                        filterList(activity, type, stateList)
                    }
                }
            }
        } else {
            errorCallBack(jsonString.getString(Constants.Extras.MESSAGE), apiType)
        }
    }

    override fun errorCallBack(jsonString: String?, apiType: String?) {
        requireActivity().show(jsonString!!, Toast.LENGTH_SHORT)
    }

    private fun filterList(
        context: Context?,
        searchType: String,
        stateArrayList: ArrayList<State?>?
    ) {
        searchDialog = Dialog(requireContext())
        searchDialog?.getWindow()?.setBackgroundDrawableResource(R.color.transparent_background)
        searchDialog?.getWindow()?.requestFeature(Window.FEATURE_NO_TITLE)
        searchDialog?.setContentView(R.layout.country_state_city_dialog)
        searchDialog?.setCancelable(true)
        etSearch = searchDialog?.findViewById<EditText>(R.id.et_search)
        val tvTitle: TextView = searchDialog?.findViewById(R.id.tv_title)!!
        if (searchType.equals(Const.COUNTRY, ignoreCase = true)) {
            tvTitle.text = "Select Country"
            etSearch?.hint = "Search Country"
        } else if (searchType.equals(Const.STATE, ignoreCase = true)) {
            tvTitle.text = "Select State"
            etSearch?.setHint("Search State")
        } else if (searchType.equals(Const.CITY, ignoreCase = true)) {
            tvTitle.text = "Select City"
            etSearch?.setHint("Search City")
        } else if (searchType.equals(Const.COLLEGE, ignoreCase = true)) {
            tvTitle.text = "Select College"
            etSearch?.setHint("Search College")
        }
        ivClearSearch = searchDialog?.findViewById<ImageView>(R.id.iv_clear_search)
        tvCancel = searchDialog?.findViewById<TextView>(R.id.tv_cancel)
        ivClearSearch?.setOnClickListener(View.OnClickListener { v: View? ->
            etSearch?.setText(
                ""
            )
        })
        tvCancel?.setOnClickListener { searchDialog?.cancel() }
        searchRecyclerview = searchDialog?.findViewById<RecyclerView>(R.id.search_recyclerview)
        searchRecyclerview?.setHasFixedSize(true)
        searchRecyclerview?.layoutManager = LinearLayoutManager(context)
        countryStateCity =
            CountryStateCity(activity, this@AcademicFragment, searchType, stateArrayList)
        searchRecyclerview?.adapter = countryStateCity
        textWatcher()
        searchDialog?.show()
    }

    fun textWatcher() {
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty()) {
                    ivClearSearch?.setVisibility(View.VISIBLE)
                } else {
                    ivClearSearch?.setVisibility(View.GONE)
                }
                //after the change calling the method and passing the search input
                filter(editable.toString())
            }
        })
    }

    private fun filter(text: String) {
        newStateList.clear()
        for (state in stateList) {
            if (type == Const.COUNTRY) {
                if (state?.name?.toLowerCase()?.contains(text.toLowerCase())!!) {
                    newStateList.add(state)
                }
            } else if (type == Const.STATE) {
                if (state?.stateName?.toLowerCase()?.contains(text.toLowerCase())!!) {
                    newStateList.add(state)
                }
            } else if (type == Const.CITY) {
                if (state?.cityName?.toLowerCase(Locale.ROOT)?.contains(text.toLowerCase())!!) {
                    newStateList.add(state)
                }
            }
        }
        if (newStateList.isNotEmpty()) {
            searchRecyclerview?.setVisibility(View.VISIBLE)
            countryStateCity?.filterList(newStateList)
        } else {
            searchRecyclerview?.setVisibility(View.INVISIBLE)
        }
    }


    fun getStateData(mType: String?, stateId: String, name: String) {
        Log.e("TAG", "getStateData: $stateId")
        this.stateId = stateId
        cityId = ""
        cityName = ""
        collegeId = ""
        collegeName = ""
        binding.selectCollegeCityTV.text = ""
        //collegeTV.setText("")
        if (stateId == "001") {
            countryId = ""
            binding.selectCollegeCityLL.visibility = View.GONE
            //collegeTV.setVisibility(View.GONE)
        } else {
            binding.selectCollegeCityLL.visibility = View.VISIBLE
            //collegeTV.setVisibility(View.VISIBLE)
        }
        stateName = name
        binding.stateTV.text = name
    }

    fun getCityData(mType: String?, stateId: String, cityId: String, name: String) {
        Log.e("TAG", "getCityData: $stateId --- $cityId")
        this.stateId = stateId
        this.cityId = cityId
        collegeId = ""
        //collegeTV.setText("")
        cityName = name
        binding.selectCollegeCityTV.setText(name)
    }

    fun getCollegeData(
        mType: String?,
        stateId: String,
        cityId: String,
        collegeId: String,
        name: String
    ) {
        Log.e("TAG", "getCollegeData: $stateId --- $cityId --- $collegeId")
        this.stateId = stateId
        this.cityId = cityId
        this.collegeId = collegeId
        collegeName = name
        binding.selectCollegeTV.text = name
    }

    private fun onStateClick() {
        if (TextUtils.isEmpty(countryId)) {
            Toast.makeText(activity, "Please select Country first.", Toast.LENGTH_SHORT).show()
        } else {
            if (countryId == "101") {
                type = Const.STATE
                myNetworkCall!!.NetworkAPICall(API.API_GET_STATES, true)
            } else {
                val builder = activity?.let {
                    AlertDialog.Builder(
                        it
                    )
                }
                builder?.setMessage("If you want to change state name please select India in country field.")
                builder?.setPositiveButton(
                    "Ok"
                ) { dialog1: DialogInterface, which: Int -> dialog1.dismiss() }
                val dialog = builder?.create()
                dialog?.show()
            }
        }
    }

    private fun onCityClick() {
        when {
            TextUtils.isEmpty(countryId) -> {
                Toast.makeText(activity, "Please select country first.", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(stateId) -> {
                Toast.makeText(activity, "Please select state first.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                type = Const.CITY
                myNetworkCall!!.NetworkAPICall(API.API_GET_CITIES, true)
            }
        }
    }

    fun setSubStreamTV(subStream: String) {
        binding.selectSubStreamTV.text = subStream
    }

    fun getCountryData(mType: String?, countryId: String, name: String) {
        Log.e(TAG, "getCountryData: $countryId")
        this.countryId = countryId
        stateId = ""
        cityId = ""
        collegeId = ""
        collegeName = ""
        binding.selectCollegeCityTV.text = ""
        binding.selectCollegeTV.text = ""
        if (countryId == "101") {
            binding.stateTV.text = ""
            binding.selectCollegeCityLL.visibility = View.VISIBLE
        } else {
            stateId = "001"
            stateName = "Foreign Medical Graduates"
            binding.stateTV.text = "Foreign Medical Graduates"
            binding.selectCollegeCityLL.visibility = View.GONE
        }
        countryName = name
        binding.countryTV.text = name
    }

}