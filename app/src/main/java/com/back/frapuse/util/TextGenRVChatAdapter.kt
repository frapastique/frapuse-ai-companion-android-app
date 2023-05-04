package com.back.frapuse.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGeneViewModel
import com.back.frapuse.R
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.data.datamodels.textgen.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvChatItemBinding

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

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TextGenRVChatViewHolder, position: Int) {
        val chat = dataset[position]

        holder.binding.tvSentFrom.text = chat.name
        holder.binding.tvMessageText.text = chat.message

        if (chat.name == "AI") {
            holder.binding.clChatItem.setBackgroundColor(
                R.color.reddish_700
            )
        }

        /*holder.binding.sivProfilePicture.setImageBitmap(
            viewModelImageGen.decodeImage(chat.profilePicture)
        )*/
    }
}