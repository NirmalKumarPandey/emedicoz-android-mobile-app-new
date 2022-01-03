package com.emedicoz.app.login.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DamsLoginFragment extends Fragment implements View.OnClickListener {

    ImageView ivBack;
    TextView loginButton;
    TextView forgotPassword;
    TextView terms;
    TextView registerTv;
    EditText damstokenET;
    EditText passEt;
    String damstoken;
    String damspass;
    String deviceId;
    LinearLayout emailSignInll;
    Progress mprogress;
    User user;
    Activity activity;


    public DamsLoginFragment() {
        // Required empty public constructor
        // shri ram test
    }

    public static DamsLoginFragment newInstance() {
        DamsLoginFragment fragment = new DamsLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);
        activity = getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceId = ((SignInActivity) activity).deviceId;

        ivBack = view.findViewById(R.id.iv_back);
        emailSignInll = view.findViewById(R.id.emailSignInll);
        loginButton = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgetpasswordTV);
        terms = view.findViewById(R.id.terms);
        registerTv = view.findViewById(R.id.register_tv);
        damstokenET = view.findViewById(R.id.damstokenET);
        passEt = view.findViewById(R.id.passwordET);

        ivBack.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        terms.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        damstokenET.setOnClickListener(this);
        passEt.setOnClickListener(this);
        emailSignInll.setOnClickListener(this);
    }

    private void networkCallForDamsLoginAuth() {
        if (Helper.isConnected(activity)) {
            mprogress.show();
            LoginApiInterface apiInterface = ApiClient.createService(LoginApiInterface.class);
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
                        Gson gson = new Gson();

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
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
                                    SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                                    Helper.GoToNextActivity(activity);
                                    activity.finish();
                                }


                            } else {
                                SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                                String popupMessage = jsonResponse.optString("popup_msg");
                                RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    Helper.showExceptionMsg(activity, t);
                }
            });
        } else {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dams_login, container, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ((SignInActivity) Objects.requireNonNull(getActivity())).customBackPress();
                break;
            case R.id.loginBtn:
                checkDAMSLoginValidation();
                break;
            case R.id.forgetpasswordTV:
                Intent intent = new Intent(activity, LoginCatActivity.class);
                intent.putExtra(Const.FRAG_TYPE, Const.FORGETPASSWORDDAMS);
                activity.startActivity(intent);
                break;
            case R.id.terms:
                Helper.GoToWebViewActivity(activity, Const.TERMS_URL);
                break;
            case R.id.register_tv:
                ((SignInActivity) Objects.requireNonNull(getActivity())).replaceFragment(SignUp.newInstance());
                break;
            case R.id.emailSignInll:
                ((SignInActivity) Objects.requireNonNull(getActivity())).replaceFragment(Login.newInstance());
                break;
            default:
                break;
        }
    }

    public void checkDAMSLoginValidation() {
        if (user == null) user = User.newInstance();
        damstoken = Helper.GetText(damstokenET);
        damspass = Helper.GetText(passEt);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(damstoken))
            isDataValid = Helper.DataNotValid(damstokenET);

        else if (TextUtils.isEmpty(damspass))
            isDataValid = Helper.DataNotValid(passEt);

        if (isDataValid) {
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (deviceId == null || deviceId.equals("")) {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) return;

                                deviceId = Objects.requireNonNull(task.getResult()).getToken();
                            });
                }
            }
            user.setDams_username(damstoken);
            user.setDams_password(damspass);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            networkCallForDamsLoginAuth();//NetworkAPICall(API.API_USER_DAMS_LOGIN_AUTHENTICATION, true);
        }
    }
}
