package com.emedicoz.app.reward.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.internal.Utils
import com.emedicoz.app.R
import com.emedicoz.app.reward.model.transmodel.Data
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val context: Context, private val data: List<Data>): RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.total_price_view_layout, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data.get(position)
        holder.txtTitleCourse.text = data.area
        holder.tvcreateDate.text = "Credit on"+getDate(data.creation_time, "dd/MM/yyyy")
        holder.tvPoint.text = "+"+data.reward

        //System.out.println("--------------------------"+getDate(data.creation_time, "dd/MM/yyyy"))
    }
    override fun getItemCount(): Int {
        return data.size

    }
    class ViewHolder(itemlayoutView: View) : RecyclerView.ViewHolder(itemlayoutView){
        val txtTitleCourse: TextView = itemlayoutView.findViewById(R.id.txtTitleCourse)
        val tvcreateDate: TextView = itemlayoutView.findViewById(R.id.tvcreateDate)
        val tvPoint: TextView = itemlayoutView.findViewById(R.id.tvPoint)

    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}