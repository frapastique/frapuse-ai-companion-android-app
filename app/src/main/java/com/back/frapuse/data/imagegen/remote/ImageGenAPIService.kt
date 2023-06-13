package com.back.frapuse.data.imagegen.remote

import com.back.frapuse.data.imagegen.models.ImageGenImageBase64
import com.back.frapuse.data.imagegen.models.ImageGenImageInfo
import com.back.frapuse.data.imagegen.models.ImageGenOptions
import com.back.frapuse.data.imagegen.models.ImageGenProgress
import com.back.frapuse.data.imagegen.models.ImageGenSDModel
import com.back.frapuse.data.imagegen.models.ImageGenSampler
import com.back.frapuse.data.imagegen.models.ImageGenTextToImageResponse
import com.back.frapuse.data.imagegen.models.ImageGenTextToImageRequest
import com.back.frapuse.util.Companions.Companion.moshi
import com.back.frapuse.util.Companions.Companion.okHttpClient
import retrofit2.Retrofit.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "http://192.168.178.20:7861/sdapi/v1/"

private val retrofit = Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ImageGenAPIService {
    @GET("sd-models")
    suspend fun getModels(): List<ImageGenSDModel>

    @GET("options")
    suspend fun getOptions(): ImageGenOptions

    @POST("txt2img")
    suspend fun startTextToImage(
        @Body body: ImageGenTextToImageRequest
    ): ImageGenTextToImageResponse

    @GET("progress")
    suspend fun getProgress(): ImageGenProgress

    @POST("options")
    suspend fun setOptions(
        @Body body: ImageGenOptions
    )

    @POST("png-info")
    suspend fun getImageMetaData(
        @Body body: ImageGenImageBase64
    ): ImageGenImageInfo

    @GET("samplers")
    suspend fun getSamplers(): List<ImageGenSampler>
}

object ImageGenAPI {
    val retrofitService: ImageGenAPIService by lazy {
        retrofit.create(ImageGenAPIService::class.java)
    }
}