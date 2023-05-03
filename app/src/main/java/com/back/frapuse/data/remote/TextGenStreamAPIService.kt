package com.back.frapuse.data.remote

import com.back.frapuse.data.datamodels.textgen.TextGenGenerateRequest
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val BASE_URL = "ws://192.168.178.20:7864/api/v1/"

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

interface TextGenStreamAPIService {
    @POST("stream")
    suspend fun generateStreamText(
        @Body body: TextGenGenerateRequest
    ): TextGenGenerateResponse
}

object TextGenStreamAPI {
    val retrofitService: TextGenStreamAPIService by lazy { retrofit.create(TextGenStreamAPIService::class.java) }
}