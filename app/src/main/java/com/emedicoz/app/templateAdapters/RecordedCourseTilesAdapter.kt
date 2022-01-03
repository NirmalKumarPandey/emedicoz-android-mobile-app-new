package com.emedicoz.app.templateAdapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.courses.activity.MyCoursesActivity
import com.emedicoz.app.dailychallenge.activity.DailyChallengeActivity
import com.emedicoz.app.databinding.RecordCoursesListItemTypeBinding
import com.emedicoz.app.modelo.courses.CourseCatTileData
import com.emedicoz.app.mycourses.MyCourseActivity
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import java.util.*

class RecordedCourseTilesAdapter(private val courseTypeList: ArrayList<CourseCatTileData>, private val context: Context) : RecyclerView.Adapter<RecordedCourseTilesAdapter.RecordCoursesListItemCategoriesViewHolder>() {


    override fun onBindViewHolder(holder: RecordCoursesListItemCategoriesViewHolder, position: Int) {
        val currentCourseItem: CourseCatTileData = courseTypeList[position]

        try {
            holder.binding.root.tag = currentCourseItem
            holder.binding.courseTypeTv.text = currentCourseItem.title
        } catch (e: Exception) {

        }
        holder.binding.root.setOnClickListener {
            val curItem = it.tag as CourseCatTileData

            when (curItem.identifier) {
                "test_series" -> {
                    //Navigation.findNavController(it).navigate(R.id.testSeriesFragment)
                    redirectToScreen(curItem)
                }
                "q_bank" -> {
                    redirectToScreen(curItem)
                }
                "my_courses" -> {
                    val intent = Intent(context, MyCourseActivity::class.java)
                    context.startActivity(intent)
                    //redirectCourseActivity(Const.MYCOURSES)
//                    Objects.requireNonNull(context as BaseABNavActivity).myCourses.performClick()
                }
                "podcast" -> {
                    redirectCourseActivity(Const.PODCAST)
//                    Objects.requireNonNull(context as BaseABNavActivity).podcastNav.performClick()
                }
                "daily_challenge" -> {
                    val intent = Intent(context, DailyChallengeActivity::class.java)
                    context.startActivity(intent)
                    //redirectCourseActivity(Const.DAILY_QUIZ)
//                    Objects.requireNonNull(context as BaseABNavActivity).dailyQuizNav.performClick()
                }
                "crs" -> {
                    val bundle = Bundle()
                    //bundle.putString(Const.Cpr, Constants.StudyType.CRS)
                    //bundle.putString(Const.FRAG_TYPE, Const.QBANK)
                    bundle.putString(Const.FRAG_TYPE, Constants.StudyType.CRS)
                    Navigation.findNavController(it).navigate(R.id.studyFragment, bundle)
                   //Helper.GoToStudySection(context, Constants.StudyType.CRS)

                    //redirectCourseActivity(Constants.StudyType.CRS)
//                    Objects.requireNonNull(context as BaseABNavActivity).qbankLLCRS.performClick()
                }
            }
        }
    }

    private fun redirectCourseActivity(fragType: String) {
        //val courseList = Intent(context, CourseActivity::class.java)
        val courseList = Intent(context, CourseActivity::class.java) //FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
        courseList.putExtra(Const.FRAG_TYPE, fragType)
        context.startActivity(courseList)
    }


    private fun redirectToScreen(curItem: CourseCatTileData) {

        val courseList = Intent(context, CourseActivity::class.java) //FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
        courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE)
        courseList.putExtra(Const.CATEGORY_ID, curItem.appcategory)
        courseList.putExtra(Const.TITLE, curItem.title)
        context.startActivity(courseList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordCoursesListItemCategoriesViewHolder {
        val binding = RecordCoursesListItemTypeBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordCoursesListItemCategoriesViewHolder(binding)
    }
            //inner class
    inner class RecordCoursesListItemCategoriesViewHolder(val binding: RecordCoursesListItemTypeBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = courseTypeList.size


}
