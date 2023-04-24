package com.back.frapuse.util

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGenerationViewModel
import com.back.frapuse.data.datamodels.ImageMetadata
import com.back.frapuse.databinding.ImageGenItemBinding

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

        val decodedByte = Base64.decode(imageData.image, Base64.DEFAULT)
        val image = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)

        holder.binding.ivGenImage.setImageBitmap(image)
    }
}