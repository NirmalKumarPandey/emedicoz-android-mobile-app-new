package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.testmodule.model.Social;

import java.util.ArrayList;

public class MatchTheFollowingAdapterTop extends RecyclerView.Adapter {
    Activity activity;
    ArrayList<Social> items;

    public MatchTheFollowingAdapterTop(Activity activity, ArrayList<Social> items) {
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drag, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder1 = ((MyViewHolder) holder);
        holder1.name1.setText(items.get(position).getName());
        holder1.optionIconTV1.setText(items.get(position).getOption());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name, name1;
        public LinearLayout llmain, llmain1;
        public TextView optionIconTV, optionIconTV1;
        public ImageButton bt_move;
        public View lyt_parent;
        RecyclerView checkRV;

        public MyViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            name1 = v.findViewById(R.id.name1);
            llmain = v.findViewById(R.id.llmain);
            llmain1 = v.findViewById(R.id.llmain1);
            optionIconTV = v.findViewById(R.id.optionIconTV);
            optionIconTV1 = v.findViewById(R.id.optionIconTV1);
            checkRV = v.findViewById(R.id.checkRV);
            checkRV.setVisibility(View.GONE);
            llmain.setVisibility(View.GONE);
            llmain1.setVisibility(View.VISIBLE);
            optionIconTV.setVisibility(View.GONE);
        }
    }
}
