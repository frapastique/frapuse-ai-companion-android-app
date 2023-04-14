package com.back.frapuse.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        viewModel.loadOptions()
        viewModel.options.observe(viewLifecycleOwner) { options ->
            binding.actvModel.setText(options.sd_model_checkpoint)
        }

        binding.etPrompt.addTextChangedListener { prompt ->
            viewModel.setPrompt(prompt.toString())
        }

        viewModel.setSteps(binding.etSteps.text.toString())
        binding.etSteps.addTextChangedListener { steps ->
            viewModel.setSteps(steps.toString())
        }

        viewModel.setWidth(binding.etWidth.text.toString())
        binding.etWidth.addTextChangedListener { width ->
            viewModel.setWidth(width.toString())
        }

        viewModel.setHeight(binding.etHeight.text.toString())
        binding.etHeight.addTextChangedListener { height ->
            viewModel.setHeight(height.toString())
        }

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

        binding.progressBar.max = 100
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = (progress.times(100)).toInt()
        }

        binding.btnGenerate.setOnClickListener {
            viewModel.loadTextToImage()
        }

        viewModel.imageBase64.observe(viewLifecycleOwner) { imageBase64 ->
            viewModel.decodeImage(imageBase64)
        }

        viewModel.image.observe(viewLifecycleOwner) { image ->
            binding.ivTextToImage.setImageBitmap(image)
        }

        binding.fabBack.setOnClickListener { fabBack ->
            fabBack.findNavController().navigate(
                TextToImageFragmentDirections.actionTextToImageFragmentToHomeFragment()
            )
        }
    }
}