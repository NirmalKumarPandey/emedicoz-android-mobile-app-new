package com.emedicoz.app.newsandarticle.Adapter

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentTopicTagHomeBinding
import com.emedicoz.app.databinding.NewsArticleRowBinding
import com.emedicoz.app.databinding.TopicTagRowBinding
import com.emedicoz.app.newsandarticle.Activity.BookmarkActivity
import com.emedicoz.app.newsandarticle.Activity.TopicAndTagActivity
import com.emedicoz.app.newsandarticle.models.TopicDetail
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class TopicTagListNewRecycleViewAdapter(private val context: Context, var mTopicList: List<TopicDetail.Topic>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = TopicTagRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicTagRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).name

        if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
            (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " article"
        } else
            (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"

       holder.mTopicTagRowBinding.cardView1.setOnClickListener(
                View.OnClickListener {



                  /*  val intent = Intent(context, BookmarkActivity::class.java)
                    intent.putExtra("")

                    context.startActivity(intent)*/



                }
        )


    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val date = Date(milliSeconds)
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

       return formatter.format(date)
    }

    override fun getItemCount(): Int {
        return mTopicList.size;
    }

    inner class TopicTagRowViewHolder(val mTopicTagRowBinding: TopicTagRowBinding)
        : RecyclerView.ViewHolder(mTopicTagRowBinding.root)

}