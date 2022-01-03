package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.SingleOverviewAdapter;
import com.emedicoz.app.courses.adapter.SingleStudyAdapter;
import com.emedicoz.app.courses.adapter.onButtonClicked;
import com.emedicoz.app.modelo.courses.Cards;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.ExamPrepItem;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionData;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse;
import com.emedicoz.app.modelo.liveclass.courses.LiveClassCourseResponse;
import com.emedicoz.app.modelo.liveclass.courses.LiveVideoResponse;
import com.emedicoz.app.modelo.liveclass.courses.NotesTestEpubResponse;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class SingleStudy extends Fragment implements onButtonClicked, View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    MyNetworkCall myNetworkCall;
    private static final String TAG = "SingleStudy";
    RecyclerView studyCourseRV;
    Activity activity;
    Course course;
    SingleStudyModel singleStudyModel;
    ArrayList<Cards> tiles = new ArrayList<>();
    ExamPrepItem examPrepItem;
    RelativeLayout buttonLow;
    LiveVideoResponse liveVideoResponse;
    DescriptionData descriptionData;
    NotesTestEpubResponse notesTestEpubResponse;
    DescriptionResponse descriptionResponse;
    public SingleCourseData singleCourseData;
    LiveClassCourseResponse liveClassCourseResponse;
    Button buyNowBtn;
    boolean refreshing = false;
    SingleStudyAdapter singleStudyAdapter;
    LinearLayoutManager linearLayoutManager;
    String imagLinkFooter;

    public String selectedTab;
    TextView noDataTV;
    RelativeLayout rlMain;

    public static SingleStudy newInstance(String image, Course course) {
        SingleStudy singleCourse = new SingleStudy();
        Bundle args = new Bundle();
        args.putString(Const.IMAGE, image);
        args.putSerializable(Const.COURSE, course);
        singleCourse.setArguments(args);
        return singleCourse;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            imagLinkFooter = getArguments().getString(Const.IMAGE);
            course = (Course) getArguments().getSerializable(Const.COURSE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        if (!TextUtils.isEmpty(course.getIs_live()) && course.getIs_live().equals("1")) {
            ((CourseActivity) activity).setToolbarTitle("Live Classes");
        } else {
            ((CourseActivity) activity).setToolbarTitle("Course");
        }

        networkCallForGetBasicCourse();
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        studyCourseRV = view.findViewById(R.id.studyCourseRV);
        buyNowBtn = view.findViewById(R.id.buyNowBtn);
        noDataTV = view.findViewById(R.id.noDataTV);
        rlMain = view.findViewById(R.id.rlMain);
        buttonLow = view.findViewById(R.id.buttonLow);

        bindControls();
    }

    private void bindControls() {
        buyNowBtn.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        studyCourseRV.setLayoutManager(linearLayoutManager);
    }

    public void networkCallForGetBasicCourse() {
        myNetworkCall.NetworkAPICall(API.API_GET_BASIC_DATA_VC, true);
    }

    private void networkCallForCourseInfoRaw() {
        myNetworkCall.NetworkAPICall(API.API_GET_SINGLE_COURSE_INFO_RAW_V2, true);
    }

    public void callForData() {
        if (!tiles.isEmpty())
            tiles.clear();
        tiles.addAll(descriptionResponse.getData().getTiles());
        Cards tile = new Cards();
        tile.setTile_name("Overview");
        tile.setType("overview");
        tiles.add(tile);

        if (tiles.get(0).getType().equalsIgnoreCase(Const.VIDEO)) {
            networkCallForVideoDataFirst();
            selectedTab = Const.VIDEO;
        } else if (tiles.get(0).getType().equalsIgnoreCase(Const.COURSE)) {
            selectedTab = Const.COURSE;
            networkCallForCourseDataFirst();
        } else if (Const.TEST_EPUB_PDF.contains(tiles.get(0).getType())) {
            selectedTab = Const.TEST_EPUB_PDF;
            networkCallForTestEpubPdfData();
        } else {
            selectedTab = "overview";
            initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, "overview");
        }
    }

    private void networkCallForTestEpubPdfData() {
        myNetworkCall.NetworkAPICall(API.API_GET_ALL_TEST_EPUB_PDF_DATA, true);
    }

    private void networkCallForVideoDataFirst() {
        myNetworkCall.NetworkAPICall(API.API_GET_VIDEO_DATA_VC, true);
    }

    private void networkCallForCourseDataFirst() {
        myNetworkCall.NetworkAPICall(API.API_GET_COURSES, true);
    }

    private void initBasicAdapter(DescriptionResponse descriptionResponse, NotesTestEpubResponse notesTestEpubResponse, LiveVideoResponse liveVideoResponse, String type) {
        Log.e(TAG, "initBasicAdapter: type = " + type);
        buttonLow.setVisibility(View.GONE);
        if (type.equalsIgnoreCase("overview")) {
            SingleOverviewAdapter singleOverviewAdapter = new SingleOverviewAdapter(activity, descriptionResponse, type, SingleStudy.this, selectedTab, this);
            studyCourseRV.setAdapter(singleOverviewAdapter);
            singleOverviewAdapter.updateimageFooter(descriptionResponse.getData().getBasic().getDescHeaderImage());
            if (descriptionResponse.getData().getIsPurchased().equals("0")) {
                Constants.IS_PURCHASED = "1";
                SharedPreference.getInstance().putBoolean(Const.SINGLE_STUDY, false);
            } else {
                Constants.IS_PURCHASED = "0";
                SharedPreference.getInstance().putBoolean(Const.SINGLE_STUDY, true);
            }
        } else {
            // type video -> button visibility visible
            if (descriptionResponse.getData().getBasic().getIs_renew().equals("1")) {
                buyNowBtn.setText(activity.getResources().getString(R.string.renew));
                buyNowBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.rewards_color));
                buttonLow.setVisibility(View.VISIBLE);

            } else if (descriptionResponse.getData().getIsPurchased().equalsIgnoreCase("0")) {
                if (descriptionResponse.getData().getBasic().getMrp().equalsIgnoreCase("0")) {
                    buttonLow.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (!descriptionResponse.getData().getBasic().getForDams().equalsIgnoreCase("0")) {
                            buttonLow.setVisibility(View.VISIBLE);
                            buyNowBtn.setText(String.format("%s (%s %s)", activity.getString(R.string.enroll), Helper.getCurrencySymbol(), descriptionResponse.getData().getBasic().getForDams()));
                            buyNowBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                        } else {
                            buttonLow.setVisibility(View.GONE);
                        }
                    } else {
                        if (!descriptionResponse.getData().getBasic().getNonDams().equalsIgnoreCase("0")) {
                            buttonLow.setVisibility(View.VISIBLE);
                            buyNowBtn.setText(String.format("%s (%s %s)", activity.getString(R.string.enroll), Helper.getCurrencySymbol(), descriptionResponse.getData().getBasic().getNonDams()));
                            buyNowBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                        } else {
                            buttonLow.setVisibility(View.GONE);
                        }
                    }
                }
            } else if (descriptionResponse.getData().getIsPurchased().equalsIgnoreCase("1")) {
                buttonLow.setVisibility(View.GONE);
                buyNowBtn.setText(activity.getResources().getString(R.string.enrolled));
                buyNowBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.green_new));
            }
            if (refreshing) {
                singleStudyAdapter.udateData(liveVideoResponse);
                singleStudyAdapter.contentType = type;
                singleStudyAdapter.notifyDataSetChanged();
            } else {
                refreshing = false;
                if (type.equalsIgnoreCase(Const.COURSE)) {
                    singleStudyAdapter = new SingleStudyAdapter(activity, liveClassCourseResponse, descriptionResponse, SingleStudy.this, selectedTab);
                } else {
                    singleStudyAdapter = new SingleStudyAdapter(activity, descriptionResponse, liveVideoResponse, notesTestEpubResponse, SingleStudy.this, descriptionResponse.getData().getIsPurchased(), selectedTab);
                }
                singleStudyAdapter.contentType = type;
                studyCourseRV.setAdapter(singleStudyAdapter);
                singleStudyAdapter.updateimageFooter(descriptionResponse.getData().getBasic().getDescHeaderImage());

                if (descriptionResponse.getData().getIsPurchased().equals("0")) {
                    Constants.IS_PURCHASED = "1";
                    SharedPreference.getInstance().putBoolean(Const.SINGLE_STUDY, false);
                } else {
                    Constants.IS_PURCHASED = "0";
                    SharedPreference.getInstance().putBoolean(Const.SINGLE_STUDY, true);
                }
            }
        }
    }

    @Override
    public void onTitleClicked(String string) {
        switch (string) {

            case Const.VIDEO:
                selectedTab = Const.VIDEO;
                networkCallForVideoDataFirst();

                break;

            case Const.TEST_EPUB_PDF:
                selectedTab = Const.TEST_EPUB_PDF;
                networkCallForTestEpubPdfData();
                break;

            case Const.COURSE:
                selectedTab = Const.COURSE;
                networkCallForCourseDataFirst();
                break;

            case "overview":
                selectedTab = "overview";
//                networkCallForCourseInfoRaw();
                initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, "overview");
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (singleStudyAdapter != null)
//            singleStudyAdapter.notifyDataSetChanged();
        if (!GenericUtils.isEmpty(selectedTab) && selectedTab.equalsIgnoreCase(Const.TEST_EPUB_PDF))
            networkCallForTestEpubPdfData();
        else if (!GenericUtils.isEmpty(selectedTab) && selectedTab.equalsIgnoreCase("overview"))
            initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, "overview");
    }

    public SingleCourseData getData(DescriptionResponse descriptionResponse) {
        singleCourseData.setCourse_type(descriptionResponse.getData().getBasic().getCourseType());
        singleCourseData.setFor_dams(descriptionResponse.getData().getBasic().getForDams());
        singleCourseData.setNon_dams(descriptionResponse.getData().getBasic().getNonDams());
        singleCourseData.setMrp(descriptionResponse.getData().getBasic().getMrp());
        singleCourseData.setId(descriptionResponse.getData().getBasic().getId());
        singleCourseData.setCover_image(descriptionResponse.getData().getBasic().getCoverImage());
        singleCourseData.setTitle(descriptionResponse.getData().getBasic().getTitle());
        singleCourseData.setLearner(descriptionResponse.getData().getBasic().getLearner());
        singleCourseData.setRating(descriptionResponse.getData().getBasic().getRating());
        singleCourseData.setGst_include(descriptionResponse.getData().getBasic().getGstInclude());

        singleCourseData.setIs_subscription(descriptionResponse.getData().getBasic().getIs_subscription());
        singleCourseData.setIs_instalment(descriptionResponse.getData().getBasic().getIs_instalment());
//      singleCourseData.setInstallment(descriptionResponse.getData().getBasic().getInstallment());


        if (descriptionResponse.getData().getBasic().getGst() != null)
            singleCourseData.setGst(descriptionResponse.getData().getBasic().getGst());
        if (descriptionResponse.getData().getBasic().getPointsConversionRate() != null)
            singleCourseData.setPoints_conversion_rate(descriptionResponse.getData().getBasic().getPointsConversionRate());
        return singleCourseData;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buyNowBtn) {
            if (descriptionResponse.getData().getIsPurchased().equals("0") ||
                    descriptionResponse.getData().getBasic().getIs_renew().equals("1")) {
                Helper.goToCourseInvoiceScreen(activity, singleCourseData);
            }
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        if (API.API_GET_BASIC_DATA_VC.equals(apiType) || API.API_GET_COURSES.equals(apiType)) {
            params.put(Constants.Extras.ID, course.getId());
        } else if (API.API_GET_VIDEO_DATA_VC.equals(apiType) || API.API_GET_ALL_TEST_EPUB_PDF_DATA.equals(apiType)) {
            params.put(Constants.Extras.ID, course.getId());
            params.put(Const.LAYER, 1);
        } else if (API.API_GET_SINGLE_COURSE_INFO_RAW_V2.equals(apiType)) {
            params.put(Const.COURSE_ID, descriptionData.getBasic().getId());
            params.put(Const.COURSE_TYPE, descriptionData.getBasic().getCourseType());
            params.put(Const.IS_COMBO, descriptionData.getBasic().getIsCombo());
            params.put(Const.PARENT_ID, "courseId");
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        if (API.API_GET_BASIC_DATA_VC.equals(apiType)) {
            descriptionResponse = new Gson().fromJson(jsonObject.toString(), DescriptionResponse.class);
            descriptionData = descriptionResponse.getData();
            if (descriptionData != null) {
                noDataTV.setVisibility(View.GONE);
                rlMain.setVisibility(View.VISIBLE);
                addToRecentCourseList(course.getId());
                if (!GenericUtils.isListEmpty(descriptionResponse.getData().getTiles()))
                    callForData();
                else
                    networkCallForCourseInfoRaw();
            } else {
                Helper.showErrorLayoutForNoNav(API.API_GET_BASIC_DATA_VC, activity, 1, 2);
            }
        } else if (API.API_GET_SINGLE_COURSE_INFO_RAW_V2.equals(apiType)) {
            JSONObject data = GenericUtils.getJsonObject(jsonObject);
            singleCourseData = new Gson().fromJson(data.toString(), SingleCourseData.class);
            Constants.COURSEID = singleCourseData.getId();
            singleCourseData.setIs_subscription(descriptionResponse.getData().getBasic().getIs_subscription()); // nimesh
            if (singleCourseData.getRating() != null && !singleCourseData.getRating().equalsIgnoreCase(""))
                Constants.RATING = Float.parseFloat(singleCourseData.getRating());

            if (singleCourseData.getReviews() != null) {
                Log.e(TAG, "onResponse: getReviews size = " + singleCourseData.getReviews().length);
            } else {
                Log.e(TAG, "onResponse: getReviews size = null");
            }
            callForData();
//                initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, "overview");
        } else if (API.API_GET_VIDEO_DATA_VC.equals(apiType)) {
            try {
                liveVideoResponse = new Gson().fromJson(jsonObject.toString(), LiveVideoResponse.class);
                initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, Const.VIDEO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (API.API_GET_ALL_TEST_EPUB_PDF_DATA.equals(apiType)) {
            notesTestEpubResponse = new Gson().fromJson(jsonObject.toString(), NotesTestEpubResponse.class);
            initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, Const.TEST_DATA);
        } else if (API.API_GET_COURSES.equals(apiType)) {
            liveClassCourseResponse = new Gson().fromJson(jsonObject.toString(), LiveClassCourseResponse.class);
            initBasicAdapter(descriptionResponse, notesTestEpubResponse, liveVideoResponse, Const.COURSE);
        }
    }

    private void addToRecentCourseList(String id) {
        String courseIds = SharedPreference.getInstance().getString(Const.RECENT_COURSE_ID);
        courseIds += course.getId() + ", ";

        SharedPreference.getInstance().putString(Const.RECENT_COURSE_ID, courseIds);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        if (API.API_GET_BASIC_DATA_VC.equals(apiType) || API.API_GET_SINGLE_COURSE_INFO_RAW_V2.equals(apiType)) {
            if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message))) {
                Helper.showErrorLayoutForNoNav(API.API_GET_BASIC_DATA_VC, activity, 1, 1);
            } else {
                Helper.showErrorLayoutForNoNav(API.API_GET_BASIC_DATA_VC, activity, 1, 2);
            }
        } else if (API.API_GET_VIDEO_DATA_VC.equals(apiType) || API.API_GET_COURSES.equals(apiType) || API.API_GET_ALL_TEST_EPUB_PDF_DATA.equals(apiType)) {
            GenericUtils.showToast(activity, jsonString);
        }
    }
}
