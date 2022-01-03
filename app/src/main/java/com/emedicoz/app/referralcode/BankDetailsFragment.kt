package com.emedicoz.app.referralcode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emedicoz.app.R
import com.emedicoz.app.podcastnew.PodcastFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BankDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BankDetailsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bank_details, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PodcastFragment().apply {
                    arguments = Bundle().apply {
                        // putInt("", param)
                    }
                }


    }
}