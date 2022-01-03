package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class MatchTheFollowingBookmarkAdapter extends RecyclerView.Adapter<MatchTheFollowingBookmarkAdapter.MatchTheFollowingViewHolder> {

    Activity activity;
    ArrayList<Social> items1 = new ArrayList<>();
    ArrayList<Social> items2 = new ArrayList<>();
    // AdapterMatchingListCheck adapterMatchingListCheck;
    AdapterMatchingOptionBookmark adapterMatchingOption;
    AdapterMatchingOptionViewSolution adapterMatchingOptionViewSolution;
    boolean isTest;
    int pagerPosition;
    ArrayList<String> answerList;
    ArrayList<String> userAnswerList;
    //public static boolean isChecked;

    public MatchTheFollowingBookmarkAdapter(Activity activity, ArrayList<Social> items1, ArrayList<Social> items2, boolean isTest, int pagerPosition, ArrayList<String> answerList) {
        this.activity = activity;
        this.items1 = items1;
        this.items2 = items2;
        this.isTest = isTest;
        this.pagerPosition = pagerPosition;
        this.answerList = answerList;
    }


    @NonNull
    @Override
    public MatchTheFollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drag, parent, false);
        return new MatchTheFollowingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchTheFollowingViewHolder holder, int position) {

        holder.name.setText(items1.get(position).getName());
        holder.name1.setText(items2.get(position).getName());
        holder.optionIconTV.setText(items1.get(position).getOption());
        holder.optionIconTV1.setText(items2.get(position).getOption());
        holder.checkRV.setLayoutManager(new GridLayoutManager(activity, 4));


        adapterMatchingOption = new AdapterMatchingOptionBookmark(activity, items2, items1, position, pagerPosition, answerList);
        holder.checkRV.setAdapter(adapterMatchingOption);


/*        if (((TestBaseActivity)activity).mtfAnswer!=null && ((TestBaseActivity)activity).mtfAnswer.size()>0){
            for (int i=0;i<((TestBaseActivity)activity).mtfAnswer.size();i++){
                if (String.valueOf(((TestBaseActivity)activity).mtfAnswer.get(i).charAt(0)).equalsIgnoreCase(items1.get(position).getOption())){
                    adapterMatchingListCheck = new AdapterMatchingListCheck(activity, items2, position, items1, isChecked,String.valueOf(((TestBaseActivity)activity).mtfAnswer.get(i).charAt(1)));
                    holder.checkRV.setAdapter(adapterMatchingListCheck);
                }else {
                    adapterMatchingListCheck = new AdapterMatchingListCheck(activity, items2, position, items1, isChecked);
                    holder.checkRV.setAdapter(adapterMatchingListCheck);
                }
            }
        }else {
            adapterMatchingListCheck = new AdapterMatchingListCheck(activity, items2, position, items1, isChecked);
            holder.checkRV.setAdapter(adapterMatchingListCheck);

        }*/
    }

    @Override
    public int getItemCount() {
        return items1.size();
    }

    public class MatchTheFollowingViewHolder extends RecyclerView.ViewHolder {


        public ImageView image;
        public TextView name, name1;
        public LinearLayout llmain, llmain1;
        public TextView optionIconTV, optionIconTV1;
        public ImageButton bt_move;
        public View lyt_parent;
        RecyclerView checkRV;

        public MatchTheFollowingViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            name1 = v.findViewById(R.id.name1);
            llmain = v.findViewById(R.id.llmain);
            llmain1 = v.findViewById(R.id.llmain1);
            optionIconTV = v.findViewById(R.id.optionIconTV);
            optionIconTV1 = v.findViewById(R.id.optionIconTV1);
            //  bt_move = (ImageButton) v.findViewById(R.id.bt_move);
            //lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            checkRV = v.findViewById(R.id.checkRV);
            llmain1.setVisibility(View.GONE);
        }
    }
}
