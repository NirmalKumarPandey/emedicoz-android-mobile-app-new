package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.activity.ViewFlashCardActivity;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.utilso.Helper;

import org.jsoup.Jsoup;

import java.util.ArrayList;

public class DisplayFlashCardAdapter extends RecyclerView.Adapter<DisplayFlashCardAdapter.DisplayHolder> {

    private Context context;
    private ArrayList<FlashCards> flashCardsArrayList;
    private boolean isAllCards;

    public DisplayFlashCardAdapter(Context context, ArrayList<FlashCards> flashCardsArrayList, boolean isAllCards) {
        this.context = context;
        this.flashCardsArrayList = flashCardsArrayList;
        this.isAllCards = isAllCards;
    }

    @NonNull
    @Override
    public DisplayHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.flash_card_question_answer, viewGroup, false);
        return new DisplayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayHolder viewHolder, int i) {
        FlashCards flashCards = flashCardsArrayList.get(i);

        viewHolder.tv_title_with_serial_no.setText(flashCards.getSubdeck() + "/" + flashCards.getTopic() + "/Card " + flashCards.getId());
        viewHolder.tv_flashcard_count.setText(Integer.toString(i + 1) + "/" + flashCardsArrayList.size());

        if (flashCards.getIs_suspended() != null) {
            if (flashCards.getIs_suspended().equals("0")) {
                viewHolder.iv_suspended.setImageResource(R.mipmap.suspended);
            } else {
                viewHolder.iv_suspended.setImageResource(R.mipmap.suspended_active);
            }
        } else {
            viewHolder.iv_suspended.setImageResource(R.mipmap.suspended);
        }


        if (flashCards.getIsBookmarked().equals("0")) {
            viewHolder.iv_bookmark.setImageResource(R.mipmap.bookmark);
        } else {
            viewHolder.iv_bookmark.setImageResource(R.mipmap.bookmarked);
        }

        Log.e("TAG", "onBindViewHolder: isAllCards : " + isAllCards);


        if (isAllCards) {
            if (flashCards.getType().equals("QAE")) {
                setQAEdata(viewHolder, flashCards, i);
            } else if (flashCards.getType().equals("QA")) {
                setQAdata(viewHolder, flashCards, i);
            } else if (flashCards.getType().equals("FB")) {
                setFBdata(viewHolder, flashCards, i);
            } else if (flashCards.getType().equals("IM")) {
                setIMdata(viewHolder, flashCards, i);
            }
            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.ll_btn.setVisibility(View.GONE);
        } else {
            viewHolder.btn_show.setVisibility(View.VISIBLE);
            viewHolder.ll_btn.setVisibility(View.VISIBLE);
            if (flashCards.getType().equals("QAE")) {
                setQAEdata(viewHolder, flashCards, i);
            } else if (flashCards.getType().equals("QA")) {
                setQAdata(viewHolder, flashCards, i);
            } else if (flashCards.getType().equals("FB")) {
                setFBdata(viewHolder, flashCards, i);
            } else if (flashCards.getType().equals("IM")) {
                setIMdata(viewHolder, flashCards, i);
            }
        }
    }

    private void setQAdata(DisplayHolder viewHolder, FlashCards flashCards, int i) {
        viewHolder.ll_question_answer.setVisibility(View.VISIBLE);
        viewHolder.flashCard_image.setVisibility(View.GONE);

        viewHolder.webView_question.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getQuestion()), "text/html", "UTF-8", null);
        viewHolder.webview_answer.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getAnswer()), "text/html", "UTF-8", null);

        if (!TextUtils.isEmpty(flashCards.getExplanation())) {
            viewHolder.webView.loadDataWithBaseURL(null, flashCards.getExplanation(), "text/html", "UTF-8", null);
            viewHolder.ll_explanation.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_explanation.setVisibility(View.GONE);
        }
//        viewHolder.tv_answer_count.setText(Integer.toString(i + 1) + "/" + flashCardsArrayList.size());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            viewHolder.tv_question.setText(Html.fromHtml(flashCards.getQuestion().trim(), Html.FROM_HTML_MODE_LEGACY));
//            viewHolder.tv_answer.setText(Html.fromHtml(flashCards.getAnswer().trim(), Html.FROM_HTML_MODE_LEGACY));
        } else {
//            viewHolder.tv_question.setText(Html.fromHtml(flashCards.getQuestion().trim()));
//            viewHolder.tv_answer.setText(Html.fromHtml(flashCards.getAnswer().trim()));
        }

        if (flashCards.isShow()) {
            viewHolder.ll_answer_counter.setVisibility(View.VISIBLE);
            viewHolder.webview_answer.setVisibility(View.VISIBLE);
            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.ll_btn.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(flashCards.getExplanation())) {
                viewHolder.ll_explanation.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ll_explanation.setVisibility(View.GONE);
            }
        } else {
            viewHolder.ll_answer_counter.setVisibility(View.GONE);
            viewHolder.webview_answer.setVisibility(View.GONE);
            viewHolder.btn_show.setVisibility(View.VISIBLE);
            viewHolder.ll_btn.setVisibility(View.GONE);
            viewHolder.ll_explanation.setVisibility(View.GONE);
        }
    }

    private void setQAEdata(DisplayHolder viewHolder, FlashCards flashCards, int i) {
        viewHolder.ll_question_answer.setVisibility(View.VISIBLE);
        viewHolder.flashCard_image.setVisibility(View.GONE);
//        viewHolder.tv_answer_count.setText(Integer.toString(i + 1) + "/" + flashCardsArrayList.size());

        viewHolder.webView_question.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getQuestion()), "text/html", "UTF-8", null);
        viewHolder.webview_answer.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getAnswer()), "text/html", "UTF-8", null);

        if (!TextUtils.isEmpty(flashCards.getExplanation())) {
//            viewHolder.webView.getSettings().setUseWideViewPort(false);
            viewHolder.webView.setPadding(0, 0, 0, 0);
            viewHolder.webView.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getExplanation()), "text/html", "UTF-8", null);
            viewHolder.ll_explanation.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_explanation.setVisibility(View.GONE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            viewHolder.tv_question.setText(Html.fromHtml(flashCards.getQuestion().trim(), Html.FROM_HTML_MODE_LEGACY));
//            viewHolder.tv_answer.setText(Html.fromHtml(flashCards.getAnswer(), Html.FROM_HTML_MODE_LEGACY));
//            viewHolder.tv_explanation.setText(Html.fromHtml(flashCards.getExplanation(), Html.FROM_HTML_MODE_LEGACY));
        } else {
//            viewHolder.tv_question.setText(Html.fromHtml(flashCards.getQuestion().trim()));
//            viewHolder.tv_answer.setText(Html.fromHtml(flashCards.getAnswer()));
//            viewHolder.tv_explanation.setText(Html.fromHtml(flashCards.getExplanation()));
        }

        if (flashCards.isShow()) {
            viewHolder.ll_answer_counter.setVisibility(View.VISIBLE);
            viewHolder.webview_answer.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(flashCards.getExplanation())) {
                viewHolder.ll_explanation.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ll_explanation.setVisibility(View.GONE);
            }

            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.ll_btn.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_answer_counter.setVisibility(View.GONE);
            viewHolder.webview_answer.setVisibility(View.GONE);
            viewHolder.ll_explanation.setVisibility(View.GONE);
            viewHolder.btn_show.setVisibility(View.VISIBLE);
            viewHolder.ll_btn.setVisibility(View.GONE);
        }
    }

    private int getScale() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width) / new Double(100);
        val = val * 100d;
        return val.intValue();
    }

    private void setIMdata(DisplayHolder viewHolder, FlashCards flashCards, int i) {
        viewHolder.ll_question_answer.setVisibility(View.GONE);
        viewHolder.flashCard_image.setVisibility(View.VISIBLE);

        if (flashCards.isShow()) {
            viewHolder.ll_btn.setVisibility(View.VISIBLE);
            viewHolder.btn_show.setVisibility(View.GONE);

            Glide.with(context).load(Jsoup.parse(flashCards.getAnswer()).select("img").attr("src")).into(viewHolder.flashCard_image);
        } else {
            viewHolder.ll_btn.setVisibility(View.GONE);
            viewHolder.btn_show.setVisibility(View.VISIBLE);

            Glide.with(context).load(Jsoup.parse(flashCards.getQuestion()).select("img").attr("src")).into(viewHolder.flashCard_image);
        }
    }

    private void setFBdata(DisplayHolder viewHolder, FlashCards flashCards, int i) {
        viewHolder.ll_question_answer.setVisibility(View.VISIBLE);
        viewHolder.flashCard_image.setVisibility(View.GONE);
        viewHolder.ll_answer_counter.setVisibility(View.GONE);
        viewHolder.tv_answer.setVisibility(View.GONE);

        if (flashCards.isShow()) {
            if (!TextUtils.isEmpty(flashCards.getExplanation())) {
                viewHolder.webView.loadDataWithBaseURL(null, flashCards.getExplanation(), "text/html", "UTF-8", null);
                viewHolder.ll_explanation.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ll_explanation.setVisibility(View.GONE);
            }
            viewHolder.ll_btn.setVisibility(View.VISIBLE);
            viewHolder.btn_show.setVisibility(View.GONE);

            viewHolder.webView_question.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getAnswer()), "text/html", "UTF-8", null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                viewHolder.tv_question.setText(Html.fromHtml(flashCards.getAnswer().trim(), Html.FROM_HTML_MODE_LEGACY));
            } else {
//                viewHolder.tv_question.setText(Html.fromHtml(flashCards.getAnswer().trim()));
            }
        } else {
            viewHolder.ll_explanation.setVisibility(View.GONE);
            viewHolder.ll_btn.setVisibility(View.GONE);
            viewHolder.btn_show.setVisibility(View.VISIBLE);
            viewHolder.webView_question.loadDataWithBaseURL(null, Helper.getTextInCenter(flashCards.getQuestion()), "text/html", "UTF-8", null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                viewHolder.tv_question.setText(Html.fromHtml(flashCards.getQuestion().trim(), Html.FROM_HTML_MODE_LEGACY));
            } else {
//                viewHolder.tv_question.setText(Html.fromHtml(flashCards.getQuestion().trim()));
            }
        }

    }

    @Override
    public int getItemCount() {
        return null != flashCardsArrayList ? flashCardsArrayList.size() : 0;
    }

    class DisplayHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int position;
        TextView tv_title_with_serial_no, tv_question, tv_answer_count, tv_answer, tv_flashcard_count, tv_explanation, tv_easy, tv_medium, tv_hard;
        Button btn_show;
        LinearLayout ll_question_answer, ll_btn, ll_answer_counter, ll_explanation;
        ImageView iv_bookmark, iv_suspended, flashCard_image;
        WebView webView_question, webview_answer, webView;

        public DisplayHolder(@NonNull View itemView) {
            super(itemView);

            tv_title_with_serial_no = itemView.findViewById(R.id.tv_title_with_serial_no);
            webView = itemView.findViewById(R.id.webView);
            tv_question = itemView.findViewById(R.id.tv_question);
            tv_answer_count = itemView.findViewById(R.id.tv_answer_count);
            tv_answer = itemView.findViewById(R.id.tv_answer);
            webview_answer = itemView.findViewById(R.id.webview_answer);
            webView_question = itemView.findViewById(R.id.webView_question);
            tv_explanation = itemView.findViewById(R.id.tv_explanation);
            tv_flashcard_count = itemView.findViewById(R.id.tv_flashcard_count);
            btn_show = itemView.findViewById(R.id.btn_show);
            ll_btn = itemView.findViewById(R.id.ll_btn);

            tv_easy = itemView.findViewById(R.id.tv_easy);
            tv_medium = itemView.findViewById(R.id.tv_medium);
            tv_hard = itemView.findViewById(R.id.tv_hard);

            flashCard_image = itemView.findViewById(R.id.flashCard_image);
            iv_bookmark = itemView.findViewById(R.id.iv_bookmark);
            iv_suspended = itemView.findViewById(R.id.iv_suspended);

            ll_question_answer = itemView.findViewById(R.id.ll_question_answer);
            ll_answer_counter = itemView.findViewById(R.id.ll_answer_counter);
            ll_explanation = itemView.findViewById(R.id.ll_explanation);

            btn_show.setOnClickListener(this);
            ll_btn.setOnClickListener(this);
            iv_bookmark.setOnClickListener(this);
            iv_suspended.setOnClickListener(this);

            tv_easy.setOnClickListener(this);
            tv_medium.setOnClickListener(this);
            tv_hard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            if (position >= 0) {
                switch (view.getId()) {
                    case R.id.tv_easy:
                        ((ViewFlashCardActivity) context).setUpdate(position, "1");
                        break;
                    case R.id.tv_medium:
                        ((ViewFlashCardActivity) context).setUpdate(position, "2");
                        break;
                    case R.id.tv_hard:
                        ((ViewFlashCardActivity) context).setUpdate(position, "3");
                        break;
                    case R.id.iv_bookmark:
                        ((ViewFlashCardActivity) context).setBookmark(position);
                        break;
                    case R.id.iv_suspended:
                        View v = Helper.newCustomDialog(context, false, context.getString(R.string.suspend_card), context.getString(R.string.suspending_this_card_will_remove_this_card_from_your_review_schedule));

                        Button btn_cancel, btn_submit;

                        btn_cancel = v.findViewById(R.id.btn_cancel);
                        btn_submit = v.findViewById(R.id.btn_submit);

                        btn_cancel.setText(R.string.cancel);
                        btn_submit.setText(R.string.confirm);

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Helper.dismissDialog();
                            }
                        });

                        btn_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Helper.dismissDialog();
                                ((ViewFlashCardActivity) context).setSuspend(position);
                            }
                        });
                        break;
                    case R.id.btn_show:
                        for (int i = 0; i < flashCardsArrayList.size(); i++) {
                            if (position == i) {
                                flashCardsArrayList.get(position).setShow(true);
                            }
                        }
                        notifyDataSetChanged();
                        break;
                    case R.id.ll_btn:

                        break;
                }
            }
        }
    }
}
