package com.emedicoz.app.registration.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.registration.fragment.CompletePaymentFragment;
import com.emedicoz.app.registration.fragment.FillCourseDetailFragment;
import com.emedicoz.app.registration.fragment.FillStudentDetailFragment;
import com.emedicoz.app.utilso.Const;

public class FillRegistrationDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView courseTab;
    TextView studentTab;
    TextView paymentTab;
    TextView toolbarTitle;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_registration_detail);
        title = getIntent().getStringExtra(Const.TITLE);
        initView();
    }

    private void initView() {
        courseTab = findViewById(R.id.courseTab);
        studentTab = findViewById(R.id.studentTab);
        paymentTab = findViewById(R.id.paymentTab);
        toolbarTitle = findViewById(R.id.toolbarTitleTV);
        bindControls();
    }

    private void bindControls() {
        toolbarTitle.setText(title);
        courseTab.setOnClickListener(this);
        studentTab.setOnClickListener(this);
        paymentTab.setOnClickListener(this);
        addFragment(FillCourseDetailFragment.newInstance());
    }

    public void addFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.courseTab:
                replaceFragment(FillCourseDetailFragment.newInstance());
                setSelectedTabBackground(Const.REG_COURSE);
                break;
            case R.id.studentTab:
                replaceFragment(FillStudentDetailFragment.newInstance());
                setSelectedTabBackground(Const.REG_STUDENT);
                break;
            case R.id.paymentTab:
                replaceFragment(CompletePaymentFragment.newInstance());
                setSelectedTabBackground(Const.REG_PAYMENT);
                break;
            default:
                break;

        }
    }

    public void setSelectedTabBackground(String type) {
        courseTab.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        courseTab.setTextColor(ContextCompat.getColor(this, R.color.reg_tab_text_color));
        studentTab.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        studentTab.setTextColor(ContextCompat.getColor(this, R.color.reg_tab_text_color));
        paymentTab.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        paymentTab.setTextColor(ContextCompat.getColor(this, R.color.reg_tab_text_color));

        switch (type) {
            case Const.REG_COURSE:
                courseTab.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_reg_tab_selected));
                courseTab.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.REG_STUDENT:
                studentTab.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_reg_tab_selected));
                studentTab.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case Const.REG_PAYMENT:
                paymentTab.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_reg_tab_selected));
                paymentTab.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            default:
                break;
        }

    }
}
