
package com.emedicoz.app.support

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
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.modelo.HelpAndSupport
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.templateAdapters.HelpAndSupportAdapter
import com.emedicoz.app.utilso.*
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

class HelpSupportActivity : AppCompatActivity() {
    private val TAG = "HelpSupportActivity"
    lateinit var crossHelp: ImageView
    lateinit var recyclerView_help: RecyclerView
    lateinit var helpSearchFilter: EditText
    lateinit var img_help_clear_search: ImageView
    lateinit var img_help_search_view: ImageView
    lateinit var questionQuery: String
    lateinit var mProgress: Progress
    var helpAndSupportArrayList: ArrayList<HelpAndSupport>? = null
    lateinit var btn_Start_Chat: LinearLayout
    lateinit var startChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)
        mProgress = Progress(this@HelpSupportActivity)
        mProgress.setCancelable(false)
        crossHelp = findViewById(R.id.crossHelp)
        recyclerView_help = findViewById(R.id.recyclerView_help)
        helpSearchFilter = findViewById(R.id.helpSearchFilter)
        img_help_clear_search = findViewById(R.id.img_help_clear_search)
        img_help_search_view = findViewById(R.id.img_help_search_view)
        btn_Start_Chat = findViewById(R.id.btn_Start_Chat)
        startChat = findViewById(R.id.startChat)

        img_help_clear_search.visibility = View.GONE
        img_help_search_view.visibility = View.VISIBLE
        helpSearchFilter.setText("")
        questionQuery = ""
        getHelpAndSupportData()


        img_help_clear_search.setOnClickListener(View.OnClickListener {
            Helper.closeKeyboard(this@HelpSupportActivity)
            img_help_clear_search.setVisibility(View.GONE)
            img_help_search_view.setVisibility(View.VISIBLE)
            helpSearchFilter.setText("")
            questionQuery = ""
            getHelpAndSupportData()
        })

        helpSearchFilter.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getHelpAndSupportDataWithQuery()
                return@OnEditorActionListener true
            }
            false
        })

        helpSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                questionQuery = s.toString()
                if (!TextUtils.isEmpty(s)) {
                    img_help_clear_search.visibility = View.VISIBLE
                    img_help_search_view.visibility = View.GONE
                } else {
                    img_help_clear_search.visibility = View.GONE
                    img_help_search_view.visibility = View.VISIBLE
                }
            }
        })

        crossHelp.setOnClickListener { finish() }
        btn_Start_Chat.setOnClickListener(View.OnClickListener { openWhatsAppChat() })

        startChat.setOnClickListener(View.OnClickListener { openWhatsAppChat() })

    }

    private fun getHelpAndSupportDataWithQuery() {
        mProgress.show()
        questionQuery = helpSearchFilter.getText().toString().trim { it <= ' ' }
        eMedicozApp.getInstance().questionQuery = questionQuery
        Helper.closeKeyboard(this)
        if (!TextUtils.isEmpty(questionQuery)) {
            helpAndSupportArrayList = ArrayList()
            val apiInterface = ApiClient.createService(ApiInterface::class.java)
            val response = apiInterface.getHelpAndSupportDataWithQuery(SharedPreference.getInstance().loggedInUser.id, questionQuery)
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
                                arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("help_and_support_list")
                                for (j in 0 until arrCourseList.length()) {
                                    val courseObject = arrCourseList.getJSONObject(j)
                                    val helpAndSupport = gson.fromJson(Objects.requireNonNull(courseObject).toString(), HelpAndSupport::class.java)
                                    helpAndSupportArrayList!!.add(helpAndSupport)
                                }
                                try {
                                    val helpAndSupportAdapter = HelpAndSupportAdapter(helpAndSupportArrayList!!, this@HelpSupportActivity)
                                    recyclerView_help.layoutManager = LinearLayoutManager(this@HelpSupportActivity)
                                    recyclerView_help.adapter = helpAndSupportAdapter
                                } catch (e: Exception) {
                                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val message = "No Data Found"
                                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            mProgress.dismiss()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(applicationContext, "No Data Found ", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, throwable: Throwable) {
                    mProgress.dismiss()
                    Toast.makeText(applicationContext, throwable.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            mProgress.dismiss()
            Toast.makeText(applicationContext, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
        }
    }


    private fun getHelpAndSupportData() {
        // asynchronous operation to get user recently viewed courses.
        mProgress.show()
        Helper.closeKeyboard(this)
        helpAndSupportArrayList = ArrayList<HelpAndSupport>()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getHelpAndSupportData(SharedPreference.getInstance().loggedInUser.id)
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
                            arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("help_and_support_list")
                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val helpAndSupport = gson.fromJson(Objects.requireNonNull(courseObject).toString(), HelpAndSupport::class.java)
                                helpAndSupportArrayList!!.add(helpAndSupport)
                            }
                            try {
                                val helpAndSupportAdapter = HelpAndSupportAdapter(helpAndSupportArrayList!!, this@HelpSupportActivity)
                                recyclerView_help.layoutManager = LinearLayoutManager(this@HelpSupportActivity)
                                recyclerView_help.adapter = helpAndSupportAdapter
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
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

    private fun openWhatsAppChat() {
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
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            Log.e(TAG, "ERROR_OPEN_MESSANGER$e")
        }
    }
}