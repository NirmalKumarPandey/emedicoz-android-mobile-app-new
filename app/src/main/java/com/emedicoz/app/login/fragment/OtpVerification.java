package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.OtpApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.DbAdapter;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OtpVerification extends
        Fragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    //    EditText otp1ET, otp2ET, otp3ET, otp4ET;
    Button verifyBtn;
    String otp;
    String otpText = "";
    String affiliate_referral_code_by;
    int type;
    BroadcastReceiver broadcastReceiver;
    Gson gson;
    User user;
    Progress mProgress;
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private TextView mResendOtpTV;
    private TextView verifyTextTV;
    Activity activity;

    public OtpVerification() {
    }

    public static OtpVerification newInstance(String otp, int type, String affiliateCode) {
        OtpVerification fragment = new OtpVerification();
        Bundle args = new Bundle();
        args.putString(Const.OTP, otp);
        args.putInt(Constants.Extras.TYPE, type);
        args.putString(Const.AFFILIATE_CODE, affiliateCode);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        activity = getActivity();
        if (getArguments() != null) {
            otp = getArguments().getString(Const.OTP);
            type = getArguments().getInt(Constants.Extras.TYPE);
            affiliate_referral_code_by = getArguments().getString(Const.AFFILIATE_CODE);
        }
    }

    public void autoReadMessage() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (b == null) return;
                String message = b.getString(Constants.Extras.MESSAGE);
                Log.e("newmesage", "" + message);
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

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otpverifcation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mPinFirstDigitEditText = view.findViewById(R.id.OPT1ET);
        mPinSecondDigitEditText = view.findViewById(R.id.OPT2ET);
        mPinThirdDigitEditText = view.findViewById(R.id.OPT3ET);
        mPinForthDigitEditText = view.findViewById(R.id.OPT4ET);
        mPinHiddenEditText = view.findViewById(R.id.pin_hidden_edittext);
        mResendOtpTV = view.findViewById(R.id.resendotpTV);
        verifyTextTV = view.findViewById(R.id.verifyTextTV);

        if (type == 2) {
            verifyTextTV.setText(String.format("%s\n%s", activity.getResources().getString(R.string.verifying_your_number_n_we_have_sent_an_otp_on_your_number).split("\\n")[0],
                    SharedPreference.getInstance().getString(Const.FORGETPASSWORD)));
        } else {
            verifyTextTV.setText(activity.getResources().getString(R.string.verifying_your_number_n_we_have_sent_an_otp_on_your_number));
        }

        setPINListeners();
        verifyBtn = view.findViewById(R.id.verifyBtn);
        user = User.getInstance();
        verifyBtn.setOnClickListener(v -> {
            otpText = mPinHiddenEditText.getText().toString().trim();
            otp = otpText;
            Log.e("OtpActivity", "opt:  " + otpText);

            networkCallForValidateOtp();
        });

        mResendOtpTV.setOnClickListener(v -> {
            if (type != 3)
                user = SharedPreference.getInstance().getLoggedInUser();
            else
                user = User.getInstance();

            if (user != null && !TextUtils.isEmpty(user.getMobile())) {
                if (type == 1) {
                    networkCallForOtp();//NetworkAPICall(API.API_OTP, true);
                } else if (type == 2)
                    networkCallForChangePassOtp();//NetworkAPICall(API.API_CHANGE_PASSWORD_OTP, true);
                else if (type == 3)
                    networkCallForOtpMobileChange();//NetworkAPICall(API.API_OTP_FOR_MOBILE_CHANGE, true);
            }
        });

        autoReadMessage();
    }

    private void networkCallForValidateOtp() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.validateOtpForMobile(user.getMobile(), otpText);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            if (type == 2) {
                                Helper.GoToChangePasswordActivity(activity, otp, Const.CHANGEPASSWORD);
                            } else if (type == 1) {
                                networkCallForRegisterUser();//NetworkAPICall(API.API_REGISTER_USER, true);
                                SharedPreference.getInstance().putBoolean(Const.IS_PHONE_VERIFIED, true);
                            } else if (type == 3) {
                                networkCallForUpdateMobNo();//NetworkAPICall(API.API_UPDATE_MOBILE_NUMBER, true);
                                SharedPreference.getInstance().putBoolean(Const.IS_PHONE_VERIFIED, true);
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
                mProgress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void networkCallForRegisterUser() {
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.registerUser(user.getName(), user.getEmail(), user.getPassword(), user.getMobile(), user.getC_code(), user.getIs_social(),
                user.getSocial_type(), user.getSocial_tokken(), user.getDevice_type(), user.getDevice_tokken(), user.getDams_tokken(), user.getProfile_picture(),
                SharedPreference.getInstance().getString(Const.LAST_KNOWN_LOCATION, ""), (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.INVITEDBY)) ? SharedPreference.getInstance().getString(Const.INVITEDBY) : ""), affiliate_referral_code_by);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                        gson = new Gson();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true);
                            SharedPreference.getInstance().putBoolean(Const.SYNC_COMPLETE, false);
                            SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true);

                            SharedPreference.getInstance().ClearLoggedInUser();
                            User userProfile = gson.fromJson(dataJsonObject.toString(), User.class);
                            SharedPreference.getInstance().putString(Const.LOGGED_IN_USER_TOKEN, userProfile.getLoggedInUserToken());
                            SharedPreference.getInstance().setLoggedInUser(userProfile);

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
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void networkCallForOtpMobileChange() {
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.opForMobileChange(SharedPreference.getInstance().getLoggedInUser().getId(), user.getC_code(), user.getMobile(), true);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            otp = dataJsonObject.optString(Const.OTP);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkCallForOtp() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.otpVerification(user.getMobile(), user.getC_code(), user.getEmail(), true);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            otp = dataJsonObject.optString(Const.OTP);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void networkCallForChangePassOtp() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.changePasswordOtp(user.getC_code(), user.getMobile(), true);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            otp = dataJsonObject.optString(Const.OTP);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void networkCallForUpdateMobNo() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.updateMobileNumber(SharedPreference.getInstance().getLoggedInUser().getId(), user.getC_code(), user.getMobile());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            activity.finish();
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

    /**
     * Sets listeners for EditText fields.
     */
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

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
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

            case R.id.OPT4ET:

            case R.id.OPT3ET:

            case R.id.OPT2ET:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = view.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
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
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }
}
