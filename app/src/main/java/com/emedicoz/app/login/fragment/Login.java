package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.login.activity.LoginCatActivity;
import com.emedicoz.app.login.activity.SignInActivity;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.DbAdapter;
import com.emedicoz.app.utilso.GenericUtils;
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

public class Login extends Fragment implements View.OnClickListener {

    public String email;
    public String password;
    public String socialType;
    public String socialToken;
    public String deviceId;
    Activity activity;
    EditText mEmailET;
    EditText mPasswordET;
    Button mLogInBtn;
    TextView mForgetPasswordTV, terms;
    ImageView mFacebookIV, gpIV, mDamsIV;
    User user;
    TextView registerTV;
    ImageView ivBack;
    Progress mProgress;
    String appLinkData;
    LinearLayout damsSignInll;


    public static Login newInstance() {

        Bundle args = new Bundle();
        Login fragment = new Login();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceId = ((SignInActivity) activity).deviceId;
        Log.e("register", "deviceId: " + deviceId);

        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);

        mEmailET = view.findViewById(R.id.emailTV);
        mPasswordET = view.findViewById(R.id.passwordET);
        damsSignInll = view.findViewById(R.id.damsSignInll);
        mFacebookIV = view.findViewById(R.id.fbIV);
        gpIV = view.findViewById(R.id.gpIV);
        mDamsIV = view.findViewById(R.id.damsIV);

        mForgetPasswordTV = view.findViewById(R.id.forgetpasswordTV);
        mLogInBtn = view.findViewById(R.id.loginBtn);

        registerTV = view.findViewById(R.id.registerTV);
        ivBack = view.findViewById(R.id.iv_back);

        terms = view.findViewById(R.id.terms);
        terms.setOnClickListener(this);

        mFacebookIV.setOnClickListener(this);
        gpIV.setOnClickListener(this);
        mLogInBtn.setOnClickListener(this);
        mForgetPasswordTV.setOnClickListener(this);
        mDamsIV.setOnClickListener(this);

        registerTV.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        damsSignInll.setOnClickListener(this);

        appLinkData = ((SignInActivity) getActivity()).appLinkData;
    }

    public void checkValidation() {
        email = Helper.GetText(mEmailET);
        password = Helper.GetText(mPasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(email))
            isDataValid = Helper.DataNotValid(mEmailET);
        else if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            isDataValid = Helper.DataNotValid(mEmailET, 1);
        else if (TextUtils.isEmpty(password))
            isDataValid = Helper.DataNotValid(mPasswordET);

        if (isDataValid) {
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (GenericUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setEmail(email);
            user.setPassword(password);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setSocial_type(socialType);
            user.setSocial_tokken(socialToken);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForUserLoginAuth();//NetworkAPICall(API.API_USER_LOGIN_AUTHENTICATION, true);
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
                if (GenericUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }

            user.setIs_social(Const.SOCIAL_TYPE_TRUE);

            user.setSocial_tokken(object.getString(Constants.Extras.ID));
            if (object.has(Const.EMAIL))
                user.setEmail(object.getString(Const.EMAIL));
            else
                user.setEmail("");

            user.setSocial_type(socialType);

            if (object.has(Constants.Extras.NAME))
                user.setName(object.getString(Constants.Extras.NAME));
            else
                user.setName("");

            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForUserLoginAuth();//NetworkAPICall(API.API_USER_LOGIN_AUTHENTICATION, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        user = User.newInstance();
        switch (view.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;
            case R.id.forgetpasswordTV:
                Intent intent = new Intent(activity, LoginCatActivity.class);
                intent.putExtra(Const.FRAG_TYPE, Const.FORGETPASSWORD);
                activity.startActivity(intent);
                break;
            case R.id.terms:
                Helper.GoToWebViewActivity(activity, Const.TERMS_URL);
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
            case R.id.registerTV:
                ((SignInActivity) activity).replaceFragment(SignUp.newInstance());
                break;

            case R.id.iv_back:
                ((SignInActivity) activity).customBackPress();
                break;
            case R.id.damsSignInll:
                ((SignInActivity) Objects.requireNonNull(getActivity())).replaceFragment(DamsLoginFragment.newInstance());
                break;
            default:
                break;
        }

    }

    private void networkCallForUserLoginAuth() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        LoginApiInterface apiInterface = ApiClient.createService(LoginApiInterface.class);
        Call<JsonObject> response = apiInterface.userLoginAuthentication(user.getEmail(), user.getPassword(),
                user.getIs_social(), user.getSocial_type(), user.getSocial_tokken(), user.getDevice_type(), user.getDevice_tokken());
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
                        try {
                            Log.e("String Login", jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                                DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                                JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);
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
                                    Helper.GoToNextActivity(activity, appLinkData);
                                    activity.finish();
                                    SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                                }

                            } else {
                                RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE),
                                        jsonResponse.getString(Const.AUTH_MESSAGE));
                                SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
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
