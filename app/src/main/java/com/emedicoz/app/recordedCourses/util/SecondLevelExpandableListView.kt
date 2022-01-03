package com.emedicoz.app.recordedCourses.util

import android.content.Context
import android.widget.ExpandableListView

class SecondLevelExpandableListView(context: Context?) : ExpandableListView(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
        var heightMeasureSpec = heightMeasureSpec
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}