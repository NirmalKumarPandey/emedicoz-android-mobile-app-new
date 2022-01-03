package com.emedicoz.app.newsandarticle.fragment

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.emedicoz.app.R

class NewsTabsPagerAdapter(context: Context?, fm: FragmentManager?): FragmentPagerAdapter(fm!!) {

    @StringRes
    private val TAB_TITLES =
        intArrayOf(R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3)
    private var mContext: Context? = null



    override fun getItem(position: Int): Fragment {
        //return NewsHomeFragment.getInstance("latest_news")
        when (position) {
            0 -> {
                return NewsHomeFragment.getInstance("latest")
            }
            1 -> {
                return TrendingFragment.getInstance("trending")
            }

            2 -> {
                return FeaturedFragment.getInstance("featured")
            }
            3 -> {
                return MostViewedFragment.getInstance("viewed")
            }
            4 -> {
                return MostLikedFragment.getInstance("liked")
            }
            else -> {
                return NewsHomeFragment.getInstance("latest")
            }

        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //   return mContext!!.resources.getString(TAB_TITLES[position])
        return "Latest"
    }

    override fun getCount(): Int {
        return 6
    }
}