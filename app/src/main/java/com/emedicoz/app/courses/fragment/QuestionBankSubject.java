package com.emedicoz.app.courses.fragment;


import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.QbankSubjectAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dialog.QbankAppRating;
import com.emedicoz.app.flashcard.activity.FlashCardActivity;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.custommodule.ModuleData;
import com.emedicoz.app.pastpaperexplanation.activity.PastPaperExplanationActivity;
import com.emedicoz.app.rating.GetQbankRating;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionBankSubject extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    CardView cvPreviousPaper;
    Activity activity;
    NonScrollRecyclerView qBankSubjectRV;
    Progress mProgress;
    LinearLayoutManager linearLayoutManager;
    SingleCourseData singleCourseData;
    QbankSubjectAdapter qbankSubjectAdapter;
    CardView flashCard;
    CardView createCustom;
    String courseId;
    LinearLayout commonBookmarkLL;
    TextView commonBookmarkTV;
    User user;
    MyNetworkCall myNetworkCall;
    private static final String TAG = "QuestionBankSubject";
    public String searchText = "";
    private QbankAppRating qbankAppRating;
    String test_status;

    public QuestionBankSubject() {
        // Required empty public constructor
    }

    public static QuestionBankSubject newInstance(String courseId) {
        QuestionBankSubject fragment = new QuestionBankSubject();
        Bundle bundle = new Bundle();
        bundle.putString(Const.COURSE_ID, courseId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static QuestionBankSubject newInstance(Course course, String startDate, String endDate) {
        QuestionBankSubject fragment = new QuestionBankSubject();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSES, course);
        bundle.putString(Constants.Extras.START_DATE, startDate);
        bundle.putString(Constants.Extras.END_DATE, endDate);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            courseId = getArguments().getString(Const.COURSE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.question_bank_subject, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("onViewCreated", "onViewCreated method");
        Log.e(TAG, "onViewCreated: " + activity);
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        qBankSubjectRV = view.findViewById(R.id.qbankSubjectRV);
        createCustom = view.findViewById(R.id.createCustom);
        flashCard = view.findViewById(R.id.flashCard);
        commonBookmarkLL = view.findViewById(R.id.commonBookmarkLL);
        commonBookmarkTV = view.findViewById(R.id.commonBookmarkTV);
        cvPreviousPaper = view.findViewById(R.id.cvPreviousPaper);
        user = SharedPreference.getInstance().getLoggedInUser();
        qbankAppRating = new QbankAppRating();
        test_status = SharedPreference.getInstance().getQbankTestStatus(Const.QBANK_TEST_STATUS, "");
        //System.out.println("test_status-------------------------------------"+test_status);


        MasterFeedsHitResponse masterResponse = SharedPreference.getInstance().getMasterHitResponse();

        /**
         * Method used to show and hide Past Paper Examination module layout based on check from backend
         */
        checkPpeCourseVisibility(masterResponse);

        /**
         * Method used to show and hide bookmark layout layout based on check from backend
         */
        checkBookmarkVisibility(masterResponse);


        commonBookmarkLL.setOnClickListener(v -> {

            // Q_TYPE_DQB = 1 for DQB Bookmark

            Intent intent = new Intent(activity, CourseActivity.class);
            intent.putExtra(Const.FRAG_TYPE, Const.MY_BOOKMARKS);
            intent.putExtra(Constants.Extras.Q_TYPE_DQB, "2");
            activity.startActivity(intent);

        });
        linearLayoutManager = new LinearLayoutManager(activity);
        qBankSubjectRV.setLayoutManager(linearLayoutManager);
        qBankSubjectRV.setNestedScrollingEnabled(false);

        /**
         * Method used to show and hide custom module layout based on check from backend
         */
        checkCustomModuleVisibility(masterResponse);

        /**
         * Method used to show and hide FlashCard layout based on check from backend
         */
        checkFlashCardVisibility(masterResponse);

        createCustom.setOnClickListener(view1 -> customModuleClick());

        flashCard.setOnClickListener(view12 -> flashCardClick());

        cvPreviousPaper.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PastPaperExplanationActivity.class);
            startActivity(intent);
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) commonBookmarkLL.getLayoutParams();
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (10 * scale + 0.5f);
        layoutParams.setMargins(pixels, 0, pixels, 0);
        commonBookmarkLL.setLayoutParams(layoutParams);
    }

    private void flashCardClick() {
        if (singleCourseData != null) {
            if (singleCourseData.getIs_purchased().equals("1")) {
                startActivity(new Intent(activity, FlashCardActivity.class));
            } else if (singleCourseData.getMrp().equals("0")) {
                startActivity(new Intent(activity, FlashCardActivity.class));
            } else if (!TextUtils.isEmpty(user.getDams_tokken())) {
                if (singleCourseData.getFor_dams().equals("0")) {
                    startActivity(new Intent(activity, FlashCardActivity.class));
                } else {
                    showBuyPopup("FlashCard");
                }
            } else {
                if (singleCourseData.getNon_dams().equals("0")) {
                    startActivity(new Intent(activity, FlashCardActivity.class));
                } else {
                    showBuyPopup("FlashCard");
                }
            }
        }
    }

    private void customModuleClick() {
        if (singleCourseData != null) {
            if (singleCourseData.getIs_purchased().equals("1")) {
                checkModule();
            } else if (singleCourseData.getMrp().equals("0")) {
                checkModule();
            } else if (!TextUtils.isEmpty(user.getDams_tokken())) {
                if (singleCourseData.getFor_dams().equals("0")) {
                    checkModule();
                } else {
                    showBuyPopup(getString(R.string.custom_string));
                }
            } else {
                if (singleCourseData.getNon_dams().equals("0")) {
                    checkModule();
                } else {
                    showBuyPopup(getString(R.string.custom_string));
                }
            }
        }
    }

    private void checkPpeCourseVisibility(MasterFeedsHitResponse masterResponse) {
        if (masterResponse != null && masterResponse.getShow_ppe_course() != null && masterResponse.getShow_ppe_course().equalsIgnoreCase("1")) {
            cvPreviousPaper.setVisibility(View.VISIBLE);
        } else {
            cvPreviousPaper.setVisibility(View.GONE);
        }
    }

    private void checkBookmarkVisibility(MasterFeedsHitResponse masterResponse) {
        if (masterResponse != null && masterResponse.getShow_bookmark() != null && masterResponse.getShow_bookmark().equalsIgnoreCase("0")) {
            commonBookmarkLL.setVisibility(View.GONE);
        } else {
            commonBookmarkLL.setVisibility(View.VISIBLE);
        }

    }

    private void checkCustomModuleVisibility(MasterFeedsHitResponse masterResponse) {
        if (masterResponse != null && masterResponse.getCustom_display() != null && SharedPreference.getInstance().getMasterHitResponse().getCustom_display().equalsIgnoreCase("1")) {
            createCustom.setVisibility(View.VISIBLE);
        } else {
            createCustom.setVisibility(View.GONE);
        }
    }

    private void checkFlashCardVisibility(MasterFeedsHitResponse masterResponse) {
        if (masterResponse != null && masterResponse.getFlashcard() != null && SharedPreference.getInstance().getMasterHitResponse().getFlashcard().equalsIgnoreCase("1")) {
            flashCard.setVisibility(View.VISIBLE);
        } else {
            flashCard.setVisibility(View.GONE);
        }

    }

    private void showBuyPopup(String type) {
        View v;
        if (type.equalsIgnoreCase(getString(R.string.custom_string))) {
            v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.you_have_to_buy_qbank_to_create_custom_question));
        } else {
            v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.you_have_to_buy_qbank_to_open_flashcard));
        }

        Space space;
        Button btnCancel;
        Button btnSubmit;

        space = v.findViewById(R.id.space);
        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        space.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnSubmit.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red));
        btnSubmit.setText(getString(R.string.enroll));
        btnSubmit.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            Helper.goToCourseInvoiceScreen(activity, singleCourseData);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof BaseABNavActivity) {
            ((BaseABNavActivity) activity).setNotificationVisibility(false);
            ((BaseABNavActivity) activity).toolbar.setBackgroundResource(R.color.dark_qbank);
            ((BaseABNavActivity) activity).searchView.setVisibility(View.VISIBLE);
        }
        Log.e("onResume", "onResume method");
        getQbankRating();
        networkCallForSubjectData();
    }

    public void networkCallForSubjectData() {
        if (myNetworkCall == null)
            myNetworkCall = new MyNetworkCall(this, activity);
        myNetworkCall.NetworkAPICall(API.API_QBANK_COURSE_DATA, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Const.COURSE_ID, courseId);
        params.put("search", SharedPreference.getInstance().getString(Constants.SharedPref.SEARCHED_QUERY));
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        singleCourseData = new Gson().fromJson(data.toString(), SingleCourseData.class);
        singleCourseData.setCourse_type("3");
        if (!GenericUtils.isEmpty(singleCourseData.getTotal_bookmarked())) {
            String totalBookmark = "Bookmarks (" + singleCourseData.getTotal_bookmarked() + ")";
            Spannable spannable = new SpannableString(totalBookmark);
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.blue)), 10, (12 + singleCourseData.getTotal_bookmarked().length()),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            commonBookmarkTV.setText(spannable, TextView.BufferType.SPANNABLE);
        }
        qbankSubjectAdapter = new QbankSubjectAdapter(activity, singleCourseData.getCurriculam().getTopics(), singleCourseData);
        qBankSubjectRV.setAdapter(qbankSubjectAdapter);
        dqbProgressData();
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
    }

    private void dqbProgressData() {
        if (activity instanceof HomeActivity) {
            Fragment fragment = ((HomeActivity) activity).getCurrentFragment();
            if (fragment instanceof StudyFragment && singleCourseData != null && singleCourseData.getCurriculam().getTopics() != null && singleCourseData.getCurriculam().getTopics().length != 0) {
                ((StudyFragment) fragment).setDQBProgress(singleCourseData);
            }
        } else if (activity instanceof CourseActivity) {
            Fragment fragment = ((HomeActivity) activity).getCurrentFragment();
            if (fragment instanceof StudyFragment && singleCourseData != null && singleCourseData.getCurriculam().getTopics() != null && singleCourseData.getCurriculam().getTopics().length != 0) {
                ((StudyFragment) fragment).setDQBProgress(singleCourseData);
            }
        }
    }
//        if (activity instanceof BaseABNavActivity) {
//            Fragment fragment = ((BaseABNavActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            if (fragment instanceof StudyFragment && singleCourseData != null && singleCourseData.getCurriculam().getTopics() != null && singleCourseData.getCurriculam().getTopics().length != 0) {
//                ((StudyFragment) fragment).setDQBProgress(singleCourseData);
//            }
//        } else if (activity instanceof CourseActivity) {
//            Fragment fragment = ((CourseActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            if (fragment instanceof StudyFragment && singleCourseData != null && singleCourseData.getCurriculam().getTopics() != null && singleCourseData.getCurriculam().getTopics().length != 0) {
//                ((StudyFragment) fragment).setDQBProgress(singleCourseData);
//            }
//        }
//    }

    private void checkModule() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.checkModuleData(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("Response", jsonResponse.toString());
                        mprogress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            SharedPreference.getInstance().putString(Constants.ResultExtras.CUSTOM_TYPE, "1");
                            ModuleData moduleData = new Gson().fromJson(String.valueOf(GenericUtils.getJsonObject(jsonResponse)), ModuleData.class);

                            Intent conceIntent = new Intent(activity, CourseActivity.class);
                            conceIntent.putExtra(Const.FRAG_TYPE, Const.STARTMODULE);
                            conceIntent.putExtra(Const.moduleData, moduleData);
                            activity.startActivity(conceIntent);
                        } else {
                            Intent courseList = new Intent(activity, CourseActivity.class);
                            courseList.putExtra(Const.FRAG_TYPE, "CUSTOM_MODULE");
                            activity.startActivity(courseList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mprogress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, activity, 1, 1);
            }
        });
    }

    //check qbank apprating exit
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

                        }else {

                        }
                    } else {
                    }
                }
            }

            @Override
            public void onFailure(Call<GetQbankRating> call, Throwable t) {
                if (test_status.equals("true")){
                    qbankAppRating.show(getChildFragmentManager(), "");
                }else {
                }
            }
        });
    }
}

