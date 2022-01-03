package com.emedicoz.app.mycourses

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.FragmentRecordCourseListBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.utilso.eMedicozApp
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyCourseFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(fragType: String): MyCourseFragment {
            val fragment = MyCourseFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var progress: Progress
    private var fragType: String = "0"
    private val myCourseList = ArrayList<Course>()
    private lateinit var _binding: FragmentRecordCourseListBinding
    private val binding get() = _binding
    lateinit var mContext: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecordCourseListBinding.inflate(inflater, container, false)

        progress = Progress(mContext)
        progress.setCancelable(false)
        val view = binding.root
        if (arguments != null) {
            fragType = arguments!!.getString(Const.FRAG_TYPE).toString()
        }
        /* binding.apply {
             getRecordedMyCourseList("")
         }*/

        return view
    }

    fun getRecordedMyCourseList(selectedFragment: String) {
        // asynchronous operation to fetch my course list.
//        binding.recordedSearchFilter.setText(searchedKeyword)

        if (selectedFragment.equals("Recorded Courses", true)) {
            fragType = "0"
        }

        progress.show()
        val apiInterface = ApiClient.createService(LandingPageApiInterface::class.java)
        val response = apiInterface.getRecordedMyCourses(SharedPreference.getInstance().loggedInUser.id,
                getIsLiveKey(), getCourseType(),
                eMedicozApp.getInstance().filterType, eMedicozApp.getInstance().searchedKeyword)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {

                    val gson = Gson()
                    val jsonObject = response.body()!!
                    val jsonResponse: JSONObject
                    if (!GenericUtils.isListEmpty(myCourseList))
                        myCourseList.clear()
                    val arrCourseList: JSONArray?
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("TAG", " onResponse: $jsonResponse")
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            // parse course list

                            arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list")
                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val course = gson.fromJson(Objects.requireNonNull(courseObject).toString(), Course::class.java)
                                course.isIs_purchased = true
                                myCourseList.add(course)
                            }
                            try {
                                val myCourseTabAdapter = MyCoursesTabRecyclerAdapter(myCourseList, mContext)
                                binding.recordedView.apply {
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = myCourseTabAdapter

                                    binding.txvNodata.visibility = View.GONE
                                    binding.recordedView.visibility = View.VISIBLE

                                }

                                progress.dismiss()
                                var errorLayout = mContext.findViewById<LinearLayout>(R.id.errorLL)
                                if (errorLayout != null) {
                                    errorLayout.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                binding.txvNodata.visibility = View.VISIBLE
                                binding.recordedView.visibility = View.GONE
                            }
                        } else {
                            progress.dismiss()
                            RetrofitResponse.getApiData(mContext, jsonResponse.optString(Const.AUTH_CODE))
                            binding.txvNodata.visibility = View.VISIBLE
                            binding.recordedView.visibility = View.GONE
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progress.dismiss()
                        binding.txvNodata.visibility = View.VISIBLE
                        binding.recordedView.visibility = View.GONE

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                binding.txvNodata.visibility = View.VISIBLE
                binding.recordedView.visibility = View.GONE

            }
        })
    }

    private fun getCourseType(): String? {
        return if (fragType == "2")
            "2,3"
        else
            "1"
    }

    private fun getIsLiveKey(): String? {
        return if (fragType == "2")
            ""
        else
            fragType
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}





