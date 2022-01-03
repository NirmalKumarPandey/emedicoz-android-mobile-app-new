package com.emedicoz.app.bookmark;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Constants;

public class BookmarkFrag extends Fragment {

    public BookmarkFrag() {
        // Required empty public constructor
    }

    public static BookmarkFrag getInstance(String type) {
        BookmarkFrag frag = new BookmarkFrag();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extras.TYPE, type);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

}
