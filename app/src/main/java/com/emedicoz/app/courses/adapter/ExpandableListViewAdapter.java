package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.LiveCourseActivity;
import com.emedicoz.app.modelo.liveclass.LiveClassList;
import com.emedicoz.app.modelo.liveclass.LiveClassVideoList;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse;
import com.emedicoz.app.modelo.liveclass.courses.LiveVideoResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter implements eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass {

    TextView examPrepTitleTV, tv_liveon;
    ImageView emojiSmileIV, expandItemArrow, downloadedIV;
    ImageView videoIcon, locIV, deleteIV, liveIV, downloadIV;
    TextView videoTitle, tv_liveon_date, downloadTimeSlot, messageTV;
    View lineView;
    LinearLayout videoPlayLayout;
    DescriptionResponse descriptionResponse;
    List<LiveClassList> list;
    LiveVideoResponse liveVideoResponse;
    private Activity activity;
    LinearLayout ll_download_view;
    ProgressBar downloadProgressPB;
    long downloadId;
    private eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass savedOfflineListener;
    private LiveClassVideoList data;

    // child data in format of header title, child title;


    public ExpandableListViewAdapter(Activity activity, List<LiveClassList> list, DescriptionResponse descriptionResponse, LiveVideoResponse liveVideoResponse) {
        this.activity = activity;
        this.list = list;
        this.descriptionResponse = descriptionResponse;
        this.liveVideoResponse = liveVideoResponse;
    }

    @Override
    public LiveClassVideoList getChild(int groupPosition, int childPosition) {

        return this.list.get(groupPosition).getVedioList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        LayoutInflater layoutInflater = (LayoutInflater) this.activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.expand_list_video_layout, null);

        videoIcon = convertView.findViewById(R.id.videoIcon);
        videoTitle = convertView.findViewById(R.id.videoTitle);
        tv_liveon = convertView.findViewById(R.id.tv_liveon);
        tv_liveon_date = convertView.findViewById(R.id.tv_liveon_date);
        downloadIV = convertView.findViewById(R.id.downloadIV);
        locIV = convertView.findViewById(R.id.locIV);
        deleteIV = convertView.findViewById(R.id.deleteIV);
        downloadTimeSlot = convertView.findViewById(R.id.downloadTimeSlot);
        videoPlayLayout = convertView.findViewById(R.id.videoPlayLayout);
        liveIV = convertView.findViewById(R.id.liveIV);
        downloadProgressPB = convertView.findViewById(R.id.downloadProgessBar);
        messageTV = convertView.findViewById(R.id.messageTV);
        ll_download_view = convertView.findViewById(R.id.ll_download_view);
        downloadedIV = convertView.findViewById(R.id.downloadedIV);

        convertView.findViewById(R.id.downloadProgessBar).setScaleY(1.5f);
        savedOfflineListener = this;

        LiveClassVideoList childItem = getChild(groupPosition, childPosition);

        setData(convertView, childItem);

        return convertView;

    }


    public void setData(View convertView, LiveClassVideoList liveClassVideoList) {
        ((TextView) convertView.findViewById(R.id.videoTitle)).setText(liveClassVideoList.getVideoTitle());

        if (!liveClassVideoList.getLiveOn().equals("0") && !liveClassVideoList.getIsLive().equals("1")) {
            convertView.findViewById(R.id.tv_liveon).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.tv_liveon_date).setVisibility(View.VISIBLE);

        } else if (liveClassVideoList.getVideoType().equals("0") && liveClassVideoList.getIsVod().equals("1")) {
            convertView.findViewById(R.id.tv_liveon).setVisibility(View.GONE);
            convertView.findViewById(R.id.tv_liveon_date).setVisibility(View.VISIBLE);
        }

        if (liveClassVideoList.getIsLive() != null /*&& !liveClassVideoList.getIsLive().equals("1")*/
                && !liveClassVideoList.getLiveOn().equals("")) {
            convertView.findViewById(R.id.tv_liveon).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.tv_liveon_date)).setText(
                    Helper.getFormattedDateTime(Long.parseLong(liveClassVideoList.getLiveOn()) * 1000));
        } else
            convertView.findViewById(R.id.tv_liveon).setVisibility(View.GONE);

        if (SharedPreference.getInstance().getBoolean(Const.SINGLE_STUDY)) {
            if (liveClassVideoList.getIsVod().equals("1")) {
                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                checkData(convertView, liveClassVideoList);
/*
            } else if (liveClassVideoList.getIsVod().equals("0") && liveClassVideoList.getFileUrl().contains(".m3u8")) {
                ((ImageView) convertView.findViewById(R.id.downloadIV)).setVisibility(View.VISIBLE);
                checkData(convertView, liveClassVideoList);*/
            } else {

                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);

            }

            convertView.findViewById(R.id.locIV).setVisibility(View.GONE);

        } else {
            if (/*Constants.IS_PURCHASED.equals("1") ||*/ descriptionResponse.getData().getIsPurchased().equals("0")) {
                if (descriptionResponse.getData().getBasic().getMrp().equals("0")) {
                    setViewOfVideoItem(convertView, liveClassVideoList);

                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (descriptionResponse.getData().getBasic().getForDams().equals("0")) {
                            setViewOfVideoItem(convertView, liveClassVideoList);
                        } else {
                            convertView.findViewById(R.id.locIV).setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                        }
                    } else {
                        if (descriptionResponse.getData().getBasic().getNonDams().equals("0")) {
                            setViewOfVideoItem(convertView, liveClassVideoList);
                        } else {
                            convertView.findViewById(R.id.locIV).setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                        }
                    }
                }

            } else {
                if (liveClassVideoList.getVideoType().equals("0") && liveClassVideoList.getIsVod().equals("1")) {
//                                llDownload.setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                } else {
//                                llDownload.setVisibility(View.GONE);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                }
                convertView.findViewById(R.id.locIV).setVisibility(View.GONE);


            }
        }
        String correctDateFormat = "";
        if (!GenericUtils.isEmpty(liveClassVideoList.getLiveOn())) {
            Date d = new Date(Long.parseLong(GenericUtils.getParsableString(liveClassVideoList.getLiveOn())) * 1000);
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String dateString = f.format(d);
            Date date = null;
            try {
                date = f.parse(f.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String[] amPM = dateString.split("\\s+");
            String[] fullDate = String.valueOf(date).split("\\s+");
            correctDateFormat = GenericUtils.getFormattedDate(fullDate, amPM);
        }


        Glide.with(activity).asGif().load(R.drawable.live_gif).into((ImageView) convertView.findViewById(R.id.liveIV));
        if (liveClassVideoList.getIsLive() != null && liveClassVideoList.getIsLive().equals("1")) {
            convertView.findViewById(R.id.liveIV).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
        } else
            convertView.findViewById(R.id.liveIV).setVisibility(View.GONE);

        bindControls(descriptionResponse, convertView, liveClassVideoList, correctDateFormat);

    }

    private void setViewOfVideoItem(View convertView, LiveClassVideoList liveClassVideoList) {
        checkData(convertView, liveClassVideoList);

        if (liveClassVideoList.getLiveUrl().equalsIgnoreCase("") && liveClassVideoList.getIsLive().equalsIgnoreCase("0")) {
            convertView.findViewById(R.id.locIV).setVisibility(View.GONE);
            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
        } else {
            if (liveClassVideoList.getFileUrl().contains(".mp4")) {
                convertView.findViewById(R.id.locIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.locIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
            }
        }
    }

    private void bindControls(DescriptionResponse descriptionResponse, View convertView, LiveClassVideoList liveClassVideoList, String correctDateFormat) {
        videoPlayLayout.setOnClickListener((View view) -> {
            if (descriptionResponse.getData().getIsPurchased().equals("1")) {
                goToLiveCourseActivity(liveClassVideoList, correctDateFormat);
            } else {
                if (descriptionResponse.getData().getBasic().getMrp().equals("0"))
                    goToLiveCourseActivity(liveClassVideoList, correctDateFormat);
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (descriptionResponse.getData().getBasic().getForDams().equals("0"))
                            goToLiveCourseActivity(liveClassVideoList, correctDateFormat);
                        else
                            Toast.makeText(activity, R.string.buy_to_watch, Toast.LENGTH_SHORT).show();
                    } else {
                        if (descriptionResponse.getData().getBasic().getNonDams().equals("0"))
                            goToLiveCourseActivity(liveClassVideoList, correctDateFormat);
                        else
                            Toast.makeText(activity, R.string.buy_to_watch, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        downloadIV.setOnClickListener((View view) -> {
            if (descriptionResponse.getData().getIsPurchased().equals("1"))
                startDownload(liveClassVideoList.getFileUrl(), convertView, liveClassVideoList);
            else {
                if (descriptionResponse.getData().getBasic().getMrp().equals("0"))
                    startDownload(liveClassVideoList.getFileUrl(), convertView, liveClassVideoList);
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (descriptionResponse.getData().getBasic().getForDams().equals("0"))
                            startDownload(liveClassVideoList.getFileUrl(), convertView, liveClassVideoList);
                        else
                            Toast.makeText(activity, R.string.buy_to_download_video, Toast.LENGTH_SHORT).show();
                    } else {
                        if (descriptionResponse.getData().getBasic().getNonDams().equals("0"))
                            startDownload(liveClassVideoList.getFileUrl(), convertView, liveClassVideoList);
                        else
                            Toast.makeText(activity, R.string.buy_to_download_video, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        deleteIV.setOnClickListener((View view) -> getDownloadCancelDialog(convertView, liveClassVideoList, activity, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD));

    }

    private void goToLiveCourseActivity(LiveClassVideoList liveClassVideoList, String correctDateFormat) {

        offlineData offline = getOfflineDataIds(liveClassVideoList.getId(), Const.VIDEOS, activity, liveClassVideoList.getId());
        if (offline == null) {
            if (Helper.isConnected(activity)) {
                if (liveClassVideoList.getIsVod().equals("0")) {
                    if (!GenericUtils.isEmpty(liveClassVideoList.getLiveOn()) && (Long.parseLong(liveClassVideoList.getLiveOn()) * 1000)
                            <= System.currentTimeMillis()) {
                        if (liveClassVideoList.getIsLive().equalsIgnoreCase("1")) {
                            Intent intent = new Intent(activity, LiveCourseActivity.class);
                            intent.putExtra(Const.VIDEO_LINK, liveClassVideoList.getLiveUrl());
                            intent.putExtra(Const.VIDEO, liveClassVideoList);
                            activity.startActivity(intent);
                        } else {
                            if (GenericUtils.isEmpty(liveClassVideoList.getLiveUrl()))
                                Helper.GoToVideoActivity(activity, liveClassVideoList.getFileUrl(), Const.VIDEO_STREAM, liveClassVideoList.getId(), Const.COURSE_VIDEO_TYPE);
                            else
                                Helper.GoToVideoActivity(activity, liveClassVideoList.getLiveUrl(), Const.VIDEO_STREAM, liveClassVideoList.getId(), Const.COURSE_VIDEO_TYPE);
                        }
                    } else {
                        Toast.makeText(activity, "This video will be live on: " + correctDateFormat, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (GenericUtils.isEmpty(liveClassVideoList.getLiveUrl()))
                        Helper.GoToVideoActivity(activity, liveClassVideoList.getFileUrl(), Const.VIDEO_STREAM, liveClassVideoList.getId(), Const.COURSE_VIDEO_TYPE);
                    else
                        Helper.GoToVideoActivity(activity, liveClassVideoList.getLiveUrl(), Const.VIDEO_STREAM, liveClassVideoList.getId(), Const.COURSE_VIDEO_TYPE);
                }
            } else
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
        } else
            singleViewCLick(liveClassVideoList, offline);

    }

    public void getDownloadCancelDialog(View convertView, LiveClassVideoList liveClassVideoList, Activity activity, final String title, final String message) {
        View v = Helper.newCustomDialog(activity, true, title, message);

        Button btnCancel, btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(activity.getString(R.string.no));
        btnSubmit.setText(activity.getString(R.string.yes));

        btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

        btnSubmit.setOnClickListener((View view) -> {
            Helper.dismissDialog();
            eMedicozDownloadManager.removeOfflineData(liveClassVideoList.getId(), Const.VIDEOS,
                    activity, liveClassVideoList.getId());

            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_new_course);

            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
            ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(0);

            convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
        });
    }


    public void singleViewCLick(LiveClassVideoList liveClassVideoList, com.emedicoz.app.Model.offlineData offlineData) {
        if (offlineData == null) {
            offlineData = getOfflineDataIds(liveClassVideoList.getId(),
                    Const.VIDEOS, activity, liveClassVideoList.getId());
        }

        if (offlineData != null && offlineData.getRequestInfo() == null) {
            offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
        }
        if (offlineData != null && offlineData.getRequestInfo() != null &&
                offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlineData.getLink() != null) {
            if ((activity.getFilesDir() + "/" + offlineData.getLink()).contains(".mp4")) {
                Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, liveClassVideoList.getId(), Const.COURSE_VIDEO_TYPE);
            } else {
                Helper.DecryptAndGoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, liveClassVideoList.getId(), Const.COURSE_VIDEO_TYPE);
            }
        }
    }

    private void checkData(View convertView, LiveClassVideoList liveClassVideoList) {
        com.emedicoz.app.Model.offlineData offlineData = getOfflineDataIds(liveClassVideoList.getId(),
                Const.VIDEOS, activity, liveClassVideoList.getId());

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

                //0. downloading in queue
                if ((offlineData.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);
                    downloadId = offlineData.getDownloadid();
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offlineData.getDownloadid(), savedOfflineListener, Const.VIDEOS);
                }
                //1. downloading in progress
                else if ((offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                        && offlineData.getRequestInfo().getProgress() < 100) {

                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                    ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlineData.getRequestInfo().getProgress());
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);
                    downloadId = offlineData.getDownloadid();
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


    private void startDownload(String fileUrl, View convertView, LiveClassVideoList liveClassVideoList) {
        com.emedicoz.app.Model.offlineData offline = getOfflineDataIds(liveClassVideoList.getId(), Const.VIDEOS, activity, liveClassVideoList.getId());

        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        if (offline != null && offline.getRequestInfo() != null) {
            //when video is downloading
            if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {

                Toast.makeText(activity, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();


            }//when video is paused
            else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

                convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pending);
                downloadId = offline.getDownloadid();

                eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.getDownloadid(), savedOfflineListener, Const.VIDEOS);

            }//when some error occurred
            else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pending);
                downloadId = offline.getDownloadid();
                eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.getDownloadid(), savedOfflineListener, Const.VIDEOS);

            } else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);

                singleViewCLick(liveClassVideoList, offline);
            }
        }//for new download
        else if (offline == null || offline.getRequestInfo() == null) {
            if (Helper.isConnected(activity)) {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, liveClassVideoList.getId(), fileUrl,
                        Helper.getFileName(fileUrl, liveClassVideoList.getVideoTitle(), Const.VIDEOS), Const.VIDEOS,
                        liveClassVideoList.getId(), downloadid -> {

                            convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                            convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                            ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);


                            if (downloadid == Constants.MIGRATED_DOWNLOAD_ID) {
                                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                                convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                                ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);

                                if (convertView.findViewById(R.id.downloadProgessBar).getVisibility() != View.GONE)
                                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                            } else if (downloadid != 0) {
                                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, downloadid, savedOfflineListener, Const.VIDEOS);
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

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.list.get(groupPosition).getVedioList().size();
    }

    @Override
    public LiveClassList getGroup(int groupPosition) {
        return this.list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.exam_prep_header_row_item, null);
        }

        emojiSmileIV = convertView.findViewById(R.id.emojiSmileCIV);
        examPrepTitleTV = convertView.findViewById(R.id.examPrepTitleTV);
        expandItemArrow = convertView.findViewById(R.id.expandItemArrow);
        lineView = convertView.findViewById(R.id.lineView);
        examPrepTitleTV.setText(getGroup(groupPosition).getTitle());
        setColorOfSmiely(groupPosition);
        if (!GenericUtils.isEmpty(getGroup(groupPosition).getImageIcon()))
            Glide.with(activity).load(getGroup(groupPosition).getImageIcon()).into(emojiSmileIV);

        if (isExpanded) {
            expandItemArrow.setRotation(180);
            lineView.setVisibility(View.GONE);
        } else {
            expandItemArrow.setRotation(0);
            lineView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void setColorOfSmiely(int groupPosition) {

        switch (groupPosition % 5) {
            case 0:
                emojiSmileIV.setColorFilter(ContextCompat.getColor(activity, R.color.smily_1));
                break;
            case 1:
                emojiSmileIV.setColorFilter(ContextCompat.getColor(activity, R.color.smily_2));
                break;
            case 2:
                emojiSmileIV.setColorFilter(ContextCompat.getColor(activity, R.color.smily_3));
                break;
            case 3:
                emojiSmileIV.setColorFilter(ContextCompat.getColor(activity, R.color.smily_4));
                break;
            default:
                emojiSmileIV.setColorFilter(ContextCompat.getColor(activity, R.color.smily_5));
                break;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
                addOfflineDataIds(data.getId(), data.getFileUrl(), activity,
                        false, true, Const.VIDEOS, requestInfo.getId(), data.getId());
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
}
