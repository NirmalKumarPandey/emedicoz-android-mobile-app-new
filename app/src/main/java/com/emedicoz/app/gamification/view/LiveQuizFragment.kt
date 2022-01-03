package com.emedicoz.app.gamification.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentLiveQuizBinding

class LiveQuizFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLiveQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLiveQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ibBack = view.findViewById<ImageButton>(R.id.ibBack)
        ibBack.setOnClickListener(this)

        val tvHeaderName = view.findViewById<TextView>(R.id.tvHeaderName)
        tvHeaderName.text = getString(R.string.live_quiz_challenge)

        binding.rlBanner.setOnClickListener(this)

        val liveQuizAdapter = LiveQuizAdapter(requireActivity()) {
            when (it) {
                0 -> {
                    findNavController().navigate(R.id.action_LiveQuizFragment_to_HostChallengeFragment)
                }
                1 -> {
//                    findNavController().navigate(R.id.action_LiveQuizFragment_to_HostChallengeFragment)
                }
                2 -> {
                    findNavController().navigate(R.id.action_LiveQuizFragment_to_AcceptChallengeFragment)
                }
                3 -> {
                    findNavController().navigate(R.id.action_LiveQuizFragment_to_MyAttemptedFragment)
                }
            }
        }
        binding.rvQuiz.adapter = liveQuizAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibBack -> {
                requireActivity().finish()
            }
            R.id.rlBanner -> {
                findNavController().navigate(R.id.action_LiveQuizFragment_to_HostChallengeFragment)
            }
        }
    }

}