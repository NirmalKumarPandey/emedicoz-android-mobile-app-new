package com.emedicoz.app.podcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.Constants;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
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


@SuppressLint("SetTextI18n")
public class PodcastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass {

    private List<Podcast> podcastList;
    private Activity activity;
    private eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass savedOfflineListener;
    private Podcast podcast;
    private boolean bookmarkedListScreen;
    private MediaPrepared mediaPrepared;
    private PodcastDeleteInterface podcastDeleteInterface;

    public PodcastAdapter(List<Podcast> podcastList, boolean screen, Activity activity, MediaPrepared mediaPrepared, PodcastDeleteInterface podcastDeleteInterface) {
        this.podcastList = podcastList;
        this.activity = activity;
        bookmarkedListScreen = screen;
        this.mediaPrepared = mediaPrepared;
        this.podcastDeleteInterface = podcastDeleteInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_podcast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder vh = (ViewHolder) holder;
        savedOfflineListener = this;
        vh.setView(podcastList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return podcastList.size();
    }

    private void networkCallToRemoveBookmark(String podcastId, int position) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.managePodcastBookmark(SharedPreference.getInstance().getLoggedInUser().getId(),
                podcastId, "1");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        Constants.UPDATE_LIST = "true";
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Toast.makeText(activity, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        if (bookmarkedListScreen) {
                            podcastList.remove(position);
                            notifyDataSetChanged();
                            podcastDeleteInterface.podcastDeleteCallback(podcastList.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });

    }

    public void getDownloadCancelDialog(View convertView, Podcast podcast, Activity activity, final String title, final String message) {
        View v = Helper.newCustomDialog(activity, true, title, message);

        Button btnCancel, btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(activity.getString(R.string.no));
        btnSubmit.setText(activity.getString(R.string.yes));

        btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

        btnSubmit.setOnClickListener((View view) -> {
            Helper.dismissDialog();
            eMedicozDownloadManager.removeOfflineData(podcast.getId(), Const.PODCAST, activity, podcast.getId());

            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_new_course);

            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
            ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(0);

            convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
        });
    }

    @Override
    public void updateUIForDownloadedVideo(View convertView, RequestInfo requestInfo, long id) {
        convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
        convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
        convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
        ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
        convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
        ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);
        eMedicozDownloadManager.
                addOfflineDataIds(podcast.getId(), podcast.getUrl(), activity,
                        false, true, Const.PODCAST, requestInfo.getId(), podcast.getId());
    }

    @Override
    public void updateProgressUI(View convertView, Integer value, int status, long id) {
        convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
        convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);

        if (status == Fetch.STATUS_QUEUED) {

            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);

        } else if (status == Fetch.STATUS_REMOVED) {

            ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(0);
            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_new_course);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
            convertView.findViewById(R.id.messageTV).setVisibility(View.INVISIBLE);

        } else if (status == Fetch.STATUS_ERROR) {

            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_reload);
            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.error_in_downloading);

        } else if (status == Fetch.STATUS_DOWNLOADING) {

            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);

            if (value > 0) {
                ((TextView) convertView.findViewById(R.id.messageTV)).setText("Downloading..." + value + "%");
            } else
                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pending);

        }

        ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(value);
    }

    @Override
    public void onStartEncoding() {

    }

    @Override
    public void onEncodingFinished() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView des;
        TextView views;
        TextView date;
        ImageView btnPlayPodcast;
        ImageView imgBookmark;
        ImageView imgDownload;
        ImageView imgDelete;
        RelativeLayout layoutRoot;
        ProgressBar loader;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            des = itemView.findViewById(R.id.description);
            views = itemView.findViewById(R.id.views);
            date = itemView.findViewById(R.id.date);

            btnPlayPodcast = itemView.findViewById(R.id.btn_play_podcast);
            imgBookmark = itemView.findViewById(R.id.imgbookmark);
            imgDownload = itemView.findViewById(R.id.downloadIV);
            imgDelete = itemView.findViewById(R.id.deleteIV);
            layoutRoot = itemView.findViewById(R.id.layout_podcast_root);
            loader = itemView.findViewById(R.id.loader);
        }

        //setting up data to each single view
        public void setView(final Podcast podcast, int position) {
            if (GenericUtils.isEmpty(podcast.getUrl()))
                podcast.setUrl("https://file-examples-com.github.io/uploads/2017/11/file_example_WAV_1MG.wav");
            if (!GenericUtils.isEmpty(podcast.getThumbnail_image()))
                Glide.with(activity).load(podcast.getThumbnail_image()).into(btnPlayPodcast);
//            if (podcast.getIsPrepared() && eMedicozApp.getInstance().getPodcastPlayer().isPlaying())
//                btnPlayPodcast.setImageResource(R.mipmap.pause);
//            else {
//                btnPlayPodcast.setImageResource(R.mipmap.play_test);
//            }

            if (podcast.getIs_bookmarked() != null && podcast.getIs_bookmarked().equalsIgnoreCase("0")) {
                imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_bookmark_24));
            } else {
                imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_bookmark_blue_24));
            }

            imgBookmark.setTag(podcast);
            imgBookmark.setOnClickListener(v -> {
                Podcast pod = (Podcast) v.getTag();
                if (pod.getIs_bookmarked() == null)
                    pod.setIs_bookmarked(bookmarkedListScreen ? "1" : "0");
                if (pod.getIs_bookmarked().equalsIgnoreCase("0")) {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_bookmark_blue_24));
                    pod.setIs_bookmarked("1");
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_bookmark_24));
                    pod.setIs_bookmarked("0");
                }
                networkCallToRemoveBookmark(pod.getId(), position);
            });

            layoutRoot.setTag(podcast);
            layoutRoot.setOnClickListener(v -> {
                Podcast pod = (Podcast) v.getTag();

                singleViewCLick(pod, null, layoutRoot);
            });

            imgDownload.setOnClickListener(v -> {
                startDownload(podcast.getUrl(), layoutRoot, podcast);
            });

            imgDelete.setOnClickListener((View view) -> getDownloadCancelDialog(layoutRoot, podcast, activity, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD));

            title.setText(podcast.getTitle());
            des.setText(podcast.getDescription());
            date.setText(podcast.getCreatedAt());

          /*  if (!TextUtils.isEmpty(podcast.getCreatedAt())) {
                date.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(podcast.getCreatedAt())).equals(activity.getString(R.string.string_minutes_ago)) ?
                        Const.JUST_NOW :
                        DateUtils.getRelativeTimeSpanString(Long.parseLong(podcast.getCreatedAt())));
            }*/
            if (!TextUtils.isEmpty(podcast.getViews())) {
                if (podcast.getViews().equals("0") || podcast.getViews().equals("1")) {
                    views.setText(podcast.getViews() + Const.VIEW);
                } else {
                    views.setText(podcast.getViews() + " Views");
                }
            }

            checkData(layoutRoot.getRootView(), podcast);
        }

        private void checkData(View convertView, Podcast podcast) {
            com.emedicoz.app.Model.offlineData offlineData = getOfflineDataIds(podcast.getId(), Const.PODCAST, activity, podcast.getId());
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_new_course);

            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
            convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
            if (offlineData != null) {
                if (offlineData.getRequestInfo() == null)
                    offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));

                if (offlineData.getRequestInfo() != null) {
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(offlineData.getRequestInfo().getProgress() < 100 ? View.VISIBLE : View.GONE);

                    //4 conditions to check at the time of intialising the video

                    //1. downloading in progress
                    if ((offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                            && offlineData.getRequestInfo().getProgress() < 100) {

                        convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                        convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                        ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlineData.getRequestInfo().getProgress());
                        ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);

                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offlineData.getDownloadid(), savedOfflineListener, Const.VIDEOS);
                    }
                    //2. download complete
                    else if (offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlineData.getRequestInfo().getProgress() == 100) {
                        ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);
                        convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                        convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                        ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                        convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    }
                    //3. downloading paused
                    else if (offlineData.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED && offlineData.getRequestInfo().getProgress() < 100) {
                        ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlineData.getRequestInfo().getProgress());
                        convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                        ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_pause);
                        ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pasued);
                    }
                    //4. error intrrupted
                    else if (offlineData.getRequestInfo().getStatus() == Fetch.STATUS_ERROR && offlineData.getRequestInfo().getProgress() < 100) {
                        ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.error_in_downloading);
                        convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                        ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlineData.getRequestInfo().getProgress());
                        convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                        ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_reload);
                    }
                    convertView.findViewById(R.id.messageTV).setVisibility(offlineData.getRequestInfo() != null ? View.VISIBLE : View.GONE);
                } else {
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                }
            } else {
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            }
        }

        public void singleViewCLick(Podcast podcast, offlineData offlineData, View convertView) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(podcast.getId(), Const.PODCAST, activity, podcast.getId());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo() != null &&
                    offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlineData.getLink() != null) {

                podcast.setDownloadedUrl(offlineData.getRequestInfo().getFilePath());
            }

            loadPlayerWithURL(podcast);
        }

        private void loadPlayerWithURL(Podcast pod) {
            for (int i = 0; i < podcastList.size(); i++)
                podcastList.get(i).setIsPrepared(false);
            notifyDataSetChanged();

//            loader.setVisibility(View.VISIBLE);
            if (eMedicozApp.getInstance().getPodcastPlayer() == null) {
                eMedicozApp.getInstance().setPodcastPlayer(new MediaPlayer());
            }

            mediaPrepared.onMediaPrepared(pod);
        }

        private void startDownload(String fileUrl, View convertView, Podcast podcast1) {
            podcast = podcast1;
            com.emedicoz.app.Model.offlineData offline = getOfflineDataIds(podcast1.getId(), Const.PODCAST, activity, podcast1.getId());

            if (offline != null && offline.getRequestInfo() == null)
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

            if (offline != null && offline.getRequestInfo() != null) {
                //when file is downloading
                if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                    Toast.makeText(activity, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();

                }//when file downloading is paused
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

                    convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pending);

                    eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.getDownloadid(), savedOfflineListener, Const.VIDEOS);

                }//when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pending);

                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.getDownloadid(), savedOfflineListener, Const.VIDEOS);

                } else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                    ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);

                    singleViewCLick(podcast1, offline, convertView);
                }
            }//for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                if (Helper.isConnected(activity)) {
                    eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, podcast1.getId(), fileUrl,
                            Helper.getFileName(podcast1.getTitle()) + ".mp3", Const.PODCAST, podcast1.getId(), downloadid -> {

                                convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                                convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);


                                if (downloadid == com.emedicoz.app.utilso.Constants.MIGRATED_DOWNLOAD_ID) {
                                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                                    convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                                    ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);

                                    if (convertView.findViewById(R.id.downloadProgessBar).getVisibility() != View.GONE)
                                        convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                                } else if (downloadid != 0) {
                                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, downloadid, savedOfflineListener, Const.PODCAST);
                                } else {
                                    convertView.findViewById(R.id.messageTV).setVisibility(View.INVISIBLE);
                                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                                    convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                                    ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_download);
                                }
                            });
                } else
                    Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            } else if (offline != null && offline.getRequestInfo() == null) {
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
                if (offline.getRequestInfo() == null) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                } else {
                    convertView.findViewById(R.id.downloadIV).performClick();
                }
            }
        }
    }
}
