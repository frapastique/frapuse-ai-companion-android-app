package com.back.frapuse.data.textgen.remote

import com.back.frapuse.data.textgen.models.haystack.TextGenHaystackFilterDocumentsRequest
import com.back.frapuse.data.textgen.models.haystack.TextGenHaystackFilterDocumentsResponse
import com.back.frapuse.data.textgen.models.haystack.TextGenHaystackQueryRequest
import com.back.frapuse.data.textgen.models.haystack.TextGenHaystackQueryResponse
import com.back.frapuse.util.Companions.Companion.moshi
import com.back.frapuse.util.Companions.Companion.okHttpClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL = "http://192.168.178.20:8000/"

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TextGenHaystackApiService {
    @Multipart
    @POST("file-upload")
    suspend fun uploadFile(
        @Part filePart: MultipartBody.Part,
        @Part("meta") meta: RequestBody,
    ): String?

    @POST("documents/get_by_filters")
    suspend fun getDocuments(
        @Body body: TextGenHaystackFilterDocumentsRequest
    ): TextGenHaystackFilterDocumentsResponse

    @POST("documents/delete_by_filters")
    suspend fun deleteDocuments(
        @Body body: TextGenHaystackFilterDocumentsRequest
    ): Boolean

    @POST("query")
    suspend fun query(
        @Body body: TextGenHaystackQueryRequest
    ): TextGenHaystackQueryResponse
}

object TextGenHaystackAPI {
    val retrofitService: TextGenHaystackApiService by lazy {
        retrofit.create(TextGenHaystackApiService::class.java)
    }
}