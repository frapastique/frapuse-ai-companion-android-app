package com.back.frapuse.ui.textgen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.back.frapuse.databinding.FragmentTextGenDocumentOperationBinding
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

        val recycler = binding.rvDocumentPreview

        viewModel.documentLibrary.observe(viewLifecycleOwner) { documentLibrary ->
            recycler.adapter = TextGenRVDocumentOperationAdapter(
                dataset = documentLibrary,
                viewModel = viewModel
            )
            recycler.scrollToPosition(documentLibrary.size - 1)
            recycler.setHasFixedSize(true)
        }

        binding.btnAddDocument.setOnClickListener {
            viewModel.launchPickPdf()
        }

        binding.btnDeleteDocuments.setOnLongClickListener {
            viewModel.deleteAllDocuments()
            true
        }
    }
}