package com.back.frapuse.data.remote

import com.back.frapuse.data.datamodels.Progress
import com.back.frapuse.data.datamodels.SDModels
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://172.20.10.2:7860/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.MINUTES)
    .writeTimeout(20, TimeUnit.MINUTES)
    .readTimeout(20, TimeUnit.MINUTES)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TextToImageAPIService {
    @GET("sdapi/v1/sd-models")
    suspend fun getModels(): List<SDModels>

    @POST("sdapi/v1/txt2img")
    suspend fun startTextToImage(
        @Body body: TextToImageRequest
    ): TextToImage

    @GET("sdapi/v1/progress?skip_current_image=true")
    suspend fun getProgress(): Progress
}

object TextToImageAPI {
    val retrofitService: TextToImageAPIService by lazy { retrofit.create(TextToImageAPIService::class.java) }
}