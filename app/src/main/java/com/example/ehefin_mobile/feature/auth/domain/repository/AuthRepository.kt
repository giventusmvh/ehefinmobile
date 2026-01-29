package com.example.ehefin_mobile.feature.auth.domain.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult

/**
 * Repository interface for authentication operations (DIP)
 * Domain layer only knows about this interface, not the implementation
 */
interface AuthRepository {
    
    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): Resource<AuthResult>
    
    /**
     * Register new customer account
     */
    suspend fun register(name: String, email: String, password: String): Resource<AuthResult>
    
    /**
     * Logout current user
     */
    suspend fun logout(): Resource<Unit>
    
    /**
     * Request password reset email
     */
    suspend fun forgotPassword(email: String): Resource<Unit>
    
    /**
     * Reset password with token
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): Resource<Unit>
    
    /**
     * Check if user is logged in
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Clear local session
     */
    suspend fun clearSession()

    /**
     * Login with Firebase ID token (Google Sign-In)
     */
    suspend fun loginWithFirebase(idToken: String, fcmToken: String? = null): Resource<AuthResult>
}