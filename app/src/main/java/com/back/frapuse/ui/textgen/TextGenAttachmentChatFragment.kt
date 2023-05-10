package com.back.frapuse.ui.textgen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.databinding.FragmentTextGenAttachmentChatBinding
import java.io.File

class TextGenAttachmentChatFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()
    // Declaration of binding
    private lateinit var binding: FragmentTextGenAttachmentChatBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextGenAttachmentChatBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Grab current chat message
        viewModel.currentChatMessage.observe(viewLifecycleOwner) { currentChatMessage ->
            binding.tvMessageName.text = currentChatMessage.name + ":"
            binding.tvMessageText.text = currentChatMessage.message
            binding.tvMessageInfo.text = currentChatMessage.tokens + " - " + currentChatMessage.dateTime
            binding.tvExtractedText.text = currentChatMessage.documentText

            // Create a file with sent document
            val file = File(currentChatMessage.sentDocument)

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
            binding.sivAttachmentPreview.setImageBitmap(bitmap)

            // Set the token count of extracted text
            viewModel.getTokenCount(currentChatMessage.documentText)
            viewModel.count.observe(viewLifecycleOwner) { count ->
                binding.tvExtractedTextTokenCount.text = "Tokens: $count"
            }

            // Close pdf page and renderer
            pdfPage.close()
            pdfRenderer.close()
        }
    }
}