package com.example.ehefin_mobile.feature.auth.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for forgot password (SRP - Single Responsibility)
 */
class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        // Validation
        if (email.isBlank()) {
            return Resource.Error("Email tidak boleh kosong")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Format email tidak valid")
        }
        
        return authRepository.forgotPassword(email.trim())
    }
}