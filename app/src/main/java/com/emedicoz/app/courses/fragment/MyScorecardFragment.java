package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.adapter.CommonListAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyScorecardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyScorecardFragment extends Fragment {

    public int previousTotalItemCount;
    RecyclerView commonListRV;
    Activity activity;
    int start = 0;
    ArrayList<ResultTestSeries> resultTestSeriesArrayList;
    LinearLayoutManager linearLayoutManager;
    CommonListAdapter commonListAdapter;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private int visibleThreshold = 5;

    public MyScorecardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyScorecardFragment newInstance() {
        /*        Bundle args = new Bundle();
        fragment.setArguments(args);*/
        return new MyScorecardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_scorecard, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
//        if (activity instanceof HomeActivity) {
//            ((HomeActivity) activity).toolbarHeader.setVisibility(View.GONE);
//            ((HomeActivity) activity).itemMyCartFragment.setVisible(false);
//            ((HomeActivity) activity).itemSavedNotesFragment.setVisible(false);
//            ((HomeActivity) activity).itemNotification.setVisible(false);
//            ActionBar actionBar = ((HomeActivity) activity).getSupportActionBar();
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resultTestSeriesArrayList = new ArrayList<>();
        commonListRV = view.findViewById(R.id.scorecardRV);
        linearLayoutManager = new LinearLayoutManager(activity);
        commonListRV.setLayoutManager(linearLayoutManager);
        networkCallForTestSeries(true);
        setPagination();
    }

    private void setPagination() {
        commonListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    Log.i("Yaeye!", "end called");
                    // Do something
                    start = start + 1;
                    networkCallForTestSeries(false);
                    loading = true;
                }

            }
        });
    }

    public void networkCallForTestSeries(final boolean status) {
        final Progress progress = new Progress(activity);
        progress.setCancelable(false);
        if (status)
            progress.show();
        Log.e("TestSeries: ", "start: " + start);
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getUserGivenTestSeries(SharedPreference.getInstance().getLoggedInUser().getId(), String.valueOf(start));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                JSONArray dataArray = null;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                       /* if (resultTestSeriesArrayList != null)
                            resultTestSeriesArrayList = new ArrayList<>();*/
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (status) {
                                resultTestSeriesArrayList.clear();
                            }
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObj = dataArray.optJSONObject(i);
                                ResultTestSeries resultTestSeries = gson.fromJson(dataObj.toString(), ResultTestSeries.class);
                                resultTestSeriesArrayList.add(resultTestSeries);
                            }

                        } else {
                            Log.e("onResponse: ", jsonResponse.optString(Constants.Extras.MESSAGE));
                            if (start == 0) {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
//                            Helper.showErrorLayoutForNoNav(API.API_GET_USER_GIVEN_TESTSERIES, activity, 1, 1);
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    initMyTestSeriesAdapter();
                    if (progress.isShowing())
                        progress.dismiss();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("onFailure: ", Objects.requireNonNull(t.getMessage()));
                if (progress.isShowing())
                    progress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_USER_GIVEN_TESTSERIES, activity, 1, 1);

            }
        });

    }

    private void initMyTestSeriesAdapter() {
        if (!GenericUtils.isListEmpty(resultTestSeriesArrayList)) {
            if (commonListAdapter == null) {
                commonListAdapter = new CommonListAdapter(activity, resultTestSeriesArrayList);
                commonListRV.setAdapter(commonListAdapter);
            } else {
                commonListAdapter.notifyDataSetChanged();
            }
            commonListRV.setVisibility(View.VISIBLE);
        } else {
            commonListRV.setVisibility(View.GONE);
        }

    }

}