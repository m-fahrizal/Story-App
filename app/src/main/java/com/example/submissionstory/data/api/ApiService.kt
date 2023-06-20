package com.example.submissionstory.data.api

import com.example.submissionstory.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignInResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): MainResponse

    @GET("stories")
    fun getStoryWithLocation(
        @Header("Authorization") bearer: String,
        @Query("location") location: Int
    ): Call<MainResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<FileUploadResponse>
}