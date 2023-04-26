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
     * Here gets the binding initialized and the layout built
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
        //viewModel.getImageMetaData(imageID)
        binding.ivTextToImage.setImageBitmap(viewModel.decodeImage(viewModel.finalImageBase64.value!!.images.first()))
    }
}