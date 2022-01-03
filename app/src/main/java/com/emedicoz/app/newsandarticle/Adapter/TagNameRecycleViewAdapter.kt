package com.emedicoz.app.newsandarticle.Adapter

import android.app.Fragment
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentTopicTagHomeBinding
import com.emedicoz.app.databinding.NewsArticleRowBinding
import com.emedicoz.app.databinding.TagNameLayoutBinding
import com.emedicoz.app.databinding.TopicTagRowBinding
import com.emedicoz.app.newsandarticle.models.Tag
import com.emedicoz.app.newsandarticle.models.TopicDetail
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class TagNameRecycleViewAdapter(private val context: Context, var mTopicList: List<Tag>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding =TagNameLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicTagRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as TopicTagRowViewHolder).mTopicTagRowBinding.tagNameTv.text = mTopicList.get(position).text

     /*  holder.mTopicTagRowBinding.cardView.setOnClickListener(
                View.OnClickListener {
                }
        )*/
    }
    override fun getItemCount(): Int {
        return mTopicList.size;
    }

    inner class TopicTagRowViewHolder(val mTopicTagRowBinding: TagNameLayoutBinding)
        : RecyclerView.ViewHolder(mTopicTagRowBinding.root)

}