package com.back.frapuse.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.imagegen.ImageGenViewModel
import com.back.frapuse.data.imagegen.models.ImageMetadata
import com.back.frapuse.databinding.ImageGenRvSmallItemBinding

class HomeBackgroundAdapter(
    // ViewModel to interact with shared methods
    private val viewModel: ImageGenViewModel,
    // Dataset which provides the wanted data
    private val dataset: List<ImageMetadata>
) : RecyclerView.Adapter<HomeBackgroundAdapter.HomeBackgroundViewHolder>() {

    inner class HomeBackgroundViewHolder(internal val binding: ImageGenRvSmallItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBackgroundViewHolder {
        val binding = ImageGenRvSmallItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeBackgroundViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: HomeBackgroundViewHolder, position: Int) {
        val imageData = dataset.reversed()[position]
        holder.binding.ivGenImage.setImageBitmap(viewModel.decodeImage(imageData.image))
    }

}