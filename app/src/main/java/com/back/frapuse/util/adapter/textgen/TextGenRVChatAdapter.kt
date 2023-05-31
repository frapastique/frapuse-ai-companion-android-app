package com.back.frapuse.util.adapter.textgen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.textgen.TextGenViewModel
import com.back.frapuse.data.textgen.models.TextGenChatLibrary
import com.back.frapuse.databinding.TextGenRvAiAttachmentFileItemBinding
import com.back.frapuse.databinding.TextGenRvChatAiItemBinding
import com.back.frapuse.databinding.TextGenRvChatEmptyItemBinding
import com.back.frapuse.databinding.TextGenRvChatHumanAttachmentItemBinding
import com.back.frapuse.databinding.TextGenRvChatHumanItemBinding
import com.back.frapuse.databinding.TextGenRvChatInstructionItemBinding
import com.back.frapuse.databinding.TextGenRvChatOperationStepItemBinding
import com.back.frapuse.ui.textgen.TextGenChatFragmentDirections
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
        private const val TYPE_AI_ATTACHMENT_FILE = 4
        private const val TYPE_OPERATION_STEP = 5
        private const val TYPE_DATABASE_AGENT = 6
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

    inner class TextGenRVChatAIAttachmentFileViewHolder(
        internal val binding: TextGenRvAiAttachmentFileItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVChatOperationStepViewHolder(
        internal val binding: TextGenRvChatOperationStepItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVChatEmptyViewHolder(
        internal val binding: TextGenRvChatEmptyItemBinding
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
            TYPE_AI_ATTACHMENT_FILE -> {
                val binding = TextGenRvAiAttachmentFileItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatAIAttachmentFileViewHolder(binding)
            }
            TYPE_OPERATION_STEP -> {
                val binding = TextGenRvChatOperationStepItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatOperationStepViewHolder(binding)
            }
            TYPE_DATABASE_AGENT -> {
                val binding = TextGenRvChatEmptyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVChatEmptyViewHolder(binding)
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
                if (chat.sentDocument.isNotEmpty()) {
                    holder.binding.mcvFile.visibility = View.VISIBLE
                    holder.binding.tvFileName.text = File(chat.sentDocument).name
                    holder.binding.tvFileInfoHuman.text = chat.tokens + " - " + chat.dateTime
                }

                // Long click listener on attachment for navigation to attachment fragment
                holder.binding.clChatHumanAttachmentWholeItem.setOnLongClickListener { item ->
                    viewModelTextGen.setCurrentChatMessage(chat.ID)
                    item.findNavController().navigate(
                        TextGenChatFragmentDirections
                            .actionTextGenChatFragmentToTextGenAttachmentChatFragment()
                    )
                    true
                }
            }

            is TextGenRVChatAIViewHolder -> {
                when(chat.status) {
                    false -> {
                        viewModelTextGen.finalStreamResponse.observe(
                            holder.itemView.context as LifecycleOwner
                        ) { finalStreamResponse ->
                            holder.binding.tvMessageTextAi.text = finalStreamResponse.drop(1)
                            holder.binding.tvMessageInfoAi.text =
                                viewModelTextGen.model.value!!.result +
                                        " - " +
                                        viewModelTextGen
                                            .streamResponseMessage.value
                                            ?.message_num?.plus(1) +
                                        " - " +
                                        chat.dateTime
                        }
                    }
                    true -> {
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

            is TextGenRVChatAIAttachmentFileViewHolder -> {
                if (chat.sentDocument.isNotEmpty()) {
                    holder.binding.mcvFile.visibility = View.VISIBLE
                    holder.binding.tvFileName.text = File(chat.sentDocument).nameWithoutExtension
                    holder.binding.mtvFileInfoAi.text = chat.dateTime

                    // Set file name to scroll horizontally if it doesn't fit in one line
                    holder.binding.tvFileName.isSelected = true
                    holder.binding.tvFileName.requestFocus()
                }

                // Long click listener on attachment for navigation to attachment fragment
                holder.binding.clChatAiAttachmentWholeItem.setOnLongClickListener { item ->
                    viewModelTextGen.setCurrentChatMessage(chat.ID)
                    item.findNavController().navigate(
                        TextGenChatFragmentDirections
                            .actionTextGenChatFragmentToTextGenAttachmentChatFragment()
                    )
                    true
                }
            }

            is TextGenRVChatOperationStepViewHolder -> {
                holder.binding.tvCurrentOperationStep.text = chat.message
                when(chat.status) {
                    false -> {
                        holder.binding.pbOperationRunning.visibility = View.VISIBLE
                        holder.binding.sivOperationDone.visibility = View.GONE
                    }
                    true -> {
                        holder.binding.pbOperationRunning.visibility = View.GONE
                        holder.binding.sivOperationDone.visibility = View.VISIBLE
                    }
                }
            }

            is TextGenRVChatEmptyViewHolder -> {  } // Prevents spawning agents response
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
            "AI Attachment" -> { // AI attachment
                TYPE_AI_ATTACHMENT_FILE
            }
            "Operation" -> { // Current operation
                TYPE_OPERATION_STEP
            }
            else -> { // Database Agent
                TYPE_DATABASE_AGENT
            }
        }
    }
}