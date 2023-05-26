package com.back.frapuse.util.adapter.textgen

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.data.textgen.models.TextGenAttachments
import com.back.frapuse.databinding.ImageGenRvSmallItemBinding
import com.back.frapuse.ui.textgen.TextGenViewModel
import java.io.File

class TextGenRVDocumentOperationAdapter(
    private var dataset: MutableList<TextGenAttachments>,
    private val viewModel: TextGenViewModel,
) : RecyclerView.Adapter<TextGenRVDocumentOperationAdapter.TextGenRVDocumentOperationViewHolder>() {
    inner class TextGenRVDocumentOperationViewHolder(
        internal val binding: ImageGenRvSmallItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TextGenRVDocumentOperationViewHolder {
        val binding = ImageGenRvSmallItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TextGenRVDocumentOperationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: TextGenRVDocumentOperationViewHolder, position: Int) {
        val document = dataset[position]

        // Create a file with attachment file
        val file = File(document.path)

        // Set file name
        holder.binding.mtvFileName.text = file.nameWithoutExtension

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
        holder.binding.sivPreview.setImageBitmap(bitmap)

        // Close pdf page and renderer
        pdfPage.close()
        pdfRenderer.close()
    }
}