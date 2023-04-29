package com.back.frapuse.data.datamodels.textgen

data class TextGenGenerateRequest(
    val prompt: String,
    val max_new_tokes: Int,
    val do_sample: Boolean,
    val temperature: Double,
    val top_p: Double,
    val typical_p: Double,
    val repetition_penalty: Double,
    val top_k: Int,
    val min_length: Int,
    val no_repeat_ngram_size: Int,
    val num_beams: Int,
    val penalty_alpha: Double,
    val length_penalty: Double,
    val early_stopping: Boolean,
    val seed: Int,
    val add_bos_token: Boolean,
    val truncation_length: Int,
    val ban_eos_token: Boolean,
    val skip_special_tokens: Boolean,
    val stopping_strings: List<String>
)

/*
{
    "prompt": " ",
    "max_new_tokens": 250,
    "do_sample": true,
    "temperature": 1.3,
    "top_p": 0.1,
    "typical_p": 1,
    "repetition_penalty": 1.18,
    "top_k": 40,
    "min_length": 0,
    "no_repeat_ngram_size": 0,
    "num_beams": 1,
    "penalty_alpha": 0,
    "length_penalty": 1,
    "early_stopping": false,
    "seed": -1,
    "add_bos_token": true,
    "truncation_length": 2048,
    "ban_eos_token": false,
    "skip_special_tokens": true,
    "stopping_strings": []
}
*/