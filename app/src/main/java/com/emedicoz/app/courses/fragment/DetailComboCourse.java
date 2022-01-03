package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.DetailComboCourseAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.utilso.Const;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailComboCourse extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    TextView bannerNameTV;
    TextView learnerTV;
    TextView courseRatingTV;
    TextView courseNameTV;
    TextView descriptionNameTV;
    TextView courseDescriptionTV;
    TextView curriculumTextTV;
    TextView errorTV;
    LinearLayout curriculumListLL, curriculumMainLL, mainLayout;
    NestedScrollView scroll;
    NonScrollRecyclerView curriculumExpListRV;
    SingleCourseData singleCourseData;
    Button seeAllB, faqB;
    CardView courseRatingCV;
    DetailComboCourseAdapter detailComboCourseAdapter;
    ImageView bannerImageIV;
    Activity activity;
    Progress mProgress;
    String courseId;

    public DetailComboCourse() {
        // Required empty public constructor
    }

    public static DetailComboCourse newInstance(SingleCourseData singleCourseData, String courseId) {
        DetailComboCourse detailComboCourse = new DetailComboCourse();
        Bundle args = new Bundle();
        args.putSerializable(Const.COURSE_DESC, singleCourseData);
        args.putString(Const.PARENT_ID, courseId);
        detailComboCourse.setArguments(args);
        return detailComboCourse;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_combo_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            singleCourseData = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
            courseId = getArguments().getString(Const.PARENT_ID);
        }
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        initViews(view);
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeDetailCombo);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        scroll = view.findViewById(R.id.scrollDetailCombo);
        bannerNameTV = view.findViewById(R.id.bannernameTV);
        courseNameTV = view.findViewById(R.id.nameTV);
        learnerTV = view.findViewById(R.id.learnerTV);
        courseRatingTV = view.findViewById(R.id.ratingTV);
        courseDescriptionTV = view.findViewById(R.id.descriptionTV);
        bannerImageIV = view.findViewById(R.id.bannerimageIV);
        curriculumListLL = view.findViewById(R.id.curriculumListLL);
        curriculumMainLL = view.findViewById(R.id.curriculumLay);
        curriculumTextTV = view.findViewById(R.id.curriculumTextTV);
        courseRatingCV = view.findViewById(R.id.courseRatingCV);
        descriptionNameTV = view.findViewById(R.id.courseDescriptionTV);
        seeAllB = view.findViewById(R.id.seeAllCurriculumBtn);
        faqB = view.findViewById(R.id.faqbtn);
        mainLayout = view.findViewById(R.id.mainLayout);
        curriculumExpListRV = view.findViewById(R.id.curriculumExpListLL);
        curriculumExpListRV.setVisibility(View.VISIBLE);
        curriculumExpListRV.setLayoutManager(new LinearLayoutManager(activity));

        setAllDataToViews();
    }

    private void setAllDataToViews() {
        courseDescriptionTV.setText(singleCourseData.getDescription());
        Glide.with(activity).load(singleCourseData.getDesc_header_image()).into(bannerImageIV);
        courseNameTV.setText(singleCourseData.getTitle());
        learnerTV.setText((singleCourseData.getLearner() + " "
                + ((singleCourseData.getLearner().equals("1") || (singleCourseData.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));
        courseRatingTV.setText((singleCourseData.getReview_count() + " "
                + ((singleCourseData.getReview_count().equals("1") || (singleCourseData.getReview_count().equals("0")) ? Const.REVIEW : Const.REVIEWS))));

        detailComboCourseAdapter = new DetailComboCourseAdapter(activity, singleCourseData.getCurriculam().getTopics(), courseId);
        curriculumExpListRV.setAdapter(detailComboCourseAdapter);
    }

    @Override
    public void onRefresh() {

    }
}
