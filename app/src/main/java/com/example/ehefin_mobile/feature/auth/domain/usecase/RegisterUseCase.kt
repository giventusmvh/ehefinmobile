package com.example.ehefin_mobile.feature.auth.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.model.AuthResult
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user registration (SRP - Single Responsibility)
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Resource<AuthResult> {
        // Validation
        if (name.isBlank()) {
            return Resource.Error("Nama tidak boleh kosong")
        }
        if (name.length < 3) {
            return Resource.Error("Nama minimal 3 karakter")
        }
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
        if (password != confirmPassword) {
            return Resource.Error("Password dan konfirmasi password tidak sama")
        }
        
        return authRepository.register(name.trim(), email.trim(), password)
    }
}
