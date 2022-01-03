package com.emedicoz.app.courses.fragment

import android.app.Activity
import android.widget.TextView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.emedicoz.app.utilso.SharedPreference
import com.bumptech.glide.Glide
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.emedicoz.app.R
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.courses.fragment.QRCodeFragment
import com.emedicoz.app.databinding.FragmentQrcodeBinding
import com.emedicoz.app.modelo.User
import com.emedicoz.app.utilso.Helper

/**
 * A simple [Fragment] subclass.
 */
class QRCodeFragment : Fragment() {
    private lateinit var binding: FragmentQrcodeBinding
    var activity: Activity? = null
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQrcodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener { activity!!.onBackPressed() }
        user = SharedPreference.getInstance().loggedInUser
        binding.apply {
            damsidTV.text = String.format("DAMS ID : %s", user.dams_tokken)
            userNameTV.text = user.name
        }
        if (!TextUtils.isEmpty(user.profile_picture)) {
            binding.apply {
                profileImage.visibility = View.VISIBLE
                profileImageText.visibility = View.GONE
                Glide.with(this@QRCodeFragment).load(user.profile_picture).into(profileImage)
            }
        } else {
            val dr: Drawable? = Helper.GetDrawable(user.name, activity, user.id)
            if (dr != null) {
                binding.apply {
                    profileImage.visibility = View.GONE
                    profileImageText.visibility = View.VISIBLE
                    profileImageText.setImageDrawable(dr)
                }
            } else {
                binding.apply {
                    profileImage.visibility = View.VISIBLE
                    profileImageText.visibility = View.GONE
                    profileImage.setImageResource(R.mipmap.default_pic)
                }
            }
        }
        if (SharedPreference.getInstance().masterHitResponse != null && !GenericUtils.isEmpty(
                SharedPreference.getInstance().masterHitResponse.qrcode
            )
        ) Glide.with(
            activity!!
        ).load(SharedPreference.getInstance().masterHitResponse.qrcode).into(binding.imageQR)
    }

    companion object {
        @JvmStatic
        fun newInstance(): QRCodeFragment {
            return QRCodeFragment()
        }
    }
}