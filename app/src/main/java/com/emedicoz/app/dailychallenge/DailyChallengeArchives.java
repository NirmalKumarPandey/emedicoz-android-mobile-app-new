package com.emedicoz.app.dailychallenge;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dailychallenge.adapter.DCArchivesAdapter;
import com.emedicoz.app.dailychallenge.model.DCArchiveData;
import com.emedicoz.app.dialog.DailyQuizTestDialog;
import com.emedicoz.app.modelo.DailyQuizData;
import com.emedicoz.app.rating.GetQbankRating;
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
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyChallengeArchives#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyChallengeArchives extends Fragment implements View.OnClickListener {

    Activity activity;
    private List<DCArchiveData> dcArchiveDataList = new ArrayList<>();
    private int pageNo = 0;
    private RecyclerView archiveRecyclerView;
    private TextView noData;
    private boolean loading = false;
    private boolean loadMoreData = true;

    private DailyQuizTestDialog dailyQuizTestDialog;
    String daily_quiz_status;

    public DailyChallengeArchives() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DailyChallengeArchives newInstance(String testSegmentId, String testSeriesId) {
        DailyChallengeArchives fragment = new DailyChallengeArchives();
        Bundle args = new Bundle();
        args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
        args.putString(Const.TEST_SERIES_ID, testSeriesId);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_daily_challenge_archives, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        archiveRecyclerView = view.findViewById(R.id.archiveRecyclerView);
        noData = view.findViewById(R.id.noDataTV);

        archiveRecyclerView.setLayoutManager(new GridLayoutManager(activity, 5));
        DCArchivesAdapter dcArchivesAdapter = new DCArchivesAdapter(activity, dcArchiveDataList);
        archiveRecyclerView.setAdapter(dcArchivesAdapter);

        dailyQuizTestDialog = new DailyQuizTestDialog();
        daily_quiz_status = SharedPreference.getInstance().getDailyQuizStatus(Const.DAILY_QUIZ_TEST, "");
        System.out.println("daily_quiz_status-----------------------------"+daily_quiz_status);

        archiveRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {

                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int visibleItemCount = Objects.requireNonNull(layoutManager).findLastCompletelyVisibleItemPosition() + 1;
                    if (!loading && loadMoreData && visibleItemCount == layoutManager.getItemCount()) {
                        loading = true;
                        networkCallForArchiveSection();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dcArchiveDataList.clear();
        pageNo = 0;
        networkCallForArchiveSection();
        getQbankRating();
    }

    @Override
    public void onClick(View v) {
        //click handler
    }

    private void networkCallForArchiveSection() {          //method to get Data for Archives Section
        if (activity.isFinishing()) return;

        final Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getDailyQuizData(SharedPreference.getInstance().getLoggedInUser().getId(), String.valueOf(pageNo * 30));

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                loading = false;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray dataArray = GenericUtils.getJsonArray(jsonResponse);
                            pageNo++;

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObj = dataArray.optJSONObject(i);
                                DailyQuizData dailyQuizData = gson.fromJson(dataObj.toString(), DailyQuizData.class);
                                DCArchiveData dcArchiveData = new DCArchiveData();
                                dcArchiveData.setId(dailyQuizData.getId());
                                dcArchiveData.setTestSeriesId(dailyQuizData.getId());
                                dcArchiveData.setTestSeriesName(dailyQuizData.getTestSeriesName());
                                dcArchiveData.setCreationTime(dailyQuizData.getTestStartDate());

                                dcArchiveData.setDqTitle(dailyQuizData.getDqTitle());
                                dcArchiveData.setTestSegmentId(dailyQuizData.getSegmentId());
                                dcArchiveData.setUserRank(dailyQuizData.getSegmentId() != null && !dailyQuizData.getSegmentId().equalsIgnoreCase("0")
                                        ? dailyQuizData.getSegmentId() : "");
                                dcArchiveDataList.add(dcArchiveData);
                            }

                            noData.setVisibility(View.GONE);
                            archiveRecyclerView.setVisibility(View.VISIBLE);
                            Objects.requireNonNull(archiveRecyclerView.getAdapter()).notifyDataSetChanged();
                        } else {
                            loadMoreData = false;
                            noData.setVisibility(View.VISIBLE);
                            archiveRecyclerView.setVisibility(View.GONE);
                            RetrofitResponse.getApiData(activity, API.API_GET_FILE_LIST_CURRICULUM);
                        }
                    } catch (JSONException e) {
                        loadMoreData = false;
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getQbankRating(){
        if (!Helper.isConnected(getContext())) {
            Toast.makeText(getContext(), R.string.internet_error_message, Toast.LENGTH_SHORT).show();
        }
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        apiInterface.getQbankRating(SharedPreference.getInstance().getLoggedInUser().getId(), "apprating").enqueue(new Callback<GetQbankRating>() {
            @Override
            public void onResponse(Call<GetQbankRating> call, Response<GetQbankRating> response) {
                GetQbankRating getQbankRating = response.body();
                if (getQbankRating.getStatus().getStatuscode().equals("200")) {

                    if (getQbankRating.getData() != null) {
                        if (SharedPreference.getInstance().getLoggedInUser().getId().equals(getQbankRating.getData().getUser_id())) {
                            //qbankAppRating.dismiss();
                        }
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<GetQbankRating> call, Throwable t) {
                if (daily_quiz_status.equals("true")){
                    dailyQuizTestDialog.show(getChildFragmentManager(), "");
                }else {
                }
            }
        });
    }
}