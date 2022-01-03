package com.emedicoz.app.gamification.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentStartQuizBinding

class StartQuizFragment : Fragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private var _binding: FragmentStartQuizBinding? = null
    private val binding get() = _binding!!
    private var font: Typeface? = null
    private var boldFont: Typeface? = null

    var progressStatus2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_StartQuizFragment_to_OneToOneChallengeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStartQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        font = ResourcesCompat.getFont(requireContext(), R.font.helvetica_neue_med)
        boldFont = ResourcesCompat.getFont(requireContext(), R.font.helvetica_neue_bd)

        val ibBack = view.findViewById<ImageButton>(R.id.ibBack)
        ibBack.setOnClickListener(this)

        val tvHeaderName = view.findViewById<TextView>(R.id.tvHeaderName)
        tvHeaderName.text = getString(R.string.live_quiz_challenge)

        if (progressStatus2 < 30) {
            progressStatus2 = 25
            binding.progressBar.progress = progressStatus2
        }

        binding.radios.setOnCheckedChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibBack -> {
                findNavController().navigate(R.id.action_StartQuizFragment_to_OneToOneChallengeFragment)
            }
        }
    }

    override fun onCheckedChanged(p0: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rbOption1 -> {
                binding.rbOption1.setBackgroundResource(R.drawable.ic_rectangle_1094_fill)
                binding.rbOption2.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption3.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption4.setBackgroundResource(R.drawable.ic_rectangle_1094)

                binding.rbOption1.typeface = boldFont
                binding.rbOption2.typeface = font
                binding.rbOption3.typeface = font
                binding.rbOption4.typeface = font
            }
            R.id.rbOption2 -> {
                binding.rbOption2.setBackgroundResource(R.drawable.ic_rectangle_1094_fill)
                binding.rbOption1.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption3.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption4.setBackgroundResource(R.drawable.ic_rectangle_1094)

                binding.rbOption2.typeface = boldFont
                binding.rbOption1.typeface = font
                binding.rbOption3.typeface = font
                binding.rbOption4.typeface = font
            }
            R.id.rbOption3 -> {
                binding.rbOption3.setBackgroundResource(R.drawable.ic_rectangle_1094_fill)
                binding.rbOption1.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption2.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption4.setBackgroundResource(R.drawable.ic_rectangle_1094)

                binding.rbOption3.typeface = boldFont
                binding.rbOption1.typeface = font
                binding.rbOption2.typeface = font
                binding.rbOption4.typeface = font
            }
            R.id.rbOption4 -> {
                binding.rbOption4.setBackgroundResource(R.drawable.ic_rectangle_1094_fill)
                binding.rbOption1.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption2.setBackgroundResource(R.drawable.ic_rectangle_1094)
                binding.rbOption3.setBackgroundResource(R.drawable.ic_rectangle_1094)

                binding.rbOption4.typeface = boldFont
                binding.rbOption1.typeface = font
                binding.rbOption2.typeface = font
                binding.rbOption3.typeface = font
            }
        }
    }
}