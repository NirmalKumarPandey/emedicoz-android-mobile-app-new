package com.emedicoz.app.feeds.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.feeds.fragment.ChangeStreamFragment;
import com.emedicoz.app.feeds.fragment.CommonFragment;
import com.emedicoz.app.feeds.fragment.FollowAnExpertFragment;
import com.emedicoz.app.feeds.fragment.NewPostFragment;
import com.emedicoz.app.feeds.fragment.PeopleTagSelectionFragment;
import com.emedicoz.app.feeds.fragment.RegistrationFragment;
import com.emedicoz.app.feeds.fragment.TagSelectionFragment;
import com.emedicoz.app.feeds.fragment.YouTubeFragment;
import com.emedicoz.app.modelo.Comment;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.podcast.AddPodcastFragment;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;

import java.util.ArrayList;

public class PostActivity extends BaseABNoNavActivity implements TagSelectionFragment.ITagSelectionListener, PeopleTagSelectionFragment.IPeopleSelectionListener {

    public boolean isImageAdded = false;
    public boolean isVideoAdded = false;
    public boolean isYoutubeVideoAttached = false;
    public boolean isAttachmentAdded = false;
    public boolean isUserEditingComment = false;
    public boolean isUpdateProfile = false;
    public int followExpertCounter = SharedPreference.getInstance().getLoggedInUser().getExpert_following();
    public String deleted_meta_ids; // this is for all the media which has been removed in the edit post
    public int commonPeopleType = 0; // 0 for Like List & 1 for ViewAll of people you may know on Common Fragment.
    public ArrayList<People> peopleArrayList;
    public ArrayList<People> followResponseArrayList;
    String fragType, postId, regType, youtubeId, commentId;
    PostResponse post;
    String name;
    Comment comment;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("PostActivity", "destroyed");
    }

    @Override
    protected void initViews() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (getIntent() != null && getIntent().getExtras() != null) {
            post = null;
            fragType = getIntent().getExtras().getString(Const.FRAG_TYPE);
            isUpdateProfile = getIntent().getExtras().getBoolean(Constants.Extras.UPDATE_PROFILE);
            postId = getIntent().getExtras().getString(Const.POST_ID);
            name = getIntent().getExtras().getString(Constants.Extras.NAME);
            post = (PostResponse) getIntent().getExtras().getSerializable(Const.POST);
            regType = getIntent().getExtras().getString(Constants.Extras.TYPE);
            youtubeId = getIntent().getExtras().getString(Const.YOUTUBE_ID);
            peopleArrayList = (ArrayList<People>) getIntent().getExtras().getSerializable(Const.PEOPLE_LIST_COMMONS);
            commonPeopleType = getIntent().getExtras().getInt(Const.COMMON_PEOPLE_TYPE);
            commentId = getIntent().getExtras().getString(Const.COMMENT_ID);
            comment = (Comment) getIntent().getSerializableExtra(Const.COMMENT);
        }
        followResponseArrayList = new ArrayList<>();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // hideSystemUi();
        View videoLayout = findViewById(R.id.layout_video);
        Log.e("Config Changed", newConfig.orientation + "");// 1 = Portrait , 2 = LandScape
        if (newConfig.orientation == 2) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (videoLayout != null) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                        getResources().getDisplayMetrics().heightPixels);
                videoLayout.setLayoutParams(layoutParams);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (videoLayout != null) {
                final float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (200 * scale + 0.5f);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);
                videoLayout.setLayoutParams(layoutParams);
            }
        }
        //updateMarqueeTextViewPosition(newConfig.orientation);
    }

    @Override
    protected Fragment getFragment() {
        switch (fragType) {
            case Const.POST_FRAG:
                setToolbarTitle(getString(R.string.new_post));
                return NewPostFragment.newInstance(post);
//            case Const.CPR_INVOICE:
//                setToolbarTitle(getString(R.string.CPR_invoice));
//                return CprInvoice.newInstance();
            case Const.REWARD_TRANSACTION_FRAGMENT:
                setToolbarTitle(getString(R.string.reward_transaction));
                return CommonFragment.newInstance(fragType);
            case Const.COMMENT:
                if (!TextUtils.isEmpty(commentId))
                    return CommonFragment.newInstance(post, fragType, commentId, null);
                else
                    return CommonFragment.newInstance(post, fragType, postId);
            case Const.COMMENT_LIST:
                if (!TextUtils.isEmpty(commentId))
                    return CommonFragment.newInstance(post, fragType, commentId, postId, name, comment);
            case Const.NOTIFICATION:
                setToolbarTitle(getString(R.string.notifications));
                return CommonFragment.newInstance(post, fragType, postId);
            case Const.REGISTRATION:
                setToolbarTitle(getString(R.string.profile));
                return RegistrationFragment.newInstance(regType, isUpdateProfile);
            case Const.CHANGE_STREAM:
                setToolbarTitle(getString(R.string.change_stream));
                return ChangeStreamFragment.newInstance(regType);
            case Const.ADD_PODCAST:
                setToolbarTitle(getString(R.string.add_podcast));
                return AddPodcastFragment.newInstance();
            case Const.YOUTUBE:
                return YouTubeFragment.newInstance(youtubeId);
            case Const.COMMON_PEOPLE_LIST:
                setToolbarTitle(getString(R.string.likes));
                return CommonFragment.newInstance(post, fragType, postId);
            case Const.COMMON_PEOPLE_VIEWALL:
                setToolbarTitle(getString(R.string.people_you_may_know));
                return CommonFragment.newInstance(fragType, commonPeopleType, peopleArrayList);
            case Const.COMMON_EXPERT_PEOPLE_VIEWALL:
                setToolbarTitle(getString(R.string.meet_the_expert));
                return CommonFragment.newInstance(fragType, commonPeopleType, peopleArrayList);
            case Const.FOLLOW_THE_EXPERT_FIRST:
                toolbar.setVisibility(View.GONE);
                return FollowAnExpertFragment.newInstance();
        }
        return null;
    }

    @Override
    public void onTagSelected(Tags tag) {
        if (mFragment instanceof NewPostFragment) {
            ((NewPostFragment) mFragment).tagTV.setVisibility(View.VISIBLE);
            ((NewPostFragment) mFragment).tagTV.setText(tag.getText());
            ((NewPostFragment) mFragment).post_tag_id = tag.getId();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (fragType.equals(Const.NOTIFICATION)) {
            getMenuInflater().inflate(R.menu.notification_menu, menu);
            MenuItem item = menu.getItem(0);
            SpannableString spanString = new SpannableString(menu.getItem(0).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
            return true;
        } else
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.readallIM:
                // Not implemented here
                return false;
            default:
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mFragment instanceof NewPostFragment) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        } else if (this.mFragment instanceof RegistrationFragment) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        } else if (this.mFragment instanceof CommonFragment) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        } else if (this.mFragment instanceof AddPodcastFragment) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void addDeletedIdToMeta(String id) {
        if (TextUtils.isEmpty(deleted_meta_ids)) deleted_meta_ids = id;
        else deleted_meta_ids = deleted_meta_ids + "," + id;
    }

    @Override
    public void onPeopleTagSelected(People response) {
        if (mFragment instanceof NewPostFragment)
            ((NewPostFragment) mFragment).addViewToTagPeople(response);
        else if (mFragment instanceof CommonFragment && isUserEditingComment)
            ((CommonFragment) mFragment).commentRVAdapter.holder.addViewToEditTagPeople(response);
        else if (mFragment instanceof CommonFragment)
            ((CommonFragment) mFragment).addViewToTagPeople(response);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onBackPressed() {
        customBackPress();
    }

    public void customBackPress() {
        eMedicozApp.getInstance().playerState = null;
        if (mFragment instanceof RegistrationFragment && !((RegistrationFragment) mFragment).isUpdateProfile
                && SharedPreference.getInstance().getMasterHitResponse() != null) {
            User user = SharedPreference.getInstance().getMasterHitResponse().getUser_detail();
            if (user.getCountry() == null || user.getState() == null || user.getCity() == null || user.getCollege() == null) {
                Toast.makeText(this, R.string.pl_clg_information, Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
