package com.emedicoz.app.testmodule.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.emedicoz.app.R;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.testmodule.callback.FragmentChangeListener;
import com.emedicoz.app.testmodule.fragment.TestBookmarkFragment;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
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

public class TestBookmarkActivity extends AppCompatActivity implements FragmentChangeListener, View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    private static final String TAG = "TestBookmarkActivity";
    public ArrayList<QuestionBank> questionBookmarks = new ArrayList<>();
    String lastId = "";
    String subjectId = "";
    String testSeriesId = "";
    String qTypeDqb;
    String type = "", searchQuery = "";
    int position = 0;
    ImageView imgBack;
    FrameLayout btnNext, btnPrev;
    MyNetworkCall myNetworkCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_test_bookmark);

        position = getIntent().getIntExtra(Constants.Extras.POSITION, 0);
        subjectId = getIntent().getStringExtra(Constants.Extras.SUB_ID);
        if (getIntent().getStringExtra(Const.TESTSERIES_ID) != null)
            testSeriesId = getIntent().getStringExtra(Const.TESTSERIES_ID);
        qTypeDqb = getIntent().getStringExtra(Constants.Extras.Q_TYPE_DQB);
        type = getIntent().getStringExtra(Constants.Extras.TYPE);

        myNetworkCall = new MyNetworkCall(this, this);
        imgBack = findViewById(R.id.iv_back);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_prev);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        imgBack.setOnClickListener(view1 -> onBackPressed());

        switch (qTypeDqb) {
            case Constants.Type.TEST_SERIES:
                type = Helper.toUpperCase(Constants.TestType.TEST);
                break;
            case Constants.Type.DQB:
                type = Helper.toUpperCase(Constants.TestType.QUIZ);
                break;
            case Constants.Type.DAILY_CHALLENGE:
                type = Helper.toUpperCase(Constants.TestType.DAILY_CHALLENGE);
                break;
            default:
                break;
        }

        networkCallForTest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void networkCallForTest() {
        searchQuery = SharedPreference.getInstance().getString(Constants.SharedPref.SEARCHED_QUERY);
        if (searchQuery == null)
            searchQuery = "";

        myNetworkCall.NetworkAPICall(API.API_BOOKMARK_LIST, true);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_pager_bookmark, fragment, fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (position < questionBookmarks.size() - 1) {
                    position++;
                    replaceFragment(TestBookmarkFragment.newInstance(position));

                }
                break;
            case R.id.btn_prev:
                if (position > 0) {
                    position--;
                    replaceFragment(TestBookmarkFragment.newInstance(position));

                }
                break;
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Constants.Extras.STREAM, SharedPreference.getInstance().getString(Constants.Extras.STREAM_ID));
        params.put(Const.LAST_POST_ID, lastId);
        params.put(Constants.Extras.TAG_ID, subjectId);
        params.put(Const.SEARCH_TEXT, searchQuery);
        params.put(Const.TESTSERIES_ID, testSeriesId);
        params.put(Constants.Extras.Q_TYPE_DQB, qTypeDqb);

        if (!GenericUtils.isEmpty(testSeriesId) || !GenericUtils.isEmpty(qTypeDqb)) {
            params.put(Constants.Extras.TYPE, type);
        } else {
            params.put(Constants.Extras.TYPE, "TEST");
        }

        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONArray data = GenericUtils.getJsonArray(jsonObject);
        if (!questionBookmarks.isEmpty()) {
            questionBookmarks.clear();
        }
        for (int i = 0; i < data.length(); i++) {
            JSONObject dataObj = data.optJSONObject(i);
            QuestionBank questionBank = new Gson().fromJson(dataObj.toString(), QuestionBank.class);
            questionBookmarks.add(questionBank);
        }
        replaceFragment(TestBookmarkFragment.newInstance(position));
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        GenericUtils.showToast(this, jsonString);
    }
}
