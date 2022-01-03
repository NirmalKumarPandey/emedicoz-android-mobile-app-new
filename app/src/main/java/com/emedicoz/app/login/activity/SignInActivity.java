package com.emedicoz.app.login.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.FacebookLoginHandler;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.login.fragment.Login;
import com.emedicoz.app.login.fragment.LoginTypeFragment;
import com.emedicoz.app.login.fragment.SignUp;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.Utils.service.ErrorAlertBroadcastReceiver;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.GooglePlus_login;
import com.emedicoz.app.utilso.Helper;
import com.facebook.FacebookException;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements FacebookLoginHandler.FacebookLoginCallback, GooglePlus_login.OnClientConnectedListener {
    private static final String TAG = "SignInActivity";
    final int REQUEST_READ_PHONE_STATE = 100;
    final int REQUEST_READ_PHONE_STATE1 = 101;
    //    public ViewPager viewPager;
    public Progress mprogress;
    public String deviceId;
    ViewPagerAdapter adapter;
    SignUp signUpFrag;
    Login loginFrag;
    CharSequence[] Titles = {"Sign Up", "Log In"};
    //    private TabLayout tabLayout;
    private FacebookLoginHandler facebookLoginHandler;
    private GooglePlus_login googlePlus_login;
    private FragmentManager fragmentManager;
    public String appLinkData;

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "DEBUG");
        } else {
            //networkCallForAppVersion();
        }//NC.NetworkAPICall(API.API_GET_APP_VERSION, false);
        registerReceiver(new ErrorAlertBroadcastReceiver(), new IntentFilter(Const.ERROR_ALERT_INTENT_FILTER));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebookLoginHandler.callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GooglePlus_login.RC_SIGN_IN) {
            googlePlus_login.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_sign_in);

        mprogress = new Progress(this);
        mprogress.setCancelable(false);
        deviceId = FirebaseInstanceId.getInstance().getToken();

        AppEventsLogger.activateApp(this);
        Helper.logUser(this);

        googlePlus_login = new GooglePlus_login(this);

        // INITIALIZE THE FACEBOOK SDK
        facebookLoginHandler = new FacebookLoginHandler(this, this, mprogress);

        int permissionCheck1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");//
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int permissionCheck4 = ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO");
        int permissionCheck7 = ContextCompat.checkSelfPermission(this, "android.permission.READ_PROFILE");

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck2 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck3 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck4 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck7 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {"android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.CAMERA",
                            "android.permission.RECORD_AUDIO",
                            "android.permission.READ_PROFILE"}, REQUEST_READ_PHONE_STATE);
        }
        addFragment(LoginTypeFragment.newInstance());

        appLinkData = getIntent().getStringExtra("appLinkData");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case REQUEST_READ_PHONE_STATE1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            default:
                break;
        }
    }

    public void LoginMaster(String socialType) {
        if (Helper.isConnected(this)) {
            switch (socialType) {
                case Const.SOCIAL_TYPE_FACEBOOK:
                    facebookLoginHandler.logoutViaFacebook();
                    facebookLoginHandler.onFacebookButtonClick();
                    break;
                case Const.SOCIAL_TYPE_GMAIL:
//                    mprogress.show();
                    googlePlus_login.signIn();
                    break;
            }
        } else {
            mprogress.hide();
            Toast.makeText(this, Const.NO_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

//    private void setupTabs() {
//        for (int i = 0; i < adapter.getCount(); i++) {
//            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//            textView.setText(Titles[i]);
//
//            tabLayout.getTabAt(i).setCustomView(textView);
//        }
//    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), viewPager);

        signUpFrag = new SignUp();
        loginFrag = new Login();

        adapter.addFrag(signUpFrag, Const.SIGNUP);
        adapter.addFrag(loginFrag, Const.LOGIN);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onGoogleProfileFetchComplete(JSONObject object) {
        mprogress.hide();
        Log.e("SIGNUP_Google", object.toString());
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fourFragmentLayout);
        if (fragment instanceof LoginTypeFragment)
            ((LoginTypeFragment) fragment).logInTask(object, Const.SOCIAL_TYPE_GMAIL);
        else if (fragment instanceof Login)
            ((Login) fragment).logInTask(object, Const.SOCIAL_TYPE_GMAIL);
        else if (fragment instanceof SignUp)
            ((SignUp) fragment).signUpTask(object, Const.SOCIAL_TYPE_GMAIL);
    }

    @Override
    public void onClientFailed() {
        GenericUtils.showToast(SignInActivity.this, "Error occurred!");
        mprogress.hide();
    }

    private void networkCallForAppVersion() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getAppVersion();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data;

                            data = GenericUtils.getJsonObject(jsonResponse);
                            String androidv = data.optString("android");
                            int aCode = Integer.parseInt(androidv.isEmpty() ? "0" : androidv.trim());
                            if (Helper.getVersionCode(SignInActivity.this) < aCode) {
                                Helper.getVersionUpdateDialog(SignInActivity.this);
                            }
                        } else {
                            JSONObject data;
                            String popupMessage = "";
                            try {
                                data = GenericUtils.getJsonObject(jsonResponse);
                                popupMessage = data.getString("popup_msg");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RetrofitResponse.handleAuthCode(SignInActivity.this, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(SignInActivity.this, t);
            }
        });

    }

    @Override
    public void facebookOnSuccess(JSONObject object, GraphResponse response) {
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fourFragmentLayout);
        if (fragment instanceof LoginTypeFragment)
            ((LoginTypeFragment) fragment).logInTask(object, Const.SOCIAL_TYPE_FACEBOOK);
        else if (fragment instanceof Login)
            ((Login) fragment).logInTask(object, Const.SOCIAL_TYPE_FACEBOOK);
        else if (fragment instanceof SignUp)
            ((SignUp) fragment).signUpTask(object, Const.SOCIAL_TYPE_FACEBOOK);
    }

    @Override
    public void facebookOnCancel() {
        GenericUtils.showToast(SignInActivity.this, "Login cancelled");
    }

    @Override
    public void facebookOnError(FacebookException error) {
        GenericUtils.showToast(SignInActivity.this, error.getMessage());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SignInActivity", "destroyed");
    }

    @Override
    public void onBackPressed() {
        customBackPress();
    }

    public void customBackPress() {
        Helper.closeKeyboard(this);
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fourFragmentLayout);
        if (fragment instanceof LoginTypeFragment) {
            finish();
        } else {
            popAllStack();
            if (!isFinishing())
                addFragment(LoginTypeFragment.newInstance());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super().
    }

    public void replaceFragment(Fragment fragment) {
        if (isFinishing()) return;

        Log.d(TAG, "replaceFragment: " + getSupportFragmentManager().getBackStackEntryCount());
        String backStateName = fragment.getClass().getSimpleName();
        FragmentManager manager = this.getSupportFragmentManager();

        try {
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.fourFragmentLayout, fragment, backStateName);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(backStateName);
                ft.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fourFragmentLayout, fragment);
        ft.commitAllowingStateLoss();
    }

    public void popAllStack() {
        FragmentManager fm = this.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPager pager;

        public ViewPagerAdapter(FragmentManager manager, ViewPager pager) {
            super(manager);
            this.pager = pager;
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
