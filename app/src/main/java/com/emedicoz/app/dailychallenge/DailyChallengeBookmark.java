package com.emedicoz.app.dailychallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dailychallenge.adapter.DCBookmarkParentAdapter;
import com.emedicoz.app.dailychallenge.model.DCBookmarkData;
import com.emedicoz.app.dailychallenge.model.DCBookmarkResponse;
import com.emedicoz.app.modelo.dvl.DVLTopic;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyChallengeBookmark#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyChallengeBookmark extends Fragment implements View.OnClickListener {

    Activity activity;
    TextView allBookmarkTitle;
    TextView totalBookmarkCount;
    ImageView frontRightIV;
    RecyclerView recyclerViewDCB;
    HashMap<Integer, ArrayList<DVLTopic>> hashMap;
    List<DCBookmarkData> dcBookmarkDataList = new ArrayList<>();
    String subjectId;

    public DailyChallengeBookmark() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DailyChallengeBookmark newInstance(String testSegmentId, String testSeriesId) {
        DailyChallengeBookmark fragment = new DailyChallengeBookmark();
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
        return inflater.inflate(R.layout.fragment_daily_challenge_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hashMap = new HashMap<>();
        initView(view);

    }

    @Override
    public void onResume() {
        super.onResume();

        networkCallForBookmarkSection();
    }

    private void initView(View view) {

        allBookmarkTitle = view.findViewById(R.id.allBookmarkTitle);
        totalBookmarkCount = view.findViewById(R.id.totalBookmarkCount);
        frontRightIV = view.findViewById(R.id.frontRightIV);
        recyclerViewDCB = view.findViewById(R.id.recyclerViewDCB);
        view.findViewById(R.id.study_all_bookmarks).setOnClickListener(v -> {

            Intent intent = new Intent(activity, CourseActivity.class);
            intent.putExtra(Const.FRAG_TYPE, Const.MY_BOOKMARKS);
            intent.putExtra(Constants.Extras.Q_TYPE_DQB, "3");
            activity.startActivity(intent);
        });

    }

    @Override
    public void onClick(View v) {
        //click handler
    }

    public void setAdapter() {
        recyclerViewDCB.setLayoutManager(new LinearLayoutManager(activity));
        DCBookmarkParentAdapter adapter = new DCBookmarkParentAdapter(activity, dcBookmarkDataList);
        recyclerViewDCB.setAdapter(adapter);
    }

    // method to get data for bookmark section
    private void networkCallForBookmarkSection() {
        if (activity.isFinishing()) return;

        final Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();

        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getDailyChallengeBookmarkList(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            DCBookmarkResponse dcBookmarkResponse = gson.fromJson(jsonObject, DCBookmarkResponse.class);
                            dcBookmarkDataList = dcBookmarkResponse.getData();
                            for (int i = 0; i < dcBookmarkDataList.size(); i++) {
                                for (int j = 0; j < dcBookmarkDataList.get(i).getSubject().size(); j++) {
                                    subjectId = dcBookmarkDataList.get(i).getSubject().get(j).getSubjectId();

                                }
                            }
                            setAdapter();
                        } else {
                            RetrofitResponse.getApiData(activity, API.API_GET_FILE_LIST_CURRICULUM);
                        }
                    } catch (JSONException e) {
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
}