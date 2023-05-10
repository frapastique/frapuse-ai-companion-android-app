package com.back.frapuse.data.textgen.models

data class TextGenTokenCountResponse(
    val results: List<TextGenTokenCountBody>
)

/*

curl -X POST \
-H "Content-Type: application/json" \
-d "{\"prompt\": \"In order to make homemade bread, follow these steps:\n1)\"}" \
http://192.168.178.20:7863/api/v1/token-count

*/