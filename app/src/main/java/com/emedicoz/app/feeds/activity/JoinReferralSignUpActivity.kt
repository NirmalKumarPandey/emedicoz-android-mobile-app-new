package com.emedicoz.app.feeds.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.common.BaseABNavActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.customviews.imagecropper.TakeImageClass
import com.emedicoz.app.customviews.imagecropper.TakeImageClass.ImageFromCropper
import com.emedicoz.app.databinding.ActivityAppSettingBinding
import com.emedicoz.app.databinding.ActivityJoinReferralSignUpBinding
import com.emedicoz.app.modelo.MediaFile
import com.emedicoz.app.referralcode.ReferEarnNowFragment
import com.emedicoz.app.referralcode.model.*
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.ReferralApiInterface
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rilixtech.Country
import com.rilixtech.CountryCodePicker
import com.rilixtech.CountryCodePicker.OnCountryChangeListener
import kotlinx.android.synthetic.main.header_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern

class JoinReferralSignUpActivity : AppCompatActivity(), View.OnClickListener, AmazonCallBack, ImageFromCropper {

    var mProgress: Progress? = null
    lateinit var firstNameET: EditText
    lateinit var lastNameET: EditText
    lateinit var emailAddressET: EditText
    lateinit var phoneNumberET: EditText
    lateinit var addressET: EditText
    lateinit var postalCodeET: EditText
    lateinit var cityET: EditText
    lateinit var stateET: EditText
    lateinit var accountHolderNameET: EditText
    lateinit var accountNumberET: EditText
    lateinit var bankNameET: EditText
    lateinit var bankBranchNameET: EditText
    lateinit var ifscCodeET: EditText
    lateinit var panCardET: EditText
    lateinit var adharCardET: EditText
    lateinit var signMeUpBtn: Button
    lateinit var updateInfo: Button
    var firstName: String? = null
    var lastName: String? = null
    var emailAddress: String? = null
    var phoneNumber: String? = null
    var address: String? = null
    var postal: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var accountHolderName: String? = null
    var bankName: String? = null
    var bankBranchName: String? = null
    var accountNumber: String? = null
    var ifscCode: String? = null
    var bankInfoId: String? = null
    var referralCode: String? = null
    var deviceId = ""
    var c_code: String? = null
    var panCard: String? = null
    var adharCard: String? = null
    var instructorName: String? = null
    var adharImage: String? = null
    var panImage: String? = null
    var bankDocumentImage: String? = null
    lateinit var ccp: CountryCodePicker
    lateinit var termCondId: TextView
    lateinit var checkBoxId: CheckBox
    private lateinit var countryET: Spinner
    var imageUploadType: String? = null
    lateinit var firstNameTIL: TextInputLayout
    lateinit var lastNameTIL: TextInputLayout
    lateinit var emailAddressTIL: TextInputLayout
    lateinit var phoneNumberTIL: TextInputLayout
    lateinit var accountHolderNameTIL: TextInputLayout
    lateinit var bankNameTIL: TextInputLayout
    lateinit var accountNumberTIL: TextInputLayout
    lateinit var ifscCodeTIL: TextInputLayout
    var list: List<String>? = null
    var dialog: Dialog? = null
    var profileInfoResponse: ProfileInfoResponse? = null
    var bankInfoResponse: BankInfoResponse? = null
    var bankInfo: List<BankInfo>? = null
    var profileTypeResponse: ProfileTypeResponse? = null
    private lateinit var instructorNameSpinner: AppCompatSpinner
    private var takeImageClass: TakeImageClass? = null
    lateinit var privacyPolicyTV: TextView
    lateinit var agreementTV:TextView
    private var s3ImageUploading: s3ImageUploading? = null
    var pattern: Pattern? = null
    private lateinit var imgBankDoc: ImageView
    private lateinit var imgPanCard:ImageView
    private lateinit var imgAdharCard:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_referral_sign_up)
        mProgress = Progress(this)
        mProgress!!.setCancelable(false)
        tvHeaderName.text = "Referral SignUp"

        initViews()
        takeImageClass = TakeImageClass(this, this)
        Log.e("register", "deviceId: $deviceId")
        val affiliateId = SharedPreference.getInstance().loggedInUser.affiliate_user_id
        if (affiliateId != null && !affiliateId.isEmpty()) {
            networkCallForBankId()
            profileInfoResponse = SharedPreference.getInstance().affiliateProfileInfo
            signMeUpBtn!!.visibility = View.GONE
            updateInfo!!.visibility = View.VISIBLE
            setData()
        } else setDefaultData()
        networkCallForProfileTypeList()
    }

    private fun setDefaultData() {
        phoneNumberET!!.setText(SharedPreference.getInstance().loggedInUser.mobile)
        emailAddressET!!.setText(SharedPreference.getInstance().loggedInUser.email)
    }

    fun initViews() {
        firstNameET = findViewById(R.id.firstNameET)
        lastNameET = findViewById(R.id.lastNameET)
        emailAddressET = findViewById(R.id.emailAddressET)
        phoneNumberET = findViewById(R.id.phoneNumberET)
        addressET = findViewById(R.id.addressET)
        postalCodeET = findViewById(R.id.postalCodeET)
        cityET = findViewById(R.id.cityET)
        stateET = findViewById(R.id.stateET)
        accountHolderNameET = findViewById(R.id.accountHolderNameET)
        bankNameET = findViewById(R.id.bankNameET)
        bankBranchNameET = findViewById(R.id.bankBranchNameET)
        accountNumberET = findViewById(R.id.accountNumberET)
        updateInfo = findViewById(R.id.updateInfo)
        updateInfo.setOnClickListener(this)
        ifscCodeET = findViewById(R.id.ifscCodeET)
        signMeUpBtn = findViewById(R.id.signMeUpBtn)
        countryET = findViewById(R.id.countryET)
        instructorNameSpinner = findViewById(R.id.instructorNameSpinner)
        firstNameTIL = findViewById(R.id.firstNameTIL)
        lastNameTIL = findViewById(R.id.lastNameTIL)
        emailAddressTIL = findViewById(R.id.emailAddressTIL)
        phoneNumberTIL = findViewById(R.id.phoneNumberTIL)
        accountHolderNameTIL = findViewById(R.id.accountHolderNameTIL)
        bankNameTIL = findViewById(R.id.bankNameTIL)
        accountNumberTIL = findViewById(R.id.accountNumberTIL)
        ifscCodeTIL = findViewById(R.id.ifscCodeTIL)
        termCondId = findViewById(R.id.termCondId)
        termCondId.setOnClickListener(this)
        privacyPolicyTV = findViewById(R.id.privacyPolicyTV)
        privacyPolicyTV.setOnClickListener(this)
        agreementTV = findViewById(R.id.agreementTV)
        agreementTV.setOnClickListener(this)
        panCardET = findViewById(R.id.panCardET)
        adharCardET = findViewById(R.id.aadhaarCardET)
        imgAdharCard = findViewById(R.id.imgAadharCard)
        imgPanCard = findViewById(R.id.imgPanCard)
        imgBankDoc = findViewById(R.id.imgBankDoc)
        checkBoxId = findViewById(R.id.checkBoxId)
        imgPanCard.setOnClickListener(View.OnClickListener { v: View? ->
            imageUploadType = "pancard"
            takeImageClass!!.getImagePickerDialog(this, getString(R.string.upload_pan_picture), getString(R.string.choose_image))
        })
        imgAdharCard.setOnClickListener(View.OnClickListener { v: View? ->
            imageUploadType = "aadharcard"
            takeImageClass!!.getImagePickerDialog(this, getString(R.string.upload_aadhar_picture), getString(R.string.choose_image))
        })
        imgBankDoc.setOnClickListener(View.OnClickListener { v: View? ->
            imageUploadType = "bankdocument"
            takeImageClass!!.getImagePickerDialog(this, getString(R.string.upload_bandoc_picture), getString(R.string.choose_image))
        })

        ibBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        //termCondId.setOnClickListener(this);
        signMeUpBtn.setOnClickListener(this)
        ccp = findViewById(R.id.ccp)
        ccp.enableHint(false)
        ccp.registerPhoneNumberTextView(phoneNumberET)
        ccp.setOnCountryChangeListener(OnCountryChangeListener { country: Country? ->
            if (phoneNumberET.getError() != null) {
                phoneNumberET.setError(null)
            }
        })
    }

    fun profileTypeData() {
        val profileTypeList = profileTypeResponse!!.data.profileTypeList
        val list: MutableList<String> = ArrayList()
        for (i in profileTypeList.indices) {
            list.add(profileTypeList[i].title)
        }
        val adapter = ArrayAdapter(this@JoinReferralSignUpActivity, R.layout.single_row_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        instructorNameSpinner.adapter = adapter
        if (profileInfoResponse != null) {
            val compareValue = profileInfoResponse!!.getData().getProfileInfo().profileType
            if (compareValue != null) {
                val spinnerPosition = adapter.getPosition(compareValue)
                instructorNameSpinner.setSelection(spinnerPosition)
            }
        }
    }
    override fun onClick(view: View?) {
        when (view!!.getId()) {
            R.id.signMeUpBtn -> {
                ccp.isValid
                if (checkValidation()) {
                    if (TextUtils.isEmpty(deviceId)) {
                        deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)
                        if (TextUtils.isEmpty(deviceId)) {
                            deviceId = FirebaseInstanceId.getInstance().token!!
                        }
                    }
                    networkCallForReferralApi()
                }
            }
            R.id.termCondId -> Helper.GoToWebViewActivity(this, Const.TERMS_URL)
            R.id.updateInfo -> if (checkValidation()) networkCallForUpdateAffiliateInfo()
            R.id.privacyPolicyTV -> Helper.GoToWebViewActivity(this, Const.PRIVACY_URL)
            R.id.agreementTV -> Helper.GoToWebViewActivity(this, Const.AGREEMENT_URL)
        }
    }

    fun setData() {
        if (profileInfoResponse == null || profileInfoResponse!!.getData() == null) return
        firstName = profileInfoResponse!!.getData().getProfileInfo().firstName
        lastName = profileInfoResponse!!.getData().getProfileInfo().lastName
        firstNameET.setText(firstName)
        lastNameET.setText(lastName)
        emailAddress = profileInfoResponse!!.getData().getProfileInfo().email
        emailAddressET.setText(emailAddress)
        phoneNumber = profileInfoResponse!!.getData().getProfileInfo().phone
        phoneNumberET.setText(phoneNumber)
        address = profileInfoResponse!!.getData().getProfileInfo().address
        addressET.setText(address)
        postal = profileInfoResponse!!.getData().getProfileInfo().postalCode
        postalCodeET.setText(postal)
        city = profileInfoResponse!!.getData().getProfileInfo().city
        cityET.setText(city)
        state = profileInfoResponse!!.getData().getProfileInfo().state
        stateET.setText(state)
        panImage = profileInfoResponse!!.getData().getProfileInfo().panImage
        adharImage = profileInfoResponse!!.getData().getProfileInfo().aadharImage
        bankDocumentImage = profileInfoResponse!!.getData().getProfileInfo().bankImage
        panCard = profileInfoResponse!!.getData().getProfileInfo().pancard
        panCardET.setText(panCard)
        adharCard = profileInfoResponse!!.getData().getProfileInfo().aadhar
        adharCardET.setText(adharCard)
        Glide.with(this@JoinReferralSignUpActivity)
                .load(profileInfoResponse!!.getData().getProfileInfo().panImage)
                .into(imgPanCard)
        Glide.with(this@JoinReferralSignUpActivity)
                .load(profileInfoResponse!!.getData().getProfileInfo().aadharImage)
                .into(imgAdharCard)
        Glide.with(this@JoinReferralSignUpActivity)
                .load(profileInfoResponse!!.getData().getProfileInfo().bankImage)
                .into(imgBankDoc)
    }

    fun setBankData() {
        accountHolderName = bankInfoResponse!!.getData()[0].getAccountHolderName()
        bankName = bankInfoResponse!!.getData()[0].getBankName()
        bankBranchName = bankInfoResponse!!.getData()[0].getBankBranchName()
        accountNumber = bankInfoResponse!!.getData()[0].getAccountNumber()
        ifscCode = bankInfoResponse!!.getData()[0].getIfscCode()
        bankInfoId = bankInfoResponse!!.getData()[0].getId()
        accountHolderNameET.setText(accountHolderName)
        bankNameET.setText(bankName)
        bankBranchNameET.setText(bankBranchName)
        accountNumberET.setText(accountNumber)
        ifscCodeET.setText(ifscCode)
    }

    fun checkValidation(): Boolean {
        firstName = Helper.GetText(firstNameET)
        lastName = Helper.GetText(lastNameET)
        emailAddress = Helper.GetText(emailAddressET)
        phoneNumber = Helper.GetText(phoneNumberET)
        c_code = ccp.selectedCountryCodeWithPlus
        panCard = Helper.GetText(panCardET)
        adharCard = Helper.GetText(adharCardET)
        address = Helper.GetText(addressET)
        postal = Helper.GetText(postalCodeET)
        city = Helper.GetText(cityET)
        state = Helper.GetText(stateET)
        accountHolderName = Helper.GetText(accountHolderNameET)
        bankName = Helper.GetText(bankNameET)
        bankBranchName = Helper.GetText(bankBranchNameET)
        accountNumber = Helper.GetText(accountNumberET)
        country = countryET.selectedItem.toString()
        instructorName = instructorNameSpinner.selectedItem.toString()
        ifscCode = Helper.GetText(ifscCodeET)
        var isDataValid = true
        pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
        val matcher = pattern!!.matcher(panCard)
        if (TextUtils.isEmpty(instructorName)) isDataValid = Helper.showErrorToast(instructorNameSpinner, "Select Instructor Name") else if (TextUtils.isEmpty(firstName)) isDataValid = Helper.DataNotValid(firstNameET) else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) isDataValid = Helper.DataNotValid(emailAddressET, 1) else if (TextUtils.isEmpty(phoneNumber)) isDataValid = Helper.DataNotValid(phoneNumberET) else if (ccp.selectedCountryCode == "91" &&
                Helper.isInValidIndianMobile(phoneNumberET.text.toString())) isDataValid = Helper.DataNotValid(phoneNumberET, 2) else if (TextUtils.isEmpty(panCard)) {
            isDataValid = Helper.DataNotValid(panCardET)
        } else if (!matcher.matches()) {
            isDataValid = Helper.showErrorToast(panCardET, "PAN no is invalid")
        } else if (TextUtils.isEmpty(panImage)) isDataValid = Helper.showErrorToast(panCardET, "PAN image is required") else if (TextUtils.isEmpty(adharCard)) {
            isDataValid = Helper.DataNotValid(adharCardET)
        } else if (adharCard!!.length < 12) {
            isDataValid = Helper.showErrorToast(adharCardET, "Aadhar number should be of 12 digits..")
        } else if (TextUtils.isEmpty(adharImage)) isDataValid = Helper.showErrorToast(adharCardET, "Aadhar image is required") else if (TextUtils.isEmpty(accountHolderName)) isDataValid = Helper.DataNotValid(accountHolderNameET) else if (TextUtils.isEmpty(bankName)) isDataValid = Helper.DataNotValid(bankNameET) else if (TextUtils.isEmpty(accountNumber)) isDataValid = Helper.DataNotValid(accountNumberET) else if (TextUtils.isEmpty(ifscCode)) isDataValid = Helper.DataNotValid(ifscCodeET) else if (TextUtils.isEmpty(bankDocumentImage)) isDataValid = Helper.showErrorToast(imgBankDoc, "Bank document image is required") else if (!checkBoxId.isChecked) isDataValid = Helper.showErrorToast(checkBoxId, "Please check terms and condition")
        return isDataValid
    }

    private fun networkCallForReferralApi() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.getReferralSignUpMe(SharedPreference.getInstance().loggedInUser.id, firstName, lastName, emailAddress, phoneNumber, panCard, adharCard, address, postal, country, state, city, accountHolderName, bankName, bankBranchName, accountNumber, ifscCode,
                panImage, adharImage, bankDocumentImage, instructorName)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val refSignUpResponse = Gson().fromJson(jsonObject, RefSignUpResponse::class.java)
                            val refData = refSignUpResponse.data
//                            val referEarnNowFragment = ReferEarnNowFragment()
//                            val args = Bundle()
//                            args.putString("AFFILIATE_USER_ID", refData.id)
                            val user = SharedPreference.getInstance().loggedInUser
                            user.affiliate_user_id = refData.id
                            SharedPreference.getInstance().loggedInUser = user
                            val intent = Intent(this@JoinReferralSignUpActivity, ReferEarnNowActivity::class.java)
                            intent.putExtra("AFFILIATE_USER_ID", refData.id)
                            startActivity(intent)
//                            referEarnNowFragment.arguments = args
//                            (activity as BaseABNavActivity).toolbarTitleTV.text = "Refer and Earn Now"
//                            val fragmentManager: FragmentManager = requireActivity().getSupportFragmentManager()
//                            val fragmentTransaction = fragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.fragment_container, referEarnNowFragment)
//                            fragmentTransaction.addToBackStack(null)
//                            fragmentTransaction.commit()
                            Toast.makeText(this@JoinReferralSignUpActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@JoinReferralSignUpActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@JoinReferralSignUpActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@JoinReferralSignUpActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun networkCallForUpdateAffiliateInfo() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.updateAffProfileInfoWithBank(SharedPreference.getInstance().loggedInUser.affiliate_user_id, bankInfoId, firstName, lastName, emailAddress, phoneNumber, panCard, adharCard, address, postal, country, state, city, accountHolderName, bankName, bankBranchName, accountNumber, ifscCode,
                panImage, adharImage, bankDocumentImage, instructorName)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val profileInfoBankUpdateResponse = Gson().fromJson(jsonObject, ProfileInfoBankUpdateResponse::class.java)
                            val profileInfoBankUpdateData = profileInfoBankUpdateResponse.data
                            val intent = Intent(this@JoinReferralSignUpActivity, ReferEarnNowActivity::class.java)
                            intent.putExtra("AFFILIATE_USER_ID", profileInfoBankUpdateData.affiliateUserId)
                            startActivity(intent)
//
//                            val referEarnNowFragment = ReferEarnNowFragment()
//                            val args = Bundle()
//                            args.putString("AFFILIATE_USER_ID", profileInfoBankUpdateData.affiliateUserId)
//                            referEarnNowFragment.arguments = args
//                            (activity as BaseABNavActivity).toolbarTitleTV.text = "Refer and Earn Now"
//                            val fragmentManager: FragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager()
//                            val fragmentTransaction = fragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.fragment_container, referEarnNowFragment)
//                            fragmentTransaction.addToBackStack(null)
//                            fragmentTransaction.commit()
                            Toast.makeText(this@JoinReferralSignUpActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@JoinReferralSignUpActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@JoinReferralSignUpActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@JoinReferralSignUpActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun networkCallForBankId() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.getAffiliateUserBankInfo(SharedPreference.getInstance().loggedInUser.affiliate_user_id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            bankInfoResponse = Gson().fromJson(jsonObject, BankInfoResponse::class.java)
                            bankInfo = bankInfoResponse!!.getData()
                            setBankData()
                        } else {
                            Toast.makeText(this@JoinReferralSignUpActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@JoinReferralSignUpActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@JoinReferralSignUpActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun networkCallForProfileTypeList() {
        mProgress!!.show()
        val apiInterface = ApiClient.createService(ReferralApiInterface::class.java)
        val response = apiInterface.profileTypeList
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        mProgress!!.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            profileTypeResponse = Gson().fromJson(jsonObject, ProfileTypeResponse::class.java)
                            profileTypeData()
                        } else {
                            Toast.makeText(this@JoinReferralSignUpActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@JoinReferralSignUpActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Toast.makeText(this@JoinReferralSignUpActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (takeImageClass != null) takeImageClass!!.onActivityResult(requestCode, resultCode, data)
    }
    override fun onS3UploadData(images: ArrayList<MediaFile>?) {
        if (!images!!.isEmpty()) {
            var picUrl = images[0].file
            if (TextUtils.isEmpty(picUrl)) {
                picUrl = "https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png"
            } else {
                when (imageUploadType) {
                    "pancard" -> panImage = picUrl
                    "aadharcard" -> adharImage = picUrl
                    "bankdocument" -> bankDocumentImage = picUrl
                }
            }
        }
    }

    override fun imagePath(str: String?) {
        if (str != null) {
            val bitmap = BitmapFactory.decodeFile(str)
            if (bitmap != null) {
                when (imageUploadType) {
                    "pancard" -> imgPanCard.setImageBitmap(bitmap)
                    "aadharcard" -> imgAdharCard.setImageBitmap(bitmap)
                    "bankdocument" -> imgBankDoc.setImageBitmap(bitmap)
                }
                val mediaFile = ArrayList<MediaFile>()
                val mf = MediaFile()
                mf.file_type = Const.IMAGE
                mf.image = bitmap
                mediaFile.add(mf)
                s3ImageUploading = s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES, this, this, null)
                s3ImageUploading!!.execute(mediaFile)
            }
        }
    }
}