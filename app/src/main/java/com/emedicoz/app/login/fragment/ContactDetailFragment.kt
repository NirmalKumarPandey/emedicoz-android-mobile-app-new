package com.emedicoz.app.login.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.customviews.imagecropper.TakeImageClass
import com.emedicoz.app.databinding.LayoutContactDetailsBinding
import com.emedicoz.app.feeds.fragment.CountryStateCity
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.modelo.MediaFile
import com.emedicoz.app.modelo.State
import com.emedicoz.app.modelo.User
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading
import com.emedicoz.app.utilso.network.API
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.layout_contact_details.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class ContactDetailFragment : Fragment(), MyNetworkCall.MyNetworkCallBack,
    TakeImageClass.ImageFromCropper, AmazonCallBack {
    private var myNetworkCall: MyNetworkCall? = null
    private var email: String = ""
    private var name: String = ""
    private var mobile: String = ""
    private var type: String = ""
    private val stateList = ArrayList<State?>()
    private val newStateList = ArrayList<State>()
    private var takeImageClass: TakeImageClass? = null
    var isPictureChanged = false
    var isDataChanged = false
    var bitmap: Bitmap? = null
    var s3ImageUploading: s3ImageUploading? = null
    var mediaFile: ArrayList<MediaFile>? = null

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
    private lateinit var user: User
    private lateinit var binding: LayoutContactDetailsBinding
    private lateinit var mProgress: Progress
    private var isEmailVerified = false
    private var isMobileVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getSerializable("user") as User
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutContactDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myNetworkCall = MyNetworkCall(this, activity)
        takeImageClass = TakeImageClass(activity, this)
        mProgress = Progress(requireActivity())
        mProgress.setCancelable(false)
        binding.countryTV.setOnClickListener {
            type = Const.COUNTRY
            myNetworkCall?.NetworkAPICall(API.API_GET_COUNTRIES, true)
        }

        if (GenericUtils.isEmpty(user.name)) {
            if (!GenericUtils.isEmpty(user.mobile)) {
                binding.phonenumberTV.setText(user.mobile)
                binding.phonenumberTV.isEnabled = false
                binding.ccp.isEnabled = false
                binding.verifyEmailBtn.visibility = View.GONE
                binding.verifyMobileBtn.visibility = View.GONE
                isMobileVerified = true
            } else if (!GenericUtils.isEmpty(user.email)) {
                binding.tietEmail.setText(user.email)
                binding.tietEmail.isEnabled = false
                binding.verifyMobileBtn.visibility = View.GONE
                binding.verifyEmailBtn.visibility = View.GONE
                isEmailVerified = true
            }
        } else {
            if (user.is_social == "1") {
                binding.phonenumberTV.isEnabled = true
                binding.ccp.isEnabled = true
                binding.ccp.isClickable = true
            } else {
                binding.phonenumberTV.isEnabled = false
                binding.ccp.isEnabled = false
                binding.ccp.isClickable = false
            }
            isMobileVerified = true
            isEmailVerified = true
            binding.tietEmail.isEnabled = false
            binding.verifyEmailBtn.visibility = View.GONE
            binding.verifyMobileBtn.visibility = View.GONE
        }

        bindControls()
        setData()
    }

    private fun setData() {
        if (!GenericUtils.isEmpty(user.name)) {
            binding.tietName.setText(user.name)
        }

        if (!GenericUtils.isEmpty(user.mobile)) {
            binding.phonenumberTV.setText(user.mobile)
        }

        if (!GenericUtils.isEmpty(user.email)) {
            binding.tietEmail.setText(user.email)
        }

        if (!GenericUtils.isEmpty(user.user_country)) {
            binding.countryTV.text = user.user_country
            countryId = user.u_c_id
        }

        if (!GenericUtils.isEmpty(user.u_c_id) && !user.u_c_id.equals("0")) {
            if (user.u_c_id == "101") {
                binding.cityTV.visibility = View.VISIBLE
                if (!GenericUtils.isEmpty(user.u_s_id)) {
                    binding.stateTV.text = user.user_state
                    stateId = user.u_s_id
                }
                if (!GenericUtils.isEmpty(user.user_city)) {
                    binding.cityTV.text = user.user_city
                    cityId = user.u_ct_id
                }
            } else {
                stateId = "001"
                stateName = "Foreign Medical Graduates"
                binding.stateTV.text = "Foreign Medical Graduates"
                binding.cityTV.visibility = View.GONE
            }
        }
        if (!GenericUtils.isEmpty(user.dams_tokken)) {
            binding.damsUserSwitch.isChecked = true
            binding.damsIdTIL.visibility = View.VISIBLE
            binding.damsIdET.setText(user.dams_tokken)
        }

    }

    private fun bindControls() {
        binding.apply {
            stateTV.setOnClickListener {
                onStateClick()
            }

            cityTV.setOnClickListener {
                onCityClick()
            }

            uploadPicIV.setOnClickListener {
                takeImageClass?.getImagePickerDialog(
                    activity,
                    getString(R.string.upload_profile_picture),
                    getString(R.string.choose_image)
                )
            }

            damsUserSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.damsIdTIL.visibility = View.VISIBLE
                } else {
                    binding.damsIdTIL.visibility = View.GONE
                }
            })

            nextBtn.setOnClickListener {
                checkValidation()
            }

            verifyEmailBtn.setOnClickListener {
                user.email = binding.tietEmail.text.toString().trim()
                networkCallForUserLoginAuth(requireActivity(), user, "FROM_EMAIL")
/*                VerifyOtpDialogFragment.newInstance(user, "FROM_EMAIL")
                    .show(childFragmentManager, VerifyOtpDialogFragment.TAG)*/
            }

            verifyMobileBtn.setOnClickListener {
                user.mobile = binding.phonenumberTV.text.toString().trim()
                networkCallForUserLoginAuth(requireActivity(), user, "FROM_MOBILE")
/*                VerifyOtpDialogFragment.newInstance(user, "FROM_MOBILE")
                    .show(childFragmentManager, VerifyOtpDialogFragment.TAG)*/
            }
        }
    }

    fun checkValidation() {
        name = Helper.GetText(binding.tietName)
        email = Helper.GetText(binding.tietEmail)
        mobile = Helper.GetText(binding.phonenumberTV)
        var isDataValid = true
        try {
            if (TextUtils.isEmpty(name))
                isDataValid = Helper.DataNotValid(binding.tietName)
            else if (!Helper.isValidEmail(email))
                isDataValid = Helper.DataNotValid(binding.tietEmail, 1)
            else if (TextUtils.isEmpty(mobile))
                isDataValid = Helper.DataNotValid(binding.phonenumberTV)
            else if (binding.ccp.selectedCountryCode == "91" && Helper.isInValidIndianMobile(
                    mobile
                )
            )
                isDataValid = Helper.DataNotValid(phonenumberTV, 2)
            else if (TextUtils.isEmpty(countryId)) {
                Toast.makeText(activity, "Please select country.", Toast.LENGTH_SHORT).show()
                isDataValid = false
            } else if (TextUtils.isEmpty(Helper.GetText(binding.stateTV))) {
                Toast.makeText(activity, "Please select state.", Toast.LENGTH_SHORT).show()
                isDataValid = false
            } else if (countryId == "101" && TextUtils.isEmpty(Helper.GetText(binding.cityTV))) {
                Toast.makeText(activity, "Please select city.", Toast.LENGTH_SHORT).show()
                isDataValid = false
            } else if (damsUserSwitch.isChecked && TextUtils.isEmpty(
                    damsIdET.text.toString().trim()
                )
            ) {
                Toast.makeText(activity, "Please enter your DAMS ID.", Toast.LENGTH_SHORT)
                    .show()
                isDataValid = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (isDataValid) {
            user.name = name
            user.email = email
            user.user_country = Helper.GetText(binding.countryTV)
            user.mobile = mobile
            user.c_code = binding.ccp.selectedCountryCodeWithPlus
            user.u_c_id = countryId
            user.u_s_id = stateId
            if (stateId == "001")
                user.u_ct_id = "-"
            else
                user.u_ct_id = cityId
            if (damsUserSwitch.isChecked)
                user.dams_tokken = binding.damsIdET.text?.trim().toString()
            else
                user.dams_tokken = ""
            var deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)
            if (GenericUtils.isEmpty(deviceId)) {
                deviceId = FirebaseInstanceId.getInstance().token
            }
            user.device_tokken = deviceId
            Log.e(TAG, "state: $stateId city: $cityId")
            /*val parentFragment = (requireActivity() as AuthActivity).getCurrentFragment()
            if (parentFragment is ProfileSubmissionFragment) {
                parentFragment.replaceFragment(AcademicFragment.newInstance(user))
                SharedPreference.getInstance().loggedInUser = user
                parentFragment.changeBackground(Const.ACADEMIC,
                    isContactFilled = true,
                    isAcademicFilled = false,
                    isCourseFilled = false)
            }*/
            if (user.is_social == "1") {
                activity?.let { networkCallForUserLoginAuth(it, user, "FROM_MOBILE") }
            } else {
                networkCallForContactRegistration()
            }
        }
    }

    fun networkCallForContactRegistration() {
        mProgress.show()
        var response: Call<JsonObject?>? = null
        val apis = ApiClient.createService(RegFragApis::class.java)
        //  if (!TextUtils.isEmpty(user.profile_picture)) {
        if (damsUserSwitch.isChecked && !TextUtils.isEmpty(damsIdET.text.toString().trim())) {
            response = apis?.contactRegistration1(
                SharedPreference.getInstance().loggedInUser.id,
                user.name,
                user.email,
                user.mobile,
                user.u_c_id,
                user.u_s_id,
                user.u_ct_id,
                user.dams_tokken,
                user.profile_picture
            )
        } else {
            response = apis?.contactRegistration2(
                SharedPreference.getInstance().loggedInUser.id,
                user.name,
                user.email,
                user.mobile,
                user.u_c_id,
                user.u_s_id,
                user.u_ct_id,
                user.profile_picture
            )
        }
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
                                parentFragment.replaceFragment(AcademicFragment.newInstance(user))
                                parentFragment.changeBackground(
                                    Const.ACADEMIC,
                                    isContactFilled = true,
                                    isAcademicFilled = false,
                                    isCourseFilled = false
                                )
                            }
                        } else {
/*                            errorCallBack(
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                API.API_STREAM_REGISTRATION
                            )*/
                            try {
                                if (!GenericUtils.isEmpty(jsonResponse.getString(Const.AUTH_MESSAGE)))
                                    RetrofitResponse.handleAuthCode(
                                        activity, jsonResponse.optString(Const.AUTH_CODE),
                                        jsonResponse.getString(Const.AUTH_MESSAGE)
                                    )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (!GenericUtils.isEmpty(jsonResponse.optString("auth_code")) && jsonResponse.optString(
                                    "auth_code"
                                ).equals("20202", true)
                            ) {
                                showPopup(
                                    "eMedicoz",
                                    jsonResponse.optString(Constants.Extras.MESSAGE)
                                )
                            }else{
                                activity!!.show(
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
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
                Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 1)
            }
        })
    }

    companion object {
        private const val TAG = "ContactDetailFragment"
        fun newInstance(user: User?): ContactDetailFragment {
            val fragment = ContactDetailFragment()
            val args = Bundle()
            args.putSerializable("user", user)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getAPI(apiType: String?, service: FlashCardApiInterface): Call<JsonObject?>? {
        val params: MutableMap<String, Any> = HashMap()
        when (apiType) {
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
        etSearch = searchDialog?.findViewById(R.id.et_search)
        val tvTitle: TextView = searchDialog?.findViewById(R.id.tv_title)!!
        if (searchType.equals(Const.COUNTRY, ignoreCase = true)) {
            tvTitle.text = "Select Country"
            etSearch?.hint = "Search Country"
        } else if (searchType.equals(Const.STATE, ignoreCase = true)) {
            tvTitle.text = "Select State"
            etSearch?.hint = "Search State"
        } else if (searchType.equals(Const.CITY, ignoreCase = true)) {
            tvTitle.text = "Select City"
            etSearch?.hint = "Search City"
        } else if (searchType.equals(Const.COLLEGE, ignoreCase = true)) {
            tvTitle.text = "Select College"
            etSearch?.hint = "Search College"
        }
        ivClearSearch = searchDialog?.findViewById(R.id.iv_clear_search)
        tvCancel = searchDialog?.findViewById(R.id.tv_cancel)
        ivClearSearch?.setOnClickListener {
            etSearch?.setText(
                ""
            )
        }
        tvCancel?.setOnClickListener { searchDialog?.cancel() }
        searchRecyclerview = searchDialog?.findViewById(R.id.search_recyclerview)
        searchRecyclerview?.setHasFixedSize(true)
        searchRecyclerview?.layoutManager = LinearLayoutManager(context)
        countryStateCity =
            CountryStateCity(activity, this@ContactDetailFragment, searchType, stateArrayList)
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

    fun getCountryData(mType: String?, countryId: String, name: String) {
        Log.e(TAG, "getCountryData: $countryId")
        this.countryId = countryId
        stateId = ""
        cityId = ""
        collegeId = ""
        collegeName = ""
        binding.cityTV.setText("")
        //collegeTV.setText("")
        if (countryId == "101") {
            binding.stateTV.setText("")
            binding.cityTV.setVisibility(View.VISIBLE)
        } else {
            stateId = "001"
            stateName = "Foreign Medical Graduates"
            binding.stateTV.setText("Foreign Medical Graduates")
            binding.cityTV.setVisibility(View.GONE)
        }
        countryName = name
        binding.countryTV.setText(name)
    }

    fun getStateData(mType: String?, stateId: String, name: String) {
        Log.e(TAG, "getStateData: $stateId")
        this.stateId = stateId
        cityId = ""
        cityName = ""
        collegeId = ""
        collegeName = ""
        binding.cityTV.text = ""
        //collegeTV.setText("")
        if (stateId == "001") {
            countryId = ""
            binding.countryTV.text = ""
            binding.cityTV.visibility = View.GONE
            //collegeTV.setVisibility(View.GONE)
        } else {
            binding.cityTV.visibility = View.VISIBLE
            //collegeTV.setVisibility(View.VISIBLE)
        }
        stateName = name
        binding.stateTV.text = name
    }

    fun getCityData(mType: String?, stateId: String, cityId: String, name: String) {
        Log.e(TAG, "getCityData: $stateId --- $cityId")
        this.stateId = stateId
        this.cityId = cityId
        collegeId = ""
        //collegeTV.setText("")
        cityName = name
        binding.cityTV.setText(name)
    }

    fun getCollegeData(
        mType: String?,
        stateId: String,
        cityId: String,
        collegeId: String,
        name: String
    ) {
        Log.e(TAG, "getCollegeData: $stateId --- $cityId --- $collegeId")
        this.stateId = stateId
        this.cityId = cityId
        this.collegeId = collegeId
        collegeName = name
        // collegeTV.setText(name)
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

    override fun imagePath(str: String?) {
        if (str != null) {
            isDataChanged = true
            bitmap = BitmapFactory.decodeFile(str)
            if (bitmap != null) {
                binding.uploadPicIV.setImageBitmap(bitmap)
                isPictureChanged = true
                binding.uploadPicIV.visibility = View.VISIBLE
                mediaFile = ArrayList<MediaFile>()
                val mf = MediaFile()
                mf.file_type = Const.IMAGE
                mf.image = bitmap
                mediaFile?.add(mf)
                s3ImageUploading = s3ImageUploading(
                    Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES,
                    activity,
                    this,
                    null
                )
                s3ImageUploading?.execute(mediaFile)
            }
        }
    }

    override fun onS3UploadData(images: ArrayList<MediaFile>?) {
        if (images!!.isNotEmpty()) {
            var pic = images[0].file
            if (TextUtils.isEmpty(pic)) {
                pic =
                    "https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png"
                //                user.setProfile_picture("https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png");
            } else {
                user.profile_picture = pic
                Glide.with(requireContext()).load(pic).into(binding.uploadPicIV)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (takeImageClass != null)
            takeImageClass?.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: ")
    }

    fun networkCallForUserLoginAuth(activity: Activity, user: User, type: String) {
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
        var response: Call<JsonObject>? = null
        if (damsUserSwitch.isChecked && !TextUtils.isEmpty(damsIdET.text.toString().trim())) {
            response = apiInterface.userLoginAuthenticationV5(
                binding.tietEmail.text.toString().trim(),
                binding.phonenumberTV.text.toString().trim(),
                binding.ccp.selectedCountryCodeWithPlus,
                "",
                user.is_social,
                user.social_type,
                user.social_tokken,
                user.device_type,
                user.device_tokken,
                user.dams_tokken
            )
        } else {
            response = apiInterface.userLoginAuthenticationV4(
                binding.tietEmail.text.toString().trim(),
                binding.phonenumberTV.text.toString().trim(),
                binding.ccp.selectedCountryCodeWithPlus,
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
                            VerifyOtpDialogFragment.newInstance(user, type)
                                .show(childFragmentManager, VerifyOtpDialogFragment.TAG)
                            activity.show(
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            )
                        } else {
                            try {
                                if (!GenericUtils.isEmpty(jsonResponse.getString(Const.AUTH_MESSAGE)))
                                    RetrofitResponse.handleAuthCode(
                                        activity, jsonResponse.optString(Const.AUTH_CODE),
                                        jsonResponse.getString(Const.AUTH_MESSAGE)
                                    )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (!GenericUtils.isEmpty(jsonResponse.optString("auth_code")) && jsonResponse.optString(
                                    "auth_code"
                                ).equals("20202", true)
                            ) {
                                showPopup(
                                    "eMedicoz",
                                    jsonResponse.optString(Constants.Extras.MESSAGE)
                                )
                            }else{
                                activity.show(
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                                )
                            }
                            /* SharedPreference.getInstance()
                                 .putBoolean(Const.IS_NOTIFICATION_BLOCKED, false)*/
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

    fun setIsEmailVerified(isVerified: Boolean) {
        isEmailVerified = isVerified
        binding.verifyEmailBtn.visibility = View.GONE
        binding.tietEmail.isEnabled = false
    }

    fun setIsMobileVerified(isVerified: Boolean) {
        isMobileVerified = isVerified
        binding.verifyMobileBtn.visibility = View.GONE
        binding.phonenumberTV.isEnabled = false
        binding.ccp.isEnabled = false
    }

    private fun showPopup(title: String, message: String) {
        val v = Helper.newCustomDialog(requireContext(), false, title, message)
        val btnCancel: Button = v.findViewById(R.id.btn_cancel)
        val btnSubmit: Button = v.findViewById(R.id.btn_submit)
        btnCancel.text = getString(R.string.no)
        btnSubmit.text = getString(R.string.ok)
        btnCancel.visibility = View.GONE
        btnSubmit.setOnClickListener { Helper.dismissDialog() }
    }

}