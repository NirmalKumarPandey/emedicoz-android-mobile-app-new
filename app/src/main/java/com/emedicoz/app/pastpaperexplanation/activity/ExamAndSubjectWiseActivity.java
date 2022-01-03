package com.emedicoz.app.pastpaperexplanation.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.pastpaperexplanation.adapter.ExamParentAdapter;
import com.emedicoz.app.pastpaperexplanation.model.PPECategoryData;
import com.emedicoz.app.pastpaperexplanation.model.PPEData;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class ExamAndSubjectWiseActivity extends AppCompatActivity implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    ImageView ivBack;
    ImageView ivDrawer;
    TextView tvTitle;
    RecyclerView examRecyclerview;
    ExamParentAdapter examParentAdapter;
    MyNetworkCall myNetworkCall;
    String topicId = "";
    String subjectId = "";
    String type = "";
    PPECategoryData ppeCategoryData;
    ArrayList<PPECategoryData> ppeCategoryDataArrayList = new ArrayList<>();
    private static final String TAG = "ExamAndSubjectWiseActiv";
    PPEData ppeData;
    String courseId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_exam_and_subject_wise);
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();
        for (int i = 0; i < masterFeedsHitResponse.getStudy_info().size(); i++) {
            if (masterFeedsHitResponse.getStudy_info().get(i).getCourse_type().equals("1")) {
                courseId = masterFeedsHitResponse.getStudy_info().get(i).getCourse_id();
            }
        }
        getIntentData();
        initViews();
    }

    private void getIntentData() {
        topicId = getIntent().getStringExtra(Constants.Extras.TOPIC_ID);
        subjectId = getIntent().getStringExtra(Constants.Extras.SUBJECT_ID);
        type = getIntent().getStringExtra(Constants.Extras.TYPE);
        ppeData = (PPEData) getIntent().getSerializableExtra(Constants.PastPaperExtras.PPE_DATA);
        Log.e(TAG, "TOPIC_ID:- " + topicId);
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        ivDrawer = findViewById(R.id.iv_drawer);
        examRecyclerview = findViewById(R.id.examRecyclerview);

        tvTitle.setText("Past Paper Explanation");

        bindControls();
    }

    private void bindControls() {
        examRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        examParentAdapter = new ExamParentAdapter(this, ppeCategoryDataArrayList, ppeData);
        examRecyclerview.setAdapter(examParentAdapter);

        ivBack.setOnClickListener(this);
        ivDrawer.setVisibility(View.GONE);
    }

    private void hitApi() {
        if (type.equalsIgnoreCase(Constants.PastPaperExtras.EXAM_WISE))
            myNetworkCall.NetworkAPICall(API.API_GET_TEST_CATEGORY_WISE, true);
        else
            myNetworkCall.NetworkAPICall(API.API_GET_TEST_SUBJECT_WISE, true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        hitApi();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        switch (type) {
            case Constants.PastPaperExtras.EXAM_WISE:
                params.put(Constants.Extras.TOPIC_ID, topicId);
                break;
            case Constants.PastPaperExtras.SUBJECT_WISE:
                params.put(Constants.Extras.COURSE_ID, courseId);
                params.put(Constants.Extras.SUBJECT_ID, subjectId);
                break;
        }

        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONArray dataArray = GenericUtils.getJsonArray(jsonObject);

        if (!ppeCategoryDataArrayList.isEmpty()) {
            ppeCategoryDataArrayList.clear();
        }
        for (int i = 0; i < dataArray.length(); i++) {
            ppeCategoryData = new Gson().fromJson(dataArray.opt(i).toString(), PPECategoryData.class);
            ppeCategoryDataArrayList.add(ppeCategoryData);
        }
        examParentAdapter.notifyDataSetChanged();
        Log.e(TAG, "SUBJECT: " + ppeCategoryData.getSubject());
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {

    }
}
