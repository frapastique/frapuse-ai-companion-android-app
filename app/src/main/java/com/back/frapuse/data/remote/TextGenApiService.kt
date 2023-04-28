package com.back.frapuse.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://192.168.178.20:7863/api/v1/"

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

interface TextGenApiService {
    @GET("model")
    suspend fun getModel()

    @POST("token-count")
    suspend fun getTokenCount()

    @POST("generate")
    suspend fun generateText()
}

object TextGenAPI {
    val retrofitService: TextGenApiService by lazy { retrofit.create(TextGenApiService::class.java) }
}