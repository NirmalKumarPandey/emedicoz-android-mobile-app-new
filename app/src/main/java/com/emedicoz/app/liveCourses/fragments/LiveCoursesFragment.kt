package com.emedicoz.app.liveCourses.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LiveCoursesFragmentBinding
import com.emedicoz.app.liveCourses.adapters.LiveCoursesMainAdapter
import com.emedicoz.app.liveCourses.model.LiveCoursesViewModel
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.UpcomingCourseData
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.modelo.courses.CourseCategory
import com.emedicoz.app.modelo.courses.CoursesData
import com.emedicoz.app.modelo.courses.PositionOrder
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.live_courses_fragment.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LiveCoursesFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(fragType: String): LiveCoursesFragment {
            val fragment = LiveCoursesFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            fragment.arguments = args
            return fragment
        }
    }

    var recyclerViewState: Parcelable? = null

    // variable Description
    private lateinit var progress: Progress
    private var filterType: String = ""
    var searchedKeyword: String = ""
    private lateinit var viewModel: LiveCoursesViewModel

    private lateinit var lcBinding: LiveCoursesFragmentBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = lcBinding


    private lateinit var liveCourseMainAdapter: LiveCoursesMainAdapter
    private var menu: Menu? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        lcBinding = LiveCoursesFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        if (activity is HomeActivity) {
            menu = (activity as HomeActivity).myMenu
            if (menu!=null){
                menu?.findItem(R.id.app_bar_search)!!.isVisible = false
            }
            (activity as HomeActivity).toolbarHeader.isVisible = false

        }

        progress = Progress(activity)
        progress.setCancelable(false)
        viewModel = ViewModelProvider(this).get(LiveCoursesViewModel::class.java)

        (activity as HomeActivity).floatingActionButton.visibility = View.GONE

        binding.filterTv.setOnClickListener {
            showPopMenu(it)
        }
        if (!TextUtils.isEmpty(searchedKeyword)) {
            binding.imgClearSearch.visibility = View.VISIBLE
            binding.imgSearchView.visibility = View.GONE
            binding.recordedSearchFilter.setText(searchedKeyword)
            binding.recordedSearchFilter.setSelection(Helper.GetText(binding.recordedSearchFilter).length)
        } else {
            binding.imgClearSearch.visibility = View.GONE
            binding.imgSearchView.visibility = View.VISIBLE
            binding.recordedSearchFilter.setText("")
        }

        binding.imgClearSearch.setOnClickListener {
            searchForKeywordInAPI(true)
        }


        binding.recordedSearchFilter.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchForKeywordInAPI(false)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.recordedSearchFilter.addTextChangedListener(object : TextWatcher {
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
//                    searchForKeywordInAPI(true)
                }
            }
        })

//        if (activity is BaseABNavActivity) {
//            swipeRefreshLayout = (activity as BaseABNavActivity).swipeRefreshLayout
//            swipeRefreshLayout.isRefreshing = false
//            binding.liveCourseRecyclerView.addOnScrollListener(object :
//                RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    val firstPos: Int =
//                        (binding.liveCourseRecyclerView.layoutManager as LinearLayoutManager)
//                            .findFirstVisibleItemPosition()
//                    swipeRefreshLayout.isEnabled = firstPos == 0 && !swipeRefreshLayout.isRefreshing
//                    Log.i("scroll position", firstPos.toString())
//                }
//            })
//        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (activity is HomeActivity) {
            (activity as HomeActivity).bottomNavigationView.visibility = View.VISIBLE
        }
        recyclerViewState = binding.liveCourseRecyclerView.layoutManager?.onSaveInstanceState()

        networkCallForLiveCourse()
        var cartTV = requireActivity().findViewById<TextView>(R.id.cartTV)
        if (cartTV != null) {

            var cartCount = SharedPreference.getInstance().getInt(Const.CART_COUNT)
            cartTV.visibility = if (cartCount > 0) View.VISIBLE else View.GONE
            cartTV.text = cartCount.toString()
        }
    }

    fun showPopMenu(view: View) {
        val popup: PopupMenu? = activity?.let { PopupMenu(it, view) }
        popup?.apply {
            inflate(R.menu.recorded_course_menu)
        }?.show()

        popup?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.FreeRC -> {
                    filterType = "free"
                }
                R.id.PaidRC -> {
                    filterType = "paid"
                }
                R.id.BestRC -> {
                    filterType = "best-seller"
                }
                R.id.TopRatedRC -> {
                    filterType = "top-rated"
                }
            }
//            filterType = item.title.toString()
            searchedKeyword = binding.recordedSearchFilter.text.trim().toString()
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            eMedicozApp.getInstance().filterType = filterType
            networkCallForLiveCourse()
            true
        }
    }

    fun networkCallForLiveCourse() {
        // asynchronous operation to fetch Courses.
        progress.show()
        binding.txvNodata.visibility = View.GONE
        binding.liveCourseRecyclerView.visibility = View.VISIBLE
        val apiInterface = ApiClient.createService(LandingPageApiInterface::class.java)
        val response = apiInterface.getLiveCourseData(
            SharedPreference.getInstance().loggedInUser.id,
            filterType, searchedKeyword
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()!!
                    val jsonResponse: JSONObject

                    val arrCourseList: JSONArray?
                    val arrUpcomingCourses: JSONArray?
                    val arrOngoingCourses: JSONArray?
                    val arrBanners: JSONArray?
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("TAG", " onResponse: $jsonResponse")
//                        swipeRefreshLayout.setRefreshing(false)
//                        mProgress.dismiss()
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val pointsConversionRate = GenericUtils.getJsonObject(jsonResponse)
                                .optString("points_conversion_rate")
                            val gst = GenericUtils.getJsonObject(jsonResponse).optString("gst")
                            val cartCount =
                                GenericUtils.getJsonObject(jsonResponse).optInt("cart_count")
                            SharedPreference.getInstance().putInt(Const.CART_COUNT, cartCount)

                            var cartTV = activity!!.findViewById<TextView>(R.id.cartTV)
                            if (cartTV != null) {

                                cartTV.visibility = if (cartCount > 0) View.VISIBLE else View.GONE
                                cartTV.text = cartCount.toString()
                            }
                            viewModel.positionOrder = gson.fromJson(
                                GenericUtils.getJsonObject(jsonResponse)
                                    .optString("position_order"), PositionOrder::class.java
                            )
                            clearArrayListsValues()

                            // parse course list
                            arrCourseList =
                                GenericUtils.getJsonObject(jsonResponse).optJSONArray(Const.COURSE)
                            if (arrCourseList != null) {
                                for (i in 0 until arrCourseList.length()) {
                                    val courseLists =
                                        arrCourseList.optJSONObject(i).optJSONArray("course_list")
                                    val categoryInfo = arrCourseList.optJSONObject(i)
                                        .optJSONObject("category_info")

                                    if (courseLists == null || courseLists.length() == 0)
                                        continue
                                    else
                                        viewModel.categoryArrayList.add(
                                            gson.fromJson(
                                                categoryInfo.toString(),
                                                CourseCategory::class.java
                                            )
                                        )

                                    val coursesData = CoursesData()
                                    val courseArrayList = java.util.ArrayList<Course>()
                                    var listSize = 0
                                    val viewType = categoryInfo.optString("app_view_type")
                                    val categoryTag = categoryInfo.optString("category_tag")

//                                    listSize = if (viewType == "1" && courseLists.length() > 2) {
//                                        3
//                                    } else {
//                                        courseLists.length()
//                                    }
                                    listSize = courseLists.length()
                                    coursesData.viewItemType = Integer.parseInt(viewType)
                                    coursesData.category_info = gson.fromJson(
                                        categoryInfo.toString(),
                                        CourseCategory::class.java
                                    )
                                    for (j in 0 until listSize) {
                                        val courseObject = courseLists.getJSONObject(j)
                                        val course = gson.fromJson(
                                            courseObject.toString(),
                                            Course::class.java
                                        )
                                        course.points_conversion_rate = pointsConversionRate
                                        course.category_tag = categoryTag;
                                        course.gst = gst
                                        if (Helper.isMrpZero(courseObject)) {
                                            course.calMrp = "Free"
                                        } else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                                            course.isIs_dams = true
                                            if (courseObject.optString("for_dams") == "0") {
                                                course.calMrp = "Free"
                                            } else {
                                                if (courseObject.optString("mrp") == courseObject.optString(
                                                        "for_dams"
                                                    )
                                                ) {
                                                    course.calMrp = String.format(
                                                        "%s %s", Helper.getCurrencySymbol(),
                                                        Helper.calculatePriceBasedOnCurrency(
                                                            courseObject.optString("mrp")
                                                        )
                                                    )
                                                } else {
                                                    course.isDiscounted = true
                                                    course.calMrp =
                                                        Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(
                                                            courseObject.optString("mrp")
                                                        ) + " " +
                                                                Helper.calculatePriceBasedOnCurrency(
                                                                    courseObject.optString("for_dams")
                                                                )
                                                }
                                            }
                                        } else {
                                            course.isIs_dams = false
                                            if (courseObject.optString("non_dams") == "0") {
                                                course.calMrp = "Free"
                                            } else {
                                                if (courseObject.optString("mrp") == courseObject.optString(
                                                        "non_dams"
                                                    )
                                                ) {
                                                    course.calMrp = String.format(
                                                        "%s %s", Helper.getCurrencySymbol(),
                                                        Helper.calculatePriceBasedOnCurrency(
                                                            courseObject.optString("mrp")
                                                        )
                                                    )
                                                } else {
                                                    course.isDiscounted = true
                                                    course.calMrp =
                                                        Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(
                                                            courseObject.optString("mrp")
                                                        ) + " " +
                                                                Helper.calculatePriceBasedOnCurrency(
                                                                    courseObject.optString("non_dams")
                                                                )
                                                }
                                            }
                                        }
                                        if (course.calMrp.equals(
                                                "free",
                                                true
                                            ) || course.isIs_purchased
                                        )
                                            course.isFreeTrial = false
                                        courseArrayList.add(course)
                                    }
                                    if (courseArrayList.isNotEmpty()) {
                                        coursesData.course_list = courseArrayList
                                        viewModel.coursesDataArrayList.add(coursesData)
                                        Helper.getStorageInstance(activity).addRecordStore(
                                            Const.OFFLINE_COURSE,
                                            viewModel.coursesDataArrayList
                                        )
                                    }
                                }
                                Helper.getStorageInstance(activity)
                                    .addRecordStore("categories", viewModel.categoryArrayList)

                            }

                            // parse banner data
                            arrBanners =
                                GenericUtils.getJsonObject(jsonResponse).optJSONArray("banners")
                            for (i in 0 until arrBanners.length()) {
                                val bannerData = arrBanners.optJSONObject(i)
                                viewModel.bannersArrayList.add(
                                    gson.fromJson(
                                        bannerData.toString(),
                                        CourseBannerData::class.java
                                    )
                                )
                            }

                            // parse upcoming course data
                            arrUpcomingCourses = GenericUtils.getJsonObject(jsonResponse)
                                .optJSONArray(Const.UPCOMING)
                            if (arrUpcomingCourses != null && arrUpcomingCourses.length() > 0) {
                                for (i in 0 until arrUpcomingCourses.length()) {
                                    val upcomingCoursesData = arrUpcomingCourses.optJSONObject(i)
                                    viewModel.upcomingArrayList.add(
                                        gson.fromJson(
                                            upcomingCoursesData.toString(),
                                            UpcomingCourseData::class.java
                                        )
                                    )
                                }
                            }
                            // parse ongoing course data
                            arrOngoingCourses =
                                GenericUtils.getJsonObject(jsonResponse).optJSONArray(Const.ONGOING)
                            if (arrOngoingCourses != null && arrOngoingCourses.length() > 0) {
                                for (i in 0 until arrOngoingCourses.length()) {
                                    val ongoingCoursesData = arrOngoingCourses.optJSONObject(i)
                                    viewModel.onGoingArrayList.add(
                                        gson.fromJson(
                                            ongoingCoursesData.toString(),
                                            UpcomingCourseData::class.java
                                        )
                                    )
                                }
                            }

                            mergeAllTemplatesAsPerPosition()
                        } else {
//                            swipeRefreshLayout.setRefreshing(false)
//                            Helper.showErrorLayoutForNav("networkCallForLiveCourse", activity, 1, 0)
                            binding.txvNodata.visibility = View.VISIBLE
                            binding.liveCourseRecyclerView.visibility = View.GONE
//                            Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }

                        // Setting up Ui and Adapter in Android
                        progress.dismiss()
                        setUpUI()
                        (activity as HomeActivity).courseStatus = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.txvNodata.visibility = View.VISIBLE
                        binding.liveCourseRecyclerView.visibility = View.GONE
//                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.txvNodata.visibility = View.VISIBLE
                    binding.liveCourseRecyclerView.visibility = View.GONE
//                    Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
                    progress.dismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                binding.txvNodata.visibility = View.VISIBLE
                binding.liveCourseRecyclerView.visibility = View.GONE
//                Helper.showErrorLayoutForNav("networkCallForLiveCourse", activity, 1, 1)
            }
        })
    }

    private fun mergeAllTemplatesAsPerPosition() {
        if (!GenericUtils.isListEmpty(viewModel.bannersArrayList))
            addTemplateToList(
                viewModel.positionOrder.banners,
                getCourseData(Constants.RecordedCourseTemplates.IMAGE_BANNERS)
            )

        if (!GenericUtils.isListEmpty(viewModel.upcomingArrayList))
            addTemplateToList(0, getCourseData(Constants.RecordedCourseTemplates.UPCOMING_CLASS))

        if (!GenericUtils.isListEmpty(viewModel.onGoingArrayList))
            addTemplateToList(1, getCourseData(Constants.RecordedCourseTemplates.ONGOING_CLASS))

        if (SharedPreference.getInstance().getString(Const.RECENT_COURSE_ID).isNotEmpty())
            addTemplateToList(
                viewModel.positionOrder.recentlyViewed,
                getCourseData(Constants.RecordedCourseTemplates.RECENTLY_VIEWED_COURSES)
            )

//        addTemplateToList(viewModel.positionOrder.qlinks, getCourseData(Constants.RecordedCourseTemplates.COURSE_CATEGORY_TILES))
//        addTemplateToList(viewModel.positionOrder.categories, getCourseData(Constants.RecordedCourseTemplates.CATEGORIES))
//
//        addTemplateToList(viewModel.positionOrder.liveCourse, getCourseData(Constants.RecordedCourseTemplates.LIVE_CLASSES))
//        addTemplateToList(viewModel.positionOrder.comboPlan, getCourseData(Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN))
//        addTemplateToList(viewModel.positionOrder.bookYourSeat, getCourseData(Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT))
    }

    private fun addTemplateToList(index: Long, data: CoursesData) {
        var i: Int = 0
        if (index > 0)
            i = (index - 1).toInt()

        if (viewModel.coursesDataArrayList.size > i)
            viewModel.coursesDataArrayList.add(i, data)
        else
            viewModel.coursesDataArrayList.add(data)
    }

    private fun getCourseData(viewType: Int): CoursesData {
        var courseData = CoursesData()
        courseData.viewItemType = viewType
        return courseData
    }

    private fun clearArrayListsValues() {
        viewModel.categoryArrayList.clear()
        viewModel.coursesDataArrayList.clear()
        viewModel.tilesArrayList.clear()
        viewModel.bannersArrayList.clear()
        viewModel.upcomingArrayList.clear()
        viewModel.onGoingArrayList.clear()
    }

    private fun searchForKeywordInAPI(loadAllData: Boolean) {
        if (loadAllData) {
            filterType = ""
            searchedKeyword = ""
            eMedicozApp.getInstance().filterType = filterType
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            binding.recordedSearchFilter.setText(searchedKeyword)
            SharedPreference.getInstance()
                .putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, searchedKeyword)

            networkCallForLiveCourse()
            return
        }

        searchedKeyword = binding.recordedSearchFilter.text.trim().toString()
        eMedicozApp.getInstance().searchedKeyword = searchedKeyword

        if (searchedKeyword.isNotEmpty()) {
            networkCallForLiveCourse()
            Helper.closeKeyboard(activity)
        } else
            Toast.makeText(activity, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
    }

    private fun setUpUI() {
        if (GenericUtils.isListEmpty(viewModel.categoryArrayList)) {
//            Helper.showErrorLayoutForNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 0)
            binding.txvNodata.visibility = View.VISIBLE
            binding.liveCourseRecyclerView.visibility = View.GONE
//            Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            liveCourseMainAdapter = LiveCoursesMainAdapter(requireContext(), viewModel)
            binding.liveCourseRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.liveCourseRecyclerView.adapter = liveCourseMainAdapter
            binding.recordedSearchFilter.setText(searchedKeyword)

// Restore state
            binding.liveCourseRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);
        } catch (e: Exception) {
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        lcBinding = null
    }

}