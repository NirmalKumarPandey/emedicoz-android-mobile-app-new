package com.emedicoz.app.courses.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.emedicoz.app.R
import com.emedicoz.app.databinding.SingleItemExpertFollowBinding
import com.emedicoz.app.feeds.activity.FeedsActivity
import com.emedicoz.app.feeds.activity.PostActivity
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.modelo.People
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.PeopleFollowApiInterface
import com.emedicoz.app.utilso.*
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ExpertFollowAdapter(
    var peopleArrayList: List<People>,
    var activity: Activity,
    var type: String,
    var peopleType: String?,
    var check: Int
) :
    RecyclerView.Adapter<ExpertFollowAdapter.ViewHolder>() {
    var currentPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItemExpertFollowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val people = peopleArrayList[position]
        holder.setPeopleData(people, getItemViewType(position))
        holder.binding.parentView.setOnClickListener {
            if (check != 1) Helper.GoToProfileActivity(
                activity, peopleArrayList[position].user_id
            )
        }
    }

    override fun getItemCount(): Int {
        return peopleArrayList.size
    }

    inner class ViewHolder(var binding: SingleItemExpertFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun networkCallForFollow() {
            val apiInterface = ApiClient.createService(
                PeopleFollowApiInterface::class.java
            )
            val response = apiInterface.follow(
                peopleArrayList[currentPosition].id,
                SharedPreference.getInstance().loggedInUser.id
            )
            response.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.body() != null) {
                        val jsonObject = response.body()
                        val jsonResponse: JSONObject
                        try {
                            jsonResponse = JSONObject(jsonObject.toString())
                            if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                                if (activity is AuthActivity)
                                    (activity as AuthActivity).followExpertCounter++
                                initViewFollowUnfollow(1)
                            } else {
                                Toast.makeText(
                                    activity,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                                ).show()
                                RetrofitResponse.getApiData(
                                    activity,
                                    jsonResponse.optString(Const.AUTH_CODE)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else Toast.makeText(
                        activity,
                        R.string.exception_api_error_message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Helper.showExceptionMsg(activity, t)
                }
            })
        }

        private fun networkCallForUnFollow() {
            val apiInterface = ApiClient.createService(
                PeopleFollowApiInterface::class.java
            )
            val response = apiInterface.unfollow(
                peopleArrayList[currentPosition].id,
                SharedPreference.getInstance().loggedInUser.id
            )
            response.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.body() != null) {
                        val jsonObject = response.body()
                        val jsonResponse: JSONObject
                        try {
                            jsonResponse = JSONObject(jsonObject.toString())
                            if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                                if (activity is AuthActivity && (activity as AuthActivity).followExpertCounter > 0)
                                    (activity as AuthActivity).followExpertCounter--
                                initViewFollowUnfollow(0)
                            } else {
                                Toast.makeText(
                                    activity,
                                    jsonResponse.optString(Constants.Extras.MESSAGE),
                                    Toast.LENGTH_SHORT
                                ).show()
                                RetrofitResponse.getApiData(
                                    activity,
                                    jsonResponse.optString(Const.AUTH_CODE)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else Toast.makeText(
                        activity,
                        R.string.exception_api_error_message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Helper.showExceptionMsg(activity, t)
                }
            })
        }

        fun initViewFollowUnfollow(type: Int) {
            if (type == 1) {
                changeFollowBackground(binding.llFollow, binding.ivFollow, binding.tvFollow, type)
                peopleArrayList[currentPosition].isWatcher_following = true
                if (!TextUtils.isEmpty(peopleType) && peopleType.equals(
                        Const.COMMON_EXPERT_PEOPLE_VIEWALL,
                        ignoreCase = true
                    )
                ) FeedsActivity.changeFollowingExpert(
                    peopleArrayList[currentPosition], 1
                ) else if (!TextUtils.isEmpty(
                        peopleType
                    ) && peopleType.equals(Const.COMMON_PEOPLE_VIEWALL, ignoreCase = true)
                ) FeedsActivity.changeFollowingPeople(
                    peopleArrayList[currentPosition], 1
                )
            } else {
                changeFollowBackground(binding.llFollow, binding.ivFollow, binding.tvFollow, type)
                peopleArrayList[currentPosition].isWatcher_following = false
                if (!TextUtils.isEmpty(peopleType) && peopleType.equals(
                        Const.COMMON_EXPERT_PEOPLE_VIEWALL,
                        ignoreCase = true
                    )
                ) FeedsActivity.changeFollowingExpert(
                    peopleArrayList[currentPosition], 0
                ) else if (!TextUtils.isEmpty(
                        peopleType
                    ) && peopleType.equals(Const.COMMON_PEOPLE_VIEWALL, ignoreCase = true)
                ) FeedsActivity.changeFollowingPeople(
                    peopleArrayList[currentPosition], 1
                )
            }
        }

        fun setPeopleData(people: People, position: Int) {
            people.name = Helper.CapitalizeText(people.name)
            if (position != 0) {
                people.user_id = people.id
            }
            binding.nameTV.text = people.name
            setProfileImage(people)

            //this is to show the specialisation for the expert people only
            if (!TextUtils.isEmpty(peopleType) && peopleType.equals(
                    Const.COMMON_EXPERT_PEOPLE_VIEWALL,
                    ignoreCase = true
                ) && !TextUtils.isEmpty(people.specification)
            ) {
                binding.specialisationTV.visibility = View.VISIBLE
                binding.specialisationTV.text = people.specification
            } else {
                binding.specialisationTV.visibility = View.GONE
            }

            // this is to show the time in like lists
            setDate(people, position)

            //this is for the first time to show users the number of followers each expert has
            setFollowersCount(people)

            //follow button will be gone if it is the profile of the person who is seeing the list
            if (!TextUtils.isEmpty(people.user_id) && people.user_id == SharedPreference.getInstance().loggedInUser.id) binding.scvFollowBtn.visibility =
                View.GONE else binding.scvFollowBtn.visibility = View.VISIBLE

            //setting the UI if person who is seeing the list is following the people or not
            if (people.isWatcher_following) {
                changeFollowBackground(binding.llFollow, binding.ivFollow, binding.tvFollow, 1)
            } else {
                changeFollowBackground(binding.llFollow, binding.ivFollow, binding.tvFollow, 0)
            }
            binding.scvFollowBtn.setOnClickListener { view1: View? -> onFollowClick() }
        }

        private fun setDate(people: People, position: Int) {
            if (position == 0) {
                if (!TextUtils.isEmpty(people.time)) {
                    binding.dateTV.visibility = View.VISIBLE
                    binding.dateTV.text =
                        if (DateUtils.getRelativeTimeSpanString(people.time.toLong()) == "0 minutes ago") "Just Now" else DateUtils.getRelativeTimeSpanString(
                            people.time.toLong()
                        )
                } else binding.dateTV.visibility = View.GONE
            }
        }

        private fun onFollowClick() {
            Log.e("setPeopleData: ", "from on click")
            currentPosition = adapterPosition
            if (peopleArrayList[currentPosition].isWatcher_following) {
                networkCallForUnFollow()
            } else {
                networkCallForFollow()
            }
        }

        private fun setFollowersCount(people: People) {
            if (check == 1) {
                if (!TextUtils.isEmpty(people.followers_count)) {
                    binding.followersTV.visibility = View.VISIBLE
                    binding.followersTV.text = String.format("%s Followers", people.followers_count)
                }
            } else {
                if (peopleType != null && peopleType.equals(
                        Const.COMMON_EXPERT_PEOPLE_VIEWALL,
                        ignoreCase = true
                    )
                ) {
                    binding.followersTV.visibility = View.VISIBLE
                    val count = people.followers_count.toLong()
                    Log.e("setPeopleData: ", "followers: $count")
                    var countString = ""
                    countString = if (Math.abs(count / 1000000) > 1) {
                        (count / 1000000).toString() + "m"
                    } else if (Math.abs(count / 1000) > 1) {
                        (count / 1000).toString() + "k"
                    } else {
                        count.toString()
                    }
                    binding.followersTV.text = String.format("%s Followers", countString)
                } else binding.followersTV.visibility = View.GONE
            }
        }

        private fun setProfileImage(people: People) {
            if (!TextUtils.isEmpty(people.profile_picture)) {
                binding.imageIV.visibility = View.VISIBLE
                binding.imageIVText.visibility = View.GONE
                Glide.with(activity)
                    .load(people.profile_picture)
                    .apply(RequestOptions().placeholder(R.mipmap.default_pic))
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            val dr: Drawable? = Helper.GetDrawable(
                                people.name,
                                activity, people.id
                            )
                            if (dr != null) {
                                binding.imageIV.visibility = View.GONE
                                binding.imageIVText.visibility = View.VISIBLE
                                binding.imageIVText.setImageDrawable(dr)
                            } else {
                                binding.imageIV.visibility = View.VISIBLE
                                binding.imageIVText.visibility = View.GONE
                                binding.imageIV.setImageResource(R.mipmap.default_pic)
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(binding.imageIV)
            } else {
                val dr: Drawable? = Helper.GetDrawable(
                    people.name,
                    activity, people.id
                )
                if (dr != null) {
                    binding.imageIV.visibility = View.GONE
                    binding.imageIVText.visibility = View.VISIBLE
                    binding.imageIVText.setImageDrawable(dr)
                } else {
                    binding.imageIV.visibility = View.VISIBLE
                    binding.imageIVText.visibility = View.GONE
                    binding.imageIV.setImageResource(R.mipmap.default_pic)
                }
            }
        }

        private fun changeFollowBackground(
            llFollow: LinearLayout,
            imageView: ImageView,
            textView: TextView,
            type: Int
        ) {
            if (type == 1) {
                imageView.setBackgroundResource(R.mipmap.profile_followers)
                textView.setText(R.string.following)
                textView.setTextColor(ContextCompat.getColor(activity, R.color.white))
                llFollow.setBackgroundResource(R.drawable.bg_capsule_fill_blue)
            } else {
                imageView.setBackgroundResource(R.mipmap.follow_blue)
                textView.setText(R.string.follow)
                textView.setTextColor(ContextCompat.getColor(activity, R.color.blue))
                llFollow.setBackgroundResource(R.drawable.reg_round_white_bg)
            }
        }
    }
}
