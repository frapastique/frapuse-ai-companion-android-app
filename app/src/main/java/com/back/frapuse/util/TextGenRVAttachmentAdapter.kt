package com.back.frapuse.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.back.frapuse.data.datamodels.textgen.TextGenAttachments
import com.back.frapuse.databinding.TextGenRvAttachmentFooterBinding
import com.back.frapuse.databinding.TextGenRvAttachmentItemBinding
import java.io.File

class TextGenRVAttachmentAdapter(
    private var dataset: List<TextGenAttachments>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitAttachmentList(list: List<TextGenAttachments>) {
        dataset = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
            val file = File(attachment.attachmentFile)
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
            // set the bitmap to the ImageView
            holder.binding.sivAttachmentPreview.setImageBitmap(bitmap)
            // close the page and the renderer
            pdfPage.close()
            pdfRenderer.close()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == dataset.size) { // last position
            TYPE_FOOTER
        } else {
            TYPE_ITEM
        }
    }

    inner class TextGenRVAttachmentItemViewHolder(
        internal val binding: TextGenRvAttachmentItemBinding
        ) : RecyclerView.ViewHolder(binding.root)

    inner class TextGenRVAttachmentFooterViewHolder(
        internal val binding: TextGenRvAttachmentFooterBinding
        ) : RecyclerView.ViewHolder(binding.root)
}