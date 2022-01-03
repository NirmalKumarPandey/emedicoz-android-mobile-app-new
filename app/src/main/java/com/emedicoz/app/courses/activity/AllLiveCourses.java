package com.emedicoz.app.courses.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.ViewAllLCPracticeAdapter;
import com.emedicoz.app.modelo.courses.Course;
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
import java.util.Objects;

import retrofit2.Call;

public class AllLiveCourses extends AppCompatActivity implements MyNetworkCall.MyNetworkCallBack {

    RecyclerView viewAllDataRV;
    ViewAllLCPracticeAdapter adapter;
    ImageButton backVideos;
    String categoryInfoId;
    ArrayList<Course> courseArrayList = new ArrayList<>();
    Course coursesData;
    TextView toolbarTitle;
    private static final String TAG = "AllLiveCourses";
    MyNetworkCall myNetworkCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.all_live_courses);
        categoryInfoId = getIntent().getStringExtra(Constants.Extras.CATEGORY_INFO_ID);
        initViews();
        networkCallForAllCatData();
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);
        viewAllDataRV = findViewById(R.id.viewAllDataRV);
        backVideos = findViewById(R.id.toolbarBackIV);
        toolbarTitle = findViewById(R.id.toolbarTitleTV);

        bindControls();
    }

    private void bindControls() {
        toolbarTitle.setText(getResources().getString(R.string.live_course));
        backVideos.setOnClickListener(v -> onBackPressed());

        viewAllDataRV.setLayoutManager(new LinearLayoutManager(AllLiveCourses.this, RecyclerView.VERTICAL, false));
        adapter = new ViewAllLCPracticeAdapter(AllLiveCourses.this, courseArrayList);
        viewAllDataRV.setAdapter(adapter);
    }

    public void networkCallForAllCatData() {
        myNetworkCall.NetworkAPICall(API.API_GET_ALL_CATEGORY_DATA, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Const.CAT_ID, categoryInfoId);
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        JSONArray dataArray = data.optJSONArray(Const.COURSE_LIST);
        if (dataArray != null && dataArray.length() > 0) {
            for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
                coursesData = new Gson().fromJson(dataArray.opt(i).toString(), Course.class);
                courseArrayList.add(coursesData);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        GenericUtils.showToast(this, jsonString);
    }
}
