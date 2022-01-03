package com.emedicoz.app.courseDetail.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import androidx.webkit.WebSettingsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.emedicoz.app.BuildConfig
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courseDetail.adapter.CoursesDescriptionTabAdapter
import com.emedicoz.app.courseDetail.adapter.FaqDescriptionAdapter
import com.emedicoz.app.courseDetail.adapter.StudentFeedbackAdapter
import com.emedicoz.app.courses.activity.FAQActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.RecordedDetailsDescriptionFragmentBinding
import com.emedicoz.app.modelo.HelpAndSupport
import com.emedicoz.app.modelo.courses.SingleCourseData
import com.emedicoz.app.modelo.liveclass.courses.*
import com.emedicoz.app.recordedCourses.model.RecordedDetailsDescriptionViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface
import com.emedicoz.app.support.HelpSupportActivity
import com.emedicoz.app.templateAdapters.HelpAndSupportAdapter
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.network.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_new_and_artical_detail.*
import kotlinx.android.synthetic.main.recorded_details_description_fragment.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class CourseDetailsDescriptionFragment : Fragment() {

    private lateinit var progress: Progress
    private var TAG = "CourseDetailsDescriptionFragment"
    private lateinit var tableText: String

    companion object {
        @JvmStatic
        fun newInstance(descriptionData: DescriptionData): CourseDetailsDescriptionFragment {
            val fragment = CourseDetailsDescriptionFragment()
            val args = Bundle()
            args.putSerializable(
                    Const.COURSE_DESC,
                    descriptionData
            )
            fragment.arguments = args
            return fragment
        }

    }

    // Declarations
    private lateinit var dBinding: RecordedDetailsDescriptionFragmentBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = dBinding
    private lateinit var descriptionData: DescriptionData
    private lateinit var instructor: DescInstructorData
    private lateinit var descriptionCourseReviews: ArrayList<DescCourseReview>
    private lateinit var descriptionCourseReviewsFull: ArrayList<DescCourseReview>
    private lateinit var descriptionFaq: List<DescriptionFAQ>
    private lateinit var viewModel: RecordedDetailsDescriptionViewModel
    private lateinit var descriptionDataBasic: DescriptionBasic
    private lateinit var singleCourseData: SingleCourseData
    private lateinit var descriptionResponse: DescriptionResponse
    private lateinit var descriptionData1: DescriptionData


    lateinit var popupWindow: PopupWindow
    lateinit var crossHelp: ImageView
    lateinit var recyclerView_help: RecyclerView
    lateinit var editTextHelp: EditText
    lateinit var questionQuery: String
    lateinit var helpImageCross: ImageView
    lateinit var helpImageSearch: ImageView
    lateinit var btnStartChat: LinearLayout
    lateinit var startChat: Button
    lateinit var helpAndSupportArrayList: java.util.ArrayList<HelpAndSupport>
    lateinit var mProgress: Progress

    lateinit var helpView: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        descriptionData = args?.getSerializable(Const.COURSE_DESC) as DescriptionData
        instructor = descriptionData.instructorData
        descriptionCourseReviews = descriptionData.courseReview as ArrayList<DescCourseReview>
        descriptionCourseReviewsFull = descriptionData.courseReview as ArrayList<DescCourseReview>
        descriptionFaq = descriptionData.faq
        descriptionDataBasic = descriptionData.basic
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dBinding = RecordedDetailsDescriptionFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        mProgress = Progress(context)
        mProgress.setCancelable(false)

        SharedPreference.getInstance().saveMyCourseStatus(Const.MY_COURSE_RATING, "true")
        SharedPreference.getInstance().saveMyCourseId(Const.MY_COURSE_ID, descriptionDataBasic.id)

        try {
            progress = Progress(activity)
            progress.setCancelable(false)

            binding.apply {
                singleCourseData = getData(descriptionDataBasic)!!
                networkCallForCourseInfoRaw()

                if (descriptionData.basic.learn_description != null && descriptionData.basic.learn_description.isNotEmpty()) {
                    learnLayout.visibility = View.VISIBLE
                    // tvCourseSummaryShowMore.text = HtmlCompat.fromHtml(descriptionData.basic.learn_description, 0)
                    //tvCourseSummaryShowMore.text = HtmlCompat.fromHtml("<style>table, th, td {border: 0.5px solid black;border-collapse: collapse;}</style>" + descriptionData.basic.learn_description, 0)
                    //tvCourseSummaryShowMore.loadData(descriptionData.basic.learn_description, "text/html", "UTF-8")



                    // tvCourseSummaryShowMore.text = HtmlCompat.fromHtml(s, 0)
                    if (SharedPreference.getInstance().getBoolean(Const.DARK_MODE)) {
                        WebSettingsCompat.setForceDark(tvCourseSummaryShowMore.settings, WebSettingsCompat.FORCE_DARK_ON)
                        val tableText :String = "<style>table, th, td {border: 0.5px solid black;border-collapse: collapse;}</style>"
                        // val tableText :String = "<style>table, th, td {border: 1px solid white; }</style>"

                        // val tableText :String = "<style>table, th, td { border: 0.5px solid #FFFFFF;border-collapse: collapse;}th,td{color:white;}</style>"
                        var Query: String = descriptionData.basic.learn_description
                        val s = StringBuilder().append(tableText).append(Query).toString()
                        /*tvCourseSummaryShowMore.getSettings().setLoadWithOverviewMode(true)
                        tvCourseSummaryShowMore.getSettings().getAllowUniversalAccessFromFileURLs()
                        tvCourseSummaryShowMore.getSettings().javaScriptCanOpenWindowsAutomatically
                        tvCourseSummaryShowMore.getSettings().setJavaScriptEnabled(true)*/
                        //tvCourseSummaryShowMore.loadData(s, "text/html", "UTF-8")
                        tvCourseSummaryShowMore.loadDataWithBaseURL(null, s, "text/html", "UTF-8", null)

                    } else {
                        // WebSettingsCompat.setForceDark(webview.settings, WebSettingsCompat.FORCE_DARK_OFF)
                        val tableText :String= "<style>table, th, td {border: 0.5px solid black;border-collapse: collapse;}</style>"
                        var Query: String = descriptionData.basic.learn_description
                        val s = StringBuilder().append(tableText).append(Query).toString()
                        tvCourseSummaryShowMore.getSettings().setLoadWithOverviewMode(true)
                        tvCourseSummaryShowMore.getSettings().getAllowUniversalAccessFromFileURLs()
                        tvCourseSummaryShowMore.getSettings().javaScriptCanOpenWindowsAutomatically
                        tvCourseSummaryShowMore.getSettings().setJavaScriptEnabled(true)
                        tvCourseSummaryShowMore.loadData(s, "text/html", "UTF-8")
                    }




                    seeTv.setOnClickListener {
                        // Gets the layout params that will allow you to resize the layout
                        val params: ViewGroup.LayoutParams = webviewLV.layoutParams
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        params.height =ViewGroup.LayoutParams.MATCH_PARENT
                        webviewLV.layoutParams = params
                        tvCourseSummaryShowMore.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY)
                        tvCourseSummaryShowMore.isVerticalScrollBarEnabled
                        tvCourseSummaryShowMore.isHorizontalFadingEdgeEnabled
                        tvCourseSummaryShowMore.isNestedScrollingEnabled
                        // tvCourseSummaryShowMore.loadData(s, "text/html", "UTF-8")
                    }

                } else {
                    learnLayout.visibility = View.GONE
                }
                // tvCourseSummaryShowMore.text = getString(R.string.about_course)

                tvCourseSummaryShowMore.setOnClickListener {
                    try {
                        val viewPager = getViewPager()
                        viewPager?.let { updatePagerHeightForChild(view, it) }
                    } catch (e: Exception) {
                    }
                }

                incLayout1.apply {
                    if (instructor.name.isNotEmpty()) {
                        instructorName.text = instructor.name
                    }
                    if (instructor.instructorDesignation.isNotEmpty()) {
                        instructorDesignation.text = instructor.instructorDesignation
                    }
                    Glide.with(context!!)
                            .load(instructor.profilePic)
                            .apply(RequestOptions.placeholderOf(com.emedicoz.app.R.drawable.profile_image).error(com.emedicoz.app.R.drawable.profile_image))
                            .into(instructorProfileImg)
                    if (instructor.instructorRating.isNotEmpty()) {
                        ratingDetails.text = "${instructor.instructorRating} Instructor Rating"
                    }
                    if (instructor.instructorReviews.isNotEmpty()) {
                        reviewDetails.text = "${instructor.instructorReviews} Reviews"
                    }
                    if (instructor.instructorStudents.isNotEmpty()) {
                        studentDetails.text = "${instructor.instructorStudents} Students"
                    }
                    if (instructor.instructorCourses.isNotEmpty()) {
                        coursesDetails.text = "${instructor.instructorCourses} Courses"
                    }
                    if (instructor.instructorDescription != null && instructor.instructorDescription.isNotEmpty()) {
                        instructorDescription.text = HtmlCompat.fromHtml(instructor.instructorDescription, 0)
                    }
//                    instructorDescription.text = getString(R.string.about_course)
                    instructorDescription.setOnClickListener {
                        try {
                            val viewPager = getViewPager()
                            viewPager?.let { updatePagerHeightForChild(view, it) }
                        } catch (e: Exception) {

                        }
                    }


                }

                incLayout2.apply {
                    if (descriptionData.student_feedback.feedback == null && descriptionData.student_feedback.avg_rating == "0") {
                        studentFeedback.visibility = View.GONE
                    } else {
                        tvTotalRating.text = descriptionData.student_feedback.avg_rating
                        if (descriptionData.student_feedback.feedback != null) {
                            val keys: Set<Double> = descriptionData.student_feedback.feedback.keys
                            val array = keys.toTypedArray<Double>()
                            val studentFeedback: StudentFeedbackAdapter = StudentFeedbackAdapter(descriptionData.student_feedback.feedback, array, context!!)
                            recyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = studentFeedback
                            }
                        }
                    }
                }

                incLayout3.apply {

                    if (descriptionCourseReviews.isEmpty()) {
                        reviewLayout.visibility = View.GONE
                        seeAllReviewsBtn.visibility = View.GONE
                    } else {
                        reviewLayout.visibility = View.VISIBLE
                        seeAllReviewsBtn.visibility = View.VISIBLE
                    }

                    if (descriptionData.isPurchased.equals("1")) {
                        userReviewsLL.submitReviewLayout.visibility = View.VISIBLE
                    } else {
                        userReviewsLL.submitReviewLayout.visibility = View.GONE
                    }

                    userReviewsLL.optionTV.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            showPopMenu(view)
                        }
                    })

                    userReviewsLL.btnSubmit.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            if (userReviewsLL.ratingBar.rating.toDouble() == 0.0) {
                                GenericUtils.showToast(activity, activity!!.getString(R.string.tap_a_star_to_give_your_rating))
                            } /* else if (TextUtils.isEmpty(addReviewTextET.getText().toString().trim())) {
                            GenericUtils.showToast(activity, activity.getString(R.string.enter_text_post_review));
                        }*/ else {
                                if (userReviewsLL.btnSubmit.tag == "0") {
                                    networkCallForAddreviewCourse() //NetworkAPICall(API.API_ADD_REVIEW_COURSE, true);
                                } else {
                                    networkCallForEditCourseReview() //NetworkAPICall(API.API_EDIT_USER_COURSE_REVIEWS, true);
                                }
                            }
                        }
                    })

                    userReviewsLL.btnCancel.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            if (binding.incLayout3.userReviewsLL.btnSubmit.tag == "0") {
                                binding.incLayout3.userReviewsLL.writereviewET.setText("")
                                binding.incLayout3.userReviewsLL.ratingBar.rating = 0f
                                binding.incLayout3.userReviewsLL.ratingBar.setIsIndicator(false)
                            } else {
                                binding.incLayout3.userReviewsLL.reviewTextTV.visibility = View.VISIBLE
                                binding.incLayout3.userReviewsLL.writereviewET.visibility = View.GONE
                                binding.incLayout3.userReviewsLL.btnControlReview.visibility = View.GONE
                                binding.incLayout3.userReviewsLL.optionTV.visibility = View.VISIBLE
                                binding.incLayout3.userReviewsLL.ratingBar.rating = singleCourseData.review.rating.toFloat()
                                binding.incLayout3.userReviewsLL.ratingBar.setIsIndicator(true)
                            }
                        }
                    })

                    setProfileImage()
                    showRatingContent(singleCourseData)

                    setReviews()

                    seeAllReviewsBtn.setOnClickListener {
                        val intent = Intent(activity, FAQActivity::class.java)
                        intent.putExtra(Constants.Extras.REVIEW, descriptionCourseReviews as Serializable)
                        intent.putExtra(Const.COURSE_ID, descriptionData.basic.id)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity!!.startActivity(intent)
                    }

                    val adapter1 = ArrayAdapter.createFromResource(context!!, com.emedicoz.app.R.array.reviewList, android.R.layout.simple_spinner_item)
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    reviewSpinner.adapter = adapter1
                    reviewSpinner.setSelection(0)
                    reviewSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            (parent.getChildAt(0) as TextView).textSize = 12f
                            try {
                                if (position == 0) {
                                    descriptionCourseReviews = descriptionCourseReviewsFull
                                    setReviews()
                                } else {
                                    networkCallForReviews(position.toString())
                                }
                            } catch (e: Exception) {
                                Log.e(CourseDetailActivity.TAG, "${e.message}")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                }

                if (descriptionFaq.isNotEmpty() && descriptionFaq.size > 2) {
                    showMore.visibility = View.VISIBLE
                } else {
                    showMore.visibility = View.GONE
                }

                if (descriptionFaq.isEmpty()) {
                    faqLayout.visibility = View.GONE
                    showMore.visibility = View.GONE
                } else {
                    faqLayout.visibility = View.VISIBLE
                    showMore.visibility = View.VISIBLE
                }

                val viewPager = getViewPager()

                val faqDescriptionTabAdapter = FaqDescriptionAdapter(descriptionFaq, context!!, viewPager, view)
                faqList.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = faqDescriptionTabAdapter
                }

                showMore.setOnClickListener {
                    val intent = Intent(activity, FAQActivity::class.java)
                    intent.putExtra(Constants.Extras.FAQ, descriptionFaq as Serializable)
                    intent.putExtra(Constants.Extras.TYPE, Const.FAQ)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    requireActivity().startActivity(intent)
                }


                needHelp.setOnClickListener {
                    val intent = Intent(activity, HelpSupportActivity::class.java)
                    startActivity(intent)
                //initHelpAndSupport()
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", "onCreateView: ${e.message}")
        }
        return view
    }

    private fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
        view.post {
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    view.width, View.MeasureSpec.EXACTLY
            )
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    0, View.MeasureSpec.UNSPECIFIED
            )
            view.measure(widthMeasureSpec, heightMeasureSpec)
            if (pager.layoutParams.height != view.measuredHeight) {
                pager.layoutParams = (pager.layoutParams).also {
                    it.height = view.measuredHeight
                }
            }
        }

    }


    fun getData(descriptionBasic: DescriptionBasic): SingleCourseData? {
        val singleCourseData = SingleCourseData()
        singleCourseData.course_type = descriptionBasic.courseType
        singleCourseData.for_dams = descriptionBasic.forDams
        singleCourseData.non_dams = descriptionBasic.nonDams
        singleCourseData.mrp = descriptionBasic.mrp
        singleCourseData.id = descriptionBasic.id

        singleCourseData.cover_image = descriptionBasic.coverImage
        singleCourseData.title = descriptionBasic.title
        singleCourseData.learner = descriptionBasic.learner
        singleCourseData.rating = descriptionBasic.rating
        singleCourseData.gst_include = descriptionBasic.gstInclude
        singleCourseData.gst = descriptionBasic.gst
        singleCourseData.points_conversion_rate = descriptionBasic.pointsConversionRate
        singleCourseData.is_subscription = descriptionBasic.is_subscription
        singleCourseData.is_instalment = descriptionBasic.is_instalment
        singleCourseData.child_courses = descriptionBasic.child_courses

        return singleCourseData
    }

    private fun setReviews() {


        if (descriptionCourseReviews.isNotEmpty() && descriptionCourseReviews.size > 2) {
            binding.incLayout3.seeAllReviewsBtn.visibility = View.VISIBLE
        } else {
            binding.incLayout3.seeAllReviewsBtn.visibility = View.GONE
        }

        val coursesDescriptionTabAdapter = CoursesDescriptionTabAdapter(descriptionCourseReviews, context!!)
        binding.incLayout3.reviewsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coursesDescriptionTabAdapter
        }


        try {
            val viewPager = getViewPager()
            viewPager?.let { updatePagerHeightForChild(view!!, it) }
        } catch (e: Exception) {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecordedDetailsDescriptionViewModel::class.java)
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        dBinding = null
    }


    private fun networkCallForReviews(rating: String) {
        progress.show()
        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.getReviewsByFilter(SharedPreference.getInstance().loggedInUser.id, descriptionData.basic.id, rating)
        descriptionCourseReviews = ArrayList<DescCourseReview>()
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val arrCourseList: JSONArray
                    val gson = Gson()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(CourseDetailActivity.TAG, " onResponse: $jsonResponse")

                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            arrCourseList = jsonResponse.getJSONArray("data")

                            for (j in 0 until arrCourseList.length()) {
                                val courseObject: JSONObject = arrCourseList.getJSONObject(j)
                                val descCourseReview: DescCourseReview = gson.fromJson(Objects.requireNonNull(courseObject).toString(), DescCourseReview::class.java)
                                descriptionCourseReviews.add(descCourseReview)
                            }
                            setReviews()
                        } else {
                            val message: String = Objects.requireNonNull(jsonResponse).optString("message")
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            descriptionCourseReviews = descriptionCourseReviewsFull
                            binding.incLayout3.reviewSpinner.setSelection(0)
                            setReviews()
                        }
                        progress.dismiss()
                    } catch (e: JSONException) {
                        progress.dismiss()
                        e.printStackTrace()
                    }
                } else {
                    progress.dismiss()
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
            }
        })


    }

    fun showPopMenu(v: View?) {
        val popup = PopupMenu(activity!!, v!!)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {

                R.id.editIM -> {
                    editReviews()
                    return@setOnMenuItemClickListener true
                }
                R.id.deleteIM -> {
                    val v1 = Helper.newCustomDialog(activity, false, activity!!.getString(R.string.app_name), activity!!.getString(R.string.deleteReviews))
                    val btnCancel: Button
                    val btnSubmit: Button
                    btnCancel = v1.findViewById(R.id.btn_cancel)
                    btnSubmit = v1.findViewById(R.id.btn_submit)
                    btnCancel.text = activity!!.resources.getString(R.string.cancel)
                    btnSubmit.text = activity!!.resources.getString(R.string.delete)
                    btnCancel.setOnClickListener { view: View? -> Helper.dismissDialog() }
                    btnSubmit.setOnClickListener { view: View? ->
                        Helper.dismissDialog()
                        deleteReviews()
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.inflate(R.menu.comment_menu)
        popup.show()
    }

    fun deleteReviews() {
        binding.incLayout3.userReviewsLL.btnSubmit.tag = "0"
        binding.incLayout3.userReviewsLL.writereviewET.setText("")
        networkCallForDeleteReviews() //NetworkAPICall(API.API_DELETE_USER_COURSE_REVIEWS, true);
    }


    private fun networkCallForBasicData() {
        progress.show()

        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.getCourseDetailData(SharedPreference.getInstance().loggedInUser.id, descriptionDataBasic.id)


        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(CourseDetailActivity.TAG, " onResponse: $jsonResponse")

                        descriptionResponse = Gson().fromJson(jsonObject.toString(), DescriptionResponse::class.java)
                        descriptionData1 = descriptionResponse.data

                        descriptionCourseReviewsFull = descriptionData1.courseReview as ArrayList<DescCourseReview>
                        descriptionCourseReviews = descriptionCourseReviewsFull
                        binding.incLayout3.reviewSpinner.setSelection(0)
                        setReviews()




                        progress.dismiss()
                    } catch (e: JSONException) {
                        progress.dismiss()
                        e.printStackTrace()
                    }
                } else {
                    progress.dismiss()
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
            }
        })


    }


    private fun networkCallForDeleteReviews() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        progress.show()
        val apiInterface = ApiClient.createService(SingleCourseApiInterface::class.java)
        val response = apiInterface.deleteUserCourseReview(SharedPreference.getInstance().loggedInUser.id,
                descriptionDataBasic.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            networkCallForCourseInfoRaw()
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_DELETE_USER_COURSE_REVIEWS, activity, 1, 1)
            }
        })
    }

    fun editReviews() {
        binding.incLayout3.userReviewsLL.reviewTextTV.visibility = View.GONE
        binding.incLayout3.userReviewsLL.writereviewET.visibility = View.VISIBLE
        binding.incLayout3.userReviewsLL.writereviewET.setText(singleCourseData.review.text)
        binding.incLayout3.userReviewsLL.btnControlReview.visibility = View.VISIBLE
        binding.incLayout3.userReviewsLL.optionTV.visibility = View.INVISIBLE
        binding.incLayout3.userReviewsLL.btnSubmit.text = activity!!.resources.getString(R.string.submit)
        binding.incLayout3.userReviewsLL.ratingBar.rating = singleCourseData.review.rating.toFloat()
        binding.incLayout3.userReviewsLL.ratingBar.setIsIndicator(false)
    }

    private fun networkCallForCourseInfoRaw() {
        if (activity!!.isFinishing) return
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        progress.show()
        val apiInterface = ApiClient.createService(SingleCourseApiInterface::class.java)
        Log.e(TAG, "course_type" + descriptionDataBasic.courseType + "is_combo" + descriptionDataBasic.isCombo + "course_id" + descriptionDataBasic.id)
        val response = apiInterface.getSingleCourseInfoRaw(SharedPreference.getInstance().loggedInUser.id,
                descriptionDataBasic.id,
                descriptionDataBasic.courseType,
                descriptionDataBasic.isCombo,
                "courseId")
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    val gson = Gson()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val data = GenericUtils.getJsonObject(jsonResponse)
                            singleCourseData = gson.fromJson(data.toString(), SingleCourseData::class.java)
                            Constants.COURSEID = singleCourseData.id
                            singleCourseData.is_subscription = descriptionDataBasic.is_subscription // nimesh
                            //singleStudy.singleCourseData.setInstallment(singleCourseData.getInstallment())
                            if (singleCourseData.rating != null && !singleCourseData.rating.equals("", ignoreCase = true)) Constants.RATING = singleCourseData.rating.toFloat()
                            if (singleCourseData.reviews != null) {
                                Log.e(TAG, "onResponse: getReviews size = " + singleCourseData.reviews.size)
                            } else {
                                Log.e(TAG, "onResponse: getReviews size = null")
                            }
                            if (singleCourseData.rating != null) {
                                Log.e(TAG, "onResponse: getRating = " + singleCourseData.rating)
                            } else {
                                Log.e(TAG, "onResponse: getRating = null")
                            }
                            if (singleCourseData.review != null) {
                                Log.e(TAG, "onResponse: getReview_rating = " + singleCourseData.review.rating)
                                Log.e(TAG, "onResponse: getReview_text = " + singleCourseData.review.text)
                            } else {
                                Log.e(TAG, "onResponse: getReview_rating = null")
                                Log.e(TAG, "onResponse: getReview_text = null")
                            }

                            //Capitalizing the first letter of User's Name
                            binding.incLayout3.userReviewsLL.profileName.text = Helper.CapitalizeText(SharedPreference.getInstance().loggedInUser.name)
                            showRatingContent(singleCourseData)
                            networkCallForBasicData()
                            /*if (singleCourseData.getReviews() != null) {
                                binding.incLayout3.userReviewsLL.cvReviewRating.setVisibility(View.VISIBLE)
                                setRatingList(singleCourseData)
                            } else {
                                binding.incLayout3.userReviewsLL.cvReviewRating.setVisibility(View.GONE)
                            }*/
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
//                Helper.showErrorLayoutForNoNav(API.API_SINGLE_COURSE_INFO_RAW, activity, 1, 1)
            }
        })
    }

    private fun showRatingContent(singleCourseData: SingleCourseData) {
        if (singleCourseData.review != null) {
            binding.incLayout3.userReviewsLL.btnControlReview.visibility = View.GONE
            binding.incLayout3.userReviewsLL.optionTV.visibility = View.VISIBLE
            binding.incLayout3.userReviewsLL.reviewTextTV.visibility = View.VISIBLE
            binding.incLayout3.userReviewsLL.writereviewET.visibility = View.GONE
            binding.incLayout3.userReviewsLL.reviewTextTV.text = singleCourseData.review.text
            binding.incLayout3.userReviewsLL.ratingBar.rating = singleCourseData.review.rating.toFloat()
            binding.incLayout3.userReviewsLL.ratingBar.setIsIndicator(true)
            binding.incLayout3.userReviewsLL.btnSubmit.tag = "1"
        } else {
            binding.incLayout3.userReviewsLL.reviewTextTV.visibility = View.GONE
            binding.incLayout3.userReviewsLL.writereviewET.visibility = View.VISIBLE
            binding.incLayout3.userReviewsLL.btnControlReview.visibility = View.VISIBLE
            binding.incLayout3.userReviewsLL.optionTV.visibility = View.INVISIBLE
            binding.incLayout3.userReviewsLL.btnSubmit.text = activity!!.resources.getString(R.string.submit)
            binding.incLayout3.userReviewsLL.ratingBar.rating = "0".toFloat()
            binding.incLayout3.userReviewsLL.ratingBar.setIsIndicator(false)
        }
    }

    private fun setProfileImage() {
        // ** To set the Image on Profile ** //
        if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.profile_picture)) {
            binding.incLayout3.userReviewsLL.userprofilepicIV.visibility = View.VISIBLE
            binding.incLayout3.userReviewsLL.userprofilepicIVText.visibility = View.GONE
            Glide.with(activity!!)
                    .asBitmap()
                    .load(SharedPreference.getInstance().loggedInUser.profile_picture)
                    .apply(RequestOptions()
                            .placeholder(R.mipmap.default_pic))
                    .into(object : SimpleTarget<Bitmap?>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            binding.incLayout3.userReviewsLL.userprofilepicIV.setImageBitmap(resource)
                        }
                    })
        } else {
            val dr: Drawable? = Helper.GetDrawable(SharedPreference.getInstance().loggedInUser.name, activity, SharedPreference.getInstance().loggedInUser.id)
            if (dr != null) {
                binding.incLayout3.userReviewsLL.userprofilepicIV.visibility = View.GONE
                binding.incLayout3.userReviewsLL.userprofilepicIVText.visibility = View.VISIBLE
                binding.incLayout3.userReviewsLL.userprofilepicIVText.setImageDrawable(dr)
            } else {
                binding.incLayout3.userReviewsLL.userprofilepicIV.visibility = View.VISIBLE
                binding.incLayout3.userReviewsLL.userprofilepicIVText.visibility = View.GONE
                binding.incLayout3.userReviewsLL.userprofilepicIV.setImageResource(R.mipmap.default_pic)
            }
        }
    }

    private fun networkCallForAddreviewCourse() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        progress.show()
        val apiInterface = ApiClient.createService(SingleCourseApiInterface::class.java)
        val response = apiInterface.addReviewCourse(SharedPreference.getInstance().loggedInUser.id,
                descriptionDataBasic.id, binding.incLayout3.userReviewsLL.ratingBar.rating.toString(), binding.incLayout3.userReviewsLL.writereviewET.text.toString())
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        progress.dismiss()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            networkCallForCourseInfoRaw()
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
            }
        })
    }

    private fun networkCallForEditCourseReview() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }

        progress.show()
        val apiInterface = ApiClient.createService(SingleCourseApiInterface::class.java)
        val response = apiInterface.editUserCourseReviews(SharedPreference.getInstance().loggedInUser.id, singleCourseData.id, binding.incLayout3.userReviewsLL.ratingBar.rating.toString(), binding.incLayout3.userReviewsLL.writereviewET.text.toString())
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            networkCallForCourseInfoRaw()
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNoNav(API.API_EDIT_USER_COURSE_REVIEWS, activity, 1, 1)
            }
        })
    }


    private fun initHelpAndSupport() {
        helpView = activity?.findViewById<LinearLayout>(R.id.helpView)!!

        val popupView = layoutInflater.inflate(R.layout.fragment_help_and_support, null)
        popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        popupWindow.showAsDropDown(helpView)
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        crossHelp = popupWindow.contentView.findViewById(R.id.crossHelp)
        recyclerView_help = popupWindow.contentView.findViewById(R.id.recyclerView_help)
        editTextHelp = popupWindow.contentView.findViewById(R.id.helpSearchFilter)
        helpImageCross = popupWindow.contentView.findViewById(R.id.img_help_clear_search)
        helpImageSearch = popupWindow.contentView.findViewById(R.id.img_help_search_view)
        helpImageCross.visibility = View.GONE
        helpImageSearch.visibility = View.VISIBLE
        editTextHelp.setText("")
        questionQuery = ""
        getHelpAndSupportData()
        helpImageCross.setOnClickListener(View.OnClickListener {
            Helper.closeKeyboard(activity)
            helpImageCross.visibility = View.GONE
            helpImageSearch.visibility = View.VISIBLE
            editTextHelp.setText("")
            questionQuery = ""
            getHelpAndSupportData()
        })
        editTextHelp.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getHelpAndSupportDataWithQuery()
                return@OnEditorActionListener true
            }
            false
        })
        editTextHelp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                questionQuery = s.toString()
                if (!TextUtils.isEmpty(s)) {
                    helpImageCross.visibility = View.VISIBLE
                    helpImageSearch.visibility = View.GONE
                } else {
                    helpImageCross.visibility = View.GONE
                    helpImageSearch.visibility = View.VISIBLE
                }
            }
        })
        crossHelp.setOnClickListener(View.OnClickListener { popupWindow.dismiss() })
        btnStartChat = popupWindow.contentView.findViewById(R.id.btn_Start_Chat)
        btnStartChat.setOnClickListener(View.OnClickListener { openWhatsAppChat() })
        startChat = popupWindow.contentView.findViewById(R.id.startChat)
        startChat.setOnClickListener(View.OnClickListener { openWhatsAppChat() })
    }


    private fun getHelpAndSupportData() {
        // asynchronous operation to get user recently viewed courses.
        mProgress.show()
        Helper.closeKeyboard(activity)
        helpAndSupportArrayList = java.util.ArrayList<HelpAndSupport>()
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
                                helpAndSupportArrayList.add(helpAndSupport)
                            }
                            try {
                                val helpAndSupportAdapter = HelpAndSupportAdapter(helpAndSupportArrayList, context!!)
                                recyclerView_help.layoutManager = LinearLayoutManager(context!!)
                                recyclerView_help.adapter = helpAndSupportAdapter
                            } catch (e: java.lang.Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val message = "No Data Found"
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                        mProgress.dismiss()
                    } catch (e: JSONException) {
                        mProgress.dismiss()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "No Data Found ", Toast.LENGTH_LONG).show()
                }
                mProgress.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, throwable: Throwable) {
                mProgress.dismiss()
                Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getHelpAndSupportDataWithQuery() {
        mProgress.show()
        questionQuery = editTextHelp.text.toString().trim({ it <= ' ' })
        eMedicozApp.getInstance().questionQuery = questionQuery
        Helper.closeKeyboard(activity)
        if (!TextUtils.isEmpty(questionQuery)) {
            helpAndSupportArrayList = java.util.ArrayList<HelpAndSupport>()
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
                                    helpAndSupportArrayList.add(helpAndSupport)
                                }
                                try {
                                    val helpAndSupportAdapter = HelpAndSupportAdapter(helpAndSupportArrayList, context!!)
                                    recyclerView_help.layoutManager = LinearLayoutManager(context)
                                    recyclerView_help.adapter = helpAndSupportAdapter
                                } catch (e: java.lang.Exception) {
                                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val message = "No Data Found"
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            mProgress.dismiss()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, "No Data Found ", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, throwable: Throwable) {
                    mProgress.dismiss()
                    Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            mProgress.dismiss()
            Toast.makeText(context, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
        }
    }


    private fun openWhatsAppChat() {
        val number = BuildConfig.WHATSAPP_NO
        try {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net")
            startActivity(sendIntent)
        } catch (e: java.lang.Exception) {
            if (e is ActivityNotFoundException) {
                val appPackageName = "com.whatsapp"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            Log.e("TAG", "ERROR_OPEN_MESSANGER$e")
        }
    }


    fun getViewPager(): ViewPager2? {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val params = viewPager?.layoutParams
        if (params != null) {
            params.height = ViewPager.LayoutParams.WRAP_CONTENT
        }
        if (viewPager != null) {
            viewPager.layoutParams = params
            viewPager.requestLayout()
        }
        return viewPager
    }

    override fun onResume() {
        try {
            val viewPager = getViewPager()
            viewPager?.let { updatePagerHeightForChild(view!!, it) }
        } catch (e: Exception) {

        }
        super.onResume()
    }
}