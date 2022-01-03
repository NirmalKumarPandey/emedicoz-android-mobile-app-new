
package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.activity.TestQuizActionActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.epubear.ePubActivity;
import com.emedicoz.app.modelo.TestSeriesResultData;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse;
import com.emedicoz.app.modelo.liveclass.courses.NotesTestDatum;
import com.emedicoz.app.modelo.liveclass.courses.NotesTestList;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class NoteTestExpandListAdapter extends BaseExpandableListAdapter implements eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass {
    LinearLayout rl1;
    LinearLayout mainLL;
    LinearLayout lockedLL;
    LinearLayout shownLL;
    View dividerView;
    View viewCourseTest;
    TextView fileCountTV;
    TextView quesCourseTest;
    TextView minsCourseTest;
    ImageView fileTypeIV;
    ImageView seeResultIV;
    long downloadId;
    eMedicozDownloadManager.SavedOfflineVideoCallBackForLiveClass savedOfflineListener;
    TextView examPrepTitleTV;
    ImageView emojiSmileIV;
    ImageView expandItemArrow;
    View lineView;
    TextView nameTV;
    TextView desTV;
    TextView testStartDate;
    TextView testEndDate;
    Button statusTV;
    List<NotesTestList> notesTestDataList;
    NotesTestDatum data;
    // child data in format of header title, child title;
    TextView validityTv;
    CardView parentLL;
    RelativeLayout showPdfRL;
    ImageView lockedIV;
    View view;
    LinearLayout dateLL;
    DescriptionResponse descriptionResponse;
    private Activity activity;
    ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;

    public NoteTestExpandListAdapter(Activity activity, DescriptionResponse descriptionResponse, List<NotesTestList> notesTestDataList) {
        this.activity = activity;
        this.descriptionResponse = descriptionResponse;
        this.notesTestDataList = notesTestDataList;
        testSeriesResultDataArrayList = new ArrayList<>();
    }

    @Override
    public NotesTestDatum getChild(int groupPosition, int childPosition) {

        return this.notesTestDataList.get(groupPosition).getData().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        NotesTestDatum childItem = getChild(groupPosition, childPosition);
        LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (childItem.getType().equalsIgnoreCase(Constants.TestType.TEST) ||
                childItem.getType().equalsIgnoreCase(Constants.TestType.Q_BANK)) {

            convertView = layoutInflater.inflate(R.layout.sub_item_notes_test_layout, null);


            desTV = convertView.findViewById(R.id.desTV);
            nameTV = convertView.findViewById(R.id.nameTV);
            statusTV = convertView.findViewById(R.id.statusTV);
            parentLL = convertView.findViewById(R.id.parentLL);
            validityTv = convertView.findViewById(R.id.validityTv);
            lockedIV = convertView.findViewById(R.id.lockedIV);
            dateLL = convertView.findViewById(R.id.dateLL);
            testStartDate = convertView.findViewById(R.id.testStartDate);
            testEndDate = convertView.findViewById(R.id.testEndDate);
            view = convertView.findViewById(R.id.view);
            setTestData(childItem);
        } else if (childItem.getType().equalsIgnoreCase("epub") ||
                childItem.getType().equalsIgnoreCase("pdf")) {

            convertView = layoutInflater.inflate(R.layout.epub_pdf_item_layout, null);

            rl1 = convertView.findViewById(R.id.rl1);

            fileTypeIV = convertView.findViewById(R.id.itemTypeimageIV);
            lockedIV = convertView.findViewById(R.id.locIV);
            mainLL = convertView.findViewById(R.id.submainLL);
            parentLL = convertView.findViewById(R.id.parentLL);
            dividerView = convertView.findViewById(R.id.dividerId);
            seeResultIV = convertView.findViewById(R.id.seeResultIV);
            fileCountTV = convertView.findViewById(R.id.itemCountTV);
            lockedLL = convertView.findViewById(R.id.lockedLL);
            shownLL = convertView.findViewById(R.id.shownLL);
            showPdfRL = convertView.findViewById(R.id.showPdfRL);
            quesCourseTest = convertView.findViewById(R.id.quesCourseTest);
            minsCourseTest = convertView.findViewById(R.id.minsCourseTest);
            viewCourseTest = convertView.findViewById(R.id.viewCourseTest);
            mainLL.setPadding(5, 0, 0, 0);
            convertView.findViewById(R.id.downloadProgessBar).setScaleY(1.5f);
            savedOfflineListener = this;
        }
        if (childItem.getType().equalsIgnoreCase("epub"))
            setEpubData(convertView, childItem);
        else if (childItem.getType().equalsIgnoreCase("pdf"))
            setPdfData(convertView, childItem);
        setLockVisibility(childItem);
        return convertView;
    }

    private void setPdfData(View convertView, NotesTestDatum pdfData) {
        ((TextView) convertView.findViewById(R.id.fileTypeTV)).setText(pdfData.getTitle());
        fileTypeIV.setImageResource(R.mipmap.pdf);
        quesCourseTest.setText(pdfData.getDescription());
        checkData(convertView, Const.PDF, pdfData);
        if (descriptionResponse.getData().getIsPurchased().equals("1") || descriptionResponse.getData().getBasic().getMrp().equals("0")) {
            convertView.findViewById(R.id.showPdfRL).setTag(pdfData);
            convertView.findViewById(R.id.showPdfRL).setOnClickListener(view1 -> {
                singleViewCLick(((NotesTestDatum) view1.getTag()), Const.PDF, null);
//            Helper.openPdfActivity(activity, data.getFileUrl());
            });
        }
        convertView.findViewById(R.id.downloadIV).setTag(pdfData);
        convertView.findViewById(R.id.downloadIV).setOnClickListener(view1 -> {
            NotesTestDatum pdfData1 = (NotesTestDatum) view1.getTag();
            if (descriptionResponse.getData().getIsPurchased().equals("1"))
                startDownload(convertView, pdfData1.getFileUrl(), Const.PDF, pdfData1);
            else {
                if (descriptionResponse.getData().getBasic().getMrp().equals("0"))
                    startDownload(convertView, pdfData1.getFileUrl(), Const.PDF, pdfData1);
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (descriptionResponse.getData().getBasic().getForDams().equals("0"))
                            startDownload(convertView, pdfData1.getFileUrl(), Const.PDF, pdfData1);
                        else
                            Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                    } else {
                        if (descriptionResponse.getData().getBasic().getNonDams().equals("0"))
                            startDownload(convertView, pdfData1.getFileUrl(), Const.PDF, pdfData1);
                        else
                            Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        convertView.findViewById(R.id.deleteIV).setTag(pdfData);
        convertView.findViewById(R.id.deleteIV).setOnClickListener(view1 -> {
            NotesTestDatum pdfData1 = (NotesTestDatum) view1.getTag();
            getDownloadCancelDialog(convertView, pdfData1, activity, Const.PDF, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD);
        });
    }

    private void setEpubData(View convertView, NotesTestDatum epubData) {
        ((TextView) convertView.findViewById(R.id.fileTypeTV)).setText(epubData.getTitle());
        fileTypeIV.setImageResource(R.mipmap.epub_new_course);

        quesCourseTest.setText(epubData.getPageCount() + " pages");
        checkData(convertView, Const.EPUB, epubData);

        if (descriptionResponse.getData().getIsPurchased().equals("1") || descriptionResponse.getData().getBasic().getMrp().equals("0")) {
            convertView.findViewById(R.id.showPdfRL).setTag(epubData);
            convertView.findViewById(R.id.showPdfRL).setOnClickListener(view -> {
                singleViewCLick(((NotesTestDatum) view.getTag()), Const.EPUB, null);
            });
        }
        convertView.findViewById(R.id.downloadIV).setTag(epubData);
        convertView.findViewById(R.id.downloadIV).setOnClickListener(view1 -> {
            NotesTestDatum epubData1 = (NotesTestDatum) view1.getTag();
            if (descriptionResponse.getData().getIsPurchased().equals("1"))
                startDownload(convertView, epubData1.getFileUrl(), Const.EPUB, epubData1);
            else {
                if (descriptionResponse.getData().getBasic().getMrp().equals("0"))
                    startDownload(convertView, epubData1.getFileUrl(), Const.EPUB, epubData1);
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (descriptionResponse.getData().getBasic().getForDams().equals("0"))
                            startDownload(convertView, epubData1.getFileUrl(), Const.EPUB, epubData1);
                        else
                            Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                    } else {
                        if (descriptionResponse.getData().getBasic().getNonDams().equals("0"))
                            startDownload(convertView, epubData1.getFileUrl(), Const.EPUB, epubData1);
                        else
                            Toast.makeText(activity, activity.getResources().getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        convertView.findViewById(R.id.deleteIV).setTag(epubData);
        convertView.findViewById(R.id.deleteIV).setOnClickListener(view1 -> {
            NotesTestDatum epubData1 = (NotesTestDatum) view1.getTag();
            getDownloadCancelDialog(convertView, epubData1, activity, Const.EPUB, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD);
        });
    }

    private void startDownload(View convertView, String fileUrl, String type, NotesTestDatum data) {
        com.emedicoz.app.Model.offlineData offline = getOfflineDataIds(data.getId(), type, activity, data.getId());

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
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.getDownloadid(), savedOfflineListener, type);

            }//when some error occurred
            else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                convertView.findViewById(R.id.messageTV).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pending);
                downloadId = offline.getDownloadid();

                eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.getDownloadid(), savedOfflineListener, type);

            } else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);

                singleViewCLick(data, type, offline);
            }
        }//for new download
        else if (offline == null || offline.getRequestInfo() == null) {
            try {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getId(), fileUrl,
                        Helper.getFileName(fileUrl, data.getTitle(), type), type, data.getId(),
                        downloadid -> {

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
                                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, downloadid, savedOfflineListener, type);
                            } else {
                                convertView.findViewById(R.id.messageTV).setVisibility(View.INVISIBLE);
                                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                                convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                                ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_download);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));
            if (offline.getRequestInfo() == null) {
                Toast.makeText(activity, activity.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                convertView.findViewById(R.id.downloadIV).performClick();
            }
        }
    }

    public void singleViewCLick(NotesTestDatum data, String type, offlineData offlineData) {
        if (offlineData == null) {
            offlineData = getOfflineDataIds(data.getId(), type, activity, data.getId());
        }

        if (offlineData != null && offlineData.getRequestInfo() == null) {
            // Helper.GoToVideoActivity(context, fileMetaData.getLink(), Const.VIDEO_STREAM);
            offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
        }
        if (offlineData != null && offlineData.getRequestInfo() != null &&
                offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
            switch (type) {
                case "pdf":
//                    displayPdf(offlineData.getRequestInfo().getFilePath());
                    Helper.openPdfActivity(activity, offlineData.getLink(),
                            activity.getFilesDir() + "/" + offlineData.getLink());

                    break;
                case "epub":
                    final Intent intent = new Intent(activity, ePubActivity.class);
                    intent.putExtra("filePath", activity.getFilesDir() + "/" + offlineData.getLink());
                    activity.startActivity(intent);

            }
        } else if (type != null && type.equals("pdf"))
            if (!GenericUtils.isEmpty(data.getFileUrl()))
                Helper.GoToWebViewActivity(activity, AES.decrypt(data.getFileUrl()));
//                Helper.openPdfActivity(activity, data.getTitle(), AES.decryptPhp(data.getFileUrl()));
            else
                Toast.makeText(activity, "url is empty", Toast.LENGTH_SHORT).show();


    }

    private void checkData(View convertView, String type, NotesTestDatum data) {
        com.emedicoz.app.Model.offlineData offlinedata = getOfflineDataIds(data.getId(),
                type, activity, data.getId());

        ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_new_course);

        convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
        convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
        convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
        convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);

        if (descriptionResponse.getData().getIsPurchased().equals("1")) {
            setDownloadedData(convertView, offlinedata);
        } else {
            if (descriptionResponse.getData().getBasic().getMrp().equals("0")) {
                setDownloadedData(convertView, offlinedata);
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (descriptionResponse.getData().getBasic().getForDams().equals("0")) {
                        setDownloadedData(convertView, offlinedata);
                    } else {
                        convertView.findViewById(R.id.locIV).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    }
                } else {
                    if (descriptionResponse.getData().getBasic().getNonDams().equals("0")) {
                        setDownloadedData(convertView, offlinedata);
                    } else {
                        convertView.findViewById(R.id.locIV).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void setDownloadedData(View convertView, offlineData offlinedata) {
        convertView.findViewById(R.id.locIV).setVisibility(View.GONE);
        convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
        if (offlinedata != null) {
            if (offlinedata.getRequestInfo() == null)
                offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

            if (offlinedata.getRequestInfo() != null) {
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(offlinedata.getRequestInfo().getProgress() < 100 ? View.VISIBLE : View.GONE);

                //4 conditions to check at the time of intialising the video

                //0. downloading in queue
                if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);
                    downloadId = offlinedata.getDownloadid();
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offlinedata.getDownloadid(), savedOfflineListener, offlinedata.getType());
                }
                //1. downloading in progress
                else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                        && offlinedata.getRequestInfo().getProgress() < 100) {

                    convertView.findViewById(R.id.downloadIV).setVisibility(View.GONE);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_queued);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.VISIBLE);
                    ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlinedata.getRequestInfo().getProgress());
                    downloadId = offlinedata.getDownloadid();
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offlinedata.getDownloadid(), savedOfflineListener, offlinedata.getType());
                }
                //2. download complete
                else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlinedata.getRequestInfo().getProgress() == 100) {
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.downloaded_offline);
                    convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                    ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.eye_on);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);

//                            sendViewMessage();
                }
                //3. downloading paused
                else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED && offlinedata.getRequestInfo().getProgress() < 100) {
                    ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlinedata.getRequestInfo().getProgress());
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                    ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_pause);
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.download_pasued);
                }
                //4. error intrrupted
                else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_ERROR && offlinedata.getRequestInfo().getProgress() < 100) {
                    ((TextView) convertView.findViewById(R.id.messageTV)).setText(R.string.error_in_downloading);
                    convertView.findViewById(R.id.deleteIV).setVisibility(View.VISIBLE);
                    ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(offlinedata.getRequestInfo().getProgress());
                    convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
                    ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_reload);
                }
                convertView.findViewById(R.id.messageTV).setVisibility(offlinedata.getRequestInfo() != null ? View.VISIBLE : View.GONE);
            } else {
                convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
                convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
                convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
                convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            }
        } else if (descriptionResponse.getData().getBasic().getForDams().equalsIgnoreCase("0")) {
            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.locIV).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
            convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
        }
    }

    public void getDownloadCancelDialog(View convertView, NotesTestDatum data, final Activity ctx, final String type, final String title, final String message) {
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
            eMedicozDownloadManager.removeOfflineData(data.getId(), type, ctx, data.getId());

            convertView.findViewById(R.id.downloadIV).setVisibility(View.VISIBLE);
            ((ImageView) convertView.findViewById(R.id.downloadIV)).setImageResource(R.mipmap.download_new_course);

            convertView.findViewById(R.id.downloadProgessBar).setVisibility(View.GONE);
            ((ProgressBar) convertView.findViewById(R.id.downloadProgessBar)).setProgress(0);

            convertView.findViewById(R.id.messageTV).setVisibility(View.GONE);
            convertView.findViewById(R.id.deleteIV).setVisibility(View.GONE);
        });
    }

    private void setTestData(NotesTestDatum data) {
//        data.setDisplay_review_answer("0");
        dateLL.setVisibility(View.VISIBLE);
        nameTV.setText(data.getTestSeriesName());
        String status = "";

        Log.e("setTestData: ", data.getIsPaused());
        if (data.getIsPaused() != null) {
            if (data.getIsPaused().equalsIgnoreCase("1")) {
                status = activity.getString(R.string.resume);
                statusTV.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
            } else if (data.getIsPaused().equalsIgnoreCase("0")) {

                if (!TextUtils.isEmpty(data.getDisplay_review_answer()) && data.getDisplay_review_answer().equals("1")) {
                    status = activity.getString(R.string.review_solution);
                    statusTV.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
                } else if (!TextUtils.isEmpty(data.getDisplay_reattempt()) && data.getDisplay_reattempt().equals("1")) {
                    status = activity.getString(R.string.reattempt);
                    statusTV.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
                } else {
                    status = activity.getString(R.string.result);
                    statusTV.setBackground(activity.getResources().getDrawable(R.drawable.completed_btn_bg));
                }
            } else {
                status = activity.getString(R.string.start);
                statusTV.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
            }
        }


        statusTV.setText(status);
        if (!TextUtils.isEmpty(data.getTestEndDate()) &&
                !data.getTestEndDate().equals("0")) {
            validityTv.setVisibility(View.GONE);
            validityTv.setText(String.format("Valid Till: %s", data.getTestEndDate()));
        } else
            validityTv.setVisibility(View.GONE);

        if (!GenericUtils.isEmpty(data.getTotalQuestions())) {
            desTV.setText(String.format("%s MCQs | %s mins", data.getTotalQuestions(), data.getTimeInMins()));
        }

        setLockVisibility(data);

        Long startDate = getMilliFromDate(data.getTestStartDate());
        Date d = new Date(startDate);
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
        String dateString = f.format(d);

        testStartDate.setText(HtmlCompat.fromHtml("<font color='#CC0000'>Start date: </font>", HtmlCompat.FROM_HTML_MODE_LEGACY) + dateString);

        final Long endDate = getMilliFromDate(data.getTestEndDate());
        Date dEnd = new Date(endDate);
        DateFormat fEnd = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
        String dateStringEnd = fEnd.format(dEnd);

        testEndDate.setText(HtmlCompat.fromHtml("<font color='#CC0000'>End date: </font>", HtmlCompat.FROM_HTML_MODE_LEGACY) + dateStringEnd);

        statusTV.setOnClickListener((View view) -> {
            if (Helper.isConnected(activity)) {
                if (data.getType().equals(Constants.TestType.TEST)) {
                    SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                    if (descriptionResponse.getData().getIsPurchased().equals("1")) {
                        managePurchasedCourseTest(data);
                    } else {
                        if (descriptionResponse.getData().getBasic().getMrp().equals("0"))
                            managePurchasedCourseTest(data);
                        else {
                            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                                if (descriptionResponse.getData().getBasic().getForDams().equals("0"))
                                    managePurchasedCourseTest(data);
                                else
                                    testQuizCourseDialog();
                            } else {
                                if (descriptionResponse.getData().getBasic().getNonDams().equals("0"))
                                    managePurchasedCourseTest(data);
                                else
                                    testQuizCourseDialog();
                            }
                        }
                    }
                } else {
                    SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                    if (descriptionResponse.getData().getIsPurchased().equals("1"))
                        goToTestStartScreen(getTestSeriesObject(data));
                    else if (descriptionResponse.getData().getBasic().getMrp().equals("0"))
                        goToTestStartScreen(getTestSeriesObject(data));
                    else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (descriptionResponse.getData().getBasic().getForDams().equals("0"))
                            goToTestStartScreen(getTestSeriesObject(data));
                        else
                            testQuizCourseDialog();
                    } else {
                        if (descriptionResponse.getData().getBasic().getNonDams().equals("0"))
                            getTestSeriesObject(data);
                        else
                            testQuizCourseDialog();
                    }
                }

            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.please_check_your_internet_connectivity), Toast.LENGTH_SHORT).show();
            }
        });

        lockedIV.setTag(data);
        lockedIV.setOnClickListener(view -> {
            statusTV.performClick();
        });
    }

    private TestSeries getTestSeriesObject(NotesTestDatum data) {
        TestSeries testSeries = new TestSeries();

        testSeries.setIs_paused(data.getIsPaused());
        testSeries.setVideo_based(data.getType());
        testSeries.setDisplay_reattempt(data.getDisplay_reattempt());
        testSeries.setTest_series_id(data.getId());
        testSeries.setTest_series_name(data.getTestSeriesName());
        testSeries.setTotal_questions(data.getTotalQuestions());
        testSeries.setAvg_rating("0");

        return testSeries;
    }


    private void goToTestStartScreen(TestSeries course) {
        String testSeriesId = course.getTest_series_id();
        Log.e("TSI", testSeriesId);
        if (course.getIs_paused().equalsIgnoreCase("")) {
            if (course.getVideo_based().equalsIgnoreCase("1")) {
                goToTestActivityForStart(course, Constants.ResultExtras.VIDEO); // video based start
            } else {
                goToTestActivityForStart(course, Constants.ResultExtras.TEXT_PHOTO); // start
            }

        } else if (course.getIs_paused().equalsIgnoreCase("0")) {
            if (!GenericUtils.isEmpty(course.getDisplay_reattempt())) {
                if (course.getDisplay_reattempt().equalsIgnoreCase("1") && course.getVideo_based().equalsIgnoreCase("1")) {
                    goToTestActivityForComplete(course, Constants.ResultExtras.VIDEO); //video based complete
                } else {
                    goToTestActivityForComplete(course, Constants.ResultExtras.TEXT_PHOTO); // complete
                }
            } else {
                ResultTestSeries resultTestSeries = new ResultTestSeries();
                resultTestSeries.setTestSeriesName(course.getTest_series_name());

                Intent resultScreen = new Intent(activity, QuizActivity.class);
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_BASIC);
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
                resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
//                resultScreen.putExtra(Constants.Extras.SUBJECT_NAME, ((TestQuizActivity) activity).titlefrag);
                activity.startActivity(resultScreen);
            }
        } else {
            if (course.getVideo_based().equalsIgnoreCase("1")) {
                goToTestActivityForPause(course, Constants.ResultExtras.VIDEO); // video based pause
            } else {
                goToTestActivityForPause(course, Constants.ResultExtras.TEXT_PHOTO); // pause
            }
        }
    }

    private void goToTestActivityForStart(TestSeries course, String quizType) {
        Intent quizView = new Intent(activity, TestQuizActionActivity.class);
        quizView.putExtra(Const.STATUS, false);
        quizView.putExtra(Const.TEST_SERIES_ID, course.getTest_series_id());
        quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType);
        quizView.putExtra(Constants.Extras.TITLE_NAME, course.getTest_series_name());
        quizView.putExtra(Constants.Extras.QUES_NUM, course.getTotal_questions());
        quizView.putExtra(Constants.Extras.RATING, course.getAvg_rating());
        quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.START);
        activity.startActivity(quizView);
    }

    private void goToTestActivityForPause(TestSeries course, String quizType) {
        Intent quizView = new Intent(activity, TestQuizActionActivity.class);
        quizView.putExtra(Const.STATUS, false);
        quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType);
        quizView.putExtra(Constants.Extras.TITLE_NAME, course.getTest_series_name());
        quizView.putExtra(Constants.Extras.QUES_NUM, course.getTotal_questions());
        quizView.putExtra(Constants.Extras.RATING, course.getAvg_rating());
        quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.PAUSE);
        quizView.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
        quizView.putExtra(Const.TEST_SERIES_ID, course.getTest_series_id());
        activity.startActivity(quizView);
    }

    private void goToTestActivityForComplete(TestSeries course, String quizType) {
        Intent quizView = new Intent(activity, TestQuizActionActivity.class);
        quizView.putExtra(Const.STATUS, false);
        quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType);
        quizView.putExtra(Constants.Extras.TITLE_NAME, course.getTest_series_name());
        quizView.putExtra(Constants.Extras.QUES_NUM, course.getTotal_questions());
        quizView.putExtra(Constants.Extras.RATING, course.getAvg_rating());
        quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.COMPLETE);
        quizView.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
        quizView.putExtra(Const.TEST_SERIES_ID, course.getTest_series_id());
        activity.startActivity(quizView);
    }

    private void setLockVisibility(NotesTestDatum data) {
        if (descriptionResponse.getData().getIsPurchased().equalsIgnoreCase("0")) {
            if (descriptionResponse.getData().getBasic().getMrp().equals("0")) {
                if (statusTV != null)
                    statusTV.setVisibility(View.VISIBLE);
                lockedIV.setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (descriptionResponse.getData().getBasic().getForDams().equals("0")) {
                        if (statusTV != null)
                            statusTV.setVisibility(View.VISIBLE);
                        lockedIV.setVisibility(View.GONE);
                    } else {
                        setVisibility(data);
                    }
                } else {
                    if (descriptionResponse.getData().getBasic().getNonDams().equals("0")) {
                        if (statusTV != null)
                            statusTV.setVisibility(View.VISIBLE);
                        lockedIV.setVisibility(View.GONE);
                    } else {
                        setVisibility(data);
                    }
                }
            }
        } else {
            if (statusTV != null)
                statusTV.setVisibility(View.VISIBLE);
            lockedIV.setVisibility(View.GONE);
        }

        if (data.getType().equalsIgnoreCase("epub") || data.getType().equalsIgnoreCase("pdf"))
            return;
        long startMillis = getMilliFromDate(data.getTestStartDate());
        long endMillis = getMilliFromDate(data.getTestEndDate());
        Log.e("MILIS : ", String.valueOf(startMillis));

        long currentTimeStamp = System.currentTimeMillis();
        if (currentTimeStamp < startMillis) {
            lockedIV.setVisibility(View.VISIBLE);
            statusTV.setVisibility(View.GONE);
            return;
        } else {
            statusTV.setVisibility(View.VISIBLE);
            lockedIV.setVisibility(View.GONE);
        }
        if (currentTimeStamp > endMillis) {
            if (data.getIsPaused().equalsIgnoreCase("0")) {
                statusTV.setVisibility(View.VISIBLE);
                lockedIV.setVisibility(View.GONE);
            } else {
                lockedIV.setVisibility(View.VISIBLE);
                statusTV.setVisibility(View.GONE);
            }
        } else {
            statusTV.setVisibility(View.VISIBLE);
            lockedIV.setVisibility(View.GONE);
        }
    }

    private void setVisibility(NotesTestDatum data) {
        if (data != null && data.getIsLocked() == null || data.getIsLocked().equalsIgnoreCase("0")) {
            lockedIV.setVisibility(View.VISIBLE);
            if (statusTV != null)
                statusTV.setVisibility(View.GONE);
        } else {
            if (statusTV != null)
                statusTV.setVisibility(View.VISIBLE);
            lockedIV.setVisibility(View.GONE);
        }
    }

    private void managePurchasedCourseTest(NotesTestDatum data) {
        if (!GenericUtils.isEmpty(data.getKeyData()) && data.getKeyData().equalsIgnoreCase(Constants.TestType.QUIZ))
            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
        else
            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);

        if (data.getIsPaused() != null) {
            if (data.getIsPaused().equalsIgnoreCase("")) {

                long millis = getMilliFromDate(data.getTestStartDate());
                String correctDateFormat = Helper.getFormatedDate(millis);
                Log.e("Current time in AM/PM: ", correctDateFormat);

                if (System.currentTimeMillis() < millis) {
                    Helper.newCustomDialog(activity,
                            activity.getString(R.string.app_name),
                            activity.getString(R.string.this_test_will_be_available_on) + " " + correctDateFormat,
                            false,
                            activity.getString(R.string.close),
                            ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red));
                } else {
                    if (getMilliFromDate(data.getTestEndDate()) < System.currentTimeMillis()) {
                        Helper.newCustomDialog(activity,
                                activity.getString(R.string.app_name),
                                activity.getString(R.string.test_date_expired),
                                false,
                                activity.getString(R.string.close),
                                ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red));
                    } else {
                   /* if (course.getVideo_based().equalsIgnoreCase("1")) {
                        Intent quizView = new Intent(context, VideoTestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Const.TEST_SERIES_ID, data.getId());
                        context.startActivity(quizView);
                    } else {*/
                        Intent quizView = new Intent(activity, TestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Const.TEST_SERIES_ID, data.getId());
                        activity.startActivity(quizView);

                    /*if (context instanceof NewTestQuizActivity) {
                        ((NewTestQuizActivity) context).lastPos = position;
                    }*/
                    }
                }
            } else if (data.getIsPaused().equals("1")) {
                Intent quizView = new Intent(activity, TestBaseActivity.class);
                quizView.putExtra(Const.STATUS, false);
                quizView.putExtra(Const.TEST_SERIES_ID, data.getId());
                activity.startActivity(quizView);
            } else {
                if (!TextUtils.isEmpty(data.getDisplay_review_answer()) && data.getDisplay_review_answer().equals("1")) {
                    Intent intent = new Intent(activity, ViewSolutionWithTabNew.class);
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.getSegId());
                    intent.putExtra(Constants.Extras.NAME, data.getTestSeriesName());
                    activity.startActivity(intent);

                } else if (!TextUtils.isEmpty(data.getDisplay_reattempt()) && data.getDisplay_reattempt().equals("1")) {
                    /**
                     * This network call will be used with the check of reattempt
                     * if its 1 then this networl call will be used
                     * but currently we are not getting any check that's why it's commented
                     */
                    networkCallForTestSeriesResult(data);
                } else {
                    if (!GenericUtils.isEmpty(data.getTestResultDate())) {
                        long tsLong = System.currentTimeMillis();
                        if (tsLong < (Long.parseLong(data.getTestResultDate()) * 1000)) {
                            Intent resultScreen = new Intent(activity, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                            resultScreen.putExtra(Constants.Extras.DATE, data.getTestResultDate());
                            activity.startActivity(resultScreen);
                        } else {
                            goToResultScreen(data);
                        }
                    } else {
                        goToResultScreen(data);
                    }
                }
            }
        }

    }

    private void goToResultScreen(NotesTestDatum data) {

        Intent resultScreen = new Intent(activity, QuizActivity.class);
        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.getSegId());
        activity.startActivity(resultScreen);
    }

    private void networkCallForTestSeriesResult(final NotesTestDatum notesTestDatum) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mprogress;
        mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCompleteInfTestSeriesResult(SharedPreference.getInstance().getLoggedInUser().getId(),
                notesTestDatum.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Gson gson = new Gson();

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (!testSeriesResultDataArrayList.isEmpty()) {
                                testSeriesResultDataArrayList.clear();
                            }
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                com.emedicoz.app.modelo.TestSeriesResultData testSeriesResult = gson.fromJson(dataObj.toString(), com.emedicoz.app.modelo.TestSeriesResultData.class);
                                testSeriesResultDataArrayList.add(testSeriesResult);
                            }
                            showPopupResult(testSeriesResultDataArrayList, notesTestDatum);

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
                Helper.showExceptionMsg(activity, t);

            }
        });
    }

    private void showPopupResult(ArrayList<com.emedicoz.app.modelo.TestSeriesResultData> resultData, final NotesTestDatum course) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.dialog_test_reattempt, null, false);
        final Dialog quizBasicInfoDialog = new Dialog(activity);
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
        if (resultData != null && resultData.get(0).getTest_series_name() != null) {
            dialogTestName.setText(resultData.get(0).getTest_series_name());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new ReattemptDialogAdapter(activity, resultData, activity, quizBasicInfoDialog));
        continueReattempt.setOnClickListener((View view) -> {
            Intent quizView = new Intent(activity, TestBaseActivity.class);
            quizView.putExtra(Const.STATUS, false);
            quizView.putExtra(Const.TEST_SERIES_ID, course.getId());
            quizView.putExtra(Constants.Extras.NAME, course.getTestSeriesName());
            activity.startActivity(quizView);
            quizBasicInfoDialog.dismiss();
        });
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

    private void testQuizCourseDialog() {
        View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.you_have_to_buy_this_course_to_attempt_test));
        Space space;
        Button btnCancel;
        Button btnSubmit;

        space = v.findViewById(R.id.space);
        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        space.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnSubmit.setText(activity.getString(R.string.enroll));


        btnSubmit.setOnClickListener(view1 -> {
            Helper.dismissDialog();
            Helper.goToCourseInvoiceScreen(activity, getData(descriptionResponse));
        });
    }

    public SingleCourseData getData(DescriptionResponse descriptionResponse) {
        SingleCourseData singleCourseData = new SingleCourseData();
        singleCourseData.setCourse_type(descriptionResponse.getData().getBasic().getCourseType());
        singleCourseData.setFor_dams(descriptionResponse.getData().getBasic().getForDams());
        singleCourseData.setNon_dams(descriptionResponse.getData().getBasic().getNonDams());
        singleCourseData.setMrp(descriptionResponse.getData().getBasic().getMrp());
        singleCourseData.setId(descriptionResponse.getData().getBasic().getId());
        singleCourseData.setCover_image(descriptionResponse.getData().getBasic().getCoverImage());
        singleCourseData.setTitle(descriptionResponse.getData().getBasic().getTitle());
        singleCourseData.setLearner(descriptionResponse.getData().getBasic().getLearner());
        singleCourseData.setRating(descriptionResponse.getData().getBasic().getRating());
        singleCourseData.setGst_include(descriptionResponse.getData().getBasic().getGstInclude());
        if (!GenericUtils.isEmpty(descriptionResponse.getData().getBasic().getGst()))
            singleCourseData.setGst(descriptionResponse.getData().getBasic().getGst());
        else
            singleCourseData.setGst("18");
        if (!GenericUtils.isEmpty(descriptionResponse.getData().getBasic().getPointsConversionRate()))
            singleCourseData.setPoints_conversion_rate(descriptionResponse.getData().getBasic().getPointsConversionRate());
        else
            singleCourseData.setPoints_conversion_rate("100");
        return singleCourseData;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.notesTestDataList.get(groupPosition).getData().size();
    }

    @Override
    public NotesTestList getGroup(int groupPosition) {
        return this.notesTestDataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.notesTestDataList.size();
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

        if (isExpanded) {
            expandItemArrow.setRotation(180);
            lineView.setVisibility(View.GONE);

        } else {
            expandItemArrow.setRotation(0);
            lineView.setVisibility(View.VISIBLE);
        }

        setColorOfSmiely(groupPosition, emojiSmileIV);
        if (!GenericUtils.isEmpty(getGroup(groupPosition).getImageIcon()))
            Glide.with(activity).load(getGroup(groupPosition).getImageIcon()).into(emojiSmileIV);

        return convertView;
    }

    private void setColorOfSmiely(int groupPosition, ImageView emojiSmileIV) {

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
                        false, true, data.getType(), requestInfo.getId(), data.getId());
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
