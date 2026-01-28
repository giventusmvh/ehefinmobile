package com.example.ehefin_mobile.feature.auth.data.source.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for login
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Request body for registration
 */
data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Request body for forgot password
 */
data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

/**
 * Request body for reset password
 */
data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("newPassword")
    val newPassword: String,
    
    @SerializedName("confirmPassword")
    val confirmPassword: String
)