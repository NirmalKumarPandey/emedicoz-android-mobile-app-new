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
import com.emedicoz.app.databinding.FragmentMyAttemptedBinding

class MyAttemptedFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMyAttemptedBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_MyAttemptedFragment_to_LiveQuizFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMyAttemptedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ibBack = view.findViewById<ImageButton>(R.id.ibBack)
        ibBack.setOnClickListener(this)

        val tvHeaderName = view.findViewById<TextView>(R.id.tvHeaderName)
        tvHeaderName.text = getString(R.string.my_attempted)

        val myAttemptAdapter = MyAttemptAdapter(requireActivity())
        binding.rvMyAttempted.adapter = myAttemptAdapter

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibBack -> {
                findNavController().navigate(R.id.action_MyAttemptedFragment_to_LiveQuizFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}