package com.back.frapuse.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.back.frapuse.R
import com.back.frapuse.SharedViewModel
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
            // binding.btnGenerate.isClickable = !prompt.isNullOrEmpty()
            binding.btnGenerate.setOnClickListener { btnGenerate ->
                btnGenerate.isClickable = !prompt.isNullOrEmpty()
                viewModel.loadPrompt(prompt.toString())
            }
        }

        viewModel.imageBase64.observe(viewLifecycleOwner) { imageBase64 ->
            viewModel.decodeImage(imageBase64)
        }

        viewModel.image.observe(viewLifecycleOwner) { image ->
            binding.ivTextToImage.setImageBitmap(image)
        }
    }
}