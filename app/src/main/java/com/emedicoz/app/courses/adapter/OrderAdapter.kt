package com.emedicoz.app.courses.adapter

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.databinding.SingleItemOrderBinding
import com.emedicoz.app.databinding.ViewDetail1Binding
import com.emedicoz.app.modelo.testseries.OrderHistoryData
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.Helper
import java.util.*

class OrderAdapter(var activity: Activity, var orderData: ArrayList<OrderHistoryData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding = SingleItemOrderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OrderViewHolder(binding)
            }
            1 -> {
                val binding = ViewDetail1Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OrderViewHolder2(binding)
            }
        }

        val binding = SingleItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 ->
                setOrderListData(holder as OrderViewHolder, position)
            1 ->
                setOrderHolder2Data(holder as OrderViewHolder2, position)
        }
    }

    private fun setOrderHolder2Data(holder: OrderViewHolder2, position: Int) {
        holder.viewDetail1Binding.llPaymentType.visibility = View.GONE
        holder.viewDetail1Binding.tvViewDetail.visibility = View.VISIBLE
        val orderHistoryData = orderData[position]
        Glide.with(activity).load(orderHistoryData.cover_image)
            .apply(RequestOptions().error(R.drawable.dams)).into(
                holder.viewDetail1Binding.courseDetails.ivCoverImage
            )
        holder.viewDetail1Binding.courseDetails.tvTitle.text = orderHistoryData.title
        holder.viewDetail1Binding.courseDetails.tvPrice.text = String.format(
            "%s %s %s",
            Helper.getCurrencySymbol(),
            orderHistoryData.course_price,
            "/-"
        )
        if (!TextUtils.isEmpty(orderHistoryData.gst)) {
            holder.viewDetail1Binding.courseDetails.tvCutPrice.text =
                "(" + Helper.getCurrencySymbol() + " " + orderHistoryData.net_amt + " + " + Helper.getCurrencySymbol() + " " + orderHistoryData.gst + " (GST))"
        } else {
            holder.viewDetail1Binding.courseDetails.tvCutPrice.text = ""
        }
        if (orderHistoryData.is_validity == "1" && !TextUtils.isEmpty(orderHistoryData.validity)) {
            holder.viewDetail1Binding.courseDetails.tvValidity.visibility = View.VISIBLE
            holder.viewDetail1Binding.courseDetails.tvValidity.text =
                Helper.getDate(orderHistoryData.validity.toLong())
        } else {
            holder.viewDetail1Binding.courseDetails.tvValidity.visibility = View.INVISIBLE
        }
        if (!TextUtils.isEmpty(orderHistoryData.upcoming_emi_date)) {
            holder.viewDetail1Binding.labelDueDate.visibility = View.VISIBLE
            holder.viewDetail1Binding.tvDueDate.text =
                Helper.getDate(orderHistoryData.upcoming_emi_date.toLong())
        } else {
            holder.viewDetail1Binding.labelDueDate.visibility = View.GONE
            holder.viewDetail1Binding.tvDueDate.text = ""
        }
        holder.viewDetail1Binding.tvViewDetail.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity, CourseActivity::class.java
                )
                    .putExtra(Const.FRAG_TYPE, Const.VIEW_DETAILS)
                    .putExtra(Const.ORDER_DETAIL, orderData[position])
            )
        }
    }

    private fun setOrderListData(holder: OrderViewHolder, position: Int) {
        if (!GenericUtils.isEmpty(orderData[position].title)) holder.singleItemOrderBinding.testOrderName.text =
            orderData[position].title
        if (!GenericUtils.isEmpty(orderData[position].cover_image)) Glide.with(activity).load(
            orderData[position].cover_image
        ).into(holder.singleItemOrderBinding.orderIV)
        if (orderData[position].net_amt != null) {
            holder.singleItemOrderBinding.orderTotalPrice.text =
                activity.resources.getString(R.string.Rs) + " " + orderData[position].net_amt
        }
        if (!GenericUtils.isEmpty(orderData[position].transaction_status)) {
            when {
                orderData[position].transaction_status.equals("0", ignoreCase = true) -> {
                    holder.singleItemOrderBinding.orderStatus.text = Constants.OrderStatus.PENDING
                    holder.singleItemOrderBinding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            activity, R.color.order_pending
                        )
                    )
                }
                orderData[position].transaction_status.equals("1", ignoreCase = true) -> {
                    holder.singleItemOrderBinding.orderStatus.text =
                        Constants.OrderStatus.SUCCESSFUL
                    holder.singleItemOrderBinding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            activity, R.color.order_successful
                        )
                    )
                }
                orderData[position].transaction_status.equals("2", ignoreCase = true) -> {
                    holder.singleItemOrderBinding.orderStatus.text = Constants.OrderStatus.FAILED
                    holder.singleItemOrderBinding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            activity, R.color.order_failed
                        )
                    )
                }
                orderData[position].transaction_status.equals("3", ignoreCase = true) -> {
                    holder.singleItemOrderBinding.orderStatus.text =
                        Constants.OrderStatus.REFUND_REQUEST
                    holder.singleItemOrderBinding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            activity, R.color.colorGray2
                        )
                    )
                }
                else -> {
                    holder.singleItemOrderBinding.orderStatus.text = Constants.OrderStatus.REFUNDED
                    holder.singleItemOrderBinding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            activity, R.color.colorGray2
                        )
                    )
                }
            }
        }
        if (!GenericUtils.isEmpty(orderData[position].creation_time)) holder.singleItemOrderBinding.orderCreationTime.text =
            orderData[position].creation_time
        if (!GenericUtils.isEmpty(orderData[position].coupon_applied)) holder.singleItemOrderBinding.orderCoupanTV.text =
            orderData[position].coupon_applied
        if (!GenericUtils.isEmpty(orderData[position].course_learner)) holder.singleItemOrderBinding.learnerCount.text =
            "learner: " + orderData[position].course_learner
        if (!GenericUtils.isEmpty(orderData[position].course_price)) holder.singleItemOrderBinding.orderPriceTV.text =
            activity.resources.getString(
                R.string.Rs
            ) + " " + orderData[position].net_amt
        if (orderData[position].gst != null) {
            holder.singleItemOrderBinding.orderGSTTV.text =
                activity.resources.getString(R.string.Rs) + " " + orderData[position].gst
            val total =
                orderData[position].net_amt.toDouble() + orderData[position].gst.toDouble()
            holder.singleItemOrderBinding.orderTotalTV.text =
                activity.resources.getString(R.string.Rs) + " " + total
            holder.singleItemOrderBinding.orderGrandTotalTV.text =
                activity.resources.getString(R.string.Rs) + " " + total
        }
        holder.singleItemOrderBinding.invoiceDetailDown.setOnClickListener {
            holder.singleItemOrderBinding.invoiceDetailDown.visibility = View.GONE
            holder.singleItemOrderBinding.invoiceDetailUp.visibility = View.VISIBLE
            holder.singleItemOrderBinding.invoiceDetail.visibility = View.VISIBLE
        }
        holder.singleItemOrderBinding.invoiceDetailUp.setOnClickListener {
            holder.singleItemOrderBinding.invoiceDetailDown.visibility = View.VISIBLE
            holder.singleItemOrderBinding.invoiceDetailUp.visibility = View.GONE
            holder.singleItemOrderBinding.invoiceDetail.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return orderData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (!TextUtils.isEmpty(orderData[position].payment_mode)) if (orderData[position].payment_mode == "1"
        ) 1 else 0 else 0
    }

    inner class OrderViewHolder(val singleItemOrderBinding: SingleItemOrderBinding) :
        RecyclerView.ViewHolder(singleItemOrderBinding.root)

    inner class OrderViewHolder2(val viewDetail1Binding: ViewDetail1Binding) :
        RecyclerView.ViewHolder(viewDetail1Binding.root)
}