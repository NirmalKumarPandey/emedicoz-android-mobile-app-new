package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Cards;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse;
import com.emedicoz.app.modelo.liveclass.courses.LiveClassCourseList;
import com.emedicoz.app.modelo.liveclass.courses.LiveClassCourseResponse;
import com.emedicoz.app.modelo.liveclass.courses.LiveVideoResponse;
import com.emedicoz.app.modelo.liveclass.courses.NotesTestEpubResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import static com.emedicoz.app.utilso.Helper.getCurrencySymbol;

public class SingleStudyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public String contentType;
    Activity activity;
    SingleStudyModel singleStudy;
    ArrayList<Cards> tiles = new ArrayList<>();
    DescriptionResponse descriptionResponse;
    NotesTestEpubResponse notesTestEpubResponse;
    LiveVideoResponse liveVideoResponse;
    onButtonClicked buttonClicked;
    ArrayList<View> viewArrayList = new ArrayList<>();
    String is_purchased;
    private String imageFooter;
    private String selectedTab;
    LiveClassCourseResponse liveClassCourseResponse;

    public SingleStudyAdapter(Activity activity, DescriptionResponse descriptionResponse, LiveVideoResponse liveVideoResponse, NotesTestEpubResponse notesTestEpubResponse, onButtonClicked buttonClicked, String isPurchased, String selectedTab) {
        this.descriptionResponse = descriptionResponse;
        this.activity = activity;
        this.notesTestEpubResponse = notesTestEpubResponse;
        this.liveVideoResponse = liveVideoResponse;
        this.buttonClicked = buttonClicked;
        this.is_purchased = is_purchased;
        this.selectedTab = selectedTab;
    }

    public SingleStudyAdapter(Activity activity, LiveClassCourseResponse liveClassCourseResponse, DescriptionResponse descriptionResponse, onButtonClicked buttonClicked, String selectedTab) {
        this.activity = activity;
        this.liveClassCourseResponse = liveClassCourseResponse;
        this.descriptionResponse = descriptionResponse;
        this.buttonClicked = buttonClicked;
        this.selectedTab = selectedTab;
    }

    public void udateData(LiveVideoResponse liveVideoResponse) {
        this.liveVideoResponse = liveVideoResponse;
        this.viewArrayList.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(activity).inflate(R.layout.exam_prep_header, null);
            return new SingleStudyHeaderHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(activity).inflate(R.layout.exam_prep_single_row_item, null);
            return new SingleStudyListHolder(view);
        } else if (viewType == 2) {
            view = LayoutInflater.from(activity).inflate(R.layout.item_webview, null);
            return new StudyWebViewHolder(view);
        } else if (viewType == 4) {
            view = LayoutInflater.from(activity).inflate(R.layout.layout_for_expand_list, null);
            return new StudyExpandListViewHolder(view);
        } else if (viewType == 5) {
            view = LayoutInflater.from(activity).inflate(R.layout.single_item_course, null);
            return new StudyCourseViewHolder(view);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.item_overview, null);
            return new StudyOverviewViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ((SingleStudyHeaderHolder) holder).setData(descriptionResponse, selectedTab);
        } else if (getItemViewType(position) == 1) {
            ((SingleStudyListHolder) holder).setData();
        } else if (getItemViewType(position) == 2) {

            ((StudyWebViewHolder) holder).setData(descriptionResponse);
        } else if (getItemViewType(position) == 4) {
            ((StudyExpandListViewHolder) holder).setData();

        } else if (getItemViewType(position) == 3) {
            ((StudyOverviewViewHolder) holder).setData(descriptionResponse);
        } else if (getItemViewType(position) == 5) {
            ((StudyCourseViewHolder) holder).setData(liveClassCourseResponse);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;  // HEADER
        else if (contentType.equalsIgnoreCase("Book")) return 2;
        else if (contentType.equalsIgnoreCase("overview")) return 3;//WebView
        else if (contentType.equalsIgnoreCase("video")) return 4;
        else if (contentType.equalsIgnoreCase(Const.COURSE)) return 5;
        else return 1;  // ANY THING ELSE
    }

    @Override
    public int getItemCount() {
        if (contentType.equalsIgnoreCase("Book"))
            return 2;
        else if (contentType.equalsIgnoreCase("overview"))
            return 2;
        else if (contentType.equalsIgnoreCase("video"))
            return 2;
        else if (contentType.equalsIgnoreCase("Test data"))
            return 2;
        else if (contentType.equalsIgnoreCase(Const.COURSE))
            return liveClassCourseResponse.getData().getList().size() + 1;
        else
            return liveVideoResponse.getData().getList().size() + 1;
    }


    public void updateimageFooter(String imagLinkFooter) {
        imageFooter = imagLinkFooter;
    }


    private void setListViewHeight(ExpandableListView listView) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupView = listAdapter.getGroupView(i, true, null, listView);
            groupView.measure(0, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupView.getMeasuredHeight();

            if (listView.isGroupExpanded(i)) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, listView);
                    listItem.measure(0, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (i == group/*((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))*/) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        totalHeight += 100;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class SingleStudyHeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout coursePanelLL;
        TextView leftTextTV, rightTextTV, titleTV;
        ImageView courseIV;
        RelativeLayout imageRL;
        TextView videoLayout, notesTestLayout, discriptionLayout, coursesLayout;

        public SingleStudyHeaderHolder(View itemView) {
            super(itemView);

            coursePanelLL = itemView.findViewById(R.id.coursePanelLL);
            imageRL = itemView.findViewById(R.id.imageRL);
            leftTextTV = itemView.findViewById(R.id.videosCount);
            rightTextTV = itemView.findViewById(R.id.testCount);
            titleTV = itemView.findViewById(R.id.upperTitle);
            courseIV = itemView.findViewById(R.id.courseImageIcon);
            videoLayout = itemView.findViewById(R.id.live_class_videos);
            notesTestLayout = itemView.findViewById(R.id.notes_test);
            discriptionLayout = itemView.findViewById(R.id.discription_layout);
            coursesLayout = itemView.findViewById(R.id.coursesLayout);
        }

        public void setData(DescriptionResponse descriptionResponse, String selectedTab) {
//            if (selectedTab == null)
//                selectedTab = Const.VIDEO;
            titleTV.setText(descriptionResponse.getData().getBasic().getTitle());
            updateTileUI(GenericUtils.isEmpty(selectedTab) ?
                    getFirstTab(descriptionResponse.getData().getTiles().get(0)) : selectedTab);

            if (!TextUtils.isEmpty(imageFooter)) {
                Ion.with(activity)
                        .load(imageFooter)
                        .withBitmap()
                        .placeholder(R.mipmap.courses_blue)
                        .error(R.mipmap.courses_blue)
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (e == null && result != null) courseIV.setImageBitmap(result);
                                else courseIV.setImageResource(R.mipmap.courses_blue);
                            }
                        });
            } else {
                courseIV.setImageResource(R.mipmap.courses_blue);
            }

            // coursePanelLL.removeAllViews();
            tiles = new ArrayList<>();
            Cards tile = new Cards();
            tile.setTile_name("Overview");
            tile.setType("overview");
            tiles.add(tile);
            tiles.addAll(descriptionResponse.getData().getTiles());

            for (Cards cards : tiles) {
                if (cards.getType().equalsIgnoreCase("video")) {
                    videoLayout.setVisibility(View.VISIBLE);
                } else if (cards.getType().equalsIgnoreCase(Constants.TestType.TEST) || cards.getType().equalsIgnoreCase("pdf") || cards.getType().equalsIgnoreCase("epub")) {
                    notesTestLayout.setVisibility(View.VISIBLE);
                } else if (cards.getType().equalsIgnoreCase("course")) {
                    coursesLayout.setVisibility(View.VISIBLE);
                } else if (cards.getType().equalsIgnoreCase("overview")) {
                    discriptionLayout.setVisibility(View.VISIBLE);
                }
            }
            videoLayout.setOnClickListener(this);
            notesTestLayout.setOnClickListener(this);
            coursesLayout.setOnClickListener(this);
            discriptionLayout.setOnClickListener(this);

        }

        private String getFirstTab(Cards cards) {
            if (cards.getType().equalsIgnoreCase(Constants.TestType.TEST) || cards.getType().equalsIgnoreCase("pdf") || cards.getType().equalsIgnoreCase("epub")) {
                return Const.TEST_EPUB_PDF;
            } else if (cards.getType().equalsIgnoreCase("course")) {
                return Const.COURSE;
            } else if (cards.getType().equalsIgnoreCase("overview")) {
                return "overview";
            }
            return Const.VIDEO;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.live_class_videos:
                    buttonClicked.onTitleClicked(Const.VIDEO);
                    break;
                case R.id.notes_test:
                    buttonClicked.onTitleClicked(Const.TEST_EPUB_PDF);
                    break;
                case R.id.coursesLayout:
                    buttonClicked.onTitleClicked(Const.COURSE);
                    break;
                case R.id.discription_layout:
                    buttonClicked.onTitleClicked("overview");
                    break;
                default:
            }
        }

        private void updateTileUI(String selectedTab) {
            switch (selectedTab) {
                case Const.VIDEO:

                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    videoLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    notesTestLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    discriptionLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    coursesLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));

                    break;
                case Const.TEST_EPUB_PDF:

                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    notesTestLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    videoLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    discriptionLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    coursesLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    break;
                case Const.COURSE:

                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    coursesLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    videoLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    discriptionLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    notesTestLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));

                    break;
                case "overview":

                    discriptionLayout.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    discriptionLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_grid_list));
                    videoLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    videoLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    notesTestLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    notesTestLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    coursesLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    coursesLayout.setTextColor(ContextCompat.getColor(activity, R.color.white));

                    break;
            }
        }

    }

    public class StudyExpandListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView noDataTV;
        RecyclerView videoThumbNailRV;
        ImageView thumbnailUpArrowIV, thumbnailDownArrowIV;
        ExpandableListView expandableListView;
        ThumbnailVideoAdapter thumbnailVideoAdapter;
        ExpandableListAdapter listAdapter;

        public StudyExpandListViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbNailRV = itemView.findViewById(R.id.videoThumbNailRV);
            thumbnailUpArrowIV = itemView.findViewById(R.id.thumbnailUpArrowIV);
            thumbnailDownArrowIV = itemView.findViewById(R.id.thumbnailDownArrowIV);
            expandableListView = itemView.findViewById(R.id.videoExpandList);
            noDataTV = itemView.findViewById(R.id.noDataTV);
            thumbnailUpArrowIV.setOnClickListener(this);
            thumbnailDownArrowIV.setOnClickListener(this);

        }

        public void setThumbnailData(String THUMB_IMAGE) {

            if (liveVideoResponse != null && !GenericUtils.isListEmpty(liveVideoResponse.getData().getUpcomingVideos())) {
                thumbnailUpArrowIV.setVisibility(View.VISIBLE);
                videoThumbNailRV.setVisibility(View.VISIBLE);

                videoThumbNailRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false));
                thumbnailVideoAdapter = new ThumbnailVideoAdapter(activity, liveVideoResponse.getData().getUpcomingVideos(), "OPEN");
                videoThumbNailRV.setAdapter(thumbnailVideoAdapter);
            } else {
                thumbnailUpArrowIV.setVisibility(View.GONE);
                videoThumbNailRV.setVisibility(View.GONE);
            }
        }

        public void setData() {
            setThumbnailData("OPEN");
            setExpandVideoList();
        }

        public void setExpandVideoList() {

            if (liveVideoResponse != null && !GenericUtils.isListEmpty(liveVideoResponse.getData().getList())) {
                noDataTV.setVisibility(View.GONE);

                listAdapter = new ExpandableListViewAdapter(activity, liveVideoResponse.getData().getList(), descriptionResponse, liveVideoResponse);

                // setting list adapter
                expandableListView.setAdapter(listAdapter);
                setListViewHeight(expandableListView);
                expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
                    setListViewHeight(parent, groupPosition);
                    return false;
                });
                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup) {
                            expandableListView.collapseGroup(previousGroup);
                            previousGroup = groupPosition;
                        }
                    }
                });
            } else
                noDataTV.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.thumbnailUpArrowIV:
                    thumbnailDownArrowIV.setVisibility(View.VISIBLE);
                    thumbnailUpArrowIV.setVisibility(View.GONE);
                    videoThumbNailRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false));
                    thumbnailVideoAdapter = new ThumbnailVideoAdapter(activity, liveVideoResponse.getData().getUpcomingVideos(), "CLOSE");
                    videoThumbNailRV.setAdapter(thumbnailVideoAdapter);
                    break;
                case R.id.thumbnailDownArrowIV:
                    thumbnailUpArrowIV.setVisibility(View.VISIBLE);
                    thumbnailDownArrowIV.setVisibility(View.GONE);
                    videoThumbNailRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false));
                    thumbnailVideoAdapter = new ThumbnailVideoAdapter(activity, liveVideoResponse.getData().getUpcomingVideos(), "OPEN");
                    videoThumbNailRV.setAdapter(thumbnailVideoAdapter);
                    break;
                default:
            }

        }
    }

    public class StudyCourseViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        TextView priceTV;
        TextView ratingTV;
        TextView learnerTV;
        TextView validityTV;
        RatingBar ratingRB;
        LinearLayout parentLL;

        public StudyCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.nameTV);
            parentLL = itemView.findViewById(R.id.parentLL);
            priceTV = itemView.findViewById(R.id.priceTV);
            ratingTV = itemView.findViewById(R.id.ratingTV);
            learnerTV = itemView.findViewById(R.id.learnerTV);
            ratingRB = itemView.findViewById(R.id.ratingRB);
            validityTV = itemView.findViewById(R.id.validityTv);
        }


        public void setData(LiveClassCourseResponse liveClassCourseResponse) {
            titleTV.setText(liveClassCourseResponse.getData().getList().get(getAdapterPosition() - 1).getTitle());
            Course course = getCourseData(liveClassCourseResponse.getData().getList().get(getAdapterPosition() - 1));
            learnerTV.setText((course.getLearner() + " "
                    + ((course.getLearner().equals("1") || (course.getLearner().equals("0")) ? Const.LEARNER : Const.LEARNERS))));


            if (course.getMrp().equals("0")) {
                priceTV.setText("Free");
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (course.getFor_dams().equals(course.getMrp())) {
                        priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                    } else {
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(course.getFor_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) priceTV.getText();
                        spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (course.getFor_dams().equalsIgnoreCase("0")) {
                        priceTV.setText("Free");
                    }
                } else {
                    if (course.getNon_dams().equals(course.getMrp())) {
                        priceTV.setText(String.format("%s %s", getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                    } else {
                        StrikethroughSpan strikeThroughSpan = new StrikethroughSpan();
                        priceTV.setText(getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(course.getNon_dams()), TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) priceTV.getText();
                        spannable.setSpan(strikeThroughSpan, 2, Helper.calculatePriceBasedOnCurrency(course.getMrp()).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    if (course.getNon_dams().equalsIgnoreCase("0")) {
                        priceTV.setText("Free");
                    }
                }
            }

            parentLL.setOnClickListener(v -> {
                Intent courseList = new Intent(activity, CourseActivity.class); // FRAG_TYPE, Const.SINGLE_COURSE from CourseListAdapter
                if (Helper.isStringValid(course.getIs_combo()) && course.getIs_combo().equalsIgnoreCase("1"))
                    courseList.putExtra(Const.FRAG_TYPE, Const.COMBO_COURSE);
                else if (Helper.isStringValid(course.getCourse_type()) && (course.getCourse_type().equalsIgnoreCase("2")
                        || course.getCourse_type().equalsIgnoreCase("3"))) {
                    if (course.getId().equalsIgnoreCase(Constants.Extras.QUESTION_BANK_COURSE_ID)) {
                        courseList.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE);
                    } else {
                        courseList.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
                    }
                } else {
                    if (course.getIs_live() != null) {
                        if (course.getIs_live().equalsIgnoreCase("1")) {
                            SharedPreference.getInstance().putString(Constants.Extras.ID, course.getId());
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_STUDY);
                            courseList.putExtra(Const.IMAGE, course.getDesc_header_image());
                        } else {
                            courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                        }
                    } else {
                        courseList.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                    }
                }
                courseList.putExtra(Const.COURSES, course);
                activity.startActivity(courseList);
            });
        }

        private Course getCourseData(LiveClassCourseList liveClassCourseList) {
            Course course = new Course();
            course.setId(liveClassCourseList.getId());
            course.setTitle(liveClassCourseList.getTitle());
            course.setMrp(liveClassCourseList.getMrp());
            course.setCourse_type(liveClassCourseList.getCourseType());
            course.setCover_image(liveClassCourseList.getCoverImage());
            course.setCourse_category_fk(liveClassCourseList.getCourseCategoryFk());
            course.setDesc_header_image(liveClassCourseList.getDescHeaderImage());
            course.setDescription(liveClassCourseList.getDescription());
            course.setFor_dams(liveClassCourseList.getForDams());
            course.setNon_dams(liveClassCourseList.getNonDams());
            course.setLearner(liveClassCourseList.getCourseLearner());
            course.setIs_combo(liveClassCourseList.getIsCombo());
            course.setIs_live(liveClassCourseList.getIsLive());
            course.setRating(liveClassCourseList.getCourseRatingCount());
            course.setGst_include(liveClassCourseList.getGstInclude());
            return course;
        }
    }

    public class SingleStudyListHolder extends RecyclerView.ViewHolder {
        TextView noDataTV;
        RelativeLayout imageRl, parentLL, studyItemLL;
        ImageView imageIcon, liveIv;
        TextView titleCategory;
        ExpandableListView expandableListView;
        NoteTestExpandListAdapter notesTestAdapter;

        public SingleStudyListHolder(View itemView) {
            super(itemView);
            liveIv = itemView.findViewById(R.id.liveIV);
            imageRl = itemView.findViewById(R.id.imageRL);
            parentLL = itemView.findViewById(R.id.parentLL);
            studyItemLL = itemView.findViewById(R.id.study_single_itemLL);
            imageIcon = itemView.findViewById(R.id.profileImage);
            noDataTV = itemView.findViewById(R.id.noDataTV);
            titleCategory = itemView.findViewById(R.id.examPrepTitleTV);
            expandableListView = itemView.findViewById(R.id.notesTestRecyclerView);

        }

        public void setData() {


            if (!GenericUtils.isListEmpty(notesTestEpubResponse.getData().getList())) {
                noDataTV.setVisibility(View.GONE);
                notesTestAdapter = new NoteTestExpandListAdapter(activity, descriptionResponse, notesTestEpubResponse.getData().getList());

                // setting list adapter
                expandableListView.setAdapter(notesTestAdapter);
                setListViewHeight(expandableListView);
                expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
                    setListViewHeight(parent, groupPosition);
                    return false;
                });
                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup) {
                            expandableListView.collapseGroup(previousGroup);
                            previousGroup = groupPosition;
                        }
                    }
                });
            } else
                noDataTV.setVisibility(View.VISIBLE);
        }
    }

    public class StudyWebViewHolder extends RecyclerView.ViewHolder {
        WebView webView;
        ProgressDialog progressBar;

        public StudyWebViewHolder(View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webView);
        }


        public void setData(DescriptionResponse descriptionResponse) {
//            progressBar = ProgressDialog.show(activity, "", "loading...");

            Log.d("singleStudy", "singleStudy:" + new Gson().toJson(singleStudy));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }

//          activity.setProgressBarVisibility(true);

            WebSettings webSettings = webView.getSettings();

            final String googleDocs = "https://docs.google.com/viewer?url=";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                    if (url.endsWith(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "application/pdf");
                        /* Check if there is any application capable to process PDF file. */
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(intent);
                        } else {
                            /* If not, show PDF in Google Docs instead. */
                            webView.loadUrl(googleDocs + url);
                        }
                    } else {
                        webView.loadUrl(url);
                    }
                    return true;
                }
            });

            webSettings.setJavaScriptEnabled(true);
            webSettings.setDefaultTextEncodingName("utf-8");
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);


            webView.loadData(descriptionResponse.getData().getBasic().getDescription(), "text/html", "UTF-8");
        }
    }

    public class StudyOverviewViewHolder extends RecyclerView.ViewHolder {

        TextView courseTitle, courseDescription;
        ExpandableListView faqExpandListView;

        public StudyOverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);

        }

        public void setData(DescriptionResponse descriptionResponse) {
            courseTitle.setText(descriptionResponse.getData().getBasic().getTitle());
            courseDescription.setText(Html.fromHtml(descriptionResponse.getData().getBasic().getDescription()));
            List<DescriptionFAQ> faq = descriptionResponse.getData().getFaq();
            if (faq != null) {
                FAQExpandAdapter faqExpandAdapter = new FAQExpandAdapter(activity, faq, faqExpandListView);
                faqExpandListView.setAdapter(faqExpandAdapter);
                faqExpandListView.deferNotifyDataSetChanged();
                faqExpandListView.expandGroup(0);
            }
        }
    }

}
