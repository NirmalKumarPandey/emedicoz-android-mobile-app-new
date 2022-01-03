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

public class forgetpassword extends Fragment {

    Activity activity;
    EditText mphonenumberET;
    CountryCodePicker countryCodePicker;
    Button msubmitBtn;
    Progress mprogress;

    String phone, c_code;

    public forgetpassword() {
        // Required empty public constructor
    }

    public static forgetpassword newInstance() {
        return new forgetpassword();
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
        return inflater.inflate(R.layout.fragment_forgetpassword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);

        mphonenumberET = view.findViewById(R.id.phonenumberTV);
        countryCodePicker = view.findViewById(R.id.ccp);

        countryCodePicker.enableHint(false);
        countryCodePicker.registerPhoneNumberTextView(mphonenumberET);

        countryCodePicker.enableSetCountryByTimeZone(true);

        countryCodePicker.setOnCountryChangeListener(country -> {
            if (mphonenumberET.getError() != null) {
                mphonenumberET.setError(null);
            }
        });
        msubmitBtn = view.findViewById(R.id.submitBtn);

        msubmitBtn.setOnClickListener(v -> checkValidation());
    }

    public void checkValidation() {
        phone = Helper.GetText(mphonenumberET);
        c_code = countryCodePicker.getSelectedCountryCodeWithPlus();

        boolean isDataValid = true;

        if (TextUtils.isEmpty(phone))
            isDataValid = Helper.DataNotValid(mphonenumberET);
        else if (countryCodePicker.getSelectedCountryCode().equals("91") && Helper.isInValidIndianMobile(phone))
            isDataValid = Helper.DataNotValid(mphonenumberET, 2);

        if (isDataValid) {
            User user = User.getInstance();
            user.setMobile(phone);
            user.setC_code(c_code);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForChangePassOtp();//NetworkAPICall(API.API_CHANGE_PASSWORD_OTP, true);
        }
    }

    private void networkCallForChangePassOtp() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        OtpApiInterface apiInterface = ApiClient.createService(OtpApiInterface.class);
        Call<JsonObject> response = apiInterface.changePasswordOtp(c_code, phone, true);
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
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
                            String otp = dataJsonObject.optString(Const.OTP);
                            SharedPreference.getInstance().putString(Const.FORGETPASSWORD, jsonResponse.optString(Constants.Extras.MESSAGE));
                            Helper.GoToOtpVerificationActivity(activity, otp, 2, "", Const.OTPVERIFICATION);
                        } else {
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
