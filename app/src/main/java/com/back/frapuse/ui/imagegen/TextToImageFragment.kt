package com.back.frapuse.ui.imagegen

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.back.frapuse.ApiOptionsStatus
import com.back.frapuse.ApiTxt2ImgStatus
import com.back.frapuse.R
import com.back.frapuse.ImageGenerationViewModel
import com.back.frapuse.databinding.FragmentTextToImageBinding

class TextToImageFragment : Fragment() {
    // Hier wird das ViewModel, in dem die Logik stattfindet, geholt
    private val viewModel: ImageGenerationViewModel by activityViewModels()

    // Das binding für das QuizFragment wird deklariert
    private lateinit var binding: FragmentTextToImageBinding

    /**
     * Lifecycle Funktion onCreateView
     * Hier wird das binding initialisiert und das Layout gebaut
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextToImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Get the hardcoded launch values for steps, width and height
        val stepsInit: Int = binding.etSteps.text.toString().toInt()
        val cfgInit: Double = binding.etCfgScale.text.toString().toDouble()
        val widthInit: Int = binding.etWidth.text.toString().toInt()
        val heightInit: Int = binding.etHeight.text.toString().toInt()

        // Load installed models
        viewModel.loadModels()

        // Load the initial configuration options
        viewModel.loadOptions()
        // Observe the config and set the currently loaded sd_model_checkpoint
        viewModel.options.observe(viewLifecycleOwner) { options ->
            binding.actvModel.setText(Regex("^[^.]*")
                .find(options.sd_model_checkpoint)?.value)
        }

        // When the prompt value is not empty set the text of prompt field
        if (!viewModel.prompt.value.isNullOrEmpty()) {
            binding.etPrompt.setText(viewModel.prompt.value)
        }
        // Prompt value gets updated when input text changes
        binding.etPrompt.addTextChangedListener { prompt ->
            viewModel.setPrompt(prompt.toString())
        }

        // When the negative prompt is not empty set the text of prompt field
        if (!viewModel.negativePrompt.value.isNullOrEmpty()) {
            binding.etNegativePrompt.setText(viewModel.negativePrompt.value)
        }
        // Negative prompt gets updated when input text changes
        binding.etNegativePrompt.addTextChangedListener { negativePrompt ->
            viewModel.setNegativePrompt(negativePrompt.toString())
        }

        // If statement to update steps value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.steps.value == null) {
            viewModel.setSteps(stepsInit)
        } else {
            binding.etSteps.setText(viewModel.steps.value.toString())
        }
        // Steps value gets Updated when input text changes
        binding.etSteps.addTextChangedListener { steps ->
            if (steps.toString().isNotBlank()) {
                viewModel.setSteps(steps.toString().toInt())
            }
        }

        // If statement to update cfg scale value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.cfgScale.value == null) {
            viewModel.setCfgScale(cfgInit)
        } else {
            binding.etCfgScale.setText(viewModel.cfgScale.value.toString())
        }
        // Steps value gets Updated when input text changes
        binding.etCfgScale.addTextChangedListener { cfgScale ->
            if (cfgScale.toString().isNotBlank()) {
                viewModel.setCfgScale(cfgScale.toString().toDouble())
            }
        }

        // If statement to update width value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.width.value == null) {
            viewModel.setWidth(widthInit)
        } else {
            binding.etWidth.setText(viewModel.width.value.toString())
        }
        // Width value gets Updated when input text changes
        binding.etWidth.addTextChangedListener { width ->
            if (width.toString().isNotBlank()) {
                viewModel.setWidth(width.toString().toInt())
            }
        }

        // If statement to update height value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.height.value == null) {
            viewModel.setHeight(heightInit)
        } else {
            binding.etHeight.setText(viewModel.height.value.toString())
        }
        // Height value gets Updated when input text changes
        binding.etHeight.addTextChangedListener { height ->
            if (height.toString().isNotBlank()) {
                viewModel.setHeight(height.toString().toInt())
            }
        }

        // Update the color and clickable state of generate button when min values of
        // prompt, steps, width, height meet the minimum requirements else set to not clickable
        viewModel.genDataStatus.observe(viewLifecycleOwner) { state ->
            if (state) {
                setButtonsState()
                viewModel.setTextToImageRequest()
            } else {
                setButtonsState()
            }
        }

        // Observe the text to image request api status and set the visibility of ProgressBar
        viewModel.txt2imgStatus.observe(viewLifecycleOwner) { status ->
            if (status == ApiTxt2ImgStatus.LOADING) {
                binding.progressBar.visibility = View.VISIBLE
                // Set maximum progressBar percentage
                binding.progressBar.max = 100
                // Update progressBar whenever the progress LiveData changes
                viewModel.progress.observe(viewLifecycleOwner) { progress ->
                    binding.progressBar.progress = (progress.times(100)).toInt()
                }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Listener for generate Button which initiates api call
        binding.btnGenerate.setOnClickListener {
            viewModel.loadTextToImage()
        }

        // Observer which initiates decoding of image in Base64 when api delivers response
        viewModel.imageBase64.observe(viewLifecycleOwner) { imageBase64 ->
            viewModel.decodeImage(imageBase64.images.first())
        }

        // Observer which initiates decoding of progress image in Base64 when api delivers response
        viewModel.progressImageBase64.observe(viewLifecycleOwner) { progressImageBase64 ->
            viewModel.decodeImage(progressImageBase64)
        }

        // Observer which loads image in ImageView when decoder sets decoded image
        viewModel.image.observe(viewLifecycleOwner) { image ->
            binding.ivTextToImage.setImageBitmap(image)
        }

        // Place models into the dropdown menu
        viewModel.models.observe(viewLifecycleOwner) { models ->
            val modelNameList: MutableList<String> = mutableListOf()
            for (element in models) {
                modelNameList.add(element.model_name)
            }
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.sd_models_item,
                modelNameList.toTypedArray()
            )
            binding.actvModel.setAdapter(arrayAdapter)
        }

        // Set model according the selection from dropdown menu
        binding.actvModel.setOnItemClickListener { parent, _, position, _ ->
            val modelName = parent.getItemAtPosition(position) as String
            viewModel.setModel(modelName)
        }

        // Place image metadata into the TextView
        viewModel.imageInfo.observe(viewLifecycleOwner) { imageInfo ->
            binding.tvImageMetaData.text = imageInfo.info
        }

        // Observe imageMetadata and when created save into database
        viewModel.imageMetadata.observe(viewLifecycleOwner) {
            viewModel.saveImage()
        }
    }

    private fun setButtonsState() {
        viewModel.optionsStatus.observe(viewLifecycleOwner) { status ->
            when(status) {
                ApiOptionsStatus.DONE -> {
                    binding.btnGenerate.isClickable = true
                    binding.btnGenerate.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_200)
                        )
                    binding.btnGenerate.setImageResource(R.drawable.checkmark_seal)
                }
                else -> {
                    binding.btnGenerate.isClickable = false
                    binding.btnGenerate.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(
                            requireContext(),
                            androidx.cardview.R.color.cardview_dark_background)
                        )
                    binding.btnGenerate.setImageResource(R.drawable.xmark_seal)
                }
            }
        }
    }
}