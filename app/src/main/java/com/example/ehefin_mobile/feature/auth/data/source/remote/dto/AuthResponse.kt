package com.example.ehefin_mobile.feature.auth.data.source.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response body for authentication (login/register)
 */
data class AuthResponseDto(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("tokenType")
    val tokenType: String,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("roles")
    val roles: List<String>,
    
    @SerializedName("permissions")
    val permissions: List<String>
)