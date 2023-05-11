package com.back.frapuse.util.adapter.imagegen

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.imagegen.ImageGenViewModel
import com.back.frapuse.data.imagegen.models.ImageMetadata
import com.back.frapuse.databinding.ImageGenRvDetailItemBinding
import com.back.frapuse.ui.imagegen.ImageGenRVDetailFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ImageGenRVDetailAdapter(
    // Prepare an imageID
    private var imageID: Int,
    // ViewModel to interact with shared methods
    private val viewModel: ImageGenViewModel,
    // Dataset which provides the wanted data
    private var dataset: List<ImageMetadata>,
    // Set context
    private val context: Context
) : RecyclerView.Adapter<ImageGenRVDetailAdapter.ImageGenRVDetailViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<ImageMetadata>) {
        dataset = list
        notifyDataSetChanged()
    }

    inner class ImageGenRVDetailViewHolder(
        internal val binding: ImageGenRvDetailItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGenRVDetailViewHolder {
        val binding = ImageGenRvDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageGenRVDetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ImageGenRVDetailViewHolder, position: Int) {
        val imageMetadata = dataset[position]

        var shareButtonVisibility = false

        holder.binding.ivTextToImage.setOnClickListener {
            shareButtonVisibility = !shareButtonVisibility
            if (shareButtonVisibility) {
                holder.binding.btnShare.visibility = View.VISIBLE
            } else {
                holder.binding.btnShare.visibility = View.GONE
            }
        }

        viewModel.setImageViewParams(holder.binding.ivTextToImage)
        holder.binding.ivTextToImage.setImageBitmap(viewModel.decodeImage(imageMetadata.image))

        holder.binding.ivTextToImage.setOnLongClickListener { ivTextToImage ->
            MaterialAlertDialogBuilder(context)
                .setTitle("Options")
                .setMessage("Choose between delete image or get back to generator.")
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Delete Image")
                        .setMessage("Are you sure you want to delete the image?")
                        .setNeutralButton("Cancel") { _, _ -> }
                        .setNegativeButton("Delete") { _, _ ->
                            viewModel.deleteImage(imageMetadata.id)
                        }
                        .show()
                }
                .setPositiveButton("Generator") { _, _ ->
                    viewModel.setImageMetadata(imageMetadata.id)
                    ivTextToImage.findNavController()
                        .navigate(ImageGenRVDetailFragmentDirections
                            .actionImageGenRVDetailFragmentToImageGenTextToImageFragment()
                        )
                }
                .show()
            true
        }
    }
}