package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Registration;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.registration.CoursesInterestedResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CourseInterestedInFragment extends Fragment {

    LinearLayout coursesLL;
    Button nextBtn;
    EditText otherET;
    User user;
    ArrayList<View> coursesViewList;
    ArrayList<String> selectedcoursesList;
    ArrayList<CheckBox> coursesCheckBox;
    ArrayList<CoursesInterestedResponse> coursesResponseList;
    Registration registration;
    Progress mprogress;
    String regType;
    CompoundButton.OnCheckedChangeListener onCheckboxClick = (buttonView, isChecked) -> {
        CoursesInterestedResponse resp = (CoursesInterestedResponse) buttonView.getTag();
        if (isChecked && !selectedcoursesList.contains(resp.getId())) {
            selectedcoursesList.add(resp.getId());
        } else if (!isChecked) {
            selectedcoursesList.remove(resp.getId());
        }
    };
    private Activity activity;
    private String openFragment;

    public CourseInterestedInFragment() {
        // default constructor
    }

    public static CourseInterestedInFragment newInstance(String regType, String s) {
        CourseInterestedInFragment fragment = new CourseInterestedInFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.TYPE, regType);
        args.putString(Constants.Extras.OPEN_FROM, s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);
        if (getArguments() != null) {
            regType = getArguments().getString(Constants.Extras.TYPE);
            openFragment = getArguments().getString(Constants.Extras.OPEN_FROM);
        }
        activity = getActivity();
        user = User.getInstance();
        registration = user.getUser_registration_info();
        selectedcoursesList = new ArrayList<>();
        coursesResponseList = new ArrayList<>();
        coursesResponseList = getCoursesInterestedList(SharedPreference.getInstance().getRegistrationResponse().getIntersted_course());
    }

    public ArrayList<CoursesInterestedResponse> getCoursesInterestedList(ArrayList<CoursesInterestedResponse> list) {
        ArrayList<CoursesInterestedResponse> coursesInterestedResponses = new ArrayList<>();
        for (CoursesInterestedResponse coursesInterestedResponse : list) {
            if (coursesInterestedResponse.getParent_id().equalsIgnoreCase("1")) {
                coursesInterestedResponses.add(coursesInterestedResponse);
            }
        }

        return coursesInterestedResponses;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coursesinterested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle("Exams Interested");
        otherET = view.findViewById(R.id.otherET);

        coursesLL = view.findViewById(R.id.coursesLL);

        nextBtn = view.findViewById(R.id.nextBtn);
        if (!coursesResponseList.isEmpty()) {
            initCoursesOptions();
        } else {
            networkCallForMasterRegHit();//// NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
        }

        nextBtn.setOnClickListener(view1 -> {
            String substr = "";
            String substrtext = "";
            for (String str : selectedcoursesList) {
                if (substr.equals("")) substr = str;
                else substr = substr + "," + str;
            }
            for (String str1 : selectedcoursesList) {
                for (CoursesInterestedResponse ci : coursesResponseList) {
                    if (ci.getId().equals(str1)) {
                        if (substrtext.equals("")) substrtext = ci.getText_name();
                        else substrtext = substrtext + ", " + ci.getText_name();
                    }
                }
            }
            registration.setInterested_course(substr);
            registration.setInterested_course_text(substrtext);
            user.setUser_registration_info(registration);
//            if (getFragmentManager() != null)
//                getFragmentManager().popBackStack(openFragment, 0);
//            else
            requireActivity().onBackPressed();
        });
    }

    public void initCoursesOptions() {
        addViewtoCoursesOpt();
    }

    public void addViewtoCoursesOpt() {
        String[] courses = null;
        coursesCheckBox = new ArrayList<>();
        coursesLL.setVisibility(View.VISIBLE);
        coursesViewList = new ArrayList<>();
        View v = View.inflate(activity, R.layout.single_row_catcourse, null);
        LinearLayout coursesoptionLL = v.findViewById(R.id.coursesoptionLL);
        int j = 0;
        while (j < coursesResponseList.size()) {
            CheckBox cb = new CheckBox(activity);
            cb.setText(coursesResponseList.get(j).getText_name());
            cb.setPadding(8, 8, 8, 8);
            cb.setTag(coursesResponseList.get(j));

            if (registration != null && registration.getInterested_course() != null && !registration.getInterested_course().equals("")) {
                courses = registration.getInterested_course().split(",");
            }
            if (courses != null) {
                for (String str : courses) {
                    if (coursesResponseList.get(j).getId().equals(str)) {
                        cb.setChecked(true);
                        selectedcoursesList.add(str);
                    }
                }
            }
            coursesCheckBox.add(cb);
            coursesoptionLL.addView(cb);
            cb.setOnCheckedChangeListener(onCheckboxClick);
            j++;
        }
        coursesLL.addView(v);


    }

    public void networkCallForMasterRegHit() {
        mprogress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getMasterRegistrationResponse();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            MasterRegistrationResponse masterRegistrationResponse = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterRegistrationResponse.class);
                            coursesResponseList = getCoursesInterestedList(masterRegistrationResponse.getIntersted_course());
                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                                Helper.showErrorLayoutForNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 1);

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(getString(R.string.something_went_wrong_string)))
                                Helper.showErrorLayoutForNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 2);

                            JSONObject data = null;
                            String popupMessage = "";
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");

                            RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                        initCoursesOptions();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 1);
            }
        });
    }

}
