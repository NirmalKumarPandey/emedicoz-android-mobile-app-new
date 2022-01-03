package com.emedicoz.app.referralcode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentPersonalDetailsBinding
import com.emedicoz.app.databinding.FragmentReferralSignUpBinding
import com.emedicoz.app.databinding.FragmentRefferralSignUpNewBinding
import com.emedicoz.app.podcastnew.PodcastFragment
import com.emedicoz.app.referralcode.ReferEarnNowFragment.newInstance
import com.emedicoz.app.utilso.Const

class RefferralSignUpNewFragment : Fragment(),View.OnClickListener {

    lateinit var binding:FragmentRefferralSignUpNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_personal_details, container, false)

        binding =  FragmentRefferralSignUpNewBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {


            personalDetailsTv.setOnClickListener {this@RefferralSignUpNewFragment}
            bankDetailsTv.setOnClickListener {this@RefferralSignUpNewFragment}

        }


        addFragment(PersonalDetailsFragment.newInstance())
        changeBackground(Const.personalDetail)


    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.personalDetailsTv -> {
                changeBackground(Const.personalDetail)
                addFragment(PersonalDetailsFragment.newInstance())
            }
            R.id.bankDetailsTv -> {
                changeBackground(Const.bankDetail)
                addFragment(BankDetailsFragment.newInstance())
            }
        }
    }


    private fun changeBackground(type: String) {
        binding.apply {
            personalDetailsTv.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.transparent
                    )
            )
            bankDetailsTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.progress_start)
            )

            bankDetailsTv.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.transparent
                    )
            )
            bankDetailsTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.progress_start
                    )
            )
            when (type) {
                Const.personalDetail -> {
                    personalDetailsTv.background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.referral_tab_bg)
                    personalDetailsTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white
                            )
                    )
                }

                Const.bankDetail -> {
                    bankDetailsTv.background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.referral_tab_bg)
                    bankDetailsTv.setTextColor(
                            ContextCompat.getColor(requireActivity(), R.color.white
                            )
                    )
                }

            }
        }
    }

    fun addFragment(fragment: Fragment) {
        childFragmentManager
                .beginTransaction()
                .add(R.id.referralContainer, fragment)
                .commitAllowingStateLoss()
    }




    fun replaceFragment(fragment: Fragment) {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.referralContainer, fragment)
                .commitAllowingStateLoss()
    }

    fun getCurrentFragment():Fragment {
        return childFragmentManager.findFragmentById(R.id.referralContainer)!!
    }


}