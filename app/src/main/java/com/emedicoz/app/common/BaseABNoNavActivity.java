package com.emedicoz.app.common;

import android.app.SearchManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.fragment.CommonFragForList;
import com.emedicoz.app.courses.fragment.CourseInvoice;
import com.emedicoz.app.courses.fragment.CourseReviews;
import com.emedicoz.app.courses.fragment.CreateCustomModule;
import com.emedicoz.app.courses.fragment.Curriculum;
import com.emedicoz.app.courses.fragment.CustomModuleStartFragement;
import com.emedicoz.app.courses.fragment.CustomModuleTopicNew;
import com.emedicoz.app.courses.fragment.GraphFragment;
import com.emedicoz.app.courses.fragment.InstructorFragment;
import com.emedicoz.app.courses.fragment.LeaderBoardFragment;
import com.emedicoz.app.courses.fragment.Quiz;
import com.emedicoz.app.courses.fragment.ResultAnalysisFragment;
import com.emedicoz.app.courses.fragment.SingleCourse;
import com.emedicoz.app.courses.fragment.SingleStudy;
import com.emedicoz.app.courses.fragment.ViewAnalysis;
import com.emedicoz.app.feeds.fragment.CommonFragment;
import com.emedicoz.app.feeds.fragment.CourseInterestedInFragment;
import com.emedicoz.app.feeds.fragment.NewPostFragment;
import com.emedicoz.app.feeds.fragment.RegistrationFragment;
import com.emedicoz.app.feeds.fragment.SpecializationFragment;
import com.emedicoz.app.feeds.fragment.SubStreamFragment;
import com.emedicoz.app.installment.fragment.SuccessfullyPaymentDoneFragment;
import com.emedicoz.app.login.fragment.ChangePassword;
import com.emedicoz.app.login.fragment.MobileVerification;
import com.emedicoz.app.login.fragment.OtpVerification;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.Utils.service.ErrorAlertBroadcastReceiver;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.navigation.NavigationView;


/**
 * Created by Cbc-03 on 06/06/17.
 */
public abstract class BaseABNoNavActivity extends AppCompatActivity {

    public String countryId = "";
    public String countryName = "";
    public String collegeName = "";
    public Fragment mFragment;
    public TextView graphTV;
    public String type = "";
    public SwipeRefreshLayout swipeRefreshLayout;
    public SearchView searchView;
    public ImageView quizNavigatorIV;
    public ImageView attemptedIV;
    public ImageView notAttemptedIV;
    public ImageButton imageViewBack;
    public TextView toolbarTitleTV;
    public Button nextButton;
    public Button tryAgainBtn;
    public Button enrollNow;
    public RelativeLayout mFragmentLayout;
    public LinearLayout errorLayout;
    public String apiType;
    public DrawerLayout drawer;
    public NavigationView navigationView2;
    public RecyclerView controllerRV;
    protected Toolbar toolbar;
    FragmentManager fragmentManager;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.quiz_question_control);
        registerReceiver(new ErrorAlertBroadcastReceiver(), new IntentFilter(Const.ERROR_ALERT_INTENT_FILTER));

        mFragmentLayout = findViewById(R.id.fragment_container);
        errorLayout = findViewById(R.id.errorLL);
        toolbar = findViewById(R.id.main_toolbar);
        toolbarTitleTV = findViewById(R.id.toolbarTitleTV);
        nextButton = findViewById(R.id.next_button);
        tryAgainBtn = findViewById(R.id.tryAgainBtn);
        enrollNow = findViewById(R.id.enrollNow);
        quizNavigatorIV = findViewById(R.id.quizNavigatorIV);
        graphTV = findViewById(R.id.graphTV);
        drawer = findViewById(R.id.drawer_layout);
        navigationView2 = findViewById(R.id.nav_view2);
        controllerRV = findViewById(R.id.controllerRV);
        attemptedIV = findViewById(R.id.attemptedIV);
        notAttemptedIV = findViewById(R.id.notattemptedIV);
        imageViewBack = findViewById(R.id.back_app_bar);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setSupportActionBar(toolbar);
        Helper.logUser(this);

        try {
            // this to check is the service to check the downloading is not initialised then it will be initialised here.
//            if (SplashScreen.intent == null) {
//                SplashScreen.intent = new Intent(eMedicozApp.getAppContext(), OnClearFromRecentService.class);
//                startService(SplashScreen.intent);
//            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        initSearchView();
        initViews();

        imageViewBack.setVisibility(View.VISIBLE);
        imageViewBack.setOnClickListener(v -> onCustomBackPress());
        mFragment = getFragment();

        tryAgainBtn.setOnClickListener(view -> {
            replaceErrorLayout(0, 0);
            retryApiButton();
        });
    }

    public void setToolbarTitle(String str) {
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        toolbarTitleTV.setText(str);

        if (fragment instanceof CreateCustomModule) {
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }
    }

    public void initSearchView() {

        searchView = findViewById(R.id.searchSV);

        searchView.setOnCloseListener(() -> {
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
            toolbarTitleTV.setVisibility(View.VISIBLE);
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, "");
            refreshFragmentList(fragment, false);
            return false;
        });

        searchView.setOnSearchClickListener((View view) -> {
            searchView.setFocusable(true);
            toolbarTitleTV.setVisibility(View.GONE);
        });

        View searchPlateView = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        // use this method for search process
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, query);
                refreshFragmentList(getSupportFragmentManager().findFragmentById(R.id.fragment_container), true);
                Helper.closeKeyboard(BaseABNoNavActivity.this);
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

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        replaceErrorLayout(0, 0);
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        } else if (mFragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(mFragment.getClass().getSimpleName()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        onCustomBackPress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onCustomBackPress();
        }
        return true;
    }

    public void onCustomBackPress() {

        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (!searchView.isIconified()) {
            toolbarTitleTV.setVisibility(View.VISIBLE);
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();

            // use this method when query submitted
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, "");
            refreshFragmentList(fragment, false);

        } else if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase(Const.NOTIFICATION) && fragment instanceof SingleCourse) {
            Helper.GoToNextActivity(this);
        } else if (fragment instanceof SubStreamFragment ||
                fragment instanceof SpecializationFragment ||
                fragment instanceof CourseInterestedInFragment ||
                fragment instanceof CustomModuleTopicNew)
            getSupportFragmentManager().popBackStack();
        else if (fragment instanceof NewPostFragment || fragment instanceof CommonFragment ||
                fragment instanceof MobileVerification || fragment instanceof ChangePassword || fragment instanceof OtpVerification ||
                fragment instanceof CommonFragForList || fragment instanceof SingleCourse)
            this.finish();
        else if (fragment instanceof RegistrationFragment) {
            if (SharedPreference.getInstance().getMasterHitResponse() != null) {
                User user = SharedPreference.getInstance().getMasterHitResponse().getUser_detail();
                if (user.getCountry() == null || user.getState() == null || user.getCity() == null || user.getCollege() == null) {
                    Toast.makeText(this, R.string.pl_clg_information, Toast.LENGTH_SHORT).show();
                } else {
                    this.finish();
                }
            } else {
                this.finish();
            }
        } else if (fragment instanceof Quiz)
            ((Quiz) fragment).showResumePopup(false);
        else if (fragment instanceof CustomModuleStartFragement) {
            if (((CustomModuleStartFragement) fragment).isModuleStart) {
                Helper.GoToNextActivity(this);
            } else {
                this.finish();
            }
        } else if (fragment instanceof SuccessfullyPaymentDoneFragment)
            Helper.GoToNextActivity(this);
        else
            this.finish();

    }

    public void refreshFragmentList(Fragment fragment, boolean isSearch) {
        if (fragment instanceof CommonFragForList) {
            ((CommonFragForList) fragment).isSearching = isSearch;
            ((CommonFragForList) fragment).lastCourseId = "";
            ((CommonFragForList) fragment).firstVisibleItem = 0;
            ((CommonFragForList) fragment).previousTotalItemCount = 0;
            ((CommonFragForList) fragment).visibleItemCount = 0;

            ((CommonFragForList) fragment).getDatas(true);
        }
    }

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
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.by_course);
            findViewById(R.id.oops).setVisibility(View.GONE);
            findViewById(R.id.tryAgainBtn).setVisibility(View.GONE);
            findViewById(R.id.enrollNow).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(R.string.no_course);
            ((TextView) findViewById(R.id.errorMessageTV2)).setText(R.string.purchases_course_msg);
            findViewById(R.id.enrollNow).setOnClickListener((View view) -> {
                CourseCategory courseCategory = new CourseCategory();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CommonFragForList.newInstance(Const.ALLCOURSES, courseCategory)).commit();
            });
        }
        mFragmentLayout.setVisibility((isError == 1 ? View.GONE : View.VISIBLE));
        errorLayout.setVisibility((isError == 1 ? View.VISIBLE : View.GONE));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment instanceof CommonFragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void retryApiButton() {
        if (!TextUtils.isEmpty(apiType)) {
            fragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if (fragment instanceof CommonFragment) {
                ((CommonFragment) fragment).retryApis(apiType);
            } else if (fragment instanceof NewPostFragment) {
                ((NewPostFragment) fragment).retryApis(apiType);
            } else if (fragment instanceof CourseInterestedInFragment) {
                ((CourseInterestedInFragment) fragment).networkCallForMasterRegHit();
            } else if (fragment instanceof RegistrationFragment) {
                ((RegistrationFragment) fragment).retryApis(apiType);
            } else if (fragment instanceof SubStreamFragment) {
                ((SubStreamFragment) fragment).networkCallForMasterReg();
            } else if (fragment instanceof CommonFragForList) {
                ((CommonFragForList) fragment).retryApi(apiType);
            } else if (fragment instanceof CourseInvoice) {
                ((CourseInvoice) fragment).retryApis(apiType);
            } else if (fragment instanceof CourseReviews) {
                ((CourseReviews) fragment).retryApis(apiType);
            } else if (fragment instanceof Curriculum) {
                ((Curriculum) fragment).retryApis(apiType);
            } else if (fragment instanceof InstructorFragment) {
                ((InstructorFragment) fragment).retryApis(apiType);
            } else if (fragment instanceof Quiz) {
                ((Quiz) fragment).networkCallForcompleteinfoTestSeries();//(apiType, true);
            } else if (fragment instanceof ResultAnalysisFragment) {
                ((ResultAnalysisFragment) fragment).networkCallForQuizResult();
            } else if (fragment instanceof SingleCourse) {
                ((SingleCourse) fragment).retryApis(apiType);
            } else if (fragment instanceof ViewAnalysis) {
                ((ViewAnalysis) fragment).netoworkCallSubjectResult(apiType);
            } else if (fragment instanceof GraphFragment) {
                ((GraphFragment) fragment).netoworkCallSubjectResult();
            } else if (fragment instanceof LeaderBoardFragment) {
                ((LeaderBoardFragment) fragment).netoworkCallForRank();
            } else if (fragment instanceof SingleStudy) {
                ((SingleStudy) fragment).networkCallForGetBasicCourse();
            }
        }
    }

    protected abstract void initViews();


    protected abstract Fragment getFragment();
}


