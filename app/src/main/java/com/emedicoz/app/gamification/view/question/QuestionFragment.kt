package com.emedicoz.app.gamification.view.question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emedicoz.app.R
import com.emedicoz.app.databinding.QuestionFragmentBinding

class QuestionFragment : Fragment(), View.OnClickListener {

    private var _binding: QuestionFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: QuestionViewModel

    lateinit var ibBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_QuestionFragment_to_HostChallengeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = QuestionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(QuestionViewModel::class.java)

        ibBack = view.findViewById(R.id.ibBack)
        ibBack.setOnClickListener(this)

        val tvHeaderName = view.findViewById<TextView>(R.id.tvHeaderName)
        tvHeaderName.text = getString(R.string.live_quiz_challenge)

        val tvNumberPickers = view.findViewById<NumberPicker>(R.id.tvNumberPickers)
        if (tvNumberPickers != null) {
            val values = arrayOf("30", "60", "90")
            tvNumberPickers.minValue = 0
            tvNumberPickers.maxValue = values.size - 1
            tvNumberPickers.displayedValues = values
            tvNumberPickers.wrapSelectorWheel = true
            tvNumberPickers.setOnValueChangedListener { picker, oldVal, newVal ->
                val text = "Changed from " + values[oldVal] + " to " + values[newVal]
            }
        }

        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = QuestionFragment()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ibBack -> {
                findNavController().navigate(R.id.action_QuestionFragment_to_HostChallengeFragment)
            }
            R.id.btnSubmit -> {
                findNavController().navigate(R.id.action_QuestionFragment_to_DifficultyFragment)
            }
        }
    }

}