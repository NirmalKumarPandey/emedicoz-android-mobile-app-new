package com.emedicoz.app.recordedCourses.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.common.BaseABNavActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.RecordedCoursesFragmentBinding
import com.emedicoz.app.login.activity.AppUpdateActivity
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.courses.*
import com.emedicoz.app.recordedCourses.adapters.RecordedCourseMainAdapter
import com.emedicoz.app.recordedCourses.model.RecordedCoursesViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface
import com.emedicoz.app.utilso.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val UPDATE_REQUEST_CODE = 123

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RecordedCoursesFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(fragType: String): RecordedCoursesFragment {
            val fragment = RecordedCoursesFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            fragment.arguments = args
            return fragment
        }
    }

    private val appUpdateManager2 by lazy { AppUpdateManagerFactory.create(requireActivity()) }
    // Variables Declaration

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progress: Progress
    private var filterType: String = ""
    var searchedKeyword: String = ""
    private lateinit var linearOne: LinearLayout
    private lateinit var btnShare: Button
    lateinit var btnUpdate: Button
    lateinit var appUpdateManager: AppUpdateManager


    private lateinit var rcBinding: RecordedCoursesFragmentBinding
    var recyclerViewState: Parcelable? = null

    // This property is only valid between onCreateView and onDestroyView.
    val binding get() = rcBinding
    lateinit var viewModel: RecordedCoursesViewModel

    private lateinit var recordedCourseMainAdapter: RecordedCourseMainAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rcBinding = RecordedCoursesFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        linearOne = view.findViewById(R.id.linearOne)
        btnShare = view.findViewById(R.id.btnShare)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        //call function in share btn action
        shareApp()
        progress = Progress(activity)
        progress.setCancelable(false)
        viewModel = ViewModelProvider(this).get(RecordedCoursesViewModel::class.java)

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

        binding.filterTv.setOnClickListener {
            showPopMenu(it)
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
//            binding.recordedCourseRecyclerView.isNestedScrollingEnabled = false
//            binding.recordedCourseRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//
//                    val firstPos: Int = (binding.recordedCourseRecyclerView.layoutManager as LinearLayoutManager)
//                        .findFirstVisibleItemPosition()
//                    swipeRefreshLayout.isEnabled = firstPos == 0 && !swipeRefreshLayout.isRefreshing
//                    Log.i("scroll position", firstPos.toString())
//                }
//            })
//        }

        if (!BuildConfig.DEBUG) {
            //app updated for forcefully dialog
            appUpdateManager = AppUpdateManagerFactory.create(requireActivity())

            val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
            // Checks that the platform will allow the specified type of update.
            println("Checking for updates")
            appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    // Request the update.
                    val intentUpdate = Intent(activity, AppUpdateActivity::class.java)
                    startActivity(intentUpdate)

                } else {
                    Snackbar.make(btnUpdate, "No Update available", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    //function create for share button and check updated
    private fun shareApp() {
        btnShare.setOnClickListener(View.OnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here")
            val app_url = "https://play.google.com/store/apps/details?id=com.emedicoz.app"
            shareIntent.putExtra(Intent.EXTRA_TEXT, app_url)
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        })

        //check update
        btnUpdate.setOnClickListener(View.OnClickListener {
            // Returns an intent object that you use to check for an update.
            val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
            // Checks that the platform will allow the specified type of update.
            System.out.println("Checking for updates")
            appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    // Request the update.
                    updatedChecker()
//                    val intentUpdate = Intent(activity, AppUpdateActivity::class.java)
//                    startActivity(intentUpdate)

                } else {
                    Snackbar.make(btnUpdate, "No Update available", Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    //fun created for update checker avaliable or not---start
    private fun updatedChecker() {
        appUpdateManager2.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appUpdateManager2.startUpdateFlowForResult(
                    it,
                    AppUpdateType.IMMEDIATE,
                    requireActivity(),
                    UPDATE_REQUEST_CODE
                )
            }
        }.addOnFailureListener {
            Log.e("ImmediateUpdateActivity", "Failed to check for update: $it")
        }
    }

    //-------------------------end

    //call start activity for result and update then activity finish---------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            requireActivity().finish()
        }
    }
    //--------------------------end

    private fun searchForKeywordInAPI(loadAllData: Boolean) {
        if (loadAllData) {
            filterType = ""
            searchedKeyword = ""
            eMedicozApp.getInstance().filterType = filterType
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            binding.recordedSearchFilter.setText(searchedKeyword)
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, searchedKeyword)

            networkCallForRecordedCourse()
            return
        }

        searchedKeyword = binding.recordedSearchFilter.text.trim().toString()
        eMedicozApp.getInstance().searchedKeyword = searchedKeyword


        if (searchedKeyword.isNotEmpty()) {
            networkCallForRecordedCourse()
            Helper.closeKeyboard(activity)
        } else
            Toast.makeText(activity, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
//        if (activity is BaseABNavActivity) {
//            (activity as BaseABNavActivity).manageToolbar(Constants.ScreenName.COURSES)
//        }
        // Save state
        recyclerViewState = binding.recordedCourseRecyclerView.layoutManager?.onSaveInstanceState()
        binding.recordedCourseRecyclerView.isNestedScrollingEnabled = false
        networkCallForRecordedCourse()
        var cartTV = activity!!.findViewById<TextView>(R.id.cartTV)
        if (cartTV != null) {

            var cartCount = SharedPreference.getInstance().getInt(Const.CART_COUNT)
            cartTV.visibility = if (cartCount > 0) View.VISIBLE else View.GONE
            cartTV.text = cartCount.toString()
        }

//        viewModel.coursesDataArrayList = Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_COURSE) as ArrayList<CoursesData>
//        viewModel.categoryArrayList = Helper.getStorageInstance(activity).getRecordObject("categories") as ArrayList<CourseCategory>
//        networkCallForRecordedCourse()
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
            networkCallForRecordedCourse()
            true
        }
    }

    fun networkCallForRecordedCourse() {
        // asynchronous operation to fetch Courses.
//        binding.recordedSearchFilter.setText(searchedKeyword)
        progress.show()
        binding.txvNodata.visibility = View.GONE
        binding.recordedCourseRecyclerView.isNestedScrollingEnabled = false
        binding.recordedCourseRecyclerView.visibility = View.VISIBLE
        val apiInterface = ApiClient.createService(LandingPageApiInterface::class.java)
        val response = apiInterface.getRecordedCourseData(
            SharedPreference.getInstance().loggedInUser.id,
            filterType, searchedKeyword
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
              //  swipeRefreshLayout.isRefreshing = false
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()!!
                    val jsonResponse: JSONObject

                    val arrCourseList: JSONArray?
                    val arrTiles: JSONArray?
                    val arrBanners: JSONArray?
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("TAG", " onResponse: $jsonResponse")
//                        swipeRefreshLayout.setRefreshing(false)
//                        mProgress.dismiss()
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val pointsConversionRate = GenericUtils.getJsonObject(jsonResponse).optString("points_conversion_rate")
                            val gst = GenericUtils.getJsonObject(jsonResponse).optString("gst")
                            val cartCount = GenericUtils.getJsonObject(jsonResponse).optInt("cart_count")
                            SharedPreference.getInstance().putInt(Const.CART_COUNT, cartCount)

                            var cartTV = activity!!.findViewById<TextView>(R.id.cartTV)
                            if (cartTV != null) {

                                cartTV.visibility = if (cartCount > 0) View.VISIBLE else View.GONE
                                cartTV.text = cartCount.toString()
                            }

                            viewModel.positionOrder =
                                gson.fromJson(GenericUtils.getJsonObject(jsonResponse).optString("position_order"), PositionOrder::class.java)
                            clearArrayListsValues()

                            // parse course list
                            arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray(Const.COURSE)
                            if (arrCourseList != null) {
                                for (i in 0 until arrCourseList.length()) {
                                    val courseLists = arrCourseList.optJSONObject(i).optJSONArray("course_list")
                                    val categoryInfo = arrCourseList.optJSONObject(i).optJSONObject("category_info")

                                    if (courseLists == null || courseLists.length() == 0)
                                        continue
                                    else
                                        viewModel.categoryArrayList.add(gson.fromJson(categoryInfo.toString(), CourseCategory::class.java))


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
                                    coursesData.category_info = gson.fromJson(categoryInfo.toString(), CourseCategory::class.java)
                                    for (j in 0 until listSize) {
                                        val courseObject = courseLists.getJSONObject(j)
                                        val course = gson.fromJson(courseObject.toString(), Course::class.java)
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
                                                if (courseObject.optString("mrp") == courseObject.optString("for_dams")) {
                                                    course.calMrp = String.format(
                                                        "%s %s", Helper.getCurrencySymbol(),
                                                        Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp"))
                                                    )
                                                } else {
                                                    course.isDiscounted = true
                                                    course.calMrp = Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(
                                                        courseObject.optString("mrp")
                                                    ) + " " +
                                                            Helper.calculatePriceBasedOnCurrency(courseObject.optString("for_dams"))
                                                }
                                            }
                                        } else {
                                            course.isIs_dams = false
                                            if (courseObject.optString("non_dams") == "0") {
                                                course.calMrp = "Free"
                                            } else {
                                                if (courseObject.optString("mrp") == courseObject.optString("non_dams")) {
                                                    course.calMrp = String.format(
                                                        "%s %s", Helper.getCurrencySymbol(),
                                                        Helper.calculatePriceBasedOnCurrency(courseObject.optString("mrp"))
                                                    )
                                                } else {
                                                    course.isDiscounted = true
                                                    course.calMrp = Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(
                                                        courseObject.optString("mrp")
                                                    ) + " " +
                                                            Helper.calculatePriceBasedOnCurrency(courseObject.optString("non_dams"))
                                                }
                                            }
                                        }
                                        if (course.calMrp.equals("free", true) || course.isIs_purchased)
                                            course.isFreeTrial = false
                                        courseArrayList.add(course)
                                    }

                                    if (courseArrayList.isNotEmpty()) {
                                        coursesData.course_list = courseArrayList
                                        viewModel.coursesDataArrayList.add(coursesData)
                                        viewModel.orignalDataArrayList.add(coursesData)
                                        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_COURSE, viewModel.coursesDataArrayList)
                                    }
                                }
                                Helper.getStorageInstance(activity).addRecordStore("categories", viewModel.categoryArrayList)
                            }

                            // parse top tiles data
                            arrTiles = GenericUtils.getJsonObject(jsonResponse).optJSONArray("qlinks")
                            for (i in 0 until arrTiles.length()) {
                                val tileData = arrTiles.optJSONObject(i)
                                viewModel.tilesArrayList.add(gson.fromJson(tileData.toString(), CourseCatTileData::class.java))
                            }

                            // parse banner data
                            arrBanners = GenericUtils.getJsonObject(jsonResponse).optJSONArray("banners")
                            for (i in 0 until arrBanners.length()) {
                                val bannerData = arrBanners.optJSONObject(i)
                                viewModel.bannersArrayList.add(gson.fromJson(bannerData.toString(), CourseBannerData::class.java))
                            }

                            mergeAllTemplatesAsPerPosition()
                        } else {
//                            swipeRefreshLayout.setRefreshing(false)
                            try {
                                binding.txvNodata.visibility = View.VISIBLE
                                binding.recordedCourseRecyclerView.visibility = View.GONE
                            } catch (e: Exception) {

                            }
//                            Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
//                            Helper.showErrorLayoutForNav("networkCallForRecordedCourse", activity, 1, 0)
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }

                        // Setting up Ui and Adapter in Android
                        setUpUI()
                        progress.dismiss()
                        (activity as BaseABNavActivity).courseStatus = false
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        binding.txvNodata.visibility = View.VISIBLE
                        binding.recordedCourseRecyclerView.visibility = View.GONE
//                        Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
//                        Helper.showErrorLayoutForNav("networkCallForRecordedCourse", activity, 1, 0)
                    }
                } else {
                    binding.txvNodata.visibility = View.VISIBLE
                    binding.recordedCourseRecyclerView.visibility = View.GONE
//                    Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
//                    Helper.showErrorLayoutForNav("networkCallForRecordedCourse", activity, 1, 1)
                    progress.dismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
              //  swipeRefreshLayout.isRefreshing = false
                binding.txvNodata.visibility = View.VISIBLE
                binding.recordedCourseRecyclerView.visibility = View.GONE
//                Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
//                Helper.showErrorLayoutForNav("networkCallForRecordedCourse", activity, 1, 1)
            }
        })
    }

    private fun mergeAllTemplatesAsPerPosition() {
        if (!GenericUtils.isListEmpty(viewModel.tilesArrayList))
            addTemplateToList(viewModel.positionOrder.qlinks.toInt(), getCourseData(Constants.RecordedCourseTemplates.COURSE_CATEGORY_TILES))
        if (!GenericUtils.isListEmpty(viewModel.categoryArrayList))
            addTemplateToList(viewModel.positionOrder.categories.toInt(), getCourseData(Constants.RecordedCourseTemplates.CATEGORIES))
        if (!GenericUtils.isListEmpty(viewModel.bannersArrayList))
            addTemplateToList(viewModel.positionOrder.banners.toInt(), getCourseData(Constants.RecordedCourseTemplates.IMAGE_BANNERS))

        val masterHitData = SharedPreference.getInstance().masterHitResponse;
        if (masterHitData != null) {
            if (masterHitData.show_ppe_course != null &&
                masterHitData.show_ppe_course.equals("1", ignoreCase = true)
            )
                addTemplateToList(viewModel.positionOrder.bookYourSeat.toInt(), getCourseData(Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT))

            if (masterHitData.display_combo != null &&
                masterHitData.display_combo.equals("1", ignoreCase = true)
            )
                addTemplateToList(viewModel.positionOrder.comboPlan.toInt(), getCourseData(Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN))

            if (masterHitData.show_live_course != null &&
                masterHitData.show_live_course.equals("1", ignoreCase = true)
            )
                addTemplateToList(viewModel.positionOrder.liveCourse.toInt(), getCourseData(Constants.RecordedCourseTemplates.LIVE_CLASSES))

        }
        if (SharedPreference.getInstance().getString(Const.RECENT_COURSE_ID).isNotEmpty())
            addTemplateToList(
                viewModel.positionOrder.recentlyViewed.toInt(),
                getCourseData(Constants.RecordedCourseTemplates.RECENTLY_VIEWED_COURSES)
            )

    }

    private fun addTemplateToList(index: Int, data: CoursesData) {
        var i: Int = 0
        if (index > 0)
            i = index - 1
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
        viewModel.orignalDataArrayList.clear()
        viewModel.tilesArrayList.clear()
        viewModel.bannersArrayList.clear()
    }

    private fun setUpUI() {

        if (context is HomeActivity) {
            (context as BaseABNavActivity).setSidePanelCourse(viewModel.orignalDataArrayList)
        }
        if (GenericUtils.isListEmpty(viewModel.categoryArrayList)) {
            binding.txvNodata.visibility = View.VISIBLE
            binding.recordedCourseRecyclerView.visibility = View.GONE
//            Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
            //Helper.showErrorLayoutForNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 0)
            return
        }
        try {
            recordedCourseMainAdapter = RecordedCourseMainAdapter(context!!, viewModel)
            binding.recordedCourseRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.recordedCourseRecyclerView.adapter = recordedCourseMainAdapter
            binding.recordedSearchFilter.setText(searchedKeyword)
// Restore state
            binding.recordedCourseRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);
            binding.recordedCourseRecyclerView.isNestedScrollingEnabled = false
            linearOne.visibility = View.VISIBLE

        } catch (e: Exception) {
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        rcBinding = null
    }


}