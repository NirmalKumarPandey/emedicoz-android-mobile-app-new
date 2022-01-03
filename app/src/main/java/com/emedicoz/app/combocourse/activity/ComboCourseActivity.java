package com.emedicoz.app.combocourse.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.emedicoz.app.R;
import com.emedicoz.app.combocourse.adapter.ComboCourseAdapter;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
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

public class ComboCourseActivity extends AppCompatActivity implements MyNetworkCall.MyNetworkCallBack {

    private static final String TAG = "ComboCourseActivity";
    ImageView ivBack, ivDrawer;
    MyNetworkCall myNetworkCall;
    ArrayList<SingleCourseData> comboCourseArrayList = new ArrayList<>();
    ComboCourseAdapter comboCourseAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    SingleCourseData comboCourse;
    private RecyclerView recyclerViewComboCourse;
    private TextView tvTitle, tvError;
    private String courseId;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_course);

        initViews();
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);

        recyclerViewComboCourse = findViewById(R.id.recyclerView_combo_course);
        recyclerViewComboCourse.setLayoutManager(new LinearLayoutManager(this));
        comboCourseAdapter = new ComboCourseAdapter(comboCourseArrayList, this);
        recyclerViewComboCourse.setAdapter(comboCourseAdapter);
        recyclerViewComboCourse.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvError = findViewById(R.id.tv_error);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        ivDrawer = findViewById(R.id.iv_drawer);

        tvTitle.setText(getResources().getString(R.string.buy_now));

        ivDrawer.setVisibility(View.GONE);

        ivBack.setOnClickListener(v -> onBackPressed());
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(() -> hitApi(false));
    }


    @Override
    protected void onResume() {
        super.onResume();
        hitApi(true);
    }

    private void hitApi(boolean showLoader) {
        myNetworkCall.NetworkAPICall(API.API_COMBO_PACKAGE, showLoader);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        switch (apiType) {
            case API.API_COMBO_PACKAGE:

                break;
            case API.API_MAKE_FREE_COURSE_TRANSACTION:
                params.put(Const.COURSE_ID, courseId);
                params.put(Const.POINTS_RATE, "0");
                params.put(Const.TAX, "0");
                params.put(Const.POINTS_USED, "0");
                params.put(Const.COURSE_PRICE, "0");
                break;
            default:
                break;
        }

        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        switch (apiType) {
            case API.API_COMBO_PACKAGE:
                swipeRefreshLayout.setRefreshing(false);
                JSONObject dataJsonObject = jsonObject.optJSONObject(Const.DATA);
                JSONArray jsonArray = Objects.requireNonNull(dataJsonObject).optJSONArray(Const.PACKAGES);
                if (jsonArray != null && jsonArray.length() != 0) {
                    tvError.setVisibility(View.GONE);
                    recyclerViewComboCourse.setVisibility(View.VISIBLE);
                    comboCourseArrayList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        comboCourse = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), SingleCourseData.class);
                        comboCourse.setGst(dataJsonObject.optString(Const.GST));
                        comboCourse.setPoints_conversion_rate(dataJsonObject.optString(Const.POINTS_CONVERSION_RATE));
                        comboCourseArrayList.add(comboCourse);
                    }

                    comboCourseAdapter.notifyDataSetChanged();
                } else {
                    errorCallBack(jsonObject.optString(Constants.Extras.MESSAGE), apiType);
                }
                break;
            case API.API_MAKE_FREE_COURSE_TRANSACTION:
                GenericUtils.showToast(this, "Subscribed Successfully");
                comboCourseArrayList.get(position).setIs_purchased("1");
                comboCourseAdapter.notifyItemChanged(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        swipeRefreshLayout.setRefreshing(false);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(jsonString);
        recyclerViewComboCourse.setVisibility(View.GONE);
    }

    public void freeCourseTransaction(String courseId, int position) {
        this.courseId = courseId;
        this.position = position;
        myNetworkCall.NetworkAPICall(API.API_MAKE_FREE_COURSE_TRANSACTION, true);
    }
}
