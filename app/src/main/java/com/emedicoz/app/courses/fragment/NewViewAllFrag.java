package com.emedicoz.app.courses.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.ComboCourseAdapter;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewViewAllFrag extends Fragment {
    RecyclerView recyclerViewAll;
    Topics[] topics = new Topics[0];


    public NewViewAllFrag() {
        // Required empty public constructor
    }

    public static NewViewAllFrag newInstance(String frag_type, Topics[] topics, String id) {
        NewViewAllFrag newViewAllFrag = new NewViewAllFrag();
        Bundle args = new Bundle();
        args.putSerializable("viewalldata", topics);
        args.putString(Const.FRAG_TYPE, frag_type);
        args.putString(Constants.Extras.ID, id);
        newViewAllFrag.setArguments(args);
        return newViewAllFrag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_view_all, container, false);
        recyclerViewAll = view.findViewById(R.id.recyclerviewall);
        recyclerViewAll.setLayoutManager(new LinearLayoutManager(getActivity()));
        topics = (Topics[]) Objects.requireNonNull(getArguments()).getSerializable("viewalldata");

        ComboCourseAdapter adapter = new ComboCourseAdapter(getActivity(), getArguments().getString(Constants.Extras.ID), topics);
        recyclerViewAll.setAdapter(adapter);
        return view;
    }

}
