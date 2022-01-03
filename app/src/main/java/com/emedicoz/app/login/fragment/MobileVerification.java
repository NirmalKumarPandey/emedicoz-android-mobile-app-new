package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.JsonObject;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MobileVerification extends Fragment {

    Activity activity;
    Button verifyBtn;
    EditText mobileET;
    CountryCodePicker countryCodePicker;
    String mobile, cCode;
    int type;
    User user;
    Progress mProgress;

    public MobileVerification() {
    }

    public static MobileVerification newInstance(int type) {
        MobileVerification fragment = new MobileVerification();
        Bundle args = new Bundle();
        args.putInt(Constants.Extras.TYPE, type);
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
            type = getArguments().getInt(Constants.Extras.TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobileverification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verifyBtn = view.findViewById(R.id.verifyBtn);
        mobileET = view.findViewById(R.id.mobileET);
        countryCodePicker = view.findViewById(R.id.ccp);

        countryCodePicker.registerPhoneNumberTextView(mobileET);
        countryCodePicker.enableHint(false);

        countryCodePicker.setOnCountryChangeListener(country -> {
            if (mobileET.getError() != null) {
                mobileET.setError(null);
            }
        });

        verifyBtn.setOnClickListener(v -> checkValidation());
    }


    public void checkValidation() {
        mobile = Helper.GetText(mobileET);
        cCode = countryCodePicker.getSelectedCountryCodeWithPlus();

        boolean isDataValid = true;

        if (TextUtils.isEmpty(mobile))
            isDataValid = Helper.DataNotValid(mobileET);
        else if (countryCodePicker.getSelectedCountryCode().equals("91") && Helper.isInValidIndianMobile(mobile))
            isDataValid = Helper.DataNotValid(mobileET, 2);

        if (isDataValid) {
            user = User.getInstance();
            user.setMobile(mobile);
            user.setC_code(cCode);
            if (type != 1) {
                SharedPreference.getInstance().setLoggedInUser(user);
                networkCallForOtp();//NetworkAPICall(API.API_OTP, true);
            } else {
                networkCallForMobileChange();//NetworkAPICall(API.API_OTP_FOR_MOBILE_CHANGE, true);
            }
        }
    }

    private void networkCallForOtp() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.otpVerification(mobile, cCode, user.getEmail(), true);
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
                            String otp = dataJsonObject.optString(Const.OTP);
                            Helper.GoToOtpVerificationActivity(activity, otp, 1, "", Const.OTPVERIFICATION); // for registration
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
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkCallForMobileChange() {
        mProgress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.opForMobileChange(SharedPreference.getInstance().getLoggedInUser().getId(), cCode, mobile, true);
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
                            String otp = dataJsonObject.optString(Const.OTP);
                            Helper.GoToOtpVerificationActivity(activity, otp, 3, "", Const.OTPVERIFICATION); // for mobile number change
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

}
