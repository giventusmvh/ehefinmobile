package com.example.ehefin_mobile.feature.auth.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user login (SRP - Single Responsibility)
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<AuthResult> {
        // Validation
        if (email.isBlank()) {
            return Resource.Error("Email tidak boleh kosong")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Format email tidak valid")
        }
        if (password.isBlank()) {
            return Resource.Error("Password tidak boleh kosong")
        }
        if (password.length < 6) {
            return Resource.Error("Password minimal 6 karakter")
        }
        
        return authRepository.login(email.trim(), password)
    }
}