package com.emedicoz.app.feeds.adapter;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;

import java.util.ArrayList;

/**
 * Created by Sagar on 20-01-2018.
 */

public class ClearOfflineRVAdapter extends RecyclerView.Adapter<ClearOfflineRVAdapter.ClearOfflineDataHolder> {

    public ArrayList<String> selectedCheckBoxes = new ArrayList<>();
    ArrayList<String> stringArrayList;
    Activity activity;
    Fragment fragment;


    public ClearOfflineRVAdapter(Activity activity, ArrayList<String> stringArrayList) {
        this.activity = activity;
        this.stringArrayList = stringArrayList;
        if (activity instanceof BaseABNavActivity)
            fragment = ((BaseABNavActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public ClearOfflineDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_delete_offline_data, parent, false);
        return new ClearOfflineDataHolder(view);
    }

    @Override
    public void onBindViewHolder(final ClearOfflineDataHolder holder, final int position) {
        holder.SetData(stringArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class ClearOfflineDataHolder extends RecyclerView.ViewHolder {
        CheckBox optionCB;
        LinearLayout mainLL;

        public ClearOfflineDataHolder(View itemView) {
            super(itemView);
            optionCB = itemView.findViewById(R.id.optionCB);
            mainLL = itemView.findViewById(R.id.mainLL);

        }

        public void SetData(String str) {
            optionCB.setText(str);
            optionCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) selectedCheckBoxes.add(compoundButton.getText().toString());
                    else selectedCheckBoxes.remove(compoundButton.getText().toString());
                }
            });
        }
    }
}
