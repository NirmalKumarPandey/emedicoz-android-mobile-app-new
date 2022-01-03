package com.emedicoz.app.courses.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.SubjectWiseResultAdapter;
import com.emedicoz.app.modelo.SubjectWiseResultData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
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
public class SubjectWiseQuizResult extends Fragment {

    Activity activity;
    RecyclerView viewSubjectWiseRV;
    SubjectWiseResultAdapter adapter;
    ArrayList<SubjectWiseResultData> subjectWiseResults;
    String status;

    public SubjectWiseQuizResult() {
        // Required empty public constructor
    }

    public static SubjectWiseQuizResult newInstance(String status) {
        SubjectWiseQuizResult subjectWiseQuizResult = new SubjectWiseQuizResult();
        Bundle args = new Bundle();
        args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, status);
        subjectWiseQuizResult.setArguments(args);
        return subjectWiseQuizResult;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        subjectWiseResults = new ArrayList<>();

        if (getArguments() != null) {
            status = getArguments().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subject_wise_quiz_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewSubjectWiseRV = view.findViewById(R.id.viewSubjectWiseRV);
        viewSubjectWiseRV.setLayoutManager(new LinearLayoutManager(activity));

        netoworkCallSubjectResult();
    }

    private void netoworkCallSubjectResult() {

        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.getSubjectWiseResult(SharedPreference.getInstance().getLoggedInUser().getId(), status);
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            for (int i = 0; i < data.length(); i++) {
                                SubjectWiseResultData result = gson.fromJson(data.optJSONObject(i).toString(), SubjectWiseResultData.class);
                                subjectWiseResults.add(result);
                            }
                        } else {
                            Helper.showErrorLayoutForNoNav(API.API_GET_USER_LEADERBOARD_RESULT, activity, 1, 2);

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showErrorLayoutForNoNav(API.API_GET_USER_LEADERBOARD_RESULT, activity, 1, 1);
            }
        });
    }
}
