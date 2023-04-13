package com.back.frapuse.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.back.frapuse.R
import com.back.frapuse.SharedViewModel
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.databinding.FragmentTextToImageBinding

class TextToImageFragment : Fragment() {
    // Hier wird das ViewModel, in dem die Logik stattfindet, geholt
    private val viewModel: SharedViewModel by activityViewModels()

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

        binding.etPrompt.addTextChangedListener { prompt ->
            viewModel.setPrompt(prompt.toString())
        }

        binding.etSteps.addTextChangedListener { steps ->
            viewModel.setSteps(steps.toString())
        }

        binding.etWidth.addTextChangedListener { width ->
            viewModel.setWidth(width.toString())
        }

        binding.etHeight.addTextChangedListener { height ->
            viewModel.setHeight(height.toString())
        }

        viewModel.generationData.observe(viewLifecycleOwner) { (prompt, steps, width, height) ->
            binding.btnGenerate.isClickable = true
            binding.btnGenerate.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.purple_500)
            )
            viewModel.setTextToImageRequest(prompt, steps, width, height)
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
    }
}