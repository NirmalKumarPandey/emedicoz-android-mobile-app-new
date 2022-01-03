package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Crs.EBookData;
import com.emedicoz.app.modelo.courses.Crs.EBookFile;
import com.emedicoz.app.modelo.dvl.DVLTopic;
import com.emedicoz.app.modelo.dvl.DvlData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.activity.DVLVideosActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CRSChildAdapter extends RecyclerView.Adapter<CRSChildAdapter.CRSChildViewHolder> {

    Activity activity;
    ArrayList<DVLTopic> topics;
    DvlData dvlData;
    EBookData crsFileData;
    ArrayList<EBookFile> files;

    public CRSChildAdapter(Activity activity, ArrayList<DVLTopic> topics, DvlData dvlData) {
        this.activity = activity;
        this.topics = topics;
        this.dvlData = dvlData;
    }

    @NonNull
    @Override
    public CRSChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_child_crs, viewGroup, false);
        return new CRSChildViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CRSChildViewHolder holder, final int i) {
        holder.titleTV.setText(topics.get(i).getTitle());
        holder.parentLL.setOnClickListener(v -> {

            Intent intent = new Intent(activity, DVLVideosActivity.class);
            intent.putExtra("topic_id", topics.get(i).getTopicId());
            intent.putExtra(Const.PARENT_ID, dvlData.getId());
            intent.putExtra(Const.DATA, dvlData);
            intent.putExtra(Constants.Extras.TYPE, Const.CRS_SECTION);
            activity.startActivity(intent);

        });
    }

    private void networkCallForCourseList(int i, final CRSChildViewHolder holder) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCRSFiles(SharedPreference.getInstance().getLoggedInUser().getId(), topics.get(i).getTopicId(), dvlData.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            mprogress.dismiss();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            crsFileData = gson.fromJson(data.toString(), EBookData.class);
                            Helper.getStorageInstance(activity).addRecordStore(Const.CRS_FILES, crsFileData.getFiles());
                            initCRSFileAdapter(crsFileData.getFiles(), holder);
                        } else {
                            mprogress.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
            }
        });
    }

    private void initCRSFileAdapter(ArrayList<EBookFile> files, CRSChildViewHolder holder) {


        if (holder.expListView.getVisibility() == View.GONE) {
            holder.expListView.setVisibility(View.VISIBLE);
            holder.imgArrow.setImageResource(R.mipmap.ic_crs_up_arrow);
        } else {
            holder.expListView.setVisibility(View.GONE);
            holder.imgArrow.setImageResource(R.mipmap.ic_crs_down_arrow);
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    class CRSChildViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        ImageView imgArrow;
        NonScrollRecyclerView expListView;
        LinearLayout mainCurriculumLL, parentLL;

        public CRSChildViewHolder(@NonNull View view) {
            super(view);
            expListView = view.findViewById(R.id.curriculumExpListLL);
            titleTV = view.findViewById(R.id.curriculumTextTV);
            mainCurriculumLL = view.findViewById(R.id.mainCurriculumLL);
            imgArrow = view.findViewById(R.id.imgArrow);
            parentLL = view.findViewById(R.id.parentLL);
            expListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        }
    }
}
