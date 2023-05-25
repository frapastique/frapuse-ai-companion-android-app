package com.back.frapuse.data.textgen.remote

import com.back.frapuse.data.textgen.models.TextGenGenerateRequest
import com.back.frapuse.data.textgen.models.TextGenGenerateResponse
import com.back.frapuse.data.textgen.models.TextGenModelResponse
import com.back.frapuse.data.textgen.models.TextGenPrompt
import com.back.frapuse.data.textgen.models.TextGenTokenCountResponse
import com.back.frapuse.util.Companions.Companion.moshi
import com.back.frapuse.util.Companions.Companion.okHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "http://192.168.178.20:7863/api/v1/"

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TextGenBlockApiService {
    @GET("model")
    suspend fun getModel(): TextGenModelResponse

    @POST("token-count")
    suspend fun getTokenCount(
        @Body body: TextGenPrompt
    ): TextGenTokenCountResponse

    @POST("generate")
    suspend fun generateText(
        @Body body: TextGenGenerateRequest
    ): TextGenGenerateResponse
}

object TextGenBlockAPI {
    val retrofitService: TextGenBlockApiService by lazy {
        retrofit.create(TextGenBlockApiService::class.java)
    }
}