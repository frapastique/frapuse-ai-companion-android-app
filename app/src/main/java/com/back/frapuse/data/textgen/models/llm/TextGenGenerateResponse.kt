package com.back.frapuse.data.textgen.models.llm

data class TextGenGenerateResponse(
    val results: List<TextGenGenerateResponseText>
)

/*
{
    "results": [
        {
            "text": " "
        }
    ]
}
*/
