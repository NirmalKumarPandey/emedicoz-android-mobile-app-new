package com.emedicoz.app.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.adapter.CurriculumRecyclerAdapter;
import com.emedicoz.app.courses.adapter.ReattemptDialogAdapter;
import com.emedicoz.app.epubear.ePubActivity;
import com.emedicoz.app.modelo.TestSeriesResultData;
import com.emedicoz.app.modelo.courses.CourseLockedManager;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.quiz.QuizModel;
import com.emedicoz.app.modelo.courses.quiz.Quiz_Basic_Info;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.AES.generateVector;
import static com.emedicoz.app.utilso.AES.generatekey;
import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

/**
 * Created by app on 21/12/17.
 */

public class SingleChildViewCurriculum implements eMedicozDownloadManager.SavedOfflineVideoCallBack {

    private static final String TAG = "SingleChildViewCurricul";

    Activity context;
    TextView fileNameTV;
    TextView fileCountTV;
    TextView messageTV;
    TextView quesCourseTest;
    TextView minsCourseTest;
    LinearLayout mainLL;
    LinearLayout rl1;
    LinearLayout parentLL;
    LinearLayout lockedLL;
    LinearLayout shownLL;
    View viewCourseTest;
    ProgressBar downloadprogessPB;
    ImageView fileTypeIV;
    ImageView deleteIV;
    ImageView downloadIV;
    ImageView seeResultIV;
    View dividerView;
    Button quizStateTV;
    String url720 = "";
    String url480 = "";
    String url360 = "";
    String url240 = "";
    Curriculam.File_meta currentFileMeta;
    SingleCourseData singleCourseData;
    ArrayList<Curriculam> curriculamArrayList;
    eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
    long downloadId;
    int chapterPosition;
    int filePosition;
    CurriculumRecyclerAdapter curriculumRecyclerAdapter;
    boolean lastPosition = false;
    boolean courseLockedStatus;
    Progress mProgress;
    View singleView;
    ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;

    public SingleChildViewCurriculum() {

    }

    public SingleChildViewCurriculum(Activity activity, CurriculumRecyclerAdapter curriculumRecyclerAdapter, SingleCourseData singleCourseData, ArrayList<Curriculam> curriculamArrayList) {
        this.singleCourseData = singleCourseData;
        this.context = activity;
        savedOfflineListener = this;
        this.curriculamArrayList = curriculamArrayList;
        this.curriculumRecyclerAdapter = curriculumRecyclerAdapter;
        testSeriesResultDataArrayList = new ArrayList<>();
    }

    public void initViews(View convertView) {

        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        singleView = convertView;

        mainLL.setPadding(5, 0, 0, 0);
        downloadprogessPB.setScaleY(1.5f);

    }

    public void singleViewCLick(Curriculam.File_meta fileMetaData, offlineData offlineData) {
        if (offlineData == null && fileMetaData.getFile().equalsIgnoreCase("pdf")) {

            offlineData = getOfflineDataIds(fileMetaData.getId(),
                    fileMetaData.getFile(), context, singleCourseData.getId());
            Helper.GoToWebViewActivity(context, fileMetaData.getLink());
        }

        if (offlineData != null && offlineData.getRequestInfo() == null) {
            offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
        }
        if (offlineData != null && offlineData.getRequestInfo() != null &&
                offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
            switch (fileMetaData.getFile()) {
                case Constants.CourseType.PDF:
                    Helper.GoToWebViewActivity(context, fileMetaData.getLink());
                    break;
                case Constants.CourseType.VIDEO:
                    Log.e(TAG, "singleViewCLick: " + context.getFilesDir() + "/" + offlineData.getLink());
                    if (fileMetaData.getEnc_url() != null && fileMetaData.getEnc_url().getToken() != null) {
                        if ((context.getFilesDir() + "/" + offlineData.getLink()).contains(".mp4")) {
                            Helper.GoToVideoActivity(context, context.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                        } else {
                            Helper.DecryptAndGoToVideoActivity(context, context.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                        }
                    } else {
                        Helper.GoToVideoActivity(context, context.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                    }
                    if (lastPosition)
                        addCourseStatus(true);
                    break;
                case Constants.CourseType.EPUB:
                    final Intent intent = new Intent(context, ePubActivity.class);
                    intent.putExtra("filePath", context.getFilesDir() + "/" + offlineData.getLink());
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    public void startDownload(String fileUrl, final Curriculam.File_meta fileMetaData) {

        offlineData offline = getOfflineDataIds(fileMetaData.getId(),
                fileMetaData.getFile(), context, singleCourseData.getId());
        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        downloadprogessPB.setVisibility(View.GONE);
        deleteIV.setVisibility(View.GONE);
        messageTV.setVisibility(View.GONE);
        downloadIV.setVisibility(View.GONE);

        if (offline != null && offline.getRequestInfo() != null) {

            //when video is downloading
            if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                Toast.makeText(context, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();
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
                        savedOfflineListener, fileMetaData.getFile());
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
                        savedOfflineListener, fileMetaData.getFile());
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

            try {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(context, fileMetaData.getId(), fileUrl,
                        Helper.getFileName(fileUrl, fileMetaData.getTitle(), fileMetaData.getFile()), fileMetaData.getFile(),
                        singleCourseData.getId(), downloadid -> {

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
                                eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, fileMetaData.getFile());
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
        } else {
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
            if (offline.getRequestInfo() == null) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                downloadIV.performClick();
            }
        }
    }

    public void startDownloadNotEncrypted(String fileUrl, final Curriculam.File_meta fileMetaData) {

        offlineData offline = getOfflineDataIds(fileMetaData.getId(),
                fileMetaData.getFile(), context, singleCourseData.getId());
        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        downloadprogessPB.setVisibility(View.GONE);
        deleteIV.setVisibility(View.GONE);
        messageTV.setVisibility(View.GONE);
        downloadIV.setVisibility(View.GONE);

        if (offline != null && offline.getRequestInfo() != null) {

            //when video is downloading
            if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                Toast.makeText(context, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();
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
                        savedOfflineListener, fileMetaData.getFile());
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
                        savedOfflineListener, fileMetaData.getFile());
            }
            // when download is completed.
            else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                messageTV.setText(R.string.downloaded_offline);
                downloadprogessPB.setVisibility(View.GONE);
                downloadIV.setVisibility(View.VISIBLE);
                downloadIV.setImageResource(R.mipmap.eye_on);
                deleteIV.setVisibility(View.VISIBLE);

                singleViewCLick(fileMetaData, offline);
            }
        }
        //for new download
        else if (offline == null || offline.getRequestInfo() == null) {

            try {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(context, fileMetaData.getId(), fileUrl,
                        Helper.getFileName(fileUrl, fileMetaData.getTitle(), fileMetaData.getFile()),
                        fileMetaData.getFile(), singleCourseData.getId(), downloadid -> {

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
                                eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, fileMetaData.getFile());
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
        } else {
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
            if (offline.getRequestInfo() == null) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                downloadIV.performClick();
            }
        }
    }


    public void setOnClick(final Curriculam.File_meta fileMetaData) {
        singleView.setOnClickListener(view -> {
            if (!fileMetaData.getFile().equals(Constants.TestType.TEST)) {
                if (singleCourseData.getIs_purchased().equals("1")) {
                    singleViewCLick(fileMetaData, null);
                } else {
                    if (singleCourseData.getMrp().equals("0"))
                        singleViewCLick(fileMetaData, null);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singleCourseData.getFor_dams().equals("0"))
                                singleViewCLick(fileMetaData, null);
                            else
                                Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                        } else {
                            if (singleCourseData.getNon_dams().equals("0"))
                                singleViewCLick(fileMetaData, null);
                            else
                                Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        fileTypeIV.setOnClickListener(view -> {
            if (fileMetaData.getFile().equals(Constants.CourseType.VIDEO)) {
                if (singleCourseData.getIs_purchased().equals("1")) {//removed temp to test the download
                    playVideoOnIconClick(fileMetaData);
                } else {
                    if (singleCourseData.getMrp().equals("0"))
                        playVideoOnIconClick(fileMetaData);
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singleCourseData.getFor_dams().equals("0"))
                                playVideoOnIconClick(fileMetaData);
                            else
                                Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                        } else {
                            if (singleCourseData.getNon_dams().equals("0"))
                                playVideoOnIconClick(fileMetaData);
                            else
                                Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        downloadIV.setOnClickListener(view -> {
            if (singleCourseData.getIs_purchased().equals("1")) {
                checkDownloadUrl(fileMetaData);
            } else {
                if (singleCourseData.getMrp().equals("0"))
                    checkDownloadUrl(fileMetaData);
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (singleCourseData.getFor_dams().equals("0"))
                            checkDownloadUrl(fileMetaData);
                        else
                            Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                    } else {
                        if (singleCourseData.getNon_dams().equals("0"))
                            checkDownloadUrl(fileMetaData);
                        else
                            Toast.makeText(context, context.getString(R.string.courseBuy), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        deleteIV.setOnClickListener(view -> {
            if (!fileMetaData.getFile().equals(Constants.TestType.TEST)) {
                getDownloadCancelDialog(fileMetaData, context, "Delete Download", "Are you sure you want to delete the download?");
            } else {
                if (singleCourseData.getIs_purchased().equals("1")) {
                    startTest();
                } else {
                    if (singleCourseData.getMrp().equals("0"))
                        startTest();
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                            if (singleCourseData.getFor_dams().equals("0"))
                                startTest();
                            else
                                Toast.makeText(context, context.getResources().getString(R.string.please_buy_course_to_start_test), Toast.LENGTH_SHORT).show();
                        } else {
                            if (singleCourseData.getNon_dams().equals("0"))
                                startTest();
                            else
                                Toast.makeText(context, context.getResources().getString(R.string.please_buy_course_to_start_test), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        quizStateTV.setOnClickListener(view -> deleteIV.performClick());


        if (fileMetaData.getFile().equals(Constants.TestType.TEST)) {
            seeResultIV.setOnClickListener(view -> {
                if (singleCourseData.getIs_purchased().equals("1") && !TextUtils.isEmpty(currentFileMeta.isIs_user_attemp())) {
                    Intent resultScreen = new Intent(context, QuizActivity.class);
                    resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                    resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, currentFileMeta.isIs_user_attemp());
                    context.startActivity(resultScreen);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.please_buy_course_to_start_test), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void playVideoOnIconClick(Curriculam.File_meta fileMetaData) {
        offlineData offline = getOfflineDataIds(fileMetaData.getId(),
                fileMetaData.getFile(), context, singleCourseData.getId());
        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        downloadprogessPB.setVisibility(View.GONE);
        deleteIV.setVisibility(View.GONE);
        messageTV.setVisibility(View.GONE);
        downloadIV.setVisibility(View.GONE);

        if (offline != null && offline.getRequestInfo() != null) {

            //when video is downloading
            if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                Toast.makeText(context, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show();
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
                        savedOfflineListener, fileMetaData.getFile());
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
                        savedOfflineListener, fileMetaData.getFile());
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
        else if (offline == null) {
            Helper.GoToVideoActivity(context, fileMetaData.getNew_link(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);

            Log.e("CTX", String.valueOf(context));
            downloadIV.setVisibility(View.VISIBLE);
        } else {
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
            if (offline.getRequestInfo() == null) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                downloadIV.performClick();
            }
        }
    }

    private void checkDownloadUrl(Curriculam.File_meta fileMetaData) {
        if (fileMetaData.getEnc_url() != null && fileMetaData.getEnc_url().getFiles() != null) {
            offlineData offlineData = getOfflineDataIds(fileMetaData.getId(),
                    fileMetaData.getFile(), context, singleCourseData.getId());

            if (offlineData == null) {
                View sheetView = Helper.openBottomSheetDialog(context);
                ArrayList<String> decryptedUrl = new ArrayList<>();
                for (int i = 0; i < fileMetaData.getEnc_url().getFiles().size(); i++) {
                    decryptedUrl.add(AES.decrypt(fileMetaData.getEnc_url().getFiles().get(i).getUrl(), generatekey(fileMetaData.getEnc_url().getToken()), generateVector(fileMetaData.getEnc_url().getToken())));
                }

                Log.e(TAG, "onClick: " + decryptedUrl.get(0));


                TextView size720, size480, size360, size240;
                final RadioButton radio1, radio2, radio3, radio4;
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
                        size720.setText(fileMetaData.getEnc_url().getFiles().get(i).getSize() + " MB");
                        url720 = fileMetaData.getEnc_url().getFiles().get(i).getUrl();
                    } else if (decryptedUrl.get(i).contains("480out_put")) {
                        size480.setText(fileMetaData.getEnc_url().getFiles().get(i).getSize() + " MB");
                        url480 = fileMetaData.getEnc_url().getFiles().get(i).getUrl();
                    } else if (decryptedUrl.get(i).contains("360out_put")) {
                        size360.setText(fileMetaData.getEnc_url().getFiles().get(i).getSize() + " MB");
                        url360 = fileMetaData.getEnc_url().getFiles().get(i).getUrl();
                    } else if (decryptedUrl.get(i).contains("240out_put")) {
                        size240.setText(fileMetaData.getEnc_url().getFiles().get(i).getSize() + " MB");
                        url240 = fileMetaData.getEnc_url().getFiles().get(i).getUrl();
                    }
                }

                radio3.setChecked(true);

                relativeLayout1.setOnClickListener((View view) -> {
                    radio1.setChecked(true);
                    radio2.setChecked(false);
                    radio3.setChecked(false);
                    radio4.setChecked(false);
                });

                relativeLayout2.setOnClickListener((View view) -> {
                    radio2.setChecked(true);
                    radio1.setChecked(false);
                    radio3.setChecked(false);
                    radio4.setChecked(false);
                });

                relativeLayout3.setOnClickListener((View view) -> {
                    radio3.setChecked(true);
                    radio2.setChecked(false);
                    radio1.setChecked(false);
                    radio4.setChecked(false);
                });

                relativeLayout4.setOnClickListener((View view) -> {
                    radio4.setChecked(true);
                    radio2.setChecked(false);
                    radio3.setChecked(false);
                    radio1.setChecked(false);

                });

                downloadVideoBtn.setOnClickListener((View view) -> {
                    if (radio1.isChecked()) {
                        Helper.dismissBottonSheetDialog();
                        startDownload(AES.decrypt(url720, generatekey(fileMetaData.getEnc_url().getToken()), generateVector(fileMetaData.getEnc_url().getToken())), fileMetaData);
                    } else if (radio2.isChecked()) {
                        Helper.dismissBottonSheetDialog();
                        startDownload(AES.decrypt(url480, generatekey(fileMetaData.getEnc_url().getToken()), generateVector(fileMetaData.getEnc_url().getToken())), fileMetaData);
                    } else if (radio3.isChecked()) {
                        Helper.dismissBottonSheetDialog();
                        startDownload(AES.decrypt(url360, generatekey(fileMetaData.getEnc_url().getToken()), generateVector(fileMetaData.getEnc_url().getToken())), fileMetaData);
                    } else if (radio4.isChecked()) {
                        Helper.dismissBottonSheetDialog();
                        startDownload(AES.decrypt(url240, generatekey(fileMetaData.getEnc_url().getToken()), generateVector(fileMetaData.getEnc_url().getToken())), fileMetaData);
                    }
                });

            } else {
                singleViewCLick(fileMetaData, offlineData);
            }
        } else {

            startDownloadNotEncrypted(fileMetaData.getLink(), fileMetaData);
        }
    }

    private void startTest() {
        SharedPreference.getInstance().putInt(Const.LAST_POS, filePosition);
        SharedPreference.getInstance().putInt(Const.CHAPTER_POS, chapterPosition);
        networkCallForTestSeries();//networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_TEST_SERIES, true);
    }

    private void sendViewMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("dowloaded");
        // You can also include some extra data.
        intent.putExtra(Constants.Extras.MESSAGE, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void addCourseStatus(boolean nextChapter) {
        try {
            if (nextChapter && curriculamArrayList.get(chapterPosition + 1) != null) { // to check that we have to check in the new chapter or same chapter
                CourseLockedManager.addCourseLockStatus(singleCourseData.getId(), String.valueOf(chapterPosition + 1), context);
                curriculumRecyclerAdapter.onUpdateStatus(chapterPosition + 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getDownloadCancelDialog(final Curriculam.File_meta fileMeta, final Activity ctx, final String title, final String message) {
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
            eMedicozDownloadManager.removeOfflineData(fileMeta.getId(), fileMeta.getFile(),
                    ctx, singleCourseData.getId());

            downloadIV.setVisibility(View.VISIBLE);
            downloadIV.setImageResource(R.mipmap.download_new_course);

            downloadprogessPB.setVisibility(View.GONE);
            downloadprogessPB.setProgress(0);

            messageTV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
        });
    }

    public void setChildData(final Curriculam.File_meta childText, final int chapterPosition, int filePosition, boolean position, boolean lockedStatus) {
        currentFileMeta = childText;
        this.chapterPosition = chapterPosition;
        this.filePosition = filePosition;
        this.lastPosition = position;
        this.courseLockedStatus = lockedStatus;
        fileNameTV.setText(childText.getTitle());


        if (position) dividerView.setVisibility(View.GONE);
        else dividerView.setVisibility(View.VISIBLE);

        // to set Image on the title with the required Doc
        switch (childText.getFile()) {
            case Const.PDF:
                fileTypeIV.setImageResource(R.mipmap.pdf_);
                break;
            case Const.VIDEO:
                fileTypeIV.setImageResource(R.mipmap.video_new_course);
                break;
            case Const.EPUB:
                fileTypeIV.setImageResource(R.mipmap.epub_new_course);
                break;
            case Const.PPT:
                fileTypeIV.setImageResource(R.mipmap.ppt);
                break;
            case Constants.TestType.TEST:
                fileTypeIV.setImageResource(R.mipmap.test_new_course);
                break;
            case Const.DOC:
                fileTypeIV.setImageResource(R.mipmap._doc_quiz);
                break;
            default:
                fileTypeIV.setImageResource(R.mipmap.courses_blue);
                break;
        }

        if (childText.getFile().equalsIgnoreCase(Constants.CourseType.VIDEO)) {
            quizStateTV.setVisibility(View.GONE);
            if (!GenericUtils.isEmpty(childText.getDuration())) {
                quesCourseTest.setVisibility(View.VISIBLE);
                quesCourseTest.setText(String.format("Video: %s mins", childText.getDuration()));
            } else
                quesCourseTest.setVisibility(View.GONE);
            if (courseLockedStatus) {
                mainLL.setOnClickListener(null);
            } else {
                rl1.setVisibility(View.VISIBLE);
                lockedLL.setVisibility(View.GONE);
                setOnClick(childText);
                // starting of downloading code snippet
                offlineData offlinedata = getOfflineDataIds(childText.getId(),
                        childText.getFile(), context, singleCourseData.getId());

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
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, currentFileMeta.getFile());
                        }
                        //1. downloading in progress
                        else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                                && offlinedata.getRequestInfo().getProgress() < 100) {

                            downloadIV.setVisibility(View.GONE);
                            deleteIV.setVisibility(View.VISIBLE);
                            downloadprogessPB.setVisibility(View.VISIBLE);
                            downloadprogessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                            messageTV.setText(R.string.download_queued);
                            downloadId = offlinedata.getDownloadid();
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, currentFileMeta.getFile());
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
        } else if ((childText.getFile().equalsIgnoreCase(Constants.TestType.TEST))) {
            quesCourseTest.setVisibility(View.VISIBLE);
            if (childText.getTotal_questions() != null) {
                quesCourseTest.setText("Ques: " + childText.getTotal_questions());
            }

            viewCourseTest.setVisibility(View.VISIBLE);
            minsCourseTest.setVisibility(View.VISIBLE);
            if (childText.getTime_in_mins() != null) {
                minsCourseTest.setText("Mins: " + childText.getTime_in_mins());
            }

            if (courseLockedStatus) {
                mainLL.setOnClickListener(null);
            } else {
                lockedLL.setVisibility(View.GONE);
                setOnClick(childText);
                rl1.setVisibility(View.VISIBLE);
                deleteIV.setVisibility(View.GONE);
                quizStateTV.setVisibility(View.VISIBLE);
                if (currentFileMeta.getIs_paused().equalsIgnoreCase("0")) {
                    if (currentFileMeta.getKey_data() != null) {
                        if (currentFileMeta.getKey_data().equalsIgnoreCase("quiz")) {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                            quizStateTV.setVisibility(View.VISIBLE);
                            quizStateTV.setText("Reattempt");
                        } else {
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                            quizStateTV.setVisibility(View.VISIBLE);
                            if (currentFileMeta.getDisplay_reattempt() != null && !currentFileMeta.getDisplay_reattempt().equalsIgnoreCase("")) {
                                if (currentFileMeta.getDisplay_reattempt().equalsIgnoreCase("1")) {
                                    quizStateTV.setText("Reattempt");
                                } else {
                                    quizStateTV.setText("View Result");
                                }
                            } else {
                                quizStateTV.setText("View Result");
                            }

                        }
                    }
                } else if (currentFileMeta.getIs_paused().equalsIgnoreCase("1")) {
                    quizStateTV.setVisibility(View.VISIBLE);
                    quizStateTV.setText(R.string.resume);
                } else {
                    quizStateTV.setVisibility(View.VISIBLE);
                    quizStateTV.setText(R.string.start);
                }
                SharedPreference.getInstance().putBoolean(Const.RE_ATTEMPT, false);
                deleteIV.setPadding(0, 0, 0, 0);
            }
        } else {
            quesCourseTest.setVisibility(View.VISIBLE);
            if (childText.getPage_count() != null) {
                quesCourseTest.setText("Page count- " + childText.getPage_count());
            }
            if (courseLockedStatus) {
                mainLL.setOnClickListener(null);
            } else {
                rl1.setVisibility(View.VISIBLE);
                lockedLL.setVisibility(View.GONE);
                setOnClick(childText);
                // starting of downloading code snippet
                offlineData offlinedata = getOfflineDataIds(childText.getId(),
                        childText.getFile(), context, singleCourseData.getId());

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
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, currentFileMeta.getFile());
                        }
                        //1. downloading in progress
                        else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                                && offlinedata.getRequestInfo().getProgress() < 100) {

                            downloadIV.setVisibility(View.GONE);
                            deleteIV.setVisibility(View.VISIBLE);
                            downloadprogessPB.setVisibility(View.VISIBLE);
                            downloadprogessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                            messageTV.setText(R.string.download_queued);
                            downloadId = offlinedata.getDownloadid();
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, currentFileMeta.getFile());
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
        }
    }

    @Override
    public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {
        if (downloadId == id) {
            downloadprogessPB.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
            messageTV.setVisibility(View.GONE);
            downloadIV.setVisibility(View.GONE);

            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.downloaded_offline);
            downloadIV.setVisibility(View.VISIBLE);
            downloadIV.setImageResource(R.mipmap.eye_on);
            deleteIV.setVisibility(View.VISIBLE);
            try {
                eMedicozDownloadManager.
                        addOfflineDataIds(currentFileMeta.getId(),
                                URLDecoder.decode(currentFileMeta.getLink(), "UTF-8").split("/")[currentFileMeta.getLink().split("/").length - 1],
                                context,
                                false, true, currentFileMeta.getFile(), requestInfo.getId(),
                                singleCourseData.getId());

                sendViewMessage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (downloadprogessPB.getVisibility() != View.GONE)
                downloadprogessPB.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateProgressUI(Integer value, int status, long id) {

        if (downloadId == id) {
            downloadprogessPB.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
            messageTV.setVisibility(View.GONE);
            downloadIV.setVisibility(View.GONE);
            downloadprogessPB.setVisibility(View.VISIBLE);
            messageTV.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onStartEncoding() {

    }

    @Override
    public void onEncodingFinished() {

    }

    private void networkCallForTestSeries() {
        mProgress = new Progress(context);
        mProgress.setCancelable(false);
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCompleteInfTestSeries(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFileMeta.getLink());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        Gson gson = new Gson();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject data = jsonResponse.optJSONObject(Const.DATA);
                            QuizModel quiz = gson.fromJson(Objects.requireNonNull(data).toString(), QuizModel.class);
                            SharedPreference.getInstance().setCurrentQuiz(quiz);

                            if (!GenericUtils.isEmpty(currentFileMeta.getTest_start_date())) {
                                long millis = getMilliFromDate(currentFileMeta.getTest_start_date());

                                String correctDateFormat = Helper.getFormatedDate(millis);

                                if (System.currentTimeMillis() < millis) {
                                    Helper.newCustomDialog(context,
                                            context.getString(R.string.app_name),
                                            context.getString(R.string.this_test_will_be_available_on) + " " + correctDateFormat,
                                            false,
                                            context.getString(R.string.close),
                                            ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
                                } else {
                                    redirectToResultScreen(quiz);
                                }
                            } else {
                                redirectToResultScreen(quiz);
                            }
                        } else {
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();

            }
        });
    }

    private void redirectToResultScreen(QuizModel quiz) {
        if (quiz.getBasic_info().getSet_type().equals("0")) {
            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
            if (!currentFileMeta.getIs_paused().equalsIgnoreCase("")) {
                if (currentFileMeta.getIs_paused().equals("0")) {
                    if (!GenericUtils.isEmpty(currentFileMeta.getDisplay_reattempt()) && currentFileMeta.getDisplay_reattempt().equalsIgnoreCase("1")) {
                        networkCallForTestSeriesResult(quiz);
                    } else {
                        String testSegmentId = currentFileMeta.isIs_user_attemp();
                        long tsLong = System.currentTimeMillis();
                        if (!GenericUtils.isEmpty(currentFileMeta.getTest_result_date()) && tsLong < (Long.parseLong(currentFileMeta.getTest_result_date()) * 1000)) {
                            Intent resultScreen = new Intent(context, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                            resultScreen.putExtra(Constants.Extras.DATE, currentFileMeta.getTest_result_date());
                            context.startActivity(resultScreen);
                        } else {
                            Intent resultScreen = new Intent(context, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                            resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                            context.startActivity(resultScreen);
                        }
                    }
                } else {
                    long millisEnd = getMilliFromDate(currentFileMeta.getTest_end_date());
                    if (System.currentTimeMillis() < millisEnd) {
                        goToTestBaseActivity(currentFileMeta.getLink());
                    } else {
                        openTestDateExpiredDialog();
                    }
                }
            } else {
                long millisEnd = getMilliFromDate(currentFileMeta.getTest_end_date());
                if (System.currentTimeMillis() < millisEnd) {
                    showPopUp(quiz);
                } else {
                    openTestDateExpiredDialog();
                }
            }
        } else {
            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
            if (lastPosition)
                addCourseStatus(true);

            if (!currentFileMeta.getIs_paused().equalsIgnoreCase("")) {
                if (currentFileMeta.getIs_paused().equals("0")) {
                    networkCallForTestSeriesResult(quiz);
                } else {
                    goToTestBaseActivity(currentFileMeta.getLink());
                }
            } else {
                showPopUp(quiz);
            }
        }
    }

    private void openTestDateExpiredDialog() {
        Helper.newCustomDialog(context,
                context.getString(R.string.app_name),
                context.getString(R.string.test_date_expired),
                false,
                context.getString(R.string.close),
                ContextCompat.getDrawable(context, R.drawable.bg_round_corner_fill_red));
    }

    private void networkCallForTestSeriesResult(final QuizModel quiz) {
        mProgress = new Progress(context);
        mProgress.setCancelable(false);
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCompleteInfTestSeriesResult(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFileMeta.getLink());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        Gson gson = new Gson();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (!testSeriesResultDataArrayList.isEmpty()) {
                                testSeriesResultDataArrayList.clear();
                            }
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                TestSeriesResultData testSeriesResult = gson.fromJson(dataObj.toString(), TestSeriesResultData.class);
                                testSeriesResultDataArrayList.add(testSeriesResult);
                            }
                            showPopupResult(testSeriesResultDataArrayList, quiz);
                        } else {
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();

            }
        });
    }

    private void showPopupResult(ArrayList<TestSeriesResultData> resultData, final QuizModel quizModel) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.dialog_test_reattempt, null, false);
        final Dialog quizBasicInfoDialog = new Dialog(context);
        quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quizBasicInfoDialog.setCanceledOnTouchOutside(false);
        quizBasicInfoDialog.setContentView(v);
        quizBasicInfoDialog.show();

        RecyclerView recyclerView;
        Button continueReattempt;
        TextView dialogTestName;

        recyclerView = v.findViewById(R.id.reattemptDialogRV);
        continueReattempt = v.findViewById(R.id.continueReattempt);
        dialogTestName = v.findViewById(R.id.dialogTestName);
        if (!GenericUtils.isListEmpty(resultData) && resultData.get(0).getTest_series_name() != null) {
            dialogTestName.setText(resultData.get(0).getTest_series_name());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ReattemptDialogAdapter(context, resultData, context, quizBasicInfoDialog));
        continueReattempt.setOnClickListener(view1 -> {
            showPopUp(quizModel);
            quizBasicInfoDialog.dismiss();
        });
    }

    private void showPopUp(final QuizModel quiz) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.popup_basicinfo_quiz, null, false);
        final Dialog quizBasicInfoDialog = new Dialog(context);
        quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quizBasicInfoDialog.setCanceledOnTouchOutside(true);
        quizBasicInfoDialog.setContentView(v);
        quizBasicInfoDialog.show();

        TextView quizTitle;
        TextView quizQuesNumTv;
        TextView quizTimeTV;
        TextView quizCorrectValue;
        TextView quizWrongValue;
        TextView quizTotalMarks;
        TextView reAttempt;
        TextView descriptionTV;
        Button startQuiz;
        Quiz_Basic_Info basicInfo = quiz.getBasic_info();

        quizTitle = v.findViewById(R.id.quizTitleTV);
        descriptionTV = v.findViewById(R.id.descriptionTV);
        quizCorrectValue = v.findViewById(R.id.marksCorrectValueTV);
        quizWrongValue = v.findViewById(R.id.marksWrongValueTV);
        quizTotalMarks = v.findViewById(R.id.marksTextValueTV);
        quizQuesNumTv = v.findViewById(R.id.numQuesValueTV);
        quizTimeTV = v.findViewById(R.id.quizTimeValueTV);
        reAttempt = v.findViewById(R.id.remarksTV);
        startQuiz = v.findViewById(R.id.startQuizBtn);

        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
            startQuiz.setText("Start Test");
        } else {
            startQuiz.setText("Start Quiz");
        }

        if (SharedPreference.getInstance().getBoolean(Const.RE_ATTEMPT)) {
            reAttempt.setVisibility(View.VISIBLE);
        }

        quizTitle.setText(basicInfo.getTest_series_name());
        quizCorrectValue.setText(basicInfo.getMarks_per_question());
        quizWrongValue.setText(basicInfo.getNegative_marking());
        quizQuesNumTv.setText(basicInfo.getTotal_questions());
        quizTotalMarks.setText(basicInfo.getTotal_marks());
        quizTimeTV.setText(basicInfo.getTime_in_mins());

        if (!TextUtils.isEmpty(basicInfo.getDescription())) {
            descriptionTV.setVisibility(View.VISIBLE);
            descriptionTV.setText(basicInfo.getDescription());
        } else {
            descriptionTV.setVisibility(View.GONE);
        }


        startQuiz.setTag(quiz);
        startQuiz.setOnClickListener(view1 -> {
            quizBasicInfoDialog.dismiss();
            if (lastPosition)
                addCourseStatus(true);
            goToTestBaseActivity(currentFileMeta.getLink());
        });

    }

    private void goToTestBaseActivity(String testSeriesId) {
        Intent quizView = new Intent(context, TestBaseActivity.class);
        quizView.putExtra(Const.STATUS, false);
        quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
        context.startActivity(quizView);
    }

    public long getMilliFromDate(String dateFormat) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = formatter.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("Today is ", String.valueOf(date));
        return Objects.requireNonNull(date).getTime();
    }

}
