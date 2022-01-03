package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailComboCourseAdapter extends RecyclerView.Adapter<DetailComboCourseAdapter.DetailComboCourseViewHolder> {

    Activity activity;
    Topics[] topics;
    String courseId;
    SingleCourseData singleCourseData;
    Progress mprogress;
    DetailComboCourseAdapterTwo detailComboCourseAdapterTwo;

    public DetailComboCourseAdapter(Activity activity, Topics[] topics, String courseId) {
        this.activity = activity;
        this.topics = topics;
        this.courseId = courseId;
        mprogress = new Progress(activity);
        mprogress.setCancelable(false);
    }

    @NonNull
    @Override
    public DetailComboCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_combo_detail, viewGroup, false);
        return new DetailComboCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailComboCourseViewHolder viewHolder, int position) {
        Log.e("onBindViewHolder: ", "DetailComboCourse");
        viewHolder.curriculumTextTV.setText(topics[position].getTitle());
        Course course = setTopicToCourse(topics[position]);
        networkCallForCourseInfoRaw(course, viewHolder);
        /*viewHolder.curriculumTextTV2.setText(singleCourseData.getCurriculam().getTitle());
        detailComboCourseAdapterTwo = new DetailComboCourseAdapterTwo(activity,singleCourseData.getCurriculam().getFile_meta());
        viewHolder.curriculumExpListLL.setAdapter(detailComboCourseAdapterTwo);*/
    }

    @Override
    public int getItemCount() {
        return topics.length;
    }

    public void networkCallForCourseInfoRaw(Course course, final DetailComboCourseViewHolder viewHolder) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        Log.e("Course_Id", courseId);
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getSingleCourseInfoRaw(SharedPreference.getInstance().getLoggedInUser().getId(),
                course.getId(), course.getCourse_type(), course.getIs_combo(), courseId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mprogress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            singleCourseData = gson.fromJson(data.toString(), SingleCourseData.class);
                            viewHolder.curriculumTextTV2.setText(singleCourseData.getCurriculam().getTitle());
                            detailComboCourseAdapterTwo = new DetailComboCourseAdapterTwo(activity, singleCourseData.getCurriculam().getFile_meta());
                            viewHolder.comboDetailRV.setAdapter(detailComboCourseAdapterTwo);
                        } else {

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
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

    private Course setTopicToCourse(Topics topics) {
        Course course = new Course();
        course.setId(topics.getTopic_id());
        course.setCourse_type(topics.getCourse_type());
        course.setCover_image(topics.getCover_image());
        course.setDesc_header_image(topics.getDesc_header_image());
        course.setIs_combo(topics.getIs_combo());
        course.setTitle(topics.getTitle());
        course.setValidity(topics.getValidity());
        return course;
    }

    public class DetailComboCourseViewHolder extends RecyclerView.ViewHolder {

        TextView curriculumTextTV, curriculumTextTV2;
        NonScrollRecyclerView comboDetailRV;

        public DetailComboCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            curriculumTextTV = itemView.findViewById(R.id.comboDetailTitle);
            curriculumTextTV2 = itemView.findViewById(R.id.comboDetailSubTitle);
            comboDetailRV = itemView.findViewById(R.id.comboDetailRV);
            comboDetailRV.setLayoutManager(new LinearLayoutManager(activity));
/*            curriculumTextTV2.setVisibility(View.VISIBLE);
            curriculumExpListLL = itemView.findViewById(R.id.curriculumExpListLL);
            curriculumExpListLL.setLayoutManager(new LinearLayoutManager(activity));*/
        }
    }
}
