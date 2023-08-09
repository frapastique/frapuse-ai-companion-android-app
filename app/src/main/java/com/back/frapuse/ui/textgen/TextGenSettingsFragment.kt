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

        // Set initial checked state of generation parameter switches
        binding.swAutoMaxNewTokens.isChecked = viewModel.auto_max_new_tokens.value!!

        binding.swAutoMaxNewTokens.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateGenerationParameters(auto_max_new_tokens = isChecked)
        }

        binding.swDoSample.isChecked = viewModel.do_sample.value!!

        binding.swDoSample.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateGenerationParameters(do_sample = isChecked)
        }

        binding.swEarlyStopping.isChecked = viewModel.early_stopping.value!!

        binding.swEarlyStopping.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateGenerationParameters(early_stopping = isChecked)
        }

        binding.swAddBosToken.isChecked = viewModel.add_bos_token.value!!

        binding.swAddBosToken.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateGenerationParameters(add_bos_token = isChecked)
        }

        binding.swBanEosToken.isChecked = viewModel.ban_eos_token.value!!

        binding.swBanEosToken.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateGenerationParameters(ban_eos_token = isChecked)
        }

        binding.swSkipSpecialTokens.isChecked = viewModel.skip_special_tokens.value!!

        binding.swSkipSpecialTokens.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateGenerationParameters(skip_special_tokens = isChecked)
        }

        binding.topAppBar.setNavigationOnClickListener { btnBack ->
            Log.e(TAG, "TopAppBar clicked:\n\tbtnBack")
            btnBack.findNavController().navigate(
                TextGenSettingsFragmentDirections
                    .actionTextGenSettingsFragmentToTextGenChatFragment()
            )
        }

        binding.ibExtensions.setOnCheckedChangeListener { _, isChecked ->
            adjustVisibleSettings(binding.clExtensionToggles, isChecked)
        }

        binding.ibInstructions.setOnCheckedChangeListener { _, isChecked ->
            adjustVisibleSettings(binding.clInstructions, isChecked)
        }

        binding.ibGenerateParameters.setOnCheckedChangeListener { _, isChecked ->
            adjustVisibleSettings(binding.clParameters, isChecked)
        }

        // Enable/disable haystack document search extension
        viewModel.extensionHaystack.observe(viewLifecycleOwner) {
            binding.mcbDocumentSearch.isChecked = it
        }
        binding.mcbDocumentSearch.isChecked = viewModel.extensionHaystack.value!!
        binding.mcbDocumentSearch.setOnClickListener {
            viewModel.extensionToggle("haystack")
        }

        // Enable/disable image generation extension
        viewModel.extensionImageGen.observe(viewLifecycleOwner) {
            binding.mcbImageGeneration.isChecked = it
        }
        binding.mcbImageGeneration.isChecked = viewModel.extensionImageGen.value!!
        binding.mcbImageGeneration.setOnClickListener {
            viewModel.extensionToggle("imageGen")
        }

        // Enable/disable image generation extension with keyword
        viewModel.extensionImageGenKeyword.observe(viewLifecycleOwner) {
            binding.mcbImageGenerationKeyword.isChecked = it
        }
        binding.mcbImageGenerationKeyword.isChecked = viewModel.extensionImageGenKeyword.value!!
        binding.mcbImageGenerationKeyword.setOnClickListener {
            viewModel.extensionToggle("imageGenKey")
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

        // Set custom image generation keyword && adjust end icon of text input layout
        var newImageGenKeyword: String
        binding.etImageGenerationKeyword.addTextChangedListener { imageGenKeyword ->
            if (!imageGenKeyword.isNullOrEmpty()) {
                newImageGenKeyword = imageGenKeyword.toString()
                binding.tiImageGenerationKeyword.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiImageGenerationKeyword.endIconMode = END_ICON_CUSTOM
                binding.tiImageGenerationKeyword.setEndIconOnClickListener {
                    viewModel.changeImageGenKeyword(newImageGenKeyword)
                }
            }
        }

        // Load current image generation keyword into edit text field
        binding.etImageGenerationKeyword.setText(viewModel.extensionImageGenKey.value)

        // Compare image generation keyword and set end icon state
        viewModel.extensionImageGenKey.observe(viewLifecycleOwner) { currentImageGenKey ->
            when {
                currentImageGenKey != viewModel.extensionImageGenKeyBase -> {
                    binding.tiImageGenerationKeyword.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiImageGenerationKeyword.endIconMode = END_ICON_CUSTOM
                    binding.tiImageGenerationKeyword.setEndIconOnClickListener {
                        viewModel.changeImageGenKeyword(viewModel.extensionImageGenKeyBase)
                        binding.etImageGenerationKeyword.setText(viewModel.extensionImageGenKey.value)
                        binding.tiImageGenerationKeyword.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiImageGenerationKeyword.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set visibility of TextInputLayout based on extensionImageGenKeyword value
        viewModel.extensionImageGenKeyword.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                binding.tiImageGenerationKeyword.visibility = View.GONE
            } else {
                binding.tiImageGenerationKeyword.visibility = View.GONE
            }
        }

        // Set custom new max tokens && adjust end icon of text input layout
        var newNewMaxTokens: Int
        binding.etNewMaxTokens.addTextChangedListener { newMaxTokens ->
            if (!newMaxTokens.isNullOrEmpty()) {
                newNewMaxTokens = newMaxTokens.toString().toInt()
                binding.tiNewMaxTokens.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiNewMaxTokens.endIconMode = END_ICON_CUSTOM
                binding.tiNewMaxTokens.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(max_new_tokens = newNewMaxTokens)
                }
            }
        }

        // Load current new max tokens into edit text field
        binding.etNewMaxTokens.setText(viewModel.max_new_tokens.value.toString())

        // Compare new max tokens and set end icon state
        viewModel.max_new_tokens.observe(viewLifecycleOwner) { currentNewMaxTokens ->
            when {
                currentNewMaxTokens != viewModel.max_new_tokens_base -> {
                    binding.tiNewMaxTokens.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiNewMaxTokens.endIconMode = END_ICON_CUSTOM
                    binding.tiNewMaxTokens.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            max_new_tokens = viewModel.max_new_tokens_base
                        )
                        binding.etNewMaxTokens.setText(viewModel.max_new_tokens.value.toString())
                        binding.tiNewMaxTokens.endIconMode = END_ICON_NONE
                    }
                }

                else -> {
                    binding.tiNewMaxTokens.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom temperature && adjust end icon of text input layout
        var newTemperature: Double
        binding.etTemperature.addTextChangedListener { temperature ->
            if (!temperature.isNullOrEmpty()) {
                newTemperature = temperature.toString().toDouble()
                binding.tiTemperature.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTemperature.endIconMode = END_ICON_CUSTOM
                binding.tiTemperature.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(temperature = newTemperature)
                }
            }
        }

        // Load current temperature into edit text field
        binding.etTemperature.setText(viewModel.temperature.value.toString())

        // Compare temperature and set end icon state
        viewModel.temperature.observe(viewLifecycleOwner) { currentTemperature ->
            when {
                currentTemperature != viewModel.temperature_base -> {
                    binding.tiTemperature.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTemperature.endIconMode = END_ICON_CUSTOM
                    binding.tiTemperature.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(temperature = viewModel.temperature_base)
                        binding.etTemperature.setText(viewModel.temperature.value.toString())
                        binding.tiTemperature.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiTemperature.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom top_p && adjust end icon of text input layout
        var newTopP: Double
        binding.etTopP.addTextChangedListener { topP ->
            if (!topP.isNullOrEmpty()) {
                newTopP = topP.toString().toDouble()
                binding.tiTopP.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTopP.endIconMode = END_ICON_CUSTOM
                binding.tiTopP.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(top_p = newTopP)
                }
            }
        }

        // Load current top_p into edit text field
        binding.etTopP.setText(viewModel.top_p.value.toString())

        // Compare top_p and set end icon state
        viewModel.top_p.observe(viewLifecycleOwner) { currentTopP ->
            when {
                currentTopP != viewModel.top_p_base -> {
                    binding.tiTopP.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTopP.endIconMode = END_ICON_CUSTOM
                    binding.tiTopP.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(top_p = viewModel.top_p_base)
                        binding.etTopP.setText(viewModel.top_p.value.toString())
                        binding.tiTopP.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiTopP.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom typical_p && adjust end icon of text input layout
        var newTypicalP: Double
        binding.etTypicalP.addTextChangedListener { typicalP ->
            if (!typicalP.isNullOrEmpty()) {
                newTypicalP = typicalP.toString().toDouble()
                binding.tiTypicalP.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTypicalP.endIconMode = END_ICON_CUSTOM
                binding.tiTypicalP.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(typical_p = newTypicalP)
                }
            }
        }

        // Load current typical_p into edit text field
        binding.etTypicalP.setText(viewModel.typical_p.value.toString())

        // Compare typical_p and set end icon state
        viewModel.typical_p.observe(viewLifecycleOwner) { currentTypicalP ->
            when {
                currentTypicalP != viewModel.typical_p_base -> {
                    binding.tiTypicalP.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTypicalP.endIconMode = END_ICON_CUSTOM
                    binding.tiTypicalP.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(typical_p = viewModel.typical_p_base)
                        binding.etTypicalP.setText(viewModel.typical_p.value.toString())
                        binding.tiTypicalP.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiTypicalP.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom repetition_penalty && adjust end icon of text input layout
        var newRepetitionPenalty: Double
        binding.etRepetitionPenalty.addTextChangedListener { repetitionPenalty ->
            if (!repetitionPenalty.isNullOrEmpty()) {
                newRepetitionPenalty = repetitionPenalty.toString().toDouble()
                binding.tiRepetitionPenalty.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiRepetitionPenalty.endIconMode = END_ICON_CUSTOM
                binding.tiRepetitionPenalty.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(repetition_penalty = newRepetitionPenalty)
                }
            }
        }

        // Load current repetition_penalty into edit text field
        binding.etRepetitionPenalty.setText(viewModel.repetition_penalty.value.toString())

        // Compare repetition_penalty and set end icon state
        viewModel.repetition_penalty.observe(viewLifecycleOwner) { currentRepetitionPenalty ->
            when {
                currentRepetitionPenalty != viewModel.repetition_penalty_base -> {
                    binding.tiRepetitionPenalty.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiRepetitionPenalty.endIconMode = END_ICON_CUSTOM
                    binding.tiRepetitionPenalty.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(repetition_penalty = viewModel.repetition_penalty_base)
                        binding.etRepetitionPenalty.setText(viewModel.repetition_penalty.value.toString())
                        binding.tiRepetitionPenalty.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiRepetitionPenalty.endIconMode = END_ICON_NONE
                }
            }
        }


        // Set custom top_k && adjust end icon of text input layout
        var newTopK: Int
        binding.etTopK.addTextChangedListener { topK ->
            if (!topK.isNullOrEmpty()) {
                newTopK = topK.toString().toInt()
                binding.tiTopK.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTopK.endIconMode = END_ICON_CUSTOM
                binding.tiTopK.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(top_k = newTopK)
                }
            }
        }

        // Load current top_k into edit text field
        binding.etTopK.setText(viewModel.top_k.value.toString())

        // Compare top_k and set end icon state
        viewModel.top_k.observe(viewLifecycleOwner) { currentTopK ->
            when {
                currentTopK != viewModel.top_k_base -> {
                    binding.tiTopK.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTopK.endIconMode = END_ICON_CUSTOM
                    binding.tiTopK.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(top_k = viewModel.top_k_base)
                        binding.etTopK.setText(viewModel.top_k.value.toString())
                        binding.tiTopK.endIconMode = END_ICON_NONE
                    }
                }

                else -> {
                    binding.tiTopK.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom min_length && adjust end icon of text input layout
        var newMinLength: Int
        binding.etMinLength.addTextChangedListener { minLength ->
            if (!minLength.isNullOrEmpty()) {
                newMinLength = minLength.toString().toInt()
                binding.tiMinLength.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiMinLength.endIconMode = END_ICON_CUSTOM
                binding.tiMinLength.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(min_length = newMinLength)
                }
            }
        }

        // Load current min_length into edit text field
        binding.etMinLength.setText(viewModel.min_length.value.toString())

        // Compare min_length and set end icon state
        viewModel.min_length.observe(viewLifecycleOwner) { currentMinLength ->
            when {
                currentMinLength != viewModel.min_length_base -> {
                    binding.tiMinLength.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiMinLength.endIconMode = END_ICON_CUSTOM
                    binding.tiMinLength.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(min_length = viewModel.min_length_base)
                        binding.etMinLength.setText(viewModel.min_length.value.toString())
                        binding.tiMinLength.endIconMode = END_ICON_NONE
                    }
                }

                else -> {
                    binding.tiMinLength.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom no_repeat_ngram_size && adjust end icon of text input layout
        var newNoRepeatNgramSize: Int
        binding.etNoRepeatNgramSize.addTextChangedListener { noRepeatNgramSize ->
            if (!noRepeatNgramSize.isNullOrEmpty()) {
                newNoRepeatNgramSize = noRepeatNgramSize.toString().toInt()
                binding.tiNoRepeatNgramSize.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiNoRepeatNgramSize.endIconMode = END_ICON_CUSTOM
                binding.tiNoRepeatNgramSize.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(
                        no_repeat_ngram_size = newNoRepeatNgramSize
                    )
                }
            }
        }

        // Load current no_repeat_ngram_size into edit text field
        binding.etNoRepeatNgramSize.setText(viewModel.no_repeat_ngram_size.value.toString())

        // Compare no_repeat_ngram_size and set end icon state
        viewModel.no_repeat_ngram_size.observe(viewLifecycleOwner) { currentNoRepeatNgramSize ->
            when {
                currentNoRepeatNgramSize != viewModel.no_repeat_ngram_size_base -> {
                    binding.tiNoRepeatNgramSize.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiNoRepeatNgramSize.endIconMode = END_ICON_CUSTOM
                    binding.tiNoRepeatNgramSize.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            no_repeat_ngram_size = viewModel.no_repeat_ngram_size_base
                        )
                        binding.etNoRepeatNgramSize.setText(
                            viewModel.no_repeat_ngram_size.value.toString()
                        )
                        binding.tiNoRepeatNgramSize.endIconMode = END_ICON_NONE
                    }
                }

                else -> {
                    binding.tiNoRepeatNgramSize.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom num_beams && adjust end icon of text input layout
        var newNumBeams: Int
        binding.etNumBeams.addTextChangedListener { numBeams ->
            if (!numBeams.isNullOrEmpty()) {
                newNumBeams = numBeams.toString().toInt()
                binding.tiNumBeams.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiNumBeams.endIconMode = END_ICON_CUSTOM
                binding.tiNumBeams.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(num_beams = newNumBeams)
                }
            }
        }

        // Load current num_beams into edit text field
        binding.etNumBeams.setText(viewModel.num_beams.value.toString())

        // Compare num_beams and set end icon state
        viewModel.num_beams.observe(viewLifecycleOwner) { currentNumBeams ->
            when {
                currentNumBeams != viewModel.num_beams_base -> {
                    binding.tiNumBeams.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiNumBeams.endIconMode = END_ICON_CUSTOM
                    binding.tiNumBeams.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(num_beams = viewModel.num_beams_base)
                        binding.etNumBeams.setText(viewModel.num_beams.value.toString())
                        binding.tiNumBeams.endIconMode = END_ICON_NONE
                    }
                }

                else -> {
                    binding.tiNumBeams.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom penalty_alpha && adjust end icon of text input layout
        var newPenaltyAlpha: Double
        binding.etPenaltyAlpha.addTextChangedListener { penaltyAlpha ->
            if (!penaltyAlpha.isNullOrEmpty()) {
                newPenaltyAlpha = penaltyAlpha.toString().toDouble()
                binding.tiPenaltyAlpha.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiPenaltyAlpha.endIconMode = END_ICON_CUSTOM
                binding.tiPenaltyAlpha.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(penalty_alpha = newPenaltyAlpha)
                }
            }
        }

        // Load current penalty_alpha into edit text field
        binding.etPenaltyAlpha.setText(viewModel.penalty_alpha.value.toString())

        // Compare penalty_alpha and set end icon state
        viewModel.penalty_alpha.observe(viewLifecycleOwner) { currentPenaltyAlpha ->
            when {
                currentPenaltyAlpha != viewModel.penalty_alpha_base -> {
                    binding.tiPenaltyAlpha.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiPenaltyAlpha.endIconMode = END_ICON_CUSTOM
                    binding.tiPenaltyAlpha.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            penalty_alpha = viewModel.penalty_alpha_base
                        )
                        binding.etPenaltyAlpha.setText(viewModel.penalty_alpha.value.toString())
                        binding.tiPenaltyAlpha.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiPenaltyAlpha.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom seed && adjust end icon of text input layout
        var newSeed: Int?
        binding.etSeed.addTextChangedListener { seed ->
            if (!seed.isNullOrEmpty()) {
                newSeed = seed.toString().toIntOrNull()
                if (newSeed != null) {
                    binding.tiSeed.setEndIconDrawable(R.drawable.apply_icon)
                    binding.tiSeed.endIconMode = END_ICON_CUSTOM
                    binding.tiSeed.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(seed = newSeed)
                    }
                }
            }
        }

        // Load current seed into edit text field
        binding.etSeed.setText(viewModel.seed.value.toString())

        // Compare seed and set end icon state
        viewModel.seed.observe(viewLifecycleOwner) { currentSeed ->
            when {
                currentSeed != viewModel.seed_base -> {
                    binding.tiSeed.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiSeed.endIconMode = END_ICON_CUSTOM
                    binding.tiSeed.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(seed = viewModel.seed_base)
                        binding.etSeed.setText(viewModel.seed.value.toString())
                        binding.tiSeed.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiSeed.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom truncation_length && adjust end icon of text input layout
        var newTruncationLength: Int
        binding.etTruncationLength.addTextChangedListener { truncationLength ->
            if (!truncationLength.isNullOrEmpty()) {
                newTruncationLength = truncationLength.toString().toInt()
                binding.tiTruncationLength.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTruncationLength.endIconMode = END_ICON_CUSTOM
                binding.tiTruncationLength.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(truncation_length = newTruncationLength)
                }
            }
        }

        // Load current truncation_length into edit text field
        binding.etTruncationLength.setText(viewModel.truncation_length.value.toString())

        // Compare truncation_length and set end icon state
        viewModel.truncation_length.observe(viewLifecycleOwner) { currentTruncationLength ->
            when {
                currentTruncationLength != viewModel.truncation_length_base -> {
                    binding.tiTruncationLength.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTruncationLength.endIconMode = END_ICON_CUSTOM
                    binding.tiTruncationLength.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            truncation_length = viewModel.truncation_length_base
                        )
                        binding.etTruncationLength.setText(
                            viewModel.truncation_length.value.toString()
                        )
                        binding.tiTruncationLength.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiTruncationLength.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom length_penalty && adjust end icon of text input layout
        var newLengthPenalty: Double
        binding.etLengthPenalty.addTextChangedListener { lengthPenalty ->
            if (!lengthPenalty.isNullOrEmpty()) {
                newLengthPenalty = lengthPenalty.toString().toDouble()
                binding.tiLengthPenalty.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiLengthPenalty.endIconMode = END_ICON_CUSTOM
                binding.tiLengthPenalty.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(length_penalty = newLengthPenalty)
                }
            }
        }

        // Load current length_penalty into edit text field
        binding.etLengthPenalty.setText(viewModel.length_penalty.value.toString())

        // Compare length_penalty and set end icon state
        viewModel.length_penalty.observe(viewLifecycleOwner) { currentLengthPenalty ->
            when {
                currentLengthPenalty != viewModel.length_penalty_base -> {
                    binding.tiLengthPenalty.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiLengthPenalty.endIconMode = END_ICON_CUSTOM
                    binding.tiLengthPenalty.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            length_penalty = viewModel.length_penalty_base
                        )
                        binding.etLengthPenalty.setText(viewModel.length_penalty.value.toString())
                        binding.tiLengthPenalty.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiLengthPenalty.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom epsilon_cutoff && adjust end icon of text input layout
        var newEpsilonCutoff: Double
        binding.etEpsilonCutoff.addTextChangedListener { epsilonCutoff ->
            if (!epsilonCutoff.isNullOrEmpty()) {
                newEpsilonCutoff = epsilonCutoff.toString().toDouble()
                binding.tiEpsilonCutoff.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiEpsilonCutoff.endIconMode = END_ICON_CUSTOM
                binding.tiEpsilonCutoff.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(epsilon_cutoff = newEpsilonCutoff)
                }
            }
        }

        // Load current epsilon_cutoff into edit text field
        binding.etEpsilonCutoff.setText(viewModel.epsilon_cutoff.value.toString())

        // Compare epsilon_cutoff and set end icon state
        viewModel.epsilon_cutoff.observe(viewLifecycleOwner) { currentEpsilonCutoff ->
            when {
                currentEpsilonCutoff != viewModel.epsilon_cutoff_base -> {
                    binding.tiEpsilonCutoff.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiEpsilonCutoff.endIconMode = END_ICON_CUSTOM
                    binding.tiEpsilonCutoff.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            epsilon_cutoff = viewModel.epsilon_cutoff_base
                        )
                        binding.etEpsilonCutoff.setText(viewModel.epsilon_cutoff.value.toString())
                        binding.tiEpsilonCutoff.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiEpsilonCutoff.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom eta_cutoff && adjust end icon of text input layout
        var newEtaCutoff: Double
        binding.etEtaCutoff.addTextChangedListener { etaCutoff ->
            if (!etaCutoff.isNullOrEmpty()) {
                newEtaCutoff = etaCutoff.toString().toDouble()
                binding.tiEtaCutoff.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiEtaCutoff.endIconMode = END_ICON_CUSTOM
                binding.tiEtaCutoff.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(eta_cutoff = newEtaCutoff)
                }
            }
        }

        // Load current eta_cutoff into edit text field
        binding.etEtaCutoff.setText(viewModel.eta_cutoff.value.toString())

        // Compare eta_cutoff and set end icon state
        viewModel.eta_cutoff.observe(viewLifecycleOwner) { currentEtaCutoff ->
            when {
                currentEtaCutoff != viewModel.eta_cutoff_base -> {
                    binding.tiEtaCutoff.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiEtaCutoff.endIconMode = END_ICON_CUSTOM
                    binding.tiEtaCutoff.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            eta_cutoff = viewModel.eta_cutoff_base
                        )
                        binding.etEtaCutoff.setText(viewModel.eta_cutoff.value.toString())
                        binding.tiEtaCutoff.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiEtaCutoff.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom tfs && adjust end icon of text input layout
        var newTfs: Double
        binding.etTfs.addTextChangedListener { tfs ->
            if (!tfs.isNullOrEmpty()) {
                newTfs = tfs.toString().toDouble()
                binding.tiTfs.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTfs.endIconMode = END_ICON_CUSTOM
                binding.tiTfs.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(tfs = newTfs)
                }
            }
        }

        // Load current tfs into edit text field
        binding.etTfs.setText(viewModel.tfs.value.toString())

        // Compare tfs and set end icon state
        viewModel.tfs.observe(viewLifecycleOwner) { currentTfs ->
            when {
                currentTfs != viewModel.tfs_base -> {
                    binding.tiTfs.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTfs.endIconMode = END_ICON_CUSTOM
                    binding.tiTfs.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            tfs = viewModel.tfs_base
                        )
                        binding.etTfs.setText(viewModel.tfs.value.toString())
                        binding.tiTfs.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiTfs.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom top_a && adjust end icon of text input layout
        var newTopA: Double
        binding.etTopA.addTextChangedListener { topA ->
            if (!topA.isNullOrEmpty()) {
                newTopA = topA.toString().toDouble()
                binding.tiTopA.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiTopA.endIconMode = END_ICON_CUSTOM
                binding.tiTopA.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(top_a = newTopA)
                }
            }
        }

        // Load current top_a into edit text field
        binding.etTopA.setText(viewModel.top_a.value.toString())

        // Compare top_a and set end icon state
        viewModel.top_a.observe(viewLifecycleOwner) { currentTopA ->
            when {
                currentTopA != viewModel.top_a_base -> {
                    binding.tiTopA.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiTopA.endIconMode = END_ICON_CUSTOM
                    binding.tiTopA.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            top_a = viewModel.top_a_base
                        )
                        binding.etTopA.setText(viewModel.top_a.value.toString())
                        binding.tiTopA.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiTopA.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom repetition_penalty_range && adjust end icon of text input layout
        var newRepetitionPenaltyRange: Int
        binding.etRepetitionPenaltyRange.addTextChangedListener { repetitionPenaltyRange ->
            if (!repetitionPenaltyRange.isNullOrEmpty()) {
                newRepetitionPenaltyRange = repetitionPenaltyRange.toString().toInt()
                binding.tiRepetitionPenaltyRange.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiRepetitionPenaltyRange.endIconMode = END_ICON_CUSTOM
                binding.tiRepetitionPenaltyRange.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(repetition_penalty_range = newRepetitionPenaltyRange)
                }
            }
        }

        // Load current repetition_penalty_range into edit text field
        binding.etRepetitionPenaltyRange.setText(viewModel.repetition_penalty_range.value.toString())

        // Compare repetition_penalty_range and set end icon state
        viewModel.repetition_penalty_range.observe(viewLifecycleOwner) { currentRepetitionPenaltyRange ->
            when {
                currentRepetitionPenaltyRange != viewModel.repetition_penalty_range_base -> {
                    binding.tiRepetitionPenaltyRange.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiRepetitionPenaltyRange.endIconMode = END_ICON_CUSTOM
                    binding.tiRepetitionPenaltyRange.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            repetition_penalty_range = viewModel.repetition_penalty_range_base
                        )
                        binding.etRepetitionPenaltyRange.setText(viewModel.repetition_penalty_range.value.toString())
                        binding.tiRepetitionPenaltyRange.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiRepetitionPenaltyRange.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom encoder_repetition_penalty && adjust end icon of text input layout
        var newEncoderRepetitionPenalty: Double
        binding.etEncoderRepetitionPenalty.addTextChangedListener { encoderRepetitionPenalty ->
            if (!encoderRepetitionPenalty.isNullOrEmpty()) {
                newEncoderRepetitionPenalty = encoderRepetitionPenalty.toString().toDouble()
                binding.tiEncoderRepetitionPenalty.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiEncoderRepetitionPenalty.endIconMode = END_ICON_CUSTOM
                binding.tiEncoderRepetitionPenalty.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(encoder_repetition_penalty = newEncoderRepetitionPenalty)
                }
            }
        }

        // Load current encoder_repetition_penalty into edit text field
        binding.etEncoderRepetitionPenalty.setText(viewModel.encoder_repetition_penalty.value.toString())

        // Compare encoder_repetition_penalty and set end icon state
        viewModel.encoder_repetition_penalty.observe(viewLifecycleOwner) { currentEncoderRepetitionPenalty ->
            when {
                currentEncoderRepetitionPenalty != viewModel.encoder_repetition_penalty_base -> {
                    binding.tiEncoderRepetitionPenalty.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiEncoderRepetitionPenalty.endIconMode = END_ICON_CUSTOM
                    binding.tiEncoderRepetitionPenalty.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            encoder_repetition_penalty = viewModel.encoder_repetition_penalty_base
                        )
                        binding.etEncoderRepetitionPenalty.setText(viewModel.encoder_repetition_penalty.value.toString())
                        binding.tiEncoderRepetitionPenalty.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiEncoderRepetitionPenalty.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom mirostat_mode && adjust end icon of text input layout
        var newMirostatMode: Int
        binding.etMirostatMode.addTextChangedListener { mirostatMode ->
            if (!mirostatMode.isNullOrEmpty()) {
                newMirostatMode = mirostatMode.toString().toInt()
                binding.tiMirostatMode.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiMirostatMode.endIconMode = END_ICON_CUSTOM
                binding.tiMirostatMode.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(mirostat_mode = newMirostatMode)
                }
            }
        }

        // Load current mirostat_mode into edit text field
        binding.etMirostatMode.setText(viewModel.mirostat_mode.value.toString())

        // Compare mirostat_mode and set end icon state
        viewModel.mirostat_mode.observe(viewLifecycleOwner) { currentMirostatMode ->
            when {
                currentMirostatMode != viewModel.mirostat_mode_base -> {
                    binding.tiMirostatMode.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiMirostatMode.endIconMode = END_ICON_CUSTOM
                    binding.tiMirostatMode.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            mirostat_mode = viewModel.mirostat_mode_base
                        )
                        binding.etMirostatMode.setText(viewModel.mirostat_mode.value.toString())
                        binding.tiMirostatMode.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiMirostatMode.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom mirostat_tau && adjust end icon of text input layout
        var newMirostatTau: Double
        binding.etMirostatTau.addTextChangedListener { mirostatTau ->
            if (!mirostatTau.isNullOrEmpty()) {
                newMirostatTau = mirostatTau.toString().toDouble()
                binding.tiMirostatTau.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiMirostatTau.endIconMode = END_ICON_CUSTOM
                binding.tiMirostatTau.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(mirostat_tau = newMirostatTau)
                }
            }
        }

        // Load current mirostat_tau into edit text field
        binding.etMirostatTau.setText(viewModel.mirostat_tau.value.toString())

        // Compare mirostat_tau and set end icon state
        viewModel.mirostat_tau.observe(viewLifecycleOwner) { currentMirostatTau ->
            when {
                currentMirostatTau != viewModel.mirostat_tau_base -> {
                    binding.tiMirostatTau.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiMirostatTau.endIconMode = END_ICON_CUSTOM
                    binding.tiMirostatTau.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            mirostat_tau = viewModel.mirostat_tau_base
                        )
                        binding.etMirostatTau.setText(viewModel.mirostat_tau.value.toString())
                        binding.tiMirostatTau.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiMirostatTau.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom mirostat_eta && adjust end icon of text input layout
        var newMirostatEta: Double
        binding.etMirostatEta.addTextChangedListener { mirostatEta ->
            if (!mirostatEta.isNullOrEmpty()) {
                newMirostatEta = mirostatEta.toString().toDouble()
                binding.tiMirostatEta.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiMirostatEta.endIconMode = END_ICON_CUSTOM
                binding.tiMirostatEta.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(mirostat_eta = newMirostatEta)
                }
            }
        }

        // Load current mirostat_eta into edit text field
        binding.etMirostatEta.setText(viewModel.mirostat_eta.value.toString())

        // Compare mirostat_eta and set end icon state
        viewModel.mirostat_eta.observe(viewLifecycleOwner) { currentMirostatEta ->
            when {
                currentMirostatEta != viewModel.mirostat_eta_base -> {
                    binding.tiMirostatEta.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiMirostatEta.endIconMode = END_ICON_CUSTOM
                    binding.tiMirostatEta.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            mirostat_eta = viewModel.mirostat_eta_base
                        )
                        binding.etMirostatEta.setText(viewModel.mirostat_eta.value.toString())
                        binding.tiMirostatEta.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiMirostatEta.endIconMode = END_ICON_NONE
                }
            }
        }

        // Set custom guidance_scale && adjust end icon of text input layout
        var newGuidanceScale: Double
        binding.etGuidanceScale.addTextChangedListener { guidanceScale ->
            if (!guidanceScale.isNullOrEmpty()) {
                newGuidanceScale = guidanceScale.toString().toDouble()
                binding.tiGuidanceScale.setEndIconDrawable(R.drawable.apply_icon)
                binding.tiGuidanceScale.endIconMode = END_ICON_CUSTOM
                binding.tiGuidanceScale.setEndIconOnClickListener {
                    viewModel.updateGenerationParameters(guidance_scale = newGuidanceScale)
                }
            }
        }

        // Load current guidance_scale into edit text field
        binding.etGuidanceScale.setText(viewModel.guidance_scale.value.toString())

        // Compare guidance_scale and set end icon state
        viewModel.guidance_scale.observe(viewLifecycleOwner) { currentGuidanceScale ->
            when {
                currentGuidanceScale != viewModel.guidance_scale_base -> {
                    binding.tiGuidanceScale.setEndIconDrawable(R.drawable.reset_icon)
                    binding.tiGuidanceScale.endIconMode = END_ICON_CUSTOM
                    binding.tiGuidanceScale.setEndIconOnClickListener {
                        viewModel.updateGenerationParameters(
                            guidance_scale = viewModel.guidance_scale_base
                        )
                        binding.etGuidanceScale.setText(viewModel.guidance_scale.value.toString())
                        binding.tiGuidanceScale.endIconMode = END_ICON_NONE
                    }
                }
                else -> {
                    binding.tiGuidanceScale.endIconMode = END_ICON_NONE
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