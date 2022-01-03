package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.adapter.ImageRVAdapter;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.modelo.PostMCQ;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.NewPostFragApis;
import com.emedicoz.app.utilso.AppPermissionRunTime;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.GetFilePath;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPostFragment extends Fragment implements View.OnClickListener, AmazonCallBack, TakeImageClass.ImageFromCropper {

    private static final String TAG = "NewPostFragment";
    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 123;
    public TextView tagTV;
    public String post_tag_id;
    ImageView addIV;
    ImageView removeIV;
    LinearLayout answerLayoutLL;
    FlowLayout taggedpeopleFL;
    LinearLayout mcqlayoutLL;
    LinearLayout addattachmentLL;
    LinearLayout addquestionLL;
    LinearLayout addmediaLL;
    LinearLayout addtagLL;
    LinearLayout addpeopleLL;
    EditText writepostET;
    TextView questioniconText;
    TextView nameTV;
    ImageView profileImageIV;
    ImageView profileImageIVText;
    Button postBtn;
    RecyclerView multiImageRV;
    ImageRVAdapter imageRVAdapter;
    ArrayList<MediaFile> imageArrayList;
    ArrayList<MediaFile> newimageArrayList; // for newtag images to be uploaded to S3 at time of editing the post
    s3ImageUploading s3ImageUploading;
    ArrayList<PostFile> fileArrayList;
    ArrayList<People> taggedpeoplearrList;
    String taggedPeopleIdsAdded;
    String taggedPeopleIdsRemoved;
    ArrayList<String> oldids;
    ArrayList<String> newids;
    Gson gson;
    String question;
    String answerOne;
    String answerTwo;
    String answerThree;
    String answerFour;
    String answerFive;
    String rightAnswer = "answer_one";
    Activity activity;
    boolean isQuestion = false;
    User user;
    int RC_TAKE_PHOTO = 12;
    File file;
    PostResponse post;
    List<View> linearLayoutList;
    List<View> taggedpeopleList;
    int mcqAnsCounter = 0;
    int isPostEdit = 0; // 0 -not edit, 1-mcq edit, 2-normal post edit
    String name;
    String profilePicture;
    String userId;
    Progress mProgress;
    ProgressBar progressBar;

    private ArrayList<AppPermissionRunTime.MyPermissionConstants> myPermissionConstantsArrayList;
    private int multiplePermissionCounter = 0;
    private int STORAGE_PERMISSION_TYPE = 0; // 1 for Images , 2 for Videos , 3 for Documents
    private int PERMISSION_TYPE = 0; // 1 for Location , 2 for Media
    private TakeImageClass takeImageClass;
    private long mLastClickTime = 0;

    public NewPostFragment() {
    }

    public static NewPostFragment newInstance(PostResponse post) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        user = SharedPreference.getInstance().getLoggedInUser();
        if (getArguments() != null) {
            post = (PostResponse) getArguments().getSerializable(Const.POST);
        }
        takeImageClass = new TakeImageClass(activity, this);
        /*{
            @Override
            public void openGallery() {
                if (imageArrayList.size() < 5) {
                    selectMultipleImages();
                } else {
                    Toast.makeText(activity, "You cannot add other Media", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void takePicture() {
                if (imageArrayList.size() < 5) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = new File(getActivity().getExternalCacheDir(),
                            String.valueOf(System.currentTimeMillis()) + ".jpg");
                    fileUri = GenericFileProvider.getUriForFile(activity, "com.emedicoz.app.utilsGenericFileProvider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getActivity().startActivityForResult(intent, RC_TAKE_PHOTO);
                } else {
                    Toast.makeText(activity, "You cannot add other Media", Toast.LENGTH_SHORT).show();
                }
            }
        };*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        fileArrayList = new ArrayList<>();
        gson = new Gson();

        progressBar = view.findViewById(R.id.newpostprogress);
        postBtn = view.findViewById(R.id.postBtn);
        addIV = view.findViewById(R.id.addIV);
        removeIV = view.findViewById(R.id.removeIV);
        profileImageIV = view.findViewById(R.id.profileImageIV);
        profileImageIVText = view.findViewById(R.id.profileImageIVText);

        writepostET = view.findViewById(R.id.writepostET);

        //Sushant
        writepostET.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {

                if (!((((PostActivity) activity).isImageAdded) && (((PostActivity) activity).isVideoAdded) && (((PostActivity) activity).isAttachmentAdded && isQuestion))) {
                    if (!(((PostActivity) activity).isYoutubeVideoAttached)) {
                        String str = mEdit.toString();

                        String x = Helper.youtubevalidation(str);
                        if (x != null) {
                            Log.e("VideoId is->", "" + x);
                            String imgUrl = "http://img.youtube.com/vi/" + x + "/0.jpg"; // this is link which will give u thumnail image of that video
                            setupYoutubeVideo(imgUrl);
                        } else {
                            if ((!str.equals(str.toUpperCase()) && str.length() == 1)) {
                                str = str.toUpperCase();
                                writepostET.setText(str);
                                writepostET.setSelection(str.length());
                            }
                        }
                    } else if (((PostActivity) activity).isYoutubeVideoAttached) {
                        imageArrayList = new ArrayList<>();
                        initImageAdapter();
                        String str = mEdit.toString();

                        String x = Helper.youtubevalidation(str);
                        if (x != null) {
                            Log.e("VideoId is->", "" + x);
                            String imgUrl = "http://img.youtube.com/vi/" + x + "/0.jpg"; // this is link which will give u thumnail image of that video
                            setupYoutubeVideo(imgUrl);
                        } else {
                            if ((!str.equals(str.toUpperCase()) && str.length() == 1)) {
                                str = str.toUpperCase();
                                writepostET.setText(str);
                                writepostET.setSelection(str.length());
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Media already attached", Toast.LENGTH_SHORT).show();
                }
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        multiImageRV = view.findViewById(R.id.multiImageRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        multiImageRV.setLayoutManager(linearLayoutManager);


        answerLayoutLL = view.findViewById(R.id.answerlayoutLL);
        taggedpeopleFL = view.findViewById(R.id.taggedpeopleFL);
        addattachmentLL = view.findViewById(R.id.attachLL);
        addquestionLL = view.findViewById(R.id.addquestionLL);
        mcqlayoutLL = view.findViewById(R.id.mcqlayout_LL);
        addmediaLL = view.findViewById(R.id.addimageLL);
        addtagLL = view.findViewById(R.id.addtagLL);
        addpeopleLL = view.findViewById(R.id.addusertagLL);

        questioniconText = addquestionLL.findViewById(R.id.questiontextTV);
        nameTV = view.findViewById(R.id.nameTV);
        tagTV = view.findViewById(R.id.tagTV);

        linearLayoutList = new ArrayList<>();
        taggedpeopleList = new ArrayList<>();

        postBtn.setOnClickListener(this);
        addattachmentLL.setOnClickListener(this);
        addquestionLL.setOnClickListener(this);
        addIV.setOnClickListener(this);
        removeIV.setOnClickListener(this);
        addmediaLL.setOnClickListener(this);
        addtagLL.setOnClickListener(this);
        addpeopleLL.setOnClickListener(this);

        progressBar.setScaleY(1f);
        progressBar.setVisibility(View.GONE);

        if (post != null) {
            userId = post.getPost_owner_info().getId();
            name = post.getPost_owner_info().getName();
            profilePicture = post.getPost_owner_info().getProfile_picture();
            editProfileInitViews();
        } else {
            userId = user.getId();
            name = user.getName();
            profilePicture = user.getProfile_picture();
        }

        name = Helper.CapitalizeText(name);
        if (!TextUtils.isEmpty(profilePicture)) {
            profileImageIV.setVisibility(View.VISIBLE);
            profileImageIVText.setVisibility(View.GONE);
            Glide.with(activity)
                    .asBitmap()
                    .load(profilePicture)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            profileImageIV.setImageBitmap(result);
                        }
                    });
        } else {
            Drawable dr = Helper.GetDrawable(name, activity, userId);
            if (dr != null) {
                profileImageIV.setVisibility(View.GONE);
                profileImageIVText.setVisibility(View.VISIBLE);
                profileImageIVText.setImageDrawable(dr);
            } else {
                profileImageIV.setVisibility(View.VISIBLE);
                profileImageIVText.setVisibility(View.GONE);
                profileImageIV.setImageResource(R.mipmap.default_pic);
            }
        }
        nameTV.setText(name);
    }

    public void editProfileInitViews() { // intialising the post which is there to edit

        if (TextUtils.isEmpty(post.getPost_tag())) {
            tagTV.setVisibility(View.GONE);
        } else {
            post_tag_id = post.getPost_tag_id();
            tagTV.setVisibility(View.VISIBLE);
            tagTV.setText(post.getPost_tag());
        }

        if (post.getPost_type().equals(Const.POST_TYPE_MCQ)) {
            isQuestion = true;
            isPostEdit = 1;
            mcqAnsCounter = 0;
            addattachmentLL.setVisibility(View.GONE);
            taggedpeopleFL.removeAllViews();
            addmediaLL.setVisibility(View.GONE);
            addquestionLL.setVisibility(View.GONE);
            answerLayoutLL.removeAllViews();
            mcqlayoutLL.setVisibility(View.VISIBLE);
            writepostET.setText("" + post.getPost_data().getQuestion());

            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_one())) {

                initMCQOptionView(++mcqAnsCounter);
                ((EditText) linearLayoutList.get(0).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_one());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_two())) {

                initMCQOptionView(++mcqAnsCounter);
                ((EditText) linearLayoutList.get(1).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_two());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_three())) {

                initMCQOptionView(++mcqAnsCounter);
                ((EditText) linearLayoutList.get(2).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_three());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_four())) {

                initMCQOptionView(++mcqAnsCounter);
                ((EditText) linearLayoutList.get(3).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_four());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_five())) {

                initMCQOptionView(++mcqAnsCounter);
                ((EditText) linearLayoutList.get(4).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_five());
            }

            addRemoveQuestionLayout(!isQuestion);

        } else if (post.getPost_type().equals(Const.POST_TYPE_NORMAL)) {
            addquestionLL.setVisibility(View.GONE);
            isPostEdit = 2;
            isQuestion = false;
            mcqlayoutLL.setVisibility(View.GONE);
            writepostET.setText(post.getPost_data().getText());
        }

        fileArrayList = new ArrayList<>();
        imageArrayList = new ArrayList<>();
        taggedpeoplearrList = new ArrayList<>();
        oldids = new ArrayList<>();

        if (!post.getTagged_people().isEmpty()) {
            if (oldids == null) oldids = new ArrayList<>();

            for (People ppl : post.getTagged_people()) {

                oldids.add(ppl.getId());

                addViewToTagPeople(ppl);
            }
        }
        if (!post.getPost_data().getPost_file().isEmpty()) {
            fileArrayList = post.getPost_data().getPost_file();
            switch (fileArrayList.get(0).getFile_type()) {
                case Const.IMAGE:
                    for (PostFile file : fileArrayList) {
                        MediaFile media = new MediaFile();
                        media.setId(file.getId());
                        media.setFile_name(file.getFile_info());
                        media.setFile_type(file.getFile_type());
                        media.setFile(file.getLink());
                        imageArrayList.add(media);
                    }
                    break;
                case Const.VIDEO:
                    MediaFile media = new MediaFile();
                    media.setId(fileArrayList.get(0).getId());
                    media.setFile_name(fileArrayList.get(0).getFile_info());
                    media.setFile(fileArrayList.get(0).getLink());
                    media.setFile_type(Const.VIDEO);
                    imageArrayList.add(media);

                    break;

                case Const.PDF:
                case Const.DOC:
                case Const.XLS: {
                    MediaFile docMedia = new MediaFile();
                    docMedia.setId(fileArrayList.get(0).getId());
                    docMedia.setFile_name(fileArrayList.get(0).getFile_info());
                    docMedia.setFile(fileArrayList.get(0).getLink());

                    if (docMedia.getFile().contains(getString(R.string.pdf_extension))) {
                        docMedia.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.pdf));
                        docMedia.setFile_type(Const.PDF);
                    } else if (docMedia.getFile().contains(getString(R.string.doc_extension))) {
                        docMedia.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.doc));
                        docMedia.setFile_type(Const.DOC);
                    } else if (docMedia.getFile().contains(getString(R.string.xls_extension))) {
                        docMedia.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.xls));
                        docMedia.setFile_type(Const.XLS);
                    }
                    imageArrayList.add(docMedia);
                    break;
                }
                default:
                    break;
            }
            initImageAdapter();
        }

    }

    public void initImageAdapter() {
        if (!imageArrayList.isEmpty()) {

            switch (imageArrayList.get(0).getFile_type()) {
                case Const.IMAGE:
                    ((PostActivity) activity).isImageAdded = true;
                    break;
                case Const.VIDEO:
                    ((PostActivity) activity).isVideoAdded = true;
                    break;
                case Const.PDF:
                case Const.DOC:
                case Const.XLS:
                    ((PostActivity) activity).isAttachmentAdded = true;
                    break;
                case Const.YOUTUBE_VIDEO:
                    ((PostActivity) activity).isYoutubeVideoAttached = true;
                    break;
                default:
                    break;
            }

            imageRVAdapter = new ImageRVAdapter(activity, imageArrayList);
            multiImageRV.setAdapter(imageRVAdapter);
            multiImageRV.setVisibility(View.VISIBLE);
        } else {
            ((PostActivity) activity).isImageAdded = false;
            ((PostActivity) activity).isVideoAdded = false;
            ((PostActivity) activity).isAttachmentAdded = false;
            ((PostActivity) activity).isYoutubeVideoAttached = false;
            imageRVAdapter = new ImageRVAdapter(activity, imageArrayList);
            multiImageRV.setAdapter(imageRVAdapter);
            multiImageRV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.attachLL:
                if (!((((PostActivity) activity).isVideoAdded || ((PostActivity) activity).isImageAdded || ((PostActivity) activity).isYoutubeVideoAttached)
                        && !imageArrayList.isEmpty())) {
                    imageArrayList = new ArrayList<>();
                    STORAGE_PERMISSION_TYPE = 3;
                    checkStoragePermission();
                } else
                    Toast.makeText(activity, R.string.you_cannot_add_more_media, Toast.LENGTH_SHORT).show();
                break;
            case R.id.addquestionLL:
                if (!(((PostActivity) activity).isImageAdded || ((PostActivity) activity).isVideoAdded || ((PostActivity) activity).isAttachmentAdded || ((PostActivity) activity).isYoutubeVideoAttached))
                    addRemoveQuestionLayout(isQuestion);
                else
                    Toast.makeText(activity, R.string.no_question_with_media, Toast.LENGTH_SHORT).show();
                break;
            case R.id.addIV:
                if (mcqAnsCounter < 5) {
                    mcqAnsCounter++;
                    initMCQOptionView(mcqAnsCounter);
                } else {
                    Toast.makeText(activity, R.string.you_cannot_add_more_opt, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.removeIV:
                if (mcqAnsCounter > 0) {
                    mcqAnsCounter--;
                    answerLayoutLL.removeViewAt(mcqAnsCounter);
                    linearLayoutList.remove(mcqAnsCounter);

                    if (mcqAnsCounter == 0) removeIV.setVisibility(View.GONE);
                    else removeIV.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(activity, R.string.no_more_opt_to_remov, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.postBtn:
                checkValidation();
                break;
            case R.id.addtagLL:
                TagSelectionFragment newFragment = (TagSelectionFragment) Fragment.instantiate(activity, TagSelectionFragment.class.getName());
                newFragment.show(((PostActivity) activity).getSupportFragmentManager(), "dialog");
                break;
            case R.id.addusertagLL:
                PeopleTagSelectionFragment newpeopletag = (PeopleTagSelectionFragment) Fragment.instantiate(activity, PeopleTagSelectionFragment.class.getName());
                Bundle bn = new Bundle();
                bn.putSerializable(Const.ALREADY_TAGGED_PEOPLE, taggedpeoplearrList);
                newpeopletag.setArguments(bn);
                newpeopletag.show(((PostActivity) activity).getSupportFragmentManager(), "dialog");
                break;
            case R.id.addimageLL:
                if (imageArrayList == null)
                    imageArrayList = new ArrayList<>();

                if (!((((PostActivity) activity).isVideoAdded || ((PostActivity) activity).isAttachmentAdded || ((PostActivity) activity).isYoutubeVideoAttached) && !imageArrayList.isEmpty()))
                    getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_media));
                else
                    Toast.makeText(activity, R.string.you_cannot_add_more_media, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void addRemoveQuestionLayout(boolean b) {
        if (b) {
            isQuestion = false;
            writepostET.setHint(getString(R.string.write_your_post_here));
            questioniconText.setText(getString(R.string.add_question));
            addmediaLL.setVisibility(View.VISIBLE);
            addattachmentLL.setVisibility(View.VISIBLE);
            mcqlayoutLL.setVisibility(View.GONE);
        } else {
            isQuestion = true;
            writepostET.setHint(getString(R.string.write_your_question_here));
            questioniconText.setText(getString(R.string.remove_survey));
            addmediaLL.setVisibility(View.GONE);
            addattachmentLL.setVisibility(View.GONE);
            mcqlayoutLL.setVisibility(View.VISIBLE);
        }

        addmediaLL.setEnabled(b);
        addattachmentLL.setEnabled(b);
        mcqlayoutLL.setVisibility((b ? View.GONE : View.VISIBLE));
    }

    public void initMCQOptionView(int i) {
        View v = View.inflate(activity, R.layout.single_row_option_layout, null);
        TextView tv = v.findViewById(R.id.optionTV);
        EditText editText = v.findViewById(R.id.answerET);
        tv = changeBackgroundColor(tv, 2);
        if (i == 1) tv.setText("A");
        else if (i == 2) tv.setText("B");
        else if (i == 3) tv.setText("C");
        else if (i == 4) tv.setText("D");
        else if (i == 5) tv.setText("E");
        v.setTag(tv.getText());
        Helper.CaptializeFirstLetter(editText);

        answerLayoutLL.addView(v);
        linearLayoutList.add(v);

        if (i == 1) removeIV.setVisibility(View.VISIBLE);
    }

    //this is to tag people in the post
    public void addViewToTagPeople(People response) {
        if (isPostEdit != 0) {
            if (newids == null) newids = new ArrayList<>();
            newids.add(response.getId());
        }
        if (taggedpeoplearrList == null) taggedpeoplearrList = new ArrayList<>();
        taggedpeoplearrList.add(response);

        View v = View.inflate(activity, R.layout.single_textview_people_tag, null);
        TextView tv = v.findViewById(R.id.nameTV);
        ImageView delete = v.findViewById(R.id.deleteIV);
        tv.setText(response.getName());
        v.setTag(response);
        delete.setTag(response);
        delete.setOnClickListener(view -> {
            People rep = (People) view.getTag();
            int pos = taggedpeoplearrList.indexOf(rep);
            taggedpeoplearrList.remove(rep);
            taggedpeopleFL.removeViewAt(pos);
            taggedpeopleList.remove(pos);
            if (isPostEdit != 0) {

                newids.remove(pos);
            }
        });

        taggedpeopleFL.addView(v);
        taggedpeopleList.add(v);
    }

    public TextView changeBackgroundColor(TextView v, int type) {

        v.setBackgroundResource(R.drawable.bg_imagebtn_transparent);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1) drawable.setColor(ContextCompat.getColor(activity, R.color.blue));
        else drawable.setColor(ContextCompat.getColor(activity, R.color.transparent));
        return v;
    }

    public void checkValidation() {

        int j = 0;
        j = linearLayoutList.size();
        question = Helper.GetText(writepostET);

        if (isQuestion) {
            if (j >= 1)
                answerOne = Helper.GetText(linearLayoutList.get(0).findViewById(R.id.answerET));
            if (j >= 2)
                answerTwo = Helper.GetText(linearLayoutList.get(1).findViewById(R.id.answerET));
            if (j >= 3)
                answerThree = Helper.GetText(linearLayoutList.get(2).findViewById(R.id.answerET));
            if (j >= 4)
                answerFour = Helper.GetText(linearLayoutList.get(3).findViewById(R.id.answerET));
            if (j >= 5)
                answerFive = Helper.GetText(linearLayoutList.get(4).findViewById(R.id.answerET));
        }
        boolean isDataValid = true;

        if (isQuestion) {
            if (TextUtils.isEmpty(question))
                isDataValid = Helper.DataNotValid(writepostET);
            else if ((j >= 1) && TextUtils.isEmpty(answerOne))
                isDataValid = Helper.DataNotValid(linearLayoutList.get(0).findViewById(R.id.answerET));
            else if ((j >= 2) && TextUtils.isEmpty(answerTwo))
                isDataValid = Helper.DataNotValid(linearLayoutList.get(1).findViewById(R.id.answerET));
            else if ((j >= 3) && TextUtils.isEmpty(answerThree))
                isDataValid = Helper.DataNotValid(linearLayoutList.get(2).findViewById(R.id.answerET));
            else if ((j >= 4) && TextUtils.isEmpty(answerFour))
                isDataValid = Helper.DataNotValid(linearLayoutList.get(3).findViewById(R.id.answerET));
            else if ((j >= 5) && TextUtils.isEmpty(answerFive))
                isDataValid = Helper.DataNotValid(linearLayoutList.get(4).findViewById(R.id.answerET));
            else if (j < 2) {
                Toast.makeText(activity, "Kindly provide atleast 2 options", Toast.LENGTH_LONG).show();
                isDataValid = false;
            }
        } else {
            if (!((((PostActivity) activity).isImageAdded || ((PostActivity) activity).isVideoAdded || ((PostActivity) activity).isAttachmentAdded) && !imageArrayList.isEmpty()) && TextUtils.isEmpty(question)) {
                isDataValid = Helper.DataNotValid(writepostET);
            }
        }
        if (isDataValid && TextUtils.isEmpty(post_tag_id)) {
            Toast.makeText(activity, "Kindly select the Tag", Toast.LENGTH_SHORT).show();
            isDataValid = false;
        }
        if (isDataValid) {
            if (isPostEdit != 0) {

                newimageArrayList = new ArrayList<>();
                if (imageArrayList == null)
                    imageArrayList = new ArrayList<>();

                for (MediaFile file : imageArrayList) {
                    if (TextUtils.isEmpty(file.getId())) {
                        newimageArrayList.add(file);
                    }
                }
                Log.e("newtag Post frag", "imageArrayList size total: " + imageArrayList.size());
                imageArrayList.clear();
                imageArrayList.addAll(newimageArrayList);
                Log.e("newtag Post frag", "newimageArrayList size newtag files: " + imageArrayList.size());
                if (imageArrayList.isEmpty()) {
                    imageArrayList = null;
                    fileArrayList.clear();
                }
            }
            if (isPostEdit == 0) {
                if (!GenericUtils.isListEmpty(taggedpeoplearrList)) {
                    for (People res : taggedpeoplearrList) {
                        if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                            taggedPeopleIdsAdded = res.getId();
                        else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + res.getId();
                    }
                }
            } else {
                if (!GenericUtils.isListEmpty(newids)) {
                    for (String str : newids) {
                        if (!oldids.contains(str)) {
                            if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                                taggedPeopleIdsAdded = str;
                            else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + str;
                        }
                    }
                }
                if (!GenericUtils.isListEmpty(oldids)) {
                    for (String str : oldids) {
                        if (!Objects.requireNonNull(newids).contains(str)) {
                            if (TextUtils.isEmpty(taggedPeopleIdsRemoved))
                                taggedPeopleIdsRemoved = str;
                            else taggedPeopleIdsRemoved = taggedPeopleIdsRemoved + "," + str;
                        }
                    }
                }

            }

            if (((PostActivity) activity).isYoutubeVideoAttached) {
                imageArrayList = null;
                fileArrayList.clear();
            }

            if (imageArrayList != null && !imageArrayList.isEmpty()) {
                if (imageArrayList.get(0).getFile_type().equals(Const.IMAGE)) {
                    s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_FANWALL_IMAGES, activity, this, progressBar);
                    s3ImageUploading.execute(imageArrayList);
                }
                if (imageArrayList.get(0).getFile_type().equals(Const.PDF) ||
                        imageArrayList.get(0).getFile_type().equals(Const.DOC)
                        || imageArrayList.get(0).getFile_type().equals(Const.XLS)) {
                    s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_DOCUMENT, activity, this, progressBar);
                    s3ImageUploading.execute(imageArrayList);

                } else if (imageArrayList.get(0).getFile_type().equals(Const.VIDEO)) {
                    s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_VIDEO, activity, this, progressBar);
                    s3ImageUploading.execute(imageArrayList);
                }

            } else if (isQuestion) {
                PostMCQ postMCQ = PostMCQ.newInstance();
                postMCQ.setUser_id((isPostEdit == 0 ? SharedPreference.getInstance().getLoggedInUser().getId() : post.getUser_id()));
                postMCQ.setQuestion(question);
                postMCQ.setAnswer_one(answerOne);
                postMCQ.setAnswer_two(answerTwo);
                postMCQ.setAnswer_three(answerThree);
                postMCQ.setAnswer_four(answerFour);
                postMCQ.setAnswer_five(answerFive);
                postMCQ.setRight_answer(rightAnswer);
                if (isPostEdit == 0)
                    networkCallForPostMcq();//NetworkAPICall(API.API_POST_MCQ, true);
                else if (isPostEdit == 1)
                    networkCallForEditPostMcq();//NetworkAPICall(API.API_EDIT_MCQ_POST, true);

            } else {
                if (isPostEdit == 0)
                    networkCallForPostVideo();//NetworkAPICall(API.API_POST_NORMAL_VIDEO, true);
                else if (isPostEdit == 2)
                    networkCallForEditNormalPost();//NetworkAPICall(API.API_EDIT_NORMAL_POST, true);

            }
        }

    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_POST_MCQ:
                networkCallForPostMcq();
                break;
            case API.API_EDIT_MCQ_POST:
                networkCallForEditPostMcq();
                break;
            case API.API_POST_NORMAL_VIDEO:
                networkCallForPostVideo();
                break;
            case API.API_EDIT_NORMAL_POST:
                networkCallForEditNormalPost();
                break;

            default:
                break;
        }
    }

    public void networkCallForPostMcq() {
        mProgress.show();
        PostMCQ postMCQ = PostMCQ.getInstance();

        NewPostFragApis apis = ApiClient.createService(NewPostFragApis.class);
        Call<JsonObject> response = apis.getPostMcq(postMCQ.getUser_id(),
                (TextUtils.isEmpty(postMCQ.getQuestion()) ? "" : postMCQ.getQuestion()),
                (TextUtils.isEmpty(postMCQ.getAnswer_one()) ? "" : postMCQ.getAnswer_one()),
                (TextUtils.isEmpty(postMCQ.getAnswer_two()) ? "" : postMCQ.getAnswer_two()),
                (TextUtils.isEmpty(postMCQ.getAnswer_three()) ? "" : postMCQ.getAnswer_three()),
                (TextUtils.isEmpty(postMCQ.getAnswer_four()) ? "" : postMCQ.getAnswer_four()),
                (TextUtils.isEmpty(postMCQ.getAnswer_five()) ? "" : postMCQ.getAnswer_five()),
                postMCQ.getRight_answer(), post_tag_id,
                (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded),
                gson.toJson(fileArrayList));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    fileArrayList = new ArrayList<>();
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        Log.e("New Post ", jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            FeedsActivity.isNewPostAdded = true;
                            imageArrayList = new ArrayList<>();
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_POST_MCQ);
                            activity.finish();

                        } else {
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_POST_MCQ);
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
                Helper.showErrorLayoutForNoNav(API.API_POST_MCQ, activity, 1, 1);

            }
        });

    }

    public void networkCallForEditPostMcq() {
        mProgress.show();

        PostMCQ postMCQ = PostMCQ.getInstance();
        NewPostFragApis apis = ApiClient.createService(NewPostFragApis.class);
        Call<JsonObject> response = apis.getEditPostMcq(postMCQ.getUser_id(), (post == null ? "" : post.getId()),
                (TextUtils.isEmpty(postMCQ.getQuestion()) ? "" : postMCQ.getQuestion()),
                (TextUtils.isEmpty(postMCQ.getAnswer_one()) ? "" : postMCQ.getAnswer_one()),
                (TextUtils.isEmpty(postMCQ.getAnswer_two()) ? "" : postMCQ.getAnswer_two()),
                (TextUtils.isEmpty(postMCQ.getAnswer_three()) ? "" : postMCQ.getAnswer_three()),
                (TextUtils.isEmpty(postMCQ.getAnswer_four()) ? "" : postMCQ.getAnswer_four()),
                (TextUtils.isEmpty(postMCQ.getAnswer_five()) ? "" : postMCQ.getAnswer_five()),
                postMCQ.getRight_answer(), post_tag_id,
                (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded),
                (TextUtils.isEmpty(taggedPeopleIdsRemoved) ? "" : taggedPeopleIdsRemoved), gson.toJson(fileArrayList),
                ((PostActivity) activity).deleted_meta_ids);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        fileArrayList = new ArrayList<>();

                        Log.e("Edit ", jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject singledatarow = GenericUtils.getJsonObject(jsonResponse);
                            PostResponse response1 = gson.fromJson(singledatarow.toString(), PostResponse.class);
                            setPostData(response1);
                            imageArrayList = new ArrayList<>();
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_EDIT_MCQ_POST);
                            activity.finish();

                        } else {
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_EDIT_MCQ_POST);
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
                Helper.showErrorLayoutForNoNav(API.API_EDIT_MCQ_POST, activity, 1, 1);
            }
        });
    }

    public void networkCallForPostVideo() {
        mProgress.show();
        NewPostFragApis apis = ApiClient.createService(NewPostFragApis.class);
        Call<JsonObject> response = apis.getPostNormalVideo(SharedPreference.getInstance().getLoggedInUser().getId(),
                question, (((PostActivity) activity).isYoutubeVideoAttached) ? Const.POST_TEXT_TYPE_YOUTUBE_TEXT : Const.POST_TEXT_TYPE_TEXT,
                post_tag_id, (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded), gson.toJson(fileArrayList));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    fileArrayList = new ArrayList<>();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        Log.e("New Post ", jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            FeedsActivity.isNewPostAdded = true;
                            imageArrayList = new ArrayList<>();
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_POST_NORMAL_VIDEO);
                            activity.finish();

                        } else {
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_POST_NORMAL_VIDEO);
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
                Helper.showErrorLayoutForNoNav(API.API_POST_NORMAL_VIDEO, activity, 1, 1);
            }
        });
    }

    public void networkCallForEditNormalPost() {
        mProgress.show();
        NewPostFragApis apis = ApiClient.createService(NewPostFragApis.class);
        Call<JsonObject> response = apis.getEditNormalPost(post.getUser_id(), (post == null ? "" : post.getId()),
                question, (((PostActivity) activity).isYoutubeVideoAttached) ? Const.POST_TEXT_TYPE_YOUTUBE_TEXT : Const.POST_TEXT_TYPE_TEXT,
                post_tag_id, (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded),
                (TextUtils.isEmpty(taggedPeopleIdsRemoved) ? "" : taggedPeopleIdsRemoved), gson.toJson(fileArrayList),
                ((PostActivity) activity).deleted_meta_ids);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        fileArrayList = new ArrayList<>();

                        Log.e("Edit ", jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            JSONObject singleDataRow = GenericUtils.getJsonObject(jsonResponse);
                            PostResponse response1 = gson.fromJson(singleDataRow.toString(), PostResponse.class);
                            setPostData(response1);
                            imageArrayList = new ArrayList<>();
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_EDIT_NORMAL_POST);
                            activity.finish();

                        } else {
                            errorCallback(jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), API.API_EDIT_NORMAL_POST);
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
                Helper.showErrorLayoutForNoNav(API.API_EDIT_NORMAL_POST, activity, 1, 1);
            }
        });
    }

    private void setPostData(PostResponse response1) {
        post.setLikes(response1.getLikes());
        post.setComments(response1.getComments());
        post.setPost_data(response1.getPost_data());
        post.setTagged_people(response1.getTagged_people());
        post.setPost_tag(response1.getPost_tag());
        post.setPost_tag_id(response1.getPost_tag_id());
        SharedPreference.getInstance().setPost(post);
        FeedsActivity.isPostUpdated = true;
    }


    public void errorCallback(String str, String apiType) {
        fileArrayList = new ArrayList<>();

        if (str.equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);
        else if (str.contains(getString(R.string.something_went_wrong_string)))
            Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
        else
            Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();

    }

    public void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Video"), Const.REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void getImagePickerDialog(final Activity ctx, final String title,
                                     final String message) {
        View v = Helper.newCustomDialog(ctx, true, title, message);

        Button btnCancel;
        Button btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(R.string.videos);
        btnSubmit.setText(R.string.images);

        btnCancel.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            if (!(((PostActivity) activity).isImageAdded && !imageArrayList.isEmpty())) {
                selectVideo();
            } else {
                Toast.makeText(activity, R.string.you_cannot_add_other_media, Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            takeImageClass.getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_image));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.REQUEST_TAKE_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            Log.d("", "images : " + Objects.requireNonNull(images).size());
            setupImages(images);

        } else if (requestCode == Const.REQUEST_TAKE_GALLERY_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedFileURI = data.getData();
            setupVideo(GetFilePath.getPath(activity, selectedFileURI));

        } else if (requestCode == Const.REQUEST_TAKE_GALLERY_DOC && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedFileURI = data.getData();
            setupDoc(selectedFileURI);
        } else if (requestCode == RC_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Image img = new Image(101, "camera", file.getPath(), false);
            ArrayList<Image> imageArrayList = new ArrayList<>();
            imageArrayList.add(img);
            setupImages(imageArrayList);
        } else if (takeImageClass != null)
            takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    public void setupImages(ArrayList<Image> images) {
        for (Image img : images) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setImage(Helper.decodeSampledBitmap(img.path, 200, 200));
            mediaFile.setFile_type(Const.IMAGE);
            imageArrayList.add(mediaFile);
        }
        initImageAdapter();
    }

    public void setupVideo(String videopath) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setImage(ThumbnailUtils.createVideoThumbnail(videopath, MediaStore.Video.Thumbnails.MICRO_KIND));
        mediaFile.setFile_type(Const.VIDEO);
        mediaFile.setFile(videopath);
        imageArrayList.add(mediaFile);
        initImageAdapter();
    }

    //Sushant
    public void setupYoutubeVideo(String videopath) {
        final MediaFile mediaFile = new MediaFile();
        mediaFile.setFile_type(Const.YOUTUBE_VIDEO);
        mediaFile.setFile(videopath);
        imageArrayList = new ArrayList<>();
        imageArrayList.add(mediaFile);
        initImageAdapter();
    }

    public void setupDoc(Uri selectedURI) {
        MediaFile mediaFile = new MediaFile();
        String docPath = GetFilePath.getPath(activity, selectedURI);

        if (docPath != null) {

            if (!TextUtils.isEmpty(docPath) && !(docPath.contains(getString(R.string.pdf_extension)) || docPath.contains(getString(R.string.doc_extension)) || docPath.contains(getString(R.string.xls_extension))))
                Toast.makeText(activity, R.string.file_format_error, Toast.LENGTH_SHORT).show();
            else {
                if (docPath.contains(getString(R.string.pdf_extension))) {
                    mediaFile.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.pdf));
                    mediaFile.setFile_type(Const.PDF);
                } else if (docPath.contains(getString(R.string.doc_extension))) {
                    mediaFile.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.doc));
                    mediaFile.setFile_type(Const.DOC);
                } else if (docPath.contains(getString(R.string.xls_extension))) {
                    mediaFile.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.xls));
                    mediaFile.setFile_type(Const.XLS);
                }
                String[] arr = docPath.split("/");
                mediaFile.setFile_name(arr[arr.length - 1]);
                mediaFile.setFile(docPath);
                imageArrayList.add(mediaFile);
                initImageAdapter();
            }
        } else {
            Toast.makeText(activity, R.string.file_format_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {
        if (!images.isEmpty()) {
            fileArrayList = new ArrayList<>();

            if (images.get(0).getFile_type().equals(Const.IMAGE)) {
                for (MediaFile str : images) {
                    PostFile file = new PostFile();
                    file.setFile_info(str.getFile_name());
                    file.setFile_type(str.getFile_type());
                    file.setLink(str.getFile());
                    fileArrayList.add(file);
                }
            } else if (images.get(0).getFile_type().equals(Const.VIDEO)) {
                PostFile pf = new PostFile();
                pf.setFile_info(images.get(0).getFile_name() + ".mp4");
                pf.setLink(pf.getFile_info());
                pf.setFile_type(Const.VIDEO);
                fileArrayList.add(pf);

            } else if (images.get(0).getFile_type().equals(Const.PDF) ||
                    images.get(0).getFile_type().equals(Const.DOC) ||
                    images.get(0).getFile_type().equals(Const.XLS)) {
                PostFile pf = new PostFile();
                pf.setFile_info(images.get(0).getFile_name());
                pf.setLink(images.get(0).getFile());
                pf.setFile_type(images.get(0).getFile_type());
                fileArrayList.add(pf);
            }

            if (isQuestion) {
                PostMCQ postMCQ = PostMCQ.newInstance();
                postMCQ.setUser_id((isPostEdit == 0 ? SharedPreference.getInstance().getLoggedInUser().getId() : post.getUser_id()));
                postMCQ.setQuestion(question);
                postMCQ.setAnswer_one(answerOne);
                postMCQ.setAnswer_two(answerTwo);
                postMCQ.setAnswer_three(answerThree);
                postMCQ.setAnswer_four(answerFour);
                postMCQ.setAnswer_five(answerFive);
                postMCQ.setRight_answer(rightAnswer);

                if (isPostEdit == 0)
                    networkCallForPostMcq();//NetworkAPICall(API.API_POST_MCQ, true);
                else if (isPostEdit == 1)
                    networkCallForEditPostMcq();//NetworkAPICall(API.API_EDIT_MCQ_POST, true);

            } else {
                if (isPostEdit == 0)
                    networkCallForPostVideo();//NetworkAPICall(API.API_POST_NORMAL_VIDEO, true);
                else if (isPostEdit == 2)
                    networkCallForEditNormalPost();//NetworkAPICall(API.API_EDIT_NORMAL_POST, true);

            }

        }

    }

    @Override
    public void imagePath(String str) {
        Log.e(TAG, "imagePath: " + str);
        Image img = new Image(101, "camera", str, false);
        ArrayList<Image> imageArrayList = new ArrayList<>();
        imageArrayList.add(img);
        setupImages(imageArrayList);
    }


    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            myPermissionConstantsArrayList = new ArrayList<>();
            PERMISSION_TYPE = 2;

            myPermissionConstantsArrayList.add(AppPermissionRunTime.MyPermissionConstants.PERMISSION_READ_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(AppPermissionRunTime.MyPermissionConstants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            if (STORAGE_PERMISSION_TYPE == 1 || STORAGE_PERMISSION_TYPE == 2)
                myPermissionConstantsArrayList.add(AppPermissionRunTime.MyPermissionConstants.PERMISSION_CAMERA);
            if (AppPermissionRunTime.checkPermission(activity, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {
                openChooser();
            }
        } else {
            // Pre-Marshmallow
            openChooser();
        }
    }

    private void openChooser() {
        if (STORAGE_PERMISSION_TYPE == 1) {         // for Images
            takeImageClass.getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_image));
        } else if (STORAGE_PERMISSION_TYPE == 2) {     // for Videos
            selectVideo();
        } else if (STORAGE_PERMISSION_TYPE == 3) {     // for Documents
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), Const.REQUEST_TAKE_GALLERY_DOC);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean isDenine = false;
        if (requestCode == REQUEST_CODE_PERMISSION_MULTIPLE) {
            int i = 0;
            for (; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    multiplePermissionCounter++;
                    isDenine = true;
                    break;
                }
            }
            if (isDenine) {
                if (multiplePermissionCounter >= 2) {
                    Helper.aDialogOnPermissionDenied(activity);
                } else {
                    AppPermissionRunTime.checkPermission(activity, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE);
                }
            } else {
                if (PERMISSION_TYPE == 1) {

                } else if (PERMISSION_TYPE == 2)
                    openChooser();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
