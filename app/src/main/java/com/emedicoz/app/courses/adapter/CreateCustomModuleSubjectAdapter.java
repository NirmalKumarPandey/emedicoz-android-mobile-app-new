package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.fragment.CreateCustomModuleSubject;
import com.emedicoz.app.courses.fragment.CustomModuleTopicNew;
import com.emedicoz.app.modelo.custommodule.SubjectData;

import java.util.HashMap;
import java.util.List;

public class CreateCustomModuleSubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    String type;
    List<SubjectData> subjectData;
    CreateCustomModuleSubject mFragment;
    HashMap<String, String> finalResponse = new HashMap<>();

    public CreateCustomModuleSubjectAdapter(Context context, List<SubjectData> subjectData, String type, HashMap<String, String> finalResponse, CreateCustomModuleSubject mFragment) {
        this.context = context;
        this.type = type;
        this.subjectData = subjectData;
        this.finalResponse = finalResponse;

        this.mFragment = mFragment;
        for (SubjectData temp : subjectData) {
            temp.setChecked(type.equals("1"));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_module_item, parent, false);
        return new CourseHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals("1"))
            return 1;
        else return 2;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final CourseHolder holder1 = (CourseHolder) holder;

        (holder1).chackbox.setChecked(subjectData.get(position).isChecked());
        if (subjectData.get(position).getCount() == subjectData.get(position).getTopics().size()) {
            holder1.allMaterial.setText("All Topics");
        } else if (((CourseActivity) context).subjectData.get(position).getCount() != 0)
            holder1.allMaterial.setText(subjectData.get(position).getCount() + " Topics");


        holder1.nameTV.setText(((CourseActivity) context).subjectData.get(position).getName());
        holder1.parentLL.setOnClickListener((View view) -> {
            subjectData.get(position).setChecked(!subjectData.get(position).isChecked());
            if (subjectData.get(position).isChecked()) {
                holder1.allMaterial.setVisibility(View.VISIBLE);
            } else {
                holder1.allMaterial.setVisibility(View.GONE);
            }
            (holder1).chackbox.setChecked(subjectData.get(position).isChecked());
            subjectData.get(position).setAllchecked(subjectData.get(position).isChecked());
        });

        holder1.allMaterial.setOnClickListener((View v) -> ((CourseActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, CustomModuleTopicNew.newInstance(((CourseActivity) context).subjectData, position, finalResponse))
                .addToBackStack("back")
                .commit());

        if (subjectData.get(position).isChecked())
            holder1.allMaterial.setVisibility(View.VISIBLE);
        else
            holder1.allMaterial.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return subjectData.size();
    }

    public class CourseHolder extends RecyclerView.ViewHolder {

        LinearLayout parentLL;
        CheckBox chackbox;
        TextView allMaterial;
        TextView nameTV;

        public CourseHolder(View itemView) {
            super(itemView);


            parentLL = itemView.findViewById(R.id.parentLL);
            allMaterial = itemView.findViewById(R.id.all_material);
            chackbox = itemView.findViewById(R.id.chackbox);
            nameTV = itemView.findViewById(R.id.nameTV);


        }
    }

}
