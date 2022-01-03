package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.login.activity.SignInActivity;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends Fragment {

    Activity activity;
    Button submitBtn;
    EditText newPasswordET;
    EditText retryPasswordET;
    ImageView ivLogo;
    TextInputLayout oldPasswordTIL;
    String retryPassword;
    String newPassword;

    String otp;
    Progress mProgress;

    public static ChangePassword newInstance(String otp) {
        ChangePassword fragment = new ChangePassword();
        Bundle args = new Bundle();
        args.putString(Const.OTP, otp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            otp = getArguments().getString(Const.OTP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_changepassword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        submitBtn = view.findViewById(R.id.submitBtn);
        ivLogo = view.findViewById(R.id.iv_logo);
        oldPasswordTIL = view.findViewById(R.id.oldPasswordTIL);
        newPasswordET = view.findViewById(R.id.newpasswordET);
        retryPasswordET = view.findViewById(R.id.retrypasswordET);
        submitBtn.setOnClickListener(view1 -> checkValidations());
        ivLogo.setVisibility(View.GONE);
        oldPasswordTIL.setVisibility(View.GONE);
    }

    private void networkCallForUpdatePass() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        User user = SharedPreference.getInstance().getLoggedInUser();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.updatePasswordviaOtp(user.getC_code(), user.getMobile(), otp,
                user.getPassword());
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
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constants.Extras.TYPE, "LOGIN");
                            startActivity(intent);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Constants.Extras.MESSAGE));
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

    public void checkValidations() {
        newPassword = Helper.GetText(newPasswordET);
        retryPassword = Helper.GetText(retryPasswordET);

        boolean isDataValid = true;

        if (TextUtils.isEmpty(newPassword))
            isDataValid = Helper.DataNotValid(newPasswordET);
        else if (TextUtils.isEmpty(retryPassword))
            isDataValid = Helper.DataNotValid(retryPasswordET);
        else if (Helper.GetText(newPasswordET).length() < 8 || Helper.GetText(newPasswordET).length() > 15) {
            Toast.makeText(activity, "Password must contain 8-15 characters..", Toast.LENGTH_SHORT).show();
            isDataValid = false;
        } else if (!newPassword.equals(retryPassword)) {
            isDataValid = false;
            Toast.makeText(activity, "Password does not match", Toast.LENGTH_SHORT).show();
        }
        if (isDataValid) {
            User user = User.getInstance();
            user.setPassword(newPassword);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForUpdatePass();//NetworkAPICall(API.API_UPDATE_PASSWORD_WITH_OTP, true);
        }
    }
}
