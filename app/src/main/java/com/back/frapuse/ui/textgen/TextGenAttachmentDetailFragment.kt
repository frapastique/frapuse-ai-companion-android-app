package com.back.frapuse.ui.textgen

import android.annotation.SuppressLint
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.back.frapuse.databinding.FragmentTextGenAttachmentDetailBinding
import com.back.frapuse.util.adapter.textgen.TextGenRVAttachmentDetailAdapter
import java.io.File

class TextGenAttachmentDetailFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()
    // Declaration of binding
    private lateinit var binding: FragmentTextGenAttachmentDetailBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextGenAttachmentDetailBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getDocumentLibrary()

        // Grab current chat message
        viewModel.currentChatMessage.observe(viewLifecycleOwner) { currentChatMessage ->
            binding.tvMessageName.text = "Attachment:"
            binding.tvMessageText.text = File(currentChatMessage.sentDocument).name
            binding.tvMessageInfo.text = currentChatMessage.tokens + " - " + currentChatMessage.dateTime

            // Create a file with sent document
            val file = File(currentChatMessage.sentDocument)

            // Create a PdfRenderer from the file
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // get the number of pages in the PDF document
            val pageCount = pdfRenderer.pageCount

            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentDetailAdapter(
                viewModel = viewModel,
                pageCount = pageCount,
                filepath = currentChatMessage.sentDocument
            )

            binding.rvAttachmentPreview.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            binding.rvAttachmentPreview.setHasFixedSize(true)

            // Close pdf renderer
            pdfRenderer.close()
        }
    }
}