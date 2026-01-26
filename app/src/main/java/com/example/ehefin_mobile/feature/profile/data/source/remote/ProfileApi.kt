package com.example.ehefin_mobile.feature.profile.data.source.remote

import com.example.ehefin_mobile.core.network.ApiResponse
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.FcmTokenRequest
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileResponseDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

/** Retrofit API interface for Profile endpoints */
interface ProfileApi {

        @GET("customer/profile") suspend fun getProfile(): Response<ApiResponse<ProfileResponseDto>>

        @Multipart
        @PUT("customer/profile")
        suspend fun updateProfile(
                @Part("data") data: RequestBody,
                @Part ktp: MultipartBody.Part?,
                @Part kk: MultipartBody.Part?,
                @Part npwp: MultipartBody.Part?
        ): Response<ApiResponse<UpdateProfileResponseDto>>

        @POST("customer/fcm-token")
        suspend fun registerFcmToken(
                @Body request: FcmTokenRequest
        ): Response<ApiResponse<Any>> // Using Any since data is null
}
