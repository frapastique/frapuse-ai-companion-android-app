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
        viewModel.documentDataset.observe(viewLifecycleOwner) { documents ->
            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                dataset = documents.toList(),
                viewModel = viewModel
            )
            viewModel.setDocumentID()
            binding.tvDocumentCount.text = documents.size.toString()
        }

        // On click listener for start operation button
        binding.btnStartOperation.setOnClickListener {
            viewModel.setCurrentDocument(viewModel.documentDataset.value!!.first())
            viewModel.insertOperationDocument()
        }

        // Observe current page
        viewModel.currentPage.observe(viewLifecycleOwner) { latestPage ->
            try {
                if (latestPage >= 0) {
                    viewModel.insertOperationStep("Convert page...")
                }
            } catch (e: Exception) {
                Log.e(
                    "TextGenDocumentOperationFragment",
                    "Got called from:\n\tviewModel.currentPage.observe(viewLifecycleOwner)"
                )
            }
        }

        // Reset operation library
        binding.btnReset.setOnClickListener {
            viewModel.deleteOperationLibrary()
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
            binding.rvDocumentOperation.scrollToPosition(
                viewModel.operationLibrary.value!!.size - 1
            )
            binding.rvDocumentOperation.setHasFixedSize(true)
        }

        // Observe final stream response and adjust recyclerview position
        viewModel.finalStreamResponse.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rvDocumentOperation.smoothScrollToPosition(
                    viewModel.operationLibrary.value!!.size
                )
            }
        }
    }
}