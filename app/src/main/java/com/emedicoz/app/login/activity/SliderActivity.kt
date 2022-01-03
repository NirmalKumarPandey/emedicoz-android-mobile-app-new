package com.emedicoz.app.login.activity

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.login.adapter.ViewPagerAdapter
import com.emedicoz.app.modelo.HelpAndSupport
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.templateAdapters.HelpAndSupportAdapter
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class SliderActivity : AppCompatActivity() {
    private lateinit var btnLoginReg: Button
    private lateinit var tvTermCond: TextView

    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    val REQUEST_READ_PHONE_STATE = 100
    val REQUEST_READ_PHONE_STATE1 = 101
    var popupWindow: PopupWindow? = null
    var crossHelp: ImageView? = null
    var recyclerView_help: RecyclerView? = null
    var editTextHelp: EditText? = null
    var questionQuery: String? = null
    var helpImageCross: ImageView? = null
    var helpImageSearch: ImageView? = null
    var btnStartChat: LinearLayout? = null
    var startChat: Button? = null
    private lateinit var mProgress: Progress
    private var helpAndSupportArrayList: ArrayList<HelpAndSupport>? = null
    var helpView:LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        setContentView(R.layout.activity_slider)
        mProgress = Progress(this)
        mProgress.setCancelable(false)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        viewPager = findViewById(R.id.viewPager)
        btnLoginReg = findViewById(R.id.btnLoginReg)
        tvTermCond = findViewById(R.id.tvTermCond)
        helpView = findViewById(R.id.helpView)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        dotsIndicator.setViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        tvTermCond.setOnClickListener(View.OnClickListener {
            Helper.GoToWebViewActivity(this@SliderActivity, Const.TERMS_URL)
        })

        btnLoginReg.setOnClickListener{
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
/*            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()*/
        }

       //setSpannable()

        val permissionCheck1 =
            ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") //

        val permissionCheck2 =
            ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE")
        val permissionCheck3 = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA")
        val permissionCheck4 =
            ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO")
        val permissionCheck7 =
            ContextCompat.checkSelfPermission(this, "android.permission.READ_PROFILE")

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED || permissionCheck3 != PackageManager.PERMISSION_GRANTED || permissionCheck4 != PackageManager.PERMISSION_GRANTED || permissionCheck7 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.CAMERA",
                    "android.permission.RECORD_AUDIO",
                    "android.permission.READ_PROFILE"
                ), REQUEST_READ_PHONE_STATE
            )
        }

    }

    fun setSpannable() {
        val string: String = resources.getString(R.string.by_login_text)
        val ss = SpannableString(string)
        val span: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Helper.GoToWebViewActivity(this@SliderActivity, Const.TERMS_URL)
            }
        }
        ss.setSpan(ForegroundColorSpan(resources.getColor(R.color.sky_blue)), 25, string.length, 0)
        ss.setSpan(span, 25, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvTermCond.text = ss
        tvTermCond.movementMethod = LinkMovementMethod.getInstance();
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            REQUEST_READ_PHONE_STATE1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else -> {
            }
        }
    }

    fun initHelpAndSupport() {
        val popupView = layoutInflater.inflate(R.layout.fragment_help_and_support, null)
        popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow?.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        popupWindow?.showAsDropDown(helpView)
        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
        crossHelp = popupWindow?.contentView?.findViewById(R.id.crossHelp)
        recyclerView_help = popupWindow?.contentView?.findViewById(R.id.recyclerView_help)
        editTextHelp = popupWindow?.contentView?.findViewById(R.id.helpSearchFilter)
        helpImageCross = popupWindow?.contentView?.findViewById(R.id.img_help_clear_search)
        helpImageSearch = popupWindow?.contentView?.findViewById(R.id.img_help_search_view)
        helpImageCross?.visibility = View.GONE
        helpImageSearch?.visibility = View.VISIBLE
        editTextHelp?.setText("")
        questionQuery = ""
        //getHelpAndSupportData()
        helpImageCross?.setOnClickListener(View.OnClickListener {
            Helper.closeKeyboard(this@SliderActivity)
            helpImageCross?.visibility = View.GONE
            helpImageSearch?.visibility = View.VISIBLE
            editTextHelp?.setText("")
            questionQuery = ""
          //  getHelpAndSupportData()
        })
        editTextHelp?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
              //  getHelpAndSupportDataWithQuery()
                return@OnEditorActionListener true
            }
            false
        })


        editTextHelp?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                questionQuery = s.toString()
                if (!TextUtils.isEmpty(s)) {
                    helpImageCross?.setVisibility(View.VISIBLE)
                    helpImageSearch?.setVisibility(View.GONE)
                } else {
                    helpImageCross?.visibility = View.GONE
                    helpImageSearch?.visibility = View.VISIBLE
                }
            }
        })
        crossHelp?.setOnClickListener{ popupWindow?.dismiss() }
        btnStartChat = popupWindow?.contentView?.findViewById(R.id.btn_Start_Chat)
        btnStartChat?.setOnClickListener{ openWhatsAppChat() }
        startChat = popupWindow?.contentView?.findViewById(R.id.startChat)
        startChat?.setOnClickListener{ openWhatsAppChat() }
    }

    fun getHelpAndSupportData() {
        // asynchronous operation to get user recently viewed courses.
        mProgress.show()
        //Helper.closeKeyboard(this)
        helpAndSupportArrayList = ArrayList<HelpAndSupport>()
        val apiInterface = ApiClient.createService(
            ApiInterface::class.java
        )
        val response =
            apiInterface.getHelpAndSupportData(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val arrCourseList: JSONArray
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            arrCourseList = GenericUtils.getJsonObject(jsonResponse)
                                .optJSONArray("help_and_support_list")
                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val helpAndSupport = gson.fromJson(
                                    Objects.requireNonNull(courseObject).toString(),
                                    HelpAndSupport::class.java
                                )
                                helpAndSupportArrayList?.add(helpAndSupport)
                            }
                            try {
                                val helpAndSupportAdapter = HelpAndSupportAdapter(
                                    helpAndSupportArrayList!!,
                                    this@SliderActivity
                                )
                                recyclerView_help?.layoutManager = LinearLayoutManager(this@SliderActivity)
                                recyclerView_help?.adapter = helpAndSupportAdapter
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            val message = "No Data Found"
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                        }
                        mProgress.dismiss()
                    } catch (e: JSONException) {
                        mProgress.dismiss()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(applicationContext, "No Data Found ", Toast.LENGTH_LONG).show()
                }
                mProgress.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, throwable: Throwable) {
                mProgress.dismiss()
                Toast.makeText(applicationContext, throwable.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun getHelpAndSupportDataWithQuery() {
        mProgress.show()
        questionQuery = editTextHelp?.getText().toString().trim { it <= ' ' }
        eMedicozApp.getInstance().questionQuery = questionQuery
        Helper.closeKeyboard(this)
        if (!TextUtils.isEmpty(questionQuery)) {
            helpAndSupportArrayList = ArrayList<HelpAndSupport>()
            val apiInterface = ApiClient.createService(
                ApiInterface::class.java
            )
            val response = apiInterface.getHelpAndSupportDataWithQuery(
                SharedPreference.getInstance().loggedInUser.id,
                questionQuery!!
            )
            response.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    mProgress.dismiss()
                    if (response.body() != null) {
                        val gson = Gson()
                        val jsonObject = response.body()
                        val jsonResponse: JSONObject
                        val arrCourseList: JSONArray
                        try {
                            jsonResponse = JSONObject(jsonObject.toString())
                            if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                                arrCourseList = GenericUtils.getJsonObject(jsonResponse)
                                    .optJSONArray("help_and_support_list")
                                for (j in 0 until arrCourseList.length()) {
                                    val courseObject = arrCourseList.getJSONObject(j)
                                    val helpAndSupport = gson.fromJson(
                                        Objects.requireNonNull(courseObject).toString(),
                                        HelpAndSupport::class.java
                                    )
                                    helpAndSupportArrayList?.add(helpAndSupport)
                                }
                                try {
                                    val helpAndSupportAdapter = HelpAndSupportAdapter(
                                        helpAndSupportArrayList!!,
                                        this@SliderActivity
                                    )
                                    recyclerView_help?.layoutManager = LinearLayoutManager(this@SliderActivity)
                                    recyclerView_help?.adapter = helpAndSupportAdapter
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        applicationContext,
                                        e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val message = "No Data Found"
                                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } catch (e: JSONException) {
                            mProgress.dismiss()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(applicationContext, "No Data Found ", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, throwable: Throwable) {
                    mProgress.dismiss()
                    Toast.makeText(applicationContext, throwable.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            mProgress.dismiss()
            Toast.makeText(
                applicationContext,
                getString(R.string.enter_search_query),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun openWhatsAppChat() {
        val number = BuildConfig.WHATSAPP_NO
        try {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net")
            startActivity(sendIntent)
        } catch (e: Exception) {
            if (e is ActivityNotFoundException) {
                val appPackageName = "com.whatsapp"
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=$appPackageName"
                            )
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            )
                        )
                    )
                }
            }
            Log.e("TAG", "ERROR_OPEN_MESSANGER$e")
        }
    }
}