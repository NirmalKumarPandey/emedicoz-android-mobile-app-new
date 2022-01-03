package com.emedicoz.app.templateAdapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.adapter.InstallmentParentAdapter
import com.emedicoz.app.courses.callback.OnSubscriptionItemClickListener
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.RecordCoursesListItemTrendingBinding
import com.emedicoz.app.installment.model.Installment
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.modelo.courses.SingleCourseData
import com.emedicoz.app.modelo.liveclass.courses.DescriptionData
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.Const.COURSE
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RecordedCourseTrendingAdapter(
    private val trendingCourseList: ArrayList<Course>,
    private val context: Context,
    private val isWishlist: Boolean,
    private val isTrending: Boolean,
    private val fragType: String
) : RecyclerView.Adapter<RecordedCourseTrendingAdapter.RecordCoursesListItemTrendingViewHolder>() {
    private lateinit var progress: Progress
    private var selectedSubscriptionId = ""

    override fun onBindViewHolder(holder: RecordCoursesListItemTrendingViewHolder, position: Int) {
        val currentCourseItem: Course = trendingCourseList[position]
        try {
            holder.binding.detailCourse.tag = currentCourseItem
            holder.binding.detailCourse.setOnClickListener {
                var item = it.tag as Course
                System.out.println("item3333------------------------" + item.points_conversion_rate)
                if (Helper.isTestOrQbankCourse(item)) {
                    //Navigation.findNavController(it).navigate(R.id.studyFragment)
                    // Toast.makeText(context, "position", Toast.LENGTH_LONG).show()
/*                    val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)*/
                    if (item != null) {
                        if (item.course_type == "2") {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.TESTS)
                            context.startActivity(intent)
//                            val args = Bundle()
//                            args.putString(Const.FRAG_TYPE, Constants.StudyType.TESTS)
//                            (context as HomeActivity).navController.navigate(R.id.studyFragment, args)
                        } else if (item.course_type == "3") {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                            context.startActivity(intent)
//                            val args = Bundle()
//                            args.putString(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
//                            (context as HomeActivity).navController.navigate(R.id.studyFragment, args)
                        } else {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                            context.startActivity(intent)
//                            (context as HomeActivity).navController.navigate(R.id.studyFragment)
                        }
                    } else {
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                        context.startActivity(intent)
//                        (context as HomeActivity).navController.navigate(R.id.studyFragment)
                    }
                } else {
                    val intent = Intent(context, CourseDetailActivity::class.java)
                    intent.putExtra(COURSE, item)
                    intent.putExtra(Const.FRAG_TYPE, fragType)
                    context.startActivity(intent)
                }
            }

            Glide.with(context)
                .load(currentCourseItem.cover_image)
                .apply(
                    RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue)
                )
                .into(holder.binding.courseImage)

            if (isTrending) {
                holder.binding.tvCourseCategory.text = context.getString(R.string.trending)
                holder.binding.tvCourseCategory.visibility = View.VISIBLE
            } else {
                if (!GenericUtils.isEmpty(currentCourseItem.category_tag)) {
                    holder.binding.tvCourseCategory.text = currentCourseItem.category_tag
                    holder.binding.tvCourseCategory.visibility = View.VISIBLE
                } else {
                    holder.binding.tvCourseCategory.visibility = View.GONE
                    if (!GenericUtils.isEmpty(currentCourseItem.course_tag)) {
                        holder.binding.tvCourseCategory.text = currentCourseItem.course_tag
                        holder.binding.tvCourseCategory.visibility = View.VISIBLE
                    }
                }
            }

            setSpannable(holder.binding.coursePrice, currentCourseItem)

            if (!currentCourseItem.calMrp.equals("free", true)) {
                holder.binding.tvCourseFeeType.text = context.getString(R.string.paid)
                holder.binding.tvCourseFeeType.setBackgroundResource(R.drawable.background_red)
                holder.binding.coursePrice.visibility = View.VISIBLE

                if (currentCourseItem.isFreeTrial) {
                    holder.binding.tvCourseFeeType.setBackgroundResource(R.drawable.background_green)
                    holder.binding.tvCourseFeeType.text = context.getString(R.string.free_trial)
                }

            } else {
                holder.binding.tvCourseFeeType.setBackgroundResource(R.drawable.background_green)
                holder.binding.tvCourseFeeType.text = context.getString(R.string.free)
                holder.binding.coursePrice.visibility = View.GONE
            }



            holder.binding.courseRating.rating = currentCourseItem.rating.toFloat()
            holder.binding.tvCourseName.text = currentCourseItem.title
            holder.binding.tvCourseRating.text = currentCourseItem.rating
            holder.binding.courseEnrolled.text =
                context.getString(R.string.enrolled_text, currentCourseItem.learner)


            holder.binding.courseImage.setOnClickListener {

            }
            if (currentCourseItem.isIs_wishlist)
                holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_selected)
            else
                holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)

            holder.binding.imgWishlist.tag = currentCourseItem
            holder.binding.imgWishlist.setOnClickListener {
                val item = it.tag as Course
                if (!item.isIs_wishlist) {
                    addToWishList(item)
                    holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_selected)
                } else {
                    removeFromWishList(item, isWishlist)
                    holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)

                }
            }

            Helper.setEnrolAndWishListBackground(
                context,
                currentCourseItem,
                holder.binding.imgWishlist,
                holder.binding.enrollBtn
            )


            holder.binding.enrollBtn.tag = currentCourseItem
            holder.binding.enrollBtn.setOnClickListener {
                val item = it.tag as Course
                System.out.println("item----------------------------" + item.points_conversion_rate)

                if (currentCourseItem.isIs_renew == "1" || !currentCourseItem.isIs_purchased && currentCourseItem.mrp != "0") {
                    if (item.mrp == "0") {
                        networkCallForFreeCourseTransaction(item)
                    } else {

                        if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                            if (currentCourseItem.for_dams != "0")
                            //  Helper.goToCourseInvoiceScreen(context, Helper.getData(currentCourseItem))

                            //................added by anil start
                                if (currentCourseItem.is_subscription != null && currentCourseItem.is_subscription.equals(
                                        "1",
                                        true
                                    )
                                ) {    // is_subscription = 1
                                    // openSubscriptionsDialog(currentCourseItem)
                                    networkCallForBasicData(currentCourseItem)
                                } else {
                                    if (currentCourseItem.isIs_cart == true) {
                                        //remove the cart item
                                        removeFromCart(currentCourseItem)
                                    } else {
                                        addToCart(currentCourseItem)
                                    }
                                    // addToCart(currentCourseItem)
                                }
                            //...............added by anil end
                            else
                                networkCallForFreeCourseTransaction(item)

                        } else {
                            if (currentCourseItem.non_dams != "0")
                            //   Helper.goToCourseInvoiceScreen(context, Helper.getData(currentCourseItem))
                            //................added by anil start
                                if (currentCourseItem.is_subscription != null && currentCourseItem.is_subscription.equals(
                                        "1",
                                        true
                                    )
                                ) {    // is_subscription = 1
                                    // openSubscriptionsDialog(currentCourseItem)
                                    networkCallForBasicData(currentCourseItem)
                                } else {
                                    if (currentCourseItem.isIs_cart.equals(true)) {
                                        //remove the cart item
                                        removeFromCart(currentCourseItem)

                                    } else {
                                        addToCart(currentCourseItem)
                                    }
                                    //addToCart(currentCourseItem)
                                }

                            //...............added by anil end


                            else
                                networkCallForFreeCourseTransaction(item)
                        }
                    }
                } else if (currentCourseItem.isIs_purchased) {
                    return@setOnClickListener
                } else {
                    networkCallForFreeCourseTransaction(item)
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun setSpannable(priceTV: TextView, courseItem: Course) {
        if (courseItem.isDiscounted) {
            priceTV.setText(courseItem.calMrp, TextView.BufferType.SPANNABLE)
            val spannable = priceTV.text as Spannable
            spannable.setSpan(
                StrikethroughSpan(),
                2,
                Helper.calculatePriceBasedOnCurrency(courseItem.mrp).length + 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            priceTV.text = courseItem.calMrp
        }
    }

    private fun networkCallForFreeCourseTransaction(item: Course) {
        var conversion_rate = item.points_conversion_rate
        var conversionRateString = ""
        if (conversion_rate == null) {
            conversionRateString = "0"
        } else {
            conversionRateString = item.points_conversion_rate
        }

        progress = Progress(context)
        progress.setCancelable(false)
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.makeFreeCourseTransaction(
            SharedPreference.getInstance().loggedInUser.id,
            conversionRateString, "0", "", "", item.id, "0"
        )
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_purchased = true
                            item.isIs_renew = "0"

                            notifyDataSetChanged()
                            Toast.makeText(
                                context,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                "networkCallForFreeCourseTransaction",
                                context as Activity?,
                                1,
                                0
                            )
                            RetrofitResponse.getApiData(
                                context,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav(
                            "networkCallForFreeCourseTransaction",
                            context as Activity?,
                            1,
                            0
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav(
                    "networkCallForFreeCourseTransaction",
                    context as Activity?,
                    1,
                    1
                )
            }
        })
    }

    private fun addToWishList(item: Course) {
        // asynchronous operation to add course to wishlist.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.addCourseToWishlist(
            SharedPreference.getInstance().loggedInUser.id,
            item.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_wishlist = true

                            notifyDataSetChanged()
                            Toast.makeText(
                                context,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                "addToWishList",
                                context as Activity?,
                                1,
                                0
                            )
                            RetrofitResponse.getApiData(
                                context,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("addToWishList", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showErrorLayoutForNav("removeFromWishList", context as Activity?, 1, 1)

            }
        })

    }

    private fun removeFromWishList(item: Course, isWishlist: Boolean?) {
        // asynchronous operation to remove course to wishlist.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.removeCourseToWishlist(
            SharedPreference.getInstance().loggedInUser.id,
            item.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            if (!isWishlist!!)
                                item.isIs_wishlist = false
                            else
                                trendingCourseList.remove(item)
                            notifyDataSetChanged()
                            Toast.makeText(
                                context,
                                jsonResponse.optString(Constants.Extras.MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Helper.showErrorLayoutForNav(
                                "removeFromWishList",
                                context as Activity?,
                                1,
                                0
                            )
                            RetrofitResponse.getApiData(
                                context,
                                jsonResponse.optString(Const.AUTH_CODE)
                            )
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav(
                            "removeFromWishList",
                            context as Activity?,
                            1,
                            0
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showErrorLayoutForNav("removeFromWishList", context as Activity?, 1, 1)

            }
        })

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordCoursesListItemTrendingViewHolder {
        val binding = RecordCoursesListItemTrendingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordCoursesListItemTrendingViewHolder(binding)
    }


//added by anil ---------------start

    private fun networkCallForBasicData(currentCourse: Course) {
        // progress.show()

        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.getCourseDetailData(
            SharedPreference.getInstance().loggedInUser.id, currentCourse.id
        )

        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        Log.e(CourseDetailActivity.TAG, " onResponse: $jsonResponse")
                        val descriptionResponse: DescriptionResponse =
                            Gson().fromJson(jsonObject.toString(), DescriptionResponse::class.java)
                        var descriptionData: DescriptionData = descriptionResponse.data
                        // is_subscription = descriptionData.basic.is_subscription
                        var subscriptionsList: List<Installment> = descriptionData.basic.installment


                        openSubscriptionsDialog(currentCourse, subscriptionsList, descriptionData)

                        //  progress.dismiss()
                    } catch (e: Exception) {
                        progress.dismiss()
                        e.printStackTrace()
                    }
                } else {
                    progress.dismiss()
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()

            }
        })


    }


    private fun openSubscriptionsDialog(
        currentCourseItem1: Course,
        subscriptionsList: List<Installment>,
        descriptionData: DescriptionData
    ) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val layoutInflaterAndroid: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflaterAndroid.inflate(R.layout.subscription_dialog_cart, null)
        builder.setView(view)
        builder.setCancelable(true)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val closeButton = view.findViewById<ImageView>(R.id.closeButton)
        val subscriptionRecyclerview =
            view.findViewById<RecyclerView>(R.id.subscriptionRecyclerview)

        val singleCourseData: SingleCourseData = Helper.getData(currentCourseItem1)

        singleCourseData.is_subscription = descriptionData.basic.is_subscription
        singleCourseData.is_instalment = descriptionData.basic.is_instalment

        subscriptionRecyclerview.adapter =
            InstallmentParentAdapter(context, subscriptionsList, singleCourseData,
                object : OnSubscriptionItemClickListener {
                    override fun OnSubscriptionItemClickPosition(position: Int) {

                        selectedSubscriptionId = subscriptionsList.get(position).id
                        alertDialog.dismiss()
                        if (currentCourseItem1.isIs_cart == true) {
                            //remove the cart item
                            removeFromCart(currentCourseItem1)

                        } else {
                            addToCart(currentCourseItem1)
                        }
                    }
                })

        subscriptionRecyclerview.addItemDecoration(
            EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL)
        )
        subscriptionRecyclerview.layoutManager = LinearLayoutManager(context)

        closeButton.setOnClickListener { alertDialog.dismiss() }

    }


    private fun addToCart(item: Course) {
        // asynchronous operation to add course to cart.
        // progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.addCourseToCart(
            SharedPreference.getInstance().loggedInUser.id,
            item.id,
            selectedSubscriptionId
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                //   progress.dismiss()
                selectedSubscriptionId = ""
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            // binding.AddToCart.text = "Added in cart"
                            // binding.AddToCart.setBackgroundResource(R.drawable.bg_light_green)
                            var prevCartCount =
                                SharedPreference.getInstance().getInt(Const.CART_COUNT)
                            SharedPreference.getInstance()
                                .putInt(Const.CART_COUNT, prevCartCount + 1)
                            //  Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()

                            Helper.goToCartScreen(context, null)


                        } else if (jsonResponse.optBoolean(Const.STATUS) == false) {
                            Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        } else {
                            //  Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            RetrofitResponse.getApiData(
                                context, jsonResponse.optString(Const.AUTH_CODE)


                            )
                        }
                    } catch (e: JSONException) {
                        //   Helper.showErrorLayoutForNav("addToCart", context, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // progress.dismiss()
                // Helper.showErrorLayoutForNav("addToCart", this@CourseDetailActivity, 1, 1)
                selectedSubscriptionId = ""
            }
        })
    }


    private fun removeFromCart(item: Course) {
        // asynchronous operation to remove course from cart.
        //  progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.removeCourseFromCart(
            SharedPreference.getInstance().loggedInUser.id,
            item.id
        )
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                //  progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            // courseList.remove(item)
                            // notifyDataSetChanged()
                            //  Toast.makeText(context, "remove done", Toast.LENGTH_SHORT).show()
                            // myCartInterface.onClickDelete()

                            addToCart(item)

                        }/* else {
                            Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 0)
                            RetrofitResponse.getApiData(context as Activity?, jsonResponse.optString(Const.AUTH_CODE))
                        }*/
                    } catch (e: JSONException) {
                        //   Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //   progress.dismiss()
                //  Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 1)
            }
        })
    }


//added by anil................end code

    inner class RecordCoursesListItemTrendingViewHolder(val binding: RecordCoursesListItemTrendingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = trendingCourseList.size
}



