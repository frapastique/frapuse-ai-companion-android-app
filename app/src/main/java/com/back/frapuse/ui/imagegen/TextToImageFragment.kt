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
import androidx.navigation.findNavController
import com.back.frapuse.AppStatus
import com.back.frapuse.R
import com.back.frapuse.ImageGenerationViewModel
import com.back.frapuse.databinding.FragmentTextToImageBinding

private const val TAG = "TextToImageFragment"

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
        val seedInit: Long = binding.etSeed.text.toString().toLong()
        val samplerInit: String = binding.actvSamplerIndex.text.toString()

        if (viewModel.finalImageBase64.value != null) {
            val image = viewModel.decodeImage(viewModel.finalImageBase64.value!!.images.first())
            binding.ivTextToImage.setImageBitmap(image)
        }

        // Observe the config and set the currently loaded sd_model_checkpoint
        viewModel.options.observe(viewLifecycleOwner) { options ->
            binding.actvModel.setText(Regex("^[^.]*")
                .find(options.sd_model_checkpoint)?.value)
        }

        // When the prompt value is not empty set the text of prompt field
        if (!viewModel.prompt.value.isNullOrEmpty()) {
            binding.etPrompt.setText(viewModel.prompt.value)
        }
        // Prompt value gets updated when input text changes & set button state accordingly
        binding.etPrompt.addTextChangedListener { prompt ->
            if (prompt.isNullOrEmpty()) {
                viewModel.setPrompt("")
                setGenButtonsState(false)
            } else {
                viewModel.setPrompt(prompt.toString())
                setGenButtonsState(true)
            }
        }

        // When the negative prompt is not empty set the text of prompt field
        if (!viewModel.negativePrompt.value.isNullOrEmpty()) {
            binding.etNegativePrompt.setText(viewModel.negativePrompt.value)
        } else {
            viewModel.setNegativePrompt("")
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

        // If statement to update height value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.seed.value == null) {
            viewModel.setSeed(seedInit)
        } else {
            binding.etSeed.setText(viewModel.seed.value.toString())
        }
        // Height value gets Updated when input text changes
        binding.etSeed.addTextChangedListener { seed ->
            if (seed.toString().isNotBlank() && seed.toString() != "-") {
                viewModel.setSeed(seed.toString().toLong())
            }
        }

        // Load image info when finalImageBase64 gets updated
        viewModel.finalImageBase64.observe(viewLifecycleOwner) { genData ->
            val image = viewModel.decodeImage(genData.images.first())
            binding.ivTextToImage.setImageBitmap(image)
            viewModel.setImage(image)
        }

        // Set save image button according to value of image (not null or empty)
        viewModel.image.observe(viewLifecycleOwner) { image ->
            if (image != null) {
                setSaveButtonState(true)
            } else {
                setSaveButtonState(false)
            }
        }

        // Place imageInfo string into debug TextView
        viewModel.imageInfo.observe(viewLifecycleOwner) {
            binding.tvDebugImageInfo.text = viewModel.imageInfo.value!!.info
        }

        // Save image when button is clicked
        binding.btnSave.setOnClickListener {
            viewModel.saveImage()
        }

        // Set maximum progressBar percentage
        binding.progressBar.max = 100

        // Update progressBar whenever the progress LiveData changes
        viewModel.progress.observe(viewLifecycleOwner) {
            // Update progressbar according the current progress
            binding.progressBar.progress = (viewModel.progress.value!!.progress.times(100)).toInt()
        }

        // Listener for generate Button which initiates api call
        binding.btnGenerate.setOnClickListener {
            viewModel.setTextToImageRequest()
            if (viewModel.appStatusSetTextToImageRequest.value == AppStatus.DONE) {
                viewModel.startTextToImageRequest()
                if (viewModel.apiStatusTextToImg.value ==AppStatus.LOADING) {
                    // Start load progress
                    viewModel.loadProgress()
                }
            }
        }

        // Observer which initiates decoding of progress image in Base64 when api delivers response
        viewModel.progressImageBase64.observe(viewLifecycleOwner) { progressImageBase64 ->
            val image = viewModel.decodeImage(progressImageBase64)
            binding.ivTextToImage.setImageBitmap(image)
        }

        // Place models into the model dropdown menu
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

        // Place samplers into the sampler dropdown menu
        viewModel.samplersList.observe(viewLifecycleOwner) { samplers ->
            val samplerNameList: MutableList<String> = mutableListOf()
            for (sampler in samplers) {
                samplerNameList.add(sampler.name)
            }
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.sd_samplers_item,
                samplerNameList.toTypedArray()
            )
            binding.actvSamplerIndex.setAdapter(arrayAdapter)
        }

        // If statement to update sampler with hardcoded value only when no value is saved
        // else place sampler from viewModel
        if (viewModel.sampler.value?.name.isNullOrEmpty()) {
            viewModel.setSampler(samplerInit)
        } else {
            binding.actvSamplerIndex.setText(viewModel.sampler.value!!.name)
        }
        // Set sampler according the selection from dropdown menu
        binding.actvSamplerIndex.setOnItemClickListener { parent, _, position, _ ->
            val samplerName = parent.getItemAtPosition(position) as String
            viewModel.setSampler(samplerName)
        }

        // When the image is clicked long navigate to the RecyclerView Fragment
        binding.ivTextToImage.setOnLongClickListener { ivTextToImage ->
            ivTextToImage.findNavController()
                .navigate(TextToImageFragmentDirections.actionTextToImageFragmentToImageGenRecyclerViewFragment())

            true
        }
    }

    private fun setGenButtonsState(state: Boolean) {
        if (state) {
            // Apply positive appearance for generate button
            binding.btnGenerate.isClickable = true
            binding.btnGenerate.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200
                    )
                )
            binding.btnGenerate.setImageResource(R.drawable.checkmark_seal)
        } else {
            // Apply negative appearance for generate button
            binding.btnGenerate.isClickable = false
            binding.btnGenerate.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        androidx.cardview.R.color.cardview_dark_background
                    )
                )
            binding.btnGenerate.setImageResource(R.drawable.xmark_seal)
        }
    }

    private fun setSaveButtonState(state: Boolean) {
        if (state) {
            // Apply positive appearance for save button
            binding.btnSave.isClickable = true
            binding.btnSave.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200
                    )
                )
        } else {
            // Apply negative appearance for save button
            binding.btnSave.isClickable = false
            binding.btnSave.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        androidx.cardview.R.color.cardview_dark_background
                    )
                )
        }
    }
}