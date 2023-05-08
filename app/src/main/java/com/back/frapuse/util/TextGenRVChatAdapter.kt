package com.back.frapuse.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ImageGeneViewModel
import com.back.frapuse.R
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.data.datamodels.textgen.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvChatItemBinding

class TextGenRVChatAdapter(
    private var dataset: List<TextGenChatLibrary>,
    private val viewModelTextGen: TextGenViewModel,
    var pdfBitmap: Bitmap? = null
   // private val viewModelImageGen: ImageGeneViewModel
) : RecyclerView.Adapter<TextGenRVChatAdapter.TextGenRVChatViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<TextGenChatLibrary>, bitmap: Bitmap?) {
        dataset = list
        pdfBitmap = bitmap
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

        if (position == 0 && chat.name == "Human") {
            holder.binding.clChatInstructions.visibility = View.VISIBLE
            holder.binding.tvInstructionsText.text = viewModelTextGen.instructionsPrompt.value
        }

        if (chat.name == "Human") {
            holder.binding.clChatHuman.visibility = View.VISIBLE
            holder.binding.clChatAi.visibility = View.GONE
            holder.binding.tvMessageTextHuman.text = chat.message
            holder.binding.tvMessageInfoHuman.text = chat.tokens + " - " + chat.dateTime

            if (pdfBitmap != null) {
                holder.binding.sivSentContent.setImageBitmap(pdfBitmap)
            }

        } else if (chat.name == "AI") {
            holder.binding.clChatInstructions.visibility = View.GONE
            holder.binding.clChatHuman.visibility = View.GONE
            holder.binding.clChatAi.visibility = View.VISIBLE
            holder.binding.tvMessageTextAi.text = chat.message
            holder.binding.tvMessageInfoAi.text = chat.tokens + " - " + chat.dateTime
        }

        /*holder.binding.sivProfilePicture.setImageBitmap(
            viewModelImageGen.decodeImage(chat.profilePicture)
        )*/
    }
}