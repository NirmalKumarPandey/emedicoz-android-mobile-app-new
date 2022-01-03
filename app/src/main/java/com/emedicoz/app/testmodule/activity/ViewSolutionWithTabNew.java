package com.emedicoz.app.testmodule.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.emedicoz.app.R;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.testmodule.callback.FragmentChangeListener;
import com.emedicoz.app.testmodule.fragment.ViewSolutionWithTabFragment;
import com.emedicoz.app.testmodule.model.ViewSolutionData;
import com.emedicoz.app.testmodule.model.ViewSolutionResult;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class ViewSolutionWithTabNew extends AppCompatActivity implements FragmentChangeListener, View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    private static final String TAG = "ViewSolutionWithTabNew";
    public ArrayList<ViewSolutionResult> viewSolutionResultSkipped;
    public ArrayList<ViewSolutionResult> viewSolutionResultCorrect;
    public ArrayList<ViewSolutionResult> viewSolutionResultIncorrect;
    public ArrayList<ViewSolutionResult> viewSolutionResultBookmarked;
    public ViewSolutionData viewSolutionData;
    public ImageView backIV, index;
    String testSegmentId, testSeriesName;
    boolean custom = false;
    HorizontalScrollView scrollView;
    TextView title;
    String markingScheme;
    TextView allTV;
    TextView correctTV;
    TextView incorrectTV;
    TextView skippedTV;
    TextView bookmarkedTV;
    TextView filterTV;
    View allView;
    View correctView;
    View incorrectView;
    View skippedView;
    View bookmarkedView;
    ArrayList<String> topics;
    int fromBookmark;
    MyNetworkCall myNetworkCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_view_solution_with_tab_new);

        myNetworkCall = new MyNetworkCall(this, this);

        viewSolutionResultSkipped = new ArrayList<>();
        viewSolutionResultCorrect = new ArrayList<>();
        viewSolutionResultIncorrect = new ArrayList<>();
        viewSolutionResultBookmarked = new ArrayList<>();
        topics = new ArrayList<>();

        topics.add(Constants.TestStatus.ALL);
        topics.add(Constants.TestStatus.CORRECT);
        topics.add(Constants.TestStatus.INCORRECT);
        topics.add(Constants.TestStatus.SKIPPED);
        topics.add(Constants.TestStatus.BOOKMARKED);
        getBundleData();

        title = findViewById(R.id.title);
        scrollView = findViewById(R.id.scrollView);
        allTV = findViewById(R.id.allTV);
        correctTV = findViewById(R.id.correctTV);
        filterTV = findViewById(R.id.filterTV);
        incorrectTV = findViewById(R.id.incorrectTV);
        index = findViewById(R.id.index);
        skippedTV = findViewById(R.id.skippedTV);
        bookmarkedTV = findViewById(R.id.bookmarkedTV);
        allView = findViewById(R.id.viewAll);
        correctView = findViewById(R.id.viewCorrect);
        incorrectView = findViewById(R.id.viewIncorrect);
        skippedView = findViewById(R.id.viewSkipped);
        bookmarkedView = findViewById(R.id.viewBookmarked);
        backIV = findViewById(R.id.backIV);
        index.setVisibility(View.VISIBLE);

        allTV.setOnClickListener(this);
        correctTV.setOnClickListener(this);
        incorrectTV.setOnClickListener(this);
        skippedTV.setOnClickListener(this);
        filterTV.setOnClickListener(this);
        bookmarkedTV.setOnClickListener(this);
        backIV.setOnClickListener(this);
        filterTV.setVisibility(View.GONE);

        if (markingScheme != null && markingScheme.equals("2")) {
            scrollView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.VISIBLE);
        }

        if (testSeriesName != null && !testSeriesName.equalsIgnoreCase(""))
            title.setText(testSeriesName);
        else
            title.setText(Const.SOLUTION);
        getData(0);
    }

    private void getBundleData() {
        testSegmentId = getIntent().getStringExtra(Constants.ResultExtras.TEST_SEGMENT_ID);
        testSeriesName = getIntent().getStringExtra(Constants.Extras.NAME);
        custom = getIntent().getBooleanExtra(Constants.Extras.CUSTOM, custom);
        markingScheme = getIntent().getStringExtra(Const.MARKING_SCHEME);
        Log.e(TAG, "getBundleData: custom - " + custom + ", test_segment_id - " + testSegmentId);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allTV:
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, getString(R.string.all)));
                showView(getString(R.string.all));

                break;
            case R.id.correctTV:
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, Constants.TestStatus.CORRECT));
                showView(Constants.TestStatus.CORRECT);
                break;
            case R.id.incorrectTV:
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, Constants.TestStatus.INCORRECT));
                showView(Constants.TestStatus.INCORRECT);
                break;
            case R.id.skippedTV:
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, Constants.TestStatus.SKIPPED));
                showView(Constants.TestStatus.SKIPPED);
                break;
            case R.id.bookmarkedTV:
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, Constants.TestStatus.BOOKMARKED));
                showView(Constants.TestStatus.BOOKMARKED);
                break;
            case R.id.backIV:
                finish();
                break;
            case R.id.filterTV:
                //this can be used to filter the data using indexing
               /* final Dialog settingsDialog = new Dialog(this);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                View iView = LayoutInflater.from(this).inflate(R.layout.dialog_indexing,null);
                settingsDialog.setContentView(iView);
                RecyclerView indexingRV = iView.findViewById(R.id.indexingRv);
                indexingRV.setLayoutManager(new LinearLayoutManager(this));
                indexingRV.setAdapter(new IndexingAdapter(this,topics));
                WindowManager.LayoutParams wmlp = settingsDialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wmlp.y = 150;
                settingsDialog.getWindow().setAttributes(wmlp);
                settingsDialog.show();
                indexingRV.addOnItemTouchListener(new CustomTouchListener(this, new onItemClickListener() {
                    @Override
                    public void onClick(View view, int index) {
                        switch (index){
                            case 0:
                                replaceFragment(ViewSolutionWithTabFragment.newInstance(test_segment_id, test_series_name, custom, "All"));
                                settingsDialog.dismiss();
                                filterTV.setText(topics.get(index));
                                break;

                            case 1:
                                replaceFragment(ViewSolutionWithTabFragment.newInstance(test_segment_id, test_series_name, custom, Const.CORRECT));
                                settingsDialog.dismiss();
                                filterTV.setText(topics.get(index));
                                break;

                            case 2:
                                replaceFragment(ViewSolutionWithTabFragment.newInstance(test_segment_id, test_series_name, custom, Const.INCORRECT));
                                settingsDialog.dismiss();
                                filterTV.setText(topics.get(index));
                                break;

                            case 3:
                                replaceFragment(ViewSolutionWithTabFragment.newInstance(test_segment_id, test_series_name, custom, Const.SKIPPED));
                                settingsDialog.dismiss();
                                filterTV.setText(topics.get(index));
                                break;

                            case 4:
                                replaceFragment(ViewSolutionWithTabFragment.newInstance(test_segment_id, test_series_name, custom, Const.BOOKMARKED));
                                settingsDialog.dismiss();
                                filterTV.setText(topics.get(index));
                                break;
                        }
                    }
                }));*/
                break;
        }
    }

    public void showView(String tabName) {
        allView.setVisibility(View.GONE);
        correctView.setVisibility(View.GONE);
        incorrectView.setVisibility(View.GONE);
        skippedView.setVisibility(View.GONE);
        bookmarkedView.setVisibility(View.GONE);

        switch (tabName) {
            case Constants.TestStatus.CORRECT:
                correctView.setVisibility(View.VISIBLE);
                break;
            case Constants.TestStatus.INCORRECT:
                incorrectView.setVisibility(View.VISIBLE);
                break;
            case Constants.TestStatus.SKIPPED:
                skippedView.setVisibility(View.VISIBLE);
                break;
            case Constants.TestStatus.BOOKMARKED:
                bookmarkedView.setVisibility(View.VISIBLE);
                break;
            case Constants.TestStatus.ALL:
            default:
                allView.setVisibility(View.VISIBLE);
                break;
        }

    }

    public void getData(final int mFromBookmark) {
        fromBookmark = mFromBookmark;
        if (custom) {
            myNetworkCall.NetworkAPICall(API.API_GET_CUSTOM_VIEW_SOLUTION_DATA, true);
        } else {
            myNetworkCall.NetworkAPICall(API.API_GET_VIEW_SOLUTION_DATA, true);
        }

       /* final Progress progress = new Progress(this);
        progress.setCancelable(false);
        progress.show();
        TestSeriesApiInterface apiInterface = ApiClient.createService(TestSeriesApiInterface.class);
        Call<JsonObject> response;
        if (custom)
            response = apiInterface.getCustomViewSolutionData(SharedPreference.getInstance().getLoggedInUser().getId());
        else
            response = apiInterface.getViewSolutionData(SharedPreference.getInstance().getLoggedInUser().getId(), testSegmentId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            viewSolutionData = gson.fromJson(Objects.requireNonNull(jsonResponse.optJSONObject(Const.DATA)).toString(), ViewSolutionData.class);
                            if (viewSolutionData != null) {
                                if (fromBookmark != 0) {
                                    viewSolutionResultSkipped.clear();
                                    viewSolutionResultCorrect.clear();
                                    viewSolutionResultIncorrect.clear();
                                    viewSolutionResultBookmarked.clear();
                                }

                                for (int i = 0; i < viewSolutionData.getResult().size(); i++) {
                                    if (viewSolutionData.getResult().get(i) == null) continue;

                                    if (viewSolutionData.getResult().get(i).getIs_bookmark().equalsIgnoreCase("1")) {
                                        viewSolutionResultBookmarked.add(viewSolutionData.getResult().get(i));
                                    }
                                    if (!GenericUtils.isEmpty(viewSolutionData.getResult().get(i).getUserAnswer())) {
                                        String[] userAnswer = viewSolutionData.getResult().get(i).getUserAnswer().split(",");
                                        ArrayList<String> userAnswerList = new ArrayList<>(Arrays.asList(userAnswer));
                                        userAnswerList.addAll(Arrays.asList(userAnswer));
                                        Collections.sort(userAnswerList);
                                        StringBuilder strUserAnswer = new StringBuilder();
                                        for (int j = 0; j < userAnswerList.size(); j++) {
                                            strUserAnswer.append(userAnswerList.get(j)).append(",");
                                        }
                                        if (strUserAnswer.toString().endsWith(",")) {
                                            strUserAnswer = new StringBuilder(strUserAnswer.substring(0, strUserAnswer.length() - 1));
                                        }

                                        if (viewSolutionData.getResult().get(i).getAnswer().equals(viewSolutionData.getResult().get(i).getUserAnswer())) {
                                            viewSolutionResultCorrect.add(viewSolutionData.getResult().get(i));
                                        } else if (!viewSolutionData.getResult().get(i).getAnswer().equals(viewSolutionData.getResult().get(i).getUserAnswer())) {
                                            viewSolutionResultIncorrect.add(viewSolutionData.getResult().get(i));
                                        }
                                    } else {
                                        viewSolutionResultSkipped.add(viewSolutionData.getResult().get(i));
                                    }
                                }


                                if (fromBookmark == 0) {
                                    replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, getString(R.string.all)));
                                    showView(getString(R.string.all));
                                } else if (fromBookmark == 1) {
                                    replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, Constants.TestStatus.BOOKMARKED));
                                    showView(Constants.TestStatus.BOOKMARKED);
                                }
                                progress.dismiss();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

            }
        });*/

    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        if (!custom) {
            params.put(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        viewSolutionData = new Gson().fromJson(Objects.requireNonNull(jsonObject.optJSONObject(Const.DATA)).toString(), ViewSolutionData.class);
        if (viewSolutionData != null) {
            if (fromBookmark != 0) {
                viewSolutionResultSkipped.clear();
                viewSolutionResultCorrect.clear();
                viewSolutionResultIncorrect.clear();
                viewSolutionResultBookmarked.clear();
            }

            for (int i = 0; i < viewSolutionData.getResult().size(); i++) {
                if (viewSolutionData.getResult().get(i) == null) continue;

                if (viewSolutionData.getResult().get(i).getIs_bookmark().equalsIgnoreCase("1")) {
                    viewSolutionResultBookmarked.add(viewSolutionData.getResult().get(i));
                }
                if (!GenericUtils.isEmpty(viewSolutionData.getResult().get(i).getUserAnswer())) {
                    String[] userAnswer = viewSolutionData.getResult().get(i).getUserAnswer().split(",");
                    ArrayList<String> userAnswerList = new ArrayList<>(Arrays.asList(userAnswer));
                    userAnswerList.addAll(Arrays.asList(userAnswer));
                    Collections.sort(userAnswerList);
                    StringBuilder strUserAnswer = new StringBuilder();
                    for (int j = 0; j < userAnswerList.size(); j++) {
                        strUserAnswer.append(userAnswerList.get(j)).append(",");
                    }
                    if (strUserAnswer.toString().endsWith(",")) {
                        strUserAnswer = new StringBuilder(strUserAnswer.substring(0, strUserAnswer.length() - 1));
                    }

                    if (viewSolutionData.getResult().get(i).getAnswer().equals(viewSolutionData.getResult().get(i).getUserAnswer())) {
                        viewSolutionResultCorrect.add(viewSolutionData.getResult().get(i));
                    } else if (!viewSolutionData.getResult().get(i).getAnswer().equals(viewSolutionData.getResult().get(i).getUserAnswer())) {
                        viewSolutionResultIncorrect.add(viewSolutionData.getResult().get(i));
                    }
                } else {
                    viewSolutionResultSkipped.add(viewSolutionData.getResult().get(i));
                }
            }


            if (fromBookmark == 0) {
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, getString(R.string.all)));
                showView(getString(R.string.all));
            } else if (fromBookmark == 1) {
                replaceFragment(ViewSolutionWithTabFragment.newInstance(testSegmentId, testSeriesName, custom, Constants.TestStatus.BOOKMARKED));
                showView(Constants.TestStatus.BOOKMARKED);
            }
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(this, jsonString, Toast.LENGTH_SHORT).show();
    }
}
