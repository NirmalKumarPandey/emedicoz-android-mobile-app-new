package com.emedicoz.app.courseDetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.CourseDetailRowItemModuleBinding
import com.emedicoz.app.databinding.SecondLevelListViewBinding
import com.emedicoz.app.recordedCourses.model.detaildatanotes.DataObject
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesList
import com.emedicoz.app.recordedCourses.model.detaildatavideo.ListDetailData
import com.emedicoz.app.recordedCourses.util.SecondLevelExpandableListView

class CourseNotesAdapter(private val context: Context, private val courseList: ArrayList<DataObject>, private val courseId: String)
    : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return courseList[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val binding = CourseDetailRowItemModuleBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root
        binding.moduleText.text = courseList[groupPosition].moduleName

        if (isExpanded) {
            binding.imgExpanded.setImageResource(R.drawable.ic_baseline_minimize_24)
        } else {
            binding.imgExpanded.setImageResource(R.drawable.ic_baseline_add_24)
        }

        val childArray: ArrayList<NotesList> = courseList[groupPosition].list

        if (childArray == null || childArray.size <= 0) {
            binding.imgExpanded.visibility = View.GONE
        }

        return view
    }

    // Need to update This accordingly
    override fun getChildrenCount(groupPosition: Int): Int {
        return 1 //courseList[groupPosition].list.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return courseList[groupPosition].list
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val binding = SecondLevelListViewBinding
            .inflate(LayoutInflater.from(parent!!.context), parent, false)
        val view = binding.root
        val childArray: ArrayList<NotesList> = courseList[groupPosition].list


        binding.expandSecond.apply {
            setAdapter(CourseNotesSubAdapter(context, childArray, courseId, courseList[groupPosition].id))
            setListViewHeight(this)
            setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long ->
                setListViewHeight(
                    parent!!, groupPosition
                )
                false
            })
            setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener {
                var previousGroup = -1
                override fun onGroupExpand(groupPosition: Int) {
                    if (groupPosition != previousGroup) {
                        collapseGroup(previousGroup)
                        previousGroup = groupPosition
                    }
                }
            })
        }
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun getGroupCount() = courseList.size

    private fun setListViewHeight(listView: ExpandableListView, group: Int) {
        val listAdapter = listView.expandableListAdapter
        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width,
            View.MeasureSpec.EXACTLY)
        for (i in 0 until listAdapter.groupCount) {
            val groupItem = listAdapter.getGroupView(i, false, null, listView)
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += groupItem.measuredHeight
            if (/*i == group*/ ((listView.isGroupExpanded(i)) && (i != group))
                || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (j in 0 until listAdapter.getChildrenCount(i)) {
                    val listItem = listAdapter.getChildView(i, j, false, null,
                        listView)
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                    totalHeight += listItem.measuredHeight
                }
            }
        }
        totalHeight += 100
        val params = listView.layoutParams
        var height = (totalHeight
                + listView.dividerHeight * (listAdapter.groupCount - 1))
        if (height < 10) height = 200
        params.height = height
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun setListViewHeight(listView: ExpandableListView) {
        val listAdapter = listView.expandableListAdapter
        var totalHeight = 0
        for (i in 0 until listAdapter.groupCount) {
            val groupView = listAdapter.getGroupView(i, true, null, listView)
            groupView.measure(0, View.MeasureSpec.UNSPECIFIED)
            totalHeight += groupView.measuredHeight
            if (listView.isGroupExpanded(i)) {
                for (j in 0 until listAdapter.getChildrenCount(i)) {
                    val listItem = listAdapter.getChildView(i, j, false, null, listView)
                    listItem.measure(0, View.MeasureSpec.UNSPECIFIED)
                    totalHeight += listItem.measuredHeight
                }
            }
        }
        val params = listView.layoutParams
        params.height = (totalHeight
                + listView.dividerHeight * (listAdapter.groupCount - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

}