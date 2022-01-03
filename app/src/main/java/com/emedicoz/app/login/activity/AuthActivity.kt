package com.emedicoz.app.login.activity

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.FacebookLoginHandler
import com.emedicoz.app.customviews.FacebookLoginHandler.FacebookLoginCallback
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityAuthBinding
import com.emedicoz.app.login.fragment.*
import com.emedicoz.app.modelo.HelpAndSupport
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.templateAdapters.HelpAndSupportAdapter
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.GooglePlus_login.OnClientConnectedListener
import com.facebook.FacebookException
import com.facebook.GraphResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class AuthActivity : AppCompatActivity(), OnClientConnectedListener, FacebookLoginCallback {
    private lateinit var binding: ActivityAuthBinding
    var followExpertCounter: Int = 0
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
    private var googlePlus_login: GooglePlus_login? = null
    private var facebookLoginHandler: FacebookLoginHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mProgress = Progress(this)
        mProgress.setCancelable(false)
        googlePlus_login = GooglePlus_login(this)
        // INITIALIZE THE FACEBOOK SDK
        facebookLoginHandler = FacebookLoginHandler(this, this, null)
        addFragment(LoginByMobileFragment.newInstance())
    }

/*    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.authContainer, fragment)
            .commitAllowingStateLoss()
    }*/

    fun addFragment(fragment: Fragment?) {
        val fragmentManager = this.supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.authContainer, fragment!!)
        ft.commitAllowingStateLoss()
    }

/*
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.authContainer, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
*/

    fun replaceFragment(fragment: Fragment) {
        if (isFinishing) return
        Log.d(TAG, "replaceFragment: " + supportFragmentManager.backStackEntryCount)
        val backStateName = fragment.javaClass.simpleName
        val manager = this.supportFragmentManager
        try {
            val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                //fragment not in back stack, create it.
                val ft = manager.beginTransaction()
                ft.replace(R.id.authContainer, fragment, backStateName)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ft.addToBackStack(backStateName)
                ft.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentFragment(): Fragment {
        val fragment = supportFragmentManager.findFragmentById(R.id.authContainer)
        return fragment!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.authContainer)
        if (fragment is ProfileSubmissionFragment)
            (fragment as ProfileSubmissionFragment).onActivityResult(requestCode, resultCode, data)

        facebookLoginHandler!!.callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GooglePlus_login.RC_SIGN_IN) {
            googlePlus_login!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.authContainer)
        if (fragment != null && (fragment is LoginByMobileFragment || fragment is ProfileSubmissionFragment)) {
            finish()
        } else {
            super.onBackPressed()
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
        popupWindow?.showAsDropDown(binding.helpView)
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
       // getHelpAndSupportData()
        helpImageCross?.setOnClickListener(View.OnClickListener {
            Helper.closeKeyboard(this@AuthActivity)
            helpImageCross?.visibility = View.GONE
            helpImageSearch?.visibility = View.VISIBLE
            editTextHelp?.setText("")
            questionQuery = ""
           // getHelpAndSupportData()
        })
        editTextHelp?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
             //   getHelpAndSupportDataWithQuery()
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
        crossHelp?.setOnClickListener { popupWindow?.dismiss() }
        btnStartChat = popupWindow?.contentView?.findViewById(R.id.btn_Start_Chat)
        btnStartChat?.setOnClickListener { openWhatsAppChat() }
        startChat = popupWindow?.contentView?.findViewById(R.id.startChat)
        startChat?.setOnClickListener { openWhatsAppChat() }
    }

    fun getHelpAndSupportData() {
        // asynchronous operation to get user recently viewed courses.
        mProgress.show()
        Helper.closeKeyboard(this)
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
                                    this@AuthActivity
                                )
                                recyclerView_help?.layoutManager =
                                    LinearLayoutManager(this@AuthActivity)
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
                                        this@AuthActivity
                                    )
                                    recyclerView_help?.layoutManager =
                                        LinearLayoutManager(this@AuthActivity)
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


    fun LoginMaster(socialType: String?) {
        if (Helper.isConnected(this)) {
            when (socialType) {
                Const.SOCIAL_TYPE_FACEBOOK -> {
                    facebookLoginHandler?.logoutViaFacebook()
                    facebookLoginHandler?.onFacebookButtonClick()
                }
                Const.SOCIAL_TYPE_GMAIL -> //                    mprogress.show();
                    googlePlus_login!!.signIn()
            }
        } else {
            // mprogress.hide()
            Toast.makeText(this, Const.NO_INTERNET, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGoogleProfileFetchComplete(`object`: JSONObject) {
        Log.e("SIGNUP_Google", `object`.toString())
        val fragmentManager = supportFragmentManager
        val fragment: Fragment = fragmentManager.findFragmentById(R.id.authContainer)!!
        if (fragment is LoginByMobileFragment) fragment.logInTask(
            `object`,
            Const.SOCIAL_TYPE_GMAIL
        ) else if (fragment is LoginByEmailFragment) fragment.logInTask(
            `object`,
            Const.SOCIAL_TYPE_GMAIL
        )
    }

    override fun onClientFailed() {
        show("Error occurred!", Toast.LENGTH_SHORT)
    }

    override fun facebookOnSuccess(`object`: JSONObject?, response: GraphResponse?) {
        val fragmentManager = supportFragmentManager
        val fragment: Fragment = fragmentManager.findFragmentById(R.id.authContainer)!!
        if (fragment is LoginByEmailFragment) `object`?.let {
            fragment.logInTask(
                it,
                Const.SOCIAL_TYPE_FACEBOOK
            )
        } else if (fragment is LoginByMobileFragment) `object`?.let {
            fragment.logInTask(
                it,
                Const.SOCIAL_TYPE_FACEBOOK
            )
        }
    }

    override fun facebookOnCancel() {
        show("Login cancelled", Toast.LENGTH_SHORT)
    }

    override fun facebookOnError(error: FacebookException) {
        error.message?.let { show(it, Toast.LENGTH_SHORT) }
    }

    companion object{
        private const val TAG = "AuthActivity"
    }

}