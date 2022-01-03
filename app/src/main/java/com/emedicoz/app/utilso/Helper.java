package com.emedicoz.app.utilso;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.legacy.widget.Space;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.courses.activity.LiveStreamActivity;
import com.emedicoz.app.courses.activity.MyCoursesActivity;
import com.emedicoz.app.customviews.ExoSpeedDemo.PlayerActivityNew;
import com.emedicoz.app.customviews.PdfActivity;
import com.emedicoz.app.customviews.TouchImageView;
import com.emedicoz.app.customviews.WebViewActivity;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.NewProfileActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.flashcard.activity.ViewFlashCardActivity;
import com.emedicoz.app.flashcard.model.FlashCards;
import com.emedicoz.app.login.activity.AuthActivity;
import com.emedicoz.app.login.activity.LoginCatActivity;
import com.emedicoz.app.login.activity.SignInActivity;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionBasic;
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesData;
import com.emedicoz.app.recordedCourses.model.detaildatavideo.VideoItemData;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.utilso.offlinedata.eMedicozStorage;
import com.emedicoz.app.video.retrofit.RetrofitClient;
import com.emedicoz.app.video.retrofit.VideoRetrofitApi;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.tonyodev.fetch.Fetch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.maxcom.http.LocalSingleHttpServer;
import okhttp3.ResponseBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Cbc-03 on 05/22/17.
 */

public class Helper {

    public static eMedicozStorage storage;
    static ProgressDialog progressDialog;
    static File outputFile;
    static String path;
    static Intent intent = null;
    private static Tracker sTracker;
    private static LocalSingleHttpServer mServer;
    private static Dialog dialog;
    private static BottomSheetDialog mBottomSheetDialog;
    public static ArrayList<Course> courseArrayList = new ArrayList<>();
    public static boolean proxy;

    public static boolean isInValidIndianMobile(String mobile) {
        if (mobile == null) return false;
        String regx = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}$";
        return !mobile.matches(regx);
    }

    public static boolean DataNotValid(EditText view) {
        view.setError("This field is required");
        view.requestFocus();
        return false;
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean showErrorToast(View view, String msg) {
        Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
        return false;
    }

    public static String getCloudFrontUrl() {
        MasterFeedsHitResponse resp = SharedPreference.getInstance().getMasterHitResponse();
        return resp != null ? resp.getCloud_front_url_prefix() : "";
    }

    public static String getS3EndPoint() {
        MasterFeedsHitResponse resp = SharedPreference.getInstance().getMasterHitResponse();
        return resp != null ? resp.getS3_url_prefix() : "https://s3.ap-south-1.amazonaws.com/";
    }

    public static String getS3UrlPrefix() {
        MasterFeedsHitResponse resp = SharedPreference.getInstance().getMasterHitResponse();
        return resp != null ? resp.getS3_url_prefix() + Const.AMAZON_S3_BUCKET_NAME_VIDEO + "/" : "";
    }

    public static String gaetFormatedDate(long time) {
        if (time == 0) return "";
        String date;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        date = DateFormat.format("dd, MMM yyyy", cal).toString();
        return date;
    }

    public static String getFormattedDateTime(long time) {
        if (time == 0) return "";
        String date;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        date = DateFormat.format("hh:mm a dd MMM, yyyy", cal).toString();
        return date;
    }


    public static String getDate(long time) {
        if (time == 0) return "";
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        return date;
    }

    public static String getFormattedDate(long time) {
        if (time == 0) return "";
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        return date;
    }

    public static String getTime(long time) {
        if (time == 0) return "";
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("hh:mm aa", cal).toString();
        return date;
    }


    public static String parseDateToddMM(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd MMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String ordinalNo(int number) {
        int mod100 = number % 100;
        int mod10 = number % 10;
        if (mod10 == 1 && mod100 != 11) {
            return number + "st";
        } else if (mod10 == 2 && mod100 != 12) {
            return number + "nd";
        } else if (mod10 == 3 && mod100 != 13) {
            return number + "rd";
        } else {
            return number + "th";
        }
    }

    public static eMedicozStorage getStorageInstance(Context activity) {
        try {
            storage = new eMedicozStorage(activity, activity.getResources().getString(R.string.app_name), activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return storage;
    }

    public static void getAvailableInternalSpace(String stringurl) {
        URL url = null;
        try {
            url = new URL(stringurl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int file_size = urlConnection.getContentLength();
            Log.e("File Size", String.valueOf(file_size));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCoursePrice(Course course) {
        return !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())
                ? course.getFor_dams() : course.getNon_dams();
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBlocks = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getBlockSizeLong();
        } else {
            availableBlocks = stat.getBlockSize();
        }
        return availableBlocks;
    }

    public static void showSnackBar(View view, CharSequence text) {
        try {
            if (view != null) {
                final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextColor(Color.parseColor(Const.SNACK_BAR_TEXT_COLOR));
                textView.setMaxLines(5);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackbar.dismiss();
                    }
                }, 4000);
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoViewFlashCardActivity(Activity activity, String title, ArrayList<FlashCards> flashCardsArrayList, boolean isAllCards) {
        Intent sharingIntent = new Intent(activity, ViewFlashCardActivity.class);
        SharedPreference.getInstance().putString(Const.FLASH_CARD_LIST, new Gson().toJson(flashCardsArrayList));
        sharingIntent.putExtra(Const.TITLE, title);
        Log.e("Helper", "gotoViewFlashCardActivity: " + title);
        sharingIntent.putExtra(Const.ALL_CARDS, isAllCards);
        activity.startActivity(sharingIntent);
    }

    public static void gotoViewFlashCardActivity(int position, Activity activity, String title, ArrayList<FlashCards> flashCardsArrayList, boolean isAllCards) {
        Log.e("Helper", "gotoViewFlashCardActivity: position - " + position);
        Log.e("Helper", "gotoViewFlashCardActivity: title - " + title);
        Intent sharingIntent = new Intent(activity, ViewFlashCardActivity.class);
        SharedPreference.getInstance().putString(Const.FLASH_CARD_LIST, new Gson().toJson(flashCardsArrayList));
        sharingIntent.putExtra(Const.POSITION, position);
        sharingIntent.putExtra(Const.TITLE, title);
        sharingIntent.putExtra(Const.ALL_CARDS, isAllCards);
        activity.startActivity(sharingIntent);
    }

    public static String youtubevalidation(String des) {

        des = des.trim();
        String[] parts = des.split("\\s+");

        Log.d("Youtube Validation", "Enter");
        Log.d("String", parts[0]);
        final String regex1 = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|\u200C\u200B%2Fvideos%2F|embed%2\u200C\u200BF|youtu.be%2F|%2Fv%2\u200C\u200BF)[^#\\&\\?\\n]*";
        final Pattern pattern1 = Pattern.compile(regex1, Pattern.MULTILINE);
        for (int i = 0; i < parts.length; i++) {
            final Matcher matcher1 = pattern1.matcher(parts[i]);
            Log.d("Youtube Validation", "Matching");
            if (matcher1.find()) {
                Log.d("Youtube Validation", "Matched");
                return matcher1.group();
            }
        }
        return null;
    }

    public static boolean isdateExpire(String date) {
        long currentDate = Calendar.getInstance().getTimeInMillis();
        long oldDate = getMilliFromDate(date);
        return (currentDate > oldDate);
    }

    public static long getMilliFromDate(String dateFormat) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = formatter.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " + date);
        return date.getTime();
    }

    public static void logUser(Activity activity) {
        eMedicozApp application = (eMedicozApp) activity.getApplication();
        sTracker = application.getDefaultTracker();
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if (SharedPreference.getInstance().getLoggedInUser() != null) {
            crashlytics.setUserId(SharedPreference.getInstance().getLoggedInUser().getId());
            sTracker.setScreenName("Image~" + SharedPreference.getInstance().getLoggedInUser().getName());
        } else {
            crashlytics.setUserId(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            sTracker.setScreenName("Image~" + "Abc");
        }
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void goToPlayStore(Context activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static String getVersionName(Activity activity) {
        String version = "";
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /*    public static int getVersionCode(Activity activity) {


                return BuildConfig.VERSION_CODE;
        }*/
    public static int getVersionCode(Activity activity) {
        int version = 0;
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    // List of the Navigation menu
    public static ArrayList<String> gettitleList(Activity activity) {
        ArrayList<String> expandableListTitle = new ArrayList<>();
        expandableListTitle.add(activity.getString(R.string.specialities));
        expandableListTitle.add(activity.getString(R.string.course));
        expandableListTitle.add(activity.getString(R.string.video));
        //expandableListTitle.add(activity.getString(R.string.my_bookmarks));
        expandableListTitle.add(activity.getString(R.string.bookmarks));
        expandableListTitle.add(activity.getString(R.string.cpr));
        expandableListTitle.add(activity.getString(R.string.rewardpoints));
        expandableListTitle.add(activity.getString(R.string.feedback));

//        expandableListTitle.add(activity.getString(R.string.termscond));
//        expandableListTitle.add(activity.getString(R.string.privacy));
        expandableListTitle.add(activity.getString(R.string.appSettings));
        expandableListTitle.add(activity.getString(R.string.logout));

        return expandableListTitle;
    }

    // List of the Navigation Course Section menu
    public static ArrayList<String> getcourseSubList(Context activity) {
        ArrayList<String> coursesublist = new ArrayList<>();
        coursesublist.add(activity.getString(R.string.allcourses));
        coursesublist.add(activity.getString(R.string.mycourse));
        coursesublist.add(activity.getString(R.string.leaderboard));
        return coursesublist;
    }

    public static boolean isStringValid(String string) {
        return string != null && !TextUtils.isEmpty(string);
    }

    public static ArrayList<String> getBookmarkSubList(Context activity) {
        ArrayList<String> coursesublist = new ArrayList<>();
        coursesublist.add(activity.getString(R.string.all));
        coursesublist.add(activity.getString(R.string.quiz));
        coursesublist.add(activity.getString(R.string.test));
        coursesublist.add(activity.getString(R.string.video));
        coursesublist.add(activity.getString(R.string.feeds));
        return coursesublist;
    }

    public static File createImageFile(Context ctx) {
        File extStorageAppBasePath = null;
        File extStorageAppCachePath;
        String state = Environment.getExternalStorageState();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // Retrieve the base path for the application in the external storage
            File externalStorageDir = Environment.getExternalStorageDirectory();

            if (externalStorageDir != null) {
                // {SD_PATH}/Android/data/com.app.urend
                extStorageAppBasePath = new File(externalStorageDir.getAbsolutePath() +
                        File.separator + "Android" + File.separator + "data" +
                        File.separator + ctx.getPackageName() + File.separator + "EmedicozImages");
            }

            if (extStorageAppBasePath != null) {
                // {SD_PATH}/Android/data/com.app.urend/cache
                extStorageAppCachePath = new File(extStorageAppBasePath.getAbsolutePath() +
                        File.separator + "cache");

                boolean isCachePathAvailable = true;

                if (!extStorageAppCachePath.exists()) {
                    // Create the cache path on the external storage
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                }

                if (!isCachePathAvailable) {
                    // Unable to create the cache path

                }
            }
        }
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                if (extStorageAppBasePath != null) {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", extStorageAppBasePath);
                } else {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", Environment.getExternalStorageDirectory());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (extStorageAppBasePath != null) {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", extStorageAppBasePath);
                } else {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", ctx.getFilesDir());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFileTemp;
    }

    //methods to compress image starts//
    public static Bitmap decodeSampledBitmap(String url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    //methods to compress image starts//
    public static byte[] FileToByteArray(String file) {
        File fil = new File(file);

        byte[] b = new byte[(int) fil.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(fil);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char) b[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        return b;
    }

    public static void downloadFile(String _url, String _name, Context mContext) {


//        try {
//            URL u = new URL(_url);
//            mContext.getFilesDir();
//            DataInputStream stream = new DataInputStream(u.openStream());
//            byte[] buffer = IOUtils.toByteArray(stream);
//            FileOutputStream fos = mContext.openFileOutput(_name, Context.MODE_PRIVATE);
//            fos.write(buffer);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
    }

    public static void downloadFileExternal(String _url, String _name, Context mContext) {

        File path = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + mContext.getPackageName() + "/" + ".Downloaded");
        if (!path.exists()) {
            Log.e("Download", "path created " + path.toString());
            path.mkdirs();
        }

        String FileName = _name;
        File filepath = new File(path + "/" + FileName);
        if (!filepath.exists()) {

            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(_url);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE |
                    DownloadManager.Request.NETWORK_WIFI).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION).
                    setTitle(mContext.getResources().getString(R.string.app_name)).setAllowedOverRoaming(true);
            File internalPath = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + mContext.getPackageName() + "/" + ".Downloaded");
            if (!internalPath.exists()) {
                Log.e("Download", "internalPath created " + internalPath.toString());
                internalPath.mkdirs();
            }

            request.setDestinationInExternalPublicDir("/Android/data/" + mContext.getPackageName() + "/" + ".Downloaded", FileName);
            request.allowScanningByMediaScanner();
            manager.enqueue(request);
//            File NewFileCreated = new File(internalPath + "/" + FileName);
//            if (postFile.getFile_type().equals(Const.PDF))
//                showPdf(NewFileCreated, activity, 1);
        }
    }

    public static boolean DeleteFileFromStorage(String _name, Context mContext) {
        return mContext.deleteFile(_name);
    }

    public static void DownloadfilefromURL(Activity activity, PostFile postFile) {
        Log.e("Download", "This is state " + Environment.getExternalStorageState());
        Log.e("Download", "This is getAbsolutePath " + Environment.getExternalStorageDirectory().getAbsolutePath());

        File path = new File(Environment.getExternalStorageDirectory().getPath() + "/" + activity.getPackageName() + "/" + "Downloaded");
        if (!path.exists()) {
            Log.e("Download", "path created " + path.toString());
            path.mkdirs();
        }

        String FileName = postFile.getFile_info();
        File filepath = new File(path + "/" + FileName);
        if (!filepath.exists()) {
            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(postFile.getLink());
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE |
                    DownloadManager.Request.NETWORK_WIFI).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED).
                    setTitle(activity.getResources().getString(R.string.app_name)).setAllowedOverRoaming(true);
            File internalPath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + activity.getPackageName() + "/" + "Downloaded");
            if (!internalPath.exists()) {
                Log.e("Download", "internalPath created " + internalPath.toString());
                internalPath.mkdirs();
            }

            request.setDestinationInExternalPublicDir(activity.getPackageName() + "/" + "Downloaded", FileName);
            request.allowScanningByMediaScanner();
            manager.enqueue(request);
            File NewFileCreated = new File(internalPath + "/" + FileName);
            if (postFile.getFile_type().equals(Const.PDF))
                showPdf(NewFileCreated, activity, 1);
        } else if (postFile.getFile_type().equals(Const.PDF) && filepath.exists())
            showPdf(filepath, activity, 1);
    }

    public static void showPdf(File file, Activity activity, int type) {
        PackageManager packageManager = activity.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        if (type == 1)
            testIntent.setType("application/pdf");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = GenericFileProvider.getUriForFile(activity, "com.emedicoz.app.utilsGenericFileProvider", file);
        if (type == 1)
            intent.setDataAndType(uri, "application/pdf");
        activity.startActivity(intent);
    }

//    public static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
//                                                                           final int maxLine, final String spanableText, final boolean viewMore) {
//        String str = strSpanned.toString();
//        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
//
//        if (str.contains(spanableText)) {
//            ssb.setSpan(new MySpannable(false) {
//                @Override
//                public void onClick(View widget) {
//                    if (viewMore) {
//                        tv.setLayoutParams(tv.getLayoutParams());
//                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
//                        tv.invalidate();
//                        makeTextViewResizable(tv, -1, " Read Less", false);
//                    } else {
//                        tv.setLayoutParams(tv.getLayoutParams());
//                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
//                        tv.invalidate();
//                        makeTextViewResizable(tv, 5, "...Read More", true);
//                    }
//                }
//            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
//        }
//        return ssb;
//    }

    public static void getimageFile(Context ctx) {

        String FILENAME = "sample-ppt";
        String string = "hello world!";
        Uri pptUri = Uri.parse("file://Internal storage/Download/sample-ppt.ppt");

        File NewFileCreated = new File("//Internal storage/Download/sample-ppt.ppt");

        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(NewFileCreated.getAbsolutePath().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("Tag Dowload ", ctx.getFilesDir().toString());
        getimage(ctx);
    }

    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getTextInCenter(String htmlText) {
        return "<center>" + htmlText + "</center>";
    }

//    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
//
//        if (tv.getTag() == null) {
//            tv.setTag(tv.getText());
//        }
//        ViewTreeObserver vto = tv.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//
//                ViewTreeObserver obs = tv.getViewTreeObserver();
//                obs.removeGlobalOnLayoutListener(this);
//                if (maxLine == 0) {
//                    int lineEndIndex = tv.getLayout().getLineEnd(0);
//                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
//                    tv.setText(text);
//                    tv.setMovementMethod(LinkMovementMethod.getInstance());
//                    tv.setText(
//                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
//                                    viewMore), TextView.BufferType.SPANNABLE);
//                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
//                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
//                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
//                    tv.setText(text);
//                    tv.setMovementMethod(LinkMovementMethod.getInstance());
//                    tv.setText(
//                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
//                                    viewMore), TextView.BufferType.SPANNABLE);
//                } else {
//                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
//                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
//                    tv.setText(text);
//                    tv.setMovementMethod(LinkMovementMethod.getInstance());
//                    tv.setText(
//                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
//                                    viewMore), TextView.BufferType.SPANNABLE);
//                }
//            }
//        });
//
//    }

    public static void getimage(Context ctx) {

        String FILENAME = "sample-ppt";
//        Uri pptUri = Uri.parse("file://Internal storage/Download/sample-ppt.ppt");
//
//        File NewFileCreated = new File("//Internal storage/Download/sample-ppt.ppt");
//        File NewFileCreatednew = new File("//Internal storage/Download/sample-ppt1.ppt");

        FileInputStream fos = null;
        try {
            int len = 0;
            fos = ctx.openFileInput(FILENAME);
            len = fos.read();
            Log.e("Tag len ", String.valueOf(len));
//            while (len  != -1) {
//                os.write(b, 0, length);
//            }
//
//            is.close();
//            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Tag Dowload ", ctx.getFilesDir().toString());

    }

    public static void sendLink(Activity activity, String subject, String msg, String msgHtml) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (sharingIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(sharingIntent);
        }
    }

    public static void makeLinks(TextView textView, String link, ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(textView.getText());

        int startIndexOfLink = textView.getText().toString().indexOf(link);
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static boolean DataNotValid(EditText view, int type) {
        if (type == 1) view.setError("This Email Id is invalid");
        else if (type == 2) view.setError("This Phone is invalid");
        view.requestFocus();
        return false;
    }

    public static String GetText(EditText text) {
        return text.getText().toString().trim();
    }

    public static String GetText(TextView text) {
        return text.getText().toString().trim();
    }

    public static boolean isConnected(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }

    public static void StopDownloadService(Context context) {
        ArrayList<offlineData> offlineDataArrayList = eMedicozDownloadManager.getAllOfflineData(context);
        if (offlineDataArrayList != null && offlineDataArrayList.size() > 0) {
            for (offlineData data : offlineDataArrayList) {
                data.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(data.getDownloadid()));

                if (data.getRequestInfo() != null &&
                        (data.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING ||
                                data.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) &&
                        data.getRequestInfo().getProgress() < 100) {
                    eMedicozDownloadManager.getFetchInstance().pause(data.getDownloadid());
                    data.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(data.getDownloadid()));
                }
            }
        }
        eMedicozDownloadManager.getFetchInstance().release();

        Helper.getStorageInstance(eMedicozApp.getAppContext()).addRecordStore(Const.OFFLINE_DOWNLOADEDIDS, offlineDataArrayList);
    }

    public static String CapitalizeText(String text1) {
        String text = "";
        if (!TextUtils.isEmpty(text1)) {
            text = text1.trim();
            if (text.contains(" ")) {
                String[] strarr = text.split(" +");
                String fname = null;
                for (String name : strarr) {
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1); // d
                    fname = (fname == null ? name : fname + " " + name);
                }
                return fname;
            } else return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return text;
    }

    public static void gotoViewFlashCardActivity(Activity activity, ArrayList<FlashCards> flashCardsArrayList, boolean isAllCards) {
        Intent sharingIntent = new Intent(activity, ViewFlashCardActivity.class);
        SharedPreference.getInstance().putString(Const.FLASH_CARD_LIST, new Gson().toJson(flashCardsArrayList));
//        sharingIntent.putExtra(Const.FLASH_CARD_LIST, flashCardsArrayList);
        sharingIntent.putExtra(Const.ALL_CARDS, isAllCards);
        activity.startActivity(sharingIntent);
    }

    public static String combinationFormatter(final int mMillis) {
        long millis = mMillis * 1000;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long hours = TimeUnit.MILLISECONDS.toHours(millis);

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "" : hours < 10 ? "0" + hours + " hour" : hours + " hours");
        //b.append(":");
        b.append(minutes == 0 ? "" : minutes < 10 ? " 0" + minutes + " minute" : " " + minutes + " minutes");
        //   b.append(":");
        b.append(seconds == 0 ? "" : seconds < 10 ? " 0" + seconds + " second" : " " + seconds + " seconds");

        return b.toString();
    }

    public static String CapitalizeFirstLetterText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.contains(" ")) {
                String[] strarr = text.split(" +");
                String fname = null;
                for (String name : strarr) {
                    if (name.length() > 1)
                        name = Character.toUpperCase(name.charAt(0)) + name.substring(1); // d
                    else
                        name = "";
                    fname = (fname == null ? name : fname + " " + name);
                }
                return fname;
            } else return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return text;
    }

    public static void getVersionUpdateDialog(final Activity ctx) {
        if (!ctx.isFinishing()) {
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(ctx);

            alertBuild
                    .setTitle(ctx.getString(R.string.update_app_dialog_title))
                    .setMessage(ctx.getString(R.string.update_app_dialog_message))
                    .setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Helper.goToPlayStore(ctx);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            ctx.finishAffinity();
                        }
                    });
            AlertDialog dialog = alertBuild.create();
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int alertTitle = ctx.getResources().getIdentifier("alertTitle", Constants.Extras.ID, "android");
            ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
            ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
        }
    }

    public static void showSoftHardPopUp(final Context context, String message, boolean hardPopup) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.soft_hard_popup, null, false);
        Button cancelPopup = null, btnPopup = null;
        TextView textPopup = null;
        ImageView imgPopup = null;

        if (dialog != null) {
            if (dialog.isShowing())
                dialog.cancel();
            dialog = null;
        }
        dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.setCanceledOnTouchOutside(!hardPopup);
        dialog.setCancelable(!hardPopup);

        btnPopup = v.findViewById(R.id.btnPopup);
        btnPopup.setTag(hardPopup);
        cancelPopup = v.findViewById(R.id.btn_cancel_popup);
        textPopup = v.findViewById(R.id.textPopup);
        imgPopup = v.findViewById(R.id.imgPopup);

        if (message.contains("<img")) {
            imgPopup.setVisibility(View.VISIBLE);
            final Document doc = Jsoup.parse(message);
            textPopup.setText(doc.body().text());
            Glide.with(context).load(doc.select("img").attr("src")).into(imgPopup);
        } else {
            imgPopup.setVisibility(View.GONE);
            textPopup.setText(Html.fromHtml(message));
        }
        btnPopup.setOnClickListener((View view) -> {
            dialog.dismiss();
            if (Boolean.parseBoolean(view.getTag().toString()))
                ((Activity) context).finishAffinity();
            dialog = null;
        });
        try {
            if (!((Activity) context).isFinishing())
                dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showUpdatePopUp(final Context context, String message, boolean hardUpdate) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.soft_hard_popup, null, false);
        Button cancelPopup = null, btnPopup = null;
        TextView textPopup = null;
        ImageView imgPopup = null;

        if (dialog == null) {
            dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(v);
            dialog.setCanceledOnTouchOutside(!hardUpdate);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        btnPopup = v.findViewById(R.id.btnPopup);
        cancelPopup = v.findViewById(R.id.btn_cancel_popup);
        textPopup = v.findViewById(R.id.textPopup);
        imgPopup = v.findViewById(R.id.imgPopup);

        if (!dialog.isShowing())
            dialog.cancel();
        if (hardUpdate)
            cancelPopup.setVisibility(View.GONE);
        else {
            cancelPopup.setVisibility(View.VISIBLE);
            cancelPopup.setOnClickListener((v1) -> dialog.dismiss());
        }

        if (message.contains("<img")) {
            imgPopup.setVisibility(View.VISIBLE);
            final Document doc = Jsoup.parse(message);
            textPopup.setText(doc.body().text());
            Glide.with(context).load(doc.select("img").attr("src")).into(imgPopup);
        } else {
            imgPopup.setVisibility(View.GONE);
            textPopup.setText(Html.fromHtml(message));
        }
        btnPopup.setOnClickListener((View view) -> {
            if (!hardUpdate)
                dialog.dismiss();
            goToPlayStore(context);
        });

        try {
            if (!((Activity) context).isFinishing())
                dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void userDetailMissingPopup(final Context context, String message, final String FragType, final String RegType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.soft_hard_popup, null, false);
        final Dialog quizBasicInfoDialog = new Dialog(context);
        quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quizBasicInfoDialog.setCanceledOnTouchOutside(false);
        quizBasicInfoDialog.setContentView(v);
        quizBasicInfoDialog.setCancelable(false);
        quizBasicInfoDialog.show();
        Button btnPopup = v.findViewById(R.id.btnPopup);
        TextView textPopup = v.findViewById(R.id.textPopup);
        ImageView imgPopup = v.findViewById(R.id.imgPopup);

        textPopup.setText(message);
        btnPopup.setText("Edit Profile");

        btnPopup.setOnClickListener((View view) -> {
            quizBasicInfoDialog.dismiss();
            GoToEditProfileActivity(((Activity) context), FragType, RegType);

        });


    }

    public static TextDrawable GetDrawable1(String text, Context context, String id) {
        if (!TextUtils.isEmpty(text)) {
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .fontSize(45) /* thickness in px */
                    .endConfig()
                    .buildRound(text, Const.numberPadColor[Integer.parseInt(id)]);
            return drawable;
        } else
            return null;
    }

    public static TextDrawable GetDrawable(String text, Context context, String userId) {
        if (!TextUtils.isEmpty(text)) {
            String firstLetter = text.substring(0, 1);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, Const.color[DbAdapter.getInstance(context).getColor(userId)]);
            return drawable;
        } else
            return null;
    }

    public static TextDrawable GetDrawablefunction(String text, Context context, String userId) {
        if (!TextUtils.isEmpty(text)) {
            String firstLetter = text.substring(0, 1);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(firstLetter, Const.color[DbAdapter.getInstance(context).getColor(userId)]);
            return drawable;
        } else
            return null;
    }

    public static TextDrawable GetDrawable(String text, Context context) {
        if (!TextUtils.isEmpty(text)) {
            String firstLetter = text.substring(0, 1);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, Const.color[DbAdapter.getInstance(context).getColor("123456")]);
            return drawable;
        } else
            return null;
    }

    public static TextDrawable GetDrawableWithCustomColor(String text, Context context, int color) {
        if (!TextUtils.isEmpty(text)) {
            String firstLetter = text.substring(0, 1);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(text, color);
            return drawable;
        } else
            return null;
    }

    public static TextDrawable GetDrawable(String text, Context context, int colorCode) {
        if (!TextUtils.isEmpty(text)) {
            String firstLetter = text.substring(0, 1);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig().textColor(Color.WHITE)
                    .toUpperCase().endConfig().buildRound(firstLetter, colorCode);
            return drawable;
        } else
            return null;
    }

    public static void GoToOtpVerificationActivity(Activity activity, String otp, int type, String affiliateCode, String FragType) {
        Intent intent = new Intent(activity, LoginCatActivity.class);
        intent.putExtra(Const.OTP, otp);
        intent.putExtra(Const.AFFILIATE_CODE, affiliateCode);
        // 1 is for registration
        // 2 is for change password after mobile verification
        // 3 if to change the mobile number after verfication
        intent.putExtra(Constants.Extras.TYPE, type);
        intent.putExtra(Const.FRAG_TYPE, FragType);

        activity.startActivity(intent);
    }

    public static void ShowKeyboard(Context ctx) {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void HideKeyboard(Activity ctx) {
        {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = ctx.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(ctx);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void GoToChangePasswordActivity(Activity activity, String otp, String FragType) {
        Intent intent = new Intent(activity, LoginCatActivity.class);
        intent.putExtra(Const.OTP, otp);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        activity.startActivity(intent);
    }

    public static void DecryptAndGoToVideoActivity(Activity activity, String url, String type, String videoId, String videoType) {
        Intent intent = null;
        switch (type) {
            case Const.VIDEO_LIVE:
                intent = new Intent(activity, LiveStreamActivity.class);
                intent.putExtra(Const.VIDEO_LINK, url);
                break;
            case Const.VIDEO_STREAM:
                intent = new Intent(activity, PlayerActivityNew.class);
                final String[] mUrl = {url};
                try {
                    mServer = new LocalSingleHttpServer();
                    final Cipher c = getCipher();
                    if (c != null) {  // null means a clear video ; no need to set a decryption processing
                        mServer.setCipher(c);
                        mServer.start();
                        path = mUrl[0];
                        path = mServer.getURL(path);
                        intent.putExtra(Const.VIDEO_LINK, path);
                    } else {
                        intent.putExtra(Const.VIDEO_LINK, url);
                    }
                    intent.putExtra(Const.VIDEO_ID, videoId);
                    intent.putExtra(Const.VIDEO_TYPE, videoType);

                } catch (Exception e) {  // exception management is not implemented in this demo code
                    // Auto-generated catch block
                    e.printStackTrace();
                }
                // }
                break;
            case Const.VIDEO_LIVE_MULTI:
                intent = new Intent(activity, PlayerActivityNew.class);// this is for exoplayer
                intent.putExtra(Const.VIDEO_LINK, url);
                break;
        }
        if (intent != null) {
            intent.putExtra("TYPE", type);
            activity.startActivity(intent);
        }
    }

    public static void GoToVideoActivity(Activity activity, String url, String type, String videoId, String videoType) {
        Intent intent = null;
        switch (type) {
            case Const.VIDEO_LIVE:
                intent = new Intent(activity, LiveStreamActivity.class);
                intent.putExtra(Const.VIDEO_LINK, url);

                break;
            case Const.VIDEO_STREAM:
                intent = new Intent(activity, PlayerActivityNew.class);
                intent.putExtra(Const.VIDEO_LINK, url);
                intent.putExtra(Const.VIDEO_ID, videoId);
                intent.putExtra(Const.VIDEO_TYPE, videoType);

                break;
            case Const.VIDEO_LIVE_MULTI:
                intent = new Intent(activity, PlayerActivityNew.class);// this is for exoplayer
                intent.putExtra(Const.VIDEO_LINK, url);

                break;
        }
        if (intent != null) {
            intent.putExtra("TYPE", type);
            activity.startActivity(intent);
        }
    }


    public static void GoToLiveClassesWithChat(Activity activity, String url, String type, String chat_platform, String videoId,boolean isFromLiveCourse,String chatNode) {
        Intent intent = new Intent(activity, LiveStreamActivity.class);
        intent.putExtra(Const.VIDEO_LINK, url);
        intent.putExtra("chat_platform", chat_platform);
        intent.putExtra("TYPE", type);
        intent.putExtra("id", videoId);
        intent.putExtra("title", "");
        intent.putExtra(Const.IS_FROM_LIVE_COURSE,isFromLiveCourse);
        intent.putExtra(Const.CHAT_NODE,chatNode);
        activity.startActivity(intent);
    }

    public static void GoToVODPlayActivity(Activity activity, String url, String type,
                                           VideoItemData item, String videoType) {

        intent = new Intent(activity, PlayerActivityNew.class);
        intent.putExtra(Const.VIDEO_LINK, url);
        intent.putExtra(Const.VIDEO_TYPE, videoType);
        intent.putExtra(Const.VIDEO_DATA, item);

        activity.startActivity(intent);
    }

    public static Cipher getCipher() throws GeneralSecurityException {
        String iv = AES.strArrayvector;

        byte[] AesKeyData = AES.strArrayKey.getBytes();
        byte[] InitializationVectorData = iv.getBytes();
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(AesKeyData, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(InitializationVectorData));
        return cipher;
    }

    public static void GoToMobileVerificationActivity(Activity activity, String FragType, int flag) {
        Intent intent = new Intent(activity, LoginCatActivity.class);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        // 0 is to send the OTP to the mobile number
        // 1 is for changing the mobile number
        intent.putExtra(Constants.Extras.TYPE, flag);
        activity.startActivity(intent);
    }

    public static void gotoBookMark(Activity activity, String FragType) {
        Intent intent = new Intent(activity, CourseActivity.class);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        activity.startActivity(intent);
    }

    public static void gotoPDFViewer(Activity activity, String FragType, String path) {
        Intent intent = new Intent(activity, CourseActivity.class); //gotoPDFViewer
        intent.putExtra(Const.FRAG_TYPE, FragType);
        intent.putExtra(Const.PATH, path);
        activity.startActivity(intent);
    }

    public static void gotoPPTViewer(Activity activity, String FragType, String path) {
        Intent intent = new Intent(activity, CourseActivity.class); //gotoPDFViewer
        intent.putExtra(Const.FRAG_TYPE, FragType);
        intent.putExtra(Const.PATH, path);
        activity.startActivity(intent);
    }

    public static void GoToNextActivity(Context activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
//        Intent intent = new Intent(activity, FeedsActivity.class);
        //intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
        activity.startActivity(intent);
    }

    public static void GoToNextActivity(Context activity, String appLinkData) {
        Intent intent = new Intent(activity, HomeActivity.class);
        //Intent intent = new Intent(activity, FeedsActivity.class);
        //intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
        intent.putExtra("appLinkData", appLinkData);
        activity.startActivity(intent);
    }

    public static void GoToStudySection(Activity activity, String fragType) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.putExtra(Const.FRAG_TYPE, Const.QBANK);
        intent.putExtra(Const.FRAG_TYPE_STUDY, fragType);
        activity.startActivity(intent);
    }


    public static void GoToStudySection(Context activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.putExtra(Const.FRAG_TYPE, Const.QBANK);
        activity.startActivity(intent);
    }

    public static void GoToFollowExpertActivity(Activity activity, String type) {
        Intent intent = new Intent(activity, PostActivity.class); // comment fragment GoToPostActivity
        intent.putExtra(Const.FRAG_TYPE, type);
        if (!type.equals(Const.POST_FRAG))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    /*public static ArrayList<Tags> getTagsForUser() {

        ArrayList<Tags> tagsArrayList = new ArrayList<>();
        User user = SharedPreference.getInstance().getLoggedInUser();
        int moderator_selected_stream = SharedPreference.getInstance().getInt(Const.MODERATOR_SELECTED_STREAM);
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getAll_tags() != null && masterFeedsHitResponse.getAll_tags().size() > 0) {
            for (Tags tag : masterFeedsHitResponse.getAll_tags()) {
                if (!TextUtils.isEmpty(user.getIs_moderate()) && user.getIs_moderate().equalsIgnoreCase("1")) {//user is moderator
                    if (moderator_selected_stream == 0 || moderator_selected_stream == 1) { // moderator has selected medical stream to see
                        if (tag.getMaster_id().equalsIgnoreCase("1")) {
                            tagsArrayList.add(tag);
                        }
                    } else if (moderator_selected_stream != 0 || moderator_selected_stream == 2) { // moderator has selected dental stream to see
                        if (tag.getMaster_id().equalsIgnoreCase("2")) {
                            tagsArrayList.add(tag);
                        }
                    }
                } else {
                    if (tag.getMaster_id().equalsIgnoreCase(user.getUser_registration_info().getMaster_id())) {
                        tagsArrayList.add(tag);
                    }
                }
            }
        }

        return tagsArrayList;
    }*/
    public static ArrayList<Tags> getTagsForUser() {

        ArrayList<Tags> tagsArrayList = new ArrayList<>();
        User user = SharedPreference.getInstance().getLoggedInUser();
        String moderator_selected_stream = SharedPreference.getInstance().getString(Const.MODERATOR_SELECTED_STREAM);
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        if (masterFeedsHitResponse != null && masterFeedsHitResponse.getAll_tags() != null && masterFeedsHitResponse.getAll_tags().size() > 0) {
            for (Tags tag : masterFeedsHitResponse.getAll_tags()) {
                if (!TextUtils.isEmpty(user.getIs_moderate()) && user.getIs_moderate().equalsIgnoreCase("1")) {//user is moderator
                    if (TextUtils.isEmpty(moderator_selected_stream) || moderator_selected_stream.equals("1")) { // moderator has selected medical stream to see
                        if (tag.getMaster_id().equalsIgnoreCase("1")) {
                            tagsArrayList.add(tag);
                        }
                    } else if (!TextUtils.isEmpty(moderator_selected_stream)) { // moderator has selected dental stream to see
                        if (tag.getMaster_id().equalsIgnoreCase(moderator_selected_stream)) {
                            tagsArrayList.add(tag);
                        }
                    }
                } else {
                    if (user.getUser_registration_info() != null && !TextUtils.isEmpty(user.getUser_registration_info().getMaster_id()) && !TextUtils.isEmpty(tag.getMaster_id())
                            && tag.getMaster_id().equalsIgnoreCase(user.getUser_registration_info().getMaster_id())) {
                        tagsArrayList.add(tag);
                    }
                }
            }
        }

        return tagsArrayList;
    }

    public static void generateKeyHash(Context ctx) {

        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("TAG_KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String toUpperCase(String string) {
        return string.toUpperCase();
    }

    public static void getMNCCode(Context ctx) {
        TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            int mcc = Integer.parseInt(networkOperator.substring(0, 3));
            int mnc = Integer.parseInt(networkOperator.substring(3));

            Log.e("MCC  & MNC", mcc + " ' " + mnc);
        }
    }

    public static void GoToVideoFragment(Context activity) {
        Intent intent = new Intent(activity, FeedsActivity.class);
        intent.putExtra(Constants.Extras.TYPE, Const.VIDEOS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void GoToFeedsFragment(Context activity) {
        Intent intent = new Intent(activity, FeedsActivity.class);
        intent.putExtra(Const.FRAG_TYPE, Const.FEEDS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void GoToRegistrationActivity(Activity activity, String FragType, String RegType) {
        Intent intent = new Intent(activity, PostActivity.class);// registration fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        intent.putExtra(Constants.Extras.TYPE, RegType);
        activity.startActivity(intent);
    }

    public static void GoToEditProfileActivity(Activity activity, String FragType, String RegType) {
        Intent intent = new Intent(activity, PostActivity.class);// edit profile registration fragment
        intent.putExtra(Const.FRAG_TYPE, FragType);
        intent.putExtra(Constants.Extras.TYPE, RegType);
        intent.putExtra(Constants.Extras.UPDATE_PROFILE, true);
        activity.startActivity(intent);
    }

    public static void GoToAddPodcastScreen(Activity activity) {
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra(Const.FRAG_TYPE, Const.ADD_PODCAST);
        activity.startActivity(intent);
    }

    public static void GoToProfileActivity(Activity activity, String id) {
        Intent intent = new Intent(activity, NewProfileActivity.class);
        intent.putExtra(Constants.Extras.ID, id);
        activity.startActivity(intent);
        if (activity instanceof NewProfileActivity)
            activity.finish();
    }

    public static void GoToPostActivity(Activity activity, PostResponse post, String type) {
        Intent intent = new Intent(activity, PostActivity.class); // comment fragment GoToPostActivity
        intent.putExtra(Const.FRAG_TYPE, type);
        intent.putExtra(Const.POST, post);
        activity.startActivity(intent);
    }

    public static void GoToWebViewActivity(Activity activity, String url) {
        Intent newintent = new Intent(activity, WebViewActivity.class);
        newintent.putExtra(Const.URL, url);
        activity.startActivity(newintent);
    }

    public static void GoToWebViewActivity(Context context, String url) {
        Intent newintent = new Intent(context, WebViewActivity.class);
        newintent.putExtra(Const.URL, url);
        context.startActivity(newintent);
    }

    public static void openPdfActivity(Activity activity, String title, String url) {
        Intent newintent = new Intent(activity, PdfActivity.class);
        newintent.putExtra(Const.TITLE, title);
        newintent.putExtra(Const.URL, url);
        activity.startActivity(newintent);
    }

    public static void openPdfActivityWithDetails(Activity activity, String title, String url, NotesData data) {
        Intent newintent = new Intent(activity, PdfActivity.class);
        newintent.putExtra(Const.TITLE, title);
        newintent.putExtra(Const.URL, url);
        newintent.putExtra(Const.NOTES_DATA, data);
        activity.startActivity(newintent);
    }

    public static void SignOutUser(Context context) {
        ClearUserData(context);
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(Constants.Extras.TYPE, "LOGIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
/*        Intent intent = new Intent(context, SignInActivity.class);
        intent.putExtra(Constants.Extras.TYPE, "LOGIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
    }

    public static void ClearUserData(Context context) {
        SharedPreference sharedPreference = SharedPreference.getInstance();
        sharedPreference.ClearLoggedInUser();
        sharedPreference.remove(Const.MASTER_FEED_HIT_RESPONSE);
        sharedPreference.remove(Const.MASTER_REGISTRATION_HIT_RESPONSE);
        sharedPreference.remove(Const.FEED_PREFERENCE);
        sharedPreference.remove(Const.MODERATOR_SELECTED_STREAM);
        sharedPreference.remove(Const.STUDIED_CARD_TIME);
        sharedPreference.putBoolean(Const.IS_USER_LOGGED_IN, false);
        sharedPreference.putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
        DbAdapter.getInstance(context).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
        Helper.getStorageInstance(context).deleteRecord(Const.COURSE_LOCK_STATUS);
        Helper.getStorageInstance(context).deleteRecord(Const.OFFLINE_FEEDS);
        Helper.getStorageInstance(context).deleteRecord(Const.OFFLINE_COURSE);
        Helper.getStorageInstance(context).deleteRecord(Const.OFFLINE_SAVEDNOTES);
        Helper.getStorageInstance(context).deleteRecord(Const.OFFLINE_VIDEOTAGS_DATA);
        Helper.getStorageInstance(context).deleteRecord(Const.NOTIFICATION_DATA);
        Helper.getStorageInstance(context).deleteRecord(Const.DVL_DATA);
        Helper.getStorageInstance(context).deleteRecord("0");
        //   Helper.getStorageInstance(context).deleteRecord(Const.CRS_FILES);
    }

    public static String getCurrencySymbol() {
//        Locale defaultLocale = Locale.getDefault();
//
//        Currency currency = Currency.
//                getInstance(new Locale("", SharedPreference.getInstance().getLoggedInUser().getC_code()));
//        System.out.println("Currency Code: " + currency.getCurrencyCode());
//        System.out.println("Symbol: " + currency.getSymbol());
//        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());

        String currencySymbol = "";

        if (SharedPreference.getInstance().getLoggedInUser() != null
                && !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getC_code())
                && SharedPreference.getInstance().getLoggedInUser().getC_code().equals("+91")) {
            currencySymbol = eMedicozApp.getAppContext().getResources().getString(R.string.rs);
            SharedPreference.getInstance().putBoolean(Const.CURRENCY_INDIAN, true);
        } else {
            currencySymbol = eMedicozApp.getAppContext().getResources().getString(R.string.dollar);
            SharedPreference.getInstance().putBoolean(Const.CURRENCY_INDIAN, false);
        }

        return currencySymbol;
    }

    public static String calculatePriceBasedOnCurrency(String price) {
        String priceValue = "";
//        0.015239
        priceValue = !SharedPreference.getInstance().getBoolean(Const.CURRENCY_INDIAN) ? String.format("%.2f", Double.parseDouble(price) * 0.014084) : price;
        return priceValue;
    }

    public static Float getDiscountedValue(String mCouponType, String mPrice, String mDiscount) {
        Float finalPrice = null;
        if (mCouponType.equals("1")) {
            if ((Float.parseFloat(mPrice) - Float.parseFloat(mDiscount)) <= 0) {
                finalPrice = 0f;
                return finalPrice;
            } else {
                finalPrice = (Float.parseFloat(mPrice) - Float.parseFloat(mDiscount));
                return finalPrice;
            }
        } else {
            if (Float.parseFloat(mPrice) - (Float.parseFloat(mPrice) * Float.parseFloat(mDiscount)) / 100.0 <= 0) {
                finalPrice = 0f;
                return finalPrice;
            } else {
                finalPrice = (float) (Float.parseFloat(mPrice) - (Float.parseFloat(mPrice) * Float.parseFloat(mDiscount)) / 100.0);
                return finalPrice;
            }
        }
    }

    public static float getGSTExcludevalue(String price) {
        if (!GenericUtils.isEmpty(price)) {
            if (price.contains(".")) {
                return (float) ((Float.parseFloat(price) / 118.0) * 100);
            } else {
                return (float) ((Float.parseFloat(price) / 118.0) * 100);
            }
        } else return 0;
    }


    public static float calculateGST(String price, String gst) {

        if (GenericUtils.isEmpty(gst)) {
            gst = "18";
        }

        if (price.contains(".")) {
            return (float) ((Float.parseFloat(price) / 100.0) * Float.parseFloat(gst));
        } else {
            Log.e("tag", "getPercentage: " + price);
            return (float) ((Float.parseFloat(price) / 100.0) * Float.parseFloat(gst));
        }
    }

    public static float calculateDiscount(String price, String discount) {
        if (price.contains(".")) {
            return (float) (Float.parseFloat(price) - (Float.parseFloat(price) * Integer.parseInt(discount) / 100.0));
        } else {
            Log.e("tag", "getPercentage: " + price);
            return (float) (Float.parseFloat(price) - (Float.parseFloat(price) * Integer.parseInt(discount) / 100.0));
        }
    }

    public static void printError(String TAG, ResponseBody responseBody) {
        try {
            Log.e(TAG, "onResponse: " + responseBody.string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeKeyboard(Activity cnx) {

        InputMethodManager imm = (InputMethodManager) cnx.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            if (imm.isAcceptingText() || imm.isActive())
                imm.hideSoftInputFromWindow(cnx.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CaptializeFirstLetter(final EditText ET) {
        ET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if ((!s.equals(s.toUpperCase()) && s.length() == 1)) {
                    s = s.toUpperCase();
                    ET.setText(s);
                    ET.setSelection(s.length());
                }
            }
        });
    }

    public static void showErrorLayoutForNav(String apiType, Activity activity, int status, int type) {
        if (activity instanceof FeedsActivity) {
            ((FeedsActivity) activity).apiType = apiType;
            ((FeedsActivity) activity).replaceErrorLayout(status, type);
        } else if (activity instanceof BaseABNavActivity) {
            ((BaseABNavActivity) activity).apiType = apiType;
            ((BaseABNavActivity) activity).replaceErrorLayout(status, type);
        } else if (activity instanceof MyCoursesActivity) {
//            ((MyCoursesActivity) activity).apiType = apiType;
            ((MyCoursesActivity) activity).replaceErrorLayout(status, type);
        } else if (activity instanceof CourseDetailActivity) {
            ((CourseDetailActivity) activity).replaceErrorLayout(status, type);
        }
    }

    public static void showErrorLayoutForNoNav(String apiType, Activity activity, int status, int type) {
        if (activity instanceof FeedsActivity) {
            ((FeedsActivity) activity).apiType = apiType;
            ((FeedsActivity) activity).replaceErrorLayout(status, type);
        } else if (activity instanceof BaseABNoNavActivity) {
            ((BaseABNoNavActivity) activity).apiType = apiType;
            ((BaseABNoNavActivity) activity).replaceErrorLayout(status, type);
        } else if (activity instanceof MyCoursesActivity) {
//            ((MyCoursesActivity) activity).apiType = apiType;
            ((MyCoursesActivity) activity).replaceErrorLayout(status, type);
        } else if (activity instanceof CourseDetailActivity) {
            ((CourseDetailActivity) activity).replaceErrorLayout(status, type);
        }
    }

    public static boolean isAppRunning(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getDeviceModelName() {
        String manufacturerName = "";
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                manufacturerName = capitalize(model);
            } else {
                manufacturerName = capitalize(manufacturer) + " " + model;
            }
            Log.i("TAG", "MODEL: " + Build.MODEL);
            Log.i("TAG", "Manufacture: " + Build.MANUFACTURER);
            Log.i("TAG", "Final String: " + manufacturerName);
            return String.format("%s%s %s%s", Const.ANDROID_VERSION, Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "",
                    Const.DEVICE_MODEL, manufacturerName != null ? manufacturerName : "");
        } catch (Exception ex) {
            ex.printStackTrace();
            return String.format("%s%s %s%s", Const.ANDROID_VERSION, Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "",
                    Const.DEVICE_MODEL, manufacturerName != null ? manufacturerName : "");
        }

    }

    private static String capitalize(String s) {
        // this method is used to capitalize first letter of any String passed as parameter
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static void aDialogOnPermissionDenied(final Activity mContext) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.alert));
        alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.reGrantPermissionMsg));
        alertDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.action_settings),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(settingsIntent);
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    public static VideoRetrofitApi getApi() {
        return RetrofitClient.getInstance(Constants.BASE_API_URL).create(VideoRetrofitApi.class);
    }

    public static String formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";
        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    public static void newCustomDialog(Context context, String title, String description, boolean isCancelable, String buttonText, Drawable buttonBackgroundDrawable) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.new_custom_alert_dialog, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setContentView(v);
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvTitle, tvDescription;
        Space space;
        Button btnCancel, btnSubmit;
        tvTitle = v.findViewById(R.id.tv_title);
        tvDescription = v.findViewById(R.id.tv_description);
        space = v.findViewById(R.id.space);
        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        tvTitle.setText(title);
        tvDescription.setText(description);

        space.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnSubmit.setText(buttonText);
        btnSubmit.setBackground(buttonBackgroundDrawable);
        btnSubmit.setOnClickListener((View view) -> dialog.dismiss());
    }

    public static View newCustomDialog(Context context, boolean isCancelable, String title, String description) {
        View view = null;

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.new_custom_alert_dialog, null, false);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_title, tv_description;
        tv_title = view.findViewById(R.id.tv_title);
        tv_description = view.findViewById(R.id.tv_description);
        tv_title.setText(title);
        tv_description.setText(description);

        dialog.show();
        return view;
    }

    public static void fullScreenImageDialog(Activity activity, String src) {
        Dialog dialog = new Dialog(activity, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setLayout(MATCH_PARENT,
                MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.fragment_blank);

        final TouchImageView iv = dialog.findViewById(R.id.imageIV);
        Glide.with(activity)
                .asBitmap()
                .load(src)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        iv.setImageBitmap(result);
                    }
                });
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static View openBottomSheetDialog(Activity activity) {
        mBottomSheetDialog = new BottomSheetDialog(activity);
        View sheetView = activity.getLayoutInflater().inflate(R.layout.download_quality_bottom_sheet, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        return sheetView;
    }

    public static View showDialogErrorTest(Activity activity) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.dialog_report_error, null, false);
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(v);
        dialog.show();
        return v;
    }

    public static String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //2nd of march 2015
        int day = cal.get(Calendar.DATE);

        switch (day % 10) {
            case 1:
                return new SimpleDateFormat("d'st' MMM yyyy").format(date);
            case 2:
                return new SimpleDateFormat("d'nd' MMM yyyy").format(date);
            case 3:
                return new SimpleDateFormat("d'rd' MMM yyyy").format(date);
            default:
                return new SimpleDateFormat("d'th' MMM yyyy").format(date);
        }
    }

    public static String getFormatedDate(long millis) {
        // this method used to return the formatted date in am or pm for timestamp passed as parameter
        Date d = new Date(millis);
        java.text.DateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        String dateString = f.format(d);
        Date date = null;
        try {
            date = f.parse(f.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] amPM = dateString.split("\\s+");

        String[] fullDate = String.valueOf(date).split("\\s+");
        String correctDateFormat = fullDate[0] + ", " + fullDate[1] + " " + fullDate[2] + ", " + fullDate[5] + " at " + amPM[1] + " " + amPM[2];
        Log.e("getFormatedDate: ", correctDateFormat);
        return correctDateFormat;
    }

    public static void goToCourseInvoiceScreen(Context context, SingleCourseData singleCourseData) {
        // this method is used to redirect to course purchase screen with data that are used to purchase the course
        Intent courseInvoice = new Intent(context, CourseActivity.class); // FRAG_TYPE, Const.COURSE_INVOICE CourseInvoice
        courseInvoice.putExtra(Const.FRAG_TYPE, Const.COURSE_INVOICE);
        courseInvoice.putExtra(Const.COURSE_DESC, singleCourseData);
        courseInvoice.putExtra(Constants.Extras.TYPE, Const.COURSE_INVOICE);
        context.startActivity(courseInvoice);
    }

    public static void goToCartScreen(Context context, SingleCourseData singleCourseData) {
        // this method is used to redirect to course purchase screen with data that are used to purchase the course
        Intent courseInvoice = new Intent(context, CourseActivity.class); // FRAG_TYPE, Const.COURSE_INVOICE CourseInvoice
        courseInvoice.putExtra(Const.FRAG_TYPE, Const.MYCART);
        courseInvoice.putExtra(Const.COURSE_DESC, singleCourseData);
        context.startActivity(courseInvoice);
    }



    public static void goToCartScreen1(Context context, SingleCourseData singleCourseData) {
        // this method is used to redirect to course purchase screen with data that are used to purchase the course
        Intent courseInvoice = new Intent(context, CourseActivity.class); // FRAG_TYPE, Const.COURSE_INVOICE CourseInvoice
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        courseInvoice.putExtra(Const.FRAG_TYPE, Const.MYCART);
        courseInvoice.putExtra(Const.COURSE_DESC, singleCourseData);
        context.startActivity(courseInvoice);
    }


    public static void setCourseButtonText(Context context, Button btn, String type) {
        // used to set bgcolor and text of button depending on type
        // if type is FREE or PURCHASED then bgcolor will be green and text will be enrolled
        // else bgcolor will be red and text will be enroll
        if (type.equalsIgnoreCase(Const.RENEW)) {
            btn.setBackgroundColor(ContextCompat.getColor(context, R.color.rewards_color));
            btn.setText(context.getResources().getString(R.string.renew));
        } else if (type.equalsIgnoreCase(Const.PURCHASED) || type.equalsIgnoreCase(Const.FREE)) {
            btn.setBackgroundColor(ContextCompat.getColor(context, R.color.green_new));
            btn.setText(context.getResources().getString(R.string.enrolled));
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            btn.setText(context.getResources().getString(R.string.enroll));
        }
    }

    public static SingleCourseData getData(Course basic) {
        SingleCourseData singleCourseData = new SingleCourseData();
        singleCourseData.setCourse_type(basic.getCourse_type());
        singleCourseData.setFor_dams(basic.getFor_dams());
        singleCourseData.setNon_dams(basic.getNon_dams());
        singleCourseData.setMrp(basic.getMrp());
        singleCourseData.setId(basic.getId());
        singleCourseData.setCourse_tag(basic.getCourse_tag());
        singleCourseData.setCategory_tag(basic.getCategory_tag());
        singleCourseData.setCover_image(basic.getCover_image());
        singleCourseData.setTitle(basic.getTitle());
        singleCourseData.setLearner(basic.getLearner());
        singleCourseData.setRating(basic.getRating());
        singleCourseData.setIs_renew(basic.isIs_renew());
        singleCourseData.setIs_subscription(basic.getIs_subscription());
        singleCourseData.setIs_live(basic.getIs_live());
        if (basic.getGst_include() != null)
            singleCourseData.setGst_include(basic.getGst_include());
        else
            singleCourseData.setGst_include("1");
        if (!GenericUtils.isEmpty(basic.getGst()))
            singleCourseData.setGst(basic.getGst());
        else
            singleCourseData.setGst("18");
        if (!GenericUtils.isEmpty(basic.getPoints_conversion_rate()))
            singleCourseData.setPoints_conversion_rate(basic.getPoints_conversion_rate());
        else
            singleCourseData.setPoints_conversion_rate("100");
        return singleCourseData;
    }

    public static void blink(Activity activity, View playerView, TextView floatingText) {
        // used to blink the text from one place to another with in video view
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Random R = new Random();
        float dx = R.nextFloat() * playerView.getWidth();
        float dy = R.nextFloat() * playerView.getHeight();
        if (dx - floatingText.getWidth() < 0)
            dx = dx + floatingText.getWidth();
        else if (playerView.getWidth() - (dx + floatingText.getWidth()) <= 0)
            dx = dx - floatingText.getWidth();

        if (dy - floatingText.getHeight() < 0)
            dy = dy + floatingText.getHeight();
        else if (playerView.getHeight() - (dy + floatingText.getHeight()) <= 0)
            dy = dy - floatingText.getHeight();

        floatingText.animate()
                .x(dx)
                .y(dy)
                .setDuration(0)
                .start();


        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 2000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        blink(activity, playerView, floatingText);
                    }
                });
            }
        }).start();
    }


    public static void dismissBottonSheetDialog() {
        if (mBottomSheetDialog.isShowing())
            mBottomSheetDialog.dismiss();
    }

    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    public static void showExceptionMsg(Context context, Throwable t) {
        String msg = context.getResources().getString(R.string.exception_api_error_message);
        if (BuildConfig.DEBUG && t != null)
            msg = t.getMessage();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getFileName(String title) {
        return title.replaceAll("\r", "")
                .replaceAll("\n", "")
                .replaceAll("/", " or ")
                .replaceAll("\\.", "")
                .replaceAll(":", " ")
                .replaceAll("\\s{2,}", " ");
    }

    public static String getFileName(String url, String title, String type) {
       /* try {
            return URLDecoder.decode(url, "UTF-8").split("/")[url.split("/").length - 1];
        } catch (UnsupportedEncodingException e) {
        }*/
        title = Helper.getFileName(title);
        switch (type) {
            case Const.PDF:
                return title + ".pdf";
            case Const.EPUB:
                return title + ".epub";
            case Const.VIDEOS:
                return title + ".mp4";
        }
        return title;
    }

    public static String getPercentage(String percent) {
        return String.format("%s %%", new DecimalFormat("0.00").format(Float.parseFloat(
                percent))).replace(".00", "");
    }

    public static int getMinimumFollowerCount() {
        if (SharedPreference.getInstance().getMasterHitResponse() != null)
            return SharedPreference.getInstance().getMasterHitResponse().getMinimumFollowersPerStream();
        else
            return 5;
    }

    public static void openShareDialog(Activity activity, QuestionBank questionBank) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "eMedicoz QBANK");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getContent(questionBank));
        activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    public static void openShareDialogLiveCourse(Context context, Course course) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        if (course.getIs_live() != null && course.getIs_live().equals("0")) {
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.emedicoz_recorded_course));
        } else {
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.emedicoz_live_course));
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getContent(course));
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }


    public static void openShareDialogShareCourse(Activity activity, DescriptionBasic descriptionBasic) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        if (descriptionBasic.getIs_live() != null && descriptionBasic.getIs_live().equals("0")) {
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.emedicoz_recorded_course));
        } else {
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.emedicoz_live_course));
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getContent(descriptionBasic));
        activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    // their is no Share Link in course
    private static String getContent(Course course) {
        StringBuilder strContent = new StringBuilder();
        strContent.append(course.getTitle()).append("\n")
                .append(course.getShareLink());

        return strContent.toString();
    }

    private static String getContent(DescriptionBasic descriptionBasic) {
        StringBuilder strContent = new StringBuilder();
        strContent.append(descriptionBasic.getTitle()).append("\n")
                .append(descriptionBasic.getShare_url());

        return strContent.toString();
    }


    private static String getContent(QuestionBank questionBank) {
        StringBuilder strContent = new StringBuilder();
        strContent.append(questionBank.getQuestion()).append("\n\n")
                .append("A. ").append(questionBank.getOption1()).append("\n")
                .append("B. ").append(questionBank.getOption2()).append("\n\n")
                .append(questionBank.getSharelink());

        return strContent.toString();
    }

    public static boolean isMrpZero(@Nullable JSONObject courseObject) {
        if (courseObject != null)
            return courseObject.optString("mrp").isEmpty() || courseObject.optString("mrp").equals("0");
        else
            return false;
    }

    public static boolean isTestOrQbankCourse(@NotNull Course item) {
        if (item.getCourse_type() != null)
            return item.getCourse_type().equals("2") || item.getCourse_type().equals("3");
        else
            return false;
    }

    public static class MySpannable extends ClickableSpan {

        private boolean isUnderline = false;

        /***
         * Constructor
         */
        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(isUnderline);
            ds.setColor(Color.parseColor("#33A2D9"));
        }

        @Override
        public void onClick(View widget) {

        }

    }

    public static String getStreamId(User user) {
        if (user != null && user.getUser_registration_info() != null)
            return user.getUser_registration_info().getMaster_id() + ";";
        return "0";
    }

    public static int count(String s, char c) {
        int res = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c)
                res++;
        }
        return res;
    }

    public static void setCourseList(ArrayList<Course> courseList) {
        if (!courseArrayList.isEmpty())
            courseArrayList.clear();
        courseArrayList.addAll(courseList);
    }

    public static ArrayList<Course> getCourseList() {
        return courseArrayList;
    }

    public static void setCompulsoryAsterisk(TextView textView, String text) {
        String colored = "*";
        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(text);
        int start = strBuilder.length();
        strBuilder.append(colored);
        int end = strBuilder.length();
        strBuilder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(strBuilder);
    }


    public static void setEnrolAndWishListBackground(Context context, Course course, ImageView imgWishlist, Button enrollBtn) {
        if (course.isIs_renew() != null && course.isIs_renew().equals("1")) {
            imgWishlist.setVisibility(View.VISIBLE);
            enrollBtn.setText(context.getString(R.string.renew));
            enrollBtn.setBackgroundResource(R.drawable.background_orange_btn);
        } else if (course.isIs_purchased()) {
            enrollBtn.setText(context.getString(R.string.enrolled));
            imgWishlist.setVisibility(View.GONE);
            enrollBtn.setBackgroundResource(R.drawable.background_green_btn);
        } else {
            enrollBtn.setText(context.getString(R.string.enroll));
            imgWishlist.setVisibility(View.VISIBLE);
            enrollBtn.setBackgroundResource(R.drawable.background_btn_red);
        }

    }


    public static void setEnrollBackground(Context context, Course course, Button enrollBtn) {
        if (course.isIs_renew() != null && course.isIs_renew().equals("1")) {
            enrollBtn.setText(context.getString(R.string.renew));
            enrollBtn.setBackgroundResource(R.drawable.background_orange_btn);
        } else if (course.isIs_purchased()) {
            enrollBtn.setText(context.getString(R.string.enrolled));
            enrollBtn.setBackgroundResource(R.drawable.background_green_btn);
        } else {
            enrollBtn.setText(context.getString(R.string.enroll));
            enrollBtn.setBackgroundResource(R.drawable.background_btn_red);
        }
    }
}
