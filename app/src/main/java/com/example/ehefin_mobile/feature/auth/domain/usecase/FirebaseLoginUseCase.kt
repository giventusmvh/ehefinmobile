package com.example.ehefin_mobile.feature.auth.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for Firebase authentication (Google Sign-In)
 * Handles login with Firebase ID token
 */
class FirebaseLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String, fcmToken: String? = null): Resource<AuthResult> {
        // Validation
        if (idToken.isBlank()) {
            return Resource.Error("Token autentikasi tidak valid")
        }

        return authRepository.loginWithFirebase(idToken, fcmToken)
    }
}