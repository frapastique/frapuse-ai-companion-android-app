package com.back.frapuse.util.adapter.textgen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.textgen.TextGenViewModel
import com.back.frapuse.data.textgen.models.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvChatAiAttachmentItemBinding
import com.back.frapuse.databinding.TextGenRvChatAiItemBinding
import com.back.frapuse.databinding.TextGenRvChatHumanAttachmentItemBinding
import com.back.frapuse.databinding.TextGenRvChatHumanItemBinding
import com.back.frapuse.databinding.TextGenRvChatInstructionItemBinding
import com.back.frapuse.ui.textgen.TextGenFragmentDirections
import java.io.File

class TextGenRVChatAdapter(
    private var dataset: List<TextGenChatLibrary>,
    private val viewModelTextGen: TextGenViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Companion object defines type of item in dataset
    companion object {
        private const val TYPE_INSTRUCTION = 0
        private const val TYPE_HUMAN_MESSAGE = 1
        private const val TYPE_HUMAN_ATTACHMENT = 2
        private const val TYPE_AI_MESSAGE = 3
        private const val TYPE_AI_ATTACHMENT = 4
    }

    inner class TextGenRVChatInstructionViewHolder(
        internal val binding: TextGenRvChatInstructionItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVChatHumanViewHolder(
        internal val binding: TextGenRvChatHumanItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVChatHumanAttachmentViewHolder(
        internal val binding: TextGenRvChatHumanAttachmentItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVChatAIViewHolder(
        internal val binding: TextGenRvChatAiItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVChatAIAttachmentViewHolder(
        internal val binding: TextGenRvChatAiAttachmentItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Append view holder according the view type
        return when (viewType) {
            TYPE_INSTRUCTION -> {
                val binding = TextGenRvChatInstructionItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatInstructionViewHolder(binding)
            }
            TYPE_HUMAN_MESSAGE -> {
                val binding = TextGenRvChatHumanItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatHumanViewHolder(binding)
            }
            TYPE_HUMAN_ATTACHMENT -> {
                val binding = TextGenRvChatHumanAttachmentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatHumanAttachmentViewHolder(binding)
            }
            TYPE_AI_MESSAGE -> {
                val binding = TextGenRvChatAiItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatAIViewHolder(binding)
            }
            TYPE_AI_ATTACHMENT -> {
                val binding = TextGenRvChatAiAttachmentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatAIAttachmentViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = dataset[position]

        when (holder) {
            is TextGenRVChatInstructionViewHolder -> {
                holder.binding.tvInstructionsText.text = chat.message
                holder.binding.tvInstructionsInfo.text = chat.tokens + " - " + chat.dateTime
            }

            is TextGenRVChatHumanViewHolder -> {
                holder.binding.tvMessageTextHuman.text = chat.message
                holder.binding.tvMessageInfoHuman.text = chat.tokens + " - " + chat.dateTime
            }

            is TextGenRVChatHumanAttachmentViewHolder -> {
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
                // Set bitmap background color
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

                // Long click listener on attachment for navigation to attachment fragment
                holder.binding.sivSentContent.setOnLongClickListener { sivSentContent ->
                    viewModelTextGen.setCurrentChatMessage(chat.ID)
                    sivSentContent.findNavController().navigate(TextGenFragmentDirections
                        .actionTextGenFragmentToTextGenAttachmentChatFragment()
                    )
                    true
                }
            }

            is TextGenRVChatAIViewHolder -> {
                when {
                    chat.message.isEmpty() -> {
                        var previousAIMessage = ""
                        viewModelTextGen.streamResponseMessage.observe(
                            holder.itemView.context as LifecycleOwner
                        ) { streamResponseMessage ->
                            if (streamResponseMessage.event == "text_stream") {
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
                    else -> {
                        holder.binding.tvMessageTextAi.text = chat.message
                        holder.binding.tvMessageInfoAi.text =
                            viewModelTextGen.model.value!!.result +
                                    " - " +
                                    chat.tokens +
                                    " - " +
                                    chat.dateTime
                    }
                }
            }

            is TextGenRVChatAIAttachmentViewHolder -> {
                TODO()
            }
        }
    }

    // Set view type according to position of the view
    override fun getItemViewType(position: Int): Int {
        return when (dataset[position].type) {
            "Instructions" -> { // Instruction text
                TYPE_INSTRUCTION
            }
            "Human" -> { // Human message text
                TYPE_HUMAN_MESSAGE
            }
            "Human Attachment" -> { // Human attachment
                TYPE_HUMAN_ATTACHMENT
            }
            "AI" -> { // AI message text
                TYPE_AI_MESSAGE
            }
            else -> { // AI attachment
                TYPE_AI_ATTACHMENT
            }
        }
    }
}