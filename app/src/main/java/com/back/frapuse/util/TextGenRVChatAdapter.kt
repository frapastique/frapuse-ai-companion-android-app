package com.back.frapuse.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.data.datamodels.textgen.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvChatItemBinding
import java.io.File

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

        if (position == 0 && chat.name == "Human") {
            holder.binding.clChatInstructions.visibility = View.VISIBLE
            holder.binding.tvInstructionsText.text = viewModelTextGen.instructionsPrompt.value
        }

        if (chat.name == "Human") {
            holder.binding.clChatHuman.visibility = View.VISIBLE
            holder.binding.clChatAi.visibility = View.GONE
            holder.binding.tvMessageTextHuman.text = chat.message
            holder.binding.tvMessageInfoHuman.text = chat.tokens + " - " + chat.dateTime
            if (chat.sentDocument.isNotEmpty()) {
                val file = File(chat.sentDocument)
                // create a PdfRenderer from the file
                val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                // get the first page of the PDF file
                val pdfPage = pdfRenderer.openPage(0)
                // create a bitmap with the same size and config as the page
                val bitmap = Bitmap.createBitmap(pdfPage.width, pdfPage.height, Bitmap.Config.ARGB_8888)
                // render the page content to the bitmap
                pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                // set the bitmap to the ImageView
                holder.binding.sivSentContent.setImageBitmap(bitmap)
                // close the page and the renderer
                pdfPage.close()
                pdfRenderer.close()
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