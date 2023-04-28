package com.back.frapuse.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGeneViewModel
import com.back.frapuse.R
import com.back.frapuse.data.datamodels.imagegen.ImageMetadata
import com.back.frapuse.databinding.ImageGenRvDetailItemBinding

class ImageGenRVDetailAdapter(
    // Prepare an imageID
    private var imageID: Long,
    // ViewModel to interact with shared methods
    private val viewModel: ImageGeneViewModel,
    // Dataset which provides the wanted data
    private val dataset: List<ImageMetadata>
) : RecyclerView.Adapter<ImageGenRVDetailAdapter.ImageGenRVDetailViewHolder>() {
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

        var infoVisible = false
        var favorite = false
        var delete = 0

        holder.binding.ivTextToImage.setOnClickListener {
            infoVisible = !infoVisible

            if (infoVisible) {
                holder.binding.clInfoPlate.visibility = View.VISIBLE
                holder.binding.clActionButtons.visibility = View.VISIBLE
            } else {
                holder.binding.clInfoPlate.visibility = View.GONE
                holder.binding.clActionButtons.visibility = View.GONE
            }
        }

        viewModel.setImageViewParams(holder.binding.ivTextToImage)
        holder.binding.tvPromptValue.text = imageMetadata.positivePrompt
        holder.binding.tvNegativePromptValue.text = imageMetadata.negativePrompt
        holder.binding.tvModelValue.text = imageMetadata.model
        holder.binding.tvSeedValue.text = imageMetadata.seed.toString()
        holder.binding.tvHeightValue.text = imageMetadata.height.toString()
        holder.binding.tvWidthValue.text = imageMetadata.width.toString()
        holder.binding.tvSamplerValue.text = imageMetadata.sampler
        holder.binding.tvStepsValue.text = imageMetadata.steps.toString()
        holder.binding.tvCfgValue.text = imageMetadata.CFGScale.toString()
        holder.binding.ivTextToImage.setImageBitmap(viewModel.decodeImage(imageMetadata.image))

        holder.binding.ivFavoriteBtn.setOnClickListener {
            favorite = !favorite
            if (favorite) {
                holder.binding.ivFavoriteBtn.setImageResource(R.drawable.star_fill_purple)
            } else {
                holder.binding.ivFavoriteBtn.setImageResource(R.drawable.star_purple)
            }
        }

        // Testing purposes
        holder.binding.ivDeleteBtn.setOnClickListener {
            delete++
            if (delete == 2) {
                viewModel.deleteImage(imageMetadata.id)
                delete = 0
            }
        }
    }
}