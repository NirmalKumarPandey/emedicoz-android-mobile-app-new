package com.emedicoz.app.bookmark.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.bookmark.model.TestModel;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.testmodule.activity.TestBookmarkActivity;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestAdapternew extends RecyclerView.Adapter<TestAdapternew.MyViewHolder> implements Html.ImageGetter {
    Context context;
    List<TestModel> itemlist = new ArrayList<>();
    int pos;
    TextView textView;
    String subjectId;
    String testSeriesId;
    String qTypeDqb;
    String nameOfTab;
    ArrayList<QuestionBank> questionBookmarks;
    private Drawable empty;
    LevelListDrawable mDrawable;
    private static final String TAG = "TestAdapternew";


    public TestAdapternew(Context context, List<TestModel> itemlist, String subjectId, String testSeriesId, String qTypeDqb, String nameOfTab) {
        this.context = context;
        this.itemlist = itemlist;
        this.subjectId = subjectId;
        this.testSeriesId = testSeriesId;
        this.qTypeDqb = qTypeDqb;
        this.nameOfTab = nameOfTab;
        questionBookmarks = new ArrayList<>();
        Log.e(TAG, TAG);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemtestadapter, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        pos = position;
        this.textView = holder.textView;
        Spanned spanned = HtmlCompat.fromHtml((position + 1) + ". " + itemlist.get(position).getQuestion(),
                HtmlCompat.FROM_HTML_MODE_LEGACY, this, null);
        holder.textView.setText(Helper.getFileName(spanned.toString()));
        holder.testBookmarkIV.setOnClickListener(view1 -> networkcallforremovebookmark(itemlist.get(position).getId(), holder.testBookmarkIV));
        holder.textView.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, TestBookmarkActivity.class);
            intent.putExtra(Const.QUESTION_BANK, questionBookmarks);
            intent.putExtra(Constants.Extras.SUB_ID, subjectId);
            intent.putExtra(Constants.Extras.POSITION, position);
            intent.putExtra(Const.TESTSERIES_ID, testSeriesId);
            intent.putExtra(Constants.Extras.Q_TYPE_DQB, qTypeDqb);
            intent.putExtra(Constants.Extras.TYPE, nameOfTab);
            context.startActivity(intent);
        });
    }

    private void networkcallforremovebookmark(String id, final ImageView testBookmarkIV) {
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.removeBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            testBookmarkIV.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ribbon));
                            notifyDataSetChanged();
                        }
                        Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Helper.showExceptionMsg(context, throwable);

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    @Override
    public Drawable getDrawable(String s) {
        LevelListDrawable d = new LevelListDrawable();
        empty = ContextCompat.getDrawable(context, R.drawable.landscape_placeholder);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        loadImage(s,d);
        return d;
    }

    public void loadImage(String s, LevelListDrawable d){
        Observable.fromCallable(() -> {
            String source = s;
            mDrawable = d;
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (result != null) {
                        Log.e(TAG, "getImage: "+ result);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable((Bitmap) result);
                        mDrawable.addLevel(1, 1, bitmapDrawable);
                        mDrawable.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                        mDrawable.setLevel(1);
                        CharSequence t = textView.getText();
                        textView.setText(t);
                    }
                });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView testBookmarkIV;

        public MyViewHolder(final View view) {
            super(view);
            textView = view.findViewById(R.id.textquestion);
            testBookmarkIV = view.findViewById(R.id.testBookmarkIV);
        }
    }
}
