package com.back.frapuse.ui.imagegen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.back.frapuse.R
import com.back.frapuse.databinding.FragmentImageGenRvSmallBinding
import com.back.frapuse.util.adapter.imagegen.ImageGenRVSmallAdapter

class ImageGenRVSmallFragment : Fragment() {
    // Load viewModel
    private val viewModel: ImageGenViewModel by activityViewModels()

    // Set binding
    private lateinit var binding: FragmentImageGenRvSmallBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_image_gen_rv_small,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.imageLibrary.observe(viewLifecycleOwner) { imageLibrary ->
            val imageAdapter = ImageGenRVSmallAdapter(
                viewModel = viewModel,
                dataset = imageLibrary
            )
            binding.rvImageLibrary.adapter = imageAdapter
            imageAdapter.submitList(imageLibrary)
            binding.rvImageLibrary.setHasFixedSize(true)
        }
    }
}