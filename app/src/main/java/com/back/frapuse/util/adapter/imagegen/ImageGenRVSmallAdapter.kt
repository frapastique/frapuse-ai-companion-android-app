package com.back.frapuse.util.adapter.imagegen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.imagegen.ImageGenViewModel
import com.back.frapuse.data.imagegen.models.ImageGenImageMetadata
import com.back.frapuse.databinding.ImageGenRvSmallItemBinding
import com.back.frapuse.ui.imagegen.ImageGenRVSmallFragmentDirections

class ImageGenRVSmallAdapter(
    // ViewModel to interact with shared methods
    private val viewModel: ImageGenViewModel,
    // Dataset which provides the wanted data
    private var dataset: List<ImageGenImageMetadata>
) : RecyclerView.Adapter<ImageGenRVSmallAdapter.ImageGenRVSmallViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<ImageGenImageMetadata>) {
        dataset = list
        notifyDataSetChanged()
    }


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
        holder.binding.sivImageGen.setImageBitmap(viewModel.decodeImage(imageData.image))
        holder.binding.sivImageGen.setOnClickListener { sivImageGen ->
            sivImageGen.findNavController().navigate(ImageGenRVSmallFragmentDirections
                .actionImageGenRVSmallFragmentToImageGenRVDetailFragment(
                    imagePosition = position
                )
            )
        }
    }
}