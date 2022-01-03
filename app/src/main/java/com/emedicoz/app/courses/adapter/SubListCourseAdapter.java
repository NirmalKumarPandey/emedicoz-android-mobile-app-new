package com.emedicoz.app.courses.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emedicoz.app.R;

import java.util.ArrayList;

/**
 * Created by Cbc-03 on 08/03/17.
 */

public class SubListCourseAdapter extends RecyclerView.Adapter<SubListCourseAdapter.ViewHolder> {

    Context context;
    private ArrayList<String> ListTitle;

    public SubListCourseAdapter(ArrayList<String> ListTitle, Context context) {
        this.ListTitle = ListTitle;
        this.context = context;
    }

    public void OnsubTextClick(String str) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String subListText = ListTitle.get(position);
        holder.listTV.setTag(subListText);

        holder.listTV.setVisibility(View.VISIBLE);
        holder.listTV.setText(subListText);
        holder.listTV.setOnClickListener(view -> OnsubTextClick((String) view.getTag())
        );
    }

    @Override
    public int getItemCount() {
        return ListTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTV;

        public ViewHolder(final View view) {
            super(view);
            listTV = view.findViewById(R.id.listItem);
        }
    }
}

