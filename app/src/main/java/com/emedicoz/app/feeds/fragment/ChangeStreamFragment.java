package com.emedicoz.app.feeds.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Registration;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.registration.StreamResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeStreamFragment extends Fragment {
    Spinner streamSpinner;
    Button nextBtn;
    TextView subStreamTV;
    TextView specialisationTV;
    TextView intCoursesTV;

    ArrayList<String> streamList;
    ArrayAdapter<String> streamAdapter;
    boolean isStreamChanged = false;
    Progress mProgress;
    private StreamResponse stream;
    private FragmentActivity activity;
    private User userMain;
    private MasterRegistrationResponse masterRegistrationResponse;
    private Registration registration;
    private String regType;
    private User user;

    public ChangeStreamFragment() {
    }

    public static ChangeStreamFragment newInstance(String regType) {
        ChangeStreamFragment fragment = new ChangeStreamFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.TYPE, regType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        activity = getActivity();
        if (getArguments() != null) {
            regType = getArguments().getString(Constants.Extras.TYPE);
        }

        userMain = SharedPreference.getInstance().getLoggedInUser();
        user = User.newInstance();
        user = User.copyInstance(userMain);

        masterRegistrationResponse = SharedPreference.getInstance().getRegistrationResponse();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setTitle(getString(R.string.change_stream));

        streamSpinner = view.findViewById(R.id.streamSpinner);
        nextBtn = view.findViewById(R.id.nextBtn);
        subStreamTV = view.findViewById(R.id.substreamTV);
        intCoursesTV = view.findViewById(R.id.IntcoursesTV);
        specialisationTV = view.findViewById(R.id.specialisationTV);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userMain != null) {
            registration = userMain.getUser_registration_info();
            initViews();
            if (masterRegistrationResponse != null && !masterRegistrationResponse.getMain_category().isEmpty())
                initStreamList();
            else
                networkCallForMasterRegHit();
        } else {
            networkCallForGetUser();
        }
    }

    public void initViews() {
        specialisationTV.setOnClickListener(v -> onSpecializationClick());
        subStreamTV.setOnClickListener(v -> onSubStreamClick());
        intCoursesTV.setOnClickListener(v -> onInitCourseClick());

        streamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    stream = masterRegistrationResponse.getMain_category().get(i - 1);
                    if (!registration.getMaster_id().equals(stream.getId())) {
                        registration.setMaster_id(stream.getId());
                        registration.setMaster_id_name(stream.getText_name());
                        registration.setMaster_id_level_one("");
                        registration.setMaster_id_level_one_name("");
                        registration.setMaster_id_level_two("");
                        registration.setMaster_id_level_two_name("");
                        specialisationTV.setText("");
                        subStreamTV.setText("");
                    }
                } else {
                    stream = null;
                    registration.setMaster_id("");
                    registration.setMaster_id_name("");
                    registration.setMaster_id_level_one("");
                    registration.setMaster_id_level_one_name("");
                    registration.setMaster_id_level_two("");
                    registration.setMaster_id_level_two_name("");
                    specialisationTV.setText("");
                    subStreamTV.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nextBtn.setOnClickListener(v -> checkValidation());

        subStreamTV.setText(!TextUtils.isEmpty(registration.getMaster_id_level_one_name()) ? registration.getMaster_id_level_one_name() : "");
        if (!(TextUtils.isEmpty(registration.getOptional_text())) &&
                (TextUtils.isEmpty(registration.getMaster_id_level_two()) || registration.getMaster_id_level_two().equals("0")))
            specialisationTV.setText(registration.getOptional_text());
        else
            specialisationTV.setText(registration.getMaster_id_level_two_name());

        intCoursesTV.setText(registration.getInterested_course_text());
    }

    private void onSubStreamClick() {
        updateUserPreference(); //substreamTV
        if (TextUtils.isEmpty(registration.getMaster_id_name())) {
            Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
        } else {
            if (Objects.requireNonNull(getFragmentManager()).findFragmentByTag(Const.SUBSTREAMFRAGMENT) == null)
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, SubStreamFragment.newInstance(regType,
                        Const.CHANGESTREAMFRAGMENT)).addToBackStack(Const.SUBSTREAMFRAGMENT).commit();
            else
                getFragmentManager().popBackStack(Const.SUBSTREAMFRAGMENT, 0);
        }
    }

    private void onSpecializationClick() {
        updateUserPreference(); //specialisationTV
        if (TextUtils.isEmpty(registration.getMaster_id_level_one_name())) {
            Toast.makeText(activity, R.string.please_select_the_substream, Toast.LENGTH_SHORT).show();
        } else {
            if (Objects.requireNonNull(getFragmentManager()).findFragmentByTag(Const.SPCIALISATIONFRAGMENT) == null)
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, SpecializationFragment.newInstance(regType,
                        Const.CHANGESTREAMFRAGMENT)).addToBackStack(Const.SPCIALISATIONFRAGMENT).commit();
            else
                getFragmentManager().popBackStack(Const.SPCIALISATIONFRAGMENT, 0);
        }
    }

    private void onInitCourseClick() {
        updateUserPreference(); //IntcoursesTV
        if (TextUtils.isEmpty(registration.getMaster_id_name())) {
            Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
        } else {
            if (Objects.requireNonNull(getFragmentManager()).findFragmentByTag(Const.INTERESTEDCOURSESFRAGMENT) == null)
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, CourseInterestedInFragment.newInstance(regType,
                        Const.CHANGESTREAMFRAGMENT)).addToBackStack(Const.INTERESTEDCOURSESFRAGMENT).commit();
            else
                getFragmentManager().popBackStack(Const.INTERESTEDCOURSESFRAGMENT, 0);
        }
    }


    public void checkValidation() {
        boolean isDataValid = true;

        if (streamSpinner.getCount() <= 0) {
            isDataValid = false;
            Toast.makeText(activity, R.string.restart_app_msg, Toast.LENGTH_SHORT).show();
        } else if (streamSpinner.getSelectedItem().equals(getString(R.string.select_stream))) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(registration.getMaster_id_level_one_name())) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_the_substream, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(registration.getMaster_id_level_two_name()) &&
                TextUtils.isEmpty(registration.getOptional_text())) {
            Toast.makeText(activity, R.string.please_select_the_specialisation, Toast.LENGTH_SHORT).show();
            isDataValid = false;
        }

        if (isDataValid) {
            updateUserPreference(); //CheckValidation
            networkCallForStreamRegistration();//NetworkAPICall(API.API_STREAM_REGISTRATION, true);
        }
    }

    public void updateUserPreference() {

        registration.setUser_id(userMain.getId());
        userMain.setUser_registration_info(registration);
    }

    public void initStreamList() {
        streamList = new ArrayList<>();
        streamList.add(getString(R.string.select_stream));
        int i = 0;
        int pos = 0;
        while (i < masterRegistrationResponse.getMain_category().size()) {
            streamList.add(masterRegistrationResponse.getMain_category().get(i).getText_name());
            if (userMain.getUser_registration_info().getMaster_id() != null && userMain.getUser_registration_info().getMaster_id().equals(masterRegistrationResponse.getMain_category().get(i).getId())) {
                stream = masterRegistrationResponse.getMain_category().get(i);
                pos = i + 1;
            }
            i++;
        }

        streamAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.single_row_spinner_item, streamList);
        streamSpinner.setAdapter(streamAdapter);
        if (stream != null) {
            streamSpinner.setSelection(pos);
        }
    }

    public void networkCallForStreamRegistration() {
        mProgress.show();
        Call<JsonObject> response;
        RegFragApis apis = ApiClient.createService(RegFragApis.class);

        response = apis.saveStreamDataForUser(registration.getUser_id(), registration.getMaster_id(),
                registration.getMaster_id_level_one(), registration.getMaster_id_level_two()/*, registration.getInterested_course()*/);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            if (registration.getProfilepicture() != null)
                                userMain.setProfile_picture(registration.getProfilepicture());
                            userMain = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), User.class);
                            SharedPreference.getInstance().setLoggedInUser(userMain);
                            SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true);
                            userMain = SharedPreference.getInstance().getLoggedInUser();
                            Helper.getStorageInstance(activity).deleteRecord(Const.OFFLINE_COURSE);

                            activity.finish();
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 2);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 1);
            }
        });
    }

    public void networkCallForGetUser() {
        mProgress.show();
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        Call<JsonObject> response = apis.getActiveUser("data_model/user/Registration/get_active_user/" +
                SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();
                    JSONObject data;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            data = GenericUtils.getJsonObject(jsonResponse);
                            userMain = gson.fromJson(data.toString(), User.class);
                            SharedPreference.getInstance().setLoggedInUser(userMain);

                            registration = userMain.getUser_registration_info();
                            initViews();
//                            if (masterRegistrationResponse != null) initStreamList();
//                            else
                            networkCallForMasterRegHit();//NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_USER, activity, 1, 1);
            }
        });
    }

    public void networkCallForMasterRegHit() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getMasterRegistrationResponse();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            masterRegistrationResponse = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterRegistrationResponse.class);
                            if (masterRegistrationResponse != null) {
                                SharedPreference.getInstance().setMasterRegistrationData(masterRegistrationResponse);
                                initStreamList();
                            } else {
                                Toast.makeText(activity, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 1);
            }
        });
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_STREAM_REGISTRATION:
                networkCallForStreamRegistration();
                break;
            case API.API_GET_MASTER_REGISTRATION_HIT:
                networkCallForMasterRegHit();
                break;
            default:
                break;
        }
    }

}
