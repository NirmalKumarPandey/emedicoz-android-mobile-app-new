package com.emedicoz.app.courses.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.cart.MyCartFragment;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.documentviewer.PdfViewer;
import com.emedicoz.app.courses.fragment.AllCategoryFragment;
import com.emedicoz.app.courses.fragment.ChooseModeFragement;
import com.emedicoz.app.courses.fragment.CommonFragForList;
import com.emedicoz.app.courses.fragment.CourseInvoice;
import com.emedicoz.app.courses.fragment.CourseReviews;
import com.emedicoz.app.courses.fragment.CreateCustomModule;
import com.emedicoz.app.courses.fragment.CreateCustomModuleSubject;
import com.emedicoz.app.courses.fragment.Curriculum;
import com.emedicoz.app.courses.fragment.CustomModuleStartFragement;
import com.emedicoz.app.courses.fragment.CustomModuleTagfragement;
import com.emedicoz.app.courses.fragment.DetailComboCourse;
import com.emedicoz.app.courses.fragment.DownloadFragment;
import com.emedicoz.app.courses.fragment.ExamPrepLayer1;
import com.emedicoz.app.courses.fragment.ExamPrepLayer2;
import com.emedicoz.app.courses.fragment.FAQFragment;
import com.emedicoz.app.courses.fragment.InstructorFragment;
import com.emedicoz.app.courses.fragment.MyScorecardFragment;
import com.emedicoz.app.courses.fragment.NewViewAllFrag;
import com.emedicoz.app.courses.fragment.OrderFragment;
import com.emedicoz.app.courses.fragment.SeeAllClassesFragment;
import com.emedicoz.app.courses.fragment.SeeAllFragment;
import com.emedicoz.app.courses.fragment.SingleStudy;
import com.emedicoz.app.courses.fragment.StudyFragment;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dailychallenge.DailyChallengeDashboard;
import com.emedicoz.app.feeds.fragment.FollowerFollowingFragment;
import com.emedicoz.app.feeds.fragment.SavedNotesFragment;
import com.emedicoz.app.installment.activity.ViewDetail;
import com.emedicoz.app.installment.fragment.InvoiceFragment;
import com.emedicoz.app.installment.fragment.OrderDetailFragment;
import com.emedicoz.app.installment.fragment.ShowTransactionStatementFragment;
import com.emedicoz.app.installment.fragment.SuccessfullyPaymentDoneFragment;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.modelo.Total;
import com.emedicoz.app.modelo.UpcomingCourseData;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.modelo.courses.ExamPrepItem;
import com.emedicoz.app.modelo.courses.Lists;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.SingleStudyModel;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.modelo.custommodule.ModuleData;
import com.emedicoz.app.modelo.custommodule.SubjectData;
import com.emedicoz.app.modelo.testseries.OrderHistoryData;
import com.emedicoz.app.mycourses.MyCourseTabFragment;
import com.emedicoz.app.podcast.PodcastFragment;
import com.emedicoz.app.recordedCourses.fragment.RecordedCoursesFragment;
import com.emedicoz.app.registration.fragment.ChooseRegistrationPreference;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseActivity extends BaseABNoNavActivity {

    private static final String TAG = "CourseActivity";
    public boolean courseSeeAll = true;
    public ArrayList<SubjectData> subjectData = new ArrayList<>();
    public String contentType = "";
    String qTypeDqb = "";
    public Lists lists;
    public Total totals;
    CourseCategory courseCategory;
    SingleCourseData singleCourseData;
    Reviews[] reviews;
    Course course;
    ArrayList<Course> arrList = new ArrayList<>();
    Topics[] topics = new Topics[0];
    String adapterType;
    String id;
    String categoryId = "";
    String title = "";
    ArrayList<CourseCategory> courseCategories = new ArrayList<>();
    HashMap<String, String> finalResponse = new HashMap<>();
    ModuleData moduleData;
    boolean isModuleStart;
    ExamPrepItem examPrepItem;
    SingleStudyModel singleStudy;
    OrderHistoryData orderHistoryData;
    private String fragType = "";
    private String courseByInstructor = "";
    private String path = "";
    private String startDate = "";
    private String endDate = "";
    private String image;
    private String courseId = "";
    List<Course> courseArrayList;
    boolean isLiveCourse;
    String classType;
    private ArrayList<UpcomingCourseData> upcomingClassList;
    View view;
    Activity activity;


    @Override
    protected void initViews() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            image = getIntent().getExtras().getString(Const.IMAGE);
            fragType = getIntent().getExtras().getString(Const.FRAG_TYPE);
            courseByInstructor = getIntent().getExtras().getString(Const.INSTRUCTOR_NAME);
            type = getIntent().getExtras().getString(Constants.Extras.TYPE);
            courseCategory = (CourseCategory) getIntent().getExtras().getSerializable(Const.COURSE_CATEGORY);
            course = (Course) getIntent().getExtras().getSerializable(Const.COURSES);
            classType = getIntent().getExtras().getString("CLASS_TYPE");
            upcomingClassList = (ArrayList<UpcomingCourseData>) getIntent().getExtras().getSerializable("UPCOMING_ONGOING_CLASSES");
            isLiveCourse = getIntent().getExtras().getBoolean(Const.LIVE_CLASSES);
            finalResponse = (HashMap<String, String>) getIntent().getExtras().getSerializable(Const.finalResponse);
            courseArrayList = (List<Course>) getIntent().getExtras().getSerializable(Const.COURSE_LIST);

            moduleData = (ModuleData) getIntent().getExtras().getSerializable(Const.moduleData);
            isModuleStart = getIntent().getExtras().getBoolean(Const.IS_MODULE_START);

            lists = (Lists) getIntent().getExtras().getSerializable(Const.LIST);
            totals = (Total) getIntent().getExtras().getSerializable(Constants.Extras.TEST_TYPE);

            Serializable courseData = getIntent().getExtras().getSerializable(Const.COURSE_DESC);
            if (courseData instanceof SingleCourseData)
                singleCourseData = (SingleCourseData) courseData;
            else
                singleCourseData = new SingleCourseData();

            singleStudy = (SingleStudyModel) getIntent().getExtras().getSerializable(Const.SINGLE_STUDY);
            reviews = (Reviews[]) getIntent().getExtras().getSerializable(Const.REVIEWS);
            path = getIntent().getExtras().getString(Const.PATH);
            startDate = getIntent().getExtras().getString(Constants.Extras.START_DATE);
            endDate = getIntent().getExtras().getString(Constants.Extras.END_DATE);
            courseId = getIntent().getExtras().getString(Const.PARENT_ID);
            categoryId = getIntent().getExtras().getString(Const.CATEGORY_ID);

            title = getIntent().getExtras().getString(Const.TITLE);
            contentType = getIntent().getExtras().getString(Const.CONTENT_TYPE);
            examPrepItem = (ExamPrepItem) getIntent().getExtras().getSerializable(Const.EXAMPREP);
            path = getIntent().getExtras().getString(Const.PATH);
            courseCategories = (ArrayList<CourseCategory>) getIntent().getExtras().getSerializable(Constants.Extras.COURSE_CAT);
            orderHistoryData = (OrderHistoryData) getIntent().getExtras().getSerializable(Const.ORDER_DETAIL);
            classType = getIntent().getExtras().getString("CLASS_TYPE");
            upcomingClassList = (ArrayList<UpcomingCourseData>) getIntent().getExtras().getSerializable("UPCOMING_ONGOING_CLASSES");
            arrList = (ArrayList<Course>) getIntent().getExtras().getSerializable(Const.COURSE_LIST);
            qTypeDqb = getIntent().getStringExtra(Constants.Extras.Q_TYPE_DQB);

            if (getIntent().getSerializableExtra("viewalldata") != null)
                topics = (Topics[]) getIntent().getSerializableExtra("viewalldata");
        }
        if (fragType != null && fragType.equalsIgnoreCase(Const.SELECT_SUBJECT)) {
            setToolbarTitle(Const.CREATE_CUSTOM_MODULE);
            getSubjectList();
        }

        if (Objects.requireNonNull(getIntent()).getStringExtra("ADAPTER_TYPE") != null) {
            adapterType = getIntent().getStringExtra("ADAPTER_TYPE");
            id = getIntent().getStringExtra(Constants.Extras.ID);
        }

        if (fragType != null && fragType.equalsIgnoreCase(Const.ALLCOURSES)) {
            searchView.setVisibility(View.VISIBLE);
        } else searchView.setVisibility(View.GONE);

    }

    private void getSubjectList() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mProgress = new Progress(this);
        mProgress.setCancelable(false);
        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getSubjectList(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("sub_LIST", jsonResponse.toString());

                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            mProgress.dismiss();
                            if (subjectData != null) subjectData.clear();
                            else subjectData = new ArrayList<>();

                            SharedPreference.getInstance().putString("IS_TAG", jsonResponse.optString("is_tag"));

                            for (int i = 0; i < jsonResponse.getJSONArray(Const.DATA).length(); i++) {
                                subjectData.add(gson.fromJson(String.valueOf(jsonResponse.getJSONArray(Const.DATA).getJSONObject(i)), SubjectData.class));
                            }
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateCustomModuleSubject.newInstance(finalResponse)).addToBackStack(null).commit();

                        } else {
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
                Helper.showErrorLayoutForNoNav(API.API_GET_REWARD_POINTS, CourseActivity.this, 1, 1);
            }
        });
    }

    @Override
    protected Fragment getFragment() {
        if (fragType == null)
            fragType = "";
        switch (fragType) {
            case Const.SEEALL_INSTRUCTOR_COURSE:
                setToolbarTitle(Const.ALLCOURSES);
                return CommonFragForList.newInstance(fragType, arrList);

            case Const.ALLCOURSES:
                setToolbarTitle(Const.ALLCOURSES);
                return CommonFragForList.newInstance(fragType, courseCategory);
            case Const.MYCOURSES:
                //setToolbarTitle(Const.MYCOURSES);
//                return MyCoursesFragment.newInstance(fragType);
                return MyCourseTabFragment.newInstance(fragType);

            case Const.MYTEST:
                setToolbarTitle(Const.MYTEST);
//                return MyCoursesFragment.newInstance(fragType);
                return RecordedCoursesFragment.newInstance(fragType);

            case Const.MYQBANK:
                setToolbarTitle(Const.MYQBANK);
//                return MyCoursesFragment.newInstance(fragType);
                return MyCourseTabFragment.newInstance(fragType);

            case Const.MYORDERS:
                setToolbarTitle(Const.MYORDERS);
                return OrderFragment.newInstance();

            case Const.INVOICE:
                setToolbarTitle(getString(R.string.invoice));
                return InvoiceFragment.newInstance(orderHistoryData);
            case Const.TRANSACTION_STATEMENT:
                setToolbarTitle(getString(R.string.transactions));
                return ShowTransactionStatementFragment.newInstance(orderHistoryData);
            case Const.VIEW_DETAILS:
                setToolbarTitle(getString(R.string.view_details));
                return ViewDetail.newInstance(orderHistoryData);

            case Const.MYDOWNLOAD:
                setToolbarTitle(Const.MYDOWNLOAD);
                return DownloadFragment.newInstance();

            case Const.BOOKMARKS:
                setToolbarTitle(Const.MY_BOOKMARKS);
                return SavedNotesFragment.newInstance();

            case Const.REWARDPOINTS:
                setToolbarTitle("Invite and Earn");

            case Const.PODCAST:
                setToolbarTitle(Const.PODCAST);
                return PodcastFragment.newInstance(false);

            case Const.DAILY_QUIZ:
                setToolbarTitle("Daily Challenge");
                PostFile postFile = eMedicozApp.getInstance().postFile;
                return DailyChallengeDashboard.newInstance(postFile);

            case Constants.StudyType.CRS:
                setToolbarTitle("Study");
                return StudyFragment.newInstance(Constants.StudyType.CRS);

            case "CUSTOM_MODULE":
                setToolbarTitle(Const.CREATE_CUSTOM_MODULE);

                return CreateCustomModule.newInstance();

            case Const.SELECT_TAG:
                setToolbarTitle("Select Tags");

                return CustomModuleTagfragement.newInstance(finalResponse);
            case Const.SELECT_MODE:
                setToolbarTitle(Const.CREATE_CUSTOM_MODULE);

                return ChooseModeFragement.newInstance(finalResponse);
            case Const.STARTMODULE:
                setToolbarTitle("Start your Module");
                return CustomModuleStartFragement.newInstance(moduleData, isModuleStart);
            case "MY_POST":
                if (id.equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                    setToolbarTitle("My Posts");
                else
                    setToolbarTitle("Posts");
                return FollowerFollowingFragment.newInstance(adapterType, id);
            case "FOLLOWING":
                setToolbarTitle("Following");
                return FollowerFollowingFragment.newInstance(adapterType, id);

            case "FOLLOWER":
                setToolbarTitle("Followers");
                return FollowerFollowingFragment.newInstance(adapterType, id);

            case Const.LEADERBOARD:
                setToolbarTitle(Const.LEADERBOARD);
                return MyScorecardFragment.newInstance();
            case "viewall":
                setToolbarTitle(Const.ALLCOURSES);
                return NewViewAllFrag.newInstance(fragType, topics, getIntent().getStringExtra("courseid"));
            case Const.SEEALL_COURSE:
                if (categoryId != null) {
                    setToolbarTitle(title);
                } else {
                    if (courseCategory != null)
                        setToolbarTitle(courseCategory.getName());
                }
                return SeeAllFragment.newInstance(fragType, categoryId, courseCategory, isLiveCourse);

            case Const.MORE_COURSE:
                setToolbarTitle(title);
                return SeeAllFragment.newInstance(Const.MORE_COURSE, courseArrayList, isLiveCourse);

            case Const.SEEALL_CLASSES:
                if (classType.equals(Const.UPCOMING))
                    setToolbarTitle("Upcoming Live Classes");
                else if (classType.equals(Const.ONGOING))
                    setToolbarTitle("Ongoing Live Classes");
                return SeeAllClassesFragment.newInstance(upcomingClassList, classType);

            case Const.STUDENT_ARE_VIEWING:
                setToolbarTitle("Student Are Viewing");
                return SeeAllFragment.newInstance(Const.RECENTLY_VIEWED_COURSE, courseArrayList, isLiveCourse);

            case Const.INSTRUCTOR_COURSES:
                setToolbarTitle(courseByInstructor);
                return SeeAllFragment.newInstance(Const.RECENTLY_VIEWED_COURSE, courseArrayList, isLiveCourse);


            case Const.RECENTLY_VIEWED_COURSE:
                setToolbarTitle(getResources().getString(R.string.rc_title_recently_viewed));
                return SeeAllFragment.newInstance(Const.RECENTLY_VIEWED_COURSE, courseArrayList, isLiveCourse);
            case Const.FAQ:
                setToolbarTitle(getString(R.string.faq_title));
                return FAQFragment.newInstance(fragType, course);
            case Const.COMBO_COURSE:
//                setToolbarTitle(!TextUtils.isEmpty(course.getTitle()) ? course.getTitle() : "");
//                return ComboCourseFragment.newInstance(course);
            case Const.TEST_COURSE:
//                setToolbarTitle(!TextUtils.isEmpty(course.getTitle()) ? course.getTitle() : "");
//                return TestQuizCourseFragment.newInstance(course, startDate, endDate);

            case Const.SINGLE_COURSE:
            case Const.SINGLE_STUDY:
                return SingleStudy.newInstance(image, course);

            case Const.QBANK_COURSE:
//                setToolbarTitle(getResources().getString(R.string.study));
//                return StudyFragment.newInstance();
                if (course != null) {
                    if (course.getCourse_type().equals("2")) {
                        Intent intent = new Intent(CourseActivity.this, HomeActivity.class);
                        intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.TESTS);
                        startActivity(intent);
                        finish();
                        //Helper.GoToStudySection(CourseActivity.this, Constants.StudyType.TESTS);
                    } else if (course.getCourse_type().equals("3")) {
                        Intent intent = new Intent(CourseActivity.this, HomeActivity.class);
                        intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS);
                        startActivity(intent);
                        finish();
                        //Helper.GoToStudySection(CourseActivity.this, Constants.StudyType.QBANKS);
                    } else {
                        Helper.GoToStudySection(CourseActivity.this);
                    }
                } else {
                    Helper.GoToStudySection(CourseActivity.this);
                }


                break;
            case "All_Cats":
                setToolbarTitle("All Categories");
                return AllCategoryFragment.newInstance(courseCategories);

            case Const.DETAIL_COMBO:
                return DetailComboCourse.newInstance(singleCourseData, courseId);
           /* case Const.SINGLE_COURSE:
                String keytohide;
                if (!TextUtils.isEmpty(getIntent().getStringExtra("hidekey"))) {
                    keytohide = getIntent().getStringExtra("hidekey");
                } else {
                    keytohide = "";
                }

                setToolbarTitle(!TextUtils.isEmpty(course.getTitle()) ? course.getTitle() : "");
                if (!Helper.isStringValid(courseId))
                    courseId = "0";
                return SingleCourse.newInstance(course, courseId, keytohide);
//                return NewSingleCourse.newInstance(course, courseId, keytohide);*/
            case Const.REVIEWS:
            case Const.INSTR_REVIEWS:
                setToolbarTitle(Const.ALL_REVIEWS);
                return CourseReviews.newInstance(fragType, singleCourseData);

            /*case Const.INSTR_REVIEWS:

                setToolbarTitle(Const.ALL_REVIEWS);
                return CourseReviews.newInstance(frag_type, singleCourseData);*/
            case Const.INSTR:
                setToolbarTitle(getString(R.string.instructor));
                return InstructorFragment.newInstance(singleCourseData);
            case Const.CURRICULUM:
                setToolbarTitle(getString(R.string.curriculum));
                return Curriculum.newInstance(singleCourseData);
            case Const.PDF:
                return PdfViewer.newInstance(fragType, path);
            case Const.EXAMPREP:
                return ExamPrepLayer1.newInstance(examPrepItem, lists, contentType, title, singleStudy);
            case Const.EXAMPREPLAST:
                return ExamPrepLayer2.newInstance(examPrepItem, lists, contentType, title, totals, singleStudy);
            case Const.COURSE_INVOICE:
                if (!TextUtils.isEmpty(singleCourseData.getIs_subscription()) && singleCourseData.getIs_subscription().equals("1")) {
                    setToolbarTitle(getString(R.string.subscription));
                } else {
                    setToolbarTitle(type.equals(Const.CPR_INVOICE) ? getString(R.string.summary) : getString(R.string.course_summary));
                }
//                setToolbarTitle(type.equals(Const.CPR_INVOICE) ? getString(R.string.CPR_invoice) : getString(R.string.Invoice));
                return CourseInvoice.newInstance(singleCourseData, type);

            case Const.MYCART:
                setToolbarTitle(Const.MYCART);
//                return CourseInvoice.newInstance(singleCourseData, type);
                return MyCartFragment.newInstance(Const.MYCART);


            case Const.MY_BOOKMARKS:
                Log.e(TAG, "getFragment: " + Constants.Extras.Q_TYPE_DQB + qTypeDqb);
                setToolbarTitle(Const.MY_BOOKMARKS);
                return SavedNotesFragment.newInstance(qTypeDqb);

            case Const.REG_COURSE:
                setToolbarTitle(getResources().getString(R.string.choose_your_preference));
                return ChooseRegistrationPreference.newInstance();

            case Const.SUCCESSFULLY_PAYMENT_DONE:
                setToolbarTitle("Successful Payment");
                return SuccessfullyPaymentDoneFragment.newInstance(orderHistoryData);

            case Const.ORDER_DETAIL:

                setToolbarTitle(getResources().getString(R.string.order_detail));
                return OrderDetailFragment.newInstance(singleCourseData);

//            case Const.PPT:
//                return PptViewer.newInstance(frag_type, path);

            default:
        }
        return null;
    }

    private void setTitle() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mFragment instanceof CourseInvoice) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
