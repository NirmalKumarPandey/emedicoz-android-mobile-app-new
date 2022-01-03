package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
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
public class NewCurriculumPlayerFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public ArrayList<String> curriculumStatus = new ArrayList<>();
    Progress mprogress;
    boolean refreshStatus = false;
    SwipeRefreshLayout swipeCurriculum;
    ArrayList<Curriculam> curriculamArrayList;
    SingleCourseData course;
    Activity activity;
    String frag_type = "", des = "";

    public NewCurriculumPlayerFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(SingleCourseData singleCourseData) {
        NewCurriculumPlayerFragment playerFragment = new NewCurriculumPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSE_DESC, singleCourseData);
        playerFragment.setArguments(bundle);
        return playerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        curriculamArrayList = new ArrayList<>();
        if (getArguments() != null) {
            frag_type = getArguments().getString(Const.FRAG_TYPE);
            course = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_curriculum_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);
        //        GenericUtils.manageScreenShotFeature(activity);

        networkCallForListCurriculum();
    }

    @Override
    public void onClick(View v) {

    }

    private void networkCallForListCurriculum() {
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getFileListCurriculum(SharedPreference.getInstance().getLoggedInUser().getId()
                , course.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            if (swipeCurriculum.isRefreshing())
                                swipeCurriculum.setRefreshing(false);
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (curriculamArrayList.size() > 0) curriculamArrayList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                Curriculam curriculam = gson.fromJson(dataObj.toString(), Curriculam.class);
                                curriculamArrayList.add(curriculam);
                            }

                            if (!curriculumStatus.contains(course.getId())) {
                                curriculumStatus.add(course.getId());
                                //Helper.getStorageInstance(getActivity()).addRecordStore(Const.CURRICULAM_IDS, curriculumStatus);
                            }

                            // if (Helper.getStorageInstance(getActivity()).getRecordObject(String.valueOf(course.getId()+"_curriculum")) == null || refreshStatus)
                            //  setAllDatatoView(curriculamArrayList);

                            // refreshStatus = false;

                            //Helper.getStorageInstance(getActivity()).addRecordStore(String.valueOf(course.getId()+"_curriculum"), curriculamArrayList);
                            // mprogress.dismiss();

                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_FILE_LIST_CURRICULUM);
                            RetrofitResponse.getApiData(activity, API.API_GET_FILE_LIST_CURRICULUM);
                            // mprogress.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_FILE_LIST_CURRICULUM, activity, 1, 1);
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_GET_FILE_LIST_CURRICULUM:
                /*errorTV.setVisibility(View.VISIBLE);
                errorTV.setText(jsonString);*/
                if (swipeCurriculum.isRefreshing()) {
                    swipeCurriculum.setRefreshing(false);
                    refreshStatus = false;
                }

                if (Helper.getStorageInstance(getActivity()).getRecordObject(course.getId() + "_curriculum") != null) {
                    curriculamArrayList = (ArrayList<Curriculam>) Helper.getStorageInstance(getActivity()).getRecordObject(course.getId() + "_curriculum");
                    //   setAllDatatoView(curriculamArrayList);

                } else {

                    if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                        Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

                    if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                        Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
                }
                break;
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
                if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

                if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
                break;

        }
    }
}
