package com.emedicoz.app.mycourses

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emedicoz.app.courseDetail.adapter.CoursesDetailScreenSlideAdapter

class MyCoursesFragmentAdapter(fa: Fragment) : FragmentStateAdapter(fa) {

    private val fragmentList = mutableListOf<Fragment>()
    private val fragmentTitleList = mutableListOf<String>()

    override fun createFragment(position: Int): Fragment {
        Log.d(CoursesDetailScreenSlideAdapter.TAG, "createFragment: $position  method Called")
        return getItem(position)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    fun getItem(i: Int): Fragment { // i is position in here
        return fragmentList[i]
    }

    override fun getItemCount() = fragmentList.size

    // gets the Fragment Name
    fun getItemTitle(i: Int): String {
        return fragmentTitleList[i]
    }
}
