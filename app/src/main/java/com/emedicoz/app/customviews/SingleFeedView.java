package com.emedicoz.app.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.dailychallenge.DailyChallengeDashboard;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.activity.ProfileActivity;
import com.emedicoz.app.feeds.adapter.GalleryAdapter;
import com.emedicoz.app.feeds.adapter.ReportAbuseListAdapter;
import com.emedicoz.app.feeds.fragment.CommonFragment;
import com.emedicoz.app.feeds.fragment.FeedsFragment;
import com.emedicoz.app.feeds.fragment.SavedNotesFragment;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.modelo.Report_reasons;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SingleFeedviewApis;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.flowlayout.FlowLayout;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.RequestInfo;
import com.white.progressview.HorizontalProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager.getOfflineDataIds;

/**
 * Created by Cbc-03 on 09/23/17.
 */

public class SingleFeedView implements eMedicozDownloadManager.SavedOfflineVideoCallBack, View.OnClickListener {

    Progress mProgress;
    RelativeLayout dailyChallengeId;
    TextView descriptionTV;
    TextView timeTV;
    TextView nameTV;
    TextView categoryTV;
    TextView tagTV;
    TextView commentTV;
    TextView likeTV;
    TextView docNameTV;
    TextView messageTV;
    TextView noteMCQTV;
    LinearLayout likeClickRL;
    LinearLayout commentClickRL;
    LinearLayout shareClickRL;
    LinearLayout taggedPeopleLL;
    LinearLayout caseOfTheDayLL;
    ImageView profilePicIV;
    ImageView profilePicIVText;
    ImageView bookmarkIV;
    ImageView videoImage;
    ImageView docImageIV;
    ImageView downloadIV;
    ImageView deleteIV;
    ImageView eyeIV;
    ImageView optionIV;

    ImageView likeClickIV;
    TextView likeClickTV;
    LinearLayout mcqOptionsLL;
    LinearLayout submitAnswerLL;
    Button submitAnswerBtn;
    Fragment fragment;
    RelativeLayout quizLL;
    RelativeLayout postCommentLikeLayout;
    LinearLayout postHeaderLayout;
    LinearLayout postFooterLayout;
    TextView dailyChallengeSubText;
    Button quizStart;
    ViewPager mViewPager;
    ProgressBar downloadProgessPB;
    RelativeLayout docRL;
    RelativeLayout viewPagerRL;
    RelativeLayout imageRL;
    RelativeLayout actionsRL;
    ArrayList<PostFile> imageArrayList;
    ArrayList<Report_reasons> reportReasonsArrayList;
    GalleryAdapter galleryAdapter;
    RelativeLayout mnVideoPlayer;
    int feedDescriptionLength = 300;
    int mcqItem;
    String reportId;
    String reportText;
    PostResponse currentFeed;
    Activity activity;
    FlowLayout taggedPeopleFL;
    List<View> linearLayoutList;
    private long mLastClickTime = 0;
    private LinearLayout dotsLayout;
    private ImageView[] dots;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //method is called when page is scrolled
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //method is called when view pager state changed
        }
    };
    private final DeletePostCallback deleteListener;


    private eMedicozDownloadManager.SavedOfflineVideoCallBack savedofflineListener;
    public View.OnClickListener onClickListener = view1 -> {
        int j = 0;
        View myView;
        j = linearLayoutList.size();
        if (view1 instanceof RadioButton) {
            myView = (View) (view1.getParent()).getParent();
        } else {
            myView = view1;
        }
        for (int i = 0; i < j; i++) {
            if (linearLayoutList.get(i).equals(myView)) {
                mcqItem = linearLayoutList.indexOf(myView) + 1;
              //  ((RadioButton) linearLayoutList.get(i).findViewById(R.id.radioRB)).setChecked(true);
                Log.e("position clicked", String.valueOf(mcqItem));
                Log.e("mcq answer", new Gson().toJson(currentFeed.getPost_data().getMcq_voting()));
                if (mcqItem != 0) {
                    networkCallForSubmitMcq(((RadioButton) linearLayoutList.get(i).findViewById(R.id.radioRB)));//networkCall.NetworkAPICall(API.API_SUBMIT_ANSWER_POST_MCQ, true);
                } else {
                    Toast.makeText(activity, "Please select any one option", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public SingleFeedView(Activity activity, DeletePostCallback deleteListener) {
        this.activity = activity;
        this.deleteListener = deleteListener;
    }

    public void initViews(View view) {
        mProgress = new Progress(activity);
        dailyChallengeId = view.findViewById(R.id.dailyChallengeId);
        mProgress.setCancelable(false);
        if (activity instanceof FeedsActivity) {
            fragment = ((FeedsActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        } else if (activity instanceof PostActivity) {
            fragment = ((PostActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        mViewPager = view.findViewById(R.id.view_pager);

        downloadProgessPB = view.findViewById(R.id.downloadProgessBar);

        taggedPeopleFL = view.findViewById(R.id.taggedpeopleFL);
        docRL = view.findViewById(R.id.docRL);
        viewPagerRL = view.findViewById(R.id.view_pagerRL);
        imageRL = view.findViewById(R.id.imageRL);
        mnVideoPlayer = view.findViewById(R.id.mn_videoplayer);
        actionsRL = view.findViewById(R.id.actionsRL);

        dotsLayout = view.findViewById(R.id.layoutDots);
        taggedPeopleLL = view.findViewById(R.id.taggedpeopleLL);

        deleteIV = view.findViewById(R.id.deleteIV);
        eyeIV = view.findViewById(R.id.eyeIV);
        downloadIV = view.findViewById(R.id.downloadIV);
        docImageIV = view.findViewById(R.id.doc_imageIV);
        quizLL = view.findViewById(R.id.quizLL);
        postHeaderLayout = view.findViewById(R.id.post_header_layout);
        postFooterLayout = view.findViewById(R.id.post_footer_layout);
        postCommentLikeLayout = view.findViewById(R.id.post_comment_like_layout);

        dailyChallengeSubText = view.findViewById(R.id.subTextItem);

        quizStart = view.findViewById(R.id.quizStart);
        videoImage = view.findViewById(R.id.video_image);
        optionIV = view.findViewById(R.id.optionTV);
        bookmarkIV = view.findViewById(R.id.bookmarkIV);
        profilePicIV = view.findViewById(R.id.profilepicIV);
        profilePicIVText = view.findViewById(R.id.profilepicIVText);

        messageTV = view.findViewById(R.id.messageTV);
        docNameTV = view.findViewById(R.id.doc_nameTV);
        descriptionTV = view.findViewById(R.id.descriptionTV);
        nameTV = view.findViewById(R.id.nameTV);
        timeTV = view.findViewById(R.id.timeTV);
        categoryTV = view.findViewById(R.id.categoryTV);
        tagTV = view.findViewById(R.id.tagTV);

        commentTV = view.findViewById(R.id.commentTV);
        likeTV = view.findViewById(R.id.likeTV);
        likeClickRL = view.findViewById(R.id.likeClickRL);
        likeClickIV = view.findViewById(R.id.likeClickIV);
        likeClickTV = view.findViewById(R.id.likeClickTV);
        commentClickRL = view.findViewById(R.id.commentClickRL);
        shareClickRL = view.findViewById(R.id.shareClickRL);
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        submitAnswerLL = view.findViewById(R.id.submitanswerLL);
        noteMCQTV = view.findViewById(R.id.noteMCQTV);
        submitAnswerBtn = view.findViewById(R.id.submitanswerBtn);
        caseOfTheDayLL = view.findViewById(R.id.caseOfTheDayLL);
        profilePicIV.setImageResource(R.mipmap.default_pic);
        savedofflineListener = this;
        linearLayoutList = new ArrayList<>();
        downloadProgessPB.setScaleY(1.5f);
    }

    public void setOnClickListner(final PostResponse feed) {

        View.OnClickListener onLikeCountListener = v -> {
            if (!feed.getLikes().equals("0")) {
                Intent intent = new Intent(activity, PostActivity.class); // comment fragment // to show all the likes
                intent.putExtra(Const.FRAG_TYPE, Const.COMMON_PEOPLE_LIST);
                intent.putExtra(Const.POST_ID, feed.getId());
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "No likes found", Toast.LENGTH_SHORT).show();
            }
        };
        View.OnClickListener onLikeClickListener = v -> {
            likeClickRL.setEnabled(false);
            currentFeed = feed;
            if (feed.isLiked()) {
                setLikes(feed, 0);
                networkcallForDislikepost();
            } else {
                setLikes(feed, 1);
                networkCallForLike();
            }
        };

        View.OnClickListener onShareClickListener = v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            currentFeed = feed;
            sharePostExternally(feed.getId(), activity);
        };

        View.OnClickListener onCommentClickListener = v -> {
            if (activity instanceof PostActivity) {
                Fragment frag = ((PostActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                ((CommonFragment) Objects.requireNonNull(frag)).commentKeyboard();
            } else
                Helper.GoToPostActivity(activity, feed, Const.COMMENT);
        };


        View.OnClickListener onBookmarkClickListener = v -> {
            bookmarkIV.setEnabled(false);
            currentFeed = feed;

            if (feed.is_bookmarked()) {
                setBookMark(0);
                networkCallForRemoveBookmark();
            } else {
                setBookMark(1);
                networkCallForAddBookmark();
            }
        };

        //setting us the listeners for the action to perform
        likeClickRL.setOnClickListener(onLikeClickListener);
        likeTV.setOnClickListener(onLikeCountListener);
        commentClickRL.setOnClickListener(onCommentClickListener);
        shareClickRL.setOnClickListener(onShareClickListener);
        bookmarkIV.setOnClickListener(onBookmarkClickListener);
        commentTV.setOnClickListener(onCommentClickListener);

        docImageIV.setOnClickListener(v -> Helper.GoToWebViewActivity(activity, feed.getPost_data().getPost_file().get(0).getLink()));

        nameTV.setOnClickListener((View view) -> {
            if (!(activity instanceof ProfileActivity))
                Helper.GoToProfileActivity(activity, feed.getUser_id());
        });
        imageRL.setOnClickListener((View view) -> {
            if (!(activity instanceof ProfileActivity))
                Helper.GoToProfileActivity(activity, feed.getUser_id());
        });

        downloadIV.setOnClickListener((View view) -> {
            if (!feed.getPost_data().getPost_text_type().equals(Const.POST_TEXT_TYPE_YOUTUBE_TEXT)) {
                offlineData offline = getOfflineDataIds(feed.getId(), Const.FEEDS, activity, feed.getId());

                if (offline != null && offline.getRequestInfo() == null)
                    offline.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offline.getDownloadid()));

                if (offline != null && offline.getRequestInfo() != null) {
                    //when video is downloading
                    if (offline.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING || offline.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED) {
                        Toast.makeText(activity, R.string.video_download_in_progress, Toast.LENGTH_SHORT).show();
                    }
                    //when video is paused
                    else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_PAUSED) {

                        messageTV.setVisibility(View.VISIBLE);
                        downloadProgessPB.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        messageTV.setText(R.string.download_pending);

                        eMedicozDownloadManager.getFetchInstance().resume(offline.getRequestInfo().getId());
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                                savedofflineListener, "video");

                    }

                    //when some error occurred
                    else if (offline.getRequestInfo().getStatus() == Fetch.STATUS_ERROR) {

                        messageTV.setVisibility(View.VISIBLE);
                        downloadProgessPB.setVisibility(View.VISIBLE);
                        downloadIV.setVisibility(View.GONE);
                        deleteIV.setVisibility(View.VISIBLE);
                        eyeIV.setVisibility(View.GONE);
                        messageTV.setText(R.string.download_pending);

                        eMedicozDownloadManager.getFetchInstance().retry(offline.getDownloadid());
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(offline.getDownloadid(),
                                savedofflineListener, "video");

                    }
                }//for new download
                else if (offline == null || offline.getRequestInfo() == null) {
                    try {
                        eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, feed.getId(),
                                Helper.getCloudFrontUrl() + feed.getPost_data().getPost_file().get(0).getFile_info(),
                                URLDecoder.decode(feed.getPost_data().getPost_file().get(0).getFile_info()),
                                Const.FEEDS, feed.getId(), downloadid -> {

                                    messageTV.setVisibility(View.VISIBLE);
                                    downloadProgessPB.setVisibility(View.VISIBLE);
                                    downloadIV.setVisibility(View.GONE);
                                    deleteIV.setVisibility(View.VISIBLE);
                                    eyeIV.setVisibility(View.GONE);
                                    messageTV.setText(R.string.download_queued);

                                    if (downloadid == Constants.MIGRATED_DOWNLOAD_ID) {
                                        downloadIV.setVisibility(View.VISIBLE);
                                        deleteIV.setVisibility(View.VISIBLE);
                                        downloadIV.setImageResource(R.mipmap.eye_on);
                                        messageTV.setVisibility(View.VISIBLE);
                                        messageTV.setText(R.string.downloaded_offline);

                                        if (downloadProgessPB.getVisibility() != View.GONE)
                                            downloadProgessPB.setVisibility(View.GONE);
                                    } else if (downloadid != 0)
                                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(downloadid, savedofflineListener, Const.VIDEOS);
                                    else {
                                        messageTV.setVisibility(View.INVISIBLE);
                                        downloadProgessPB.setVisibility(View.GONE);
                                        downloadIV.setVisibility(View.VISIBLE);
                                        deleteIV.setVisibility(View.GONE);
                                        eyeIV.setVisibility(View.GONE);
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
                        downloadIV.performClick();
                    }
                }
            }

        });

        deleteIV.setOnClickListener((View view) -> getDownloadCancelDialog(feed, activity, "Delete Download", "Are you sure you want to delete the download?"));

        mnVideoPlayer.setOnClickListener(onCommentClickListener);
        eyeIV.setOnClickListener(onCommentClickListener);

//        eyeIV.setOnClickListener((View view) -> {
//            /*offlineData offlinedata = getOfflineDataIds(feed.getId(), Const.FEEDS, activity, feed.getId());
//            if (offlinedata != null && offlinedata.getRequestInfo() == null)
//                offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));
//
//            if (offlinedata != null && offlinedata.getRequestInfo() != null && offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DONE) {
//                Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlinedata.getLink(), Const.VIDEO_STREAM, feed.getId(), Const.COURSE_VIDEO_TYPE);
////                loadPlayerWithVideoURL(activity.getFilesDir() + "/" + offlinedata.getLink());
//            }*/
//        });
    }

    public void getDownloadCancelDialog(final PostResponse feed, final Activity ctx, final String title, final String message) {
        View v = Helper.newCustomDialog(ctx, true, title, message);

        Button btnCancel;
        Button btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(ctx.getString(R.string.no));
        btnSubmit.setText(ctx.getString(R.string.yes));

        btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

        btnSubmit.setOnClickListener((View view) -> {
            Helper.dismissDialog();
            eMedicozDownloadManager.removeOfflineData(feed.getId(), Const.FEEDS, ctx, feed.getId());

            downloadIV.setVisibility(View.VISIBLE);
            downloadIV.setImageResource(R.drawable.app_progress);

            downloadProgessPB.setVisibility(View.GONE);
            downloadProgessPB.setProgress(0);

            messageTV.setVisibility(View.INVISIBLE);
            deleteIV.setVisibility(View.GONE);
            eyeIV.setVisibility(View.GONE);
        });
    }

    public void initImageView() {
        addBottomDots(0);
        galleryAdapter = new GalleryAdapter(activity, imageArrayList) {
            @Override
            public void getDynamicHeight(int height) {
                if (imageArrayList.size() == 1) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
                    viewPagerRL.setLayoutParams(params);
                }
            }
        };
        mViewPager.setAdapter(galleryAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        dotsLayout.removeAllViews();
        if (imageArrayList.size() > 1) {
            dots = new ImageView[imageArrayList.size()];
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new ImageView(activity);
                dots[i].setImageResource(R.drawable.nonselecteditem_dot);
                dots[i].setPadding(5, 5, 5, 0);
                dotsLayout.addView(dots[i]);
            }
            if (dots.length > 0)
                dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
        }
    }

    public void setFeed(final PostResponse feed, int itemType) {
        currentFeed = feed;

        setOnClickListner(feed);
        if (!feed.getPost_data().getPost_file().isEmpty()) {

            // if the feeds contains any images
            switch (feed.getPost_data().getPost_file().get(0).getFile_type()) {
                case Const.IMAGE:
                    quizLL.setVisibility(View.GONE);
                    viewPagerRL.setVisibility(View.VISIBLE);
                    mnVideoPlayer.setVisibility(View.GONE);
                    docRL.setVisibility(View.GONE);

                    imageArrayList = feed.getPost_data().getPost_file();
                    initImageView();

                    // if feed contains any video
                    break;
                case Const.VIDEO:
                    quizLL.setVisibility(View.GONE);
                    mnVideoPlayer.setVisibility(View.VISIBLE);
                    viewPagerRL.setVisibility(View.GONE);
                    docRL.setVisibility(View.GONE);
                    actionsRL.setVisibility(View.GONE);

                    // if the video is not of youtube
                    if (!feed.getPost_data().getPost_text_type().equals(Const.POST_TEXT_TYPE_YOUTUBE_TEXT)) {

                        actionsRL.setVisibility(View.VISIBLE);
                        offlineData offlinedata = getOfflineDataIds(feed.getId(), Const.FEEDS, activity, feed.getId());
                        downloadIV.setImageResource(R.mipmap.download_download);

                        if (offlinedata != null && offlinedata.getRequestInfo() == null)
                            offlinedata.setRequestInfo(eMedicozDownloadManager.getFetchInstance().get(offlinedata.getDownloadid()));

                        if (offlinedata != null && offlinedata.getRequestInfo() != null) {
                            downloadProgessPB.setVisibility(offlinedata.getRequestInfo().getProgress() < 100 ? View.VISIBLE : View.GONE);

                            // 4 conditions to check at the time of intialising the video

                            //0. downloading in queue
                            if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_QUEUED)) {
                                downloadIV.setVisibility(View.GONE);
                                deleteIV.setVisibility(View.VISIBLE);
                                messageTV.setText(R.string.download_queued);
                                eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedofflineListener, Const.VIDEOS);
                            }
                            //1. downloading in progress
                            else if ((offlinedata.getRequestInfo().getStatus() == Fetch.STATUS_DOWNLOADING)
                                    && offlinedata.getRequestInfo().getProgress() < 100) {

                                downloadIV.setVisibility(View.GONE);
                                deleteIV.setVisibility(View.VISIBLE);
                                eyeIV.setVisibility(View.GONE);
                                downloadProgessPB.setVisibility(View.VISIBLE);
                                downloadProgessPB.setProgress(offlinedata.getRequestInfo().getProgress());
                                messageTV.setText(R.string.download_pending);

                                eMedicozDownloadManager.getInstance().downloadProgressUpdate(offlinedata.getDownloadid(), savedofflineListener, Const.VIDEOS);

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

                                downloadProgessPB.setVisibility(View.VISIBLE);
                                downloadProgessPB.setProgress(offlinedata.getRequestInfo().getProgress());

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
                            downloadIV.setImageResource(R.mipmap.download_download);
                        }

                    }
                    if (feed.getPost_data().getPost_file().get(0).getLink().contains(Helper.getS3EndPoint()))
                        Glide.with(activity).load(feed.getPost_data().getPost_file().get(0).getLink()).into(videoImage);
                    else
                        Glide.with(activity).load(Helper.getS3EndPoint() + Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES +
                                "/" + feed.getPost_data().getPost_file().get(0).getFile_info().split(".mp4")[0] + ".png")
                                .into(videoImage);

                    videoImage.setScaleType(ImageView.ScaleType.FIT_XY);

                    break;
                case Const.PDF:
                case Const.DOC:
                case Const.XLS:

                    quizLL.setVisibility(View.GONE);
                    mnVideoPlayer.setVisibility(View.GONE);
                    viewPagerRL.setVisibility(View.GONE);
                    docRL.setVisibility(View.VISIBLE);

                    switch (feed.getPost_data().getPost_file().get(0).getFile_type()) {
                        case Const.PDF:
                            docNameTV.setText(feed.getPost_data().getPost_file().get(0).getFile_info());
                            docImageIV.setImageResource(R.mipmap.pdf_download);
                            break;
                        case Const.DOC:
                            docNameTV.setText(feed.getPost_data().getPost_file().get(0).getFile_info());
                            docImageIV.setImageResource(R.mipmap.doc_download);
                            break;
                        case Const.XLS:
                            docNameTV.setText(feed.getPost_data().getPost_file().get(0).getFile_info());
                            docImageIV.setImageResource(R.mipmap.xls_download);
                            break;
                        default:
                            break;
                    }
                    break;
                case "quiz":
                    if (SharedPreference.getInstance().getMasterHitResponse() != null && SharedPreference.getInstance().getMasterHitResponse().getShow_daily_quiz() != null) {
                        if (SharedPreference.getInstance().getMasterHitResponse().getShow_daily_quiz().equalsIgnoreCase("0")) {
                            quizLL.setVisibility(View.GONE);
                        } else {
                            quizLL.setVisibility(View.VISIBLE);
                            postFooterLayout.setVisibility(View.GONE);
                            postHeaderLayout.setVisibility(View.GONE);
                            postCommentLikeLayout.setVisibility(View.GONE);
                            descriptionTV.setVisibility(View.GONE);
                        }
                    } else {
                        quizLL.setVisibility(View.VISIBLE);
                        postFooterLayout.setVisibility(View.GONE);
                        postHeaderLayout.setVisibility(View.GONE);
                        postCommentLikeLayout.setVisibility(View.GONE);
                        descriptionTV.setVisibility(View.GONE);
                    }
                    final String testseriesid = feed.getPost_data().getPost_file().get(0).getFile_info();
                    SharedPreference.getInstance().putString(Const.TEST_SERIES_ID, testseriesid);

                    if (feed.getPost_data().getPost_file().get(0).getTest_segment_id().equalsIgnoreCase("")) {
                        dailyChallengeId.setBackgroundResource(R.drawable.daily_challenge_start_btn_red);
                        dailyChallengeSubText.setText("ENTER THE ARENA");
                        quizStart.setText("Start Now");
                    } else {
                        dailyChallengeId.setBackgroundResource(R.drawable.daily_challenge_result_btn_green);
                        dailyChallengeSubText.setText("CHECK TODAY'S PERFORMANCE");
                        quizStart.setText("View Result");
                    }

                    dailyChallengeId.setOnClickListener((View view) -> {
                        SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                        SharedPreference.getInstance().putString("SOLUTION", "No");

                        if (activity instanceof BaseABNavActivity) {
                            ((BaseABNavActivity) activity).dailyQuizClicked = true;
                            PostFile postFile = eMedicozApp.getInstance().postFile;
                            /*if (postFile == null) {
                                Toast.makeText(activity, R.string.wait_till_data_loads, Toast.LENGTH_SHORT).show();
                                ((BaseABNavActivity) activity).dailyQuizClicked = false;
                                return;
                            }*/
                            fragment = DailyChallengeDashboard.newInstance(postFile);
                            ((BaseABNavActivity) activity).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment).addToBackStack(Const.DAILY_CHALLENGE).commit();
                        }
                    });
                    break;
                default:
                    break;
            }
        } else {
            quizLL.setVisibility(View.GONE);
            mnVideoPlayer.setVisibility(View.GONE);
            viewPagerRL.setVisibility(View.GONE);
            docRL.setVisibility(View.GONE);
        }

        //for tagging people UI
        int counter = 0;
        if (feed.getTagged_people() != null && !feed.getTagged_people().isEmpty()) {
            taggedPeopleLL.setVisibility(View.VISIBLE);

            if (taggedPeopleFL.getChildCount() > 0) taggedPeopleFL.removeAllViews();

            if (TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                for (People ppl : feed.getTagged_people()) {
                    if (!ppl.getId().equals(Const.DAMS_MOPUP_USER_ID)) {
                        addViewToTagPeople(ppl);
                        counter++;
                    }
                }
                if (counter == 0) taggedPeopleLL.setVisibility(View.GONE);
            } else {
                for (People ppl : feed.getTagged_people()) {

                    addViewToTagPeople(ppl);
                }
            }
        } else taggedPeopleLL.setVisibility(View.GONE);

        categoryTV.setText(feed.getPost_owner_info().getSpeciality());

        // this is for post tags
        if (TextUtils.isEmpty(feed.getPost_tag())) {
            tagTV.setVisibility(View.GONE);
        } else {
            tagTV.setVisibility(View.VISIBLE);
            tagTV.setText(feed.getPost_tag());
        }

        // setting the time in the feeds.
        timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(feed.getCreation_time()))
                .equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(feed.getCreation_time())));


        //setting the option if can report abuse or not.
        optionIV.setOnClickListener((View view) -> {
            currentFeed = feed;
            reportReasonsArrayList = new ArrayList<>();
            int temp = 0;
            if (feed.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId())) {
                temp++;
                SharedPreference.getInstance().putInt("PinnedDecider", temp);
            }
            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) && SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equals("1")) {
                temp += 2;
                SharedPreference.getInstance().putInt("PinnedDecider", temp);
            }
            showPopMenu(view, temp);
        });


        //Capitalizing the first letter of User's Name
        feed.getPost_owner_info().setName(Helper.CapitalizeText(feed.getPost_owner_info().getName()));


        profilePicIV.setImageResource(R.color.transparent);
        profilePicIV.setImageResource(R.mipmap.default_pic);
        //Setting user Image or Text Drawable
        if (!TextUtils.isEmpty(feed.getPost_owner_info().getProfile_picture())) {
            profilePicIV.setVisibility(View.VISIBLE);
            profilePicIVText.setVisibility(View.GONE);
            Glide.with(activity)
                    .asBitmap()
                    .load(feed.getPost_owner_info().getProfile_picture())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.default_pic))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            profilePicIV.setImageBitmap(result);
                        }
                    });
        } else {
            Drawable dr = Helper.GetDrawable(feed.getPost_owner_info().getName(), activity, feed.getPost_owner_info().getId());
            if (dr != null) {
                profilePicIV.setVisibility(View.GONE);
                profilePicIVText.setVisibility(View.VISIBLE);
                profilePicIVText.setImageDrawable(dr);
            } else {
                profilePicIV.setVisibility(View.VISIBLE);
                profilePicIVText.setVisibility(View.GONE);
                profilePicIV.setImageResource(R.mipmap.default_pic);
            }
        }

        //like button action
        setLikes(feed, 2);

        //comment view
        setComments(feed);

        //Bookmark/Save button action
        if (feed.is_bookmarked()) {
            bookmarkIV.setImageResource(R.mipmap.bookmarked);

        } else {
            bookmarkIV.setImageResource(R.mipmap.bookmark);
        }

        //setting up user's name or the shared post
        nameTV.setText(HtmlCompat.fromHtml("<b>" + feed.getPost_owner_info().getName() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));


        //setting description or mcq answers
        if (itemType == 1) {
            boolean pollVisiblity = false;
            mcqOptionsLL.removeAllViews();
            linearLayoutList.clear();
            noteMCQTV.setText(HtmlCompat.fromHtml(activity.getResources().getString(R.string.mcqNote), HtmlCompat.FROM_HTML_MODE_LEGACY));

            //TODO to set the item view to pinned or not
            if (!TextUtils.isEmpty(currentFeed.getPinned_post()))
                caseOfTheDayLL.setVisibility(View.VISIBLE);
            else caseOfTheDayLL.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_given_by_user())) {
                pollVisiblity = true;
                submitAnswerLL.setVisibility(View.GONE);
                noteMCQTV.setVisibility(View.GONE);
            } else {
                submitAnswerLL.setVisibility(View.GONE);
                noteMCQTV.setVisibility(View.VISIBLE);
            }
            descriptionTV.setText(String.format("%s", feed.getPost_data().getQuestion()));

            if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_one())) {
                mcqOptionsLL.addView(initMCQOptionView("A", feed.getPost_data().getAnswer_one(),
                        feed.getPost_data().getMcq_voting().getAnswer_one(), pollVisiblity));
            }
            if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_two())) {
                mcqOptionsLL.addView(initMCQOptionView("B", feed.getPost_data().getAnswer_two(),
                        feed.getPost_data().getMcq_voting().getAnswer_two(), pollVisiblity));
            }
            if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_three())) {
                mcqOptionsLL.addView(initMCQOptionView("C", feed.getPost_data().getAnswer_three(),
                        feed.getPost_data().getMcq_voting().getAnswer_three(), pollVisiblity));
            }
            if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_four())) {
                mcqOptionsLL.addView(initMCQOptionView("D", feed.getPost_data().getAnswer_four(),
                        feed.getPost_data().getMcq_voting().getAnswer_four(), pollVisiblity));
            }
            if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_five())) {
                mcqOptionsLL.addView(initMCQOptionView("E", feed.getPost_data().getAnswer_five(),
                        feed.getPost_data().getMcq_voting().getAnswer_five(), pollVisiblity));
            }

        } else if (itemType == 2) {
            mcqOptionsLL.setVisibility(View.GONE);
            String des = feed.getPost_data().getText();


            //TODO to set the item view to pinned or not
            if (!TextUtils.isEmpty(currentFeed.getPinned_post()))
                caseOfTheDayLL.setVisibility(View.VISIBLE);
            else caseOfTheDayLL.setVisibility(View.GONE);

            if (feed.getPost_data().getPost_text_type().equals(Const.POST_TEXT_TYPE_YOUTUBE_TEXT)) {
                actionsRL.setVisibility(View.GONE);
                mnVideoPlayer.setVisibility(View.VISIBLE);
                viewPagerRL.setVisibility(View.GONE);
                docRL.setVisibility(View.GONE);

                String x = Helper.youtubevalidation(des);
                if (x != null) {
                    String imgUrl = "http://img.youtube.com/vi/" + x + "/0.jpg"; // this is link which will give u thumnail image of that video
                    Glide.with(activity).load(imgUrl).into(videoImage);
                }
                videoImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            des = removeYoutubeUrl(des);

            if (!(activity instanceof PostActivity) && des.length() > feedDescriptionLength) {
                des = des.substring(0, feedDescriptionLength) + "...";
                descriptionTV.setText(String.format("%s%s", des, HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                ClickableSpan readmoreclick = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, PostActivity.class); // comment fragment
                        intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                        intent.putExtra(Const.POST, feed);
                        activity.startActivity(intent);
                    }
                };
                makeLinks(descriptionTV, "Read More", readmoreclick);
            } else {
                descriptionTV.setText(des);
            }
        }
    }
//    private void showMCQPopupView(final PostResponse feed) {
//        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View v = li.inflate(R.layout.submit_answer_mcq, null, false);
//        final Dialog submitMcq = new Dialog(activity);
//        submitMcq.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        submitMcq.setCanceledOnTouchOutside(false);
//        submitMcq.setContentView(v);
//        submitMcq.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        submitMcq.show();

//        CardView submitCard;
//        TextView questionTV;
//
//        submitCard = v.findViewById(R.id.bottomCard);
//        questionTV = v.findViewById(R.id.questionTV);
//        mcqoptions = v.findViewById(R.id.mcqoptions);
//
//        mcqoptions.removeAllViews();
//        LinearLayoutList.clear();
//        questionTV.setText(String.format("%s %s", "Q. ", feed.getPost_data().getQuestion()));
//
//        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_one())) {
//            mcqoptions.addView(initMCQPopupView("A", feed.getPost_data().getAnswer_one()));
//        }
//        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_two())) {
//            mcqoptions.addView(initMCQPopupView("B", feed.getPost_data().getAnswer_two()));
//        }
//        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_three())) {
//            mcqoptions.addView(initMCQPopupView("C", feed.getPost_data().getAnswer_three()));
//        }
//        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_four())) {
//            mcqoptions.addView(initMCQPopupView("D", feed.getPost_data().getAnswer_four()));
//        }
//        if (!TextUtils.isEmpty(feed.getPost_data().getAnswer_five())) {
//            mcqoptions.addView(initMCQPopupView("E", feed.getPost_data().getAnswer_five()));
//        }
//
//        submitCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mcq_item != 0) {
//                    networkCall.NetworkAPICall(API.API_SUBMIT_ANSWER_POST_MCQ, true);
//                    submitMcq.dismiss();
//                } else {
//                    Toast.makeText(activity, "Please select any one option", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }

//    private LinearLayout initMCQPopupView(String text1, String text2) {
//        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.mcq_popup, null);
//        TextView tv = (TextView) v.findViewById(R.id.optioniconTV);
//        final CheckedTextView radioButton = v.findViewById(R.id.radioOptionTV);
//        tv = changeBackgroundColor(tv, 1);
////        v = changeBackgroundColor(v, 1);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, 10, 0, 0);
//        v.setLayoutParams(lp);
//        tv.setText(text1);
//        radioButton.setText(text2);
//
//        v.setTag(tv.getText());
////        v.setOnClickListener(onClickListener);
////        LinearLayoutList.add(v);
//        return v;
//    }

    public void sharePostExternally(String id, final Activity activity) {

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://emedicoz.com/?postId=" + id))
                .setDynamicLinkDomain("wn2d8.app.goo.gl")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
//                .setIosParameters(new DynamicLink.IosParameters.Builder("com.eMedicoz.app").build())
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();

                        String msgHtml = String.format("<p>Let's refer the post by %s on eMedicoz App."
                                + "<a href=\"%s\">Click Here</a>!</p>", currentFeed.getPost_owner_info().getName(), shortLink.toString());

                        String msg = "Let's refer the post by " + currentFeed.getPost_owner_info().getName() + " on eMedicoz App. Click on Link : " + shortLink.toString();

                        Helper.sendLink(activity, "eMedicoz Post Reference", msg,
                                msgHtml
                        );
                    } else {
                        Toast.makeText(activity, "Link could not be generated please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String removeYoutubeUrl(String des) {
        des = des.trim();
        String[] parts = des.split("\\s+");
        final String regex1 = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|\u200C\u200B%2Fvideos%2F|embed%2\u200C\u200BF|youtu.be%2F|%2Fv%2\u200C\u200BF)[^#\\&\\?\\n]*";
        final Pattern pattern1 = Pattern.compile(regex1, Pattern.MULTILINE);
        for (String part : parts) {
            final Matcher matcher1 = pattern1.matcher(part);
            Log.d("Youtube Validation", "Matching");
            if (matcher1.find()) {
                Log.d("Youtube Validation", "Matched");
                des = des.replaceAll(part, "");
            }
        }
        return des;
    }

    public void makeLinks(TextView textView, String link, ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(textView.getText());

        int startIndexOfLink = textView.getText().toString().indexOf(link);
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public LinearLayout initMCQOptionView(String text1, String text2, final String pollValue, boolean pollVisiblity) {
        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.layout_option_mcq_view, null);
        TextView tv = v.findViewById(R.id.optionIconTV);
        TextView text = v.findViewById(R.id.optionTextTV);
        RadioButton radioRB = v.findViewById(R.id.radioRB);
        LinearLayout parentLL = v.findViewById(R.id.viewLL);
        final HorizontalProgressView progressBarPoll = v.findViewById(R.id.progressPoll);

        if (!pollVisiblity) {
            tv = changeBackgroundColor(tv, 1);
            parentLL = changeBackgroundColor(parentLL, 1);
            v.setOnClickListener(onClickListener);
            radioRB.setOnClickListener(onClickListener);
            radioRB.setVisibility(View.VISIBLE);
        }


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);

        if (pollVisiblity) {
            radioRB.setVisibility(View.GONE);
            progressBarPoll.setVisibility(View.VISIBLE);
            progressBarPoll.setMax(100);
            progressBarPoll.setProgressInTime(0, Integer.parseInt(pollValue), 5);
        }

        v.setLayoutParams(lp);
        tv.setText(String.format("%s.", text1));
        tv.setGravity(Gravity.CENTER);
        text.setText(text2);
        v.setTag(tv.getText());
        linearLayoutList.add(v);
        return v;
    }

    public TextView changeBackgroundColor(TextView v, int type) {

        v.setBackgroundResource(R.drawable.bg_refcode_et);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1)
            drawable.setColor(ContextCompat.getColor(v.getContext(), R.color.greayrefcode_dark));
        else if (type == 2)
            drawable.setColor(ContextCompat.getColor(v.getContext(), R.color.green));
        else if (type == 3) drawable.setColor(ContextCompat.getColor(v.getContext(), R.color.red));
        return v;
    }

    public LinearLayout changeBackgroundColor(LinearLayout v, int type) {

        v.setBackgroundResource(R.drawable.bg_refcode_et);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(ContextCompat.getColor(v.getContext(), R.color.white));
        if (type == 1)
            drawable.setStroke(2, ContextCompat.getColor(v.getContext(), R.color.greayrefcode_dark));
        else if (type == 2)
            drawable.setStroke(2, ContextCompat.getColor(v.getContext(), R.color.green));
        else if (type == 3)
            drawable.setStroke(2, ContextCompat.getColor(v.getContext(), R.color.red));

        return v;
    }

    public void showPopMenu(final View v, int userDecider) {
        PopupMenu popup = new PopupMenu(activity, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reportIM:
                        networkCallForReportAbuseList();//networkCall.NetworkAPICall(API.API_GET_REPORT_ABUSE_LIST, true);
                        return true;

                    case R.id.editIM:
                        editPost();
                        return true;

                    case R.id.pinIM:
                        setPostAsPinned(item.getTitle().toString());
                        return true;

                    case R.id.deleteIM:
                        View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.deleteMessage));

                        Button btnCancel, btnSubmit;

                        btnCancel = v.findViewById(R.id.btn_cancel);
                        btnSubmit = v.findViewById(R.id.btn_submit);

                        btnCancel.setText(activity.getString(R.string.cancel));
                        btnSubmit.setText(activity.getString(R.string.delete));

                        btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

                        btnSubmit.setOnClickListener((View view) -> {
                            Helper.dismissDialog();
                            deletePost();
                        });
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.feed_menu);
        Menu menu = popup.getMenu();

        menu.findItem(R.id.reportIM).setVisible((userDecider == 0 || userDecider == 2));
        menu.findItem(R.id.editIM).setVisible((userDecider != 0));
        menu.findItem(R.id.deleteIM).setVisible((userDecider != 0));
        menu.findItem(R.id.pinIM).setVisible((userDecider == 3 || userDecider == 2));

        // to set post as Pinned or Unpinned
        if (TextUtils.isEmpty(currentFeed.getPinned_post()))
            menu.findItem(R.id.pinIM).setTitle("Pin");
        else menu.findItem(R.id.pinIM).setTitle("Unpin");

        popup.show();
    }

    private void setPostAsPinned(String title) {
        if (!title.equals("Pin"))
            networkCallForUnpin();//networkCall.NetworkAPICall(API.API_SET_POST_AS_UNPINNED, true);
        else
            networkCallForSetPostalPin();//networkCall.NetworkAPICall(API.API_SET_POST_AS_PINNED, true);
    }

    public void setReportId(String id) {
        reportId = id;
        Log.e("ReportId :", reportId);
    }

    private void showReportPostView() {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.report_abuse, null, false);
        final Dialog reportPost = new Dialog(activity);
        reportPost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportPost.setCanceledOnTouchOutside(false);
        reportPost.setContentView(v);
        reportPost.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reportPost.show();

        RecyclerView reportPostList;
        CardView submitCard, upperCard;
        ImageView cross;
        EditText writefeedbackET;

        reportPostList = v.findViewById(R.id.reasonListReport);
        submitCard = v.findViewById(R.id.bottomCard);
        upperCard = v.findViewById(R.id.upperCard);
        cross = v.findViewById(R.id.crossimageIV);
        writefeedbackET = v.findViewById(R.id.writefeedbackET);

        if (reportPostList.getVisibility() == View.GONE) {
            reportPostList.setVisibility(View.VISIBLE);
            writefeedbackET.setVisibility(View.VISIBLE);
            upperCard.setVisibility(View.VISIBLE);
        }

        reportPostList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        ReportAbuseListAdapter reportAbuseListAdapter = new ReportAbuseListAdapter(activity, reportReasonsArrayList, SingleFeedView.this);
        reportPostList.setAdapter(reportAbuseListAdapter);


        submitCard.setTag(R.id.questions, v);
        submitCard.setTag(R.id.optionsAns, reportPost);
        submitCard.setOnClickListener(this);
        cross.setTag(reportPost);
        cross.setOnClickListener(this);
    }

    public void deletePost() {
        networkCallForDeletePost();//networkCall.NetworkAPICall(API.API_DELETE_POST, true);
    }

    public void reportPost() {
        networkCallForReportPost();//networkCall.NetworkAPICall(API.API_REPORT_POST, true);
    }

    public void editPost() {
        Intent intent = new Intent(activity, PostActivity.class);// Edit Post
        intent.putExtra(Const.FRAG_TYPE, Const.POST_FRAG);
        intent.putExtra(Const.POST, currentFeed);
        activity.startActivity(intent);
    }

    // this is to show the tagged people in the post
    public void addViewToTagPeople(final People response) {

        View v = View.inflate(activity, R.layout.single_textview_people_tag, null);
        TextView tv = v.findViewById(R.id.nameTV);
        ImageView delete = v.findViewById(R.id.deleteIV);
        delete.setVisibility(View.GONE);
        tv.setText(response.getName());
        v.setTag(response);
        v.setOnClickListener((View view) -> Helper.GoToProfileActivity(activity, response.getId()));

        taggedPeopleFL.addView(v);
    }

    public void setLikes(final PostResponse feed, int type) {
        String finallikes = "";
        String likes = feed.getLikes();

        //this is to increment or decrement the likes on the post.
        if (type == 1) {
            feed.setLiked(true);
            finallikes = String.valueOf(Integer.parseInt(likes) + 1);
        } else if (type == 0) {
            feed.setLiked(false);
            finallikes = String.valueOf(Integer.parseInt(likes) - 1);
        } else
            finallikes = likes;

        // this is to change the icon and color of likes on the post.
        if (likeClickIV != null && likeClickTV != null) {
            if (feed.isLiked()) {
                likeClickTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                likeClickIV.setImageResource(R.mipmap.like_blue);
            } else {
                likeClickTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
                likeClickIV.setImageResource(R.mipmap.like);
            }
        }
        feed.setLikes(finallikes);

        //this is to show the count of likes on the post.
        if (feed.getLikes().equals("1") || feed.getLikes().equals("0"))
            likeTV.setText(String.format("%s like", feed.getLikes()));
        else
            likeTV.setText(String.format("%s likes", feed.getLikes()));

    }

    public void setComments(final PostResponse feed) {

        if (!TextUtils.isEmpty(feed.getComments()) && feed.getComments().equals("1"))
            commentTV.setText(String.format("%s Comment", feed.getComments()));
        else
            commentTV.setText(String.format("%s Comments", TextUtils.isEmpty(feed.getComments()) ? "0" : feed.getComments()));

    }

    private void networkCallForLike() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getLikePost(SharedPreference.getInstance().getLoggedInUser().getId(), currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        likeClickRL.setEnabled(true);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            checkPostActivity(API.API_LIKE_POST);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            setLikes(currentFeed, 0);

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkcallForDislikepost() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getDisLikePost(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        likeClickRL.setEnabled(true);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            checkPostActivity(API.API_DISLIKE_POST);

                        } else {
                            likeClickRL.setEnabled(true);
                            setLikes(currentFeed, 1);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForAddBookmark() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getBookmarkePost(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        bookmarkIV.setEnabled(true);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            checkPostActivity(API.API_ADD_BOOKMARK);
                            Log.e("API_ADD_BOOKMARK ", "API_ADD_BOOKMARK true");
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("API_ADD_BOOKMARK ", "API_ADD_BOOKMARK false");

                            currentFeed.setIs_bookmarked(false);
                            bookmarkIV.setImageResource(R.mipmap.bookmark);

                            bookmarkIV.setEnabled(true);

                            setBookMark(0);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForRemoveBookmark() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getRemoveBookmark(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        bookmarkIV.setEnabled(true);
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            checkPostActivity(API.API_REMOVE_BOOKMARK);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            Log.e("API_REMOVE_BOOKMARK ", "API_REMOVE_BOOKMARK true");
                        } else {
                            bookmarkIV.setEnabled(true);
                            setBookMark(1);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForReportPost() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getReportPost(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getId(), reportId, reportText);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            currentFeed.setReport_abuse("1");
                            checkPostActivity(API.API_REPORT_POST);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForDeletePost() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getDeletePost(currentFeed.getUser_id(), currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            checkPostActivity(API.API_DELETE_POST);
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();

                            if (deleteListener != null && currentFeed != null)
                                deleteListener.deletePost(currentFeed);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForReportAbuseList() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getAbuseList();
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            checkPostActivity(API.API_GET_REPORT_ABUSE_LIST);
                            JSONArray arrJson = GenericUtils.getJsonObject(jsonResponse).optJSONArray("report_reasons");
                            if (arrJson != null) {
                                for (int i = 0; i < arrJson.length(); i++) {
                                    JSONObject jsonObject1 = arrJson.optJSONObject(i);
                                    Report_reasons reportReasons = new Gson().fromJson(jsonObject1.toString(), Report_reasons.class);
                                    reportReasonsArrayList.add(reportReasons);
                                }
                            }
                            showReportPostView();
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForUnpin() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.removePinnedPost(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("POST UNPINNED", jsonResponse.optString(Constants.Extras.MESSAGE));
                            currentFeed.setPinned_post(GenericUtils.getJsonObject(jsonResponse).optString("pinned_post"));
                            caseOfTheDayLL.setVisibility(View.GONE);
                            checkPostActivity(API.API_SET_POST_AS_UNPINNED);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForSetPostalPin() {
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.getPinnedPost(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("POST PINNED", jsonResponse.optString(Constants.Extras.MESSAGE));
                            currentFeed.setPinned_post(GenericUtils.getJsonObject(jsonResponse).optString("pinned_post"));
                            caseOfTheDayLL.setVisibility(View.VISIBLE);
                            checkPostActivity(API.API_SET_POST_AS_PINNED);
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void networkCallForSubmitMcq(RadioButton radioBtn) {
        mProgress.show();
        SingleFeedviewApis apis = ApiClient.createService(SingleFeedviewApis.class);
        Call<JsonObject> response = apis.submitAnswerPostmcq(SharedPreference.getInstance().getLoggedInUser().getId(),
                currentFeed.getPost_data().getId(), String.valueOf(mcqItem));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            mProgress.dismiss();
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                JSONObject singledatarow = GenericUtils.getJsonObject(jsonResponse);
                                PostResponse response1 = null;
                                response1 = new Gson().fromJson(singledatarow.toString(), PostResponse.class);
                                if (activity instanceof FeedsActivity) {
                                    if (fragment instanceof FeedsFragment) {
                                        if (((FeedsFragment) fragment).feedRVAdapter != null)
                                            ((FeedsFragment) fragment).feedRVAdapter.itemChangeDatePostId(response1, 0);
                                    }
                                    if (fragment instanceof SavedNotesFragment) {
                                        if (((SavedNotesFragment) fragment).feedRVAdapter != null)
                                            ((SavedNotesFragment) fragment).feedRVAdapter.itemChangeDatePostId(response1, 0);
                                    }
                                } else if (activity instanceof ProfileActivity) {
                                    ((ProfileActivity) activity).feedRVAdapter.itemChangeDatePostId(response1, 0);
                                } else if (activity instanceof PostActivity) {
                                    if (fragment != null && fragment instanceof CommonFragment)
                                        ((CommonFragment) fragment).singleFeedView.setFeed(response1, 1);
                                }
                                radioBtn.setChecked(true);
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                checkPostActivity(API.API_SUBMIT_ANSWER_POST_MCQ);
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkPostActivity(String apiType) {
        if (activity instanceof PostActivity) {
            if (!apiType.equals(API.API_REPORT_POST) && !apiType.equals(API.API_DELETE_POST) && !apiType.equals(API.API_REQUEST_VIDEO_LINK)) {
                FeedsActivity.isCommentRefreshed = true;
                SharedPreference.getInstance().setPost(currentFeed);
            }
        }
    }

    public void setBookMark(int type) {// 0- Remove Bookmark and 1- Add Bookmark
        if (type == 1) {
            currentFeed.setIs_bookmarked(true);
            bookmarkIV.setImageResource(R.mipmap.bookmarked);
        } else if (type == 0) {
            currentFeed.setIs_bookmarked(false);
            bookmarkIV.setImageResource(R.mipmap.bookmark);
        }
    }

    @Override
    public void updateUIForDownloadedVideo(RequestInfo requestInfo, long id) {

        downloadIV.setVisibility(View.GONE);
        deleteIV.setVisibility(View.VISIBLE);
        downloadProgessPB.setVisibility(View.GONE);
        eyeIV.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(R.string.downloaded_offline);
        eMedicozDownloadManager.
                addOfflineDataIds(currentFeed.getId(), currentFeed.getPost_data().getPost_file().get(0).getLink(), activity,
                        false, true, Const.FEEDS, requestInfo.getId(), currentFeed.getId());
    }

    @Override
    public void updateProgressUI(Integer value, int status, long id) {

        messageTV.setVisibility(View.VISIBLE);
        downloadProgessPB.setVisibility(View.VISIBLE);
        if (status == Fetch.STATUS_QUEUED) {

            downloadIV.setVisibility(View.GONE);

            deleteIV.setVisibility(View.VISIBLE);
            eyeIV.setVisibility(View.GONE);

            messageTV.setText(R.string.download_queued);

        } else if (status == Fetch.STATUS_REMOVED) {

            downloadProgessPB.setProgress(0);
            downloadProgessPB.setVisibility(View.GONE);

            downloadIV.setVisibility(View.VISIBLE);
            downloadIV.setImageResource(R.mipmap.download_download);

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

        downloadProgessPB.setProgress(value);
    }

    @Override
    public void onStartEncoding() {
    }

    @Override
    public void onEncodingFinished() {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bottomCard:
                View v = (View) view.getTag(R.id.questions);
                Dialog reportDialog = (Dialog) view.getTag(R.id.optionsAns);
                if (!TextUtils.isEmpty(((EditText) v.findViewById(R.id.writefeedbackET)).getText().toString().trim())) {
                    reportText = ((EditText) v.findViewById(R.id.writefeedbackET)).getText().toString().trim();
                    reportPost();
                    reportDialog.dismiss();
                } else {
                    Toast.makeText(activity, "Please enter your feedback about this post", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.crossimageIV:
                Dialog reportDialog1 = (Dialog) view.getTag();
                reportDialog1.dismiss();
                break;
        }

    }

    public interface DeletePostCallback {
        void deletePost(PostResponse feed);
    }

}
