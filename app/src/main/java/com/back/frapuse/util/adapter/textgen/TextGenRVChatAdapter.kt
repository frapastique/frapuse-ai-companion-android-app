package com.back.frapuse.util.adapter.textgen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.textgen.TextGenViewModel
import com.back.frapuse.data.textgen.models.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvChatItemBinding
import com.back.frapuse.ui.textgen.TextGenFragmentDirections
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TextGenRVChatViewHolder, position: Int) {
        val chat = dataset[position]

        // Set visibility and content of elements according to chat properties
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
                // Create a file with sent document
                val file = File(chat.sentDocument)

                // Create a PdfRenderer from the file
                val parcelFileDescriptor =
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)

                // Get the first page of the PDF file
                val pdfPage = pdfRenderer.openPage(0)

                // Create a bitmap with the same size and config as the page
                val bitmap = Bitmap.createBitmap(
                    pdfPage.width,
                    pdfPage.height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(Color.WHITE)

                // Render the page content to the bitmap
                pdfPage.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )

                // Set bitmap into ImageView
                holder.binding.sivSentContent.setImageBitmap(bitmap)

                // Close pdf page and renderer
                pdfPage.close()
                pdfRenderer.close()
            }
        } else if (chat.name == "AI") {
            var previousAIMessage = ""

            holder.binding.clChatInstructions.visibility = View.GONE
            holder.binding.clChatHuman.visibility = View.GONE

            if (chat.message.isEmpty()) {
                viewModelTextGen.streamResponseMessage.observe(
                    holder.itemView.context as LifecycleOwner
                ) { streamResponseMessage ->
                    when (streamResponseMessage.event) {
                        "text_stream" -> {
                            holder.binding.clChatAi.visibility = View.VISIBLE
                            previousAIMessage += streamResponseMessage.text
                            holder.binding.tvMessageTextAi.text = previousAIMessage.drop(1)
                            holder.binding.tvMessageInfoAi.text =
                                viewModelTextGen.model.value!!.result +
                                        " - " +
                                        streamResponseMessage.message_num.plus(1) +
                                        " - " +
                                        chat.dateTime
                        }
                    }
                }
            } else {
                holder.binding.clChatAi.visibility = View.VISIBLE
                holder.binding.tvMessageTextAi.text = chat.message
                holder.binding.tvMessageInfoAi.text =
                    viewModelTextGen.model.value!!.result +
                            " - " +
                            chat.tokens +
                            " - " +
                            chat.dateTime
            }
        }

        // Long click listener on attachment for navigation to attachment fragment
        holder.binding.sivSentContent.setOnLongClickListener { sivSentContent ->
            viewModelTextGen.setCurrentChatMessage(chat.chatID)
            sivSentContent.findNavController().navigate(TextGenFragmentDirections
                .actionTextGenFragmentToTextGenAttachmentChatFragment()
            )
            true
        }
    }
}