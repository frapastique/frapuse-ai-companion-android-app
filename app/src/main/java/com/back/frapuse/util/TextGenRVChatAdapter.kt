package com.back.frapuse.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGeneViewModel
import com.back.frapuse.R
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.data.datamodels.textgen.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvChatItemBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class TextGenRVChatAdapter(
    private var dataset: List<TextGenChatLibrary>,
    private val viewModelTextGen: TextGenViewModel,
   // private val viewModelImageGen: ImageGeneViewModel
) : RecyclerView.Adapter<TextGenRVChatAdapter.TextGenRVChatViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<TextGenChatLibrary>) {
        dataset = list
        notifyDataSetChanged()
    }

    inner class TextGenRVChatViewHolder(
        internal val binding: TextGenRvChatItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextGenRVChatViewHolder {
        val binding = TextGenRvChatItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TextGenRVChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: TextGenRVChatViewHolder, position: Int) {
        val chat = dataset[position]

        holder.binding.tvSentFrom.text = chat.name
        holder.binding.tvMessageText.text = chat.message

        if (chat.name == "AI") {
            holder.binding.clChatItem.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.purple_1000
                )
            )
        } else {
            holder.binding.clChatItem.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.dark_grey
                )
            )
        }

        /*holder.binding.sivProfilePicture.setImageBitmap(
            viewModelImageGen.decodeImage(chat.profilePicture)
        )*/
    }
}