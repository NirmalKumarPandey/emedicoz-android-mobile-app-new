package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.epubear.ePubActivity;
import com.emedicoz.app.modelo.courses.Crs.EBookData;
import com.emedicoz.app.modelo.courses.Crs.EBookFile;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.dvl.DVLVideo;
import com.emedicoz.app.modelo.dvl.DvlData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.fragment.DVLFragment;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import java.util.ArrayList;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class EBookAdapter extends RecyclerView.Adapter<EBookAdapter.EBookViewHolder> {

    Activity activity;
    ArrayList<EBookFile> files;
    EBookData crsFileData;
    int row_index = -1;
    DvlData dvlData;

    public EBookAdapter(Activity activity, ArrayList<EBookFile> files, EBookData crsFileData, DvlData dvlData) {
        this.activity = activity;
        this.files = files;
        this.crsFileData = crsFileData;
        this.dvlData = dvlData;
    }

    @NonNull
    @Override
    public EBookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_premium_videos, viewGroup, false);
        return new EBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EBookViewHolder holder, int i) {
        holder.setView(files.get(i), i);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class EBookViewHolder extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {
        eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
        long downloadId;
        LinearLayout parentLL;
        TextView title, messageTV, videoDurationTV;
        ImageView downloadIV, deleteIV, itemTypeimageIV;
        ProgressBar downloadprogessPB;
        DVLVideo data;
        String url720 = "", url480 = "", url360 = "", url240 = "";
        CardView parentCardView;

        public EBookViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLL = itemView.findViewById(R.id.parentLL);
            parentCardView = itemView.findViewById(R.id.parentCardView);
            title = itemView.findViewById(R.id.fileTypeTV);
            downloadprogessPB = itemView.findViewById(R.id.downloadProgessBar);
            messageTV = itemView.findViewById(R.id.messageTV);
            videoDurationTV = itemView.findViewById(R.id.videoDurationTV);
            itemTypeimageIV = itemView.findViewById(R.id.itemTypeimageIV);
            downloadprogessPB.setScaleY(1.5f);
            savedOfflineListener = this;
            downloadIV = itemView.findViewById(R.id.downloadIV);
            deleteIV = itemView.findViewById(R.id.deleteIV);
            downloadIV.setVisibility(View.GONE);
        }

        public void setView(final EBookFile eBookFile, int position) {

            if (eBookFile.getType().equalsIgnoreCase(Const.EPUB))
                itemTypeimageIV.setImageResource(R.mipmap.epub_new_course);
            else if (eBookFile.getType().equalsIgnoreCase(Const.PDF))
                itemTypeimageIV.setImageResource(R.mipmap.pdf);

            title.setText(eBookFile.getTitle());
            checkData(eBookFile, position);
            downloadIV.setOnClickListener(v -> {
                if (crsFileData.getIsPurchased().equals("1")) {
                    startDownload(eBookFile.getFileUrl(), eBookFile);
                } else {
                    if (dvlData.getMrp().equals("0"))
                        startDownload(eBookFile.getFileUrl(), eBookFile);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (dvlData.getForDams().equals("0"))
                                startDownload(eBookFile.getFileUrl(), eBookFile);
                            else
                                showBuyPopup();
                        } else {
                            if (dvlData.getNonDams().equals("0"))
                                startDownload(eBookFile.getFileUrl(), eBookFile);
                            else
                                showBuyPopup();
                        }
                    }
                }
            });

            deleteIV.setOnClickListener(v -> showPopupMenu(v, position, eBookFile));
        }

        private void checkData(EBookFile file, int position) {
            String type = file.getType();
            offlineData offlinedata = getOfflineDataIds(file.getElementFk(),
                    type, activity, file.getElementFk());

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
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, type);
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
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, type);
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

        private void startDownload(String file_url, EBookFile data) {
            String type = data.getType();
            offlineData offline = getOfflineDataIds(data.getElementFk(), type, activity, data.getElementFk());

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
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, type);

                }//when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, type);

                } else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                    messageTV.setText(R.string.downloaded_offline);
                    downloadprogessPB.setVisibility(View.GONE);
                    downloadIV.setVisibility(View.VISIBLE);
                    downloadIV.setImageResource(R.mipmap.eye_on);
                    deleteIV.setVisibility(View.VISIBLE);

                    //   encryptDownloadedFile(data);

                    singleViewCLick(data, offline);
                }
            }//for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                try {
                    eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getElementFk(), file_url,
                            Helper.getFileName(file_url, data.getTitle(), type), type, data.getElementFk(), downloadid -> {

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
                                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, type);
                                } else {
                                    messageTV.setVisibility(View.INVISIBLE);
                                    downloadprogessPB.setVisibility(View.GONE);
                                    deleteIV.setVisibility(View.GONE);
                                    downloadIV.setVisibility(View.VISIBLE);
                                    downloadIV.setImageResource(R.mipmap.download_download);
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    addOfflineDataIds(data.getElementFk(), data.getFileUrl(), activity,
                            false, true, data.getType(), requestInfo.getId(), data.getElementFk());
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

        public void getdownloadcancelDialog(final EBookFile fileMeta, final Activity ctx, final String title, final String message) {
            android.app.AlertDialog.Builder alertBuild = new android.app.AlertDialog.Builder(ctx);
            alertBuild
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, whichButton) -> {
                        dialog.dismiss();
                        eMedicozDownloadManager.removeOfflineData(fileMeta.getElementFk(), fileMeta.getType(),
                                ctx, fileMeta.getElementFk());

                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_new_course);

                        downloadprogessPB.setVisibility(View.GONE);
                        downloadprogessPB.setProgress(0);

                        messageTV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.GONE);
                    })
                    .setNegativeButton("No", (dialog, whichButton) -> dialog.dismiss());
            android.app.AlertDialog dialog = alertBuild.create();
            dialog.show();
            int alertTitle = ctx.getResources().getIdentifier("alertTitle", Constants.Extras.ID, "android");
            ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
            ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
        }


        public void singleViewCLick(EBookFile fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(fileMetaData.getElementFk(),
                        fileMetaData.getType(), activity, fileMetaData.getElementFk());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                // Helper.GoToVideoActivity(activity, data.getLink(), Const.VIDEO_STREAM);
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

                if (fileMetaData.getType().equalsIgnoreCase(Const.EPUB)) {
                    final Intent intent = new Intent(activity, ePubActivity.class);
                    intent.putExtra("filePath", activity.getFilesDir() + "/" + offlineData.getLink());
                    activity.startActivity(intent);
                } else {
                    Helper.GoToWebViewActivity(activity, fileMetaData.getFileUrl());
                }
            }

        }

        private void showBuyPopup() {
            View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.you_have_to_buy_DVL_subscription_to_view_this_content));

            Space space;
            Button btnCancel, btnSubmit;

            space = v.findViewById(R.id.space);
            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
            btnSubmit.setBackgroundResource(R.drawable.bg_round_corner_fill_red);
            btnSubmit.setText(activity.getResources().getString(R.string.enroll));

            btnSubmit.setOnClickListener((View view) -> {
                Helper.dismissDialog();
                DVLFragment.IS_BUY_CLICKED = true;
                Helper.goToCourseInvoiceScreen(activity, getData(dvlData));
            });
        }


        public SingleCourseData getData(DvlData basic) {
            SingleCourseData singleCourseData = new SingleCourseData();
            singleCourseData.setCourse_type(basic.getCourse_type());
            singleCourseData.setFor_dams(basic.getForDams());
            singleCourseData.setNon_dams(basic.getNonDams());
            singleCourseData.setMrp(basic.getMrp());
            singleCourseData.setId(basic.getId());
            singleCourseData.setCover_image(basic.getCover_image());
            singleCourseData.setTitle(basic.getTitle());
            singleCourseData.setLearner(basic.getLearner());
            singleCourseData.setRating(basic.getRating());
            singleCourseData.setGst_include(basic.getGstInclude());
            if (!GenericUtils.isEmpty(basic.getGst()))
                singleCourseData.setGst(basic.getGst());
            else
                singleCourseData.setGst("18");
            if (!GenericUtils.isEmpty(basic.getPointsConversionRate()))
                singleCourseData.setPoints_conversion_rate(basic.getPointsConversionRate());
            else
                singleCourseData.setPoints_conversion_rate("100");
            return singleCourseData;
        }

        private void showPopupMenu(View v, final int position, final EBookFile file) {

            PopupMenu popup = new PopupMenu(activity, v);
            popup.setOnMenuItemClickListener((MenuItem item) -> {
                switch (item.getItemId()) {
                    case R.id.deleteMenu:
                        getdownloadcancelDialog(file, activity, file.getTitle(), "Do you really want to delete the video?");
                        return true;
                    default:
                        return false;
                }
            });

            popup.inflate(R.menu.delete_menu);
            Menu menu = popup.getMenu();
            popup.show();
        }
    }
}


