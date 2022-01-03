package com.emedicoz.app.login.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courses.fragment.FollowExpertFragment
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.FragmentProfileSubmissionBinding
import com.emedicoz.app.modelo.User
import com.emedicoz.app.response.MasterRegistrationResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProfileSubmissionFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileSubmissionBinding
    private lateinit var mProgress: Progress
    private var user: User? = null
    private var type:String = ""
    private var masterRegistrationResponse: MasterRegistrationResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getSerializable("user") as User
        type = arguments?.getString(Constants.Extras.TYPE) as String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileSubmissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgress = Progress(requireContext())
        mProgress.setCancelable(false)
        networkCallForMasterRegData()
        if (type == Const.FOLLOW){
            addFragment(FollowExpertFragment.newInstance())
            changeBackground(Const.FOLLOW,
                isContactFilled = true,
                isAcademicFilled = true,
                isCourseFilled = true
            )
        }else {
            addFragment(UploadProfileFragment.newInstance(user!!))
            changeBackground(Const.CONTACT,
                isContactFilled = false,
                isAcademicFilled = false,
                isCourseFilled = false
            )
        }
        binding.apply {
            switchContactTV.setOnClickListener(this@ProfileSubmissionFragment)
            switchAcademicTV.setOnClickListener(this@ProfileSubmissionFragment)
            switchCourseTV.setOnClickListener(this@ProfileSubmissionFragment)
            switchFollowTV.setOnClickListener(this@ProfileSubmissionFragment)
        }
    }

    fun changeBackground(
        type: String?,
        isContactFilled: Boolean,
        isAcademicFilled: Boolean,
        isCourseFilled: Boolean
    ) {
        if (isContactFilled) {
            binding.switchContactRL.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.circle_green
            )
            binding.contactTextTV.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.dark_green_new
                )
            )
        } else {
            binding.switchContactRL.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.circle_white_stroke_gray
            )
            binding.contactTextTV.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.gray_8e8e8e
                )
            )
        }
        if (isAcademicFilled){
            binding.switchAcademicRL.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.circle_green
            )
            binding.academicTextTV.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.dark_green_new
                )
            )
        }else {
            binding.switchAcademicRL.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.circle_white_stroke_gray
            )
            binding.academicTextTV.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.gray_8e8e8e
                )
            )
        }
        if (isCourseFilled){
            binding.switchCourseRL.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.circle_green
            )
            binding.courseTextTV.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.dark_green_new
                )
            )
        }else {
            binding.switchCourseRL.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.circle_white_stroke_gray
            )
            binding.courseTextTV.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.gray_8e8e8e
                )
            )
        }
        binding.switchFollowRL.background = ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.circle_white_stroke_gray
        )
        binding.followTextTV.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.gray_8e8e8e
            )
        )
        when (type) {
            Const.CONTACT -> {
                binding.switchContactRL.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.circle_blue
                )
                binding.contactTextTV.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.blue
                    )
                )
            }
            Const.ACADEMIC -> {
                binding.switchAcademicRL.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.circle_blue
                )
                binding.academicTextTV.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.blue
                    )
                )
            }
            Const.COURSE -> {
                binding.switchCourseRL.background =
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.circle_blue
                    )

                binding.courseTextTV.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.blue
                    )
                )
            }
            Const.FOLLOW -> {
                binding.switchFollowRL.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.circle_blue
                )
                binding.followTextTV.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.blue
                    )
                )
            }
        }
    }

/*    fun addFragment(fragment: Fragment) {
        try {
            childFragmentManager
                .beginTransaction()
                .add(R.id.profileSubmissionContainer, fragment)
                .commitAllowingStateLoss();
        } catch (e: IllegalStateException) {
            e.printStackTrace();
        }
    }*/

    fun addFragment(fragment: Fragment?) {
        val fragmentManager = childFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.profileSubmissionContainer, fragment!!)
        ft.commitAllowingStateLoss()
    }

/*    fun replaceFragment(fragment: Fragment) {
        try {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.profileSubmissionContainer, fragment)
                .commitAllowingStateLoss();
        } catch (e: IllegalStateException) {
            e.printStackTrace();
        }
    }*/

    fun replaceFragment(fragment: Fragment) {
        Log.d(TAG, "replaceFragment: " + childFragmentManager.backStackEntryCount)
        val backStateName = fragment.javaClass.simpleName
        val manager = childFragmentManager
        try {
            val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                //fragment not in back stack, create it.
                val ft = manager.beginTransaction()
                ft.replace(R.id.profileSubmissionContainer, fragment, backStateName)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ft.addToBackStack(backStateName)
                ft.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "ProfileSubmissionFragme"

        @JvmStatic
        fun newInstance(user: User,type:String): ProfileSubmissionFragment {
            val fragment = ProfileSubmissionFragment()
            val args = Bundle()
            args.putSerializable("user", user)
            args.putString(Constants.Extras.TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClick(v: View?) {
/*        when (v?.id) {
            R.id.switchContactTV -> {
                replaceFragment(ContactDetailFragment.newInstance(SharedPreference.getInstance().loggedInUser))
                changeBackground(Const.CONTACT,
                    isContactFilled = false,
                    isAcademicFilled = false,
                    isCourseFilled = false)
            }

            R.id.switchAcademicTV -> {
                 replaceFragment(AcademicFragment.newInstance(SharedPreference.getInstance().loggedInUser))
                changeBackground(Const.ACADEMIC,
                    isContactFilled = true,
                    isAcademicFilled = false,
                    isCourseFilled = false)
            }

            R.id.switchCourseTV -> {
                replaceFragment(SelectCourseFragment.newInstance(SharedPreference.getInstance().loggedInUser))
                changeBackground(Const.ACADEMIC,
                    isContactFilled = true,
                    isAcademicFilled = true,
                    isCourseFilled = false)
            }

            R.id.switchFollowTV -> {
                replaceFragment(FollowExpertFragment.newInstance())
                changeBackground(Const.ACADEMIC,
                    isContactFilled = true,
                    isAcademicFilled = true,
                    isCourseFilled = true)
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: ")
        val fragment = childFragmentManager.findFragmentById(R.id.profileSubmissionContainer)
        if (fragment is ContactDetailFragment || fragment is UploadProfileFragment)
            fragment.onActivityResult(requestCode, resultCode, data)
    }

    private fun networkCallForMasterRegData() {
        if (!Helper.isConnected(requireContext())) {
            Toast.makeText(requireContext(), R.string.internet_error_message, Toast.LENGTH_SHORT)
                .show()
            return
        }
        Log.e("BASE", "networkCallForMasterRegData: ")
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
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Log.e("BASE:regMaster", "onResponse: ")
                            masterRegistrationResponse = Gson().fromJson(
                                GenericUtils.getJsonObject(jsonResponse).toString(),
                                MasterRegistrationResponse::class.java
                            )
                            if (masterRegistrationResponse != null && masterRegistrationResponse?.main_category?.isNotEmpty()!!) {
                                SharedPreference.getInstance()
                                    .setMasterRegistrationData(masterRegistrationResponse)
                            } else {
                                if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(
                                        getString(R.string.something_went_wrong),
                                        ignoreCase = true
                                    )
                                ) {
                                    networkCallForMasterRegData() //networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                                } else {
                                    Toast.makeText(
                                        requireContext(), jsonResponse.optString(
                                            Constants.Extras.MESSAGE
                                        ), Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(
                                    getString(R.string.something_went_wrong),
                                    ignoreCase = true
                                )
                            ) {
                                networkCallForMasterRegData() //networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                            } else {
                                Toast.makeText(
                                    requireContext(), jsonResponse.optString(
                                        Constants.Extras.MESSAGE
                                    ), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                mProgress.dismiss()
                Helper.showExceptionMsg(requireContext(), t)
            }
        })
    }

    fun getCurrentFragment(): Fragment {
        val fragment = childFragmentManager.findFragmentById(R.id.profileSubmissionContainer)
        return fragment!!
    }
}