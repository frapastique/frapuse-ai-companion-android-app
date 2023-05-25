package com.back.frapuse.data.imagegen.remote

import com.back.frapuse.data.imagegen.models.ImageBase64
import com.back.frapuse.data.imagegen.models.ImageInfo
import com.back.frapuse.data.imagegen.models.Options
import com.back.frapuse.data.imagegen.models.Progress
import com.back.frapuse.data.imagegen.models.SDModel
import com.back.frapuse.data.imagegen.models.Sampler
import com.back.frapuse.data.imagegen.models.TextToImage
import com.back.frapuse.data.imagegen.models.TextToImageRequest
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
    val retrofitService: ImageGenAPIService by lazy {
        retrofit.create(ImageGenAPIService::class.java)
    }
}