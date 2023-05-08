package com.back.frapuse.ui.textgen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.back.frapuse.AppStatus
import com.back.frapuse.R
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.databinding.FragmentTextGenBinding
import com.back.frapuse.util.TextGenRVChatAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        binding.btnSend.setOnClickListener {
            var filePath = ""
            // observe the LiveData variable from the viewmodel
            viewModel.filePathLiveData.observe(viewLifecycleOwner) { newFilePath ->
                filePath = newFilePath
            }
            viewModel.setNextPrompt(prompt, filePath)
            binding.etPrompt.setText("")
            viewModel.resetFilePath()
            binding.btnAttachment.visibility = View.VISIBLE
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
                .setNeutralButton("Cancel") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Document") { dialog, which ->
                    // Respond to positive button press
                    viewModel.launchPickPdf()
                }
                .setPositiveButton("Image") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
        }
    }
}
