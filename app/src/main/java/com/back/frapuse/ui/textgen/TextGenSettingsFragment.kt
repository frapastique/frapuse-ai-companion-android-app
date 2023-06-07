package com.back.frapuse.ui.textgen

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.back.frapuse.R
import com.back.frapuse.databinding.FragmentTextGenSettingsBinding
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE

private const val TAG = "TextGenSettingsFragment"

class TextGenSettingsFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentTextGenSettingsBinding

    /**
     * Lifecycle Function onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextGenSettingsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Load current instructions into edit text field
        binding.etInstructions.setText(viewModel.instructionsContext.value)

        binding.topAppBar.setNavigationOnClickListener { btnBack ->
            Log.e(TAG, "TopAppBar clicked:\n\tbtnBack")
            btnBack.findNavController().navigate(TextGenSettingsFragmentDirections
                .actionTextGenSettingsFragmentToTextGenChatFragment()
            )
        }

        binding.ibExtensions.setOnCheckedChangeListener { _, isChecked ->
            adjustVisibleSettings(binding.clExtensionToggles, isChecked)
        }

        binding.ibInstructions.setOnCheckedChangeListener { _, isChecked ->
            adjustVisibleSettings(binding.clInstructions, isChecked)
        }

        // Enable/disable haystack document search extension
        binding.mcbDocumentSearch.isChecked = viewModel.extensionHaystack.value!!
        binding.mcbDocumentSearch.setOnClickListener {
            viewModel.extensionToggle("haystack")
        }

        // Enable/disable image generation extension
        binding.mcbImageGeneration.isChecked = viewModel.extensionImageGen.value!!
        binding.mcbImageGeneration.setOnClickListener {
            viewModel.extensionToggle("imageGen")
        }

        // Set custom instructions && adjust end icon of text input layout
        var newInstructionsContext: String
        binding.etInstructions.addTextChangedListener { newInstruction ->
            if (!newInstruction.isNullOrEmpty()) {
                newInstructionsContext = newInstruction.toString()
                binding.tiInstructions.setEndIconDrawable(
                    R.drawable.apply_icon
                )
                binding.tiInstructions.endIconMode = END_ICON_CUSTOM
                binding.tiInstructions.setEndIconOnClickListener {
                    viewModel.updateInstructionsContext(newInstructionsContext)
                }
            }
        }

        // Load current instructions into edit text field
        binding.etInstructionsAgentHaystack.setText(viewModel.agentHaystackPrompt.value)

        // Compare instructions and set instructions end icon state
        viewModel.instructionsContext.observe(viewLifecycleOwner) { currentInstructions ->
            when {
                currentInstructions != viewModel.standardInstruction -> {
                    binding.tiInstructions.setEndIconDrawable(
                        R.drawable.reset_icon
                    )
                    binding.tiInstructions.endIconMode = END_ICON_CUSTOM
                    binding.tiInstructions.setEndIconOnClickListener {
                        viewModel.updateInstructionsContext(viewModel.standardInstruction)
                        binding.etInstructions.setText(viewModel.instructionsContext.value)
                        binding.tiInstructions.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiInstructions.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom agent haystack instructions && adjust end icon of text input layout
        var newInstructionsAgentDocSearch: String
        binding.etInstructionsAgentHaystack.addTextChangedListener { newInstruction ->
            if (!newInstruction.isNullOrEmpty()) {
                newInstructionsAgentDocSearch = newInstruction.toString()
                binding.tiInstructionsAgentHaystack.setEndIconDrawable(
                    R.drawable.apply_icon
                )
                binding.tiInstructionsAgentHaystack.endIconMode = END_ICON_CUSTOM
                binding.tiInstructionsAgentHaystack.setEndIconOnClickListener {
                    viewModel.updateAgentHaystackPrompt(newInstructionsAgentDocSearch)
                }
            }
        }

        // Compare agent haystack instructions and set instructions end icon state
        viewModel.agentHaystackPrompt.observe(viewLifecycleOwner) { currentInstructions ->
            when {
                currentInstructions != viewModel.agentHaystackStandardPrompt -> {
                    binding.tiInstructionsAgentHaystack.setEndIconDrawable(
                        R.drawable.reset_icon
                    )
                    binding.tiInstructionsAgentHaystack.endIconMode = END_ICON_CUSTOM
                    binding.tiInstructionsAgentHaystack.setEndIconOnClickListener {
                        viewModel.updateAgentHaystackPrompt(viewModel.agentHaystackStandardPrompt)
                        binding.etInstructionsAgentHaystack.setText(
                            viewModel.agentHaystackPrompt.value
                        )
                        binding.tiInstructionsAgentHaystack.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiInstructionsAgentHaystack.endIconMode = END_ICON_NONE
                }
            }
        }

        // Load current additional instructions after haystack into edit text field
        binding.etInstructionsAfterAgentHaystack.setText(
            viewModel.instructionsAfterHaystack.value
        )

        // Set custom additional instructions after haystack && adjust end icon of text input layout
        var newInstructionsAfterAgentDocSearch: String
        binding.etInstructionsAfterAgentHaystack.addTextChangedListener { newInstruction ->
            if (!newInstruction.isNullOrEmpty()) {
                newInstructionsAfterAgentDocSearch = newInstruction.toString()
                binding.tiInstructionsAfterAgentHaystack.setEndIconDrawable(
                    R.drawable.apply_icon
                )
                binding.tiInstructionsAfterAgentHaystack.endIconMode = END_ICON_CUSTOM
                binding.tiInstructionsAfterAgentHaystack.setEndIconOnClickListener {
                    viewModel.updateInstructionsAfterHaystack(
                        newInstructionsAfterAgentDocSearch
                    )
                }
            }
        }

        // Compare additional instructions after haystack and set instructions end icon state
        viewModel.instructionsAfterHaystack.observe(viewLifecycleOwner) { currentInstructions ->
            when {
                currentInstructions != viewModel.instructionsAfterHaystackStandard -> {
                    binding.tiInstructionsAfterAgentHaystack.setEndIconDrawable(
                        R.drawable.reset_icon
                    )
                    binding.tiInstructionsAfterAgentHaystack.endIconMode = END_ICON_CUSTOM
                    binding.tiInstructionsAfterAgentHaystack.setEndIconOnClickListener {
                        viewModel.updateInstructionsAfterHaystack(
                            viewModel.instructionsAfterHaystackStandard
                        )
                        binding.etInstructionsAfterAgentHaystack.setText(
                            viewModel.instructionsAfterHaystack.value
                        )
                        binding.tiInstructionsAfterAgentHaystack.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiInstructionsAfterAgentHaystack.endIconMode = END_ICON_NONE
                }
            }
        }
    }

    private fun adjustVisibleSettings(constraintLayout: ConstraintLayout, state: Boolean) {
        val lp = constraintLayout.layoutParams as ConstraintLayout.LayoutParams
        if (state) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            constraintLayout.layoutParams = lp
        } else {
            lp.height = 50.dpToPx()
            constraintLayout.layoutParams = lp
        }
    }

    private fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density + 0.5f).toInt()
    }
}