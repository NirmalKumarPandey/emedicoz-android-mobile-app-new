package com.emedicoz.app.video.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.vimeo.VimeoPlayer;
import com.emedicoz.app.feeds.activity.YouTubeVideoPlayer;
import com.emedicoz.app.modelo.Comment;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.Constants;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.adapter.VideoCommentAdapter;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.emedicoz.app.video.fragment.VideoFragmentViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.flowlayout.FlowLayout;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.maxcom.http.LocalSingleHttpServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.AES.generateVector;
import static com.emedicoz.app.utilso.AES.generatekey;
import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

/**
 * Created by admin1 on 31/10/17.
 */

public class VideoDetail extends AppCompatActivity implements eMedicozDownloadManager.SavedOfflineVideoCallBack, View.OnClickListener {

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    private static final CookieManager DEFAULT_COOKIE_MANAGER;


    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    public RelativeLayout rootView;
    public LinearLayout likeClickRL, commentLayout, taggedPeopleLL;
    public String taggedPeopleIdsRemoved;
    public String taggedPeopleIdsAdded;
    public String commentText;
    public String lastCommentId;
    public String id;
    public String videoId;
    public String type;
    public String parentId;
    public String errorMessage;
    public boolean isUserEditingComment = false;
    public int firstVisibleItem;
    public int visibleItemCount;
    public int totalItemCount;
    public int visibleThreshold = 5;
    public int previousTotalItemCount;
    public LinearLayout writeCommentLL;
    public String apiType;
    String url720 = "", url480 = "", url360 = "", url240 = "";
    Comment comment;
    LinearLayout likeLayout, rl1;
    ProgressBar downloadProgressBar;
    FlowLayout taggedPeopleFL;
    ScrollView videoSV;
    LinearLayout errorLayout;
    ImageView imageIV;
    Button tryAgainBtn;
    TextView nameTV, nameExpertIV, dateTV, commentTV, likeComment;
    LinearLayoutManager layoutManager;
    Video data;
    List<Comment> commentList = new ArrayList<>();
    Activity activity;
    ArrayList<String> oldIds, newIds;
    VideoCommentAdapter commentAdapter;
    SharedPreference sharedPreference = SharedPreference.getInstance();
    LinearLayout singleRow;
    Progress mProgress;
    Toolbar videoToolbar;
    ImageButton imgBack;
    ImageView pullChatLayout;
    private LinearLayout debugRootView;
    private Button retryButton;
    public boolean inErrorState;
    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private int width, height;
    private RelativeLayout videoPlayerRL, actionsRL, videoParentRL, commentsParentRL;
    private TextView descriptionTV, likeTV, viewTV, title, uploadedDate, noComment, messageTV;
    private ImageView videoImage, downloadIV, deleteIV, eyeIV, imgBookmark;
    private RecyclerView commentRecyclerView;
    private EditText writeComment;
    private ImageButton submitComment;
    private boolean loading = true;
    private eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
    private boolean chatLayoutShown = false;
    ImageView errorImageIV;
    TextView errorMessageTV;
    private offlineData downloadedVideo;

    public static String youtubevalidation(String des) {

        des = des.trim();
        String[] parts = des.split("\\s+");

        Log.d("Youtube Validation", "Enter");
        Log.d("String", parts[0]);
        final String regex1 = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|\u200C\u200B%2Fvideos%2F|embed%2\u200C\u200BF|youtu.be%2F|%2Fv%2\u200C\u200BF)[^#\\&\\?\\n]*";
        final Pattern pattern1 = Pattern.compile(regex1, Pattern.MULTILINE);
        for (String part : parts) {
            final Matcher matcher1 = pattern1.matcher(part);
            Log.d("Youtube Validation", "Matching");
            if (matcher1.find()) {
                Log.d("Youtube Validation", "Matched");
                return matcher1.group();
            }
        }
        return null;
    }

/*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("Config Changed", newConfig.orientation + "");// 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 2) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoToolbar.setVisibility(View.GONE);
            commentLayout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels);
            rootView.setLayoutParams(layoutParams);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoToolbar.setVisibility(View.VISIBLE);
            commentLayout.setVisibility(View.VISIBLE);

            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (200 * scale + 0.5f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pixels);
            rootView.setLayoutParams(layoutParams);
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate method");
        GenericUtils.manageScreenShotFeature(this);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.video_description);

        Toolbar toolbar = findViewById(R.id.videoToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);


        mProgress = new Progress(VideoDetail.this);
        mProgress.setCancelable(false);

        activity = this;
        singleRow = findViewById(R.id.singleRow);
        imageIV = findViewById(R.id.imageIV);
        nameTV = findViewById(R.id.nameTV);
        nameExpertIV = findViewById(R.id.nameExpertIV);
        dateTV = findViewById(R.id.dateTV);
        commentTV = findViewById(R.id.commentTV);
        likeComment = findViewById(R.id.commentLikeCount);
        pullChatLayout = findViewById(R.id.pull_chat_layout);
        errorImageIV = findViewById(R.id.errorImageIV);
        errorMessageTV = findViewById(R.id.errorMessageTV);

        data = (Video) getIntent().getSerializableExtra(Const.DATA);
        downloadedVideo = getOfflineDataIds(Objects.requireNonNull(data).getId(), Const.VIDEOS, activity, data.getId());

        id = getIntent().getStringExtra(Const.URL);
        type = getIntent().getStringExtra(com.emedicoz.app.utilso.Constants.Extras.TYPE);
        comment = (Comment) getIntent().getSerializableExtra(Const.COMMENT);
        videoId = getIntent().getStringExtra(Const.VIDEO_ID);
        parentId = getIntent().getStringExtra(Const.PARENT_ID);
        initView();
        if (!type.equalsIgnoreCase(Const.COMMENT_LIST)) {
            videoParentRL.setVisibility(View.VISIBLE);
            commentsParentRL.setVisibility(View.VISIBLE);
            rootView.setVisibility(View.VISIBLE);
            likeLayout.setVisibility(View.VISIBLE);
            singleRow.setVisibility(View.GONE);
            rl1.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(id)) {
                //networkCallForSingleVideoData();//networkCall.NetworkAPICall(API.API_GET_SINGLE_VIDEO_DATA, true);
            } else {
                initDatatoViews();
            }
        } else {
            initDatatoViews();
            commentLayout.setVisibility(View.VISIBLE);
            videoParentRL.setVisibility(View.GONE);
            rootView.setVisibility(View.GONE);
            rl1.setVisibility(View.GONE);
            singleRow.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            linearParams.setMargins(0, 300, 0, 0);
            commentRecyclerView.setLayoutParams(linearParams);
            commentRecyclerView.requestLayout();
            likeLayout.setVisibility(View.GONE);
            nameTV.setText(comment.getName());
            String date = (String) DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime()));
            String[] dates = date.split("\\s+");
            if (dates.length > 1) {
                if (dates[1].equalsIgnoreCase("minutes")) {
                    dates[1] = dates[1].substring(0, 1);
                } else {
                    dates[1] = dates[1].substring(0, 1);
                }
                if (DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime())).equals("0 minutes ago")) {
                    dateTV.setText(Const.JUST_NOW);
                } else {
                    String newDate = dates[0] + " " + dates[1];
                    dateTV.setText(newDate);
                }
            } else {
                dateTV.setText(dates[0]);
            }
            commentTV.setText(comment.getComment());
            likeComment.setText(comment.getLikes());
            Glide.with(this).load(comment.getProfile_picture()).into(imageIV);
            commentsParentRL.setVisibility(View.VISIBLE);
            networkCallForGetVideoComment("onCreate");//networkCall.NetworkAPICall(API.API_GET_VIDEO_COMMENT, true);
        }

        addScrollingOnRecyclerView();
        networkCallForViewVideo();

        if (data != null && data.getVideo_type().equalsIgnoreCase("1")) {
            Intent videoplayer = new Intent(getApplicationContext(), YouTubeVideoPlayer.class);
            videoplayer.putExtra(Const.YOUTUBE_ID, youtubevalidation(data.getURL()));
            startActivity(videoplayer);
            finish();
        } else if (data != null && data.getVideo_type().equalsIgnoreCase("2")) {
            Intent videoplayer = new Intent(getApplicationContext(), VimeoPlayer.class);
            String url = data.getURL();
            String[] wholeUrl = url.split("/");
            videoplayer.putExtra(Const.VIMEO_ID, wholeUrl[3]);
            startActivity(videoplayer);
            finish();
        } else {
            if (downloadedVideo != null && downloadedVideo.getRequestInfo() == null)
                downloadedVideo.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(downloadedVideo.getDownloadid()));

            if (downloadedVideo != null && downloadedVideo.getRequestInfo() != null) {
                if (downloadedVideo.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || downloadedVideo.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                    if (data != null && data.getLive_url() != null) {
                        if (!data.getLive_url().equals("")) {
                            initializePlayer(Helper.getCloudFrontUrl() + data.getLive_url(), Const.VIDEO_LIVE_MULTI);
                        } else {
                            initializePlayer(Helper.getCloudFrontUrl() + data.getURL(), Const.VIDEO_STREAM);
                        }
                    } else {
                        if (data != null && !GenericUtils.isEmpty(data.getURL()))
                            initializePlayer(Helper.getCloudFrontUrl() + data.getURL(), Const.VIDEO_STREAM);
                    }
                    //Toast.makeText(VideoDetail.this, R.string.video_download_in_progress, Toast.LENGTH_SHORT).show();

                }
                if (downloadedVideo.getRequestInfo() != null && downloadedVideo.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                    if ((activity.getFilesDir() + "/" + downloadedVideo.getLink()).contains(".mp4"))
                        initializePlayer(activity.getFilesDir() + "/" + downloadedVideo.getLink(), Const.VIDEO_STREAM);
                    else {
                        LocalSingleHttpServer mServer;
                        ProgressDialog progressDialog;
                        String path;
                        final String[] mUrl = {activity.getFilesDir() + "/" + downloadedVideo.getLink()};
                        //   if (type.equals(Const.VIDEO_STREAM)) {
                        try {
                            mServer = new LocalSingleHttpServer();
                            final Cipher c = getCipher();
                            if (c != null) {  // null means a clear video ; no need to set a decryption processing
                                mServer.setCipher(c);
                                mServer.start();
                                path = mUrl[0];
                                path = mServer.getURL(path);
                                initializePlayer(path, Const.VIDEO_STREAM);
                            }

                        } catch (Exception e) {  // exception management is not implemented in this demo code
                            // Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    // Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlinedata.getLink(), Const.VIDEO_STREAM);
                }
            } else {
                if (data != null && data.getLive_url() != null) {
                    if (!data.getLive_url().equals("")) {
                        initializePlayer(Helper.getCloudFrontUrl() + data.getLive_url(), Const.VIDEO_LIVE_MULTI);
                    } else {
                        initializePlayer(Helper.getCloudFrontUrl() + data.getURL(), Const.VIDEO_STREAM);
                    }
                } else {
                    if (data != null && !GenericUtils.isEmpty(data.getURL()))
                        initializePlayer(Helper.getCloudFrontUrl() + data.getURL(), Const.VIDEO_STREAM);
                }
            }
        }
        if (data != null)
            Log.e("onCreate: ", data.getId() + data.getURL() + data.getVideo_title());

        pullChatLayout.setOnClickListener(v -> {
            if (chatLayoutShown) {
                findViewById(R.id.layout_chat).setVisibility(View.VISIBLE);
                pullChatLayout.setRotation(0);
            } else {
                findViewById(R.id.layout_chat).setVisibility(View.GONE);
                pullChatLayout.setRotation(180);
            }
            chatLayoutShown = !chatLayoutShown;
        });
    }

    private void addScrollingOnRecyclerView() {
        commentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (totalItemCount >= 10) { // if the list is more than 10 then only pagination will work
                    if (loading && totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold) && !commentList.isEmpty() &&
                            commentList.size() > totalItemCount - 1) {
                        lastCommentId = commentList.get(totalItemCount - 1).getId();
                    }
                    networkCallForGetVideoComment("onScrolled");//networkCall.NetworkAPICall(API.API_GET_VIDEO_COMMENT, false);
                }
            }
        });
    }

    private void loadPlayerWithVideoURL(String url) {
//        url = "https://dams-apps-production.s3.ap-south-1.amazonaws.com/Test_Folder/doc_folder/4128039";
        getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                getPlayerFragment(AES.decrypt(url))).commit();

    }

    private Fragment getPlayerFragment(String url) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.exoplayer_container_layout);
        if (frag == null)
            return ExoPlayerFragment.newInstance(url, data.getId(), "2");
        else {
            if (url != null && !url.contains("http"))
                ((ExoPlayerFragment) frag).setMediaSource(url);
            return frag;
        }
    }

    private void initView() {

        videoToolbar = findViewById(R.id.videoToolbar);
        setSupportActionBar(videoToolbar);
        title = findViewById(R.id.title);

        rootView = findViewById(R.id.rootVideoDetail);
        rootView.setOnClickListener(this);
        debugRootView = findViewById(R.id.controls_root_video_detail);


        errorLayout = findViewById(R.id.errorLL);
        videoSV = findViewById(R.id.videoSV);
        tryAgainBtn = findViewById(R.id.tryAgainBtn);

        rl1 = findViewById(R.id.rl1);
        descriptionTV = findViewById(R.id.descriptionTV);
        videoImage = findViewById(R.id.video_image);
        imgBookmark = findViewById(R.id.imgbookmark);
        videoPlayerRL = findViewById(R.id.videoplayerRL);
        videoParentRL = findViewById(R.id.videoparentRL);
        commentsParentRL = findViewById(R.id.commentsparentRL);
        likeTV = findViewById(R.id.likeTV);
        viewTV = findViewById(R.id.viewTV);
        likeClickRL = findViewById(R.id.likeClickRL);
        commentRecyclerView = findViewById(R.id.recycler_view);
        writeComment = findViewById(R.id.writecommentET);

        likeLayout = findViewById(R.id.like_layout);

        taggedPeopleLL = findViewById(R.id.taggedpeopleLL);
        taggedPeopleFL = findViewById(R.id.taggedpeopleFL);

        Helper.CaptializeFirstLetter(writeComment);

        submitComment = findViewById(R.id.postcommentIBtn);
        noComment = findViewById(R.id.no_comment);
        commentLayout = findViewById(R.id.comment_layout);
        uploadedDate = findViewById(R.id.date);
        writeCommentLL = findViewById(R.id.writecommentLL);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(layoutManager);

        actionsRL = findViewById(R.id.actionsRL);
        downloadIV = findViewById(R.id.downloadIV);
        messageTV = findViewById(R.id.messageTV);
        deleteIV = findViewById(R.id.deleteIV);
        eyeIV = findViewById(R.id.eyeIV);
        downloadProgressBar = findViewById(R.id.downloadProgessBar);

        savedOfflineListener = this;

        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);

        tryAgainBtn.setOnClickListener(view1 -> retryApiButton());

        if (data != null && data.getIs_bookmarked() != null && !data.getIs_bookmarked().equals("0")) {
            imgBookmark.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ribbon_blue));
        }

        imgBookmark.setOnClickListener(v -> {
            if (data != null && data.getIs_bookmarked() != null) {
                if (data.getIs_bookmarked().equals("0")) {
                    networkcalltobookmark();
                } else {
                    Toast.makeText(VideoDetail.this, "Already Bookmarked", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        imgBack.setOnClickListener(v -> onBackPressed());

    }

    private void networkcalltobookmark() {
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.createBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), data.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean("status")) {
                            Constants.UPDATE_LIST = "true";
                            imgBookmark.setImageDrawable(ContextCompat.getDrawable(VideoDetail.this, R.mipmap.ribbon_blue));
                        }
                        Toast.makeText(activity, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Toast.makeText(activity, throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void initDatatoViews() {
        if (data != null) {
            if (data.getComments() != null) {
                if (!TextUtils.isEmpty(data.getComments()) && !data.getComments().equals("0")) {
                    networkCallForGetVideoComment("initDataViews");//networkCall.NetworkAPICall(API.API_GET_VIDEO_COMMENT, true);
                } else {
                    noComment.setVisibility(View.VISIBLE);
                    commentRecyclerView.setVisibility(View.GONE);
                }
            }

            if (data.getAllow_comments() != null) {
                if (!TextUtils.isEmpty(data.getAllow_comments()) && data.getAllow_comments().equals("0")) {
                    commentLayout.setVisibility(View.GONE);
                } else commentLayout.setVisibility(View.VISIBLE);
            }
            if (data.getVideo_title() != null) {
                title.setText(data.getVideo_title());
            }
            if (data.getLikes() != null) {
                if (data.getLikes().equals("0") || data.getLikes().equals("1")) {
                    likeTV.setText(data.getLikes() + " Like");
                } else {
                    likeTV.setText(data.getLikes() + " Likes");
                }
            }
            if (data.getViews() != null) {
                if (data.getViews().equals("0") || data.getViews().equals("1")) {
                    viewTV.setText(data.getViews() + " View");

                } else {
                    viewTV.setText(data.getViews() + " Views");

                }
            }
            if (data.getIs_like() != null) {
                if (data.getIs_like().equals("1")) {
                    likeTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                } else {
                    likeTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                }
            }
        /*if (data.getIs_like().equals("1")) {
            ((TextView) likeClickRL.getChildAt(1)).setTextColor(ContextCompat.getColor(this,R.color.blue));
            ((ImageView) likeClickRL.getChildAt(0)).setImageResource(R.mipmap.like_blue);
        } else {
            ((TextView) likeClickRL.getChildAt(1)).setTextColor(ContextCompat.getColor(this,R.color.black));
            ((ImageView) likeClickRL.getChildAt(0)).setImageResource(R.mipmap.like);
        }*/

            if (data.getCreation_time() != null) {
                final String date = "Published On " + (DateUtils.getRelativeTimeSpanString(Long.parseLong(data.getCreation_time())).equals("0 minutes ago") ? Const.JUST_NOW : DateUtils.getRelativeTimeSpanString(Long.parseLong(data.getCreation_time()))).toString();
                uploadedDate.setText(date);
            }
            //for tagging people UI
            if (!TextUtils.isEmpty(data.getScreen_tag())) {
                taggedPeopleLL.setVisibility(View.VISIBLE);

                if (taggedPeopleFL.getChildCount() > 0) taggedPeopleFL.removeAllViews();
                String[] tagList = data.getScreen_tag().split(",");
                if (tagList.length > 0) {
                    for (String str : tagList) {
                        AddViewToTagPeople(str);
                    }
                }
            } else taggedPeopleLL.setVisibility(View.GONE);


            descriptionTV.setText(data.getVideo_desc());

            videoPlayerRL.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(data.getThumbnail_url()).into(videoImage);
            actionsRL.setVisibility(View.GONE);

            if (data.getVideo_type() != null && data.getVideo_type().equalsIgnoreCase("0")) {
                actionsRL.setVisibility(View.VISIBLE);
                offlineData offlinedata = getOfflineDataIds(data.getId(), Const.VIDEOS, activity, data.getId());
                downloadIV.setImageResource(R.mipmap.download_new_course);
                if (offlinedata != null && offlinedata.getRequestInfo() == null)
                    offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

                if (offlinedata != null && offlinedata.getRequestInfo() != null) {
                    downloadProgressBar.setVisibility(offlinedata.getRequestInfo().getProgress() < 100 ? View.VISIBLE : View.GONE);

                    // 4 conditions to check at the time of intializing the video

                    //0. downloading in queue
                    if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        messageTV.setText(R.string.download_queued);
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.VIDEOS);
                    }
                    //1. downloading in progress
                    else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                            && offlinedata.getRequestInfo().getProgress() < 100) {

                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        downloadProgressBar.setVisibility(View.VISIBLE);
                        downloadProgressBar.setProgress(offlinedata.getRequestInfo().getProgress());
                        messageTV.setText(R.string.download_queued);

                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedOfflineListener, Const.VIDEOS);

                    }
                    //2. download complete
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE && offlinedata.getRequestInfo().getProgress() == 100) {

                        messageTV.setText(R.string.downloaded_offline);

                        downloadIV.setVisibility(View.GONE);

                        deleteIV.setVisibility(View.VISIBLE);

                        eyeIV.setVisibility(View.VISIBLE);

                    }
                    //3. downloading paused
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED && offlinedata.getRequestInfo().getProgress() < 100) {

                        downloadProgressBar.setVisibility(View.VISIBLE);
                        downloadProgressBar.setProgress(offlinedata.getRequestInfo().getProgress());

                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_pause);

                        messageTV.setText(R.string.download_pasued);

                    }
                    //4. error intrrupted
                    else if (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_ERROR && offlinedata.getRequestInfo().getProgress() < 100) {

                        messageTV.setText(R.string.error_in_downloading);

                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        downloadIV.setVisibility(View.VISIBLE);
                        downloadIV.setImageResource(R.mipmap.download_reload);
                    }
                    messageTV.setVisibility(offlinedata.getRequestInfo() != null ? View.VISIBLE : View.INVISIBLE);
                } else {
                    downloadIV.setVisibility(View.VISIBLE);
                    downloadIV.setImageResource(R.mipmap.download_new_course);
                }
            }

            deleteIV.setOnClickListener(v1 -> getdownloadcancelDialog(data, VideoDetail.this, "Delete Download", Const.CONFIRM_DELETE_DOWNLOAD));

            downloadIV.setOnClickListener(view1 -> {
                if (data.getVideo_type().equalsIgnoreCase("0")) {

                    if (data.getEnc_url() != null && data.getEnc_url().getToken() != null) {
                        offlineData offlineData = getOfflineDataIds(data.getId(),
                                Const.VIDEOS, VideoDetail.this, data.getId());

                        if (offlineData == null) {
                            View sheetView = Helper.openBottomSheetDialog(VideoDetail.this);
                            ArrayList<String> decryptedUrl = new ArrayList<>();
                            for (int i = 0; i < data.getEnc_url().getFiles().size(); i++) {
                                decryptedUrl.add(AES.decrypt(data.getEnc_url().getFiles().get(i).getUrl(), generatekey(data.getEnc_url().getToken()), generateVector(data.getEnc_url().getToken())));
                            }

                            TextView size720, size480, size360, size240;
                            final RadioButton radio1, radio2, radio3, radio4;
                            RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3, relativeLayout4;
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
                                    size720.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
                                    url720 = data.getEnc_url().getFiles().get(i).getUrl();
                                } else if (decryptedUrl.get(i).contains("480out_put")) {
                                    size480.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
                                    url480 = data.getEnc_url().getFiles().get(i).getUrl();
                                } else if (decryptedUrl.get(i).contains("360out_put")) {
                                    size360.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
                                    url360 = data.getEnc_url().getFiles().get(i).getUrl();
                                } else if (decryptedUrl.get(i).contains("240out_put")) {
                                    size240.setText(data.getEnc_url().getFiles().get(i).getSize() + " MB");
                                    url240 = data.getEnc_url().getFiles().get(i).getUrl();
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
                                    //         Toast.makeText(context, AES.decrypt(url720,generatekey(fileMetaData.getEnc_url().getToken()),generateVector(fileMetaData.getEnc_url().getToken())), Toast.LENGTH_SHORT).show();
                                    Helper.dismissBottonSheetDialog();
                                    startDownload(AES.decrypt(url720, generatekey(data.getEnc_url().getToken()), generateVector(data.getEnc_url().getToken())), data);
                                } else if (radio2.isChecked()) {
                                    Helper.dismissBottonSheetDialog();
                                    startDownload(AES.decrypt(url480, generatekey(data.getEnc_url().getToken()), generateVector(data.getEnc_url().getToken())), data);
                                    //     Toast.makeText(context, AES.decrypt(url480,generatekey(fileMetaData.getEnc_url().getToken()),generateVector(fileMetaData.getEnc_url().getToken())), Toast.LENGTH_SHORT).show();
                                } else if (radio3.isChecked()) {
                                    Helper.dismissBottonSheetDialog();
                                    startDownload(AES.decrypt(url360, generatekey(data.getEnc_url().getToken()), generateVector(data.getEnc_url().getToken())), data);
                                    //   Toast.makeText(context, AES.decrypt(url360,generatekey(fileMetaData.getEnc_url().getToken()),generateVector(fileMetaData.getEnc_url().getToken())), Toast.LENGTH_SHORT).show();
                                } else if (radio4.isChecked()) {
                                    Helper.dismissBottonSheetDialog();
                                    startDownload(AES.decrypt(url240, generatekey(data.getEnc_url().getToken()), generateVector(data.getEnc_url().getToken())), data);
                                    // Toast.makeText(context, AES.decrypt(url240,generatekey(fileMetaData.getEnc_url().getToken()),generateVector(fileMetaData.getEnc_url().getToken())), Toast.LENGTH_SHORT).show();
                                }
                            });


                            // startDownload(AES.decrypt(fileMetaData.getEnc_url().getUrl().get(0),generatekey(fileMetaData.getEnc_url().getToken()),generateVector(fileMetaData.getEnc_url().getToken())),fileMetaData);
                        } else {
                            //singleViewCLick(data, offlineData);
                        }
                    } else {

                        startDownloadNotEncrypted(data.getURL(), data);
                    }
                }

            });

            eyeIV.setOnClickListener((View view) -> {
                offlineData offlinedata = getOfflineDataIds(data.getId(), Const.VIDEOS, activity, data.getId());
                if (offlinedata != null && offlinedata.getRequestInfo() == null) {
                    offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

                    if (offlinedata.getRequestInfo() != null && offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                        if ((activity.getFilesDir() + "/" + offlinedata.getLink()).contains(".mp4"))
                            initializePlayer(activity.getFilesDir() + "/" + offlinedata.getLink(), Const.VIDEO_STREAM);
                        else {
                            LocalSingleHttpServer mServer;
                            ProgressDialog progressDialog;
                            File outputFile;
                            String path;
                            final String[] mUrl = {activity.getFilesDir() + "/" + offlinedata.getLink()};
                            try {
                                mServer = new LocalSingleHttpServer();
                                final Cipher c = getCipher();
                                if (c != null) {  // null means a clear video ; no need to set a decryption processing
                                    mServer.setCipher(c);
                                    mServer.start();
                                    path = mUrl[0];
                                    path = mServer.getURL(path);
                                    initializePlayer(path, Const.VIDEO_STREAM);
                                }

                            } catch (Exception e) {  // exception management is not implemented in this demo code
                                // Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            videoImage.setOnClickListener((View view) -> {

                //local video type
                if (data.getVideo_type().equals("0")) {
                    offlineData offlinedata = getOfflineDataIds(data.getId(), Const.VIDEOS, activity, data.getId());
                    if (offlinedata != null && offlinedata.getRequestInfo() == null)
                        offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

                    if (offlinedata == null) {
                        if (data.getLive_url() != null) {
                            if (!data.getLive_url().equals("")) {
                                String url = Helper.getCloudFrontUrl() + data.getLive_url();
                                Helper.GoToVideoActivity(VideoDetail.this, url, Const.VIDEO_LIVE_MULTI, data.getId(), Const.COURSE_VIDEO_TYPE);
                            }
                        } else {
                            networkCallForRequestVideoLink();//networkCall.NetworkAPICall(API.API_REQUEST_VIDEO_LINK, true);
                        }
                    } else if (offlinedata.getRequestInfo() != null && offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
                        Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlinedata.getLink(), Const.VIDEO_STREAM, data.getId(), Const.COURSE_VIDEO_TYPE);

                    } else if (offlinedata.getRequestInfo() != null &&
                            (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {

                        networkCallForRequestVideoLink();//networkCall.NetworkAPICall(API.API_REQUEST_VIDEO_LINK, true);
                    }
                }

                if (data.getVideo_type().equals("1")) {
                    Intent videoplayer = new Intent(getApplicationContext(), YouTubeVideoPlayer.class);
                    videoplayer.putExtra(Const.YOUTUBE_ID, youtubevalidation(data.getURL()));
                    startActivity(videoplayer);
                }

                // Vimeo Player Video Type
                else if (data.getVideo_type().equals("2")) {
                    Intent videoplayer = new Intent(getApplicationContext(), VimeoPlayer.class);
                    String url = data.getURL();
                    String[] whole_url = url.split("/");
                    videoplayer.putExtra(Const.VIMEO_ID, whole_url[3]);
                    startActivity(videoplayer);
                }

                if (data.getIs_viewed().equals("0")) {
                    networkCallForViewVideo();//networkCall.NetworkAPICall(API.API_ADD_VIEW_VIDEO, false);
                }
            });

            likeTV.setOnClickListener((View view) -> {
                likeTV.setEnabled(false);

                if (data.getIs_like().equals("1")) {

                    setLikes(data, 0);
                    networkCallForDislike();//networkCall.NetworkAPICall(API.API_DISLIKE_VIDEO, false);

                } else {
                    setLikes(data, 1);
                    networkCallForLikevideo();//networkCall.NetworkAPICall(API.API_LIKE_VIDEO, false);


                }
            });

        }
        submitComment.setOnClickListener((View view) -> {
            if (Helper.isConnected(getApplicationContext())) {
                CheckValidation();
            } else {
                Toast.makeText(getApplicationContext(), R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startDownloadNotEncrypted(String url, Video data) {

        offlineData offline = getOfflineDataIds(data.getId(), Const.VIDEOS, VideoDetail.this, data.getId());
        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        //when video is downloading
        if (offline != null && offline.getRequestInfo() != null &&
                (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
            Toast.makeText(VideoDetail.this, R.string.video_download_in_progress, Toast.LENGTH_SHORT).show();

        }
        //when video is paused
        else if (offline != null && offline.getRequestInfo() != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setText(R.string.download_pending);

            eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                    savedOfflineListener, "video");

        }

        //when some error occurred
        else if (offline != null && offline.getRequestInfo() != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setText(R.string.download_pending);

            eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                    savedOfflineListener, "video");

        } else if (offline != null && offline.getRequestInfo() != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
            messageTV.setText(R.string.downloaded_offline);
            downloadProgressBar.setVisibility(View.GONE);
            downloadIV.setVisibility(View.VISIBLE);
            downloadIV.setImageResource(R.mipmap.eye_on);
            deleteIV.setVisibility(View.VISIBLE);

            singleViewCLick(data, offline);
        }
        //for new download
        else {

            eMedicozDownloadManager.getInstance().showQualitySelectionPopup(VideoDetail.this,
                    data.getId(), Helper.getCloudFrontUrl() + data.getURL(),
                    Helper.getFileName(data.getURL(), data.getVideo_title(), Const.VIDEOS), Const.VIDEOS, data.getId(),
                    downloadid -> {

                        messageTV.setVisibility(View.VISIBLE);
                        downloadProgressBar.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        messageTV.setText(R.string.download_queued);

                        if (downloadid == com.emedicoz.app.utilso.Constants.MIGRATED_DOWNLOAD_ID) {
                            downloadIV.setVisibility(View.VISIBLE);
                            deleteIV.setVisibility(View.VISIBLE);
                            downloadIV.setImageResource(R.mipmap.eye_on);
                            messageTV.setVisibility(View.VISIBLE);
                            messageTV.setText(R.string.downloaded_offline);

                            if (downloadProgressBar.getVisibility() != View.GONE)
                                downloadProgressBar.setVisibility(View.GONE);
                        } else if (downloadid != 0) {
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, Const.VIDEOS);
                            if (data.getIs_viewed().equals("0"))
                                networkCallForViewVideo();//networkCall.NetworkAPICall(API.API_ADD_VIEW_VIDEO, false);

                        } else {
                            messageTV.setVisibility(View.INVISIBLE);
                            downloadProgressBar.setVisibility(View.GONE);
                            deleteIV.setVisibility(View.GONE);
                            downloadIV.setVisibility(View.VISIBLE);
                            downloadIV.setImageResource(R.mipmap.download_new_course);
                        }
                    });
        }

    }

    public void singleViewCLick(Video fileMetaData, offlineData offlineData) {
        if (offlineData == null) {
            offlineData = getOfflineDataIds(fileMetaData.getId(),
                    type, activity, fileMetaData.getId());
        }

        if (offlineData != null && offlineData.getRequestInfo() == null) {
            offlineData.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlineData.getDownloadid()));
        }
        if (offlineData != null && offlineData.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

            if (type.equalsIgnoreCase(Const.VIDEOS)) {
                if (fileMetaData.getEnc_url() != null && fileMetaData.getEnc_url().getToken() != null) {
                    if ((activity.getFilesDir() + "/" + offlineData.getLink()).contains(".mp4")) {
                        Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                    } else {
                        Helper.DecryptAndGoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, fileMetaData.getId(), Const.COURSE_VIDEO_TYPE);
                    }
                } else {
                    Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineData.getLink(), Const.VIDEO_STREAM, data.getId(), Const.COURSE_VIDEO_TYPE);
                }
            }
        }
    }

    // this is to show the tagged people in the post
    public void AddViewToTagPeople(final String text) {

        View v = View.inflate(activity, R.layout.single_textview_people_tag, null);
        TextView tv = v.findViewById(R.id.nameTV);
        ImageView delete = v.findViewById(R.id.deleteIV);
        delete.setVisibility(View.GONE);
        tv.setText(text);
        v.setTag(text);
        taggedPeopleFL.addView(v);
    }

    public void getdownloadcancelDialog(final Video video, final Activity ctx, final String title, final String message) {
        View v = Helper.newCustomDialog(ctx, true, title, message);

        Button btnCancel, btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(ctx.getString(R.string.no));
        btnSubmit.setText(ctx.getString(R.string.yes));

        btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

        btnSubmit.setOnClickListener((View view) -> {
            Helper.dismissDialog();
            eMedicozDownloadManager.removeOfflineData(video.getId(), Const.VIDEOS, ctx, video.getId());
            downloadIV.setVisibility(View.VISIBLE);
            if (downloadProgressBar.getVisibility() == View.VISIBLE) {
                downloadProgressBar.setVisibility(View.GONE);
                downloadProgressBar.setProgress(0);
            }
            messageTV.setVisibility(View.INVISIBLE);
            deleteIV.setVisibility(View.GONE);
            eyeIV.setVisibility(View.GONE);
            downloadIV.setImageResource(R.mipmap.download_new_course);
        });
    }

    public void changeLike() {
        VideoFragmentViewPager.IS_LIKE_UPDATE = true;
        sharedPreference.setLikeUpdate(data);
    }

    public void changeView() {
        VideoFragmentViewPager.IS_VIEW_UPDATE = true;
        sharedPreference.setViewUpdate(data);
    }

    public void callcommentview() {
        if (!data.getComments().equals("0")) {
            networkCallForGetVideoComment("callCommentViews");//networkCall.NetworkAPICall(API.API_GET_VIDEO_COMMENT, true);
        } else {
            noComment.setVisibility(View.VISIBLE);
            commentRecyclerView.setVisibility(View.GONE);
        }
    }

    private void networkCallForRequestVideoLink() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.requestVideoLink(data.getURL());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            String Url = GenericUtils.getJsonObject(jsonResponse).optString(Const.LINK);
                            Helper.GoToVideoActivity(VideoDetail.this, Url, Const.VIDEO_STREAM, data.getId(), Const.COURSE_VIDEO_TYPE);
                        } else {
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
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

    private void networkCallForSingleVideoData() {
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getSingleVideoData(SharedPreference.getInstance().getLoggedInUser().getId(), id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            data = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), Video.class);
                            initDatatoViews();
                        } else {
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
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

    public void networkCallForGetVideoComment(final String msg) {
        // mprogress.show();
        Call<JsonObject> response = null;
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Log.e("ABCD", "video_id" + data.getId() + "parent_id" + parentId + "last_comment_id" + lastCommentId);
        if (!type.equalsIgnoreCase(Const.COMMENT_LIST)) {
            response = apiInterface.getSingleVideoComment(SharedPreference.getInstance().getLoggedInUser().getId(), data.getId(), lastCommentId);
        } else {
            response = apiInterface.getSingleVideoCommmentList(SharedPreference.getInstance().getLoggedInUser().getId(), videoId, parentId, lastCommentId);
        }
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        // mprogress.dismiss();
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            if (!msg.equalsIgnoreCase("onScrolled") && !commentList.isEmpty()) {
                                commentList.clear();
                            }

                            JSONArray jsonArray = GenericUtils.getJsonArray(jsonResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Comment comment = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), Comment.class);
                                commentList.add(comment);
                            }
                            if (type.equalsIgnoreCase(Const.COMMENT_LIST)) {
                                commentLayout.setVisibility(View.VISIBLE);
                                videoParentRL.setVisibility(View.GONE);
                                rootView.setVisibility(View.GONE);
                                rl1.setVisibility(View.GONE);
                                likeLayout.setVisibility(View.GONE);
                                commentsParentRL.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(VideoDetail.this, "No Comment Found", Toast.LENGTH_SHORT).show();
                            // errorCallBack(jsonResponse.optString(Const.MESSAGE), API.API_GET_VIDEO_COMMENT);
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    InitCommentAdapter();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // mprogress.dismiss();
                // replaceErrorLayout(API.API_GET_VIDEO_COMMENT, 1, 2);
            }
        });
    }

    private void networkCallForAddVideoComment() {
        mProgress.show();
        Call<JsonObject> response = null;
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        if (!type.equalsIgnoreCase(Const.COMMENT_LIST)) {
            response = apiInterface.addVideoComment(SharedPreference.getInstance().getLoggedInUser().getId(), data.getId(),
                    StringEscapeUtils.escapeJava(writeComment.getText().toString()));
        } else {
            response = apiInterface.addVideoCommentList(SharedPreference.getInstance().getLoggedInUser().getId(),
                    videoId, parentId, StringEscapeUtils.escapeJava(writeComment.getText().toString()));
        }

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        mProgress.dismiss();
                        writeComment.setText("");
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            lastCommentId = "";
                            networkCallForGetVideoComment("addVideoComment");//networkCall.NetworkAPICall(API.API_GET_VIDEO_COMMENT, true);
                            noComment.setVisibility(View.GONE);
                            commentRecyclerView.setVisibility(View.VISIBLE);

                            Comment_Update();
                            if (type.equalsIgnoreCase(Const.COMMENT_LIST))
                                VideoFragmentViewPager.IS_COMMENT_UPDATE = true;
                        } else {
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
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

    private void networkCallForLikevideo() {
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.likeVideoData(SharedPreference.getInstance().getLoggedInUser().getId(),
                data.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        likeTV.setEnabled(true);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            changeLike();

                        } else {
                            errorCallBack(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_LIKE_VIDEO);
                            setLikes(data, 0);
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                replaceErrorLayout(API.API_LIKE_VIDEO, 1, getResources().getString(R.string.exception_api_error_message));
            }
        });
    }

    // apiType:  to save the API name on which the error occurred
    // isError:  Whether there is an error or success response
    // layoutType: Whether there is an internet issue or api Error like "Something went wrong"

    private void networkCallForDislike() {
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.dislikeVideoData(SharedPreference.getInstance().getLoggedInUser().getId(),
                data.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        likeTV.setEnabled(true);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("API_DISLIKE_POST ", "API_DISLIKE_POST true");
                            changeLike();
                        } else {
                            Log.e("API_DISLIKE_POST ", "API_DISLIKE_POST false");
                            setLikes(data, 1);
                            errorCallBack(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_DISLIKE_VIDEO);
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                replaceErrorLayout(API.API_DISLIKE_VIDEO, 1, getResources().getString(R.string.exception_api_error_message));
            }
        });
    }

    private void networkCallForViewVideo() {
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.addViewVideo(SharedPreference.getInstance().getLoggedInUser().getId(),
                data.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            if (data != null && data.getIs_viewed() != null) {
                                if (!data.getIs_viewed().equals("1")) {
                                    //Integer.valueOf(data.getViews())+1;
                                    data.setIs_viewed("1");
                                    data.setViews(String.valueOf(Integer.parseInt(data.getViews()) + 1));
                                    if (data.getViews().equals("0") || data.getViews().equals("1"))
                                        viewTV.setText(String.format("%s View", data.getViews()));
                                    else
                                        viewTV.setText(data.getViews() + " Views");
                                }
                            }
                            changeView();
                        } else {
                            errorCallBack(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_ADD_VIEW_VIDEO);
                            RetrofitResponse.getApiData(VideoDetail.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                replaceErrorLayout(API.API_ADD_VIEW_VIDEO, 1, getResources().getString(R.string.exception_api_error_message));
            }
        });
    }

    private void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {

            case API.API_LIKE_VIDEO:
                likeTV.setEnabled(true);

                Log.e("API_LIKE_POST ", "API_LIKE_POST false");
                if (!jsonString.equalsIgnoreCase("Video Already liked.")) setLikes(data, 0);

                break;
            case API.API_GET_VIDEO_COMMENT:
                if (TextUtils.isEmpty(lastCommentId)) {
                    errorMessage = jsonString;
                    apiType = apiType;
                }
                InitCommentAdapter();

                break;
            case API.API_DISLIKE_VIDEO:
                likeTV.setEnabled(true);

                Log.e("API_DISLIKE_POST ", "API_DISLIKE_POST false");
                if (!jsonString.equalsIgnoreCase("Video Already Disliked.")) setLikes(data, 1);

                break;
        }
        replaceErrorLayout(apiType, 1, jsonString);
    }

    public void InitCommentAdapter() {
        if (TextUtils.isEmpty(lastCommentId)) {
            if (!commentList.isEmpty()) {
                commentAdapter = new VideoCommentAdapter(commentList, VideoDetail.this, data);
                commentRecyclerView.setAdapter(commentAdapter);
                noComment.setVisibility(View.GONE);
                commentRecyclerView.setVisibility(View.VISIBLE);
            } else {
                noComment.setText(errorMessage);
                noComment.setVisibility(View.VISIBLE);
                commentRecyclerView.setVisibility(View.GONE);
                /*if (errorMessage.contains("Something went wrong")) {
                    replaceErrorLayout(apiType, 1, 2);
                }*/
            }
        } else commentAdapter.notifyDataSetChanged();
    }

    public void CheckValidation() {
        commentText = Helper.GetText(writeComment);
        boolean isDataValid = true;

        if (!GenericUtils.isListEmpty(newIds)) {
            for (String str : newIds) {
                if (!oldIds.contains(str)) {
                    if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                        taggedPeopleIdsAdded = str;
                    else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + str;
                }
            }
        }

        if (!GenericUtils.isListEmpty(oldIds)) {
            for (String str : oldIds) {
                if (!Objects.requireNonNull(newIds).contains(str)) {
                    if (TextUtils.isEmpty(taggedPeopleIdsRemoved))
                        taggedPeopleIdsRemoved = str;
                    else taggedPeopleIdsRemoved = taggedPeopleIdsRemoved + "," + str;
                }
            }
        }

        if (TextUtils.isEmpty(commentText))
            isDataValid = Helper.DataNotValid(writeComment);

        if (isDataValid) {
            // MainComment.setComment(commentText);
            networkCallForAddVideoComment();//networkCall.NetworkAPICall(API.API_ADD_VIDEO_COMMENT, true);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VideoFragmentViewPager.IS_COMMENT_UPDATE) {
            id = data.getId();
            //   networkCallForSingleVideoData();//networkCall.NetworkAPICall(API.API_GET_SINGLE_VIDEO_DATA, true);
            VideoFragmentViewPager.IS_COMMENT_UPDATE = false;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            eMedicozApp.getInstance().savePlayerState = true;
            return;
        }
        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase(Const.NOTIFICATION))
            Helper.GoToVideoFragment(this);
        else if (type.equalsIgnoreCase(Const.COMMENT_LIST)) {
            eMedicozApp.getInstance().playerState = null;
            finish();
            Intent intent = new Intent(this, VideoDetail.class); // VideoDetail to open the related comment replies from VideoCommentAdapter
            intent.putExtra(com.emedicoz.app.utilso.Constants.Extras.TYPE, Const.VIDEO);
            intent.putExtra("is_bookmarked", data.getIs_bookmarked());
            intent.putExtra(Const.DATA, data);
            startActivity(intent);
        } else {
            eMedicozApp.getInstance().savePlayerState = false;
            super.onBackPressed();
        }
    }

    public void setLikes(final Video video, int type) {
        String finallikes = "";
        String likes = video.getLikes();

        //this is to increment or decrement the likes on the post.
        if (type == 1) {
            video.setIs_like("1");
            finallikes = String.valueOf(Integer.parseInt(likes) + 1);
        } else if (type == 0) {
            video.setIs_like("0");
            finallikes = String.valueOf(Integer.parseInt(likes) - 1);
        } else
            finallikes = likes;

        // this is to change the icon and color of likes on the post.
        /*if (video.getIs_like().equals("1")) {
            ((TextView) likeClickRL.getChildAt(1)).setTextColor(ContextCompat.getColor(this,R.color.blue));
            ((ImageView) likeClickRL.getChildAt(0)).setImageResource(R.mipmap.like_blue);
        } else {
            ((TextView) likeClickRL.getChildAt(1)).setTextColor(ContextCompat.getColor(this,R.color.black));
            ((ImageView) likeClickRL.getChildAt(0)).setImageResource(R.mipmap.like);
        }*/
        if (video.getIs_like().equals("1")) {
            likeTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else {
            likeTV.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

        video.setLikes(finallikes);

        //this is to show the count of likes on the post.
        if (video.getLikes().equals("1") || video.getLikes().equals("0"))
            likeTV.setText(video.getLikes() + " Like");
        else
            likeTV.setText(video.getLikes() + " Likes");
    }

    public void Comment_Update() {
        VideoFragmentViewPager.IS_COMMENT_UPDATE = true;
        SharedPreference sharedPreference = SharedPreference.getInstance();
        sharedPreference.setCommentUpdate(data);
    }

    public void retryApiButton() {
       /* if (!TextUtils.isEmpty(apiType)) {
            networkCall.NetworkAPICall(apiType, true);
        }*/
        if (apiType == null) apiType = "";
        switch (apiType) {
            case API.API_ADD_VIEW_VIDEO:
                networkCallForViewVideo();
                break;
            case API.API_REQUEST_VIDEO_LINK:
                networkCallForRequestVideoLink();
                break;
            case API.API_GET_VIDEO_COMMENT:
                networkCallForGetVideoComment("addVideoComment");
                break;
            case API.API_LIKE_VIDEO:
                networkCallForLikevideo();
                break;
            case API.API_DISLIKE_VIDEO:
                networkCallForDislike();
                break;
            default:
                break;
        }
    }

    // apiType:  to save the API name on which the error occurred
    // isError:  Whether there is an error or success response
    // layoutType: Whether there is an internet issue or api Error like "Something went wrong"
    public void replaceErrorLayout(String apiType, int isError, String errorMessage) {
        if (downloadedVideo != null) return;

        this.apiType = apiType;
        errorImageIV.setImageResource(R.mipmap.no_internet);
        errorMessageTV.setText(errorMessage);
        writeCommentLL.setVisibility((isError == 1 ? View.GONE : View.VISIBLE));
        videoSV.setVisibility((isError == 1 ? View.GONE : View.VISIBLE));
        errorLayout.setVisibility((isError == 1 ? View.VISIBLE : View.GONE));
    }

    @Override
    public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {

        downloadIV.setVisibility(View.GONE);
        deleteIV.setVisibility(View.VISIBLE);
        downloadProgressBar.setVisibility(View.GONE);
        eyeIV.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(R.string.downloaded_offline);
        eMedicozDownloadManager.
                addOfflineDataIds(data.getId(), data.getURL(), activity,
                        false, true, Const.VIDEOS, requestInfo.getId(), data.getId());
    }

    @Override
    public void updateProgressUI(Integer value, int status, long id) {

        messageTV.setVisibility(View.VISIBLE);
        downloadProgressBar.setVisibility(View.VISIBLE);
        if (status == Fetch.STATUS_QUEUED) {

            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setText(R.string.download_queued);

        } else if (status == Fetch.STATUS_REMOVED) {

            downloadProgressBar.setProgress(0);
            downloadProgressBar.setVisibility(View.GONE);

            downloadIV.setVisibility(View.VISIBLE);
            downloadIV.setImageResource(R.mipmap.download_new_course);

            deleteIV.setVisibility(View.GONE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setVisibility(View.INVISIBLE);

        } else if (status == Fetch.STATUS_ERROR) {

            downloadIV.setImageResource(R.mipmap.download_reload);
            downloadIV.setVisibility(View.VISIBLE);

            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setText(R.string.error_in_downloading);

        } else if (status == Fetch.STATUS_DOWNLOADING) {

            downloadIV.setVisibility(View.GONE);

            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            if (value > 0) {
                messageTV.setText(String.format("Downloading...%d%%", value));
            } else
                messageTV.setText(R.string.download_pending);

        }

        downloadProgressBar.setProgress(value);
    }

/*    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || simpleExoPlayer == null)) {
            //initializePlayer();
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        if (!eMedicozApp.getInstance().savePlayerState) {
            eMedicozApp.getInstance().playerState = null;
            eMedicozApp.getInstance().savePlayerState = false;
        }
        releaseAdsLoader();
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    public void initializePlayer(String url, String type) {
        Log.e("initializePlayer: ", url);
        Intent intent = getIntent();
        loadPlayerWithVideoURL(url);

        String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
            try {

            } catch (Exception e) {
                showToast(R.string.ima_not_loaded);
            }
        } else {
            releaseAdsLoader();
        }
        inErrorState = false;
        updateButtonVisibilities();

        new Handler().post(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;

            Log.e("Widht & height", height + " -- " + width);
        });
    }


    private void showToast(int messageId) {
        Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_LONG).show();
    }

    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();

        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
        debugRootView.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
        debugRootView.addView(retryButton);


    }


    /**
     * Returns an ads media source, reusing the ads loader if one exists.
     *
     * @throws Exception Thrown if it was not possible to create an ads media source, for example, due
     *                   to a missing dependency.
     */

    private void releaseAdsLoader() {
        if (imaAdsLoader != null) {
            try {
                Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
                Method releaseMethod = loaderClass.getMethod("release");
                releaseMethod.invoke(imaAdsLoader);
            } catch (Exception e) {
                // Should never happen.
                throw new IllegalStateException(e);
            }
            imaAdsLoader = null;
            loadedAdTagUri = null;

        }
    }

    // Player.EventListener implementation

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        return super.dispatchKeyEvent(event);
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            //  initializePlayer();
            if (data.getLive_url() != null) {
                if (!data.getLive_url().equals("")) {
                    initializePlayer(Helper.getCloudFrontUrl() + data.getLive_url(), Const.VIDEO_LIVE_MULTI);
                } else {
                    initializePlayer(Helper.getCloudFrontUrl() + data.getURL(), Const.VIDEO_STREAM);
                }
            } else {
                if (!GenericUtils.isEmpty(data.getURL()))
                    initializePlayer(Helper.getCloudFrontUrl() + data.getURL(), Const.VIDEO_STREAM);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }


    @Override
    public void onStartEncoding() {
    }

    @Override
    public void onEncodingFinished() {
    }

    public void startDownload(String file_url, final Video data) {
        offlineData offline = getOfflineDataIds(data.getId(), Const.VIDEOS, VideoDetail.this, data.getId());
        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        //when video is downloading
        if (offline != null &&
                (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
            Toast.makeText(VideoDetail.this, R.string.video_download_in_progress, Toast.LENGTH_SHORT).show();

        }
        //when video is paused
        else if (offline != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setText(R.string.download_pending);

            eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                    savedOfflineListener, "video");

        }

        //when some error occurred
        else if (offline != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);
            messageTV.setText(R.string.download_pending);

            eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                    savedOfflineListener, "video");

        }
        //for new download
        else {

            eMedicozDownloadManager.getInstance().showQualitySelectionPopup(VideoDetail.this,
                    data.getId(), file_url, Helper.getFileName(file_url, data.getVideo_title(), Const.VIDEOS),
                    Const.VIDEOS, data.getId(), downloadid -> {

                        messageTV.setVisibility(View.VISIBLE);
                        downloadProgressBar.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        messageTV.setText(R.string.download_queued);

                        if (downloadid == com.emedicoz.app.utilso.Constants.MIGRATED_DOWNLOAD_ID) {
                            downloadIV.setVisibility(View.VISIBLE);
                            deleteIV.setVisibility(View.VISIBLE);
                            downloadIV.setImageResource(R.mipmap.eye_on);
                            messageTV.setVisibility(View.VISIBLE);
                            messageTV.setText(R.string.downloaded_offline);

                            if (downloadProgressBar.getVisibility() != View.GONE)
                                downloadProgressBar.setVisibility(View.GONE);
                        } else if (downloadid != 0) {
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, Const.VIDEOS);
                            if (data.getIs_viewed().equals("0"))
                                networkCallForViewVideo();//networkCall.NetworkAPICall(API.API_ADD_VIEW_VIDEO, false);

                        } else {
                            messageTV.setVisibility(View.INVISIBLE);
                            downloadProgressBar.setVisibility(View.GONE);
                            deleteIV.setVisibility(View.GONE);
                            downloadIV.setVisibility(View.VISIBLE);
                            downloadIV.setImageResource(R.mipmap.download_new_course);
                        }
                    });
        }
    }

    public Cipher getCipher() throws GeneralSecurityException {
        String iv = AES.strArrayvector;

        byte[] AesKeyData = AES.strArrayKey.getBytes();
        byte[] InitializationVectorData = iv.getBytes();
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(AesKeyData, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(InitializationVectorData));
        return cipher;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
