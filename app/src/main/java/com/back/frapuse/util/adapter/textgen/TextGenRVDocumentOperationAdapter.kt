package com.back.frapuse.util.adapter.textgen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.data.textgen.models.TextGenDocumentOperation
import com.back.frapuse.databinding.TextGenRvChatAiItemBinding
import com.back.frapuse.databinding.TextGenRvChatHumanAttachmentItemBinding
import com.back.frapuse.databinding.TextGenRvDocumentOperationStepItemBinding
import com.back.frapuse.ui.textgen.TextGenViewModel
import java.io.File

class TextGenRVDocumentOperationAdapter(
    private var dataset: List<TextGenDocumentOperation>,
    private val viewModelTextGen: TextGenViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Companion object defines type of item in dataset
    companion object {
        private const val TYPE_DOCUMENT = 0
        private const val TYPE_OPERATION = 1
        private const val TYPE_AI = 2
    }

    inner class TextGenRVDocumentOperationTypeViewHolder(
        internal val binding: TextGenRvChatHumanAttachmentItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVDocumentOperationStepViewHolder(
        internal val binding: TextGenRvDocumentOperationStepItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVDocumentOperationAIViewHolder(
        internal val binding: TextGenRvChatAiItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Append view holder according the view type
        return when (viewType) {
            TYPE_DOCUMENT -> {
                val binding = TextGenRvChatHumanAttachmentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVDocumentOperationTypeViewHolder(binding)
            }
            TYPE_OPERATION -> {
                val binding = TextGenRvDocumentOperationStepItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVDocumentOperationStepViewHolder(binding)
            }
            TYPE_AI -> {
                val binding = TextGenRvChatAiItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVDocumentOperationAIViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val operation = dataset[position]

        when (holder) {
            is TextGenRVDocumentOperationTypeViewHolder -> {
                holder.binding.tvFileName.text = File(operation.path).name
                holder.binding.tvFileInfoHuman.text =
                    operation.tokens +
                        " - " +
                        operation.dateTime
            }
            is TextGenRVDocumentOperationStepViewHolder -> {
                holder.binding.tvCurrentOperationStep.text =
                    operation.message +
                            " (${operation.currentPage+1}/${operation.pageCount})"
                when (operation.status) {
                    "Loading" -> {
                        holder.binding.pbOperationRunning.visibility = View.VISIBLE
                        holder.binding.sivOperationDone.visibility = View.GONE
                        when (operation.message) {
                            "Convert page..." -> {
                                viewModelTextGen.convertDocument(operation.id)
                            }
                            "Extract text..." -> {
                                viewModelTextGen.extractDocumentText(operation.id)
                            }
                            "Process text..." -> {
                                viewModelTextGen.processDocumentText(operation.id)
                            }
                        }
                    }
                    "Done" -> {
                        holder.binding.pbOperationRunning.visibility = View.GONE
                        holder.binding.sivOperationDone.visibility = View.VISIBLE
                    }
                }
            }
            is TextGenRVDocumentOperationAIViewHolder -> {
                when (operation.status) {
                    "Loading" -> {
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
                                            operation.dateTime
                            }
                        }
                    }
                    "Done" -> {
                        holder.binding.tvMessageTextAi.text = operation.message
                        holder.binding.tvMessageInfoAi.text =
                            viewModelTextGen.model.value!!.result +
                                    " - " +
                                    operation.tokens +
                                    " - " +
                                    operation.dateTime
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataset[position].type) {
            "Document" -> {
                TYPE_DOCUMENT
            }
            "Operation" -> {
                TYPE_OPERATION
            }
            else -> {
                TYPE_AI
            }
        }
    }
}