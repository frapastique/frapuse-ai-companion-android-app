package com.back.frapuse.ui.imagegen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.back.frapuse.databinding.FragmentImageGenRvDetailBinding
import com.back.frapuse.util.ImageGenRVDetailAdapter

class ImageGenRVDetailFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: ImageGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentImageGenRvDetailBinding

    // Prepare an imageID
    private var imageID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageID = it.getInt("imageID")
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
        binding = FragmentImageGenRvDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //viewModel.getImageMetadata(imageID)

        viewModel.imageLibrary.observe(viewLifecycleOwner) { imageLibrary ->
            val imageAdapter = ImageGenRVDetailAdapter(
                imageID = imageID,
                viewModel = viewModel,
                dataset = imageLibrary,
                requireContext()
            )
            binding.rvImageLibrary.adapter = imageAdapter
            imageAdapter.submitList(imageLibrary)
            binding.rvImageLibrary.setHasFixedSize(true)
            binding.rvImageLibrary.scrollToPosition(imageID)

            /*val helper: SnapHelper = PagerSnapHelper()
            helper.attachToRecyclerView(binding.rvImageLibrary)*/
        }
    }
}