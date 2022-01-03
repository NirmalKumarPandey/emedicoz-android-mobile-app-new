package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Constants;

import java.util.List;

/**
 * Created by Cbc-03 on 01/06/18.
 */

public class TitleCurriculumRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<String> listTitle;

    public TitleCurriculumRecyclerView(List<String> listTitle, Context context) {
        this.listTitle = listTitle;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_list_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        String subListText = listTitle.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(subListText);
    }

    @Override
    public int getItemCount() {
        return listTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTV;
        ImageView icon;

        public ViewHolder(final View view) {
            super(view);
            listTV = view.findViewById(R.id.listTitle);
            icon = view.findViewById(R.id.icon);

            view.setOnClickListener((View v) -> Toast.makeText(view.getContext(), listTitle.get(getAdapterPosition()), Toast.LENGTH_SHORT).show());
        }

        public void setData(String title) {
            listTV.setVisibility(View.VISIBLE);
            switch (title) {
                case "pdf":
                    listTV.setText("PDF");
                    icon.setImageResource(R.mipmap.pdf_);
                    break;
                case "video":
                    listTV.setText("VIDEO");
                    icon.setImageResource(R.mipmap.play_);
                    break;
                case "epub":
                    listTV.setText("EPUB");
                    icon.setImageResource(R.mipmap.epub);
                    break;
                case "ppt":
                    listTV.setText("PPT");
                    icon.setImageResource(R.mipmap.ppt);
                    break;
                case Constants.TestType.TEST:
                    listTV.setText("Test");
                    icon.setImageResource(R.mipmap.letter);
                    break;
                case "doc":
                    listTV.setText("Doc");
                    icon.setImageResource(R.mipmap.letter);
                    break;
                default:
                    break;
            }
        }
    }
}

