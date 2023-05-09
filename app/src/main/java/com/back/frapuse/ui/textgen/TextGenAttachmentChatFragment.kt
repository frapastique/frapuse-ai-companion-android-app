package com.back.frapuse.ui.textgen

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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

class TextGenAttachmentChatFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentTextGenAttachmentChatBinding

    private var pdf = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pdf = it.getString("pdf").toString()
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val file = File(pdf)
        // create a PdfRenderer from the file
        val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        // get the first page of the PDF file
        val pdfPage = pdfRenderer.openPage(0)
        // create a bitmap with the same size and config as the page
        val bitmap = Bitmap.createBitmap(pdfPage.width, pdfPage.height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        // render the page content to the bitmap
        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                binding.tvExtractedText.text = processTextBlock(visionText)
            }
            .addOnFailureListener { e ->
                binding.tvExtractedText.text = e.toString()
            }

        pdfPage.close()
        pdfRenderer.close()
    }

    private fun processTextBlock(result: Text): String {
        val textBlocks = result.textBlocks
        if (textBlocks.size == 0) {
            return "No text found"
        }
        val stringBuilder = StringBuilder()
        for (block in textBlocks) {
            stringBuilder.append("\n\n")
            val lines = block.lines
            for (line in lines) {
                val elements = line.elements
                for (element in elements) {
                    val elementText = element.text
                    stringBuilder.append("$elementText ")
                }
            }
        }
        return stringBuilder.toString()
    }
}