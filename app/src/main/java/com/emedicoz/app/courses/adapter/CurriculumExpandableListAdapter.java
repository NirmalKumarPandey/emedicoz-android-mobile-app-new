package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.SingleChildViewCurriculum;
import com.emedicoz.app.modelo.courses.File_Meta_Type;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.utilso.Constants;

import java.util.List;

/**
 * Created by app on 23/11/17.
 */

public class CurriculumExpandableListAdapter extends BaseExpandableListAdapter {

    SingleCourseData singleCourseData;
    SingleChildViewCurriculum singleChildViewCurriculum;
    private Context context;
    private List<File_Meta_Type> fileMetaTypeArrayList;
    private List<String> fileMetaTitle;


    public CurriculumExpandableListAdapter(Context activity, List<String> fileMetaTypeTitle, List<File_Meta_Type> fileMetaTypeArrayList, SingleCourseData singleCourseData) {
        this.context = activity;
        this.singleCourseData = singleCourseData;
        this.fileMetaTitle = fileMetaTypeTitle;
        this.fileMetaTypeArrayList = fileMetaTypeArrayList;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        TextView fileNameTV;
        TextView fileCountTV;
        LinearLayout rl1;
        ImageView fileTypeIV;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_row_currciulum_item, null);
        }

        fileNameTV = convertView.findViewById(R.id.fileTypeTV);
        fileTypeIV = convertView.findViewById(R.id.itemTypeimageIV);
        fileCountTV = convertView.findViewById(R.id.fileCountTV);
        rl1 = convertView.findViewById(R.id.rl1);
        rl1.setVisibility(View.GONE);

        fileCountTV.setText(String.valueOf(fileMetaTypeArrayList.get(groupPosition).getFileMetaArrayList().size()));
        switch (headerTitle) {
            case "pdf":
                fileNameTV.setText("PDF");
                fileTypeIV.setImageResource(R.mipmap.pdf_);
                break;
            case "video":
                fileNameTV.setText("VIDEO");
                fileTypeIV.setImageResource(R.mipmap.play_);
                break;
            case "epub":
                fileNameTV.setText("EPUB");
                fileTypeIV.setImageResource(R.mipmap.epub);
                break;
            case "ppt":
                fileNameTV.setText("PPT");
                fileTypeIV.setImageResource(R.mipmap.ppt);
                break;
            case Constants.TestType.TEST:
                fileNameTV.setText("Test");
                fileTypeIV.setImageResource(R.mipmap.test);
                break;
            case "doc":
                fileNameTV.setText("Doc");
                fileTypeIV.setImageResource(R.mipmap.doc_quiz);
                break;
            default:
                break;
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.single_row_currciulum_sub_item, null);
            convertView.setTag(singleChildViewCurriculum);
        } else {
            singleChildViewCurriculum = (SingleChildViewCurriculum) convertView.getTag();
        }
        return convertView;
    }


    @Override
    public int getGroupCount() {
        return fileMetaTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return fileMetaTypeArrayList.get(groupPosition).getFileMetaArrayList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return fileMetaTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return fileMetaTypeArrayList.get(groupPosition).getFileMetaArrayList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
