package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.activity.LiveCourseActivity;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.model.VideoCourse;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.epubear.ePubActivity;
import com.emedicoz.app.modelo.TestSeriesResultData;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.modelo.courses.quiz.Quiz_Basic_Info;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class ExamPrepLayer3Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int lang;  // 0 for Hindi , 1 for English
    Activity activity;
    ArrayList<Quiz_Basic_Info> resultTestSeriesArrayList;
    ArrayList<VideoCourse> videoArrayList;
    Bitmap bitmap;
    private SingleStudyModel singlestudyModel;
    private VideoCourse video;


    public ExamPrepLayer3Adapter(Activity activity, ArrayList<Quiz_Basic_Info> resultTestSeriesArrayList, SingleStudyModel singlestudyModel) {
        this.activity = activity;
        this.resultTestSeriesArrayList = resultTestSeriesArrayList;
        this.singlestudyModel = singlestudyModel;
    }

    public ExamPrepLayer3Adapter(Activity activity, ArrayList<VideoCourse> videoArrayList, String content, SingleStudyModel singlestudyModel) {
        this.activity = activity;
        this.videoArrayList = videoArrayList;
        this.singlestudyModel = singlestudyModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (((CourseActivity) activity).contentType.equals(Const.VIDEO)) {
            view = LayoutInflater.from(activity).inflate(R.layout.single_item_videos_examprep, null);
            return new SubjectVideosHolder(view);
        } else if (((CourseActivity) activity).contentType.equals(Const.EPUB)) {
            view = LayoutInflater.from(activity).inflate(R.layout.live_course_epub, null);
            return new SingleEpubListHolder(view);
        } else if (((CourseActivity) activity).contentType.equals(Const.PDF)) {
            view = LayoutInflater.from(activity).inflate(R.layout.live_course_epub, null);
            return new SinglePdfListHolder(view);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.layerthird_single_item, null);
            return new SingleStudyListHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (((CourseActivity) activity).contentType.equals(Const.VIDEO)) {
            ((SubjectVideosHolder) holder).setData(videoArrayList.get(position), position);
            video = videoArrayList.get(position);
        } else if (((CourseActivity) activity).contentType.equals(Const.EPUB)) {
            ((SingleEpubListHolder) holder).setEpubData(videoArrayList.get(position), position);
        } else if (((CourseActivity) activity).contentType.equals(Const.PDF)) {
            ((SinglePdfListHolder) holder).setPdfData(videoArrayList.get(position), position);
        } else {
            ((SingleStudyListHolder) holder).setData(resultTestSeriesArrayList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        if (((CourseActivity) activity).contentType.equals(Const.VIDEO))
            return videoArrayList.size();
        else if (((CourseActivity) activity).contentType.equals(Const.EPUB) || ((CourseActivity) activity).contentType.equals(Const.PDF))
            return videoArrayList.size();
        else
            return resultTestSeriesArrayList.size();
    }

    public class SingleStudyListHolder extends RecyclerView.ViewHolder {
        ImageView textDrawable;
        ImageView forward, locIV;
        TextView counter;
        TextView title, subtitleTV;
        LinearLayout studySingleItemLL, attemptLL;

        public SingleStudyListHolder(View itemView) {
            super(itemView);
            textDrawable = itemView.findViewById(R.id.study_item_logoIV);
            forward = itemView.findViewById(R.id.forwardIV);
            locIV = itemView.findViewById(R.id.locIV);
            title = itemView.findViewById(R.id.study_item_titleTV);
            subtitleTV = itemView.findViewById(R.id.studySubTitleTV);
            counter = itemView.findViewById(R.id.countTextTV);
            studySingleItemLL = itemView.findViewById(R.id.study_single_itemLL);
            attemptLL = itemView.findViewById(R.id.attemptLL);
        }

        public void setEpubData(VideoCourse epubData, int position) {
            title.setText(epubData.getTitle());
        }

        public void setData(final Quiz_Basic_Info quizBasicInfo, final int position) {
            title.setText(quizBasicInfo.getTest_series_name());

            TextDrawable textDrawables = TextDrawable.builder()
                    .beginConfig()
                    .textColor(activity.getResources().getColor(R.color.white))
                    .endConfig()
                    .buildRound(String.valueOf(position + 1), Const.color[new Random().nextInt(6) + 1]);
            textDrawable.setImageDrawable(textDrawables);

            subtitleTV.setText(String.format("%s Questions | %s Mins", quizBasicInfo.getTotal_questions(), quizBasicInfo.getTime_in_mins()));

            if (quizBasicInfo.getIs_locked().equalsIgnoreCase("0")) {
                // lockedIV.setVisibility(View.VISIBLE);
                if (quizBasicInfo.getIs_paused().equalsIgnoreCase("0")) {
                    forward.setImageDrawable(activity.getResources().getDrawable(R.mipmap.complete));
                } else if (quizBasicInfo.getIs_paused().equalsIgnoreCase("1")) {
                    //status = context.getString(R.string.pause);
                    forward.setImageDrawable(activity.getResources().getDrawable(R.mipmap.pause));
                } else {
                    //status = context.getString(R.string.start);
                    forward.setImageDrawable(activity.getResources().getDrawable(R.mipmap.start));
                }
            } else {
                forward.setImageDrawable(activity.getResources().getDrawable(R.mipmap.lock));
            }

            forward.setOnClickListener((View view) -> {
                if (singlestudyModel.getIs_purchased().equals("1")) {
                    startTest(quizBasicInfo);
                } else {
                    if (singlestudyModel.getBasic().getMrp().equals("0"))
                        startTest(quizBasicInfo);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singlestudyModel.getBasic().getFor_dams().equals("0"))
                                startTest(quizBasicInfo);
                            else
                                Toast.makeText(activity, "You have to buy this course to attempt the test..", Toast.LENGTH_SHORT).show();
                        } else {
                            if (singlestudyModel.getBasic().getNon_dams().equals("0"))
                                startTest(quizBasicInfo);
                            else
                                Toast.makeText(activity, "You have to buy this course to attempt the test..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        private void startTest(Quiz_Basic_Info quizBasicInfo) {
            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
            String testseriesid = quizBasicInfo.getId();
            Log.e("TSI", testseriesid);
            if (quizBasicInfo.getIs_paused().equalsIgnoreCase("")) {
                Intent quizView = new Intent(activity, TestBaseActivity.class);
                quizView.putExtra(Const.STATUS, false);
                quizView.putExtra(Const.TEST_SERIES_ID, testseriesid);
                activity.startActivity(quizView);

            } else if (quizBasicInfo.getIs_paused().equalsIgnoreCase("0")) {
                ResultTestSeries resultTestSeries = new ResultTestSeries();
                resultTestSeries.setTestSeriesName(quizBasicInfo.getTest_series_name());

                Intent resultScreen = new Intent(activity, QuizActivity.class);
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, quizBasicInfo.getSeg_id());
                activity.startActivity(resultScreen);
            } else {
                Intent quizView = new Intent(activity, TestBaseActivity.class);
                quizView.putExtra(Const.STATUS, false);
                quizView.putExtra(Const.TEST_SERIES_ID, testseriesid);
                activity.startActivity(quizView);
                // networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_TEST_SERIES, true);
            }
        }
    }


    public class SingleEpubListHolder extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {
        TextView fileNameTV, fileCountTV, messageTV, quesCourseTest, minsCourseTest;
        LinearLayout mainLL, rl1, parentLL, lockedLL, shownLL;
        View viewCourseTest;
        ProgressBar downloadprogessPB;
        ImageView fileTypeIV, deleteIV, downloadIV, seeResultIV;
        View dividerView;
        Button quizStateTV;
        SingleCourseData singleCourseData;
        ArrayList<Curriculam> curriculamArrayList;
        eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
        long downloadId;
        int chapterPosition, filePosition;
        CurriculumRecyclerAdapter curriculumRecyclerAdapter;
        boolean lastPosition = false, courseLockedStatus;
        Progress mprogress;
        VideoCourse data;
        View singleView;
        ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;
        //    FolioReader folioReader;
        View.OnClickListener onSingleItemClick;
        ProgressDialog pd;
        private int curriculumPos;

        public SingleEpubListHolder(View convertView) {
            super(convertView);
            rl1 = convertView.findViewById(R.id.rl1);
            quizStateTV = convertView.findViewById(R.id.quizStateTV);
            fileNameTV = convertView.findViewById(R.id.fileTypeTV);
            fileTypeIV = convertView.findViewById(R.id.itemTypeimageIV);
            deleteIV = convertView.findViewById(R.id.deleteIV);
            mainLL = convertView.findViewById(R.id.submainLL);
            parentLL = convertView.findViewById(R.id.parentLL);
            downloadprogessPB = convertView.findViewById(R.id.downloadProgessBar);
            dividerView = convertView.findViewById(R.id.dividerId);
            messageTV = convertView.findViewById(R.id.messageTV);
            downloadIV = convertView.findViewById(R.id.downloadIV);
            seeResultIV = convertView.findViewById(R.id.seeResultIV);
            fileCountTV = convertView.findViewById(R.id.itemCountTV);
            lockedLL = convertView.findViewById(R.id.lockedLL);
            shownLL = convertView.findViewById(R.id.shownLL);
            quesCourseTest = convertView.findViewById(R.id.quesCourseTest);
            minsCourseTest = convertView.findViewById(R.id.minsCourseTest);
            viewCourseTest = convertView.findViewById(R.id.viewCourseTest);
            savedOfflineListener = this;
            mainLL.setPadding(5, 0, 0, 0);
            downloadprogessPB.setScaleY(1.5f);
        }

        public void setEpubData(final VideoCourse epubData, int position) {
            data = epubData;
            fileNameTV.setText(epubData.getTitle());
            fileTypeIV.setImageResource(R.mipmap.epub_new_course);
            quesCourseTest.setText(epubData.getPage_count() + " pages");
            checkData(epubData, position);

            downloadIV.setOnClickListener((View view) -> {
                if (singlestudyModel.getIs_purchased().equals("1"))
                    startDownload(epubData.getURL(), epubData);
                else {
                    if (singlestudyModel.getBasic().getMrp().equals("0"))
                        startDownload(epubData.getURL(), epubData);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singlestudyModel.getBasic().getFor_dams().equals("0"))
                                startDownload(epubData.getURL(), epubData);
                            else
                                Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                        } else {
                            if (singlestudyModel.getBasic().getNon_dams().equals("0"))
                                startDownload(epubData.getURL(), epubData);
                            else
                                Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            deleteIV.setOnClickListener((View view) -> getdownloadcancelDialog(epubData, activity, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD));
        }


        public void getdownloadcancelDialog(final VideoCourse fileMeta, final Activity ctx, final String title, final String message) {
            View v = Helper.newCustomDialog(ctx, true, title, message);

            Button btnCancel, btnSubmit;

            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(ctx.getString(R.string.no));
            btnSubmit.setText(ctx.getString(R.string.yes));

            btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

            btnSubmit.setOnClickListener((View view) -> {
                Helper.dismissDialog();
                eMedicozDownloadManager.removeOfflineData(fileMeta.getId(), Const.EPUB,
                        ctx, fileMeta.getId());

                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.download_new_course);

                downloadprogessPB.setVisibility(View.GONE);
                downloadprogessPB.setProgress(0);

                messageTV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
            });
        }


        private void startDownload(String fileUrl, VideoCourse data) {
            offlineData offline = getOfflineDataIds(data.getId(), Const.EPUB, activity, data.getId());

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
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, Const.EPUB);

                }//when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, Const.EPUB);

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
                try {
                    eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getId(), fileUrl,
                            Helper.getFileName(fileUrl, data.getTitle(), Const.EPUB), Const.EPUB, data.getId(), downloadid -> {

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
                                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, Const.EPUB);
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

        public void singleViewCLick(VideoCourse fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(fileMetaData.getId(),
                        Const.EPUB, activity, fileMetaData.getId());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

                final Intent intent = new Intent(activity, ePubActivity.class);
                intent.putExtra("filePath", activity.getFilesDir() + "/" + offlineData.getLink());
                activity.startActivity(intent);
            }
        }

        private void checkData(VideoCourse epubData, int position) {
            offlineData offlinedata = getOfflineDataIds(epubData.getId(),
                    Const.EPUB, activity, epubData.getId());

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
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.EPUB);
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
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.EPUB);
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


        @Override
        public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {
            downloadIV.setVisibility(View.VISIBLE);
            deleteIV.setVisibility(View.VISIBLE);
            downloadprogessPB.setVisibility(View.GONE);
            downloadIV.setImageResource(R.mipmap.eye_on);
            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.downloaded_offline);
            eMedicozDownloadManager.
                    addOfflineDataIds(data.getId(), data.getURL(), activity,
                            false, true, Const.EPUB, requestInfo.getId(), data.getId());
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
                downloadIV.setImageResource(R.mipmap.download_download);

                deleteIV.setVisibility(View.GONE);
                messageTV.setVisibility(View.INVISIBLE);

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
    }

    public class SinglePdfListHolder extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {
        TextView fileNameTV, fileCountTV, messageTV, quesCourseTest, minsCourseTest;
        LinearLayout mainLL, rl1, parentLL, lockedLL, shownLL;
        View viewCourseTest;
        ProgressBar downloadprogessPB;
        ImageView fileTypeIV, deleteIV, downloadIV, seeResultIV;
        View dividerView;
        Button quizStateTV;
        SingleCourseData singleCourseData;
        ArrayList<Curriculam> curriculamArrayList;
        eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
        long downloadId;
        int chapterPosition, filePosition;
        CurriculumRecyclerAdapter curriculumRecyclerAdapter;
        boolean lastPosition = false, courseLockedStatus;
        Progress mprogress;
        VideoCourse data;
        View singleView;
        ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;
        //    FolioReader folioReader;
        View.OnClickListener onSingleItemClick;
        ProgressDialog pd;
        private int curriculumPos;

        public SinglePdfListHolder(View convertView) {
            super(convertView);
            rl1 = convertView.findViewById(R.id.rl1);
            quizStateTV = convertView.findViewById(R.id.quizStateTV);
            fileNameTV = convertView.findViewById(R.id.fileTypeTV);
            fileTypeIV = convertView.findViewById(R.id.itemTypeimageIV);
            deleteIV = convertView.findViewById(R.id.deleteIV);
            mainLL = convertView.findViewById(R.id.submainLL);
            parentLL = convertView.findViewById(R.id.parentLL);
            downloadprogessPB = convertView.findViewById(R.id.downloadProgessBar);
            dividerView = convertView.findViewById(R.id.dividerId);
            messageTV = convertView.findViewById(R.id.messageTV);
            downloadIV = convertView.findViewById(R.id.downloadIV);
            seeResultIV = convertView.findViewById(R.id.seeResultIV);
            fileCountTV = convertView.findViewById(R.id.itemCountTV);
            lockedLL = convertView.findViewById(R.id.lockedLL);
            shownLL = convertView.findViewById(R.id.shownLL);
            quesCourseTest = convertView.findViewById(R.id.quesCourseTest);
            minsCourseTest = convertView.findViewById(R.id.minsCourseTest);
            viewCourseTest = convertView.findViewById(R.id.viewCourseTest);
            savedOfflineListener = this;
            mainLL.setPadding(5, 0, 0, 0);
            downloadprogessPB.setScaleY(1.5f);
        }

        public void setPdfData(final VideoCourse pdfData, int position) {
            data = pdfData;
            fileNameTV.setText(pdfData.getTitle());
            fileTypeIV.setImageResource(R.mipmap.pdf);
            quesCourseTest.setText(pdfData.getDescription());
            checkData(pdfData, position);

            downloadIV.setOnClickListener((View view) -> {
                if (singlestudyModel.getIs_purchased().equals("1"))
                    startDownload(pdfData.getURL(), pdfData);
                else {
                    if (singlestudyModel.getBasic().getMrp().equals("0"))
                        startDownload(pdfData.getURL(), pdfData);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singlestudyModel.getBasic().getFor_dams().equals("0"))
                                startDownload(pdfData.getURL(), pdfData);
                            else
                                Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                        } else {
                            if (singlestudyModel.getBasic().getNon_dams().equals("0"))
                                startDownload(pdfData.getURL(), pdfData);
                            else
                                Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            deleteIV.setOnClickListener((View view) -> getdownloadcancelDialog(pdfData, activity, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD));
        }


        public void getdownloadcancelDialog(final VideoCourse fileMeta, final Activity ctx, final String title, final String message) {
            View v = Helper.newCustomDialog(ctx, true, title, message);

            Button btnCancel, btnSubmit;

            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(ctx.getString(R.string.no));
            btnSubmit.setText(ctx.getString(R.string.yes));

            btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

            btnSubmit.setOnClickListener((View view) -> {
                Helper.dismissDialog();
                eMedicozDownloadManager.removeOfflineData(fileMeta.getId(), Const.PDF,
                        ctx, fileMeta.getId());

                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.download_new_course);

                downloadprogessPB.setVisibility(View.GONE);
                downloadprogessPB.setProgress(0);

                messageTV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
            });
        }


        private void startDownload(String fileUrl, VideoCourse data) {
            offlineData offline = getOfflineDataIds(data.getId(), Const.PDF, activity, data.getId());

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
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, Const.PDF);

                }//when some error occurred
                else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                    messageTV.setVisibility(View.VISIBLE);
                    downloadprogessPB.setVisibility(View.VISIBLE);
                    downloadIV.setVisibility(View.GONE);
                    deleteIV.setVisibility(View.VISIBLE);
                    messageTV.setText(R.string.download_pending);
                    downloadId = offline.getDownloadid();
                    eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(), savedOfflineListener, Const.PDF);

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

                try {
                    eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getId(), fileUrl,
                            Helper.getFileName(fileUrl, data.getTitle(), Const.PDF), Const.PDF, data.getId(), downloadid -> {

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
                                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, Const.PDF);
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

        public void singleViewCLick(VideoCourse fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(fileMetaData.getId(),
                        Const.PDF, activity, fileMetaData.getId());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                // Helper.GoToVideoActivity(context, fileMetaData.getLink(), Const.VIDEO_STREAM);
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

                Helper.GoToWebViewActivity(activity, fileMetaData.getURL());
            }
        }

        private void checkData(VideoCourse epubData, int position) {
            offlineData offlinedata = getOfflineDataIds(epubData.getId(),
                    Const.PDF, activity, epubData.getId());

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
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.PDF);
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
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.PDF);
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


        @Override
        public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {
            downloadIV.setVisibility(View.VISIBLE);
            deleteIV.setVisibility(View.VISIBLE);
            downloadprogessPB.setVisibility(View.GONE);
            downloadIV.setImageResource(R.mipmap.eye_on);
            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.downloaded_offline);
            eMedicozDownloadManager.
                    addOfflineDataIds(data.getId(), data.getURL(), activity,
                            false, true, Const.PDF, requestInfo.getId(), data.getId());
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
                downloadIV.setImageResource(R.mipmap.download_download);

                deleteIV.setVisibility(View.GONE);
                messageTV.setVisibility(View.INVISIBLE);

            } else if (status == Fetch.STATUS_ERROR) {

                downloadIV.setImageResource(R.mipmap.download_reload);
                downloadIV.setVisibility(View.VISIBLE);

                deleteIV.setVisibility(View.VISIBLE);
                messageTV.setText(R.string.error_in_downloading);

            } else if (status == Fetch.STATUS_DOWNLOADING) {

                downloadIV.setVisibility(View.GONE);

                deleteIV.setVisibility(View.VISIBLE);

                if (value > 0) {
                    messageTV.setText("Downloading..." + value + "%");
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
    }

    public class SubjectVideosHolder extends RecyclerView.ViewHolder implements eMedicozDownloadManager.SavedOfflineVideoCallBack {
        private static final String TAG = "SubjectVideosHolder";
        ImageView deleteIV, downloadIV;
        TextView messageTV, tvLiveon, tvLiveonDate;
        ProgressBar downloadprogessPB;
        VideoCourse data;
        TextView name, desc, ivShow, ibtSingleSubVdTvDes;
        ImageView thumb, locIV;
        ImageView liveIv;
        long downloadId;
        RelativeLayout videoTile, imageRl;
        LinearLayout llDownload;
        private eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;

        public SubjectVideosHolder(View itemView) {
            super(itemView);
            liveIv = itemView.findViewById(R.id.liveIV);
            name = itemView.findViewById(R.id.ibt_single_sub_vd_tv_title);
            desc = itemView.findViewById(R.id.ibt_single_sub_vd_tv_day);
            thumb = itemView.findViewById(R.id.ibt_single_sub_vd_iv);
            ibtSingleSubVdTvDes = itemView.findViewById(R.id.ibt_single_sub_vd_tv_des);
            locIV = itemView.findViewById(R.id.locIV);
            imageRl = itemView.findViewById(R.id.ImageRL);
            videoTile = itemView.findViewById(R.id.ibt_single_sub_vd_RL);
            tvLiveon = itemView.findViewById(R.id.tv_liveon);
            tvLiveonDate = itemView.findViewById(R.id.tv_liveon_date);
            savedOfflineListener = this;
            downloadprogessPB = itemView.findViewById(R.id.downloadProgessBar);
            downloadprogessPB.setScaleY(1.5f);
            deleteIV = itemView.findViewById(R.id.deleteIV);
            downloadIV = itemView.findViewById(R.id.downloadIV);
            messageTV = itemView.findViewById(R.id.messageTV);
            ivShow = itemView.findViewById(R.id.iv_show);
            llDownload = itemView.findViewById(R.id.ll_download_view);
        }

        public void setData(final VideoCourse data, final int position) {
            this.data = data;
            desc.setText(String.format("%s.", position + 1));
            name.setText(data.getVideo_title());
            ibtSingleSubVdTvDes.setText(data.getDescription());

            if (!data.getLive_on().equals("0") && !data.getIs_live().equals("1")) {
                tvLiveon.setVisibility(View.VISIBLE);
                tvLiveonDate.setVisibility(View.VISIBLE);

                if (!data.getLive_on().equals(""))
                    tvLiveonDate.setText(Helper.gaetFormatedDate(Long.parseLong(data.getLive_on()) * 1000));

            } else if (data.getVideo_type().equals("0") && data.getIs_vod().equals("1")) {
                tvLiveon.setVisibility(View.GONE);
                tvLiveonDate.setVisibility(View.VISIBLE);

            }

            Glide.with(activity).asGif()
                    .load(R.drawable.live_gif)
                    .into(liveIv);
            if (data.getIs_live() != null) {
                if (data.getIs_live().equals("1"))
                    liveIv.setVisibility(View.VISIBLE);
                else
                    liveIv.setVisibility(View.GONE);
            } else {
                liveIv.setVisibility(View.GONE);
            }
            if (SharedPreference.getInstance().getBoolean(Const.SINGLE_STUDY)) {
                if (data.getIs_vod().equals("1")) {
                    downloadIV.setVisibility(View.VISIBLE);
                    checkData(data, position);
                } else {
                    downloadIV.setVisibility(View.GONE);
                }

                locIV.setVisibility(View.GONE);
                thumb.setVisibility(View.VISIBLE);
                imageRl.setBackground(null);

                Glide.with(activity)
                        .asBitmap()
                        .load(Uri.parse(data.getThumbnail_url()).toString())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                thumb.setImageBitmap(result);
                            }
                        });
            } else {
                if (Constants.IS_PURCHASED != null && Constants.IS_PURCHASED.equals("1")) {
                    if (data.getIs_locked().equals("1")) {
                        locIV.setVisibility(View.VISIBLE);
                        llDownload.setVisibility(View.GONE);
                        downloadIV.setVisibility(View.GONE);
                        Glide.with(activity)
                                .asBitmap()
                                .load(Uri.parse(data.getThumbnail_url()).toString())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                        thumb.setImageBitmap(result);
                                    }
                                });
                    } else {
                        if (data.getVideo_type().equals("0") && data.getIs_vod().equals("0")) {
//                                llDownload.setVisibility(View.VISIBLE);
                            // downloadIV.setVisibility(View.VISIBLE);
                        } else {
//                                llDownload.setVisibility(View.GONE);
                            downloadIV.setVisibility(View.GONE);
                        }
                        locIV.setVisibility(View.GONE);
                        thumb.setVisibility(View.VISIBLE);
                        imageRl.setBackground(null);

                        Glide.with(activity)
                                .asBitmap()
                                .load(Uri.parse(data.getThumbnail_url()).toString())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                        thumb.setImageBitmap(result);
                                    }
                                });

                    }
                } else {
                    locIV.setVisibility(View.GONE);
                    thumb.setVisibility(View.VISIBLE);
                    imageRl.setBackground(null);

                    Glide.with(activity)
                            .asBitmap()
                            .load(Uri.parse(data.getThumbnail_url()).toString())
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                    thumb.setImageBitmap(result);
                                }
                            });
                }
            }
            Date d = new Date(Long.parseLong(GenericUtils.getParsableString(data.getLive_on())) * 1000);
            //Date d = new Date(Long.parseLong("1576837800")*1000);
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
            final String correctDateFormat = GenericUtils.getFormattedDate(fullDate, amPM);

            Log.e(TAG, "Current time in AM/PM: " + correctDateFormat);

            bindControls(data, correctDateFormat);
        }

        private void bindControls(VideoCourse data, String correctDateFormat) {
            videoTile.setOnClickListener((View view) -> {
                if (singlestudyModel.getIs_purchased().equals("1")) {
                    goToLiveCourseActivity(data, correctDateFormat);
                } else {
                    if (singlestudyModel.getBasic().getMrp().equals("0"))
                        goToLiveCourseActivity(data, correctDateFormat);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singlestudyModel.getBasic().getFor_dams().equals("0"))
                                goToLiveCourseActivity(data, correctDateFormat);
                            else
                                Toast.makeText(activity, R.string.buy_to_watch, Toast.LENGTH_SHORT).show();
                        } else {
                            if (singlestudyModel.getBasic().getNon_dams().equals("0"))
                                goToLiveCourseActivity(data, correctDateFormat);
                            else
                                Toast.makeText(activity, R.string.buy_to_watch, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            downloadIV.setOnClickListener((View view) -> {
                if (singlestudyModel.getIs_purchased().equals("1"))
                    startDownload(data.getURL(), data);
                else {
                    if (singlestudyModel.getBasic().getMrp().equals("0"))
                        startDownload(data.getURL(), data);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singlestudyModel.getBasic().getFor_dams().equals("0"))
                                startDownload(data.getURL(), data);
                            else
                                Toast.makeText(activity, R.string.buy_to_download_video, Toast.LENGTH_SHORT).show();
                        } else {
                            if (singlestudyModel.getBasic().getNon_dams().equals("0"))
                                startDownload(data.getURL(), data);
                            else
                                Toast.makeText(activity, R.string.buy_to_download_video, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            deleteIV.setOnClickListener((View view) -> getdownloadcancelDialog(data, activity, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD));

        }

        private void goToLiveCourseActivity(VideoCourse data, String correctDateFormat) {
            Log.e(TAG, "onClick: " + System.currentTimeMillis());
            if (!GenericUtils.isEmpty(data.getLive_on()) && (Long.parseLong(data.getLive_on()) * 1000)
                    <= System.currentTimeMillis()) {
                if (data.getURL().contains(".m3u8")) {
                    Intent intent = new Intent(activity, LiveCourseActivity.class);
                    intent.putExtra(Const.VIDEO_LINK, data.getURL());
                    intent.putExtra(Const.VIDEO, data);
                    activity.startActivity(intent);
                } else {
                    if (GenericUtils.isEmpty(data.getLive_url()))
                        Helper.GoToVideoActivity(activity, data.getURL(), Const.VIDEO_STREAM, data.getId(), Const.COURSE_VIDEO_TYPE);
                    else
                        Helper.GoToVideoActivity(activity, data.getLive_url(), Const.VIDEO_STREAM, data.getId(), Const.COURSE_VIDEO_TYPE);
                }

            } else {
                Toast.makeText(activity, "This video will be live on: " + correctDateFormat, Toast.LENGTH_SHORT).show();
            }
        }

        public void getdownloadcancelDialog(final VideoCourse fileMeta, final Activity ctx, final String title, final String message) {
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


        public void singleViewCLick(VideoCourse fileMetaData, offlineData offlineData) {
            if (offlineData == null) {
                offlineData = getOfflineDataIds(fileMetaData.getId(),
                        Const.VIDEOS, activity, fileMetaData.getId());
            }

            if (offlineData != null && offlineData.getRequestInfo() == null) {
                offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
            }
            if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                if ((activity.getFilesDir() + "/" + offlineData.getLink()).contains(".mp4")) {
                    Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                } else {
                    Helper.DecryptAndGoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                }
            }
        }

        private void checkData(VideoCourse epubData, int position) {
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


        private void startDownload(String fileUrl, VideoCourse data) {
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

                    singleViewCLick(data, offline);
                }
            }//for new download
            else if (offline == null || offline.getRequestInfo() == null) {
                messageTV.setVisibility(View.VISIBLE);
                downloadprogessPB.setVisibility(View.VISIBLE);
                downloadIV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.VISIBLE);
                messageTV.setText(R.string.download_queued);

                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getId(), fileUrl,
                        Helper.getFileName(fileUrl, data.getVideo_title(), Const.VIDEOS), Const.VIDEOS, data.getId(),
                        downloadid -> {

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
                    addOfflineDataIds(data.getId(), data.getURL(), activity,
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
                downloadIV.setImageResource(R.mipmap.download_download);

                deleteIV.setVisibility(View.GONE);
                messageTV.setVisibility(View.INVISIBLE);

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
    }

    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public GetImageFromUrl(ImageView img) {
            this.imageView = img;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            bitmap = null;
            InputStream inputStream;
            try {
                inputStream = new java.net.URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
