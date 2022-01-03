package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.content.Intent;
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
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.adapter.CreateCustomModuleSubjectAdapter;
import com.emedicoz.app.modelo.custommodule.SubjectData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCustomModuleSubject extends Fragment implements View.OnClickListener {

    private static final String TAG = "CreateCustomSubject";
    public Button allSubjectButton, chooseSubjectButton;
    Activity activity;
    View view;
    RecyclerView allSubjectRecyclerView;
    CreateCustomModuleSubjectAdapter createCustomModuleSubjectAdapter;
    HashMap<String, String> finalResponse = new HashMap<>();
    String subject = "";
    String topics = "";

    public CreateCustomModuleSubject() {
        // Required empty public constructor
    }

    public static CreateCustomModuleSubject newInstance(HashMap<String, String> finalResponse) {
        CreateCustomModuleSubject fragment = new CreateCustomModuleSubject();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.finalResponse, finalResponse);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " + "onCreate of" + TAG);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_custom_module_subject, container, false);
        Log.e(TAG, "onCreateView: " + "onCreateView of" + TAG);
        initView();
        getBundleData();
        ((BaseABNoNavActivity) activity).nextButton.setVisibility(View.VISIBLE);
        ((BaseABNoNavActivity) activity).nextButton.setOnClickListener(v -> {
                for (int i = 0; i < ((CourseActivity) activity).subjectData.size(); i++) {
                    if (((CourseActivity) activity).subjectData.get(i).getAllchecked()) {
                        subject = subject + "," + ((CourseActivity) activity).subjectData.get(i).getId();
                        for (int j = 0; j < ((CourseActivity) activity).subjectData.get(i).getTopics().size(); j++) {
                            if (((CourseActivity) activity).subjectData.get(i).getTopics().get(j).getChecked())
                                topics = topics + "," + ((CourseActivity) activity).subjectData.get(i).getTopics().get(j).getId();
                            else if (((CourseActivity) activity).subjectData.get(i).getCount() == 0)
                                topics = topics + "," + ((CourseActivity) activity).subjectData.get(i).getTopics().get(j).getId();
                        }
                    }

                }

                Log.e("subject", subject);
                Log.e("topics", topics);
                if (!subject.equals("") && !topics.equals("")) {
                    if (subject.startsWith(",")) {
                        subject = subject.substring(1);
                    }
                    if (subject.endsWith(",")) {
                        subject = subject.substring(0, subject.length() - 1);
                    }

                    if (topics.startsWith(",")) {
                        topics = topics.substring(1);
                    }
                    if (topics.endsWith(",")) {
                        topics = topics.substring(0, topics.length() - 1);
                    }

                    finalResponse.put("subject", subject);
                    finalResponse.put("topics", topics);

                    if (SharedPreference.getInstance().getString("IS_TAG").equals("1")) {
                        Intent conceIntent = new Intent(activity, CourseActivity.class);
                        conceIntent.putExtra(Const.FRAG_TYPE, Const.SELECT_TAG);
                        conceIntent.putExtra(Const.finalResponse, finalResponse);
                        activity.startActivity(conceIntent);
                    } else {
                        Intent conceIntent1 = new Intent(activity, CourseActivity.class);
                        conceIntent1.putExtra(Const.FRAG_TYPE, Const.SELECT_MODE);
                        conceIntent1.putExtra(Const.finalResponse, finalResponse);
                        activity.startActivity(conceIntent1);
                    }
                }
        });

        allSubjectButton.setSelected(true);
        allSubjectRecyclerView.setVisibility(View.VISIBLE);
        allSubjectButton.setOnClickListener(this);
        chooseSubjectButton.setOnClickListener(this);
        // getSubjectList();
        for (int j = 0; j < ((CourseActivity) activity).subjectData.size(); j++) {
            ((CourseActivity) activity).subjectData.get(j).setAllchecked(true);
        }
        initAdapter(allSubjectRecyclerView, ((CourseActivity) activity).subjectData, "1");


        return view;
    }

    private void getBundleData() {
        if (getArguments() != null) {
            finalResponse = (HashMap<String, String>) getArguments().getSerializable(Const.finalResponse);
        }
    }

    private void initAdapter(RecyclerView recyclerView, ArrayList<SubjectData> subjectData, String type) {

        createCustomModuleSubjectAdapter = new CreateCustomModuleSubjectAdapter(activity, ((CourseActivity) activity).subjectData, type, finalResponse, this);
        recyclerView.setAdapter(createCustomModuleSubjectAdapter);
    }

    private void initView() {
        allSubjectButton = view.findViewById(R.id.all_subject_button);
        chooseSubjectButton = view.findViewById(R.id.choose_subject_button);

        allSubjectRecyclerView = view.findViewById(R.id.all_subject_recycler_view);

        allSubjectRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_subject_button:
                allSubjectButton.setSelected(true);
                chooseSubjectButton.setSelected(false);
                for (int j = 0; j < ((CourseActivity) activity).subjectData.size(); j++) {
                    ((CourseActivity) activity).subjectData.get(j).setAllchecked(true);
                }

                if (((CourseActivity) activity).subjectData != null)
                    initAdapter(allSubjectRecyclerView, ((CourseActivity) activity).subjectData, "1");
                break;
            case R.id.choose_subject_button:

                allSubjectButton.setSelected(false);
                chooseSubjectButton.setSelected(true);
                for (int j = 0; j < ((CourseActivity) activity).subjectData.size(); j++) {
                    ((CourseActivity) activity).subjectData.get(j).setAllchecked(false);
                }
                if (((CourseActivity) activity).subjectData != null)
                    initAdapter(allSubjectRecyclerView, ((CourseActivity) activity).subjectData, "2");
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " + "onResume of" + TAG);
        subject = "";
        topics = "";
    }
}
