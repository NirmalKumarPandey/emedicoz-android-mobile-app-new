package com.emedicoz.app.recordedCourses.model

import androidx.lifecycle.ViewModel
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.courses.CourseCatTileData
import com.emedicoz.app.modelo.courses.CourseCategory
import com.emedicoz.app.modelo.courses.CoursesData
import com.emedicoz.app.modelo.courses.PositionOrder
import org.json.JSONArray
import org.json.JSONObject

const val TAG: String = "RecordedCoursesViewModel"

class RecordedCoursesViewModel : ViewModel() {

    internal var positionOrder = PositionOrder()

    internal var coursesDataArrayList: java.util.ArrayList<CoursesData> = java.util.ArrayList()
    public var orignalDataArrayList: java.util.ArrayList<CoursesData> = java.util.ArrayList()

    internal var categoryArrayList: java.util.ArrayList<CourseCategory> = java.util.ArrayList()

    internal var tilesArrayList: java.util.ArrayList<CourseCatTileData> = java.util.ArrayList()

    internal var bannersArrayList: java.util.ArrayList<CourseBannerData> = java.util.ArrayList()

}

private fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    for (i in 0 until length()) {
        action(optJSONObject(i))
    }
}


