package com.emedicoz.app.feeds.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.adapter.DailyQuizAdapter;
import com.emedicoz.app.modelo.DailyQuizData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyQuizFragment extends Fragment {

    RecyclerView dailyQuizRV;
    Activity activity;
    DailyQuizAdapter adapter;
    Progress mProgress;
    LinearLayoutManager linearLayoutManager;
    ArrayList<DailyQuizData> dataArrayList;
    String lastId = "0";

    public DailyQuizFragment() {
        // Required empty public constructor
    }

    public static DailyQuizFragment newInstance() {

        return new DailyQuizFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public void onResume() {
        super.onResume();
        networkCallForDailyQuiz();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        dailyQuizRV = view.findViewById(R.id.dailyQuizRV);
        linearLayoutManager = new LinearLayoutManager(activity);
        dailyQuizRV.setLayoutManager(linearLayoutManager);
    }

    public void networkCallForDailyQuiz() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getDailyQuizData(SharedPreference.getInstance().getLoggedInUser().getId(), lastId);
        Log.e("LAST_ID: ", lastId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                Gson gson = new Gson();
                JSONArray dataArray;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            dataArrayList = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObj = dataArray.optJSONObject(i);
                                DailyQuizData dailyQuizData = gson.fromJson(dataObj.toString(), DailyQuizData.class);
                                dataArrayList.add(dailyQuizData);
                            }
                            adapter = new DailyQuizAdapter(activity, dataArrayList);
                            dailyQuizRV.setAdapter(adapter);

                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });

    }
}
