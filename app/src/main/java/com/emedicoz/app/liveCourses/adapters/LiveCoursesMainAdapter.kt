package com.emedicoz.app.liveCourses.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.combocourse.activity.ComboCourseActivity
import com.emedicoz.app.common.BaseABNavActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.databinding.*
import com.emedicoz.app.liveCourses.model.LiveCoursesViewModel
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.modelo.courses.CoursesData
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.templateAdapters.*
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class LiveCoursesMainAdapter(
    private val context: Context,
    private val viewModel: LiveCoursesViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            Constants.RecordedCourseTemplates.UPCOMING_CLASS -> {
                val binding = LayoutUpcomingClassBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return UpcomingClassViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.ONGOING_CLASS -> {
                val binding = LayoutUpcomingClassBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return UpcomingClassViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.VERTICAL_LIST -> {
                val binding = RecordedScreenVerticalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return VerticalViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.TRENDING_COURSES -> {
                val binding = RecordedScreenVerticalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return TrendingCoursesViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.HORIZONTAL_LIST -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return HorizontalViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.STUDENT_VIEWING_COURSES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return StudentViewingViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.BEST_SELLING_COURSES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return BestSellerViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.COURSES_AS_PER_SEARCH -> {
                val binding = RecordedScreenVerticalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return CourseAsPerSearchViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.COURSE_CATEGORY_TILES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return CategoryTileViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.CATEGORIES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return CategoriesCoursesViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.IMAGE_BANNERS -> {
                val binding = RecordedScreenVerticalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ImageViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.RECENTLY_VIEWED_COURSES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return RecentlyCoursesViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.LIVE_CLASSES -> {
                val binding = RecordedScreenButtonsBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ButtonViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN -> {
                val binding = RecordedScreenButtonsBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ButtonViewHolder(binding)
            }
            Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT -> {
                val binding = RecordedScreenButtonsBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ButtonViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.MY_LIVE_COURSES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return MyLiveCoursesViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.UPCOMING_LIVE_CLASSES -> {
                val binding = RecordedScreenHorizontalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return UpcomingLiveClassesViewHolder(binding)
            }

            Constants.RecordedCourseTemplates.UPCOMING_LECTURE_TODAY -> {
                val binding = RecordedScreenVerticalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return UpcomingLectureTodayViewHolder(binding)
            }

        }

        // else or default layout
        val binding = RecordedScreenDefaultBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Constants.RecordedCourseTemplates.UPCOMING_CLASS -> {
                upcomingClassView(holder as UpcomingClassViewHolder, position)
            }

            Constants.RecordedCourseTemplates.ONGOING_CLASS -> {
                ongoingClassView(holder as UpcomingClassViewHolder, position)
            }
            Constants.RecordedCourseTemplates.VERTICAL_LIST -> {
                verticalListView(holder as VerticalViewHolder, position)
            }
            Constants.RecordedCourseTemplates.TRENDING_COURSES -> {
                trendingCoursesView(holder as TrendingCoursesViewHolder, position)
            }

            Constants.RecordedCourseTemplates.HORIZONTAL_LIST -> {
                horizontalListView(holder as HorizontalViewHolder, position)
            }
            Constants.RecordedCourseTemplates.STUDENT_VIEWING_COURSES -> {
                studentViewingCoursesView(holder as StudentViewingViewHolder, position)
            }

            Constants.RecordedCourseTemplates.BEST_SELLING_COURSES -> {
                bestSellingCoursesView(holder as BestSellerViewHolder, position)
            }
            Constants.RecordedCourseTemplates.COURSES_AS_PER_SEARCH -> {
                courseAsPerSearchView(holder as CourseAsPerSearchViewHolder, position)
            }

            Constants.RecordedCourseTemplates.COURSE_CATEGORY_TILES -> {
                categoryTilesView(holder as CategoryTileViewHolder)
            }
            Constants.RecordedCourseTemplates.CATEGORIES -> {
                categoriesCourseView(holder as CategoriesCoursesViewHolder)
            }
            Constants.RecordedCourseTemplates.IMAGE_BANNERS -> {
                imageBannerView(holder as ImageViewHolder)
            }
            Constants.RecordedCourseTemplates.RECENTLY_VIEWED_COURSES -> {
                recentlyViewedCourseView(holder as RecentlyCoursesViewHolder)
            }

            Constants.RecordedCourseTemplates.LIVE_CLASSES -> {
                buttonView(holder as ButtonViewHolder, position)
            }
            Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN -> {
                buttonView(holder as ButtonViewHolder, position)
            }
            Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT -> {
                buttonView(holder as ButtonViewHolder, position)
            }

            Constants.RecordedCourseTemplates.MY_LIVE_COURSES -> {
                myLiveCoursesView(holder as MyLiveCoursesViewHolder, position)
            }

            Constants.RecordedCourseTemplates.UPCOMING_LIVE_CLASSES -> {
                upcomingLiveClassesView(holder as UpcomingLiveClassesViewHolder, position)
            }

            Constants.RecordedCourseTemplates.UPCOMING_LECTURE_TODAY -> {
                upcomingLectureTodayView(holder as UpcomingLectureTodayViewHolder, position)
            }

        }
    }

    private fun ongoingClassView(
        holder: LiveCoursesMainAdapter.UpcomingClassViewHolder,
        position: Int
    ) {
        val upcomingClassAdapter = OngoingAdapter(context, viewModel.onGoingArrayList)
        holder.layoutUpcomingClassBinding.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        holder.layoutUpcomingClassBinding.innerRecyclerViewVertical.adapter = upcomingClassAdapter
//        holder.verticalListRecordedScreen.root.visibility = View.GONE
        holder.layoutUpcomingClassBinding.upcomingClassTV.text = "Ongoing Live Class"

        // holder.layoutUpcomingClassBinding.showAllUpcomingClass.tag = viewModel.coursesDataArrayList.get(position)
        if (upcomingClassAdapter.itemCount > 4) {
            holder.layoutUpcomingClassBinding.showAllUpcomingClass.visibility = View.VISIBLE
            holder.layoutUpcomingClassBinding.showAllUpcomingClass.setOnClickListener {
                val courseList = Intent(context, CourseActivity::class.java)
                courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_CLASSES)
                courseList.putExtra("UPCOMING_ONGOING_CLASSES", viewModel.onGoingArrayList)
                courseList.putExtra("CLASS_TYPE", Const.ONGOING)
                context.startActivity(courseList)
            }
        } else
            holder.layoutUpcomingClassBinding.showAllUpcomingClass.visibility = View.GONE
    }

    private fun upcomingClassView(holder: UpcomingClassViewHolder, position: Int) {
        val upcomingClassAdapter = UpcomingClassAdapter(context, viewModel.upcomingArrayList)
        holder.layoutUpcomingClassBinding.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        holder.layoutUpcomingClassBinding.innerRecyclerViewVertical.adapter = upcomingClassAdapter
//        holder.verticalListRecordedScreen.root.visibility = View.GONE
        holder.layoutUpcomingClassBinding.upcomingClassTV.text = "Upcoming Live Class"

        // holder.layoutUpcomingClassBinding.showAllUpcomingClass.tag = viewModel.coursesDataArrayList.get(position)
        if (upcomingClassAdapter.itemCount > 4) {
            holder.layoutUpcomingClassBinding.showAllUpcomingClass.visibility = View.VISIBLE
            holder.layoutUpcomingClassBinding.showAllUpcomingClass.setOnClickListener {
                val courseList = Intent(context, CourseActivity::class.java)
                courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_CLASSES)
                courseList.putExtra("UPCOMING_ONGOING_CLASSES", viewModel.upcomingArrayList)
                courseList.putExtra("CLASS_TYPE", Const.UPCOMING)
                context.startActivity(courseList)
            }
        } else
            holder.layoutUpcomingClassBinding.showAllUpcomingClass.visibility = View.GONE
    }

    private fun getCourseListByIndex(position: Int): java.util.ArrayList<Course> {
        return viewModel.coursesDataArrayList.get(position).course_list
    }

    private fun redirectToSeeAll(coursesData: CoursesData) {

        val courseList = Intent(context, CourseActivity::class.java)
        courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE)
        courseList.putExtra(Const.LIVE_CLASSES, true)
        courseList.putExtra(Const.COURSE_CATEGORY, coursesData.category_info)
        context.startActivity(courseList)
    }

    private fun redirectToSeeAll(coursesList: ArrayList<Course>) {

        val courseList = Intent(context, CourseActivity::class.java)
        courseList.putExtra(Const.FRAG_TYPE, Const.RECENTLY_VIEWED_COURSE)
        courseList.putExtra(Const.LIVE_CLASSES, true)
        courseList.putExtra(Const.COURSE_LIST, coursesList)
        context.startActivity(courseList)
    }

    //region Rendering of views
    //region Vertical listing; data received form record course list API
    private fun verticalListView(holder: VerticalViewHolder, position: Int) {
        val recordedCourseTrendingAdapter = RecordedCourseTrendingAdapter(
            getCourseListByIndex(position),
            context,
            false,
            false,
            Const.LIVE_CLASSES
        )
        holder.verticalListRecordedScreen.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context)
        holder.verticalListRecordedScreen.innerRecyclerViewVertical.adapter =
            recordedCourseTrendingAdapter
//        holder.verticalListRecordedScreen.root.visibility = View.GONE
        holder.verticalListRecordedScreen.courseCategories.text =
            viewModel.coursesDataArrayList.get(position).category_info.name

        holder.verticalListRecordedScreen.showAllVerticalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCourseTrendingAdapter.itemCount > 4) {
            holder.verticalListRecordedScreen.showAllVerticalLink.visibility = View.VISIBLE
            holder.verticalListRecordedScreen.showAllVerticalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.verticalListRecordedScreen.showAllVerticalLink.visibility = View.GONE

    }

    private fun trendingCoursesView(holder: TrendingCoursesViewHolder, position: Int) {
        val recordedCourseTrendingAdapter = RecordedCourseTrendingAdapter(
            getCourseListByIndex(position),
            context,
            false,
            true,
            Const.LIVE_CLASSES
        )
        holder.trendingRecordedScreen.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context)
        holder.trendingRecordedScreen.innerRecyclerViewVertical.adapter =
            recordedCourseTrendingAdapter
        holder.trendingRecordedScreen.courseCategories.text =
            context.getString(R.string.rc_title_trending_courses)

        holder.trendingRecordedScreen.showAllVerticalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCourseTrendingAdapter.itemCount > 4) {
            holder.trendingRecordedScreen.showAllVerticalLink.visibility = View.VISIBLE
            holder.trendingRecordedScreen.showAllVerticalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.trendingRecordedScreen.showAllVerticalLink.visibility = View.GONE
    }
    //endregion

    //region Horizontal listing; data received form record course list API
    private fun horizontalListView(holder: HorizontalViewHolder, position: Int) {
        val recordedCourseBestSellerAdapter = RecordedCourseBestSellerAdapter(
            getCourseListByIndex(position),
            context,
            false,
            Const.LIVE_CLASSES
        )
        holder.horizontalListCourseBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.horizontalListCourseBinding.innerRecyclerViewHorizontal.adapter =
            recordedCourseBestSellerAdapter
        holder.horizontalListCourseBinding.courseCategories.text =
            viewModel.coursesDataArrayList.get(position).category_info.name
//        holder.horizontalListCourseBinding.root.visibility = View.GONE

        holder.horizontalListCourseBinding.showAllHorizontalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCourseBestSellerAdapter.itemCount > 4) {
            holder.horizontalListCourseBinding.showAllHorizontalLink.visibility = View.VISIBLE
            holder.horizontalListCourseBinding.showAllHorizontalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.horizontalListCourseBinding.showAllHorizontalLink.visibility = View.GONE
    }

    private fun studentViewingCoursesView(holder: StudentViewingViewHolder, position: Int) {
        val recordedCoursesStudentViewingAdapter = RecordedCoursesStudentViewingAdapter(
            getCourseListByIndex(position),
            context,
            Const.LIVE_CLASSES
        )
        holder.studentViewingBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.studentViewingBinding.innerRecyclerViewHorizontal.adapter =
            recordedCoursesStudentViewingAdapter
        holder.studentViewingBinding.courseCategories.text =
            context.getString(R.string.rc_title_student_viewing)

        holder.studentViewingBinding.showAllHorizontalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCoursesStudentViewingAdapter.itemCount > 4) {
            holder.studentViewingBinding.showAllHorizontalLink.visibility = View.VISIBLE
            holder.studentViewingBinding.showAllHorizontalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.studentViewingBinding.showAllHorizontalLink.visibility = View.GONE
    }

    private fun bestSellingCoursesView(holder: BestSellerViewHolder, position: Int) {
        val recordedCoursesBestSellerAdapter = RecordedCourseBestSellerAdapter(
            getCourseListByIndex(position),
            context,
            true,
            Const.LIVE_CLASSES
        )
        holder.bestsellerCoursesBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.bestsellerCoursesBinding.innerRecyclerViewHorizontal.adapter =
            recordedCoursesBestSellerAdapter
        holder.bestsellerCoursesBinding.courseCategories.text =
            context.getString(R.string.rc_title_best_seller)

        holder.bestsellerCoursesBinding.showAllHorizontalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCoursesBestSellerAdapter.itemCount > 4) {
            holder.bestsellerCoursesBinding.showAllHorizontalLink.visibility = View.VISIBLE
            holder.bestsellerCoursesBinding.showAllHorizontalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.bestsellerCoursesBinding.showAllHorizontalLink.visibility = View.GONE
    }
    //endregion


    private fun myLiveCoursesView(holder: MyLiveCoursesViewHolder, position: Int) {
        val list = getCroppedCourseList(getCourseListByIndex(position))
        val recordedCoursesBestSellerAdapter = LiveCoursesMyCourseAdapter(list, context)
        holder.myLiveCoursesBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.myLiveCoursesBinding.innerRecyclerViewHorizontal.adapter =
            recordedCoursesBestSellerAdapter
        holder.myLiveCoursesBinding.courseCategories.text =
            context.getString(R.string.live_title_my_live_courses)

        holder.myLiveCoursesBinding.showAllHorizontalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCoursesBestSellerAdapter.itemCount > 4) {
            holder.myLiveCoursesBinding.showAllHorizontalLink.visibility = View.VISIBLE
            holder.myLiveCoursesBinding.showAllHorizontalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.myLiveCoursesBinding.showAllHorizontalLink.visibility = View.GONE
    }


    private fun upcomingLiveClassesView(holder: UpcomingLiveClassesViewHolder, position: Int) {
        val recordedCoursesBestSellerAdapter =
            LiveCoursesUpcomingCoursesAdapter(getCourseListByIndex(position), context)
        holder.upcomingLiveClassesBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.upcomingLiveClassesBinding.innerRecyclerViewHorizontal.adapter =
            recordedCoursesBestSellerAdapter
        holder.upcomingLiveClassesBinding.courseCategories.text =
            context.getString(R.string.live_title_upcoming_live_classes)

        holder.upcomingLiveClassesBinding.showAllHorizontalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCoursesBestSellerAdapter.itemCount > 4) {
            holder.upcomingLiveClassesBinding.showAllHorizontalLink.visibility = View.VISIBLE
            holder.upcomingLiveClassesBinding.showAllHorizontalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.upcomingLiveClassesBinding.showAllHorizontalLink.visibility = View.GONE
    }

    private fun upcomingLectureTodayView(holder: UpcomingLectureTodayViewHolder, position: Int) {
        val list = getCroppedCourseList(getCourseListByIndex(position))
        val recordedCoursesBestSellerAdapter = LiveCoursesUpcomingLectureAdapter(list, context)
        holder.upcomingLectureTodayBinding.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context)
        holder.upcomingLectureTodayBinding.innerRecyclerViewVertical.adapter =
            recordedCoursesBestSellerAdapter
        holder.upcomingLectureTodayBinding.courseCategories.text =
            context.getString(R.string.live_title_upcoming_lecture_today)

        holder.upcomingLectureTodayBinding.showAllVerticalLink.tag =
            viewModel.coursesDataArrayList.get(position)
        if (recordedCoursesBestSellerAdapter.itemCount > 4) {
            holder.upcomingLectureTodayBinding.showAllVerticalLink.visibility = View.VISIBLE
            holder.upcomingLectureTodayBinding.showAllVerticalLink.setOnClickListener {
                redirectToSeeAll(it.tag as CoursesData)
            }
        } else
            holder.upcomingLectureTodayBinding.showAllVerticalLink.visibility = View.GONE
    }


    //region Horizontal listing; data received form separate keys of record course list API
    private fun categoryTilesView(holder: CategoryTileViewHolder) {
        val recordedCourseTileAdapter =
            RecordedCourseTilesAdapter(viewModel.tilesArrayList, context)
        holder.typeCourseBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.typeCourseBinding.innerRecyclerViewHorizontal.adapter = recordedCourseTileAdapter
        holder.typeCourseBinding.courseCategories.visibility = View.GONE
        holder.typeCourseBinding.showAllHorizontalLink.visibility = View.GONE
    }

    private fun categoriesCourseView(holder: CategoriesCoursesViewHolder) {
        val recordedCourseCategoriesAdapter =
            RecordedCourseCategoriesAdapter(viewModel.categoryArrayList, context)
        holder.categoriesCoursesBinding.innerRecyclerViewHorizontal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.categoriesCoursesBinding.innerRecyclerViewHorizontal.adapter =
            recordedCourseCategoriesAdapter
        holder.categoriesCoursesBinding.courseCategories.text =
            context.getString(R.string.rc_title_categories)
//        holder.categoriesCoursesBinding.showAllHorizontalLink.visibility = View.GONE
        if (viewModel.categoryArrayList.size > 5) {
            holder.categoriesCoursesBinding.showAllHorizontalLink.visibility = View.VISIBLE
        } else {
            holder.categoriesCoursesBinding.showAllHorizontalLink.visibility = View.GONE
        }

        holder.categoriesCoursesBinding.showAllHorizontalLink.setOnClickListener {
            if (context is HomeActivity) {
                context.courseSidePanel()
                context.setSidePanelCourse(viewModel.coursesDataArrayList)
            }
        }
    }
    //endregion

    //region Slider views
    private fun imageBannerView(holder: ImageViewHolder) {
        val recordedCourseImageSliderAdapter =
            RecordedCourseImageSliderAdapter(getImageBannerList(), context)
        holder.imageViewBinding.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context)
        holder.imageViewBinding.innerRecyclerViewVertical.adapter = recordedCourseImageSliderAdapter
        holder.imageViewBinding.courseCategories.visibility = View.GONE
        holder.imageViewBinding.showAllVerticalLink.visibility = View.GONE
        if (getImageBannerList()[0].size == 0)
            holder.imageViewBinding.root.visibility = View.GONE
    }

    private fun courseAsPerSearchView(holder: CourseAsPerSearchViewHolder, position: Int) {
        val recordedCourseAsPerSearchAdapter =
            RecordedCourseAsPerSearchAdapter(getCourseAsPerSearchList(position), context)
        holder.courseAsPerSearch.innerRecyclerViewVertical.layoutManager =
            LinearLayoutManager(context)
        holder.courseAsPerSearch.innerRecyclerViewVertical.adapter =
            recordedCourseAsPerSearchAdapter
        holder.courseAsPerSearch.courseCategories.text =
            context.getString(R.string.rc_title_searched_courses)
        holder.courseAsPerSearch.showAllVerticalLink.visibility = View.GONE
    }

    private fun getImageBannerList(): ArrayList<ArrayList<CourseBannerData>> {
        val imageBannerList: ArrayList<ArrayList<CourseBannerData>> = ArrayList()
        imageBannerList.add(viewModel.bannersArrayList)
        return imageBannerList
    }

    private fun getCourseAsPerSearchList(position: Int): ArrayList<ArrayList<Course>> {
        val imageBannerList: ArrayList<ArrayList<Course>> = ArrayList()
        imageBannerList.add(getCourseListByIndex(position))
        return imageBannerList
    }
    //endregion

    //Horizontal listing; data received from an API
    private fun recentlyViewedCourseView(holder: RecentlyCoursesViewHolder) {
        holder.recentlyCourseBinding.courseCategories.text =
            context.getString(R.string.rc_title_recently_viewed)
        if (SharedPreference.getInstance().getString(Const.RECENT_COURSE_ID).isEmpty())
            holder.recentlyCourseBinding.root.visibility = View.GONE
        else
            getRecentlyViewedCourses(holder)
    }

    //Independent button views
    private fun buttonView(holder: ButtonViewHolder, position: Int) {
        when (holder.itemViewType) {
            Constants.RecordedCourseTemplates.LIVE_CLASSES -> {
                holder.buttonView.txtButtons.text = context.getString(R.string.live_classes)
            }
            Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN -> {
                holder.buttonView.txtButtons.text = context.getString(R.string.browse_combo_plan)
            }
            Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT -> {
                holder.buttonView.txtButtons.text = context.getString(R.string.classroom_reg)
            }
        }

        holder.buttonView.root.setOnClickListener {
            when (holder.itemViewType) {
                Constants.RecordedCourseTemplates.LIVE_CLASSES -> {
                    Navigation.findNavController(it).navigate(R.id.liveCoursesFragment)
                    //Objects.requireNonNull(context as HomeActivity).liveClassesLL.performClick()
                }
                Constants.RecordedCourseTemplates.BROWSE_COMBO_PLAN -> {
                    val intent = Intent(context, ComboCourseActivity::class.java)
                    context.startActivity(intent)
                }

                Constants.RecordedCourseTemplates.BOOK_YOUR_SEAT -> {
                    val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.REG_COURSE)
                    context.startActivity(intent)
                }
            }
        }
    }
    //endregion

    override fun getItemViewType(position: Int): Int {
        return viewModel.coursesDataArrayList.get(position).viewItemType
    }

    override fun getItemCount(): Int {
        return viewModel.coursesDataArrayList.size
    }

    //region All viewHolders
    inner class UpcomingClassViewHolder(val layoutUpcomingClassBinding: LayoutUpcomingClassBinding) :
        RecyclerView.ViewHolder(layoutUpcomingClassBinding.root)

    inner class VerticalViewHolder(val verticalListRecordedScreen: RecordedScreenVerticalBinding) :
        RecyclerView.ViewHolder(verticalListRecordedScreen.root)

    inner class HorizontalViewHolder(val horizontalListCourseBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(horizontalListCourseBinding.root)

    inner class TrendingCoursesViewHolder(val trendingRecordedScreen: RecordedScreenVerticalBinding) :
        RecyclerView.ViewHolder(trendingRecordedScreen.root)

    inner class RecentlyCoursesViewHolder(val recentlyCourseBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(recentlyCourseBinding.root)

    inner class CategoriesCoursesViewHolder(val categoriesCoursesBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(categoriesCoursesBinding.root)

    inner class CategoryTileViewHolder(val typeCourseBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(typeCourseBinding.root)

    inner class StudentViewingViewHolder(val studentViewingBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(studentViewingBinding.root)

    inner class BestSellerViewHolder(val bestsellerCoursesBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(bestsellerCoursesBinding.root)

    inner class DefaultViewHolder(defaultViewBinding: RecordedScreenDefaultBinding) :
        RecyclerView.ViewHolder(defaultViewBinding.root)

    inner class ImageViewHolder(val imageViewBinding: RecordedScreenVerticalBinding) :
        RecyclerView.ViewHolder(imageViewBinding.root)

    inner class CourseAsPerSearchViewHolder(val courseAsPerSearch: RecordedScreenVerticalBinding) :
        RecyclerView.ViewHolder(courseAsPerSearch.root)

    inner class ButtonViewHolder(val buttonView: RecordedScreenButtonsBinding) :
        RecyclerView.ViewHolder(buttonView.root)

    inner class UpcomingLiveClassesViewHolder(val upcomingLiveClassesBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(upcomingLiveClassesBinding.root)

    inner class MyLiveCoursesViewHolder(val myLiveCoursesBinding: RecordedScreenHorizontalBinding) :
        RecyclerView.ViewHolder(myLiveCoursesBinding.root)

    inner class UpcomingLectureTodayViewHolder(val upcomingLectureTodayBinding: RecordedScreenVerticalBinding) :
        RecyclerView.ViewHolder(upcomingLectureTodayBinding.root)


    //endregion

    private fun getRecentlyViewedCourses(holder: RecentlyCoursesViewHolder) {
        // asynchronous operation to get user recently viewed courses.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getLiveRecentlyViewedCourses(
            SharedPreference.getInstance().loggedInUser.id,
            getCourseId()!!
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val gson = Gson()

                    val arrCourseList: JSONArray?
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    val courseArrayList = java.util.ArrayList<Course>()
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            arrCourseList =
                                GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list")

                            for (j in 0 until arrCourseList.length()) {
                                val courseObject = arrCourseList.getJSONObject(j)
                                val course = gson.fromJson(
                                    Objects.requireNonNull(courseObject).toString(),
                                    Course::class.java
                                )

                                if (Helper.isMrpZero(courseObject)) {
                                    course.calMrp = "Free"
                                } else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                                    course.isIs_dams = true
                                    if (courseObject.optString("for_dams") == "0") {
                                        course.calMrp = "Free"
                                    } else {
                                        if (courseObject.optString("mrp") == courseObject.optString(
                                                "for_dams"
                                            )
                                        ) {
                                            course.calMrp = String.format(
                                                "%s %s", Helper.getCurrencySymbol(),
                                                Helper.calculatePriceBasedOnCurrency(
                                                    courseObject.optString(
                                                        "mrp"
                                                    )
                                                )
                                            )
                                        } else {
                                            course.isDiscounted = true
                                            course.calMrp =
                                                Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(
                                                    courseObject.optString("mrp")
                                                ) + " " +
                                                        Helper.calculatePriceBasedOnCurrency(
                                                            courseObject.optString("non_dams")
                                                        )
                                        }
                                    }
                                } else {
                                    course.isIs_dams = false
                                    if (courseObject.optString("non_dams") == "0") {
                                        course.calMrp = "Free"
                                    } else {
                                        if (courseObject.optString("mrp") == courseObject.optString(
                                                "non_dams"
                                            )
                                        ) {
                                            course.calMrp = String.format(
                                                "%s %s", Helper.getCurrencySymbol(),
                                                Helper.calculatePriceBasedOnCurrency(
                                                    courseObject.optString(
                                                        "mrp"
                                                    )
                                                )
                                            )
                                        } else {
                                            course.isDiscounted = true
                                            course.calMrp =
                                                Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(
                                                    courseObject.optString("mrp")
                                                ) + " " +
                                                        Helper.calculatePriceBasedOnCurrency(
                                                            courseObject.optString("non_dams")
                                                        )
                                        }
                                    }
                                }
                                if (course.calMrp.equals("free", true) || course.isIs_purchased)
                                    course.isFreeTrial = false
                                courseArrayList.add(course)
                            }

                            holder.recentlyCourseBinding.root.visibility = View.VISIBLE
                            val recordedCourseRecentlyAdapter = RecordedCourseRecentlyAdapter(
                                getCroppedCourseList(courseArrayList),
                                context,
                                Const.LIVE_CLASSES
                            )
                            holder.recentlyCourseBinding.innerRecyclerViewHorizontal.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            holder.recentlyCourseBinding.innerRecyclerViewHorizontal.adapter =
                                recordedCourseRecentlyAdapter
                            holder.recentlyCourseBinding.courseCategories.text =
                                context.getString(R.string.rc_title_recently_viewed)

                            if (courseArrayList.size > 5) {
                                holder.recentlyCourseBinding.showAllHorizontalLink.visibility =
                                    View.VISIBLE
                            } else {
                                holder.recentlyCourseBinding.showAllHorizontalLink.visibility =
                                    View.GONE
                            }

                            holder.recentlyCourseBinding.showAllHorizontalLink.tag =
                                "recently_viewed"
                            holder.recentlyCourseBinding.showAllHorizontalLink.setOnClickListener {
                                redirectToSeeAll(courseArrayList)
                            }
                        } else {
                            holder.recentlyCourseBinding.root.visibility = View.GONE
                        }

                    } catch (e: JSONException) {
                        holder.recentlyCourseBinding.root.visibility = View.GONE
                        e.printStackTrace()
                    }
                } else
                    holder.recentlyCourseBinding.root.visibility = View.GONE
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                holder.recentlyCourseBinding.root.visibility = View.GONE
            }
        })
    }

    private fun getCourseId(): String? {
        var s = SharedPreference.getInstance().getString(Const.RECENT_COURSE_ID)
        return s.subSequence(0, s.lastIndexOf(",")).trim().toString()
    }

    private fun getCroppedCourseList(list: java.util.ArrayList<Course>): java.util.ArrayList<Course> {
        return if (list.size > 5)
            ArrayList(list.subList(0, 5))
        else
            list
    }


}