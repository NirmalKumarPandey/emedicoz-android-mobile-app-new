package com.emedicoz.app.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;

import java.util.List;

/**
 * Created by mridul.
 */

public class SubListBookmarkAdapter extends RecyclerView.Adapter<SubListBookmarkAdapter.ViewHolder> {

    Context context;
    private List<String> listTitle;


    public SubListBookmarkAdapter(List<String> listTitle, Context context) {
        this.listTitle = listTitle;
        this.context = context;
    }

    public void onSubTextClick(String tag) {
        //  handle click listener
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String subListText = listTitle.get(position);
        holder.listTV.setTag(subListText);
        holder.viewBtn.setVisibility(View.GONE);
        holder.listTV.setVisibility(View.VISIBLE);
        holder.listTV.setText(subListText);
        holder.listTV.setOnClickListener(view -> onSubTextClick((String) view.getTag())
        );
    }

    @Override
    public int getItemCount() {
        return listTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTV;
        TextView viewBtn;

        public ViewHolder(final View view) {
            super(view);
            listTV = view.findViewById(R.id.listItem);
            viewBtn = view.findViewById(R.id.viewBtn);
        }
    }
}

