package com.back.frapuse.ui.imagegen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.back.frapuse.ImageGenerationViewModel
import com.back.frapuse.databinding.FragmentImageGenDetailBinding

class ImageGenDetailFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: ImageGenerationViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentImageGenDetailBinding

    // Prepare an imageID
    private var imageID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageID = it.getLong("imageID")
        }
    }

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageGenDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getImageMetadata(imageID)
        viewModel.imageMetadata.observe(viewLifecycleOwner) { imageMetadata ->
            binding.ivTextToImage.setImageBitmap(viewModel.decodeImage(imageMetadata.image))
            binding.etPrompt.setText(imageMetadata.positivePrompt)
            binding.etNegativePrompt.setText(imageMetadata.negativePrompt)
            binding.etSeed.setText(imageMetadata.seed.toString())
            binding.etModel.setText(imageMetadata.model)
            binding.etSteps.setText(imageMetadata.steps.toString())
            binding.etCfgScale.setText(imageMetadata.CFGScale.toString())
            binding.etHeight.setText(imageMetadata.height.toString())
            binding.etWidth.setText(imageMetadata.width.toString())
            binding.etSampler.setText(imageMetadata.sampler)
        }
    }
}