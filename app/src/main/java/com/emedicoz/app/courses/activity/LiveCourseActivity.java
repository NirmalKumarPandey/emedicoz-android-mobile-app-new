package com.emedicoz.app.courses.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.adapter.BookmarkAdapter;
import com.emedicoz.app.courses.adapter.PubSubAdapter;
import com.emedicoz.app.courses.callback.IndexBookmarkClicks;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.feeds.adapter.ChatAdapter;
import com.emedicoz.app.feeds.model.chatPojo;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.courses.Bookmark;
import com.emedicoz.app.modelo.liveclass.LiveClassVideoList;
import com.emedicoz.app.pubnub.PubSubPojo;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.DateTimeUtil;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.JsonUtil;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;
import com.view.circulartimerview.CircularTimerListener;
import com.view.circulartimerview.TimeFormatEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;


public class LiveCourseActivity extends AppCompatActivity implements View.OnClickListener, IndexBookmarkClicks, eMedicozDownloadManager.SavedOfflineVideoCallBack, TakeImageClass.ImageFromCropper, AmazonCallBack {

    public static final String TAG = LiveCourseActivity.class.getSimpleName();
    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    public static List<Bookmark> books = new ArrayList<>();
    public static List<String> pubSubChannel;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public String courseId = "", videoId = "";
    PubNub mPubnubDataStream;
    PubSubPnCallbackNew mPubSubPnCallback;
    PubSubAdapter mPubSub;
    ArrayList<PubSubPojo> values = new ArrayList<>();
    View rootView;
    int highRootView;
    Activity activity;
    LinearLayout indexChatLL;
    BookmarkAdapter bookmarkAdapter;
    ChildEventListener listener;
    String url;
    LiveClassVideoList data;
    ChatAdapter chatAdapter;
    EditText mMessage, chatMessage;
    ImageView ivSend;
    ImageView transIvSend;
    ImageView ivChat;
    ImageView pickImageIV;
    ImageView commentImageIV;
    ImageView commentDeleteIV;
    RelativeLayout commentImageRL;
    ArrayList<chatPojo> arrChat = new ArrayList<>();
    String urlType;
    ProgressBar downloadProgressBar;
    LinearLayout rl1;
    ImageView downloadIV, deleteIV;
    ImageView pullChatLayout;
    TextView messageTV;
    Query query;
    User userInfo;
    FrameLayout exoPlayerFragment;
    private eMedicozDownloadManager.SavedOfflineVideoCallBack savedOfflineListener;
    private CountDownTimer countDownTimer = null;
    private DatabaseReference mFirebaseDatabaseReference1;
    private LinearLayout debugRootView;
    public LinearLayout llChat;
    public LinearLayout transLlChat;
    private RelativeLayout studySingleItemLL;
    private RecyclerView recyclerChat;
    private RecyclerView transRecyclerView;
    private TextView tvHeading;
    private TextView indexBtn;
    private TextView chatBtn;
    private TextView bookmarkBtn;
    private Button retryButton;
    Bitmap bitmap;
    MediaFile mediaFile;
    public boolean isImageAdded = false;
    String datetime = "";
    // is_blocked has to be send 0 and when received 1 have to block particular user

    public boolean inErrorState;
    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private ScheduledExecutorService executorService;
    private boolean chatLayoutShown = false;
    DatabaseReference reference;
    ValueEventListener valueEventListener;
    TakeImageClass takeImageClass;
    s3ImageUploading s3ImageUploading;
    ProgressBar progressBar;
    String image = "";
    String user_blocked = "0";


// Activity lifecycle

    public static int math(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);

        activity = this;
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        setContentView(R.layout.activity_live_course);
        values = new ArrayList<>();
        userInfo = SharedPreference.getInstance().getLoggedInUser();
        savedOfflineListener = this;
        takeImageClass = new TakeImageClass(this, this);

        url = Objects.requireNonNull(getIntent().getExtras()).getString(Const.VIDEO_LINK);
        urlType = getIntent().getExtras().getString(Constants.Extras.TYPE);

        data = (LiveClassVideoList) getIntent().getSerializableExtra(Const.VIDEO);
        // Create a PlaylistItem that points to your content
        if (GenericUtils.isEmpty(Objects.requireNonNull(data).getLiveUrl())) {
            url = data.getFileUrl();
        } else {
            url = data.getLiveUrl();
        }
        loadPlayerWithVideoURL(url);
        Log.e(TAG, "URL: " + url);
        rootView = findViewById(R.id.root_new);
        highRootView = rootView.getHeight();
        rootView.setOnClickListener(this);
        recyclerChat = findViewById(R.id.recycler_view);
        transRecyclerView = findViewById(R.id.trans_recycler_view);

        transLlChat = findViewById(R.id.trans_Layout);
        llChat = findViewById(R.id.linearLayout);
        ivChat = findViewById(R.id.live_chat_image);
        indexChatLL = findViewById(R.id.llll);
        tvHeading = findViewById(R.id.tvHeading);
        ivSend = findViewById(R.id.iv_send);
        pickImageIV = findViewById(R.id.pickImageIV);
        transIvSend = findViewById(R.id.trans_iv_send);
        mMessage = findViewById(R.id.et_message);
        chatMessage = findViewById(R.id.chat_massage);
        downloadIV = findViewById(R.id.downloadIV);
        deleteIV = findViewById(R.id.deleteIV);
        messageTV = findViewById(R.id.messageTV);
        pullChatLayout = findViewById(R.id.pull_chat_layout);

        rl1 = findViewById(R.id.rl1);
        studySingleItemLL = findViewById(R.id.study_single_itemLL);
        debugRootView = findViewById(R.id.controls_root_video_detail);
        exoPlayerFragment = findViewById(R.id.exoplayer_container_layout);
        progressBar = findViewById(R.id.newpostprogress);
        progressBar.setScaleY(1f);
        progressBar.setVisibility(View.GONE);
        commentImageIV = findViewById(R.id.commentimageIV);
        commentDeleteIV = findViewById(R.id.commentdeleteIV);
        commentImageRL = findViewById(R.id.commentimageRL);
        downloadProgressBar = findViewById(R.id.downloadProgessBar);

        indexBtn = findViewById(R.id.index_btn);

        bookmarkBtn = findViewById(R.id.bookmark_btn);
        bookmarkBtn.setOnClickListener(view -> {
            disableAll();
            bookmarkBtn.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
            bookMarkApi(data.getId());

        });

        chatBtn = findViewById(R.id.chat_btn);

        if (data.getIsVod().equalsIgnoreCase("0")) {
            bookmarkBtn.setVisibility(View.GONE);
            rl1.setVisibility(View.GONE);
        } else {
            bookmarkBtn.setVisibility(View.VISIBLE);
            // rl1.setVisibility(View.VISIBLE);
            // checkVideoData(data);
        }

        chatBtn.setOnClickListener(view -> setChat());
        TextView txtDesc = findViewById(R.id.txt_desc);

        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);
        ivChat.setOnClickListener(v -> {
            try {
                if (ivChat.isSelected()) {
                    ivChat.setSelected(false);
                    transLlChat.setVisibility(View.GONE);
                } else {
                    ivChat.setSelected(true);
                    transLlChat.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        videoId = data.getId();
        courseId = SharedPreference.getInstance().getString(Constants.Extras.ID);
        studySingleItemLL.setVisibility(View.GONE);

        ivChat.setSelected(false);
        txtDesc.setText(data.getVideoTitle());
        blink();

        downloadIV.setOnClickListener(view -> startDownload(data));
        recyclerChat.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        ivSend.setOnClickListener(v -> {
            if (data.getChatPlatform() != null) {
                if (data.getChatPlatform().equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                    if (!GenericUtils.isEmpty(mMessage)) {
                        publish(v);
                    } else {
                        Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                    }
                } else {

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    datetime = dateformat.format(c.getTime());

                    if (arrChat.size() == 0) {
                        HashMap<String, String> hash = new HashMap<>();
                        hash.put(Constants.Extras.NAME, userInfo.getName());
                        hash.put(Constants.Extras.ID, userInfo.getId());
                        hash.put(Constants.Extras.DATE, datetime);
                    }

                    if (isImageAdded) {
                        uploadImageToS3Bucket();
                    } else {
                        if (!GenericUtils.isEmpty(mMessage)) {
                            chatPojo message = new chatPojo(userInfo.getId(), mMessage.getText().toString(), userInfo.getName(), datetime, "1", userInfo.getProfile_picture(), "android", userInfo.getDams_tokken(), image);
                            query.getRef().push().setValue(message);
                            mMessage.setText("");
                            Helper.HideKeyboard(LiveCourseActivity.this);
                        } else {
                            Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                        }
                    }
                }
            } else {
                if (!GenericUtils.isEmpty(mMessage)) {
                    publish(v);
                } else {
                    Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                }
            }
        });

        if (!GenericUtils.isEmpty(data.getChatPlatform())) {
            if (data.getChatPlatform().equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                pickImageIV.setVisibility(View.GONE);
            } else {
                pickImageIV.setVisibility(View.VISIBLE);
            }
        } else {
            pickImageIV.setVisibility(View.GONE);
        }
        pickImageIV.setOnClickListener(view1 -> takeImageClass.getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_image)));

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

        commentDeleteIV.setOnClickListener(view1 -> {
            isImageAdded = false;
            mediaFile = new MediaFile();
            image = "";
            commentImageRL.setVisibility(View.GONE);
        });

/*        circularTimerView.setCircularTimerListener(new CircularTimerListener() {
            @Override
            public String updateDataOnTick(long remainingTimeInMs) {
                return String.valueOf((int)Math.ceil((remainingTimeInMs / 1000.f)));
            }

            @Override
            public void onTimerFinished() {
                Toast.makeText(LiveCourseActivity.this, "FINISHED", Toast.LENGTH_SHORT).show();
                circularTimerView.setPrefix("");
                circularTimerView.setSuffix("");
                circularTimerView.setVisibility(View.GONE);            }
        }, 30, TimeFormatEnum.SECONDS, 10);*/
    }

    private void uploadImageToS3Bucket() {
        ArrayList<MediaFile> imageArrayList = new ArrayList<>();
        imageArrayList.add(mediaFile);
        s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_FIREBASE_CHAT, activity, this, progressBar);
        s3ImageUploading.execute(imageArrayList);
    }

    public void publish(View view) {
        HashMap<String, String> message = new HashMap<>();
        message.put(Constants.Extras.ID, userInfo.getId());
        message.put(Constants.ChatExtras.SENDER, userInfo.getName());
        message.put(Constants.Extras.MESSAGE, mMessage.getText().toString());
        message.put(Constants.ChatExtras.TIMESTAMP, DateTimeUtil.getTimeStampUtc());
        message.put(Constants.Extras.TYPE, Constants.ChatExtras.ANDROID);
        message.put(Constants.ChatExtras.PROFILE_PIC, userInfo.getProfile_picture());
        LiveCourseActivity.this.mPubnubDataStream.publish().channel(data.getChatNode()).message(message).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        try {
                            if (!status.isError()) {
                                mMessage.setText("");
                                Log.v(TAG, "publish(" + JsonUtil.asJson(result) + ")");
                            } else {
                                Log.v(TAG, "publishErr(" + JsonUtil.asJson(status) + ")");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    private void loadPlayerWithVideoURL(String url) {
        getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                getPlayerFragment(AES.decrypt(url))).commit();


    }

    private Fragment getPlayerFragment(String url) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.exoplayer_container_layout);
        if (frag == null) {
            if (data.getIsLive().equals("1"))
                return ExoPlayerFragment.newInstance(url);
            else
                return ExoPlayerFragment.newInstance(url, data.getId(), Const.COURSE_VIDEO_TYPE);
        } else
            return frag;
    }


    private void initPubNub() {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        config.setUuid(userInfo.getId());
        config.setSecure(true);

        this.mPubnubDataStream = new PubNub(config);
        //   this.mPubnub_Multi = new PubNub(config);
    }

    private void initChannels() {
        this.mPubnubDataStream.addListener(this.mPubSubPnCallback);
        this.mPubnubDataStream.subscribe().channels(pubSubChannel).execute();
    }

    private void disconnectAndCleanup() {
        getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE).edit().clear().apply();

        if (this.mPubnubDataStream != null) {
            this.mPubnubDataStream.unsubscribe().channels(pubSubChannel).execute();
            this.mPubnubDataStream.removeListener(this.mPubSubPnCallback);
            this.mPubnubDataStream.stop();
            this.mPubnubDataStream = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause: ", "onPause is called");
        if (!GenericUtils.isEmpty(data.getChatNode())) {
            if (data.getChatPlatform() != null) {
                if (data.getChatPlatform().equalsIgnoreCase(getString(R.string.pubnub))) {
                    disconnectAndCleanup();
                } else {
                    if (!data.getChatNode().isEmpty())
                        setUserOffline();
                    if (listener != null)
                        query.getRef().removeEventListener(listener);

                    if (valueEventListener != null)
                        reference.removeEventListener(valueEventListener);
                }
            } else {
                disconnectAndCleanup();
            }
        }


    }

    public void setAdapter() {
        PubSubAdapter adapter = new PubSubAdapter(this, values);
        recyclerChat.setAdapter(adapter);
    }

    private void startDownload(LiveClassVideoList data) {

        offlineData offline = getOfflineDataIds(data.getId(), Const.VIDEOS, activity, data.getId());
        if (offline != null && offline.getRequestInfo() == null)
            offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

        //when video is downloading
        if (offline != null &&
                (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
            Toast.makeText(activity, R.string.video_download_in_progress, Toast.LENGTH_SHORT).show();
        }
        //when video is paused
        else if (offline != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.download_pending);

            eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                    savedOfflineListener, Const.VIDEO);

        }

        //when some error occurred
        else if (offline != null && offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

            messageTV.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
            deleteIV.setVisibility(View.VISIBLE);
            messageTV.setText(R.string.download_pending);

            eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
            eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                    savedOfflineListener, Const.VIDEO);

        }
        //for new download
        else if (offline == null || offline.getRequestInfo() == null) {
            eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, data.getId(), data.getFileUrl(),
                    Helper.getFileName(data.getFileUrl(), data.getVideoTitle(), Const.VIDEOS), Const.VIDEOS, data.getId(),
                    downloadid -> {

                        messageTV.setVisibility(View.VISIBLE);
                        downloadProgressBar.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.GONE);
                        downloadIV.setImageResource(R.mipmap.download_download);
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
                            eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedOfflineListener, Const.VIDEO);

                        } else {
                            messageTV.setVisibility(View.INVISIBLE);
                            downloadProgressBar.setVisibility(View.GONE);
                            downloadIV.setVisibility(View.VISIBLE);
                            deleteIV.setVisibility(View.GONE);
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
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");

        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();


    }


    @Override
    public void onResume() {
        super.onResume();
        if (!data.getChatNode().isEmpty()) {
            if (data.getChatPlatform() != null) {
                if (data.getChatPlatform().equalsIgnoreCase(getString(R.string.pubnub))) {
                    pubSubChannel = Collections.singletonList(data.getChatNode());
                    this.mPubSubPnCallback = new PubSubPnCallbackNew(this.mPubSub);
                    initPubNub();
                    initChannels();
                } else {
                    fireBaseOperation();
                }
            } else {
                pubSubChannel = Collections.singletonList(data.getChatNode());
                this.mPubSubPnCallback = new PubSubPnCallbackNew(this.mPubSub);
                initPubNub();
                initChannels();
            }

            tvHeading.setText(R.string.live_chat);
            ((TextView) findViewById(R.id.chat_btn)).setText(R.string.live_chat);
            tvHeading.setVisibility(View.VISIBLE);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setChatLLVisibility();
                recyclerChat.setVisibility(View.VISIBLE);
                chatBtn.setVisibility(View.VISIBLE);
            } else {
                if (data.getIsLive().equals("1")) {
                    setChatLLVisibility();
                    recyclerChat.setVisibility(View.VISIBLE);
                    chatBtn.setVisibility(View.VISIBLE);
                } else {
                    llChat.setVisibility(View.GONE);
                    recyclerChat.setVisibility(View.GONE);
                    chatBtn.setVisibility(View.GONE);
                }
            }
        } else {
            tvHeading.setVisibility(View.GONE);
            tvHeading.setText("");
            llChat.setVisibility(View.GONE);
            recyclerChat.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
        }
    }

    private void setChatLLVisibility() {
        if (user_blocked.equals("1"))
            llChat.setVisibility(View.GONE);
        else
            llChat.setVisibility(View.VISIBLE);
    }

    private void fireBaseOperation() {
        setUserOnline();
        setUserBlocked();
        //--------------------------This node is for testing image upload----------------------------
        // query = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_testing/" + "admin").limitToLast(10);

        //--------------------------This is the main node that is used for chatting----------------------------
        query = FirebaseDatabase.getInstance().getReference().child("course_emedicoz_appsquadz/" + data.getChatNode()).limitToLast(10);
        arrChat.clear();
        chatAdapter = new ChatAdapter(this, "poll", arrChat);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerChat.setAdapter(chatAdapter);

        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "onChildAdded: ");
                getFirebaseData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // When Child Element Changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // When Child Element Removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // When Child Element Moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addChildEventListener(listener);

        //  getSingleValue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eMedicozApp.getInstance().playerState = null;
        if (executorService != null) executorService.shutdown();
        releaseAdsLoader();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

// OnClickListener methods

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        return super.dispatchKeyEvent(event);
    }

// PlaybackControlView.VisibilityListener implementation

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            initializePlayer();
        }
    }
/*

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("Config Changed", newConfig.orientation + "");
        // 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 1) {
            transLlChat.setVisibility(View.GONE);
            chatMessage.setVisibility(View.GONE);

            ivChat.setVisibility(View.GONE);
            studySingleItemLL.setVisibility(View.VISIBLE);
            recyclerChat.setVisibility(View.VISIBLE);
            transRecyclerView.setVisibility(View.GONE);
            tvHeading.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (200 * scale + 0.5f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);
            rootView.setLayoutParams(layoutParams);
        } else {
            chatMessage.setVisibility(View.GONE);
            transLlChat.setVisibility(View.GONE);
            transRecyclerView.setVisibility(View.GONE);
            recyclerChat.setVisibility(View.GONE);
            studySingleItemLL.setVisibility(View.GONE);
            recyclerChat.setVisibility(View.GONE);
            tvHeading.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels);
            rootView.setLayoutParams(layoutParams);
        }
    }
*/

    private void initializePlayer() {
        Intent intent = getIntent();
        String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
        } else {
            releaseAdsLoader();
        }
        inErrorState = false;
        updateButtonVisibilities();
    }

    private void blink() {
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        final Handler handler = new Handler();
        new Thread(() -> {
            int timeToBlink = 2000;    //in milissegunds
            try {
                Thread.sleep(timeToBlink);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(this::blink);
        }).start();
    }

    @Override
    public void onBackPressed() {
        int orientation1 = getResources().getConfiguration().orientation;
        if (orientation1 == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (countDownTimer != null) countDownTimer.cancel();
            super.onBackPressed();
        }
    }

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


    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();

        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
        debugRootView.setVisibility(inErrorState ? View.VISIBLE : View.GONE);

        debugRootView.addView(retryButton);


    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    private void setUserOffline() {
        if (mFirebaseDatabaseReference1 != null)
            mFirebaseDatabaseReference1.child(userInfo.getId()).child(Constants.ChatExtras.ONLINE).setValue(ServerValue.TIMESTAMP);
    }

    private void setUserOnline() {
        mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live/" + data.getChatNode());
        mFirebaseDatabaseReference1.child(userInfo.getId()).child(Constants.ChatExtras.ONLINE).setValue("true");
        mFirebaseDatabaseReference1.child(userInfo.getId()).child(Constants.Extras.NAME).setValue(userInfo.getName());
        mFirebaseDatabaseReference1.child(userInfo.getId()).child(Constants.ChatExtras.PROFILE_PICTURE).setValue(userInfo.getProfile_picture());
        mFirebaseDatabaseReference1.child(userInfo.getId()).child(Constants.ChatExtras.ERP_TOKEN).setValue(userInfo.getDams_tokken());
        mFirebaseDatabaseReference1.child(userInfo.getId()).child(Constants.Extras.TYPE).setValue(Constants.ChatExtras.ANDROID);
    }

    public void setUserBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live/" + data.getChatNode()).child(SharedPreference.getInstance().getLoggedInUser().getId()).child("is_blocked");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: is_blocked - " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().toString().equals("1")) {
                    user_blocked = "1";
                    llChat.setVisibility(View.GONE);
                } else {
                    user_blocked = "0";
                    llChat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBookMark() {
        recyclerChat.setVisibility(View.GONE);
        chatBtn.setVisibility(View.GONE);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        llChat.setVisibility(View.GONE);
        recyclerChat.setAdapter(bookmarkAdapter);
    }

    private void setChat() {
        if (!data.getChatNode().isEmpty()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setChatLLVisibility();
            } else {
                if (data.getIsLive().equals("1")) {
                    setChatLLVisibility();
                    recyclerChat.setVisibility(View.VISIBLE);
                    chatBtn.setVisibility(View.VISIBLE);
                } else {
                    llChat.setVisibility(View.GONE);
                    recyclerChat.setVisibility(View.GONE);
                    chatBtn.setVisibility(View.GONE);
                }
            }
        } else {
            llChat.setVisibility(View.GONE);
            recyclerChat.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
        }
        disableAll();
        chatBtn.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
        recyclerChat.setAdapter(chatAdapter);
    }

    void disableAll() {
        bookmarkBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.off_white));
        indexBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.off_white));
        chatBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.off_white));
    }

    private void bookMarkApi(String id) {
        final Progress progress = new Progress(this);
        progress.setCancelable(false);
        progress.show();
        ApiInterface api = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = api.getVideoSeekTime(userInfo.getId()
                , id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (!GenericUtils.isListEmpty(books)) {
                                books.clear();
                            }
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                Bookmark bookmark = gson.fromJson(dataObj.toString(), Bookmark.class);
                                books.add(bookmark);
                            }

                            bookmarkAdapter = new BookmarkAdapter(LiveCourseActivity.this, books);
                            setBookMark();
                        }
                        progress.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                progress.dismiss();
            }
        });
    }

    @Override
    public void onSeek(Bookmark data) {

        if (inErrorState) {
            Toast.makeText(this, "Unable to play video", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDelete(Bookmark data) {
        // BookMarkApi(data.getId(), "", "", "2");
    }

    @Override
    public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {
        downloadIV.setVisibility(View.VISIBLE);
        downloadProgressBar.setVisibility(View.GONE);
        downloadIV.setImageResource(R.mipmap.eye_on);
        deleteIV.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(R.string.downloaded_offline);
        eMedicozDownloadManager.
                addOfflineDataIds(data.getId(), data.getFileUrl(), activity,
                        false, true, Const.VIDEOS, requestInfo.getId(), data.getId());
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

        downloadProgressBar.setProgress(value);
    }

    @Override
    public void onStartEncoding() {

    }

    @Override
    public void onEncodingFinished() {

    }

    private void getFirebaseData(DataSnapshot dataSnapshot) {
        chatPojo message = dataSnapshot.getValue(chatPojo.class);
        if ((!Objects.requireNonNull(message).getType().equalsIgnoreCase("admin")) || (message.getOriginal() != null)) {
            // if ((!message.getType().equalsIgnoreCase("admin")) || (message.getOriginal()!=null))
            if (arrChat.size() == 15) {
                arrChat.remove(0);
                arrChat.add(14, message);
            } else {
                arrChat.add(message);
            }
        } else if (message.getType().equalsIgnoreCase("admin") && message.getOriginal() == null) {
            if (arrChat.size() == 15) {
                arrChat.remove(0);
                arrChat.add(14, message);
            } else {
                arrChat.add(message);
            }
        }
        chatAdapter.notifyDataSetChanged();
        recyclerChat.smoothScrollToPosition(arrChat.size());
    }

    private void scrollChatToBottom() {
        recyclerChat.scrollToPosition(values.size() - 1);
    }

    @Override
    public void imagePath(String str) {
        if (str != null) {
            bitmap = BitmapFactory.decodeFile(str);
            if (bitmap != null) {
                isImageAdded = true;
                mediaFile = new MediaFile();
                mediaFile.setFile_type(Const.IMAGE);
                mediaFile.setImage(bitmap);
                mediaFile.setFile(str);
                initImageView(mediaFile);
            }
        }
    }

    public void initImageView(MediaFile image) {
        commentImageIV.setImageBitmap(image.getImage());
        commentImageIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        commentImageRL.setVisibility(View.VISIBLE);
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {
        if (!images.isEmpty()) {
            Log.e(TAG, "onS3UploadData: " + images.get(0).getFile());

            for (MediaFile media : images) {
                image = media.getFile();
                Log.e("Tag", image);
                chatPojo message = new chatPojo(userInfo.getId(), mMessage.getText().toString(), userInfo.getName(), datetime, "1", userInfo.getProfile_picture(), "android", userInfo.getDams_tokken(), image);
                query.getRef().push().setValue(message);
                mMessage.setText("");
                Helper.HideKeyboard(LiveCourseActivity.this);
                isImageAdded = false;
                image = "";
                commentImageRL.setVisibility(View.GONE);
            }
        }
    }

    public class PubSubPnCallbackNew extends SubscribeCallback {
        private final String TAG = LiveStreamActivity.PubSubPnCallbackNew.class.getName();


        public PubSubPnCallbackNew(PubSubAdapter chatAdapter) {
        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
 /*           if (status.getOperation() == PNOperationType.PNSubscribeOperation && status.getAffectedChannels()
                    .contains(Constants.CHANNEL_NAME)) {
                fetchHistory();
            }*/
            // fetchHistory();
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            try {
                try {
                    JsonNode jsonMsg = message.getMessage();
                    PubSubPojo msg = JsonUtil.convert(jsonMsg, PubSubPojo.class);
                    if (values.size() == 50) {
                        values.remove(0);
                        values.add(49, msg);
                    } else {
                        values.add(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    setAdapter();
                    scrollChatToBottom();
                    // Stuff that updates the UI

                });
                for (int i = 0; i < values.size(); i++) {
                    Log.v(TAG, "MESSAGE: " + values.get(i).getMessage());
                }
                // adapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            // no presence handling for simplicity
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (takeImageClass != null)
            takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    private void getSingleValue() {

        reference = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live").child("chat").child("status");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: chat -> status - " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().toString().equals("0")) {
                        indexChatLL.setVisibility(View.GONE);
                        recyclerChat.setVisibility(View.GONE);
                        llChat.setVisibility(View.GONE);
                    } else {
                        indexChatLL.setVisibility(View.VISIBLE);
                        recyclerChat.setVisibility(View.VISIBLE);
                        setChatLLVisibility();
                    }
                } else {
                    indexChatLL.setVisibility(View.VISIBLE);
                    recyclerChat.setVisibility(View.VISIBLE);
                    setChatLLVisibility();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getPollVisibilityRef() {
        DatabaseReference ref = null;
    //    ref = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live_Poll/").child(chatNode).child("pollVisibility").child("isPollVisible");
        //ref.setValue("1");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: isPollVisible - " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().toString().equals("1")) {
                    //  user_blocked = "1";
//                    newPollLL.setVisibility(View.VISIBLE);
                } else {
                    //  user_blocked = "0";
//                    newPollLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
