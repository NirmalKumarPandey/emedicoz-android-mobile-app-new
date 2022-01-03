package com.emedicoz.app.registration.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Helper;

public class FillCourseDetailFragment extends Fragment {

    Activity activity;
    TextView franchiseNameTV;
    TextView sessionTV;
    TextView courseGroupTV;
    TextView courseListTV;

    public static FillCourseDetailFragment newInstance(){
        FillCourseDetailFragment fragment = new FillCourseDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_registration_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        franchiseNameTV = view.findViewById(R.id.franchiseNameTV);
        sessionTV = view.findViewById(R.id.sessionTV);
        courseGroupTV = view.findViewById(R.id.courseGroupTV);
        courseListTV = view.findViewById(R.id.courseListTV);
        setAsteriskOnText();
    }

    private void setAsteriskOnText() {
        Helper.setCompulsoryAsterisk(franchiseNameTV,activity.getResources().getString(R.string.franchise_name));
        Helper.setCompulsoryAsterisk(sessionTV,activity.getResources().getString(R.string.session));
        Helper.setCompulsoryAsterisk(courseGroupTV,activity.getResources().getString(R.string.course_group_name));
        Helper.setCompulsoryAsterisk(courseListTV,activity.getResources().getString(R.string.course_list_reg));
    }
}
