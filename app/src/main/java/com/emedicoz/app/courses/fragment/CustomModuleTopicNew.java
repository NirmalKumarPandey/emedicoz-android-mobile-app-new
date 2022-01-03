package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.CustomModuleChildTopicAdapter;
import com.emedicoz.app.modelo.custommodule.SubjectData;
import com.emedicoz.app.utilso.Const;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomModuleTopicNew extends Fragment implements View.OnClickListener {

    public Button allSubjectButton, chooseSubjectButton;
    Activity activity;
    View view;
    RecyclerView allSubjectRecyclerView;
    Button done_button;
    ArrayList<SubjectData> subjectData = new ArrayList<>();
    HashMap<String, String> finalResponse = new HashMap<>();
    CustomModuleChildTopicAdapter customModuleChildTopicAdapter;
    private int position;

    public static CustomModuleTopicNew newInstance(ArrayList<SubjectData> subject_data, int position, HashMap<String, String> finalResponse) {

        CustomModuleTopicNew fragment = new CustomModuleTopicNew();
        Bundle args = new Bundle();
        args.putSerializable(Const.SUBJECT_DATA, subject_data);
        args.putSerializable(Const.finalResponse, finalResponse);
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom_module_child_topic, container, false);
        getBundleData();
        initView();

        allSubjectRecyclerView.setVisibility(View.VISIBLE);
        allSubjectButton.setOnClickListener(this);
        chooseSubjectButton.setOnClickListener(this);

        if (((CourseActivity) activity).subjectData.get(position).getCount() == ((CourseActivity) activity).subjectData.get(position).getTopics().size()) {
            allSubjectButton.setSelected(true);
            initAdapter(allSubjectRecyclerView, "1");
        } else if (((CourseActivity) activity).subjectData.get(position).getCount() != 0) {
            chooseSubjectButton.setSelected(true);
            initAdapter(allSubjectRecyclerView, "3");
        } else {
            allSubjectButton.setSelected(true);
            initAdapter(allSubjectRecyclerView, "1");
        }


        return view;
    }

    private void initView() {
        allSubjectButton = view.findViewById(R.id.all_subject_button);
        chooseSubjectButton = view.findViewById(R.id.choose_subject_button);
        allSubjectRecyclerView = view.findViewById(R.id.all_subject_recycler_view);
        done_button = view.findViewById(R.id.done_button);

        allSubjectRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        done_button.setOnClickListener(this);
    }

    private void getBundleData() {
        if (getArguments() != null) {
            subjectData = (ArrayList<SubjectData>) getArguments().getSerializable(Const.SUBJECT_DATA);
            finalResponse = (HashMap<String, String>) getArguments().getSerializable(Const.finalResponse);
            position = getArguments().getInt("Position");
        }
    }

    private void initAdapter(RecyclerView recyclerView, String type) {
        customModuleChildTopicAdapter = new CustomModuleChildTopicAdapter(activity, position, ((CourseActivity) activity).subjectData, type, this);
        recyclerView.setAdapter(customModuleChildTopicAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_subject_button:
                allSubjectButton.setSelected(true);
                chooseSubjectButton.setSelected(false);

                for (int j = 0; j < ((CourseActivity) activity).subjectData.get(position).getTopics().size(); j++) {
                    ((CourseActivity) activity).subjectData.get(position).getTopics().get(j).setChecked(true);
                }

                initAdapter(allSubjectRecyclerView, "1");
                break;
            case R.id.choose_subject_button:
                allSubjectButton.setSelected(false);
                chooseSubjectButton.setSelected(true);

                if (((CourseActivity) activity).subjectData.get(position).getCount() != 0)
                    initAdapter(allSubjectRecyclerView, "2");
                else if (((CourseActivity) activity).subjectData.get(position).getCount() == ((CourseActivity) activity).subjectData.get(position).getTopics().size()) {
                    for (int j = 0; j < ((CourseActivity) activity).subjectData.get(position).getTopics().size(); j++) {
                        ((CourseActivity) activity).subjectData.get(position).getTopics().get(j).setChecked(false);
                    }
                    initAdapter(allSubjectRecyclerView, "2");
                } else {
                    for (int j = 0; j < ((CourseActivity) activity).subjectData.get(position).getTopics().size(); j++) {
                        ((CourseActivity) activity).subjectData.get(position).getTopics().get(j).setChecked(false);
                    }
                    initAdapter(allSubjectRecyclerView, "2");
                }

                break;
            case R.id.done_button:
                addSelectedValue();
                break;

        }
    }

    private void addSelectedValue() {
        for (int i = 0; i < ((CourseActivity) activity).subjectData.size(); i++) {
            int count = 0;
            for (int j = 0; j < ((CourseActivity) activity).subjectData.get(i).getTopics().size(); j++) {
                if (((CourseActivity) activity).subjectData.get(i).getTopics().get(j).getChecked()) {
                    ((CourseActivity) activity).subjectData.get(i).setCount(++count);
                }
            }

            Log.e("topic_count", ((CourseActivity) activity).subjectData.get(i).getName() + ((CourseActivity) activity).subjectData.get(i).getCount());
        }
        ((CourseActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateCustomModuleSubject.newInstance(finalResponse)).addToBackStack(null).commit();
    }
}
