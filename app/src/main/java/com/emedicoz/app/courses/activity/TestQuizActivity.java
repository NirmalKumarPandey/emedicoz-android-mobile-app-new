package com.emedicoz.app.courses.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.ViewPagerAdapter;
import com.emedicoz.app.courses.fragment.ChildTestQuizCourseFrag;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Masters;
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
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestQuizActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TestQuizActivity";
    public int lastPos;
    public String titlefrag;
    public ImageView index;
    String is_purchased;
    ArrayList<TestSeries> completed = new ArrayList<>();
    ArrayList<TestSeries> unattempted = new ArrayList<>();
    ArrayList<TestSeries> paused = new ArrayList<>();
    LinearLayout noDataLL;
    ImageView noDataImage;
    TextView noDataText;
    SingleCourseData singleCourseData;
    private String courseId;
    private RelativeLayout toolbar;
    private LinearLayout activeParentLL;
    private LinearLayout upcomingParentLL;
    private LinearLayout missedParentLL;
    private LinearLayout completedParentLL;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TestQuizPojo testQuizPojo;
    private ArrayList<TestSeries> testSeriesByCourse;
    private ArrayList<String> topicList;
    private ArrayList<Masters> masters;
    private TextView noDataTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_test_quiz);
        Log.e(TAG, "onCreate: TestQuizActivity");
        SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
        getIntentData();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkCall();
    }

    private void initViews() {
        masters = new ArrayList<>();
        testSeriesByCourse = new ArrayList<>();
        topicList = new ArrayList<>();

        noDataTV = findViewById(R.id.noDataTV);
        toolbar = findViewById(R.id.relativeToolbar);
        activeParentLL = findViewById(R.id.activeParentLL);
        upcomingParentLL = findViewById(R.id.upcomingParentLL);
        missedParentLL = findViewById(R.id.missedParentLL);
        completedParentLL = findViewById(R.id.completedParentLL);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        index = findViewById(R.id.index);
        noDataText = findViewById(R.id.noDataText);
        noDataImage = findViewById(R.id.noDataImage);
        noDataLL = findViewById(R.id.noDataLL);
        index.setVisibility(View.GONE);
        TextView title = toolbar.findViewById(R.id.title);
        ImageButton backIV = toolbar.findViewById(R.id.backIV);
        title.setText(titlefrag);

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
        titlefrag = getIntent().getStringExtra("titlefrag");
        is_purchased = getIntent().getStringExtra("is_purchased");
        singleCourseData = (SingleCourseData) getIntent().getSerializableExtra(Const.COURSE_DESC);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backIV:
                onBackPressed();
                break;
            default:
        }
    }

    private void networkCall() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress progress = new Progress(this);
        progress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getTestData(SharedPreference.getInstance().getLoggedInUser().getId(),
                courseId); //courseId
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    progress.dismiss();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            testQuizPojo = gson.fromJson(data.toString(), TestQuizPojo.class);
                            extractData();
                            extractTopics();
                            extractMastersData();
                            setupViewPager(testSeriesByCourse, topicList);
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                // Helper.showErrorLayoutForNoNav(API.API_SINGLE_COURSE_INFO_RAW, activity, 1, 1);
            }
        });
    }

    private void extractTopics() {
        if (!topicList.isEmpty()) {
            topicList.clear();
        }
        for (int i = 0; i < testSeriesByCourse.size(); i++) {
            if (!topicList.contains(testSeriesByCourse.get(i).getTopic())) {
                topicList.add(testSeriesByCourse.get(i).getTopic());
            }
        }
        Log.e("extractTopics: ", String.valueOf(topicList.size()));
    }

    private void extractData() {
        if (!testSeriesByCourse.isEmpty()) {
            testSeriesByCourse.clear();
        }
        for (int i = 0; i < testQuizPojo.getTest_series().length; i++) {
            //  TestSeries test_series = testQuizPojo.getTest_series()[i];
            //  allTestList.add(test_series);
            if (testQuizPojo.getTest_series()[i].getTopic_name().equalsIgnoreCase(titlefrag)) {
                testSeriesByCourse.add(testQuizPojo.getTest_series()[i]);
            }
        }
    }

    private void extractMastersData() {
        if (!masters.isEmpty()) {
            masters.clear();
        }
        masters.addAll(Arrays.asList(testQuizPojo.getMasters()));
    }

    private void setupViewPager(ArrayList<TestSeries> list, ArrayList<String> topicList) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (!list.isEmpty()) {
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            adapter.addFragment(ChildTestQuizCourseFrag.getInstance(list, topicList, courseId, getString(R.string.all), is_purchased, singleCourseData), getString(R.string.all));

            addPausedTab(list, adapter);
            addCompletedTab(list, adapter);
            addUnattemptedTab(list, adapter);


        } else {
            tabLayout.setVisibility(View.GONE);
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.VISIBLE);
            noDataText.setText("In this subject no quiz is available");
        }
        viewPager.setAdapter(adapter);
    }

    private void addCompletedTab(ArrayList<TestSeries> list, ViewPagerAdapter adapter) {
        if (!completed.isEmpty()) {
            completed.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIs_paused().equalsIgnoreCase("0")) {
                completed.add(list.get(i));
            }
        }
        // if (completed.size() > 0) {
        adapter.addFragment(ChildTestQuizCourseFrag.getInstance(completed, topicList, courseId, "Completed", is_purchased, singleCourseData), "Completed");
        // }
    }

    private void addUnattemptedTab(ArrayList<TestSeries> list, ViewPagerAdapter adapter) {
        if (!unattempted.isEmpty()) {
            unattempted.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIs_user_attemp().equalsIgnoreCase("")) {
                unattempted.add(list.get(i));
            }
        }
        // if (unattempted.size() > 0) {
        adapter.addFragment(ChildTestQuizCourseFrag.getInstance(unattempted, topicList, courseId, "Unattempted", is_purchased, singleCourseData), "Unattempted");
        // }
    }

    private void addPausedTab(ArrayList<TestSeries> list, ViewPagerAdapter adapter) {

        if (!paused.isEmpty()) {
            paused.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIs_paused().equalsIgnoreCase("1")) {
                paused.add(list.get(i));
            }
        }
        // if (paused.size() > 0) {
        adapter.addFragment(ChildTestQuizCourseFrag.getInstance(paused, topicList, courseId, "Paused", is_purchased, singleCourseData), "Paused");
        // }
    }

    private ArrayList<TestSeries> getTestSeriesData(ArrayList<TestSeries> parentList, int pos) {
        ArrayList<TestSeries> list = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            if (parentList.get(i).getTopic_name().equalsIgnoreCase(testQuizPojo.getMasters()[pos].getTest_type_title())) {
                list.add(parentList.get(i));
            }
        }
        return list;
    }
}
