package com.back.frapuse.data.imagegen.models

data class ImageGenOptions(
    val sd_model_checkpoint: String,
    val live_previews_enable: Boolean = true,
    val show_progress_every_n_steps: Int = 1,
)
