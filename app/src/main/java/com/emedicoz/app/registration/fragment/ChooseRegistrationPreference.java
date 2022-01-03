package com.emedicoz.app.registration.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.ClassRoomWebView;
import com.emedicoz.app.registration.activity.FillRegistrationDetailActivity;
import com.emedicoz.app.utilso.Const;


public class ChooseRegistrationPreference extends Fragment implements View.OnClickListener {

    Activity activity;
    TextView f2fClassesTV;
    TextView vsatClassesTV;
    ImageView f2fClassesIV;
    ImageView vsatClassesIV;
    LinearLayout f2fClassesLL;
    LinearLayout vsatClassesLL;
    String title;

    public static ChooseRegistrationPreference newInstance() {

        Bundle args = new Bundle();

        ChooseRegistrationPreference fragment = new ChooseRegistrationPreference();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_registration_preference, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

    }

    public void initViews(View view) {
        f2fClassesTV = view.findViewById(R.id.f2fClassesTV);
        vsatClassesTV = view.findViewById(R.id.vsatClassesTV);
        f2fClassesIV = view.findViewById(R.id.f2fClassesIV);
        vsatClassesIV = view.findViewById(R.id.vsatClassesIV);
        f2fClassesLL = view.findViewById(R.id.f2fClassesLL);
        vsatClassesLL = view.findViewById(R.id.vsatClassesLL);
        bindControls();
    }

    private void bindControls() {
        f2fClassesLL.setOnClickListener(this);
        vsatClassesLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.f2fClassesLL:
                title = activity.getResources().getString(R.string.f_2_f_classes);
                setActiveBlock(Const.F_2_F);
                goToWebViewActivity(Const.REGISTRATION_URL_FTF);
               // Helper.GoToWebViewActivity(activity,Const.REGISTRATION_URL_FTF);
                break;
            case R.id.vsatClassesLL:
                title = activity.getResources().getString(R.string.vsat_classes);
                setActiveBlock(Const.VSAT);
                goToWebViewActivity(Const.REGISTRATION_URL_VSAT);
               // Helper.GoToWebViewActivity(activity,Const.REGISTRATION_URL_VSAT);
                break;
            default:
                break;
        }
    }

    private void goToWebViewActivity(String url) {
        Intent intent = new Intent(activity, ClassRoomWebView.class);
        intent.putExtra(Const.URL, url);
        activity.startActivity(intent);
    }

    private void setActiveBlock(String type) {
        f2fClassesLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_registration_pref));
        vsatClassesLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_registration_pref));
        f2fClassesTV.setTextColor(ContextCompat.getColor(activity,R.color.stroke_reg_pref));
        vsatClassesTV.setTextColor(ContextCompat.getColor(activity,R.color.stroke_reg_pref));
        f2fClassesIV.setImageResource(R.mipmap.ftf_classes_default);
        vsatClassesIV.setImageResource(R.mipmap.f_t_f_classes);
        if (type.equalsIgnoreCase(Const.F_2_F)){
            f2fClassesLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_registration_pref_active));
            f2fClassesTV.setTextColor(ContextCompat.getColor(activity,R.color.blue));
            f2fClassesIV.setImageResource(R.mipmap.vsat_classes);
        }else if (type.equalsIgnoreCase(Const.VSAT)){
            vsatClassesLL.setBackground(ContextCompat.getDrawable(activity,R.drawable.bg_registration_pref_active));
            vsatClassesTV.setTextColor(ContextCompat.getColor(activity,R.color.blue));
            vsatClassesIV.setImageResource(R.mipmap.vasat_classes_active);
        }
    }

    public void goToFillDetailActivity(String title) {
        Intent intent = new Intent(activity, FillRegistrationDetailActivity.class);
        intent.putExtra(Const.TITLE, title);
        activity.startActivity(intent);
    }
}
