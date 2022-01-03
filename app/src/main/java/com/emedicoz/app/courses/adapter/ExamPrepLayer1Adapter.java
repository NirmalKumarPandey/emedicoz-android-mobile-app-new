package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.fragment.ExamPrepLayer1;
import com.emedicoz.app.customviews.CircleImageView;
import com.emedicoz.app.modelo.courses.ExamPrepItem;
import com.emedicoz.app.modelo.courses.Lists;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.SharedPreference;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Random;

public class ExamPrepLayer1Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    Bitmap bitmap;
    ExamPrepItem examPrepItem;
    int DEFAULT_SPAN_COUNT = 2;
    ExamPrepLayer1 examPrepLayer1;
    private SingleStudyModel singlestudyModel;

    public ExamPrepLayer1Adapter(Activity activity, ExamPrepItem examPrepItem, ExamPrepLayer1 fragment, SingleStudyModel singlestudyModel) {
        this.examPrepItem = examPrepItem;
        this.activity = activity;
        this.examPrepLayer1 = fragment;
        this.singlestudyModel = singlestudyModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(activity).inflate(R.layout.exam_prep_single_row_item, null);
            return new SingleStudyListHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(activity).inflate(R.layout.study_single_item, null);
            return new SingleStudyVideoListHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (((CourseActivity) activity).contentType.equals(Const.VIDEO))
            return 1;
        else return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (((CourseActivity) activity).contentType.equals(Const.VIDEO))
                ((SingleStudyVideoListHolder) holder).setData(examPrepItem.getList(), position);
            else
                ((SingleStudyListHolder) holder).setData(examPrepItem.getList(), position);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (examPrepItem != null && examPrepItem.getList() != null)
            return examPrepItem.getList().size();
        else
            return 0;

    }

    public class SingleStudyVideoListHolder extends RecyclerView.ViewHolder {
        ImageView textDrawable;
        CircleImageView logo;
        ImageView forward, liveIV;
        TextView counter;
        TextView title, subtitleTV;
        RelativeLayout study_single_itemLL;

        public SingleStudyVideoListHolder(View itemView) {
            super(itemView);
            liveIV = itemView.findViewById(R.id.liveIV);
            textDrawable = itemView.findViewById(R.id.study_item_logoIV);
            forward = itemView.findViewById(R.id.forwardIV);
            title = itemView.findViewById(R.id.study_item_titleTV);
            counter = itemView.findViewById(R.id.countTextTV);
            study_single_itemLL = itemView.findViewById(R.id.study_single_itemLL);

            counter.setVisibility(View.VISIBLE);
            forward.setVisibility(View.GONE);
        }

        public void setData(final ArrayList<Lists> list, final int position) {
            try {
                if (list.get(position).getIs_live() != null) {
                    if (list.get(position).getIs_live().equals("1"))
                        liveIV.setVisibility(View.VISIBLE);
                    else
                        liveIV.setVisibility(View.GONE);
                } else {
                    liveIV.setVisibility(View.GONE);
                }

                Glide.with(activity).asGif().load(R.drawable.live_gif).into(liveIV);
//            if (((CourseActivity) activity).contentType.equals(Const.CONCEPTT)) {
//                title.setText(examPrepItem.getList().get(position).getTitle());
//                counter.setText(String.format("%s %s", examPrepItem.getList().get(position).getTotal().get(0).getCount(),
//                        examPrepItem.getList().get(position).getTotal().get(0).getText()));
//            } else {
//                title.setText(list.get(position).getTitle());
//                counter.setText(list.get(position).getTotal().get(0).getCount());
//            }

                title.setText(list.get(position).getTitle());
                counter.setText(list.get(position).getTotal().get(0).getCount());

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                TextDrawable textDrawables = TextDrawable.builder()
                        .beginConfig()
                        .textColor(activity.getResources().getColor(R.color.white))
                        .endConfig()
                        .buildRound(String.valueOf(position + 1), Const.studyColor[position % 5]);
                textDrawable.setImageDrawable(textDrawables);


                // textDrawable.setBackgroundColor(color);


                study_single_itemLL.setOnClickListener(v -> {
                    if (list.get(position).getTotal().size() == 1) {
                        Intent examPrepLayer1 = new Intent(activity, CourseActivity.class);
                        examPrepLayer1.putExtra(Const.EXAMPREP, examPrepItem);
                        examPrepLayer1.putExtra(Const.FRAG_TYPE, Const.EXAMPREPLAST);
                        examPrepLayer1.putExtra(Const.TITLE, ((CourseActivity) activity).lists.getTitle());
                        examPrepLayer1.putExtra(Const.SINGLE_STUDY, singlestudyModel);
                        examPrepLayer1.putExtra(Const.CONTENT_TYPE, ((CourseActivity) activity).contentType);
                        examPrepLayer1.putExtra(Constants.Extras.TEST_TYPE, examPrepItem.getList().get(position).getTotal().get(0));
                        examPrepLayer1.putExtra(Const.LIST, examPrepItem.getList().get(position));
                        SharedPreference.getInstance().putString(Const.MAIN_ID, ((CourseActivity) activity).lists.getId());
                        activity.startActivity(examPrepLayer1);
                    } else {
                        Intent examPrepLayer1 = new Intent(activity, CourseActivity.class);
                        examPrepLayer1.putExtra(Const.EXAMPREP, examPrepItem);
                        examPrepLayer1.putExtra(Const.FRAG_TYPE, Const.EXAMPREP);
                        //examPrepLayer1.putExtra(Const.TEST_THIRD, true);
                        examPrepLayer1.putExtra(Const.SINGLE_STUDY, singlestudyModel);
                        examPrepLayer1.putExtra(Const.TITLE, ((CourseActivity) activity).lists.getTitle());
                        examPrepLayer1.putExtra(Const.CONTENT_TYPE, ((CourseActivity) activity).contentType);
                        examPrepLayer1.putExtra(Const.LIST, list.get(position));
                        SharedPreference.getInstance().putString(Const.MAIN_ID, ((CourseActivity) activity).lists.getId());
                        activity.startActivity(examPrepLayer1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SingleStudyListHolder extends RecyclerView.ViewHolder {
        RelativeLayout imageRl, parentLL, studyitemLL;
        ImageView imageIcon, liveIV;
        TextView titleCategory;
       // NonScrollRecyclerView subItemRV;

        public SingleStudyListHolder(View itemView) {
            super(itemView);
            liveIV = itemView.findViewById(R.id.liveIV);
            imageRl = itemView.findViewById(R.id.imageRL);
            parentLL = itemView.findViewById(R.id.parentLL);
            studyitemLL = itemView.findViewById(R.id.study_single_itemLL);
            imageIcon = itemView.findViewById(R.id.profileImage);
            titleCategory = itemView.findViewById(R.id.examPrepTitleTV);
            //  subItemRV = itemView.findViewById(R.id.subItemRV);
        }

        public void setData(final ArrayList<Lists> list, final int position) {
            try {
                SubItemStudyAdapter subItemStudyAdapter;
                if (list.get(position).getIs_live() != null) {
                    if (list.get(position).getIs_live().equals("1"))
                        liveIV.setVisibility(View.VISIBLE);
                    else
                        liveIV.setVisibility(View.GONE);
                } else {
                    liveIV.setVisibility(View.GONE);
                }


                if (!TextUtils.isEmpty(list.get(position).getImage_icon())) {
                    //      new GetImageFromUrl(imageIcon).execute(list.get(position).getImage_icon());
                    Ion.with(activity)
                            .load(list.get(position).getImage_icon())
                            .withBitmap()
                            .placeholder(R.drawable.landscape_placeholder)
                            .error(R.drawable.landscape_placeholder)
                            .asBitmap()
                            .setCallback(new FutureCallback<Bitmap>() {
                                @Override
                                public void onCompleted(Exception e, Bitmap result) {
                                    if (e == null && result != null)
                                        imageIcon.setImageBitmap(result);
                                    else
                                        imageIcon.setImageResource(R.drawable.landscape_placeholder);
                                }
                            });
                } else {
                    imageIcon.setImageResource(R.drawable.landscape_placeholder);
                }

                Log.e("setData: ", list.get(position).getTitle());
                titleCategory.setText(list.get(position).getTitle());

                // subItemRV.setLayoutManager(new GridLayoutManager(activity, DEFAULT_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
                subItemStudyAdapter = new SubItemStudyAdapter(activity, list.get(position).getTotal());
                //subItemRV.setAdapter(subItemStudyAdapter);
                // subItemRV.setLayoutFrozen(true);

                parentLL.setOnClickListener(view -> {
                    if (list.get(position).getTotal().size() == 1) {
                        Intent examPrepLayer1 = new Intent(activity, CourseActivity.class);
                        examPrepLayer1.putExtra(Const.EXAMPREP, examPrepItem);
                        examPrepLayer1.putExtra(Const.SINGLE_STUDY, singlestudyModel);
                        examPrepLayer1.putExtra(Const.FRAG_TYPE, Const.EXAMPREPLAST);
                        examPrepLayer1.putExtra(Const.TITLE, ((CourseActivity) activity).lists.getTitle());
                        examPrepLayer1.putExtra(Const.CONTENT_TYPE, ((CourseActivity) activity).contentType);
                        examPrepLayer1.putExtra(Constants.Extras.TEST_TYPE, examPrepItem.getList().get(position).getTotal().get(0));
                        examPrepLayer1.putExtra(Const.LIST, examPrepItem.getList().get(position));
                        SharedPreference.getInstance().putString(Const.MAIN_ID, ((CourseActivity) activity).lists.getId());
                        activity.startActivity(examPrepLayer1);
                    } else {
                        Intent examPrepLayer1 = new Intent(activity, CourseActivity.class);
                        examPrepLayer1.putExtra(Const.EXAMPREP, examPrepItem);
                        examPrepLayer1.putExtra(Const.SINGLE_STUDY, singlestudyModel);
                        examPrepLayer1.putExtra(Const.FRAG_TYPE, Const.EXAMPREP);
                        //    examPrepLayer1.putExtra(Const.TEST_THIRD, true);
                        examPrepLayer1.putExtra(Const.TITLE, ((CourseActivity) activity).lists.getTitle());
                        examPrepLayer1.putExtra(Const.CONTENT_TYPE, ((CourseActivity) activity).contentType);
                        examPrepLayer1.putExtra(Const.LIST, list.get(position));
                        SharedPreference.getInstance().putString(Const.MAIN_ID, ((CourseActivity) activity).lists.getId());
                        activity.startActivity(examPrepLayer1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

