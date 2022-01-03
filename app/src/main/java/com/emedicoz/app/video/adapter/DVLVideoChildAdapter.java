package com.emedicoz.app.video.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.dvl.DVLVideo;
import com.emedicoz.app.modelo.dvl.DVLVideoData;
import com.emedicoz.app.modelo.dvl.DvlData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.activity.DVLVideosActivity;
import com.emedicoz.app.video.fragment.DVLFragment;
import com.google.gson.JsonObject;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.AES.generateVector;
import static com.emedicoz.app.utilso.AES.generatekey;
import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class DVLVideoChildAdapter extends RecyclerView.Adapter<DVLVideoChildAdapter.DVLVideoChildViewHolder> {

    List<DVLVideo> videoArrayList;
    Activity activity;
    DvlData dvlData;
    int rowIndex = -1;
    DVLVideoData videoData;
    String type;

    public DVLVideoChildAdapter(List<DVLVideo> videoArrayList, Activity activity, DvlData dvlData, DVLVideoData videoData) {
        this.videoArrayList = videoArrayList;
        this.activity = activity;
        this.dvlData = dvlData;
        this.videoData = videoData;
    }

    @NonNull
    @Override
    public DVLVideoChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_premium_videos, viewGroup, false);
        return new DVLVideoChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DVLVideoChildViewHolder holder, int i) {

        holder.setView(videoArrayList.get(i), i);
        if (rowIndex == i) {
            holder.parentCardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.blue));
            holder.videoDurationTV.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.title.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.itemTypeImageIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.play));
        } else {
            holder.parentCardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            holder.videoDurationTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
            holder.title.setTextColor(ContextCompat.getColor(activity, R.color.black));
            holder.itemTypeImageIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.video_new_course));

        }
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class DVLVideoChildViewHolder extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {
        eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
        long downloadId;
        LinearLayout parentLL;
        TextView title;
        TextView messageTV;
        TextView videoDurationTV;
        ImageView downloadIV;
        ImageView deleteIV;
        ImageView itemTypeImageIV;
        ProgressBar downloadprogessPB;
        DVLVideo data;
        String url720 = "";
        String url480 = "";
        String url360 = "";
        String url240 = "";
        CardView parentCardView;

        public DVLVideoChildViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLL = itemView.findViewById(R.id.parentLL);
            parentCardView = itemView.findViewById(R.id.parentCardView);
            title = itemView.findViewById(R.id.fileTypeTV);
            downloadprogessPB = itemView.findViewById(R.id.downloadProgessBar);
            messageTV = itemView.findViewById(R.id.messageTV);
            videoDurationTV = itemView.findViewById(R.id.videoDurationTV);
            itemTypeImageIV = itemView.findViewById(R.id.itemTypeimageIV);
            downloadprogessPB.setScaleY(1.5f);
            savedOfflineListener = this;
            downloadIV = itemView.findViewById(R.id.downloadIV);
            deleteIV = itemView.findViewById(R.id.deleteIV);
            downloadIV.setVisibility(View.GONE);
        }

        public void setView(final DVLVideo video, final int position) {
            this.data = video;

            if (video.getType().equalsIgnoreCase(Const.VIDEO))
                type = Const.VIDEOS;
            else
                type = video.getType();

            if (type.equalsIgnoreCase(Const.VIDEOS))
                itemTypeImageIV.setImageResource(R.mipmap.video_new_course);
            else
                itemTypeImageIV.setImageResource(R.mipmap.pdf);

            checkData(video);
            parentLL.setOnClickListener(v -> itemTypeImageIV.performClick());
            downloadIV.setOnClickListener((View v) -> {
                if (videoData.getIsPurchased().equals("1")) {
                    videoDownload(video);
                } else {
                    if (dvlData.getMrp().equals("0"))
                        videoDownload(video);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (dvlData.getForDams().equals("0"))
                                videoDownload(video);
                            else
                                showBuyPopup();
                        } else {
                            if (dvlData.getNonDams().equals("0"))
                                videoDownload(video);
                            else
                                showBuyPopup();
                        }
                    }
                }
            });

            deleteIV.setOnClickListener((View v) -> showPopupMenu(v, video));

            itemTypeImageIV.setOnClickListener((View view) -> {
                rowIndex = position;

                if (videoData.getIsPurchased().equals("1")) {
                    playVideo(video);

                } else {
                    if (dvlData.getMrp().equals("0"))
                        playVideo(video);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (dvlData.getForDams().equals("0"))
                                playVideo(video);
                            else
                                showBuyPopup();
                        } else {
                            if (dvlData.getNonDams().equals("0"))
                                playVideo(video);
                            else
                                showBuyPopup();
                        }
                    }
                }
            });
            title.setText(video.getTitle());
            if (GenericUtils.isEmpty(video.getDuration()))
                videoDurationTV.setVisibility(View.GONE);
            else {
                videoDurationTV.setVisibility(View.VISIBLE);
                videoDurationTV.setText(String.format("Duration: %s mins", video.getDuration()));
            }
        }

        private void playVideo(DVLVideo video) {
            offlineData offline = getOfflineDataIds(data.getElementFk(), type, activity, data.getElementFk());
            if (offline == null)
                playVideoOnline(data);
            else
                singleViewCLick(data, offline);
        }

        private void playVideoOnline(DVLVideo video) {
            if (Helper.isConnected(activity)) {
                ((DVLVideosActivity) activity).imgDVL.setVisibility(View.GONE);
                ((DVLVideosActivity) activity).rootView.setVisibility(View.VISIBLE);

                if (!video.getNew_link().equals("")) {
                    ((DVLVideosActivity) activity).bitrate = true;
                    networkCallForVideoDuration(video, video.getNew_link());
                } else {
                    ((DVLVideosActivity) activity).bitrate = false;
                    networkCallForVideoDuration(video, video.getFileUrl());
                }
            } else
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
        }

        private void playVideoOffline(offlineData video) {
            ((DVLVideosActivity) activity).imgDVL.setVisibility(View.GONE);
            ((DVLVideosActivity) activity).rootView.setVisibility(View.VISIBLE);
            ((DVLVideosActivity) activity).initializePlayer(activity.getFilesDir() + "/" + video.getLink());
        }

        private void videoDownload(DVLVideo video) {
            if (video.getEnc_url() != null && video.getEnc_url().getFiles() != null) {
                offlineData offlineData = getOfflineDataIds(video.getElementFk(),
                        type, activity, video.getElementFk());

                if (offlineData == null) {
                    View sheetView = Helper.openBottomSheetDialog(activity);
                    ArrayList<String> decryptedUrl = new ArrayList<>();
                    for (int i = 0; i < video.getEnc_url().getFiles().size(); i++) {
                        decryptedUrl.add(AES.decrypt(video.getEnc_url().getFiles().get(i).getUrl(), generatekey(video.getEnc_url().getToken()), generateVector(video.getEnc_url().getToken())));
                    }

                    Log.e("TAG", "onClick: " + decryptedUrl.get(0));

                    TextView size720;
                    TextView size480;
                    TextView size360;
                    TextView size240;
                    RadioButton radio1;
                    RadioButton radio2;
                    RadioButton radio3;
                    RadioButton radio4;
                    RelativeLayout relativeLayout1;
                    RelativeLayout relativeLayout2;
                    RelativeLayout relativeLayout3;
                    RelativeLayout relativeLayout4;
                    Button downloadVideoBtn;
                    size720 = sheetView.findViewById(R.id.size720);
                    size480 = sheetView.findViewById(R.id.size480);
                    size360 = sheetView.findViewById(R.id.size360);
                    size240 = sheetView.findViewById(R.id.size240);
                    radio1 = sheetView.findViewById(R.id.radio1);
                    radio2 = sheetView.findViewById(R.id.radio2);
                    radio3 = sheetView.findViewById(R.id.radio3);
                    radio4 = sheetView.findViewById(R.id.radio4);
                    relativeLayout1 = sheetView.findViewById(R.id.relativeLayout1);
                    relativeLayout2 = sheetView.findViewById(R.id.relativeLayout2);
                    relativeLayout3 = sheetView.findViewById(R.id.relativeLayout3);
                    relativeLayout4 = sheetView.findViewById(R.id.relativeLayout4);
                    downloadVideoBtn = sheetView.findViewById(R.id.downloadVideoBtn);

                    for (int i = 0; i < decryptedUrl.size(); i++) {
                        if (decryptedUrl.get(i).contains("720out_put")) {
                            size720.setText(video.getEnc_url().getFiles().get(i).getSize() + " MB");
                            url720 = video.getEnc_url().getFiles().get(i).getUrl();
                        } else if (decryptedUrl.get(i).contains("480out_put")) {
                            size480.setText(video.getEnc_url().getFiles().get(i).getSize() + " MB");
                            url480 = video.getEnc_url().getFiles().get(i).getUrl();
                        } else if (decryptedUrl.get(i).contains("360out_put")) {
                            size360.setText(video.getEnc_url().getFiles().get(i).getSize() + " MB");
                            url360 = video.getEnc_url().getFiles().get(i).getUrl();
                        } else if (decryptedUrl.get(i).contains("240out_put")) {
                            size240.setText(video.getEnc_url().getFiles().get(i).getSize() + " MB");
                            url240 = video.getEnc_url().getFiles().get(i).getUrl();
                        }
                    }

                    radio3.setChecked(true);

                    relativeLayout1.setOnClickListener(view1 -> {
                        radio1.setChecked(true);
                        radio2.setChecked(false);
                        radio3.setChecked(false);
                        radio4.setChecked(false);
                    });

                    relativeLayout2.setOnClickListener(view1 -> {
                        radio2.setChecked(true);
                        radio1.setChecked(false);
                        radio3.setChecked(false);
                        radio4.setChecked(false);

                    });

                    relativeLayout3.setOnClickListener(view1 -> {
                        radio3.setChecked(true);
                        radio2.setChecked(false);
                        radio1.setChecked(false);
                        radio4.setChecked(false);

                    });

                    relativeLayout4.setOnClickListener(view1 -> {
                        radio4.setChecked(true);
                        radio2.setChecked(false);
                        radio3.setChecked(false);
                        radio1.setChecked(false);
                    });

                    downloadVideoBtn.setOnClickListener(view1 -> {
                        if (radio1.isChecked()) {
                            Helper.dismissBottonSheetDialog();
                            startDownloadEncrypted(AES.decrypt(url720, generatekey(video.getEnc_url().getToken()), generateVector(video.getEnc_url().getToken())), video);
                        } else if (radio2.isChecked()) {
                            Helper.dismissBottonSheetDialog();
                            startDownloadEncrypted(AES.decrypt(url480, generatekey(video.getEnc_url().getToken()), generateVector(video.getEnc_url().getToken())), video);
                        } else if (radio3.isChecked()) {
                            Helper.dismissBottonSheetDialog();
                            startDownloadEncrypted(AES.decrypt(url360, generatekey(video.getEnc_url().getToken()), generateVector(video.getEnc_url().getToken())), video);
                        } else if (radio4.isChecked()) {
                            Helper.dismissBottonSheetDialog();
                            startDownloadEncrypted(AES.decrypt(url240, generatekey(video.getEnc_url().getToken()), generateVector(video.getEnc_url().getToken())), video);
                        }
                    });
                } else {
                    singleViewCLick(video, offlineData);
                }
            } else {

                startDownload(video.getFileUrl(), video);
            }
        }

        private void startDownloadEncrypted(String fileUrl, DVLVideo fileMetaData) {

            offlineData offline = getOfflineDataIds(fileMetaData.getElementFk(),
                    type, activity, fileMetaData.getElementFk());
            if (offline != null && offline.getRequestInfo() == null)
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

            downloadprogessPB.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
            messageTV.setVisibility(View.GONE);
            downloadIV.setVisibility(View.GONE);

            if (offline != null && offline.getRequestInfo() != null) {

                //when video is downloading
                if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                    Toast.makeText(activity, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();
                }
                //when video is paused
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                            savedOfflineListener, type);
                }

                //when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                            savedOfflineListener, type);
                }
                // when download is completed.
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                    messageTV.setText(R.string.downloaded_offline);
                    downloadprogessPB.setVisibility(View.GONE);
                    downloadIV.setVisibility(View.VISIBLE);
                    downloadIV.setImageResource(R.mipmap.eye_on);
                    deleteIV.setVisibility(View.VISIBLE);

                }
            }
            //for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, fileMetaData.getElementFk(),
                        fileUrl, Helper.getFileName(fileUrl, fileMetaData.getTitle(), type), type, fileMetaData.getElementFk(),
                        downloadid -> {

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
            } else {
                offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
                if (offline.getRequestInfo() == null) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                } else {
                    downloadIV.performClick();
                }
            }
        }

        private void showBuyPopup() {
            View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.you_have_to_buy_DVL_subscription_to_view_this_content));

            Space space;
            Button btnCancel;
            Button btnSubmit;

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

        private void checkData(DVLVideo epubData) {
            offlineData offlinedata = getOfflineDataIds(epubData.getElementFk(),
                    type, activity, epubData.getElementFk());

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
                        messageTV.setText(R.string.download_queued);
                        downloadprogessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                        downloadprogessPB.setVisibility(View.VISIBLE);
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

        private void startDownload(String fileUrl, DVLVideo data) {
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

                    singleViewCLick(data, offline);
                }
            }//for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getElementFk(), fileUrl,
                        Helper.getFileName(fileUrl, data.getTitle(), type), type, data.getElementFk(), downloadid -> {

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

            } else {
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
                            false, true, type, requestInfo.getId(), data.getElementFk());
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

        public void getdownloadcancelDialog(final DVLVideo fileMeta, final Activity ctx, final String title, final String message) {
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
                eMedicozDownloadManager.removeOfflineData(fileMeta.getElementFk(), type,
                        ctx, fileMeta.getElementFk());

                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.download_new_course);

                downloadprogessPB.setVisibility(View.GONE);
                downloadprogessPB.setProgress(0);

                messageTV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
            });
        }


        public void singleViewCLick(DVLVideo fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(data.getElementFk(),
                        type, activity, data.getElementFk());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo() != null &&
                    offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlineData.getLink() != null) {

                if (type.equalsIgnoreCase(Const.VIDEOS)) {
                    /*if (fileMetaData.getEnc_url() != null && fileMetaData.getEnc_url().getToken() != null) {
                        if ((activity.getFilesDir() + "/" + offlineData.getLink()).contains(".mp4")) {
                            Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, data.getElementFk(), Const.COURSE_VIDEO_TYPE);
                        } else {
                            Helper.DecryptAndGoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, data.getElementFk(), Const.COURSE_VIDEO_TYPE);
                        }
                    } else {
                        Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, data.getElementFk(), Const.COURSE_VIDEO_TYPE);
                    }*/
                    playVideoOffline(offlineData);
                } else if (type.equalsIgnoreCase(Const.PDF)) {
                    Helper.GoToWebViewActivity(activity, fileMetaData.getFileUrl());
                }
            } else
                playVideoOnline(fileMetaData);
        }

        public void networkCallForVideoDuration(final DVLVideo data, final String url) {
//            ((DVLVideosActivity) activity).initializePlayer(url);
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            final Progress mprogress = new Progress(activity);
            mprogress.setCancelable(false);
            mprogress.show();
            final SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
            Call<JsonObject> response = apiInterface.getVideoDuration(SharedPreference.getInstance().getLoggedInUser().getId()
                    , data.getElementFk(), "1");
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mprogress.dismiss();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                JSONObject object = GenericUtils.getJsonObject(jsonResponse);
                                String durationLimit = object.optString("duration_limit");
                                String watched = object.optString("watched");
                                if (durationLimit.equals("")) {
                                    durationLimit = "0";
                                    ((DVLVideosActivity) activity).isLimited = false;
                                    ((DVLVideosActivity) activity).durationTV.setVisibility(View.GONE);
                                } else {
                                    ((DVLVideosActivity) activity).isLimited = true;
                                    ((DVLVideosActivity) activity).durationTV.setVisibility(View.GONE);
                                }
                                long remainingTime = (Integer.parseInt(durationLimit) - Integer.parseInt(watched)) * 60 * 1000;
                                ((DVLVideosActivity) activity).totalRemainingTime = 0;
                                ((DVLVideosActivity) activity).videoDuration = String.valueOf(remainingTime);
                                ((DVLVideosActivity) activity).watched = watched;
                                ((DVLVideosActivity) activity).fileMeta = data;
                                ((DVLVideosActivity) activity).imgDVL.setVisibility(View.GONE);
                                ((DVLVideosActivity) activity).rootView.setVisibility(View.VISIBLE);


                                ((DVLVideosActivity) activity).initializePlayer(url);
                                SharedPreference.getInstance().putString("LINK", url);
                                notifyDataSetChanged();

                                Log.e("TAG", "Duration_Limit " + object.optString("duration_limit"));
                            } else {
                                RetrofitResponse.getApiData(activity, API.API_GET_FILE_LIST_CURRICULUM);
                                mprogress.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mprogress.dismiss();
                    Helper.showExceptionMsg(activity, t);
                }
            });
        }


        private void showPopupMenu(View v, final DVLVideo video) {

            PopupMenu popup = new PopupMenu(activity, v);
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.deleteMenu) {
                    getdownloadcancelDialog(video, activity, video.getTitle(), activity.getResources().getString(R.string.do_you_really_want_to_delete_video));
                    return true;
                }
                return false;
            });

            popup.inflate(R.menu.delete_menu);
            popup.show();
        }

    }
}
