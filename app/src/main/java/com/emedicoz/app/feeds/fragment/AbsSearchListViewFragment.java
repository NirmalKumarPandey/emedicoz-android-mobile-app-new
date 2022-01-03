package com.emedicoz.app.feeds.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CustomSearchListAdapter;
import com.emedicoz.app.customviews.VerticalSpaceItemDecoration;

import java.util.Objects;

/**
 * Created by jikoobaruah on 10/02/16.
 */
public abstract class AbsSearchListViewFragment extends DialogFragment {

    private View mParentView;
    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(getLayoutResID(), container, false);
        return mParentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView mSearchView = mParentView.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((CustomSearchListAdapter) Objects.requireNonNull(mRecyclerView.getAdapter())).filter(newText);
                return true;
            }
        });

        mRecyclerView = mParentView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(2));
        mRecyclerView.setAdapter(getListAdapter());

        fetchList();
    }

    protected abstract void fetchList();

    protected abstract RecyclerView.Adapter getListAdapter();

    protected abstract int getLayoutResID();

}
