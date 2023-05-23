package com.back.frapuse.data.textgen.remote

import com.back.frapuse.data.textgen.models.TextGenHaystackFileUpload
import com.back.frapuse.data.textgen.models.TextGenHaystackFilterDocumentsRequest
import com.back.frapuse.data.textgen.models.TextGenHaystackFilterDocumentsResponse
import com.back.frapuse.data.textgen.models.TextGenHaystackQueryRequest
import com.back.frapuse.data.textgen.models.TextGenHaystackQueryResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://192.168.178.20:8000/"

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

interface TextGenHaystackApiService {
    @POST("file-upload")
    suspend fun uploadFile(
        @Body body: TextGenHaystackFileUpload
    ): String

    @POST("documents/get_by_filters")
    suspend fun getDocuments(
        @Body body: TextGenHaystackFilterDocumentsRequest
    ): TextGenHaystackFilterDocumentsResponse

    @POST("documents/delete_by_filters")
    suspend fun deleteDocuments(
        @Body body: TextGenHaystackFilterDocumentsRequest
    ): TextGenHaystackFilterDocumentsResponse

    @POST("query")
    suspend fun query(
        @Body body: TextGenHaystackQueryRequest
    ): TextGenHaystackQueryResponse
}

object TextGenHaystackAPI {
    val retrofitService: TextGenHaystackApiService by lazy { retrofit.create(TextGenHaystackApiService::class.java) }
}