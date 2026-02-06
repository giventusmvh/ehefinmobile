package com.example.ehefin_mobile.feature.auth.data.repository

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.database.DatabaseCleaner
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.feature.auth.data.mapper.toDomain
import com.example.ehefin_mobile.feature.auth.data.source.remote.AuthRemoteDataSource
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Implementation of AuthRepository using DataSource interface.
 * Handles session management with proper layer separation.
 */
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val databaseCleaner: DatabaseCleaner
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return when (val result = remoteDataSource.login(email, password)) {
            is DataResult.Success -> handleSuccessfulAuth(result.data)
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<AuthResult> {
        return when (val result = remoteDataSource.register(name, email, password)) {
            is DataResult.Success -> handleSuccessfulAuth(result.data)
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    override suspend fun logout(): Resource<Unit> {
        remoteDataSource.logout()
        // Clear local data regardless of API response
        clearLocalData()
        return Resource.Success(Unit)
    }

    override suspend fun forgotPassword(email: String): Resource<Unit> {
        return when (val result = remoteDataSource.forgotPassword(email)) {
            is DataResult.Success -> Resource.Success(Unit)
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    override suspend fun resetPassword(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): Resource<Unit> {
        return when (val result = remoteDataSource.resetPassword(token, newPassword, confirmPassword)) {
            is DataResult.Success -> Resource.Success(Unit)
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn().first()
    }

    override suspend fun clearSession() {
        tokenManager.clearSession()
    }

    override suspend fun loginWithFirebase(idToken: String, fcmToken: String?): Resource<AuthResult> {
        return when (val result = remoteDataSource.loginWithFirebase(idToken, fcmToken)) {
            is DataResult.Success -> handleSuccessfulAuth(result.data)
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    private suspend fun handleSuccessfulAuth(
        authData: com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto
    ): Resource<AuthResult> {
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

        return Resource.Success(authData.toDomain())
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
}