package com.example.ehefin_mobile.feature.auth.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto

/**
 * Interface for Auth remote data source operations.
 * Abstracts API calls for testability and separation of concerns.
 */
interface AuthRemoteDataSource {

    /**
     * Login with email and password.
     */
    suspend fun login(email: String, password: String): DataResult<AuthResponseDto>

    /**
     * Register a new user.
     */
    suspend fun register(name: String, email: String, password: String): DataResult<AuthResponseDto>

    /**
     * Logout current user.
     */
    suspend fun logout(): DataResult<Unit>

    /**
     * Request password reset email.
     */
    suspend fun forgotPassword(email: String): DataResult<Unit>

    /**
     * Reset password with token.
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): DataResult<Unit>

    /**
     * Login with Firebase ID token.
     */
    suspend fun loginWithFirebase(idToken: String, fcmToken: String?): DataResult<AuthResponseDto>
}