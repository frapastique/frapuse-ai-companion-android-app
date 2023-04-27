package com.back.frapuse.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGenerationViewModel
import com.back.frapuse.data.datamodels.ImageMetadata
import com.back.frapuse.databinding.ImageGenRvSmallItemBinding
import com.back.frapuse.ui.imagegen.ImageGenRVSmallFragmentDirections

class ImageGenRVSmallAdapter(
    // ViewModel to interact with shared methods
    private val viewModel: ImageGenerationViewModel,
    // Dataset which provides the wanted data
    private val dataset: List<ImageMetadata>
) : RecyclerView.Adapter<ImageGenRVSmallAdapter.ImageGenRVSmallViewHolder>() {
    inner class ImageGenRVSmallViewHolder(
        internal val binding: ImageGenRvSmallItemBinding
        ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGenRVSmallViewHolder {
        val binding = ImageGenRvSmallItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageGenRVSmallViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ImageGenRVSmallViewHolder, position: Int) {
        val imageData = dataset[position]

        holder.binding.ivGenImage.setImageBitmap(viewModel.decodeImage(imageData.image))

        holder.binding.ivGenImage.setOnClickListener { ivGenImage ->

            ivGenImage.findNavController().navigate(ImageGenRVSmallFragmentDirections
                .actionImageGenRVSmallFragmentToImageGenRVDetailFragment(
                    imageID = imageData.id
                )
            )
        }
    }
}