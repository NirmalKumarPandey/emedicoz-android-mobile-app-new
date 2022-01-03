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
import com.emedicoz.app.retrofit.apiinterfaces.ForgetDamsPassApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class forgetDamspassword extends Fragment {

    Activity activity;
    EditText mphonenumberET;
    Button msubmitBtn;
    String text = "";
    Progress mprogress;

    public forgetDamspassword() {
        // Required empty public constructor
    }

    public static forgetDamspassword newInstance() {
        return new forgetDamspassword();
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
        return inflater.inflate(R.layout.fragment_forgetpassword_dams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);

        mphonenumberET = view.findViewById(R.id.phonenumberTV);
        msubmitBtn = view.findViewById(R.id.submitBtn);

        msubmitBtn.setOnClickListener(view1 -> checkValidation());
    }

    public void checkValidation() {
        text = Helper.GetText(mphonenumberET);


        boolean isDataValid = true;

        if (TextUtils.isEmpty(text))
            isDataValid = Helper.DataNotValid(mphonenumberET);

        if (isDataValid) {
            User user = User.getInstance();

            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForForgetPass();//NetworkAPICall(API.API_FORGET_PASS_DAMS, true);
        }
    }

    private void networkCallForForgetPass() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        ForgetDamsPassApiInterface apiInterface = ApiClient.createService(ForgetDamsPassApiInterface.class);
        Call<JsonObject> response = apiInterface.forgetpass(text);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            callBackPress();
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            callBackPress();
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

    private void callBackPress() {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

}
