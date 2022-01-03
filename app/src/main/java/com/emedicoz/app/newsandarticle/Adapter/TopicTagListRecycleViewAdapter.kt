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
import com.emedicoz.app.newsandarticle.Activity.TopicAndTagDetailListActivity
import com.emedicoz.app.newsandarticle.models.TopicDetail
import com.emedicoz.app.utilso.SharedPreference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class TopicTagListRecycleViewAdapter(private val context: Context, var mTopicList: List<TopicDetail.Topic>, var contentType: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = TopicTagRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicTagRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (contentType.equals("subject")) {

            if (SharedPreference.getInstance().getString("searchType").equals("searchSubject")) {
                (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).subject
                (holder).mTopicTagRowBinding.lv1.setOnClickListener {
                    val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
                    intent.putExtra("type", SharedPreference.getInstance().getString("type"))
                    intent.putExtra("id", mTopicList.get(position).id)
                    // intent.putExtra("title", mList.data.get(temPosition).t)
                    SharedPreference.getInstance().putString("titleName", mTopicList.get(position).subject)
                    context.startActivity(intent)
                }

                if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
                    (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " article"
                } else
                    (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"


            } else {
                (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).name
               // SharedPreference.getInstance().putString("titleName", mTopicList.get(position).name)
                (holder).mTopicTagRowBinding.lv1.setOnClickListener {
                    val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
                    intent.putExtra("type", SharedPreference.getInstance().getString("type"))
                    intent.putExtra("id", mTopicList.get(position).id)
                    SharedPreference.getInstance().putString("titleName", mTopicList.get(position).name)
                    context.startActivity(intent)
                }
                if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
                    (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " article"
                } else
                    (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"
            }


        } else if (contentType.equals("topics")) {
          //  (holder as TopicTagListNewRecycleViewAdapter.TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).topic
            (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).topic

            (holder).mTopicTagRowBinding.lv1.setOnClickListener {
                val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
                intent.putExtra("type", SharedPreference.getInstance().getString("type"))
                intent.putExtra("id", mTopicList.get(position).id)
                // intent.putExtra("title", mList.data.get(temPosition).t)
                SharedPreference.getInstance().putString("titleName", mTopicList.get(position).topic)
                context.startActivity(intent)

            }
            if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " article"
            } else
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"



        } else if (contentType.equals("all_tag")) {
            (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).text
           // SharedPreference.getInstance().putString("titleName", mTopicList.get(position).text)


            (holder).mTopicTagRowBinding.lv1.setOnClickListener {

                val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
                intent.putExtra("type", SharedPreference.getInstance().getString("type"))
                intent.putExtra("id", mTopicList.get(position).id)
                // intent.putExtra("title", mList.data.get(temPosition).t)
                SharedPreference.getInstance().putString("titleName", mTopicList.get(position).text)
                context.startActivity(intent)

            }

            if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " article"
            } else
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"


        } else if (contentType.equals("followed_tag")) {
            (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).text

          //  SharedPreference.getInstance().putString("titleName", mTopicList.get(position).text)
            (holder).mTopicTagRowBinding.lv1.setOnClickListener {

                val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
                intent.putExtra("type", SharedPreference.getInstance().getString("type"))
                intent.putExtra("id", mTopicList.get(position).id)
                // intent.putExtra("title", mList.data.get(temPosition).t)
                SharedPreference.getInstance().putString("titleName", mTopicList.get(position).text)
                context.startActivity(intent)

            }

            if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " article"
            } else
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"


        }


        else if (contentType.equals("category")) {
            (holder as TopicTagRowViewHolder).mTopicTagRowBinding.newstitle.text = mTopicList.get(position).text

            //  SharedPreference.getInstance().putString("titleName", mTopicList.get(position).text)
            (holder).mTopicTagRowBinding.lv1.setOnClickListener {

                val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
                intent.putExtra("type", SharedPreference.getInstance().getString("type"))
                intent.putExtra("id", mTopicList.get(position).id)
                // intent.putExtra("title", mList.data.get(temPosition).t)
                SharedPreference.getInstance().putString("titleName", mTopicList.get(position).text)
                context.startActivity(intent)
            }
            if (mTopicList.get(position).count.equals("1") || mTopicList.get(position).count.equals("0")) {
                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + "article"
            } else

                (holder).mTopicTagRowBinding.countTv.text = mTopicList.get(position).count + " articles"

        }

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