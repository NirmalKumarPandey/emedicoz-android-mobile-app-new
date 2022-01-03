package com.emedicoz.app.newsandarticle.models

import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.databinding.NewsArticleRowBinding
import com.emedicoz.app.newsandarticle.Activity.NewAndArticalDetailActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class LatestNewsListRecycleViewAdapter(private val context: Context, var mNewsList: List<NewsDetail.Article>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = NewsArticleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsArticleRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NewsArticleRowViewHolder).mNewsArticleRowBinding.newstitle.text = mNewsList.get(position).title
        (holder).mNewsArticleRowBinding.createdBy.text = "By : " + mNewsList.get(position).createdBy
        //  (holder ).mNewsArticleRowBinding.creationDate.text = mNewsList.get(position).creationDateval
        (holder).mNewsArticleRowBinding.creationDate.text = getDate((mNewsList.get(position).creationDateval).toLong(), "dd-MM-yyyy HH:mm a").toString()
        //dd MMM, yyyy  |  hh:mm a
        (holder).mNewsArticleRowBinding.noviews.text = mNewsList.get(position).views+" "+if ( mNewsList.get(position).views == "0" || mNewsList.get(position).views == "1") "view" else "views"
        // Picasso.with(context).load(mNewsList.get(position).image).into((holder).mNewsArticleRowBinding.image)
        Glide.with(context)
                .load(mNewsList.get(position).image)
                .apply(RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                .into(holder.mNewsArticleRowBinding.image)

        (holder).mNewsArticleRowBinding.profileLayout.setOnClickListener(
                View.OnClickListener {
                    val intent = Intent(context, NewAndArticalDetailActivity::class.java)
                    intent.putExtra("articleId", mNewsList.get(position).articleId)
                    context.startActivity(intent)

                }
        )


/*
               (holder ).mAddresslistRowBinding.addressSelect.setOnClickListener(
                View.OnClickListener {

                    Toast.makeText(context, "Adrres Select Pos "+position, Toast.LENGTH_SHORT).show()
                    (context as AddressList).returnAddress(addresslist.get(position), position)
                }
            )
*/


    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val date = Date(milliSeconds)
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        return formatter.format(date)
    }

    override fun getItemCount(): Int {
        return mNewsList.size;
    }

    inner class NewsArticleRowViewHolder(val mNewsArticleRowBinding: NewsArticleRowBinding)
        : RecyclerView.ViewHolder(mNewsArticleRowBinding.root)

}