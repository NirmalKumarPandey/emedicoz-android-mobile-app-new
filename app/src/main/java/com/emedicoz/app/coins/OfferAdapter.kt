package com.emedicoz.app.coins
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R

class OfferAdapter(private val context: Context) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.big_offer_item_view,parent,false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
    override fun getItemCount(): Int {
       return 2
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView){
        init {

        }
    }

}