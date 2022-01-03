package com.emedicoz.app.login.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemoActivity extends AppCompatActivity {

    final int REQUEST_READ_PHONE_STATE = 100;
    final int REQUEST_READ_PHONE_STATE1 = 101;
    ViewPager mViewPager;
    MyPagerAdapter mPagerAdapter;
    Timer timer;
    int page = 1;
    int permissionCheck, permissionCheck1, permissionCheck2, permissionCheck3,
            permissionCheck4, permissionCheck7, permissionCheck8, permissionCheck9;
    private int[] layouts;
    private ImageView[] dots;
    private LinearLayout dotsLayout;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private Button btn_signup;
    private Button btn_login;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_demo);

        mViewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        Helper.logUser(this);

        layouts = new int[]{R.layout.demo_screen1, R.layout.demo_screen2, R.layout.demo_screen3, R.layout.demo_screen4};

        addBottomDots(0);

        mPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        pageSwitcher(3);
        onclick();
//        CheckDeepLinking();

        permissionCheck1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");//
        permissionCheck2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        permissionCheck3 = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        permissionCheck4 = ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO");
        permissionCheck7 = ContextCompat.checkSelfPermission(this, "android.permission.READ_PROFILE");


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
        } else {

        }
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), seconds * 1000, seconds * 1000);
    }

    public void onclick() {
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, SignInActivity.class);
                intent.putExtra(Constants.Extras.TYPE, "SIGNUP");
                startActivity(intent);
            }
        });
//        btn_signup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendEmailForReferral();
//            }
//        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, SignInActivity.class);
                intent.putExtra(Constants.Extras.TYPE, "LOGIN");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkCallForAppVersion();//NC.NetworkAPICall(API.API_GET_APP_VERSION, false);
    }

    public void sendEmailForReferral() {

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://emedicoz.com/?invitedby=dsda"))
                .setDynamicLinkDomain("wn2d8.app.goo.gl")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
//                .setIosParameters(new DynamicLink.IosParameters.Builder("com.eMedicoz.app").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        String subject = String.format("XYZ wants you to play MyExampleGame!");
        String invitationLink = dynamicLinkUri.toString();
        String msg = "Let's play MyExampleGame together! Use my referrer link: "
                + invitationLink;
        String msgHtml = String.format("<p>Let's play MyExampleGame together! Use my "
                + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
                            JSONObject data = null;

                            data = GenericUtils.getJsonObject(jsonResponse);
                            String androidv = data.optString("android");
                            int aCode = Integer.parseInt(androidv.isEmpty() ? "0" : androidv.trim());
                            if (Helper.getVersionCode(DemoActivity.this) < aCode) {
                                Helper.getVersionUpdateDialog(DemoActivity.this);
                            }
                        } else {
                            RetrofitResponse.getApiData(DemoActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Helper.showExceptionMsg(DemoActivity.this, t);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("DemoActivity", "destroyed");
    }

    private void addBottomDots(int currentPage) {
        dots = new ImageView[layouts.length];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.nonselecteditem_dot);
            dots[i].setPadding(10, 10, 10, 0);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page > layouts.length) {
                        timer.cancel();
                    } else {
                        mViewPager.setCurrentItem(page++);
                    }
                }
            });
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
