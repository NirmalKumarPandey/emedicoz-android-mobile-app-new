package com.emedicoz.app.feeds.activity

import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import android.os.Bundle
import android.view.View
import android.widget.*
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityResetContentBinding
import com.emedicoz.app.utilso.network.API
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.*
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.util.*

class ResetContentActivity : AppCompatActivity(), View.OnClickListener, MyNetworkCallBack {
    private lateinit var binding: ActivityResetContentBinding
    var deleteTest: String? = null
    var deleteDqb: String? = null
    var deleteBookmark: String? = null
    private lateinit var myNetworkCall: MyNetworkCall
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityResetContentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myNetworkCall = MyNetworkCall(this, this)
        binding.toolbarResetContent.title.setText("Reset Content")
        binding.checkboxTestRL.setOnClickListener(this)
        binding.checkboxQBankRL.setOnClickListener(this)
        binding.checkboxBookmarkRL.setOnClickListener(this)
        binding.resetButton.setOnClickListener(this)
        binding.toolbarResetContent.backIV.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.checkboxTestRL -> binding.checkboxTest.isChecked = !binding.checkboxTest.isChecked
            R.id.checkboxQBankRL -> binding.checkboxQBank.isChecked =
                !binding.checkboxQBank.isChecked
            R.id.checkboxBookmarkRL -> binding.checkboxBookmark.isChecked =
                !binding.checkboxBookmark.isChecked
            R.id.resetButton -> if (binding.checkboxTest.isChecked || binding.checkboxQBank.isChecked || binding.checkboxBookmark.isChecked) {
                val v = Helper.newCustomDialog(
                    this, true, getString(R.string.app_name), getString(
                        R.string.if_you_reset_these_contents_you_can_not_get_back_your_previous_history
                    )
                )
                val btnCancel: Button = v.findViewById(R.id.btn_cancel)
                val btnSubmit: Button = v.findViewById(R.id.btn_submit)
                btnCancel.text = getString(R.string.cancel).toUpperCase(Locale.ROOT)
                btnSubmit.text = getString(R.string.clear).toUpperCase(Locale.ROOT)
                btnCancel.setOnClickListener { Helper.dismissDialog() }
                btnSubmit.setOnClickListener {
                    Helper.dismissDialog()
                    myNetworkCall.NetworkAPICall(API.API_CLEAR_DATA, true)
                }
            } else {
                Toast.makeText(this, "Please select atleast one option.", Toast.LENGTH_SHORT).show()
            }
            R.id.backIV -> onBackPressed()
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun getAPI(apiType: String, service: FlashCardApiInterface): Call<JsonObject> {
        setValues()
        val params: MutableMap<String, Any?> = HashMap()
        params[Const.USER_ID] = SharedPreference.getInstance().loggedInUser.id
        params[Constants.Extras.DELETE_TEST] = deleteTest
        params[Constants.Extras.DELETE_DQB] = deleteDqb
        params[Constants.Extras.DELETE_BOOKMARK] = deleteBookmark
        return service.postData(apiType, params)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonObject: JSONObject, apiType: String) {
        Toast.makeText(
            this@ResetContentActivity,
            jsonObject.optString(Constants.Extras.MESSAGE),
            Toast.LENGTH_SHORT
        ).show()
        binding.checkboxTest.isChecked = !binding.checkboxTest.isChecked
        binding.checkboxQBank.isChecked = !binding.checkboxQBank.isChecked
        binding.checkboxBookmark.isChecked = !binding.checkboxBookmark.isChecked
    }

    override fun errorCallBack(jsonString: String, apiType: String) {
        Toast.makeText(this@ResetContentActivity, jsonString, Toast.LENGTH_SHORT).show()
    }

    private fun setValues() {
        deleteTest = if (binding.checkboxTest.isChecked) {
            "1"
        } else {
            "0"
        }
        deleteDqb = if (binding.checkboxQBank.isChecked) {
            "1"
        } else {
            "0"
        }
        deleteBookmark = if (binding.checkboxBookmark.isChecked) {
            "1"
        } else {
            "0"
        }
    }
}