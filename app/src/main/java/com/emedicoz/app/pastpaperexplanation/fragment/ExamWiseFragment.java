package com.emedicoz.app.pastpaperexplanation.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.pastpaperexplanation.activity.PastPaperExplanationActivity;
import com.emedicoz.app.pastpaperexplanation.adapter.ExamWiseAdapter;
import com.emedicoz.app.pastpaperexplanation.adapter.SubjectWiseAdapter;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GridSpacingItemDecoration;

import java.util.ArrayList;

public class ExamWiseFragment extends Fragment {

    Activity activity;
    int position;
    RecyclerView recyclerView;
    ExamWiseAdapter examWiseAdapter;
    SubjectWiseAdapter subjectWiseAdapter;
    ArrayList<String> stringSubjectArrayList = new ArrayList<>();

    public static ExamWiseFragment newInstance(int position) {
        Bundle args = new Bundle();
        ExamWiseFragment fragment = new ExamWiseFragment();
        args.putInt(Const.POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            position = getArguments().getInt(Const.POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam_wise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TEST DATA

        /*stringSubjectArrayList.add("ANATOMY");
        stringSubjectArrayList.add("PHYSIOLOGY");
        stringSubjectArrayList.add("BIOCHEM");
        stringSubjectArrayList.add("PATHO");
        stringSubjectArrayList.add("PHARMA");
        stringSubjectArrayList.add("MICRO");
        stringSubjectArrayList.add("FSM");
        stringSubjectArrayList.add("SPM");*/

        initViews(view);
    }

    private void initViews(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);


        bindControls();
    }

    private void bindControls() {
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 30, true));

        if (position == 0) {
            examWiseAdapter = new ExamWiseAdapter(activity, ((PastPaperExplanationActivity)activity).topicArrayList,((PastPaperExplanationActivity)activity).ppeData);
            recyclerView.setAdapter(examWiseAdapter);
        } else {
            subjectWiseAdapter = new SubjectWiseAdapter(activity, ((PastPaperExplanationActivity)activity).subjectArrayList,((PastPaperExplanationActivity)activity).ppeData);
            recyclerView.setAdapter(subjectWiseAdapter);
        }
    }
}
