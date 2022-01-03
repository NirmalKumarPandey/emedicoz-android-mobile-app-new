package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.SingleFeedView;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.activity.YouTubeVideoPlayer;
import com.emedicoz.app.feeds.adapter.CommentRVAdapter;
import com.emedicoz.app.feeds.adapter.NotificationRVAdapter;
import com.emedicoz.app.feeds.adapter.PeopleRVAdapter;
import com.emedicoz.app.feeds.adapter.PushNotificationRVAdapter;
import com.emedicoz.app.feeds.adapter.RewardTransactionsAdapter;
import com.emedicoz.app.modelo.Comment;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.Notification;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.RelatedCourseCommentData;
import com.emedicoz.app.modelo.RewardTransaction;
import com.emedicoz.app.response.NotificationResponse;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nex3z.flowlayout.FlowLayout;
import com.tonyodev.fetch.Fetch;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

public class CommonFragment extends Fragment implements SingleFeedView.DeletePostCallback, TakeImageClass.ImageFromCropper, AmazonCallBack {

    private static final String TAG = "CommonFragment";
    public TabLayout tabLayout;
    public LinearLayout writeCommentLL;
    public CommentRVAdapter commentRVAdapter;
    public String fragType;
    public PostResponse post;
    public Comment singleComment;
    public String lastActivityId;
    public String lastRewardId;
    public String lastCommentId;
    public String lastLikelistId = "0";
    public int firstVisibleItem;
    public int visibleItemCount;
    public int totalItemCount;
    public int previousTotalItemCount;
    public View mView;
    public SingleFeedView singleFeedView;
    public boolean isImageAdded = false;
    public boolean isCommentEditing = false;
    public TakeImageClass takeImageClass;
    public MediaFile mediaFile;
    public CommentRVAdapter.ViewHolder vHolder;
    public String commentText;
    public String commentImages;
    public TextView commentCount;
    public TextView nameTV;
    public TextView commentTV;
    PushNotificationRVAdapter pushNotificationRVAdapter;
    ImageView replyIV;
    ImageView likeCommentIV;
    EditText writeCommentET;
    Bitmap bitmap;
    ImageButton postCommentIBtn;
    ImageButton addCommentIV;
    View videoImage;
    NestedScrollView nestedScrollV;
    RecyclerView commonRV;
    RecyclerView outerCommonRV;
    RecyclerView pushRV;
    RecyclerView recommendedCourseRV;
    LinearLayout feedsSingleRowLL;
    LinearLayout singleRowLL;
    TextView errorTV;
    TextView outErrrorTV;
    RelativeLayout contentLayout;
    TextView commentLikeCount;
    TextView dateTV;
    ArrayList<Comment> commentArrayList;
    ArrayList<NotificationResponse> notificationArrayList;
    ArrayList<RewardTransaction> rewardTransactionArrayList;
    ArrayList<People> peopleArrayList;
    s3ImageUploading s3ImageUploading;
    NotificationRVAdapter notificationRVAdapter;
    RewardTransactionsAdapter rewardTransactionsAdapter;
    PeopleRVAdapter peopleRVAdapter;
    Activity activity;
    String postId;
    String commentId;
    ArrayList<RelatedCourseCommentData> relatedCourses;
    int commonPeopleType = 0; // 0 for Like List & 1 for ViewAll of people you may know on Common Fragment.
    FlowLayout taggedPeopleCommentFL;
    ArrayList<People> taggedPeopleArrList;
    LinearLayout addPeopleLL;
    String taggedPeopleIdsAdded;
    String errorMessage;
    boolean isPostVisible = false;
    LinearLayoutManager linearLayoutManager;
    ImageView imageIV;
    ImageView deleteIV;
    ImageView imageCircle;
    RelativeLayout imageRL;
    String videoUrl;
    SharedPreference sharedPreference = SharedPreference.getInstance();
    int isAlreadyConnected = 0;
    Progress mProgress;
    String name;
    Comment comment;
    TextView profileNameTV;
    DisplayMetrics displayMetrics;
    int height;
    private boolean loading = true;
    private int visibleThreshold = 2;
    private ArrayList<Notification> arrayNotification = new ArrayList<>();
    private offlineData offlinedata;
    LinearLayout no_notificationLayout;


    public CommonFragment() {
    }

    public static CommonFragment newInstance(String fragType) {
        CommonFragment fragment = new CommonFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        fragment.setArguments(args);
        return fragment;
    }

    public static CommonFragment newInstance(PostResponse post, String fragType, String postId) {
        CommonFragment fragment = new CommonFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.POST, post);
        args.putString(Const.FRAG_TYPE, fragType);
        args.putString(Const.POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    // PARAMETERS
    // Overloaded Method to get the parameters from Feeds People you may know (View All Option).
    public static Fragment newInstance(String fragType, int commonPeopleType, ArrayList<People> peopleArrayList) {
        CommonFragment fragment = new CommonFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        args.putInt(Const.COMMON_PEOPLE_TYPE, commonPeopleType);
        args.putSerializable(Const.PEOPLE_LIST_COMMONS, peopleArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    // PARAMETERS
    // this is to show the post for comment tagged in user
    public static Fragment newInstance(PostResponse post, String fragType, String commentId, String postId) {
        CommonFragment fragment = new CommonFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.POST, post);
        args.putString(Const.FRAG_TYPE, fragType);
        args.putSerializable(Const.COMMENT_ID, commentId);
        args.putString(Const.POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(PostResponse post, String fragType, String commentId, String postId, String name, Comment comment) {
        CommonFragment fragment = new CommonFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.POST, post);
        args.putString(Const.FRAG_TYPE, fragType);
        args.putSerializable(Const.COMMENT_ID, commentId);
        args.putString(Const.POST_ID, postId);
        args.putString(Constants.Extras.NAME, name);
        args.putSerializable(Const.COMMENT, comment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);

        peopleArrayList = new ArrayList<>();
        relatedCourses = new ArrayList<>();
        if (getArguments() != null) {
            post = (PostResponse) getArguments().getSerializable(Const.POST);
            fragType = getArguments().getString(Const.FRAG_TYPE);
            postId = getArguments().getString(Const.POST_ID);
            peopleArrayList = (ArrayList<People>) getArguments().getSerializable(Const.PEOPLE_LIST_COMMONS);
            commonPeopleType = getArguments().getInt(Const.COMMON_PEOPLE_TYPE);
            commentId = getArguments().getString(Const.COMMENT_ID);
            name = getArguments().getString(Constants.Extras.NAME);
            comment = (Comment) getArguments().getSerializable(Const.COMMENT);

        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common_two, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.readallIM) {
            lastActivityId = "";
            networkCallForReadNoti();//NetworkAPICall(API.API_ALL_NOTIFICATION_READ, true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.isPostUpdated) {
            if (SharedPreference.getInstance().getPost() != null) {
                post = SharedPreference.getInstance().getPost();
                initPost(mView);
            }
            Constants.isPostUpdated = false;
        }

        // todo Restrict the API HIT when i am editing the image from Comment Section //
        // so we are checking the variable isCommentEditing = false*/
        if (Constants.isCommentRefreshed && !fragType.equalsIgnoreCase(Const.COMMENT_LIST) && !isCommentEditing && SharedPreference.getInstance().getPost() != null) {
            post = SharedPreference.getInstance().getPost();
            lastCommentId = "";
            initPost(mView);
        }
    }

    // this is to tag people in the comment
    public void addViewToTagPeople(People response) {

        if (taggedPeopleArrList == null) taggedPeopleArrList = new ArrayList<>();
        taggedPeopleArrList.add(response);

        View v = View.inflate(activity, R.layout.single_textview_people_tag, null);
        TextView tv = v.findViewById(R.id.nameTV);
        ImageView delete = v.findViewById(R.id.deleteIV);
        tv.setText(response.getName());
        v.setTag(response);
        delete.setTag(response);
        delete.setOnClickListener(v1 -> {
            People rep = (People) v1.getTag();
            int pos = taggedPeopleArrList.indexOf(rep);
            taggedPeopleArrList.remove(rep);
            taggedPeopleCommentFL.removeViewAt(pos);
        });

        taggedPeopleCommentFL.addView(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        commentArrayList = new ArrayList<>();
        rewardTransactionArrayList = new ArrayList<>();
        notificationArrayList = new ArrayList<>();
        lastRewardId = "";
        lastActivityId = "";
        lastCommentId = "";
        mView = view;
        feedsSingleRowLL = view.findViewById(R.id.feeds_single_row_CV);
        singleRowLL = view.findViewById(R.id.singleRow);
        feedsSingleRowLL.setVisibility(View.GONE);
        errorTV = view.findViewById(R.id.errorTV);
        outErrrorTV = view.findViewById(R.id.outerrorTV);
        contentLayout = view.findViewById(R.id.content_layout);
        nameTV = view.findViewById(R.id.nameTV);
        commentTV = view.findViewById(R.id.commentTV);
        replyIV = view.findViewById(R.id.replyIV);
        commentLikeCount = view.findViewById(R.id.commentLikeCount);
        likeCommentIV = view.findViewById(R.id.likeComment);
        writeCommentET = view.findViewById(R.id.writecommentET);
        postCommentIBtn = view.findViewById(R.id.postcommentIBtn);
        videoImage = view.findViewById(R.id.video_image);
        commentCount = view.findViewById(R.id.commentCount);
        dateTV = view.findViewById(R.id.dateTV);
        imageCircle = view.findViewById(R.id.imageIV);
        profileNameTV = view.findViewById(R.id.profileNameTV);
        taggedPeopleCommentFL = view.findViewById(R.id.taggedpeoplecommentFL);
        addPeopleLL = view.findViewById(R.id.addusertagLL);
        nestedScrollV = view.findViewById(R.id.nestedScrollV);
        commonRV = view.findViewById(R.id.commonRV);
        outerCommonRV = view.findViewById(R.id.outercommonRV);
        imageIV = view.findViewById(R.id.commentimageIV);
        deleteIV = view.findViewById(R.id.commentdeleteIV);
        imageRL = view.findViewById(R.id.commentimageRL);
        no_notificationLayout = view.findViewById(R.id.no_notificationLayout);

        pushRV = view.findViewById(R.id.pushRV);
        recommendedCourseRV = view.findViewById(R.id.recommendedCourseRV);
        recommendedCourseRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        addCommentIV = view.findViewById(R.id.addcommentIV);
        tabLayout = view.findViewById(R.id.tabLayout);
        writeCommentLL = view.findViewById(R.id.writecommentLL);
        if (post != null) {
            commentCount.setText(String.format("Comments (%s)", post.getComments()));
        }
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        tabLayout.addTab(tabLayout.newTab().setText("App Notification"));
        tabLayout.addTab(tabLayout.newTab().setText("Promotional Notification"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        takeImageClass = new TakeImageClass(activity, this);
        loadVideoUrlFromAPI();

        if (fragType.equals(Const.COMMENT_LIST)) {
            outerCommonRV.setLayoutManager(linearLayoutManager);
            writeCommentLL.setVisibility(View.VISIBLE);
            feedsSingleRowLL.setVisibility(View.GONE);
            singleRowLL.setVisibility(View.VISIBLE);
            nestedScrollV.setVisibility(View.VISIBLE);

            displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            Log.e(TAG, "onViewCreated: height - " + height);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, height * 22 / 100, 0, 0);
            outerCommonRV.setLayoutParams(params);
            outerCommonRV.requestLayout();
            commentCount.setText("Replies");
            nameTV.setText(name);
            commentTV.setText(comment.getComment());
            commentLikeCount.setText(String.format("%s like", comment.getLikes()));
            Glide.with(activity).load(comment.getProfile_picture()).into(imageCircle);
            String date = (String) DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime()));
            String[] dates = date.split("\\s+");
            if (dates.length > 1) {
                if (dates[1].equalsIgnoreCase("minutes")) {
                    dates[1] = dates[1].substring(0, 1);
                } else {
                    dates[1] = dates[1].substring(0, 1);
                }
                if (DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime())).equals("0 minutes ago")) {
                    dateTV.setText("Just Now");
                } else {
                    String newDate = dates[0] + " " + dates[1];
                    dateTV.setText(newDate);
                }
            } else {
                dateTV.setText(dates[0]);
            }
            replyIV.setVisibility(View.GONE);
            likeCommentIV.setVisibility(View.GONE);
            if (comment.getIs_like() != null) {
                if (comment.getIs_like().equalsIgnoreCase("1")) {
                    likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like_blue));
                } else {
                    likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like));
                }
            }

            outErrrorTV.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);

        } else if (!fragType.equals(Const.COMMENT)) {
            outerCommonRV.setLayoutManager(linearLayoutManager);
            writeCommentLL.setVisibility(View.GONE);
            feedsSingleRowLL.setVisibility(View.GONE);
            nestedScrollV.setVisibility(View.GONE);
        } else {
            commonRV.setLayoutManager(linearLayoutManager);
            commonRV.setNestedScrollingEnabled(false);
            writeCommentLL.setVisibility(View.VISIBLE);
            feedsSingleRowLL.setVisibility(View.VISIBLE);
            nestedScrollV.setVisibility(View.VISIBLE);
            outErrrorTV.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            outerCommonRV.setVisibility(View.GONE);
            if (post != null && profileNameTV != null && post.getPost_owner_info() != null)
                profileNameTV.setText(post.getPost_owner_info().getName());
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    outerCommonRV.setVisibility(View.VISIBLE);
                    pushRV.setVisibility(View.GONE);
                    if (!notificationArrayList.isEmpty()) {
                        no_notificationLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);
                    } else {
                        no_notificationLayout.setVisibility(View.VISIBLE);
                        contentLayout.setVisibility(View.GONE);
                    }
                } else {

                    outerCommonRV.setVisibility(View.GONE);
                    pushRV.setVisibility(View.VISIBLE);

                    if (!arrayNotification.isEmpty()) {
                        no_notificationLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);
                    } else {
                        no_notificationLayout.setVisibility(View.VISIBLE);
                        contentLayout.setVisibility(View.GONE);
                        outErrrorTV.setText(R.string.no_notification_found);
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        outerCommonRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {

                    Log.i("Yaeye!", "end called");

                    switch (fragType) {
                        case Const.NOTIFICATION:
                            lastActivityId = notificationArrayList.get(totalItemCount - 1).getId();
                            break;
                        case Const.REWARD_TRANSACTION_FRAGMENT:
                            lastRewardId = rewardTransactionArrayList.get(totalItemCount - 1).getId();
                            break;
                        case Const.COMMON_PEOPLE_LIST:
                            lastLikelistId = peopleArrayList.get(totalItemCount - 1).getId();
                            break;
                        case Const.COMMENT_LIST:
                            lastCommentId = commentArrayList.get(totalItemCount - 1).getId();
                            break;
                        default:
                    }

                    refreshCommon(false); // from the scrolling action
                    loading = true;
                }

            }
        });

        nestedScrollV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                            int i = 0;
                            while (i < totalItemCount) {
                                if (fragType.equals(Const.COMMENT))
                                    lastCommentId = commentArrayList.get(totalItemCount - 1 - i).getId();
                                i = totalItemCount;
                                Log.e("Tag", String.valueOf(i));
                            }
                            if (isAlreadyConnected == 0) {
                                refreshCommon(false); // from the scrolling action
                                loading = true;
                                isAlreadyConnected = 1;
                            }
//                        Load Your Data
                        }
                    }
                }
            }
        });

        addPeopleLL.setOnClickListener(view12 -> {
            PeopleTagSelectionFragment newpeopletag = (PeopleTagSelectionFragment) Fragment.instantiate(activity, PeopleTagSelectionFragment.class.getName());
            Bundle bn = new Bundle();
            bn.putSerializable(Const.ALREADY_TAGGED_PEOPLE, taggedPeopleArrList);
            newpeopletag.setArguments(bn);
            newpeopletag.show(((PostActivity) activity).getSupportFragmentManager(), "dialog");
        });


        // this is to open gallerpopup and to choose image to upload it on comment Section
        addCommentIV.setOnClickListener(view13 -> takeImageClass.getImagePickerDialog(activity, "Upload Image", getString(R.string.choose_image)));

        deleteIV.setOnClickListener(view14 -> {
            isImageAdded = false;
            mediaFile = new MediaFile();
            commentImages = "";
            imageRL.setVisibility(View.GONE);
        });

        // this is to init the page for the data as the fragment type
        switch (fragType) {
            case Const.REWARD_TRANSACTION_FRAGMENT:
            case Const.NOTIFICATION:
            case Const.COMMON_PEOPLE_LIST:
                refreshCommon(true); // to get the notification list or to the likes list
                break;
            case Const.COMMENT_LIST:
                writeCommentLL.setVisibility(View.VISIBLE);
                refreshCommon(true); // to get the notification list or to the likes list

                break;
            case Const.COMMON_PEOPLE_VIEWALL:
            case Const.COMMON_EXPERT_PEOPLE_VIEWALL:
                initPeopleAdapter(); // to view all people you may know

                break;
            case Const.COMMENT:
                writeCommentLL.setVisibility(View.VISIBLE);

                if (post == null && postId != null) {
                    networkCallForSinglePost();//NetworkAPICall(API.API_SINGLE_POST_FOR_USER, true);
                } else if (post == null && commentId != null) {
                    networkCallForSingleCommentData();//NetworkAPICall(API.API_SINGLE_COMMENT_DATA, true);
                } else {
                    isPostVisible = true;
                    feedsSingleRowLL.setVisibility(View.VISIBLE);
                    if (post != null)
                        postId = post.getId();
                    initPost(mView);
                }
                break;
        }

        Helper.CaptializeFirstLetter(writeCommentET);

        postCommentIBtn.setOnClickListener(view15 -> checkValidation());

        videoImage.setOnClickListener(view1 -> {
            if (!StringUtils.isEmpty(videoUrl))
                loadPlayerWithVideoURL(videoUrl);
            else
                loadVideoUrlFromAPI();
        });

    }


    private void loadPlayerWithVideoURL(String url) {
        if (!activity.isFinishing()) {

            Log.e(TAG, "loadPlayerWithVideoURL: " + url);
            getChildFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                    ExoPlayerFragment.newInstance(AES.decrypt(url))).commit();
        }
    }

    private void loadVideoUrlFromAPI() {

        if (post == null || post.getPost_data() == null || post.getPost_data().getPost_text_type() == null)
            return;
        if (!post.getPost_data().getPost_text_type().equals(Const.POST_TEXT_TYPE_YOUTUBE_TEXT)) {
            offlinedata = getOfflineDataIds(post.getId(), Const.FEEDS, activity, post.getId());
            if (offlinedata != null && offlinedata.getRequestInfo() == null)
                offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

            if (offlinedata == null && !GenericUtils.isListEmpty(post.getPost_data().getPost_file())) {
                videoUrl = Helper.getCloudFrontUrl() + post.getPost_data().getPost_file().get(0).getFile_info();

            } else if (offlinedata != null && offlinedata.getRequestInfo() != null && offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {

              /*  Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlinedata.getLink(), Const.VIDEO_STREAM, post.getId(), Const.COURSE_VIDEO_TYPE);
                try {
                    new Handler().post(() -> {
                        activity.finish();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                loadPlayerWithVideoURL(activity.getFilesDir() + "/" + offlinedata.getLink());

            } else if (offlinedata != null && offlinedata.getRequestInfo() != null &&
                    (offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {

                videoUrl = Helper.getCloudFrontUrl() + post.getPost_data().getPost_file().get(0).getFile_info();
            }
        } else {
            Intent intent = new Intent(activity, YouTubeVideoPlayer.class);// youtube video player
            intent.putExtra(Const.FRAG_TYPE, Const.YOUTUBE);
            intent.putExtra(Const.YOUTUBE_ID, Helper.youtubevalidation(post.getPost_data().getText()));
            activity.startActivity(intent);
        }

    }

    public void checkValidation() {
        commentText = Helper.GetText(writeCommentET);
        boolean isDataValid = true;

        if (taggedPeopleArrList != null && !taggedPeopleArrList.isEmpty()) {
            for (People res : taggedPeopleArrList) {
                if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                    taggedPeopleIdsAdded = res.getId();
                else
                    taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + res.getId();
            }
        }
        if (TextUtils.isEmpty(commentText))
            isDataValid = Helper.DataNotValid(writeCommentET);

        if (isDataValid) {
            postCommentIBtn.setEnabled(false);
            if (isImageAdded) {
                ArrayList<MediaFile> mediaFiles = new ArrayList<>();
                mediaFiles.add(mediaFile);
                s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_COMMENT, activity, this, null);
                s3ImageUploading.execute(mediaFiles);
            } else
                networkCallForAddComment();//NetworkAPICall(API.API_ADD_COMMENT, true);
        }
    }

    public void initPushAdapter() {
        arrayNotification.clear();
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.NOTIFICATION_DATA))) {
            Type type = new TypeToken<ArrayList<Notification>>() {
            }.getType();
            arrayNotification = new Gson().fromJson(SharedPreference.getInstance().getString(Const.NOTIFICATION_DATA), type);
        }

        pushRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        pushNotificationRVAdapter = new PushNotificationRVAdapter(arrayNotification, getActivity(), CommonFragment.this);
        pushRV.setAdapter(pushNotificationRVAdapter);
    }

    public void initCommentAdapter() {
        if (fragType.equals(Const.COMMENT)) {
            if (!StringUtils.isEmpty(videoUrl))
                loadPlayerWithVideoURL(videoUrl);

            if (TextUtils.isEmpty(lastCommentId)) {
                if (!commentArrayList.isEmpty() && isPostVisible) {
                    commentRVAdapter = new CommentRVAdapter(commentArrayList, activity) {
                        @Override
                        public void onChangeImage(Comment comment, ViewHolder viewHolder) {
                            vHolder = viewHolder;
                        }
                    };
                    commonRV.setAdapter(commentRVAdapter);
                    ViewCompat.setNestedScrollingEnabled(commonRV, false);
                    commonRV.setNestedScrollingEnabled(false);
                    errorTV.setVisibility(View.GONE);
                    commonRV.setVisibility(View.VISIBLE);
                } else {
                    errorTV.setText(errorMessage);
                    errorTV.setVisibility(View.VISIBLE);
                    commonRV.setVisibility(View.GONE);
                }
            } else {
                commentRVAdapter.notifyItemRangeInserted(commentRVAdapter.getItemCount(), commentArrayList.size());
            }
        } else if (fragType.equals(Const.COMMENT_LIST)) {
            if (TextUtils.isEmpty(lastCommentId)) {
                outerCommonRV.setVisibility(View.VISIBLE);
                outErrrorTV.setVisibility(View.GONE);
                errorTV.setVisibility(View.GONE);
                if (!commentArrayList.isEmpty()) {
                    commentRVAdapter = new CommentRVAdapter(commentArrayList, activity) {
                        @Override
                        public void onChangeImage(Comment comment, ViewHolder viewHolder) {
                            vHolder = viewHolder;
                        }
                    };
                    outerCommonRV.setAdapter(commentRVAdapter);
                    ViewCompat.setNestedScrollingEnabled(outerCommonRV, false);
                    outerCommonRV.setNestedScrollingEnabled(false);
//                    contentLayout.setVisibility(View.VISIBLE);
//                    outerCommonRV.setVisibility(View.VISIBLE);
                } else {
                    outErrrorTV.setText(errorMessage);
                    outErrrorTV.setVisibility(View.VISIBLE);
//                    contentLayout.setVisibility(View.GONE);
//                    outerCommonRV.setVisibility(View.GONE);
                }
            } else commentRVAdapter.notifyDataSetChanged();
        }
    }

    private void initPeopleAdapter() {
        if (commonPeopleType == 1) {
            if (!peopleArrayList.isEmpty()) {
                peopleRVAdapter = new PeopleRVAdapter(peopleArrayList, activity, Const.PEOPLE_LIST_FEEDS_COMMONS, fragType, 0);
                outerCommonRV.setAdapter(peopleRVAdapter);
                outErrrorTV.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                outerCommonRV.setVisibility(View.VISIBLE);
            } else {
                outErrrorTV.setText(errorMessage);
                outErrrorTV.setVisibility(View.VISIBLE);
                contentLayout.setVisibility(View.GONE);
                outerCommonRV.setVisibility(View.GONE);
            }
        } else {
            if (lastLikelistId.equals("0")) {
                if (!peopleArrayList.isEmpty()) {
                    peopleRVAdapter = new PeopleRVAdapter(peopleArrayList, activity, Const.PEOPLE_LIST_COMMONS, null, 0);
                    outerCommonRV.setAdapter(peopleRVAdapter);
                    outErrrorTV.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    outerCommonRV.setVisibility(View.VISIBLE);
                } else {
                    outErrrorTV.setText(errorMessage);
                    outErrrorTV.setVisibility(View.VISIBLE);
                    contentLayout.setVisibility(View.GONE);
                    outerCommonRV.setVisibility(View.GONE);
                }
            } else peopleRVAdapter.notifyDataSetChanged();
        }

    }

    protected void initNotificationAdapter() {
        if (TextUtils.isEmpty(lastActivityId)) {
            if (!notificationArrayList.isEmpty()) {
                //notificationRVAdapter = new NotificationRVAdapter(notificationArrayList, activity, this);
                outerCommonRV.setAdapter(notificationRVAdapter);
                no_notificationLayout.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                outerCommonRV.setVisibility(View.VISIBLE);
            } else {
                no_notificationLayout.setVisibility(View.VISIBLE);
                outErrrorTV.setVisibility(View.GONE);
                contentLayout.setVisibility(View.GONE);
                outerCommonRV.setVisibility(View.GONE);
            }
        } else notificationRVAdapter.notifyDataSetChanged();
    }

    protected void initRewardTransactionsAdapter() {
        if (TextUtils.isEmpty(lastRewardId)) {
            if (!rewardTransactionArrayList.isEmpty()) {
                rewardTransactionsAdapter = new RewardTransactionsAdapter(rewardTransactionArrayList);
                outerCommonRV.setAdapter(rewardTransactionsAdapter);
                outErrrorTV.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                outerCommonRV.setVisibility(View.VISIBLE);
            } else {
                outErrrorTV.setText(errorMessage);
                outErrrorTV.setVisibility(View.VISIBLE);
                contentLayout.setVisibility(View.GONE);
                outerCommonRV.setVisibility(View.GONE);
            }
        } else rewardTransactionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (takeImageClass != null)
            takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    public void initImageView(MediaFile image) {
        imageIV.setImageBitmap(image.getImage());
        imageIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageRL.setVisibility(View.VISIBLE);
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_SINGLE_COMMENT_DATA:
                networkCallForSingleCommentData();
                break;
            case API.API_ADD_COMMENT:
                networkCallForAddComment();
                break;
            case API.API_GET_COMMENT_LIST:
                networkCallForCommentList();
                break;
            case API.API_GET_USER_NOTIFICATIONS:
                networkCallForGetUserNoti(true);
                break;
            case API.API_GET_REWARD_TRANSACTION:
                networkCallForRewardTransaction();
                break;
            case API.API_ALL_NOTIFICATION_READ:
                networkCallForReadNoti();
                break;
            case API.API_LIKES_COUNT_LIST:
                networkCallForLikesCount();
                break;
            case API.API_SINGLE_POST_FOR_USER:
                networkCallForSinglePost();
                break;

            default:
                break;
        }
    }

    //region Network API calls
    public void networkCallForSingleCommentData() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getSingleCommentData(commentId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject singleDataRow = GenericUtils.getJsonObject(jsonResponse);
                            singleComment = gson.fromJson(singleDataRow.toString(), Comment.class);
                            postId = singleComment.getPost_id();
                            networkCallForSinglePost();//NetworkAPICall(API.API_SINGLE_POST_FOR_USER, true);
                        } else {
                            errorTV.setVisibility(View.VISIBLE);
                            commonRV.setVisibility(View.GONE);
                            writeCommentLL.setVisibility(View.GONE);
                            errorTV.setText(jsonResponse.optString(Constants.Extras.MESSAGE));
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_SINGLE_COMMENT_DATA);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }

                        checkFeedActivity(API.API_SINGLE_COMMENT_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                if (offlinedata == null)
                    Helper.showErrorLayoutForNoNav(API.API_SINGLE_COMMENT_DATA, activity, 0, 0);

            }
        });

    }

    public void networkCallForAddComment() {
        mProgress.show();
        Call<JsonObject> response = null;
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        if (fragType.equals(Const.COMMENT)) {
            response = apis.getAddCommentDataForComment(SharedPreference.getInstance().getLoggedInUser().getId(), postId,
                    StringEscapeUtils.escapeJava(commentText), commentImages, (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded));
        } else if (fragType.equals(Const.COMMENT_LIST)) {
            response = apis.getAddCommentDataForCommentList(commentId, SharedPreference.getInstance().getLoggedInUser().getId(),
                    postId, commentImages, StringEscapeUtils.escapeJava(commentText), (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded));
        }
        if (response != null)
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mProgress.dismiss();
                    if (response.body() != null) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            taggedPeopleArrList = new ArrayList<>();
                            taggedPeopleCommentFL.removeAllViews();
                            taggedPeopleIdsAdded = "";
                            lastCommentId = "";
                            firstVisibleItem = 0;
                            previousTotalItemCount = 0;
                            visibleItemCount = 0;
                            commentImages = "";
                            writeCommentET.setText("");
                            postCommentIBtn.setEnabled(true);
                            imageRL.setVisibility(View.GONE);

                            if (jsonResponse.optBoolean(Const.STATUS)) {
                                PostResponse postResponse = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), PostResponse.class);
                                post = postResponse;
                                commonRV.smoothScrollToPosition(0);
                                if (fragType.equals(Const.COMMENT)) {
                                    commentCount.setText(String.format("Comments (%s)", post.getComments()));
                                } else {
                                    commentCount.setText("Replies");
                                }

                                if (singleFeedView != null) {
                                    singleFeedView.setComments(post);
                                }

                                if (!TextUtils.isEmpty(post.getComments()) && !post.getComments().equals("0"))
                                    refreshCommon(true);
                                else {
                                    if (fragType.equals(Const.COMMENT)) {
                                        errorTV.setVisibility(View.VISIBLE);
                                        commonRV.setVisibility(View.GONE);
                                        errorTV.setText(activity.getString(R.string.no_comments_found));
                                    } else if (fragType.equals(Const.COMMENT_LIST)) {
                                        outErrrorTV.setVisibility(View.VISIBLE);
                                        outerCommonRV.setVisibility(View.GONE);
                                        outErrrorTV.setText(activity.getString(R.string.no_comments_found));
                                    }
                                }
                            } else {
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_ADD_COMMENT);
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));

                            }
                            checkFeedActivity(API.API_ADD_COMMENT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mProgress.dismiss();
                    if (offlinedata == null)
                        Helper.showErrorLayoutForNoNav(API.API_ADD_COMMENT, activity, 1, 1);
                }
            });

    }

    public void networkCallForCommentList() {
        mProgress.show();
        Call<JsonObject> response = null;
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        if (fragType.equals(Const.COMMENT)) {
            Log.e("COMMENT_LIST", "post_id" + postId + " " + "last_comment_id" + lastCommentId);
            response = apis.getCommentListForComment(SharedPreference.getInstance().getLoggedInUser().getId(), postId, lastCommentId);
        } else if (fragType.equals(Const.COMMENT_LIST)) {
            response = apis.getCommentListForCommentList(SharedPreference.getInstance().getLoggedInUser().getId(), postId, commentId, lastCommentId);
        }
        if (response != null)
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mProgress.dismiss();
                    outErrrorTV.setVisibility(View.GONE);
                    errorTV.setVisibility(View.GONE);

                    if (response.body() != null) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            isAlreadyConnected = 0;
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                JSONArray dataArray = GenericUtils.getJsonArray(jsonResponse);
                                if (TextUtils.isEmpty(lastCommentId)) {
                                    commentArrayList = new ArrayList<>();
                                }
                                if (dataArray.length() > 0) {
                                    int i = 0;
                                    while (i < dataArray.length()) {
                                        JSONObject singledatarow = dataArray.optJSONObject(i);
                                        Comment response1 = gson.fromJson(singledatarow.toString(), Comment.class);
                                        if (response1.getIs_expert().equalsIgnoreCase("1")) {
                                            commentArrayList.add(0, response1);
                                        } else {
                                            commentArrayList.add(response1);
                                        }
                                        i++;
                                    }
                                }
                            } else {
                                errorTV.setVisibility(View.VISIBLE);
                                outErrrorTV.setVisibility(View.GONE);
                                errorTV.setText(jsonResponse.optString(Constants.Extras.MESSAGE));
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                            initCommentAdapter();
                            if (fragType.equalsIgnoreCase(Const.COMMENT_LIST)) {
                                ((BaseABNoNavActivity) activity).setToolbarTitle("All Replies");
                            }
                            checkFeedActivity(API.API_GET_COMMENT_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mProgress.dismiss();
                    if (offlinedata == null)
                        Helper.showErrorLayoutForNoNav(API.API_GET_COMMENT_LIST, activity, 1, 1);
                }
            });

    }

    public void networkCallForGetUserNoti(final boolean show) {
        if (show)
            mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getUserNotification(SharedPreference.getInstance().getLoggedInUser().getId(), lastActivityId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (show)
                    mProgress.dismiss();

                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray jsonArray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(lastActivityId)) {
                                notificationArrayList = new ArrayList<>();
                            }
                            if (jsonArray.length() > 0) {
                                int i = 0;
                                while (i < jsonArray.length()) {
                                    JSONObject singledatarow = jsonArray.optJSONObject(i);
                                    NotificationResponse response1 = gson.fromJson(singledatarow.toString(), NotificationResponse.class);
                                    notificationArrayList.add(response1);
                                    i++;
                                }
                            }
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_USER_NOTIFICATIONS);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        checkFeedActivity(API.API_GET_USER_NOTIFICATIONS);
                        initNotificationAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (show)
                    mProgress.dismiss();
                if (offlinedata == null)
                    Helper.showErrorLayoutForNoNav(API.API_GET_USER_NOTIFICATIONS, activity, 1, 1);
            }
        });
    }

    public void networkCallForRewardTransaction() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getRewardTransaction(SharedPreference.getInstance().getLoggedInUser().getId(), lastRewardId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONArray dataarray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(lastRewardId)) {
                                rewardTransactionArrayList = new ArrayList<>();
                            }
                            if (dataarray.length() > 0) {
                                int i = 0;
                                while (i < dataarray.length()) {
                                    JSONObject singledatarow = dataarray.optJSONObject(i);
                                    RewardTransaction response1 = gson.fromJson(singledatarow.toString(), RewardTransaction.class);
                                    rewardTransactionArrayList.add(response1);
                                    i++;
                                }
                            }
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_REWARD_TRANSACTION);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        checkFeedActivity(API.API_GET_REWARD_TRANSACTION);
                        initRewardTransactionsAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                if (offlinedata == null)
                    Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_TRANSACTION, activity, 1, 1);
            }
        });
    }

    public void networkCallForReadNoti() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getReadNotification(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            lastActivityId = "";
                            if (!notificationArrayList.isEmpty()) {
                                for (NotificationResponse NR : notificationArrayList) {
                                    NR.setView_state(1);
                                }
                            }
                            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, 0);
                            ShortcutBadger.removeCount(activity.getApplicationContext());
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_ALL_NOTIFICATION_READ);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        checkFeedActivity(API.API_ALL_NOTIFICATION_READ);
                        initNotificationAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                if (offlinedata == null)
                    Helper.showErrorLayoutForNoNav(API.API_ALL_NOTIFICATION_READ, activity, 1, 1);
            }
        });
    }

    public void networkCallForLikesCount() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getLikesCount("is_watcher=" + SharedPreference.getInstance().getLoggedInUser().getId(),
                postId, lastLikelistId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            JSONArray dataArray = GenericUtils.getJsonArray(jsonResponse);
                            Log.e("people adapter ", " dataarray " + dataArray);

                            if (lastLikelistId.equals("0")) {
                                peopleArrayList = new ArrayList<>();
                            }
                            if (dataArray.length() > 0) {
                                int i = 0;
                                while (i < dataArray.length()) {
                                    JSONObject singledatarow = dataArray.optJSONObject(i);
                                    People response1 = gson.fromJson(singledatarow.toString(), People.class);
                                    peopleArrayList.add(response1);
                                    i++;
                                }
                            }
                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_LIKES_COUNT_LIST);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        checkFeedActivity(API.API_LIKES_COUNT_LIST);
                        initPeopleAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                if (offlinedata == null)
                    Helper.showErrorLayoutForNoNav(API.API_LIKES_COUNT_LIST, activity, 1, 1);

            }
        });
    }

    public void networkCallForSinglePost() {
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getSinglePostdata(SharedPreference.getInstance().getLoggedInUser().getId(), postId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        outErrrorTV.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.POSTID_REFFERED))) {
                            SharedPreference.getInstance().putString(Const.POSTID_REFFERED, "");
                            FeedsActivity.isNewPostAdded = true;
                        }
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject singleDataRow = GenericUtils.getJsonObject(jsonResponse);
                            post = gson.fromJson(singleDataRow.toString(), PostResponse.class);
                            feedsSingleRowLL.setVisibility(View.VISIBLE);
                            initPost(mView);
//                    InitcommentAdapter(); // temporary removed
                            isPostVisible = true;
                        } else {
                            errorMessage = jsonResponse.optString("message");
                            if (offlinedata == null) {
                                outErrrorTV.setVisibility(View.VISIBLE);
                                contentLayout.setVisibility(View.GONE);
                                outErrrorTV.setText(errorMessage);
//                                Helper.showErrorLayoutForNoNav(API.API_SINGLE_POST_FOR_USER, activity, 1, 0);
                            }

                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_SINGLE_POST_FOR_USER);
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        checkFeedActivity(API.API_SINGLE_POST_FOR_USER);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                if (offlinedata == null)
                    Helper.showErrorLayoutForNoNav(API.API_SINGLE_POST_FOR_USER, activity, 1, 1);
            }
        });
    }

    private void checkFeedActivity(String apiType) {
        if (!apiType.equals(API.API_GET_USER_NOTIFICATIONS) && !apiType.equals(API.API_GET_COMMENT_LIST) && !apiType.equals(API.API_SINGLE_POST_FOR_USER)) {
            Constants.isCommentRefreshed = true;
            sharedPreference.setPost(post);
        }
    }
    //endregion

    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_GET_REWARD_TRANSACTION:
            case API.API_GET_COMMENT_LIST:
            case API.API_GET_USER_NOTIFICATIONS:
            case API.API_LIKES_COUNT_LIST:
                if (TextUtils.isEmpty(lastActivityId) && TextUtils.isEmpty(lastCommentId) && lastLikelistId.equals("0") && TextUtils.isEmpty(lastRewardId)) {
                    errorMessage = jsonString;
                    if (!fragType.equals(Const.COMMENT_LIST))
                        noInternetConnection();

                    if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                        if (offlinedata == null)
                            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
                }
                break;
            case API.API_ADD_COMMENT:
                Toast.makeText(activity, jsonString, Toast.LENGTH_SHORT).show();
                postCommentIBtn.setEnabled(true);
                break;
            default:
                if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                    if (offlinedata == null)
                        Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
                break;
        }

        if (jsonString.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
            if (offlinedata == null)
                Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);
    }

    public void noInternetConnection() {
        if (fragType.equalsIgnoreCase(Const.COMMENT)) {
            errorTV.setVisibility(View.VISIBLE);
            commonRV.setVisibility(View.GONE);
            errorTV.setText(errorMessage);
        } else {
            outErrrorTV.setVisibility(View.VISIBLE);
            outerCommonRV.setVisibility(View.GONE);
            outErrrorTV.setText(errorMessage);
        }
    }

    public void initPost(View view) {

        profileNameTV.setText(post.getPost_owner_info().getName());
        if (!GenericUtils.isListEmpty(post.getPost_data().getPost_file()))
            videoUrl = post.getPost_data().getPost_file().get(0).getLink();

        if (!post.getComments().equals("0"))
            refreshCommon(true); // once the post is initialised get all comments
        else {
            errorTV.setVisibility(View.VISIBLE);
            commonRV.setVisibility(View.GONE);
            errorTV.setText(activity.getString(R.string.no_comments_found));
            if (videoUrl != null)
                loadPlayerWithVideoURL(videoUrl);
        }
        int itemtype = 0;
        if (post.getPost_type().equals(Const.POST_TYPE_MCQ))
            itemtype = 1;
        else if (post.getPost_type().equals(Const.POST_TYPE_NORMAL))
            itemtype = 2;

        String str;
        if (post.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            str = String.valueOf(HtmlCompat.fromHtml("<b>Your</b> post", HtmlCompat.FROM_HTML_MODE_LEGACY));
        else
            str = String.valueOf(HtmlCompat.fromHtml("<b>" + post.getPost_owner_info().getName() + "'s</b> post", HtmlCompat.FROM_HTML_MODE_LEGACY));

        ((BaseABNoNavActivity) activity).setToolbarTitle(str);

        singleFeedView = new SingleFeedView(activity, this);
        singleFeedView.initViews(view);
        singleFeedView.setFeed(post, itemtype);
    }


    public void setComments(int type) {
        String finalComments;
        String comments = post.getComments();
        if (type == 1)
            finalComments = String.valueOf(Integer.valueOf(comments) + 1);
        else if (type == 0)
            finalComments = String.valueOf(Integer.valueOf(comments) - 1);
        else finalComments = comments;

        post.setComments(finalComments);
        if (fragType.equalsIgnoreCase(Const.COMMENT)) {
            commentCount.setText(String.format("Comments (%s)", post.getComments()));
        } else {
            commentCount.setText("Replies");
        }
        if (singleFeedView != null)
            singleFeedView.setComments(post);
    }

    public void commentKeyboard() {
        Helper.ShowKeyboard(activity);
        writeCommentET.requestFocus();
    }

    public void refreshCommon(boolean show) {
        switch (fragType) {
            case Const.COMMENT:
                networkCallForCommentList();//NetworkAPICall(API.API_GET_COMMENT_LIST, show);
                break;
            case Const.COMMENT_LIST:
                networkCallForCommentList();//NetworkAPICall(API.API_GET_COMMENT_LIST, show);
                break;
            case Const.COMMON_PEOPLE_LIST:
                networkCallForLikesCount();//NetworkAPICall(API.API_LIKES_COUNT_LIST, show);
                break;
            case Const.NOTIFICATION:
                networkCallForGetUserNoti(show);//NetworkAPICall(API.API_GET_USER_NOTIFICATIONS, show);

                break;
            case Const.REWARD_TRANSACTION_FRAGMENT:
                networkCallForRewardTransaction();//NetworkAPICall(API.API_GET_REWARD_TRANSACTION, show);
                break;
            default:
        }
    }

    @Override
    public void deletePost(PostResponse feed) {
        FeedsActivity.isPostDeleted = true;
        sharedPreference.setPost(feed);
        activity.finish();
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
                if (isCommentEditing) {    //TODO to updating the imageView & layout during the edit comment from CommentRvAdapter.Class
                    vHolder.imageIV.setImageBitmap(mediaFile.getImage());
                    vHolder.imageIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    vHolder.mediaFile = mediaFile;
                    vHolder.commentImageRL.setVisibility(View.VISIBLE);
                } else
                    initImageView(mediaFile);
            }
        }
    }

    @Override
    public void onS3UploadData
            (ArrayList<MediaFile> images) {
        if (!images.isEmpty()) {
            for (MediaFile media : images) {
                commentImages = media.getFile();
                Log.e("Tag", commentImages);
                networkCallForAddComment();//NetworkAPICall(API.API_ADD_COMMENT, true);
            }
        }
    }
}
