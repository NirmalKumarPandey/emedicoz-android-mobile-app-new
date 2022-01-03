package com.emedicoz.app.courses.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.login.activity.AppUpdateActivity
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.Registration
import com.emedicoz.app.modelo.User
import com.emedicoz.app.modelo.courses.*
import com.emedicoz.app.recordedCourses.adapters.RecordedCourseMainAdapter
import com.emedicoz.app.recordedCourses.model.RecordedCoursesViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_recorded.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val UPDATE_REQUEST_CODE = 123

class RecordedFragment : Fragment(R.layout.fragment_recorded) {
    private var registration: Registration? = null
    private var user1: User? = null
    private lateinit var recordedCourseMainAdapter: RecordedCourseMainAdapter
    lateinit var viewModel: RecordedCoursesViewModel
    private lateinit var progress: Progress
    private var filterType: String = ""
    var searchedKeyword: String = ""
    var recyclerViewState: Parcelable? = null
    private lateinit var linearOne: LinearLayout
    private lateinit var btnShare: Button
    lateinit var btnUpdate: Button
    lateinit var cardUpadteApp: CardView
    lateinit var appUpdateManager: AppUpdateManager
    private val appUpdateManager2 by lazy { AppUpdateManagerFactory.create(requireActivity()) }
    var user: User? = null
    lateinit var ivProfileImage: ImageView
    lateinit var tvProfileName: TextView
    lateinit var tvSlogan: TextView
    var damsDialogTV: TextView? = null
    private var menu: Menu? = null

    //private lateinit var tvCount: TextView
    var dialog: Dialog? = null
    var damsToken: String? = null
    var damsPass: String? = null
    var mDAMSET: EditText? = null
    var mDAMSPasswordET: EditText? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivProfileImage = view.findViewById(R.id.ivProfileImage)

        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvSlogan = view.findViewById(R.id.tvSlogan)
        damsDialogTV = view.findViewById(R.id.damsDialogTV);

        progress = Progress(activity)
        progress.setCancelable(false)
        viewModel = ViewModelProvider(this).get(RecordedCoursesViewModel::class.java)
        networkCallForRecordedCourse()

        linearOne = view.findViewById(R.id.linearOne)
        btnShare = view.findViewById(R.id.btnShare)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        cardUpadteApp = view.findViewById(R.id.cardUpadteApp)

        if (TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) damsDialogTV!!.visibility =
            View.VISIBLE
        Log.e("RecordedCourse", "onCreateView: ")
        damsDialogTV!!.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.blink))

        damsDialogTV!!.setOnClickListener {
            getDAMSLoginDialog(
                activity
            )
        }
        (activity as HomeActivity).floatingActionButton.visibility = View.GONE
        //call function in share btn action
        shareApp()
        //app updated for forcefully dialog
        appUpdateManager = AppUpdateManagerFactory.create(requireActivity())

        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        System.out.println("Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                if (BuildConfig.DEBUG) {

                } else {
                    val intentUpdate = Intent(activity, AppUpdateActivity::class.java)
                    startActivity(intentUpdate)
                    cardUpadteApp.visibility = View.VISIBLE
                }

            } else {
//                Snackbar.make(btnUpdate, "No Update available", Snackbar.LENGTH_LONG).show()
                cardUpadteApp.visibility = View.GONE
            }
        }

        //searching implements
        if (!TextUtils.isEmpty(searchedKeyword)) {
            img_clear_search.visibility = View.VISIBLE
            img_search_view.visibility = View.GONE
            recordedSearchFilter.setText(searchedKeyword)
            recordedSearchFilter.setSelection(Helper.GetText(recordedSearchFilter).length)
        } else {
            img_clear_search.visibility = View.GONE
            img_search_view.visibility = View.VISIBLE
            recordedSearchFilter.setText("")
        }

        filterTv.setOnClickListener {
            showPopMenu(it)
        }
        img_clear_search.setOnClickListener {
            searchForKeywordInAPI(true)
        }


        recordedSearchFilter.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchForKeywordInAPI(false)
                return@setOnEditorActionListener true
            }
            false
        }

        recordedSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    img_clear_search.visibility = View.VISIBLE
                    img_search_view.visibility = View.GONE
                } else {
                    img_clear_search.visibility = View.GONE
                    img_search_view.visibility = View.VISIBLE
                    //searchForKeywordInAPI(true)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (activity is HomeActivity) {
            (activity as HomeActivity).toolbarHeader.isVisible = true
            (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
        }
        networkCallForRecordedCourse()
        callOnResume()
    }

    @SuppressLint("SetTextI18n")
    fun callOnResume() {
        user = SharedPreference.getInstance().loggedInUser
        user?.name = Helper.CapitalizeText(user?.name)
        tvProfileName.text = "Hi," + user?.name + "!"
        tvSlogan.text = user?.text_message?.text_message
        if (!TextUtils.isEmpty(user?.profile_picture)) {
            Glide.with(this).load(user?.profile_picture).into(ivProfileImage)
        }
    }

    //function created for filter
    private fun showPopMenu(view: View) {
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
            searchedKeyword = recordedSearchFilter.text.trim().toString()
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            eMedicozApp.getInstance().filterType = filterType
            networkCallForRecordedCourse()
            true
        }
    }

    //------------search for keywork implementation------------------------------------
    private fun searchForKeywordInAPI(loadAllData: Boolean) {
        if (loadAllData) {
            filterType = ""
            searchedKeyword = ""
            eMedicozApp.getInstance().filterType = filterType
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            recordedSearchFilter.setText(searchedKeyword)
            SharedPreference.getInstance()
                .putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, searchedKeyword)

            networkCallForRecordedCourse()
            return
        }

        searchedKeyword = recordedSearchFilter.text.trim().toString()
        eMedicozApp.getInstance().searchedKeyword = searchedKeyword


        if (searchedKeyword.isNotEmpty()) {
            networkCallForRecordedCourse()
            Helper.closeKeyboard(activity)
        } else
            Toast.makeText(activity, getString(R.string.enter_search_query), Toast.LENGTH_SHORT)
                .show()
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


    //network call api create function
    private fun networkCallForRecordedCourse() {

        progress.show()
        txv_nodata.visibility = View.GONE
        recordedCourseRecyclerView.isNestedScrollingEnabled = false
        val apiInterface = ApiClient.createService(LandingPageApiInterface::class.java)
        val response = apiInterface.getRecordedCourseData(
            SharedPreference.getInstance().loggedInUser.id,
            filterType, searchedKeyword
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                //swipeRefreshLayout.isRefreshing = false
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
                            val pointsConversionRate = GenericUtils.getJsonObject(jsonResponse)
                                .optString("points_conversion_rate")
                            val gst = GenericUtils.getJsonObject(jsonResponse).optString("gst")
                            val cartCount =
                                GenericUtils.getJsonObject(jsonResponse).optInt("cart_count")
                            SharedPreference.getInstance().putInt(Const.CART_COUNT, cartCount)

//                            var cartTV = activity!!.findViewById<TextView>(R.id.cartTV)
//                            if (cartTV != null) {
//
//                                cartTV.visibility = if (cartCount > 0) View.VISIBLE else View.GONE
//                                cartTV.text = cartCount.toString()
//                            }

                            viewModel.positionOrder =
                                gson.fromJson(
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
                                        viewModel.orignalDataArrayList.add(coursesData)
                                        Helper.getStorageInstance(activity!!).addRecordStore(
                                            Const.OFFLINE_COURSE,
                                            viewModel.coursesDataArrayList
                                        )
                                    }
                                }
                                Helper.getStorageInstance(activity)
                                    .addRecordStore("categories", viewModel.categoryArrayList)
                            }

                            // parse top tiles data
                            arrTiles =
                                GenericUtils.getJsonObject(jsonResponse).optJSONArray("qlinks")
                            for (i in 0 until arrTiles.length()) {
                                val tileData = arrTiles.optJSONObject(i)
                                viewModel.tilesArrayList.add(
                                    gson.fromJson(
                                        tileData.toString(),
                                        CourseCatTileData::class.java
                                    )
                                )
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

                            mergeAllTemplatesAsPerPosition()
                        } else {
//                            swipeRefreshLayout.setRefreshing(false)
                            try {

                                txv_nodata.visibility = View.VISIBLE
                                recordedCourseRecyclerView.visibility = View.GONE
                            } catch (e: Exception) {

                            }
//                            Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show()
//                            Helper.showErrorLayoutForNav("networkCallForRecordedCourse", activity, 1, 0)
                            RetrofitResponse.getApiData(
                                activity,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }

                        // Setting up Ui and Adapter in Android
                        setUpUI()
                        progress.dismiss()

                        (activity as HomeActivity).courseStatus = false
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        txv_nodata.visibility = View.VISIBLE
                        recordedCourseRecyclerView.visibility = View.GONE
                    }
                } else {
                    txv_nodata.visibility = View.VISIBLE
                    recordedCourseRecyclerView.visibility = View.GONE
                    progress.dismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //swipeRefreshLayout.isRefreshing = false
                txv_nodata.visibility = View.VISIBLE
                recordedCourseRecyclerView.visibility = View.GONE
            }
        })
    }

    private fun mergeAllTemplatesAsPerPosition() {
        if (!GenericUtils.isListEmpty(viewModel.tilesArrayList))
            addTemplateToList(
                viewModel.positionOrder.qlinks.toInt(),
                getCourseData(Constants.RecordedCourseTemplates.COURSE_CATEGORY_TILES)
            )
        if (!GenericUtils.isListEmpty(viewModel.categoryArrayList))
            addTemplateToList(
                viewModel.positionOrder.categories.toInt(),
                getCourseData(Constants.RecordedCourseTemplates.CATEGORIES)
            )
        if (!GenericUtils.isListEmpty(viewModel.bannersArrayList))
            addTemplateToList(
                viewModel.positionOrder.banners.toInt(),
                getCourseData(Constants.RecordedCourseTemplates.IMAGE_BANNERS)
            )

        val masterHitData = SharedPreference.getInstance().masterHitResponse;
        if (masterHitData != null) {
            if (masterHitData.show_ppe_course != null &&
                masterHitData.show_ppe_course.equals("1", ignoreCase = true)
            )
                addTemplateToList(
                    viewModel.positionOrder.bookYourSeat.toInt(),
                    getCourseData(Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT)
                )

            if (masterHitData.display_combo != null &&
                masterHitData.display_combo.equals("1", ignoreCase = true)
            )
                addTemplateToList(
                    viewModel.positionOrder.comboPlan.toInt(),
                    getCourseData(Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN)
                )

            if (masterHitData.show_live_course != null &&
                masterHitData.show_live_course.equals("1", ignoreCase = true)
            )
                addTemplateToList(
                    viewModel.positionOrder.liveCourse.toInt(),
                    getCourseData(Constants.RecordedCourseTemplates.LIVE_CLASSES)
                )

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


    //create function call all lists are clear
    private fun clearArrayListsValues() {
        viewModel.categoryArrayList.clear()
        viewModel.coursesDataArrayList.clear()
        viewModel.orignalDataArrayList.clear()
        viewModel.tilesArrayList.clear()
        viewModel.bannersArrayList.clear()
    }

    private fun setUpUI() {
        if (GenericUtils.isListEmpty(viewModel.categoryArrayList)) {
            txv_nodata.visibility = View.VISIBLE
            recordedCourseRecyclerView.visibility = View.VISIBLE
            return
        }
        try {
            recordedCourseMainAdapter = RecordedCourseMainAdapter(requireContext(), viewModel)
            recordedCourseRecyclerView.layoutManager = LinearLayoutManager(context)
            recordedCourseRecyclerView.adapter = recordedCourseMainAdapter
            recordedSearchFilter.setText(searchedKeyword)

            // Restore state
            recordedCourseRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);
            recordedCourseRecyclerView.isNestedScrollingEnabled = false
            linearOne.visibility = View.VISIBLE

        } catch (e: Exception) {
        }

    }

    private fun getDAMSLoginDialog(ctx: Activity?) {

        // custom dialog
        dialog = Dialog(ctx!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.layout_dams_login)
        dialog!!.setCancelable(false)
        // set the custom dialog components - text, image and button
        val mDAMSLoginbtn: Button = dialog!!.findViewById(R.id.loginBtn)
        val mDAMScancelbtn: Button = dialog!!.findViewById(R.id.cancelBtn)
        mDAMSET = dialog!!.findViewById(R.id.damstokenET)
        mDAMSPasswordET = dialog!!.findViewById(R.id.damspassET)
        // if button is clicked, close the custom dialog
        mDAMSLoginbtn.setOnClickListener { checkDAMSLoginValidation() }
        mDAMScancelbtn.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }

    private fun checkDAMSLoginValidation() {
        damsToken = Helper.GetText(mDAMSET)
        damsPass = Helper.GetText(mDAMSPasswordET)
        var isDataValid = true
        if (TextUtils.isEmpty(damsToken)) isDataValid =
            Helper.DataNotValid(mDAMSET) else if (TextUtils.isEmpty(damsPass)) isDataValid =
            Helper.DataNotValid(mDAMSPasswordET)
        if (isDataValid) {
            if (user == null)
                user = User.newInstance()
            user!!.dams_username = damsToken
            user!!.dams_password = damsPass
            networkCallForUpdateDamsToken()
        }
    }

    private fun networkCallForUpdateDamsToken() {
        progress.show()
        val apis = ApiClient.createService(RegFragApis::class.java)
        val response =
            apis.updateDamsToken(user!!.id, user!!.dams_username, user!!.dams_password)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val gson = Gson()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
                            val jsonObject1 = GenericUtils.getJsonObject(jsonResponse)
                            user1 = gson.fromJson(jsonObject1.toString(), User::class.java)
                            user = User.newInstance()
                            user = User.copyInstance(user1)
                            registration =
                                if (user!!.user_registration_info == null) Registration() else user!!.user_registration_info
                            user!!.user_registration_info = registration
                            SharedPreference.getInstance().loggedInUser = user
                            damsDialogTV!!.visibility = View.GONE
                        } else {
                            Toast.makeText(
                                activity,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
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
                progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_UPDATE_DAMS_TOKEN, activity, 1, 1)
            }
        })
    }

}