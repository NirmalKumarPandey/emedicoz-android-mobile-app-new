package com.emedicoz.app.mycourses

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.MyCoursesFragmentBinding
import com.emedicoz.app.dialog.MyCourseDialog
import com.emedicoz.app.rating.GetQbankRating
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface
import com.emedicoz.app.utilso.*
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyCourseTabFragment : Fragment() {

    private var selectedPage: Int = 0
    private lateinit var adapter: MyCoursesFragmentAdapter
    private lateinit var rcBinding: MyCoursesFragmentBinding
    private var filterType: String = ""
    var searchedKeyword: String = ""

    // This property is only valid between onCreateView and onDestroyView.
    val binding get() = rcBinding
    private lateinit var progress: Progress
    lateinit var myCourseDialog: MyCourseDialog
    var course_status = ""


    companion object {
        @JvmStatic
        fun newInstance(fragType: String): MyCourseTabFragment {
            val fragment = MyCourseTabFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rcBinding = MyCoursesFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewPager = binding.viewpagerSolution
        val tabLayout = binding.tabLayout

        progress = Progress(context)
        progress.setCancelable(false)

        eMedicozApp.getInstance().filterType = ""
        eMedicozApp.getInstance().searchedKeyword = ""

        adapter = MyCoursesFragmentAdapter(this)


        myCourseDialog = MyCourseDialog()

        course_status = SharedPreference.getInstance().getMyCourseStatus(Const.MY_COURSE_RATING, "")

        networkCallForTabsVisibility()
        return view
    }


    fun setUi() {


        binding.viewpagerSolution.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerSolution) { tab, position ->
            tab.text = adapter.getItemTitle(position)
        }.attach()

        binding.viewpagerSolution.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println(position)
                selectedPage = position
                networkCallForMyCourse()
            }


            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectedPage = position
            }
        })


        if (!TextUtils.isEmpty(searchedKeyword)) {
            binding.imgClearSearch.visibility = View.VISIBLE
            binding.imgSearchView.visibility = View.GONE
            binding.courseSearchFilter.setText(searchedKeyword)
            binding.courseSearchFilter.setSelection(Helper.GetText(binding.courseSearchFilter).length)
        } else {
            binding.imgClearSearch.visibility = View.GONE
            binding.imgSearchView.visibility = View.VISIBLE
            binding.courseSearchFilter.setText("")
        }

        binding.filterTv.setOnClickListener {
            showPopMenu(it)
        }

        binding.imgClearSearch.setOnClickListener {
            searchForKeywordInAPI(true)
        }

        binding.courseSearchFilter.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchForKeywordInAPI(false)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.courseSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    binding.imgClearSearch.visibility = View.VISIBLE
                    binding.imgSearchView.visibility = View.GONE
                } else {
                    binding.imgClearSearch.visibility = View.GONE
                    binding.imgSearchView.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
//        if (activity is HomeActivity){
//            (activity as HomeActivity).floatingActionButton.visibility = View.GONE
//            (activity as HomeActivity).bottomNavigationView.visibility = View.GONE
//            (activity as HomeActivity).itemNotification.isVisible = false
//            (activity as HomeActivity).itemSavedNotesFragment.isVisible = false
//            (activity as HomeActivity).itemMyCartFragment.isVisible = false
//            (activity as HomeActivity).search.visibility = View.GONE
//            (activity as HomeActivity).toolbarHeader.isVisible = false
//            val actionBar = (activity as HomeActivity).supportActionBar
//            actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
//            actionBar?.setDisplayHomeAsUpEnabled(true)
//        }

        getQbankRating();


//        if (activity is BaseABNavActivity) {
//            (activity as BaseABNavActivity).manageToolbar(Const.MYCOURSES)
//        }
    }

    fun showPopMenu(view: View) {
        val popup: PopupMenu? = activity?.let { PopupMenu(it, view) }
        popup?.apply {
            inflate(R.menu.my_course_menu)
        }?.show()

        popup?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.FreeRC -> {
                    filterType = "free"
                }
                R.id.PaidRC -> {
                    filterType = "paid"
                }
            }
//            filterType = item.title.toString()
            eMedicozApp.getInstance().filterType = filterType
            searchedKeyword = binding.courseSearchFilter.text.trim().toString()
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            networkCallForMyCourse()
            true
        }
    }

    private fun searchForKeywordInAPI(loadAllData: Boolean) {
        if (loadAllData) {
            filterType = ""
            searchedKeyword = ""
            eMedicozApp.getInstance().filterType = filterType
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            binding.courseSearchFilter.setText(searchedKeyword)
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, searchedKeyword)

            networkCallForMyCourse()
            return
        }

        searchedKeyword = binding.courseSearchFilter.text.trim().toString()
        eMedicozApp.getInstance().searchedKeyword = searchedKeyword

        if (searchedKeyword.isNotEmpty()) {
            networkCallForMyCourse()
            Helper.closeKeyboard(activity)
        } else
            Toast.makeText(activity, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
    }


    fun networkCallForTabsVisibility() {
        progress.show()
        val apiInterface = ApiClient.createService(LandingPageApiInterface::class.java)
        val response = apiInterface.getTabsVisibility(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()!!
                    val jsonResponse: JSONObject
                    val arrCourseListLive: JSONArray?
                    val arrCourseListRecorded: JSONArray?
                    val arrCourseListStudy: JSONArray?
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("TAG", " onResponse: $jsonResponse")
                        if (jsonResponse.optBoolean(Const.STATUS)) {


                            arrCourseListLive = GenericUtils.getJsonObject(jsonResponse).optJSONArray("live_courses")
                            arrCourseListRecorded = GenericUtils.getJsonObject(jsonResponse).optJSONArray("recorded_courses")
                            arrCourseListStudy = GenericUtils.getJsonObject(jsonResponse).optJSONArray("test_courses")

                            if (arrCourseListRecorded != null && arrCourseListRecorded.length() > 0) {
                                adapter.addFragment(MyCourseFragment.newInstance("0"), "Recorded Courses")
                            }
                            if (arrCourseListLive != null && arrCourseListLive.length() > 0) {
                                adapter.addFragment(MyCourseFragment.newInstance("1"), "Live Courses")
                            }

                            if (arrCourseListStudy != null && arrCourseListStudy.length() > 0) {
                                adapter.addFragment(MyCourseFragment.newInstance("2"), "Study")
                            }

                            setUi()
                            progress.dismiss()
                        } else {
                            progress.dismiss()
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE))
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progress.dismiss()

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
            }
        })
    }

    private fun networkCallForMyCourse() {
        val fragment = adapter.getItem(selectedPage)
        val selectedFragment = adapter.getItemTitle(selectedPage).toString()
        if (fragment is MyCourseFragment) {
            fragment.getRecordedMyCourseList(selectedFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        rcBinding = null
    }

    //check course rating api exit
    private fun getQbankRating() {
        if (!Helper.isConnected(context)) {
            Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
        }
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        apiInterface.getQbankRating(SharedPreference.getInstance().loggedInUser.id, "apprating").enqueue(object : Callback<GetQbankRating?> {
            override fun onResponse(call: Call<GetQbankRating?>, response: Response<GetQbankRating?>) {
                val getQbankRating = response.body()
                if (getQbankRating!= null){
                    if (getQbankRating!!.status.statuscode == "200") {
                        if (getQbankRating.data != null) {
                            if (SharedPreference.getInstance().loggedInUser.id == getQbankRating.data.user_id) {
                            }
                        } else {
                        }
                    }
                }else{

                }

            }

            override fun onFailure(call: Call<GetQbankRating?>, t: Throwable) {
                if (course_status.equals("true")){
                    myCourseDialog.show(childFragmentManager, "");
                }else {

                }
            }
        })
    }
}