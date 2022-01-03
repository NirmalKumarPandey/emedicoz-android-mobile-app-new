package com.emedicoz.app.feeds.activity

import android.app.Dialog
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityAppSettingBinding
import com.emedicoz.app.feeds.adapter.ClearOfflineRVAdapter
import com.emedicoz.app.modelo.User
import com.emedicoz.app.response.NotificationSettingsResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.header_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class AppSettingActivity : AppCompatActivity(), View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivityAppSettingBinding
    private var key: String? = null
    private var value = false
    private var darkMode = false
    private var user: User? = null
    private var mProgress: Progress? = null
    private var uiModeManager: UiModeManager? = null
    private var clearOfflineRVAdapter: ClearOfflineRVAdapter? = null
    private var deleteOfflineDialog: Dialog? = null
    private var response: NotificationSettingsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mProgress = Progress(this)
        mProgress!!.setCancelable(false)
        user = SharedPreference.getInstance().loggedInUser
        darkMode = SharedPreference.getInstance().getBoolean(Const.DARK_MODE)
        initView()

        uiModeManager = ContextCompat.getSystemService(this, UiModeManager::class.java)
        networkCallForAppSetting() //NetworkAPICall(API.API_GET_NOTIFICATION_SETTING, true);
        updateSwitchCompat()
    }

    private fun initView(){
        tvHeaderName.text = "App Settings"
        ibBack.setOnClickListener(View.OnClickListener { finish() })

        binding.apply {
            clearofflineBtn.setOnClickListener(this@AppSettingActivity)
            clearAllDQB.setOnClickListener(this@AppSettingActivity)
            termsCard.setOnClickListener(this@AppSettingActivity)
            privacyCard.setOnClickListener(this@AppSettingActivity)
            resetContentCard.setOnClickListener(this@AppSettingActivity)
            changePasswordCard.setOnClickListener(this@AppSettingActivity)
        }

        binding.switchDayNight.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                ThemeHelper.applyTheme("dark")
                SharedPreference.getInstance().putBoolean(Const.DARK_MODE, true)
            } else {
                ThemeHelper.applyTheme("light")
                SharedPreference.getInstance().putBoolean(Const.DARK_MODE, false)
            }
            //recreate()
        })

        if (SharedPreference.getInstance().loggedInUser != null) {
            if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                binding.changePasswordCard.visibility = View.VISIBLE
            } else {
                binding.changePasswordCard.visibility = View.GONE
            }
        } else {
            binding.changePasswordCard.visibility = View.GONE
        }
    }

    private fun updateSwitchCompat() {
        binding.switchDayNight.isChecked = darkMode
    }

    private fun networkCallForAppSetting() {
        mProgress!!.show()
        val api = ApiClient.createService(
                ApiInterface::class.java
        )
        val response = api.getAppSettingUser(user!!.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, responsecall: Response<JsonObject?>) {
                mProgress!!.dismiss()
                if (responsecall.body() != null) {
                    val jsonObject = responsecall.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            GenericUtils.getJsonObject(jsonResponse)
                            this@AppSettingActivity.response = Gson().fromJson(
                                    GenericUtils.getJsonObject(jsonResponse).toString(),
                                    NotificationSettingsResponse::class.java
                            )
                            if (this@AppSettingActivity.response != null) initNotificationViews()
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(
                                            this@AppSettingActivity.resources.getString(R.string.internet_error_message),
                                            ignoreCase = true
                                    )
                            ) Helper.showErrorLayoutForNav(
                                    API.API_GET_NOTIFICATION_SETTING,
                                    this@AppSettingActivity,
                                    1,
                                    1
                            )
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(
                                            getString(
                                                    R.string.something_went_wrong_string
                                            )
                                    )
                            ) Helper.showErrorLayoutForNav(
                                    API.API_GET_NOTIFICATION_SETTING,
                                    this@AppSettingActivity,
                                    1,
                                    2
                            )
                            RetrofitResponse.getApiData(
                                    this@AppSettingActivity,
                                    jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Helper.showErrorLayoutForNav(API.API_GET_NOTIFICATION_SETTING, this@AppSettingActivity, 1, 2)
            }
        })
    }

    private fun networkCallForEditSetting() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        mProgress!!.show()
        val apiInterface = ApiClient.createService(
                ApiInterface::class.java
        )
        val response = apiInterface.getAppeditSettingUser(user!!.id, key!!, if (value) "1" else "0")
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mProgress!!.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Toast.makeText(
                                    this@AppSettingActivity,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(
                                            this@AppSettingActivity!!.resources.getString(R.string.internet_error_message),
                                            ignoreCase = true
                                    )
                            ) Helper.showErrorLayoutForNav(
                                    API.API_GET_NOTIFICATION_SETTING,
                                    this@AppSettingActivity,
                                    1,
                                    1
                            )
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(
                                            getString(
                                                    R.string.something_went_wrong_string
                                            )
                                    )
                            ) Helper.showErrorLayoutForNav(
                                    API.API_GET_NOTIFICATION_SETTING,
                                    this@AppSettingActivity,
                                    1,
                                    2
                            )
                            RetrofitResponse.getApiData(
                                    this@AppSettingActivity,
                                    jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress!!.dismiss()
                Helper.showErrorLayoutForNav(API.API_GET_NOTIFICATION_SETTING, this@AppSettingActivity, 1, 2)
            }
        })
    }

    fun retryApis(apiname: String?) {
        when (apiname) {
            API.API_GET_NOTIFICATION_SETTING -> networkCallForAppSetting()
            API.API_EDIT_NOTIFICATION_SETTING -> networkCallForEditSetting()
            else -> {
            }
        }
    }

    fun initNotificationViews() {
        binding.apply {
            likeswitch.isChecked = response!!.post_like_notification == "1"
            commentswitch.isChecked = response!!.comment_on_post_notification == "1"
            tagswitch.isChecked = response!!.tag_notification == "1"
            followingswitch.isChecked = response!!.follow_notification == "1"
            adminswitch.isChecked = response!!.other_notification == "1"
            likeswitch.setOnCheckedChangeListener(this@AppSettingActivity)
            commentswitch.setOnCheckedChangeListener(this@AppSettingActivity)
            tagswitch.setOnCheckedChangeListener(this@AppSettingActivity)
            adminswitch.setOnCheckedChangeListener(this@AppSettingActivity)
            liveswitch.setOnCheckedChangeListener(this@AppSettingActivity)
            followingswitch.setOnCheckedChangeListener(this@AppSettingActivity)
        }
    }

    private fun showDeleteOfflineDataView() {
        val li = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = li.inflate(R.layout.layout_delete_offline_dialog, null, false)
        deleteOfflineDialog = Dialog(this)
        deleteOfflineDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteOfflineDialog!!.setCanceledOnTouchOutside(false)
        deleteOfflineDialog!!.setContentView(v)
        deleteOfflineDialog!!.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        deleteOfflineDialog!!.show()
        val clearOfflineRV: RecyclerView = v.findViewById(R.id.deleteofflineListRV)
        val submitCard: CardView = v.findViewById(R.id.bottomCard)
        val upperCard: CardView = v.findViewById(R.id.upperCard)
        val cross: ImageView = v.findViewById(R.id.crossimageIV)
        if (clearOfflineRV.visibility == View.GONE) {
            clearOfflineRV.visibility = View.VISIBLE
            upperCard.visibility = View.VISIBLE
        }
        val stringArrayList = ArrayList<String>()
        stringArrayList.add("Videos From Feeds")
        stringArrayList.add("Videos From Videos Segment")
        stringArrayList.add("Videos From Courses")
        clearOfflineRV.layoutManager =
                LinearLayoutManager(this@AppSettingActivity, LinearLayoutManager.VERTICAL, false)
        clearOfflineRVAdapter = ClearOfflineRVAdapter(this@AppSettingActivity, stringArrayList)
        clearOfflineRV.adapter = clearOfflineRVAdapter
        submitCard.setTag(R.id.questions, v)
        submitCard.setTag(R.id.optionsAns, deleteOfflineDialog)
        submitCard.setOnClickListener {
            if (clearOfflineRVAdapter!!.selectedCheckBoxes.isNotEmpty()) {
                deleteSelectedTypes(clearOfflineRVAdapter!!.selectedCheckBoxes)
            } else {
                Toast.makeText(
                        this,
                        R.string.please_select_any_option_to_clear,
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
        cross.tag = deleteOfflineDialog
        cross.setOnClickListener { deleteOfflineDialog!!.dismiss() }
    }

    private fun deleteSelectedTypes(strings: ArrayList<String?>) {
        val offlineDataArrayList = eMedicozDownloadManager.getAllOfflineData(this)
        if (offlineDataArrayList != null && offlineDataArrayList.size > 0) {
            for (str in strings) {
                when (str) {
                    "Videos From Feeds" -> eMedicozDownloadManager.removeOfflineDataByType(
                            "Feeds",
                            this
                    )
                    "Videos From Videos Segment" -> eMedicozDownloadManager.removeOfflineDataByType(
                            "Videos",
                            this
                    )
                    "Videos From Courses" -> eMedicozDownloadManager.removeOfflineDataByType(
                            Const.VIDEO,
                            this
                    )
                    "PDF From Courses" -> {
                    }
                    "ePub From Courses" -> {
                    }
                    "DOC From Courses" -> {
                    }
                    "PPT From Courses" -> {
                    }
                }
            }
            Toast.makeText(
                    this,
                    R.string.selected_media_removed_from_device,
                    Toast.LENGTH_SHORT
            ).show()
            if (deleteOfflineDialog != null && deleteOfflineDialog!!.isShowing) deleteOfflineDialog!!.dismiss()
        } else {
            Toast.makeText(this, R.string.no_offline_media_found, Toast.LENGTH_SHORT).show()
            if (deleteOfflineDialog != null && deleteOfflineDialog!!.isShowing) deleteOfflineDialog!!.dismiss()
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.termsCard -> Helper.GoToWebViewActivity(this, Const.TERMS_URL)
            R.id.privacyCard -> Helper.GoToWebViewActivity(this, Const.PRIVACY_URL)
            R.id.clearofflineBtn -> showDeleteOfflineDataView()
            R.id.clearAllDQB -> {
            }
            R.id.changePasswordCard -> {
                //Log.e( "onClick: ", requireActivity().toString())
                //(requireActivity() as HomeActivity).navController.navigate(R.id.changePasswordFragment)
/*                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, ChangePasswordFragment.newInstance())
                    .addToBackStack(
                        Const.CHANGE_PASSWORD
                    ).commit()*/
            }
            R.id.resetContentCard -> {
                val intent = Intent(this@AppSettingActivity, ResetContentActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, b: Boolean) {
        when (p0!!.id) {
            R.id.likeswitch -> key = "post_like_notification"
            R.id.commentswitch -> key = "comment_on_post_notification"
            R.id.tagswitch -> key = "tag_notification"
            R.id.followingswitch -> key = "follow_notification"
            R.id.adminswitch -> key = "other_notification"
        }
        value = b
        networkCallForEditSetting() //NetworkAPICall(API.API_EDIT_NOTIFICATION_SETTING, true);
    }
}