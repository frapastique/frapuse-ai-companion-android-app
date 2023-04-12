package com.back.frapuse.data.remote

import com.back.frapuse.data.datamodels.TextToImage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

const val BASE_URL = "http://127.0.0.1:7860/sdapi/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TextToImageAPIService {
    @POST("txt2img")
    suspend fun getPrompt(
        @Query("prompt") prompt: String,
        @Query("steps") steps: Int,
        @Query("width") width: Int,
        @Query("height") height: Int,
    ): TextToImage
}

object TextToImageAPI {
    val retrofitService: TextToImageAPIService by lazy { retrofit.create(TextToImageAPIService::class.java) }
}