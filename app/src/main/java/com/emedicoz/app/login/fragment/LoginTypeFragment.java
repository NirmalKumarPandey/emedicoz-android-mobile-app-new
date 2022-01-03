package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.DbAdapter;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginTypeFragment extends Fragment implements View.OnClickListener {

    TextView registerTV, damsLoginTV, emailLoginTV, terms;
    ImageView fbIV, gloginIV;
    String socialType, deviceId;
    User user;
    Progress mprogress;
    private SignInActivity signInActivity;
    Activity activity;

    public LoginTypeFragment() {
        // Required empty public constructor
    }


    public static LoginTypeFragment newInstance() {
        LoginTypeFragment fragment = new LoginTypeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        signInActivity = ((SignInActivity) context);
    }

    private void networkCallForLoginAuth() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        LoginApiInterface apiInterface = ApiClient.createService(LoginApiInterface.class);
        Call<JsonObject> response = apiInterface.userLoginAuthentication(user.getEmail(), user.getPassword(),
                user.getIs_social(), user.getSocial_type(), user.getSocial_tokken(), user.getDevice_type(),
                user.getDevice_tokken());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("String Login", jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                            DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                            JSONObject dataJsonObject = jsonResponse.getJSONObject(Const.DATA);
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_STREAM_CHANGE, true);
                            SharedPreference.getInstance().putBoolean(Const.IS_LANDING_DATA, true);
                            SharedPreference.getInstance().putBoolean(Const.SYNC_COMPLETE, false);

                            User userProfile = gson.fromJson(dataJsonObject.toString(), User.class);
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

                            RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE),
                                    jsonResponse.getString(Const.AUTH_MESSAGE));
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
                mprogress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_type, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerTV = view.findViewById(R.id.register_tv);
        damsLoginTV = view.findViewById(R.id.dams_login_tv);
        emailLoginTV = view.findViewById(R.id.email_login_tv);
        terms = view.findViewById(R.id.terms);

        fbIV = view.findViewById(R.id.fbIV);
        gloginIV = view.findViewById(R.id.gpIV);

        registerTV.setOnClickListener(this);
        damsLoginTV.setOnClickListener(this);
        emailLoginTV.setOnClickListener(this);
        terms.setOnClickListener(this);

        fbIV.setOnClickListener(this);
        gloginIV.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dams_login_tv:
                signInActivity.replaceFragment(DamsLoginFragment.newInstance());
                break;
            case R.id.email_login_tv:
                signInActivity.replaceFragment(Login.newInstance());
                break;
            case R.id.register_tv:
                signInActivity.replaceFragment(SignUp.newInstance());
                break;
            case R.id.terms:
                Helper.GoToWebViewActivity(activity, Const.TERMS_URL);
                break;
            case R.id.fbIV:
                socialType = Const.SOCIAL_TYPE_FACEBOOK;
                signInActivity.LoginMaster(socialType);
                break;
            case R.id.gpIV:
                socialType = Const.SOCIAL_TYPE_GMAIL;
                signInActivity.LoginMaster(socialType);
                break;
            default:
                break;

        }
    }


    public void logInTask(JSONObject object, String type) {
        if (user == null) user = User.newInstance();
        try {
            switch (type) {
                case Const.SOCIAL_TYPE_FACEBOOK: //facebook picture url
                    user.setProfile_picture(
                            Objects.requireNonNull(Objects.requireNonNull(object.optJSONObject(Const.PICTURE)).
                                    optJSONObject(Const.DATA)).
                                    optString(Const.URL));
                    break;
                case Const.SOCIAL_TYPE_GMAIL://gmail image url
                    user.setProfile_picture(Const.IMGURL);
                    break;

            }
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (deviceId == null || deviceId.equals("")) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }

            user.setIs_social(Const.SOCIAL_TYPE_TRUE);

            user.setSocial_tokken(object.getString(Constants.Extras.ID));
            if (object.has(Const.EMAIL))
                user.setEmail(object.getString(Const.EMAIL));
            else
                user.setEmail("");

            user.setSocial_type(type);

            if (object.has(Constants.Extras.NAME))
                user.setName(object.getString(Constants.Extras.NAME));
            else
                user.setName("");

            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForLoginAuth();//NetworkAPICall(API.API_USER_LOGIN_AUTHENTICATION, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
