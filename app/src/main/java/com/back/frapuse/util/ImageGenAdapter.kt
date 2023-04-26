package com.back.frapuse.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGenerationViewModel
import com.back.frapuse.data.datamodels.ImageMetadata
import com.back.frapuse.databinding.ImageGenItemBinding
import com.back.frapuse.ui.imagegen.ImageGenRecyclerViewFragmentDirections

class ImageGenAdapter(
    // ViewModel to interact with shared methods
    private val viewModel: ImageGenerationViewModel,
    // Dataset which provides the wanted data
    private val dataset: List<ImageMetadata>
) : RecyclerView.Adapter<ImageGenAdapter.ImageGenViewHolder>() {
    inner class ImageGenViewHolder(internal val binding: ImageGenItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGenViewHolder {
        val binding = ImageGenItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageGenViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ImageGenViewHolder, position: Int) {
        val imageData = dataset[position]

        holder.binding.ivGenImage.setImageBitmap(viewModel.decodeImage(imageData.image))

        holder.binding.ivGenImage.setOnClickListener { ivGenImage ->
            ivGenImage.findNavController().navigate(ImageGenRecyclerViewFragmentDirections
                .actionImageGenRecyclerViewFragmentToImageGenDetailFragment(
                    imageID = imageData.id
                )
            )
        }
    }
}