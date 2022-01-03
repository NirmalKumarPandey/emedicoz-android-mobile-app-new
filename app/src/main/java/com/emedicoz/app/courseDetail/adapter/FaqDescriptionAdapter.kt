package com.emedicoz.app.courseDetail.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.emedicoz.app.R
import com.emedicoz.app.databinding.LayoutDescriptionFaqItemBinding
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ


class FaqDescriptionAdapter(private val faqList: List<DescriptionFAQ>, private var context: Context, private var viewPager: ViewPager2?, private var view: View)
    : RecyclerView.Adapter<FaqDescriptionAdapter.FaqDescriptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqDescriptionViewHolder {
        val binding = LayoutDescriptionFaqItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqDescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqDescriptionViewHolder, position: Int) {
        val currentDescriptionItem: DescriptionFAQ = faqList[position]

        try {

            holder.binding.apply {


                questionPosition.text = currentDescriptionItem.position
                question.text = currentDescriptionItem.question

                if(!currentDescriptionItem.isExpanded){
                    imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    descriptionLayout.visibility = View.GONE
                }else{
                    imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    descriptionLayout.visibility = View.VISIBLE
                }


                questionLayout.setOnClickListener {
                    if (descriptionLayout.visibility == View.GONE) {
                        imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                        descriptionLayout.visibility = View.VISIBLE
                        val height: Int = getLinearViewHeight(descriptionLayout)
                        for (element in faqList) {
                            element.isExpanded = false
                        }
                        viewPager?.let { it1 -> updatePagerHeightForChild(view, it1, height) }

                        currentDescriptionItem.isExpanded = true
                    }else {
                        val height: Int = getLinearViewHeight(descriptionLayout)
                        viewPager?.let { it1 -> updatePagerHeightForChild(view, it1, -height) }

                        currentDescriptionItem.isExpanded = false
                    }
                    notifyDataSetChanged()
                }
                description.text = currentDescriptionItem.description

            }


        } catch (e: Exception) {
        }

    }

    private fun getLinearViewHeight(layout: LinearLayout): Int {
        var height: Int = 0;
        val observer = layout.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            Log.d("Log", "Height: " + layout.height)
            height = layout.height
        }
        return height;
    }


    private fun updatePagerHeightForChild(view: View, pager: ViewPager2, height: Int) {
        view.post {
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    view.width, View.MeasureSpec.EXACTLY
            )
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    0, View.MeasureSpec.UNSPECIFIED
            )
            view.measure(widthMeasureSpec, heightMeasureSpec)
            if (pager.layoutParams.height != view.measuredHeight) {
                pager.layoutParams = (pager.layoutParams).also {
                    it.height = view.measuredHeight + height
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return if (faqList.size > 2) 2 else faqList.size
    }

    inner class FaqDescriptionViewHolder(val binding: LayoutDescriptionFaqItemBinding)
        : RecyclerView.ViewHolder(binding.root)

}










