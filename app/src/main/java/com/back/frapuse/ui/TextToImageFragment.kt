package com.back.frapuse.ui

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
    ): View? {
        binding = FragmentTextToImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Get the hardcoded launch values for steps, width and height
        val stepsInit: Int = binding.etSteps.text.toString().toInt()
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
        // Prompt value gets Updated when input text changes
        binding.etPrompt.addTextChangedListener { prompt ->
            viewModel.setPrompt(prompt.toString())
        }

        // If statement to update steps value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.steps.value == null) {
            viewModel.setSteps(stepsInit.toString())
        } else {
            binding.etSteps.setText(viewModel.steps.value.toString())
        }
        // Steps value gets Updated when input text changes
        binding.etSteps.addTextChangedListener { steps ->
            viewModel.setSteps(steps.toString())
        }

        // If statement to update width value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.width.value == null) {
            viewModel.setWidth(widthInit.toString())
        } else {
            binding.etWidth.setText(viewModel.width.value.toString())
        }
        // Width value gets Updated when input text changes
        binding.etWidth.addTextChangedListener { width ->
            viewModel.setWidth(width.toString())
        }

        // If statement to update height value with hardcoded value only when no value is saved
        // else place text from LiveData
        if (viewModel.height.value == null) {
            viewModel.setHeight(heightInit.toString())
        } else {
            binding.etHeight.setText(viewModel.height.value.toString())
        }
        // Height value gets Updated when input text changes
        binding.etHeight.addTextChangedListener { height ->
            viewModel.setHeight(height.toString())
        }

        // Update the color and clickable state of generate button when min values of
        // prompt, steps, width, height meet the minimum requirements else set to not clickable
        viewModel.generationData.observe(viewLifecycleOwner) { (prompt, steps, width, height) ->
            if (prompt.isNotEmpty() && steps > 0 && width >= 256 && height >= 256) {
                binding.btnGenerate.isClickable = true
                binding.btnGenerate.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200
                    )
                )
                viewModel.setTextToImageRequest(prompt, steps, width, height)
            } else {
                binding.btnGenerate.isClickable = false
                binding.btnGenerate.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        androidx.cardview.R.color.cardview_dark_background
                    )
                )
            }
        }

        // Set maximum progressBar percentage
        binding.progressBar.max = 100
        // Update progressBar whenever the progress LiveData changes
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = (progress.times(100)).toInt()
        }

        // Listener for generate Button which initiates api call
        binding.btnGenerate.setOnClickListener {
            viewModel.loadTextToImage()
        }

        // Observer which initiates decoding of image in Base64 when api delivers response
        viewModel.imageBase64.observe(viewLifecycleOwner) { imageBase64 ->
            viewModel.decodeImage(imageBase64)
        }

        // Observer which loads image in ImageView when decoder sets decoded image
        viewModel.image.observe(viewLifecycleOwner) { image ->
            binding.ivTextToImage.setImageBitmap(image)
        }

        // Back button to navigate to home screen
        binding.fabBack.setOnClickListener { fabBack ->
            fabBack.findNavController().navigate(
                TextToImageFragmentDirections.actionTextToImageFragmentToHomeFragment()
            )
        }

        // Place models into the dropdown menu
        viewModel.models.observe(viewLifecycleOwner) { models ->
            var modelNameList: MutableList<String> = mutableListOf()
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


        binding.actvModel.setOnItemClickListener { parent, _, position, _ ->
            val modelName = parent.getItemAtPosition(position) as String
            viewModel.setModel(modelName)
        }
    }
}