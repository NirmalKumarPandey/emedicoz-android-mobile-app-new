package com.emedicoz.app.helpdesk
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.QuestionsAnswerItemBinding


class QuestionsAdapter(private val context: Context) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuestionsAnswerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
      //  return ViewHolder(LayoutInflater.from(context).inflate(R.layout.questions_answer_item, parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hit: Int =0
        holder.binding.imageExpanded.setOnClickListener(View.OnClickListener {

            holder.binding.descriptionLayout.visibility = View.VISIBLE
            Toast.makeText(context,"this is toast message",Toast.LENGTH_SHORT).show()
        })

    }
    override fun getItemCount(): Int {
        return 5
    }
    inner class ViewHolder(val binding: QuestionsAnswerItemBinding) :RecyclerView.ViewHolder(binding.root) {

    }
}