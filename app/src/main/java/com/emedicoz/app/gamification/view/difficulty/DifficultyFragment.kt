package com.emedicoz.app.gamification.view.difficulty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emedicoz.app.R
import com.emedicoz.app.databinding.DifficultyFragmentBinding

class DifficultyFragment : Fragment(), View.OnClickListener {
    private var _binding: DifficultyFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DifficultyViewModel

    lateinit var ibBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_DifficultyFragment_to_QuestionFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DifficultyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DifficultyViewModel::class.java)

        ibBack = view.findViewById(R.id.ibBack)
        ibBack.setOnClickListener(this)

        val tvHeaderName = view.findViewById<TextView>(R.id.tvHeaderName)
        tvHeaderName.text = getString(R.string.live_quiz_challenge)

        binding.apply {
            easyLayout.setOnClickListener(this@DifficultyFragment)
            smallMediumLayout.setOnClickListener(this@DifficultyFragment)
            smallHardLayout.setOnClickListener(this@DifficultyFragment)
            btnNext.setOnClickListener(this@DifficultyFragment)
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibBack -> {
                findNavController().navigate(R.id.action_DifficultyFragment_to_QuestionFragment)
            }
            R.id.btnNext -> {
                findNavController().navigate(R.id.action_QuestionFragment_to_SubjectFragment)
            }
            R.id.easyLayout -> {
                binding.largeEasyLayout.visibility = View.VISIBLE
                binding.easyLayout.visibility = View.GONE

                binding.smallMediumLayout.visibility = View.VISIBLE
                binding.largeMediumLayout.visibility = View.GONE

                binding.smallHardLayout.visibility = View.VISIBLE
                binding.largeHardLayout.visibility = View.GONE
            }

            R.id.smallMediumLayout -> {
                binding.largeEasyLayout.visibility = View.GONE
                binding.easyLayout.visibility = View.VISIBLE

                binding.smallMediumLayout.visibility = View.GONE
                binding.largeMediumLayout.visibility = View.VISIBLE

                binding.smallHardLayout.visibility = View.VISIBLE
                binding.largeHardLayout.visibility = View.GONE
            }

            R.id.smallHardLayout -> {
                binding.largeEasyLayout.visibility = View.GONE
                binding.easyLayout.visibility = View.VISIBLE

                binding.smallMediumLayout.visibility = View.VISIBLE
                binding.largeMediumLayout.visibility = View.GONE

                binding.smallHardLayout.visibility = View.GONE
                binding.largeHardLayout.visibility = View.VISIBLE
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = DifficultyFragment()
    }
}