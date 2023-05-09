package com.back.frapuse.ui.textgen

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.back.frapuse.AppStatus
import com.back.frapuse.R
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.data.datamodels.textgen.TextGenAttachments
import com.back.frapuse.databinding.FragmentTextGenBinding
import com.back.frapuse.util.TextGenRVAttachmentAdapter
import com.back.frapuse.util.TextGenRVChatAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

private const val TAG = "TextGenFragment"

class TextGenFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentTextGenBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getAllChats()

        binding = FragmentTextGenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //binding.tvInstructionsText.text = viewModel.instructionsPrompt.value

        binding.topAppBar.setNavigationOnClickListener { btnBack ->
            btnBack.findNavController().navigate(TextGenFragmentDirections
                .actionTextGenFragmentToHomeFragment()
            )
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_settings -> {
                    /*findNavController().navigate(TextGenFragmentDirections
                        .actionTextGenFragmentToTextGenSettingsFragment()
                    )*/
                    true
                }
                else -> false
            }
        }

        var prompt = ""
        binding.etPrompt.addTextChangedListener { newPrompt ->
            if (!newPrompt.isNullOrEmpty()) {
                prompt = newPrompt.toString()
            } else {
                prompt = ""
            }

            if (prompt.isEmpty()) {
                binding.btnAttachment.visibility = View.VISIBLE

                binding.btnSend.isClickable = false
                binding.btnSend.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            androidx.cardview.R.color.cardview_dark_background
                        )
                    )
                binding.btnSend.setImageResource(
                    R.drawable.bolt_trianglebadge_exclamationmark_white
                )
            } else {
                binding.btnAttachment.visibility = View.GONE

                binding.btnSend.isClickable = true
                binding.btnSend.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_200
                        )
                    )
                binding.btnSend.setImageResource(R.drawable.wand_and_stars_white)
            }
        }

        var filePath = ""
        binding.btnSend.setOnClickListener {
            binding.rvAttachmentPreview.visibility = View.GONE
            if (filePath.isNotEmpty()) {
                binding.etPrompt.setTextColor (ContextCompat.getColor (requireContext(), R.color.black))
                val file = File(filePath)
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

                viewModel.extractText(bitmap)

                pdfPage.close()
                pdfRenderer.close()

                viewModel.textOut.observe(viewLifecycleOwner) { textOut ->
                    if (textOut.isNotEmpty()) {
                        viewModel.setNextPrompt(prompt, filePath)
                        binding.etPrompt.setText("")
                        binding.etPrompt.setTextColor (ContextCompat.getColor (requireContext(), R.color.white))
                        binding.btnAttachment.visibility = View.VISIBLE
                        viewModel.resetFilePath()
                        filePath = ""
                    }
                }
            } else {
                viewModel.setNextPrompt(prompt, filePath)
                binding.etPrompt.setText("")
                binding.btnAttachment.visibility = View.VISIBLE
                filePath = ""
            }
        }

        binding.btnSend.setOnLongClickListener {
            viewModel.deleteChatLibrary()
            viewModel.deleteAllPdf(requireContext())

            true
        }

        viewModel.chatLibrary.observe(viewLifecycleOwner) { chatLibrary ->
            val chatAdapter = TextGenRVChatAdapter(
                dataset = chatLibrary,
                viewModelTextGen = viewModel
            )
            binding.rvChatLibrary.adapter = chatAdapter
            chatAdapter.submitList(chatLibrary)
            binding.rvChatLibrary.scrollToPosition(chatLibrary.size - 1)
            binding.rvChatLibrary.setHasFixedSize(true)
        }

        viewModel.tokenCount.observe(viewLifecycleOwner) { count ->
            binding.tvTokens.text = count
        }

        binding.tiPrompt.setOnClickListener {
            binding.rvChatLibrary.smoothScrollToPosition(viewModel.chatLibrary.value!!.size - 1)
        }

        viewModel.apiStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                AppStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSend.isClickable = false
                    binding.btnSend.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                androidx.cardview.R.color.cardview_dark_background
                            )
                        )
                    binding.btnSend.setImageResource(
                        R.drawable.clock_arrow_circlepath_white
                    )
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSend.isClickable = true
                    binding.btnSend.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.purple_200
                            )
                        )
                    binding.btnSend.setImageResource(
                        R.drawable.arrow_trianglepath_white
                    )
                }
            }
        }

        viewModel.registerPickPdfContract(requireActivity().activityResultRegistry)

        binding.btnAttachment.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Attachment")
                .setMessage("Choose to attach a document or an image.")
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Document") { _, _ ->
                    // Respond to positive button press
                    viewModel.launchPickPdf()
                    viewModel.filePathLiveData.observe(viewLifecycleOwner) { newFilePath ->
                        if (newFilePath.isNotEmpty()) {
                            filePath = newFilePath
                            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                                listOf(
                                    TextGenAttachments(
                                        attachmentID = 0,
                                        attachmentFile = filePath
                                    )
                                )
                            )
                            binding.rvAttachmentPreview.visibility = View.VISIBLE
                        } else {
                            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                                emptyList()
                            )
                            binding.rvAttachmentPreview.visibility = View.GONE
                        }
                    }
                }
                .setPositiveButton("Image") { _, _ ->
                    // Respond to positive button press
                }
                .show()
        }
    }
}
