package com.emedicoz.app.referralcode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentPersonalDetailsBinding
import com.emedicoz.app.databinding.LayoutPodcastNewRevmpBinding
import com.emedicoz.app.podcastnew.PodcastFragment


class PersonalDetailsFragment : Fragment() {

     lateinit var binding: FragmentPersonalDetailsBinding




    companion object {
        @JvmStatic
        fun newInstance() =
                PodcastFragment().apply {
                    arguments = Bundle().apply {
                       // putInt("", param)
                    }
                }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_personal_details, container, false)

        binding =  FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {


        }
    }


}