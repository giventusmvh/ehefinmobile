package com.example.ehefin_mobile.feature.profile.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case to register FCM token
 */
class RegisterFcmTokenUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(token: String): Resource<Unit> {
        if (token.isBlank()) {
            return Resource.Error("Token invalid")
        }
        return profileRepository.registerFcmToken(token)
    }
}