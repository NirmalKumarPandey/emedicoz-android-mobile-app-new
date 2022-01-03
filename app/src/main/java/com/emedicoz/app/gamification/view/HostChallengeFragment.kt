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
import com.emedicoz.app.databinding.FragmentHostChallengeBinding

class HostChallengeFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentHostChallengeBinding? = null
    private val binding get() = _binding!!

    lateinit var ibBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_HostChallengeFragment_to_LiveQuizFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHostChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ibBack = view.findViewById(R.id.ibBack)
        ibBack.setOnClickListener(this)

        val tvHeaderName = view.findViewById<TextView>(R.id.tvHeaderName)
        tvHeaderName.text = getString(R.string.live_quiz_challenge)

        binding.apply {
            val clickListener = this@HostChallengeFragment
            groupLayout.setOnClickListener(clickListener)
            oneLayout.setOnClickListener(clickListener)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibBack -> {
                findNavController().navigate(R.id.action_HostChallengeFragment_to_LiveQuizFragment)
            }
            R.id.groupLayout -> {
                findNavController().navigate(R.id.action_HostChallengeFragment_to_QuestionFragment)
            }
            R.id.oneLayout -> {
                findNavController().navigate(R.id.action_HostChallengeFragment_to_QuestionFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
