package com.emedicoz.app.feeds.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.LiveStreamActivity;
import com.emedicoz.app.feeds.model.chatPojo;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable {

    private static final String TAG = "LiveAdapter";
    public ArrayList<chatPojo> dataSet;
    Context mContext;
    Bitmap bitmap;
    String type;
    private int dataPosition = 0;
    private ArrayList<LinearLayout> linearLayoutList;
    private ArrayList<RelativeLayout> relativeLayoutList;
    private ArrayList<TextView> textViewList;
    private ArrayList<TextView> percentageTVList;
    private ArrayList<String> optionList;

    public ChatAdapter() {
    }

    public ChatAdapter(Context context, String type, ArrayList<chatPojo> dataSet) {
        this.mContext = context;
        this.dataSet = dataSet;
        this.type = type;
        linearLayoutList = new ArrayList<>();
        relativeLayoutList = new ArrayList<>();
        textViewList = new ArrayList<>();
        percentageTVList = new ArrayList<>();
        optionList = new ArrayList<>();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int listPosition) {
        Log.e(TAG, "onBindViewHolder is called: " + listPosition);
        int itemtype = getItemViewType(listPosition);
        if (itemtype == 1) {
            MyViewHolder2 holder = (MyViewHolder2) holder1;
            setTransparentData(holder, listPosition);
        } else {
            MyViewHolder holder = (MyViewHolder) holder1;
            setData(holder, listPosition);
        }
    }

    private void addPollOption(MyViewHolder holder, chatPojo chatPojo, int listPosition) {
        for (int j = 1; j <= 4; j++) {
            switch (j) {
                case 1:
                    if (chatPojo.getPoll().getOption1().isEmpty()) {
                        break;
                    }
                    holder.pollLL.addView(initMCQOptionView("A", chatPojo.getPoll().getOption1(), j, listPosition, holder));
                    break;
                case 2:
                    if (chatPojo.getPoll().getOption2().isEmpty()) {
                        break;
                    }
                    holder.pollLL.addView(initMCQOptionView("B", chatPojo.getPoll().getOption2(), j, listPosition, holder));

                    break;
                case 3:
                    if (chatPojo.getPoll().getOption3().isEmpty()) {
                        break;
                    }
                    holder.pollLL.addView(initMCQOptionView("C", chatPojo.getPoll().getOption3(),
                            j, listPosition, holder));
                    break;
                case 4:
                    if (chatPojo.getPoll().getOption4().isEmpty()) {
                        break;
                    }
                    holder.pollLL.addView(initMCQOptionView("D", chatPojo.getPoll().getOption4(),
                            j, listPosition, holder));
                    break;
                default:
                    break;
            }
        }
    }

    public LinearLayout initMCQOptionView(String text1, String text2, int tag, int listPosition, MyViewHolder holder) {
        LinearLayout v = (LinearLayout) View.inflate(mContext, R.layout.single_item_live_poll, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);
        ProgressBar progressBar = v.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        TextView optionTV = v.findViewById(R.id.optionTV);
        optionTV.setText(text1 + ": " + text2);
        TextView optionTvProgress = v.findViewById(R.id.optionTV1);
        optionTvProgress.setText(text1 + ": " + text2);
        RelativeLayout progressRL = v.findViewById(R.id.progressRL);
        TextView percentageTV = v.findViewById(R.id.percentageTV);
        percentageTV.setVisibility(View.VISIBLE);
        v.setLayoutParams(lp);
        v.setTag(tag);
        linearLayoutList.add(v);
        relativeLayoutList.add(progressRL);
        textViewList.add(optionTV);
        percentageTVList.add(percentageTV);
        optionList.add(String.valueOf(tag - 1));
        if (String.valueOf(dataSet.get(listPosition).getPoll().getIsPollEnded()).equalsIgnoreCase("1")) {
            Log.e(TAG, "initMCQOptionView: IsPollEnded"+" position: "+listPosition );
            if (!GenericUtils.isEmpty(dataSet.get(listPosition).getPoll().getUserAnsweredId())) {
                Log.e(TAG, "initMCQOptionView: IsPollEnded1"+" position: "+listPosition );
                String[] userAnswerIds = dataSet.get(listPosition).getPoll().getUserAnsweredId().split(",");
                String[] answers = dataSet.get(listPosition).getPoll().getAnswer().split(",");
                if (Arrays.asList(userAnswerIds).contains(SharedPreference.getInstance().getLoggedInUser().getId())) {
                    Log.e(TAG, "initMCQOptionView: IsPollEnded2"+" position: "+listPosition );
                    for (int i = 0; i < userAnswerIds.length; i++) {
                        if (userAnswerIds[i].equalsIgnoreCase(SharedPreference.getInstance().getLoggedInUser().getId())) {
                            Log.e(TAG, "initMCQOptionView: IsPollEnded3"+" position: "+listPosition );
                            if (Integer.parseInt(answers[i]) == tag - 1) {
                                Log.e(TAG, "initMCQOptionView: IsPollEnded4"+" position: "+listPosition );
                                if (answers[i].equals(String.valueOf(dataSet.get(listPosition).getPoll().getCorrectAnswer() - 1))) {
                                    Log.e(TAG, "initMCQOptionView: IsPollEnded5"+" position: "+listPosition );
                                    holder.isCorrectTV.setVisibility(View.VISIBLE);
                                    holder.isCorrectTV.setText("Your answer is correct");
                                    holder.isCorrectTV.setTextColor(Color.parseColor("#2e7d32"));
                                    ((ProgressBar) progressRL.getChildAt(0)).setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.gradient_progress));
                                } else {
                                    Log.e(TAG, "initMCQOptionView: IsPollEnded6"+" position: "+listPosition );
                                    holder.isCorrectTV.setVisibility(View.VISIBLE);
                                    holder.isCorrectTV.setText("Your answer is wrong");
                                    holder.isCorrectTV.setTextColor(Color.parseColor("#c62828"));
                                    ((ProgressBar) progressRL.getChildAt(0)).setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.gradient_progress_wrong));
                                }
                            } else {
                                Log.e(TAG, "initMCQOptionView: IsPollEnded7"+" position: "+listPosition );
                                if (tag - 1 == Integer.parseInt(String.valueOf(dataSet.get(listPosition).getPoll().getCorrectAnswer() - 1))) {
                                    Log.e(TAG, "initMCQOptionView: IsPollEnded8"+" position: "+listPosition );
                                    ((ProgressBar) progressRL.getChildAt(0)).setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.gradient_progress));
                                }
                            }
                            setPercentage(percentageTV,progressBar,tag,listPosition);
                        }
                    }
                    progressRL.setVisibility(View.VISIBLE);
                    optionTV.setVisibility(View.GONE);
                    Log.e(TAG, "initMCQOptionView: " + "it contains");
                } else {
                    Log.e(TAG, "initMCQOptionView: IsPollEnded9"+" position: "+listPosition );
                    progressRL.setVisibility(View.VISIBLE);
                    optionTV.setVisibility(View.GONE);
                    holder.isCorrectTV.setVisibility(View.VISIBLE);
                    holder.isCorrectTV.setText("You have not attempted this question.");
                    holder.isCorrectTV.setTextColor(Color.parseColor("#616161"));
                    setPercentage(percentageTV,progressBar,tag,listPosition);
                }
            } else {
                Log.e(TAG, "initMCQOptionView: IsPollEnded10"+" position: "+listPosition );
                progressRL.setVisibility(View.VISIBLE);
                optionTV.setVisibility(View.GONE);
                holder.isCorrectTV.setVisibility(View.VISIBLE);
                holder.isCorrectTV.setText("You have not attempted this question.");
                holder.isCorrectTV.setTextColor(Color.parseColor("#616161"));
                setPercentage(percentageTV,progressBar,tag,listPosition);
            }
        } else {
            Log.e(TAG, "initMCQOptionView: PollNotEnded1"+" position: "+listPosition );
            if (!GenericUtils.isEmpty(dataSet.get(listPosition).getPoll().getUserAnsweredId())) {
                Log.e(TAG, "initMCQOptionView: PollNotEnded2"+" position: "+listPosition );
                String[] userAnswerIds = dataSet.get(listPosition).getPoll().getUserAnsweredId().split(",");
                String[] answers = dataSet.get(listPosition).getPoll().getAnswer().split(",");
                if (Arrays.asList(userAnswerIds).contains(SharedPreference.getInstance().getLoggedInUser().getId())) {
                    Log.e(TAG, "initMCQOptionView: PollNotEnded3"+" position: "+listPosition );
                    for (int i = 0; i < userAnswerIds.length; i++) {
                        if (userAnswerIds[i].equalsIgnoreCase(SharedPreference.getInstance().getLoggedInUser().getId())) {
                            Log.e(TAG, "initMCQOptionView: PollNotEnded4"+" position: "+listPosition );
                            if (Integer.parseInt(answers[i]) == tag - 1) {
                                Log.e(TAG, "initMCQOptionView: PollNotEnded5"+" position: "+listPosition );
                                if (answers[i].equals(String.valueOf(dataSet.get(listPosition).getPoll().getCorrectAnswer() - 1))) {
                                    Log.e(TAG, "initMCQOptionView: PollNotEnded6"+" position: "+listPosition );
                                    holder.isCorrectTV.setVisibility(View.VISIBLE);
                                    holder.isCorrectTV.setText("Your answer is correct");
                                    holder.isCorrectTV.setTextColor(Color.parseColor("#2e7d32"));
                                    ((ProgressBar) progressRL.getChildAt(0)).setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.gradient_progress));
                                } else {
                                    Log.e(TAG, "initMCQOptionView: PollNotEnded7"+" position: "+listPosition );
                                    holder.isCorrectTV.setVisibility(View.VISIBLE);
                                    holder.isCorrectTV.setText("Your answer is wrong");
                                    holder.isCorrectTV.setTextColor(Color.parseColor("#c62828"));
                                    ((ProgressBar) progressRL.getChildAt(0)).setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.gradient_progress_wrong));
                                }
                            } else {
                                Log.e(TAG, "initMCQOptionView: PollNotEnded8"+" position: "+listPosition );
                                if (tag - 1 == Integer.parseInt(String.valueOf(dataSet.get(listPosition).getPoll().getCorrectAnswer() - 1))) {
                                    Log.e(TAG, "initMCQOptionView: PollNotEnded9"+" position: "+listPosition );
                                    ((ProgressBar) progressRL.getChildAt(0)).setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.gradient_progress));
                                }
                            }
                            setPercentage(percentageTV,progressBar,tag,listPosition);
                        }
                    }
                    progressRL.setVisibility(View.VISIBLE);
                    optionTV.setVisibility(View.GONE);
                    Log.e(TAG, "initMCQOptionView: " + "it contains");
                }
            }else {
                holder.isCorrectTV.setVisibility(View.GONE);
            }
        }
        v.setOnClickListener(v1 -> {
            if (!String.valueOf(dataSet.get(listPosition).getPoll().getIsPollEnded()).equalsIgnoreCase("1")) {
                String[] userAnswerIds = dataSet.get(listPosition).getPoll().getUserAnsweredId().split(",");
                if (!Arrays.asList(userAnswerIds).contains(SharedPreference.getInstance().getLoggedInUser().getId())) {
                    int selectedOptionPosition = ((int) v1.getTag()) - 1;
                    Log.e(TAG, String.valueOf(listPosition));
                    try {
                        if (mContext instanceof LiveStreamActivity) {
                            ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("answer").setValue(dataSet.get(listPosition).getPoll().getAnswer() + String.valueOf(selectedOptionPosition) + ",");
                            ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("userAnsweredId").setValue(dataSet.get(listPosition).getPoll().getUserAnsweredId() + SharedPreference.getInstance().getLoggedInUser().getId() + ",");
                            long usersAttempted = 0;
                            if (dataSet.get(listPosition).getPoll().getTotalUsersAttempted().equals("")) {
                                usersAttempted = 0;
                            } else {
                                usersAttempted = Long.parseLong(dataSet.get(listPosition).getPoll().getTotalUsersAttempted());
                            }
                            ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("totalUsersAttempted").setValue(String.valueOf(usersAttempted + 1));
                            switch (selectedOptionPosition) {
                                case 0:
                                    long usersAttemptedOption1 = 0;
                                    if (dataSet.get(listPosition).getPoll().getTotalUsersAttempted().equals("")) {
                                        usersAttemptedOption1 = 0;
                                    } else {
                                        usersAttemptedOption1 = Long.parseLong(dataSet.get(listPosition).getPoll().getUsersAttemptedOption1());
                                    }
                                    ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("usersAttemptedOption1").setValue(String.valueOf(usersAttemptedOption1 + 1));
                                    break;
                                case 1:
                                    long usersAttemptedOption2 = 0;
                                    if (dataSet.get(listPosition).getPoll().getTotalUsersAttempted().equals("")) {
                                        usersAttemptedOption2 = 0;
                                    } else {
                                        usersAttemptedOption2 = Long.parseLong(dataSet.get(listPosition).getPoll().getUsersAttemptedOption2());
                                    }
                                    ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("usersAttemptedOption2").setValue(String.valueOf(usersAttemptedOption2 + 1));
                                    break;
                                case 2:
                                    long usersAttemptedOption3 = 0;
                                    if (dataSet.get(listPosition).getPoll().getTotalUsersAttempted().equals("")) {
                                        usersAttemptedOption3 = 0;
                                    } else {
                                        usersAttemptedOption3 = Long.parseLong(dataSet.get(listPosition).getPoll().getUsersAttemptedOption3());
                                    }
                                    ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("usersAttemptedOption3").setValue(String.valueOf(usersAttemptedOption3 + 1));
                                    break;
                                case 3:
                                    long usersAttemptedOption4 = 0;
                                    if (dataSet.get(listPosition).getPoll().getTotalUsersAttempted().equals("")) {
                                        usersAttemptedOption4 = 0;
                                    } else {
                                        usersAttemptedOption4 = Long.parseLong(dataSet.get(listPosition).getPoll().getUsersAttemptedOption4());
                                    }
                                    ((LiveStreamActivity) mContext).query.getRef().child(((LiveStreamActivity) mContext).nodes.get(listPosition)).child("poll").child("usersAttemptedOption4").setValue(String.valueOf(usersAttemptedOption4 + 1));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return v;
    }

    private void setPercentage(TextView percentageTV, ProgressBar progressBar, int tag, int listPosition) {
        if (dataSet.get(listPosition).getPoll().getUsersAttemptedOption1().equals("")) {
            dataSet.get(listPosition).getPoll().setUsersAttemptedOption1("0");
        }
        if (dataSet.get(listPosition).getPoll().getUsersAttemptedOption2().equals("")) {
            dataSet.get(listPosition).getPoll().setUsersAttemptedOption2("0");
        }
        if (dataSet.get(listPosition).getPoll().getUsersAttemptedOption3().equals("")) {
            dataSet.get(listPosition).getPoll().setUsersAttemptedOption3("0");
        }
        if (dataSet.get(listPosition).getPoll().getUsersAttemptedOption4().equals("")) {
            dataSet.get(listPosition).getPoll().setUsersAttemptedOption4("0");
        }
        if (dataSet.get(listPosition).getPoll().getTotalUsersAttempted().equals("")) {
            dataSet.get(listPosition).getPoll().setTotalUsersAttempted("0");
        }
        double percentageOption1 = (Double.parseDouble(dataSet.get(listPosition).getPoll().getUsersAttemptedOption1()) / Double.parseDouble(dataSet.get(listPosition).getPoll().getTotalUsersAttempted())) * 100;
        double percentageOption2 = (Double.parseDouble(dataSet.get(listPosition).getPoll().getUsersAttemptedOption2()) / Double.parseDouble(dataSet.get(listPosition).getPoll().getTotalUsersAttempted())) * 100;
        double percentageOption3 = (Double.parseDouble(dataSet.get(listPosition).getPoll().getUsersAttemptedOption3()) / Double.parseDouble(dataSet.get(listPosition).getPoll().getTotalUsersAttempted())) * 100;
        double percentageOption4 = (Double.parseDouble(dataSet.get(listPosition).getPoll().getUsersAttemptedOption4()) / Double.parseDouble(dataSet.get(listPosition).getPoll().getTotalUsersAttempted())) * 100;
        switch (tag) {
            case 1:
                percentageTV.setText((int) Math.round(percentageOption1) + "%");
                progressBar.setProgress((int) Math.round(percentageOption1));
                break;
            case 2:
                percentageTV.setText((int) Math.round(percentageOption2) + "%");
                progressBar.setProgress((int) Math.round(percentageOption2));
                break;
            case 3:
                percentageTV.setText((int) Math.round(percentageOption3) + "%");
                progressBar.setProgress((int) Math.round(percentageOption3));
                break;
            case 4:
                percentageTV.setText((int) Math.round(percentageOption4) + "%");
                progressBar.setProgress((int) Math.round(percentageOption4));
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        Log.e(TAG, "onCreateViewHolder is called: ");
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trans_chat_layout, parent, false);
            return new MyViewHolder2(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat_layout, parent, false);
            return new MyViewHolder(view);
        }
    }

    public void setData(final MyViewHolder holder, int listPosition) {
        dataPosition = listPosition;
        if (type.equals("poll")) {
            if (dataSet.get(listPosition).getChatType() != null && dataSet.get(listPosition).getChatType().equalsIgnoreCase("poll")) {
                holder.pollParentLL.setVisibility(View.VISIBLE);
                holder.normalParentLL.setVisibility(View.GONE);
                holder.pollLL.removeAllViews();
                holder.questionTV.setText(dataSet.get(listPosition).getPoll().getQuestion());
                addPollOption(holder, dataSet.get(listPosition), listPosition);
            } else {
                holder.pollParentLL.setVisibility(View.GONE);
                holder.normalParentLL.setVisibility(View.VISIBLE);
                if (dataSet.get(listPosition).getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()) && !dataSet.get(listPosition).getType().equalsIgnoreCase("admin")) {
                    Log.e(TAG, "setData is called " + listPosition);
                    holder.llAdmin.setVisibility(View.GONE);
                    holder.llUser.setVisibility(View.VISIBLE);
                    if (!GenericUtils.isEmpty(dataSet.get(listPosition).getImage())) {
                        Log.e(TAG, "setData: " + dataSet.get(listPosition).getImage());
                        String finalImageUrl = dataSet.get(listPosition).getImage().replace(Helper.getS3UrlPrefix(), Helper.getCloudFrontUrl());

                        Log.e(TAG, "setData: " + "finalUrl:-" + finalImageUrl);
                        holder.imgUser.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(finalImageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_profile_img))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                        Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                        holder.imgUser.setImageBitmap(scale);
                                    }
                                });

                    } else {
                        holder.imgUser.setVisibility(View.GONE);
                    }

                    String msg = dataSet.get(listPosition).getName() + "-" + dataSet.get(listPosition).getMessage();

                    Spannable spannable = new SpannableString(msg);
                    spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.blue)), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tvUser.setText(spannable, TextView.BufferType.SPANNABLE);
                    //  holder.tvUser.setText(msg);

                    if (!SharedPreference.getInstance().getLoggedInUser().getProfile_picture().equals("")) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_profile_img))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                        Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                        holder.ivUser.setImageBitmap(scale);
                                    }
                                });
                    } else {
                        holder.ivUser.setImageResource(R.mipmap.default_profile_img);
                    }

                } else {
                    if (!dataSet.get(listPosition).getProfile_picture().equals("")) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(dataSet.get(listPosition).getProfile_picture())
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_profile_img))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                        Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                        holder.ivAdmin.setImageBitmap(scale);
                                    }
                                });
                    } else {
                        holder.ivAdmin.setImageResource(R.mipmap.default_profile_img);
                    }
                    holder.llUser.setVisibility(View.GONE);
                    holder.llAdmin.setVisibility(View.VISIBLE);

                    if (!GenericUtils.isEmpty(dataSet.get(listPosition).getImage())) {
                        Log.e(TAG, "setData: " + dataSet.get(listPosition).getImage());
                        String finalImageUrl = dataSet.get(listPosition).getImage().replace(Helper.getS3UrlPrefix(), Helper.getCloudFrontUrl());

                        Log.e(TAG, "setData: " + "finalUrl:-" + finalImageUrl);
                        holder.imgAdmin.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(finalImageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_profile_img))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                        Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                        holder.imgAdmin.setImageBitmap(scale);
                                    }
                                });
                    } else {
                        holder.imgAdmin.setVisibility(View.GONE);
                    }
                    String msg = dataSet.get(listPosition).getName() + "-" + dataSet.get(listPosition).getMessage();

                    Spannable spannable = new SpannableString(msg);
                    spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.blue)), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tvAdmin.setText(spannable, TextView.BufferType.SPANNABLE);

                }
            }
        } else {
            if (dataSet.get(listPosition).getChatType() != null && dataSet.get(listPosition).getChatType().equalsIgnoreCase("poll"))
                holder.normalParentLL.setVisibility(View.GONE);
            else
                holder.normalParentLL.setVisibility(View.VISIBLE);
            holder.pollParentLL.setVisibility(View.GONE);
            if (dataSet.get(listPosition).getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()) && !dataSet.get(listPosition).getType().equalsIgnoreCase("admin")) {
                Log.e(TAG, "setData is called " + listPosition);
                holder.llAdmin.setVisibility(View.GONE);
                holder.llUser.setVisibility(View.VISIBLE);
                if (!GenericUtils.isEmpty(dataSet.get(listPosition).getImage())) {
                    Log.e(TAG, "setData: " + dataSet.get(listPosition).getImage());
                    String finalImageUrl = dataSet.get(listPosition).getImage().replace(Helper.getS3UrlPrefix(), Helper.getCloudFrontUrl());

                    Log.e(TAG, "setData: " + "finalUrl:-" + finalImageUrl);
                    holder.imgUser.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(finalImageUrl)
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.default_profile_img))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                    holder.imgUser.setImageBitmap(scale);
                                }
                            });

                } else {
                    holder.imgUser.setVisibility(View.GONE);
                }

                String msg = dataSet.get(listPosition).getName() + "-" + dataSet.get(listPosition).getMessage();

                Spannable spannable = new SpannableString(msg);
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.blue)), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvUser.setText(spannable, TextView.BufferType.SPANNABLE);
                //  holder.tvUser.setText(msg);

                if (!SharedPreference.getInstance().getLoggedInUser().getProfile_picture().equals("")) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.default_profile_img))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                    holder.ivUser.setImageBitmap(scale);
                                }
                            });
                } else {
                    holder.ivUser.setImageResource(R.mipmap.default_profile_img);
                }

            } else {
                if (!dataSet.get(listPosition).getProfile_picture().equals("")) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(dataSet.get(listPosition).getProfile_picture())
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.default_profile_img))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                    holder.ivAdmin.setImageBitmap(scale);
                                }
                            });
                } else {
                    holder.ivAdmin.setImageResource(R.mipmap.default_profile_img);
                }
                holder.llUser.setVisibility(View.GONE);
                holder.llAdmin.setVisibility(View.VISIBLE);

                if (!GenericUtils.isEmpty(dataSet.get(listPosition).getImage())) {
                    Log.e(TAG, "setData: " + dataSet.get(listPosition).getImage());
                    String finalImageUrl = dataSet.get(listPosition).getImage().replace(Helper.getS3UrlPrefix(), Helper.getCloudFrontUrl());

                    Log.e(TAG, "setData: " + "finalUrl:-" + finalImageUrl);
                    holder.imgAdmin.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(finalImageUrl)
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.default_profile_img))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    Bitmap scale = Bitmap.createScaledBitmap(result, 100, 100, true);
                                    holder.imgAdmin.setImageBitmap(scale);
                                }
                            });
                } else {
                    holder.imgAdmin.setVisibility(View.GONE);
                }
                String msg = dataSet.get(listPosition).getName() + "-" + dataSet.get(listPosition).getMessage();

                Spannable spannable = new SpannableString(msg);
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.blue)), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dataSet.get(listPosition).getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvAdmin.setText(spannable, TextView.BufferType.SPANNABLE);

            }
        }
    }

    private void setTransparentData(final MyViewHolder2 holder, int listPosition) {

        if (!dataSet.get(listPosition).getProfile_picture().equals("")) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(dataSet.get(listPosition).getProfile_picture())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.default_profile_img))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            holder.ivAdmin.setImageBitmap(result);
                        }
                    });
        } else {
            holder.ivAdmin.setImageResource(R.mipmap.default_profile_img);
        }
        holder.llAdmin.setVisibility(View.VISIBLE);
        holder.tvAdmin.setText(dataSet.get(listPosition).getMessage());
        holder.tv_username.setText(dataSet.get(listPosition).getName());
        holder.tv_time.setText(dataSet.get(listPosition).getDate());

    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals("trans")) return 1; // list of transparent layout
        else return 0;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView ivAdmin, ivUser;
        ImageView imgAdmin, imgUser;
        TextView tvAdmin, tvUser;
        LinearLayout llAdmin, llUser;
        LinearLayout pollParentLL;
        TextView questionTV;
        LinearLayout pollLL;
        LinearLayout normalParentLL;
        LinearLayout mainLL;
        TextView isCorrectTV;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivUser = itemView.findViewById(R.id.iv_user);
            ivAdmin = itemView.findViewById(R.id.iv_admin);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvAdmin = itemView.findViewById(R.id.tv_admin);
            llUser = itemView.findViewById(R.id.ll_user);
            llAdmin = itemView.findViewById(R.id.ll_admin);
            imgAdmin = itemView.findViewById(R.id.imgAdmin);
            imgUser = itemView.findViewById(R.id.imgUser);
            pollParentLL = itemView.findViewById(R.id.pollParentLL);
            questionTV = itemView.findViewById(R.id.questionTV);
            pollLL = itemView.findViewById(R.id.pollLL);
            normalParentLL = itemView.findViewById(R.id.normalParentLL);
            isCorrectTV = itemView.findViewById(R.id.isCorrectTV);
            mainLL = itemView.findViewById(R.id.mainLL);
        }
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {

        ImageView ivAdmin;
        TextView tvAdmin, tv_username, tv_time;
        LinearLayout llAdmin;

        public MyViewHolder2(View itemView) {
            super(itemView);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_time = itemView.findViewById(R.id.tv_time);

            ivAdmin = itemView.findViewById(R.id.iv_admin);

            tvAdmin = itemView.findViewById(R.id.tv_admin);

            llAdmin = itemView.findViewById(R.id.ll_admin);
        }


    }

    private class PollViewHolder extends RecyclerView.ViewHolder {
        TextView questionTV;
        LinearLayout pollLL;

        public PollViewHolder(View itemView) {
            super(itemView);
            questionTV = itemView.findViewById(R.id.questionTV);
            pollLL = itemView.findViewById(R.id.pollLL);
        }
    }

    public void setType(String type) {
        this.type = type;
    }
}
