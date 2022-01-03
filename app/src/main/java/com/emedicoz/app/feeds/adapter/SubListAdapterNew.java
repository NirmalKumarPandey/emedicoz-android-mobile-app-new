package com.emedicoz.app.feeds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.Tags;

import java.util.List;

/**
 * Created by Cbc-03 on 08/03/17.
 */

public class SubListAdapterNew extends RecyclerView.Adapter<SubListAdapterNew.ViewHolder> {

    Context context;
    private List<Tags> listTitle;


    public SubListAdapterNew(List<Tags> listTitle, Context context) {
        this.listTitle = listTitle;
        this.context = context;
    }

    public void onsubTextClick(Tags tag) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Tags subListText = listTitle.get(position);
        holder.listTV.setTag(subListText);
        holder.viewBtn.setTag(subListText);
        holder.viewBtn.setVisibility(View.GONE);
        holder.listTV.setVisibility(View.VISIBLE);
        holder.listTV.setText(subListText.getText());

        holder.listTV.setOnClickListener(view -> onsubTextClick((Tags) view.getTag()));
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

