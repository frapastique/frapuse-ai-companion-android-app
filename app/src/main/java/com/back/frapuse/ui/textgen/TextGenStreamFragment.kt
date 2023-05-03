package com.back.frapuse.ui.textgen

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.back.frapuse.R
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.databinding.FragmentTextGenStreamBinding

class TextGenStreamFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentTextGenStreamBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextGenStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var prompt = ""

        binding.etPrompt.addTextChangedListener { newPrompt ->
            prompt = newPrompt.toString()

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

        binding.btnSend.setOnClickListener {
            viewModel.testBlock(prompt)
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

        viewModel.genResponseText.observe(viewLifecycleOwner) { response ->
            binding.tvResponse.text = response.text
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