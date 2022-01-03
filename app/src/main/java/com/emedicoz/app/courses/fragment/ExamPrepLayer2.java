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
import com.emedicoz.app.courses.adapter.ExamPrepLayer3Adapter;
import com.emedicoz.app.courses.model.VideoCourse;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Total;
import com.emedicoz.app.modelo.courses.ExamPrepItem;
import com.emedicoz.app.modelo.courses.Lists;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.modelo.courses.quiz.Quiz_Basic_Info;
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

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamPrepLayer2 extends Fragment {

    String contentType, title;
    RecyclerView examPrepLayerRV;
    Activity activity;
    TextView titleExamPrep;
    ArrayList<Quiz_Basic_Info> quizBasicInfoArrayList;
    ArrayList<VideoCourse> videoArrayList;
    ExamPrepLayer3Adapter examPrepLayer3Adapter;
    Lists list;
    Total total;
    TextView noDataTV;
    ExamPrepItem prevexamPrepItem;
    RelativeLayout parentLL;
    private SingleStudyModel singleStudy;

    public ExamPrepLayer2() {
        // Required empty public constructor
    }

    public static ExamPrepLayer2 newInstance(ExamPrepItem examPrepItem, Lists mainId, String contentType, String title, Total totals, SingleStudyModel singleStudy) {
        ExamPrepLayer2 examPrepLayer2 = new ExamPrepLayer2();
        Bundle args = new Bundle();
        args.putSerializable(Const.EXAMPREP, examPrepItem);
        args.putSerializable(Const.LIST, mainId);
        args.putSerializable(Constants.Extras.TEST_TYPE, totals);
        args.putString(Const.TITLE, title);
        args.putString(Const.CONTENT_TYPE, contentType);
        args.putSerializable(Const.SINGLE_STUDY, singleStudy);
        examPrepLayer2.setArguments(args);
        return examPrepLayer2;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_prep_layer2, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        quizBasicInfoArrayList = new ArrayList<>();
        videoArrayList = new ArrayList<>();
        if (getArguments() != null) {
            list = (Lists) getArguments().getSerializable(Const.LIST);
            total = (Total) getArguments().getSerializable(Constants.Extras.TEST_TYPE);
            contentType = getArguments().getString(Const.CONTENT_TYPE);
            title = getArguments().getString(Const.TITLE);
            prevexamPrepItem = (ExamPrepItem) getArguments().getSerializable(Const.EXAMPREP);
            singleStudy = (SingleStudyModel) getArguments().getSerializable(Const.SINGLE_STUDY);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (contentType.equals(Const.VIDEO))
            ((CourseActivity) activity).setToolbarTitle(list.getTitle());
        else if (!TextUtils.isEmpty(title))
            ((CourseActivity) activity).setToolbarTitle(title);

        initView(view);
    }

    private void initView(View view) {
        titleExamPrep = view.findViewById(R.id.titleExamPrep);
        noDataTV = view.findViewById(R.id.noDataTV);
        parentLL = view.findViewById(R.id.parentLL);
        examPrepLayerRV = view.findViewById(R.id.examPrepLayerRV);

        examPrepLayerRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
    }

    private void refreshDataList() {
        switch (contentType) {
            case Const.VIDEO:
                networkCallForGetVideoDataFirst();
                break;

            case Constants.TestType.TEST:
                networkCallForGetTestDataFirst();
                break;
            case Const.EPUB:
                networkCallForGetEpubDataFirst();
                break;
            case Const.PDF:
                networkCallForGetPdfDataFirst();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDataList();
    }

    private void networkCallForGetTestDataFirst() {
        if (Helper.isConnected(activity)) {
            final Progress progress = new Progress(activity);
            progress.setCancelable(false);
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;
            response = service.getTestData2(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "3",
                    list.getId());
            Log.e("API_GET_TEST_DATA: ", SharedPreference.getInstance().getString(Constants.Extras.ID) + "," + SharedPreference.getInstance().getString(Const.MAIN_ID) + "," + list.getId());

            assert response != null;
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progress.dismiss();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Gson gson = new Gson();
                            Log.e("TAG", jsonResponse.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                if (!quizBasicInfoArrayList.isEmpty())
                                    quizBasicInfoArrayList.clear();
                                Log.e("API_GET_TEST_DATA: ", "SUCCESS");
                                JSONObject data1 = jsonResponse.getJSONObject(Const.DATA);
                                for (int i = 0; i < data1.optJSONArray(Const.LIST).length(); i++) {
                                    Quiz_Basic_Info resultTestSeries = gson.fromJson(data1.optJSONArray(Const.LIST).get(i).toString(), Quiz_Basic_Info.class);
                                    quizBasicInfoArrayList.add(resultTestSeries);
                                }
                                initTestAdapter();

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
                    progress.dismiss();

                }
            });
        } else {

            Toast.makeText(activity, activity.getResources().getString(R.string.retry_with_internet), Toast.LENGTH_LONG).show();
        }

    }

    private void networkCallForGetVideoDataFirst() {
        if (Helper.isConnected(activity)) {
            final Progress progress = new Progress(activity);
            progress.setCancelable(false);
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;

            Log.e("API_GET_VIDEO_DATA: ", SharedPreference.getInstance().getString(Constants.Extras.ID) + "," + SharedPreference.getInstance().getString(Const.MAIN_ID) + "," + list.getId());
            response = service.getVideoData3(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "3", SharedPreference.getInstance().getString(Const.MAIN_ID), list.getId());

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
                                JSONObject data1 = jsonResponse.getJSONObject(Const.DATA);
                                if (videoArrayList == null) videoArrayList = new ArrayList<>();
                                else if (!videoArrayList.isEmpty())
                                    videoArrayList.clear();
                                for (int i = 0; i < Objects.requireNonNull(data1.optJSONArray(Const.LIST)).length(); i++) {
                                    com.emedicoz.app.courses.model.VideoCourse video = gson.fromJson(data1.optJSONArray(Const.LIST).get(i).toString(), com.emedicoz.app.courses.model.VideoCourse.class);
                                    videoArrayList.add(video);
                                }
                                initTestAdapter();
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
            final Progress progress = new Progress(activity);
            progress.setCancelable(false);
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;

            Log.e("API_GET_EPUB_DATA: ", SharedPreference.getInstance().getString(Constants.Extras.ID) + "," + SharedPreference.getInstance().getString(Const.MAIN_ID) + "," + list.getId());
            response = service.getEpubData3(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "3", SharedPreference.getInstance().getString(Const.MAIN_ID), list.getId());

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
                                JSONObject data1 = jsonResponse.getJSONObject(Const.DATA);
                                if (videoArrayList == null) videoArrayList = new ArrayList<>();
                                else if (!videoArrayList.isEmpty())
                                    videoArrayList.clear();
                                for (int i = 0; i < Objects.requireNonNull(data1.optJSONArray(Const.LIST)).length(); i++) {
                                    com.emedicoz.app.courses.model.VideoCourse video = gson.fromJson(data1.optJSONArray(Const.LIST).get(i).toString(), com.emedicoz.app.courses.model.VideoCourse.class);
                                    videoArrayList.add(video);
                                }
                                initTestAdapter();
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

    private void networkCallForGetPdfDataFirst() {
        if (Helper.isConnected(activity)) {
            final Progress progress = new Progress(activity);
            progress.setCancelable(false);
            if (activity.isFinishing()) return;
            progress.show();

            LiveCourseInterface service = ApiClient.createService(LiveCourseInterface.class);
            Call<JsonObject> response = null;

            Log.e("API_GET_PDF_DATA: ", SharedPreference.getInstance().getString(Constants.Extras.ID) + "," + SharedPreference.getInstance().getString(Const.MAIN_ID) + "," + list.getId());
            response = service.getPdfData3(
                    SharedPreference.getInstance().getLoggedInUser().getId(),
                    SharedPreference.getInstance().getString(Constants.Extras.ID),
                    "3", SharedPreference.getInstance().getString(Const.MAIN_ID), list.getId());

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
                                JSONObject data1 = jsonResponse.getJSONObject(Const.DATA);
                                if (videoArrayList == null) videoArrayList = new ArrayList<>();
                                else if (!videoArrayList.isEmpty())
                                    videoArrayList.clear();
                                for (int i = 0; i < Objects.requireNonNull(data1.optJSONArray(Const.LIST)).length(); i++) {
                                    com.emedicoz.app.courses.model.VideoCourse video = gson.fromJson(data1.optJSONArray(Const.LIST).get(i).toString(), com.emedicoz.app.courses.model.VideoCourse.class);
                                    videoArrayList.add(video);
                                }
                                initTestAdapter();
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

    private void initTestAdapter() {
        if (parentLL.getVisibility() == View.GONE) parentLL.setVisibility(View.VISIBLE);
        if (((CourseActivity) activity).contentType.equals(Const.VIDEO)) {
            if (!videoArrayList.isEmpty()) {
                titleExamPrep.setVisibility(View.GONE);
                parentLL.setVisibility(View.VISIBLE);
                noDataTV.setVisibility(View.GONE);
                examPrepLayer3Adapter = new ExamPrepLayer3Adapter(activity, videoArrayList, ((CourseActivity) activity).contentType, singleStudy);
            } else {
                noDataTV.setVisibility(View.VISIBLE);
                noDataTV.setText("No Videos Found.");
                parentLL.setVisibility(View.GONE);
            }
        } else if (((CourseActivity) activity).contentType.equals(Constants.TestType.TEST)) {
            if (titleExamPrep.getVisibility() == View.GONE)
                titleExamPrep.setVisibility(View.VISIBLE);
            if (total != null)
                titleExamPrep.setText(total.getText());
            else
                titleExamPrep.setText(list.getTitle());
            examPrepLayer3Adapter = new ExamPrepLayer3Adapter(activity, quizBasicInfoArrayList, singleStudy);
        } else {
            if (titleExamPrep.getVisibility() == View.GONE)
                titleExamPrep.setVisibility(View.VISIBLE);
            titleExamPrep.setText(list.getTitle());
            examPrepLayer3Adapter = new ExamPrepLayer3Adapter(activity, videoArrayList, ((CourseActivity) activity).contentType, singleStudy);

        }
        examPrepLayerRV.setAdapter(examPrepLayer3Adapter);
    }
}
