package com.emedicoz.app.login.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.emedicoz.app.R
import com.emedicoz.app.login.activity.SliderActivity

class ViewPagerAdapter(val context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null

    //array of image
    val imagArray = arrayOf(R.drawable.ic__98_layers_cropped, R.drawable.banner_01, R.drawable.ic_banner_2, R.drawable.ic__36_layers_cropped)
    val headerArray = arrayOf("Interactive Live Classes\n", "Get Comprehensive Study\n" +
            "Material", "Test Series\n", "Discuss Doubts To Better\n" +
            "Understand")
    val subTitleArrary = arrayOf(
        "Learn and Chat with expert Faculties\n in an Interactive Live Classes",
        "Study material prepared by our excellent\n faculties.",
        "Boost your exam preparation with 1000+ tests\n" +
                "& get in depth analysis",
        "Discuss and clear your doubts to avoid\n" +
                "confusion and make learning easy "
    )

    override fun getCount(): Int {
        return imagArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slider, container, false)
        val image = view.findViewById<ImageView>(R.id.ivBanner)
        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
        val tvHeader2 = view.findViewById<TextView>(R.id.tvHeader2)
        val tvHelp = view.findViewById<TextView>(R.id.tvHelp)
        image.setImageResource(imagArray[position])
        tvHeader.text = headerArray[position]
        tvHeader2.text = subTitleArrary[position]
        tvHelp.setOnClickListener {
            (context as SliderActivity).initHelpAndSupport()
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}