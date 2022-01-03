package com.emedicoz.app.feeds.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.customviews.imagecropper.TakeImageClass
import com.emedicoz.app.customviews.imagecropper.TakeImageClass.ImageFromCropper
import com.emedicoz.app.modelo.HelpQuery
import com.emedicoz.app.modelo.MediaFile
import com.emedicoz.app.modelo.PostFile
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.SubmitQueryApiInterface
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FeedbackActivity : AppCompatActivity(), ImageFromCropper, AmazonCallBack {
    lateinit var titleET: EditText
    lateinit var descriptionET:EditText
    lateinit var submitbtn: Button
    lateinit var imageIV: ImageView
    lateinit var deleteIV: ImageView
    lateinit var imageRL: RelativeLayout

    var selectionoptionLL: LinearLayout? = null
    var substreamViewList: ArrayList<RadioButton>? = null
    var optionRG: RadioGroup? = null
    var selectionList = arrayOf("General", "Installation", "LMS issue", "Others", "None")
    var title: String? = null
    var description:String? = null
    var selectedoption = ""
    lateinit var helpQuery: HelpQuery
    var mediaFile: MediaFile? = null
    var postFile: PostFile? = null
    var isImageAdded = false
    var bitmap: Bitmap? = null
    var s3ImageUploading: s3ImageUploading? = null
    var mprogress: Progress? = null
    private lateinit var addimageLL: LinearLayout
    private var takeImageClass: TakeImageClass? = null
    private lateinit var tvHeaderName: TextView
    private lateinit var ibBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        mprogress = Progress(this)
        mprogress!!.setCancelable(false)

        initView()
    }

    private fun initView(){
        titleET = findViewById(R.id.addtitleET)
        descriptionET = findViewById(R.id.adddescriptionET)
        addimageLL = findViewById(R.id.addimageLL)
        submitbtn = findViewById(R.id.submitBtn)
        imageIV = findViewById(R.id.imageIV)
        deleteIV = findViewById(R.id.deleteIV)
        ibBack = findViewById(R.id.ibBack)
        Helper.CaptializeFirstLetter(titleET)
        Helper.CaptializeFirstLetter(descriptionET)
        selectionoptionLL = findViewById(R.id.selectionoptionLL)
        imageRL = findViewById(R.id.imageRL)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        tvHeaderName.text = "Feedback"

        optionRG = findViewById(R.id.optionRG)
        postFile = PostFile()
        mediaFile = MediaFile()

        addViewtoSelectionOpt()

        deleteIV.setOnClickListener { view1: View? ->
            mediaFile = MediaFile()
            imageRL.visibility = View.GONE
        }

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        takeImageClass = TakeImageClass(this@FeedbackActivity, this)

        submitbtn.setOnClickListener { view12: View? -> checkValidation() }
        addimageLL.setOnClickListener(View.OnClickListener { v: View? -> takeImageClass!!.getImagePickerDialog(this, "Upload Profile Picture", getString(R.string.choose_image)) })
    }

    fun checkValidation() {
        val radioButtonID = optionRG!!.checkedRadioButtonId
        val radioButton: RadioButton = optionRG!!.findViewById(radioButtonID)
        selectedoption = radioButton.text as String
        title = Helper.GetText(titleET)
        description = Helper.GetText(descriptionET)
        var isDataValid = true
        if (TextUtils.isEmpty(title)) isDataValid = Helper.DataNotValid(titleET) else if (TextUtils.isEmpty(description)) isDataValid = Helper.DataNotValid(descriptionET)
        if (isDataValid) {
            if (selectedoption != "") {
                helpQuery = HelpQuery.newInstance()
                helpQuery.setUser_id(SharedPreference.getInstance().loggedInUser.id)
                helpQuery.setCategory(selectedoption)
                helpQuery.setTitle(title)
                helpQuery.setDescription(description)
                if (isImageAdded) {
                    val MFArray = ArrayList<MediaFile>()
                    MFArray.add(mediaFile!!)
                    s3ImageUploading = s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_FEEDBACK, this, this, null)
                    s3ImageUploading!!.execute(MFArray)
                } else networkCallForSubmitQuery() //NetworkAPICall(API.API_SUBMIT_QUERY, true);
            } else Toast.makeText(this, R.string.kindly_select_any_on_option_above, Toast.LENGTH_SHORT).show()
        }
    }

    fun networkCallForSubmitQuery() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        mprogress!!.show()
        val apiInterface = ApiClient.createService(SubmitQueryApiInterface::class.java)
        val response = apiInterface.getresponseofsubmitquery(helpQuery.user_id, helpQuery.title,
                helpQuery.description, helpQuery.category, postFile!!.link)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mprogress!!.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Toast.makeText(this@FeedbackActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            clearViews()
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(this@FeedbackActivity.getResources().getString(R.string.internet_error_message), ignoreCase = true)) Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, this@FeedbackActivity, 1, 1)
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(getString(R.string.something_went_wrong_string))) Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, this@FeedbackActivity, 1, 2)
                            RetrofitResponse.getApiData(this@FeedbackActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mprogress!!.dismiss()
                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, this@FeedbackActivity, 1, 2)
            }
        })
    }

    fun clearViews() {
        titleET.setText("")
        descriptionET.setText("")
        description = ""
        title = description
        selectedoption = ""
        imageRL.visibility = View.GONE
        imageIV.setImageResource(R.drawable.splashbg)
        //(activity as BaseABNavActivity).coursesLL.performClick()
    }

    fun addViewtoSelectionOpt() {
        substreamViewList = ArrayList()
        optionRG!!.removeAllViews()
        Log.e("My RG", optionRG!!.childCount.toString())
        var i = 0
        while (i < selectionList.size) {
            val rb = RadioButton(this)
            rb.text = selectionList[i]
            rb.tag = selectionList[i]
            optionRG!!.addView(rb)
            substreamViewList!!.add(rb)
            i++
            if (i == selectionList.size) {
                rb.isChecked = true
                selectedoption = rb.text as String
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeImageClass!!.onActivityResult(requestCode, resultCode, data)
    }

    fun initImageView(image: MediaFile) {
        imageIV.setImageBitmap(image.image)
        imageIV.scaleType = ImageView.ScaleType.CENTER_CROP
        imageRL.visibility = View.VISIBLE
    }

    override fun imagePath(str: String?) {
        if (str != null) {
            bitmap = BitmapFactory.decodeFile(str)
            if (bitmap != null) {
                isImageAdded = true
                mediaFile = MediaFile()
                mediaFile!!.file_type = Const.IMAGE
                mediaFile!!.image = bitmap
                mediaFile!!.file = str
                initImageView(mediaFile!!)
            }
        }
    }

    override fun onS3UploadData(images: ArrayList<MediaFile>?) {
        if (!images!!.isEmpty()) {
            for (media in images) {
                postFile = PostFile()
                postFile!!.link = media.file
                Log.e("Tag", Gson().toJson(postFile))
                networkCallForSubmitQuery() //NetworkAPICall(API.API_SUBMIT_QUERY, true);
            }
        }
    }
}