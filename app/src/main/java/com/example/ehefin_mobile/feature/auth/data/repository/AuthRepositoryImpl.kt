package com.example.ehefin_mobile.feature.auth.data.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.core.database.DatabaseCleaner
import com.example.ehefin_mobile.feature.auth.data.mapper.toDomain
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthApi
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.ForgotPasswordRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.FirebaseLoginRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.LoginRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.RegisterRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.ResetPasswordRequest
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of AuthRepository
 * Handles API calls and session management
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val databaseCleaner: DatabaseCleaner,
    private val gson: Gson
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            handleAuthResponse(response)
        } catch (e: Exception) {
            Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
    
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<AuthResult> {
        return try {
            val response = authApi.register(RegisterRequest(name, email, password))
            handleAuthResponse(response)
        } catch (e: Exception) {
            Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
    
    override suspend fun logout(): Resource<Unit> {
        return try {
            val response = authApi.logout()
            // Clear local data regardless of API response
            clearLocalData()
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Clear local session even on network error
            clearLocalData()
            Resource.Success(Unit)
        }
    }
    
    /**
     * Clears all local user data including tokens and database cache.
     * This ensures no user data persists after logout.
     */
    private suspend fun clearLocalData() {
        // Clear session tokens and user data from DataStore
        tokenManager.clearSession()
        // Clear all cached data from Room database
        databaseCleaner.clearAllData()
    }
    
    override suspend fun forgotPassword(email: String): Resource<Unit> {
        return try {
            val response = authApi.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(Unit)
            } else {
                // Always return success for security (as per backend design)
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
    
    override suspend fun resetPassword(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): Resource<Unit> {
        return try {
            val response = authApi.resetPassword(
                ResetPasswordRequest(token, newPassword, confirmPassword)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(Unit)
            } else {
                val errorMessage = parseErrorMessage(response)
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn().first()
    }
    
    override suspend fun clearSession() {
        tokenManager.clearSession()
    }

    override suspend fun loginWithFirebase(idToken: String, fcmToken: String?): Resource<AuthResult> {
        return try {
            val response = authApi.firebaseLogin(FirebaseLoginRequest(idToken, fcmToken))
            handleAuthResponse(response)
        } catch (e: Exception) {
            Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
    
    private suspend fun handleAuthResponse(
        response: Response<com.example.ehefin_mobile.core.network.ApiResponse<com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto>>
    ): Resource<AuthResult> {
        return if (response.isSuccessful && response.body()?.success == true) {
            val authData = response.body()!!.data!!
            
            // Check if user switched (different from last logged in user)
            val lastUserId = tokenManager.getLastLoggedInUserId().first()
            if (lastUserId != null && lastUserId != authData.userId) {
                // User switched - clear all cached data
                databaseCleaner.clearAllData()
            }
            
            // Save session to DataStore
            tokenManager.saveUserSession(
                token = authData.token,
                userId = authData.userId,
                email = authData.email,
                name = authData.name
            )
            
            // Save current user as last logged in user
            tokenManager.saveLastLoggedInUserId(authData.userId)
            
            Resource.Success(authData.toDomain())
        } else {
            val errorMessage = parseErrorMessage(response)
            Resource.Error(errorMessage)
        }
    }
    
    private fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                jsonObject.get("message")?.asString ?: "Terjadi kesalahan"
            } else {
                "Terjadi kesalahan"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }
}