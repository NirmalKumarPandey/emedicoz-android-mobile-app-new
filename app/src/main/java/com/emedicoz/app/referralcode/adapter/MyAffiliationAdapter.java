package com.emedicoz.app.referralcode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.referralcode.model.MyAffiliation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class MyAffiliationAdapter extends RecyclerView.Adapter<MyAffiliationAdapter.ViewHolder> {
    List<MyAffiliation> myAffiliationList;
    Context context;

    public MyAffiliationAdapter(List<MyAffiliation> myAffiliationList, Context context) {
        this.myAffiliationList = myAffiliationList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAffiliationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_affiliation_record_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull MyAffiliationAdapter.ViewHolder viewHolder, int i) {
        MyAffiliation myAffiliation = myAffiliationList.get(i);
        viewHolder.personNameTV.setText(myAffiliation.getFirstName());
        viewHolder.coursePurchasedTV.setText(myAffiliation.getCoursePurchased());
        if (myAffiliation.getTransactionDate() != null) {
            try {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date d = f.parse(myAffiliation.getTransactionDate());
                DateFormat date = new SimpleDateFormat("MM/dd/yyyy");

                viewHolder.purchaseDateTV.setText(date.format(Objects.requireNonNull(d)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (myAffiliationList != null) {
            return myAffiliationList.size();
        } else {
            return 0;
        }

    }

    public void setList(List<MyAffiliation> affiliationList) {
        this.myAffiliationList = affiliationList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView personNameTV;
        TextView coursePurchasedTV;
        TextView purchaseDateTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            personNameTV = itemView.findViewById(R.id.person_name_TV);
            coursePurchasedTV = itemView.findViewById(R.id.course_purchased_TV);
            purchaseDateTV = itemView.findViewById(R.id.purchase_date_TV);
        }
    }
}
