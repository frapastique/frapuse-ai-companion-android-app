package com.back.frapuse.data.imagegen.remote

import com.back.frapuse.data.imagegen.models.ImageBase64
import com.back.frapuse.data.imagegen.models.ImageInfo
import com.back.frapuse.data.imagegen.models.Options
import com.back.frapuse.data.imagegen.models.Progress
import com.back.frapuse.data.imagegen.models.SDModel
import com.back.frapuse.data.imagegen.models.Sampler
import com.back.frapuse.data.imagegen.models.TextToImage
import com.back.frapuse.data.imagegen.models.TextToImageRequest
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

private const val BASE_URL = "http://192.168.178.20:7861/sdapi/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.MINUTES)
    .writeTimeout(20, TimeUnit.MINUTES)
    .readTimeout(20, TimeUnit.MINUTES)
    .build()

private val retrofit = Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ImageGenAPIService {
    @GET("sd-models")
    suspend fun getModels(): List<SDModel>

    @GET("options")
    suspend fun getOptions(): Options

    @POST("txt2img")
    suspend fun startTextToImage(
        @Body body: TextToImageRequest
    ): TextToImage

    @GET("progress")
    suspend fun getProgress(): Progress

    @POST("options")
    suspend fun setOptions(
        @Body body: Options
    )

    @POST("png-info")
    suspend fun getImageMetaData(
        @Body body: ImageBase64
    ): ImageInfo

    @GET("samplers")
    suspend fun getSamplers(): List<Sampler>
}

object ImageGenAPI {
    val retrofitService: ImageGenAPIService by lazy { retrofit.create(ImageGenAPIService::class.java) }
}