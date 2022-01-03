package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.ExamPrepLayer1Adapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.ExamPrepItem;
import com.emedicoz.app.modelo.courses.Lists;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamPrepLayer1 extends Fragment {

    String contentType, title;
    Progress progress;
    RecyclerView examPrepLayerRV;
    RelativeLayout parentLL;
    Activity activity;
    TextView titleExamPrep;
    Lists list;
    ExamPrepItem prevexamPrepItem, examPrepItem;
    ExamPrepLayer1Adapter examPrepLayer1Adapter;
    private SingleStudyModel singlestudyModel;

    public static ExamPrepLayer1 newInstance(ExamPrepItem examPrepItem, Lists mainId, String contentType, String title, SingleStudyModel singlestudyModel) {
        ExamPrepLayer1 examPrepLayer1 = new ExamPrepLayer1();
        Bundle args = new Bundle();
        args.putSerializable(Const.EXAMPREP, examPrepItem);
        args.putSerializable(Const.SINGLE_STUDY, singlestudyModel);
        args.putSerializable(Const.LIST, mainId);
        args.putString(Const.TITLE, title);
        args.putString(Const.CONTENT_TYPE, contentType);
        examPrepLayer1.setArguments(args);
        return examPrepLayer1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        if (getArguments() != null) {
            list = (Lists) getArguments().getSerializable(Const.LIST);
            contentType = getArguments().getString(Const.CONTENT_TYPE);
            title = getArguments().getString(Const.TITLE);
            prevexamPrepItem = (ExamPrepItem) getArguments().getSerializable(Const.EXAMPREP);
            singlestudyModel = (SingleStudyModel) getArguments().getSerializable(Const.SINGLE_STUDY);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        if (!TextUtils.isEmpty(title))
            ((CourseActivity) activity).setToolbarTitle(title);

        refreshDataList();
    }

    private void initView(View view) {
        titleExamPrep = view.findViewById(R.id.titleExamPrep);
        parentLL = view.findViewById(R.id.parentLL);
        examPrepLayerRV = view.findViewById(R.id.examPrepLayerRV);

        examPrepLayerRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_prep_layer1, container, false);

    }

    private void refreshDataList() {
        switch (contentType) {
            case Const.VIDEO:
                networkCallForGetVideoDataFirst();
                break;

            case Const.EPUB:
                networkCallForGetEpubDataFirst();
                break;

            case Const.PDF:
                networkCallForGetPdfDataFirst();
                break;
        }
    }


    private void networkCallForGetVideoDataFirst() {
        if (Helper.isConnected(activity)) {
            progress = new Progress(activity);
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;
            response = service.getVideoData2(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "2",
                    list.getId());

            assert response != null;
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progress.hide();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Gson gson = new Gson();
                            Log.e("TAG", jsonResponse.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                examPrepItem = gson.fromJson(jsonResponse.optString(Const.DATA), ExamPrepItem.class);
                                initListAdapter(examPrepItem);

                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progress.hide();
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {

            Toast.makeText(activity, activity.getResources().getString(R.string.retry_with_internet), Toast.LENGTH_LONG).show();
        }

    }


    private void networkCallForGetEpubDataFirst() {
        if (Helper.isConnected(activity)) {
            progress = new Progress(activity);
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;
            response = service.getEpubData2(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "2",
                    list.getId());

            assert response != null;
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progress.hide();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Gson gson = new Gson();
                            Log.e("TAG", jsonResponse.toString());

                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                examPrepItem = gson.fromJson(jsonResponse.optString(Const.DATA), ExamPrepItem.class);
                                initListAdapter(examPrepItem);
                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progress.hide();
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {

            Toast.makeText(activity, activity.getResources().getString(R.string.internet_error_message), Toast.LENGTH_LONG).show();
        }

    }

    private void networkCallForGetPdfDataFirst() {
        if (Helper.isConnected(activity)) {
            progress = new Progress(activity);
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;
            response = service.getPdfData2(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "2",
                    list.getId());

            assert response != null;
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progress.hide();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Gson gson = new Gson();
                            Log.e("TAG", jsonResponse.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                examPrepItem = gson.fromJson(jsonResponse.optString(Const.DATA), ExamPrepItem.class);
                                initListAdapter(examPrepItem);

                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progress.hide();
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {

            Toast.makeText(activity, activity.getResources().getString(R.string.retry_with_internet), Toast.LENGTH_LONG).show();
        }

    }


    public void initListAdapter(ExamPrepItem examPrepItem) {
        if (parentLL.getVisibility() == View.GONE) parentLL.setVisibility(View.VISIBLE);
        titleExamPrep.setText(list.getTitle());
        examPrepLayer1Adapter = new ExamPrepLayer1Adapter(activity, examPrepItem, ExamPrepLayer1.this, singlestudyModel);
        examPrepLayerRV.setAdapter(examPrepLayer1Adapter);
    }
}
