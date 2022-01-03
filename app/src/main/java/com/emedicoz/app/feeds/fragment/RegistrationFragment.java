package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.feeds.activity.NewProfileActivity;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.Registration;
import com.emedicoz.app.modelo.State;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.response.MasterRegistrationResponse;
import com.emedicoz.app.response.registration.StreamResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.OtpApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.RegFragApis;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rilixtech.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack, AmazonCallBack, TakeImageClass.ImageFromCropper, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    Spinner streamSpinner;
    Button nextBtn;
    Bitmap bitmap;
    s3ImageUploading s3ImageUploading;
    ArrayList<MediaFile> mediaFile;
    ImageView profileImage;
    ImageView profileImageText;
    ImageView editImageIV;
    FrameLayout circleImageFL;
    TextView phoneTV;
    TextView subStreamTV;
    TextView specialisationTV;
    TextView intCoursesTV;
    TextView stateTV;
    TextView cityTV;
    TextView countryTV;
    TextView collegeTV;
    EditText damsIdET;
    EditText mDAMSPasswordET;
    EditText mDAMSET;
    Dialog dialog;
    CheckBox damsIdCB;
    Button damsIdBtn;
    TextView damsIdTV;
    String damsToken;
    String damsPassword;
    EditText nameET;
    EditText emailET;
    EditText phoneET;
    Activity activity;
    ArrayList<String> streamList;
    MasterRegistrationResponse masterRegistrationResponse;
    ArrayAdapter<String> streamAdapter;
    LinearLayout layoutStream;
    User user;
    User userMain;
    Registration registration;
    String name;
    String profilePicture;
    String regType;
    String email;
    String phone;
    String c_code;
    String responseOtp;
    boolean isDataChanged = false;
    boolean isStreamChanged = false;
    boolean isPictureChanged = false;
    Button verifyBtn;
    Button cancelBtn;
    String otpText = "";
    BroadcastReceiver broadcastReceiver;
    User user1;
    Progress mProgress;
    Dialog searchDialog;
    ImageView ivClearSearch;
    RecyclerView searchRecyclerview;
    TextView tvCancel;
    CountryStateCity countryStateCity;
    MyNetworkCall myNetworkCall;
    String type;
    private StreamResponse stream;
    private TakeImageClass takeImageClass;
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private EditText etSearch;
    private TextView mResendOtpTV;
    private State state;
    private ArrayList<State> stateList = new ArrayList<>();
    private ArrayList<State> newStateList = new ArrayList<>();
    private String countryId = "";
    String stateId = "";
    String cityId = "";
    String collegeId = "";
    private String countryName = "";
    String stateName = "";
    String cityName = "";
    String collegeName = "";
    private String TAG = "RegistrationFragment";
    public boolean isUpdateProfile = false;

    public RegistrationFragment() {
    }

    public static RegistrationFragment newInstance(String regType, boolean isUpdateProfile) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.TYPE, regType);
        args.putBoolean(Constants.Extras.UPDATE_PROFILE, isUpdateProfile);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        if (getArguments() != null) {
            regType = getArguments().getString(Constants.Extras.TYPE);
            isUpdateProfile = getArguments().getBoolean(Constants.Extras.UPDATE_PROFILE);
        }
        activity = getActivity();
        userMain = SharedPreference.getInstance().getLoggedInUser();
        User user1 = SharedPreference.getInstance().getLoggedInUser();
        user = User.newInstance();
        user = User.copyInstance(user1);

        if (regType.equals(Const.REGISTRATION)) {
            registration = new Registration();
            user.setUser_registration_info(registration);
        }

        masterRegistrationResponse = SharedPreference.getInstance().getRegistrationResponse();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (regType.equals(Const.PROFILE)) {
            activity.setTitle(getString(R.string.profile));
        } else {
            activity.setTitle(getString(R.string.registration));
        }

        myNetworkCall = new MyNetworkCall(this, activity);
        profileImage = view.findViewById(R.id.cirle_image);
        profileImageText = view.findViewById(R.id.cirle_imageText);
        editImageIV = view.findViewById(R.id.editimage);

        circleImageFL = view.findViewById(R.id.circle_image_FL);

        streamSpinner = view.findViewById(R.id.streamSpinner);
        nextBtn = view.findViewById(R.id.nextBtn);
        phoneET = view.findViewById(R.id.phoneET);
        damsIdBtn = view.findViewById(R.id.verifyBtn);

        nameET = view.findViewById(R.id.nameET);
        emailET = view.findViewById(R.id.emailET);
        subStreamTV = view.findViewById(R.id.substreamTV);
        layoutStream = view.findViewById(R.id.layout_stream);
        intCoursesTV = view.findViewById(R.id.IntcoursesTV);
        stateTV = view.findViewById(R.id.stateTV);
        cityTV = view.findViewById(R.id.cityTV);
        collegeTV = view.findViewById(R.id.collegeTV);
        countryTV = view.findViewById(R.id.countryTV);
        specialisationTV = view.findViewById(R.id.specialisationTV);
        phoneTV = view.findViewById(R.id.phonenumberTV);
        damsIdTV = view.findViewById(R.id.damsidTV);

        damsIdET = view.findViewById(R.id.damsidET);
        damsIdCB = view.findViewById(R.id.damsidCB);

        Helper.CaptializeFirstLetter(nameET);
        if (!isUpdateProfile)
            layoutStream.setVisibility(View.VISIBLE);
        else
            layoutStream.setVisibility(View.GONE);
        takeImageClass = new TakeImageClass(activity, this);
        damsIdCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getDAMSLoginDialog(activity);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void getMobileNumberDialog() {
// custom dialog
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_change_mobile_number);
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        Button mVerifybtn = dialog.findViewById(R.id.verifyBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
        final EditText mobileET = dialog.findViewById(R.id.mobileET);
        final CountryCodePicker countryCodePicker = dialog.findViewById(R.id.ccp);

        countryCodePicker.registerPhoneNumberTextView(mobileET);
        countryCodePicker.enableHint(false);
        mVerifybtn.setText(activity.getString(R.string.verfiy));
        mobileET.setHint(R.string.phone_number);
        countryCodePicker.enableSetCountryByTimeZone(true);

        countryCodePicker.setOnCountryChangeListener(country -> {
            if (mobileET.getError() != null) {
                mobileET.setError(null);
            }
        });

        // if button is clicked, close the custom dialog
        mVerifybtn.setOnClickListener(v -> {
            phone = Helper.GetText(mobileET);
            c_code = countryCodePicker.getSelectedCountryCodeWithPlus();

            boolean isDataValid = true;

            if (TextUtils.isEmpty(phone)) {
                isDataValid = Helper.DataNotValid(mobileET);
            } else if (countryCodePicker.getSelectedCountryCode().equals("91") && Helper.isInValidIndianMobile(phone)) {
                if (dialog != null && !dialog.isShowing()) dialog.show();
                isDataValid = Helper.DataNotValid(mobileET, 2);
            }
            if (isDataValid) {
                if (dialog != null && dialog.isShowing()) dialog.hide();
                networkCallForOtp();
            }
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void getOTPVerifyDialog() {
// custom dialog
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_for_otp_change_password);
        dialog.setCancelable(false);
        mPinFirstDigitEditText = dialog.findViewById(R.id.OPT1ET);
        mPinSecondDigitEditText = dialog.findViewById(R.id.OPT2ET);
        mPinThirdDigitEditText = dialog.findViewById(R.id.OPT3ET);
        mPinForthDigitEditText = dialog.findViewById(R.id.OPT4ET);
        mPinHiddenEditText = dialog.findViewById(R.id.pin_hidden_edittext);
        mResendOtpTV = dialog.findViewById(R.id.resendotpTV);

        setPINListeners();
        verifyBtn = dialog.findViewById(R.id.verifyBtn);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        user = User.getInstance();
        verifyBtn.setOnClickListener(v -> {
            otpText = mPinHiddenEditText.getText().toString().trim();
            networkCallForValidateOtp();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        mResendOtpTV.setOnClickListener(v -> {
            if (user != null && !TextUtils.isEmpty(user.getMobile())) {
                networkCallForOtp();
            }
        });

        autoReadMessage();
        dialog.show();
    }

    private void networkCallForValidateOtp() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.validateOtpForMobile(user.getMobile(), otpText);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            networkCallForUpdateMobNo();

                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    public void checkDAMSLoginValidation() {
        damsToken = Helper.GetText(mDAMSET);
        damsPassword = Helper.GetText(mDAMSPasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(damsToken))
            isDataValid = Helper.DataNotValid(mDAMSET);

        else if (TextUtils.isEmpty(damsPassword))
            isDataValid = Helper.DataNotValid(mDAMSPasswordET);

        if (isDataValid) {
            user.setDams_username(damsToken);
            user.setDams_password(damsPassword);
            networkCallForUpdateDamsToken();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (regType.equals(Const.PROFILE)) {
            if (user != null) {
                registration = user.getUser_registration_info();
                initViews();
                if (masterRegistrationResponse != null && !masterRegistrationResponse.getMain_category().isEmpty())
                    initStreamList();
                else
                    networkCallForMasterRegHit();
            } else {
                networkCallForGetUser();
            }
        } else if (regType.equals(Const.REGISTRATION)) {
            initViews();
            isDataChanged = true;
            isStreamChanged = true;
            if (masterRegistrationResponse != null && !masterRegistrationResponse.getMain_category().isEmpty())
                initStreamList();
            else
                networkCallForMasterRegHit();
        }


        /*if (stateId.equals("001")) {
            stateTV.setText("Foreign Medical Graduates");
            cityTV.setVisibility(View.GONE);
            collegeTV.setVisibility(View.GONE);
        } else {
            cityTV.setVisibility(View.VISIBLE);
            collegeTV.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(((BaseABNoNavActivity) activity).countryName) && !TextUtils.isEmpty(((BaseABNoNavActivity) activity).collegeName) && !TextUtils.isEmpty(((BaseABNoNavActivity) activity).countryId)) {
            collegeTV.setVisibility(View.VISIBLE);
            stateTV.setText("Foreign Medical Graduates");
            countryTV.setText(((BaseABNoNavActivity) activity).countryName);
            collegeTV.setText(((BaseABNoNavActivity) activity).collegeName);
            this.countryId = ((BaseABNoNavActivity) activity).countryId;
            if (countryId.equals("101")) {
                cityTV.setVisibility(View.VISIBLE);
                collegeTV.setText("");
                stateTV.setText("");
                stateId = "";
            } else {
                cityTV.setVisibility(View.GONE);
            }

            ((BaseABNoNavActivity) activity).countryName = "";
            ((BaseABNoNavActivity) activity).collegeName = "";
            ((BaseABNoNavActivity) activity).countryId = "";
        }*/
/*        if (countryId.equals("101") || TextUtils.isEmpty(countryId)) {
            cityTV.setVisibility(View.VISIBLE);
            cityTV.setText(cityName);
        } else {
            cityTV.setVisibility(View.GONE);
        }
        countryTV.setText(countryName);
        stateTV.setText(stateName);
        collegeTV.setText(collegeName);*/

        Log.e(TAG, "onResume: country id -> " + countryId);
        Log.e(TAG, "onResume: country name -> " + countryName);
        Log.e(TAG, "onResume: state id -> " + stateId);
        Log.e(TAG, "onResume: state name -> " + stateName);
        Log.e(TAG, "onResume: city id -> " + cityId);
        Log.e(TAG, "onResume: city name -> " + cityName);
        Log.e(TAG, "onResume: college id -> " + collegeId);
        Log.e(TAG, "onResume: college name -> " + collegeName);


    }

    public void initViews() {
        specialisationTV.setOnClickListener(v -> onSpecializationClick());
        subStreamTV.setOnClickListener(v -> onSubStreamClick());
        intCoursesTV.setOnClickListener(v -> onInitCourseClick());

        countryTV.setOnClickListener(v -> {
            type = Const.COUNTRY;
            myNetworkCall.NetworkAPICall(API.API_GET_COUNTRIES, true);
        });

        stateTV.setOnClickListener(v -> onStateClick());

        cityTV.setOnClickListener(v -> onCityClick());

        collegeTV.setOnClickListener(view -> onCollegeClick());

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

        phoneET.setOnClickListener(v -> getMobileNumberDialog());

        if (!isPictureChanged) {
            user.setName(user.getName());
            if (!TextUtils.isEmpty(user.getProfile_picture())) {
                profilePicture = user.getProfile_picture();
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);

                Glide.with(this)
                        .asBitmap()
                        .load(user.getProfile_picture())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                profileImage.setImageBitmap(result);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(user.getName(), activity, user.getId());
                if (dr != null) {
                    profileImage.setVisibility(View.GONE);
                    profileImageText.setVisibility(View.VISIBLE);
                    profileImageText.setImageDrawable(dr);
                } else {
                    profileImage.setVisibility(View.VISIBLE);
                    profileImageText.setVisibility(View.GONE);
                    profileImage.setImageResource(R.mipmap.default_pic);
                }

            }
        } else if (!TextUtils.isEmpty(registration.getProfilepicture())) {
            profileImage.setVisibility(View.VISIBLE);
            profileImageText.setVisibility(View.GONE);
            Glide.with(this)
                    .asBitmap()
                    .load(registration.getProfilepicture())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            profileImage.setImageBitmap(result);
                        }
                    });
        }

        nameET.setText(user.getName());
        emailET.setText(user.getEmail());
        phoneET.setText(String.format("%s%s", user.getC_code(), user.getMobile()));

        countryId = user.getCountry_id();
        cityId = user.getCity_id();
        stateId = user.getState_id();
        collegeId = user.getCollege_id();

        if (TextUtils.isEmpty(user.getDams_tokken())) {
            damsIdCB.setVisibility(View.VISIBLE);
            damsIdCB.setChecked(false);
            damsIdTV.setVisibility(View.GONE);
        } else {
            damsIdCB.setVisibility(View.GONE);
            damsIdTV.setVisibility(View.VISIBLE);
            damsIdTV.setText(user.getDams_tokken());
        }

        if (!TextUtils.isEmpty(user.getCountry())) {
            countryTV.setText(user.getCountry());
            collegeTV.setText(user.getCollege());
            stateTV.setText(user.getState());

            countryId = user.getCountry_id();
            if (!countryId.equals("101")) {
                stateName = "Foreign Medical Graduates";
                stateTV.setText("Foreign Medical Graduates");
            }

            if (user.getCountry_id().equals("101") || TextUtils.isEmpty(user.getCountry_id())) {
                cityTV.setVisibility(View.VISIBLE);
                cityTV.setText(user.getCity());
            } else {
                cityTV.setVisibility(View.GONE);
            }
        }

        subStreamTV.setText(!TextUtils.isEmpty(registration.getMaster_id_level_one_name()) ? registration.getMaster_id_level_one_name() : "");

        if (!(TextUtils.isEmpty(registration.getOptional_text())) &&
                (TextUtils.isEmpty(registration.getMaster_id_level_two()) || registration.getMaster_id_level_two().equals("0")))
            specialisationTV.setText(registration.getOptional_text());
        else
            specialisationTV.setText(registration.getMaster_id_level_two_name());

        intCoursesTV.setText(registration.getInterested_course_text());

        editImageIV.setOnClickListener(v -> takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_profile_picture), getString(R.string.choose_image)));

        circleImageFL.setOnClickListener(v -> takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_profile_picture), getString(R.string.choose_image)));
    }

    private void onCollegeClick() {
        if (stateId != null) {
            if (stateId.equals("001")) {
                AlertDialog.Builder alertBuild = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View v = inflater.inflate(R.layout.enter_college_name_dialog, null);
                alertBuild.setView(v);
                final AlertDialog dialog = alertBuild.create();
                Button btnSubmit;
                final EditText etCollegeName;
                etCollegeName = v.findViewById(R.id.et_college_name);
                btnSubmit = v.findViewById(R.id.btn_submit);
                btnSubmit.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    collegeId = "";
                    collegeName = Helper.GetText(etCollegeName);
                    collegeTV.setText(Helper.GetText(etCollegeName));
                });
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                dialog.getWindow().setLayout(displayMetrics.widthPixels * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                if (TextUtils.isEmpty(countryId)) {
                    Toast.makeText(activity, "Please select country first.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stateId)) {
                    Toast.makeText(activity, "Please select state first.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(cityId)) {
                    Toast.makeText(activity, "Please select city first.", Toast.LENGTH_SHORT).show();
                } else {
                    type = Const.COLLEGE;
                    myNetworkCall.NetworkAPICall(API.API_GET_COLLEGES, true);
                }
            }
        }
    }

    private void onCityClick() {
        if (TextUtils.isEmpty(countryId)) {
            Toast.makeText(activity, "Please select country first.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(stateId)) {
            Toast.makeText(activity, "Please select state first.", Toast.LENGTH_SHORT).show();
        } else {
            type = Const.CITY;
            myNetworkCall.NetworkAPICall(API.API_GET_CITIES, true);

        }
    }

    private void onStateClick() {
        if (TextUtils.isEmpty(countryId)) {
            Toast.makeText(activity, "Please select Country first.", Toast.LENGTH_SHORT).show();
        } else {
            if (countryId.equals("101")) {
                type = Const.STATE;
                myNetworkCall.NetworkAPICall(API.API_GET_STATES, true);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("If you want to change state name please select India in country field.");
                builder.setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void onInitCourseClick() {
        updateUserPreference(); //IntcoursesTV
        if (Objects.requireNonNull(getFragmentManager()).findFragmentByTag(Const.INTERESTEDCOURSESFRAGMENT) == null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, CourseInterestedInFragment.newInstance(regType,
                    Const.REGISTRATIONFRAGMENT)).addToBackStack(Const.INTERESTEDCOURSESFRAGMENT).commit();
        else
            getFragmentManager().popBackStack(Const.INTERESTEDCOURSESFRAGMENT, 0);
    }

    private void onSubStreamClick() {
        updateUserPreference(); //substreamTV
        if (TextUtils.isEmpty(registration.getMaster_id_name())) {
            Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
        } else {
            if (Objects.requireNonNull(getFragmentManager()).findFragmentByTag(Const.SUBSTREAMFRAGMENT) == null)
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, SubStreamFragment.newInstance(regType,
                        Const.REGISTRATIONFRAGMENT)).addToBackStack(Const.SUBSTREAMFRAGMENT).commit();
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
                        Const.REGISTRATIONFRAGMENT)).addToBackStack(Const.SPCIALISATIONFRAGMENT).commit();
            else
                getFragmentManager().popBackStack(Const.SPCIALISATIONFRAGMENT, 0);
        }
    }

    public void filterList(Context context, String searchType, ArrayList<State> stateArrayList) {
        searchDialog = new Dialog(context);
        Objects.requireNonNull(searchDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent_background);
        searchDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.country_state_city_dialog);
        searchDialog.setCancelable(true);
        etSearch = searchDialog.findViewById(R.id.et_search);
        TextView tvTitle = searchDialog.findViewById(R.id.tv_title);
        if (searchType.equalsIgnoreCase(Const.COUNTRY)) {
            tvTitle.setText("Select Country");
            etSearch.setHint("Search Country");
        } else if (searchType.equalsIgnoreCase(Const.STATE)) {
            tvTitle.setText("Select State");
            etSearch.setHint("Search State");
        } else if (searchType.equalsIgnoreCase(Const.CITY)) {
            tvTitle.setText("Select City");
            etSearch.setHint("Search City");
        } else if (searchType.equalsIgnoreCase(Const.COLLEGE)) {
            tvTitle.setText("Select College");
            etSearch.setHint("Search College");
        }

        ivClearSearch = searchDialog.findViewById(R.id.iv_clear_search);
        tvCancel = searchDialog.findViewById(R.id.tv_cancel);
        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));
        tvCancel.setOnClickListener(v -> searchDialog.cancel());
        searchRecyclerview = searchDialog.findViewById(R.id.search_recyclerview);
        searchRecyclerview.setHasFixedSize(true);
        searchRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        countryStateCity = new CountryStateCity(activity, RegistrationFragment.this, searchType, stateArrayList);
        searchRecyclerview.setAdapter(countryStateCity);

        textWatcher();
        searchDialog.show();
    }

    public void textWatcher() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ivClearSearch.setVisibility(View.VISIBLE);
                } else {
                    ivClearSearch.setVisibility(View.GONE);
                }
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        newStateList.clear();
        for (State state : stateList) {
            if (type.equals(Const.COUNTRY)) {
                if (state.getName().toLowerCase().contains(text.toLowerCase())) {
                    newStateList.add(state);
                }
            } else if (type.equals(Const.STATE)) {
                if (state.getStateName().toLowerCase().contains(text.toLowerCase())) {
                    newStateList.add(state);
                }
            } else if (type.equals(Const.CITY)) {
                if (state.getCityName().toLowerCase().contains(text.toLowerCase())) {
                    newStateList.add(state);
                }
            }

        }
        if (!newStateList.isEmpty()) {
            searchRecyclerview.setVisibility(View.VISIBLE);

            countryStateCity.filterList(newStateList);
        } else {
            searchRecyclerview.setVisibility(View.INVISIBLE);
        }
    }

    public void getCountryData(String mType, String countryId, String name) {
        Log.e(TAG, "getCountryData: " + countryId);
        this.countryId = countryId;

        stateId = "";
        cityId = "";
        collegeId = "";
        collegeName = "";
        cityTV.setText("");
        collegeTV.setText("");

        if (countryId.equals("101")) {
            stateTV.setText("");
            cityTV.setVisibility(View.VISIBLE);
        } else {
            stateId = "001";
            stateName = "Foreign Medical Graduates";
            stateTV.setText("Foreign Medical Graduates");
            cityTV.setVisibility(View.GONE);
        }
        countryName = name;
        countryTV.setText(name);
    }

    public void getStateData(String mType, String stateId, String name) {
        Log.e(TAG, "getStateData: " + stateId);
        this.stateId = stateId;
        cityId = "";
        cityName = "";
        collegeId = "";
        collegeName = "";
        cityTV.setText("");
        collegeTV.setText("");

        if (stateId.equals("001")) {
            countryId = "";
            countryTV.setText("");
            cityTV.setVisibility(View.GONE);
            collegeTV.setVisibility(View.GONE);
        } else {
            cityTV.setVisibility(View.VISIBLE);
            collegeTV.setVisibility(View.VISIBLE);
        }
        stateName = name;
        stateTV.setText(name);
    }

    public void getCityData(String mType, String stateId, String cityId, String name) {
        Log.e(TAG, "getCityData: " + stateId + " --- " + cityId);
        this.stateId = stateId;
        this.cityId = cityId;
        collegeId = "";
        collegeTV.setText("");
        cityName = name;
        cityTV.setText(name);
    }

    public void getCollegeData(String mType, String stateId, String cityId, String collegeId, String name) {
        Log.e(TAG, "getCollegeData: " + stateId + " --- " + cityId + " --- " + collegeId);

        this.stateId = stateId;
        this.cityId = cityId;
        this.collegeId = collegeId;

        collegeName = name;
        collegeTV.setText(name);
    }

    public void checkValidation() {
        name = Helper.GetText(nameET);
        email = Helper.GetText(emailET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(name))
            isDataValid = Helper.DataNotValid(nameET);
        else if (TextUtils.isEmpty(email))
            isDataValid = Helper.DataNotValid(emailET);

        else if (!isUpdateProfile) {
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

        } else if (TextUtils.isEmpty(countryId)) {
            Toast.makeText(activity, "Please select country.", Toast.LENGTH_SHORT).show();
            isDataValid = false;
        } else if (TextUtils.isEmpty(Helper.GetText(stateTV))) {
            Toast.makeText(activity, "Please select state.", Toast.LENGTH_SHORT).show();
            isDataValid = false;
        } else if (countryId.equals("101")) {
            if (TextUtils.isEmpty(Helper.GetText(cityTV))) {
                Toast.makeText(activity, "Please select city.", Toast.LENGTH_SHORT).show();
                isDataValid = false;
            } else if (TextUtils.isEmpty(Helper.GetText(collegeTV))) {
                Toast.makeText(activity, "Please select college.", Toast.LENGTH_SHORT).show();
                isDataValid = false;
            }
        } else if (TextUtils.isEmpty(Helper.GetText(collegeTV))) {
            Toast.makeText(activity, "Please select college.", Toast.LENGTH_SHORT).show();
            isDataValid = false;
        }


        if (isDataValid) {
            if (!name.equals(userMain.getName()))
                isDataChanged = true;
            if (!email.equals(userMain.getEmail()))
                isDataChanged = true;
            if (!Helper.GetText(countryTV).equals(userMain.getCountry()))
                isDataChanged = true;
            if (!Helper.GetText(stateTV).equals(userMain.getState()))
                isDataChanged = true;
            if (!Helper.GetText(cityTV).equals(userMain.getCity()))
                isDataChanged = true;
            if (!Helper.GetText(collegeTV).equals(userMain.getCollege()))
                isDataChanged = true;
            if (regType.equals(Const.PROFILE)) {
                if (stream != null) {
                    if (!stream.getId().equals(userMain.getUser_registration_info().getMaster_id())) {
                        isDataChanged = true;
                        SharedPreference.getInstance().putBoolean(Const.IS_PROFILE_CHANGED, true);
                    }
                }
                if (!registration.getMaster_id_level_one().equals(userMain.getUser_registration_info().getMaster_id_level_one())) {
                    isDataChanged = true;
                }
                if (!registration.getMaster_id_level_two().equals(userMain.getUser_registration_info().getMaster_id_level_two()) ||
                        !registration.getOptional_text().equals(userMain.getUser_registration_info().getOptional_text())) {
                    isDataChanged = true;
                }
                if (!registration.getInterested_course().equals(userMain.getUser_registration_info().getInterested_course())) {
                    isDataChanged = true;
                }
            }
            updateUserPreference(); //CheckValidation
            if (isDataChanged) {
                networkCallForStreamRegistration();//NetworkAPICall(API.API_STREAM_REGISTRATION, true);
            } else {
                Toast.makeText(activity, R.string.no_data_changed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateUserPreference() {
        if (profilePicture != null)
            registration.setProfilepicture(profilePicture);
        else registration.setProfilepicture(user.getProfile_picture());
        registration.setName(name);
        registration.setEmail(email);
        registration.setUser_id(user.getId());
        user.setUser_registration_info(registration);
    }

    public void initStreamList() {
        streamList = new ArrayList<>();
        streamList.add(getString(R.string.select_stream));
        int i = 0;
        int pos = 0;
        while (i < masterRegistrationResponse.getMain_category().size()) {

            streamList.add(masterRegistrationResponse.getMain_category().get(i).getText_name());
            if (user.getUser_registration_info().getMaster_id() != null && user.getUser_registration_info().getMaster_id().equals(masterRegistrationResponse.getMain_category().get(i).getId())) {
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

    public void getDAMSLoginDialog(final Activity ctx) {

        // custom dialog
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dams_login);
        dialog.setCancelable(false);
        // set the custom dialog components - text, image and button
        Button mDAMSLoginbtn = dialog.findViewById(R.id.loginBtn);
        Button mDAMScancelbtn = dialog.findViewById(R.id.cancelBtn);
        mDAMSET = dialog.findViewById(R.id.damstokenET);
        mDAMSPasswordET = dialog.findViewById(R.id.damspassET);
        // if button is clicked, close the custom dialog
        mDAMSLoginbtn.setOnClickListener(v -> checkDAMSLoginValidation());
        mDAMScancelbtn.setOnClickListener(v -> {
            dialog.dismiss();
            damsIdCB.setChecked(false);
        });

        dialog.show();
    }

    public void networkCallForUpdateMobNo() {
        mProgress.show();
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        Call<JsonObject> response = apis.updateMobileNumber(SharedPreference.getInstance().getLoggedInUser().getId(),
                c_code, phone);
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
                            phoneET.setText(c_code + phone);
                            user.setMobile(phone);
                            user.setC_code(c_code);
                            SharedPreference.getInstance().setLoggedInUser(user);
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_UPDATE_MOBILE_NUMBER);
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
                Helper.showErrorLayoutForNoNav(API.API_UPDATE_MOBILE_NUMBER, activity, 1, 1);
            }
        });
    }

    public void networkCallForStreamRegistration() {
        mProgress.show();
        Call<JsonObject> response = null;
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        if (!isUpdateProfile) {
            if (!TextUtils.isEmpty(registration.getProfilepicture())) {
                response = apis.getStreamRegistration2(registration.getUser_id(), registration.getMaster_id(),
                        registration.getMaster_id_level_one(), registration.getMaster_id_level_two(), registration.getOptional_text(),
                        registration.getInterested_course(), registration.getName(), registration.getEmail(),
                        registration.getProfilepicture());
                SharedPreference.getInstance().putBoolean(Const.PICTURE, true);
            } else {
                response = apis.getStreamRegistration1(registration.getUser_id(), registration.getMaster_id(),
                        registration.getMaster_id_level_one(), registration.getMaster_id_level_two(), registration.getOptional_text(),
                        registration.getInterested_course(), registration.getName(), registration.getEmail());
            }
        } else {
            if (!TextUtils.isEmpty(registration.getProfilepicture())) {
                response = apis.getStreamRegistration2(registration.getUser_id(), registration.getOptional_text(),
                        registration.getInterested_course(), registration.getName(), registration.getEmail(), registration.getProfilepicture());
                SharedPreference.getInstance().putBoolean(Const.PICTURE, true);
            } else {
                response = apis.getStreamRegistration1(registration.getUser_id(), registration.getOptional_text(),
                        registration.getInterested_course(), registration.getName(), registration.getEmail());
            }

        }
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
                            Log.e(TAG, "onResponse: " + jsonResponse.toString());
                            if (registration.getProfilepicture() != null)
                                user.setProfile_picture(registration.getProfilepicture());
                            user = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), User.class);
                            SharedPreference.getInstance().setLoggedInUser(user);
                            SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true);
                            userMain = SharedPreference.getInstance().getLoggedInUser();
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_STREAM_REGISTRATION);
                            myNetworkCall.NetworkAPICall(API.API_GET_UPDATE_COLLEGE_INFO, true);
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_STREAM_REGISTRATION);
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
                Helper.showErrorLayoutForNoNav(API.API_STREAM_REGISTRATION, activity, 1, 1);
            }
        });
    }

    public void networkCallForUpdateDamsToken() {
        mProgress.show();
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        Call<JsonObject> response = apis.updateDamsToken(user.getId(), user.getDams_username(), user.getDams_password());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            JSONObject data;
                            data = GenericUtils.getJsonObject(jsonResponse);
                            user1 = gson.fromJson(data.toString(), User.class);
                            user = User.newInstance();
                            user = User.copyInstance(user1);
                            registration = (user.getUser_registration_info() == null ? new Registration() : user.getUser_registration_info());

                            user.setUser_registration_info(registration);
                            SharedPreference.getInstance().setLoggedInUser(user);
                            initViews();

                            if (masterRegistrationResponse != null) initStreamList();
                            else
                                networkCallForMasterRegHit();//NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);

                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_UPDATE_DAMS_TOKEN);
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
                Helper.showErrorLayoutForNoNav(API.API_UPDATE_DAMS_TOKEN, activity, 1, 1);
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
                            user1 = gson.fromJson(data.toString(), User.class);
                            user = User.newInstance();
                            user = User.copyInstance(user1);
                            SharedPreference.getInstance().setLoggedInUser(user1);

                            registration = user.getUser_registration_info();
                            initViews();
                            if (masterRegistrationResponse != null) initStreamList();
                            else
                                networkCallForMasterRegHit();//NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_USER);
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
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_MASTER_REGISTRATION_HIT);
                            }
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_MASTER_REGISTRATION_HIT);
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
                Helper.showErrorLayoutForNoNav(API.API_GET_MASTER_REGISTRATION_HIT, activity, 1, 1);
            }
        });
    }

    public void networkCallForOtp() {
        mProgress.show();
        RegFragApis apis = ApiClient.createService(RegFragApis.class);
        Call<JsonObject> response = apis.otpForMobileChange(SharedPreference.getInstance().getLoggedInUser().getId(),
                phone, c_code, true);
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
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            responseOtp = dataJsonObject.optString(Const.OTP);
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            getOTPVerifyDialog();
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_OTP_FOR_MOBILE_CHANGE);
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
                Helper.showErrorLayoutForNoNav(API.API_OTP_FOR_MOBILE_CHANGE, activity, 1, 1);
            }
        });
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_STREAM_REGISTRATION:
                networkCallForStreamRegistration();
                break;
            case API.API_UPDATE_MOBILE_NUMBER:
                networkCallForUpdateMobNo();
                break;
            case API.API_UPDATE_DAMS_TOKEN:
                networkCallForUpdateDamsToken();
                break;
            case API.API_GET_USER:
                networkCallForGetUser();
                break;
            case API.API_GET_MASTER_REGISTRATION_HIT:
                networkCallForMasterRegHit();
                break;
            case API.API_OTP_FOR_MOBILE_CHANGE:
                networkCallForOtp();
                break;
            default:
                break;
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        switch (apiType) {
            case API.API_GET_COLLEGES:
                params.put(Const.STATE, stateId);
                params.put(Const.CITY, cityId);
                break;
            case API.API_GET_STATES:
            case API.API_GET_COUNTRIES:
                return service.get(apiType);
            case API.API_GET_CITIES:
                params.put(Const.STATE, stateId);
                break;
            case API.API_GET_UPDATE_COLLEGE_INFO:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
                params.put(Const.COUNTRY_ID, countryId);
                params.put(Const.COUNTRY_NAME, Helper.GetText(countryTV));
                params.put(Const.STATE_ID, stateId.equals("001") ? "" : stateId);
                params.put(Const.CITY_ID, cityId);
                params.put(Const.COLLEGE_ID, collegeId);
                params.put(Const.COLLEGE_NAME, Helper.GetText(collegeTV));
                break;
        }
        Log.e("RegistrationFragment", "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonString, String apiType) throws JSONException {
        Gson gson = new Gson();
        Log.e("TAG", jsonString.toString());
        JSONObject data;
        User user1;
        if (jsonString.optBoolean(Const.STATUS)) {
            Helper.showErrorLayoutForNoNav(apiType, activity, 0, 0);
            switch (apiType) {
                case API.API_GET_COLLEGES:
                case API.API_GET_COUNTRIES:
                case API.API_GET_STATES:
                case API.API_GET_CITIES:
                    JSONArray jsonArray = GenericUtils.getJsonArray(jsonString);
                    if (jsonArray.length() > 0) {
                        stateList.clear();
                        if (type.equals(Const.STATE)) {
//                            stateList.add(new State("001", "Foreign Medical Graduates"));
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            state = gson.fromJson(jsonArray.optJSONObject(i).toString(), State.class);
                            stateList.add(state);
                        }
                        filterList(activity, type, stateList);
                    }
                    break;
                case API.API_GET_UPDATE_COLLEGE_INFO:
                    user.setCollege(GenericUtils.getJsonObject(jsonString).optString("college_name"));
                    user.setCollege_id(collegeId);
                    user.setCity(GenericUtils.getJsonObject(jsonString).optString("city_name"));
                    user.setCity_id(cityId);
                    user.setState(GenericUtils.getJsonObject(jsonString).optString("state_name"));
                    user.setState_id(stateId);
                    user.setCountry(GenericUtils.getJsonObject(jsonString).optString("country_name"));
                    user.setCountry_id(countryId);
                    SharedPreference.getInstance().setLoggedInUser(user);
                    MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();
                    if (masterFeedsHitResponse != null) {
                        masterFeedsHitResponse.setUser_detail(user);
                        SharedPreference.getInstance().setMasterHitData(masterFeedsHitResponse);
                    }
                    NewProfileActivity.IS_PROFILE_UPDATED = true;
                    isDataChanged = false;
                    if (regType.equals(Const.REGISTRATION)) {
                        if (userMain.getExpert_following() < Helper.getMinimumFollowerCount()) {
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, false);
                            Helper.GoToFollowExpertActivity(activity, Const.FOLLOW_THE_EXPERT_FIRST);
                        } else {
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                            Helper.GoToNextActivity(activity);
                        }
                    } else {
                        activity.finish();
                    }
                    break;
                case API.API_GET_USER:
                    data = jsonString.getJSONObject(Const.DATA);
                    user1 = gson.fromJson(data.toString(), User.class);
                    user = User.newInstance();
                    user = User.copyInstance(user1);
                    registration = user.getUser_registration_info();
                    initViews();
                    if (masterRegistrationResponse != null) initStreamList();
                    else
                        networkCallForMasterRegHit();//NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                    break;
                case API.API_UPDATE_DAMS_TOKEN:
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();

                    data = jsonString.getJSONObject(Const.DATA);
                    user1 = gson.fromJson(data.toString(), User.class);
                    user = User.newInstance();
                    user = User.copyInstance(user1);
                    registration = (user.getUser_registration_info() == null ? new Registration() : user.getUser_registration_info());

                    user.setUser_registration_info(registration);
                    SharedPreference.getInstance().setLoggedInUser(user);
                    initViews();

                    if (masterRegistrationResponse != null) initStreamList();
                    else
                        networkCallForMasterRegHit();//NetworkAPICall(API.API_GET_MASTER_REGISTRATION_HIT, true);
                    break;

                case API.API_GET_MASTER_REGISTRATION_HIT:
                    masterRegistrationResponse = new Gson().fromJson(jsonString.getJSONObject(Const.DATA).toString(), MasterRegistrationResponse.class);
                    if (masterRegistrationResponse != null) {
                        SharedPreference.getInstance().setMasterRegistrationData(masterRegistrationResponse);
                        initStreamList();
                    } else {
                        errorCallBack(jsonString.getString(Constants.Extras.MESSAGE), apiType);
                    }
                    break;
                case API.API_STREAM_REGISTRATION:
                    if (registration.getProfilepicture() != null)
                        user.setProfile_picture(registration.getProfilepicture());
                    user = new Gson().fromJson(jsonString.getJSONObject(Const.DATA).toString(), User.class);
                    SharedPreference.getInstance().setLoggedInUser(user);
                    userMain = SharedPreference.getInstance().getLoggedInUser();

                    NewProfileActivity.IS_PROFILE_UPDATED = true;
                    this.errorCallBack(jsonString.optString(Constants.Extras.MESSAGE), apiType);
                    isDataChanged = false;
                    if (regType.equals(Const.REGISTRATION)) {
                        if (userMain.getExpert_following() < Helper.getMinimumFollowerCount()) {
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, false);
                            Helper.GoToFollowExpertActivity(activity, Const.FOLLOW_THE_EXPERT_FIRST);
                        } else {
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                            Helper.GoToNextActivity(activity);
                        }
                    } else activity.finish();
                    break;
                case API.API_OTP_FOR_MOBILE_CHANGE:
                    JSONObject dataJsonObject = jsonString.getJSONObject(Const.DATA);
                    responseOtp = dataJsonObject.getString(Const.OTP);
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    getOTPVerifyDialog();
                    break;
                case API.API_UPDATE_MOBILE_NUMBER:
                    phoneET.setText(String.format("%s%s", c_code, phone));
                    user.setMobile(phone);
                    user.setC_code(c_code);
                    SharedPreference.getInstance().setLoggedInUser(user);
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(activity, jsonString.getString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
            }
        } else {
            errorCallBack(jsonString.getString(Constants.Extras.MESSAGE), apiType);
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_OTP_FOR_MOBILE_CHANGE:
                if (dialog != null && !dialog.isShowing()) dialog.show();
            case API.API_STREAM_REGISTRATION:
            case API.API_GET_COLLEGES:
            case API.API_GET_STATES:
            case API.API_GET_CITIES:
                Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

        if (jsonString.contains(getString(R.string.something_went_wrong_string)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
    }

    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void autoReadMessage() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                String message = Objects.requireNonNull(b).getString(Constants.Extras.MESSAGE);
                try {
                    if (message != null && message.contains("eMedicoz")) {
                        otpText = parseCode(message);
                        Log.e("OTP MESSAGE", otpText);
                        mPinHiddenEditText.setText(otpText);
                        mPinFirstDigitEditText.setText(String.valueOf(otpText.charAt(0)));
                        mPinSecondDigitEditText.setText(String.valueOf(otpText.charAt(1)));
                        mPinThirdDigitEditText.setText(String.valueOf(otpText.charAt(2)));
                        mPinForthDigitEditText.setText(String.valueOf(otpText.charAt(3)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (getActivity() != null)
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("broadCastOtp"));
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (takeImageClass != null)
            takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {
        if (!images.isEmpty()) {
            profilePicture = images.get(0).getFile();
            if (TextUtils.isEmpty(profilePicture)) {
                profilePicture = "https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png";
//                user.setProfile_picture("https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png");
            } else {
                updateUserPreference(); //onS3UploadData
            }

        }
    }

    public void imagePath(String str) {
        if (str != null) {
            isDataChanged = true;
            bitmap = BitmapFactory.decodeFile(str);
            Bitmap rotatedBitmap = modifyOrientation(bitmap,str);
            if (rotatedBitmap != null) {
                profileImage.setImageBitmap(rotatedBitmap);
                isPictureChanged = true;
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);
                mediaFile = new ArrayList<>();
                MediaFile mf = new MediaFile();
                mf.setFile_type(Const.IMAGE);
                mf.setImage(rotatedBitmap);
                mediaFile.add(mf);
                s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES, activity, this, null);
                s3ImageUploading.execute(mediaFile);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        if (s.length() == 0) {
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            mPinFirstDigitEditText.setText(String.format("%s", s.charAt(0)));
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 2) {
            mPinSecondDigitEditText.setText(String.format("%s", s.charAt(1)));
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 3) {
            mPinThirdDigitEditText.setText(String.format("%s", s.charAt(2)));
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            mPinForthDigitEditText.setText(String.format("%s", s.charAt(3)));
            hideSoftKeyboard(mPinForthDigitEditText);
        }
    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        final int id = view.getId();
        switch (id) {
            case R.id.OPT1ET:
            case R.id.OPT2ET:
            case R.id.OPT3ET:
            case R.id.OPT4ET:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = view.getId();
            if (id == R.id.pin_hidden_edittext && keyCode == KeyEvent.KEYCODE_DEL) {
                if (mPinHiddenEditText.getText().length() == 4)
                    mPinForthDigitEditText.setText("");
                else if (mPinHiddenEditText.getText().length() == 3)
                    mPinThirdDigitEditText.setText("");
                else if (mPinHiddenEditText.getText().length() == 2)
                    mPinSecondDigitEditText.setText("");
                else if (mPinHiddenEditText.getText().length() == 1)
                    mPinFirstDigitEditText.setText("");

                if (mPinHiddenEditText.length() > 0)
                    mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_absolute_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
