package com.back.frapuse.ui.textgen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.back.frapuse.data.textgen.models.TextGenAttachments
import com.back.frapuse.databinding.FragmentTextGenDocumentOperationBinding
import com.back.frapuse.util.adapter.textgen.TextGenRVAttachmentAdapter
import com.back.frapuse.util.adapter.textgen.TextGenRVDocumentOperationAdapter


class TextGenDocumentOperationFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()
    // Declaration of binding
    private lateinit var binding: FragmentTextGenDocumentOperationBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextGenDocumentOperationBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Parse activityResultRegistry to viewModel for the pdf contract
        viewModel.registerPickPdfContract(requireActivity().activityResultRegistry)

        binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
            dataset = emptyList(),
            viewModel = viewModel
        )

        // Observe pdf path and update the recycler view with current document
        viewModel.pdfPath.observe(viewLifecycleOwner) { newFilePath ->
            if (newFilePath.isNotEmpty()) {
                binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                    dataset = listOf(
                        TextGenAttachments(
                            id = viewModel.documentID.value!!,
                            path = newFilePath
                        )
                    ),
                    viewModel = viewModel
                )
                viewModel.setDocumentID()
                binding.btnStartOperation.visibility = View.VISIBLE
            }
        }

        // On click listener for start operation button
        binding.btnStartOperation.setOnClickListener {
            viewModel.insertOperationDocument()
        }

        // Observe current page
        viewModel.currentPage.observe(viewLifecycleOwner) { latestPage ->
            try {
                if (latestPage >= 0) {
                    viewModel.insertOperationStep("Convert page...")
                }
            } catch (e: Exception) {
                Log.e("TextGenDocumentOperationFragment", "Got called from:\n\tviewModel.currentPage.observe(viewLifecycleOwner)")
            }
        }

        // Reset operation library
        binding.btnReset.setOnClickListener { btnReset ->
            viewModel.deleteOperationLibrary()
            btnReset.visibility = View.GONE
            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                dataset = emptyList(),
                viewModel = viewModel
            )
            binding.rvAttachmentPreview.visibility = View.VISIBLE
        }

        // Operation library set recycler view content whenever operation library changes
        viewModel.operationLibrary.observe(viewLifecycleOwner) { operationLibrary ->
            binding.rvDocumentOperation.adapter = TextGenRVDocumentOperationAdapter(
                dataset = operationLibrary,
                viewModelTextGen = viewModel
            )
            binding.rvDocumentOperation.smoothScrollToPosition(
                viewModel.operationLibrary.value!!.size
            )
            binding.rvDocumentOperation.setHasFixedSize(true)
        }

        // Observe stream from server and create final output
        var finalOutput = ""
        viewModel.streamResponseMessage.observe(viewLifecycleOwner) { streamResponseMessage ->
            when (streamResponseMessage.event) {
                "text_stream" -> {
                    finalOutput += streamResponseMessage.text
                    binding.rvDocumentOperation.smoothScrollToPosition(
                        viewModel.operationLibrary.value!!.size
                    )
                }
                "stream_end" -> {
                    viewModel.updateAIResponseOperation(
                        viewModel.operationLibrary.value!!.last().id,
                        finalOutput.drop(1)
                    )
                    viewModel.resetStream()
                }
                "waiting" -> {
                    finalOutput = ""
                }
            }
        }
    }
}