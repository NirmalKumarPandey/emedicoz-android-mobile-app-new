package com.emedicoz.app.login.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emedicoz.app.databinding.LayoutMobileOtpRegisterationBinding

class LoginWithEmailOrNumber : Fragment() {
    private lateinit var binding: LayoutMobileOtpRegisterationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutMobileOtpRegisterationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): LoginWithEmailOrNumber {
            return LoginWithEmailOrNumber()
        }
    }
}