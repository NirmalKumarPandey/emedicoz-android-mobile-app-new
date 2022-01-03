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
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTestQuizActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewTestQuizActivity";
    public ViewPager viewPager;
    public ArrayList<TestSeries> activeTestList;
    public int lastPos;
    int position = -1;
    public String titleFrag;
    String isPurchased;
    LinearLayout noDataLL;
    ImageView noDataImage;
    TextView noDataText;
    ArrayList<String> subjectId;
    ArrayList<TestSeries> list1 = new ArrayList<>();
    SingleCourseData singleCourseData;
    ViewPagerAdapter adapter;
    private String courseId;
    private String titleString;
    private RelativeLayout toolbar;
    private LinearLayout activeParentLL;
    private LinearLayout upcomingParentLL;
    private LinearLayout missedParentLL;
    private LinearLayout completedParentLL;
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
        Log.e(TAG, getString(R.string.on_resume_is_called));
        networkCall();
    }

    private void networkCall() {
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
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            progress.dismiss();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            testQuizPojo = gson.fromJson(data.toString(), TestQuizPojo.class);
                            noDataLL.setVisibility(View.GONE);
                            extractSubjectData();
                            setupViewPager();
                            tabLayout.setupWithViewPager(viewPager);
                        } else {
                            progress.dismiss();
                            tabLayout.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.GONE);
                            noDataLL.setVisibility(View.VISIBLE);
                            noDataText.setText(getText(R.string.no_test_found));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progress.dismiss();
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(getText(R.string.no_test_found));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                if (t != null && t.getMessage() != null) {
                    if (t.getMessage().contains(Constants.BASE_DOMAIN_URL)) {
                        Toast.makeText(NewTestQuizActivity.this, getResources().getString(R.string.please_check_your_internet_connectivity), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewTestQuizActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (activeTestList.size() > 0) {
            activeTestList.clear();
        }


        Collections.addAll(activeTestList, testQuizPojo.getTest_series());

        if (activeTestList.size() > 0) {
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(activeTestList, courseId, getString(R.string.all), isPurchased, singleCourseData), getString(R.string.all));

            for (int i = 0; i < testQuizPojo.getMasters().length; i++) {
                if (list1.size() > 0) {
                    list1.clear();
                }
                for (int j = 0; j < testQuizPojo.getTest_series().length; j++) {

                    if (testQuizPojo.getTest_series()[j].getTopic_name().equalsIgnoreCase(testQuizPojo.getMasters()[i].getTest_type_title())) {
                        list1.add(testQuizPojo.getTest_series()[j]);
                    }
                }
                adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list1, courseId, "Others", isPurchased, singleCourseData), testQuizPojo.getMasters()[i].getTest_type_title());
            }

            viewPager.setAdapter(adapter);
        } else {
            tabLayout.setVisibility(View.GONE);
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.VISIBLE);
            noDataText.setText(getResources().getString(R.string.no_test_found));
            viewPager.setAdapter(adapter);
        }
    }

    private void initViews() {
        activeTestList = new ArrayList<>();
        subjectId = new ArrayList<>();

        noDataTV = findViewById(R.id.noDataTV);
        toolbar = findViewById(R.id.toolbar);
        activeParentLL = findViewById(R.id.activeParentLL);
        upcomingParentLL = findViewById(R.id.upcomingParentLL);
        missedParentLL = findViewById(R.id.missedParentLL);
        completedParentLL = findViewById(R.id.completedParentLL);
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
        if (getIntent()!=null) {
            courseId = getIntent().getStringExtra(Const.COURSE_ID);
            titleString = getIntent().getStringExtra(Const.TITLE);
            titleFrag = getIntent().getStringExtra(Constants.Extras.TITLE_FRAG);
            isPurchased = getIntent().getStringExtra(Constants.Extras.IS_PURCHASED);
            singleCourseData = (SingleCourseData) getIntent().getSerializableExtra(Const.COURSE_DESC);
            position = Objects.requireNonNull(getIntent().getExtras()).getInt("position");
        }
    }
}
