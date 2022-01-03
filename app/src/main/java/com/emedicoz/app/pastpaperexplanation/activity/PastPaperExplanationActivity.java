package com.emedicoz.app.pastpaperexplanation.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.pastpaperexplanation.adapter.PPEViewPagerAdapter;
import com.emedicoz.app.pastpaperexplanation.model.PPEData;
import com.emedicoz.app.pastpaperexplanation.model.Subject;
import com.emedicoz.app.pastpaperexplanation.model.Topic;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class PastPaperExplanationActivity extends AppCompatActivity implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    TabLayout tabLayout;
    ImageView ivBack;
    ImageView ivDrawer;
    TextView tvTitle;
    ViewPager viewPager;
    ArrayList<String> stringArrayList = new ArrayList<>();
    PPEViewPagerAdapter ppeViewPagerAdapter;
    MyNetworkCall myNetworkCall;
    public PPEData ppeData;
    public ArrayList<Topic> topicArrayList = new ArrayList<>();
    public ArrayList<Subject> subjectArrayList = new ArrayList<>();
    RelativeLayout mainLayout;
    TextView noDataTV;
    Button buyNowBtn;
    private static final String TAG = "PastPaperExplanationAct";
    String courseId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_past_paper_explanation);

        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        for (int i = 0; i < masterFeedsHitResponse.getStudy_info().size(); i++) {
            if (masterFeedsHitResponse.getStudy_info().get(i).getCourse_type().equals("1")) {
                courseId = masterFeedsHitResponse.getStudy_info().get(i).getCourse_id();
            }
        }
        // TEST DATA
        stringArrayList.add("Exam Wise");
        stringArrayList.add("Subject Wise");
        stringArrayList.add("About");

        initViews();
        hitApi();
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);
        mainLayout = findViewById(R.id.mainLayout);
        noDataTV = findViewById(R.id.noDataTV);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        ivDrawer = findViewById(R.id.iv_drawer);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        buyNowBtn = findViewById(R.id.buyNowBtn);

        ivBack.setOnClickListener(this);
        ivDrawer.setVisibility(View.GONE);

        tvTitle.setText("Past Paper Explanation");
    }

    private void hitApi() {
        myNetworkCall.NetworkAPICall(API.API_GET_PAST_PAPER_EXPLANATION, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apitype, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Constants.Extras.COURSE_ID, courseId);
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apitype, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        Gson gson = new Gson();
        if (jsonObject.optJSONObject(Const.DATA) != null && jsonObject.optJSONObject(Const.DATA).length() > 0) {
            ppeData = gson.fromJson(jsonObject.optJSONObject(Const.DATA).toString(), PPEData.class);
            mainLayout.setVisibility(View.VISIBLE);
            noDataTV.setVisibility(View.GONE);
            setBuyNowVisibility();
            if (ppeData.getCurriculam() != null) {
                if (!GenericUtils.isListEmpty(ppeData.getCurriculam().getTopics())) {
                    topicArrayList.clear();
                    topicArrayList.addAll(ppeData.getCurriculam().getTopics());
                }
                if (!GenericUtils.isListEmpty(ppeData.getCurriculam().getSubjects())) {
                    subjectArrayList.clear();
                    subjectArrayList.addAll(ppeData.getCurriculam().getSubjects());
                }
                setTab();
            }
        } else {
            mainLayout.setVisibility(View.GONE);
            noDataTV.setVisibility(View.VISIBLE);
        }
    }

    private void setBuyNowVisibility() {
        if (ppeData.getIsPurchased().equalsIgnoreCase("0")) {
            if (ppeData.getMrp().equalsIgnoreCase("0")) {
                buyNowBtn.setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (!ppeData.getForDams().equalsIgnoreCase("0")) {
                        buyNowBtn.setVisibility(View.VISIBLE);
                        buyNowBtn.setText(getString(R.string.enroll) + " (" + Helper.getCurrencySymbol() + " " + ppeData.getForDams() + ")");
                        buyNowBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                        buyNowBtn.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(this, getData(ppeData)));
                    } else {
                        buyNowBtn.setVisibility(View.GONE);
                    }
                } else {
                    if (!ppeData.getNonDams().equalsIgnoreCase("0")) {
                        buyNowBtn.setVisibility(View.VISIBLE);
                        buyNowBtn.setText(getString(R.string.enroll) + " (" + Helper.getCurrencySymbol() + " " + ppeData.getNonDams() + ")");
                        buyNowBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                        buyNowBtn.setOnClickListener(v -> Helper.goToCourseInvoiceScreen(this, getData(ppeData)));
                    } else {
                        buyNowBtn.setVisibility(View.GONE);
                    }
                }
            }
        } else if (ppeData.getIsPurchased().equalsIgnoreCase("1")) {
            buyNowBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void errorCallBack(String jsonstring, String apitype) {
        mainLayout.setVisibility(View.GONE);
        noDataTV.setVisibility(View.VISIBLE);
        noDataTV.setText(jsonstring);
    }

    private void setTab() {
        for (int k = 0; k < stringArrayList.size(); k++) {
            tabLayout.addTab(tabLayout.newTab().setText(stringArrayList.get(k)));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ppeViewPagerAdapter = new PPEViewPagerAdapter(getSupportFragmentManager(), stringArrayList, this);
        viewPager.setAdapter(ppeViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout.getTabCount() <= 3) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setBuyNowVisibility();
                } else {
                    buyNowBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }

    public SingleCourseData getData(PPEData ppeData) {
        SingleCourseData singleCourseData = new com.emedicoz.app.modelo.courses.SingleCourseData();
        singleCourseData.setCourse_type(ppeData.getCourseType());
        singleCourseData.setFor_dams(ppeData.getForDams());
        singleCourseData.setNon_dams(ppeData.getNonDams());
        singleCourseData.setMrp(ppeData.getMrp());
        singleCourseData.setId(ppeData.getId());
        singleCourseData.setCover_image(ppeData.getCoverImage());
        singleCourseData.setTitle(ppeData.getTitle());
        singleCourseData.setLearner(ppeData.getLearner());
        singleCourseData.setRating(ppeData.getRating());
        singleCourseData.setGst_include(ppeData.getGstInclude());
        singleCourseData.setGst(ppeData.getGst());
        singleCourseData.setPoints_conversion_rate(ppeData.getPointsConversionRate());
        singleCourseData.setIs_subscription(ppeData.getIsSubscription());
        singleCourseData.setIs_instalment(ppeData.getIsInstalment());
        singleCourseData.setInstallment(ppeData.getInstallment());
        if (ppeData.getChildCourses() != null)
            singleCourseData.setChild_courses(ppeData.getChildCourses());

        return singleCourseData;
    }
}
