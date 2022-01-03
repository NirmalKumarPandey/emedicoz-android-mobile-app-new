package com.emedicoz.app.pastpaperexplanation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.epubear.ePubActivity;
import com.emedicoz.app.pastpaperexplanation.model.PPECategoryChildData;
import com.emedicoz.app.pastpaperexplanation.model.PPEData;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class ExamChildAdapter extends RecyclerView.Adapter<ExamChildAdapter.MyViewHolderChild> {

    Context context;
    List<PPECategoryChildData> ppeCategoryChildArrayList;
    PPEData ppeData;

    public ExamChildAdapter(Context context, List<PPECategoryChildData> ppeCategoryChildArrayList, PPEData ppeData) {
        this.context = context;
        this.ppeCategoryChildArrayList = ppeCategoryChildArrayList;
        this.ppeData = ppeData;
    }

    @NonNull
    @Override
    public MyViewHolderChild onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_exam_child_adapter, parent, false);
        return new MyViewHolderChild(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderChild holder, int position) {
        PPECategoryChildData ppeCategoryChildData = ppeCategoryChildArrayList.get(position);
        holder.setData(ppeCategoryChildData);
        if (ppeCategoryChildData.getTestSeriesName() != null)
            holder.tvTitle.setText(ppeCategoryChildData.getTestSeriesName());

/*        if (!TextUtils.isEmpty(ppeCategoryChildData.getQuestionCount())) {
            holder.tvQuestionNumber.setVisibility(View.VISIBLE);
            holder.tvQuestionNumber.setText("Question: " + ppeCategoryChildData.getQuestionCount());
        } else {
            holder.tvQuestionNumber.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        return ppeCategoryChildArrayList.size();
    }

    class MyViewHolderChild extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {

        eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
        long downloadId;
        TextView tvTitle;
        TextView messageTV;
        TextView startTestTV;
        ImageView downloadIV;
        ImageView deleteIV;
        ProgressBar downloadProgressBar;
        TextView tvQuestionNumber;
        ImageView itemTypeIV;
        PPECategoryChildData data;

        public MyViewHolderChild(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            messageTV = itemView.findViewById(R.id.messageTV);
            downloadIV = itemView.findViewById(R.id.downloadIV);
            deleteIV = itemView.findViewById(R.id.deleteIV);
            startTestTV = itemView.findViewById(R.id.startTestTV);
            itemTypeIV = itemView.findViewById(R.id.itemTypeimageIV);
            downloadProgressBar = itemView.findViewById(R.id.downloadProgessBar);
            downloadProgressBar.setScaleY(1.5f);
            savedOfflineListener = this;
            downloadIV.setVisibility(View.GONE);
        }

        private void checkData(PPECategoryChildData ppeCategoryChildData) {
            offlineData offlinedata = getOfflineDataIds(ppeCategoryChildData.getId(),
                    ppeCategoryChildData.getFileType(), (Activity) context, ppeCategoryChildData.getId());

            downloadIV.setImageResource(R.mipmap.download_new_course);

            downloadProgressBar.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
            messageTV.setVisibility(View.GONE);
            downloadIV.setVisibility(View.GONE);
            if (offlinedata != null) {
                if (offlinedata.getRequestInfo() == null)
                    offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

                if (offlinedata.getRequestInfo() != null) {
                    downloadProgressBar.setVisibility(offlinedata.getRequestInfo().getProgress() < 100 ? View.VISIBLE : View.GONE);

                    //4 conditions to check at the time of intialising the video
                    //0. downloading in queue
                    if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        messageTV.setText(R.string.download_queued);
                        downloadId = offlinedata.getDownloadid();
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, ppeCategoryChildData.getFileType());
                    }
                    //1. downloading in progress
                    else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                            && offlinedata.getRequestInfo().getProgress() < 100) {

                        downloadIV.setVisibility(View.GONE);
                        downloadProgressBar.setProgress(offlinedata.getRequestInfo().getProgress());
                        downloadProgressBar.setVisibility(View.VISIBLE);
                        deleteIV.setVisibility(View.VISIBLE);
                        messageTV.setText(R.string.download_queued);
                        downloadId = offlinedata.getDownloadid();
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, ppeCategoryChildData.getFileType());
                    }
                    //2. download complete
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlinedata.getRequestInfo().getProgress() == 100) {
                        messageTV.setText(R.string.downloaded_offline);
                        downloadProgressBar.setVisibility(View.GONE);
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.eye_on);
                        deleteIV.setVisibility(View.VISIBLE);

                    }
                    //3. downloading paused
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED && offlinedata.getRequestInfo().getProgress() < 100) {
                        downloadProgressBar.setProgress(offlinedata.getRequestInfo().getProgress());
                        deleteIV.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_pause);
                        messageTV.setText(R.string.download_pasued);
                    }
                    //4. error intrrupted
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_ERROR && offlinedata.getRequestInfo().getProgress() < 100) {
                        messageTV.setText(R.string.error_in_downloading);
                        deleteIV.setVisibility(View.VISIBLE);
                        downloadProgressBar.setProgress(offlinedata.getRequestInfo().getProgress());
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_reload);
                    }
                    messageTV.setVisibility(offlinedata.getRequestInfo() != null ? View.VISIBLE : View.GONE);
                } else {
                    downloadProgressBar.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.GONE);
                    messageTV.setVisibility(View.GONE);
                    downloadIV.setVisibility(View.VISIBLE);
                }
            } else {
                downloadProgressBar.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
                messageTV.setVisibility(View.GONE);
                downloadIV.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {
            downloadIV.setVisibility(View.VISIBLE);
            deleteIV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.GONE);
            downloadIV.setImageResource(R.mipmap.eye_on);
            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.downloaded_offline);
            eMedicozDownloadManager.
                    addOfflineDataIds(data.getId(), data.getFileUrl(), (Activity) context,
                            false, true, data.getFileType(), requestInfo.getId(), data.getId());
        }

        @Override
        public void updateProgressUI(Integer value, int status, long id) {
            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            if (status == Fetch.STATUS_QUEUED) {

                downloadIV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.VISIBLE);
                messageTV.setText(R.string.download_queued);

            } else if (status == Fetch.STATUS_REMOVED) {

                downloadProgressBar.setProgress(0);
                downloadProgressBar.setVisibility(View.GONE);

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
                    messageTV.setText(R.string.download_queued);

            }

            downloadProgressBar.setProgress(value);
        }

        @Override
        public void onStartEncoding() {

        }

        @Override
        public void onEncodingFinished() {

        }

        private void startDownload(String fileUrl, PPECategoryChildData data) {
            offlineData offline = getOfflineDataIds(data.getId(), data.getFileType(), (Activity) context, data.getId());

            if (offline != null && offline.getRequestInfo() == null)
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

            if (offline != null && offline.getRequestInfo() != null) {
                //when video is downloading
                if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {

                    Toast.makeText(context, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();


                }//when video is paused
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadProgressBar.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();

                    eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, data.getFileType());

                }//when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadProgressBar.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, data.getFileType());

                } else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                    messageTV.setText(R.string.downloaded_offline);
                    downloadProgressBar.setVisibility(View.GONE);
                    downloadIV.setVisibility(View.VISIBLE);
                    downloadIV.setImageResource(R.mipmap.eye_on);
                    deleteIV.setVisibility(View.VISIBLE);

                    singleViewCLick(data, offline);
                }
            }//for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                try {
                    eMedicozDownloadManager.getInstance().showQualitySelectionPopup((Activity) context, data.getId(), fileUrl,
                            URLDecoder.decode(AES.decrypt(fileUrl), "UTF-8").split("/")[AES.decrypt(fileUrl).split("/").length - 1],
                            data.getFileType(), data.getId(), downloadid -> {

                                messageTV.setVisibility(View.VISIBLE);
                                downloadProgressBar.setVisibility(View.VISIBLE);
                                downloadIV.setVisibility(View.GONE);
                                deleteIV.setVisibility(View.VISIBLE);
                                messageTV.setText(R.string.download_queued);

                                if (downloadid == Constants.MIGRATED_DOWNLOAD_ID) {
                                    downloadIV.setVisibility(View.VISIBLE);
                                    deleteIV.setVisibility(View.VISIBLE);
                                    downloadIV.setImageResource(R.mipmap.eye_on);
                                    messageTV.setVisibility(View.VISIBLE);
                                    messageTV.setText(R.string.downloaded_offline);

                                    if (downloadProgressBar.getVisibility() != View.GONE)
                                        downloadProgressBar.setVisibility(View.GONE);
                                } else if (downloadid != 0) {
                                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, data.getFileType());
                                } else {
                                    messageTV.setVisibility(View.INVISIBLE);
                                    downloadProgressBar.setVisibility(View.GONE);
                                    deleteIV.setVisibility(View.GONE);
                                    downloadIV.setVisibility(View.VISIBLE);
                                    downloadIV.setImageResource(R.mipmap.download_download);
                                }
                            });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
                if (offline.getRequestInfo() == null) {
                    Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                } else {
                    downloadIV.performClick();
                }
            }
        }

        private void showPopupMenu(View v, final PPECategoryChildData ppeCategoryChildData) {

            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.deleteMenu) {
                    getDownloadCancelDialog(ppeCategoryChildData, (Activity) context, ppeCategoryChildData.getTestSeriesName(), context.getResources().getString(R.string.do_you_really_want_to_delete_video));
                    return true;
                }
                return false;
            });

            popup.inflate(R.menu.delete_menu);
            popup.show();
        }

        public void getDownloadCancelDialog(final PPECategoryChildData fileMeta, final Activity ctx, final String title, final String message) {
            View v = Helper.newCustomDialog(ctx, true, title, message);

            Button btnCancel;
            Button btnSubmit;

            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(ctx.getString(R.string.no));
            btnSubmit.setText(ctx.getString(R.string.yes));

            btnCancel.setOnClickListener(view1 -> Helper.dismissDialog());

            btnSubmit.setOnClickListener(view1 -> {
                Helper.dismissDialog();
                eMedicozDownloadManager.removeOfflineData(fileMeta.getId(), fileMeta.getFileType(),
                        ctx, fileMeta.getId());

                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.download_new_course);

                downloadProgressBar.setVisibility(View.GONE);
                downloadProgressBar.setProgress(0);

                messageTV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
            });
        }

        public void singleViewCLick(PPECategoryChildData fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(fileMetaData.getId(),
                        fileMetaData.getFileType(), (Activity) context, fileMetaData.getId());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

                if (fileMetaData.getFileType().equalsIgnoreCase(Constants.CourseType.VIDEO)) {
                    Helper.GoToVideoActivity((Activity) context, context.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                } else if (fileMetaData.getFileType().equalsIgnoreCase(Constants.CourseType.PDF)) {
                    Helper.openPdfActivity((Activity) context, offlineData.getLink(),
                            offlineData.getRequestInfo().getFilePath());
                } else if (fileMetaData.getFileType().equalsIgnoreCase(Constants.CourseType.EPUB)) {
                    final Intent intent = new Intent(context, ePubActivity.class);
                    intent.putExtra("filePath", context.getFilesDir() + "/" + offlineData.getLink());
                    context.startActivity(intent);
                }

            }
        }

        public void setData(PPECategoryChildData ppeCategoryChildData) {
            data = ppeCategoryChildData;
            if (!ppeCategoryChildData.getFileType().equalsIgnoreCase(Constants.TestType.TEST)) {
                switch (ppeCategoryChildData.getFileType()) {
                    case Constants.CourseType.PDF:
                        itemTypeIV.setImageResource(R.mipmap.pdf_);
                        break;
                    case Constants.CourseType.VIDEO:
                        itemTypeIV.setImageResource(R.mipmap.video_new_course);
                        break;
                    case Constants.CourseType.EPUB:
                        itemTypeIV.setImageResource(R.mipmap.epub_new_course);
                        break;
                }
                checkData(ppeCategoryChildData);
            } else {
                itemTypeIV.setImageResource(R.mipmap.test_new_course);
                setTestData(ppeCategoryChildData);
            }

            itemTypeIV.setOnClickListener(v1 -> {
                if (ppeCategoryChildData.getFileType().equalsIgnoreCase(Constants.CourseType.VIDEO)) {
                    if (ppeData.getIsPurchased().equals("1")) {
                        playVideo(ppeCategoryChildData);
                    } else if (ppeData.getMrp().equals("0"))
                        playVideo(ppeCategoryChildData);
                    else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (ppeData.getForDams().equals("0"))
                            playVideo(ppeCategoryChildData);
                        else
                            Toast.makeText(context, R.string.buy_to_watch, Toast.LENGTH_SHORT).show();
                    } else {
                        if (ppeData.getNonDams().equals("0"))
                            playVideo(ppeCategoryChildData);
                        else
                            Toast.makeText(context, R.string.buy_to_watch, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            downloadIV.setOnClickListener((View v) -> {
                if (ppeData.getIsPurchased().equals("1")) {
                    startDownload(ppeCategoryChildData.getFileUrl(), ppeCategoryChildData);
                } else if (ppeData.getMrp().equals("0"))
                    startDownload(ppeCategoryChildData.getFileUrl(), ppeCategoryChildData);
                else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (ppeData.getForDams().equals("0"))
                        startDownload(ppeCategoryChildData.getFileUrl(), ppeCategoryChildData);
                    else
                        Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                } else {
                    if (ppeData.getNonDams().equals("0"))
                        startDownload(ppeCategoryChildData.getFileUrl(), ppeCategoryChildData);
                    else
                        Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                }
            });

            deleteIV.setOnClickListener(v -> showPopupMenu(v, ppeCategoryChildData));
        }

        private void playVideo(PPECategoryChildData ppeCategoryChildData) {
            if (!GenericUtils.isEmpty(ppeCategoryChildData.getVodLink())) {
                Helper.GoToVideoActivity((Activity) context, ppeCategoryChildData.getVodLink(), Const.VIDEO_STREAM, ppeCategoryChildData.getId(), Const.COURSE_VIDEO_TYPE);
            } else {
                Helper.GoToVideoActivity((Activity) context, ppeCategoryChildData.getFileUrl(), Const.VIDEO_STREAM, ppeCategoryChildData.getId(), Const.COURSE_VIDEO_TYPE);
            }
        }

        private void setTestData(PPECategoryChildData ppeCategoryChildData) {
            startTestTV.setVisibility(View.VISIBLE);
            if (ppeCategoryChildData.getIsPaused() != null) {
                if (ppeCategoryChildData.getIsPaused().equals("1")) {
                    startTestTV.setText("Resume");
                } else if (ppeCategoryChildData.getIsPaused().equals("0")) {
                    startTestTV.setText("Result");
                } else {
                    startTestTV.setText("Start");
                }
            }

            startTestTV.setOnClickListener(v -> {

                if (ppeData.getIsPurchased().equals("1")) {
                    startTest(ppeCategoryChildData);
                } else if (ppeData.getMrp().equals("0"))
                    startTest(ppeCategoryChildData);
                else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (ppeData.getForDams().equals("0"))
                        startTest(ppeCategoryChildData);
                    else
                        Toast.makeText(context, context.getResources().getString(R.string.please_buy_course_to_start_test), Toast.LENGTH_SHORT).show();
                } else {
                    if (ppeData.getNonDams().equals("0"))
                        startTest(ppeCategoryChildData);
                    else
                        Toast.makeText(context, context.getResources().getString(R.string.please_buy_course_to_start_test), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void startTest(PPECategoryChildData ppeCategoryChildData) {
            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
            if (ppeCategoryChildData.getIsPaused() != null) {
                if (ppeCategoryChildData.getIsPaused().equals("0")) {
                    Intent resultScreen = new Intent(context, QuizActivity.class);
                    resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                    resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.getResultId());
                    context.startActivity(resultScreen);
                } else {
                    Intent intent = new Intent(context, TestBaseActivity.class);
                    intent.putExtra(Const.STATUS, false);
                    intent.putExtra(Const.TEST_SERIES_ID, ppeCategoryChildData.getId());
                    context.startActivity(intent);
                }
            }
        }
    }
}