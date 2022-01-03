//package com.emedicoz.app.Feeds.Adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.emedicoz.app.CustomViews.SingleChildViewCurriculum;
//import com.emedicoz.app.Model.Courses.Curriculam;
//import com.emedicoz.app.Model.Courses.SingleCourseData;
//import com.emedicoz.app.R;
//
//import java.util.List;
//
//public class NotificationAdatper extends ExpandableRecyclerViewAdapter<NotificationAdatper.ParentViewHolder, NotificationAdatper.SubChildViewHolder> {
//
//    private Context context;
//    List<? extends ExpandableGroup> groupList;
//
//    public NotificationAdatper(List<? extends ExpandableGroup> groups, Context activity) {
//        super(groups);
//        this.context = activity;
//        this.groupList = groups;
//    }
//
//
//    @Override
//    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_currciulum_item, parent, false);
//        return new ParentViewHolder(view);
//    }
//
//    @Override
//    public SubChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_currciulum_sub_item, parent, false);
//        return new SubChildViewHolder(view);
//    }
//
//    @Override
//    public void onBindChildViewHolder(SubChildViewHolder holder, int flatPosition, ExpandableGroup group,
//                                      int childIndex) {
//        final Curriculam.File_meta fileMeta = (Curriculam.File_meta) group.getItems().get(childIndex);
//        holder.onBind(fileMeta);
//    }
//
//    @Override
//    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition,
//                                      ExpandableGroup group) {
//        holder.setGenreTitle(group);
//    }
//
//    public class ParentViewHolder extends GroupViewHolder {
//        TextView fileNameTV, fileCountTV;
//        LinearLayout rl1;
//        ImageView fileTypeIV;
//
//        public ParentViewHolder(View itemView) {
//            super(itemView);
//
//            fileNameTV = (TextView) itemView.findViewById(R.id.fileTypeTV);
//            fileTypeIV = (ImageView) itemView.findViewById(R.id.itemTypeimageIV);
//            fileCountTV = (TextView) itemView.findViewById(R.id.fileCountTV);
//            rl1 = itemView.findViewById(R.id.rl1);
//            rl1.setVisibility(View.GONE);
//        }
//
//        public void setGenreTitle(ExpandableGroup group) {
//            String headerTitle = (String) group.getTitle();
//
//        }
//    }
//
//    public class SubChildViewHolder extends ChildViewHolder {
//
//
//        public SubChildViewHolder(View itemView) {
//            super(itemView);
//        }
//
//        public void onBind(Curriculam.File_meta fileMeta) {
//        }
//    }
//
//
//}
