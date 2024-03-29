package com.back.frapuse.ui.home

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.back.frapuse.ui.imagegen.ImageGenViewModel
import com.back.frapuse.databinding.FragmentHomeBinding
import com.back.frapuse.util.adapter.home.HomeBackgroundAdapter
import com.back.frapuse.util.layoutmanager.NoScrollLayoutManager

class HomeFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: ImageGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentHomeBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getAllImages()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnTextToImage.setOnClickListener { btnTextToImage ->
            btnTextToImage.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToImageGenTextToImageFragment()
            )
        }

        binding.btnChat.setOnClickListener { btnChat ->
            btnChat.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToTextGenChatFragment()
            )
        }

        binding.btnDocument.setOnClickListener { btnDocument ->
            btnDocument.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToTextGenDocumentOperationFragment()
            )
        }

        // Observer for imageLibrary, which sets a blurred RecyclerView into the background when min
        // build version of phone is same or greater than min requirement.
        viewModel.imageLibrary.observe(viewLifecycleOwner) { imageLibrary ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                binding.rvImageLibrary.setRenderEffect(
                    RenderEffect.createBlurEffect(33f, 33f, Shader.TileMode.DECAL)
                )
                binding.rvImageLibrary.layoutManager = NoScrollLayoutManager(requireContext(), 2)
                binding.rvImageLibrary.adapter = HomeBackgroundAdapter(viewModel, imageLibrary)
                binding.rvImageLibrary.setHasFixedSize(true)
            }
        }
    }
}