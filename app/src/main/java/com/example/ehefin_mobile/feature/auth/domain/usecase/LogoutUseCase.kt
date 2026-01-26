package com.example.ehefin_mobile.feature.auth.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user logout (SRP - Single Responsibility)
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.logout()
    }
}
