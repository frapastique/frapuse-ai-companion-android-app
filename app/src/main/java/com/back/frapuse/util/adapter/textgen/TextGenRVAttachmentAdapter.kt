package com.back.frapuse.util.adapter.textgen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.ui.textgen.TextGenViewModel
import com.back.frapuse.data.textgen.models.TextGenAttachments
import com.back.frapuse.databinding.TextGenRvAttachmentFooterBinding
import com.back.frapuse.databinding.TextGenRvAttachmentItemBinding
import java.io.File

class TextGenRVAttachmentAdapter(
    private var dataset: List<TextGenAttachments>,
    private val viewModel: TextGenViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Companion object defines type of item in dataset
    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    // Method to update current dataset
    @SuppressLint("NotifyDataSetChanged")
    fun submitAttachmentList(newDataset: List<TextGenAttachments>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

    // Class for attachment item view holder, holds binding property
    inner class TextGenRVAttachmentItemViewHolder(
        internal val binding: TextGenRvAttachmentItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVAttachmentFooterViewHolder(
        internal val binding: TextGenRvAttachmentFooterBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Append view holder according the view type
        return when (viewType) {
            TYPE_ITEM -> {
                val binding = TextGenRvAttachmentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVAttachmentItemViewHolder(binding)
            }
            TYPE_FOOTER -> {
                val binding = TextGenRvAttachmentFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextGenRVAttachmentFooterViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataset.size + 1 // one more for the footer
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextGenRVAttachmentItemViewHolder) {
            val attachment = dataset[position]

            // Create a file with attachment file
            val file = File(attachment.file)

            // Create a PdfRenderer from the file
            val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
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

            // Set the bitmap to the ImageView
            holder.binding.sivAttachmentPreview.setImageBitmap(bitmap)

            // Parse the bitmap to the viewModel
            viewModel.setPdfBitmap(bitmap)

            // Extract text from pdf
            viewModel.extractText()

            // Close pdf page and renderer
            pdfPage.close()
            pdfRenderer.close()
        }
    }

    // Set view type according to position of the view
    override fun getItemViewType(position: Int): Int {
        return if (position == dataset.size) { // last position
            TYPE_FOOTER
        } else {
            TYPE_ITEM
        }
    }
}