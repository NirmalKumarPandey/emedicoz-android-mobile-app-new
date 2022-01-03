package com.emedicoz.app.utilso.offlinedata;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesData;
import com.emedicoz.app.recordedCourses.model.detaildatavideo.VideoItemData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.hlsdownloader.crypto.PlaylistDownloader;
import com.google.gson.JsonObject;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cbc-03 on 12/18/17.
 */

public class eMedicozDownloadManager {

    public static Fetch fetch;
    public static eMedicozDownloadManager eMedicozdownloadmanager;
    public static LinkedHashMap<Long, View> downloadRequests = new LinkedHashMap<>();
    Request request;

    public static eMedicozDownloadManager getInstance() {
        if (eMedicozdownloadmanager == null) {
//            fetch = getFetchInstance();
//            fetch.setConcurrentDownloadsLimit(5);
            return new eMedicozDownloadManager();
        } else
            return eMedicozdownloadmanager;
    }

    public static Fetch getFetchInstance() {
        if (fetch == null || !fetch.isValid())
            fetch = Fetch.newInstance(eMedicozApp.getAppContext());
        return fetch;
    }

    public static void removeOfflineData(String id, String type, Activity activity, String secondaryid) {
        ArrayList<offlineData> offlineVideoIds = new ArrayList<>();
        if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS) != null) {
            offlineVideoIds = (ArrayList<offlineData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);
            User user = SharedPreference.getInstance().getLoggedInUser();
            for (offlineData offlinedata : offlineVideoIds) {
                if (offlinedata.getId() != null && offlinedata.getType() != null) {
                    if (((Helper.count(offlinedata.getId(), '_') == 4 &&
                            offlinedata.getId().equalsIgnoreCase(user.getId() + "_" + secondaryid + "_" + id + "_" + type
                                    + "_" + Helper.getStreamId(user)))
                            || offlinedata.getId().equalsIgnoreCase(user.getId() + "_" + secondaryid + "_" + id + "_" + type))
                            && offlinedata.getType().equalsIgnoreCase(type)) {

                        if (offlinedata.getRequestInfo() == null)
                            offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));
                        if (offlinedata.getRequestInfo() != null) {
                            eMedicozDownloadManager.getFetchInstance().remove(offlinedata.getDownloadid());
                        } else {
                            File file = new File(activity.getFilesDir().toString() + "/" + offlinedata.getLink());
                            if (file.exists())
                                file.delete();
                        }
                        offlineVideoIds.remove(offlinedata);
                        break;
                    }
                }
            }
        }
        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_DOWNLOADEDIDS, offlineVideoIds);
    }

    public static void removeOfflineDataByType(String type, Activity activity) {
        ArrayList<offlineData> offlineVideoIds = new ArrayList<>();
        if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS) != null) {
            offlineVideoIds = (ArrayList<offlineData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);

            for (offlineData offlinedata : offlineVideoIds) {
                if (offlinedata.getType().equalsIgnoreCase(type)) {
                    if (offlinedata.getRequestInfo() == null)
                        offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));
                    if (offlinedata.getRequestInfo() != null) {
                        eMedicozDownloadManager.getFetchInstance().remove(offlinedata.getDownloadid());
                    } else {
                        File file = new File(activity.getFilesDir().toString() + "/" + offlinedata.getLink());
                        if (file.exists())
                            file.delete();
                    }
                    offlineVideoIds.remove(offlinedata);
                    break;
                }
            }
        }
        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_DOWNLOADEDIDS, offlineVideoIds);
    }

    public static boolean removeOfflineDataByCourse(String courseId, Activity activity) {
        ArrayList<offlineData> offlineCourseIds = new ArrayList<>();
        int flag = 0;
        if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS) != null) {
            offlineCourseIds = (ArrayList<offlineData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);

            for (offlineData offlinedata : offlineCourseIds) {
                if (offlinedata.getId().split("_")[1].equalsIgnoreCase(courseId)) {
                    if (offlinedata.getRequestInfo() == null)
                        offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));
                    if (offlinedata.getRequestInfo() != null) {
                        eMedicozDownloadManager.getFetchInstance().remove(offlinedata.getDownloadid());
                    } else {
                        File file = new File(activity.getFilesDir().toString() + "/" + offlinedata.getLink());
                        if (file.exists())
                            file.delete();
                    }
                    offlineCourseIds.remove(offlinedata);
                    flag = 1;
                    break;
                }
            }

        }
        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_DOWNLOADEDIDS, offlineCourseIds);
        return (flag == 1);
    }

    public static boolean getOfflineDataStatusByCourse(String courseId, Activity activity) {
        ArrayList<offlineData> offlineCourseIds;
        int flag = 0;
        if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS) != null) {
            offlineCourseIds = (ArrayList<offlineData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);

            for (offlineData offlinedata : offlineCourseIds) {
                if (offlinedata.getId().split("_")[1].equalsIgnoreCase(courseId)) {
//                    if (offlinedata.getRequestInfo() == null)
//                        offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));
//                    if (offlinedata.getRequestInfo() != null) {
//                        eMedicozDownloadManager.getFetchInstance().remove(offlinedata.getDownloadid());
//                    }
//                    offlineCourseIds.remove(offlinedata);
                    flag = 1;
                    break;
                }
            }

        }
//        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_DOWNLOADEDIDS, offlineCourseIds);
        return (flag == 1);
    }

    public static void addOfflineDataIds(String id, String name, Activity activity, boolean isdownloadinprogress,
                                         boolean isdownloadcomplete, String type, long downloadid, String secondaryid) {
        int flag = 0;
        ArrayList<offlineData> offlineVideoIds = new ArrayList<>();
        if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS) != null) {
            offlineVideoIds = (ArrayList<offlineData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);

            for (offlineData offlinedata : offlineVideoIds) {
                if (offlinedata.getId() != null && offlinedata.getType() != null &&
                        offlinedata.getId().equalsIgnoreCase(SharedPreference.getInstance().getLoggedInUser().getId() + "_" + secondaryid + "_" + id + "_" + type
                                + "_" + Helper.getStreamId(SharedPreference.getInstance().getLoggedInUser())) && offlinedata.getType().equalsIgnoreCase(type)) {

                    if (getFetchInstance().get(downloadid) != null)
                        offlinedata.setRequestInfo(getFetchInstance().get(downloadid));
                    else
                        offlinedata.setRequestInfo(new RequestInfo(0, Fetch.STATUS_DONE, "", "", 100,
                                0, 0, 0, new ArrayList<>(), 0));
                    flag = 1;
                    break;
                }
            }
        }
        if (flag == 0) {
            Log.e("Offline", "downloaded file " + name);
            offlineVideoIds.add(new offlineData(SharedPreference.getInstance().getLoggedInUser().getId() + "_" + secondaryid + "_" + id + "_" + type
                    + "_" + Helper.getStreamId(SharedPreference.getInstance().getLoggedInUser()), name, isdownloadinprogress, isdownloadcomplete, type, downloadid));
        }
        Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_DOWNLOADEDIDS, offlineVideoIds);
    }

    public static offlineData getOfflineDataIds(String id, String type, Activity activity, String secondaryid) {
        ArrayList<offlineData> offlineVideoIds;
        if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS) != null) {
            offlineVideoIds = (ArrayList<offlineData>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);
            User user = SharedPreference.getInstance().getLoggedInUser();

            for (offlineData offlinedata : offlineVideoIds) {
                if (Helper.count(offlinedata.getId(), '_') == 4) {
                    if (offlinedata.getId().equalsIgnoreCase(user.getId() + "_" + secondaryid + "_" + id + "_" + type
                            + "_" + Helper.getStreamId(user)) && offlinedata.getType().equalsIgnoreCase(type))
                        return setRequestInfo(offlinedata);

                } else if (offlinedata.getId().equalsIgnoreCase(user.getId() + "_" + secondaryid + "_" + id + "_" + type)
                        && offlinedata.getType().equalsIgnoreCase(type))
                    return setRequestInfo(offlinedata);
            }
        }
        return null;
    }

    private static offlineData setRequestInfo(offlineData offlinedata) {
        if (offlinedata.getDownloadid() == Constants.MIGRATED_DOWNLOAD_ID /*|| ifFileExists(activity, offlinedata.getLink())*/)
            offlinedata.setRequestInfo(new RequestInfo(0, Fetch.STATUS_DONE, "", "", 100,
                    0, 0, 0, new ArrayList<>(), 0));
        return offlinedata;
    }

    private static boolean ifFileExists(Activity activity, String link) {
        File file = new File(activity.getFilesDir().toString() + "/" + link);
        return (file.exists());
    }

    public static ArrayList<offlineData> getAllOfflineData(Context context) {
        ArrayList<offlineData> finalList = new ArrayList<>();
        ArrayList<offlineData> tempList = (ArrayList<offlineData>) Helper.getStorageInstance(context).getRecordObject(Const.OFFLINE_DOWNLOADEDIDS);
        if (!GenericUtils.isListEmpty(tempList)) {
            for (offlineData temp : tempList) {
                User user = SharedPreference.getInstance().getLoggedInUser();
                if (temp.getId() != null && user != null) {
                    if (Helper.count(temp.getId(), '_') == 4) {
                        if (temp.getId().contains(user.getId() + "_")
                                && temp.getId().contains("_" + Helper.getStreamId(user)))
                            finalList.add(temp);

                    } else if (temp.getId().contains(user.getId() + "_"))
                        finalList.add(temp);
                }
            }
        }
        return finalList;
    }

    public static void getDownloadableLinkFromAPI(String fileUrl, VideoLinkListener videoLinkListener) {

        Context appContext = eMedicozApp.getAppContext();
        User user = SharedPreference.getInstance().getLoggedInUser();
        LoginApiInterface apiInterface = ApiClient.createService(LoginApiInterface.class);
        Call<JsonObject> response1 = apiInterface.getDownloadableLink(user.getId(), fileUrl);
        response1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            videoLinkListener.onUrlReceived(GenericUtils.getJsonArray(jsonResponse));
                        } else {
                            videoLinkListener.onUrlReceived(null);
                            RetrofitResponse.getApiData(appContext, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        videoLinkListener.onUrlReceived(null);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(appContext, t);
                videoLinkListener.onUrlReceived(null);
            }
        });
    }


    public long createNewDownloadRequest(Activity activity, String id, String url, String link, String type, String secondaryId) {
        request = new Request(url,
                activity.getFilesDir().toString(),
                link);
        long downloadid = getFetchInstance().enqueue(request);
        RequestInfo requestInfo = getFetchInstance().get(downloadid);
        if (downloadid != -1 || requestInfo != null) {

            if (Helper.getAvailableInternalMemorySize() - (requestInfo.getFileSize() * 2) > 0) {
                addOfflineDataIds(id, link, activity, true,
                        false, type, downloadid, secondaryId);
                return downloadid;
            } else {
                getFetchInstance().remove(downloadid);
                Toast.makeText(activity, "InSufficient Storage space", Toast.LENGTH_SHORT).show();
                return 0;
            }
        } else {
            if (fetch != null) fetch.release();
            Toast.makeText(activity, "Something Went wrong please try again!", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }


/*    public long createNewDownloadRequest(Activity activity, String id, String url, String link, String type, String secondaryId) {

        long downloadid = 0;

        try {
            if (GenericUtils.isEmpty(url)) return 0;
            String fileName = getFileNameIfExists(activity, url);

            Request request = new Request(url, activity.getFilesDir().toString(),
                    GenericUtils.isEmpty(fileName) ? link : fileName);
            downloadid = getFetchInstance().enqueue(request);
            RequestInfo requestInfo = getFetchInstance().get(downloadid);

            if (downloadid != -1 || requestInfo != null) {
                if (Helper.getAvailableInternalMemorySize() - (Objects.requireNonNull(requestInfo).getFileSize() * 2) > 0) {
                    addOfflineDataIds(id, link, activity, true, false, type, downloadid, secondaryId);
                } else {
                    if (new File(requestInfo.getFilePath()).exists()) {
                        downloadid = Constants.MIGRATED_DOWNLOAD_ID;
                        addOfflineDataIds(id, fileName, activity, false, true, type, downloadid, secondaryId);
                    } else {
                        downloadid = 0;
                        Toast.makeText(activity, "InSufficient Storage space", Toast.LENGTH_SHORT).show();
                    }
                    getFetchInstance().remove(downloadid);
                }
            } else {
                fileName = link;
                if (!GenericUtils.isEmpty(fileName)) {
                    downloadid = Constants.MIGRATED_DOWNLOAD_ID;
                    addOfflineDataIds(id, fileName, activity, false, true, type, downloadid, secondaryId);
                } else {
//                Toast.makeText(activity, "Something Went wrong please try again!", Toast.LENGTH_SHORT).show();
                    downloadid = 0;
                }
            }
        } catch (Exception e) {
        }

        if (fetch != null) fetch.release();
        return downloadid;
    }*/

    public long downloadVideoFromHLS(Activity activity, String id, String url, String link, String type, String secondaryId) {

        String fileName = getFileNameIfExists(activity, url);
        if (GenericUtils.isEmpty(fileName)) fileName = link;

        PlaylistDownloader downloader;
        try {
            downloader = new PlaylistDownloader(url, null);
            downloader.download(activity.getFilesDir().toString() + fileName);

            long downloadid = Constants.MIGRATED_DOWNLOAD_ID;
            addOfflineDataIds(id, fileName, activity, false, true, type, downloadid, secondaryId);

            return downloadid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void linkDownloadedFile(Activity activity, String id, String url, String type) {
        String fileName = ifFileDownloaded(activity, url);

        if (!GenericUtils.isEmpty(fileName)) {
            addOfflineDataIds(id, fileName, activity, false, true,
                    type, Constants.MIGRATED_DOWNLOAD_ID, id);
        }
    }

    public void showQualitySelectionPopup(Activity activity, String id, final String url,
                                          String link, String type, String secondaryId, DownloadAddedListener listener) {
        if (GenericUtils.isEmpty(url)) {
            Toast.makeText(activity, "url is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        listener.onDownloadAdded(createNewDownloadRequest(activity, id, AES.decrypt(url), link, type, secondaryId));

//        if (type.equals(Const.VIDEOS)) {
//            //region code for multi-bitrate video download
//            getDownloadableLinkFromAPI(/*AES.decryptPhp(url)*/ url, urls -> {
//
//                if (urls != null && urls.length() > 0) {
//                    try {
//                        for (int i = 0; i < urls.length(); i++)
//                            urls.put(i, (AES.decrypt(url)));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    View sheetView = Helper.openBottomSheetDialog(activity);
//
//                    TextView size720, size480, size360, size240;
//                    final RadioButton radio1, radio2, radio3, radio4;
//                    RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3, relativeLayout4;
//                    Button downloadVideoBtn;
//
//                    size720 = sheetView.findViewById(R.id.size720);
//                    size480 = sheetView.findViewById(R.id.size480);
//                    size360 = sheetView.findViewById(R.id.size360);
//                    size240 = sheetView.findViewById(R.id.size240);
//
//                    radio1 = sheetView.findViewById(R.id.radio1);
//                    radio2 = sheetView.findViewById(R.id.radio2);
//                    radio3 = sheetView.findViewById(R.id.radio3);
//                    radio4 = sheetView.findViewById(R.id.radio4);
//
//                    relativeLayout1 = sheetView.findViewById(R.id.relativeLayout1);
//                    relativeLayout2 = sheetView.findViewById(R.id.relativeLayout2);
//                    relativeLayout3 = sheetView.findViewById(R.id.relativeLayout3);
//                    relativeLayout4 = sheetView.findViewById(R.id.relativeLayout4);
//                    downloadVideoBtn = sheetView.findViewById(R.id.downloadVideoBtn);
//                   /* for (int i = 0; i < decryptedUrl.size(); i++) {
//                        if (decryptedUrl.get(i).contains("720out_put")) {
//                            size720.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
//                            url720 = data.getEnc_url().getFiles().get(i).getUrl();
//                        } else if (decryptedUrl.get(i).contains("480out_put")) {
//                            size480.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
//                            url480 = data.getEnc_url().getFiles().get(i).getUrl();
//                        } else if (decryptedUrl.get(i).contains("360out_put")) {
//                            size360.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
//                            url360 = data.getEnc_url().getFiles().get(i).getUrl();
//                        } else if (decryptedUrl.get(i).contains("240out_put")) {
//                            size240.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
//                            url240 = data.getEnc_url().getFiles().get(i).getUrl();
//                        }
//                    }*/
//
//                    radio3.setChecked(true);
//
//                    switch (urls.length()) {
//                        case 1:
//                            relativeLayout1.setVisibility(View.VISIBLE);
//                            relativeLayout2.setVisibility(View.GONE);
//                            relativeLayout3.setVisibility(View.GONE);
//                            relativeLayout4.setVisibility(View.GONE);
//                            break;
//                        case 2:
//                            relativeLayout1.setVisibility(View.VISIBLE);
//                            relativeLayout2.setVisibility(View.VISIBLE);
//                            relativeLayout3.setVisibility(View.GONE);
//                            relativeLayout4.setVisibility(View.GONE);
//                            break;
//                        case 3:
//                            relativeLayout1.setVisibility(View.VISIBLE);
//                            relativeLayout2.setVisibility(View.VISIBLE);
//                            relativeLayout3.setVisibility(View.VISIBLE);
//                            relativeLayout4.setVisibility(View.GONE);
//                            break;
//                        case 4:
//                            relativeLayout1.setVisibility(View.VISIBLE);
//                            relativeLayout2.setVisibility(View.VISIBLE);
//                            relativeLayout3.setVisibility(View.VISIBLE);
//                            relativeLayout4.setVisibility(View.VISIBLE);
//                            break;
//                    }
//                    relativeLayout1.setOnClickListener((View view) -> {
//                        radio1.setChecked(true);
//                        radio2.setChecked(false);
//                        radio3.setChecked(false);
//                        radio4.setChecked(false);
//
//                    });
//
//                    relativeLayout2.setOnClickListener((View view) -> {
//                        radio2.setChecked(true);
//                        radio1.setChecked(false);
//                        radio3.setChecked(false);
//                        radio4.setChecked(false);
//
//                    });
//
//                    relativeLayout3.setOnClickListener((View view) -> {
//                        radio3.setChecked(true);
//                        radio2.setChecked(false);
//                        radio1.setChecked(false);
//                        radio4.setChecked(false);
//
//                    });
//
//                    relativeLayout4.setOnClickListener((View view) -> {
//                        radio4.setChecked(true);
//                        radio2.setChecked(false);
//                        radio3.setChecked(false);
//                        radio1.setChecked(false);
//                    });
//
//                    org.json.JSONArray finalUrls = urls;
//                    downloadVideoBtn.setOnClickListener((View view) -> {
//                        if (radio1.isChecked()) {
//                            Helper.dismissBottonSheetDialog();
//                            listener.onDownloadAdded(createNewDownloadRequest(activity, id, finalUrls.optString(0), link, type, secondaryId));
//
//                        } else if (radio2.isChecked()) {
//                            Helper.dismissBottonSheetDialog();
//                            listener.onDownloadAdded(createNewDownloadRequest(activity, id, finalUrls.optString(1), link, type, secondaryId));
//
//                        } else if (radio3.isChecked()) {
//                            Helper.dismissBottonSheetDialog();
//                            listener.onDownloadAdded(createNewDownloadRequest(activity, id, finalUrls.optString(2), link, type, secondaryId));
//
//                        } else if (radio4.isChecked()) {
//                            Helper.dismissBottonSheetDialog();
//                            listener.onDownloadAdded(createNewDownloadRequest(activity, id, finalUrls.optString(3), link, type, secondaryId));
//                        }
//                    });
//                } else {
//                    listener.onDownloadAdded(createNewDownloadRequest(activity, id, AES.decrypt(url), link, type, secondaryId));
//                }
//            });
//            //endregion
//        } else
//            listener.onDownloadAdded(createNewDownloadRequest(activity, id, AES.decrypt(url), link, type, secondaryId));

    }

    private String ifFileDownloaded(Activity activity, String url) {
        String fileName = url.contains("http") ? url.substring(url.lastIndexOf("/") + 1) : url;
        String filePath = activity.getFilesDir().toString() + fileName;
        File file = new File(filePath);
        if (file.exists())
            return fileName;
        return "";
    }

    private String getFileNameIfExists(Activity activity, String url) {
        try {
            String fileName = /*SharedPreference.getInstance().getLoggedInUser().getId() + "_" +*/ Objects.requireNonNull(url).substring(url.lastIndexOf("/") + 1);
            File file = new File(new Request(url, activity.getFilesDir().toString(), fileName).getFilePath());
            if (file.exists()) {
                return fileName;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public void downloadProgressUpdate(final long downloadid,
                                       final SavedOfflineVideoCallBack savedofflineListener, final String type) {


        FetchListener fetchListener = (id, status, progress, downloadedBytes, fileSize, error) -> {
            Log.e("onUpdate", "progress: " + progress + " status: " + status + " id: " + id);

            if ((downloadid == id && status == Fetch.STATUS_DOWNLOADING) ||
                    (downloadid == id && status == Fetch.STATUS_QUEUED)
                    || downloadid == id && status == Fetch.STATUS_REMOVED
                    || downloadid == id && status == Fetch.STATUS_ERROR) {
                savedofflineListener.updateProgressUI(progress, status, id);

            } else if (downloadid == id && status == Fetch.STATUS_DONE) {
                final RequestInfo RI = getFetchInstance().get(downloadid);
                savedofflineListener.updateUIForDownloadedVideo(getFetchInstance().get(downloadid), id);

            } else if (error != Fetch.NO_ERROR) {
                //An error occurred

                if (error == Fetch.ERROR_HTTP_NOT_FOUND) {
                    //handle error
                }

            }
        };
        getFetchInstance().addFetchListener(fetchListener);
    }

    public void downloadProgressUpdate(View convertView, final long downloadid,
                                       final SavedOfflineVideoCallBackForLiveClass savedofflineListener, final String type) {
        eMedicozDownloadManager.downloadRequests.put(downloadid, convertView);

        FetchListener fetchListener = (id, status, progress, downloadedBytes, fileSize, error) -> {
            Log.e("onUpdate", "progress: " + progress + " status: " + status + " id: " + id);
            if (status == Fetch.STATUS_DOWNLOADING
                    || status == Fetch.STATUS_QUEUED
                    || status == Fetch.STATUS_REMOVED
                    || status == Fetch.STATUS_ERROR) {
                savedofflineListener.updateProgressUI(eMedicozDownloadManager.downloadRequests.get(id),
                        progress, status, id);

            } else if (status == Fetch.STATUS_DONE) {
                final RequestInfo RI = getFetchInstance().get(id);
                savedofflineListener.updateUIForDownloadedVideo(eMedicozDownloadManager.downloadRequests.get(id),
                        getFetchInstance().get(id), id);

            } else if (error != Fetch.NO_ERROR) {
                //An error occurred

                if (error == Fetch.ERROR_HTTP_NOT_FOUND) {
                    //handle error
                }

            }
        };
        getFetchInstance().addFetchListener(fetchListener);
    }

    public void downloadProgressUpdate(View convertView, final long downloadid, VideoItemData data,
                                       final SaveOfflineVideoListener savedofflineListener, final String type) {
        eMedicozDownloadManager.downloadRequests.put(downloadid, convertView);

        FetchListener fetchListener = (id, status, progress, downloadedBytes, fileSize, error) -> {
            Log.e("onUpdate", "progress: " + progress + " status: " + status + " id: " + id);
            if (status == Fetch.STATUS_DOWNLOADING
                    || status == Fetch.STATUS_QUEUED
                    || status == Fetch.STATUS_REMOVED
                    || status == Fetch.STATUS_ERROR) {
                savedofflineListener.updateProgressUI(eMedicozDownloadManager.downloadRequests.get(id),
                        progress, status, id);

            } else if (status == Fetch.STATUS_DONE) {
                final RequestInfo RI = getFetchInstance().get(id);
                savedofflineListener.updateUIForDownloadedVideo(eMedicozDownloadManager.downloadRequests.get(id),
                        data, getFetchInstance().get(id), id);

            } else if (error != Fetch.NO_ERROR) {
                //An error occurred

                if (error == Fetch.ERROR_HTTP_NOT_FOUND) {
                    //handle error
                }

            }
        };
        getFetchInstance().addFetchListener(fetchListener);
    }

    public void downloadProgressUpdate(View convertView, final long downloadid, NotesData data,
                                       final SaveOfflineNotesListener savedofflineListener, final String type) {
        eMedicozDownloadManager.downloadRequests.put(downloadid, convertView);

        FetchListener fetchListener = (id, status, progress, downloadedBytes, fileSize, error) -> {
            Log.e("onUpdate", "progress: " + progress + " status: " + status + " id: " + id);
            if (status == Fetch.STATUS_DOWNLOADING
                    || status == Fetch.STATUS_QUEUED
                    || status == Fetch.STATUS_REMOVED
                    || status == Fetch.STATUS_ERROR) {
                savedofflineListener.updateProgressUI(eMedicozDownloadManager.downloadRequests.get(id),
                        progress, status, id);

            } else if (status == Fetch.STATUS_DONE) {
                final RequestInfo RI = getFetchInstance().get(id);
                savedofflineListener.updateUIForDownloadedVideo(eMedicozDownloadManager.downloadRequests.get(id),
                        data, getFetchInstance().get(id), id);

            } else if (error != Fetch.NO_ERROR) {
                //An error occurred

                if (error == Fetch.ERROR_HTTP_NOT_FOUND) {
                    //handle error
                }

            }
        };
        getFetchInstance().addFetchListener(fetchListener);
    }

    public interface SavedOfflineVideoCallBack {
        void updateUIForDownloadedVideo(RequestInfo requestInfo, long id);

        void updateProgressUI(Integer value, int status, long id);

        void onStartEncoding();

        void onEncodingFinished();
    }

    public interface SavedOfflineVideoCallBackForLiveClass {
        void updateUIForDownloadedVideo(View convertView, RequestInfo requestInfo, long id);

        void updateProgressUI(View convertView, Integer value, int status, long id);

        void onStartEncoding();

        void onEncodingFinished();
    }

    public interface SaveOfflineVideoListener {
        void updateUIForDownloadedVideo(View convertView, VideoItemData data, RequestInfo requestInfo, long id);

        void updateProgressUI(View convertView, Integer value, int status, long id);

    }

    public interface SaveOfflineNotesListener {
        void updateUIForDownloadedVideo(View convertView, NotesData data, RequestInfo requestInfo, long id);

        void updateProgressUI(View convertView, Integer value, int status, long id);

    }

}
