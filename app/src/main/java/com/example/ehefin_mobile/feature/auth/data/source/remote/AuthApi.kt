package com.example.ehefin_mobile.feature.auth.data.source.remote

import com.example.ehefin_mobile.core.common.Constants.Endpoints
import com.example.ehefin_mobile.core.network.ApiResponse
import com.example.ehefin_mobile.core.network.EmptyResponse
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.ForgotPasswordRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.LoginRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.RegisterRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints
 */
interface AuthApi {
    
    @POST(Endpoints.AUTH_LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponseDto>>
    
    @POST(Endpoints.AUTH_REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponseDto>>
    
    @POST(Endpoints.AUTH_LOGOUT)
    suspend fun logout(): Response<EmptyResponse>
    
    @POST(Endpoints.AUTH_FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<EmptyResponse>
    
    @POST(Endpoints.AUTH_RESET_PASSWORD)
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<EmptyResponse>
}
