package com.emedicoz.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emedicoz.app.Books.Activity.BookHomeActivity
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.cart.activity.MyCartActivity
import com.emedicoz.app.courses.activity.MyScorecardActivity
import com.emedicoz.app.courses.adapter.DrawerCourseCategoryAdapter
import com.emedicoz.app.courses.adapter.IndexingAdapter
import com.emedicoz.app.courses.fragment.CommonFragForList
import com.emedicoz.app.courses.fragment.NewTestFragment
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.dailychallenge.activity.DailyChallengeActivity
import com.emedicoz.app.dialog.UpdateRatingDialog
import com.emedicoz.app.feeds.activity.*
import com.emedicoz.app.feeds.adapter.MyListAdapter
import com.emedicoz.app.modelo.*
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.modelo.courses.CourseCategory
import com.emedicoz.app.modelo.courses.CoursesData
import com.emedicoz.app.modelo.courses.Review
import com.emedicoz.app.newsandarticle.NewsAndArticle
import com.emedicoz.app.mycourses.MyCourseActivity
import com.emedicoz.app.notifications.NotificationActivity
import com.emedicoz.app.notifications.fragment.AllNotificationFragment
import com.emedicoz.app.podcastnew.activity.PodcastMainActivity
import com.emedicoz.app.rating.GetQbankRating
import com.emedicoz.app.rating.Rating
import com.emedicoz.app.recordedCourses.fragment.RecordedCoursesFragment
import com.emedicoz.app.response.MasterFeedsHitResponse
import com.emedicoz.app.response.MasterRegistrationResponse
import com.emedicoz.app.response.registration.StreamResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.support.HelpSupportActivity
import com.emedicoz.app.utilso.*
import com.emedicoz.app.video.fragment.DVLFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nex3z.flowlayout.FlowLayout
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.uxcam.UXCam
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.nav_drawer_header.*
import kotlinx.android.synthetic.main.tv.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.util.*


class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    lateinit var navController: NavController
    lateinit var navhost: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    var courseStatus = true
    var masterHitStatus: Boolean = true
    var videoStatus = true
    private var isMenuClicked = false
    private var cprStatus = 0
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileImageText: ImageView
    private lateinit var damsIdTV: TextView
    private lateinit var specialityTV: TextView
    lateinit var floatingActionButton: FloatingActionButton
    var user: User? = null
    var tagsArrayList: ArrayList<Tags>? = null
    private var expandableListTitle: ArrayList<String>? = null
    private var listView: ListView? = null
    private var listAdapter: MyListAdapter? = null
    private var masterRegistrationResponse: MasterRegistrationResponse? = null
    private lateinit var navHeaderLL: LinearLayout
    private lateinit var navButtonLL: LinearLayout
    private var stream: StreamResponse? = null
    private var mProgress: Progress? = null
    lateinit var streamPopUp: PopupWindow
    private lateinit var navStreamLL: LinearLayout
    private lateinit var streamLayout: LinearLayout
    lateinit var streamTV: TextView
    lateinit var tvStream: TextView
    var videoTableArrayList = ArrayList<Videotable>()
    var masterFeedsHitResponse: MasterFeedsHitResponse? = null
    var frag_type = ""
    var frag_type_study = ""
    var courseCategory: CourseCategory? = null
    var type: String? = null
    lateinit var toolbarHome: Toolbar
    var myMenu: Menu? = null
    lateinit var fragment: Fragment
    var darkMode = false
    lateinit var tvCount: TextView
    lateinit var tvCartCount: TextView
    lateinit var relativeCount: RelativeLayout
    lateinit var relative_cart_count: RelativeLayout
    lateinit var toolbarHeader: LinearLayout
    lateinit var imageView8: ImageView
    lateinit var itemNotification: MenuItem
    lateinit var newsAndArticleFragment: MenuItem
    lateinit var bookNav: MenuItem
    lateinit var podcastFragment: MenuItem
    lateinit var itemMyCartFragment: MenuItem
    lateinit var item_search: MenuItem
    lateinit var iv_course_drawer: MenuItem
    lateinit var vNameTV: TextView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var searchManager: SearchManager
    lateinit var search: SearchView
    var trendingArrayList: ArrayList<Review>? = null
    var searchText: String? = null
    lateinit var bookingsLay: LinearLayout
    lateinit var popupWindow: PopupWindow
    var bookingsView = ArrayList<View>()
    var rating = 0f
    lateinit var dialogPlus2: DialogPlus
    lateinit var updateRatingDialog: UpdateRatingDialog
    var navHostController: NavHostFragment? = null
    private var flow_layout_top_searches_courses: FlowLayout? = null
    private var etSearch: EditText? = null
    private var ivClearSearch: ImageView? = null
    private var ivIconSearch: ImageView? = null
    private var tvAllCategory: TextView? = null
    var rvCourseCategory: RecyclerView? = null
    private var tvError: TextView? = null
    var cross: ImageView? = null
    private var drawerCourseCategoryAdapter: DrawerCourseCategoryAdapter? = null
    private val newCoursesDataArrayList = ArrayList<CoursesData>()


    private val listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                itemNotification = myMenu?.findItem(R.id.notification)!!
                relativeCount = itemNotification.actionView.findViewById(R.id.relativeCount)
                tvCount = itemNotification.actionView.findViewById(R.id.tvCount)
                imageView8 = itemNotification.actionView.findViewById(R.id.imageView8)
                itemMyCartFragment = myMenu?.findItem(R.id.myCart)!!
                relative_cart_count = itemMyCartFragment.actionView.findViewById(R.id.relative_cart_count)
                tvCartCount = itemMyCartFragment.actionView.findViewById(R.id.tv_cartCount)
                item_search = myMenu?.findItem(R.id.app_bar_search)!!
                search = item_search.actionView as SearchView
                iv_course_drawer = myMenu?.findItem(R.id.iv_course_drawer)!!
                var cartCount = SharedPreference.getInstance().getInt(Const.CART_COUNT).toString()
                if (cartCount > 0.toString()) {
                    tvCartCount.visibility = View.VISIBLE
                    tvCartCount.text = cartCount + "  "
                } else {
                    tvCartCount.visibility = View.GONE
                }
                networkCallForNotificationCount()

                search.setOnSearchClickListener {
                    imageView8.visibility = View.GONE
                    relative_cart_count.visibility = View.GONE
                    relativeCount.visibility = View.GONE

                }
                search.setOnCloseListener(object : SearchView.OnCloseListener {
                    override fun onClose(): Boolean {
                        imageView8.visibility = View.VISIBLE
                        relative_cart_count.visibility = View.VISIBLE
                        return false
                    }
                })

                search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(query: String?): Boolean {
                        return true
                    }
                })

                relativeCount.setOnClickListener(View.OnClickListener {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
//                    val action = MainNavGraphDirections.actionGlobalNotificationFragment()
//                    navController.navigate(action)
//                    itemSavedNotesFragment.isVisible = false
//                    itemMyCartFragment.isVisible = false
//                    item_search.isVisible = false
//                    tvCount.visibility = View.GONE

                })

                relative_cart_count.setOnClickListener(View.OnClickListener {
                    val intent = Intent(this, MyCartActivity::class.java)
                    startActivity(intent)
                    //val action = MainNavGraphDirections.actionGlobalMyCartFragment()
//                    navController.navigate(action)
//                    itemNotification.isVisible = false
//                    itemMyCartFragment.isVisible = false
//                    item_search.isVisible = false
//                    toolbarHeader.visibility = View.GONE
//                    relative_cart_count.visibility = View.VISIBLE
                })



                if (destination.id == R.id.videoFragment) {
                    item_search.isVisible = true

                } else if (destination.id == R.id.recordedFragment) {
                    itemNotification.isVisible = true
                    item_search.isVisible = false
                    tvCount.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarHeader.visibility = View.VISIBLE
                    itemMyCartFragment.isVisible = true

                } else if (destination.id == R.id.feedsFragment) {
                    item_search.isVisible = true
                } else {
                    itemNotification.isVisible = true
                    itemMyCartFragment.isVisible = true
                    item_search.isVisible = false
                    tvCount.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarHeader.visibility = View.GONE
                    relativeCount.visibility = View.VISIBLE
                }
            }


    lateinit var drawerSwitch: Switch
    lateinit var menuItem: MenuItem

    fun changeFollowingExpert(people: People, type: Int) {
        val masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.expert_list != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse.expert_list
                )
        ) {
            for (ppl in masterFeedsHitResponse.expert_list) {
                if (ppl.id.equals(people.id, ignoreCase = true)) ppl.isWatcher_following = type != 0
            }
        }
        SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse)
    }

    fun changeFollowingPeople(people: People, type: Int) {
        val masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        if (masterFeedsHitResponse != null && masterFeedsHitResponse.people_you_may_know_list != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse.people_you_may_know_list
                )
        ) {
            for (ppl in masterFeedsHitResponse.people_you_may_know_list) {
                if (ppl.id.equals(people.id, ignoreCase = true)) ppl.isWatcher_following = type != 0
            }
        }
        SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse)
    }

    fun initStreamList() {
        val mod_selected_stream =
                SharedPreference.getInstance().getString(Const.MODERATOR_SELECTED_STREAM)
        var i = 0
        // this is hidden for all type of user as client asked
        navButtonLL.visibility = View.VISIBLE
        while (i < masterRegistrationResponse!!.main_category.size) {
            if (TextUtils.isEmpty(mod_selected_stream)
                    && user != null && user!!.user_registration_info != null && user!!.user_registration_info.master_id == masterRegistrationResponse!!.main_category[i].id
            ) {
                stream = masterRegistrationResponse!!.main_category[i]
                setModeratorSelectedStream(stream!!)
            } else if (mod_selected_stream == masterRegistrationResponse!!.main_category[i].id) {
                stream = masterRegistrationResponse!!.main_category[i]
                setModeratorSelectedStream(stream!!)
            }
            i++
        }
        if (stream != null) {
            SharedPreference.getInstance().putString(Constants.SharedPref.STREAM_ID, stream!!.id)
            Log.e("Streamdetail", stream!!.id + "    " + stream!!.text_name)
            streamTV.text = stream!!.text_name
            tvStream.text = let { stream!!.text_name }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        setContentView(R.layout.activity_home)
        toolbarHome = toolbar
        setSupportActionBar(toolbarHome)

        navController = NavController(this)





        //add UX cam for monitoring
        UXCam.startWithKey(Constants.CAM_KEY)
        toolbarHeader = findViewById(R.id.toolbarHeader)
//        toolbarHeader.visibility = View.VISIBLE
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottom_nav_view.itemIconTintList = null
        System.out.println("user_id--------------------------" + SharedPreference.getInstance().loggedInUser.id)
        navHostController =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostController!!.navController
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.recordedFragment,
                        R.id.liveCoursesFragment,
                        R.id.videoFragment,
                        R.id.studyFragment,
                        R.id.feedsFragment
                ), drawer_layout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottom_nav_view.setupWithNavController(navController)
        nav_drawer.setupWithNavController(navController)

        val frag_type = intent.getStringExtra(Const.FRAG_TYPE)
        System.out.println("frag_type-----------------------------"+frag_type)

        if (frag_type != null && frag_type == Constants.StudyType.TESTS) {
            val bundle = Bundle()
            bundle.putString(Const.FRAG_TYPE, "tests")
            //bundle.putString(Constants.Type.STUDY_TYPE_TEST, "2")
            navController.navigate(R.id.studyFragment, bundle)

        } else if (frag_type != null && frag_type == Constants.StudyType.QBANKS) {
            navController.navigate(R.id.studyFragment)
//        }else if (frag_type !=null && frag_type == "All Courses"){
//            val bundle = Bundle()
//            //navController.navigate(R.id.studyFragment)
//            bundle.putString(Constants.Type.STUDY_TYPE_TEST, "2")
//            navController.navigate(R.id.studyFragment, bundle)
////            val fragment = NewTestFragment()
////            val transaction = supportFragmentManager.beginTransaction()
////            transaction.replace(R.id.fl_container, fragment)
////            transaction.commit()
        }


        newsAndArticleFragment = nav_drawer.menu.findItem(R.id.NewsAndArticleFragment)
        podcastFragment = nav_drawer.menu.findItem(R.id.podcast)
       bookNav = nav_drawer.menu.findItem(R.id.bookNav)

        newsAndArticleFragment = nav_drawer.menu.findItem(R.id.NewsAndArticleFragment)
        podcastFragment = nav_drawer.menu.findItem(R.id.podcast)


        darkMode = SharedPreference.getInstance().getBoolean(Const.DARK_MODE)
        //navigation drawer open then click switch event call dark mode
        drawerSwitch = nav_drawer.menu.findItem(R.id.app_bar_switch).actionView.findViewById(R.id.darkMode)
        drawerSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    ThemeHelper.applyTheme("dark")
                    SharedPreference.getInstance().putBoolean(Const.DARK_MODE, true)
                } else {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    ThemeHelper.applyTheme("light")
                    SharedPreference.getInstance().putBoolean(Const.DARK_MODE, false)
                }
                //recreate()
            }
        })
        //end of line---------------------------

        //now call the navigation drawer item click listener------------------------------[
        nav_drawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.join -> {
                    mProgress!!.show()
                    val affiliateId = SharedPreference.getInstance().loggedInUser.affiliate_user_id
                    System.out.println("affiliateId------------------------------------"+affiliateId)
                    if (affiliateId == null || affiliateId.isEmpty()) {
                        val intent = Intent(this, JoinReferralSignUpActivity::class.java)
                        startActivity(intent)
                        mProgress!!.dismiss()
//                        val action = MainNavGraphDirections.actionGlobalReferralSignUp()
//                        navController.navigate(action)
                    } else {
                        //val intent = Intent(this, JoinReferralSignUpActivity::class.java)
                        val intent = Intent(this, ReferEarnNowActivity::class.java)
                        startActivity(intent)
                        mProgress!!.dismiss()
//                        val action = MainNavGraphDirections.actionGlobalReferEarnNowFragment()
//                        navController.navigate(action)
                    }
                }
                R.id.rating -> {
                    getAppRating()
                    //appRatingDialog()
//                    val status = SharedPreference.getInstance().getStatus(Const.IS_FEED_STATUS, "")
//                    if (status.equals("")){
//                        getAppRating()
//                        //appRatingDialog()
//                    }else{
//                        Toast.makeText(this, "All Ready rated", Toast.LENGTH_LONG).show()
//                    }
                }
                R.id.rewardPoints -> {
                    val intent = Intent(this, RewardsPointsActivity::class.java)
                    startActivity(intent)
                }
                R.id.appSettings -> {
                    var intent = Intent(this, AppSettingActivity::class.java)
                    startActivity(intent)
                }
                R.id.helpSupport -> {
                    val intent = Intent(this, FeedbackActivity::class.java)
                    startActivity(intent)
                }

                R.id.myScorecard ->{
                    val intent = Intent(this, MyScorecardActivity::class.java)
                    startActivity(intent)
                }

                R.id.dailyChallenge ->{
                    val intent = Intent(this, DailyChallengeActivity::class.java)
                    startActivity(intent)
                }

                R.id.myCourse ->{
                    val intent = Intent(this, MyCourseActivity::class.java)
                    startActivity(intent)
                }

                R.id.savedNotes ->{
                    val intent = Intent(this, MyBookmarksActivity::class.java)
                    startActivity(intent)
                }

                R.id.help_support -> {
                    val intent = Intent(this, HelpSupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.NewsAndArticleFragment -> {
                    val intent = Intent(this, NewsAndArticle::class.java)
                    startActivity(intent)
                }
               R.id.bookNav -> {
                   val intent = Intent(this,BookHomeActivity::class.java)
                    startActivity(intent)
               }
                
               R.id.podcast -> {
                  val intent = Intent(this, PodcastMainActivity::class.java)
                   startActivity(intent)
               }

                else -> it.onNavDestinationSelected(navController) || super.onOptionsItemSelected(it)

            }
            drawer_layout.closeDrawers()
            true
        }

        //----------------------------end---------------------
        updateSwitchCompat()
        initView()
        tagsArrayList = ArrayList()
        tagsArrayList?.addAll(Helper.getTagsForUser())
        getNavData()

        mProgress = Progress(this@HomeActivity)
        mProgress!!.setCancelable(false)

        if (masterRegistrationResponse != null && !masterRegistrationResponse!!.main_category
                        .isEmpty()
        ) initStreamList() else networkCallForMasterRegData() //networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);


        tagsArrayList = ArrayList()
        tagsArrayList!!.addAll(Helper.getTagsForUser())
        getNavData()

        if (SharedPreference.getInstance().masterHitResponse == null) {
            networkCallForMasterHit()
        }

        floatingActionButton.setOnClickListener(View.OnClickListener {
            fragment = getCurrentFragment()
            //System.out.println("currentFragment-----------------------"+fragment)
            if (fragment is com.emedicoz.app.video.fragment.VideoFragment) {
                (fragment as com.emedicoz.app.video.fragment.VideoFragment).openFilterMenu()
            } else if (fragment is DVLFragment) {
                (fragment as DVLFragment).openFilterMenu()
            } else {
                Helper.GoToPostActivity(this@HomeActivity, null, Const.POST_FRAG)
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun initTagsAdapter(tagsList: ArrayList<Tags>?) {
        listAdapter = object : MyListAdapter(this, expandableListTitle, tagsList, cprStatus) {
            override fun onTextClick(title: String, type: Int) {
                //customNavigationClick(title)
            }
        }
        listView?.adapter = listAdapter
    }

    private fun updateSwitchCompat() {
        drawerSwitch.isChecked = darkMode
    }


    private fun initView() {
        val headerView: View = nav_drawer.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.profileImage)
        profileName = headerView.findViewById(R.id.profileName)
        profileImageText = headerView.findViewById(R.id.profileImageText)
        damsIdTV = headerView.findViewById(R.id.damsidTV)
        listView = headerView.findViewById(R.id.navLV)
        navHeaderLL = headerView.findViewById(R.id.nav_headerLL)
        navButtonLL = headerView.findViewById(R.id.nav_buttonLL)
        navStreamLL = headerView.findViewById(R.id.navStreamLL)
        streamLayout = findViewById(R.id.streamLayout)
        streamTV = headerView.findViewById(R.id.streamTV)
        tvStream = findViewById(R.id.tvStream)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        specialityTV = headerView.findViewById(R.id.specialityTV)

        vNameTV = nav_drawer.findViewById(R.id.vNameTV)
        vNameTV.text = Html.fromHtml("<b>Version- </b>" + Helper.getVersionName(this@HomeActivity))

        popupWindow = PopupWindow()

        navHeaderLL.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START)
            Helper.GoToProfileActivity(this, SharedPreference.getInstance().loggedInUser.id)
        }

        specialityTV.text = SharedPreference.getInstance().loggedInUser.email

        if (intent.extras != null) {
            type = intent.extras!!.getString(Constants.Extras.TYPE)
            if (intent.extras!!.getString(Const.FRAG_TYPE) != null) {
                frag_type = intent.extras!!.getString(Const.FRAG_TYPE)!!
                // frag_type_study = intent.extras!!.getString(Const.FRAG_TYPE_STUDY)!!
                courseCategory = intent.extras!!.getSerializable(Const.COURSE_CATEGORY) as CourseCategory?
            }
        } else {
            //else condition code
        }



        navStreamLL.setOnClickListener(View.OnClickListener {
            if (masterRegistrationResponse != null && masterRegistrationResponse!!.main_category != null &&
                    !masterRegistrationResponse!!.main_category.isEmpty()
            ) {
                showPopMenuForStream(navStreamLL)
            } else {
                isMenuClicked = true
                networkCallForMasterRegData() //networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
            }
        })

        streamLayout.setOnClickListener {
            if (masterRegistrationResponse != null && masterRegistrationResponse!!.main_category != null &&
                    !masterRegistrationResponse!!.main_category.isEmpty()
            ) {
                showPopMenuForStream(streamLayout)
            } else {
                isMenuClicked = true
                networkCallForMasterRegData() //networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
            }
        }

    }


    fun setModeratorSelectedStream(stream: StreamResponse) {
        SharedPreference.getInstance().putString(Const.MODERATOR_SELECTED_STREAM, stream.id)
        tagsArrayList = ArrayList()
        tagsArrayList!!.addAll(Helper.getTagsForUser())
        getNavData()
    }

    override fun onResume() {
        super.onResume()
        callOnResume()

        //cartTV.setText(SharedPreference.getInstance().getInt(Const.CART_COUNT).toString())
    }

    @SuppressLint("SetTextI18n")
    private fun callOnResume() {
        user = SharedPreference.getInstance().loggedInUser
        user?.name = Helper.CapitalizeText(user?.name)
        //networkCallForAppVersion()

        if (!TextUtils.isEmpty(user?.profile_picture)) {
            profileImage.visibility = View.VISIBLE
            profileImageText.visibility = View.GONE
            Glide.with(this).load(user?.profile_picture).into(profileImage)
        } else {
            val dr: Drawable? = Helper.GetDrawable(user?.name, this, user?.id)
            if (dr != null) {
                profileImage.visibility = View.GONE
                profileImageText.visibility = View.VISIBLE
                profileImageText.setImageDrawable(dr)
            } else {
                profileImage.visibility = View.VISIBLE
                profileImageText.visibility = View.GONE
                profileImage.setImageResource(R.mipmap.default_pic)
            }
        }

        if (SharedPreference.getInstance().getBoolean(Const.IS_PROFILE_CHANGED)) {
            tagsArrayList?.addAll(Helper.getTagsForUser())
            getNavData()
        }
        if (SharedPreference.getInstance().getBoolean(Const.IS_STREAM_CHANGE)) {

//            coursesLL.performClick();
            networkCallForMasterHit() //networkCall.NetworkAPICall(API.API_GET_MASTER_HIT, false);
        }

        profileName.text = user?.name
        if (!TextUtils.isEmpty(user?.dams_tokken)) damsIdTV.visibility = View.VISIBLE else damsIdTV.visibility = View.GONE

        damsIdTV.text = HtmlCompat.fromHtml(
                "<b>Dams Id:</b> " + if (TextUtils.isEmpty(user?.dams_tokken)) "" else user?.dams_tokken,
                HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun networkCallForAppVersion() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        Log.e("BASE", "networkCallForAppVersion: ")
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getAppVersion()
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Log.e("BASE:appVersion", "onResponse: ")
                            val data = GenericUtils.getJsonObject(jsonResponse)
                            val androidVersion = data.optString("android")
                            val aCode: Int =
                                    if (androidVersion.isEmpty()) 0 else androidVersion.trim { it <= ' ' }
                                            .toInt()
                            if (Helper.getVersionCode(this@HomeActivity) < aCode)
                                Helper.getVersionUpdateDialog(this@HomeActivity)
                        } else {
                            val data: JSONObject
                            var popupMessage: String? = ""
                            data = GenericUtils.getJsonObject(jsonResponse)
                            popupMessage = data.getString("popup_msg")
                            RetrofitResponse.handleAuthCode(
                                    this@HomeActivity,
                                    jsonResponse.optString(Const.AUTH_CODE),
                                    popupMessage
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showExceptionMsg(this@HomeActivity, t)
            }
        })
    }

    private fun networkCallForMasterHit() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getMasterFeedForUser(
                SharedPreference.getInstance().loggedInUser.id,
                SharedPreference.getInstance().getString(Constants.SharedPref.USER_INFO)
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                val gson = Gson()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(TAG, " networkcallForMaster Home: $jsonResponse")
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val masterHitData = gson.fromJson(
                                    GenericUtils.getJsonObject(jsonResponse).toString(),
                                    MasterFeedsHitResponse::class.java
                            )
                            SharedPreference.getInstance().setMasterHitData(masterHitData)

                            manageOptionsVisibility(masterHitData)

                            /* if (masterHitData.getUser_detail().getCountry()==null || masterHitData.getUser_detail().getState()==null || masterHitData.getUser_detail().getCity()==null || masterHitData.getUser_detail().getCollege()==null){
                            Helper.userDetailMissingPopup(BaseABNavActivity.this,masterHitData.getPop_msg(), Const.REGISTRATION, Const.PROFILE);
                        }*///manageOptionsVisibility(masterHitData)
                            tagsArrayList = ArrayList()
                            tagsArrayList!!.addAll(Helper.getTagsForUser())
                            masterHitStatus = false
                            getNavData()
                            if (SharedPreference.getInstance().getBoolean(Const.IS_STREAM_CHANGE)) {
                                SharedPreference.getInstance()
                                        .putBoolean(Const.IS_STREAM_CHANGE, false)
//                                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
//                                if (fragment is RecordedCoursesFragment) refreshFragmentList(fragment, 1) else coursesLL.performClick()
                            }
                        } else {
                            val data: JSONObject
                            var popupMessage: String? = ""
                            data = GenericUtils.getJsonObject(jsonResponse)
                            popupMessage = data.getString("popup_msg")
                            RetrofitResponse.handleAuthCode(
                                    this@HomeActivity,
                                    jsonResponse.optString(Const.AUTH_CODE),
                                    popupMessage
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showExceptionMsg(this@HomeActivity, t)
            }
        })
    }


    // for nav hide and show item condition
    private fun manageOptionsVisibility(masterHitData: MasterFeedsHitResponse?) {

        if (masterHitData?.show_podcast != null && masterHitData?.show_podcast.equals("0", ignoreCase = true)) {
            podcastFragment.isVisible = false
        } else {
            podcastFragment.isVisible = true
        }

        if (masterHitData?.news_article != null && masterHitData?.news_article.equals("0", ignoreCase = true)) {
            newsAndArticleFragment.isVisible = false
       } else {
           newsAndArticleFragment.isVisible = true
       }


    }

    fun getNavData() {
        if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
            networkCallForValidateDAMSUser()
        }
        val tags = ArrayList<Tags>()
        if (tagsArrayList != null && !tagsArrayList!!.isEmpty()) {
            tags.addAll(tagsArrayList!!)
        }

        //this is for the main titles of the navigation drawer
        expandableListTitle = Helper.gettitleList(this)
        //this is for the specified tags under specialities
        val masterResponse = SharedPreference.getInstance().masterHitResponse
        if (masterResponse != null) {
            cprStatus = masterResponse.cpr_view
        }
        // setFourthTab()
        initTagsAdapter(tags)
    }


    private fun networkCallForValidateDAMSUser() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        Log.e("BASE", "networkcallForValidateDAMSUser: ")
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response =
                apis.validateDAMSUser(SharedPreference.getInstance().loggedInUser.dams_tokken)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Log.e("BASE:validateDams", "onResponse: ")
                        } else {
                            Toast.makeText(
                                    this@HomeActivity,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                            ).show()
                            Helper.SignOutUser(this@HomeActivity)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {}
        })
    }

    private fun networkCallForMasterRegData() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        Log.e("BASE", "networkCallForMasterRegData: ")
        mProgress?.show()
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getMasterRegistrationResponse()
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (!this@HomeActivity.isFinishing) mProgress?.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            Log.e("BASE:regMaster", "onResponse: ")
                            mProgress?.dismiss()
                            masterRegistrationResponse = Gson().fromJson(
                                    GenericUtils.getJsonObject(jsonResponse).toString(),
                                    MasterRegistrationResponse::class.java
                            )
                            if (masterRegistrationResponse != null && !masterRegistrationResponse!!.main_category
                                            .isEmpty()
                            ) {
                                SharedPreference.getInstance()
                                        .setMasterRegistrationData(masterRegistrationResponse)
                                initStreamList()
                                if (isMenuClicked) {
                                    isMenuClicked = false
                                    showPopMenuForStream(navStreamLL)
                                    showPopMenuForStream(streamLayout)
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
                                            this@HomeActivity,
                                            jsonResponse.optString(Constants.Extras.MESSAGE),
                                            Toast.LENGTH_SHORT
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
                                        this@HomeActivity,
                                        jsonResponse.optString(Constants.Extras.MESSAGE),
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                            val data: JSONObject
                            var popupMessage: String? = ""
                            data = GenericUtils.getJsonObject(jsonResponse)
                            popupMessage = data.getString("popup_msg")
                            RetrofitResponse.handleAuthCode(
                                    this@HomeActivity,
                                    jsonResponse.optString(Const.AUTH_CODE),
                                    popupMessage
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                if (!this@HomeActivity.isFinishing) {
                    mProgress?.dismiss()
                    Helper.showExceptionMsg(this@HomeActivity, t)
                }
            }
        })
    }


    fun showPopMenuForStream(v: View?) {
        val popUpView =
                layoutInflater.inflate(R.layout.dialog_indexing, null) // inflating popup layout
        streamPopUp = PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        ) // Creation of popup
        streamPopUp.animationStyle = android.R.style.Animation_Dialog
        val container = window.decorView
        val context: Context = streamPopUp.contentView.context
        val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
        val indexingRV: RecyclerView = popUpView.findViewById(R.id.indexingRv)
        indexingRV.layoutManager = LinearLayoutManager(this)
        indexingRV.adapter = IndexingAdapter(this, masterRegistrationResponse!!.main_category)
        streamPopUp.showAsDropDown(v)
//        streamPopUp.showAtLocation(popUpView, Gravity.CENTER, (int) v.getX(), (int) v.getY() + v.getHeight()); // Displaying popup
    }

    fun setData(videos: ArrayList<Video>, id: String) {
        var flag = 0
        if (videoTableArrayList == null) {
            videoTableArrayList = Helper.getStorageInstance(this@HomeActivity)
                    .getRecordObject(Const.OFFLINE_VIDEOTAGS_DATA) as ArrayList<Videotable>
            if (videoTableArrayList == null) {
                videoTableArrayList = ArrayList()
            }
        }
        for (video in videoTableArrayList) {
            if (video.id == id) {
                video.videos = videos
                video.message = "Data Updated"
                flag = 1
            }
        }
        if (flag == 0) {
            val videotable = Videotable()
            videotable.id = id
            if (videos.isEmpty()) videotable.message =
                    getString(R.string.no_data_found) else videotable.message =
                    getString(R.string.data_inserted)
            videotable.videos = videos
            videoTableArrayList.add(videotable)
        }
        Helper.getStorageInstance(this)
                .addRecordStore(Const.OFFLINE_VIDEOTAGS_DATA, videoTableArrayList)
    }

    fun getData(id: String): String? {
        if (videoTableArrayList != null && !videoTableArrayList.isEmpty()) {
            for (video in videoTableArrayList) {
                if (video.id == id) {
                    return Gson().toJson(video)
                }
            }
        } else if (!videoStatus || !Helper.isConnected(this)) {
            videoTableArrayList = Helper.getStorageInstance(this)
                    .getRecordObject(Const.OFFLINE_VIDEOTAGS_DATA) as ArrayList<Videotable>
            if (videoTableArrayList != null) {
                for (video in videoTableArrayList) {
                    if (video.id == id) {
                        return Gson().toJson(video)
                    }
                }
            }
        }
        val videotable = Videotable()
        videotable.message = getString(R.string.no_record_found)
        return Gson().toJson(videotable)
    }


    //get banner data
    fun getBannerData(): Banner? {
        masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        if (masterFeedsHitResponse != null && masterFeedsHitResponse!!.banner_list != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse!!.banner_list
                )
        ) {
            val random = Random()
            return masterFeedsHitResponse!!.banner_list
                    .get(random.nextInt(masterFeedsHitResponse!!.banner_list.size))
        }
        return null
    }

    fun getVideoData(): ArrayList<Video>? {
        masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        val videoArrayList = ArrayList<Video>()
        if (masterFeedsHitResponse != null && masterFeedsHitResponse!!.suggested_videos != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse!!.suggested_videos
                )
        ) {
            var oneTimeCount = 0
            oneTimeCount =
                    if (masterFeedsHitResponse!!.suggested_videos.size > 20) masterFeedsHitResponse!!.suggested_videos.size * 30 / 100 // 30% of total size of list will go at once.
                    else masterFeedsHitResponse!!.suggested_videos.size // if size of list is lest than 20 then we will take the same size as list
            var i: Int
            Collections.shuffle(masterFeedsHitResponse!!.suggested_videos)
            i = 0
            while (i < oneTimeCount) {
                videoArrayList.add(masterFeedsHitResponse!!.suggested_videos[i])
                i++
            }
            return videoArrayList
        }
        return videoArrayList
    }

    fun getCourseData(): ArrayList<Course>? {
        masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        val courseArrayList = ArrayList<Course>()
        if (masterFeedsHitResponse != null && masterFeedsHitResponse!!.suggested_course != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse!!.suggested_course
                )
        ) {
            var oneTimeCount = 0
            oneTimeCount =
                    if (masterFeedsHitResponse!!.suggested_course.size > 20) masterFeedsHitResponse!!.suggested_course.size * 30 / 100 // 30% of total size of list will go at once.
                    else masterFeedsHitResponse!!.suggested_course.size // if size of list is lest than 20 then we will take the same size as list
            var i: Int
            Collections.shuffle(masterFeedsHitResponse!!.suggested_course)
            i = 0
            while (i < oneTimeCount) {
                courseArrayList.add(masterFeedsHitResponse!!.suggested_course[i])
                i++
            }
            return courseArrayList
        }
        return courseArrayList
    }

    // type:
    // 1: if we need all the list(this will be shown in view all people page)
    // or
    // 2: only limited list as per the percentage(this will be shown to feeds)
    fun getExpertPeopleData(type: Int): ArrayList<People>? {
        masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        val expertArrayList = ArrayList<People>()
        if (masterFeedsHitResponse != null && masterFeedsHitResponse!!.expert_list != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse!!.expert_list
                )
        ) {
            var oneTimeCount = 0
            if (type == 0) {
                oneTimeCount =
                        if (masterFeedsHitResponse!!.expert_list.size > 20) masterFeedsHitResponse!!.expert_list.size * 30 / 100 // 30% of total size of list will go at once.
                        else masterFeedsHitResponse!!.expert_list.size // if size of list is lest than 20 then we will take the same size as list
                var i: Int
                Collections.shuffle(masterFeedsHitResponse!!.expert_list)
                i = 0
                while (i < oneTimeCount) {
                    expertArrayList.add(masterFeedsHitResponse!!.expert_list[i])
                    i++
                }
            } else if (type == 1) {
                expertArrayList.addAll(masterFeedsHitResponse!!.expert_list)
            }

            // TODO to show Myself as Expert in "MEET the Expert"
            if (SharedPreference.getInstance().loggedInUser != null &&
                    !TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.is_expert) && SharedPreference.getInstance().loggedInUser.is_expert == "1"
            ) {
                val people = People()
                people.id = SharedPreference.getInstance().loggedInUser.id
                people.name = SharedPreference.getInstance().loggedInUser.name
                people.profile_picture = SharedPreference.getInstance().loggedInUser.profile_picture
                people.followers_count = SharedPreference.getInstance().loggedInUser.followers_count
                people.specification =
                        SharedPreference.getInstance().loggedInUser.user_registration_info.master_id_level_one_name
                expertArrayList.add(0, people)
            }
            return expertArrayList
        }
        return expertArrayList
    }


    // type:
    // 1: if we need all the list(this will be shown in view all people page)
    // or
    // 2: only limited list as per the percentage(this will be shown to feeds)
    fun getPeopleYMKData(type: Int): ArrayList<People>? {
        masterFeedsHitResponse = SharedPreference.getInstance().masterHitResponse
        val peopleArrayList = ArrayList<People>()
        if (masterFeedsHitResponse != null && masterFeedsHitResponse!!.people_you_may_know_list != null && !GenericUtils.isListEmpty(
                        masterFeedsHitResponse!!.people_you_may_know_list
                )
        ) {
            var oneTimeCount = 0
            if (type == 0) {
                oneTimeCount =
                        if (masterFeedsHitResponse!!.people_you_may_know_list.size > 20) masterFeedsHitResponse!!.people_you_may_know_list.size * 30 / 100 // 30% of total size of list will go at once.
                        else masterFeedsHitResponse!!.people_you_may_know_list.size // if size of list is lest than 20 then we will take the same size as list
                var i: Int
                Collections.shuffle(masterFeedsHitResponse!!.people_you_may_know_list)
                i = 0
                while (i < oneTimeCount) {
                    peopleArrayList.add(masterFeedsHitResponse!!.people_you_may_know_list[i])
                    i++
                }
            } else if (type == 1) {
                peopleArrayList.addAll(masterFeedsHitResponse!!.people_you_may_know_list)
            }
            return peopleArrayList
        }
        return peopleArrayList
    }

    //items selected call event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.iv_course_drawer -> {
                //Toast.makeText(this, "dkjfjhdjf", Toast.LENGTH_SHORT).show()
//                val action = MainNavGraphDirections.actionGlobalNotificationFragment()
//                navController.navigate(action)
                courseSidePanel()
                return true
            }
            R.id.savedNotes ->{
                val intent = Intent(this, MyBookmarksActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
        //return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    //call option menu items in toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        myMenu = menu
        Log.e(TAG, "onCreateOptionsMenu: ")
         navController.addOnDestinationChangedListener(listener)
        return super.onCreateOptionsMenu(menu)
    }

    //back press event call app exit
    override fun onBackPressed() {
        if (bottomNavigationView.selectedItemId == R.id.recordedFragment) {
            exitAppDialog()
        } else {
            bottomNavigationView.selectedItemId = R.id.recordedFragment
        }
    }


    //create function i call app exit dialog show
    private fun exitAppDialog() {
        val dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(ViewHolder(R.layout.bottom_sheet_dialog))
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .setContentBackgroundResource(R.drawable.dialog_top_corner)
                .create()

        val myView = dialogPlus.holderView
        val ivClose: ImageView = myView.findViewById(R.id.imageView7)
        val btnExit: Button = myView.findViewById(R.id.btnExit)

        ivClose.setOnClickListener { dialogPlus.dismiss() }

        btnExit.setOnClickListener {
            dialogPlus.dismiss()
            finish()
        }

        ivClose.setOnClickListener {
            dialogPlus.dismiss()
        }

        val tvStudy: TextView = myView.findViewById(R.id.tvStudy)
        tvStudy.setOnClickListener {
            navController.navigate(R.id.studyFragment)
            dialogPlus.dismiss()
        }
        val tvLiveClass: TextView = myView.findViewById(R.id.tvLiveClass)
        tvLiveClass.setOnClickListener {
            navController.navigate(R.id.liveCoursesFragment)
            dialogPlus.dismiss()
        }

        val tvRecordedCourses: TextView = myView.findViewById(R.id.tvRecordedCourses)
        tvRecordedCourses.setOnClickListener {
            navController.navigate(R.id.recordedFragment)
            dialogPlus.dismiss()
        }
        dialogPlus.show()
    }

    //create function logout
    private fun logout(title: String, message: String) {
        val v = Helper.newCustomDialog(this, false, title, message)
        val btnCancel: Button
        val btnSubmit: Button
        btnCancel = v.findViewById(R.id.btn_cancel)
        btnSubmit = v.findViewById(R.id.btn_submit)
        btnCancel.text = getString(R.string.no)
        btnSubmit.text = getString(R.string.yes)
        btnCancel.setOnClickListener { view: View? -> Helper.dismissDialog() }
        btnSubmit.setOnClickListener { view: View? ->
            Helper.dismissDialog()
            Helper.SignOutUser(this@HomeActivity)
        }
    }

    fun getCurrentFragment(): Fragment {
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)!!
    }

//    override fun onPause() {
//        navController.removeOnDestinationChangedListener(listener)
//        super.onPause()
//    }


    //notification count api call
    private fun networkCallForNotificationCount() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        Log.e("BASE", "networkCallForNotificationCount: ")
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getNotiCountForUser(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e("BaseAbNavActivity ", "networkCallForNotificationCount onResponse: $jsonResponse")
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val data = GenericUtils.getJsonObject(jsonResponse)
                            var notiCount: Int = if (data.optString(Const.COUNTER).isEmpty()) 0 else data.optString(Const.COUNTER).toInt()
                            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, notiCount)

                            if (notiCount > 0) {
                                imageView8.visibility = View.VISIBLE
                                tvCount.visibility = View.VISIBLE
                                tvCount.text = data.optString(Const.COUNTER)
                            } else {
                                imageView8.visibility = View.VISIBLE
                                tvCount.visibility = View.GONE
                            }
                        } else {
                            RetrofitResponse.getApiData(this@HomeActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showExceptionMsg(this@HomeActivity, t)
            }
        })
    }


    //app rating dialog calling---------------------------------and create function
    private fun appRatingDialog() {
        dialogPlus2 = DialogPlus.newDialog(this)
                .setContentHolder(ViewHolder(R.layout.dialog_rate_us_layout))
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .setContentBackgroundResource(R.drawable.dialog_top_corner)
                .create()

        val myView = dialogPlus2.holderView
        val tv_submit: TextView = myView.findViewById(R.id.tv_submit)
        val ratingRB: RatingBar = myView.findViewById(R.id.ratingRB)
        val imageView7: ImageView = myView.findViewById(R.id.imageView7)
        val imageView6: ImageView = myView.findViewById(R.id.imageView6)
        imageView6.setImageResource(R.drawable.ic__rating_layers)

        imageView7.setOnClickListener(View.OnClickListener {
            dialogPlus2.dismiss()
        })
        rating = 0f

        ratingRB.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            this.rating = rating
            if (rating < 4) {
                System.out.println("onRatingChanged: $rating")
            } else if (rating >= 4) {
                System.out.println("onRatingChanged: $rating")
            }
        }

        tv_submit.setOnClickListener(View.OnClickListener {
            if (rating == 0f) {
                GenericUtils.showToast(this, getString(R.string.rate_before_submit))
            } else if (rating < 4 && rating > 0) {
                // open a feedback dialog and submit the server
                val bundle = Bundle()
                bundle.putFloat("rating", rating)
                navController.navigate(R.id.feedbackDialog, bundle)
                dialogPlus2.dismiss()

            } else if (rating >= 4) {
                navController.navigate(R.id.ratingFragmentDialog)
                dialogPlus2.dismiss()
                ratingApi(SharedPreference.getInstance().loggedInUser.id, rating, "", "apprating")

            }
        })
        dialogPlus2.show()
    }

    //function create rating api call to send to our server
    private fun ratingApi(user_id: String, rating: Float, feedback: String, apprating: String) {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apis = ApiClient.createService(com.emedicoz.app.api.ApiInterface::class.java)
        val response = apis.addRating(user_id, rating, feedback, apprating)
        response?.enqueue(object : Callback<Rating?> {
            override fun onResponse(call: Call<Rating?>?, response: Response<Rating?>?) {
                val rating = response!!.body()
                if (rating?.status?.statuscode == 200) {
                    Toast.makeText(this@HomeActivity, rating.message.message, Toast.LENGTH_LONG).show()
                } else if (rating?.status?.statuscode == 201) {
                    Toast.makeText(this@HomeActivity, rating.message.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Rating?>?, t: Throwable?) {
                Helper.showExceptionMsg(this@HomeActivity, t)
            }
        })
    }


    private fun getAppRating() {
        updateRatingDialog = UpdateRatingDialog()
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
        }
        val apiInterface = ApiClient.createService(com.emedicoz.app.api.ApiInterface::class.java)
        apiInterface.getQbankRating(SharedPreference.getInstance().loggedInUser.id, "apprating").enqueue(object : Callback<GetQbankRating?> {
            override fun onResponse(call: Call<GetQbankRating?>, response: Response<GetQbankRating?>) {
                val getQbankRating = response.body()
                if (getQbankRating!!.status.statuscode == "200") {
                    if (getQbankRating.data != null) {
                        if (SharedPreference.getInstance().loggedInUser.id == getQbankRating.data.user_id) {
                            if (getQbankRating.data.rating < "4") {
                                val bundle = Bundle()
                                bundle.putString("rating", getQbankRating.data.rating)
                                navController.navigate(R.id.updateRatingDialog, bundle)
                                //updateRatingDialog.show(supportFragmentManager, bundle)
                            } else {
                                Toast.makeText(this@HomeActivity, "Already rated.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            //appRatingDialog()
                        }
                    } else {

                    }
                } else {

                }
            }

            override fun onFailure(call: Call<GetQbankRating?>, t: Throwable) {
                System.out.println("-----------------------------------")
                //Helper.showExceptionMsg(this@HomeActivity, t)
                appRatingDialog()
            }
        })
    }

    fun courseSidePanel() {
        val li = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.dialog_course_drawer_layout, null, false)
        val dialog = Dialog(this, R.style.drawerDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(view)
        Objects.requireNonNull(dialog.window)!!.setLayout(resources.getDimension(R.dimen.dp350).toInt(), LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window!!.setGravity(Gravity.END)
        dialog.show()

        flow_layout_top_searches_courses = view.findViewById(R.id.flow_layout_top_searches_courses)
        etSearch = view.findViewById(R.id.et_search)
        ivClearSearch = view.findViewById(R.id.iv_clear_search)
        ivIconSearch = view.findViewById(R.id.iv_icon_search)
        tvAllCategory = view.findViewById(R.id.tv_all_category)
        rvCourseCategory = view.findViewById(R.id.recycler_view_course_category)
        tvError = view.findViewById(R.id.tv_error)
        fragment = getCurrentFragment()
        //currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        cross = view.findViewById(R.id.cross)

        if (!GenericUtils.isListEmpty(trendingArrayList)) {
            createTopSearchedCourse()
        } else {
            networkCallForTrendingSearch(Const.DRAWER)
        }

        etSearch!!.setHint(R.string.search_course_hint)

        cross!!.setOnClickListener(View.OnClickListener { dialog.dismiss() })

        if (!TextUtils.isEmpty(searchText)) {
            ivClearSearch!!.setVisibility(View.VISIBLE)
            etSearch!!.setText(searchText)
            etSearch!!.setSelection(Helper.GetText(etSearch).length)
            ivIconSearch!!.setVisibility(View.GONE)
        } else {
            ivIconSearch!!.setVisibility(View.VISIBLE)
            ivClearSearch!!.setVisibility(View.GONE)
            etSearch!!.setText("")
        }
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) {
                    ivClearSearch!!.setVisibility(View.VISIBLE)
                    ivIconSearch!!.setVisibility(View.GONE)
                } else {
                    ivClearSearch!!.setVisibility(View.GONE)
                    ivIconSearch!!.setVisibility(View.VISIBLE)
                }
                filter(s.toString())
            }
        })
        etSearch!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(Helper.GetText(etSearch))) {
                    Toast.makeText(this@HomeActivity, R.string.enter_search_query, Toast.LENGTH_SHORT).show()
                } else {
                    //searchData(Helper.GetText(etSearch))
                }
                return@setOnEditorActionListener true
            }
            false
        }
        if (fragment is RecordedCoursesFragment) etSearch!!.setText((fragment as RecordedCoursesFragment).searchedKeyword)

        ivClearSearch!!.setOnClickListener { view1: View? ->
            searchText = ""
            etSearch!!.setText("")
            if (fragment is RecordedCoursesFragment) {
                (fragment as RecordedCoursesFragment).searchedKeyword = ""
            }
            //refreshFragmentList(currentFragment, 1)
            ivIconSearch!!.visibility = View.VISIBLE
        }

        rvCourseCategory!!.setLayoutManager(LinearLayoutManager(this@HomeActivity))
        if (fragment is RecordedCoursesFragment) {
            /*if (((RecordedCoursesFragment) currentFragment).isSearching) {
                if (!((RecordedCoursesFragment) currentFragment).viewModel.getSearchDataArrayList$app_debug().isEmpty())
                    setSearchCourseData(((RecordedCoursesFragment) currentFragment).viewModel.getSearchDataArrayList$app_debug());
                else
                    setErrorMessage(((RecordedCoursesFragment) currentFragment).errorMessage);
            } else*/
            setSidePanelCourse((getCurrentFragment() as RecordedCoursesFragment).viewModel.orignalDataArrayList)
        }
        rvCourseCategory!!.addItemDecoration(EqualSpacingItemDecoration(15, EqualSpacingItemDecoration.VERTICAL))
    }

    fun setSidePanelCourse(coursesDataArrayList: ArrayList<CoursesData>) {
        if (rvCourseCategory != null) {
            if (!GenericUtils.isListEmpty(coursesDataArrayList)) {
                tvAllCategory!!.visibility = View.VISIBLE
                drawerCourseCategoryAdapter = DrawerCourseCategoryAdapter(coursesDataArrayList, this)
                rvCourseCategory!!.adapter = drawerCourseCategoryAdapter
                newCoursesDataArrayList.addAll(coursesDataArrayList)
            } else {
                tvError!!.visibility = View.VISIBLE
                rvCourseCategory!!.visibility = View.GONE
            }
        }
    }

    private fun filter(text: String) {
        // newCoursesDataArrayList.clear()
        //fragment = getCurrentFragment()
        val filteredlist: ArrayList<CoursesData> = ArrayList()
        for (coursesData in newCoursesDataArrayList) {
            if (coursesData.category_info.name.toLowerCase().contains(text.toLowerCase())) {
                //newCoursesDataArrayList.add(coursesData)
                filteredlist.add(coursesData)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            //Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            drawerCourseCategoryAdapter!!.filterList(filteredlist);
        }
//        if (!newCoursesDataArrayList.isEmpty()) {
//            tvError!!.visibility = View.GONE
//            rvCourseCategory!!.visibility = View.VISIBLE
//            drawerCourseCategoryAdapter!!.filterList(newCoursesDataArrayList)
//        } else {
//            tvError!!.visibility = View.VISIBLE
//            tvError!!.text = getString(R.string.no_match_found)
//            rvCourseCategory!!.visibility = View.GONE
//        }
    }


    private fun createTopSearchedCourse() {
        flow_layout_top_searches_courses!!.removeAllViews()
        for (review in trendingArrayList!!) {
            val text = TextView(this@HomeActivity)
            text.background = ContextCompat.getDrawable(this, R.drawable.bg_capsule_fill_white_selector)
            text.setTextColor(ContextCompat.getColor(this, R.color.black))
            text.setPadding(30, 7, 30, 7)
            text.textSize = 12f
            text.text = review.text
            text.tag = review.text
            text.setOnClickListener { view: View ->
                searchText = view.tag as String
                etSearch!!.setText(view.tag as String)
                etSearch!!.setSelection(Helper.GetText(etSearch).length)
                if (fragment is CommonFragForList) {
                    (fragment as CommonFragForList).etSearch.setText(view.tag as String)
                    (fragment as CommonFragForList).etSearch.setSelection(Helper.GetText(etSearch).length)
                }
                //searchData(view.tag as String)
            }
            flow_layout_top_searches_courses!!.addView(text)
        }
    }

    fun searchData(tag: String) {
        searchText = tag
        fragment = getCurrentFragment()
        //fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)!!
        SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, tag)
        if (popupWindow != null) popupWindow.dismiss()
        (fragment as RecordedCoursesFragment).searchedKeyword = SharedPreference.getInstance().getString(Constants.SharedPref.COURSE_SEARCHED_QUERY)
        //refreshFragmentList(fragment, 0)
        Helper.closeKeyboard(this@HomeActivity)
    }

    private fun networkCallForTrendingSearch(type: String) {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apis = ApiClient.createService(ApiInterface::class.java)
        val response = apis.getTrendingSearch(SharedPreference.getInstance().loggedInUser.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        //Log.e(BaseABNavActivity.TAG, "onResponse: $jsonResponse")
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val data = GenericUtils.getJsonArray(jsonResponse)
                            trendingArrayList = ArrayList()
                            for (i in 0 until data.length()) {
                                val review = gson.fromJson(data.optJSONObject(i).toString(), Review::class.java)
                                trendingArrayList!!.add(review)
                            }
                            if (type == Const.DRAWER) {
                                createTopSearchedCourse()
                            } else {
                                //if (!searchView.isIconified()) initTrendingAdapter()
                            }
                        } else {
                            Toast.makeText(this@HomeActivity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(this@HomeActivity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showExceptionMsg(this@HomeActivity, t)
            }
        })
    }

}


