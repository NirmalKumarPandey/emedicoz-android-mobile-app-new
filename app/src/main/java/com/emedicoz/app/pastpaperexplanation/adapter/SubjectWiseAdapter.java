package com.emedicoz.app.pastpaperexplanation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.pastpaperexplanation.activity.ExamAndSubjectWiseActivity;
import com.emedicoz.app.pastpaperexplanation.model.PPEData;
import com.emedicoz.app.pastpaperexplanation.model.Subject;
import com.emedicoz.app.utilso.Constants;

import java.io.Serializable;
import java.util.List;

public class SubjectWiseAdapter extends RecyclerView.Adapter<SubjectWiseAdapter.MyViewHolder> {

    Context context;
    List<Subject> subjectArrayList;
    PPEData ppeData;

    public SubjectWiseAdapter(Context context, List<Subject> subjectArrayList, PPEData ppeData) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.ppeData = ppeData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_exam_wise_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Subject subject = subjectArrayList.get(i);
        myViewHolder.titleTV.setText(subject.getName());
        myViewHolder.rootCV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.progress_medium_yellow));
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView rootCV;
        TextView titleTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rootCV = itemView.findViewById(R.id.cv_root);
            titleTV = itemView.findViewById(R.id.tv_title);

            titleTV.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position >= 0) {
                subjectArrayList.get(position);
                Intent intent = new Intent(context, ExamAndSubjectWiseActivity.class);
                intent.putExtra(Constants.Extras.SUBJECT_ID, subjectArrayList.get(position).getId());
                intent.putExtra(Constants.Extras.TYPE,Constants.PastPaperExtras.SUBJECT_WISE);
                intent.putExtra(Constants.PastPaperExtras.PPE_DATA, (Serializable) ppeData);
                context.startActivity(intent);
            }
        }
    }
}

