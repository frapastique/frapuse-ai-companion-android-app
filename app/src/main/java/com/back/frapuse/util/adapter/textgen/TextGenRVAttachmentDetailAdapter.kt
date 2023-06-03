package com.back.frapuse.util.adapter.textgen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.databinding.TextGenRvAttachmentDetailItemBinding
import com.back.frapuse.ui.textgen.TextGenViewModel
import java.io.File

class TextGenRVAttachmentDetailAdapter(
    private val viewModel: TextGenViewModel,
    private val pageCount: Int,
    private val filepath: String
) : RecyclerView.Adapter<TextGenRVAttachmentDetailAdapter.TextGenRVAttachmentDetailViewHolder>() {

    inner class TextGenRVAttachmentDetailViewHolder(
        internal val binding: TextGenRvAttachmentDetailItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TextGenRVAttachmentDetailViewHolder {
        val binding = TextGenRvAttachmentDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TextGenRVAttachmentDetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pageCount
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TextGenRVAttachmentDetailViewHolder, position: Int) {
        holder.binding.tvExtractedText.text
        holder.binding.tvPageCount.text = "${position+1}/$pageCount"

        // Create a file with sent document
        val file = File(filepath)

        // Create a PdfRenderer from the file
        val parcelFileDescriptor =
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

        // Get the first page of the PDF file
        val pdfPage = pdfRenderer.openPage(position)

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
        holder.binding.sivAttachmentPreview.setImageBitmap(bitmap)
    }
}