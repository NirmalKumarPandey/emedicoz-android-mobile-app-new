package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.adapter.LCPracticeRV1Adapter;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
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

public class LCPracticeChildFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    RecyclerView recyclerView1, recyclerView2;
    CardView cardView;
    Activity activity;
    RelativeLayout mainParentLL;
    private ArrayList<CoursesData> coursesDataArrayList = new ArrayList<>();
    private LCPracticeRV1Adapter adapter1;
    MyNetworkCall myNetworkCall;
    private static final String TAG = "LCPracticeChildFragment";

    public static LCPracticeChildFragment newInstance() {
        Bundle args = new Bundle();
        LCPracticeChildFragment fragment = new LCPracticeChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Log.e("onCreate: ", "onCreate method of LCPracticeChildFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ibt_fragment_practice_child, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof BaseABNavActivity) {
            ((BaseABNavActivity) activity).setNotificationVisibility(false);
        }
        networkCallForLandingDataExam();
    }

    private void initView(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        recyclerView1 = view.findViewById(R.id.fragment_practice_child_RV1);
        recyclerView2 = view.findViewById(R.id.fragment_practice_child_RV2);
        cardView = view.findViewById(R.id.fragment_practice_child_CV);
        mainParentLL = view.findViewById(R.id.mainParentLL);

        recyclerView1.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(activity));
        adapter1 = new LCPracticeRV1Adapter(activity, coursesDataArrayList);
        recyclerView2.setAdapter(adapter1);
    }

    private void networkCallForLandingDataExam() {
        myNetworkCall.NetworkAPICall(API.API_GET_LIVE_COURSE_LANDING_PAGE_DATA, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONArray dataArray = jsonObject.optJSONArray(Const.DATA);
        if (dataArray != null && dataArray.length() > 0) {
            coursesDataArrayList.clear();
            mainParentLL.setVisibility(View.VISIBLE);
            for (int i = 0; i < dataArray.length(); i++) {
                CoursesData coursesData = new Gson().fromJson(dataArray.get(i).toString(), CoursesData.class);
                coursesDataArrayList.add(coursesData);
            }
            adapter1.notifyDataSetChanged();
        } else
            Helper.showErrorLayoutForNav(API.API_GET_LIVE_COURSE_LANDING_PAGE_DATA, activity, 1, 0);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Helper.showErrorLayoutForNav(API.API_GET_LIVE_COURSE_LANDING_PAGE_DATA, activity, 1, 0);
        mainParentLL.setVisibility(View.GONE);
    }
}
