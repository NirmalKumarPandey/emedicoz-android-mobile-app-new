package com.emedicoz.app.courseDetail.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.adapter.CoursesDetailScreenSlideAdapter
import com.emedicoz.app.courseDetail.fragment.CourseDetailsDescriptionFragment
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.courses.adapter.InstallmentParentAdapter
import com.emedicoz.app.courses.callback.OnSubscriptionItemClickListener
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.ActivityCourseDetailBinding
import com.emedicoz.app.feeds.activity.MyBookmarksActivity
import com.emedicoz.app.installment.model.Installment
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.courses.Cards
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.modelo.courses.SingleCourseData
import com.emedicoz.app.modelo.liveclass.courses.DescriptionBasic
import com.emedicoz.app.modelo.liveclass.courses.DescriptionData
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse
import com.emedicoz.app.recordedCourses.fragment.CourseDetailVideoFragment
import com.emedicoz.app.recordedCourses.fragment.CourseListingFragment
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface
import com.emedicoz.app.templateAdapters.RecordedCourseImageBannerAdapter
import com.emedicoz.app.templateAdapters.RecordedDetailScreenImageBannerAdapter
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.Const.COURSE
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var course: Course
    private lateinit var progress: Progress
    var descriptionResponse: DescriptionResponse = DescriptionResponse()
    private lateinit var descriptionData: DescriptionData
    private lateinit var is_subscription: String
    private lateinit var subscriptionsList: List<Installment>
    private var selectedSubscriptionId = ""
    var isFreeTrial: Boolean = false

    private val instructorCourseList = java.util.ArrayList<Course>()
    private val studentAreViewingList = java.util.ArrayList<Course>()

    companion object {
        const val TAG = "CourseDetailActivity"
    }

    private var fragType = ""
    private var courseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        progress = Progress(this@CourseDetailActivity)
        progress.setCancelable(false)

        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        course = intent.getSerializableExtra(COURSE) as Course
        fragType = intent.getSerializableExtra(Const.FRAG_TYPE) as String

        if (course.typeBanner != null && course.typeBanner == "1") {
            getCourseData()
        }

//        if (course.title == null) {
//            getCourseData()
//        } else {
        networkCallForBasicData()
//        }

        binding.toolbar.bookmarkIV.setOnClickListener {
            val intent = Intent(this, MyBookmarksActivity::class.java)
            startActivity(intent)
            //Helper.gotoBookMark(this@CourseDetailActivity, Const.BOOKMARKS)
        }

        setActivityTitle(fragType)
    }

    private fun getDetailCourseId(course: Course): String? {
        var s = course.id
        return "$s,"
    }


    private fun getCourseData() {
        // asynchronous operation to get courses of instructor.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getRecentlyViewedCourses(
                SharedPreference.getInstance().loggedInUser.id,
                course.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val gson = Gson()

                    val arrCourseList: JSONArray?
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            arrCourseList =
                                    GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list")

                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                course = gson.fromJson(
                                        Objects.requireNonNull(courseObject).toString(),
                                        Course::class.java
                                )

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
                                                            courseObject.optString(
                                                                    "mrp"
                                                            )
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
                                                            courseObject.optString(
                                                                    "mrp"
                                                            )
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

                            }

                            // networkCallForBasicData()

                        } else {
                            shoeError("Something went Wrong")
                        }

                    } catch (e: JSONException) {
                        shoeError("Something went Wrong")
                        e.printStackTrace()
                    }
                } else {
                    shoeError("Something went Wrong")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                shoeError("Something went Wrong")
            }
        })
    }

    private fun shoeError(string: String) {
        Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show()
    }


    private fun setActivityTitle(fragType: String) {
        if (fragType == Const.ALLCOURSES) {
            binding.toolbar.tvTitle.text = getString(R.string.recorded_courses)
        } else if (fragType == Const.MYCOURSES) {
            binding.toolbar.tvTitle.text = getString(R.string.my_course_detail)
        } else if (fragType == Const.LIVE_CLASSES) {
            binding.toolbar.tvTitle.text = getString(R.string.live_course)
        } else {
            if (course.is_live == "1") {
                binding.toolbar.tvTitle.text = getString(R.string.live_course)
            } else {
                binding.toolbar.tvTitle.text = getString(R.string.recorded_courses)
            }
        }
    }


    private fun setShare(descriptionBasic: DescriptionBasic) {
        binding.apply {
            imgShare.tag = descriptionBasic
            imgShare.setOnClickListener {
                var item = it.tag as DescriptionBasic
                sharePostExternally(course)
                //Helper.openShareDialogShareCourse(this@CourseDetailActivity, item)
            }
        }
    }


    //    private void showMCQPopupView(final PostResponse feed) {
    //        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //        final View v = li.inflate(R.layout.submit_answer_mcq, null, false);
    //        final Dialog submitMcq = new Dialog(activity);
    //        submitMcq.requestWindowFeature(Window.FEATURE_NO_TITLE);
    //        submitMcq.setCanceledOnTouchOutside(false);
    //        submitMcq.setContentView(v);
    //        submitMcq.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    //        submitMcq.show();
    //        CardView submitCard;
    //        TextView questionTV;
    //
    //        submitCard = v.findViewById(R.id.bottomCard);
    //        questionTV = v.findViewById(R.id.questionTV);
    //        mcqoptions = v.findViewById(R.id.mcqoptions);
    //
    //        mcqoptions.removeAllViews();
    //        LinearLayoutList.clear();
    //        questionTV.setText(String.format("%s %s", "Q. ", feed.getPost_data().getQuestion()));
    //
    //        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_one())) {
    //            mcqoptions.addView(initMCQPopupView("A", feed.getPost_data().getAnswer_one()));
    //        }
    //        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_two())) {
    //            mcqoptions.addView(initMCQPopupView("B", feed.getPost_data().getAnswer_two()));
    //        }
    //        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_three())) {
    //            mcqoptions.addView(initMCQPopupView("C", feed.getPost_data().getAnswer_three()));
    //        }
    //        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_four())) {
    //            mcqoptions.addView(initMCQPopupView("D", feed.getPost_data().getAnswer_four()));
    //        }
    //        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_five())) {
    //            mcqoptions.addView(initMCQPopupView("E", feed.getPost_data().getAnswer_five()));
    //        }
    //
    //        submitCard.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View view) {
    //                if (mcq_item != 0) {
    //                    networkCall.NetworkAPICall(API.API_SUBMIT_ANSWER_POST_MCQ, true);
    //                    submitMcq.dismiss();
    //                } else {
    //                    Toast.makeText(activity, "Please select any one option", Toast.LENGTH_SHORT).show();
    //                }
    //            }
    //        });
    //
    //    }
    //    private LinearLayout initMCQPopupView(String text1, String text2) {
    //        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.mcq_popup, null);
    //        TextView tv = (TextView) v.findViewById(R.id.optioniconTV);
    //        final CheckedTextView radioButton = v.findViewById(R.id.radioOptionTV);
    //        tv = changeBackgroundColor(tv, 1);
    ////        v = changeBackgroundColor(v, 1);
    //        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    //        lp.setMargins(0, 10, 0, 0);
    //        v.setLayoutParams(lp);
    //        tv.setText(text1);
    //        radioButton.setText(text2);
    //
    //        v.setTag(tv.getText());
    ////        v.setOnClickListener(onClickListener);
    ////        LinearLayoutList.add(v);
    //        return v;
    //    }
    private fun sharePostExternally(course: Course) {
        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://emedicoz.com/?course_id=${course.id}"))
            .setDynamicLinkDomain("wn2d8.app.goo.gl")
            .setAndroidParameters(
                    AndroidParameters.Builder().build()
            )
            // Open links with com.example.ios on iOS
            //                .setIosParameters(new DynamicLink.IosParameters.Builder("com.eMedicoz.app").build())
            // Set parameters
            // ...
            .buildShortDynamicLink()
            .addOnCompleteListener(
                    this@CourseDetailActivity
            ) { task: Task<ShortDynamicLink?> ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = task.result!!.shortLink
                    val msgHtml = String.format(
                            "<p>Let's refer this %s on eMedicoz App."
                                    + "<a href=\"%s\">Click Here</a>!</p>",
                            course.title,
                            shortLink.toString()
                    )
                    val msg =
                        "Let's refer this " + course.title + " on eMedicoz App. Click on Link : " + shortLink.toString()
                    Helper.sendLink(
                            this@CourseDetailActivity, "eMedicoz Post Reference", msg,
                            msgHtml
                    )
                } else {
                    Toast.makeText(
                            this@CourseDetailActivity,
                            "Link could not be generated please try again!",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun bindTabLayoutWithViewPager(descriptionData: DescriptionData) {

        // Bind Tab Layouts with Fragments
        val courseDetailScreenSlideAdapter =
            CoursesDetailScreenSlideAdapter(this@CourseDetailActivity)
        if (ifAddFragment(Const.VIDEOS, descriptionData.tiles))
            courseDetailScreenSlideAdapter.addFragment(
                    CourseDetailVideoFragment.newInstance(
                            Const.VIDEOS,
                            descriptionData
                    ), Const.VIDEOS
            )
        if (ifAddFragment(Const.NOTES_TEST, descriptionData.tiles))
            courseDetailScreenSlideAdapter.addFragment(
                    CourseDetailVideoFragment.newInstance(
                            Const.NOTES_TEST,
                            descriptionData.basic.id
                    ), Const.NOTES_TEST
            )
        if (ifAddFragment(Const.COURSES, descriptionData.tiles))
            courseDetailScreenSlideAdapter.addFragment(
                    CourseListingFragment.newInstance(
                            "CHILD_COURSE_LIST",
                            course.id
                    ), Const.COURSES
            )

        courseDetailScreenSlideAdapter.addFragment(
                CourseDetailsDescriptionFragment.newInstance(
                        descriptionData
                ), "Description"
        )

        binding.viewPager.apply {
            adapter = courseDetailScreenSlideAdapter
            currentItem = 0
            setPageTransformer(NoPageTransformer())

        }

        TabLayoutMediator(binding.tabsLayout, binding.viewPager) { tab, position ->
            tab.text = courseDetailScreenSlideAdapter.getItemTitle(position)
        }.attach()


        // Commented for now

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                println(state)
            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println(position)
            }


            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val params = binding.viewPager.layoutParams
                params.height = ViewPager.LayoutParams.WRAP_CONTENT
                binding.viewPager.layoutParams = params
                binding.viewPager.requestLayout()
                if (position + 1 == binding.viewPager.adapter?.itemCount) {
                    if (!GenericUtils.isListEmpty(instructorCourseList))
                        setInstructorCourseSlider()
                    else {
                        getMoreCoursesByInstructor(descriptionData)
                    }

                    val view = courseDetailScreenSlideAdapter.getItem(position).view
                    if (view != null) {
                        updatePagerHeightForChild(view, binding.viewPager)
                    }

                } else {
                    if (!GenericUtils.isListEmpty(studentAreViewingList))
                        setViewingCourseSlider()
                    else
                        getStudentViewingCourses()
                }


            }
        })
        // End
    }


    fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
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

    private fun ifAddFragment(fragType: String, tiles: ArrayList<Cards>): Boolean {
        var cardType = ""
        when (fragType) {
            Const.VIDEOS -> cardType = Const.VIDEO
            Const.NOTES_TEST -> cardType = "qbank_test_pdf_epub"
            Const.COURSES -> cardType = Const.COURSE
        }

        for (cards in tiles) {
            if (cardType.contains(cards.type, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun networkCallForBasicData() {
        progress.show()
        isFreeTrial = course.isFreeTrial
        if (course.isFreeTrial && !GenericUtils.isEmpty(course.free_message)) {
            showFreetrialDialog(course.free_message)
        }

        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.getCourseDetailData(
                SharedPreference.getInstance().loggedInUser.id,
                course.id
        )

        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(TAG, " onResponse: $jsonResponse")

                        descriptionResponse =
                                Gson().fromJson(jsonObject.toString(), DescriptionResponse::class.java)
                        if (descriptionResponse.data.isPurchased == "0") {
                            Constants.IS_PURCHASED = "1"
                            SharedPreference.getInstance().putBoolean(Const.SINGLE_STUDY, false)
                        } else {
                            Constants.IS_PURCHASED = "0"
                            SharedPreference.getInstance().putBoolean(Const.SINGLE_STUDY, true)
                        }
                        descriptionData = descriptionResponse.data


                        is_subscription = descriptionData.basic.is_subscription
                        subscriptionsList = descriptionData.basic.installment


                        setUIAndOnClicks(descriptionData)



                        setImageVideoBanner(descriptionData)
                        bindTabLayoutWithViewPager(descriptionData)

                        if (descriptionData.is_cart_added!!.equals("1")) {
                            binding.AddToCart.text = "Added in cart"
                            binding.AddToCart.setBackgroundResource(R.drawable.bg_light_green)
                        }

                        setShare(descriptionData.basic)
//                        getMoreCoursesByInstructor(descriptionData)

                        addToRecentCourseList(course.id)

                        binding.apply {
                            layoutSlider.visibility = View.VISIBLE
                            headerLayout.visibility = View.VISIBLE
                        }

                        progress.dismiss()
                    } catch (e: Exception) {
                        progress.dismiss()
                        e.printStackTrace()
                        Helper.showErrorLayoutForNav(
                                "networkCallForRecordedCourse",
                                this@CourseDetailActivity,
                                1,
                                0
                        )
                    }
                } else {
                    progress.dismiss()
                    Helper.showErrorLayoutForNav(
                            "networkCallForRecordedCourse",
                            this@CourseDetailActivity,
                            1,
                            1
                    )
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav(
                        "networkCallForRecordedCourse",
                        this@CourseDetailActivity,
                        1,
                        2
                )
            }
        })


    }

    private fun addToRecentCourseList(id: String) {
        var courseIds = SharedPreference.getInstance().getString(Const.RECENT_COURSE_ID)
        courseIds += "$id, "
        SharedPreference.getInstance().putString(Const.RECENT_COURSE_ID, courseIds)
    }

    private fun enrollCourseBtnUpdate(course: Course) {
        if (course.isIs_renew == "1") {
            binding.imgWishlist.visibility = View.GONE
            binding.enrollBtn.text = getString(R.string.renew)
            binding.enrollBtn.setBackgroundResource(R.drawable.background_orange_btn)
        } else if (course.isIs_purchased) {
            binding.imgWishlist.visibility = View.GONE
            binding.AddToCart.visibility = View.GONE
            binding.enrollBtn.text = getString(R.string.enrolled)
            binding.enrollBtn.setBackgroundResource(R.drawable.background_green_btn)
        } else {
            binding.enrollBtn.text = getString(R.string.enroll_now)
            binding.enrollBtn.setBackgroundResource(R.drawable.background_btn_red)
        }
    }

    private class NoPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(
                view: View,
                position: Float
        ) {
            view.translationX = view.width * -position
            if (position <= -1.0f || position >= 1.0f) {
                view.visibility = View.GONE
            } else if (position == 0.0f) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    private fun setImageVideoBanner(descriptionData: DescriptionData) {
        var courseBannerDataList: MutableList<CourseBannerData> = descriptionData.courseBanner

        if (descriptionData.basic.coverVideo != null && !GenericUtils.isEmpty(descriptionData.basic.coverVideo)) {
            var introVideoBanner = CourseBannerData()
            introVideoBanner.imageLink = descriptionData.basic.coverVideo
            introVideoBanner.media_type = "video"
            courseBannerDataList.add(introVideoBanner)
        }

        Collections.reverse(courseBannerDataList)
        var show = false
        if (courseBannerDataList.size == 1) {

            if (courseBannerDataList[0].media_type == "video") {
                show = true
            } else {

                binding.imageBanner.visibility = View.VISIBLE
                val currentImageBannerList: CourseBannerData = courseBannerDataList[0]
                Glide.with(this)
                    .load(currentImageBannerList.imageLink)
                    .into(binding.ivAutoImageSlider)


                binding.ivAutoImageSlider.setOnClickListener {
                    if (currentImageBannerList.webImageLink != null) {
                        Helper.GoToWebViewActivity(this, currentImageBannerList.webLink)
                    } else {
                        if (currentImageBannerList.id == Constants.Extras.QUESTION_BANK_COURSE_ID) {
                            val intent =
                                Intent(this@CourseDetailActivity, CourseActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                            intent.putExtra(Const.PARENT_ID, currentImageBannerList.id)
                            startActivity(intent)
                        } else {
                            val intent =
                                Intent(this@CourseDetailActivity, CourseActivity::class.java)
                            intent.putExtra(Const.PARENT_ID, currentImageBannerList.id)
                            if (currentImageBannerList.is_live == "1") {
                                intent.putExtra(Const.FRAG_TYPE, Const.LIVE_CLASSES)
                            } else {
                                intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        } else {
            binding.imageBanner.visibility = View.GONE
        }

        if (courseBannerDataList.size > 1) {
            binding.imageVideoBanner.visibility = View.VISIBLE
        } else {
            if (show) {
                binding.imageVideoBanner.visibility = View.VISIBLE
            } else {
                binding.imageVideoBanner.visibility = View.GONE
            }
        }

        val recordedCourseImageBannerAdapter =
            RecordedCourseImageBannerAdapter(courseBannerDataList, this@CourseDetailActivity)
        binding.imageVideoBanner.apply {
            setSliderAdapter(recordedCourseImageBannerAdapter)
            setIndicatorAnimation(IndicatorAnimationType.WORM)
            setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            indicatorSelectedColor = Color.RED
            indicatorUnselectedColor = Color.WHITE
            isAutoCycle = false
        }
    }

    private fun setCoursesBannerAdapter(courseList: MutableList<Course>) {
        val recordedCourseAsPerSearchItemAdapter =
            RecordedDetailScreenImageBannerAdapter(courseList, this@CourseDetailActivity)
        binding.coursesSlider.apply {
            setSliderAdapter(recordedCourseAsPerSearchItemAdapter)
            setIndicatorAnimation(IndicatorAnimationType.WORM)
            setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            indicatorSelectedColor = Color.RED
            isAutoCycle = true
            scrollTimeInSec = 3
        }.startAutoCycle()
    }

    private fun setUIAndOnClicks(descriptionData: DescriptionData) {
        val descriptionBasic = descriptionData.basic
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.tvCourseName.text = descriptionBasic.title
        if (!course.calMrp.equals("free", true) || !course.isIs_purchased) {
            binding.courseMrp.text = "₹${descriptionBasic.mrp}"
            setSpannable(binding.courseMrp, course)
        } else {
            binding.courseMrp.text = ""
        }

        if (course.calMrp == null || course.calMrp.equals("free", true)) {
            isFreeTrial = false
            binding.AddToCart.visibility = View.GONE
            binding.courseMrp.visibility = View.GONE
        } else {
            binding.AddToCart.visibility = View.VISIBLE
            binding.courseMrp.visibility = View.VISIBLE
        }


        if (!GenericUtils.isEmpty(course.category_tag)) {
            binding.tvCourseCategory.text = course.category_tag
            binding.tvCourseCategory.visibility = View.VISIBLE
        } else {
            binding.tvCourseCategory.visibility = View.GONE
            if (!GenericUtils.isEmpty(course.course_tag)) {
                binding.tvCourseCategory.text = course.course_tag
                binding.tvCourseCategory.visibility = View.VISIBLE
            }
        }

        binding.courseRating.rating = descriptionBasic.rating.toFloat()

        binding.tvCourseLearner.text =
            "(${descriptionBasic.reviewCount} ratings) ${descriptionBasic.learner} students"
        binding.tvCourseRating.text = descriptionBasic.rating

        setEnrollButton()
        setWishListIcon(descriptionData.wishList, descriptionData.isPurchased)

        if (fragType == Const.MYCOURSES) {
            binding.imgWishlist.visibility = View.GONE
            course.isIs_purchased = true
            enrollCourseBtnUpdate(course)
        }

    }

    private fun setWishListIcon(isWishList: String, purchased: String) {
        if (purchased == "1") {
            binding.imgWishlist.visibility = View.GONE
        } else {
            binding.imgWishlist.visibility = View.VISIBLE
        }

        if (isWishList == "1")
            binding.imgWishlist.setImageResource(R.drawable.wishlist_selected)
        else
            binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)

        binding.imgWishlist.setOnClickListener {
            if (isWishList != "1") {
                addToWishList(course)
                binding.imgWishlist.setImageResource(R.drawable.wishlist_selected)
            } else {
                removeFromWishList(course)
                binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)
            }
        }
        binding.AddToCart.setOnClickListener {
            if ((it as Button).text == "Add To Cart")

                if (is_subscription != null && is_subscription.equals(
                                "1",
                                true
                        )
                ) {    // is_subscription = 1
                    openSubscriptionsDialog()
                } else {
                    addToCart(course)
                }
        }
    }

    private fun openSubscriptionsDialog() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val layoutInflaterAndroid: LayoutInflater = LayoutInflater.from(this)
        val view: View = layoutInflaterAndroid.inflate(R.layout.subscription_dialog_cart, null)
        builder.setView(view)
        builder.setCancelable(true)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val closeButton = view.findViewById<ImageView>(R.id.closeButton)
        val subscriptionRecyclerview =
            view.findViewById<RecyclerView>(R.id.subscriptionRecyclerview)

        val singleCourseData: SingleCourseData = Helper.getData(course)


        singleCourseData.is_subscription = descriptionData.basic.is_subscription
        singleCourseData.is_instalment = descriptionData.basic.is_instalment

        subscriptionRecyclerview.adapter = InstallmentParentAdapter(
                this,
                subscriptionsList,
                singleCourseData,
                object : OnSubscriptionItemClickListener {
                    override fun OnSubscriptionItemClickPosition(position: Int) {

                        selectedSubscriptionId = subscriptionsList.get(position).id
                        alertDialog.dismiss()

                        //adding for first remove then add
                       // removeFromCart(course)

                        //previous code
                         addToCart(course)
                    }
                })
        subscriptionRecyclerview.addItemDecoration(
                EqualSpacingItemDecoration(
                        30,
                        EqualSpacingItemDecoration.VERTICAL
                )
        )
        subscriptionRecyclerview.layoutManager = LinearLayoutManager(this)

        closeButton.setOnClickListener { alertDialog.dismiss() }

    }

    private fun setEnrollButton() {
        binding.enrollBtn.setOnClickListener {
            if (course.isIs_renew == "1" || !course.isIs_purchased && course.mrp != "0") {
                if (course.mrp == "0") {
                    networkCallForFreeCourseTransaction(course)
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (course.for_dams != "0")
                           // Helper.goToCourseInvoiceScreen(this@CourseDetailActivity, Helper.getData(course))

                        //................added by anil start
                            if (is_subscription != null && is_subscription.equals("1", true
                                    )
                            ) {    // is_subscription = 1
                                openSubscriptionsDialog()
                            } else {
                                // addToCart(course)
                                if(course.isIs_cart==true)
                                {
                                    //remove the cart item
                                    removeFromCart(course)
                                }else {
                                    addToCart(course)
                                }

                            }

                        //...............added by anil end




                        else
                            networkCallForFreeCourseTransaction(course)
                    } else {
                        if (course.non_dams != "0")
                           // Helper.goToCourseInvoiceScreen(this@CourseDetailActivity, Helper.getData(course))

                       //................added by anil start
                        if (is_subscription != null && is_subscription.equals("1", true
                                )
                        ) {    // is_subscription = 1
                            openSubscriptionsDialog()
                        } else {
                           // addToCart(course)
                            if(course.isIs_cart==true)
                            {
                                //remove the cart item
                                removeFromCart(course)
                            }else {
                                addToCart(course)
                            }

                        }

                       //...............added by anil end
                        else
                            networkCallForFreeCourseTransaction(course)
                    }
                }
            } else if (course.isIs_purchased) {
                return@setOnClickListener
            } else {
                networkCallForFreeCourseTransaction(course)
            }
        }
        enrollCourseBtnUpdate(course)
    }

    private fun networkCallForFreeCourseTransaction(item: Course) {

        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.makeFreeCourseTransaction(
                SharedPreference.getInstance().loggedInUser.id,
                item.points_conversion_rate, "0", "", "", item.id, "0"
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_purchased = true
                            item.isIs_renew = "0"
                            enrollCourseBtnUpdate(item)
                            Toast.makeText(
                                    applicationContext,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                    "networkCallForFreeCourseTransaction",
                                    this@CourseDetailActivity,
                                    1,
                                    0
                            )
                            RetrofitResponse.getApiData(
                                    applicationContext,
                                    jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav(
                                "networkCallForFreeCourseTransaction",
                                this@CourseDetailActivity,
                                1,
                                0
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav(
                        "networkCallForFreeCourseTransaction",
                        this@CourseDetailActivity,
                        1,
                        1
                )
            }
        })
    }

    private fun addToCart(item: Course) {
        // asynchronous operation to add course to cart.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.addCourseToCart(
                SharedPreference.getInstance().loggedInUser.id,
                item.id,
                selectedSubscriptionId
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                selectedSubscriptionId = ""
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            binding.AddToCart.text = "Added in cart"
                            binding.AddToCart.setBackgroundResource(R.drawable.bg_light_green)
                            var prevCartCount = SharedPreference.getInstance().getInt(Const.CART_COUNT)
                            SharedPreference.getInstance().putInt(Const.CART_COUNT, prevCartCount + 1)
                            //Toast.makeText(applicationContext, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            Helper.goToCartScreen(this@CourseDetailActivity, null)

                        } else {
                           // Toast.makeText(applicationContext, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                           // RetrofitResponse.getApiData(this@CourseDetailActivity, jsonResponse.optString(Const.AUTH_CODE))
                            removeFromCart(item)



                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("addToCart", this@CourseDetailActivity, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("addToCart", this@CourseDetailActivity, 1, 1)
                selectedSubscriptionId = ""
            }
        })
    }

    private fun addToWishList(item: Course) {
        // asynchronous operation to add course to wishlist.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.addCourseToWishlist(
                SharedPreference.getInstance().loggedInUser.id,
                item.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_wishlist = true
                            setWishListIcon("1", descriptionData.isPurchased)
                            Toast.makeText(
                                    applicationContext,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                    "addToWishList",
                                    this@CourseDetailActivity,
                                    1,
                                    0
                            )
                            RetrofitResponse.getApiData(
                                    this@CourseDetailActivity,
                                    jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav(
                                "addToWishList",
                                this@CourseDetailActivity,
                                1,
                                0
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("addToWishList", this@CourseDetailActivity, 1, 1)

            }
        })

    }

    private fun removeFromWishList(item: Course) {
        // asynchronous operation to remove course from wishlist.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.removeCourseToWishlist(
                SharedPreference.getInstance().loggedInUser.id,
                item.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_wishlist = false
                            setWishListIcon("0", descriptionData.isPurchased)
                            Toast.makeText(
                                    applicationContext,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                    "removeFromWishList",
                                    this@CourseDetailActivity,
                                    1,
                                    0
                            )
                            RetrofitResponse.getApiData(
                                    this@CourseDetailActivity,
                                    jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav(
                                "removeFromWishList",
                                this@CourseDetailActivity,
                                1,
                                0
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("removeFromWishList", this@CourseDetailActivity, 1, 1)

            }
        })

    }


    private fun getStudentViewingCourses() {
        // asynchronous operation to get courses by category.
        progress.show()
        val response: Call<JsonObject>
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        response = if (course.is_live == "1") {
            apiInterface.getLiveCourseByCat(
                    SharedPreference.getInstance().loggedInUser.id,
                    descriptionData.student_viewing_cat_id
            )
        } else {
            apiInterface.getCourseByCat(
                    SharedPreference.getInstance().loggedInUser.id,
                    descriptionData.student_viewing_cat_id
            )
        }
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val gson = Gson()

                    val arrCourseList: JSONArray?
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            arrCourseList =
                                    GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list")

                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val course = gson.fromJson(
                                        Objects.requireNonNull(courseObject).toString(),
                                        Course::class.java
                                )

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
                                                            courseObject.optString(
                                                                    "mrp"
                                                            )
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
                                                            courseObject.optString(
                                                                    "mrp"
                                                            )
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
                                studentAreViewingList.add(course)
                            }
                            setViewingCourseSlider()
                        } else {
                            binding.coursesSlider.visibility = View.GONE
                            binding.CoursesSliderHeader.visibility = View.GONE
                        }

                    } catch (e: JSONException) {
                        binding.coursesSlider.visibility = View.GONE
                        binding.CoursesSliderHeader.visibility = View.GONE
                        e.printStackTrace()
                    }
                } else {
                    binding.coursesSlider.visibility = View.GONE
                    binding.CoursesSliderHeader.visibility = View.GONE
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                binding.coursesSlider.visibility = View.GONE
                binding.CoursesSliderHeader.visibility = View.GONE
            }
        })
    }

    private fun getMoreCoursesByInstructor(descriptionData: DescriptionData) {
        // asynchronous operation to get courses of instructor.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)

        val response: Call<JsonObject>
        if (course.is_live == "1") {
            response = apiInterface.getLiveRecentlyViewedCourses(
                    SharedPreference.getInstance().loggedInUser.id,
                getCourseId(descriptionData)!!
            )
        } else {
            response = apiInterface.getRecentlyViewedCourses(
                    SharedPreference.getInstance().loggedInUser.id,
                getCourseId(descriptionData)!!
            )
        }
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val gson = Gson()

                    val arrCourseList: JSONArray?
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            arrCourseList =
                                    GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list")

                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val course = gson.fromJson(
                                        Objects.requireNonNull(courseObject).toString(),
                                        Course::class.java
                                )

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
                                                            courseObject.optString(
                                                                    "mrp"
                                                            )
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
                                                            courseObject.optString(
                                                                    "mrp"
                                                            )
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
                                instructorCourseList.add(course)
                            }

                            setInstructorCourseSlider()

                        } else {
                            binding.coursesSlider.visibility = View.GONE
                            binding.CoursesSliderHeader.visibility = View.GONE
                        }

                    } catch (e: JSONException) {
                        binding.coursesSlider.visibility = View.GONE
                        binding.CoursesSliderHeader.visibility = View.GONE
                        e.printStackTrace()
                    }
                } else {
                    binding.coursesSlider.visibility = View.GONE
                    binding.CoursesSliderHeader.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                binding.coursesSlider.visibility = View.GONE
                binding.CoursesSliderHeader.visibility = View.GONE
            }
        })
    }

    private fun setInstructorCourseSlider() {

        if (GenericUtils.isListEmpty(instructorCourseList)) {
            binding.CourseSliderLayout.visibility = View.GONE
        } else {
            binding.CourseSliderLayout.visibility = View.VISIBLE
        }

        val title = "More Courses by ${descriptionData.instructorData.name}"
        binding.courseCategories.text = title
        if (instructorCourseList.size > 4) {
            setCoursesBannerAdapter(instructorCourseList.subList(0, 4))
        } else {
            setCoursesBannerAdapter(instructorCourseList)
        }
        if (instructorCourseList.size > 4) {
            binding.showAllLink.visibility = View.VISIBLE
        } else {
            binding.showAllLink.visibility = View.GONE
        }

        binding.showAllLink.setOnClickListener {
            redirectToSeeAll(instructorCourseList, Const.MORE_COURSE, title)
        }


    }

    private fun setViewingCourseSlider() {
        if (GenericUtils.isListEmpty(studentAreViewingList)) {
            binding.CourseSliderLayout.visibility = View.GONE
        } else {
            binding.CourseSliderLayout.visibility = View.VISIBLE
        }

        binding.courseCategories.text = "Student Are Viewing"
        if (studentAreViewingList.size > 4) {
            setCoursesBannerAdapter(studentAreViewingList.subList(0, 4))
        } else {
            setCoursesBannerAdapter(studentAreViewingList)
        }
        binding.showAllLink.setOnClickListener {
            redirectToSeeAll(studentAreViewingList, Const.STUDENT_ARE_VIEWING, "")
        }
        if (studentAreViewingList.size > 4) {
            binding.showAllLink.visibility = View.VISIBLE
        } else {
            binding.showAllLink.visibility = View.GONE
        }

    }

    private fun getCourseId(descriptionData: DescriptionData): String? {
        return descriptionData.instructorData.instructorOtherCourseIds
    }

    private fun redirectToSeeAll(coursesList: ArrayList<Course>, fragType: String, title: String) {
        val courseList = Intent(this@CourseDetailActivity, CourseActivity::class.java)
        courseList.putExtra(Const.FRAG_TYPE, fragType)
        courseList.putExtra(Const.COURSE_LIST, coursesList)
        courseList.putExtra(Const.TITLE, title)
        startActivity(courseList)
    }

    private fun setSpannable(priceTV: TextView, courseItem: Course) {
        if (courseItem.isDiscounted) {
            priceTV.setText(courseItem.calMrp, TextView.BufferType.SPANNABLE)
            val spannable = priceTV.text as Spannable
            spannable.setSpan(
                    StrikethroughSpan(),
                    2,
                    Helper.calculatePriceBasedOnCurrency(courseItem.mrp).length + 2,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            priceTV.text = courseItem.calMrp
        }
    }

    // isError:  Whether there is an error or success response
    // layoutType: Whether there is an internet issue or api Error like "Something went wrong"
    fun replaceErrorLayout(isError: Int, layoutType: Int) {
        // 0 is for no data found
        // 1 is for no internet connection
        // 2 is for something went wrong or everything else.
        if (layoutType == 0) {
            (findViewById<View>(R.id.errorImageIV) as ImageView).setImageResource(R.mipmap.no_post_found)
            (findViewById<View>(R.id.errorMessageTV) as TextView).text =
                resources.getString(R.string.no_data_found)
            hideViews()
        } else if (layoutType == 1) {
            if (!Helper.isConnected(this)) {
                (findViewById<View>(R.id.errorImageIV) as ImageView).setImageResource(R.mipmap.no_internet)
                (findViewById<View>(R.id.errorMessageTV) as TextView).text =
                    resources.getString(R.string.internet_error_message)
            } else {
                (findViewById<View>(R.id.errorImageIV) as ImageView).setImageResource(R.mipmap.something_went_wrong)
                (findViewById<View>(R.id.errorMessageTV) as TextView).text =
                    resources.getString(R.string.exception_api_error_message)
                hideViews()
            }
        } else if (layoutType == 2) {
            (findViewById<View>(R.id.errorImageIV) as ImageView).setImageResource(R.mipmap.something_went_wrong)
            (findViewById<View>(R.id.errorMessageTV) as TextView).text =
                resources.getString(R.string.exception_api_error_message)
            hideViews()
        }

        findViewById<View>(R.id.errorLL).visibility = if (isError == 1) View.VISIBLE else View.GONE
        binding.container.visibility = if (isError != 1) View.VISIBLE else View.GONE
    }

    private fun hideViews() {
        findViewById<View>(R.id.errorMessageTV2).visibility = View.GONE
        findViewById<View>(R.id.tryAgainBtn).visibility = View.GONE
        findViewById<View>(R.id.enrollNow).visibility = View.GONE
        findViewById<View>(R.id.oops).visibility = View.GONE
    }

    fun showFreetrialDialog(freeMessage: String) {

        val alertBuild = AlertDialog.Builder(this)
        alertBuild
            //.setTitle(ctx.getString(R.string.update_app_dialog_title))
            .setMessage(freeMessage)
            .setCancelable(false)
        /*.setNegativeButton("OK") { dialog, whichButton ->
            dialog.dismiss()
        }*/
        val dialog = alertBuild.create()
        dialog.window!!.attributes.gravity = Gravity.BOTTOM
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setBackgroundDrawableResource(R.drawable.bg_free_trail_dialog)
        try {
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val alertTitle = this.resources.getIdentifier("alertTitle", Constants.Extras.ID, "android")
        (dialog.findViewById<View>(alertTitle) as TextView).gravity = Gravity.CENTER
        (dialog.findViewById<View>(alertTitle) as TextView).gravity = Gravity.CENTER

        Handler().postDelayed(object : Runnable {
            override fun run() {

                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }, 4000)

    }


    // added by anil
    private fun removeFromCart(item: Course) {
        // asynchronous operation to remove course from cart.
        //  progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.removeCourseFromCart(SharedPreference.getInstance().loggedInUser.id, item.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                //  progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            // courseList.remove(item)
                            // notifyDataSetChanged()
                            //  Toast.makeText(context, "remove done", Toast.LENGTH_SHORT).show()
                            // myCartInterface.onClickDelete()

                            addToCart(item)

                        }/* else {
                            Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 0)
                            RetrofitResponse.getApiData(context as Activity?, jsonResponse.optString(Const.AUTH_CODE))
                        }*/
                    } catch (e: JSONException) {
                        //   Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //   progress.dismiss()
                //  Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 1)
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
       // Toast.makeText(applicationContext, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()


    }

}