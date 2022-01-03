package com.emedicoz.app.liveCourses.model

import androidx.lifecycle.ViewModel
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.UpcomingCourseData
import com.emedicoz.app.modelo.courses.CourseCatTileData
import com.emedicoz.app.modelo.courses.CourseCategory
import com.emedicoz.app.modelo.courses.CoursesData
import com.emedicoz.app.modelo.courses.PositionOrder

const val TAG: String = "LiveCoursesViewModel"

class LiveCoursesViewModel : ViewModel() {

    internal var positionOrder = PositionOrder()

    internal var coursesDataArrayList: ArrayList<CoursesData> = ArrayList()

    internal var categoryArrayList: ArrayList<CourseCategory> = ArrayList()

    internal var tilesArrayList: ArrayList<CourseCatTileData> = ArrayList()

    internal var bannersArrayList: ArrayList<CourseBannerData> = ArrayList()

    internal var upcomingArrayList: ArrayList<UpcomingCourseData> = ArrayList()

    internal var onGoingArrayList: ArrayList<UpcomingCourseData> = ArrayList()

}