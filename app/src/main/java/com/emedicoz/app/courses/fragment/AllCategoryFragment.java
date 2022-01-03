package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.AllCatAdapter;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.utilso.Constants;

import java.util.ArrayList;

public class AllCategoryFragment extends Fragment {

    RecyclerView recyclerView;
    Activity activity;
    ArrayList<CourseCategory> courseCategories;

    public static AllCategoryFragment newInstance(ArrayList<CourseCategory> courseCategories) {
        AllCategoryFragment fragment = new AllCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.Extras.COURSE_CAT, courseCategories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            courseCategories = (ArrayList<CourseCategory>) getArguments().getSerializable(Constants.Extras.COURSE_CAT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_category, container, false);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.allCatRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (courseCategories != null && !courseCategories.isEmpty()) {
            AllCatAdapter adapter = new AllCatAdapter(activity, courseCategories);
            recyclerView.setAdapter(adapter);
        }
    }
}
