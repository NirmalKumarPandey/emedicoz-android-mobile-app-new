package com.emedicoz.app.courses.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.PollOptionSelectedInterface;
import com.emedicoz.app.courses.adapter.PubSubAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.feeds.adapter.ChatAdapter;
import com.emedicoz.app.feeds.model.chatPojo;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.Poll;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.polls.adapter.PollsOptionsAdapter;
import com.emedicoz.app.polls.model.Options;
import com.emedicoz.app.polls.model.SocketGeneralObject;
import com.emedicoz.app.polls.model.SocketPollsObject;
import com.emedicoz.app.pubnub.PubSubPojo;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
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
import com.emedicoz.app.utilso.network.API;
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
import com.view.circulartimerview.CircularTimerListener;
import com.view.circulartimerview.CircularTimerView;
import com.view.circulartimerview.TimeFormatEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;


public class LiveStreamActivity extends AppCompatActivity implements View.OnClickListener, TakeImageClass.ImageFromCropper, AmazonCallBack {

    public static final String TAG = LiveStreamActivity.class.getSimpleName();
    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";


    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    public static List<String> PUBSUB_CHANNEL = Arrays.asList(Constants.CHANNEL_NAME);

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public String courseId = "", videoId = "", questionId = "";
    PubNub mPubnubDataStream;
    PubSubPnCallbackNew mPubSubPnCallback;
    PubSubAdapter mPubSub;
    ArrayList<PubSubPojo> values = new ArrayList<>();
    View rootView;
    int highRootView;
    public Query query;
    boolean isLimited = false;
    Activity activity;
    String image = "";

    // is_blocked has to be send 0 and when received 1 have to block particular user
    String is_blocked = "0";

    long remainingTime;
    String url, chat_platform;
    Video data;
    int videoTimeMS;
    ChatAdapter chatAdapter;
    LinearLayout llll;
    EditText mMessage, chatMessage;
    ImageView ivSend;
    ImageView transIvSend;
    ImageView ivChat;
    ImageView commentImageIV;
    ImageView commentDeleteIV;
    RelativeLayout commentImageRL;
    ArrayList<chatPojo> arrChat = new ArrayList<>();
    public ArrayList<String> nodes = new ArrayList<>();
    String urlType;
    ChildEventListener listener;
    String chat_status = "1";
    FrameLayout exoPlayerFragment;
    private DatabaseReference mFirebaseDatabaseReference1;
    private LinearLayout debugRootView;
    public LinearLayout llChat;
    public LinearLayout trans_llChat;
    private RelativeLayout studySingleItemLL;
    private RecyclerView recyclerChat;
    private TextView tvHeading;
    private TextView indexBtn;
    private TextView chatBtn;
    private TextView bookmarkBtn;
    private TextView txtTimer;
    private Button retryButton;

    ImageView pickImageIV;
    TakeImageClass takeImageClass;
    ValueEventListener valueEventListener;
    DatabaseReference reference;
    private boolean chatLayoutShown = false;
    ImageView pull_chat_layout;
    public boolean inErrorState;
    s3ImageUploading s3ImageUploading;
    ProgressBar progressBar;
    Bitmap bitmap;
    MediaFile mediaFile;
    public boolean isImageAdded = false;
    String datetime = "";

    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private ScheduledExecutorService executorService;
    String user_blocked = "0";

    //    public static final String SERVER_IP = "3.7.22.193";
//    public static final String SERVER_IP = "65.2.57.25";
    public static final String SERVER_IP = Constants.BASE_DOMAIN_URL;
    public static final int SERVER_PORT = 2050;
    private ClientThread clientThread;
    private Thread thread;
    private WebSocketClient webSocketClient;
    private Socket socket;
    private PollsOptionsAdapter pollsOptionsAdapter;
    private String selectedOptionId = "-1";
    private String correctOptionId;
    private ArrayList<Options> optionsList;
    Gson gson;
    Progress mProgress;
    TextView remarkTextview;
    private String chatNode = "";
    private boolean isFromLiveCourse = false;
    LinearLayout newPollLL;
    RelativeLayout liveChatRL;
    RelativeLayout livePollRL;
    CircularTimerView circularTimerView;
    TextView viewPollBtn;
    TextView progressText;
    private long pollTimer;
    TextView viewPollTV;
    Query queryPollTiming;
    Query queryPollVisible;
    private String isPollVisible = "";

    public static int math(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        clearResumePosition();
        gson = new Gson();

        activity = this;
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_live_stream);
        values = new ArrayList<>();
        takeImageClass = new TakeImageClass(this, this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString(Const.VIDEO_LINK);
            urlType = getIntent().getExtras().getString(Constants.Extras.TYPE);
            data = (Video) getIntent().getExtras().getSerializable(Const.VIDEO);
            videoId = getIntent().getExtras().getString("id");
            chat_platform = getIntent().getExtras().getString(Constants.ChatExtras.CHAT_PALTFORM);
            chatNode = getIntent().getExtras().getString(Const.CHAT_NODE) != null ? getIntent().getExtras().getString(Const.CHAT_NODE) : "";
            isFromLiveCourse = getIntent().getExtras().getBoolean(Const.IS_FROM_LIVE_COURSE);
        }

        if (GenericUtils.isEmpty(url)) {
            url = "https://d2enu63wt1sf3u.cloudfront.net/vod/7240861610883563.m3u8";
            videoId = "1738";
        }

        createWebSocketClient();//directly connect to 3.7.22.193:2050
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();

        Log.e(TAG, "URL: " + url);
        rootView = findViewById(R.id.root_new);
        highRootView = rootView.getHeight();
        rootView.setOnClickListener(this);
        recyclerChat = findViewById(R.id.recycler_view);
        trans_llChat = findViewById(R.id.trans_Layout);
        llChat = findViewById(R.id.linearLayout);
        pull_chat_layout = findViewById(R.id.pull_chat_layout);
        ivChat = findViewById(R.id.live_chat_image);
        tvHeading = findViewById(R.id.tvHeading);
        ivSend = findViewById(R.id.iv_send);
        pickImageIV = findViewById(R.id.pickImageIV);
        transIvSend = findViewById(R.id.trans_iv_send);
        llll = findViewById(R.id.llll);
        mMessage = findViewById(R.id.et_message);
        chatMessage = findViewById(R.id.chat_massage);
        studySingleItemLL = findViewById(R.id.study_single_itemLL);
        debugRootView = findViewById(R.id.controls_root_video_detail);
        exoPlayerFragment = findViewById(R.id.exoplayer_container_layout);
        progressBar = findViewById(R.id.newpostprogress);
        progressBar.setScaleY(1f);
        progressBar.setVisibility(View.GONE);
        commentImageIV = findViewById(R.id.commentimageIV);
        commentDeleteIV = findViewById(R.id.commentdeleteIV);
        commentImageRL = findViewById(R.id.commentimageRL);
        indexBtn = findViewById(R.id.index_btn);
        txtTimer = findViewById(R.id.txt_timer);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        chatBtn = findViewById(R.id.chat_btn);
        liveChatRL = findViewById(R.id.liveChatRL);
        livePollRL = findViewById(R.id.livePollRL);
        newPollLL = findViewById(R.id.newPollLL);
        viewPollBtn = findViewById(R.id.viewPollBtn);
        viewPollTV = findViewById(R.id.viewPollTV);
        progressText = findViewById(R.id.progressText);
        viewPollBtn.setOnClickListener(v -> {
            livePollRL.setVisibility(View.VISIBLE);
            llChat.setVisibility(View.GONE);
            newPollLL.setVisibility(View.GONE);
            changeTabBackground(Constants.Extras.LIVE_POLL);
            chatAdapter.setType("poll");
            chatAdapter.notifyDataSetChanged();
            recyclerChat.smoothScrollToPosition(arrChat.size());
        });
        bookmarkBtn.setVisibility(View.GONE);
        loadPlayerWithVideoURL(url);

        liveChatRL.setOnClickListener(view1 -> {
            changeTabBackground(Constants.Extras.LIVE_CHAT);
            setChat();
            if (isPollVisible.equals("1"))
                newPollLL.setVisibility(View.VISIBLE);
            chatAdapter.setType("");
            chatAdapter.notifyDataSetChanged();
            recyclerChat.smoothScrollToPosition(arrChat.size());
        });
        livePollRL.setOnClickListener(view -> {
            changeTabBackground(Constants.Extras.LIVE_POLL);
            llChat.setVisibility(View.GONE);
            newPollLL.setVisibility(View.GONE);
            chatAdapter.setType("poll");
            chatAdapter.notifyDataSetChanged();
            recyclerChat.smoothScrollToPosition(arrChat.size());
        });
        retryButton = findViewById(R.id.retry_button_video_detail);
        retryButton.setOnClickListener(this);
        ivChat.setOnClickListener(view1 -> {
            try {
                if (ivChat.isSelected()) {
                    ivChat.setSelected(false);
                    trans_llChat.setVisibility(View.GONE);
                } else {
                    ivChat.setSelected(true);
                    trans_llChat.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        courseId = SharedPreference.getInstance().getString(Constants.Extras.ID);
        studySingleItemLL.setVisibility(View.GONE);
        ivChat.setSelected(false);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        ivSend.setOnClickListener((View v) -> {
            if (chat_platform != null) {
                if (chat_platform.equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                    if (!GenericUtils.isEmpty(mMessage)) {
                        publish(v);
                    } else {
                        Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                    }
                } else {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    datetime = dateformat.format(c.getTime());

                    if (arrChat.isEmpty()) {
                        HashMap<String, String> hash = new HashMap<>();
                        hash.put(Constants.Extras.NAME, SharedPreference.getInstance().getLoggedInUser().getName());
                        hash.put(Constants.Extras.ID, SharedPreference.getInstance().getLoggedInUser().getId());
                        hash.put(Constants.Extras.DATE, datetime);
                    }
                    if (isImageAdded) {
                        uploadImageToS3Bucket();
                    } else {
                        if (!GenericUtils.isEmpty(mMessage)) {
                            chatPojo message = new chatPojo(SharedPreference.getInstance().getLoggedInUser().getId(), mMessage.getText().toString(), SharedPreference.getInstance().getLoggedInUser().getName(), datetime, "1", SharedPreference.getInstance().getLoggedInUser().getProfile_picture(), "android", SharedPreference.getInstance().getLoggedInUser().getDams_tokken(), image, "normal", null);
                            if (query != null)
                                query.getRef().push().setValue(message);
                            mMessage.setText("");
                            Helper.HideKeyboard(LiveStreamActivity.this);
                        } else {
                            Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                        }
                    }
                }
            } else {
/*                if (!GenericUtils.isEmpty(mMessage)) {
                    publish(v);
                } else {
                    Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                }*/

                if (!GenericUtils.isEmpty(mMessage)) {
                    chatPojo message = new chatPojo(SharedPreference.getInstance().getLoggedInUser().getId(), mMessage.getText().toString(), SharedPreference.getInstance().getLoggedInUser().getName(), datetime, "1", SharedPreference.getInstance().getLoggedInUser().getProfile_picture(), "android", SharedPreference.getInstance().getLoggedInUser().getDams_tokken(), image, "normal", null);
                    if (query != null)
                        query.getRef().push().setValue(message);
                    mMessage.setText("");
                    Helper.HideKeyboard(LiveStreamActivity.this);
                } else {
                    Helper.showSnackBar(ivSend, getString(R.string.please_enter_your_query));
                }

            }
        });

        commentDeleteIV.setOnClickListener((View v) -> {
            isImageAdded = false;
            mediaFile = new MediaFile();
            image = "";
            commentImageRL.setVisibility(View.GONE);
        });

        if (!GenericUtils.isEmpty(chat_platform)) {
            if (chat_platform.equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                pickImageIV.setVisibility(View.GONE);
            } else {
                pickImageIV.setVisibility(View.VISIBLE);
            }
        } else {
            pickImageIV.setVisibility(View.GONE);
        }
        pickImageIV.setOnClickListener(view1 -> takeImageClass.getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_image)));

        pull_chat_layout.setOnClickListener(v -> {
            if (chatLayoutShown) {
                findViewById(R.id.layout_chat).setVisibility(View.VISIBLE);
                pull_chat_layout.setRotation(0);
            } else {
                findViewById(R.id.layout_chat).setVisibility(View.GONE);
                pull_chat_layout.setRotation(180);
            }
            chatLayoutShown = !chatLayoutShown;
        });

        try {
            TextView videoTitle = findViewById(R.id.vide_title);
            if (data != null) {
                videoTitle.setVisibility(View.VISIBLE);
                videoTitle.setText(data.getVideo_title());
            } else {
                videoTitle.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            findViewById(R.id.iv_back).setOnClickListener(view -> finish());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeTabBackground(String tabType) {
        switch (tabType) {
            case Constants.Extras.LIVE_CHAT:
                liveChatRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_live_chat_poll_enable));
                livePollRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_live_chat_poll_disable));
                break;
            case Constants.Extras.LIVE_POLL:
                livePollRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_live_chat_poll_enable));
                liveChatRL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_live_chat_poll_disable));
                break;
        }
    }

    private void loadPlayerWithVideoURL(String url) {
        getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                getPlayerFragment(AES.decrypt(url))).commit();

    }

    private Fragment getPlayerFragment(String url) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.exoplayer_container_layout);
        if (frag == null)
            return ExoPlayerFragment.newInstance(url);
        else
            return frag;
    }

    public void publish(View view) {
        HashMap<String, String> message = new HashMap<>();
        message.put(Constants.Extras.ID, SharedPreference.getInstance().getLoggedInUser().getId());
        message.put("sender", SharedPreference.getInstance().getLoggedInUser().getName());
        message.put(Constants.Extras.MESSAGE, mMessage.getText().toString());
        message.put("timestamp", DateTimeUtil.getTimeStampUtc());
        message.put(Constants.Extras.TYPE, "android");
        message.put("profile_pic", SharedPreference.getInstance().getLoggedInUser().getProfile_picture());
        LiveStreamActivity.this.mPubnubDataStream.publish().channel(Constants.CHANNEL_NAME).message(message).async(
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

    private final void initPubNub() {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        config.setUuid(SharedPreference.getInstance().getLoggedInUser().getId());
        config.setSecure(true);

        this.mPubnubDataStream = new PubNub(config);
    }

    private final void initChannels() {
        this.mPubnubDataStream.addListener(this.mPubSubPnCallback);

        this.mPubnubDataStream.subscribe().channels(PUBSUB_CHANNEL).execute();
    }

    private void disconnectAndCleanup() {
        getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE).edit().clear().apply();

        if (this.mPubnubDataStream != null) {
            this.mPubnubDataStream.unsubscribe().channels(PUBSUB_CHANNEL).execute();
            this.mPubnubDataStream.removeListener(this.mPubSubPnCallback);
            this.mPubnubDataStream.stop();
            this.mPubnubDataStream = null;
        }

    }

    private void uploadImageToS3Bucket() {
        ArrayList<MediaFile> imageArrayList = new ArrayList<>();
        imageArrayList.add(mediaFile);
        s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_FIREBASE_CHAT, activity, this, progressBar);
        s3ImageUploading.execute(imageArrayList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        MasterFeedsHitResponse masterHitResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (isFromLiveCourse){
            if (chat_platform != null) {
                if (chat_platform.equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                    PUBSUB_CHANNEL = Collections.singletonList(Constants.CHANNEL_NAME);
                    this.mPubSubPnCallback = new PubSubPnCallbackNew(this.mPubSub);
                    initPubNub();
                    initChannels();
                } else {
                    fireBaseOperation();
                }
            } else {
/*                PUBSUB_CHANNEL = Collections.singletonList(Constants.CHANNEL_NAME);
                this.mPubSubPnCallback = new PubSubPnCallbackNew(this.mPubSub);
                initPubNub();
                initChannels();*/
                fireBaseOperation();
            }
            tvHeading.setText("Live Chat");
            recyclerChat.setVisibility(View.VISIBLE);
            llll.setVisibility(View.VISIBLE);
            findViewById(R.id.chat_btn).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.chat_btn)).setText("Live Chat");
            tvHeading.setVisibility(View.VISIBLE);
            setChatLLVisibility();
        }else {
            if (masterHitResponse != null && masterHitResponse.getFanwall_chat().equalsIgnoreCase("1")) {
                if (chat_platform != null) {
                    if (chat_platform.equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                        PUBSUB_CHANNEL = Collections.singletonList(Constants.CHANNEL_NAME);
                        this.mPubSubPnCallback = new PubSubPnCallbackNew(this.mPubSub);
                        initPubNub();
                        initChannels();
                    } else {
                        fireBaseOperation();
                    }
                } else {
/*                PUBSUB_CHANNEL = Collections.singletonList(Constants.CHANNEL_NAME);
                this.mPubSubPnCallback = new PubSubPnCallbackNew(this.mPubSub);
                initPubNub();
                initChannels();*/
                    fireBaseOperation();
                }
                tvHeading.setText("Live Chat");
                recyclerChat.setVisibility(View.VISIBLE);
                llll.setVisibility(View.VISIBLE);
                findViewById(R.id.chat_btn).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.chat_btn)).setText("Live Chat");
                tvHeading.setVisibility(View.VISIBLE);
                setChatLLVisibility();
            } else {
                llChat.setVisibility(View.GONE);
                tvHeading.setVisibility(View.GONE);
                recyclerChat.setVisibility(View.GONE);
                llll.setVisibility(View.GONE);
                findViewById(R.id.chat_btn).setVisibility(View.GONE);
            }
        }
    }

    public void setAdapter() {
        PubSubAdapter adapter = new PubSubAdapter(this, values);
        recyclerChat.setAdapter(adapter);
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        clearResumePosition();

        Log.e(TAG, "onNewIntent");
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
           /* if (Util.SDK_INT > 23) {
                initializePlayer();
            }*/

    }

    private void fireBaseOperation() {
        setUserOnline();
        setUserBlocked();

        //--------------------------This is the main node that is used for chatting----------------------------
        if (BuildConfig.DEBUG) {
/*            if (isFromLiveCourse) {
                query = FirebaseDatabase.getInstance().getReference().child("course_emedicoz_appsquadz_test/" + chatNode).limitToLast(10);
            } else {
                query = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_test/" + "admin").limitToLast(10);
            }
        } else {
            if (isFromLiveCourse) {
                query = FirebaseDatabase.getInstance().getReference().child("course_emedicoz_appsquadz/" + chatNode).limitToLast(10);
            } else {*/
            query = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall/" + "admin").limitToLast(50);
            //          }
        }
        //--------------------------This node is for testing image upload----------------------------
        //query = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_testing/" + "admin").limitToLast(10);

        nodes.clear();
        arrChat.clear();
        chatAdapter = new ChatAdapter(this, "", arrChat);
//        chatAdapter1 = new ChatAdapter(this, "trans", arrChat);

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
                if (dataSnapshot == null)
                    return;
                chatPojo message = dataSnapshot.getValue(chatPojo.class);
                Log.e(TAG, "onChildChanged: " + message.getPoll().getAnswer());
                if (message != null) {
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).equalsIgnoreCase(dataSnapshot.getKey())) {
                            if (!GenericUtils.isListEmpty(arrChat))
                                arrChat.set(i, message);
                            break;
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addChildEventListener(listener);

        getSingleValue();
        getPollVisibilityRef();
        getPollTimingRef();

    }

    private void getPollTimingRef() {
        queryPollTiming = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live_Poll/").child("admin").child("pollVisibility").child("pollTiming");
        //ref.getRef().push().setValue("30");
        queryPollTiming.getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "onDataChange: " + snapshot.getValue().toString());
                if (snapshot.getValue() != null) {
                    if (circularTimerView == null) {
                        Log.e(TAG, "onDataChange: " + "aaya");
                        circularTimerView = findViewById(R.id.progress_circular);
                        pollTimer = Long.parseLong(snapshot.getValue().toString());
                        circularTimerView.setCircularTimerListener(new CircularTimerListener() {
                            @Override
                            public String updateDataOnTick(long remainingTimeInMs) {
                                circularTimerView.setVisibility(View.VISIBLE);
                                String msg = String.valueOf((int) Math.ceil((remainingTimeInMs / 1000.f))) + "\nsec";
                                Spannable spannable = new SpannableString(msg);
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf((int) Math.ceil((remainingTimeInMs / 1000.f))).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new RelativeSizeSpan(1.5f), 0, String.valueOf((int) Math.ceil((remainingTimeInMs / 1000.f))).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                progressText.setText(spannable, TextView.BufferType.SPANNABLE);
                                viewPollTV.setText("New Poll Ends in " + String.valueOf((int) Math.ceil((remainingTimeInMs / 1000.f))) + " seconds");
                                queryPollTiming.getRef().setValue(String.valueOf((int) Math.ceil((remainingTimeInMs / 1000.f))));
                                /*                return String.valueOf((int)Math.ceil((remainingTimeInMs / 1000.f)))+"\nsec";*/
                                return "";
                            }

                            @Override
                            public void onTimerFinished() {
                                Log.e(TAG, "onTimerFinished: ");
                                circularTimerView.setPrefix("");
                                circularTimerView.setSuffix("");
                                circularTimerView.setVisibility(View.GONE);
                                livePollRL.setVisibility(View.GONE);
                                queryPollVisible.getRef().setValue("0");
                                changeTabBackground(Constants.Extras.LIVE_CHAT);
                                setChat();
                                circularTimerView = null;
                                chatAdapter.setType("");
                                chatAdapter.notifyDataSetChanged();
                                newPollLL.setVisibility(View.GONE);
                                try {
                                    for (int i = 0; i < arrChat.size(); i++) {
                                        Log.e(TAG, "onTimerFinished: "+arrChat.get(i).toString() );
                                        if (arrChat.get(i).getPoll()!=null) {
                                            Log.e(TAG, "onTimerFinished: In ForLoop" );
                                            if (!String.valueOf(arrChat.get(i).getPoll().getIsPollEnded()).equalsIgnoreCase("1")) {
                                                Log.e(TAG, "onTimerFinished: In PollENded" );
                                                query.getRef().child(nodes.get(i)).child("poll").child("isPollEnded").setValue(1);
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, pollTimer, TimeFormatEnum.SECONDS, 10);
                        if (circularTimerView != null)
                            circularTimerView.startTimer();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPause() {
        // simpleExoPlayerView.Pause();
        super.onPause();
            /*if (Util.SDK_INT <= 23) {
                releasePlayer();
            }*/

        Log.e("onPause: ", "onPause is called");
        MasterFeedsHitResponse masterHitResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (masterHitResponse != null && masterHitResponse.getFanwall_chat().equalsIgnoreCase("1")) {
            if (chat_platform != null) {
                if (chat_platform.equalsIgnoreCase(Constants.ChatExtras.PUBNUB)) {
                    disconnectAndCleanup();
                } else {
                    setUserOffline();
                    if (listener != null)
                        query.getRef().removeEventListener(listener);

                    if (valueEventListener != null)
                        reference.removeEventListener(valueEventListener);
                }
            } else {
                setUserOffline();
                if (listener != null)
                    query.getRef().removeEventListener(listener);

                if (valueEventListener != null)
                    reference.removeEventListener(valueEventListener);
                // disconnectAndCleanup();
            }
        }


    }

    @Override
    public void onStop() {
        super.onStop();
           /* if (Util.SDK_INT > 23) {
                releasePlayer();
            }
*/
        Log.e("onStop: ", "onStop is called");
  /*      if (SharedPreference.getInstance().getMasterHitResponse().getFanwall_chat().equalsIgnoreCase("1"))
            setUserOffline();
  */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eMedicozApp.getInstance().playerState = null;
        if (executorService != null) executorService.shutdown();
        // PlaybackControlView.lastPositionInMS = 0;
        releaseAdsLoader();

        if (null != clientThread) {
            clientThread.sendMessage("Disconnect");
            clientThread = null;
        }
    }

// Activity input

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        } else if (view.getParent() == debugRootView) {
               /* if (trackSelector != null) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                    if (mappedTrackInfo != null) {
                        trackSelectionHelper.showSelectionDialog(this, ((Button) view).getText(),
                                trackSelector.getCurrentMappedTrackInfo(), (int) view.getTag());
                    }

                }*/
        }
    }


// Internal methods

      /*  @Override
        public void onVisibilityChange(int visibility) {
            debugRootView.setVisibility(visibility);
        }*/

    private void initializePlayer() {
        Intent intent = getIntent();
        loadPlayerWithVideoURL(url);


        String action = intent.getAction();
        Uri[] uris;
        String[] extensions;
//        if (ACTION_VIEW.equalsIgnoreCase(action)) {
        uris = new Uri[]{Uri.parse(url)};
        extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};


        String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
            try {
                //mediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));
            } catch (Exception e) {
                showToast(R.string.ima_not_loaded);
            }
        } else {
            releaseAdsLoader();
        }


        if (videoTimeMS != 0) {
            // player.seekTo(videoTimeMS);

        }
        inErrorState = false;
        updateButtonVisibilities();

        updateMarqueeTextViewPosition(1);

    }

    private void updateMarqueeTextViewPosition(int orientation) {
    }


       /* private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
            int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                    : Util.inferContentType("." + overrideExtension);

            return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);*/
       /* } else {
            return new ExtractorMediaSource(uri,
                    new CacheDataSourceFactory(this, 100 * 1024 * 1024, 5 * 1024 * 1024),
                    new DefaultExtractorsFactory(), null, null);
        }*/


    /* private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(UUID uuid,
                                                                               String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {
         HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
                 buildHttpDataSourceFactory(false));
         if (keyRequestPropertiesArray != null) {
             for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                 drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                         keyRequestPropertiesArray[i + 1]);
             }
         }
         return new DefaultDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback,
                 null, mainHandler, eventLogger);
     }
*/
    private void releasePlayer() {
            /*if (player != null) {
                debugViewHelper.stop();
                debugViewHelper = null;
                shouldAutoPlay = player.getPlayWhenReady();
                updateResumePosition();
                player.release();
                player = null;
                trackSelector = null;
                trackSelectionHelper = null;
                eventLogger = null;
            }*/
    }


    @Override
    public void onBackPressed() {
        int orientation1 = getResources().getConfiguration().orientation;
        if (orientation1 == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();

        }
    }


    private void clearResumePosition() {
           /* resumeWindow = C.INDEX_UNSET;
            resumePosition = C.TIME_UNSET;*/
    }


    /**
     * Returns an ads media source, reusing the ads loader if one exists.
     *
     * @throws Exception Thrown if it was not possible to create an ads media source, for example, due
     *                   to a missing dependency.
     */
       /* private MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) throws Exception {
            // Load the extension source using reflection so the demo app doesn't have to depend on it.
            // The ads loader is reused for multiple playbacks, so that ad playback can resume.
            Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
            if (imaAdsLoader == null) {
                imaAdsLoader = loaderClass.getConstructor(Context.class, Uri.class)
                        .newInstance(this, adTagUri);
                adOverlayViewGroup = new FrameLayout(this);
                // The demo app has a non-null overlay frame layout.
                simpleExoPlayerView.getOverlayFrameLayout().addView(adOverlayViewGroup);
            }
            Class<?> sourceClass =
                    Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsMediaSource");
            Constructor<?> constructor = sourceClass.getConstructor(MediaSource.class,
                    DataSource.Factory.class, loaderClass, ViewGroup.class);
            return (MediaSource) constructor.newInstance(mediaSource, mediaDataSourceFactory, imaAdsLoader,
                    adOverlayViewGroup);
        }*/


// Player.EventListener implementation
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
            //  simpleExoPlayerView.getOverlayFrameLayout().removeAllViews();

        }
    }


    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();

        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
        debugRootView.setVisibility(inErrorState ? View.VISIBLE : View.GONE);

        debugRootView.addView(retryButton);


    }

    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    private void setUserOnline() {
        if (mFirebaseDatabaseReference1 == null) {
            try {
                String userId = SharedPreference.getInstance().getLoggedInUser().getId();
                if (BuildConfig.DEBUG) {
                    /*if (isFromLiveCourse) {
                        mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live_test/" + chatNode);
                    } else {
                        mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live_test/" + Const.FAN_WALL_LIVE_USER_SHOW_ONLINE_CHILD);
                    }
                } else {
                    if (isFromLiveCourse) {
                        mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live/" + chatNode);
                    } else {*/
                    mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live/" + Const.FAN_WALL_LIVE_USER_SHOW_ONLINE_CHILD);
                    // }
                }
                mFirebaseDatabaseReference1.child(userId).child("online").setValue("true");
                mFirebaseDatabaseReference1.child(userId).child(Constants.Extras.NAME).setValue(SharedPreference.getInstance().getLoggedInUser().getName());
                mFirebaseDatabaseReference1.child(userId).child("profile_picture").setValue(SharedPreference.getInstance().getLoggedInUser().getProfile_picture());
                mFirebaseDatabaseReference1.child(userId).child("erp_token").setValue(SharedPreference.getInstance().getLoggedInUser().getDams_tokken());
                mFirebaseDatabaseReference1.child(userId).child(Constants.Extras.TYPE).setValue("android");
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    private void setChatLLVisibility() {
        if (user_blocked.equals("1"))
            llChat.setVisibility(View.GONE);
        else
            llChat.setVisibility(View.VISIBLE);
    }

    public void setUserBlocked() {
        DatabaseReference ref = null;
        if (BuildConfig.DEBUG) {
           /* if (isFromLiveCourse) {
                ref = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live_test/" + chatNode).child(SharedPreference.getInstance().getLoggedInUser().getId()).child("is_blocked");
            } else {
                ref = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live_test/" + Const.FAN_WALL_LIVE_USER_SHOW_ONLINE_CHILD).child(SharedPreference.getInstance().getLoggedInUser().getId()).child("is_blocked");
            }
        } else {
            if (isFromLiveCourse) {
                ref = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live/" + chatNode).child(SharedPreference.getInstance().getLoggedInUser().getId()).child("is_blocked");
            } else {*/
            ref = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live/" + Const.FAN_WALL_LIVE_USER_SHOW_ONLINE_CHILD).child(SharedPreference.getInstance().getLoggedInUser().getId()).child("is_blocked");
            // }
        }
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


    public void getPollVisibilityRef() {

        queryPollVisible = FirebaseDatabase.getInstance().getReference().child("emedicoz_Live_Poll/").child("admin").child("pollVisibility").child("isPollVisible");
        //ref.setValue("1");
        queryPollVisible.getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: isPollVisible - " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().toString().equals("1")) {
                    //  user_blocked = "1";
                    isPollVisible = "1";
                    newPollLL.setVisibility(View.VISIBLE);
                } else {
                    isPollVisible = "0";
                    //  user_blocked = "0";
                    newPollLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserOffline() {
        if (!isDestroyed() && mFirebaseDatabaseReference1 != null && ServerValue.TIMESTAMP != null) {
            try {

                /*if (isFromLiveCourse) {
                    mFirebaseDatabaseReference1.child(SharedPreference.getInstance().getLoggedInUser().getId()).child(Constants.ChatExtras.ONLINE).setValue(ServerValue.TIMESTAMP);
                } else {*/
                mFirebaseDatabaseReference1.child(SharedPreference.getInstance().getLoggedInUser().getId()).child("online").setValue(ServerValue.TIMESTAMP);
                // }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }


    public void ErrorCallBack(String message, String apiType) {
        showToast(message);
    }


    private void setChat() {
        setChatLLVisibility();
    }

    void disableAll() {
        bookmarkBtn.setBackgroundColor(getResources().getColor(R.color.off_white));
        indexBtn.setBackgroundColor(getResources().getColor(R.color.off_white));
        chatBtn.setBackgroundColor(getResources().getColor(R.color.off_white));
    }

    private String formattedTime(long millis) {
        if (millis < 1) {
            return "00:00:00";
        }
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public void networkCallForVideoDuration(Video video) {

        mProgress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getVideoDuration(SharedPreference.getInstance().getLoggedInUser().getId()
                , video.getId(), "1");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equalsIgnoreCase(Const.TRUE)) {
                            mProgress.dismiss();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            String durationLimit = data.optString("duration_limit");
                            String watched = data.optString("watched");
                            if (durationLimit.equalsIgnoreCase("")) {
                                durationLimit = "0";
                                isLimited = false;
                                txtTimer.setVisibility(View.GONE);
                            } else {
                                isLimited = true;
                                txtTimer.setVisibility(View.VISIBLE);
                            }
                            remainingTime = (Integer.parseInt(durationLimit) - Integer.parseInt(watched)) * 60 * 1000;
                            //  setTimer(remainingTime);
                        } else {
                            RetrofitResponse.getApiData(LiveStreamActivity.this, API.API_GET_FILE_LIST_CURRICULUM);
                            mProgress.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                // Helper.showErrorLayoutForNoNav(API.API_GET_FILE_LIST_CURRICULUM, NewCurriculumActivity.this, 1, 1);
            }
        });
    }

    private void getFirebaseData(DataSnapshot dataSnapshot) {
        chatPojo message = dataSnapshot.getValue(chatPojo.class);
        Log.d(TAG, "getFirebaseData " + message.getId() + " " + chat_status);
        if (message.getType() != null) {
            if ((!message.getType().equalsIgnoreCase("admin")) || (message.getOriginal() != null)) {
                // if ((!message.getType().equalsIgnoreCase("admin")) || (message.getOriginal()!=null))
                /*if (arrChat.size() == 50) {
                    chatPojo chatPojo = arrChat.get(0);
                    chatPojo = null;
                    arrChat.remove(0);
                    arrChat.add(49, message);
                } else {
                    arrChat.add(message);
                }*/
                arrChat.add(message);
            } else if (message.getType().equalsIgnoreCase("admin") && message.getOriginal() == null) {
                arrChat.add(message);
                /*if (arrChat.size() == 50) {
                    chatPojo chatPojo = arrChat.get(0);
                    chatPojo = null;
                    arrChat.remove(0);
                    arrChat.add(49, message);
                } else {
                    arrChat.add(message);
                }*/
            }
            nodes.add(dataSnapshot.getKey());
        }
        chatAdapter.setType("");
        chatAdapter.notifyDataSetChanged();
        recyclerChat.smoothScrollToPosition(arrChat.size());

    }

/*
        private void fetchHistory() {
            History.getAllMessages(mPubnub_DataStream, Constants.CHANNEL_NAME,
                    new History.CallbackSkeleton() {
                        @Override
                        public void handleResponse(final List<PubSubPojo> messages) {
                            Log.e(TAG, "handleResponse: " + messages.size());
                            if (!messages.isEmpty()) {
                                if (messages != null && messages.size() > 0)
                                    values.addAll(0, messages);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        //adapter.notifyDataSetChanged();
                                        if (messages != null && messages.size() > 0)
                                            setAdapter();
                                        // Stuff that updates the UI

                                    }
                                });
                            }
                        }
                    });
        }
*/

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
                InitImageView(mediaFile);
            }
        }

    }

    public void InitImageView(MediaFile image) {
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
                chatPojo message = new chatPojo(SharedPreference.getInstance().getLoggedInUser().getId(), mMessage.getText().toString(), SharedPreference.getInstance().getLoggedInUser().getName(), datetime, "1", SharedPreference.getInstance().getLoggedInUser().getProfile_picture(), "android", SharedPreference.getInstance().getLoggedInUser().getDams_tokken(), image,"normal",null);
                query.getRef().push().setValue(message);
                mMessage.setText("");
                Helper.HideKeyboard(LiveStreamActivity.this);
                isImageAdded = false;
                image = "";
                commentImageRL.setVisibility(View.GONE);
            }
        }

    }

    public class PubSubPnCallbackNew extends SubscribeCallback {
        private final String TAG = PubSubPnCallbackNew.class.getName();
        private final PubSubAdapter chatAdapter;


        public PubSubPnCallbackNew(PubSubAdapter chatAdapter) {
            this.chatAdapter = chatAdapter;
        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
 /*           if (status.getOperation() == PNOperationType.PNSubscribeOperation && status.getAffectedChannels()
                    .contains(Constants.CHANNEL_NAME)) {
                fetchHistory();
            }*/
            //   fetchHistory();
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            try {
                try {
                    JsonNode jsonMsg = message.getMessage();
                    PubSubPojo msg = JsonUtil.convert(jsonMsg, PubSubPojo.class);
                    if (values.size() == 50) {
                        PubSubPojo pubSubPojo = values.get(0);
                        pubSubPojo = null;
                        values.remove(0);
                        values.add(49, msg);
                    } else {
                        values.add(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //adapter.notifyDataSetChanged();
                        setAdapter();
                        scrollChatToBottom();
                        // Stuff that updates the UI

                    }
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
        if (takeImageClass != null)
            takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    private void getSingleValue() {
        if (BuildConfig.DEBUG) {
           /* if (isFromLiveCourse) {
                reference = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live_test").child("chat").child("status");
            } else {
                reference = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live_test").child("chat").child("status");
            }
        } else {
            if (isFromLiveCourse) {
                reference = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live").child("chat").child("status");
            } else {*/
            reference = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_Live").child("chat").child("status");
            // }
        }
        //  reference = FirebaseDatabase.getInstance().getReference().child("emedicoz_Fanwall_testing").child("chat").child("status");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: chat -> status - " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().toString().equals("0")) {
                        llll.setVisibility(View.GONE);
                        recyclerChat.setVisibility(View.GONE);
                        llChat.setVisibility(View.GONE);
                    } else {
                        llll.setVisibility(View.VISIBLE);
                        recyclerChat.setVisibility(View.VISIBLE);
                        setChatLLVisibility();
                    }
                } else {
                    llll.setVisibility(View.VISIBLE);
                    recyclerChat.setVisibility(View.VISIBLE);
                    setChatLLVisibility();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //method called when database through error
            }
        });
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://" + SERVER_IP + ":" + SERVER_PORT + "/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                ShowLogMessage("onOpenReceived");
            }

            @Override
            public void onTextReceived(String message) {
                ShowLogMessage(message);
                selectedOptionId = "-1";
                correctOptionId = "-1";

                SocketGeneralObject socketGeneralObject = gson.fromJson(message, SocketGeneralObject.class);

                if (socketGeneralObject != null && socketGeneralObject.getData() != null && socketGeneralObject.getData().toString().contains("event_type")) {

                    SocketPollsObject socketPollsObject = gson.fromJson(socketGeneralObject.getData().toString(), SocketPollsObject.class);

                    if (socketPollsObject != null && socketPollsObject.getEvent_type() != null) {

                        switch (socketPollsObject.getEvent_type()) {

                            case Const.PollsSocket.GET_QUESTION:
                                questionId = socketPollsObject.getId();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callGetPollQuestionDetails();
                                    }
                                });
                                break;

                            case Const.PollsSocket.GET_RESULT:

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callGetPollResultsApi();
                                    }
                                });
                                break;
                        }
                    }
                }
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                ShowLogMessage("onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                ShowLogMessage("onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                ShowLogMessage("onPongReceived");
            }

            @Override
            public void onException(Exception e) {
                ShowLogMessage("Error " + e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                ShowLogMessage("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(1800000);
        webSocketClient.setReadTimeout(1800000);
        webSocketClient.enableAutomaticReconnection(10000);
        webSocketClient.connect();

    }

    private void ShowLogMessage(String msg) {
        Log.i("socket", msg);
    }

    /* clientThread class defined to run the client connection to the socket network using the server ip and port
     * and send message */
    class ClientThread implements Runnable {

        private BufferedReader input;

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                ShowLogMessage("Connecting to Server...");
                socket = new Socket(serverAddr, SERVER_PORT);

                if (socket.isBound() && socket.isConnected()) {
                    ShowLogMessage("Connected to Server...");
                }


                while (!Thread.currentThread().isInterrupted()) {
                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected...";
                        ShowLogMessage(message);
                        break;
                    }
                    ShowLogMessage("Server: " + message);
                }

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                ShowLogMessage("Problem Connecting to server... Check your server IP and Port and try again");
                Thread.interrupted();
                e1.printStackTrace();
            } catch (NullPointerException e3) {
                ShowLogMessage("error returned");
            }

        }

        void sendMessage(final String message) {
            new Thread(() -> {
                try {
                    if (null != socket) {
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    private void openPollsDialog(JSONObject jsonResponse) {

        JSONObject apiData = GenericUtils.getJsonObject(jsonResponse);

        String questionTitle = apiData.optString("title");
        String questionId = apiData.optString("question_id");
        String courseId = apiData.optString("course_id");
        correctOptionId = apiData.optString("correct_option_id");

        parseOptionList(jsonResponse);

        Dialog pollsDialog = new Dialog(activity);
        pollsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        pollsDialog.setContentView(R.layout.polls_dialog);
        pollsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView closeButton = pollsDialog.findViewById(R.id.closeButton);
        TextView questionTextview = pollsDialog.findViewById(R.id.questionTextview);
        remarkTextview = pollsDialog.findViewById(R.id.remarkTextview);
        RecyclerView pollsOptionsRecyclerview = pollsDialog.findViewById(R.id.pollsAnswerRecyclerview);

        questionTextview.setText(questionTitle);
        remarkTextview.setVisibility(View.GONE);

        closeButton.setOnClickListener(view -> pollsDialog.dismiss());

        pollsOptionsAdapter = new PollsOptionsAdapter(optionsList, selectedOptionId, correctOptionId, this, new PollOptionSelectedInterface() {
            @Override
            public void pollOptionSelectedPosition(int position) {

                selectedOptionId = optionsList.get(position).getOptionId();
                callPollSubmitApi(selectedOptionId);

            }
        });
        pollsOptionsRecyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        pollsOptionsRecyclerview.setAdapter(pollsOptionsAdapter);

        pollsDialog.show();
    }

    private void callGetPollQuestionDetails() {

        showLoader();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getPollQuestionDetails(SharedPreference.getInstance().getLoggedInUser().getId(), questionId, videoId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equalsIgnoreCase(Const.TRUE)) {
                            hideLoader();
                            JSONObject apiData = GenericUtils.getJsonObject(jsonResponse);

                            openPollsDialog(jsonResponse);

                        } else {
                            Toast.makeText(LiveStreamActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            hideLoader();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideLoader();
            }
        });
    }

    private void showLoader() {
        try {
            mProgress.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLoader() {
        try {
            mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callPollSubmitApi(String selectedOptionId) {

        showLoader();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.submitPollResponse(SharedPreference.getInstance().getLoggedInUser().getId(), questionId, videoId, selectedOptionId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equalsIgnoreCase(Const.TRUE)) {
                            hideLoader();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);

                            for (Options options : optionsList) {
                                options.setDesc("");
                            }
                            pollsOptionsAdapter.notifyChange(optionsList, selectedOptionId, correctOptionId);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callGetPollResultsApi();
                                }
                            }, 3000);

                        } else {
                            Toast.makeText(LiveStreamActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            hideLoader();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideLoader();
            }
        });
    }

    private void callGetPollResultsApi() {

        showLoader();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getPollResults(SharedPreference.getInstance().getLoggedInUser().getId(), questionId, videoId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equalsIgnoreCase(Const.TRUE)) {
                            hideLoader();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);

                            String remarktext = data.getString("message");
                            if (remarkTextview != null && remarktext != null && !remarktext.equals("")) {
                                remarkTextview.setVisibility(View.VISIBLE);
                                remarkTextview.setText(remarktext);
                            } else {
                                remarkTextview.setVisibility(View.GONE);
                                remarkTextview.setText("");
                            }

                            parseOptionList(jsonResponse);
                            pollsOptionsAdapter.notifyChange(optionsList, selectedOptionId, correctOptionId);

                        } else {
                            Toast.makeText(LiveStreamActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            hideLoader();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideLoader();
            }
        });
    }

    private void parseOptionList(JSONObject jsonResponse) {

        JSONArray jsonOptionList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("options_data");
        optionsList = new ArrayList<>();

        for (int i = 0; i < jsonOptionList.length(); i++) {

            JSONObject jsonOptionsObject = null;
            try {
                jsonOptionsObject = jsonOptionList.getJSONObject(i);
                Options options = gson.fromJson(Objects.requireNonNull(jsonOptionsObject).toString(), Options.class);
                optionsList.add(options);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
