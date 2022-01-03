package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.VideoSolution;
import com.emedicoz.app.modelo.VideoSolutionCounterData;
import com.emedicoz.app.modelo.VideoSolutionData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.DBHandler;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class VideoSolutionAdapter extends RecyclerView.Adapter<VideoSolutionAdapter.VideoSolutionViewHolder> {
    Activity activity;
    List<VideoSolutionData> videoSolutionArrayList;
    int viewCount = 0;
    int count = 1;
    DBHandler db;
    CardView cardView;
    int rowIndex = -1;

    public VideoSolutionAdapter(Activity activity, List<VideoSolutionData> videoSolutionArrayList) {
        this.activity = activity;
        this.videoSolutionArrayList = videoSolutionArrayList;
        db = new DBHandler(activity);
    }

    @NonNull
    @Override
    public VideoSolutionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_video_solution, viewGroup, false);
        return new VideoSolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoSolutionViewHolder videoSolutionViewHolder, final int position) {

        videoSolutionViewHolder.setData(videoSolutionArrayList.get(position), position);
        videoSolutionViewHolder.videoSolutionName.setText(videoSolutionArrayList.get(position).getTitle());
        if (videoSolutionArrayList.get(position).getDuration() != null) {
            videoSolutionViewHolder.videoSolutionDuration.setText(videoSolutionArrayList.get(position).getDuration());
        }


        videoSolutionViewHolder.videoSolutionPlayIV.setOnClickListener(v -> {
            rowIndex = position;
            VideoSolutionData data = videoSolutionArrayList.get(position);
            offlineData offline = getOfflineDataIds(data.getId(), Const.VIDEOS, activity, data.getId());
            if (offline == null)
                playOnlineVideo(data);
            else
                videoSolutionViewHolder.singleViewCLick(data, offline);
        });
    }

    private void playOnlineVideo(VideoSolutionData data) {
        if (Helper.isConnected(activity)) {
            if (!GenericUtils.isEmpty(data.getAllow_to_watch())) {

                if (data.getAllow_to_watch().equalsIgnoreCase("1")) {
                    networkCallForVideoCounter(data);
                } else {
                    Toast.makeText(activity, "You do not have permission to watch the video solution", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "You do not have permission to watch the video solution", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
    }

    private void networkCallForVideoCounter(final VideoSolutionData videoSolutionData) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getVideoSolutionCounter(SharedPreference.getInstance().getLoggedInUser().getId()
                , videoSolutionData.getId());//test_segment_id
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
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            VideoSolutionCounterData videoData = gson.fromJson(data.toString(), VideoSolutionCounterData.class);

                            if (videoData.getCounter() != null && !videoData.getCounter().equals("")) {
                                if (Integer.parseInt(videoData.getCounter()) <= Integer.parseInt(videoSolutionData.getView_count())) {

                                    ((VideoSolution) activity).rootView.setVisibility(View.VISIBLE);
                                    if (!videoSolutionData.getVideo_url_live().equals("")) {
                                        ((VideoSolution) activity).bitrate = true;
                                        ((VideoSolution) activity).initializePlayer(videoSolutionData.getVideo_url_live());
                                        SharedPreference.getInstance().putString("SOLUTION_LINK", videoSolutionData.getVideo_url_live());
                                    } else {
                                        ((VideoSolution) activity).bitrate = false;
                                        ((VideoSolution) activity).initializePlayer(videoSolutionData.getVideo_url());
                                        SharedPreference.getInstance().putString("SOLUTION_LINK", videoSolutionData.getVideo_url());
                                    }

                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(activity, "You have already completed your view limit", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(activity, activity.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, "No Videos Found", Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, API.API_GET_FILE_LIST_CURRICULUM);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoSolutionArrayList.size();
    }

    public class VideoSolutionViewHolder extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {
        eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
        long downloadId;
        TextView videoSolutionName;
        TextView messageTV;
        TextView videoSolutionDuration;
        ImageView videoSolutionPlayIV;
        ImageView downloadIV;
        ImageView deleteIV;
        ProgressBar downloadprogessPB;
        VideoSolutionData data;
        CardView solutionRL;

        public VideoSolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            videoSolutionName = itemView.findViewById(R.id.videoSolutionName);
            videoSolutionDuration = itemView.findViewById(R.id.videoSolutionDuration);
            videoSolutionPlayIV = itemView.findViewById(R.id.videoSolutionPlayIV);
            downloadIV = itemView.findViewById(R.id.videoSolutionDownloadIV);
            solutionRL = itemView.findViewById(R.id.solutionRL);
            deleteIV = itemView.findViewById(R.id.videoSolutionDeleteIV);
            downloadprogessPB = itemView.findViewById(R.id.downloadprogess);
            messageTV = itemView.findViewById(R.id.videoSolutionMessageTV);
            downloadprogessPB.setScaleY(1.5f);
            savedOfflineListener = this;
        }

        private void checkData(VideoSolutionData epubData, int position) {
            offlineData offlinedata = getOfflineDataIds(epubData.getId(),
                    Const.VIDEOS, activity, epubData.getId());

            downloadIV.setImageResource(R.mipmap.download_new_course);

            downloadprogessPB.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
            messageTV.setVisibility(View.GONE);
            downloadIV.setVisibility(View.GONE);
            if (offlinedata != null) {
                if (offlinedata.getRequestInfo() == null)
                    offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

                if (offlinedata.getRequestInfo() != null) {
                    downloadprogessPB.setVisibility(offlinedata.getRequestInfo().getProgress() < 100 ? View.VISIBLE : View.GONE);

                    //4 conditions to check at the time of intialising the video
                    //0. downloading in queue
                    if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        messageTV.setText(R.string.download_queued);
                        downloadId = offlinedata.getDownloadid();
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.VIDEOS);
                    }
                    //1. downloading in progress
                    else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                            && offlinedata.getRequestInfo().getProgress() < 100) {

                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        downloadprogessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                        downloadprogessPB.setVisibility(View.VISIBLE);
                        messageTV.setText(R.string.download_queued);
                        downloadId = offlinedata.getDownloadid();
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.VIDEOS);
                    }
                    //2. download complete
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlinedata.getRequestInfo().getProgress() == 100) {
                        messageTV.setText(R.string.downloaded_offline);
                        downloadprogessPB.setVisibility(View.GONE);
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.eye_on);
                        deleteIV.setVisibility(View.VISIBLE);

//                            sendViewMessage();
                    }
                    //3. downloading paused
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED && offlinedata.getRequestInfo().getProgress() < 100) {
                        downloadprogessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                        deleteIV.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_pause);
                        messageTV.setText(R.string.download_pasued);
                    }
                    //4. error intrrupted
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_ERROR && offlinedata.getRequestInfo().getProgress() < 100) {
                        messageTV.setText(R.string.error_in_downloading);
                        deleteIV.setVisibility(View.VISIBLE);
                        downloadprogessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_reload);
                    }
                    messageTV.setVisibility(offlinedata.getRequestInfo() != null ? View.VISIBLE : View.GONE);
                } else {
                    downloadprogessPB.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.GONE);
                    messageTV.setVisibility(View.GONE);
                    downloadIV.setVisibility(View.VISIBLE);
                }
            } else {
                downloadprogessPB.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
                messageTV.setVisibility(View.GONE);
                downloadIV.setVisibility(View.VISIBLE);
            }
        }


        private void startDownload(String fileUrl, VideoSolutionData data) {
            offlineData offline = getOfflineDataIds(data.getId(), Const.VIDEOS, activity, data.getId());

            if (offline != null && offline.getRequestInfo() == null)
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

            if (offline != null && offline.getRequestInfo() != null) {
                //when video is downloading
                if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {

                    Toast.makeText(activity, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();


                }//when video is paused
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();

                    eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, Const.VIDEOS);

                }//when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, Const.VIDEOS);

                } else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                    messageTV.setText(R.string.downloaded_offline);
                    downloadprogessPB.setVisibility(View.GONE);
                    downloadIV.setVisibility(View.VISIBLE);
                    downloadIV.setImageResource(R.mipmap.eye_on);
                    deleteIV.setVisibility(View.VISIBLE);

                    //   encryptDownloadedFile(fileMetaData);

                    singleViewCLick(data, offline);
                }
            }//for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                if (Helper.isConnected(activity)) {
                    eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getId(), fileUrl,
                            Helper.getFileName(fileUrl, data.getTitle(), Const.VIDEOS), Const.VIDEOS, data.getId(), downloadid -> {

                                messageTV.setVisibility(View.VISIBLE);
                                downloadprogessPB.setVisibility(View.VISIBLE);
                                downloadIV.setVisibility(View.GONE);
                                deleteIV.setVisibility(View.VISIBLE);
                                messageTV.setText(R.string.download_queued);

                                if (downloadid == Constants.MIGRATED_DOWNLOAD_ID) {
                                    downloadIV.setVisibility(View.VISIBLE);
                                    deleteIV.setVisibility(View.VISIBLE);
                                    downloadIV.setImageResource(R.mipmap.eye_on);
                                    messageTV.setVisibility(View.VISIBLE);
                                    messageTV.setText(R.string.downloaded_offline);

                                    if (downloadprogessPB.getVisibility() != View.GONE)
                                        downloadprogessPB.setVisibility(View.GONE);
                                } else if (downloadid != 0) {
                                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, Const.VIDEOS);
                                } else {
                                    messageTV.setVisibility(View.INVISIBLE);
                                    downloadprogessPB.setVisibility(View.GONE);
                                    deleteIV.setVisibility(View.GONE);
                                    downloadIV.setVisibility(View.VISIBLE);
                                    downloadIV.setImageResource(R.mipmap.download_download);
                                }
                            });
                } else
                    Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            } else if (offline != null && offline.getRequestInfo() == null) {
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
                if (offline.getRequestInfo() == null) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                } else {
                    downloadIV.performClick();
                }
            }
        }

        @Override
        public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {
            downloadIV.setVisibility(View.VISIBLE);
            deleteIV.setVisibility(View.VISIBLE);
            downloadprogessPB.setVisibility(View.GONE);
            downloadIV.setImageResource(R.mipmap.eye_on);
            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.downloaded_offline);
            eMedicozDownloadManager.
                    addOfflineDataIds(data.getId(), data.getVideo_url(), activity,
                            false, true, Const.VIDEOS, requestInfo.getId(), data.getId());
        }

        @Override
        public void updateProgressUI(Integer value, int status, long id) {
            messageTV.setVisibility(View.VISIBLE);
            downloadprogessPB.setVisibility(View.VISIBLE);
            if (status == Fetch.STATUS_QUEUED) {

                downloadIV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.VISIBLE);
                messageTV.setText(R.string.download_queued);

            } else if (status == Fetch.STATUS_REMOVED) {

                downloadprogessPB.setProgress(0);
                downloadprogessPB.setVisibility(View.GONE);

                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.download_new_course);

                deleteIV.setVisibility(View.GONE);
                messageTV.setVisibility(View.GONE);

            } else if (status == Fetch.STATUS_ERROR) {

                downloadIV.setImageResource(R.mipmap.download_reload);
                downloadIV.setVisibility(View.VISIBLE);

                deleteIV.setVisibility(View.VISIBLE);
                messageTV.setText(R.string.error_in_downloading);

            } else if (status == Fetch.STATUS_DOWNLOADING) {

                downloadIV.setVisibility(View.GONE);

                deleteIV.setVisibility(View.VISIBLE);

                if (value > 0) {
                    messageTV.setText(String.format("Downloading...%d%%", value));
                } else
                    messageTV.setText(R.string.download_pending);

            }

            downloadprogessPB.setProgress(value);
        }

        @Override
        public void onStartEncoding() {

        }

        @Override
        public void onEncodingFinished() {

        }

        public void getdownloadcancelDialog(final VideoSolutionData fileMeta, final Activity ctx, final String title, final String message) {
            View v = Helper.newCustomDialog(ctx, true, title, message);

            Button btnCancel, btnSubmit;

            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(ctx.getString(R.string.no));
            btnSubmit.setText(ctx.getString(R.string.yes));

            btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

            btnSubmit.setOnClickListener((View view) -> {
                Helper.dismissDialog();
                eMedicozDownloadManager.removeOfflineData(fileMeta.getId(), Const.VIDEOS,
                        ctx, fileMeta.getId());

                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.download_new_course);

                downloadprogessPB.setVisibility(View.GONE);
                downloadprogessPB.setProgress(0);

                messageTV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
            });
        }


        public void singleViewCLick(VideoSolutionData fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(fileMetaData.getId(),
                        Const.VIDEOS, activity, fileMetaData.getId());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

                ((VideoSolution) activity).rootView.setVisibility(View.VISIBLE);
                ((VideoSolution) activity).bitrate = false;
                ((VideoSolution) activity).initializePlayer(activity.getFilesDir() + "/" + offlineData.getLink());
            } else
                playOnlineVideo(data);

        }

        public void setData(final VideoSolutionData videoSolutionData, int position) {
            this.data = videoSolutionData;
            checkData(videoSolutionData, position);

            solutionRL.setOnClickListener(v -> videoSolutionPlayIV.performClick());
            downloadIV.setOnClickListener((View view) -> startDownload(videoSolutionData.getVideo_url(), videoSolutionData));

            deleteIV.setOnClickListener((View view) -> getdownloadcancelDialog(videoSolutionData, activity, "Delete Download", "Do you really want to delete this download."));
        }
    }
}
