package com.emedicoz.app.epubear.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emedicoz.app.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amykh on 01.12.2016.
 */
public class HashMapAdapter extends BaseAdapter {
    private Map<String, String> mData = new HashMap<String, String>();
    private String[] mKeys;

    public HashMapAdapter(Map<String, String> data) {
        mData = data;
        mKeys = mData.keySet().toArray(new String[data.size()]);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        String url = mKeys[pos];
        String caption = getItem(pos).toString();

        View view;
        TextView text;

        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer_list, parent, false);
        } else {
            view = convertView;
        }

        text = view.findViewById(R.id.toc_caption);
        text.setText(caption);
        view.setTag(url);

        return view;
    }
}
