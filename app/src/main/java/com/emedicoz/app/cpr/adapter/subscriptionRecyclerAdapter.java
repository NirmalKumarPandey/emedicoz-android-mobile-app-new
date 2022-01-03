package com.emedicoz.app.cpr.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.cpr.fragment.UpToDateFragment;
import com.emedicoz.app.response.SubscriptionOptionList;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;

/**
 * Created by app on 30/1/18.
 */

public class subscriptionRecyclerAdapter extends RecyclerView.Adapter<subscriptionRecyclerAdapter.ViewHolder1> {

    Context context;
    ArrayList<SubscriptionOptionList> subscriptionList;
    private UpToDateFragment fragment;


    public subscriptionRecyclerAdapter(UpToDateFragment fragment, ArrayList<SubscriptionOptionList> subscriptionList) {
        this.subscriptionList = subscriptionList;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_recycler_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder1 holder, final int position) {
        SpannableString mrp = null;
        mrp = new SpannableString(Helper.calculatePriceBasedOnCurrency(subscriptionList.get(position).getMrp()));
        mrp.setSpan(new StrikethroughSpan(), 0, mrp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.rb.setText(subscriptionList.get(position).getMonths());

        holder.rs.setText(Helper.getCurrencySymbol());
        holder.mrpTv.setText(mrp);


        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken()))
            holder.discountedTv.setText(Helper.getCurrencySymbol() + Helper.calculatePriceBasedOnCurrency(subscriptionList.get(position).getForDams()));
        else
            holder.discountedTv.setText(Helper.getCurrencySymbol() + Helper.calculatePriceBasedOnCurrency(subscriptionList.get(position).getNonDams()));

        holder.rb.setChecked(subscriptionList.get(position).isSelected());

        holder.rb.setOnClickListener(v -> fragment.update(position));

        holder.mainLL.setOnClickListener(v -> fragment.update(position));

    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    // SingleChildView for single feed
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        RadioButton rb;
        TextView mrpTv, discountedTv, rs;
        LinearLayout mainLL;


        public ViewHolder1(final View view) {
            super(view);
            rb = view.findViewById(R.id.rb);
            rs = view.findViewById(R.id.rs);
            mrpTv = view.findViewById(R.id.mrpTv);
            discountedTv = view.findViewById(R.id.discountedTv);
            mainLL = view.findViewById(R.id.mainLL);
        }
    }
}
