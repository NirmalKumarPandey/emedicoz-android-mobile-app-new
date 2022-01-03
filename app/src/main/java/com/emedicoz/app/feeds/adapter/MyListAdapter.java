package com.emedicoz.app.feeds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.bookmark.SubListBookmarkAdapter;
import com.emedicoz.app.courses.adapter.SubListCourseAdapter;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cbc-03 on 08/01/17.
 */

public class MyListAdapter extends BaseAdapter {
    int cprStatus;
    List<Tags> tagsList;
    List<String> coursesSubList;
    boolean isFeedsExpanded = true;
    boolean isCoursesExpanded = false;
    boolean isBookmarkExpanded = true;
    RecyclerView subRecyclerView;
    SubListAdapterNew subListAdapter;
    SubListCourseAdapter subListCourseAdapter;
    private SubListBookmarkAdapter subListBookmarkAdapter;
    private Context context;
    private List<String> expandableListTitle;

    public MyListAdapter(Context context, List<String> expandableListTitle, List<Tags> tagsList, int cprStatus) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.tagsList = tagsList;
        this.cprStatus = cprStatus;
    }

    @Override
    public int getCount() {
        return expandableListTitle.size();
    }

    @Override
    public Object getItem(int i) {
        return expandableListTitle.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void onIndicatorClick(View v) {
        View view = (View) v.getTag();
        TextView tv = view.findViewById(R.id.listTitle);
        switch (tv.getText().toString()) {
            case Const.COURSES:
                onCoursesIndicatorClick(view);
                break;
            case Const.SPECIALITIES:
                onFeedsIndicatorClick(view);
                break;
            case Const.MY_BOOKMARKS:
                onBookmarkIndicatorClick(view);
                break;
            default:
                break;
        }
    }

    public void onFeedsIndicatorClick(View v) {
        if (isFeedsExpanded) {
            (v.findViewById(R.id.navRV)).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("+");
            isFeedsExpanded = false;
        } else {
            (v.findViewById(R.id.navRV)).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("-");
            subListAdapter.notifyDataSetChanged();
            isFeedsExpanded = true;
        }
    }

    public void onBookmarkIndicatorClick(View v) {
        if (isBookmarkExpanded) {
            (v.findViewById(R.id.navRV)).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("+");
            isBookmarkExpanded = false;
        } else {
            (v.findViewById(R.id.navRV)).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("-");
            subListBookmarkAdapter.notifyDataSetChanged();
            isBookmarkExpanded = true;
        }
    }

    public void onCoursesIndicatorClick(View v) {
        if (isCoursesExpanded) {
            (v.findViewById(R.id.navRV)).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("+");
            isCoursesExpanded = false;
        } else {
            (v.findViewById(R.id.navRV)).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("-");
            subListCourseAdapter.notifyDataSetChanged();
            isCoursesExpanded = true;
        }
    }

    public void onTextClick(String title, int type) {
        // can be used for writing logic of action performed on tag text click
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String listTitle = expandableListTitle.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.single_row_list_group, null);
        }
        RelativeLayout relativeLayout = convertView.findViewById(R.id.RL1);
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        ImageView image = convertView.findViewById(R.id.icon);
        TextView iconAdd = convertView.findViewById(R.id.iconadd);
        listTitleTextView.setText(listTitle);

        subRecyclerView = convertView.findViewById(R.id.navRV);
        subRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        subRecyclerView.setVisibility(View.GONE);
        if (listTitle.equals(Const.SPECIALITIES)) {
            if (tagsList == null)
                tagsList = new ArrayList<>();
            subListAdapter = new SubListAdapterNew(getTags(tagsList), context) {
                @Override
                public void onsubTextClick(Tags tag) {
                    SharedPreference.getInstance().putString(Const.SUBTITLE, new Gson().toJson(tag, Tags.class));
                    onTextClick(Const.SPECIALITIES, 1);
                }
            };
            subRecyclerView.setNestedScrollingEnabled(false);
            subRecyclerView.setAdapter(subListAdapter);
            if (isFeedsExpanded) {
                subRecyclerView.setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.iconadd)).setText("-");
            }
        }
        if (listTitle.equals(Const.COURSES)) {
            if (coursesSubList == null)
                coursesSubList = new ArrayList<>();
            subListCourseAdapter = new SubListCourseAdapter(Helper.getcourseSubList(context), context) {
                @Override
                public void OnsubTextClick(String str) {
                    SharedPreference.getInstance().putString(Const.SUBTITLECOURSE, str);
                    onTextClick(str, 0);
                }

            };
            subRecyclerView.setNestedScrollingEnabled(false);
            subRecyclerView.setAdapter(subListCourseAdapter);
            if (isCoursesExpanded) {
                subRecyclerView.setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.iconadd)).setText("-");
            }
        }

        if (listTitle.equals(Const.MY_BOOKMARKS)) {
            subListBookmarkAdapter = new SubListBookmarkAdapter(Helper.getBookmarkSubList(context), context) {
                @Override
                public void onSubTextClick(String str) {
                    SharedPreference.getInstance().putString(Const.SUBTITLECOURSE, str);
                    onTextClick(str, 0);
                }

            };
            subRecyclerView.setNestedScrollingEnabled(false);
            subRecyclerView.setAdapter(subListBookmarkAdapter);
            if (isBookmarkExpanded) {
                subRecyclerView.setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.iconadd)).setText("-");
            }
        }

        relativeLayout.setTag(convertView);

        if (listTitle.equals(Const.COURSES) || listTitle.equals(Const.SPECIALITIES)
                || listTitle.equals(Const.MY_BOOKMARKS)) {
            iconAdd.setTag(convertView);
            iconAdd.setOnClickListener(this::onIndicatorClick);
        }

        relativeLayout.setOnClickListener(view -> {
            View v = (View) view.getTag();
            TextView tv = v.findViewById(R.id.listTitle);
            if (tv.getText().toString().equalsIgnoreCase(Const.SPECIALITIES))
                SharedPreference.getInstance().putString(Const.SUBTITLE, null);

            onTextClick(tv.getText().toString(), 0);
        });

        iconAdd.setVisibility(View.GONE);

        switch (listTitle) {
            case Const.SPECIALITIES:
                iconAdd.setVisibility(View.VISIBLE);
                image.setImageResource(R.mipmap.home_active);
                break;
            case Const.COURSES:
                iconAdd.setVisibility(View.VISIBLE);
                image.setImageResource(R.mipmap.courses_icon_active);
                break;
            case Const.MY_BOOKMARKS:
                iconAdd.setVisibility(View.VISIBLE);
                image.setImageResource(R.mipmap.ribbon_blue);
                break;
            case Const.BOOKMARKS:
                image.setImageResource(R.mipmap.saved_notes_blue);
                break;
            case Const.Cpr:
                if (cprStatus == 0) {
                    relativeLayout.setVisibility(View.GONE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                image.setImageResource(R.mipmap.up_to_date_active);
                break;
            case Const.FEEDBACK:
                image.setImageResource(R.mipmap.help_blue);
                break;
            case Const.VIDEOS:
                image.setImageResource(R.mipmap.video_blue);
                break;
            case Const.RATEUS:
                image.setImageResource(R.mipmap.rate_blue);
                break;
            case Const.APPSETTING:
                image.setImageResource(R.mipmap.settings_blue);
                break;
            case Const.REWARDPOINTS:
                image.setImageResource(R.mipmap.refer_earn_blue);
                break;
            case Const.LOGOUT:
                image.setImageResource(R.mipmap.logout_blue);
                break;
            case Const.TERMS:
                image.setImageResource(R.mipmap.terms_condition);
                break;
            case Const.PRIVACY:
                image.setImageResource(R.mipmap.privacy_policy);
                break;
            default:
                break;
        }

        return convertView;
    }


    public List<Tags> getTags(List<Tags> tags) {
        ArrayList<Tags> tag = new ArrayList<>();
        if (!tags.isEmpty()) {
            int j = 0;

            while (j < tags.size()) {
                tag.add(tags.get(j));
                j++;
            }
        }
        return tag;
    }
}

