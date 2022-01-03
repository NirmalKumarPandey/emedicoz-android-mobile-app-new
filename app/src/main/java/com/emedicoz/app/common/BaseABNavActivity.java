package com.emedicoz.app.common;

/**
 * Created by Cbc-03 on 06/07/17.
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.PodcastNewKotlin;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.bookmark.BookmarkFrag;
import com.emedicoz.app.cart.MyCartFragment;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.CourseSearchAdapter;
import com.emedicoz.app.courses.adapter.DrawerCourseCategoryAdapter;
import com.emedicoz.app.courses.adapter.IndexingAdapter;
import com.emedicoz.app.courses.fragment.CommonFragForList;
import com.emedicoz.app.courses.fragment.DownloadFragment;
import com.emedicoz.app.courses.fragment.LCPracticeChildFragment;
import com.emedicoz.app.courses.fragment.MyCoursesFragment;
import com.emedicoz.app.courses.fragment.MyScorecardFragment;
import com.emedicoz.app.courses.fragment.QRCodeFragment;
import com.emedicoz.app.courses.fragment.StudyFragment;
import com.emedicoz.app.cpr.fragment.UpToDateFragment;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dailychallenge.DailyChallengeDashboard;
import com.emedicoz.app.epubear.utils.FileUtils;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.adapter.MyListAdapter;
import com.emedicoz.app.feeds.fragment.DailyQuizFragment;
import com.emedicoz.app.feeds.fragment.FeedsFragment;
import com.emedicoz.app.feeds.fragment.SavedNotesFragment;
import com.emedicoz.app.liveCourses.fragments.LiveCoursesFragment;
import com.emedicoz.app.modelo.HelpAndSupport;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.Videotable;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.modelo.courses.Review;
import com.emedicoz.app.mycourses.MyCourseTabFragment;
import com.emedicoz.app.podcast.PhoneCallReceiver;
import com.emedicoz.app.recordedCourses.fragment.RecordedCoursesFragment;
import com.emedicoz.app.referralcode.ReferEarnNowFragment;
import com.emedicoz.app.referralcode.ReferralSignUp;
import com.emedicoz.app.registration.fragment.ChooseRegistrationPreference;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.registration.StreamResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.templateAdapters.HelpAndSupportAdapter;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.ThemeHelper;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.CheckConnection;
import com.emedicoz.app.video.fragment.DVLFragment;
import com.emedicoz.app.video.fragment.VideoFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.PopupWindow.INPUT_METHOD_NEEDED;
import static com.emedicoz.app.utilso.service.MyFirebaseMessagingService.CHANNEL_ID;

@SuppressLint("RestrictedApi")
public abstract class BaseABNavActivity extends AppCompatActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "BaseABNavActivity";
    public static FloatingActionButton postFAB;
    public static LinearLayout bottomPanelRL;
    public boolean feedStatus = true;
    public boolean videoStatus = true;
    public boolean courseStatus = true;
    public boolean masterHitStatus = true;
    public boolean dailyQuizClicked = false;
    public Fragment fragment, currentFragment;
    public Button medicalBtn;
    public Button dentalBtn;
    public ImageView profileImage, cross;
    public ImageView profileImageText;

    public SwipeRefreshLayout swipeRefreshLayout;
    public RelativeLayout titleRL;
    public SearchView searchView;
    public LinearLayout helpView;
    public TextView toolbarTitleTV;
    public TextView toolbarSubTitleTV;
    public TextView notifyTV, cartTV;
    private ImageView downArrowIV;
    private TextView editAffiliateProfile;
    private ImageView ivCourseDrawer;
    private TextView newsAndarticleNav;


    public TextView streamTV;
    public TextView podcastNav;
    public TextView feedsNav, testNav, myCourses, myQbankNav, myTestNav, qBankNav,
            courseNav, whatsappChat, myScorecard, myDownloads, videosNav, myTestSeries, myBookmarks,
            feedbackTV, inviteTV, appSettingTV, logoutTV, dailyQuizNav, joinForReward, registration, liveCourseNav, studyNav;
    public RelativeLayout cartIVLayout, notifyLayout;
    public ImageView notificationIconIV, imageQR, bookmarkIV, cartIV;
    public User user;
    public ArrayList<Tags> tagsArrayList;
    public ArrayList<Review> trendingArrayList;
    //  public ImageView refreshIV;
    public ArrayList<Videotable> videoTableArrayList = new ArrayList<>();
    public String apiType;
    public Toolbar toolbar;
    public DrawerLayout drawer;
    public LinearLayout savedNotesLL;
    public PopupWindow popupWindow;
    public ImageView crossHelp;
    public RecyclerView recyclerView_help;
    public EditText editTextHelp;
    public String questionQuery;
    public ImageView helpImageCross;
    public ImageView helpImageSearch;
    public LinearLayout btnStartChat;
    public Button startChat;
    public LinearLayout feedsLL;
    public LinearLayout videosLL, qBankLL, qbankLLCRS;
    public RecyclerView rvCourseCategory;
    public LinearLayout coursesLL, myCoursesLL, liveClassesLL;
    public LinearLayout navStreamLL;
    public LinearLayout cprLL;
    private ActionBarDrawerToggle toggle;
    private TextView rateUsNav;
    private Button enrollNow;
    private LinearLayout navHeaderLL, navButtonLL;
    private RelativeLayout mFragmentLayout;
    private LinearLayout errorLayout;
    private Button tryAgainBtn;
    private TextView profileName, specialityTV, damsIdTV, versionNameTV;
    private ListView listView;
    private MyListAdapter listAdapter;
    private ArrayList<String> expandableListTitle;
    private Tags tg = null;
    private String feedPref;
    private long backPressed = 0L;
    private MasterRegistrationResponse masterRegistrationResponse;
    private boolean isMenuClicked = false;
    private final ArrayList<View> bookingsView = new ArrayList<>();
    private LinearLayout bookingsLay;
    private StreamResponse stream;
    private int cprStatus = 0;
    private Progress mProgress;
    private RelativeLayout.LayoutParams relativeParams;
    private FlowLayout flow_layout_top_searches_courses;
    public CourseSearchAdapter courseSearchAdapter;
    private final ArrayList<CoursesData> newCoursesDataArrayList = new ArrayList<>();
    private DrawerCourseCategoryAdapter drawerCourseCategoryAdapter;
    private TextView tvError, tvAllCategory;
    private EditText etSearch;
    private ImageView ivClearSearch;
    private ImageView ivIconSearch;
    public String searchText, fragType = "";
    float rating;
    TextView feedsTV, coursesTV, myCoursesTV, qbankTV, videosTV, liveClassesTV, cprTV, studyTV, saveNotesTV;
    ImageView feedsIV, coursesIV, myCoursesIV, qbankIV, videosIV, liveClassesIV, cprIV, studyIV, saveNotesIV;
    Switch dayNightSwitch;
    boolean darkMode = false;

    private ArrayList<HelpAndSupport> helpAndSupportArrayList;
    public PopupWindow streamPopUp;

    public void initStreamList() {
        String mod_selected_stream = SharedPreference.getInstance().getString(Const.MODERATOR_SELECTED_STREAM);
        int i = 0;
        // this is hidden for all type of user as client asked
        navButtonLL.setVisibility(View.VISIBLE);
        while (i < masterRegistrationResponse.getMain_category().size()) {
            if (TextUtils.isEmpty(mod_selected_stream)
                    && user != null && user.getUser_registration_info() != null
                    && user.getUser_registration_info().getMaster_id().equals(masterRegistrationResponse.getMain_category().get(i).getId())) {
                stream = masterRegistrationResponse.getMain_category().get(i);
                setModeratorSelectedStream(stream);
            } else if (mod_selected_stream.equals(masterRegistrationResponse.getMain_category().get(i).getId())) {
                stream = masterRegistrationResponse.getMain_category().get(i);
                setModeratorSelectedStream(stream);
            }
            i++;
        }
        if (stream != null) {
            SharedPreference.getInstance().putString(Constants.SharedPref.STREAM_ID, stream.getId());
            Log.e("Streamdetail", stream.getId() + "    " + stream.getText_name());
            streamTV.setText(stream.getText_name());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_main);
        mProgress = new Progress(BaseABNavActivity.this);
        mProgress.setCancelable(false);
        registerReceiver(new com.emedicoz.app.Utils.service.ErrorAlertBroadcastReceiver(), new IntentFilter(Const.ERROR_ALERT_INTENT_FILTER));

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(new PhoneCallReceiver(), filter);

        // TODO: Move this to where you establish a user session
        Helper.logUser(this);
        Helper.getStorageInstance(this).deleteRecord(Const.SINGLE_COURSE_IDS);
        Helper.getStorageInstance(this).deleteRecord(Const.CURRICULAM_IDS);
        Helper.getStorageInstance(this).deleteRecord(Const.COURSESEEALL_IDS);
        Helper.getStorageInstance(this).deleteRecord(Const.SUBSCRIPTION_IDS);
        toolbar = findViewById(R.id.feeds_toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setHomeAsUpIndicator(R.mipmap.menu);
//
//        try {
//            // this to check is the service to check the downloading is not initialised then it will be initialised here.
//            if (SplashScreen.intent == null) {
//                SplashScreen.intent = new Intent(eMedicozApp.getAppContext(), OnClearFromRecentService.class);
//                startService(SplashScreen.intent);
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }

        user = SharedPreference.getInstance().getLoggedInUser();
        darkMode = SharedPreference.getInstance().getBoolean(Const.DARK_MODE);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Helper.HideKeyboard(BaseABNavActivity.this);
                if (popupWindow != null)
                    popupWindow.dismiss();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        initialiseViews();
        initSearchView();
        setClickListeners();
        relativeParams = new RelativeLayout.LayoutParams(new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);

        specialityTV.setText(SharedPreference.getInstance().getLoggedInUser().getEmail());

        //Init Tags even if the user is moderator
/*        if (!TextUtils.isEmpty(user.getIs_moderate()) && !user.getIs_moderate().equalsIgnoreCase("1")) {
            navButtonLL.setVisibility(View.GONE);
        } else */
        if (!TextUtils.isEmpty(user.getIs_moderate()) && user.getIs_moderate().equalsIgnoreCase("1")) {
            if (masterRegistrationResponse != null && !masterRegistrationResponse.getMain_category().isEmpty())
                initStreamList();
            else
                networkCallForMasterRegData();//networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
        }

        tagsArrayList = new ArrayList<>();
        tagsArrayList.addAll(Helper.getTagsForUser());
        getNavData();
        tryAgainBtn.setOnClickListener((View view) -> {
            replaceErrorLayout(0, 0);
            retryApiButton();
        });

        versionNameTV.setText(Html.fromHtml("<b>Version- </b>" + Helper.getVersionName(BaseABNavActivity.this)));

        // this hit will only pe called once the app is launched or the activity has been called.
        networkCallForNotificationCount();//networkCall.NetworkAPICall(API.API_GET_NOTIFICATION_COUNT, false);

        File dir = FileUtils.getAppFilesDir(this);
        deleteRecursive(dir);

        updateSwitchCompat();
        dayNightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ThemeHelper.applyTheme("dark");
                SharedPreference.getInstance().putBoolean(Const.DARK_MODE, true);
            } else {
                ThemeHelper.applyTheme("light");
                SharedPreference.getInstance().putBoolean(Const.DARK_MODE, false);
            }
            recreate();
        });

    }

    private void updateSwitchCompat() {
        dayNightSwitch.setChecked(darkMode);
    }

    private void initialiseViews() {

        streamTV = findViewById(R.id.streamTV);
        errorLayout = findViewById(R.id.errorLL);
        tryAgainBtn = findViewById(R.id.tryAgainBtn);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        postFAB = findViewById(R.id.postFAB);
        bottomPanelRL = findViewById(R.id.RL1);

        profileName = findViewById(R.id.profileName);
        specialityTV = findViewById(R.id.specialityTV);
        damsIdTV = findViewById(R.id.damsidTV);
        medicalBtn = findViewById(R.id.medicalbtn);
        dentalBtn = findViewById(R.id.dentalbtn);
        versionNameTV = findViewById(R.id.vNameTV);
        enrollNow = findViewById(R.id.enrollNow);
        imageQR = findViewById(R.id.imageQR);

        titleRL = findViewById(R.id.titleRL);
        toolbarTitleTV = findViewById(R.id.toolbartitleTV);
        toolbarSubTitleTV = findViewById(R.id.toolbarsubtitleTV);
        downArrowIV = findViewById(R.id.downarrowIV);

        notifyTV = findViewById(R.id.notifyTV);
        cartTV = findViewById(R.id.cartTV);
        searchView = findViewById(R.id.searchSV);
        helpView = findViewById(R.id.helpView);
        editAffiliateProfile = findViewById(R.id.editAffiliateProfile);
        ivCourseDrawer = findViewById(R.id.iv_course_drawer);

        notificationIconIV = findViewById(R.id.imageIV);
        cartIV = findViewById(R.id.cartIV);
        cartIVLayout = findViewById(R.id.cart_layout);
        notifyLayout = findViewById(R.id.notify_layout);
        bookmarkIV = findViewById(R.id.bookmarkIV);

        navStreamLL = findViewById(R.id.navStreamLL);
        feedsLL = findViewById(R.id.feedsLL);
        savedNotesLL = findViewById(R.id.savenotesLL);
        cprLL = findViewById(R.id.cprLL);
        videosLL = findViewById(R.id.videosLL);
        qBankLL = findViewById(R.id.qbankLL);
        qbankLLCRS = findViewById(R.id.qbankLLCRS);
        coursesLL = findViewById(R.id.coursesLL);
        myCoursesLL = findViewById(R.id.myCoursesLL);
        liveClassesLL = findViewById(R.id.liveClassesLL);

        profileImage = findViewById(R.id.profileImage);
        profileImageText = findViewById(R.id.profileImageText);

        listView = findViewById(R.id.navLV);
        navHeaderLL = findViewById(R.id.nav_headerLL);
        navButtonLL = findViewById(R.id.nav_buttonLL);
        // refreshIV = findViewById(R.id.refreshIV);
        feedsNav = findViewById(R.id.feedsNav);
        myCourses = findViewById(R.id.myCoursesNav);
        courseNav = findViewById(R.id.courseNav);
        liveCourseNav = findViewById(R.id.liveCourseNav);
        studyNav = findViewById(R.id.myStudyNav);
        whatsappChat = findViewById(R.id.start_whatsapp_chat);
        qBankNav = findViewById(R.id.qBankNav);
        testNav = findViewById(R.id.testNav);
        myQbankNav = findViewById(R.id.myQbankNav);
        myTestNav = findViewById(R.id.myTestNav);
        podcastNav = findViewById(R.id.podcastNav);
        myBookmarks = findViewById(R.id.myBookmarksNav);
        videosNav = findViewById(R.id.videosNav);
        myDownloads = findViewById(R.id.myDownloadsNav);
        myScorecard = findViewById(R.id.myScorecardNav);
        myTestSeries = findViewById(R.id.myTestSeriesNav);
        dailyQuizNav = findViewById(R.id.dailyQuizNav);
        feedbackTV = findViewById(R.id.feedbackNav);
        inviteTV = findViewById(R.id.inviteNav);
        appSettingTV = findViewById(R.id.appSettingNav);
        //logoutTV = findViewById(R.id.logoutNav);
        joinForReward = findViewById(R.id.joinForReward);
        registration = findViewById(R.id.registration);
        feedsTV = findViewById(R.id.feedTV);
        videosTV = findViewById(R.id.videosTV);
        coursesTV = findViewById(R.id.coursesTV);
        myCoursesTV = findViewById(R.id.myCoursesTV);
        liveClassesTV = findViewById(R.id.liveClassesTV);
        saveNotesTV = findViewById(R.id.savenotesTV);
        cprTV = findViewById(R.id.cprTV);
        qbankTV = findViewById(R.id.qbankTV);
        feedsIV = findViewById(R.id.feedIV);
        videosIV = findViewById(R.id.videosIV);
        coursesIV = findViewById(R.id.coursesIV);
        myCoursesIV = findViewById(R.id.myCoursesIV);
        liveClassesIV = findViewById(R.id.liveClassesIV);
        saveNotesIV = findViewById(R.id.savenotesIV);
        cprIV = findViewById(R.id.cprIV);
        qbankIV = findViewById(R.id.qbankIV);
        rateUsNav = findViewById(R.id.rateusNav);
        dayNightSwitch = findViewById(R.id.switchDayNight);
        newsAndarticleNav = findViewById(R.id.newsAndarticleNav);


    }

    private void setClickListeners() {

        ivCourseDrawer.setOnClickListener(this);
        notificationIconIV.setOnClickListener(this);
        cartIV.setOnClickListener(this);
        feedsLL.setOnClickListener(this);
        savedNotesLL.setOnClickListener(this);
        cprLL.setOnClickListener(this);
        coursesLL.setOnClickListener(this);
        myCoursesLL.setOnClickListener(this);
        liveClassesLL.setOnClickListener(this);
        videosLL.setOnClickListener(this);
        qBankLL.setOnClickListener(this);
        qbankLLCRS.setOnClickListener(this);
        bookmarkIV.setOnClickListener(this);
        medicalBtn.setOnClickListener(this);
        dentalBtn.setOnClickListener(this);
        navStreamLL.setOnClickListener(this);
        feedsNav.setOnClickListener(this);
        myCourses.setOnClickListener(this);
        myBookmarks.setOnClickListener(this);
        myScorecard.setOnClickListener(this);
        rateUsNav.setOnClickListener(this);
        appSettingTV.setOnClickListener(this);
        //logoutTV.setOnClickListener(this);
        rateUsNav.setOnClickListener(this);
        feedbackTV.setOnClickListener(this);
        myDownloads.setOnClickListener(this);
        videosNav.setOnClickListener(this);
        inviteTV.setOnClickListener(this);
        enrollNow.setOnClickListener(this);
        courseNav.setOnClickListener(this);
        liveCourseNav.setOnClickListener(this);
        studyNav.setOnClickListener(this);
        whatsappChat.setOnClickListener(this);
        qBankNav.setOnClickListener(this);
        testNav.setOnClickListener(this);
        myQbankNav.setOnClickListener(this);
        myTestNav.setOnClickListener(this);
        podcastNav.setOnClickListener(this);
        newsAndarticleNav.setOnClickListener(this);
        imageQR.setOnClickListener(this);
        dailyQuizNav.setOnClickListener(this);
        joinForReward.setOnClickListener(this);
        registration.setOnClickListener(this);
        editAffiliateProfile.setOnClickListener(this);
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
            imageQR.setVisibility(View.VISIBLE);
        } else {
            imageQR.setVisibility(View.GONE);
        }

        feedsLL.setTag(Const.SPECIALITIES);
        savedNotesLL.setTag(Const.BOOKMARKS);
        cprLL.setTag(Const.Cpr);
        videosLL.setTag(Const.VIDEOS);
        qBankLL.setTag(Constants.TestType.Q_BANK);
        qbankLLCRS.setTag(Constants.StudyType.CRS);
        bookmarkIV.setTag(Const.BOOKMARKS);
        cartIV.setTag(Const.MYCART);
        coursesLL.setTag(Const.COURSES);
        myCoursesLL.setTag(Const.MYCOURSES);
        liveClassesLL.setTag(Const.LIVE_CLASSES);

        mFragmentLayout = findViewById(R.id.fragment_container);
        navHeaderLL.setOnClickListener((View view) -> {
                    Helper.GoToProfileActivity(BaseABNavActivity.this, user.getId());
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
        );
        initViews();
        fragment = getFragment();
        postFAB.setOnClickListener((View view) -> {
            if (fragment instanceof VideoFragment) {
                ((VideoFragment) fragment).openFilterMenu();
            } else if (fragment instanceof DVLFragment) {
                ((DVLFragment) fragment).openFilterMenu();
            } else {
                Helper.GoToPostActivity(BaseABNavActivity.this, null, Const.POST_FRAG);

            }
        });
        if (fragment != null) {
            replaceErrorLayout(0, 0);
            if (fragment instanceof RecordedCoursesFragment) {
                coursesLL.setBackgroundResource(R.drawable.bg_back_dock);
//                ((TextView) coursesLL.getChildAt(1)).setTextColor(ContextCompat.getColor(this, R.color.blue));

                toolbarTitleTV.setText(getString(R.string.recorded_courses));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(Const.COURSES).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
            }
        }

        if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {
            try {
                String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                feedPref = SharedPreference.getInstance().getString(Const.FEED_PREFERENCE);
                tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tg != null && !TextUtils.isEmpty(feedPref)) {
                toolbarSubTitleTV.setText(String.format("%s / %s", tg.getText(), feedPref));
                toolbarSubTitleTV.setVisibility(View.GONE);
            } else if (tg != null) {
                toolbarSubTitleTV.setText(String.format("%s / %s", tg.getText(), getString(R.string.all)));
            }
        } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.FEED_PREFERENCE))) {
            toolbarSubTitleTV.setVisibility(View.GONE);
            toolbarSubTitleTV.setText(String.format("%s", SharedPreference.getInstance().getString(Const.FEED_PREFERENCE)));
        }
        // at the time of initialisation the
        else {
            SharedPreference.getInstance().putString(Const.FEED_PREFERENCE, getString(R.string.all));
            toolbarSubTitleTV.setVisibility(View.GONE);
            toolbarSubTitleTV.setText(getString(R.string.all));
        }

        //Init Tags even if the user is moderator
        /*if (!TextUtils.isEmpty(user.getIs_moderate()) && !user.getIs_moderate().equalsIgnoreCase("1")) {
            navButtonLL.setVisibility(View.GONE);
        } else */
        if (masterRegistrationResponse != null && !masterRegistrationResponse.getMain_category().isEmpty())
            initStreamList();
        else
            networkCallForMasterRegData();//networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);

        tagsArrayList = new ArrayList<>();
        tagsArrayList.addAll(Helper.getTagsForUser());
        getNavData();
        tryAgainBtn.setOnClickListener((View view) -> {
            replaceErrorLayout(0, 0);
            retryApiButton();
        });

        versionNameTV.setText(Html.fromHtml("<b>Version- </b>" + Helper.getVersionName(BaseABNavActivity.this)));

        // this hit will only pe called once the app is launched or the activity has been called.
        networkCallForNotificationCount();//networkCall.NetworkAPICall(API.API_GET_NOTIFICATION_COUNT, false);

        File dir = FileUtils.getAppFilesDir(this);
        deleteRecursive(dir);
    }

    public void setPostFABLayoutParams(int marginBottom) {
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        relativeParams.setMargins(0, 0, 30, marginBottom);
        postFAB.setLayoutParams(relativeParams);

        Log.e(TAG, "setPostFABLayoutParams: bottomPanelRL height - " + bottomPanelRL.getMeasuredHeight());
    }

    private void networkCallForNotificationCount() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("BASE", "networkCallForNotificationCount: ");
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getNotiCountForUser(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("BaseAbNavActivity ", "networkCallForNotificationCount onResponse: " + jsonResponse);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            int notiCount = Integer.parseInt(data.optString(Const.COUNTER).isEmpty() ? "0" : data.optString(Const.COUNTER));
                            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, notiCount);
                            if (notiCount > 0 && !(fragment instanceof FeedsFragment)) {
                                notifyTV.setVisibility(View.VISIBLE);
                                notifyTV.setText(data.optString(Const.COUNTER));
                            }
                        } else {
                            RetrofitResponse.getApiData(BaseABNavActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(BaseABNavActivity.this, t);

            }
        });
    }

    public void initSearchView() {
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24));

        searchView.setOnCloseListener(() -> {
            if (popupWindow != null)
                popupWindow.dismiss();
            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            titleRL.setVisibility(View.VISIBLE);
            if (fragment instanceof VideoFragment || fragment instanceof DVLFragment) {
                if (DVLFragment.free) {
                    setPostFABLayoutParams(bottomPanelRL.getMeasuredHeight() + 30);
                    postFAB.setVisibility(View.VISIBLE);
                } else
                    postFAB.setVisibility(View.GONE);
            }
            bottomPanelRL.setVisibility(View.VISIBLE);

            if (fragment instanceof RecordedCoursesFragment) {
                SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, "");
                if (popupWindow != null) popupWindow.dismiss();
            } else
                SharedPreference.getInstance().putString(Constants.SharedPref.SEARCHED_QUERY, "");

            refreshFragmentList(fragment, 1);
            return false;
        });

        searchView.setOnSearchClickListener((View view) -> {
            searchView.setFocusable(true);
            postFAB.setVisibility(View.GONE);
            bottomPanelRL.setVisibility(View.GONE);
            titleRL.setVisibility(View.GONE);

            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof RecordedCoursesFragment || fragment instanceof VideoFragment || fragment instanceof DVLFragment) {
                networkCallForTrendingSearch("search");
            }
        });

        View searchPlateView = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        // use this method for search process
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment instanceof RecordedCoursesFragment) {
                    SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, query);
                } else {
                    SharedPreference.getInstance().putString(Constants.SharedPref.SEARCHED_QUERY, query);
                }
                if (popupWindow != null)
                    popupWindow.dismiss();
                refreshFragmentList(fragment, 0);
                Helper.closeKeyboard(BaseABNavActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    public void refreshFragmentList(Fragment fragment, int type) {
        if (fragment instanceof RecordedCoursesFragment) {
            ((RecordedCoursesFragment) fragment).networkCallForRecordedCourse();

        } else if (fragment instanceof FeedsFragment) {
            ((FeedsFragment) fragment).isRefresh = true;
            ((FeedsFragment) fragment).lastPostId = "";
            ((FeedsFragment) fragment).firstVisibleItem = 0;
            ((FeedsFragment) fragment).previousTotalItemCount = 0;
            ((FeedsFragment) fragment).visibleItemCount = 0;
            ((FeedsFragment) fragment).refreshFeedList(true);

        } else if (fragment instanceof SavedNotesFragment) {
            ((SavedNotesFragment) fragment).isRefresh = true;
            ((SavedNotesFragment) fragment).lastPostId = "";
            ((SavedNotesFragment) fragment).firstVisibleItem = 0;
            ((SavedNotesFragment) fragment).previousTotalItemCount = 0;
            ((SavedNotesFragment) fragment).visibleItemCount = 0;
            ((SavedNotesFragment) fragment).refreshFeedList();

        } else if (/*fragment instanceof VideoFragment || */fragment instanceof DVLFragment) {
            //((DVLFragment) fragment).searchVideoList(type);
            ((DVLFragment) fragment).searchVideoList(type);
        }
    }

    public void initTagsAdapter(final ArrayList<Tags> tagsList) {

        listAdapter = new MyListAdapter(this, expandableListTitle, tagsList, cprStatus) {
            @Override
            public void onTextClick(String title, int type) {
                customNavigationClick(title);
            }
        };
        listView.setAdapter(listAdapter);
    }

    public static int getNotiIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.app_icon_small : R.mipmap.ic_launcher;
    }

    public void customNavigationClick(String title) {
        manageToolbar("");
        String fragTitle = "";
        Log.e(TAG, "CustomNavigationClick: title = " + title);
        searchView.setVisibility(View.GONE);
        postFAB.setVisibility(View.GONE);
        postFAB.setImageResource(R.mipmap.writeicon);
        bottomPanelRL.setVisibility(View.GONE);
        toolbarSubTitleTV.setVisibility(View.GONE);
        downArrowIV.setVisibility(View.GONE);

        if (!searchView.isIconified()) {
            titleRL.setVisibility(View.VISIBLE);
            if (title.equalsIgnoreCase(Const.VIDEOS)) {
                if (DVLFragment.free)
                    postFAB.setVisibility(View.VISIBLE);
                else
                    postFAB.setVisibility(View.GONE);
            }
            bottomPanelRL.setVisibility(View.VISIBLE);
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();

            // use this method when query submitted
            SharedPreference.getInstance().putString(Constants.SharedPref.SEARCHED_QUERY, "");
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, "");
        }

        if (title.equals(Const.COURSES)) { //nimesh
            setSearchVisibility(0);
        } else {
            searchText = "";
            setSearchVisibility(1);
        }

        switch (title) {
            case Const.SPECIALITIES:
                swipeRefreshLayout.setEnabled(true);
                bottomPanelRL.setVisibility(View.VISIBLE);
                setSearchVisibility(1);
                downArrowIV.setVisibility(View.GONE);
                setNotificationVisibility(false);
                fragTitle = getString(R.string.app_name);
                fragment = FeedsFragment.newInstance();
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {
                    try {
                        String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                        feedPref = SharedPreference.getInstance().getString(Const.FEED_PREFERENCE);
                        tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tg != null && !TextUtils.isEmpty(feedPref)) {
                        toolbarSubTitleTV.setText(String.format("%s / %s", tg.getText(), feedPref));
                    } else if (tg != null) {
                        toolbarSubTitleTV.setText(String.format("%s", tg.getText()));
                    }
                    toolbarSubTitleTV.setVisibility(View.GONE);
                } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.FEED_PREFERENCE))) {
                    toolbarSubTitleTV.setVisibility(View.GONE);
                    toolbarSubTitleTV.setText(String.format("%s", SharedPreference.getInstance().getString(Const.FEED_PREFERENCE)));
                }
                changeLeftPanelColor(Const.SPECIALITIES);
                break;

            case Const.BOOKMARKS:
                fragType = "";
                swipeRefreshLayout.setEnabled(false);
                searchView.setVisibility(View.GONE);
                bottomPanelRL.setVisibility(View.GONE);

                bookmarkIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                setNotificationVisibility(false);
                fragTitle = getString(R.string.bookmarks);
                fragment = SavedNotesFragment.newInstance();
                changeLeftPanelColor(Const.MY_BOOKMARKS);
                break;

            case Const.MYCART:

                Helper.goToCartScreen(BaseABNavActivity.this, null);

                /*fragType = "";
                swipeRefreshLayout.setEnabled(false);
                searchView.setVisibility(View.GONE);
                bottomPanelRL.setVisibility(View.GONE);

                bookmarkIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                setNotificationVisibility(false);
                fragTitle = Const.MYCART;
                fragment = MyCartFragment.newInstance(Const.MYCART);
                changeLeftPanelColor(Const.MYCART);*/
                break;

            case Constants.TestType.Q_BANK:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);
                setSearchVisibility(2);
                setNotificationVisibility(true);

                fragTitle = getString(R.string.study);
                fragment = StudyFragment.newInstance();
                changeLeftPanelColor(Constants.TestType.Q_BANK);
                changeTabColor(Constants.TestType.Q_BANK);
                break;

            case Const.LIVE_CLASSES:
                fragType = Const.LIVE_CLASSES;
                bottomPanelRL.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(2);
                setNotificationVisibility(false);


                fragTitle = getString(R.string.live_courses);
                fragment = LiveCoursesFragment.newInstance(getString(R.string.live_course));
                changeLeftPanelColor(Const.LIVE_CLASSES);
                changeTabColor(Const.LIVE_CLASSES);
                break;

            case Const.Cpr:
                swipeRefreshLayout.setEnabled(false);
                fragTitle = getString(R.string.cpr);
                fragment = UpToDateFragment.newInstance();
                break;

            case Const.COURSES:
            case Const.ALLCOURSES:
                fragType = Const.COURSES;
                setSearchVisibility(0);
                setNotificationVisibility(true);
                swipeRefreshLayout.setEnabled(true);
                bottomPanelRL.setVisibility(View.VISIBLE);
                fragTitle = getString(R.string.recorded_courses);

                fragment = RecordedCoursesFragment.newInstance(getString(R.string.recorded_courses));
                changeLeftPanelColor(Const.COURSES);
                changeTabColor(Const.COURSES);
                break;

            case Const.MY_BOOKMARKS:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);
                setSearchVisibility(1);
                fragTitle = getString(R.string.my_bookmarks);
                fragment = BookmarkFrag.getInstance(Const.ALLCOURSES);
                changeLeftPanelColor(Const.MY_BOOKMARKS);
                break;

            case Const.LEADERBOARD:
                swipeRefreshLayout.setEnabled(false);
                searchView.setVisibility(View.GONE);

                Intent intent = new Intent(BaseABNavActivity.this, CourseActivity.class); // AllCourse List
                intent.putExtra(Const.FRAG_TYPE, Const.LEADERBOARD);
                startActivity(intent);
                break;

            case Const.REWARDPOINTS:
                swipeRefreshLayout.setEnabled(false);
                fragTitle = getString(R.string.invite_n_earn);
                break;

            case Const.MYCOURSES:
                swipeRefreshLayout.setEnabled(false);
                fragTitle = Const.MYCOURSES;
                fragType = Const.MYCOURSES;

                bottomPanelRL.setVisibility(View.VISIBLE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);
                setNotificationVisibility(true);

                fragment = MyCourseTabFragment.newInstance(Const.MYCOURSES);
                changeLeftPanelColor(Const.MYCOURSES);
                break;

            case Const.FEEDBACK:
                swipeRefreshLayout.setEnabled(false);
                fragTitle = getString(R.string.feedback);
                bottomPanelRL.setVisibility(View.GONE);
                //fragment = HelpSupportFragment.newInstance();

                break;
            case Const.VIDEOS:
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(1);
                postFAB.setVisibility(View.VISIBLE);
                postFAB.setImageResource(R.mipmap.filter);
                setNotificationVisibility(true);
                bookmarkIV.setVisibility(View.VISIBLE);
                fragTitle = getString(R.string.video);
                bottomPanelRL.setVisibility(View.VISIBLE);

                fragment = DVLFragment.newInstance();
                videoTableArrayList = new ArrayList<>();
                changeLeftPanelColor(Const.VIDEOS);
                changeTabColor(Const.VIDEOS);
                break;
            case Const.RATEUS:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);
                Helper.goToPlayStore(this);

                break;
            case Const.APPSETTING:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);
                fragTitle = getString(R.string.appSettings);
                break;
            case Const.TERMS:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);
                if (!isFinishing())
                    Helper.GoToWebViewActivity(this, Const.TERMS_URL);
                break;

            case Const.PRIVACY:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);
                Helper.GoToWebViewActivity(this, Const.PRIVACY_URL);
                break;

            case Const.LOGOUT:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);
                setSearchVisibility(1);
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {

                    SharedPreference.getInstance().putString(Const.SUBTITLE, "");
                    toolbarSubTitleTV.setVisibility(View.GONE);
                }
                getLogoutDialog(getString(R.string.logout), getString(R.string.logout_confirmation_message));
                break;
        }
        if (!GenericUtils.isEmpty(fragTitle)) {
            changeTabColor(title);

            Log.d("response", "title:" + title);
            toolbarTitleTV.setText(fragTitle);
        }

        if (fragment != null && !isFinishing()) {
            replaceErrorLayout(0, 0);
            if (fragment instanceof RecordedCoursesFragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(Const.COURSES).commit();
                downArrowIV.setVisibility(View.GONE);
            } else {
                downArrowIV.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
            }
        }
        if (!isFinishing() && drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START, false);

        if (fragment != null && fragment instanceof FeedsFragment) {
            swipeRefreshLayout.setEnabled(true);
        }
    }

    public void showNotification(String pushMessage, String pushTitle, Intent intent) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.medicos_icon);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(pushTitle);
        bigPictureStyle.setSummaryText(Html.fromHtml(pushMessage).toString());

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(getNotiIcon())
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 120, 120, false))
                .setContentTitle(pushTitle)
                .setContentText(pushMessage)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(pushMessage))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{500, 500, 500, 500, 500})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);
        Random random = new Random();
        int notificationId = random.nextInt(10000);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        }

        int color = 0xffffff;
        notificationBuilder.setColor(color);
        ShortcutBadger.applyCount(getApplicationContext(), SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT));
        notificationManager.notify(notificationId/* ID of notification */, notificationBuilder.build());
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        callOnResume();
        cartTV.setText(String.valueOf(SharedPreference.getInstance().getInt(Const.CART_COUNT)));
    }

    public void callOnResume() {

        manageOptionsVisibility(SharedPreference.getInstance().getMasterHitResponse());
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.e(TAG, "onResume: fragType = " + fragType);
        if (fragment instanceof FeedsFragment && dailyQuizClicked) {
            if (CheckConnection.isConnection(this)) {
                ((FeedsFragment) fragment).lastPostId = "";
                ((FeedsFragment) fragment).firstVisibleItem = 0;
                ((FeedsFragment) fragment).previousTotalItemCount = 0;
                ((FeedsFragment) fragment).visibleItemCount = 0;
                ((FeedsFragment) fragment).refreshFeedList(false);
                dailyQuizClicked = false;
            }
        } else if (fragment instanceof FeedsFragment || fragment instanceof SavedNotesFragment || fragment instanceof RecordedCoursesFragment || fragment instanceof MyScorecardFragment || fragment instanceof MyCoursesFragment) {
            if (fragType.equals(Const.MYCOURSES) || fragType.equals(Const.MYQBANK) || fragType.equals(getString(R.string.bookmarks)) || fragType.equals(Const.LEADERBOARD)
                    || fragType.equals(Const.MYTEST)) {
                setNotificationVisibility(false);
            } else setNotificationVisibility(fragType.equals(Const.COURSES));
        } else setNotificationVisibility(!(fragment instanceof LiveCoursesFragment));

        if (fragment instanceof DVLFragment) {
            setCartVisibility(true);
            setNotificationVisibility(true);
            bottomPanelRL.setVisibility(View.VISIBLE);
            setSearchVisibility(1);
        }

        user = SharedPreference.getInstance().getLoggedInUser();
        user.setName(Helper.CapitalizeText(user.getName()));
        networkCallForAppVersion();
        if (!TextUtils.isEmpty(user.getProfile_picture())) {
            profileImage.setVisibility(View.VISIBLE);
            profileImageText.setVisibility(View.GONE);

            Glide.with(this).load(user.getProfile_picture()).into(profileImage);
        } else {
            Drawable dr = Helper.GetDrawable(user.getName(), BaseABNavActivity.this, user.getId());
            if (dr != null) {
                profileImage.setVisibility(View.GONE);
                profileImageText.setVisibility(View.VISIBLE);
                profileImageText.setImageDrawable(dr);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);
                profileImage.setImageResource(R.mipmap.default_pic);
            }
        }

        if (SharedPreference.getInstance().getBoolean(Const.IS_PROFILE_CHANGED)) {
            tagsArrayList.addAll(Helper.getTagsForUser());
            getNavData();
        }
        if (SharedPreference.getInstance().getBoolean(Const.IS_STREAM_CHANGE)) {

//            coursesLL.performClick();
            networkCallForMasterHit();//networkCall.NetworkAPICall(API.API_GET_MASTER_HIT, false);
        }

        profileName.setText(user.getName());

        if (!TextUtils.isEmpty(user.getDams_tokken()))
            damsIdTV.setVisibility(View.VISIBLE);
        else
            damsIdTV.setVisibility(View.GONE);

        damsIdTV.setText(HtmlCompat.fromHtml("<b>Dams Id:</b> " + (TextUtils.isEmpty(user.getDams_tokken()) ? "" : user.getDams_tokken()), HtmlCompat.FROM_HTML_MODE_LEGACY));

        // todo to handle bottom panel in case of ALL courses
        if (fragment instanceof RecordedCoursesFragment) {
            bottomPanelRL.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE); //nimesh
        }
    }

    @Override
    public void onBackPressed() {
        onCustomBackPress();
    }

    protected abstract Fragment getFragment();

    protected abstract void initViews();

    public void onCustomBackPress() {

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.e(TAG, "onCustomBackPress: " + Objects.requireNonNull(fragment).getClass().getSimpleName());
        if (!searchView.isIconified()) {
            titleRL.setVisibility(View.VISIBLE);
            if (fragment instanceof VideoFragment || fragment instanceof DVLFragment) {
                if (DVLFragment.free)
                    postFAB.setVisibility(View.VISIBLE);
                else
                    postFAB.setVisibility(View.GONE);
            }
            bottomPanelRL.setVisibility(View.VISIBLE);
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            if (popupWindow != null) popupWindow.dismiss();

            // use this method when query submitted
            SharedPreference.getInstance().putString(Constants.SharedPref.SEARCHED_QUERY, "");
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, "");
            refreshFragmentList(fragment, 1);

        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment instanceof SavedNotesFragment ||
                //fragment instanceof HelpSupportFragment ||
                fragment instanceof DownloadFragment ||
                fragment instanceof DailyQuizFragment ||
                fragment instanceof MyScorecardFragment ||
                fragment instanceof ReferralSignUp ||
                fragment instanceof DailyChallengeDashboard ||
                fragment instanceof PodcastNewKotlin ||
                fragment instanceof ReferEarnNowFragment ||
                fragment instanceof MyCoursesFragment ||
                fragment instanceof MyCartFragment ||
                fragment instanceof MyCourseTabFragment ||
                fragment instanceof QRCodeFragment) {
            toolbarTitleTV.setText(getString(R.string.app_name));
            toolbar.setVisibility(View.VISIBLE);
            changeTabColor(Const.COURSES);
            customNavigationClick(Const.COURSES);

        } else if (fragment instanceof FeedsFragment || fragment instanceof VideoFragment ||
                fragment instanceof DVLFragment || fragment instanceof RecordedCoursesFragment
                || fragment instanceof LiveCoursesFragment
                || fragment instanceof LCPracticeChildFragment
                || fragment instanceof StudyFragment) {
            if (backPressed + 3000 > System.currentTimeMillis()) {
                finishAffinity();
//                if (SplashScreen.intent != null)
//                    stopService(SplashScreen.intent);
            } else {
                backPressed = System.currentTimeMillis();
                Helper.showSnackBar(drawer, getString(R.string.press_again_to_exit));
            }
        } else if (fragment instanceof UpToDateFragment) {
            if (((UpToDateFragment) fragment).webView.canGoBack()) {
                ((UpToDateFragment) fragment).webView.goBack();
            } else {
                toolbarTitleTV.setText(getString(R.string.app_name));
                changeTabColor(Const.COURSES);
                customNavigationClick(Const.COURSES);
            }
        } else
            this.finish();
    }

    @Override
    public void onClick(View v) {
        String fragtitle = "";
        switch (v.getId()) {
            case R.id.imageIV:
                Intent intent = new Intent(BaseABNavActivity.this, PostActivity.class);// notification fragment
                intent.putExtra(Const.FRAG_TYPE, Const.NOTIFICATION);
                startActivity(intent);
                break;

            case R.id.joinForReward:
                changeLeftPanelColor(Const.AFFILIATE);
                String affiliateId = SharedPreference.getInstance().getLoggedInUser().getAffiliate_user_id();
                if (affiliateId == null || affiliateId.isEmpty()) {
                    fragtitle = getString(R.string.referral_signup);
                    fragment = ReferralSignUp.newInstance();
                } else {
                    fragtitle = getString(R.string.refer_earn_now);
                    editAffiliateProfile.setVisibility(View.VISIBLE);
                    fragment = ReferEarnNowFragment.newInstance();
                }
                break;
            case R.id.registration:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = getResources().getString(R.string.choose_your_preference);
                bottomPanelRL.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                fragment = ChooseRegistrationPreference.newInstance();
                break;
            case R.id.editAffiliateProfile:

                swipeRefreshLayout.setEnabled(false);
                fragtitle = getString(R.string.update_affiliate);
                bottomPanelRL.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                fragment = ReferralSignUp.newInstance();
                break;

            case R.id.cprLL:
            case R.id.feedsLL:
                swipeRefreshLayout.setEnabled(true);
                changeLeftPanelColor(Const.SPECIALITIES);
            case R.id.savenotesLL:
                swipeRefreshLayout.setEnabled(false);
                changeLeftPanelColor(Const.MY_BOOKMARKS);
            case R.id.videosLL:
                swipeRefreshLayout.setEnabled(false);
                changeLeftPanelColor(Const.VIDEOS);
                setNotificationVisibility(true);
                changeTabColor(Const.VIDEOS);
            case R.id.coursesLL:
                fragType = Const.COURSES;
                swipeRefreshLayout.setEnabled(true);
                changeLeftPanelColor(Const.ALLCOURSES);
            case R.id.bookmarkIV:
                swipeRefreshLayout.setEnabled(true);
                changeLeftPanelColor(Const.MY_BOOKMARKS);
            case R.id.cartIV:
                swipeRefreshLayout.setEnabled(true);
                changeLeftPanelColor(Const.MYCART);
            case R.id.qbankLL:
                fragType = Constants.TestType.Q_BANK;
                swipeRefreshLayout.setEnabled(false);
                //                customNavigationClick((String) v.getTag());
            case R.id.liveClassesLL:
                fragType = Const.LIVE_CLASSES;
                bottomPanelRL.setVisibility(View.VISIBLE);

                swipeRefreshLayout.setEnabled(true);
                customNavigationClick((String) v.getTag());
                setSearchVisibility(0);
                break;
            case R.id.myCoursesLL:
                fragType = Const.MYCOURSES;
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);

                changeLeftPanelColor(Const.MYCOURSES);
                changeTabColor(Const.MYCOURSES);
//                fragment = LCPracticeChildFragment.newInstance();
                break;
            case R.id.navStreamLL:
                swipeRefreshLayout.setEnabled(true);
                if (masterRegistrationResponse != null &&
                        masterRegistrationResponse.getMain_category() != null &&
                        !masterRegistrationResponse.getMain_category().isEmpty()) {
                    showPopMenuForStream(navStreamLL);
                } else {
                    isMenuClicked = true;
                    networkCallForMasterRegData();//networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                }
                break;

            case R.id.feedsNav:
                swipeRefreshLayout.setEnabled(true);
                setNotificationVisibility(false);
                toolbarTitleTV.setText(getString(R.string.app_name));
                bottomPanelRL.setVisibility(View.VISIBLE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                setSearchVisibility(1);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragment = FeedsFragment.newInstance();
                changeTabColor(Const.SPECIALITIES);
                changeLeftPanelColor(Const.SPECIALITIES);
                break;

            case R.id.myCoursesNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.MYCOURSES;
                fragType = Const.MYCOURSES;
                bottomPanelRL.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                setSearchVisibility(0);
                setNotificationVisibility(true);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);

                fragment = MyCourseTabFragment.newInstance(Const.MYCOURSES);
                //fragment = MyCoursesFragment.newInstance(Const.MYCOURSES);
                changeLeftPanelColor(Const.MYCOURSES);
                changeTabColor(Const.MYCOURSES);
                break;

            case R.id.qbankLLCRS:
                fragType = Constants.StudyType.CRS;
                titleRL.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(2);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                toolbarTitleTV.setText(getString(R.string.study));
                toolbarSubTitleTV.setVisibility(View.GONE);
                setNotificationVisibility(true);
                fragment = StudyFragment.newInstance(Constants.StudyType.CRS);

                changeLeftPanelColor(Constants.TestType.Q_BANK);
                changeTabColor(Constants.TestType.Q_BANK);

                break;

            case R.id.myQbankNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.MYQBANK;
                fragType = Const.MYQBANK;
                setSearchVisibility(0);
                setNotificationVisibility(false);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragment = MyCoursesFragment.newInstance(Const.MYQBANK);
                changeLeftPanelColor(Const.MYQBANK);
                break;

            case R.id.myTestNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.MYTEST;
                fragType = Const.MYTEST;
                setSearchVisibility(0);
                setNotificationVisibility(false);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragment = MyCoursesFragment.newInstance(Const.MYTEST);
                changeLeftPanelColor(Const.MYTEST);
                break;

            case R.id.start_whatsapp_chat:

                initHelpAndSupport();
//                /commentedweds`   z
//                String number = BuildConfig.WHATSAPP_NO;

//                String url = "https://api.whatsapp.com/send?phone=" + number;
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);

//                commented
//                try {
//                    Intent sendIntent = new Intent("android.intent.action.MAIN");
//                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
//                    startActivity(sendIntent);
//
//                } catch (Exception e) {
//                    if (e instanceof ActivityNotFoundException) {
//                        final String appPackageName = "com.whatsapp";
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                        }
//                    }
//                    Log.e(TAG, "ERROR_OPEN_MESSANGER" + e.toString());
//                }
                break;

            case R.id.podcastNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.PODCAST;
                fragType = Const.PODCAST;
                setSearchVisibility(1);
                setNotificationVisibility(true);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragment = PodcastNewKotlin.newInstance();
                changeLeftPanelColor(Const.PODCAST);
                break;

            case R.id.newsAndarticleNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.NEWSARTICLE;
                fragType = Const.NEWSARTICLE;
                setSearchVisibility(1);
                setNotificationVisibility(true);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);
                changeLeftPanelColor(Const.NEWSARTICLE);
                break;

            case R.id.myBookmarksNav:
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(0);
                setNotificationVisibility(false);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);

                fragtitle = getString(R.string.bookmarks);
                fragType = getString(R.string.bookmarks);

                fragment = SavedNotesFragment.newInstance();
                changeTabColor(Const.BOOKMARKS);
                changeLeftPanelColor(Const.MY_BOOKMARKS);
                break;
            case R.id.imageQR:
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                fragtitle = getString(R.string.qr_code);
                fragment = QRCodeFragment.newInstance();
                break;
            case R.id.dailyQuizNav:
                fragtitle = Const.DAILY_QUIZ;
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                ivCourseDrawer.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                setSearchVisibility(0);
                setNotificationVisibility(false);

//                fragment = DailyQuizFragment.newInstance();
                PostFile postFile = eMedicozApp.getInstance().postFile;
               /* if (postFile == null) {
                    Toast.makeText(this, R.string.wait_till_data_loads, Toast.LENGTH_SHORT).show();
                    return;
                }*/
                fragment = DailyChallengeDashboard.newInstance(postFile);
                changeLeftPanelColor(Const.DAILY_QUIZ);
                break;
            case R.id.myScorecardNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.LEADERBOARD;
                fragType = Const.LEADERBOARD;
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                setSearchVisibility(0);
                setNotificationVisibility(false);

                fragment = MyScorecardFragment.newInstance();
                changeLeftPanelColor(Const.LEADERBOARD);
                break;
            case R.id.feedbackNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = getString(R.string.feedback);
                setSearchVisibility(0);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                //fragment = HelpSupportFragment.newInstance();
                changeLeftPanelColor(Const.FEEDBACK);
                break;
            case R.id.myDownloadsNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.MYDOWNLOAD;
                bottomPanelRL.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragment = DownloadFragment.newInstance();
                changeLeftPanelColor(Const.MYDOWNLOAD);
                break;
            case R.id.myTestSeriesNav:
                break;
            case R.id.inviteNav:
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(0);
                fragtitle = getString(R.string.invite_n_earn);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);
                changeLeftPanelColor(Const.INVITEDBY);
                break;
            case R.id.appSettingNav:
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(0);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragtitle = getString(R.string.appSettings);
                changeLeftPanelColor(Const.APPSETTING);
                break;
//            case R.id.logoutNav:
//                swipeRefreshLayout.setEnabled(false);
//                bottomPanelRL.setVisibility(View.VISIBLE);
//                setSearchVisibility(1);
//                if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {
//
//                    SharedPreference.getInstance().putString(Const.SUBTITLE, "");
//                    toolbarSubTitleTV.setVisibility(View.GONE);
//                }
//                logout(getString(R.string.logout), getString(R.string.logout_confirmation_message));
//                break;
            case R.id.videosNav:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = getString(R.string.video);
                bottomPanelRL.setVisibility(View.VISIBLE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                setSearchVisibility(1);
                setNotificationVisibility(true);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                fragment = DVLFragment.newInstance();
                changeTabColor(Const.VIDEOS);
                changeLeftPanelColor(Const.VIDEOS);
                break;

            case R.id.enrollNow:
                swipeRefreshLayout.setEnabled(false);
                fragtitle = Const.ALLCOURSES;
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                setSearchVisibility(0);
                downArrowIV.setVisibility(View.GONE);

                fragment = RecordedCoursesFragment.newInstance(getString(R.string.recorded_courses));
                break;

            case R.id.courseNav:
                fragType = Const.COURSES;
                titleRL.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(true);
                bottomPanelRL.setVisibility(View.VISIBLE);
                setSearchVisibility(0);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                toolbarTitleTV.setText(getString(R.string.recorded_courses));
                toolbarSubTitleTV.setVisibility(View.GONE);
                setNotificationVisibility(true);
                fragment = RecordedCoursesFragment.newInstance(getString(R.string.recorded_courses));

                changeLeftPanelColor(Const.ALLCOURSES);
                changeTabColor(Const.ALLCOURSES);
                break;


            case R.id.myStudyNav:

                fragType = Constants.TestType.Q_BANK;
                titleRL.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(2);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                toolbarTitleTV.setText(getString(R.string.study));
                toolbarSubTitleTV.setVisibility(View.GONE);
                setNotificationVisibility(true);
                fragment = StudyFragment.newInstance();

                changeLeftPanelColor(Constants.TestType.Q_BANK);
                changeTabColor(Constants.TestType.Q_BANK);
                break;

            case R.id.liveCourseNav:
                fragType = Const.LIVE_CLASSES;
                titleRL.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
                setSearchVisibility(0);

                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                toolbarTitleTV.setText(getString(R.string.live_courses));
                toolbarSubTitleTV.setVisibility(View.GONE);
                setNotificationVisibility(false);
                searchView.setVisibility(View.GONE);
                fragment = LiveCoursesFragment.newInstance("Live Courses");

                changeLeftPanelColor(Const.LIVE_CLASSES);
                changeTabColor(Const.LIVE_CLASSES);
                break;

            case R.id.qBankNav:
                swipeRefreshLayout.setEnabled(false);
                changeLeftPanelColor(Const.QBANK);
                Intent courseList = new Intent(this, CourseActivity.class);//FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
                courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE);
                courseList.putExtra(Const.CATEGORY_ID, Constants.Extras.QUESTION_BANK_CAT_ID);
                courseList.putExtra(Const.TITLE, "Question Bank");
                startActivity(courseList);
                break;

            case R.id.testNav:
                swipeRefreshLayout.setEnabled(false);
                changeLeftPanelColor(Constants.TestType.TEST);
                Intent courseList2 = new Intent(this, CourseActivity.class);//FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
                courseList2.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE);
                courseList2.putExtra(Const.CATEGORY_ID, Constants.Extras.TEST_SERIES_CAT_ID);
                courseList2.putExtra(Const.TITLE, "Test Series");
                startActivity(courseList2);
                break;
            case R.id.rateusNav:
                rateUsDialog();
//                intent = new Intent(this, FeedsActivity.class);
//                showNotification("Testing notification", getString(R.string.app_name), intent);
                break;
            case R.id.iv_course_drawer:
                courseSidePanel();
//                startActivity(new Intent(this, ViewDetail.class));
                break;
            default:
        }

        if (!fragtitle.equals("")) {
            toolbarTitleTV.setText(fragtitle);
        }

        try {
            if (fragment != null && !isFinishing()) {
                replaceErrorLayout(0, 0);
                if (fragment instanceof RecordedCoursesFragment) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(Const.COURSES).commit();
                    downArrowIV.setVisibility(View.GONE);
                    toolbarSubTitleTV.setVisibility(View.GONE);
                } else {
                    downArrowIV.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (v.getId() != R.id.navStreamLL) {
            if (drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START);
        }

        if (fragment instanceof FeedsFragment) {
            swipeRefreshLayout.setEnabled(true);
        }

        eMedicozApp.getInstance().filterType = "";
        eMedicozApp.getInstance().searchedKeyword = "";
    }

    public void manageToolbar(String screen) {
        switch (screen) {
            case Constants.ScreenName.DAILY_CHALLENGE:
                setSearchVisibility(0);
                setCartVisibility(false);
                setNotificationVisibility(false);
                swipeRefreshLayout.setEnabled(false);
                searchView.setVisibility(View.GONE);

                bookmarkIV.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                break;

            case Constants.ScreenName.FAN_WALL:
                swipeRefreshLayout.setEnabled(false);
                setNotificationVisibility(false);
                setCartVisibility(false);
                searchView.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                break;

            case Constants.ScreenName.STUDY:
                setSearchVisibility(0);
                setCartVisibility(false);
                setNotificationVisibility(false);
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.VISIBLE);

                toolbarTitleTV.setText(getString(R.string.study));
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

                changeTabColor(Constants.TestType.Q_BANK);
                break;

            case Constants.ScreenName.COURSES:
                setSearchVisibility(0);
                setCartVisibility(true);
                setNotificationVisibility(true);
                swipeRefreshLayout.setEnabled(true);
                bottomPanelRL.setVisibility(View.VISIBLE);
                break;

            case Const.MYSCORECARD:
            case Const.MYCOURSES:
                setSearchVisibility(0);
                setCartVisibility(false);
                setNotificationVisibility(false);
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);
                break;

            case Const.BOOKMARKS:
                setSearchVisibility(0);
                setCartVisibility(false);
                setNotificationVisibility(false);
                swipeRefreshLayout.setEnabled(true);
                bottomPanelRL.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);
                break;

            case Constants.ScreenName.AFFILIATE:
                setSearchVisibility(0);
                setCartVisibility(false);
                setNotificationVisibility(false);
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);

                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);
                break;

            case Constants.ScreenName.APP_SETTING:
                setSearchVisibility(0);
                setCartVisibility(false);
                setNotificationVisibility(true);
                swipeRefreshLayout.setEnabled(false);
                bottomPanelRL.setVisibility(View.GONE);

                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);
                break;

            default:
                setCartVisibility(false);
                bottomPanelRL.setVisibility(View.GONE);
                toolbarSubTitleTV.setVisibility(View.GONE);
                downArrowIV.setVisibility(View.GONE);
                editAffiliateProfile.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.VISIBLE);

        }
        toolbar.setBackgroundResource(R.drawable.bg_action_bar);
    }

    private void rateUsDialog() {
        Dialog dialog;
        View view;
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.dialog_rate_us_layout, null, false);
        dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
        dialog.show();
        dialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        rating = 0;
        ((RatingBar) view.findViewById(R.id.ratingRB)).setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            BaseABNavActivity.this.rating = rating;
            if (rating < 4) {
                Log.e(TAG, "onRatingChanged: " + rating);
            } else if (rating >= 4) {
                Log.e(TAG, "onRatingChanged: " + rating);
            }
        });

        //view.findViewById(R.id.tv_not_now).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.tv_submit).setOnClickListener(v -> {
            if (rating == 0) {
                GenericUtils.showToast(BaseABNavActivity.this, getString(R.string.rate_before_submit));
            } else if (rating < 4 && rating > 0) {
                dialog.dismiss();
                // rating send to our server
            } else if (rating >= 4) {
                dialog.dismiss();
                Helper.goToPlayStore(BaseABNavActivity.this);
            }
        });
    }

    private void setSearchVisibility(int n) {
        if (n == 0) {
            searchView.setVisibility(View.GONE);
            ivCourseDrawer.setVisibility(View.GONE);
        } else if (n == 1) {
            searchView.setVisibility(View.VISIBLE);
            ivCourseDrawer.setVisibility(View.GONE);
        } else if (n == 2) {
            searchView.setVisibility(View.GONE);
            ivCourseDrawer.setVisibility(View.VISIBLE);
        }
    }

    public void courseSidePanel() {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = li.inflate(R.layout.dialog_course_drawer_layout, null, false);
        final Dialog dialog = new Dialog(this, R.style.drawerDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) getResources().getDimension(R.dimen.dp350), LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.END);
        dialog.show();

        flow_layout_top_searches_courses = view.findViewById(R.id.flow_layout_top_searches_courses);
        etSearch = view.findViewById(R.id.et_search);
        ivClearSearch = view.findViewById(R.id.iv_clear_search);
        ivIconSearch = view.findViewById(R.id.iv_icon_search);
        tvAllCategory = view.findViewById(R.id.tv_all_category);
        rvCourseCategory = view.findViewById(R.id.recycler_view_course_category);
        tvError = view.findViewById(R.id.tv_error);
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        cross = view.findViewById(R.id.cross);

        if (!GenericUtils.isListEmpty(trendingArrayList)) {
            createTopSearchedCourse();
        } else {
            networkCallForTrendingSearch(Const.DRAWER);
        }

        etSearch.setHint(R.string.search_course_hint);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (!TextUtils.isEmpty(searchText)) {
            ivClearSearch.setVisibility(View.VISIBLE);
            etSearch.setText(searchText);
            etSearch.setSelection(Helper.GetText(etSearch).length());
            ivIconSearch.setVisibility(View.GONE);
        } else {
            ivIconSearch.setVisibility(View.VISIBLE);
            ivClearSearch.setVisibility(View.GONE);
            etSearch.setText("");
        }
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ivClearSearch.setVisibility(View.VISIBLE);
                    ivIconSearch.setVisibility(View.GONE);
                } else {
                    ivClearSearch.setVisibility(View.GONE);
                    ivIconSearch.setVisibility(View.VISIBLE);
                }
//                        filter(s.toString());
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(Helper.GetText(etSearch))) {
                    Toast.makeText(BaseABNavActivity.this, R.string.enter_search_query, Toast.LENGTH_SHORT).show();
                } else {
                    searchData(Helper.GetText(etSearch));
                }
                return true;
            }
            return false;
        });

        if (fragment instanceof RecordedCoursesFragment)
            etSearch.setText(((RecordedCoursesFragment) fragment).getSearchedKeyword());

        ivClearSearch.setOnClickListener(view1 -> {
            searchText = "";
            etSearch.setText("");
            if (currentFragment instanceof RecordedCoursesFragment) {
                ((RecordedCoursesFragment) currentFragment).setSearchedKeyword("");
            }
            refreshFragmentList(currentFragment, 1);
            ivIconSearch.setVisibility(View.VISIBLE);
        });

        rvCourseCategory.setLayoutManager(new LinearLayoutManager(BaseABNavActivity.this));
        if (currentFragment instanceof RecordedCoursesFragment) {
            /*if (((RecordedCoursesFragment) currentFragment).isSearching) {
                if (!((RecordedCoursesFragment) currentFragment).viewModel.getSearchDataArrayList$app_debug().isEmpty())
                    setSearchCourseData(((RecordedCoursesFragment) currentFragment).viewModel.getSearchDataArrayList$app_debug());
                else
                    setErrorMessage(((RecordedCoursesFragment) currentFragment).errorMessage);
            } else*/
            setSidePanelCourse(((RecordedCoursesFragment) currentFragment).viewModel.getOrignalDataArrayList());
        }
        rvCourseCategory.addItemDecoration(new EqualSpacingItemDecoration(15, EqualSpacingItemDecoration.VERTICAL));
    }

    public void setSidePanelCourse(ArrayList<CoursesData> coursesDataArrayList) {
        if (rvCourseCategory != null) {
            if (!GenericUtils.isListEmpty(coursesDataArrayList)) {
                tvAllCategory.setVisibility(View.VISIBLE);
                drawerCourseCategoryAdapter = new DrawerCourseCategoryAdapter(coursesDataArrayList, this);
                rvCourseCategory.setAdapter(drawerCourseCategoryAdapter);
            } else {
                tvError.setVisibility(View.VISIBLE);
                rvCourseCategory.setVisibility(View.GONE);
            }
        }
    }

    public void setSearchCourseData(ArrayList<Course> searchArrayList) {
        if (rvCourseCategory != null) {
            if (rvCourseCategory.getVisibility() == View.GONE)
                rvCourseCategory.setVisibility(View.VISIBLE);
            tvAllCategory.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
            courseSearchAdapter = new CourseSearchAdapter(this, searchArrayList);
            rvCourseCategory.setAdapter(courseSearchAdapter);
        }
    }

    // call this method when course data not found
    public void setErrorMessage(String errorMessage) {
        if (rvCourseCategory != null) {
            rvCourseCategory.setVisibility(View.GONE);
        }
        if (tvError != null) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(errorMessage);
        }
    }

    private void filter(String text) {
        newCoursesDataArrayList.clear();
        for (CoursesData coursesData : ((CommonFragForList) currentFragment).coursesDataArrayList) {
            if (coursesData.getCategory_info().getName().toLowerCase().contains(text.toLowerCase())) {
                newCoursesDataArrayList.add(coursesData);
            }
        }

        if (!newCoursesDataArrayList.isEmpty()) {
            tvError.setVisibility(View.GONE);
            rvCourseCategory.setVisibility(View.VISIBLE);
            drawerCourseCategoryAdapter.filterList(newCoursesDataArrayList);
        } else {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.no_match_found));
            rvCourseCategory.setVisibility(View.GONE);
        }
    }

    public void setNotificationVisibility(boolean show) {
        if (show) {
            if (SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) > 0) {
                notifyTV.setVisibility(View.VISIBLE);
                notifyTV.setText(String.valueOf(SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT)));
            } else {
                notifyTV.setVisibility(View.GONE);
            }
            notificationIconIV.setVisibility(View.VISIBLE);
            notifyLayout.setVisibility(View.VISIBLE);
        } else {
            notifyTV.setVisibility(View.GONE);
            notificationIconIV.setVisibility(View.GONE);
            notifyLayout.setVisibility(View.GONE);
        }
    }

    public void setCartVisibility(boolean show) {
        if (show) {
            if (SharedPreference.getInstance().getInt(Const.CART_COUNT) > 0) {
                cartTV.setVisibility(View.VISIBLE);
                cartTV.setText(String.valueOf(SharedPreference.getInstance().getInt(Const.CART_COUNT)));
            } else {
                cartTV.setVisibility(View.GONE);
            }
            cartIV.setVisibility(View.VISIBLE);
            cartIVLayout.setVisibility(View.VISIBLE);
        } else {
            cartTV.setVisibility(View.GONE);
            cartIV.setVisibility(View.GONE);
            cartIVLayout.setVisibility(View.GONE);
        }
    }

    private void createTopSearchedCourse() {
        flow_layout_top_searches_courses.removeAllViews();
        for (Review review : trendingArrayList) {
            TextView text = new TextView(BaseABNavActivity.this);
            text.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_capsule_fill_white_selector));
            text.setTextColor(ContextCompat.getColor(this, R.color.black));
            text.setPadding(30, 7, 30, 7);
            text.setTextSize(12f);
            text.setText(review.getText());
            text.setTag(review.getText());
            text.setOnClickListener((View view) -> {
                searchText = (String) view.getTag();
                etSearch.setText((String) view.getTag());
                etSearch.setSelection(Helper.GetText(etSearch).length());
                if (currentFragment instanceof CommonFragForList) {
                    ((CommonFragForList) currentFragment).etSearch.setText((String) view.getTag());
                    ((CommonFragForList) currentFragment).etSearch.setSelection(Helper.GetText(etSearch).length());
                }
                searchData((String) view.getTag());
            });
            flow_layout_top_searches_courses.addView(text);
        }
    }

    public void searchData(String tag) {
        searchText = tag;
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, tag);
        if (popupWindow != null)
            popupWindow.dismiss();
        ((RecordedCoursesFragment) fragment).setSearchedKeyword(SharedPreference.getInstance().getString(Constants.SharedPref.COURSE_SEARCHED_QUERY));
        refreshFragmentList(fragment, 0);
        Helper.closeKeyboard(BaseABNavActivity.this);
    }

    public void showPopMenuForStream(final View v) {

        View popUpView = getLayoutInflater().inflate(R.layout.dialog_indexing, null); // inflating popup layout
        streamPopUp = new PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true); // Creation of popup
        streamPopUp.setAnimationStyle(android.R.style.Animation_Dialog);

        View container = getWindow().getDecorView();
        Context context = streamPopUp.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);

        RecyclerView indexingRV = popUpView.findViewById(R.id.indexingRv);
        indexingRV.setLayoutManager(new LinearLayoutManager(this));
        indexingRV.setAdapter(new IndexingAdapter(this, masterRegistrationResponse.getMain_category()));

        streamPopUp.showAsDropDown(v);
//        streamPopUp.showAtLocation(popUpView, Gravity.CENTER, (int) v.getX(), (int) v.getY() + v.getHeight()); // Displaying popup
    }

    public void getLogoutDialog(final String title, final String message) {
        logout(title, message);
    }

    private void logout(final String title, final String message) {
        View v = Helper.newCustomDialog(this, false, title, message);

        Button btnCancel, btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(getString(R.string.no));
        btnSubmit.setText(getString(R.string.yes));

        btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

        btnSubmit.setOnClickListener((View view) -> {
            Helper.dismissDialog();
            Helper.SignOutUser(BaseABNavActivity.this);
        });
    }

    public void changeTabColor(String title) {

        feedsLL.setBackgroundResource(0);
//        feedsIV.setImageResource(R.mipmap.home);
//        feedsTV.setTextColor(ContextCompat.getColor(this, R.color.black));
//
        setFourthTab();

        videosLL.setBackgroundResource(0);

//        videosIV.setImageResource(R.mipmap.videos);
//        videosTV.setTextColor(ContextCompat.getColor(this, R.color.black));
//
//        coursesIV.setImageResource(R.mipmap.courses);
//        coursesTV.setTextColor(ContextCompat.getColor(this, R.color.black));
        coursesLL.setBackgroundResource(0);

        saveNotesIV.setImageResource(R.mipmap.saved_notes_menu);
        saveNotesTV.setTextColor(ContextCompat.getColor(this, R.color.black));

        myCoursesIV.setImageResource(R.mipmap.my_courses);
        myCoursesTV.setTextColor(ContextCompat.getColor(this, R.color.black));

//        liveClassesIV.setImageResource(R.mipmap.live_classes);
//        liveClassesTV.setTextColor(ContextCompat.getColor(this, R.color.black));

        liveClassesLL.setBackgroundResource(0);
        qBankLL.setBackgroundResource(0);

        cprIV.setImageResource(R.mipmap.up_to_date);
        cprTV.setTextColor(ContextCompat.getColor(this, R.color.black));

//        qbankIV.setImageResource(R.mipmap.study_active);
//        qbankTV.setTextColor(ContextCompat.getColor(this, R.color.black));

        switch (title) {
            case Const.SPECIALITIES:
                feedsLL.setBackgroundResource(R.drawable.bg_back_dock);
//                feedsIV.setImageResource(R.mipmap.home_active);
//                feedsTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.BOOKMARKS:
                saveNotesIV.setImageResource(R.mipmap.saved_notes_blue);
                saveNotesTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.Cpr:
                cprIV.setImageResource(R.mipmap.up_to_date_active);
                cprTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.COURSES:
            case Const.ALLCOURSES:
                coursesLL.setBackgroundResource(R.drawable.bg_back_dock);
//                coursesIV.setImageResource(R.mipmap.courses_active);
//                coursesTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.MYCOURSES:
                myCoursesIV.setImageResource(R.mipmap.my_courses_active);
                myCoursesTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.LIVE_CLASSES:
                liveClassesLL.setBackgroundResource(R.drawable.bg_back_dock);
//                liveClassesIV.setImageResource(R.mipmap.live_classes_active);
//                liveClassesTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.VIDEOS:
                videosLL.setBackgroundResource(R.drawable.bg_back_dock);
//                videosIV.setImageResource(R.mipmap.videos_active);
//                videosTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Constants.TestType.Q_BANK:
                qBankLL.setBackgroundResource(R.drawable.bg_back_dock);
//                qbankIV.setImageResource(R.mipmap.study);
//                qbankTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
        }

        manageOptionsVisibility(SharedPreference.getInstance().getMasterHitResponse());
    }

    public void changeLeftPanelColor(String title) {
        Helper.HideKeyboard(this);

        feedsNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        myCourses.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        myQbankNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        myTestNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        podcastNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        newsAndarticleNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        testNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        qBankNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        courseNav.setBackgroundResource(0);
        studyNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        liveCourseNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        videosNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        myScorecard.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        dailyQuizNav.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        myBookmarks.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        myDownloads.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        feedbackTV.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        inviteTV.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        appSettingTV.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
      //  logoutTV.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));
        joinForReward.setBackgroundColor(ContextCompat.getColor(this, R.color.left_panel_background_color));

        Log.e(TAG, "changeLeftPanelColor: title = " + title);
        switch (title) {
            case Const.SPECIALITIES:
                feedsNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.MYCOURSES:
                myCourses.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.MYQBANK:
                myQbankNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.MYTEST:
                myTestNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.PODCAST:
                podcastNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;

            case Const.NEWSARTICLE:
                newsAndarticleNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Constants.TestType.TEST:
                testNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.QBANK:
                qBankNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Constants.TestType.Q_BANK:
                studyNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.LIVE_CLASSES:
                liveCourseNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.COURSES:
            case Const.ALLCOURSES:
                courseNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.VIDEOS:
                videosNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.LEADERBOARD:
                myScorecard.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.DAILY_QUIZ:
                dailyQuizNav.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.MY_BOOKMARKS:
                myBookmarks.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.MYDOWNLOAD:
                myDownloads.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.FEEDBACK:
                feedbackTV.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.INVITEDBY:
                inviteTV.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.APPSETTING:
                appSettingTV.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.LOGOUT:
               // logoutTV.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            case Const.AFFILIATE:
                joinForReward.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_left_panel_background));
                break;
            default:
        }
    }

    private void setFourthTab() {
        cprLL.setVisibility(View.VISIBLE);
        if (cprStatus == 0) {
            myCoursesLL.setVisibility(View.GONE); // My courses tab hidden as LIVE classes tab added!
            cprLL.setVisibility(View.GONE);
        } else {
            myCoursesLL.setVisibility(View.GONE);
            cprLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof FeedsFragment) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                ((FeedsFragment) fragment).isRefresh = true;
                ((FeedsFragment) fragment).lastPostId = "";
                ((FeedsFragment) fragment).firstVisibleItem = 0;
                ((FeedsFragment) fragment).previousTotalItemCount = 0;
                ((FeedsFragment) fragment).visibleItemCount = 0;
                ((FeedsFragment) fragment).refreshFeedList(true);
            } else {
                ((FeedsFragment) fragment).isRefresh = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        } else if (fragment instanceof SavedNotesFragment) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                ((SavedNotesFragment) fragment).isRefresh = true;
                ((SavedNotesFragment) fragment).lastPostId = "";
                ((SavedNotesFragment) fragment).firstVisibleItem = 0;
                ((SavedNotesFragment) fragment).previousTotalItemCount = 0;
                ((SavedNotesFragment) fragment).visibleItemCount = 0;
                ((SavedNotesFragment) fragment).refreshFeedList();
            } else {
                ((SavedNotesFragment) fragment).isRefresh = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        } else if (fragment instanceof CommonFragForList && ((CommonFragForList) fragment).fragType.equals(Const.ALLCOURSES)) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                courseStatus = true;
                ((CommonFragForList) fragment).isRefreshing = false;
                ((CommonFragForList) fragment).getDatas(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        } else if (fragment instanceof RecordedCoursesFragment) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                courseStatus = true;
                ((RecordedCoursesFragment) fragment).networkCallForRecordedCourse();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        } else if (fragment instanceof LiveCoursesFragment) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                courseStatus = true;
                ((LiveCoursesFragment) fragment).networkCallForLiveCourse();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }


    }

    public void networkCallForMasterHit() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getMasterFeedForUser(SharedPreference.getInstance().getLoggedInUser().getId(),
                SharedPreference.getInstance().getString(Constants.SharedPref.USER_INFO));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, " networkcallForMasterHiton Response: " + jsonResponse);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            MasterFeedsHitResponse masterHitData = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterFeedsHitResponse.class);
                       /* if (masterHitData.getUser_detail().getCountry()==null || masterHitData.getUser_detail().getState()==null || masterHitData.getUser_detail().getCity()==null || masterHitData.getUser_detail().getCollege()==null){
                            Helper.userDetailMissingPopup(BaseABNavActivity.this,masterHitData.getPop_msg(), Const.REGISTRATION, Const.PROFILE);
                        }*/

                            manageOptionsVisibility(masterHitData);
                            tagsArrayList = new ArrayList<>();
                            tagsArrayList.addAll(Helper.getTagsForUser());
                            masterHitStatus = false;
                            getNavData();
                            if (SharedPreference.getInstance().getBoolean(Const.IS_STREAM_CHANGE)) {
                                SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, false);
                                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                if (fragment instanceof RecordedCoursesFragment)
                                    refreshFragmentList(fragment, 1);
                                else
                                    coursesLL.performClick();
                            }
                        } else {
                            JSONObject data;
                            String popupMessage = "";
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");
                            RetrofitResponse.handleAuthCode(BaseABNavActivity.this, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(BaseABNavActivity.this, t);
            }
        });
    }

    private void manageOptionsVisibility(MasterFeedsHitResponse masterHitData) {
        SharedPreference.getInstance().setMasterHitData(masterHitData);
        if (fragment instanceof CommonFragForList)
            ((CommonFragForList) fragment).manageLiveAndComboCourseVisibility();
        if (masterHitData != null) {
            if (masterHitData.getShow_feeds() != null
                    && masterHitData.getShow_feeds().equalsIgnoreCase("0")) {
                feedsLL.setVisibility(View.GONE);
                feedsNav.setVisibility(View.GONE);
            } else {
                feedsLL.setVisibility(View.VISIBLE);
                feedsNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getCourse_view() != null
                    && masterHitData.getCourse_view().equalsIgnoreCase("0")) {
                coursesLL.setVisibility(View.GONE);
                courseNav.setVisibility(View.GONE);
            } else {
                coursesLL.setVisibility(View.VISIBLE);
                courseNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_dvl() != null
                    && masterHitData.getShow_dvl().equalsIgnoreCase("0")) {
                videosLL.setVisibility(View.GONE);
                videosNav.setVisibility(View.GONE);
            } else {
                videosLL.setVisibility(View.VISIBLE);
                videosNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_dqb() != null
                    && masterHitData.getShow_dqb().equalsIgnoreCase("0")) {
                qBankLL.setVisibility(View.GONE);
                studyNav.setVisibility(View.GONE);
            } else {
                qBankLL.setVisibility(View.VISIBLE);
                studyNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_bookmark() != null
                    && masterHitData.getShow_bookmark().equalsIgnoreCase("0")) {
                myBookmarks.setVisibility(View.GONE);
                bookmarkIV.setVisibility(View.GONE);
            } else {
                myBookmarks.setVisibility(View.VISIBLE);
                bookmarkIV.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_daily_quiz() != null
                    && masterHitData.getShow_daily_quiz().equalsIgnoreCase("0")) {
                dailyQuizNav.setVisibility(View.GONE);
            } else {
                dailyQuizNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_affiliate_program() != null
                    && masterHitData.getShow_affiliate_program().equalsIgnoreCase("0")) {
                joinForReward.setVisibility(View.GONE);
            } else {
                joinForReward.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_live_course() != null
                    && masterHitData.getShow_live_course().equalsIgnoreCase("0")) {
                liveClassesLL.setVisibility(View.GONE);
                liveCourseNav.setVisibility(View.GONE);
            } else {
                liveClassesLL.setVisibility(View.VISIBLE);
                liveCourseNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getShow_podcast() != null
                    && masterHitData.getShow_podcast().equalsIgnoreCase("0")) {
                podcastNav.setVisibility(View.GONE);
            } else {
                podcastNav.setVisibility(View.VISIBLE);
            }

            if (masterHitData.getNews_article() != null && masterHitData.getNews_article().equalsIgnoreCase("0")) {
                newsAndarticleNav.setVisibility(View.GONE);
            } else {
                newsAndarticleNav.setVisibility(View.VISIBLE);
            }

        } else {
            feedsLL.setVisibility(View.VISIBLE);
            qBankLL.setVisibility(View.VISIBLE);
            videosLL.setVisibility(View.VISIBLE);
            coursesLL.setVisibility(View.VISIBLE);
            liveClassesLL.setVisibility(View.VISIBLE);

            feedsNav.setVisibility(View.VISIBLE);
            videosNav.setVisibility(View.VISIBLE);
            courseNav.setVisibility(View.VISIBLE);
            dailyQuizNav.setVisibility(View.VISIBLE);
            bookmarkIV.setVisibility(View.VISIBLE);
            myBookmarks.setVisibility(View.VISIBLE);

            joinForReward.setVisibility(View.VISIBLE);
            podcastNav.setVisibility(View.VISIBLE);
            newsAndarticleNav.setVisibility(View.VISIBLE);
        }
    }


    private void networkCallForValidateDAMSUser() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("BASE", "networkcallForValidateDAMSUser: ");
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.validateDAMSUser(SharedPreference.getInstance().getLoggedInUser().getDams_tokken());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("BASE:validateDams", "onResponse: ");
                        } else {
                            Toast.makeText(BaseABNavActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            Helper.SignOutUser(BaseABNavActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void networkCallForMasterRegData() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("BASE", "networkCallForMasterRegData: ");
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getMasterRegistrationResponse();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!BaseABNavActivity.this.isFinishing())
                    mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("BASE:regMaster", "onResponse: ");
                            masterRegistrationResponse = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterRegistrationResponse.class);

                            if (masterRegistrationResponse != null && !masterRegistrationResponse.getMain_category().isEmpty()) {
                                SharedPreference.getInstance().setMasterRegistrationData(masterRegistrationResponse);

                                initStreamList();
                                if (isMenuClicked) {
                                    isMenuClicked = false;
                                    showPopMenuForStream(navStreamLL);
                                }
                            } else {
                                if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(getString(R.string.something_went_wrong))) {
                                    networkCallForMasterRegData();//networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                                } else {
                                    Toast.makeText(BaseABNavActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(getString(R.string.something_went_wrong))) {
                                networkCallForMasterRegData();//networkCall.NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                            } else {
                                Toast.makeText(BaseABNavActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                            JSONObject data;
                            String popupMessage = "";
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");
                            RetrofitResponse.handleAuthCode(BaseABNavActivity.this, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (!BaseABNavActivity.this.isFinishing()) {
                    mProgress.dismiss();
                    Helper.showExceptionMsg(BaseABNavActivity.this, t);
                }

            }
        });
    }

    private void networkCallForAppVersion() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("BASE", "networkCallForAppVersion: ");
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getAppVersion();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("BASE:appVersion", "onResponse: ");
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            String androidVersion = data.optString("android");
                            int aCode = Integer.parseInt(androidVersion.isEmpty() ? "0" : androidVersion.trim());
                            if (Helper.getVersionCode(BaseABNavActivity.this) < aCode)
                                Helper.getVersionUpdateDialog(BaseABNavActivity.this);
                        } else {
                            JSONObject data;
                            String popupMessage = "";
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");

                            RetrofitResponse.handleAuthCode(BaseABNavActivity.this, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(BaseABNavActivity.this, t);

            }
        });
    }

    private void networkCallForTrendingSearch(String type) {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getTrendingSearch(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, "onResponse: " + jsonResponse);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            trendingArrayList = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Review review = gson.fromJson(data.optJSONObject(i).toString(), Review.class);
                                trendingArrayList.add(review);
                            }

                            if (type.equals(Const.DRAWER)) {
                                createTopSearchedCourse();
                            } else {
                                if (!searchView.isIconified())
                                    initTrendingAdapter();
                            }
                        } else {
                            Toast.makeText(BaseABNavActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(BaseABNavActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(BaseABNavActivity.this, t);

            }
        });
    }

    private void initTrendingAdapter() {
        View popupView = getLayoutInflater().inflate(R.layout.trending_dialog_lay, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        popupWindow.showAsDropDown(searchView);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // set the custom dialog components - text, image and button
        bookingsLay = popupWindow.getContentView().findViewById(R.id.bookingsLay);
        addBookings();
    }


    private void initHelpAndSupport() {
        View popupView = getLayoutInflater().inflate(R.layout.fragment_help_and_support, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        popupWindow.showAsDropDown(helpView);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        crossHelp = popupWindow.getContentView().findViewById(R.id.crossHelp);
        recyclerView_help = popupWindow.getContentView().findViewById(R.id.recyclerView_help);
        editTextHelp = popupWindow.getContentView().findViewById(R.id.helpSearchFilter);
        helpImageCross = popupWindow.getContentView().findViewById(R.id.img_help_clear_search);
        helpImageSearch = popupWindow.getContentView().findViewById(R.id.img_help_search_view);


        helpImageCross.setVisibility(View.GONE);
        helpImageSearch.setVisibility(View.VISIBLE);
        editTextHelp.setText("");
        questionQuery = "";
        getHelpAndSupportData();


        helpImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.closeKeyboard(BaseABNavActivity.this);
                helpImageCross.setVisibility(View.GONE);
                helpImageSearch.setVisibility(View.VISIBLE);
                editTextHelp.setText("");
                questionQuery = "";
                getHelpAndSupportData();
            }
        });


        editTextHelp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getHelpAndSupportDataWithQuery();
                    return true;
                }
                return false;
            }
        });


        editTextHelp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                questionQuery = s.toString();
                if (!TextUtils.isEmpty(s)) {
                    helpImageCross.setVisibility(View.VISIBLE);
                    helpImageSearch.setVisibility(View.GONE);
                } else {
                    helpImageCross.setVisibility(View.GONE);
                    helpImageSearch.setVisibility(View.VISIBLE);
                }

            }
        });


        crossHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        btnStartChat = popupWindow.getContentView().findViewById(R.id.btn_Start_Chat);
        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppChat();
            }
        });

        startChat = popupWindow.getContentView().findViewById(R.id.startChat);
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppChat();
            }
        });

    }


    private void getHelpAndSupportDataQuery() {
//        questionQuery = editTextHelp.getText().toString().trim();
        eMedicozApp.getInstance().questionQuery = questionQuery;
        if (!TextUtils.isEmpty(questionQuery)) {
            helpAndSupportArrayList = new ArrayList<HelpAndSupport>();
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.getHelpAndSupportDataWithQuery(SharedPreference.getInstance().getLoggedInUser().getId(), questionQuery);
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.body() != null) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;
                        JSONArray arrCourseList;
                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                                arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("help_and_support_list");

                                for (int j = 0; j < arrCourseList.length(); j++) {
                                    JSONObject courseObject = arrCourseList.getJSONObject(j);
                                    HelpAndSupport helpAndSupport = gson.fromJson(Objects.requireNonNull(courseObject).toString(), HelpAndSupport.class);
                                    helpAndSupportArrayList.add(helpAndSupport);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }

                    try {
                        HelpAndSupportAdapter helpAndSupportAdapter = new HelpAndSupportAdapter(helpAndSupportArrayList, BaseABNavActivity.this);
                        recyclerView_help.setLayoutManager(new LinearLayoutManager(BaseABNavActivity.this));
                        recyclerView_help.setAdapter(helpAndSupportAdapter);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show();
        }

    }


    private void getHelpAndSupportData() {
        // asynchronous operation to get user recently viewed courses.
        mProgress.show();
        Helper.closeKeyboard(this);
        helpAndSupportArrayList = new ArrayList<HelpAndSupport>();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getHelpAndSupportData(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    JSONArray arrCourseList;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("help_and_support_list");

                            for (int j = 0; j < arrCourseList.length(); j++) {
                                JSONObject courseObject = arrCourseList.getJSONObject(j);
                                HelpAndSupport helpAndSupport = gson.fromJson(Objects.requireNonNull(courseObject).toString(), HelpAndSupport.class);
                                helpAndSupportArrayList.add(helpAndSupport);
                            }

                            try {
                                HelpAndSupportAdapter helpAndSupportAdapter = new HelpAndSupportAdapter(helpAndSupportArrayList, BaseABNavActivity.this);
                                recyclerView_help.setLayoutManager(new LinearLayoutManager(BaseABNavActivity.this));
                                recyclerView_help.setAdapter(helpAndSupportAdapter);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = "No Data Found";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        }

                        mProgress.dismiss();
                    } catch (JSONException e) {
                        mProgress.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Data Found ", Toast.LENGTH_LONG).show();
                }
                mProgress.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getHelpAndSupportDataWithQuery() {
        mProgress.show();
        questionQuery = editTextHelp.getText().toString().trim();
        eMedicozApp.getInstance().questionQuery = questionQuery;
        Helper.closeKeyboard(this);
        if (!TextUtils.isEmpty(questionQuery)) {
            helpAndSupportArrayList = new ArrayList<HelpAndSupport>();
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.getHelpAndSupportDataWithQuery(SharedPreference.getInstance().getLoggedInUser().getId(), questionQuery);
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mProgress.dismiss();
                    if (response.body() != null) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;
                        JSONArray arrCourseList;
                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                                arrCourseList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("help_and_support_list");

                                for (int j = 0; j < arrCourseList.length(); j++) {
                                    JSONObject courseObject = arrCourseList.getJSONObject(j);
                                    HelpAndSupport helpAndSupport = gson.fromJson(Objects.requireNonNull(courseObject).toString(), HelpAndSupport.class);
                                    helpAndSupportArrayList.add(helpAndSupport);
                                }

                                try {
                                    HelpAndSupportAdapter helpAndSupportAdapter = new HelpAndSupportAdapter(helpAndSupportArrayList, BaseABNavActivity.this);
                                    recyclerView_help.setLayoutManager(new LinearLayoutManager(BaseABNavActivity.this));
                                    recyclerView_help.setAdapter(helpAndSupportAdapter);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String message = "No Data Found";
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            mProgress.dismiss();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Data Found ", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show();
        }

    }


    private void openWhatsAppChat() {
        String number = BuildConfig.WHATSAPP_NO;
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
            startActivity(sendIntent);

        } catch (Exception e) {
            if (e instanceof ActivityNotFoundException) {
                final String appPackageName = "com.whatsapp";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            Log.e(TAG, "ERROR_OPEN_MESSANGER" + e.toString());
        }

    }


    private void addBookings() {
        for (int i = 0; i < trendingArrayList.size(); i++) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.single_row_trending, null);
            RelativeLayout bookingLay;
            TextView trendingText;

            bookingLay = view.findViewById(R.id.parentLL);
            trendingText = view.findViewById(R.id.nameTV);
            bookingLay.setTag(i);

            trendingText.setText(trendingArrayList.get(i).getText());
            final int finalI = i;
            bookingLay.setOnClickListener((View v) -> {
                if (popupWindow != null) popupWindow.dismiss();
                fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, trendingArrayList.get(finalI).getText());
                searchView.setQuery(trendingArrayList.get(finalI).getText(), true);
                /*Helper.closeKeyboard(BaseABNavActivity.this);*/
                /*RefreshFragmentList(fragment, 0);*/
            });

            bookingsLay.addView(view);
            bookingsView.add(view);
        }
    }

    // setting the moderator selected stream as per moderator selection.
    // this will only work if user is moderator and changes its preference to see the feeds.
    public void setModeratorSelectedStream(StreamResponse stream) {
        SharedPreference.getInstance().putString(Const.MODERATOR_SELECTED_STREAM, stream.getId());
        tagsArrayList = new ArrayList<>();
        tagsArrayList.addAll(Helper.getTagsForUser());
        getNavData();
    }

    public void setData(ArrayList<Video> videos, String id) {
        int flag = 0;
        if (videoTableArrayList == null) {
            videoTableArrayList = (ArrayList<Videotable>) Helper.getStorageInstance(BaseABNavActivity.this).getRecordObject(Const.OFFLINE_VIDEOTAGS_DATA);
            if (videoTableArrayList == null) {
                videoTableArrayList = new ArrayList<>();
            }
        }

        for (Videotable video : videoTableArrayList) {
            if (video.id.equals(id)) {
                video.videos = videos;
                video.message = "Data Updated";
                flag = 1;
            }
        }

        if (flag == 0) {
            Videotable videotable = new Videotable();
            videotable.id = id;
            if (videos.isEmpty())
                videotable.message = getString(R.string.no_data_found);
            else
                videotable.message = getString(R.string.data_inserted);
            videotable.videos = videos;
            videoTableArrayList.add(videotable);
        }
        Helper.getStorageInstance(this).addRecordStore(Const.OFFLINE_VIDEOTAGS_DATA, videoTableArrayList);
    }

    public String getData(String id) {
        if (videoTableArrayList != null && !videoTableArrayList.isEmpty()) {
            for (Videotable video : videoTableArrayList) {
                if (video.id.equals(id)) {
                    return new Gson().toJson(video);
                }
            }
        } else if (!videoStatus || !Helper.isConnected(this)) {
            videoTableArrayList = (ArrayList<Videotable>) Helper.getStorageInstance(this).getRecordObject(Const.OFFLINE_VIDEOTAGS_DATA);
            if (videoTableArrayList != null) {
                for (Videotable video : videoTableArrayList) {
                    if (video.id.equals(id)) {
                        return new Gson().toJson(video);
                    }
                }
            }
        }

        Videotable videotable = new Videotable();
        videotable.message = getString(R.string.no_record_found);
        return new Gson().toJson(videotable);
    }

    // isError:  Whether there is an error or success response
    // layoutType: Whether there is an internet issue or api Error like "Something went wrong"
    public void replaceErrorLayout(int isError, int layoutType) {
        // 0 is for no data found
        // 1 is for no internet connection
        // 2 is for something went wrong or everything else.
        if (layoutType == 0) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.no_post_found);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.no_data_found));
            findViewById(R.id.errorMessageTV2).setVisibility(View.GONE);
            findViewById(R.id.tryAgainBtn).setVisibility(View.GONE);
            findViewById(R.id.enrollNow).setVisibility(View.GONE);
            findViewById(R.id.oops).setVisibility(View.GONE);

        } else if (layoutType == 1) {
            if (!Helper.isConnected(this)) {
                ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.no_internet);
                ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.internet_error_message));
            } else {
                ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.something_went_wrong);
                ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.exception_api_error_message));

            }
        } else if (layoutType == 2) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.something_went_wrong);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.exception_api_error_message));
        } else if (layoutType == 3) {
            //((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.by_course);   // old design
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.no_post_found);   // new design
            findViewById(R.id.oops).setVisibility(View.GONE);
            findViewById(R.id.tryAgainBtn).setVisibility(View.GONE);
            findViewById(R.id.enrollNow).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(R.string.no_course);
            ((TextView) findViewById(R.id.errorMessageTV2)).setText(R.string.purchases_course_msg);
        }
//        swipeRefreshLayout.setVisibility((isError == 1 ? View.GONE : View.VISIBLE));
        errorLayout.setVisibility((isError == 1 ? View.VISIBLE : View.GONE));
    }

    public void retryApiButton() {
        if (!TextUtils.isEmpty(apiType)) {
            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            if (fragment instanceof HelpSupportFragment) {
//                ((HelpSupportFragment) fragment).networkCallForSubmitQuery();
//            }
            if (/*fragment instanceof VideoFragment || */fragment instanceof DVLFragment) { //API_GET_SINGLE_CAT_VIDEO_DATA
                if (!((DVLFragment) fragment).isSearching) {
//                    ((VideoFragment) fragment).initView(((VideoFragment) fragment).mView);
                    //refreshIV.performClick();
                } else {
                    ((DVLFragment) fragment).retryApis(apiType);
                }
            }
            if (fragment instanceof FeedsFragment) {
                ((FeedsFragment) fragment).retryApi(apiType);
            }
            if (fragment instanceof SavedNotesFragment) {
                ((SavedNotesFragment) fragment).makeTabs();
            }
            if (fragment instanceof CommonFragForList) {
                ((CommonFragForList) fragment).retryApi(apiType);
            }
            if (fragment instanceof StudyFragment) {
                ((StudyFragment) fragment).networkCallForTabData("retryApiButton");
            }
            if (fragment instanceof MyScorecardFragment) {
                ((MyScorecardFragment) fragment).networkCallForTestSeries(true);
            }

        }
    }

    public void getNavData() {
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
            networkCallForValidateDAMSUser();
        }
        ArrayList<Tags> tags = new ArrayList<>();
        if (tagsArrayList != null && !tagsArrayList.isEmpty()) {
            tags.addAll(tagsArrayList);

        }

        //this is for the main titles of the navigation drawer
        expandableListTitle = Helper.gettitleList(this);
        //this is for the specified tags under specialities
        MasterFeedsHitResponse masterResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (masterResponse != null) {
            cprStatus = masterResponse.getCpr_view();
        }
        setFourthTab();
        initTagsAdapter(tags);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (fragment instanceof HelpSupportFragment) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
    }

    void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory != null && fileOrDirectory.isDirectory() && fileOrDirectory.listFiles() != null)
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            if (fileOrDirectory != null) {
                fileOrDirectory.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
