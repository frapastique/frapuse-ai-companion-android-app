package com.back.frapuse.ui.textgen

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.back.frapuse.R
import com.back.frapuse.data.textgen.models.TextGenAttachments
import com.back.frapuse.databinding.FragmentTextGenChatBinding
import com.back.frapuse.ui.imagegen.ImageGenViewModel
import com.back.frapuse.util.AppStatus
import com.back.frapuse.util.adapter.textgen.TextGenRVAttachmentAdapter
import com.back.frapuse.util.adapter.textgen.TextGenRVChatAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay

private const val TAG = "TextGenFragment"

class TextGenChatFragment : Fragment() {
    // Get the text gen viewModel into the logic
    private val viewModelTextGen: TextGenViewModel by activityViewModels()
    // Get the image gen viewModel into the logic
    private val viewModelImageGen: ImageGenViewModel by activityViewModels()
    // Declaration of binding
    private lateinit var binding: FragmentTextGenChatBinding

    /**
     * Lifecycle Function onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModelTextGen.setImageGenViewModel(viewModelImageGen)
        viewModelTextGen.getAllChats()
        binding = FragmentTextGenChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Introduce local prompt variable
        var prompt = ""
        // Introduce local filePath variable
        var filePath = ""
        // Parse activityResultRegistry to viewModel for the pdf contract
        viewModelTextGen.registerPickPdfContract(requireActivity().activityResultRegistry)
        viewModelTextGen.getDocumentLibrary()

        binding.topAppBar.inflateMenu(R.menu.top_app_bar)
        // Navigate to home
        binding.topAppBar.setNavigationOnClickListener { btnBack ->
            Log.e(TAG, "TopAppBar clicked:\n\tbtnBack")
            btnBack.findNavController().navigate(TextGenChatFragmentDirections
                .actionTextGenChatFragmentToHomeFragment()
            )
        }
        // Menu item click listener for topAppBar
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_settings -> {
                    Log.e(TAG, "TopAppBar clicked:\n\tbtnSettings")
                    findNavController().navigate(TextGenChatFragmentDirections
                        .actionTextGenChatFragmentToTextGenSettingsFragment()
                    )
                    true
                }
                else -> false
            }
        }

        // Set prompt variable on EditTextView text change and set visibility and state of buttons
        binding.etPrompt.addTextChangedListener { newPrompt ->
            prompt = if (!newPrompt.isNullOrEmpty()) {
                newPrompt.toString()
            } else {
                ""
            }
            // Set the state and visibility of buttons according to prompt value
            if (prompt.isEmpty()) {
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

        // Set next prompt value in viewModel and adjust visibility of elements
        binding.btnSend.setOnClickListener {
            binding.rvAttachmentPreview.visibility = View.GONE
            if (filePath.isNotEmpty()) {
                viewModelTextGen.saveAttachment(filePath)
            }
            viewModelTextGen.setHumanContext(prompt, filePath)
            binding.etPrompt.setText("")
            filePath = ""
            viewModelTextGen.resetPdfPath()
        }

        // Execute generate block when promptStatus is done
        viewModelTextGen.createPromptStatus.observe(viewLifecycleOwner) { promptStatus ->
            when (promptStatus) {
                AppStatus.DONE -> {
                    // viewModel.generateBlock()
                    viewModelTextGen.generateStream()
                }
                else -> Log.e(TAG, "Prompt status:\n\t$promptStatus")
            }
        }

        // Empty chat history and pdf file cache
        binding.btnSend.setOnLongClickListener {
            viewModelTextGen.deleteChatLibrary()
            viewModelTextGen.deleteAllPdf(requireContext())
            true
        }

        // Set chat library on RecyclerView
        viewModelTextGen.chatLibrary.observe(viewLifecycleOwner) { chatLibrary ->
            binding.rvChatLibrary.adapter = TextGenRVChatAdapter(
                dataset = chatLibrary,
                viewModelTextGen = viewModelTextGen,
                viewModelImageGen = viewModelImageGen
            )
            binding.rvChatLibrary.scrollToPosition(chatLibrary.size-1)
            binding.rvChatLibrary.setHasFixedSize(true)
        }

        // Observe final stream response and adjust recyclerview position
        viewModelTextGen.finalStreamResponse.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rvChatLibrary.smoothScrollToPosition(
                    viewModelTextGen.chatLibrary.value!!.size
                )
            }
        }

        // Apply token count for current prompt context
        viewModelTextGen.tokenCount.observe(viewLifecycleOwner) { count ->
            binding.tvTokens.text = count
        }

        // Scroll to the latest chat message when clicking to type next message
        binding.tiPrompt.setOnClickListener {
            binding.rvChatLibrary.smoothScrollToPosition(viewModelTextGen.chatLibrary.value!!.size)
        }

        // Set state and visibility of elements according to API status
        viewModelTextGen.apiStatus.observe(viewLifecycleOwner) { status ->
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
                    binding.btnSend.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                androidx.cardview.R.color.cardview_dark_background
                            )
                        )
                    binding.btnSend.setImageResource(R.drawable.wand_and_stars_white)
                }
            }
        }

        // Open a dialog when attachment button is clicked
        binding.btnAttachment.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Attachment")
                .setMessage("Choose to attach a document or an image.")
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Document") { _, _ ->
                    // Create attachment preview in a RecyclerView
                    viewModelTextGen.launchPickPdf()
                    viewModelTextGen.pdfPath.observe(viewLifecycleOwner) { newFilePath ->
                        if (newFilePath.isNotEmpty()) {
                            filePath = newFilePath
                            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                                dataset = listOf(
                                    TextGenAttachments(
                                        id = 0,
                                        path = filePath,
                                        pageCount = 0
                                    )
                                ),
                                viewModel = viewModelTextGen
                            )
                            binding.rvAttachmentPreview.visibility = View.VISIBLE
                        } else {
                            binding.rvAttachmentPreview.adapter = TextGenRVAttachmentAdapter(
                                dataset = emptyList(),
                                viewModelTextGen
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

        // Observer of api generation status
        viewModelImageGen.apiStatusTextToImg.observe(viewLifecycleOwner) { status ->
            when (status) {
                AppStatus.LOADING ->  {
                    // Start load progress
                    viewModelImageGen.loadProgress()
                    // Set maximum progressBar percentage
                    binding.progressBar.max = 100
                    // Set visibility of ProgressBar
                    binding.progressBar.visibility = View.VISIBLE
                    // Update progressBar whenever the progress LiveData changes
                    viewModelImageGen.progress.observe(viewLifecycleOwner) {
                        // Update progressbar according the current progress
                        binding.progressBar.progress =
                            (viewModelImageGen.progress.value!!.progress.times(100)).toInt()
                    }
                }
                AppStatus.DONE -> {
                    // Remove ProgressBar when generation is done
                    binding.progressBar.visibility = View.GONE
                }
                else -> {}
            }
        }
    }
}
