package com.emedicoz.app.courses.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.CourseListAdapter;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

import java.util.ArrayList;
import java.util.Objects;

import static com.emedicoz.app.utilso.Helper.getCourseList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewMyCourseFragment extends Fragment {
    RecyclerView myCoursesRV;
    CourseListAdapter courseListAdapter;
    ArrayList<Course> courseArrayList = new ArrayList<>();

    ArrayList<Course> newList = new ArrayList<>();
    String name;


    public NewMyCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_my_course, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        try {
            myCoursesRV = view.findViewById(R.id.recyclermycourses);
            myCoursesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (getArguments() != null) {
                name = getArguments().getString(Constants.Extras.NAME);
            }
            courseArrayList = getCourseList();
            newList.clear();

            if (Objects.requireNonNull(name).equalsIgnoreCase(getString(R.string.all))) {
                newList.addAll(courseArrayList);
            } else {

                for (int i = 0; i < courseArrayList.size(); i++) {
                    if (name.equalsIgnoreCase(courseArrayList.get(i).getSubject_title())) {
                        newList.add(courseArrayList.get(i));
                    }
                }
            }
            if (!newList.isEmpty()) {
                courseListAdapter = new CourseListAdapter(getActivity(), newList, Const.MYCOURSES);
                myCoursesRV.setAdapter(courseListAdapter);
            }
        }catch (Exception e){

        }
    }

}
