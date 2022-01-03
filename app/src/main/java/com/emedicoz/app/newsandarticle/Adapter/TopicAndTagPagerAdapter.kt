package com.emedicoz.app.newsandarticle.Adapter

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.emedicoz.app.R
import com.emedicoz.app.newsandarticle.Activity.Fragment.TopicTagHomeFragment
import com.emedicoz.app.newsandarticle.fragment.NewsHomeFragment

class TopicAndTagPagerAdapter(context: Context?, fm: FragmentManager?): FragmentPagerAdapter(fm!!) {

    @StringRes
    private val TAB_TITLES = intArrayOf(R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3)
    private var mContext: Context? = null

    override fun getItem(position: Int): Fragment {
        //return TopicTagHomeFragment.getInstance("subject")
        when (position) {
            0 -> {
                // return TopicTagHomeFragment.getInstance("subject")

            }

            1 -> {
                // return TopicTagHomeFragment.getInstance("topics")
            }

            2 -> {
                // return TopicTagHomeFragment.getInstance("all_tag")
            }

            3 -> {
                // return TopicTagHomeFragment.getInstance("followed_tag")
            }
            else -> {

                // return TopicTagHomeFragment.getInstance("trending")
                return TopicTagHomeFragment()
            }

        }
        return TopicTagHomeFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //   return mContext!!.resources.getString(TAB_TITLES[position])
        return "Latest"
    }

    override fun getCount(): Int {
        return 6
    }
}