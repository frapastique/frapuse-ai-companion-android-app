package com.back.frapuse.ui.textgen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.back.frapuse.TextGenViewModel
import com.back.frapuse.databinding.FragmentTextGenBinding

class TextGenFragment : Fragment() {
    // Get the viewModel into the logic
    private val viewModel: TextGenViewModel by activityViewModels()

    // Declaration of binding
    private lateinit var binding: FragmentTextGenBinding

    /**
     * Lifecycle Funktion onCreateView
     * binding gets initialized and the layout built
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextGenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var prompt = ""

        binding.etPrompt.addTextChangedListener { newPrompt ->
            prompt = newPrompt.toString()
        }

        binding.btnSend.setOnClickListener {
            viewModel.test(prompt)
            binding.progressBar.visibility = View.VISIBLE
        }

        viewModel.genResponseText.observe(viewLifecycleOwner) { response ->
            binding.tvResponse.text = response.text
            binding.progressBar.visibility = View.GONE
        }
    }
}