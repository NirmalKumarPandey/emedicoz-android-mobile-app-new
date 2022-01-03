package com.emedicoz.app.polls.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.emedicoz.app.courses.PollOptionSelectedInterface
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.PollsOptionItemBinding
import com.emedicoz.app.polls.adapter.PollsOptionsAdapter.PollsViewHolder
import com.emedicoz.app.polls.model.Options
import java.util.ArrayList

class PollsOptionsAdapter(
    private var pollsOptionsList: ArrayList<Options>,
    private var selectedOptionId: String,
    private var correctOptionId: String,
    private val context: Context,
    private val pollOptionSelectedInterface: PollOptionSelectedInterface
) : RecyclerView.Adapter<PollsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollsViewHolder {
        val binding = PollsOptionItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PollsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollsViewHolder, position: Int) {
        holder.binding.optionText.text = pollsOptionsList[position].title
        holder.binding.optionComment.text = pollsOptionsList[position].desc
        if (pollsOptionsList[position].desc == null || pollsOptionsList[position].desc == "") {
            holder.binding.optionComment.visibility = View.VISIBLE
        } else {
            holder.binding.optionComment.visibility = View.VISIBLE
        }

        // code for correct answer background
        if (selectedOptionId != "-1" && correctOptionId == pollsOptionsList[position].optionId) {
            holder.binding.optionText.setTextColor(context.resources.getColor(R.color.polls_option_text_highlighted))
            holder.binding.optionText.background =
                context.resources.getDrawable(R.drawable.polls_option_correct_background)
        }

        // code for incorrect answer background
        if (selectedOptionId != "-1" && selectedOptionId != correctOptionId && selectedOptionId == pollsOptionsList[position].optionId) {
            holder.binding.optionText.setTextColor(context.resources.getColor(R.color.polls_option_text_highlighted))
            holder.binding.optionText.background =
                context.resources.getDrawable(R.drawable.polls_option_incorrect_background)
        }
        holder.binding.optionText.setOnClickListener {
            pollOptionSelectedInterface.pollOptionSelectedPosition(
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return pollsOptionsList.size
    }

    inner class PollsViewHolder(val binding: PollsOptionItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun notifyChange(
        pollsOptionsList: ArrayList<Options>,
        selectedOptionId: String,
        correctOptionId: String
    ) {
        this.pollsOptionsList = pollsOptionsList
        this.selectedOptionId = selectedOptionId
        this.correctOptionId = correctOptionId
        notifyDataSetChanged()
    }
}