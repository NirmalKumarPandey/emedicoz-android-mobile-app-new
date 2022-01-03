package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.login.activity.SignInActivity;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.referralcode.model.CheckValidReferralCode;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.OtpApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.ReferralApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.SignupApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.DbAdapter;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends Fragment implements View.OnClickListener {

    Activity activity;
    EditText mnameET, memailET, mphonenumberET, mpasswordET, mdamsidET, mreferralET, mDAMSET, mDAMSpasswordET;
    Button msignUpBtn;
    ImageView mfacebookIV, mgooglePlusIV, mdamsIV, ivBack;
    TextView mloginTV;
    CheckBox mdamsuserCB;
    String name, email, c_code, mobile, password, damstoken, damspass,
            damsid, referral,
            socialType, socialToken, affiliate_referral_code,
            deviceId = "";
    Boolean isDamsUser;
    Progress mprogress;
    TextInputEditText referralCodeET;
    CountryCodePicker ccp;
    User user;

    public static SignUp newInstance() {
        Bundle args = new Bundle();
        SignUp fragment = new SignUp();
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);

        deviceId = ((SignInActivity) activity).deviceId;
        Log.e("register", "deviceId: " + deviceId);
        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        mnameET = view.findViewById(R.id.nameTV);
        memailET = view.findViewById(R.id.emailTV);
        mphonenumberET = view.findViewById(R.id.phonenumberTV);
        mpasswordET = view.findViewById(R.id.passwordET);
        mdamsidET = view.findViewById(R.id.damsidET);
        mreferralET = view.findViewById(R.id.referralET);

        mdamsIV = view.findViewById(R.id.damsIV);
        msignUpBtn = view.findViewById(R.id.signupBtn);

        mdamsuserCB = view.findViewById(R.id.damsidCB);

        mloginTV = view.findViewById(R.id.loginTV);

        mfacebookIV = view.findViewById(R.id.fbIV);
        mgooglePlusIV = view.findViewById(R.id.gpIV);
        referralCodeET = view.findViewById(R.id.referralCodeET);

        msignUpBtn.setOnClickListener(this);
        mloginTV.setOnClickListener(this);
        mfacebookIV.setOnClickListener(this);
        mgooglePlusIV.setOnClickListener(this);
        mdamsIV.setOnClickListener(this);


        ccp = view.findViewById(R.id.ccp);
        ccp.enableHint(false);
        ccp.registerPhoneNumberTextView(mphonenumberET);
        referralCodeET.setHint("Enter Affiliate Referral Code");
        referralCodeET.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                referralCodeET.setHint("");
            } else {
                referralCodeET.setHint("Enter Affiliate Referral Code");
            }
        });
        mdamsuserCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mdamsidET.setEnabled(isChecked);
            if (!mdamsuserCB.isChecked()) {
                if (mdamsidET.getError() != null) mdamsidET.setError(null);
            }
            if (mdamsuserCB.isChecked()) mdamsidET.setVisibility(View.VISIBLE);
            else mdamsidET.setVisibility(View.GONE);
        });

        ccp.setOnCountryChangeListener(country -> {
            if (mphonenumberET.getError() != null) {
                mphonenumberET.setError(null);
            }
        });

        Helper.CaptializeFirstLetter(mnameET);
    }

    @Override
    public void onClick(View view) {
        user = User.newInstance();

        switch (view.getId()) {
            case R.id.signupBtn:
                Log.e("number", ccp.getFullNumberWithPlus() + " " + ccp.getNumber() + " " + ccp.getPhoneNumber() + " getSelectedCountryCodeWithPlus " + ccp.getSelectedCountryCodeWithPlus() + " " + ccp.isValid());

                if (!GenericUtils.isEmpty(Objects.requireNonNull(referralCodeET.getText()).toString())) {
                    affiliate_referral_code = Helper.GetText(referralCodeET);
                    networkCallForCheckValidReferralCode();
                } else {
                    checkValidation();
                }

                break;
            case R.id.loginTV:
            case R.id.iv_back:
                ((SignInActivity) activity).customBackPress();
                break;
            case R.id.fbIV:
                socialType = Const.SOCIAL_TYPE_FACEBOOK;
                ((SignInActivity) activity).LoginMaster(socialType);
                break;
            case R.id.gpIV:
                socialType = Const.SOCIAL_TYPE_GMAIL;
                ((SignInActivity) activity).LoginMaster(socialType);
                break;

            case R.id.damsIV:
                ((SignInActivity) activity).replaceFragment(DamsLoginFragment.newInstance());
                break;
            default:
                break;
        }
    }

    public void CheckDAMSLoginValidation() {
        damstoken = Helper.GetText(mDAMSET);
        damspass = Helper.GetText(mDAMSpasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(damstoken))
            isDataValid = Helper.DataNotValid(mDAMSET);

        else if (TextUtils.isEmpty(damspass))
            isDataValid = Helper.DataNotValid(mDAMSpasswordET);

        if (isDataValid) {
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (GenericUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setDams_username(damstoken);
            user.setDams_password(damspass);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForUserDamsLoginAuth();//
            // NetworkAPICall(API.API_USER_DAMS_LOGIN_AUTHENTICATION, true);
        }
    }

    public void getDAMSLoginDialog(final Activity ctx) {
// custom dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dams_login);
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        Button mDAMSLoginbtn = dialog.findViewById(R.id.loginBtn);
        Button mDAMScancelbtn = dialog.findViewById(R.id.cancelBtn);
        mDAMSET = dialog.findViewById(R.id.damstokenET);
        mDAMSpasswordET = dialog.findViewById(R.id.damspassET);
        // if button is clicked, close the custom dialog
        mDAMSLoginbtn.setOnClickListener(v -> CheckDAMSLoginValidation());
        mDAMScancelbtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void checkValidation() {
        name = Helper.GetText(mnameET);
        email = Helper.GetText(memailET);
        mobile = Helper.GetText(mphonenumberET);
        c_code = ccp.getSelectedCountryCodeWithPlus();
        password = Helper.GetText(mpasswordET);
        damsid = Helper.GetText(mdamsidET);
        referral = Helper.GetText(mreferralET);
        isDamsUser = mdamsuserCB.isChecked();
        ccp.isValid();
        boolean isDataValid = true;

        if (TextUtils.isEmpty(name))
            isDataValid = Helper.DataNotValid(mnameET);
        else if (TextUtils.isEmpty(email))
            isDataValid = Helper.DataNotValid(memailET);
        else if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            isDataValid = Helper.DataNotValid(memailET, 1);
        else if (TextUtils.isEmpty(mobile))
            isDataValid = Helper.DataNotValid(mphonenumberET);
        else if (ccp.getSelectedCountryCode().equals("91") &&
                Helper.isInValidIndianMobile(mphonenumberET.getText().toString()))
            isDataValid = Helper.DataNotValid(mphonenumberET, 2);
        /*else if ((Patterns.PHONE.matcher(mobile).matches() != true) || (mobile.length() != 10))
            isDataValid = Helper.DataNotValid(mphonenumberET, 2);*/
        else if (TextUtils.isEmpty(password))
            isDataValid = Helper.DataNotValid(mpasswordET);
        else if (password.trim().length() < 8 || password.trim().length() > 15) {
            isDataValid = false;
            Toast.makeText(activity, "Password must contain 8-15 characters..", Toast.LENGTH_SHORT).show();
        } else if (isDamsUser && TextUtils.isEmpty(damsid))
            isDataValid = Helper.DataNotValid(mdamsidET);

        if (isDataValid) {
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setName(name);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setC_code(c_code);
            user.setPassword(password);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setSocial_type(socialType);
            user.setSocial_tokken(socialToken);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            user.setDams_tokken(damsid);
            SharedPreference.getInstance().setLoggedInUser(user);

            if (isDamsUser) {
                networkCallForUserDamsVerification();//NetworkAPICall(API.API_USER_DAMS_VERIFICATION, true);
            } else {
                networkCallForOtp();//NetworkAPICall(API.API_OTP, true);
            }
        }
    }

    public void signUpTask(JSONObject object, String type) {
        try {
            user = User.newInstance();
            switch (type) {
                case Const.SOCIAL_TYPE_FACEBOOK: //facebook picture url
                    user.setProfile_picture(
                            Objects.requireNonNull(Objects.requireNonNull(object.optJSONObject(Const.PICTURE)).
                                    optJSONObject(Const.DATA)).
                                    optString(Const.URL));
                    break;
                case Const.SOCIAL_TYPE_GMAIL://gmail image url
                    user.setProfile_picture(object.optString(Const.IMGURL));
                    break;
                default:
                    break;

            }
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setSocial_type(socialType);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            user.setIs_social(Const.SOCIAL_TYPE_TRUE);
            user.setSocial_tokken(object.getString(Constants.Extras.ID));
            if (object.has(Const.EMAIL))
                user.setEmail(object.getString(Const.EMAIL));
            else
                user.setEmail("");
            user.setName(object.getString(Constants.Extras.NAME));
            SharedPreference.getInstance().setLoggedInUser(user);

            if (user.getMobile() == null) {
                if (activity == null) {
                    activity = getActivity();
                    Helper.GoToMobileVerificationActivity(activity, Const.MOBILEVERIFICATION, 0);
                } else Helper.GoToMobileVerificationActivity(activity, Const.MOBILEVERIFICATION, 0);

            } else {
                networkCallForOtp();//NetworkAPICall(API.API_OTP, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void networkCallForUserDamsVerification() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        SignupApiInterface apiInterface = ApiClient.createService(SignupApiInterface.class);
        Call<JsonObject> response = apiInterface.getUserDamsVerification(damsid);
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
                            networkCallForOtp();//NetworkAPICall(API.API_OTP, true);
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
                mprogress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void networkCallForCheckValidReferralCode() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        ReferralApiInterface apiInterface = ApiClient.createService(ReferralApiInterface.class);
        Call<JsonObject> response = apiInterface.checkAffiliateReferralCode(affiliate_referral_code);
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
                            CheckValidReferralCode checkValidReferralCode = new Gson().fromJson(jsonObject, CheckValidReferralCode.class);
                            if (checkValidReferralCode.getData().equalsIgnoreCase(affiliate_referral_code)) {
                                checkValidation();
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
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
                mprogress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void networkCallForOtp() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.otpVerification(mobile, c_code, email, true);
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
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            String otp = dataJsonObject.optString(Const.OTP);
                            Helper.GoToOtpVerificationActivity(activity, otp, 1, affiliate_referral_code, Const.OTPVERIFICATION);
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
                mprogress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void networkCallForUserDamsLoginAuth() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        SignupApiInterface apiInterface = ApiClient.createService(SignupApiInterface.class);
        Call<JsonObject> response = apiInterface.damsLoginAuthentication(user.getDams_username(), user.getDams_password(),
                user.getIs_social(), user.getDevice_type(), user.getDevice_tokken(),
                (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.INVITEDBY)) ? SharedPreference.getInstance().getString(Const.INVITEDBY) : ""));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Gson gson = new Gson();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                            DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                            JSONObject dataJsonObject = jsonResponse.getJSONObject(Const.DATA);

                            SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true);
                            SharedPreference.getInstance().putBoolean(Const.SYNC_COMPLETE, false);

                            User userProfile = gson.fromJson(dataJsonObject.toString(), User.class);
                            Log.e("String Login", gson.toJson(user));
                            SharedPreference.getInstance().setLoggedInUser(userProfile);
                            SharedPreference.getInstance().putString(Const.LOGGED_IN_USER_TOKEN, userProfile.getLoggedInUserToken());

                            if (userProfile.getIs_course_register().equals("0")) {
                                SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, false);
                                Helper.GoToRegistrationActivity(activity, Const.REGISTRATION, Const.REGISTRATION);
                            } else if (userProfile.getIs_course_register().equals("1") && userProfile.getExpert_following() < Helper.getMinimumFollowerCount()) {
                                Helper.GoToFollowExpertActivity(activity, Const.FOLLOW_THE_EXPERT_FIRST);
                                SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, false);
                            } else {
                                Helper.GoToNextActivity(activity);
                                SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                            }

                        } else {
                            SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showExceptionMsg(activity, t);

            }
        });
    }

}
