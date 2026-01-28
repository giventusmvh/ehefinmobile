package com.example.ehefin_mobile.core.network

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper matching backend ApiResponse structure
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("timestamp")
    val timestamp: String?
)

/**
 * Empty response for endpoints that don't return data
 */
data class EmptyResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("timestamp")
    val timestamp: String?
)