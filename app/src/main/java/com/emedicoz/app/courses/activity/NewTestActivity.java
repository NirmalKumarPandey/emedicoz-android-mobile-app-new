package com.emedicoz.app.courses.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.ViewPagerAdapter;
import com.emedicoz.app.courses.fragment.NewChildTestQuizCourseFrag;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestQuizPojo;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewTestActivity";
    public ViewPager viewPager;
    public ArrayList<TestSeries> activeTestList;
    public int position = -1;
    public String titleFrag;
    String isPurchased;
    LinearLayout noDataLL;
    ImageView noDataImage;
    TextView noDataText;
    ArrayList<String> subjectId;
    ArrayList<TestSeries> list1 = new ArrayList<>();
    ArrayList<TestSeries> list2 = new ArrayList<>();
    ArrayList<TestSeries> list3 = new ArrayList<>();
    ArrayList<TestSeries> list4 = new ArrayList<>();
    SingleCourseData singleCourseData;
    ViewPagerAdapter adapter;
    private String courseId;
    private String titleString;
    private TabLayout tabLayout;
    private TestQuizPojo testQuizPojo;
    private TextView noDataTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_test_quiz);
        getIntentData();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: onResume() is called");
        networkCall();
    }

    private void networkCall() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress progress = new Progress(this);
        progress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Log.e(TAG, "CourseId:" + courseId + ", user_id:" + SharedPreference.getInstance().getLoggedInUser().getId());
        Call<JsonObject> response = apiInterface.getTestData(SharedPreference.getInstance().getLoggedInUser().getId(),
                courseId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            progress.dismiss();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            testQuizPojo = gson.fromJson(data.toString(), TestQuizPojo.class);
                            noDataLL.setVisibility(View.GONE);
                            extractData();
                            extractSubjectData();
                            setupViewPager(activeTestList, list1, list2, list3, list4);
                            tabLayout.setupWithViewPager(viewPager);
                        } else {
                            progress.dismiss();
                            tabLayout.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.GONE);
                            noDataLL.setVisibility(View.VISIBLE);
                            noDataText.setText(getString(R.string.no_test_found));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    progress.dismiss();
                    tabLayout.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(getString(R.string.no_test_found));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                if (Objects.requireNonNull(t.getMessage()).contains(Constants.BASE_DOMAIN_URL)) {
                    Toast.makeText(NewTestActivity.this, getResources().getString(R.string.please_check_your_internet_connectivity), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewTestActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backIV) {
            onBackPressed();
        }
    }

    private void extractData() {

        clearArrayList();

        for (int i = 0; i < testQuizPojo.getTest_series().length; i++) {
            TestSeries testSeries = testQuizPojo.getTest_series()[i];
            activeTestList.add(testSeries);
            if (testQuizPojo.getMasters() != null)
                for (int j = 0; j < testQuizPojo.getMasters().length; j++) {
                    if (j == 0) {
                        if (testSeries.getTopic_name().equalsIgnoreCase(testQuizPojo.getMasters()[j].getTest_type_title())) {
                            list1.add(testSeries);
                        }
                    } else if (j == 1) {
                        if (testSeries.getTopic_name().equalsIgnoreCase(testQuizPojo.getMasters()[j].getTest_type_title())) {
                            list2.add(testSeries);
                        }
                    } else if (j == 2) {
                        if (testSeries.getTopic_name().equalsIgnoreCase(testQuizPojo.getMasters()[j].getTest_type_title())) {
                            list3.add(testSeries);
                        }
                    } else if (j == 3 && testSeries.getTopic_name().equalsIgnoreCase(testQuizPojo.getMasters()[j].getTest_type_title())) {
                        list4.add(testSeries);
                    }
                }
        }


    }

    private void clearArrayList() {
        if (!activeTestList.isEmpty()) {
            activeTestList.clear();
        }
        if (!list1.isEmpty()) {
            list1.clear();
        }
        if (!list2.isEmpty()) {
            list2.clear();
        }
        if (!list3.isEmpty()) {
            list3.clear();
        }
        if (!list4.isEmpty()) {
            list4.clear();
        }
    }

    private void extractSubjectData() {
        if (!subjectId.isEmpty()) {
            subjectId.clear();
        }
        for (int i = 0; i < testQuizPojo.getTest_series().length; i++) {
            if (!subjectId.contains(testQuizPojo.getTest_series()[i].getSubject_id())) {
                subjectId.add(testQuizPojo.getTest_series()[i].getSubject_id());
            }
        }
    }

    private void setupViewPager(ArrayList<TestSeries> activeTestList, ArrayList<TestSeries> list1, ArrayList<TestSeries> list2, ArrayList<TestSeries> list3, ArrayList<TestSeries> list4) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (!activeTestList.isEmpty()) {
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(activeTestList, courseId, getString(R.string.all), isPurchased, singleCourseData), getString(R.string.all));

            if (testQuizPojo.getMasters() != null) {
                for (int i = 0; i < testQuizPojo.getMasters().length; i++) {

                    if (i == 0) {
                        adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list1, courseId, getString(R.string.others_string), isPurchased, singleCourseData), testQuizPojo.getMasters()[i].getTest_type_title());

                    } else if (i == 1) {
                        adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list2, courseId, getString(R.string.others_string), isPurchased, singleCourseData), testQuizPojo.getMasters()[i].getTest_type_title());

                    } else if (i == 2) {
                        adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list3, courseId, getString(R.string.others_string), isPurchased, singleCourseData), testQuizPojo.getMasters()[i].getTest_type_title());

                    } else if (i == 3) {
                        adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list4, courseId, getString(R.string.others_string), isPurchased, singleCourseData), testQuizPojo.getMasters()[i].getTest_type_title());

                    }
                }
                viewPager.setAdapter(adapter);
            }
            Log.e(getString(R.string.title_frag), titleFrag);

            switch (titleFrag) {
                case Constants.TestType.Mock_Test:
                    viewPager.setCurrentItem(3);
                    break;
                case Constants.TestType.GRAND_TEST:
                    viewPager.setCurrentItem(1);
                    break;
                case Constants.TestType.Subject_Wise_Test:
                    viewPager.setCurrentItem(2);
                    break;
                case Constants.TestType.Class_Wise_Test:
                    viewPager.setCurrentItem(4);
                    break;
                default:
                    viewPager.setCurrentItem(0);
                    break;
            }
        } else {
            tabLayout.setVisibility(View.GONE);
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.VISIBLE);
            noDataText.setText(getString(R.string.no_test_found));
            viewPager.setAdapter(adapter);
        }
    }

    private void initViews() {
        activeTestList = new ArrayList<>();
        subjectId = new ArrayList<>();

        noDataTV = findViewById(R.id.noDataTV);
        RelativeLayout toolbar = findViewById(R.id.toolbar);
        LinearLayout activeParentLL = findViewById(R.id.activeParentLL);
        LinearLayout upcomingParentLL = findViewById(R.id.upcomingParentLL);
        LinearLayout missedParentLL = findViewById(R.id.missedParentLL);
        LinearLayout completedParentLL = findViewById(R.id.completedParentLL);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        noDataText = findViewById(R.id.noDataText);
        noDataImage = findViewById(R.id.noDataImage);
        noDataImage.setImageResource(R.mipmap.test_blank);
        noDataLL = findViewById(R.id.noDataLL);
        TextView title = toolbar.findViewById(R.id.title);
        ImageView backIV = toolbar.findViewById(R.id.backIV);
        title.setText(titleString);

        activeParentLL.setTag(Constants.TestStatus.ACTIVE);
        upcomingParentLL.setTag(Constants.TestStatus.UPCOMING);
        missedParentLL.setTag(Constants.TestStatus.MISSED);
        completedParentLL.setTag(Constants.TestStatus.COMPLETED);

        activeParentLL.setOnClickListener(this);
        upcomingParentLL.setOnClickListener(this);
        missedParentLL.setOnClickListener(this);
        completedParentLL.setOnClickListener(this);
        backIV.setOnClickListener(this);
    }

    private void getIntentData() {
        courseId = getIntent().getStringExtra(Const.COURSE_ID);
        titleString = getIntent().getStringExtra(Const.TITLE);
        titleFrag = getIntent().getStringExtra(Constants.Extras.TITLE_FRAG);
        isPurchased = getIntent().getStringExtra(Constants.Extras.IS_PURCHASED);
        singleCourseData = (SingleCourseData) getIntent().getSerializableExtra(Const.COURSE_DESC);
        position = Objects.requireNonNull(getIntent().getExtras()).getInt("position");
    }
}
