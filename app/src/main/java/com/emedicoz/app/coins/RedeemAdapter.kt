package com.emedicoz.app.coins
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R

class RedeemAdapter(private val context: Context) : RecyclerView.Adapter<RedeemAdapter.Viewholder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(LayoutInflater.from(context).inflate(R.layout.redeem_item,parent,false))
    }
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
    }
    override fun getItemCount(): Int {
        return 2
    }

    class Viewholder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView){
        lateinit var txtDiscount: TextView
        lateinit var imgRedeemItem: ImageView
        lateinit var crdRedeemNow: CardView
        init {
            txtDiscount = itemLayoutView.findViewById(R.id.txtDiscount)
            imgRedeemItem = itemLayoutView.findViewById(R.id.imgRedeemItem)
            crdRedeemNow = itemLayoutView.findViewById(R.id.crdRedeemNow)
        }
    }
}