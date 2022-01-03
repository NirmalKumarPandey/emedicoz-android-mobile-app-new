package com.emedicoz.app.courses.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.fragment.MyCoursesFragment;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;

public class MyCoursesActivity extends AppCompatActivity {

    ImageView backIV;
    ImageView drawerIV;
    TextView titleTV;
    private LinearLayout errorLayout;
    private FrameLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_mycourses);

        initViews();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, MyCoursesFragment.newInstance(getIntent().getStringExtra(Const.FRAG_TYPE)))
                .commitAllowingStateLoss();

    }

    private void initViews() {
        backIV = findViewById(R.id.iv_back);
        titleTV = findViewById(R.id.tv_title);
        drawerIV = findViewById(R.id.iv_drawer);
        errorLayout = findViewById(R.id.errorLL);
        container = findViewById(R.id.fl_container);

        drawerIV.setVisibility(View.GONE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(40);
        titleTV.setLayoutParams(params);
        titleTV.setText(getIntent().getStringExtra(Const.FRAG_TYPE));

        backIV.setOnClickListener(view1 -> onBackPressed());
    }


    // isError:  Whether there is an error or success response
    // layoutType: Whether there is an internet issue or api Error like "Something went wrong"
    public void replaceErrorLayout(int isError, int layoutType) {
        // 0 is for no data found
        // 1 is for no internet connection
        // 2 is for something went wrong or everything else.
        if (layoutType == 0) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.no_post_found);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.no_data_found));
            findViewById(R.id.errorMessageTV2).setVisibility(View.GONE);
            findViewById(R.id.tryAgainBtn).setVisibility(View.GONE);
            findViewById(R.id.enrollNow).setVisibility(View.GONE);
            findViewById(R.id.oops).setVisibility(View.GONE);

        } else if (layoutType == 1) {
            if (!Helper.isConnected(this)) {
                ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.no_internet);
                ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.internet_error_message));
            } else {
                ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.something_went_wrong);
                ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.exception_api_error_message));

            }
        } else if (layoutType == 2) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.something_went_wrong);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.exception_api_error_message));
        } else if (layoutType == 3) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.by_course);
            findViewById(R.id.oops).setVisibility(View.GONE);
            findViewById(R.id.tryAgainBtn).setVisibility(View.GONE);
            findViewById(R.id.enrollNow).setVisibility(View.VISIBLE);
            findViewById(R.id.enrollNow).setOnClickListener(view -> {
                Helper.GoToNextActivity(MyCoursesActivity.this);
            });
            ((TextView) findViewById(R.id.errorMessageTV)).setText(R.string.no_course);
            ((TextView) findViewById(R.id.errorMessageTV2)).setText(R.string.purchases_course_msg);
        }
        errorLayout.setVisibility((isError == 1 ? View.VISIBLE : View.GONE));
        container.setVisibility((isError != 1 ? View.VISIBLE : View.GONE));
    }

}
