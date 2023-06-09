package com.back.frapuse.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.back.frapuse.R
import com.back.frapuse.databinding.FragmentSplashScreenBinding
import com.back.frapuse.databinding.ImageGenRvSmallItemBinding
import com.back.frapuse.ui.imagegen.ImageGenViewModel
import com.back.frapuse.ui.textgen.TextGenViewModel

class SplashScreenFragment : Fragment() {
    private val viewModelImageGen: ImageGenViewModel by activityViewModels()
    private val viewModelTextGen: TextGenViewModel by activityViewModels()

    private lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_splash_screen,
            container,
            false
        )

        viewModelImageGen
        viewModelTextGen

        Handler(Looper.myLooper()!!).postDelayed(
            {
                findNavController().navigate(
                    SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment()
                )
            },
            1500
        )

        return binding.root
    }
}