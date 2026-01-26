package com.example.ehefin_mobile.core.network

import com.example.ehefin_mobile.core.datastore.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp Interceptor that automatically adds Bearer token to authenticated requests
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth header for public endpoints
        val publicEndpoints = listOf(
            "auth/login",
            "auth/register",
            "auth/forgot-password",
            "auth/reset-password",
            "products",
            "branches"
        )
        
        val isPublicEndpoint = publicEndpoints.any { 
            originalRequest.url.encodedPath.contains(it) 
        }
        
        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }
        
        // Get token synchronously (blocking)
        val token = runBlocking { tokenManager.getAccessToken().first() }
        
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
}
