package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ;

import java.util.List;

public class FAQExpandAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    ExpandableListView expandableListView;
    private Activity activity;
    private List<DescriptionFAQ> faqData;

    public FAQExpandAdapter(Activity activity, List<DescriptionFAQ> faqData, ExpandableListView expandableListView) {
        this.activity = activity;
        this.faqData = faqData;
        this.expandableListView = expandableListView;
    }

    @Override
    public void onClick(View view) {
        if (expandableListView.isGroupExpanded((Integer) view.getTag())) {
            expandableListView.collapseGroup((Integer) view.getTag());
        } else {
            expandableListView.expandGroup((Integer) view.getTag());
        }
    }

    @Override
    public int getGroupCount() {
        return faqData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(activity, R.layout.faq_expand_layout, null);
        }
        TextView faq_question = view.findViewById(R.id.faq_question);
        faq_question.setText(faqData.get(i).getQuestion());
        TextView expandTV = view.findViewById(R.id.expandTV);
        TextView collapse = view.findViewById(R.id.collapseTV);
        if (isExpanded) {
            expandTV.setVisibility(View.GONE);
            collapse.setVisibility(View.VISIBLE);
        } else {
            collapse.setVisibility(View.GONE);
            expandTV.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(activity, R.layout.desc_review_layout, null);
        }
        TextView answeredTV = view.findViewById(R.id.answeredTV);
        answeredTV.setText(faqData.get(i).getDescription());
        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
