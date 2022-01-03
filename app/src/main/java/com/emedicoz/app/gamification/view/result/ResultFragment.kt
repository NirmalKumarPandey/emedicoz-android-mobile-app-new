package com.emedicoz.app.gamification.view.result

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ResultFragmentBinding

class ResultFragment : Fragment() {
    private var _binding: ResultFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ResultViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ResultFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResultViewModel::class.java)
    }


    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
