package com.emedicoz.app.templateAdapters

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.HelpAndSupportQuestionBinding
import com.emedicoz.app.modelo.HelpAndSupport

class HelpAndSupportAdapter(private val helpList: ArrayList<HelpAndSupport>, private val context: Context)
    : RecyclerView.Adapter<HelpAndSupportAdapter.HelpAndSupportViewHolder>() {

    override fun onBindViewHolder(holder: HelpAndSupportViewHolder, position: Int) {
        val currentItem: HelpAndSupport = helpList[position]

        try {
            holder.binding.apply {

                question.text = HtmlCompat.fromHtml(currentItem.question, 0)
                ans.text = currentItem.answer.toHtml()

                questionDescription.setOnClickListener {
                    if (descriptionLayout.visibility == View.VISIBLE) {
                        descriptionLayout.visibility = View.GONE
                        imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    } else {
                        descriptionLayout.visibility = View.VISIBLE
                        imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    }
                }

            }
        } catch (e: Exception) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpAndSupportViewHolder {
        val binding = HelpAndSupportQuestionBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return HelpAndSupportViewHolder(binding)
    }

    inner class HelpAndSupportViewHolder(val binding: HelpAndSupportQuestionBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = helpList.size


    fun String?.toHtml(): Spanned? {
        if (this.isNullOrEmpty()) return null
        return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}