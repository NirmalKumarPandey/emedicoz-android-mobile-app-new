package com.emedicoz.app.courses.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.courses.adapter.ExpertFollowAdapter
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LayoutFollowExpertBinding
import com.emedicoz.app.feeds.activity.FeedsActivity
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.response.MasterFeedsHitResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.youtube.player.internal.i


class FollowExpertFragment : Fragment() {
    private lateinit var binding: LayoutFollowExpertBinding
    private lateinit var expertFollowAdapter: ExpertFollowAdapter
    var masterFeedsHitResponse: MasterFeedsHitResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFollowExpertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.expertFollowRV.layoutManager = LinearLayoutManager(requireContext())
        networkCallForMasterFeed()
        binding.nextBtn.setOnClickListener {
            if (!GenericUtils.isListEmpty(masterFeedsHitResponse?.expert_list)) {
                if ((activity as AuthActivity).followExpertCounter >= 5) {
                    SharedPreference.getInstance()
                        .putBoolean(Const.IS_USER_REGISTRATION_DONE, true)
                    SharedPreference.getInstance()
                        .putBoolean(Const.IS_USER_LOGGED_IN, true)
                    val intent = Intent(activity, HomeActivity::class.java)
                    //val intent = Intent(activity, FeedsActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    requireActivity().startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You must have to follow at least 5 experts",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                SharedPreference.getInstance()
                    .putBoolean(Const.IS_USER_REGISTRATION_DONE, true)
                SharedPreference.getInstance()
                    .putBoolean(Const.IS_USER_LOGGED_IN, true)
                val intent = Intent(activity, HomeActivity::class.java)
                //val intent = Intent(activity, FeedsActivity::class.java)
                intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireActivity().startActivity(intent)
            }

        }
    }

    private fun networkCallForMasterFeed() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val mprogress = Progress(requireContext())
        mprogress.setCancelable(false)
        mprogress.show()
        val apis = ApiClient.createService(
            SinglecatVideoDataApiInterface::class.java
        )
        val response = apis.getMasterFeedForUser(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                mprogress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val gson = Gson()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            masterFeedsHitResponse = gson.fromJson(
                                GenericUtils.getJsonObject(jsonResponse).toString(),
                                MasterFeedsHitResponse::class.java
                            )
                            if (masterFeedsHitResponse != null && masterFeedsHitResponse?.expert_list != null) {
                                SharedPreference.getInstance()
                                    .setMasterHitData(masterFeedsHitResponse)
                            }
                            if (!GenericUtils.isListEmpty(masterFeedsHitResponse?.expert_list!!)) {
                                binding.expertFollowRV.visibility = View.VISIBLE
                                binding.noExpertTV.visibility = View.GONE
                                expertFollowAdapter = ExpertFollowAdapter(
                                    masterFeedsHitResponse?.expert_list!!, requireActivity(),
                                    Const.PEOPLE_LIST_FEEDS_COMMONS,
                                    Const.COMMON_EXPERT_PEOPLE_VIEWALL,
                                    1
                                )
                                binding.expertFollowRV.adapter = expertFollowAdapter
                            } else {
                                binding.expertFollowRV.visibility = View.GONE
                                binding.noExpertTV.visibility = View.VISIBLE
                            }
                        } else {
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
                mprogress.dismiss()
                Helper.showExceptionMsg(activity, t)
            }
        })
    }

    companion object {
        private const val TAG = "FollowExpertFragment"
        fun newInstance(): FollowExpertFragment {
            return FollowExpertFragment()
        }
    }
}