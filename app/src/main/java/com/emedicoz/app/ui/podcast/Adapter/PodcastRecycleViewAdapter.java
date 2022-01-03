package com.emedicoz.app.ui.podcast.Adapter;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.PodcastNewKotlin;
import com.emedicoz.app.R;
import com.emedicoz.app.podcast.Podcast;
import com.emedicoz.app.podcastnew.activity.PodcastByAuthor;
import com.emedicoz.app.podcastnew.activity.PodcastMainActivity;
import com.emedicoz.app.podcastnew.callback.OnPodcastBookmarkClick;
import com.emedicoz.app.ui.podcast.StoryName;
import com.emedicoz.app.ui.podcast.ViewHolder.PodcastRecycleViewHolder;
import com.emedicoz.app.ui.views.RoundedImageView;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shri ram
 */
public class PodcastRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass {


    // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_HEADER = 0;

    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    private String name;
    private int profile;
    private String email;

    // private List<StoryName> list = new ArrayList<>();
    private List<Podcast> list = new ArrayList<>();

    private Activity mContext;
    private eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass savedOfflineListener;
    private Podcast podcast;
    OnPodcastBookmarkClick onPodcastBookmarkClick;

    // titles, icons, name, email, profile pic are passed from the main activity
    public PodcastRecycleViewAdapter(Activity mContext, List<Podcast> list, OnPodcastBookmarkClick onPodcastBookmarkClick) {
        this.list = list;
        this.mContext = mContext;
        this.onPodcastBookmarkClick = onPodcastBookmarkClick;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = null;
        RecyclerView.ViewHolder rcv = null;

        switch (viewType) {
            case 0:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcastrow, null);
                rcv = new PodcastRecycleViewHolder(mContext, layoutView);
                break;
        }

        return rcv;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 0:
                PodcastRecycleViewHolder rowViewHolder = (PodcastRecycleViewHolder) holder;
                savedOfflineListener = this;
                configure_header(rowViewHolder, position);
                break;

        }
    }


    private void configure_header(PodcastRecycleViewHolder rowViewHolder, int position) {

        rowViewHolder.episodetitle.setText(list.get(position).getTitle());
        rowViewHolder.publisher.setText(list.get(position).getCreatedBy());
        if (list.get(position).getIs_bookmarked() != null && list.get(position).getIs_bookmarked().equals("0")) {
            rowViewHolder.imgBookmark.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_bookmark_border));
        } else {
            rowViewHolder.imgBookmark.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_bookmark_white));
        }
        if (!GenericUtils.isEmpty(list.get(position).getThumbnail_image()))
            Glide.with(mContext).load(list.get(position).getThumbnail_image()).into(rowViewHolder.profile_pic);
        if (!GenericUtils.isEmpty(list.get(position).getDuration())) {
            rowViewHolder.podcast_size.setText(Helper.formatSeconds(Integer.parseInt(list.get(position).getDuration()))+" hours");
        }else {
            rowViewHolder.podcast_size.setText("00:00:00 hours");
        }
        rowViewHolder.imgDownload.setOnClickListener(v -> startDownload(list.get(position).getUrl(), rowViewHolder.mainRL, list.get(position)));
        rowViewHolder.imgDelete.setOnClickListener(v -> getDownloadCancelDialog(rowViewHolder.mainRL, list.get(position), mContext, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD));
        rowViewHolder.imgBookmark.setOnClickListener(v -> onPodcastBookmarkClick.onPodcastBookmarkClick(list.get(position), position, rowViewHolder));
        checkData(rowViewHolder.mainRL, list.get(position));
        rowViewHolder.profile_pic.setOnClickListener(v -> singleViewCLick(list.get(position), null, rowViewHolder.mainRL));
        rowViewHolder.podcastTitleLL.setOnClickListener(v -> {
            rowViewHolder.profile_pic.performClick();
        });
    }


    private void load_profile_bitmap(RoundedImageView imageView2, String url) {


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    // With the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        int p = 1;
        if (position == 0) {
            p = 0;
        } else {
            p = 1;
        }

        p = 0;
        return p;
    }

    private void startDownload(String fileUrl, View convertView, Podcast podcast1) {
        podcast = podcast1;
        com.emedicoz.app.Model.offlineData offline = getOfflineDataIds(podcast1.getId(), Const.PODCAST, mContext, podcast1.getId());

        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        if (offline != null && offline.getRequestInfo() != null) {
            //when file is downloading
            if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                Toast.makeText(mContext, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();

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
            if (Helper.isConnected(mContext)) {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(mContext, podcast1.getId(), fileUrl,
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
                                ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.file_download_black_24_dp);
                            }
                        });
            } else
                Toast.makeText(mContext, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
        } else if (offline != null && offline.getRequestInfo() == null) {
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
            if (offline.getRequestInfo() == null) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                convertView.findViewById(R.id.downloadIV).performClick();
            }
        }
    }

    public void singleViewCLick(Podcast podcast, offlineData offlineData, View convertView) {
        if (offlineData == null) {
            offlineData = getOfflineDataIds(podcast.getId(), Const.PODCAST, mContext, podcast.getId());
        }

        if (offlineData != null && offlineData.getRequestInfo() == null) {
            offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
        }
        if (offlineData != null && offlineData.getRequestInfo() != null &&
                offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlineData.getLink() != null) {

            podcast.setDownloadedUrl(offlineData.getRequestInfo().getFilePath());
        }
        Log.e("singleViewCLick: ", mContext.toString());
        if (mContext instanceof PodcastMainActivity){
            Fragment fragment = ((PodcastMainActivity) mContext).getCurrentFragment();
            if (fragment instanceof PodcastNewKotlin) {
                ((PodcastNewKotlin) fragment).loadPlayerWithURL(podcast);
            }
        }else if (mContext instanceof PodcastByAuthor){
            ((PodcastByAuthor) mContext).loadPlayerWithURL(podcast);
        }
//        if (mContext instanceof HomeActivity) {
//            Fragment fragment = ((HomeActivity) mContext).getCurrentFragment();
//            if (fragment instanceof PodcastNewKotlin) {
//                ((PodcastNewKotlin) fragment).loadPlayerWithURL(podcast);
//            }
//        } else if (mContext instanceof PodcastByAuthor)
//            ((PodcastByAuthor) mContext).loadPlayerWithURL(podcast);
    }

    private void checkData(View convertView, Podcast podcast) {
        com.emedicoz.app.Model.offlineData offlineData = getOfflineDataIds(podcast.getId(), Const.PODCAST, mContext, podcast.getId());
        ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.file_download_black_24_dp);

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
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.file_download_black_24_dp);

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
                addOfflineDataIds(podcast.getId(), podcast.getUrl(), mContext,
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
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.file_download_black_24_dp);
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
}
