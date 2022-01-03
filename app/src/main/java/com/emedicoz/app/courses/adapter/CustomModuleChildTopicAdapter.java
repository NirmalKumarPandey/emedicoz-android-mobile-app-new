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
import com.emedicoz.app.courses.fragment.CustomModuleTopicNew;
import com.emedicoz.app.modelo.custommodule.SubjectData;
import com.emedicoz.app.modelo.custommodule.Topic;

import java.util.List;


public class CustomModuleChildTopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<SubjectData> subjectData;
    String type;
    int p;
    CustomModuleTopicNew mFragment;

    public CustomModuleChildTopicAdapter(Context context, int p, List<SubjectData> subjectData, String type, CustomModuleTopicNew mFragment) {
        this.context = context;
        this.type = type;
        this.subjectData = subjectData;
        this.p = p;
        this.mFragment = mFragment;

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
        else if (type.equals("3"))
            return 3;
        else return 2;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final CourseHolder holder1 = (CourseHolder) holder;
        if (type.equals("1")) {
            (holder1).chackbox.setChecked(true);

        } else if (type.equals("2"))
            (holder1).chackbox.setChecked(false);
        else {
            if (((CourseActivity) context).subjectData.get(p).getTopics().get(position).getChecked())
                (holder1).chackbox.setChecked(true);
            else
                (holder1).chackbox.setChecked(false);

        }
        holder1.nameTV.setText(((CourseActivity) context).subjectData.get(p).getTopics().get(position).getTopic());

        holder1.parentLL.setOnClickListener((View v) -> {
            if (holder1.chackbox.isChecked()) {
                (holder1).chackbox.setChecked(false);
                ((CourseActivity) context).subjectData.get(p).getTopics().get(position).setChecked(false);

            } else {
                (holder1).chackbox.setChecked(true);
                ((CourseActivity) context).subjectData.get(p).getTopics().get(position).setChecked(true);

            }
        });

        holder1.allMaterial.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return ((CourseActivity) context).subjectData.get(p).getTopics().size();
    }

    public List<Topic> getvalue() {
        return ((CourseActivity) context).subjectData.get(p).getTopics();
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
            nameTV = itemView.findViewById(R.id.nameTV);
            chackbox = itemView.findViewById(R.id.chackbox);


        }
    }

}
